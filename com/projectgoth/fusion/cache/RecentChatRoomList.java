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
import com.projectgoth.fusion.common.StringUtil;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RecentChatRoomList {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecentChatRoomList.class));
    public static final String RECENT_CHATROOM_LIST_NAMESPACE = "RCL";
    public static final String RECENT_CHATROOM_LIST_DISTRIBUTED_LOCK_NAMESPACE = "RCLDL";
    public static final int MAX_RECENT_CHAT_ROOMS = 15;

    public static List<String> newRecentChatRoomList() {
        return new LinkedList<String>();
    }

    public static String getKey(String username) {
        return MemCachedUtils.getCacheKeyInNamespace(RECENT_CHATROOM_LIST_NAMESPACE, username);
    }

    public static List<String> getRecentChatRoomList(MemCachedClient instance, String username) {
        return (List)instance.get(RecentChatRoomList.getKey(username));
    }

    public static List<String> getRecentChatRoomList(MemCachedClient instance, String[] usernameArray) {
        String[] keyArray = new String[usernameArray.length];
        for (int i = 0; i < usernameArray.length; ++i) {
            keyArray[i] = RecentChatRoomList.getKey(usernameArray[i]);
        }
        LinkedList<String> recentChatRoomList = new LinkedList<String>();
        for (Object o : instance.getMultiArray(keyArray)) {
            if (o == null) continue;
            recentChatRoomList.addAll((List)o);
        }
        return recentChatRoomList;
    }

    public static boolean setRecentChatRoomList(MemCachedClient instance, String username, List<String> recentChatRoomList) {
        return instance.set(RecentChatRoomList.getKey(username), recentChatRoomList);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean addRecentChatRoom(MemCachedClient instance, String username, String recentChatRoom) {
        block4: {
            boolean bl;
            try {
                if (!MemCachedUtils.getLock(instance, RECENT_CHATROOM_LIST_DISTRIBUTED_LOCK_NAMESPACE, username, 15000)) break block4;
                List<String> recentChatRoomList = RecentChatRoomList.getRecentChatRoomList(instance, username);
                if (recentChatRoomList == null) {
                    recentChatRoomList = RecentChatRoomList.newRecentChatRoomList();
                }
                RecentChatRoomList.addRecentChatRoomToList((LinkedList)recentChatRoomList, recentChatRoom);
                RecentChatRoomList.setRecentChatRoomList(instance, username, recentChatRoomList);
                bl = true;
                Object var6_6 = null;
            }
            catch (Throwable throwable) {
                Object var6_8 = null;
                MemCachedUtils.releaseLock(instance, RECENT_CHATROOM_LIST_DISTRIBUTED_LOCK_NAMESPACE, username);
                throw throwable;
            }
            MemCachedUtils.releaseLock(instance, RECENT_CHATROOM_LIST_DISTRIBUTED_LOCK_NAMESPACE, username);
            return bl;
        }
        log.error((Object)("Failed to get a lock to update user [" + username + "]'s recent chat room list in 15 seconds"));
        boolean bl = false;
        Object var6_7 = null;
        MemCachedUtils.releaseLock(instance, RECENT_CHATROOM_LIST_DISTRIBUTED_LOCK_NAMESPACE, username);
        return bl;
    }

    private static void addRecentChatRoomToList(LinkedList<String> recentChatRoomList, String recentChatRoom) {
        if (recentChatRoomList.size() > 0 && !recentChatRoomList.remove(recentChatRoom) && recentChatRoomList.size() >= 15) {
            recentChatRoomList.removeLast();
        }
        recentChatRoomList.addFirst(recentChatRoom);
    }

    public static boolean deleteRecentChatRoomList(MemCachedClient instance, String username) {
        return instance.delete(RecentChatRoomList.getKey(username));
    }

    public static String asString(List<String> recentChatRoomList) {
        return StringUtil.asString(recentChatRoomList);
    }

    public static void main(String[] args) {
        LinkedList recents = (LinkedList)RecentChatRoomList.newRecentChatRoomList();
        for (int i = 0; i < 16; ++i) {
            RecentChatRoomList.addRecentChatRoomToList(recents, i + "");
        }
        System.out.println(StringUtil.asString(recents));
        System.out.println((String)recents.get(0));
        RecentChatRoomList.addRecentChatRoomToList(recents, "10");
        System.out.println(StringUtil.asString(recents));
    }
}

