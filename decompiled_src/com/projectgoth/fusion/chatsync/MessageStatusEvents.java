/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.chatsync;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.MessageStatusEvent;
import com.projectgoth.fusion.chatsync.MessageStatusEventKey;
import com.projectgoth.fusion.chatsync.MessageStatusEventPersistable;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageStatusEvents
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(MessageStatusEvents.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    @Deprecated
    protected int writeOpCount;
    protected MessageStatusEventPersistable[] events;
    protected final Mode mode;
    protected final MessageStatusEventKey eventsKey;
    protected final Long startTime;
    protected final Long endTime;
    protected final Integer maxResults;
    protected long[] messageTimestamps;
    protected String[] messageGuids;

    public MessageStatusEvents(ChatDefinition chatKey, Long startTime, Long endTime, Integer maxResults, String messageSource) throws FusionException {
        this.mode = Mode.BY_TIMESTAMP_RANGE;
        this.eventsKey = new MessageStatusEventKey(chatKey, messageSource);
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxResults = maxResults;
        this.messageTimestamps = null;
        this.messageGuids = null;
        this.events = null;
    }

    public MessageStatusEvents(ChatDefinition chatKey, long[] messageTimestamps, String[] messageGuids, Integer maxResults, String messageSource) throws FusionException {
        this.mode = Mode.BY_MESSAGE_LIST;
        this.eventsKey = new MessageStatusEventKey(chatKey, messageSource);
        this.messageTimestamps = messageTimestamps;
        this.messageGuids = messageGuids;
        this.maxResults = maxResults;
        this.startTime = null;
        this.endTime = null;
        this.events = null;
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.MESSAGE_STATUS_EVENT;
    }

    @Override
    public String getKey() {
        return this.eventsKey.getKey();
    }

    public MessageStatusEventKey getEventsKey() {
        return this.eventsKey;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        for (ChatSyncStore store : stores) {
            ArrayList<ChatSyncPipelineOp> readOps = new ArrayList<ChatSyncPipelineOp>();
            readOps.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.READ));
            store.setSlave();
            store.pipelined(readOps);
        }
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        if (this.mode == Mode.BY_MESSAGE_LIST) {
            this.retrievePipelineByMessageList(pipelineStore);
        } else {
            this.retrievePipelineByTimestampRange(pipelineStore);
        }
    }

    private void retrievePipelineByMessageList(ChatSyncStore pipelineStore) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("retrievePipelineByMessageList with timestamps and guids request params=");
        }
        for (int i = 0; i < this.messageTimestamps.length; ++i) {
            if (log.isDebugEnabled()) {
                log.debug("   timestamp=" + this.messageTimestamps[i] + " guid=" + this.messageGuids[i]);
            }
            pipelineStore.zrangeByScore(this.eventsKey, this.messageTimestamps[i], this.messageTimestamps[i]);
        }
    }

    private void retrievePipelineByTimestampRange(ChatSyncStore pipelineStore) throws FusionException {
        long lEnd;
        long lStart = this.startTime != null ? this.startTime : 0L;
        long l = lEnd = this.endTime != null ? this.endTime : Long.MAX_VALUE;
        if (this.maxResults != null) {
            pipelineStore.zrevrangeByScoreWithScores(this.eventsKey, lEnd, lStart, 0, this.maxResults);
        } else {
            pipelineStore.zrangeByScoreWithScores(this.eventsKey, lStart, lEnd);
        }
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        if (this.mode == Mode.BY_TIMESTAMP_RANGE) {
            return this.loadPipelineByTimestampRange(pipelineResults, startIndex);
        }
        return this.loadPipelineByMessageList(pipelineResults, startIndex);
    }

    private int loadPipelineByMessageList(List<Object> pipelineResults, int startIndex) throws FusionException {
        if (pipelineResults == null) {
            this.events = new MessageStatusEventPersistable[0];
            return startIndex;
        }
        ArrayList<MessageStatusEventPersistable> results = new ArrayList<MessageStatusEventPersistable>();
        if (log.isDebugEnabled()) {
            log.debug("loadPipelineByMessageList :");
        }
        for (int i = 0; i < this.messageTimestamps.length; ++i) {
            Set eventsAtTimestamp = (Set)pipelineResults.get(startIndex + i);
            if (log.isDebugEnabled()) {
                log.debug("Result " + i + " has event count=" + eventsAtTimestamp);
            }
            for (String gson : eventsAtTimestamp) {
                MessageStatusEventPersistable mse = new MessageStatusEventPersistable(this.eventsKey.getChatID(), this.messageTimestamps[i], gson, this.eventsKey.getMessageSource());
                if (log.isDebugEnabled()) {
                    log.debug("requesting guid=" + this.messageGuids[i] + " stored event guid=" + mse.getMessageGUID());
                }
                if (!this.messageGuids[i].equals(mse.getMessageGUID())) continue;
                if (log.isDebugEnabled()) {
                    log.debug("Adding stored event with guid=" + mse.getMessageGUID() + " status=" + (Object)((Object)mse.getMessageStatus()));
                }
                results.add(mse);
            }
        }
        this.events = results.toArray(new MessageStatusEventPersistable[results.size()]);
        return startIndex + this.messageTimestamps.length;
    }

    private int loadPipelineByTimestampRange(List<Object> pipelineResults, int startIndex) throws FusionException {
        if (pipelineResults == null) {
            this.events = new MessageStatusEventPersistable[0];
            return startIndex;
        }
        Set storedEventsSet = (Set)pipelineResults.get(startIndex);
        ArrayList storedEvents = new ArrayList(storedEventsSet);
        if (this.maxResults != null) {
            Collections.reverse(storedEvents);
        }
        ArrayList<MessageStatusEventPersistable> eventsList = new ArrayList<MessageStatusEventPersistable>();
        for (Tuple tuple : storedEvents) {
            long timestamp = (long)tuple.getScore();
            String gson = tuple.getElement();
            MessageStatusEventPersistable mse = new MessageStatusEventPersistable(this.eventsKey.getChatID(), timestamp, gson, this.eventsKey.getMessageSource());
            eventsList.add(mse);
        }
        this.events = eventsList.toArray(new MessageStatusEventPersistable[eventsList.size()]);
        return startIndex + 1;
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.RETRY_GET_MESSAGE_STATUS_EVENTS_ENABLED);
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        throw new FusionException("Unimplemented");
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        throw new FusionException("Unimplemented");
    }

    public Multimap<Long, MessageStatusEvent> getEventsByTimestamp() {
        ArrayListMultimap map = ArrayListMultimap.create();
        for (MessageStatusEventPersistable event : this.events) {
            map.put((Object)event.getMessageTimestamp(), (Object)event);
        }
        return map;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum Mode {
        BY_TIMESTAMP_RANGE,
        BY_MESSAGE_LIST;

    }
}

