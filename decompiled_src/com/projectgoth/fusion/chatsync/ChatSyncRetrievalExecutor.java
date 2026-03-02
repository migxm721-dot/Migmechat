/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncExecutor;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.RedisChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatSyncRetrievalExecutor
extends ChatSyncExecutor {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatSyncRetrievalExecutor.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);

    private ChatSyncRetrievalExecutor() {
    }

    public static ChatSyncRetrievalExecutor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Future<Boolean> scheduleEntityRetrieval(ChatSyncEntity entity) throws FusionException {
        ChatSyncStats.getInstance().incrementTotalReadRequests();
        return this.scheduleEntityRetrieval(entity, 0);
    }

    private Future<Boolean> scheduleEntityRetrieval(ChatSyncEntity entity, Integer tryIndex) throws FusionException {
        try {
            int queueSize = this.executor.getQueue().size();
            ChatSyncStats.getInstance().setReadQueueHighWatermark(queueSize);
            ChatSyncStats.getInstance().setReadActiveThreads(this.executor.getActiveCount());
            int maxQueueSize = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.TPOOL_TASK_QUEUE_MAX_SIZE);
            if (queueSize <= maxQueueSize) {
                log.debug("Queueing check for chat sync entities");
                return this.executor.submit(new ChatSyncEntityRetrievalWorker(entity, tryIndex));
            }
            log.warn("ChatSyncEntityRetrievalWorker queue too busy (" + queueSize + "/" + maxQueueSize + "). ");
            return null;
        }
        catch (Exception e) {
            log.error("While sceduling entity retrieval", e);
            throw new FusionException("In scheduleEntityRetrieval: " + e);
        }
    }

    public ChatSyncEntity scheduleEntityRetrievalAndWait(ChatSyncEntity entity) throws FusionException {
        if (entity.canRetryReads()) {
            ChatSyncStats.getInstance().incrementTotalReadRequests();
            int maxTries = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_TRIES_READ_SYNC);
            for (int tryIndex = 0; tryIndex < maxTries; ++tryIndex) {
                ChatSyncEntity result;
                if (tryIndex != 0) {
                    ChatSyncStats.getInstance().incrementTotalReadRetries();
                }
                if ((result = this.scheduleEntityRetrievalAndWaitInner(entity)) != null) {
                    return result;
                }
                if (tryIndex != 0) continue;
                ChatSyncStats.getInstance().incrementTotalReadInitialFails();
            }
            ChatSyncStats.getInstance().incrementTotalReadFinalFails();
            return null;
        }
        return this.scheduleEntityRetrievalAndWaitInner(entity);
    }

    public ChatSyncEntity scheduleEntityRetrievalAndWaitInner(ChatSyncEntity entity) throws FusionException {
        Future<Boolean> ftr = null;
        try {
            ftr = this.scheduleEntityRetrieval(entity, null);
            Boolean result = null;
            if (ftr != null) {
                int timeout = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.RETRIEVAL_TIMEOUT_MILLIS);
                result = ftr.get(timeout, TimeUnit.MILLISECONDS);
                return result != false ? entity : null;
            }
            return null;
        }
        catch (TimeoutException e) {
            log.error("TimeoutException storing chat sync entity " + entity, e);
            ftr.cancel(true);
            return null;
        }
        catch (Exception e) {
            log.error("exception storing chat sync entity " + entity, e);
            return null;
        }
    }

    private void retry(ChatSyncEntity entity, int newTryIndex) throws FusionException {
        int maxTries;
        if (newTryIndex == 1) {
            ChatSyncStats.getInstance().incrementTotalReadInitialFails();
        }
        if ((maxTries = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_TRIES_READ_ASYNC)) > 1 && newTryIndex < maxTries) {
            this.scheduleEntityRetrieval(entity, newTryIndex);
            ChatSyncStats.getInstance().incrementTotalReadRetries();
        } else {
            ChatSyncStats.getInstance().incrementTotalReadFinalFails();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class ChatSyncEntityRetrievalWorker
    implements Callable<Boolean> {
        private ChatSyncEntity entity;
        private Integer tryIndex;

        ChatSyncEntityRetrievalWorker(ChatSyncEntity entity, Integer tryIndex) {
            this.entity = entity;
            this.tryIndex = tryIndex;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean call() {
            long startCpu = ChatSyncStats.getInstance().getCpuTime();
            try {
                ChatSyncStore[] stores = new ChatSyncStore[]{new RedisChatSyncStore(ChatSyncStore.StorePrimacy.MASTER)};
                this.entity.retrieve(stores);
                ChatSyncStats.getInstance().incrementTotalReadSuccesses();
                Boolean bl = true;
                Object var6_7 = null;
                ChatSyncStats.getInstance().addCpuTimeForRetrieval(startCpu, this.entity.getEntityType());
                return bl;
            }
            catch (Exception e) {
                try {
                    log.error("Exception retrieving chat sync entity: " + this.entity, e);
                    try {
                        if (this.tryIndex != null && this.entity.canRetryReads()) {
                            ChatSyncRetrievalExecutor.this.retry(this.entity, this.tryIndex + 1);
                        }
                    }
                    catch (FusionException e2) {
                        log.error("Exception scheduling retry=" + (this.tryIndex + 1) + " of retrieving chatsync entity: " + this.entity, (Throwable)((Object)e2));
                    }
                    Boolean bl = false;
                    Object var6_8 = null;
                    ChatSyncStats.getInstance().addCpuTimeForRetrieval(startCpu, this.entity.getEntityType());
                    return bl;
                }
                catch (Throwable throwable) {
                    Object var6_9 = null;
                    ChatSyncStats.getInstance().addCpuTimeForRetrieval(startCpu, this.entity.getEntityType());
                    throw throwable;
                }
            }
        }
    }

    private static class SingletonHolder {
        public static final ChatSyncRetrievalExecutor INSTANCE = new ChatSyncRetrievalExecutor();

        private SingletonHolder() {
        }
    }
}

