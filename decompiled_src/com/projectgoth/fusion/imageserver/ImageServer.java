/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.imageserver;

import Ice.Application;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.imageserver.Connection;
import com.projectgoth.fusion.imageserver.ConnectionPurger;
import com.projectgoth.fusion.imageserver.ImageCache;
import com.projectgoth.fusion.imageserver.ImageItem;
import com.projectgoth.fusion.imageserver.ImageServerAdminI;
import com.projectgoth.fusion.mogilefs.MogileFSManager;
import com.projectgoth.fusion.slice.ImageServerStats;
import com.projectgoth.fusion.stats.IceStats;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ImageServer
extends Application
implements Runnable {
    private static final String APP_NAME = "ImageServer";
    private static final String CONFIG_FILE = "ImageServer.cfg";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ImageServer.class));
    private ObjectAdapter adminAdaptor;
    private ThreadPoolExecutor pool;
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private ConnectionPurger purger;
    private ImageCache imageCache;
    private String host;
    private int port;
    private Map<Connection, Integer> pendingRegistrations = new HashMap<Connection, Integer>();
    private RequestCounter requestCounter;
    private long startTime;
    private int selectorTimeout;
    private int selectorPauseInterval;
    private int maxThreadPoolSize;
    private int timeoutInterval;
    private String mogileFSDomain;
    private String[] mogileFSTrackers;
    private int mogileFSConnectionsPerTracker;
    private int maxConcurrentImageIOAccess;
    private int maxImageCacheSize;
    private boolean cacheOriginalImage;
    private boolean cacheScaledImage;

    private boolean getSettings(String[] args) {
        Properties properties = ImageServer.communicator().getProperties();
        this.host = properties.getPropertyWithDefault("Server", "0.0.0.0");
        this.port = properties.getPropertyAsIntWithDefault("ServerPort", 80);
        this.selectorTimeout = properties.getPropertyAsIntWithDefault("SelectorTimeout", 100);
        this.selectorPauseInterval = properties.getPropertyAsIntWithDefault("SelectorPauseInterval", 10);
        this.timeoutInterval = properties.getPropertyAsIntWithDefault("IdleConnectionPurgeInterval", 0) * 1000;
        this.maxThreadPoolSize = properties.getPropertyAsIntWithDefault("MaxThreadPoolSize", Integer.MAX_VALUE);
        this.mogileFSDomain = properties.getProperty("MogileFSDomain");
        this.mogileFSTrackers = properties.getProperty("MogileFSTrackers").split("[,;]");
        this.mogileFSConnectionsPerTracker = properties.getPropertyAsIntWithDefault("MogileFSConnectionsPerTracker", 1);
        this.maxConcurrentImageIOAccess = properties.getPropertyAsIntWithDefault("MaxConcurrentImageIOAccess", 1);
        this.maxImageCacheSize = properties.getPropertyAsIntWithDefault("MaxImageCacheSize", 0);
        this.cacheOriginalImage = properties.getPropertyAsIntWithDefault("CacheOriginalImage", 1) == 1;
        this.cacheScaledImage = properties.getPropertyAsIntWithDefault("CacheScaledImage", 1) == 1;
        return true;
    }

    public ImageServerStats getStats() {
        ImageServerStats stats = ServiceStatsFactory.getImageServerStats(this.startTime);
        stats.port = this.port;
        stats.numConnectionObjects = 0;
        stats.maxConnectionObjects = 0;
        stats.requestsPerSecond = this.requestCounter.getRequestsPerSecond();
        stats.maxRequestsPerSecond = this.requestCounter.getMaxRequestsPerSecond();
        stats.cacheInfo = this.imageCache.getPerformanceSummary();
        stats.threadPoolSize = this.pool.getActiveCount();
        stats.maxThreadPoolSize = this.pool.getLargestPoolSize();
        stats.threadPoolQueueSize = this.pool.getQueue().size();
        return stats;
    }

    public ImageCache getImageCache() {
        return this.imageCache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerConnection(Connection connection, int interest) {
        Map<Connection, Integer> map = this.pendingRegistrations;
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
    private void registerPendingConnecitons() {
        Map<Connection, Integer> map = this.pendingRegistrations;
        synchronized (map) {
            for (Connection connection : this.pendingRegistrations.keySet()) {
                try {
                    int interest = this.pendingRegistrations.get(connection);
                    SelectionKey key = connection.channel.keyFor(this.selector);
                    if (key != null) {
                        interest |= key.interestOps();
                    }
                    connection.channel.register(this.selector, interest, connection);
                }
                catch (CancelledKeyException e) {
                    log.debug((Object)"Failed to register connection - key cancelled");
                }
                catch (ClosedChannelException e) {
                    log.debug((Object)"Failed to register connection - channel closed");
                }
                catch (Exception e) {
                    log.warn((Object)(e.getClass().getName() + " caught in registerPendingConnecitons() - " + e.getMessage()));
                }
            }
            this.pendingRegistrations.clear();
        }
    }

    public void onConnectionDisconnected(Connection connection) {
        this.purger.remove(connection);
        SelectionKey key = connection.channel.keyFor(this.selector);
        if (key != null) {
            key.cancel();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processSelections(Set<SelectionKey> selectedKeys) {
        Iterator<SelectionKey> i = selectedKeys.iterator();
        while (i.hasNext()) {
            Object var6_7;
            SelectionKey key = i.next();
            try {
                block8: {
                    try {
                        if (!key.isValid()) break block8;
                        if (key.isAcceptable()) {
                            this.onKeyAcceptable(key);
                        }
                        if (key.isWritable()) {
                            this.onKeyWritable(key);
                        }
                        if (!key.isReadable()) break block8;
                        this.onKeyReadable(key);
                    }
                    catch (CancelledKeyException e) {
                        log.debug((Object)"CancelledKeyException caught in processSelections()");
                        key.cancel();
                        var6_7 = null;
                        i.remove();
                        continue;
                    }
                    catch (Exception e) {
                        log.warn((Object)(e.getClass().getName() + " caught in processSelections() - " + e.getMessage()));
                        key.cancel();
                        var6_7 = null;
                        i.remove();
                        continue;
                    }
                }
                var6_7 = null;
                i.remove();
            }
            catch (Throwable throwable) {
                var6_7 = null;
                i.remove();
                throw throwable;
            }
        }
    }

    private void onKeyAcceptable(SelectionKey key) {
        block5: {
            SocketChannel channel = null;
            try {
                channel = this.serverChannel.accept();
                while (channel != null) {
                    channel.configureBlocking(false);
                    Connection connection = new Connection(this, channel);
                    this.purger.add(connection);
                    this.executeTask(connection);
                    channel = this.serverChannel.accept();
                }
            }
            catch (Exception e) {
                log.warn((Object)(e.getClass().getName() + " caught in onKeyAcceptable() - " + e.getMessage()));
                if (channel == null) break block5;
                try {
                    channel.close();
                }
                catch (Exception ie) {
                    // empty catch block
                }
            }
        }
    }

    private void onKeyReadable(SelectionKey key) {
        try {
            key.interestOps(key.interestOps() ^ 1);
            this.executeTask((Connection)key.attachment());
        }
        catch (Exception e) {
            log.warn((Object)(e.getClass().getName() + " caught in onKeyReadable() - " + e.getMessage()));
            key.cancel();
        }
    }

    private void onKeyWritable(SelectionKey key) {
        try {
            key.interestOps(key.interestOps() ^ 4);
            this.executeTask((Connection)key.attachment());
        }
        catch (Exception e) {
            log.warn((Object)(e.getClass().getName() + " caught in onKeyWritable() - " + e.getMessage()));
            key.cancel();
        }
    }

    private void executeTask(Runnable r) {
        this.requestCounter.add();
        this.pool.execute(r);
    }

    public int run(String[] arg0) {
        MogileFSManager mogileFSManager;
        if (!this.getSettings(arg0)) {
            return -1;
        }
        this.requestCounter = new RequestCounter();
        this.startTime = System.currentTimeMillis();
        try {
            mogileFSManager = new MogileFSManager(this.mogileFSDomain, this.mogileFSTrackers, this.mogileFSConnectionsPerTracker, log);
            log.info((Object)("Connected to " + this.mogileFSTrackers.length + " MogileFS trackers. " + this.mogileFSConnectionsPerTracker + " connection(s) per tracker"));
        }
        catch (Exception e) {
            log.fatal((Object)("Unable to initialize MogileFS - " + e.getMessage()));
            return -1;
        }
        this.imageCache = new ImageCache(mogileFSManager, this.maxImageCacheSize, this.cacheOriginalImage, this.cacheScaledImage);
        ImageItem.setImageIOSemaphore(new Semaphore(this.maxConcurrentImageIOAccess));
        this.pool = this.maxThreadPoolSize <= 0 || this.maxThreadPoolSize == Integer.MAX_VALUE ? (ThreadPoolExecutor)Executors.newCachedThreadPool() : (ThreadPoolExecutor)Executors.newFixedThreadPool(this.maxThreadPoolSize);
        try {
            this.selector = Selector.open();
        }
        catch (IOException e) {
            log.fatal((Object)"Unable to open selector");
            return -1;
        }
        try {
            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.configureBlocking(false);
            this.serverChannel.socket().bind(new InetSocketAddress(this.host, this.port));
            this.serverChannel.register(this.selector, 16);
        }
        catch (IOException e) {
            log.fatal((Object)("Failed to create server socket on port " + this.port + " - " + e.getMessage()));
            return -1;
        }
        IceStats.getInstance().setIceObjects(ImageServer.communicator(), null, null);
        this.adminAdaptor = ImageServer.communicator().createObjectAdapter("AdminAdapter");
        this.adminAdaptor.add((Ice.Object)new ImageServerAdminI(this), Util.stringToIdentity((String)"ImageServerAdmin"));
        this.adminAdaptor.activate();
        int scanInterval = ImageServer.communicator().getProperties().getPropertyAsIntWithDefault("IdleConnetionScanIntrval", 60) * 1000;
        this.purger = new ConnectionPurger(this.timeoutInterval, log);
        new Timer().schedule((TimerTask)this.purger, scanInterval, (long)scanInterval);
        log.info((Object)("Image Server started - Port: " + this.serverChannel.socket().getLocalPort() + " Cache Size: " + new DecimalFormat().format((double)this.maxImageCacheSize / 1024.0) + " KB\n"));
        this.pool.execute(this);
        ImageServer.communicator().waitForShutdown();
        log.info((Object)"Terminating server");
        try {
            this.serverChannel.close();
        }
        catch (IOException e) {
            // empty catch block
        }
        this.pool.shutdownNow();
        log.info((Object)"Server terminated");
        return 0;
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    if (this.selector.select(this.selectorTimeout) > 0) {
                        this.processSelections(this.selector.selectedKeys());
                    }
                    this.registerPendingConnecitons();
                    if (this.selectorPauseInterval <= 0) continue;
                    Thread.sleep(this.selectorPauseInterval);
                }
            }
            catch (Exception e) {
                log.warn((Object)(e.getClass().getName() + " caught in selection loop"), (Throwable)e);
                log.info((Object)"Reinitializing selector");
                try {
                    this.selector.close();
                    this.selector = Selector.open();
                    this.serverChannel.register(this.selector, 16);
                }
                catch (Exception ie) {
                    log.fatal((Object)(ie.getClass().getName() + " caught in while reinitializing selector"), (Throwable)ie);
                    System.exit(-1);
                }
                log.info((Object)"Selector reinitialized");
                continue;
            }
            break;
        }
    }

    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((Object)"ImageServer version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        ImageServer imageServer = new ImageServer();
        String configFile = args.length > 0 ? args[0] : CONFIG_FILE;
        int status = imageServer.main(imageServer.getClass().getName(), args, configFile);
        System.exit(status);
    }
}

