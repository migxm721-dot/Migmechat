/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.uns;

import com.google.gson.Gson;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.uns.UserNotificationServiceI;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class UserNotificationPurger {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserNotificationPurger.class));
    private static ConcurrentHashMap<Integer, Long> userMutexMap = new ConcurrentHashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void trimUserNotificationItemsIfNeededOnWrite(Jedis jedisRead, Jedis jedisMasterPassedIn, int userID) {
        boolean setIt;
        long mutexTimeout = SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.USER_NOTIFICATION_USER_MUTEX_TIMEOUT_MINS) * 60 * 1000;
        Long userMutex = userMutexMap.get(userID);
        if (userMutex != null && System.currentTimeMillis() - userMutex < mutexTimeout) {
            return;
        }
        if (userMutex != null) {
            setIt = userMutexMap.replace(userID, userMutex, System.currentTimeMillis());
        } else {
            Long oldValue = userMutexMap.putIfAbsent(userID, System.currentTimeMillis());
            boolean bl = setIt = oldValue == null;
        }
        if (!setIt) {
            return;
        }
        try {
            UserNotificationPurger.trimUserNotificationItemsIfNeeded(jedisRead, jedisMasterPassedIn, userID);
        }
        finally {
            userMutexMap.remove(userID);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void trimUserNotificationItemsIfNeeded(Jedis jedisRead, Jedis jedisMasterPassedIn, int userID) {
        Jedis jedisMaster = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("trimUserNotificationItemsIfNeeded for userID=" + userID));
            }
            int count = UserNotificationPurger.countUserNotificationItems(jedisRead, userID);
            int limit = SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_NOTIFICATIONS_TO_SEND_TO_USER);
            if (log.isDebugEnabled()) {
                log.debug((Object)("trimUserNotificationItemsIfNeeded for userID=" + userID + " count=" + count + " limit=" + limit));
            }
            if (count > limit) {
                jedisMaster = jedisMasterPassedIn != null ? jedisMasterPassedIn : Redis.getMasterInstanceForUserID(userID);
                UserNotificationPurger.trimUserNotifications(jedisMaster, userID, count, SystemProperty.getInt(SystemPropertyEntities.UserNotificationServiceSettings.MAX_NOTIFICATIONS_TO_SEND_TO_USER_TRUNCATION));
            }
            if (jedisMasterPassedIn != null) return;
        }
        catch (Exception e) {
            try {
                log.error((Object)("Warning: failed to trim user notifications for userID=" + userID + ": cause=" + e), (Throwable)e);
                if (jedisMasterPassedIn != null) return;
            }
            catch (Throwable throwable) {
                if (jedisMasterPassedIn != null) throw throwable;
                Redis.disconnect(jedisMaster, log);
                throw throwable;
            }
            Redis.disconnect(jedisMaster, log);
            return;
        }
        Redis.disconnect(jedisMaster, log);
        return;
    }

    private static int countUserNotificationItems(Jedis jedis, int userID) {
        int count = 0;
        for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.MIGBO_SET) {
            String redisKey = UserNotificationServiceI.getUnsKey(userID, notfnType.getType());
            count = (int)((long)count + jedis.hlen(redisKey));
        }
        return count;
    }

    private static void trimUserNotifications(Jedis jedis, int userID, int hashItemCount, int maxCountToTruncateTo) {
        TreeMap<Long, String[]> userHKeyFieldPairsByDate = new TreeMap<Long, String[]>();
        Long oldestTimestamp = null;
        for (Enums.NotificationTypeEnum notfnType : Enums.NotificationTypeEnum.MIGBO_SET) {
            String key = UserNotificationServiceI.getUnsKey(userID, notfnType.getType());
            ArrayList hfields = new ArrayList(jedis.hkeys(key));
            for (String hfield : hfields) {
                String hvalue = jedis.hget(key, hfield);
                long dateCreated = UserNotificationPurger.getDateCreated(hvalue);
                String[] pair = new String[]{key, hfield};
                Long timestampToDelete = null;
                String[] keyAndFieldToDelete = null;
                if (oldestTimestamp != null && dateCreated < oldestTimestamp) {
                    timestampToDelete = dateCreated;
                    keyAndFieldToDelete = pair;
                } else {
                    userHKeyFieldPairsByDate.put(dateCreated, pair);
                    if (userHKeyFieldPairsByDate.size() > maxCountToTruncateTo) {
                        timestampToDelete = (Long)userHKeyFieldPairsByDate.firstKey();
                        keyAndFieldToDelete = (String[])userHKeyFieldPairsByDate.get(timestampToDelete);
                        userHKeyFieldPairsByDate.remove(timestampToDelete);
                        oldestTimestamp = (Long)userHKeyFieldPairsByDate.firstKey();
                    }
                }
                if (keyAndFieldToDelete == null) continue;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("HDEL " + keyAndFieldToDelete[0] + " " + keyAndFieldToDelete[1] + " with dateCreated=" + timestampToDelete));
                }
                jedis.hdel(keyAndFieldToDelete[0], new String[]{keyAndFieldToDelete[1]});
            }
        }
    }

    private static long getDateCreated(String jsonMessage) {
        int x = StringUtil.toIntOrDefault(jsonMessage, -1);
        if (x != -1) {
            return System.currentTimeMillis();
        }
        try {
            Message message = (Message)((Object)new Gson().fromJson(jsonMessage, Message.class));
            return message.dateCreated;
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Couldn't parse dateCreated from hvalue=" + jsonMessage));
            }
            return System.currentTimeMillis();
        }
    }
}

