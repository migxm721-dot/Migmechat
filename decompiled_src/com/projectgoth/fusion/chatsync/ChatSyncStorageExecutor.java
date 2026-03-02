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
public class ChatSyncStorageExecutor
extends ChatSyncExecutor {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatSyncStorageExecutor.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    public static final String STORAGE_FAILURE_USER_MSG = "Due to an internal error, migme was unable to store chat sync info";

    private ChatSyncStorageExecutor() {
    }

    public static ChatSyncStorageExecutor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public StorageResult scheduleStorageAndWait(ChatSyncEntity entity) throws FusionException {
        if (entity.canRetryWrites()) {
            ChatSyncStats.getInstance().incrementTotalWriteRequests();
            int maxTries = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_TRIES_WRITE_SYNC);
            StorageResult result = new StorageResult(STORAGE_FAILURE_USER_MSG);
            for (int tryIndex = 0; tryIndex < maxTries; ++tryIndex) {
                if (tryIndex != 0) {
                    ChatSyncStats.getInstance().incrementTotalWriteRetries();
                }
                if (!(result = this.scheduleStorageAndWaitInner(entity)).failed()) {
                    return result;
                }
                if (tryIndex != 0) continue;
                ChatSyncStats.getInstance().incrementTotalWriteInitialFails();
            }
            ChatSyncStats.getInstance().incrementTotalWriteFinalFails();
            return result;
        }
        return this.scheduleStorageAndWaitInner(entity);
    }

    private StorageResult scheduleStorageAndWaitInner(ChatSyncEntity entity) {
        StorageResult result = new StorageResult(STORAGE_FAILURE_USER_MSG);
        Future<StorageResult> ftr = null;
        try {
            ftr = ChatSyncStorageExecutor.getInstance().scheduleStorage(entity, null);
            if (ftr != null) {
                int timeout = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.STORAGE_TIMEOUT_MILLIS);
                result = ftr.get(timeout, TimeUnit.MILLISECONDS);
            }
            return result;
        }
        catch (TimeoutException e) {
            log.error("TimeoutException storing chat sync entity " + entity, e);
            ftr.cancel(true);
            return new StorageResult(STORAGE_FAILURE_USER_MSG);
        }
        catch (Exception e) {
            log.error("exception storing chat sync entity " + entity, e);
            return new StorageResult(STORAGE_FAILURE_USER_MSG);
        }
    }

    public Future<StorageResult> scheduleStorage(ChatSyncEntity entity) throws FusionException {
        ChatSyncStats.getInstance().incrementTotalWriteRequests();
        return this.scheduleStorage(entity, 0);
    }

    private Future<StorageResult> scheduleStorage(ChatSyncEntity entity, Integer tryIndex) throws FusionException {
        try {
            int queueSize = this.executor.getQueue().size();
            ChatSyncStats.getInstance().setWriteQueueHighWatermark(queueSize);
            ChatSyncStats.getInstance().setWriteActiveThreads(this.executor.getActiveCount());
            int maxQueueSize = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.TPOOL_TASK_QUEUE_MAX_SIZE);
            if (queueSize <= maxQueueSize) {
                log.debug("Queueing storage of entity: " + entity);
                ChatSyncStorageWorker worker = new ChatSyncStorageWorker(entity, tryIndex);
                return this.executor.submit(worker);
            }
            log.warn("ChatSyncExecutor queue too busy (" + queueSize + "/" + maxQueueSize + "). " + "ChatSyncEntity storage failed for entity " + entity);
            return null;
        }
        catch (Exception e) {
            log.error("While sceduling entity storage", e);
            throw new FusionException("In scheduleStorage: " + e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private StorageResult storeEntity(ChatSyncEntity entity) throws Exception {
        long startCpu = ChatSyncStats.getInstance().getCpuTime();
        try {
            try {
                ChatSyncStore[] stores = new ChatSyncStore[]{new RedisChatSyncStore(ChatSyncStore.StorePrimacy.MASTER)};
                entity.store(stores);
            }
            catch (Exception e) {
                log.error("Failed to store chat sync entity " + entity, e);
                StorageResult result = new StorageResult(StorageResult.Value.FAILURE);
                result.setError(STORAGE_FAILURE_USER_MSG);
                StorageResult storageResult = result;
                Object var8_6 = null;
                ChatSyncStats.getInstance().addCpuTimeForStorage(startCpu, entity.getEntityType());
                return storageResult;
            }
            Object var8_5 = null;
            ChatSyncStats.getInstance().addCpuTimeForStorage(startCpu, entity.getEntityType());
        }
        catch (Throwable throwable) {
            Object var8_7 = null;
            ChatSyncStats.getInstance().addCpuTimeForStorage(startCpu, entity.getEntityType());
            throw throwable;
        }
        return new StorageResult(StorageResult.Value.SUCCESS);
    }

    private void retry(ChatSyncEntity entity, int newTryIndex) throws FusionException {
        int maxTries;
        if (newTryIndex == 1) {
            ChatSyncStats.getInstance().incrementTotalWriteInitialFails();
        }
        if ((maxTries = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.MAX_TRIES_WRITE_ASYNC)) > 1 && newTryIndex < maxTries) {
            this.scheduleStorage(entity, newTryIndex);
            ChatSyncStats.getInstance().incrementTotalWriteRetries();
        } else {
            ChatSyncStats.getInstance().incrementTotalWriteFinalFails();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class ChatSyncStorageWorker
    implements Callable<StorageResult> {
        private ChatSyncEntity entity;
        private Integer tryIndex;

        public ChatSyncStorageWorker(ChatSyncEntity entity, Integer tryIndex) throws Exception {
            this.entity = entity;
            this.tryIndex = tryIndex;
        }

        @Override
        public StorageResult call() {
            try {
                StorageResult result = ChatSyncStorageExecutor.this.storeEntity(this.entity);
                if (result != null) {
                    if (result.failed()) {
                        if (this.tryIndex != null && this.entity.canRetryWrites()) {
                            ChatSyncStorageExecutor.this.retry(this.entity, this.tryIndex + 1);
                        }
                    } else {
                        ChatSyncStats.getInstance().incrementTotalWriteSuccesses();
                    }
                }
                return result;
            }
            catch (Exception e) {
                log.error("Unable to store chat sync entity: " + this.entity, e);
                try {
                    if (this.tryIndex != null && this.entity.canRetryWrites()) {
                        ChatSyncStorageExecutor.this.retry(this.entity, this.tryIndex + 1);
                    }
                }
                catch (FusionException e2) {
                    log.error("Exception scheduling retry=" + (this.tryIndex + 1) + " of chatsync storer: " + this.entity, (Throwable)((Object)e2));
                }
                StorageResult res = new StorageResult(StorageResult.Value.FAILURE);
                res.setError("Unable to store chat sync entity");
                return res;
            }
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
        public static final ChatSyncStorageExecutor INSTANCE = new ChatSyncStorageExecutor();

        private SingletonHolder() {
        }
    }
}

