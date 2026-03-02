/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.cache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class GiftsReceivedCounter {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GiftsReceivedCounter.class));

    public static Integer getCacheCount(int userId) {
        Integer count;
        block5: {
            count = null;
            try {
                Jedis slaveInstance = Redis.getSlaveInstanceForUserID(userId);
                if (slaveInstance == null) break block5;
                String userDetailKey = Redis.getUserDetailKey(userId);
                String countStr = slaveInstance.hget(userDetailKey, Redis.FieldUserDetails.GIFTS_RECEIVED_COUNTER.toString());
                if (countStr != null) {
                    try {
                        count = Integer.parseInt(countStr);
                    }
                    catch (NumberFormatException e) {
                        log.error((Object)("Unable to parse int from string [" + countStr + "]: " + e));
                    }
                }
                Redis.disconnect(slaveInstance, log);
            }
            catch (Exception e) {
                log.error((Object)("Unable to get the slave Redis instance for User ID [" + userId + "]: " + e));
            }
        }
        return count;
    }

    public static void setCacheCount(int userId, int count) {
        try {
            Jedis masterInstance = Redis.getMasterInstanceForUserID(userId);
            if (masterInstance != null) {
                String userDetailKey = Redis.getUserDetailKey(userId);
                masterInstance.hsetnx(userDetailKey, Redis.FieldUserDetails.GIFTS_RECEIVED_COUNTER.toString(), Integer.toString(count));
                Redis.disconnect(masterInstance, log);
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to get the master redis instance for User ID [" + userId + "]: " + e));
        }
    }

    public static void incrementCacheCount(int userId) {
        GiftsReceivedCounter.incrementCacheCountBy(userId, 1);
    }

    public static void decrementCacheCount(int userId) {
        GiftsReceivedCounter.incrementCacheCountBy(userId, -1);
    }

    private static void incrementCacheCountBy(int userId, int value) {
        Integer count = GiftsReceivedCounter.getCacheCount(userId);
        if (count != null) {
            if (value < 1 && count < 1) {
                return;
            }
            try {
                Jedis masterInstance = Redis.getMasterInstanceForUserID(userId);
                if (masterInstance != null) {
                    String userDetailKey = Redis.getUserDetailKey(userId);
                    masterInstance.hincrBy(userDetailKey, Redis.FieldUserDetails.GIFTS_RECEIVED_COUNTER.toString(), (long)value);
                    Redis.disconnect(masterInstance, log);
                }
            }
            catch (Exception e) {
                log.error((Object)("Unable to get the master redis instance for User ID [" + userId + "]: " + e));
            }
        } else {
            log.info((Object)("Unable to increment gifts received counter because no cache value exists for User ID [" + userId + "]"));
        }
    }
}

