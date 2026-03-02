/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatListVersion;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncPipelineOp;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.packet.chatsync.FusionPktChat;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRenamer
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ChatRenamer.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private ChatDefinition chatDef;
    private String chatName;
    private RegistryPrx registryPrx;
    private String renamingUser;

    public ChatRenamer(ChatDefinition chatDef, String chatName, RegistryPrx registryPrx, String renamingUser) throws FusionException {
        this.chatDef = chatDef;
        this.chatName = chatName;
        this.registryPrx = registryPrx;
        this.renamingUser = renamingUser;
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        ChatSyncStore store = stores[0];
        this.chatDef.retrieve(stores);
        this.chatDef.setChatName(this.chatName);
        this.chatDef.store(stores);
        try {
            this.informParticipants(this.chatDef, stores);
        }
        catch (Exception e) {
            log.error("Unable to inform participants of renaming of chat=" + this.chatDef, e);
        }
    }

    private void informParticipants(ChatDefinition def, ChatSyncStore[] stores) throws Exception {
        UserPrx user;
        if (stores == null || stores.length == 0) {
            log.error("Invalid chatsync stores passed to informParticipants");
            return;
        }
        ChatSyncStore store = stores[0];
        HashSet<String> participants = new HashSet<String>(Arrays.asList(def.getParticipantUsernames()));
        participants.remove(this.renamingUser);
        ArrayList<ChatSyncPipelineOp> readOps = new ArrayList<ChatSyncPipelineOp>();
        HashMap<String, UserPrx> users = new HashMap<String, UserPrx>();
        HashMap<String, ChatListVersion> chatListVersions = new HashMap<String, ChatListVersion>();
        for (String username : participants) {
            try {
                user = this.registryPrx.findUserObject(username);
                ChatListVersion clvKey = new ChatListVersion(user.getUserData().userID);
                readOps.add(new ChatSyncPipelineOp(clvKey, ChatSyncPipelineOp.OpType.READ));
                users.put(username, user);
                chatListVersions.put(username, clvKey);
            }
            catch (Exception e) {
                log.warn("Unable to forward chat renaming to user=" + username + " (couldnt get userprx)", e);
            }
        }
        store.setSlave();
        store.pipelinedBinary(readOps);
        for (String username : participants) {
            try {
                user = (UserPrx)users.get(username);
                ChatListVersion clv = (ChatListVersion)chatListVersions.get(username);
                this.forwardToUser(def, username, user, clv);
            }
            catch (Exception e) {
                log.warn("Unable to forward chat renaming to user=" + username, e);
            }
        }
    }

    private void forwardToUser(ChatDefinition def, String username, UserPrx user, ChatListVersion clv) throws Exception {
        SessionPrx[] sessions = user.getSessions();
        FusionPktChat chat = def.toFusionPktChat((short)0, clv.getVersion(), System.currentTimeMillis(), username);
        for (SessionPrx sessionPrx : sessions) {
            try {
                sessionPrx.putSerializedPacket(chat.toSerializedBytes());
                if (!log.isDebugEnabled()) continue;
                log.debug("Forwarded renaming of chatID=" + this.chatDef + " ok to " + username + " at session=" + sessionPrx);
            }
            catch (Exception e) {
                log.warn("Unable to forward chat renaming to session=" + sessionPrx + " of user=" + username, e);
            }
        }
    }

    @Override
    public String getKey() {
        return this.chatDef.getKey();
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.CONVERSATION;
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
    }

    @Override
    public void retrievePipeline(ChatSyncStore pipelineStore) throws FusionException {
    }

    @Override
    public int loadPipeline(List<Object> pipelineResults, int startIndex) throws FusionException {
        return 0;
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
        return SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.RETRY_RENAME_CHAT_ENABLED);
    }
}

