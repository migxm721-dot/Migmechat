/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.ejb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.ChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EJBChatRoomDAOChain
extends ChatRoomDAOChain {
    private static final Logger log = Logger.getLogger(EJBChatRoomDAOChain.class);

    @Override
    public String[] getGroupChatRooms(int groupId) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETGROUPCHATROOMS)) {
            return super.getGroupChatRooms(groupId);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getGroupChatRooms(groupId);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get GroupChatRooms for group:%s", groupId), (Throwable)e);
            return super.getGroupChatRooms(groupId);
        }
    }

    @Override
    public Map<Integer, List<String>> getChatroomNamesPerCategory(boolean activeChatroomsAndCategoriesOnly) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETCHATROOMNAMESPERCATEGORY)) {
            return super.getChatroomNamesPerCategory(activeChatroomsAndCategoriesOnly);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getChatroomNamesPerCategory(activeChatroomsAndCategoriesOnly);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get ChatroomNamesPerCategory for activeChatroomsAndCategoriesOnly:%s", activeChatroomsAndCategoriesOnly), (Throwable)e);
            return super.getChatroomNamesPerCategory(activeChatroomsAndCategoriesOnly);
        }
    }

    @Override
    public List<ChatRoomData> getChatRooms(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETCHATROOMS)) {
            return super.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get ChatRooms for countryID:%s, search:%s, language:%s, includeAdultOnly:%s, searchKeywords:%s", countryID, search, language, includeAdultOnly, search), (Throwable)e);
            return super.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
        }
    }

    @Override
    public List<ChatRoomData> getFavouriteChatRooms(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETFAVCHATROOMS)) {
            return super.getFavouriteChatRooms(user);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getFavouriteChatRooms(user.getUsername());
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to ge tFavouriteChatRooms for user:%s", user), (Throwable)e);
            return super.getFavouriteChatRooms(user);
        }
    }

    @Override
    public List<ChatRoomData> getRecentChatRooms(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETRECENTCHATROOMS)) {
            return super.getRecentChatRooms(user);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getRecentChatRooms(user.getUsername());
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get RecentChatRooms for user:%s", user), (Throwable)e);
            return super.getRecentChatRooms(user);
        }
    }

    @Override
    public ChatRoomData getSimpleChatRoomData(String normalizedChatRoomName) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETSIMPLECHATROOM)) {
            return super.getSimpleChatRoomData(normalizedChatRoomName);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getSimpleChatRoomData(normalizedChatRoomName, null);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get SimpleChatRoomData for chatroom:%s", normalizedChatRoomName), (Throwable)e);
            return super.getSimpleChatRoomData(normalizedChatRoomName);
        }
    }

    @Override
    public ChatRoomData getChatRoom(String chatRoomName) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETCHATROOM)) {
            return super.getChatRoom(chatRoomName);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getChatRoom(chatRoomName);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get ChatRoomData for chatroom:%s", chatRoomName), (Throwable)e);
            return super.getChatRoom(chatRoomName);
        }
    }
}

