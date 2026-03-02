/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.PipelineBlock
 */
package com.projectgoth.fusion.restapi.util;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.data.SSOSessionMetrics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RedisDataUtil {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RedisDataUtil.class));
    public static final String PRIVACY_KEY_MOBILENUMBER = "MobNumPrivacy";
    public static final String PRIVACY_KEY_BIRTHDAY = "DobPrivacy";
    public static final String PRIVACY_KEY_FIRSTLASTNAME = "FLNamePv";
    public static final String PRIVACY_KEY_EXTERNALEMAIL = "ExtEmPv";
    public static final String PRIVACY_KEY_CHAT = "ChatPv";
    public static final String PRIVACY_KEY_BUZZ = "BuzzPv";
    public static final String PRIVACY_KEY_LOOKOUT = "LOPv";
    public static final String PRIVACY_KEY_FOOTPRINTS = "FPPv";
    public static final String PRIVACY_KEY_FEED_CONTENT = "FeedPv";
    public static final String SSO_KEY_METRICS = "SSOM";
    public static final String SSO_KEY_METRICS_INDEX = "SSOM:INDEX";

    public static String getRedisKeyForSSOSessionMetric(SSOEnums.View view, String sessionID) {
        return "SSOM:" + view.value() + ":" + sessionID;
    }

    public static SSOEnums.View getViewFromSSOSessionMetricKey(String key) {
        String[] tokens = key.split(":");
        if (tokens.length != 3) {
            return SSOEnums.View.UNKNOWN;
        }
        return SSOEnums.View.fromValue(Integer.parseInt(tokens[1]));
    }

    public static String getSessionIDFromSSOSessionMetricKey(String key) {
        String[] tokens = key.split(":");
        if (tokens.length != 3) {
            return null;
        }
        return tokens[2];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void logSSOClientSession(final SSOSessionMetrics metrics) {
        Jedis masterInst;
        block7: {
            block6: {
                masterInst = null;
                SSOEnums.View view = metrics.getView();
                String sessionID = metrics.getSessionID();
                final long timestamp = metrics.getTimestamp();
                int CLOCK_SKEW_IN_SECONDS = 60;
                try {
                    try {
                        long stored_timestamp;
                        masterInst = Redis.getGamesMasterInstance();
                        final String redisKey = RedisDataUtil.getRedisKeyForSSOSessionMetric(view, sessionID);
                        log.debug((Object)String.format("logSSOClientSession: [%s]", redisKey));
                        final boolean sessionMetricExists = masterInst.exists(redisKey);
                        if (sessionMetricExists && (stored_timestamp = Long.parseLong(masterInst.hget(redisKey, "timestamp"))) >= timestamp + 60L) {
                            log.warn((Object)("Received outdated request to log SSO session metrics. Ignoring [" + sessionID + "] [" + view.toString() + "]"));
                            Object var12_10 = null;
                            break block6;
                        }
                        masterInst.pipelined(new PipelineBlock(){

                            public void execute() {
                                Object value;
                                String field;
                                for (Map.Entry<String, Integer> entry : metrics.getCounters().entrySet()) {
                                    field = entry.getKey();
                                    value = entry.getValue();
                                    log.debug((Object)String.format("incrementing [%s] [%s] [%d]", redisKey, field, ((Integer)value).intValue()));
                                    this.hincrBy(redisKey, field, ((Integer)value).intValue());
                                }
                                for (Map.Entry<String, Object> entry : metrics.getRecords().entrySet()) {
                                    field = entry.getKey();
                                    value = (String)entry.getValue();
                                    log.debug((Object)String.format("setting [%s] [%s] [%s]", redisKey, field, value));
                                    this.hset(redisKey, field, (String)value);
                                }
                                if (!sessionMetricExists) {
                                    log.debug((Object)String.format("setting [%s] [%s] [%s]", redisKey, "sessionStartTime", Long.toString(metrics.getSessionStartTime())));
                                    this.hset(redisKey, "sessionStartTime", Long.toString(metrics.getSessionStartTime()));
                                    this.hset(redisKey, "viewStartTime", Long.toString(System.currentTimeMillis()));
                                    this.zadd(RedisDataUtil.SSO_KEY_METRICS_INDEX, new Double(metrics.getSessionStartTime().longValue()), redisKey);
                                }
                                this.hset(redisKey, "timestamp", Long.toString(timestamp));
                            }
                        });
                        break block7;
                    }
                    catch (Exception e) {
                        log.error((Object)String.format("Unable to log SSO session [%s] [%s] : %s", sessionID, view.toString(), e.getMessage()), (Throwable)e);
                        Object var12_12 = null;
                        Redis.disconnect(masterInst, log);
                        return;
                    }
                }
                catch (Throwable throwable) {
                    Object var12_13 = null;
                    Redis.disconnect(masterInst, log);
                    throw throwable;
                }
            }
            Redis.disconnect(masterInst, log);
            return;
        }
        Object var12_11 = null;
        Redis.disconnect(masterInst, log);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Map<String, Integer> getIntMapByFields(String key, int userId, String[] fields) {
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        if (fields == null) return ret;
        if (fields.length == 0) {
            return ret;
        }
        Jedis slaveInst = null;
        try {
            try {
                slaveInst = Redis.getSlaveInstanceForUserID(userId);
                List vals = slaveInst.hmget(key, fields);
                int index = 0;
                for (String val : vals) {
                    if (val != null) {
                        try {
                            ret.put(fields[index], Integer.parseInt(val));
                        }
                        catch (NumberFormatException nfe) {
                            log.error((Object)String.format("Unexpected for key '%s' user id %d, unable to parse '%s' into an integer", key, userId, val));
                        }
                    }
                    ++index;
                }
                Object var11_11 = null;
            }
            catch (Exception e) {
                log.error((Object)String.format("Unable to get redis slave instance for user id %d", userId));
                Object var11_12 = null;
                Redis.disconnect(slaveInst, log);
                return ret;
            }
        }
        catch (Throwable throwable) {
            Object var11_13 = null;
            Redis.disconnect(slaveInst, log);
            throw throwable;
        }
        Redis.disconnect(slaveInst, log);
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Map<String, String> getStringMapByFields(String key, int userId, String[] fields) {
        if (SystemPropertyEntities.Temp.Cache.se552NPEFixEnabled.getValue() == false) return RedisDataUtil.getStringMapByFields_PreSE552(key, userId, fields);
        HashMap<String, String> ret = new HashMap<String, String>();
        if (fields == null) return ret;
        if (fields.length == 0) {
            return ret;
        }
        Jedis slaveInst = null;
        try {
            block8: {
                try {
                    slaveInst = Redis.getSlaveInstanceForUserID(userId);
                    if (slaveInst != null) {
                        List vals = slaveInst.hmget(key, fields);
                        int index = 0;
                        for (String val : vals) {
                            if (val != null) {
                                ret.put(fields[index], val);
                            }
                            ++index;
                        }
                        break block8;
                    }
                    if (!log.isDebugEnabled()) break block8;
                    log.debug((Object)("getStringMapByFields[" + key + "," + userId + "] has no configured shard instance"));
                }
                catch (Exception e) {
                    log.error((Object)String.format("Unable to get redis slave instance for user id %d", userId), (Throwable)e);
                    Object var10_11 = null;
                    Redis.disconnect(slaveInst, log);
                    return ret;
                }
            }
            Object var10_10 = null;
            Redis.disconnect(slaveInst, log);
            return ret;
        }
        catch (Throwable throwable) {
            Object var10_12 = null;
            Redis.disconnect(slaveInst, log);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Map<String, String> getStringMapByFields_PreSE552(String key, int userId, String[] fields) {
        HashMap<String, String> ret = new HashMap<String, String>();
        if (fields == null) return ret;
        if (fields.length == 0) {
            return ret;
        }
        Jedis slaveInst = null;
        try {
            try {
                slaveInst = Redis.getSlaveInstanceForUserID(userId);
                List vals = slaveInst.hmget(key, fields);
                int index = 0;
                for (String val : vals) {
                    if (val != null) {
                        ret.put(fields[index], val);
                    }
                    ++index;
                }
                Object var10_10 = null;
            }
            catch (Exception e) {
                log.error((Object)String.format("Unable to get redis slave instance for user id %d", userId), (Throwable)e);
                Object var10_11 = null;
                Redis.disconnect(slaveInst, log);
                return ret;
            }
        }
        catch (Throwable throwable) {
            Object var10_12 = null;
            Redis.disconnect(slaveInst, log);
            throw throwable;
        }
        Redis.disconnect(slaveInst, log);
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean setStringMap(String key, int userId, Map<String, String> values) {
        if (values == null) return true;
        if (values.isEmpty()) {
            return true;
        }
        Jedis masterInst = null;
        String ret = "Failed";
        try {
            block5: {
                try {
                    masterInst = Redis.getMasterInstanceForUserID(userId);
                    ret = masterInst.hmset(key, values);
                    if ("ok".equalsIgnoreCase(ret)) break block5;
                    log.error((Object)String.format("Failed to save key '%s' to redis master instance for user id %d", key, userId));
                }
                catch (Exception e) {
                    log.error((Object)String.format("Unable to get redis master instance for user id %d", userId), (Throwable)e);
                    Object var7_6 = null;
                    Redis.disconnect(masterInst, log);
                    return "ok".equalsIgnoreCase(ret);
                }
            }
            Object var7_5 = null;
            Redis.disconnect(masterInst, log);
            return "ok".equalsIgnoreCase(ret);
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            Redis.disconnect(masterInst, log);
            throw throwable;
        }
    }

    private static Map<String, Integer> getPrivacy(int userId, String[] fields) {
        return RedisDataUtil.getIntMapByFields(Redis.SubKeySpaceUserEntity.CONFIG.getFullKey(userId), userId, fields);
    }

    public static Map<String, Integer> getAccountProfilePrivacy(int userId) {
        return RedisDataUtil.getPrivacy(userId, new String[]{PRIVACY_KEY_BIRTHDAY, PRIVACY_KEY_MOBILENUMBER, PRIVACY_KEY_FIRSTLASTNAME, PRIVACY_KEY_EXTERNALEMAIL});
    }

    public static boolean setAccountPrivacy(int userId, Map<String, Integer> values) {
        log.debug((Object)String.format("writing privacy value for user %d", userId));
        HashMap<String, String> valueStrs = new HashMap<String, String>();
        for (Map.Entry<String, Integer> e : values.entrySet()) {
            valueStrs.put(e.getKey(), e.getValue().toString());
            log.debug((Object)String.format(" added %s %s", e.getKey(), e.getValue().toString()));
        }
        return RedisDataUtil.setStringMap(Redis.SubKeySpaceUserEntity.CONFIG.getFullKey(userId), userId, valueStrs);
    }

    public static Map<String, Integer> getAccountCommunicationPrivacy(int userId) {
        return RedisDataUtil.getPrivacy(userId, new String[]{PRIVACY_KEY_CHAT, PRIVACY_KEY_BUZZ, PRIVACY_KEY_LOOKOUT, PRIVACY_KEY_FOOTPRINTS, PRIVACY_KEY_FEED_CONTENT});
    }

    public static Map<String, Integer> removeUnchangedPrivacy(Map<String, Integer> newPrivacy, Map<String, Integer> oldPrivacy) {
        HashMap<String, Integer> updatedPrivacy = new HashMap<String, Integer>(newPrivacy.size());
        for (Map.Entry<String, Integer> entry : newPrivacy.entrySet()) {
            Integer oldValue = oldPrivacy.get(entry.getKey());
            if (oldValue != null && oldValue.equals(entry.getValue())) continue;
            updatedPrivacy.put(entry.getKey(), entry.getValue());
        }
        return updatedPrivacy;
    }

    public static Map<String, Integer> keepChangedPrivacy(Map<String, Integer> newPrivacy, Map<String, Integer> oldPrivacy) {
        HashMap<String, Integer> updatedPrivacy = new HashMap<String, Integer>(newPrivacy.size());
        for (Map.Entry<String, Integer> entry : newPrivacy.entrySet()) {
            Integer oldValue = oldPrivacy.get(entry.getKey());
            if (oldValue == null || oldValue.equals(entry.getValue())) continue;
            updatedPrivacy.put(entry.getKey(), entry.getValue());
        }
        return updatedPrivacy;
    }

    public static boolean setAdditionalUserInfo(int userId, Map<String, String> values) {
        return RedisDataUtil.setStringMap(Redis.getUserDetailKey(userId), userId, values);
    }

    public static int getUserDisplayPictureSetting(int userId) {
        if (SystemPropertyEntities.Temp.Cache.se552NPEFixEnabled.getValue().booleanValue()) {
            Map<String, String> userDetMap = RedisDataUtil.getStringMapByFields(Redis.getUserDetailKey(userId), userId, UserDisplayPictureSettingKeys.KEY_AS_ARRAY);
            if (userDetMap == null || userDetMap.size() == 0) {
                return -1;
            }
            try {
                String value = userDetMap.get(UserDisplayPictureSettingKeys.KEY);
                if (!StringUtil.isBlank(value)) {
                    return Integer.parseInt(value);
                }
                return -1;
            }
            catch (NumberFormatException nfe) {
                log.warn((Object)("getUserDisplayPictureSetting:" + nfe));
                return -1;
            }
        }
        return RedisDataUtil.getUserDisplayPictureSetting_PreSE552(userId);
    }

    private static int getUserDisplayPictureSetting_PreSE552(int userId) {
        String key = Redis.FieldUserDetails.DISPLAY_PICTURE.toString();
        String value = RedisDataUtil.getStringMapByFields(Redis.getUserDetailKey(userId), userId, new String[]{key}).get(key);
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public static boolean setUserDisplayPictureSetting(int userId, int displayPictureSetting) {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(Redis.FieldUserDetails.DISPLAY_PICTURE.toString(), Integer.toString(displayPictureSetting));
        return RedisDataUtil.setStringMap(Redis.getUserDetailKey(userId), userId, values);
    }

    public static Map<String, Integer> getAllAccountPrivacy(int userId) {
        return RedisDataUtil.getPrivacy(userId, new String[]{PRIVACY_KEY_BIRTHDAY, PRIVACY_KEY_MOBILENUMBER, PRIVACY_KEY_FIRSTLASTNAME, PRIVACY_KEY_EXTERNALEMAIL, PRIVACY_KEY_CHAT, PRIVACY_KEY_BUZZ, PRIVACY_KEY_LOOKOUT, PRIVACY_KEY_FOOTPRINTS, PRIVACY_KEY_FEED_CONTENT});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Map<ProfileCounterType, Integer> getUserProfileCounters(final int userId) {
        HashMap<ProfileCounterType, Integer> result = new HashMap<ProfileCounterType, Integer>();
        Jedis slaveInst = null;
        Jedis lbSlaveInst = null;
        try {
            try {
                slaveInst = Redis.getSlaveInstanceForUserID(userId);
                lbSlaveInst = Redis.getLeaderboardsSlaveInstance();
                List pipelineResult = slaveInst.pipelined(new PipelineBlock(){

                    public void execute() {
                        int footprintsMaxDuration = SystemProperty.getInt("footprintsMaxDurationInSeconds", 14515200);
                        this.hget(Redis.getUserDetailKey(userId), Redis.FieldUserDetails.AVATAR_VOTES_COUNTER.toString());
                        this.hget(Redis.getUserDetailKey(userId), Redis.FieldUserDetails.AVATAR_COMMENTS_COUNTER.toString());
                        this.zcount("UserProfileViewedBy:" + userId, System.currentTimeMillis() / 1000L - (long)footprintsMaxDuration, 9.223372036854776E18);
                        this.scard(Redis.KeySpace.USER_ENTITY.toString() + userId + Redis.FieldUserDetails.USER_LIKES_SET.toString());
                        this.scard(Redis.KeySpace.USER_ENTITY.toString() + userId + Redis.FieldUserDetails.THIRD_PARTY_GAMES_PLAYED_COUNTER.toString());
                    }
                });
                result.put(ProfileCounterType.AVATAR_VOTES, RedisDataUtil.convertPipelinedCounterResultToInt(pipelineResult.get(0)));
                result.put(ProfileCounterType.AVATAR_COMMENTS, RedisDataUtil.convertPipelinedCounterResultToInt(pipelineResult.get(1)));
                result.put(ProfileCounterType.FOOTPRINTS, RedisDataUtil.convertPipelinedCounterResultToInt(pipelineResult.get(2)));
                result.put(ProfileCounterType.USERLIKES, RedisDataUtil.convertPipelinedCounterResultToInt(pipelineResult.get(3)));
                result.put(ProfileCounterType.THIRD_PARTY_GAMES_PLAYED, RedisDataUtil.convertPipelinedCounterResultToInt(pipelineResult.get(4)));
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                final String username = userEJB.getUsernameByUserid(userId, null);
                pipelineResult = lbSlaveInst.pipelined(new PipelineBlock(){

                    public void execute() {
                        String userkey = username + ":" + userId;
                        for (Leaderboard.Type type : Leaderboard.Type.TOTAL_PLAYED_LEADERBOARDS) {
                            this.zscore(type.append(Leaderboard.Period.WEEKLY.toString()), userkey);
                        }
                    }
                });
                int totalChatGamesPlayed = 0;
                for (int i = 0; i < Leaderboard.Type.TOTAL_PLAYED_LEADERBOARDS.length; totalChatGamesPlayed += RedisDataUtil.convertPipelinedCounterResultToInt(pipelineResult.get(i)), ++i) {
                }
                result.put(ProfileCounterType.CHATROOM_GAMES_PLAYED, totalChatGamesPlayed);
            }
            catch (Exception e) {
                log.error((Object)String.format("Exception caught while retrieving profile counters for userid %d", userId), (Throwable)e);
                Object var10_11 = null;
                Redis.disconnect(slaveInst, log);
                Redis.disconnect(lbSlaveInst, log);
                return result;
            }
            Object var10_10 = null;
        }
        catch (Throwable throwable) {
            Object var10_12 = null;
            Redis.disconnect(slaveInst, log);
            Redis.disconnect(lbSlaveInst, log);
            throw throwable;
        }
        Redis.disconnect(slaveInst, log);
        Redis.disconnect(lbSlaveInst, log);
        return result;
    }

    public static int convertPipelinedCounterResultToInt(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Number) {
            return ((Number)o).intValue();
        }
        if (o instanceof String) {
            return (int)StringUtil.toDoubleOrDefault((String)o, 0.0);
        }
        log.error((Object)("[" + o + "] is not a Number or String"));
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean recordProfileView(UserData viewedUserData, CountryData viewedCountryData, UserData viewingUserData, CountryData viewingCountryData) {
        boolean bl;
        Jedis mastInstA = null;
        Jedis mastInstB = null;
        int maxAgeSeconds = SystemProperty.getInt("footprintsMaxDurationInSeconds", 14515200);
        try {
            mastInstA = Redis.getMasterInstanceForUserID(viewingUserData.userID);
            String key = "UserProfilesViewed:" + viewingUserData.userID;
            JSONObject obj = new JSONObject();
            obj.put("UserID", (Object)("" + viewedUserData.userID));
            obj.put("Username", (Object)viewedUserData.username);
            obj.put("Country", (Object)viewedCountryData.name);
            Redis.addToSortedSetWithTimestampAndTrimByAge(mastInstA, key, obj.toString(), maxAgeSeconds, true);
            mastInstB = Redis.getMasterInstanceForUserID(viewedUserData.userID);
            key = "UserProfileViewedBy:" + viewedUserData.userID;
            obj = new JSONObject();
            obj.put("UserID", (Object)("" + viewingUserData.userID));
            obj.put("Username", (Object)viewingUserData.username);
            obj.put("Country", (Object)viewingCountryData.name);
            Redis.addToSortedSetWithTimestampAndTrimByAge(mastInstB, key, obj.toString(), maxAgeSeconds, true);
            bl = true;
            Object var11_12 = null;
        }
        catch (Exception e) {
            boolean bl2;
            try {
                log.error((Object)String.format("Unable to record profile view user id %s vieweruserid", viewedUserData.userID, viewingUserData.userID), (Throwable)e);
                bl2 = false;
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                Redis.disconnect(mastInstA, log);
                Redis.disconnect(mastInstB, log);
                throw throwable;
            }
            Redis.disconnect(mastInstA, log);
            Redis.disconnect(mastInstB, log);
            return bl2;
        }
        Redis.disconnect(mastInstA, log);
        Redis.disconnect(mastInstB, log);
        return bl;
    }

    private static final class UserDisplayPictureSettingKeys {
        public static final String KEY = Redis.FieldUserDetails.DISPLAY_PICTURE.toString();
        public static final String[] KEY_AS_ARRAY = new String[]{KEY};

        private UserDisplayPictureSettingKeys() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ProfileCounterType {
        AVATAR_VOTES,
        AVATAR_COMMENTS,
        FOOTPRINTS,
        USERLIKES,
        THIRD_PARTY_GAMES_PLAYED,
        CHATROOM_GAMES_PLAYED;

    }
}

