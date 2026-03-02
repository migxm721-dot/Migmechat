/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ParticipantList
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ParticipantList.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    protected int writeOpCount;
    protected ChatDefinition chatID;
    protected String[] participants = new String[0];

    public ParticipantList(ChatDefinition chatID) {
        this.chatID = chatID;
    }

    public ParticipantList(ChatDefinition chatID, String[] participants) {
        this.chatID = chatID;
        this.participants = participants;
    }

    @Override
    public String getKey() {
        return this.chatID.getChatID();
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

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.CONVERSATION;
    }

    protected void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_DEFINITION_EXPIRY_HOURS);
            store.expire(this, hrs * 3600);
            ++this.writeOpCount;
        }
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        ChatSyncStore store = stores[0];
        store.setMaster();
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_READ_ENABLED, store);
        ArrayList<ChatSyncPipelineOp> ops = new ArrayList<ChatSyncPipelineOp>();
        ops.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.READ));
        store.setSlave();
        store.pipelined(ops);
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        pipelineStore.zrange(this, 0, -1);
    }

    @Override
    public int loadPipeline(List<Object> results, int startIndex) throws FusionException {
        ArrayList<String> ps = new ArrayList<String>();
        if (startIndex < results.size()) {
            ArrayList usernamesBytes = new ArrayList((Set)results.get(startIndex));
            for (Object username : usernamesBytes) {
                ps.add((String)username);
            }
        }
        this.participants = ps.toArray(new String[ps.size()]);
        return startIndex + 1;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        this.writeOpCount = 0;
        pipelineStore.zremrangeByRank(this, 0, -1);
        ++this.writeOpCount;
        if (this.participants != null) {
            for (String username : this.participants) {
                pipelineStore.zadd((ChatSyncEntity)this, (double)System.currentTimeMillis(), username);
                ++this.writeOpCount;
            }
        }
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, pipelineStore);
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return startIndex + this.writeOpCount;
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return false;
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return false;
    }

    public String[] getUsernames() {
        return this.participants;
    }

    public int getWriteOpCount() {
        return this.writeOpCount;
    }
}

