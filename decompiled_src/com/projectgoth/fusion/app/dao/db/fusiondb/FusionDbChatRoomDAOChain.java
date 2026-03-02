/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.ChatRoomDAOChain;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.cache.RecentChatRoomList;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionDbChatRoomDAOChain
extends ChatRoomDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbChatRoomDAOChain.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getGroupChatRooms(int groupId) throws DAOException {
        String[] stringArray;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String[] chatroomArr;
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select name from chatroom where groupid = ? and status = ?");
            ps.setInt(1, groupId);
            ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            LinkedList<String> chatrooms = new LinkedList<String>();
            while (rs.next()) {
                chatrooms.add(rs.getString("name"));
            }
            rs.close();
            stringArray = chatroomArr = chatrooms.toArray(new String[chatrooms.size()]);
            Object var9_10 = null;
        }
        catch (SQLException e) {
            String[] stringArray2;
            try {
                log.error((Object)String.format("Failed to get GroupChatRooms for groupid:%s", groupId), (Throwable)e);
                stringArray2 = super.getGroupChatRooms(groupId);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return stringArray2;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return stringArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Integer, List<String>> getChatroomNamesPerCategory(boolean activeChatroomsAndCategoriesOnly) throws DAOException {
        HashMap<Integer, List<String>> hashMap;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            String sql = "SELECT c2crl.chatroomcategorylistid, c.name FROM chatroom c     ,chatroomcategorylist crl \t   ,chatroomtochatroomcategorylist c2crl WHERE c.id = c2crl.chatroomid AND crl.id = c2crl.chatroomcategorylistid ";
            if (activeChatroomsAndCategoriesOnly) {
                sql = sql + "AND crl.status = ? AND c.status = ? ";
            }
            sql = sql + "ORDER BY c2crl.orderIndex DESC";
            ps = conn.prepareStatement(sql);
            if (activeChatroomsAndCategoriesOnly) {
                ps.setInt(1, ChatroomCategoryData.StatusEnum.ACTIVE.value());
                ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
            }
            rs = ps.executeQuery();
            HashMap<Integer, List<String>> chatroomNamesPerCategory = new HashMap<Integer, List<String>>();
            while (rs.next()) {
                int categoryID = rs.getInt("chatroomcategorylistid");
                if (!chatroomNamesPerCategory.containsKey(categoryID)) {
                    chatroomNamesPerCategory.put(categoryID, new ArrayList());
                }
                ((List)chatroomNamesPerCategory.get(categoryID)).add(rs.getString("name"));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully get ChatroomNamesPerCategory from fusion databse with activeChatroomsAndCategoriesOnly:%s", activeChatroomsAndCategoriesOnly));
            }
            hashMap = chatroomNamesPerCategory;
            Object var9_11 = null;
        }
        catch (SQLException e) {
            Map<Integer, List<String>> map;
            try {
                log.error((Object)String.format("Failed to get ChatroomNamesPerCategory for activeChatroomsAndCategoriesOnly:%s", activeChatroomsAndCategoriesOnly), (Throwable)e);
                map = super.getChatroomNamesPerCategory(activeChatroomsAndCategoriesOnly);
                Object var9_12 = null;
            }
            catch (Throwable throwable) {
                Object var9_13 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return map;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashMap;
    }

    @Override
    public List<ChatRoomData> getChatRooms(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws DAOException {
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.ENABLED_REFINED_SQL_FOR_SEARCHING_CHATROOM)) {
            return this.getChatRoomsV2(countryID, search, language, includeAdultOnly, searchKeywords);
        }
        return this.getChatRoomsV1(countryID, search, language, includeAdultOnly, searchKeywords);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<ChatRoomData> getChatRoomsV1(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws DAOException {
        LinkedList<ChatRoomData> adultRooms;
        LinkedList<ChatRoomData> chatRooms;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block33: {
            ChatRoomData exactMatch;
            LinkedList<ChatRoomData> languageMatchRooms;
            boolean languageSearch;
            block32: {
                conn = null;
                ps = null;
                rs = null;
                try {
                    try {
                        int maxChatRoomsReturned = 33;
                        try {
                            maxChatRoomsReturned = SystemProperty.getInt("MaxChatRoomsReturned");
                        }
                        catch (NoSuchFieldException e) {
                            log.error((Object)"Failed to get MaxChatRoomsReturned from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
                        }
                        chatRooms = new LinkedList<ChatRoomData>();
                        adultRooms = new LinkedList<ChatRoomData>();
                        conn = DBUtils.getFusionReadConnection();
                        boolean performingSearch = StringUtils.hasLength((String)search);
                        if (performingSearch) {
                            String searchLike = search.trim().replaceAll("[\\*%]", "");
                            int minChatRoomSearchLength = SystemProperty.getInt("MinChatRoomSearchLength", 0);
                            if (searchLike.length() < minChatRoomSearchLength) {
                                throw new DAOException("Search string must have at least " + minChatRoomSearchLength + " characters");
                            }
                            searchLike = searchLike.replaceAll("_", "\\\\_") + "%";
                            String sql = "(select chatroom.*, null as keywords, 1 as sortorder from chatroom where name = ? and status = 1) union (select chatroom.*, null as keywords, 2 as sortorder from chatroom where name like ? and status = 1 ";
                            if (!includeAdultOnly) {
                                sql = sql + "and chatroom.adultonly=0 ";
                            }
                            sql = sql + "order by datelastaccessed desc limit ?) ";
                            if (searchKeywords) {
                                sql = sql + "union (select chatroom.*, group_concat(keyword.keyword) as keywords, 2 as sortorder from chatroom left join chatroomkeyword on chatroom.id=chatroomkeyword.chatroomid left join keyword on chatroomkeyword.keywordid=keyword.id where keyword.keyword = ? and chatroom.status = 1 ";
                                if (!includeAdultOnly) {
                                    sql = sql + "and chatroom.adultonly=0 ";
                                }
                                sql = sql + "group by id, name, description, type, creator, chatroomcategoryid, primarycountryid, secondarycountryid, locationid, groupid, adultonly, maximumsize, userowned, allowkicking, allowuserkeywords, allowBots, language, datecreated, datelastaccessed, status order by datelastaccessed desc limit ?) ";
                            }
                            sql = sql + "order by sortorder, datelastaccessed desc limit ?";
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, search);
                            ps.setString(2, searchLike);
                            ps.setInt(3, maxChatRoomsReturned);
                            if (searchKeywords) {
                                ps.setString(4, search);
                                ps.setInt(5, maxChatRoomsReturned);
                                ps.setInt(6, maxChatRoomsReturned);
                            } else {
                                ps.setInt(4, maxChatRoomsReturned);
                            }
                            rs = ps.executeQuery();
                            languageSearch = performingSearch && StringUtils.hasLength((String)language);
                            languageMatchRooms = null;
                            if (languageSearch) {
                                languageMatchRooms = new LinkedList<ChatRoomData>();
                            }
                            exactMatch = null;
                            break block32;
                        }
                        ps = conn.prepareStatement("select chatroom.*, null as keywords from chatroom where status = ? order by datelastaccessed desc limit ?");
                        ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
                        ps.setInt(2, maxChatRoomsReturned);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            ChatRoomData room = new ChatRoomData(rs);
                            if (room.adultOnly == null || room.adultOnly.booleanValue()) {
                                adultRooms.add(room);
                                continue;
                            }
                            chatRooms.add(room);
                        }
                        break block33;
                    }
                    catch (DAOException e) {
                        log.error((Object)String.format("Failed to get ChatRooms for countryID:%s, search:%s, language:%s, includeAdultOnly:%s, searchKeywords:%s", countryID, search, language, includeAdultOnly, search));
                        throw e;
                    }
                    catch (SQLException e) {
                        log.error((Object)String.format("Failed to get ChatRooms for countryID:%s, search:%s, language:%s, includeAdultOnly:%s, searchKeywords:%s", countryID, search, language, includeAdultOnly, search), (Throwable)e);
                        List<ChatRoomData> list = super.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
                        Object var26_38 = null;
                        DBUtils.closeResource(rs, ps, conn, log);
                        return list;
                    }
                }
                catch (Throwable throwable) {
                    Object var26_39 = null;
                    DBUtils.closeResource(rs, ps, conn, log);
                    throw throwable;
                }
            }
            while (rs.next()) {
                ChatRoomData room = new ChatRoomData(rs);
                if (room.name.equalsIgnoreCase(search)) {
                    exactMatch = room;
                    continue;
                }
                if (room.adultOnly == null || room.adultOnly.booleanValue()) {
                    adultRooms.add(room);
                    continue;
                }
                if (languageSearch && room.language != null && room.language.equals(language)) {
                    languageMatchRooms.add(room);
                    continue;
                }
                chatRooms.add(room);
            }
            if (languageSearch) {
                chatRooms.addAll(0, languageMatchRooms);
            }
            if (exactMatch != null) {
                chatRooms.add(0, exactMatch);
            }
        }
        if (adultRooms.size() > 0) {
            int chatRoomInitialPageSize = 33;
            try {
                chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomInitialPageSize");
            }
            catch (NoSuchFieldException e) {
                log.error((Object)"Failed to get ChatRoomInitialPageSize from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
            }
            int chatRoomPageSize = 33;
            try {
                chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomPageSize");
            }
            catch (NoSuchFieldException e) {
                log.error((Object)"Failed to get ChatRoomPageSize from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
            }
            int chatRoomCleanPages = 33;
            try {
                chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomCleanPages");
            }
            catch (NoSuchFieldException e) {
                log.error((Object)"Failed to get ChatRoomCleanPages from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
            }
            int cleanRoomsToShow = chatRoomInitialPageSize + chatRoomPageSize * chatRoomCleanPages;
            double adultRoomsPerPage = (double)adultRooms.size() / ((double)(chatRooms.size() - cleanRoomsToShow) / (double)chatRoomPageSize);
            double roundingRatio = adultRoomsPerPage - (double)((int)adultRoomsPerPage);
            for (int i = cleanRoomsToShow; i < chatRooms.size() && adultRooms.size() > 0; i += chatRoomPageSize) {
                double noOfRooms = secureRandom.nextDouble() > roundingRatio ? Math.floor(adultRoomsPerPage) : Math.ceil(adultRoomsPerPage);
                int j = 0;
                while ((double)j < noOfRooms && adultRooms.size() > 0) {
                    chatRooms.add(i, (ChatRoomData)adultRooms.remove(0));
                    ++j;
                }
            }
            chatRooms.addAll(adultRooms);
        }
        LinkedList<ChatRoomData> linkedList = chatRooms;
        Object var26_37 = null;
        DBUtils.closeResource(rs, ps, conn, log);
        return linkedList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<ChatRoomData> getChatRoomsV2(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws DAOException {
        block44: {
            block43: {
                conn = null;
                ps = null;
                rs = null;
                stmt = null;
                try {
                    try {
                        maxChatRoomsReturned = 33;
                        try {
                            maxChatRoomsReturned = SystemProperty.getInt("MaxChatRoomsReturned");
                        }
                        catch (NoSuchFieldException e) {
                            FusionDbChatRoomDAOChain.log.error((Object)"Failed to get MaxChatRoomsReturned from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
                        }
                        chatRooms = new LinkedList<ChatRoomData>();
                        adultRooms = new LinkedList<ChatRoomData>();
                        conn = DBUtils.getFusionReadConnection();
                        performingSearch = StringUtils.hasLength((String)search);
                        if (performingSearch) {
                            searchLike = search.trim().replaceAll("[\\*%]", "");
                            minChatRoomSearchLength = SystemProperty.getInt("MinChatRoomSearchLength", 0);
                            if (searchLike.length() < minChatRoomSearchLength) {
                                throw new DAOException("Search string must have at least " + minChatRoomSearchLength + " characters");
                            }
                            searchLike = searchLike.replaceAll("_", "\\\\_") + "%";
                            retrieveChatroomID = "(select chatroom.id, chatroom.adultonly, datelastaccessed from chatroom where name like ? and status = 1 order by datelastaccessed desc limit ?) ";
                            if (searchKeywords) {
                                retrieveChatroomID = retrieveChatroomID + " union all ";
                                retrieveChatroomID = retrieveChatroomID + "(select chatroom.id, chatroom.adultonly, datelastaccessed from chatroom join chatroomkeyword on chatroom.id=chatroomkeyword.chatroomid join keyword on chatroomkeyword.keywordid=keyword.id where keyword.keyword = ? and chatroom.status = 1 order by datelastaccessed desc limit ?) order by datelastaccessed desc";
                            }
                            ps = conn.prepareStatement(retrieveChatroomID);
                            ps.setString(1, searchLike);
                            ps.setInt(2, maxChatRoomsReturned);
                            if (searchKeywords) {
                                ps.setString(3, search);
                                ps.setInt(4, maxChatRoomsReturned);
                            }
                            rs = ps.executeQuery();
                            chatroomIDSet = new LinkedHashSet<Integer>();
                            chatroomIdCount = 0;
                            break block43;
                        }
                        ps = conn.prepareStatement("select chatroom.*, null as keywords from chatroom where status = ? order by datelastaccessed desc limit ?");
                        ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
                        ps.setInt(2, maxChatRoomsReturned);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            room = new ChatRoomData(rs);
                            if (room.adultOnly.booleanValue()) {
                                adultRooms.add(room);
                                continue;
                            }
                            chatRooms.add(room);
                        }
                        break block44;
                    }
                    catch (DAOException e) {
                        FusionDbChatRoomDAOChain.log.error((Object)String.format("Failed to get ChatRooms for countryID:%s, search:%s, language:%s, includeAdultOnly:%s, searchKeywords:%s", new Object[]{countryID, search, language, includeAdultOnly, search}));
                        throw e;
                    }
                    catch (SQLException e) {
                        FusionDbChatRoomDAOChain.log.error((Object)String.format("Failed to get ChatRooms for countryID:%s, search:%s, language:%s, includeAdultOnly:%s, searchKeywords:%s", new Object[]{countryID, search, language, includeAdultOnly, search}), (Throwable)e);
                        var11_15 = super.getChatRooms(countryID, search, language, includeAdultOnly, searchKeywords);
                        var27_38 = null;
                        DBUtils.closeResource(rs, ps, conn, FusionDbChatRoomDAOChain.log);
                        try {
                            if (stmt == null) return var11_15;
                            stmt.close();
                            return var11_15;
                        }
                        catch (SQLException e) {
                            stmt = null;
                            FusionDbChatRoomDAOChain.log.warn((Object)"Failed to close statement", (Throwable)e);
                        }
                        return var11_15;
                    }
                }
                catch (Throwable var26_54) {
                    var27_39 = null;
                    DBUtils.closeResource(rs, ps, conn, FusionDbChatRoomDAOChain.log);
                    try {}
                    catch (SQLException e) {
                        stmt = null;
                        FusionDbChatRoomDAOChain.log.warn((Object)"Failed to close statement", (Throwable)e);
                        throw var26_54;
                    }
                    if (stmt == null) throw var26_54;
                    stmt.close();
                    throw var26_54;
                }
            }
            while (rs.next() && chatroomIdCount < maxChatRoomsReturned) {
                if (false == includeAdultOnly && rs.getInt(2) == 1) continue;
                chatroomIDSet.add(rs.getInt(1));
                ++chatroomIdCount;
            }
            if (chatroomIDSet.isEmpty()) {
                var19_32 = Collections.emptyList();
                var27_36 = null;
                DBUtils.closeResource(rs, ps, conn, FusionDbChatRoomDAOChain.log);
                ** try [egrp 8[TRYBLOCK] [14 : 1249->1264)] { 
lbl93:
                // 1 sources

                if (stmt == null) return var19_32;
                stmt.close();
                return var19_32;
lbl96:
                // 1 sources

                catch (SQLException e) {
                    stmt = null;
                    FusionDbChatRoomDAOChain.log.warn((Object)"Failed to close statement", (Throwable)e);
                }
                return var19_32;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            retrieveChatroomInfo = "select chatroom.*, group_concat(keyword.keyword) as keywords from chatroom left join chatroomkeyword on chatroom.id=chatroomkeyword.chatroomid left join keyword on chatroomkeyword.keywordid=keyword.id where chatroom.id in (%s) group by chatroom.id order by field(chatroom.id, %s);";
            sb = new StringBuilder();
            for (Integer id : chatroomIDSet) {
                sb.append(id + ",");
            }
            chatroomIDList = sb.substring(0, sb.length() - 1);
            retrieveChatroomInfo = String.format(retrieveChatroomInfo, new Object[]{chatroomIDList, chatroomIDList});
            stmt = conn.createStatement();
            rs = stmt.executeQuery(retrieveChatroomInfo);
            languageSearch = performingSearch != false && StringUtils.hasLength((String)language) != false;
            languageMatchRooms = null;
            if (languageSearch) {
                languageMatchRooms = new LinkedList<ChatRoomData>();
            }
            exactMatch = null;
            while (rs.next()) {
                room = new ChatRoomData(rs);
                if (room.name.equalsIgnoreCase(search)) {
                    exactMatch = room;
                    continue;
                }
                if (room.adultOnly == null || room.adultOnly.booleanValue()) {
                    adultRooms.add(room);
                    continue;
                }
                if (languageSearch && room.language != null && room.language.equals(language)) {
                    languageMatchRooms.add(room);
                    continue;
                }
                chatRooms.add(room);
            }
            if (languageSearch) {
                chatRooms.addAll(0, languageMatchRooms);
            }
            if (exactMatch != null) {
                chatRooms.add(0, exactMatch);
            }
        }
        if (adultRooms.size() > 0) {
            chatRoomInitialPageSize = 33;
            try {
                chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomInitialPageSize");
            }
            catch (NoSuchFieldException e) {
                FusionDbChatRoomDAOChain.log.error((Object)"Failed to get ChatRoomInitialPageSize from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
            }
            chatRoomPageSize = 33;
            try {
                chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomPageSize");
            }
            catch (NoSuchFieldException e) {
                FusionDbChatRoomDAOChain.log.error((Object)"Failed to get ChatRoomPageSize from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
            }
            chatRoomCleanPages = 33;
            try {
                chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomCleanPages");
            }
            catch (NoSuchFieldException e) {
                FusionDbChatRoomDAOChain.log.error((Object)"Failed to get ChatRoomCleanPages from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
            }
            cleanRoomsToShow = chatRoomInitialPageSize + chatRoomPageSize * chatRoomCleanPages;
            adultRoomsPerPage = (double)adultRooms.size() / ((double)(chatRooms.size() - cleanRoomsToShow) / (double)chatRoomPageSize);
            roundingRatio = adultRoomsPerPage - (double)((int)adultRoomsPerPage);
            for (i = cleanRoomsToShow; i < chatRooms.size() && adultRooms.size() > 0; i += chatRoomPageSize) {
                noOfRooms = FusionDbChatRoomDAOChain.secureRandom.nextDouble() > roundingRatio ? Math.floor(adultRoomsPerPage) : Math.ceil(adultRoomsPerPage);
                j = 0;
                while ((double)j < noOfRooms && adultRooms.size() > 0) {
                    chatRooms.add(i, (ChatRoomData)adultRooms.remove(0));
                    ++j;
                }
            }
            chatRooms.addAll(adultRooms);
        }
        var14_20 = chatRooms;
        var27_37 = null;
        DBUtils.closeResource(rs, ps, conn, FusionDbChatRoomDAOChain.log);
        ** try [egrp 8[TRYBLOCK] [14 : 1249->1264)] { 
lbl186:
        // 1 sources

        if (stmt == null) return var14_20;
        stmt.close();
        return var14_20;
lbl189:
        // 1 sources

        catch (SQLException e) {
            stmt = null;
            FusionDbChatRoomDAOChain.log.warn((Object)"Failed to close statement", (Throwable)e);
        }
        return var14_20;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ChatRoomData> getFavouriteChatRooms(UserObject user) throws DAOException {
        LinkedList<ChatRoomData> linkedList;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select c.* from chatroom c, chatroombookmark b where c.name = b.chatroomname and b.username = ? order by b.datecreated desc limit ?");
            ps.setString(1, user.getUsername());
            int maxChatRoomBookmarks = 33;
            try {
                maxChatRoomBookmarks = SystemProperty.getInt("MaxChatRoomBookmarks");
            }
            catch (NoSuchFieldException e) {
                log.error((Object)"Failed to get MaxChatRoomBookmarks from System Properties will use 33 instead, should not reach here! MUST add the default value into system table", (Throwable)e);
            }
            ps.setInt(2, maxChatRoomBookmarks);
            rs = ps.executeQuery();
            LinkedList<ChatRoomData> chatRooms = new LinkedList<ChatRoomData>();
            while (rs.next()) {
                chatRooms.add(new ChatRoomData(rs));
            }
            linkedList = chatRooms;
            Object var9_11 = null;
        }
        catch (SQLException e) {
            List<ChatRoomData> list;
            try {
                log.error((Object)String.format("Failed to get FavouriteChatRooms for user:%s", user), (Throwable)e);
                list = super.getFavouriteChatRooms(user);
                Object var9_12 = null;
            }
            catch (Throwable throwable) {
                Object var9_13 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return linkedList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getAllRecentChatRooms(UserObject user) throws DAOException {
        List<String> list;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select chatroomnames from recentchatrooms where username = ?");
            ps.setString(1, user.getUsername());
            rs = ps.executeQuery();
            List<String> allRecentChatRooms = RecentChatRoomList.newRecentChatRoomList();
            while (rs.next()) {
                String[] rooms;
                for (String room : rooms = StringUtil.asArray(rs.getString("chatroomnames"))) {
                    allRecentChatRooms.add(room);
                }
            }
            list = allRecentChatRooms;
            Object var12_13 = null;
        }
        catch (SQLException e) {
            List<String> list2;
            try {
                log.error((Object)String.format("Failed to get AllRecentChatRooms for user:%s", user), (Throwable)e);
                list2 = super.getAllRecentChatRooms(user);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list2;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ChatRoomData> getRecentChatRooms(List<String> allRecentChatRooms) throws DAOException {
        LinkedList<ChatRoomData> linkedList;
        if (allRecentChatRooms.isEmpty()) {
            return Collections.emptyList();
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from chatroom where status = ? and name in (" + RecentChatRoomList.asString(allRecentChatRooms) + ")");
            ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            HashMap<String, ChatRoomData> chatRoomMap = new HashMap<String, ChatRoomData>();
            while (rs.next()) {
                ChatRoomData chatRoomData = new ChatRoomData(rs);
                chatRoomMap.put(chatRoomData.name.toLowerCase(), chatRoomData);
            }
            LinkedList<ChatRoomData> chatRoomList = new LinkedList<ChatRoomData>();
            for (String recentChatRoom : allRecentChatRooms) {
                ChatRoomData chatRoomData = (ChatRoomData)chatRoomMap.get(recentChatRoom.toLowerCase());
                if (chatRoomData == null) continue;
                chatRoomList.add(chatRoomData);
            }
            linkedList = chatRoomList;
            Object var11_12 = null;
        }
        catch (SQLException e) {
            List<ChatRoomData> list;
            try {
                log.error((Object)String.format("Failed to get RecentChatRooms for chatrooms:%s", allRecentChatRooms), (Throwable)e);
                list = super.getRecentChatRooms(allRecentChatRooms);
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return linkedList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChatRoomData getSimpleChatRoomData(String normalizedChatRoomName) throws DAOException {
        ChatRoomData chatRoomData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block12: {
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from chatroom where name = ? and status = ?");
            ps.setString(1, normalizedChatRoomName);
            ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            if (rs.next() && normalizedChatRoomName.equalsIgnoreCase(rs.getString("name"))) break block12;
            ChatRoomData chatRoomData2 = null;
            Object var8_8 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return chatRoomData2;
        }
        try {
            ChatRoomData room = new ChatRoomData(rs);
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
                log.warn((Object)"Failed to close ResultSet", (Throwable)e);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
                log.warn((Object)"Failed to close PreparedStatement", (Throwable)e);
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.MULTI_ID_CONTROL_ENABLED)) {
                ps = conn.prepareStatement("select * from chatroomextradata where chatroomid = ?");
                ps.setInt(1, room.id);
                rs = ps.executeQuery();
                room.updateExtraData(rs);
            }
            chatRoomData = room;
            Object var8_9 = null;
        }
        catch (SQLException e) {
            ChatRoomData chatRoomData3;
            try {
                log.error((Object)String.format("Failed to get SimpleChatRoomData for chatrooms:%s", normalizedChatRoomName), (Throwable)e);
                chatRoomData3 = super.getSimpleChatRoomData(normalizedChatRoomName);
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return chatRoomData3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return chatRoomData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<String, String> getChatRoomTheme(int themeID) throws DAOException {
        HashMap<String, String> hashMap;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select attributekey,attributevalue from chatroomtheme, chatroomthemeattribute where chatroomtheme.id=? and chatroomtheme.status=? and chatroomtheme.id=chatroomthemeattribute.chatroomthemeid");
            ps.setInt(1, themeID);
            ps.setInt(2, 1);
            rs = ps.executeQuery();
            HashMap<String, String> theme = new HashMap<String, String>();
            while (rs.next()) {
                theme.put(rs.getString("attributekey"), rs.getString("attributevalue"));
            }
            hashMap = theme;
            Object var8_9 = null;
        }
        catch (SQLException e) {
            Map<String, String> map;
            try {
                log.error((Object)String.format("Failed to get ChatRoomTheme for themeID:%s", themeID), (Throwable)e);
                map = super.getChatRoomTheme(themeID);
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return map;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<String> getChatRoomModerators(ChatRoomData chatroom) throws DAOException {
        HashSet<String> hashSet;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select username from chatroommoderator where chatroomid=?");
            ps.setInt(1, chatroom.id);
            rs = ps.executeQuery();
            HashSet<String> moderators = new HashSet<String>();
            while (rs.next()) {
                moderators.add(rs.getString("username"));
            }
            hashSet = moderators;
            Object var8_9 = null;
        }
        catch (SQLException e) {
            Set<String> set;
            try {
                log.error((Object)String.format("Failed to get ChatRoomModerators for chat room:%s", chatroom.id), (Throwable)e);
                set = super.getChatRoomModerators(chatroom);
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return set;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<String> getChatRoomBannedUsers(ChatRoomData chatroom) throws DAOException {
        HashSet<String> hashSet;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select username from chatroombanneduser where chatroomid=?");
            ps.setInt(1, chatroom.id);
            rs = ps.executeQuery();
            HashSet<String> bannedUsers = new HashSet<String>();
            while (rs.next()) {
                bannedUsers.add(rs.getString("username"));
            }
            hashSet = bannedUsers;
            Object var8_9 = null;
        }
        catch (SQLException e) {
            Set<String> set;
            try {
                log.error((Object)String.format("Failed to get ChatRoomBannedUsers for chat room:%s", chatroom.id), (Throwable)e);
                set = super.getChatRoomBannedUsers(chatroom);
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return set;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashSet;
    }
}

