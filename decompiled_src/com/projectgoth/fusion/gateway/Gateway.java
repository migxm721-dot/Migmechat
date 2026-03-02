/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Object
 *  Ice.ObjectPrx
 *  Ice.Util
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import Ice.ObjectPrx;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.MultiSampler;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.ConnectionHTTP;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.ConnectionTCP;
import com.projectgoth.fusion.gateway.GatewayContext;
import com.projectgoth.fusion.gateway.GatewayMain;
import com.projectgoth.fusion.gateway.InstrumentedThreadPool;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ConnectionPrxHelper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GatewayStats;
import com.projectgoth.fusion.slice.GatewayThreadPoolStats;
import com.projectgoth.fusion.stats.ConcurrentCountsMap;
import com.projectgoth.fusion.stats.FusionPktOpenURLStats;
import com.projectgoth.fusion.userevent.EventTextTranslator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Gateway
implements Runnable {
    protected GatewayContext applicationContext;
    private ServerSocketChannel serverChannel;
    private Map<ConnectionI, Integer> pendingRegistrations = new HashMap<ConnectionI, Integer>();
    private Set<ConnectionI> connectionSet = Collections.newSetFromMap(new ConcurrentHashMap());
    protected ServerType serverType;
    private String host;
    protected int port;
    private int backlog;
    private String offlineMessage;
    private static EventTextTranslator translator = new EventTextTranslator();
    AtomicInteger connectionCount = new AtomicInteger(0);
    private boolean tooBusy;
    private int id;
    private int maxThreadPoolQueueSize;
    private int maxConnections;
    private int minProtocolVersion;
    private int minMidletVersion;
    private int maxMidletVersion;
    private int maxAllowableThreadPoolQueueSize;
    private int maxAllowableTimeInQueue;
    private int keepAliveInterval;
    int timeoutInterval;
    private int generalCoolDown;
    private int maxFailedLoginAttempts;
    private int loginReplyDelay;
    private int socketCloseDelay;
    private int selectorTimeout;
    private int selectorPauseInterval;
    private int tooBusyCheckInterval;
    private int timesTooBusy;
    private int imLoginTimeout;
    private int minIntervalBetweenPackets;
    private int maxPacketsToBuffer;
    private int numberOfEventsToKeep;
    private int chatRoomNotificationInterval;
    private boolean enableOfflineMessage;
    private Date lastTimeTooBusy = new Date();
    private String imFilePath;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Gateway.class));
    private static volatile ConcurrentCountsMap<String> remoteIpCxnCounts = new ConcurrentCountsMap();
    private Runnable processAcceptsThread = new Runnable(){

        public void run() {
            try {
                Gateway.this.doAccepts();
            }
            catch (FusionException e) {
                log.error((Object)"Exception when accepting new connections ", (Throwable)((Object)e));
            }
        }
    };
    private static LazyLoader<Boolean> trackCxnsPerRemoteIPEnabled = new LazyLoader<Boolean>("TRACK_CONNECTIONS_PER_REMOTE_IP_ENABLED", 60000L){
        private Boolean previousValue;

        @Override
        protected Boolean fetchValue() throws Exception {
            return SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.TRACK_CONNECTIONS_PER_REMOTE_IP_ENABLED);
        }

        @Override
        protected void onValueChanged(Boolean lastValue, Boolean currentValue) {
            remoteIpCxnCounts = new ConcurrentCountsMap();
        }
    };

    public Gateway(GatewayContext context) {
        this.applicationContext = context;
        this.initializeSettings();
    }

    private void initializeSettings() {
        String strServerType = this.applicationContext.getProperties().getPropertyWithDefault("ServerType", "TCP");
        this.serverType = SystemPropertyEntities.Temp.Cache.se493WebSocketsEnabled.getValue().booleanValue() ? (strServerType.equalsIgnoreCase("HTTP") ? ServerType.HTTP : (strServerType.equalsIgnoreCase("TCP") ? ServerType.TCP : ServerType.WS)) : (strServerType.equalsIgnoreCase("HTTP") ? ServerType.HTTP : ServerType.TCP);
        this.host = this.applicationContext.getProperties().getPropertyWithDefault("Server", "0.0.0.0");
        this.port = this.applicationContext.getProperties().getPropertyAsIntWithDefault("ServerPort", 8888);
        this.backlog = this.applicationContext.getProperties().getPropertyAsIntWithDefault("Backlog", 100);
        this.offlineMessage = this.applicationContext.getProperties().getPropertyWithDefault("OfflineMessage", "");
        this.minProtocolVersion = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MinProtocolVersion", 0);
        this.minMidletVersion = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MinMidletVersion", 0);
        this.maxMidletVersion = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MaxMidletVersion", Integer.MAX_VALUE);
        this.generalCoolDown = this.applicationContext.getProperties().getPropertyAsIntWithDefault("GeneralCoolDown", 15) * 1000;
        this.maxFailedLoginAttempts = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MaxFailedLoginAttempts", 10);
        this.loginReplyDelay = this.applicationContext.getProperties().getPropertyAsIntWithDefault("LoginReplyDelay", 10) * 1000;
        this.socketCloseDelay = this.applicationContext.getProperties().getPropertyAsIntWithDefault("SocketCloseDelay", 10);
        this.keepAliveInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("KeepAliveInterval", 20) * 1000;
        this.timeoutInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("IdleConnectionPurgeInterval", 0) * 1000;
        this.selectorTimeout = this.applicationContext.getProperties().getPropertyAsIntWithDefault("SelectorTimeout", 100);
        this.selectorPauseInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("SelectorPauseInterval", 10);
        this.tooBusyCheckInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("TooBusyCheckInterval", 500);
        this.maxAllowableThreadPoolQueueSize = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MaxAllowableThreadPoolQueueSize", Integer.MAX_VALUE);
        this.maxAllowableTimeInQueue = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MaxAllowableTimeInQueue", Integer.MAX_VALUE);
        if (this.maxAllowableTimeInQueue < Integer.MAX_VALUE) {
            this.maxAllowableTimeInQueue *= 1000;
        }
        this.minIntervalBetweenPackets = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MinIntervalBetweenPackets", 2000);
        this.maxPacketsToBuffer = this.applicationContext.getProperties().getPropertyAsIntWithDefault("MaxPacketsToBuffer", 20);
        this.enableOfflineMessage = this.applicationContext.getProperties().getPropertyAsIntWithDefault("EnableOfflineMessage", 1) == 1;
        this.imFilePath = this.applicationContext.getProperties().getPropertyWithDefault("IMFilePath", "/usr/fusion/im");
        this.imLoginTimeout = this.applicationContext.getProperties().getPropertyAsIntWithDefault("IMLoginTimeout", 30) * 1000;
        this.numberOfEventsToKeep = this.applicationContext.getProperties().getPropertyAsIntWithDefault("EventsToKeep", 10);
        this.chatRoomNotificationInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("ChatRoomNotificationInterval", 60) * 1000;
    }

    public GatewayStats getStats() {
        int remoteIPs;
        GatewayStats stats = ServiceStatsFactory.getGatewayStats(this.applicationContext.getStartTime());
        stats.serverType = this.serverType.toString();
        stats.port = this.port;
        stats.numConnectionObjects = this.connectionCount.get();
        stats.maxConnectionObjects = this.maxConnections;
        stats.threadPoolStats = new GatewayThreadPoolStats[this.applicationContext.getGatewayThreadPool().size()];
        stats.tooBusy = this.tooBusy;
        stats.timesTooBusy = this.timesTooBusy;
        stats.lastTimeTooBusy = this.lastTimeTooBusy.getTime();
        int i = 0;
        for (ThreadPoolName name : this.applicationContext.getGatewayThreadPool().keySet()) {
            InstrumentedThreadPool pool = this.applicationContext.getGatewayThreadPool().get((Object)name);
            GatewayThreadPoolStats poolStats = new GatewayThreadPoolStats();
            poolStats.name = name.toString();
            poolStats.requestsPerSecond = pool.getRequestsPerSecond();
            poolStats.maxRequestsPerSecond = pool.getMaxRequestsPerSecond();
            poolStats.threadPoolSize = pool.getActiveCount();
            poolStats.maxThreadPoolSize = pool.getLargestPoolSize();
            poolStats.threadPoolQueueSize = pool.getQueueSize();
            poolStats.maxThreadPoolQueueSize = 0;
            stats.threadPoolStats[i++] = poolStats;
        }
        if (trackCxnsPerRemoteIPEnabled.getValue().booleanValue() && (remoteIPs = remoteIpCxnCounts.size()) != 0) {
            stats.connectionsPerRemoteIP = (float)this.connectionCount.get() / (float)remoteIPs;
        }
        if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.OPEN_URL_STATS_ENABLED)) {
            FusionPktOpenURLStats instance = FusionPktOpenURLStats.getInstance();
            stats.openUrlAttempts = instance.getTotalAttempts();
            stats.openUrlFailures = instance.getTotalFailures();
            stats.openUrlFailurePercent = stats.openUrlAttempts != 0 ? (float)stats.openUrlFailures / (float)stats.openUrlAttempts * 100.0f : 0.0f;
            stats.openUrlFailuresByUrl = instance.getFailuresByURL();
            int successes = stats.openUrlAttempts - stats.openUrlFailures;
            stats.averageSuccessfulProcessingTimeSeconds = successes != 0 ? (float)instance.getTotalSuccessesProcessingTime() / (float)successes / 1000.0f : 0.0f;
        }
        return stats;
    }

    public ConnectionPrx addConnection(String objectId, ConnectionI connection) {
        ObjectPrx basePrx = this.applicationContext.getConnectionAdapter().add((Ice.Object)connection, Util.stringToIdentity((String)objectId));
        int count = this.connectionCount.incrementAndGet();
        if (count > this.maxConnections) {
            this.maxConnections = count;
        }
        if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.PT66915380_ENABLE_CONNECTION_TRACKING)) {
            this.connectionSet.add(connection);
        }
        if (trackCxnsPerRemoteIPEnabled.getValue().booleanValue()) {
            remoteIpCxnCounts.increment(connection.remoteAddress);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Connection " + objectId + " (" + connection.getDisplayName() + ") added to Ice adaptor"));
        }
        return ConnectionPrxHelper.uncheckedCast(basePrx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerConnection(ConnectionI connection, int interest) throws FusionException {
        Map<ConnectionI, Integer> map = this.pendingRegistrations;
        synchronized (map) {
            Integer oldInterest = this.pendingRegistrations.get(connection);
            if (oldInterest != null) {
                if (oldInterest == interest) {
                    return;
                }
                interest |= oldInterest.intValue();
            }
            this.pendingRegistrations.put(connection, interest);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void registerPendingConnections() throws FusionException {
        Map<ConnectionI, Integer> map = this.pendingRegistrations;
        synchronized (map) {
            if (this.pendingRegistrations.size() > 0) {
                for (ConnectionI connection : this.pendingRegistrations.keySet()) {
                    try {
                        int interest = this.pendingRegistrations.get(connection);
                        SelectionKey key = connection.getSelectionKey(this.applicationContext.getSelector());
                        if (key == null) {
                            connection.channel.register(this.applicationContext.getSelector(), interest, connection);
                            continue;
                        }
                        key.interestOps(key.interestOps() | interest);
                    }
                    catch (ClosedChannelException e) {
                        log.debug((Object)"Failed to register connection - channel closed");
                    }
                    catch (CancelledKeyException e) {
                        log.debug((Object)"CancelledKeyException caught in registerPendingConnections()");
                    }
                    catch (Exception e) {
                        log.warn((Object)(e.getClass().getName() + " caught in registerPendingConnections() - " + e.getMessage()));
                    }
                }
                this.pendingRegistrations.clear();
            }
        }
    }

    public ConnectionI findConnection(String objectId) {
        try {
            return (ConnectionI)this.applicationContext.getConnectionAdapter().find(Util.stringToIdentity((String)objectId));
        }
        catch (Exception e) {
            return null;
        }
    }

    public void removeConnection(String objectId, String remoteAddress) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Removing connection " + objectId + " from Ice adaptor"));
        }
        ConnectionI connection = (ConnectionI)this.applicationContext.getConnectionAdapter().remove(Util.stringToIdentity((String)objectId));
        this.connectionCount.decrementAndGet();
        if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.PT66915380_ENABLE_CONNECTION_TRACKING)) {
            this.connectionSet.remove(connection);
        }
        if (trackCxnsPerRemoteIPEnabled.getValue().booleanValue()) {
            remoteIpCxnCounts.decrement(remoteAddress);
        }
    }

    public void onConnectionAccessed(ConnectionI connection) {
        this.applicationContext.getPurger().monitor(connection);
    }

    public void onConnectionDisconnected(ConnectionI connection) {
        SelectionKey key;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Connection " + connection.getDisplayName() + " disconnected"));
        }
        if (connection.getConnectionPrx() == null) {
            this.applicationContext.getPurger().remove(connection);
        }
        if ((key = connection.getSelectionKey(this.applicationContext.getSelector())) != null) {
            key.cancel();
        }
    }

    public boolean verifyProtocolVersion(int version) {
        return version >= this.minProtocolVersion;
    }

    public boolean verifyMidletVersion(int version) {
        return version >= this.minMidletVersion && version <= this.maxMidletVersion;
    }

    private void onTooBusy() {
        this.tooBusy = true;
        this.lastTimeTooBusy.setTime(System.currentTimeMillis());
        ++this.timesTooBusy;
        log.warn((Object)("Server too busy, [" + this.applicationContext.getGatewayThreadPool().get((Object)ThreadPoolName.PRIMARY).getQueueSize() + "] tasks in the queue"));
    }

    private void onNoLongerTooBusy() throws IOException {
        log.warn((Object)"Server recovered from too busy");
        this.bindServerSocket();
        this.tooBusy = false;
    }

    public boolean isTooBusy() throws IOException {
        if (this.maxAllowableTimeInQueue == Integer.MAX_VALUE) {
            return false;
        }
        if (this.tooBusy) {
            int size = this.applicationContext.getGatewayThreadPool().get((Object)ThreadPoolName.PRIMARY).getQueueSize();
            if (size > this.maxThreadPoolQueueSize) {
                this.maxThreadPoolQueueSize = size;
            }
            if (size <= this.maxAllowableThreadPoolQueueSize) {
                this.onNoLongerTooBusy();
            }
        } else {
            InstrumentedThreadPool.Task eldestTask = this.applicationContext.getGatewayThreadPool().get((Object)ThreadPoolName.PRIMARY).getEldestTaskOnQueue();
            if (eldestTask != null && System.currentTimeMillis() - eldestTask.getTimeCreated() > (long)this.maxAllowableTimeInQueue) {
                this.onTooBusy();
            }
        }
        return this.tooBusy;
    }

    public int getPort() {
        return this.port;
    }

    public String getOfflineMessage() {
        return this.offlineMessage;
    }

    public int getKeepAliveInterval() {
        return this.keepAliveInterval;
    }

    public int getGeneralCoolDown() {
        return this.generalCoolDown;
    }

    public int getMaxFailedLoginAttempts() {
        return this.maxFailedLoginAttempts;
    }

    public int getLoginReplyDelay() {
        return this.loginReplyDelay;
    }

    public int getSocketCloseDelay() {
        return this.socketCloseDelay;
    }

    public boolean getEnableOfflineMessage() {
        return this.enableOfflineMessage;
    }

    public int getMinIntervalBetweenPackets() {
        return this.minIntervalBetweenPackets;
    }

    public int getMaxPacketsToBuffer() {
        return this.maxPacketsToBuffer;
    }

    public MultiSampler getSampler() {
        return this.applicationContext.getSamplingTask().sampler;
    }

    public int getID() {
        return this.id;
    }

    public int getImLoginTimeout() {
        return this.imLoginTimeout;
    }

    public static EventTextTranslator getTranslator() {
        return translator;
    }

    public int getNumberOfEventsToKeep() {
        return this.numberOfEventsToKeep;
    }

    public int getChatRoomNotificationInterval() {
        return this.chatRoomNotificationInterval;
    }

    public String getIMFilePath() {
        return this.imFilePath;
    }

    protected void processSelections(Set<SelectionKey> selectedKeys) throws FusionException {
        for (SelectionKey key : selectedKeys) {
            try {
                if (!key.isValid()) continue;
                if (key.isAcceptable()) {
                    this.onKeyAcceptable(key);
                }
                if (key.isWritable()) {
                    this.onKeyWritable(key);
                }
                if (!key.isReadable()) continue;
                this.onKeyReadable(key);
            }
            catch (CancelledKeyException e) {
                log.debug((Object)"CancelledKeyException caught in processSelections()");
                key.cancel();
            }
            catch (Exception e) {
                log.warn((Object)(e.getClass().getName() + " caught in processSelections() - " + e.getMessage()));
                key.cancel();
            }
        }
        selectedKeys.clear();
    }

    protected void onKeyAcceptable(SelectionKey key) throws FusionException {
        this.executeTask(ThreadPoolName.PRIMARY, this.processAcceptsThread);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void doAccepts() throws FusionException {
        SocketChannel channel = null;
        try {
            ServerSocketChannel serverSocketChannel = this.serverChannel;
            synchronized (serverSocketChannel) {
                channel = this.serverChannel.accept();
            }
            while (channel != null) {
                channel.configureBlocking(false);
                ConnectionI connection = this.serverType == ServerType.HTTP ? new ConnectionHTTP(this, channel, this.applicationContext) : new ConnectionTCP(this, channel, this.applicationContext);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Accepted connection " + connection.getDisplayName()));
                }
                this.applicationContext.getPurger().add(connection);
                channel.register(this.applicationContext.getSelector(), 1, connection);
                ServerSocketChannel serverSocketChannel2 = this.serverChannel;
                synchronized (serverSocketChannel2) {
                    channel = this.serverChannel.accept();
                }
            }
            return;
        }
        catch (Exception e) {
            log.warn((Object)(e.getClass().getName() + " caught in onKeyAcceptable()"), (Throwable)e);
            if (channel == null) return;
            try {
                channel.close();
                return;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void onKeyReadable(SelectionKey key) throws FusionException {
        try {
            ConnectionI conn = (ConnectionI)key.attachment();
            key.interestOps(key.interestOps() ^ 1);
            this.executeTask(ThreadPoolName.PRIMARY, conn.getReadCallBack());
        }
        catch (Exception e) {
            log.warn((Object)(e.getClass().getName() + " caught in onKeyReadable()"), (Throwable)e);
            key.cancel();
        }
    }

    protected void onKeyWritable(SelectionKey key) throws FusionException {
        try {
            ConnectionI conn = (ConnectionI)key.attachment();
            key.interestOps(key.interestOps() ^ 4);
            this.executeTask(ThreadPoolName.PRIMARY, conn.getWriteCallBack());
        }
        catch (Exception e) {
            log.warn((Object)(e.getClass().getName() + " caught in onKeyWritable()"), (Throwable)e);
            key.cancel();
        }
    }

    public void executeTask(ThreadPoolName name, Runnable task) {
        this.applicationContext.getGatewayThreadPool().get((Object)name).execute(task);
    }

    public void scheduleTask(ThreadPoolName name, Runnable task, long delay, TimeUnit unit) {
        if (delay == 0L) {
            this.executeTask(name, task);
        } else {
            this.applicationContext.getGatewayThreadPool().get((Object)name).schedule(task, delay, unit);
        }
    }

    public void bindServerSocket() throws IOException {
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false);
        this.serverChannel.socket().bind(new InetSocketAddress(this.host, this.port), this.backlog);
        this.serverChannel.register(this.applicationContext.getSelector(), 16);
    }

    @Override
    public void run() {
        long lastChecked = 0L;
        while (true) {
            try {
                while (true) {
                    if (System.currentTimeMillis() - lastChecked > (long)this.tooBusyCheckInterval) {
                        lastChecked = System.currentTimeMillis();
                        if (this.isTooBusy() && this.serverChannel.isOpen()) {
                            log.warn((Object)"Closing server socket");
                            this.serverChannel.close();
                        }
                    }
                    if (this.applicationContext.getSelector().select(this.selectorTimeout) > 0) {
                        this.processSelections(this.applicationContext.getSelector().selectedKeys());
                    }
                    this.registerPendingConnections();
                    if (this.selectorPauseInterval <= 0) continue;
                    Thread.sleep(this.selectorPauseInterval);
                }
            }
            catch (Exception e) {
                log.fatal((Object)(e.getClass().getName() + " caught in selection loop"), (Throwable)e);
                System.exit(-1);
                continue;
            }
            break;
        }
    }

    public void closeServerChannel() throws IOException {
        this.serverChannel.close();
    }

    public int getLocalPort() {
        return this.serverChannel.socket().getLocalPort();
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public Selector getSelector() {
        return this.applicationContext.getSelector();
    }

    public void logDebugPurgingCompleted() {
        log.debug((Object)("Purging task completed. " + this.connectionCount.get() + " " + (Object)((Object)this.getServerType()) + " connection(s) left on the adaptor. " + this.getSelector().keys().size() + " key(s) left on selector"));
    }

    public long getTimeoutInterval() {
        return this.timeoutInterval;
    }

    public static void main(String[] args) {
        GatewayMain.main(args);
    }

    public List<ConnectionI> getConnections() {
        return new ArrayList<ConnectionI>(this.connectionSet);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ThreadPoolName {
        PRIMARY,
        HTTP_TUNNEL,
        DATA_UPLOAD,
        DELAYED_LOGIN_RESPONSE_RESULT;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ServerType {
        HTTP,
        TCP,
        WS;

    }
}

