/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.ChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.data.ChatRoomData;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRoomDAO {
    private ChatRoomDAOChain readChain;
    private ChatRoomDAOChain writeChain;

    public ChatRoomDAO(ChatRoomDAOChain readChain, ChatRoomDAOChain writeChain) {
        this.readChain = readChain;
        this.writeChain = writeChain;
    }

    public String[] getGroupChatRooms(int groupId) throws DAOException {
        return this.readChain.getGroupChatRooms(groupId);
    }

    public Map<Integer, List<String>> getChatroomNamesPerCategory(boolean activeChatroomsAndCategoriesOnly) throws DAOException {
        return this.readChain.getChatroomNamesPerCategory(activeChatroomsAndCategoriesOnly);
    }

    public List<ChatRoomData> getChatRooms(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws DAOException {
        return this.readChain.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
    }

    public List<ChatRoomData> getChatRooms(int countryID, String search) throws DAOException {
        return this.getChatRooms(countryID, search, null, true, false);
    }

    public List<ChatRoomData> getFavouriteChatRooms(UserObject user) throws DAOException {
        return this.readChain.getFavouriteChatRooms(user);
    }

    public List<String> getAllRecentChatRooms(UserObject user) throws DAOException {
        return this.readChain.getAllRecentChatRooms(user);
    }

    public List<ChatRoomData> getRecentChatRooms(List<String> allRecentChatRooms) throws DAOException {
        return this.readChain.getRecentChatRooms(allRecentChatRooms);
    }

    public List<ChatRoomData> getRecentChatRooms(UserObject user) throws DAOException {
        try {
            List<String> allRecentChatRooms = this.getAllRecentChatRooms(user);
            return this.getRecentChatRooms(allRecentChatRooms);
        }
        catch (DAOException e) {
            return this.readChain.getRecentChatRooms(user);
        }
    }

    public ChatRoomData getChatRoom(String chatRoomName) throws DAOException {
        try {
            String normalizedChatRoomName = ChatRoomUtils.normalizeChatRoomName(chatRoomName);
            ChatRoomData room = this.getSimpleChatRoomData(normalizedChatRoomName);
            if (room == null) {
                return null;
            }
            if (room.themeID != null) {
                room.theme = this.getChatRoomTheme(room.themeID);
            }
            if (room.userOwned.booleanValue()) {
                room.moderators = this.getChatRoomModerators(room);
                room.bannedUsers = this.getChatRoomBannedUsers(room);
            }
            return room;
        }
        catch (DAOException e) {
            return this.readChain.getChatRoom(chatRoomName);
        }
    }

    public ChatRoomData getSimpleChatRoomData(String normalizedChatRoomName) throws DAOException {
        return this.readChain.getSimpleChatRoomData(normalizedChatRoomName);
    }

    public Map<String, String> getChatRoomTheme(int themeID) throws DAOException {
        return this.readChain.getChatRoomTheme(themeID);
    }

    public Set<String> getChatRoomModerators(ChatRoomData chatroom) throws DAOException {
        return this.readChain.getChatRoomModerators(chatroom);
    }

    public Set<String> getChatRoomBannedUsers(ChatRoomData chatroom) throws DAOException {
        return this.readChain.getChatRoomBannedUsers(chatroom);
    }
}

