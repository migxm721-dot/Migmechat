/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatList;
import com.projectgoth.fusion.chatsync.ChatListVersion;
import com.projectgoth.fusion.chatsync.ChatMessage;
import com.projectgoth.fusion.chatsync.ChatMessages;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.OldChatLists;
import com.projectgoth.fusion.chatsync.UserChatLists;
import com.projectgoth.fusion.chatsync.WallclockTime;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LazyStats;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

public class ChatSyncStats
extends LazyStats {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatSyncStats.class));
    private AtomicLong totalGetChatsReceived = new AtomicLong(0L);
    private AtomicLong totalGetMessagesReceived = new AtomicLong(0L);
    private AtomicLong totalChatsRetrieved = new AtomicLong(0L);
    private AtomicLong totalMessagesRetrieved = new AtomicLong(0L);
    private AtomicLong totalChatsStored = new AtomicLong(0L);
    private AtomicLong totalMessagesStored = new AtomicLong(0L);
    private AtomicLong totalGetMessageStatusEventsReceived = new AtomicLong(0L);
    private WallclockTime getChatsAsyncQueueingWallclockTime = new WallclockTime();
    private WallclockTime getChatsAsyncWallclockTime = new WallclockTime();
    private WallclockTime getChatsAsyncPeakWallclockTime = new WallclockTime();
    private WallclockTime getChatsAsyncOffpeakWallclockTime = new WallclockTime();
    private WallclockTime getMessagesAsyncQueueingWallclockTime = new WallclockTime();
    private WallclockTime getMessagesWallclockTime = new WallclockTime();
    private WallclockTime getMessagesPeakWallclockTime = new WallclockTime();
    private WallclockTime getMessagesOffpeakWallclockTime = new WallclockTime();
    private AtomicLong totalReadRequests = new AtomicLong(0L);
    private AtomicLong totalReadSuccesses = new AtomicLong(0L);
    private AtomicLong totalReadInitialFails = new AtomicLong(0L);
    private AtomicLong totalReadRetries = new AtomicLong(0L);
    private AtomicLong totalReadFinalFails = new AtomicLong(0L);
    private AtomicLong readQueueHighWatermark = new AtomicLong(0L);
    private AtomicLong readQueueCumulativeSize = new AtomicLong(0L);
    private AtomicLong readActiveThreads = new AtomicLong(0L);
    private AtomicLong totalWriteRequests = new AtomicLong(0L);
    private AtomicLong totalWriteSuccesses = new AtomicLong(0L);
    private AtomicLong totalWriteInitialFails = new AtomicLong(0L);
    private AtomicLong totalWriteRetries = new AtomicLong(0L);
    private AtomicLong totalWriteFinalFails = new AtomicLong(0L);
    private AtomicLong writeQueueHighWatermark = new AtomicLong(0L);
    private AtomicLong writeQueueCumulativeSize = new AtomicLong(0L);
    private AtomicLong writeActiveThreads = new AtomicLong(0L);
    private AtomicLong totalRedisOps = new AtomicLong(0L);
    private AtomicLong totalCpuTime = new AtomicLong(0L);
    private AtomicLong totalRedisChatStorageOps = new AtomicLong(0L);
    private AtomicLong totalRedisChatRetrievalOps = new AtomicLong(0L);
    private AtomicLong totalRedisChatListStorageOps = new AtomicLong(0L);
    private AtomicLong totalRedisChatListRetrievalOps = new AtomicLong(0L);
    private AtomicLong totalRedisMessageStorageOps = new AtomicLong(0L);
    private AtomicLong totalRedisMessageRetrievalOps = new AtomicLong(0L);
    private AtomicLong totalRedisChatStorageOpsPipelined = new AtomicLong(0L);
    private AtomicLong totalRedisChatRetrievalOpsPipelined = new AtomicLong(0L);
    private AtomicLong totalRedisChatListStorageOpsPipelined = new AtomicLong(0L);
    private AtomicLong totalRedisChatListRetrievalOpsPipelined = new AtomicLong(0L);
    private AtomicLong totalRedisMessageStorageOpsPipelined = new AtomicLong(0L);
    private AtomicLong totalRedisMessageRetrievalOpsPipelined = new AtomicLong(0L);

    protected boolean isStatsEnabled() {
        return true;
    }

    protected int getStatsIntervalMinutes() {
        return SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.STATS_COLLECTION_INTERVAL_MINUTES);
    }

    private ChatSyncStats() {
    }

    public static ChatSyncStats getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void incrementRedisStats(ChatSyncEntity entity, boolean storage) {
        if (entity instanceof ChatDefinition) {
            if (storage) {
                this.totalRedisChatStorageOps.incrementAndGet();
            } else {
                this.totalRedisChatRetrievalOps.incrementAndGet();
            }
        } else if (entity instanceof ChatList) {
            if (storage) {
                this.totalRedisChatListStorageOps.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOps.incrementAndGet();
            }
        } else if (entity instanceof ChatListVersion) {
            if (storage) {
                this.totalRedisChatListStorageOps.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOps.incrementAndGet();
            }
        } else if (entity instanceof ChatMessage) {
            if (storage) {
                this.totalRedisChatStorageOps.incrementAndGet();
            } else {
                this.totalRedisChatRetrievalOps.incrementAndGet();
            }
        } else if (entity instanceof ChatMessages) {
            if (storage) {
                this.totalRedisChatStorageOps.incrementAndGet();
            } else {
                this.totalRedisChatRetrievalOps.incrementAndGet();
            }
        } else if (entity instanceof OldChatLists) {
            if (storage) {
                this.totalRedisChatListStorageOps.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOps.incrementAndGet();
            }
        } else if (entity instanceof UserChatLists) {
            if (storage) {
                this.totalRedisChatListStorageOps.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOps.incrementAndGet();
            }
        }
    }

    public void incrementRedisStatsPipelined(ChatSyncEntity entity, boolean storage) {
        if (entity instanceof ChatDefinition) {
            if (storage) {
                this.totalRedisChatStorageOpsPipelined.incrementAndGet();
            } else {
                this.totalRedisChatRetrievalOpsPipelined.incrementAndGet();
            }
        } else if (entity instanceof ChatList) {
            if (storage) {
                this.totalRedisChatListStorageOpsPipelined.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOpsPipelined.incrementAndGet();
            }
        } else if (entity instanceof ChatListVersion) {
            if (storage) {
                this.totalRedisChatListStorageOpsPipelined.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOpsPipelined.incrementAndGet();
            }
        } else if (entity instanceof ChatMessage) {
            if (storage) {
                this.totalRedisChatStorageOpsPipelined.incrementAndGet();
            } else {
                this.totalRedisChatRetrievalOpsPipelined.incrementAndGet();
            }
        } else if (entity instanceof ChatMessages) {
            if (storage) {
                this.totalRedisChatStorageOpsPipelined.incrementAndGet();
            } else {
                this.totalRedisChatRetrievalOpsPipelined.incrementAndGet();
            }
        } else if (entity instanceof OldChatLists) {
            if (storage) {
                this.totalRedisChatListStorageOpsPipelined.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOpsPipelined.incrementAndGet();
            }
        } else if (entity instanceof UserChatLists) {
            if (storage) {
                this.totalRedisChatListStorageOpsPipelined.incrementAndGet();
            } else {
                this.totalRedisChatListRetrievalOpsPipelined.incrementAndGet();
            }
        }
    }

    public void incrementTotalGetChatsReceived() {
        this.totalGetChatsReceived.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalGetMessagesReceived() {
        this.totalGetMessagesReceived.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalGetMessageStatusEventsReceived() {
        this.totalGetMessageStatusEventsReceived.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalChatsRetrieved() {
        this.totalChatsRetrieved.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalMessagesRetrieved(long delta) {
        this.totalMessagesRetrieved.addAndGet(delta);
        this.logStatsPeriodically();
    }

    public void incrementTotalChatsStored() {
        this.totalChatsStored.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalMessagesStored() {
        this.totalMessagesStored.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalReadRequests() {
        this.totalReadRequests.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalReadSuccesses() {
        this.totalReadSuccesses.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalReadInitialFails() {
        this.totalReadInitialFails.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalReadRetries() {
        this.totalReadRetries.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalReadFinalFails() {
        this.totalReadFinalFails.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void setReadQueueHighWatermark(long readQueueSize) {
        if (readQueueSize > this.readQueueHighWatermark.get()) {
            this.readQueueHighWatermark.set(readQueueSize);
            this.readQueueCumulativeSize.addAndGet(readQueueSize);
        }
        this.logStatsPeriodically();
    }

    public void setReadActiveThreads(long count) {
        this.readActiveThreads.set(count);
        this.logStatsPeriodically();
    }

    public void incrementTotalWriteRequests() {
        this.totalWriteRequests.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalWriteSuccesses() {
        this.totalWriteSuccesses.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalWriteInitialFails() {
        this.totalWriteInitialFails.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalWriteRetries() {
        this.totalWriteRetries.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void incrementTotalWriteFinalFails() {
        this.totalWriteFinalFails.incrementAndGet();
        this.logStatsPeriodically();
    }

    public void setWriteQueueHighWatermark(long queueSize) {
        if (queueSize > this.writeQueueHighWatermark.get()) {
            this.writeQueueHighWatermark.set(queueSize);
            this.writeQueueCumulativeSize.addAndGet(queueSize);
        }
        this.logStatsPeriodically();
    }

    public void setWriteActiveThreads(long count) {
        this.writeActiveThreads.set(count);
        this.logStatsPeriodically();
    }

    public void addGetChatsAsyncQueueingWallclockTime(long millis) {
        this.getChatsAsyncQueueingWallclockTime.addRequest(millis);
        this.logStatsPeriodically();
    }

    public void addGetChatsAsyncWallclockTime(long millis) {
        this.getChatsAsyncWallclockTime.addRequest(millis);
        if (this.isPeakTime()) {
            this.getChatsAsyncPeakWallclockTime.addRequest(millis);
        } else {
            this.getChatsAsyncOffpeakWallclockTime.addRequest(millis);
        }
        this.logStatsPeriodically();
    }

    public void addGetMessagesAsyncQueueingWallclockTime(long millis) {
        this.getMessagesAsyncQueueingWallclockTime.addRequest(millis);
        this.logStatsPeriodically();
    }

    public void addGetMessagesWallclockTime(long millis) {
        this.getMessagesWallclockTime.addRequest(millis);
        if (this.isPeakTime()) {
            this.getMessagesPeakWallclockTime.addRequest(millis);
        } else {
            this.getMessagesOffpeakWallclockTime.addRequest(millis);
        }
        this.logStatsPeriodically();
    }

    public long getCpuTime() {
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CPU_TIME_STATS_ENABLED)) {
            return super.getCpuTime();
        }
        return 0L;
    }

    public void addCpuTimeForStorage(long lStartCpuTimeNanos, ChatSyncEntity.ChatSyncEntityType entityType) {
        if (entityType.equals((Object)ChatSyncEntity.ChatSyncEntityType.CONVERSATION)) {
            this.addCpuTime(lStartCpuTimeNanos, ChatSyncOpCategory.CHAT_STORAGE);
        } else if (entityType.equals((Object)ChatSyncEntity.ChatSyncEntityType.MESSAGE)) {
            this.addCpuTime(lStartCpuTimeNanos, ChatSyncOpCategory.MESSAGE_STORAGE);
        }
    }

    public void addCpuTimeForRetrieval(long lStartCpuTimeNanos, ChatSyncEntity.ChatSyncEntityType entityType) {
        if (entityType.equals((Object)ChatSyncEntity.ChatSyncEntityType.CONVERSATION)) {
            this.addCpuTime(lStartCpuTimeNanos, ChatSyncOpCategory.CHAT_RETRIEVAL);
        } else if (entityType.equals((Object)ChatSyncEntity.ChatSyncEntityType.MESSAGE)) {
            this.addCpuTime(lStartCpuTimeNanos, ChatSyncOpCategory.MESSAGE_RETRIEVAL);
        }
    }

    public void addCpuTime(long lStartCpuTimeNanos, ChatSyncOpCategory opCategory) {
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CPU_TIME_STATS_ENABLED)) {
            long delta = this.getCpuTime() - lStartCpuTimeNanos;
            this.totalCpuTime.addAndGet(delta);
            opCategory.addCpuTime(delta);
        }
    }

    protected void doLog() {
        log.info((Object)"ChatSync stats:");
        log.info((Object)("ChatSync: total GET_CHATS packets received=" + this.totalGetChatsReceived.get()));
        log.info((Object)("ChatSync: total GET_MESSAGES packets received=" + this.totalGetMessagesReceived.get()));
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.WALLCLOCK_TIME_STATS_ENABLED)) {
            log.info((Object)("ChatSync: GET_CHATS (async) queueing wallclock time=" + this.getChatsAsyncQueueingWallclockTime));
            log.info((Object)("ChatSync: GET_CHATS (async) processing wallclock time=" + this.getChatsAsyncWallclockTime));
            log.info((Object)("ChatSync: peak time: GET_CHATS (async) processing wallclock time=" + this.getChatsAsyncPeakWallclockTime));
            log.info((Object)("ChatSync: offpeak time: GET_CHATS (async) processing wallclock time=" + this.getChatsAsyncOffpeakWallclockTime));
            log.info((Object)("ChatSync: GET_MESSAGES queueing wallclock time=" + this.getMessagesAsyncQueueingWallclockTime));
            log.info((Object)("ChatSync: GET_MESSAGES processing wallclock time=" + this.getMessagesWallclockTime));
            log.info((Object)("ChatSync: peak time: GET_MESSAGES processing wallclock time=" + this.getMessagesPeakWallclockTime));
            log.info((Object)("ChatSync: offpeak time: GET_MESSAGES processing wallclock time=" + this.getMessagesOffpeakWallclockTime));
        }
        log.info((Object)("ChatSync: total chats retrieved=" + this.totalChatsRetrieved.get()));
        log.info((Object)("ChatSync: total messages retrieved=" + this.totalMessagesRetrieved.get()));
        log.info((Object)("ChatSync: total chats stored=" + this.totalChatsStored.get()));
        log.info((Object)("ChatSync: total messages stored=" + this.totalMessagesStored.get()));
        log.info((Object)("ChatSync: total read requests=" + this.totalReadRequests.get()));
        log.info((Object)("ChatSync: total read successes=" + this.totalReadSuccesses.get()));
        log.info((Object)("ChatSync: total initial read fails=" + this.totalReadInitialFails.get()));
        log.info((Object)("ChatSync: total read retries=" + this.totalReadRetries.get()));
        log.info((Object)("ChatSync: total final read fails=" + this.totalReadFinalFails.get()));
        log.info((Object)("ChatSync: read active threads=" + this.readActiveThreads.get()));
        log.info((Object)("ChatSync: read queue high watermark=" + this.readQueueHighWatermark.get()));
        if (this.totalReadRequests.get() != 0L) {
            log.info((Object)("ChatSync: read queue mean size=" + this.readQueueCumulativeSize.get() / this.totalReadRequests.get()));
        }
        log.info((Object)("ChatSync: total write requests=" + this.totalWriteRequests.get()));
        log.info((Object)("ChatSync: total write successes=" + this.totalWriteSuccesses.get()));
        log.info((Object)("ChatSync: total initial write fails=" + this.totalWriteInitialFails.get()));
        log.info((Object)("ChatSync: total write retries=" + this.totalWriteRetries.get()));
        log.info((Object)("ChatSync: total final write fails=" + this.totalWriteFinalFails.get()));
        log.info((Object)("ChatSync: write active threads=" + this.writeActiveThreads.get()));
        log.info((Object)("ChatSync: write queue high watermark=" + this.writeQueueHighWatermark.get()));
        if (this.totalWriteRequests.get() != 0L) {
            log.info((Object)("ChatSync: write queue mean size=" + this.writeQueueCumulativeSize.get() / this.totalWriteRequests.get()));
        }
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CPU_TIME_STATS_ENABLED)) {
            double totalCpuSecs = (double)this.totalCpuTime.get() / 1.0E9;
            log.info((Object)("ChatSync: total cpu time=" + totalCpuSecs));
            log.info((Object)("ChatSync: total cpu time (chat retrieval)=" + ChatSyncOpCategory.CHAT_RETRIEVAL.getCpuTimeSeconds()));
            log.info((Object)("ChatSync: total cpu time (chat storage)=" + ChatSyncOpCategory.CHAT_STORAGE.getCpuTimeSeconds()));
            log.info((Object)("ChatSync: total cpu time (message retrieval)=" + ChatSyncOpCategory.MESSAGE_RETRIEVAL.getCpuTimeSeconds()));
            log.info((Object)("ChatSync: total cpu time (message storage)=" + ChatSyncOpCategory.MESSAGE_STORAGE.getCpuTimeSeconds()));
        }
        log.info((Object)"ChatSync:");
        log.info((Object)"ChatSync: Redis ops (non-pipelined)");
        log.info((Object)("ChatSync: chat storage redis ops (non-pipelined)" + this.totalRedisChatStorageOps.get()));
        log.info((Object)("ChatSync: chat retrieval redis ops (non-pipelined)" + this.totalRedisChatRetrievalOps.get()));
        log.info((Object)("ChatSync: chatlist storage redis ops (non-pipelined)" + this.totalRedisChatListStorageOps.get()));
        log.info((Object)("ChatSync: chatlist retrieval redis ops (non-pipelined)" + this.totalRedisChatListRetrievalOps.get()));
        log.info((Object)("ChatSync: msg storage redis ops (non-pipelined)" + this.totalRedisMessageStorageOps.get()));
        log.info((Object)("ChatSync: msg retrieval redis ops (non-pipelined)" + this.totalRedisMessageRetrievalOps.get()));
        log.info((Object)"ChatSync:");
        log.info((Object)"ChatSync: Redis ops (pipelined)");
        log.info((Object)("ChatSync: chat storage redis ops (pipelined)" + this.totalRedisChatStorageOpsPipelined.get()));
        log.info((Object)("ChatSync: chat retrieval redis ops (pipelined)" + this.totalRedisChatRetrievalOpsPipelined.get()));
        log.info((Object)("ChatSync: chatlist storage redis ops (pipelined)" + this.totalRedisChatListStorageOpsPipelined.get()));
        log.info((Object)("ChatSync: chatlist retrieval redis ops (pipelined)" + this.totalRedisChatListRetrievalOpsPipelined.get()));
        log.info((Object)("ChatSync: msg storage redis ops (pipelined)" + this.totalRedisMessageStorageOpsPipelined.get()));
        log.info((Object)("ChatSync: msg retrieval redis ops (pipelined)" + this.totalRedisMessageRetrievalOpsPipelined.get()));
    }

    public long getTotalReadRequests() {
        return this.totalReadRequests.get();
    }

    public long getTotalReadSuccesses() {
        return this.totalReadSuccesses.get();
    }

    public long getTotalReadInitialFails() {
        return this.totalReadInitialFails.get();
    }

    public long getTotalReadRetries() {
        return this.totalReadRetries.get();
    }

    public long getTotalReadFinalFails() {
        return this.totalReadFinalFails.get();
    }

    public long getTotalWriteRequests() {
        return this.totalWriteRequests.get();
    }

    public long getTotalWriteSuccesses() {
        return this.totalWriteSuccesses.get();
    }

    public long getTotalWriteInitialFails() {
        return this.totalWriteInitialFails.get();
    }

    public long getTotalWriteRetries() {
        return this.totalWriteRetries.get();
    }

    public long getTotalWriteFinalFails() {
        return this.totalWriteFinalFails.get();
    }

    public WallclockTime getGetChatsAsyncWallclockTime() {
        return this.getChatsAsyncWallclockTime;
    }

    private static class SingletonHolder {
        public static final ChatSyncStats INSTANCE = new ChatSyncStats();

        private SingletonHolder() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ChatSyncOpCategory {
        CHAT_STORAGE,
        CHAT_RETRIEVAL,
        MESSAGE_STORAGE,
        MESSAGE_RETRIEVAL;

        private AtomicLong cpuTime = new AtomicLong(0L);

        public void addCpuTime(long deltaNanos) {
            this.cpuTime.addAndGet(deltaNanos);
        }

        public double getCpuTimeSeconds() {
            return (double)this.cpuTime.get() / 1.0E9;
        }
    }
}

