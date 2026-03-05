/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatMessage;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStats;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.MessageStatusEventKey;
import com.projectgoth.fusion.chatsync.MessageStatusEvents;
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
public class ChatMessages
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatMessages.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    protected final ChatDefinition chatID;
    protected final Long oldestTimestamp;
    protected final Long newestTimestamp;
    private final Integer maxMessages;
    private final String parentUsername;
    protected final ChatMessage chatMessageKey;
    protected final MessageStatusEvents statuses;
    protected Integer startRank;
    protected byte[] precedingMessage;
    protected ArrayList<byte[]> retrievedMessages;
    protected List<Tuple> msgs;

    public ChatMessages(ChatDefinition chatID, Long oldestTimestamp, Long newestTimestamp, Integer maxMessages, String parentUsername) throws FusionException {
        this.chatID = chatID;
        this.oldestTimestamp = oldestTimestamp;
        this.newestTimestamp = newestTimestamp;
        this.maxMessages = maxMessages;
        this.parentUsername = parentUsername;
        this.chatMessageKey = new ChatMessage(chatID);
        this.statuses = chatID.isPrivateChat() ? new MessageStatusEvents(chatID, oldestTimestamp, newestTimestamp, maxMessages, parentUsername) : null;
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.MESSAGE;
    }

    @Override
    public String getKey() {
        return this.chatID.getKey();
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
    }

    private void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_CONTENT_EXPIRY_HOURS);
            store.expire(this, hrs * 3600);
            if (this.statuses != null) {
                store.expire(this.statuses, hrs * 3600);
                String otherParty = this.chatID.internalToExternalChatID(this.parentUsername);
                MessageStatusEventKey otherPartyStatuses = new MessageStatusEventKey(this.chatID, otherParty);
                store.expire(otherPartyStatuses, hrs * 3600);
            }
        }
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("ChatMessages.retrieve: chatID=" + this.chatID);
        }
        this.retrievedMessages = new ArrayList();
        for (ChatSyncStore store : stores) {
            Set<byte[]> beforeFirst;
            ArrayList<byte[]> arr2;
            store.setMaster();
            this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_READ_ENABLED, store);
            ArrayList<ChatSyncPipelineOp> readOps = new ArrayList<ChatSyncPipelineOp>();
            readOps.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.READ));
            if (this.statuses != null) {
                readOps.add(new ChatSyncPipelineOp(this.statuses, ChatSyncPipelineOp.OpType.READ));
            }
            store.setSlave();
            store.pipelinedBinary(readOps);
            if (this.msgs == null) {
                return;
            }
            if (this.msgs.size() == 0) continue;
            this.startRank = store.zrankBinary(this.chatMessageKey, this.msgs.get(0).getBinaryElement());
            if (this.startRank > 0 && (arr2 = new ArrayList<byte[]>(beforeFirst = store.zrangeBinary(this.chatMessageKey, this.startRank - 1, this.startRank - 1))).size() != 0) {
                this.precedingMessage = arr2.get(0);
            }
            for (Tuple tuple : this.msgs) {
                this.retrievedMessages.add(tuple.getBinaryElement());
            }
        }
        ChatSyncStats.getInstance().incrementTotalMessagesRetrieved(this.retrievedMessages.size());
        if (log.isDebugEnabled()) {
            log.debug("ChatMessages.retrieve: retrieved count=" + this.retrievedMessages.size());
        }
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        long lEnd;
        long lStart = this.oldestTimestamp != null ? this.oldestTimestamp : 0L;
        long l = lEnd = this.newestTimestamp != null ? this.newestTimestamp : Long.MAX_VALUE;
        if (this.maxMessages != null) {
            pipelineStore.zrevrangeByScoreWithScores(this.chatMessageKey, lEnd, lStart, 0, this.maxMessages);
        } else {
            pipelineStore.zrangeByScoreWithScores(this.chatMessageKey, lStart, lEnd);
        }
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        if (pipelineResults == null) {
            this.msgs = new ArrayList<Tuple>();
            return startIndex;
        }
        this.msgs = new ArrayList<Tuple>((Set)pipelineResults.get(startIndex));
        if (this.maxMessages != null) {
            Collections.reverse(this.msgs);
        }
        return startIndex + 1;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return 0;
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return false;
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return false;
    }
}

