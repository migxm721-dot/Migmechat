/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatMessage;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.CurrentChatList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.packet.chatsync.FusionPktLatestMessagesDigest;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionPrx;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LatestMessagesDigestPusher
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(LatestMessagesDigestPusher.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private final int userID;
    private final SessionPrx session;
    private final short txnID;
    private final String parentUsername;

    public LatestMessagesDigestPusher(int userID, SessionPrx session, short txnID, String parentUsername) throws FusionException {
        this.userID = userID;
        this.session = session;
        this.txnID = txnID;
        this.parentUsername = parentUsername;
    }

    private void renewExpiry(ChatSyncEntity entity, SystemPropertyEntities.ChatSyncSettings toggle, ChatSyncStore store) throws FusionException {
        if (SystemProperty.getBool(toggle)) {
            int hrs = SystemProperty.getInt(SystemPropertyEntities.ChatSyncSettings.CHAT_CONTENT_EXPIRY_HOURS);
            store.expire(entity, hrs * 3600);
        }
    }

    @Override
    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        if (!SystemPropertyEntities.Temp.Cache.se464LmdChangesEnabled.getValue().booleanValue()) {
            this.retrieve_beforeSE464(stores);
            return;
        }
        CurrentChatList ccl = new CurrentChatList(this.userID);
        ccl.retrieve(stores);
        ArrayList<FusionPktLatestMessagesDigest.LatestMessage> results = new ArrayList<FusionPktLatestMessagesDigest.LatestMessage>();
        String[] chatIDs = ccl.getChatIDs();
        for (ChatSyncStore store : stores) {
            for (String cid : chatIDs) {
                ChatDefinition chatKey = new ChatDefinition(cid);
                store.setMaster();
                this.renewExpiry(chatKey, SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_READ_ENABLED, store);
                store.setSlave();
                byte[] lastMsgData = null;
                ChatMessage msgsKey = new ChatMessage(chatKey);
                Set<byte[]> msgs = store.zrangeBinary(msgsKey, -1, -1);
                if (msgs != null && msgs.size() != 0) {
                    ArrayList<byte[]> msgs2 = new ArrayList<byte[]>(msgs);
                    lastMsgData = msgs2.get(0);
                }
                if (lastMsgData == null) continue;
                this.processLastMessage(chatKey, lastMsgData, results);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("LatestMessagesDigestPusher: pushing details on " + results.size() + " latest messages to userID=" + this.userID);
        }
        FusionPktLatestMessagesDigest.LatestMessage[] arrResults = results.toArray(new FusionPktLatestMessagesDigest.LatestMessage[0]);
        FusionPktLatestMessagesDigest digest = new FusionPktLatestMessagesDigest(this.txnID, arrResults);
        this.session.putSerializedPacket(digest.toSerializedBytes());
    }

    private void processLastMessage(ChatDefinition chatKey, byte[] lastMsgData, List<FusionPktLatestMessagesDigest.LatestMessage> results) {
        try {
            ChatMessage unpacked = new ChatMessage(chatKey, lastMsgData);
            String externalChatID = chatKey.internalToExternalChatID(this.parentUsername);
            FusionPktLatestMessagesDigest.LatestMessage latest = new FusionPktLatestMessagesDigest.LatestMessage(externalChatID, unpacked.getGUID(), unpacked.getTimestamp(), chatKey.getChatType(), unpacked.getMessageText());
            results.add(latest);
            if (log.isDebugEnabled()) {
                log.debug("LatestMessagesDigestPusher: found latest message=" + latest.getGUID() + " for chatID=" + chatKey.getChatID());
            }
        }
        catch (Exception e) {
            log.error("Exception processing latest message for chatID=" + chatKey.getChatID() + " for user=" + this.parentUsername + " e=" + e, e);
        }
    }

    private void retrieve_beforeSE464(ChatSyncStore[] stores) throws FusionException {
        CurrentChatList ccl = new CurrentChatList(this.userID);
        ccl.retrieve(stores);
        ArrayList<FusionPktLatestMessagesDigest.LatestMessage> results = new ArrayList<FusionPktLatestMessagesDigest.LatestMessage>();
        String[] chatIDs = ccl.getChatIDs();
        for (ChatSyncStore store : stores) {
            for (String cid : chatIDs) {
                ChatDefinition chatKey = new ChatDefinition(cid);
                store.setMaster();
                this.renewExpiry(chatKey, SystemPropertyEntities.ChatSyncSettings.RENEW_EXPIRY_ON_READ_ENABLED, store);
                store.setSlave();
                byte[] lastMsgData = null;
                ChatMessage msgsKey = new ChatMessage(chatKey);
                Set<byte[]> msgs = store.zrangeBinary(msgsKey, -1, -1);
                if (msgs != null && msgs.size() != 0) {
                    ArrayList<byte[]> msgs2 = new ArrayList<byte[]>(msgs);
                    lastMsgData = msgs2.get(0);
                }
                if (lastMsgData == null) continue;
                ChatMessage unpacked = new ChatMessage(chatKey, lastMsgData);
                String externalChatID = chatKey.internalToExternalChatID(this.parentUsername);
                FusionPktLatestMessagesDigest.LatestMessage latest = new FusionPktLatestMessagesDigest.LatestMessage(externalChatID, unpacked.getGUID(), unpacked.getTimestamp(), chatKey.getChatType(), unpacked.getMessageText());
                results.add(latest);
                if (!log.isDebugEnabled()) continue;
                log.debug("LatestMessagesDigestPusher: found latest message=" + latest.getGUID() + " for chatID=" + cid);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("LatestMessagesDigestPusher: pushing details on " + results.size() + " latest messages to userID=" + this.userID);
        }
        FusionPktLatestMessagesDigest.LatestMessage[] arrResults = results.toArray(new FusionPktLatestMessagesDigest.LatestMessage[0]);
        FusionPktLatestMessagesDigest digest = new FusionPktLatestMessagesDigest(this.txnID, arrResults);
        this.session.putSerializedPacket(digest.toSerializedBytes());
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public void unpack(String storedKey, String storedValue) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return null;
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public void storePipeline(ChatSyncStore pipelineStore) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public int processPipelineStoreResults(List<Object> pipelineResults, int startIndex) throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public boolean canRetryReads() throws FusionException {
        return SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.RETRY_LATEST_MESSAGES_DIGEST_ENABLED);
    }

    @Override
    public boolean canRetryWrites() throws FusionException {
        return false;
    }
}

