/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.CurrentChatList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GroupChatCreationHandler
implements ChatSyncEntity {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(GroupChatCreationHandler.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    private ChatDefinition cdGroupChat;
    private String creatorUsername;
    private String privateChatPartnerUsername;
    private GroupChatPrx groupChat;
    private ChatDefinition previousPrivateChatIdIfAny;

    public GroupChatCreationHandler(ChatDefinition cdGroupChat, String creatorUsername, String privateChatPartnerUsername, GroupChatPrx groupChat, ChatDefinition previousPrivateChatIdIfAny) throws FusionException {
        this.cdGroupChat = cdGroupChat;
        this.creatorUsername = creatorUsername;
        this.privateChatPartnerUsername = privateChatPartnerUsername;
        this.groupChat = groupChat;
        this.previousPrivateChatIdIfAny = previousPrivateChatIdIfAny;
    }

    @Override
    public ChatSyncEntity.ChatSyncEntityType getEntityType() {
        return ChatSyncEntity.ChatSyncEntityType.USER;
    }

    @Override
    public void store(ChatSyncStore[] stores) throws FusionException {
        this.cdGroupChat.store(stores);
        log.debug("GroupChatCreationHandler.store: group chat stored");
        ChatSyncStore store = stores[0];
        int[] participantUserIDs = this.groupChat.getParticipantUserIDs();
        int creatorUserID = this.groupChat.getCreatorUserID();
        Integer privateChatPartnerUserID = this.privateChatPartnerUsername != null ? Integer.valueOf(this.groupChat.getPrivateChatPartnerUserID()) : null;
        for (int userID : participantUserIDs) {
            CurrentChatList ccl = new CurrentChatList(userID);
            if (userID == creatorUserID || privateChatPartnerUserID != null && userID == privateChatPartnerUserID) {
                ccl.update(store, this.cdGroupChat, this.previousPrivateChatIdIfAny);
                continue;
            }
            ccl.update(store, this.cdGroupChat, null);
        }
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
        return true;
    }
}

