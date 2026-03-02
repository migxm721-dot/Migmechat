/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatDefinition;
import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.chatsync.CurrentChatList;
import com.projectgoth.fusion.chatsync.OldChatList;
import com.projectgoth.fusion.chatsync.OldChatLists;
import com.projectgoth.fusion.chatsync.RedisChatSyncStore;
import com.projectgoth.fusion.chatsync.UserChatLists;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.log4j.Logger;

public class UserMissingChats
extends UserChatLists {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(UserMissingChats.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    protected int chatListVersion;
    protected Integer limit;
    protected Byte chatType;
    protected ConnectionPrx connection;
    protected ArrayList<ChatDefinition> missingChatDefs = new ArrayList();

    public UserMissingChats(int userID, int chatListVersion, Integer limit, Byte chatType, ConnectionPrx connection) {
        super(userID);
        this.chatListVersion = chatListVersion;
        this.limit = limit;
        this.chatType = chatType;
        this.connection = connection;
        if (log.isDebugEnabled()) {
            log.debug("UserMissingChats.UserMissingChats: chatType=" + chatType + " for userID=" + userID);
        }
    }

    public ChatDefinition[] getMissingChats() {
        return this.missingChatDefs.toArray(new ChatDefinition[0]);
    }

    public void retrieve(ChatSyncStore[] stores) throws FusionException {
        super.retrieve(stores);
        if (log.isDebugEnabled()) {
            String[] chatIDs;
            log.debug("UserMissingChats.retrieve: current chat list for userID=" + this.userID + " with chat ids");
            for (String id : chatIDs = this.currentChatList.getChatIDs()) {
                log.debug("chatID=" + id + " for userID=" + this.userID);
            }
            log.debug("");
        }
        if (this.chatListVersion != this.currentChatList.getVersion()) {
            if (log.isDebugEnabled()) {
                log.debug("UserMissingChats.retrieve: userID=" + this.userID + " has chat list ver=" + this.chatListVersion);
            }
            this.findMissingChats(this.oldChatLists, this.currentChatList, this.chatListVersion, this.chatType);
            this.markPassivatedChatrooms();
        }
    }

    private void findMissingChats(OldChatLists oldChatLists, CurrentChatList newestChatList, int clientsChatListVersion, Byte chatType) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug("findMissingChats-- clientsChatListVersion=" + clientsChatListVersion + " newestChatList version=" + newestChatList.getVersion() + " oldChatLists=" + oldChatLists + (oldChatLists != null ? " oldChatLists.size=" + oldChatLists.size() : "") + " for userID=" + this.userID);
        }
        if (clientsChatListVersion != newestChatList.getVersion()) {
            String[] missingIDs;
            OldChatList clientsList = null;
            for (int i = 0; i < oldChatLists.size(); ++i) {
                OldChatList list = oldChatLists.get(i);
                if (log.isDebugEnabled()) {
                    log.debug("findMissingChats--Checking OldChatList with version=" + list.getVersion() + "for userID=" + this.userID);
                }
                if (list.getVersion() != clientsChatListVersion) continue;
                clientsList = list;
                break;
            }
            if (log.isDebugEnabled()) {
                log.debug("findMissingChats-- found matching OldChatList=" + clientsList + "for userID=" + this.userID);
            }
            if (clientsList != null) {
                if (log.isDebugEnabled()) {
                    log.debug("findMissingChats-- clientsList version=" + clientsList.getVersion() + " clientsList.length=" + clientsList.getChatIDs().length + " for userID=" + this.userID);
                }
                missingIDs = clientsList.findMissingChats(newestChatList.getChatIDs());
            } else {
                missingIDs = newestChatList.getChatIDs();
            }
            if (log.isDebugEnabled()) {
                log.debug("findMissingChats-- no of chats to push=" + missingIDs.length + " for userID=" + this.userID);
            }
            RedisChatSyncStore store = new RedisChatSyncStore(ChatSyncStore.StorePrimacy.MASTER);
            for (String id : missingIDs) {
                if (log.isDebugEnabled()) {
                    log.debug("findMissingChats-- loading chat def for chat id=" + id + " for userID=" + this.userID);
                }
                try {
                    ChatDefinition chatDef = new ChatDefinition(id, store);
                    if (log.isDebugEnabled()) {
                        log.debug("findMissingChats-- retrieved chatdef for chatID=" + id + " for userID=" + this.userID);
                    }
                    if (chatType != null && chatDef.getChatType() != chatType.byteValue()) continue;
                    this.missingChatDefs.add(chatDef);
                }
                catch (ChatDefinition.ChatDefinitionNotFoundException e) {
                    log.info("Chat id=" + id + " not found in redis (probably expired), " + " removing from current chat list of user id=" + this.userID);
                    ChatDefinition chatKey = new ChatDefinition(id);
                    newestChatList.update(store, null, chatKey);
                }
            }
        }
    }

    private void markPassivatedChatrooms() {
        if (!SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHATROOM_SYNC_ENABLED)) {
            return;
        }
        if (this.connection == null) {
            return;
        }
        UserPrx user = this.connection.getUserObject();
        String[] currentChatrooms = user.getCurrentChatrooms();
        HashSet<String> set = new HashSet<String>(Arrays.asList(currentChatrooms));
        for (ChatDefinition def : this.missingChatDefs) {
            if (def.getChatType() != (byte)MessageDestinationData.TypeEnum.CHAT_ROOM.value()) continue;
            boolean inChatList = set.contains(def.getChatName());
            def.setIsPassivatedChat(!inChatList);
        }
    }
}

