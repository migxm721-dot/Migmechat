/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.PipelineBlock
 */
package com.projectgoth.fusion.objectcache;

import com.google.gson.Gson;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OfflineMessageHelper {
    public static final String STORAGE_FAILURE_USER_MSG = "The recipient is offline and due to an internal error, migme was unable to store your message as an offline message";
    private static final String LIMIT_REACHED_MSG = "You have reached your limit for offline messaging for today";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(OfflineMessageHelper.class));
    private ThreadPoolExecutor executor;
    private AtomicLong lastSentStatsLoggingTime = new AtomicLong(0L);
    private AtomicLong totalMessagesStored = new AtomicLong(0L);
    private AtomicLong totalMessagesPushedToMigbo = new AtomicLong(0L);
    private AtomicLong lastReceivedStatsLoggingTime = new AtomicLong(0L);
    private AtomicLong totalMessagesRetrieved = new AtomicLong(0L);
    private AtomicLong totalMessagesDelivered = new AtomicLong(0L);
    private AtomicLong totalMessagesDropped = new AtomicLong(0L);

    private OfflineMessageHelper() {
        this.executor = new ThreadPoolExecutor(SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_CORE_SIZE), SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_MAX_SIZE), SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_KEEP_ALIVE_SECONDS), TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        if (SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.REFRESH_THREAD_POOL_PROPERTIES_ENABLED)) {
            new ThreadPoolPropsRefreshThread().start();
        }
    }

    public static OfflineMessageHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public StorageResult scheduleOfflineMessageStorageAndWait(MessageData message, int senderID, int recipientID) {
        MessageDestinationData dest = message.messageDestinations.get(0);
        StorageResult result = new StorageResult(STORAGE_FAILURE_USER_MSG);
        Future<StorageResult> ftr = null;
        try {
            ftr = OfflineMessageHelper.getInstance().scheduleOfflineMessageStorage(message, dest, senderID, recipientID);
            if (ftr != null) {
                int timeout = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.STORAGE_TIMEOUT_MILLIS);
                result = ftr.get(timeout, TimeUnit.MILLISECONDS);
            }
            return result;
        }
        catch (TimeoutException e) {
            log.error((Object)"TimeoutException storing offline message", (Throwable)e);
            ftr.cancel(true);
            return new StorageResult(STORAGE_FAILURE_USER_MSG);
        }
        catch (Exception e) {
            log.error((Object)"exception storing offline message", (Throwable)e);
            return new StorageResult(STORAGE_FAILURE_USER_MSG);
        }
    }

    public Future<StorageResult> scheduleOfflineMessageStorage(MessageData msg, MessageDestinationData destination, int senderID, int recipientID) throws Exception {
        int maxQueueSize;
        if (msg.contentType != MessageData.ContentTypeEnum.TEXT && msg.contentType != MessageData.ContentTypeEnum.EMOTE) {
            throw new FusionException("Offline messaging only implemented for TEXT and some EMOTE content types, not for " + (Object)((Object)msg.contentType));
        }
        if (destination.type != MessageDestinationData.TypeEnum.INDIVIDUAL) {
            throw new FusionException("Offline messaging only implemented for private messaging, not for " + (Object)((Object)destination.type));
        }
        int queueSize = this.executor.getQueue().size();
        if (queueSize <= (maxQueueSize = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_TASK_QUEUE_MAX_SIZE))) {
            log.debug((Object)("Queueing storage of offline message for user: " + destination.destination));
            OfflineMessageStorageWorker worker = new OfflineMessageStorageWorker(msg, destination, senderID, recipientID);
            return this.executor.submit(worker);
        }
        log.warn((Object)("OfflineMessageHelper queue too busy (" + queueSize + "/" + maxQueueSize + "). " + "Offline message storage failed for recipient " + destination.destination));
        return null;
    }

    public Future<Boolean> scheduleCheckForOfflineMessages(UserDataIce recipientData, SessionPrx session, ClientType deviceType, short clientVersion, IcePrxFinder ipf) throws Exception {
        if (SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.OFFLINE_MESSAGE_GUARDSET_ENABLED)) {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            if (!userEJB.isUserInMigboAccessList(recipientData.userID, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.OFFLINE_MESSAGING.value())) {
                log.debug((Object)("Cannot check for offline messages: " + recipientData.username + " not in offline messaging guardset"));
                return null;
            }
            log.debug((Object)("Can check for offline messages: " + recipientData.username + " is in offline messaging guardset"));
        } else {
            int minMigLevel = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.MIN_MIG_LEVEL_FOR_RECEIVING);
            if (minMigLevel != 0) {
                log.debug((Object)"OLM receiving control by mig level enabled");
                Integer level = MemCacheOrEJB.getUserReputationLevel(recipientData.userID);
                if (level == null) {
                    level = 1;
                }
                if (level >= minMigLevel) {
                    log.debug((Object)("Cannot check for offline messages: " + recipientData.username + " has mig level " + level + " whereas min mig level for receiving OLM=" + minMigLevel));
                    return null;
                }
            } else {
                log.debug((Object)"Can check for offline messages: offline msging guardset is disabled  and no control by mig level");
            }
        }
        int queueSize = this.executor.getQueue().size();
        int maxQueueSize = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_TASK_QUEUE_MAX_SIZE);
        if (queueSize <= maxQueueSize) {
            log.debug((Object)("Queueing check for offline messages for user: " + recipientData.username));
            return this.executor.submit(new OfflineMessageRetrievalWorker(recipientData, session, deviceType, clientVersion, ipf));
        }
        log.warn((Object)("OfflineMessageRetrievalWorker queue too busy (" + queueSize + "/" + maxQueueSize + "). " + "Deferring offline message check until next login for user " + recipientData.username));
        return null;
    }

    private void refreshThreadPoolExecutorProps() {
        int coreSizeProp = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_CORE_SIZE);
        int maxSizeProp = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_MAX_SIZE);
        int keepAliveSecondsProp = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_KEEP_ALIVE_SECONDS);
        if (this.executor.getCorePoolSize() != coreSizeProp) {
            log.info((Object)("Thread pool core size param changed: updating to=" + coreSizeProp));
            this.executor.setCorePoolSize(coreSizeProp);
        }
        if (this.executor.getMaximumPoolSize() != maxSizeProp) {
            log.info((Object)("Thread pool max size param changed: updating to=" + maxSizeProp));
            this.executor.setMaximumPoolSize(maxSizeProp);
        }
        if (this.executor.getKeepAliveTime(TimeUnit.SECONDS) != (long)keepAliveSecondsProp) {
            log.info((Object)("Thread pool keepalive time param changed: updating to=" + keepAliveSecondsProp));
            this.executor.setKeepAliveTime(keepAliveSecondsProp, TimeUnit.SECONDS);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private StorageResult storeOfflineMessage(MessageData msg, MessageDestinationData destination, int senderID, int recipientID) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Storing offline message to user destDest=" + destination.destination + " with id:" + recipientID + " and msg.username=" + msg.username + " and sender user id=" + senderID));
        }
        Jedis recipientInstance = null;
        try {
            try {
                final String msgKey = Redis.getOfflineMessageKey(recipientID);
                log.debug((Object)("Storing offline message under redis key=" + msgKey));
                Gson gson = new Gson();
                final String gsonMsg = gson.toJson((Object)msg);
                recipientInstance = Redis.getMasterInstanceForUserID(recipientID);
                List results = recipientInstance.pipelined(new PipelineBlock(){

                    public void execute() {
                        this.rpush(msgKey, new String[]{gsonMsg});
                        this.expire(msgKey, 86400 * SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.EXPIRY_DAYS));
                    }
                });
                int recipientListSize = ((Long)results.get(0)).intValue();
                log.debug((Object)("Recipient " + recipientID + " now has " + recipientListSize + " offline msgs for today"));
                if (SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.STATS_COLLECTION_ENABLED)) {
                    this.totalMessagesStored.incrementAndGet();
                    this.logSentStatsPeriodically();
                }
                if (SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.PUSH_TO_MIGBO_ENABLED)) {
                    try {
                        String pathPrefix = "/user/" + senderID + "/messages/@new";
                        MigboApiUtil apiUtil = MigboApiUtil.getInstance();
                        String jsonStr = "{\"destinations\":[" + recipientID + "],\"message\":{\"_version\":\"1.0\",\"body\":\"" + msg.messageText + "\"}}";
                        if (log.isDebugEnabled()) {
                            log.debug((Object)("Making API call to POST " + pathPrefix + " with senderID=" + senderID + " and jsonStr=" + jsonStr));
                        }
                        boolean ok = apiUtil.postAndCheckOk(pathPrefix, jsonStr);
                        log.debug((Object)("Made API call to POST " + pathPrefix + " with success=" + ok));
                        if (!ok) {
                            log.info((Object)("Completed but returned false: Migbo POST to " + pathPrefix + " jsonStr=" + jsonStr));
                        }
                        if (SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.STATS_COLLECTION_ENABLED)) {
                            this.totalMessagesPushedToMigbo.incrementAndGet();
                            this.logSentStatsPeriodically();
                        }
                    }
                    catch (Exception e) {
                        String exmsg = "Failed (exception thrown) to post offline message to migbo private messaging from senderID=" + senderID + " to recipientID=" + recipientID;
                        if (e.toString().contains("You can only private message your friends, followers and people you follow")) {
                            log.info((Object)exmsg, (Throwable)e);
                        }
                        log.error((Object)exmsg, (Throwable)e);
                    }
                }
                Object var16_20 = null;
            }
            catch (Exception e) {
                log.error((Object)("Failed to store offline message for recipient [" + destination.destination + "]" + e));
                StorageResult result = new StorageResult(StorageResult.Value.FAILURE);
                result.setError(STORAGE_FAILURE_USER_MSG);
                StorageResult storageResult = result;
                Object var16_21 = null;
                Redis.disconnect(recipientInstance, log);
                return storageResult;
            }
        }
        catch (Throwable throwable) {
            Object var16_22 = null;
            Redis.disconnect(recipientInstance, log);
            throw throwable;
        }
        Redis.disconnect(recipientInstance, log);
        return new StorageResult(StorageResult.Value.SUCCESS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void checkOfflineMessages(UserDataIce userData, SessionPrx session, ClientType deviceType, short clientVersion, IcePrxFinder ipf) {
        log.debug((Object)"Entering checkPendingOfflineMessages");
        Jedis ji = null;
        try {
            try {
                ji = Redis.getMasterInstanceForUserID(userData.userID);
                this.checkOfflineMessagesInner(userData, session, deviceType, clientVersion, ipf, ji);
            }
            catch (Exception e) {
                log.error((Object)("Exception checking offline messages for user " + userData.userID + " " + e));
                Object var9_8 = null;
                Redis.disconnect(ji, log);
                return;
            }
            Object var9_7 = null;
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            Redis.disconnect(ji, log);
            throw throwable;
        }
        Redis.disconnect(ji, log);
    }

    private void checkOfflineMessagesInner(final UserDataIce userData, SessionPrx session, ClientType deviceType, short clientVersion, IcePrxFinder ipf, Jedis ji) throws Exception {
        block11: {
            List msgsAndDelResults = ji.pipelined(new PipelineBlock(){

                public void execute() {
                    Date lastLogin = new Date(userData.lastLoginDate);
                    int maxDays = SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.EXPIRY_DAYS);
                    Calendar calEarliest = Calendar.getInstance();
                    calEarliest.setTime(new Date());
                    calEarliest.add(5, -1 * maxDays);
                    Date earliestDate = calEarliest.getTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    do {
                        String key = Redis.getOfflineMessageKey(userData.userID, cal.getTime());
                        log.debug((Object)("Pipelining check for: " + key));
                        this.lrange(key, 0L, -1L);
                        this.del(key);
                        cal.add(5, -1);
                    } while (cal.getTime().after(lastLogin) && cal.getTime().after(earliestDate));
                }
            });
            int maxOlmsgRetrievalsPerLogin = deviceType == ClientType.ANDROID && clientVersion >= 300 ? SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.RX_LIMIT_ANDROID_3PLUS) : (deviceType == ClientType.BLACKBERRY ? SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.RX_LIMIT_BLACKBERRY) : (deviceType == ClientType.MIDP2 && clientVersion >= 420 ? SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.RX_LIMIT_J2ME) : (deviceType == ClientType.MRE ? SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.RX_LIMIT_MRE) : SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.RX_LIMIT_DEFAULT))));
            log.debug((Object)("Recipient " + userData.username + ": using max offline message retrievals per login=" + maxOlmsgRetrievalsPerLogin));
            Gson gson = new Gson();
            int rxCount = 0;
            ArrayList<MessageDataIce> msgsToResend = new ArrayList<MessageDataIce>();
            int daysBefore = 0;
            while (daysBefore * 2 < msgsAndDelResults.size()) {
                List dayList = (List)msgsAndDelResults.get(daysBefore * 2);
                if (dayList.size() != 0) {
                    for (int i = dayList.size() - 1; i >= 0; --i) {
                        String msg = (String)dayList.get(i);
                        MessageData md = (MessageData)gson.fromJson(msg, MessageData.class);
                        if (rxCount < maxOlmsgRetrievalsPerLogin) {
                            log.debug((Object)("Converted json blob back to MessageData with source=" + md.source + " and username=" + md.username));
                            MessageDataIce mdi = md.toIceObject();
                            msgsToResend.add(0, mdi);
                        }
                        ++rxCount;
                    }
                }
                ++daysBefore;
            }
            if (SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.STATS_COLLECTION_ENABLED)) {
                this.totalMessagesRetrieved.addAndGet(rxCount);
                this.totalMessagesDropped.addAndGet(rxCount - msgsToResend.size());
                this.logReceivedStatsPeriodically();
            }
            for (MessageDataIce mdi : msgsToResend) {
                session.getUserProxy(userData.username).putMessage(mdi);
                log.debug((Object)("Resent offline message id=" + mdi.id + " to " + userData.username));
            }
            if (SystemProperty.getBool(SystemPropertyEntities.OfflineMessageSettings.CHECK_YOUR_MIGBO_MSGS_ALERT_ENABLED) && rxCount > maxOlmsgRetrievalsPerLogin) {
                boolean oldMidpClient;
                String msg = "You've got " + (rxCount - maxOlmsgRetrievalsPerLogin) + " more offline messages! To see them, please check your migbo messages";
                boolean bl = oldMidpClient = deviceType == ClientType.MIDP2 && clientVersion < 420;
                if (oldMidpClient) {
                    log.debug((Object)("Sending via FusionPktAlert: " + msg));
                    session.putAlertMessage(msg, "Offline messages", (short)1000);
                } else {
                    try {
                        log.debug((Object)("Sending to " + userData.username + " via migbo alert: " + msg));
                        UserNotificationServicePrx uns = ipf.getUserNotificationServiceProxy();
                        log.debug((Object)("Got UserNotificationServicePrx: uns=" + uns));
                        String alertKey = userData.username + ":OLM";
                        String alertMessage = msg;
                        HashMap<String, String> messageMap = new HashMap<String, String>();
                        messageMap.put("alert_key", alertKey);
                        messageMap.put("alert_content", alertMessage);
                        uns.notifyFusionUser(new Message(alertKey, userData.userID, userData.username, Enums.NotificationTypeEnum.SYS_ALERT.getType(), System.currentTimeMillis(), messageMap));
                        log.debug((Object)("Sent via migbo alert: " + msg));
                    }
                    catch (Exception e) {
                        log.error((Object)("While sending via UNS: " + e));
                        if (e.getCause() == null) break block11;
                        log.error((Object)("UNS exception cause=" + e.getCause()));
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void logSentStatsPeriodically() {
        OfflineMessageHelper offlineMessageHelper = this;
        synchronized (offlineMessageHelper) {
            if (System.currentTimeMillis() - this.lastSentStatsLoggingTime.get() > (long)(SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.STATS_COLLECTION_INTERVAL_MINUTES) * 60 * 1000)) {
                log.info((Object)"OLM: Offline messaging stats (sending):");
                log.info((Object)("OLM: total messages sent=" + this.totalMessagesStored.get()));
                log.info((Object)("OLM: total messages pushed to migbo=" + this.totalMessagesPushedToMigbo.get()));
                this.lastSentStatsLoggingTime.set(System.currentTimeMillis());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void logReceivedStatsPeriodically() {
        OfflineMessageHelper offlineMessageHelper = this;
        synchronized (offlineMessageHelper) {
            if (System.currentTimeMillis() - this.lastReceivedStatsLoggingTime.get() > (long)(SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.STATS_COLLECTION_INTERVAL_MINUTES) * 60 * 1000)) {
                log.info((Object)"OLM: Offline messaging stats (receiving):");
                log.info((Object)("OLM: total messages retrieved=" + this.totalMessagesRetrieved.get()));
                log.info((Object)("OLM: total messages retrieved (delivered)=" + this.totalMessagesDelivered.get()));
                log.info((Object)("OLM: total messages retrieved (dropped)=" + this.totalMessagesDropped.get()));
                this.lastReceivedStatsLoggingTime.set(System.currentTimeMillis());
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class OfflineMessageRetrievalWorker
    implements Callable<Boolean> {
        private UserDataIce recipientData;
        private SessionPrx session;
        private ClientType deviceType;
        private short clientVersion;
        private IcePrxFinder icePrxFinder;

        OfflineMessageRetrievalWorker(UserDataIce recipientData, SessionPrx session, ClientType deviceType, short clientVersion, IcePrxFinder ipf) {
            this.recipientData = recipientData;
            this.session = session;
            this.deviceType = deviceType;
            this.clientVersion = clientVersion;
            this.icePrxFinder = ipf;
        }

        @Override
        public Boolean call() {
            try {
                OfflineMessageHelper.this.checkOfflineMessages(this.recipientData, this.session, this.deviceType, this.clientVersion, this.icePrxFinder);
                return true;
            }
            catch (Exception e) {
                log.error((Object)("Unable to check offline messages for user: " + this.recipientData.username), (Throwable)e);
                return false;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class OfflineMessageStorageWorker
    implements Callable<StorageResult> {
        private MessageData msg;
        private MessageDestinationData destination;
        private int senderID;
        private int recipientID;

        public OfflineMessageStorageWorker(MessageData msg, MessageDestinationData destination, int senderID, int recipientID) throws Exception {
            this.msg = msg;
            this.destination = destination;
            this.senderID = senderID;
            this.recipientID = recipientID;
        }

        @Override
        public StorageResult call() {
            try {
                return OfflineMessageHelper.this.storeOfflineMessage(this.msg, this.destination, this.senderID, this.recipientID);
            }
            catch (Exception e) {
                log.error((Object)("Unable to store offline messages for destination: " + this.destination.destination), (Throwable)e);
                StorageResult res = new StorageResult(StorageResult.Value.FAILURE);
                res.setError("We weren't able to store your offline message this time, please try later!");
                return res;
            }
        }
    }

    private class ThreadPoolPropsRefreshThread
    extends Thread {
        private ThreadPoolPropsRefreshThread() {
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                OfflineMessageHelper.this.refreshThreadPoolExecutorProps();
                try {
                    Thread.sleep(1000 * SystemProperty.getInt(SystemPropertyEntities.OfflineMessageSettings.TPOOL_PROPS_REFRESH_INTERVAL_SECS));
                }
                catch (InterruptedException e) {
                    log.info((Object)"ThreadPoolPropsRefreshThread interrupted and shutting down");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            log.info((Object)"ThreadPoolPropsRefreshThread exiting");
        }
    }

    public static class StorageResult {
        private Value value;
        private String error;

        StorageResult(Value v) {
            this.value = v;
        }

        StorageResult(String errorArg) {
            this.value = Value.FAILURE;
            this.error = errorArg;
        }

        public boolean failed() {
            return this.value == Value.FAILURE;
        }

        public Value getValue() {
            return this.value;
        }

        public void setError(String s) {
            this.error = s;
        }

        public String getError() {
            return this.error;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Value {
            SUCCESS,
            FAILURE;

        }
    }

    private static class SingletonHolder {
        public static final OfflineMessageHelper INSTANCE = new OfflineMessageHelper();

        private SingletonHolder() {
        }
    }
}

