/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LoginChatroomCategoryList {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(LoginChatroomCategoryList.class));
    private static Map<String, List<ChatroomCategoryData>> chatroomCategories = new ConcurrentHashMap<String, List<ChatroomCategoryData>>();
    private static Semaphore semaphore = new Semaphore(1);
    private static long lastUpdated;

    private static long getCacheTimeInMillis() {
        return SystemProperty.getLong(SystemPropertyEntities.Chatroom.CATEGORIES_LIST_CACHE_TIME_IN_MILLIS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadCategories() {
        if (lastUpdated == 0L) {
            semaphore.acquireUninterruptibly();
        } else if (!semaphore.tryAcquire()) {
            return;
        }
        try {
            try {
                int maxNewbieLevel = SystemProperty.getInt(SystemPropertyEntities.Chatroom.MAX_NEWBIE_MIG33_LEVEL);
                int[] alwaysLoadedCategoryIds = SystemProperty.getIntArray(SystemPropertyEntities.Chatroom.CATEGORIES_ALWAYS_INCLUDED_IN_LOGIN);
                ArrayList<Integer> alwaysLoadedCategoryIdsInt = new ArrayList<Integer>();
                for (int categoryId : alwaysLoadedCategoryIds) {
                    alwaysLoadedCategoryIdsInt.add(categoryId);
                }
                Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
                List categories = messageEJB.getLoginChatroomCategories();
                ArrayList<ChatroomCategoryData> newbieChatrooms = new ArrayList<ChatroomCategoryData>();
                ArrayList<ChatroomCategoryData> nonNewbieChatrooms = new ArrayList<ChatroomCategoryData>();
                for (ChatroomCategoryData category : categories) {
                    if (alwaysLoadedCategoryIdsInt.contains((int)category.id)) {
                        newbieChatrooms.add(category);
                        nonNewbieChatrooms.add(category);
                        continue;
                    }
                    if (category.maxLevel != 0 && category.maxLevel <= maxNewbieLevel) {
                        newbieChatrooms.add(category);
                        continue;
                    }
                    nonNewbieChatrooms.add(category);
                }
                chatroomCategories.put("newbie", newbieChatrooms);
                chatroomCategories.put("chatrooms", nonNewbieChatrooms);
                lastUpdated = System.currentTimeMillis();
            }
            catch (Exception e) {
                log.warn((Object)"Unable to load chatroom categories", (Throwable)e);
                Object var10_14 = null;
                semaphore.release();
            }
            Object var10_13 = null;
            semaphore.release();
        }
        catch (Throwable throwable) {
            Object var10_15 = null;
            semaphore.release();
            throw throwable;
        }
    }

    public static List<ChatroomCategoryData> get(String username, int userID) throws CreateException, RemoteException {
        int migLevel;
        boolean nueEnabled = SystemProperty.getBool(SystemPropertyEntities.Chatroom.NUE_NEWBIE_CATEGORIES_ENABLED);
        int maxNewbieLevel = SystemProperty.getInt(SystemPropertyEntities.Chatroom.MAX_NEWBIE_MIG33_LEVEL);
        if (nueEnabled && (migLevel = MemCacheOrEJB.getUserReputationLevel(username, userID)) <= maxNewbieLevel) {
            return LoginChatroomCategoryList.getNewbieChatroomCategories();
        }
        return LoginChatroomCategoryList.getNonNewbieChatroomCategories();
    }

    private static List<ChatroomCategoryData> getNewbieChatroomCategories() {
        LoginChatroomCategoryList.checkAndLoadCategories();
        return chatroomCategories.get("newbie");
    }

    private static List<ChatroomCategoryData> getNonNewbieChatroomCategories() {
        LoginChatroomCategoryList.checkAndLoadCategories();
        return chatroomCategories.get("chatrooms");
    }

    public static ChatroomCategoryData getChatroomCategory(Integer categoryId) throws Exception {
        LoginChatroomCategoryList.checkAndLoadCategories();
        for (ChatroomCategoryData category : chatroomCategories.get("newbie")) {
            if (categoryId.intValue() != category.id.intValue()) continue;
            return category;
        }
        for (ChatroomCategoryData category : chatroomCategories.get("chatrooms")) {
            if (categoryId.intValue() != category.id.intValue()) continue;
            return category;
        }
        log.error((Object)("Unable to get chatroom category [" + categoryId + "]"));
        throw new Exception("Unable to get chatroom category.");
    }

    private static void checkAndLoadCategories() {
        if (System.currentTimeMillis() - lastUpdated > LoginChatroomCategoryList.getCacheTimeInMillis()) {
            LoginChatroomCategoryList.loadCategories();
        }
    }

    static {
        chatroomCategories.put("newbie", new ArrayList());
        chatroomCategories.put("chatrooms", new ArrayList());
    }
}

