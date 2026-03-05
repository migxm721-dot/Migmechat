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
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class UserReferrerCache {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserReferrerCache.class));

    public static boolean isWithinCapAllowed(UserData inviterUserData, UserData invitedUserData, RewardProgramData rewardProgramData) {
        if (SystemProperty.getBool(SystemPropertyEntities.Temp.ER78_ENABLED)) {
            int[] rewardProgramType = SystemProperty.getIntArray(SystemPropertyEntities.MechanicsEngineSettings.EXCLUDE_TYPE_INVITEE_REWARDED_TRACKING);
            for (int i = 0; i < rewardProgramType.length; ++i) {
                if (rewardProgramType[i] != rewardProgramData.type.value()) continue;
                return true;
            }
            int[] rewardProgramIds = SystemProperty.getIntArray(SystemPropertyEntities.MechanicsEngineSettings.EXCLUDE_REWARD_ID_INVITEE_REWARDED_TRACKING);
            for (int i = 0; i < rewardProgramIds.length; ++i) {
                if (rewardProgramIds[i] != rewardProgramData.id) continue;
                return true;
            }
        }
        return UserReferrerCache.isWithinCapAllowed(inviterUserData, invitedUserData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean isWithinCapAllowed(UserData inviterUserData, UserData invitedUserData) {
        Jedis masterInstance;
        block8: {
            boolean bl;
            masterInstance = null;
            if (!SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.ENABLE_INVITEE_REWARDED_TRACKING)) {
                return true;
            }
            try {
                try {
                    Set ids;
                    masterInstance = Redis.getMasterInstanceForUserID(inviterUserData.userID);
                    String member = String.valueOf(invitedUserData.userID);
                    String userDetailKey = Redis.SubKeySpaceUserEntity.REWARDED_INVITEE_TIMESTAMP.getFullKey(inviterUserData.userID);
                    Double timestamp = masterInstance.zscore(userDetailKey, member);
                    if (timestamp == null && masterInstance.zcard(userDetailKey) <= (long)SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.UNIQUE_INVITEE_REWARDED_REDIS_MAX_COUNT)) {
                        masterInstance.zadd(userDetailKey, (double)System.currentTimeMillis(), member);
                    }
                    if (!(ids = masterInstance.zrange(userDetailKey, 0L, (long)SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.UNIQUE_INVITEE_REWARDED_MAX_COUNT))).contains(member)) break block8;
                    bl = true;
                    Object var9_9 = null;
                }
                catch (Exception e) {
                    log.error((Object)("Unable to get the slave Redis instance for User ID [" + inviterUserData.userID + "]: " + e));
                    Object var9_11 = null;
                    Redis.disconnect(masterInstance, log);
                    return false;
                }
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                Redis.disconnect(masterInstance, log);
                throw throwable;
            }
            Redis.disconnect(masterInstance, log);
            return bl;
        }
        Object var9_10 = null;
        Redis.disconnect(masterInstance, log);
        return false;
    }
}

