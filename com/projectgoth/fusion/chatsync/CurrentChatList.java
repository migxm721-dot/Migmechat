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
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.OldChatList;
import com.projectgoth.fusion.chatsync.OldChatLists;
import com.projectgoth.fusion.chatsync.ParticipantListUpdater;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.FusionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CurrentChatList
extends ChatList {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(CurrentChatList.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private ChatListVersion versionKey;
    private ChatDefinition addChatID;
    private ChatDefinition removeChatID;
    private int writeOpCount;

    public CurrentChatList(int userID) {
        super(userID, 0, new String[0]);
        this.versionKey = new ChatListVersion(userID);
    }

    public CurrentChatList(ChatListIce iceList) {
        super(iceList);
        this.versionKey = new ChatListVersion(this.userID);
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

    public void renewExpiry(SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            this.renewExpiry(store);
        }
    }

    public void renewExpiry(ChatSyncStore store) throws FusionException {
        int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CURRENT_CHAT_LIST_EXPIRY_HOURS);
        store.expire(this, hrs * 3600);
        ++this.writeOpCount;
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        ChatSyncStore store = stores[0];
        store.setMaster();
        this.renewExpiry(store);
        ArrayList<ChatSyncPipelineOp> ops = new ArrayList<ChatSyncPipelineOp>();
        ops.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.READ));
        store.setSlave();
        store.pipelined(ops);
    }

    public void update(ChatSyncStore store, ChatDefinition addChatID, ChatDefinition removeChatID) throws FusionException {
        this.update(store, addChatID, removeChatID, null, false);
    }

    public void update(ChatSyncStore store, ChatDefinition addChatID, ChatDefinition removeChatID, String usernameToAddRemoveFromChatDef) throws FusionException {
        this.update(store, addChatID, removeChatID, usernameToAddRemoveFromChatDef, false);
    }

    public void update(ChatSyncStore store, ChatDefinition addChatID, ChatDefinition removeChatID, String usernameToAddRemoveFromChatDef, boolean debug) throws FusionException {
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update with addChatID=" + addChatID + " removeChatID=" + removeChatID);
        }
        store.setMaster();
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, store);
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update: renewed expiry");
        }
        this.addChatID = addChatID;
        this.removeChatID = removeChatID;
        ArrayList<ChatSyncPipelineOp> readOps = new ArrayList<ChatSyncPipelineOp>();
        readOps.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.READ));
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update: pipelinedBinary on read ops");
        }
        store.setSlave();
        store.pipelinedBinary(readOps);
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update: building write pipeline");
        }
        this.addChat(addChatID);
        this.removeChat(removeChatID);
        int newChatListVersion = this.getVersion() + 1;
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update: archiving old chat list with version=" + newChatListVersion);
        }
        OldChatList archiveMe = new OldChatList(this.userID, newChatListVersion, this.getChatIDs());
        ChatListVersion versionKey = new ChatListVersion(this.userID);
        ArrayList<ChatSyncPipelineOp> writeOps = new ArrayList<ChatSyncPipelineOp>();
        writeOps.add(new ChatSyncPipelineOp(this, ChatSyncPipelineOp.OpType.WRITE));
        writeOps.add(new ChatSyncPipelineOp(archiveMe, ChatSyncPipelineOp.OpType.WRITE));
        writeOps.add(new ChatSyncPipelineOp(versionKey, ChatSyncPipelineOp.OpType.WRITE));
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update: calling pipelinedBinary on write pipeline for user objects");
        }
        store.setMaster();
        store.pipelinedBinary(writeOps);
        if (usernameToAddRemoveFromChatDef != null) {
            ParticipantListUpdater plu;
            ArrayList<ChatSyncPipelineOp> participsWriteOps = new ArrayList<ChatSyncPipelineOp>();
            if (addChatID != null && !addChatID.isPrivateChat() && !addChatID.isChatRoomDef()) {
                plu = new ParticipantListUpdater(addChatID, usernameToAddRemoveFromChatDef, null);
                participsWriteOps.add(new ChatSyncPipelineOp(plu, ChatSyncPipelineOp.OpType.WRITE));
            }
            if (removeChatID != null && !removeChatID.isPrivateChat() && !removeChatID.isChatRoomDef()) {
                plu = new ParticipantListUpdater(removeChatID, null, usernameToAddRemoveFromChatDef);
                participsWriteOps.add(new ChatSyncPipelineOp(plu, ChatSyncPipelineOp.OpType.WRITE));
            }
            if (log.isDebugEnabled() || debug) {
                log.info("CurrentChatList.update: calling pipelinedBinary on write pipeline for particips list");
            }
            if (participsWriteOps.size() != 0) {
                store.setMaster();
                store.pipelinedBinary(participsWriteOps);
            }
        }
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update: truncating old chat lists");
        }
        OldChatLists.truncateIfNeeded(this.userID, store);
        if (log.isDebugEnabled() || debug) {
            log.info("CurrentChatList.update: estimated current chat list size after update=" + this.getChatIDs().length + " for userID=" + this.userID);
        }
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        pipelineStore.zrange(this, 0, -1);
        this.versionKey.retrievePipeline(pipelineStore);
    }

    @Override
    public int loadPipeline(List<Object> results, int startIndex) throws FusionException {
        this.chatIDs = new ArrayList();
        if (results == null) {
            this.versionKey = new ChatListVersion(0);
            return startIndex;
        }
        if (log.isDebugEnabled()) {
            log.debug("CurrentChatList.retrieve: pipelined results=");
            for (Object o : results) {
                log.debug(o);
            }
        }
        ArrayList idsBytes = new ArrayList((Set)results.get(startIndex));
        if (log.isDebugEnabled()) {
            log.debug("CurrentChatList.retrieve: retrieved ids=" + idsBytes + " for userID=" + this.userID);
        }
        if (idsBytes != null) {
            for (Object id : idsBytes) {
                this.chatIDs.add((String)id);
            }
        }
        startIndex = this.versionKey.loadPipeline(results, startIndex + 1);
        this.chatListVersion = this.versionKey.getVersion();
        if (log.isDebugEnabled()) {
            log.debug("CurrentChatList.retrieve: retrieved chat list ver=" + this.chatListVersion + " for userID=" + this.userID);
        }
        return startIndex;
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        int maxLength;
        this.writeOpCount = 0;
        if (this.addChatID != null) {
            pipelineStore.zadd((ChatSyncEntity)this, (double)System.currentTimeMillis(), this.addChatID.getKey());
            if (log.isDebugEnabled()) {
                log.debug("CurrentChatList.update: added chatID=" + this.addChatID + " for userID=" + this.userID);
            }
            ++this.writeOpCount;
        }
        if (this.removeChatID != null) {
            pipelineStore.zrem(this, this.removeChatID.getKey());
            if (log.isDebugEnabled()) {
                log.debug("CurrentChatList.update: removed chatID=" + this.removeChatID + " for userID=" + this.userID);
            }
            ++this.writeOpCount;
        }
        if ((maxLength = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CURRENT_CHAT_LIST_MAX_LENGTH)) == 0) {
            log.debug("CurrentChatList length truncation disabled");
        } else {
            int estSize = this.getChatIDs().length;
            estSize += this.addChatID != null ? 1 : 0;
            if ((estSize += this.removeChatID != null ? 1 : 0) > maxLength) {
                int delta = estSize - maxLength;
                if (log.isDebugEnabled()) {
                    log.debug("CurrentChatList: truncating by " + delta + " elements");
                }
                pipelineStore.zremrangeByRank(this, 0, delta - 1);
                ++this.writeOpCount;
            }
        }
        this.renewExpiry(SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_WRITE_ENABLED, pipelineStore);
    }

    @Override
    public int processPipelineStoreResults(List<Object> results, int startIndex) throws FusionException {
        return startIndex + this.writeOpCount;
    }

    private class ChatIDAndTTL {
        public String chatID;
        public int ttl;

        public ChatIDAndTTL(String chatID, int ttl) {
            this.chatID = chatID;
            this.ttl = ttl;
        }
    }
}

