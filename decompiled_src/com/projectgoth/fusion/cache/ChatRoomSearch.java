/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.cache;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.data.ChatRoomData;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRoomSearch {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatRoomSearch.class));
    public static final String CHATROOM_SEARCH_NAMESPACE = "CS";
    public static final int MINUTES_TO_CACHE_FOR = 10;

    public static List<String> newChatRoomSearch() {
        return new LinkedList<String>();
    }

    public static String getKey(Integer countryID, String search, String language, Boolean includeAdultOnly, Boolean searchKeywords) {
        return MemCachedUtils.getCacheKeyInNamespace(CHATROOM_SEARCH_NAMESPACE, countryID.toString() + ',' + search + ',' + language + ',' + includeAdultOnly.toString() + ',' + searchKeywords.toString());
    }

    public static List<ChatRoomData> getChatRoomSearch(MemCachedClient instance, int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) {
        if (instance == null) {
            log.warn((Object)"ChatRoomSearch.getChatRoomSearch(): ChatRoomSearch memcache instance is null");
            return null;
        }
        return (List)instance.get(ChatRoomSearch.getKey(countryID, search, language, includeAdultOnly, searchKeywords));
    }

    public static boolean setChatRoomSearchList(MemCachedClient instance, Integer countryID, String search, String language, Boolean includeAdultOnly, Boolean searchKeywords, List<ChatRoomData> searchResults) {
        if (instance == null) {
            log.warn((Object)"ChatRoomSearch.setChatRoomSearch(): ChatRoomSearch memcache instance is null");
            return false;
        }
        Calendar expireTime = Calendar.getInstance();
        expireTime.add(12, 10);
        return instance.set(ChatRoomSearch.getKey(countryID, search, language, includeAdultOnly, searchKeywords), searchResults, expireTime.getTime());
    }
}

