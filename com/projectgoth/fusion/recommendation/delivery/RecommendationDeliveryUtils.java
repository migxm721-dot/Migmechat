/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.hashtag.data.HashTagData
 *  javax.ejb.CreateException
 *  org.apache.commons.lang.StringUtils
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Pipeline
 *  redis.clients.jedis.Tuple
 */
package com.projectgoth.fusion.recommendation.delivery;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LRUCache;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.SystemPropertyEntryWithParent;
import com.projectgoth.fusion.data.RecommendationData;
import com.projectgoth.fusion.data.RecommendationItem;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.recommendation.delivery.Enums;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.hashtag.data.HashTagData;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.CreateException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RecommendationDeliveryUtils {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecommendationDeliveryUtils.class));
    private static final Logger spamLog = Logger.getLogger((String)"SpamLog");
    private static final String SPAM_LOG_PATTERN = "|%s|%s";
    public static final String RECOMMENDATION_NAME_SPACE = "Rec";
    private static final int GLOBAL_ID = -1;
    public static final String SOURCE_HASHMAP_NAME_SPACE = "S";
    private static LRUCache<String, List<RecommendationItem>> recommendationsCache;
    private static int recommendationCacheMaxSize;
    private static LazyLoader<Boolean> redisLocationLoggingEnabled;
    private static final Comparator<RecommendationItem> ORDER_BY_SCORE_DESC_COMPARATOR;

    public static String generateRecommendationKey(Enums.RecommendationTypeEnum type, Enums.RecommendationTargetEnum target, int targetId, String subtype) {
        return subtype == null ? String.format("%s:%s:%s", target.getRedisKeySpace().append(targetId), RECOMMENDATION_NAME_SPACE, type.value()) : String.format("%s:%s:%s:%s", target.getRedisKeySpace().append(targetId), RECOMMENDATION_NAME_SPACE, type.value(), subtype);
    }

    public static String generateRecommendationKey(Enums.RecommendationTypeEnum type, Enums.RecommendationTargetEnum target, int targetId) {
        return RecommendationDeliveryUtils.generateRecommendationKey(type, target, targetId, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RecommendationData getRecommendation(Enums.RecommendationTypeEnum typeEnum, Enums.RecommendationTargetEnum target, int targetId, String subtype, int limit, int offset, boolean refreshCache) {
        StringBuilder logStrBuilder;
        List<RecommendationItem> recommendations;
        block19: {
            block20: {
                Jedis handle;
                block18: {
                    if (typeEnum == Enums.RecommendationTypeEnum.USERRECOMMENDATION) {
                        return RecommendationDeliveryUtils.handleUserRecommendation(typeEnum, target, targetId, limit, offset, refreshCache);
                    }
                    handle = null;
                    recommendations = RecommendationDeliveryUtils.getCachedRecommendations(typeEnum, target, targetId, subtype);
                    if (!refreshCache && recommendations == null) {
                        recommendations = RecommendationDeliveryUtils.getCachedRecommendations(typeEnum, target, -1, subtype);
                    }
                    if (!refreshCache && recommendations != null) break block20;
                    try {
                        recommendations = new ArrayList<RecommendationItem>();
                        handle = Redis.getSlaveInstanceForEntityID(target.getRedisKeySpace(), targetId);
                        String key = RecommendationDeliveryUtils.generateRecommendationKey(typeEnum, target, targetId, subtype);
                        boolean fallBackToGlobal = false;
                        if (null == handle || !handle.exists(key).booleanValue()) {
                            StringBuilder logStrBuilder2;
                            if (log.isDebugEnabled()) {
                                logStrBuilder2 = RecommendationDeliveryUtils.append(typeEnum, target, targetId, limit, offset, refreshCache);
                                logStrBuilder2.append("null == handle || 0 == handle.exists( key ):key=[").append(key).append("].");
                                log.debug((Object)logStrBuilder2);
                            }
                            if (null != handle) {
                                Redis.disconnect(handle, log);
                            }
                            handle = Redis.getSlaveInstanceForEntityID(target.getRedisKeySpace(), -1);
                            key = RecommendationDeliveryUtils.generateRecommendationKey(typeEnum, target, -1, subtype);
                            fallBackToGlobal = true;
                            if (log.isDebugEnabled()) {
                                logStrBuilder2 = RecommendationDeliveryUtils.append(typeEnum, target, targetId, limit, offset, refreshCache);
                                logStrBuilder2.append("rediskeyspace=[").append((Object)target.getRedisKeySpace()).append("].handle=[").append(handle).append("].");
                                logStrBuilder2.append("key:[").append(key).append("].");
                                log.debug((Object)logStrBuilder2);
                            }
                        }
                        if (null != handle) {
                            Set resultSet = handle.zrevrangeWithScores(key, 0L, -1L);
                            for (Tuple tuple : resultSet) {
                                if (redisLocationLoggingEnabled.getValue().booleanValue()) {
                                    recommendations.add(new RecommendationItem(tuple, handle, key));
                                    continue;
                                }
                                recommendations.add(new RecommendationItem(tuple));
                            }
                            if (log.isDebugEnabled()) {
                                logStrBuilder = RecommendationDeliveryUtils.append(typeEnum, target, targetId, limit, offset, refreshCache);
                                logStrBuilder.append("redis key:[").append(key).append("].");
                                logStrBuilder.append("fallBackToGlobal:[").append(fallBackToGlobal).append("].");
                                logStrBuilder.append("recommendations from redis:[").append(recommendations + "]");
                                log.debug((Object)logStrBuilder);
                            }
                            if (fallBackToGlobal) {
                                RecommendationDeliveryUtils.setCachedRecommendations(recommendations, typeEnum, target, -1, subtype);
                            } else {
                                RecommendationDeliveryUtils.setCachedRecommendations(recommendations, typeEnum, target, targetId, subtype);
                            }
                            RecommendationDeliveryUtils.refreshCacheExpiry();
                            break block18;
                        }
                        log.warn((Object)("Unable to retrieve any recomendation for Type=" + typeEnum.toString() + " TargetType=" + target.toString() + " targetID=" + targetId));
                    }
                    catch (Exception e) {
                        try {
                            log.error((Object)("Failed to get recommendations: " + e.getMessage()), (Throwable)e);
                        }
                        catch (Throwable throwable) {
                            Redis.disconnect(handle, log);
                            throw throwable;
                        }
                        Redis.disconnect(handle, log);
                        break block19;
                    }
                }
                Redis.disconnect(handle, log);
                break block19;
            }
            if (log.isDebugEnabled()) {
                StringBuilder logStrBuilder3 = RecommendationDeliveryUtils.append(typeEnum, target, targetId, limit, offset, refreshCache);
                logStrBuilder3.append("recommendations from cache:[").append(recommendations + "]");
                log.debug((Object)logStrBuilder3);
            }
        }
        recommendations = RecommendationDeliveryUtils.combineFeaturedRecommendation(recommendations, typeEnum, target, targetId);
        recommendations = RecommendationDeliveryUtils.filterBlackListedItemsFromRecommendation(recommendations, typeEnum, target, targetId);
        recommendations = RecommendationDeliveryUtils.applyCustomFilterForRecommendation(recommendations, typeEnum, target, targetId);
        LinkedHashSet<RecommendationItem> set = new LinkedHashSet<RecommendationItem>(recommendations);
        recommendations.clear();
        recommendations.addAll(set);
        RecommendationData extractedData = RecommendationDeliveryUtils.extractRequestedPageFromRecommendation(recommendations, offset, limit);
        if (log.isDebugEnabled()) {
            logStrBuilder = RecommendationDeliveryUtils.append(typeEnum, target, targetId, limit, offset, refreshCache);
            logStrBuilder.append("Final Recommendations result:[").append(extractedData.getRecommendations() + "]");
            log.debug((Object)logStrBuilder);
        }
        return extractedData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static RecommendationData handleUserRecommendation(Enums.RecommendationTypeEnum typeEnum, Enums.RecommendationTargetEnum target, int targetId, int limit, int offset, boolean refreshCache) {
        Jedis handle = null;
        List<RecommendationItem> recommendations = RecommendationDeliveryUtils.getCachedRecommendations(typeEnum, target, targetId);
        List<RecommendationItem> globalRecommendations = null;
        List<RecommendationItem> regionalRecommendations = null;
        boolean fallBack = false;
        if (refreshCache || recommendations == null) {
            block17: {
                try {
                    recommendations = new ArrayList<RecommendationItem>();
                    handle = Redis.getSlaveInstanceForEntityID(target.getRedisKeySpace(), targetId);
                    String parentKey = RecommendationDeliveryUtils.generateRecommendationKey(typeEnum, target, targetId);
                    if (!RecommendationDeliveryUtils.userRecommendationKeyexists(handle, parentKey)) {
                        fallBack = true;
                        break block17;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)String.format("Retriving customzied user recommendation for user:%s", targetId));
                    }
                    HashMap<String, RecommendationItem> recUsersMap = new HashMap<String, RecommendationItem>();
                    HashMap<String, Integer> recUsersWeightMap = new HashMap<String, Integer>();
                    for (Enums.UserRecommendationEnum e : Enums.UserRecommendationEnum.values()) {
                        String sourceKey;
                        String key = RecommendationDeliveryUtils.getUserRecommendationKey(parentKey, e);
                        if (!RecommendationDeliveryUtils.checkAllKeysExist(handle, key, sourceKey = RecommendationDeliveryUtils.getUserRecommendationSourceKey(parentKey, e))) continue;
                        Pipeline pipeline = handle.pipelined();
                        pipeline.zrevrangeWithScores(key, 0L, -1L);
                        pipeline.hgetAll(sourceKey);
                        List pipelineResult = pipeline.syncAndReturnAll();
                        Set resultSet = (Set)pipelineResult.get(0);
                        Map sourceMap = (Map)pipelineResult.get(1);
                        if (resultSet == null || resultSet.size() == 0 || sourceMap == null || sourceMap.size() == 0) continue;
                        for (Tuple tuple : resultSet) {
                            String tupleKey = tuple.getElement();
                            if (!sourceMap.containsKey(tupleKey)) {
                                if (!log.isDebugEnabled()) continue;
                                log.debug((Object)String.format("Can not find key:%s in sourceMap for, RecommendationTypeEnum:%s, RecommendationTargetEnum:%s, targetId:%s, ingore it", tupleKey, typeEnum, target, targetId));
                                continue;
                            }
                            if (recUsersMap.containsKey(tupleKey)) {
                                RecommendationItem previousItem = (RecommendationItem)recUsersMap.get(tupleKey);
                                previousItem.setScore(previousItem.getScore() + RecommendationDeliveryUtils.getForUserRecommendationWeight(e, tuple));
                                recUsersMap.put(tupleKey, previousItem);
                                double weight = tuple.getScore();
                                if (recUsersWeightMap.containsKey(tupleKey)) {
                                    weight += (double)((Integer)recUsersWeightMap.get(tupleKey)).intValue();
                                }
                                recUsersWeightMap.put(tupleKey, (int)weight);
                                continue;
                            }
                            RecommendationItem item = redisLocationLoggingEnabled.getValue() != false ? new RecommendationItem(tuple, handle, key) : new RecommendationItem(tuple);
                            item.setScore(RecommendationDeliveryUtils.getForUserRecommendationWeight(e, tuple));
                            item.setSource((String)sourceMap.get(tupleKey));
                            recUsersMap.put(tupleKey, item);
                            recUsersWeightMap.put(tupleKey, (int)tuple.getScore());
                        }
                    }
                    String reasonDetailP1 = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.INDIVIDUAL_USER_RECOMMENDATION_REASON_DETAILS_PART1);
                    String reasonDetailP2 = reasonDetailP1 + SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.INDIVIDUAL_USER_RECOMMENDATION_REASON_DETAILS_PART2);
                    for (String k : recUsersMap.keySet()) {
                        if (!recUsersWeightMap.containsKey(k)) continue;
                        RecommendationItem item = (RecommendationItem)recUsersMap.get(k);
                        int sourceCount = (Integer)recUsersWeightMap.get(k);
                        if (sourceCount > 1) {
                            item.setReason(String.format(reasonDetailP2, sourceCount));
                        } else {
                            item.setReason(reasonDetailP1);
                        }
                        recommendations.add(item);
                    }
                    if (recommendations == null || recommendations.size() <= 0) break block17;
                    RecommendationDeliveryUtils.setCachedRecommendations(recommendations, typeEnum, target, targetId);
                    RecommendationDeliveryUtils.refreshCacheExpiry();
                }
                catch (Exception e) {
                    try {
                        log.error((Object)("Failed to get recommendations: " + e.getMessage()), (Throwable)e);
                    }
                    catch (Throwable throwable) {
                        Redis.disconnect(handle, log);
                        throw throwable;
                    }
                    Redis.disconnect(handle, log);
                }
            }
            Redis.disconnect(handle, log);
        }
        if (fallBack || SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.MIX_USER_RECOMMENDATION_ENABLE)) {
            globalRecommendations = RecommendationDeliveryUtils.getGlobalVerifiedUserRecommendation();
            regionalRecommendations = RecommendationDeliveryUtils.getRegionalActiveUserRecommendation(targetId);
            RecommendationDeliveryUtils.mixGlobalRegionalUserRecommendation(recommendations, globalRecommendations, regionalRecommendations);
        }
        recommendations = RecommendationDeliveryUtils.combineFeaturedRecommendation(recommendations, typeEnum, target, targetId);
        recommendations = RecommendationDeliveryUtils.filterBlackListedItemsFromRecommendation(recommendations, typeEnum, target, targetId);
        recommendations = RecommendationDeliveryUtils.applyCustomFilterForRecommendation(recommendations, typeEnum, target, targetId);
        LinkedHashSet<RecommendationItem> set = new LinkedHashSet<RecommendationItem>(recommendations);
        recommendations.clear();
        recommendations.addAll(set);
        RecommendationData extractedData = RecommendationDeliveryUtils.extractRequestedPageFromRecommendation(recommendations, offset, limit);
        if (log.isDebugEnabled()) {
            StringBuilder logStrBuilder = RecommendationDeliveryUtils.append(typeEnum, target, targetId, limit, offset, refreshCache);
            logStrBuilder.append("Final Recommendations result:[").append(extractedData.getRecommendations() + "]");
            log.debug((Object)logStrBuilder);
        }
        return extractedData;
    }

    public static void mixGlobalRegionalUserRecommendation(List<RecommendationItem> recommendation, List<RecommendationItem> globalRecommendations, List<RecommendationItem> regionalRecommendations) {
        double weight = SystemProperty.getDouble(SystemPropertyEntities.RecommendationServiceSettings.MIX_USER_RECOMMENDATION_WEIGHT);
        for (RecommendationItem item : recommendation) {
            item.setScore((int)((double)item.getScore() * weight));
        }
        if (SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.BOOST_GLOBAL_VERIFIED_USER_RECOMMENDATION_ENABLE) && recommendation.size() <= SystemProperty.getInt(SystemPropertyEntities.RecommendationServiceSettings.USER_RECOMMENDATION_MIN_THRESHOLD) && globalRecommendations != null && globalRecommendations.size() > 0) {
            double globalBoostWeight = SystemProperty.getDouble(SystemPropertyEntities.RecommendationServiceSettings.GLOBAL_VERIFIED_USER_RECOMMENDATION_BOOST_WEIGHT);
            for (RecommendationItem item : globalRecommendations) {
                item.setScore((int)((double)item.getScore() * globalBoostWeight));
            }
        }
        if (globalRecommendations != null && globalRecommendations.size() > 0) {
            recommendation.addAll(globalRecommendations);
        }
        if (regionalRecommendations != null && regionalRecommendations.size() > 0) {
            recommendation.addAll(regionalRecommendations);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<RecommendationItem> getGlobalVerifiedUserRecommendation() {
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.GLOBAL_VERIFIED_USER_RECOMMENDATION_ENABLE)) {
            return Collections.emptyList();
        }
        List<RecommendationItem> recommendations = RecommendationDeliveryUtils.getCachedRecommendations(Enums.RecommendationTypeEnum.USERRECOMMENDATION, Enums.RecommendationTargetEnum.INDIVIDUAL, -1);
        if (recommendations != null && recommendations.size() > 0) {
            return recommendations;
        }
        recommendations = new ArrayList<RecommendationItem>();
        Jedis handle = null;
        try {
            handle = Redis.getSlaveInstanceForEntityID(Redis.KeySpace.USER_ENTITY, -1);
            if (handle == null) {
                log.warn((Object)"Failed to get redis handle");
                List<RecommendationItem> list = null;
                return list;
            }
            String key = RecommendationDeliveryUtils.generateRecommendationKey(Enums.RecommendationTypeEnum.USERRECOMMENDATION, Enums.RecommendationTargetEnum.INDIVIDUAL, -1);
            if (!handle.exists(key).booleanValue()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)String.format("Global recommendation:%s does not exist!, check pyric's log for more details", key));
                }
                List<RecommendationItem> list = null;
                return list;
            }
            String globalUserRecommendationReason = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.GLOBAL_USER_RECOMMENDATION_REASON);
            Set resultSet = handle.zrevrangeWithScores(key, 0L, -1L);
            for (Tuple tuple : resultSet) {
                RecommendationItem item = redisLocationLoggingEnabled.getValue() != false ? new RecommendationItem(tuple, handle, key) : new RecommendationItem(tuple);
                item.setReason(globalUserRecommendationReason);
                recommendations.add(item);
            }
            RecommendationDeliveryUtils.setCachedRecommendations(recommendations, Enums.RecommendationTypeEnum.USERRECOMMENDATION, Enums.RecommendationTargetEnum.INDIVIDUAL, -1);
        }
        catch (Exception e) {
            log.error((Object)("Failed to get recommendations: " + e.getMessage()), (Throwable)e);
        }
        finally {
            if (handle != null) {
                Redis.disconnect(handle, log);
            }
        }
        return recommendations;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<RecommendationItem> getRegionalActiveUserRecommendation(int userid) {
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.REGIONAL_MOST_ACTIVE_USER_RECOMMENDATION_ENABLE)) {
            return Collections.emptyList();
        }
        List<RecommendationItem> recommendations = null;
        UserLocal userBean = null;
        Jedis handle = null;
        try {
            userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userBean.loadUserFromID(userid);
            if (userData == null) {
                log.error((Object)String.format("Failed to get UserData to userid:%s, skipping regional active user recommendtion", userid));
                List<RecommendationItem> list = null;
                return list;
            }
            if (userData.countryID == null) {
                log.error((Object)String.format("Failed to get country id (countryid is null) for user:%s, skipping regional active user recommendtion", userid));
                List<RecommendationItem> list = null;
                return list;
            }
            recommendations = RecommendationDeliveryUtils.getCachedRecommendations(Enums.RecommendationTypeEnum.USERRECOMMENDATION, Enums.RecommendationTargetEnum.COUNTRY, userData.countryID);
            if (recommendations != null && recommendations.size() > 0) {
                List<RecommendationItem> list = recommendations;
                return list;
            }
            recommendations = new ArrayList<RecommendationItem>();
            handle = Redis.getSlaveInstanceForEntityID(Redis.KeySpace.COUNTRY_ENTITY, userData.countryID);
            if (handle == null) {
                log.warn((Object)String.format("Failed to get redis handle, skipping regional active user recommendtion for user:%s", userid));
                List<RecommendationItem> list = null;
                Redis.disconnect(handle, log);
                return list;
            }
            String key = RecommendationDeliveryUtils.generateRecommendationKey(Enums.RecommendationTypeEnum.USERRECOMMENDATION, Enums.RecommendationTargetEnum.COUNTRY, userData.countryID);
            if (!handle.exists(key).booleanValue()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)String.format("Regional recommendation:%s does not exist!, check pyric's log for more details", key));
                }
                List<RecommendationItem> list = null;
                Redis.disconnect(handle, log);
                return list;
            }
            String regionalUserRecommendationReason = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.REGIONAL_USER_RECOMMENDATION_REASON);
            Set resultSet = handle.zrevrangeWithScores(key, 0L, -1L);
            double threshold = SystemProperty.getDouble(SystemPropertyEntities.RecommendationServiceSettings.REGIONAL_MOST_ACTIVE_USER_SPAM_THRESHOLD);
            for (Tuple tuple : resultSet) {
                if (tuple.getScore() >= threshold) {
                    spamLog.info((Object)String.format(SPAM_LOG_PATTERN, tuple.getElement(), tuple.getScore()));
                    continue;
                }
                RecommendationItem item = redisLocationLoggingEnabled.getValue() != false ? new RecommendationItem(tuple, handle, key) : new RecommendationItem(tuple);
                item.setReason(regionalUserRecommendationReason);
                recommendations.add(item);
            }
            RecommendationDeliveryUtils.setCachedRecommendations(recommendations, Enums.RecommendationTypeEnum.USERRECOMMENDATION, Enums.RecommendationTargetEnum.COUNTRY, userData.countryID);
            Redis.disconnect(handle, log);
            return recommendations;
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to get reginal active user recommendation to userid:%s, EJBException '%s'", userid, e.getMessage()));
            return recommendations;
        }
        finally {
            Redis.disconnect(handle, log);
        }
    }

    private static int getForUserRecommendationWeight(Enums.UserRecommendationEnum e, Tuple tuple) {
        double weight = 1.0;
        if (e == Enums.UserRecommendationEnum.FRIENDS_FOLLOWING) {
            weight = SystemProperty.getDouble(SystemPropertyEntities.RecommendationServiceSettings.USER_RECOMMENDATION_WEIGHT_FOR_FRIENDS);
        } else if (e == Enums.UserRecommendationEnum.FOLLOWERS_FOLLOWING) {
            weight = SystemProperty.getDouble(SystemPropertyEntities.RecommendationServiceSettings.USER_RECOMMENDATION_WEIGHT_FOR_FOLLOWER);
        } else if (e == Enums.UserRecommendationEnum.FOLLOWINGS_FOLLOWING) {
            weight = SystemProperty.getDouble(SystemPropertyEntities.RecommendationServiceSettings.USER_RECOMMENDATION_WEIGHT_FOR_FOLLOWING);
        }
        return (int)(weight * tuple.getScore());
    }

    private static String getUserRecommendationKey(String parentKey, Enums.UserRecommendationEnum userRecType) {
        return String.format("%s:%s", parentKey, userRecType.value());
    }

    private static String getUserRecommendationSourceKey(String parentKey, Enums.UserRecommendationEnum userRecType) {
        return String.format("%s:%s:%s", parentKey, userRecType.value(), SOURCE_HASHMAP_NAME_SPACE);
    }

    private static boolean userRecommendationKeyexists(Jedis handle, String parentKey) {
        if (handle == null) {
            return false;
        }
        Pipeline pipeline = handle.pipelined();
        for (Enums.UserRecommendationEnum e : Enums.UserRecommendationEnum.values()) {
            pipeline.exists(RecommendationDeliveryUtils.getUserRecommendationKey(parentKey, e));
        }
        List pipelineResult = pipeline.syncAndReturnAll();
        for (Object obj : pipelineResult) {
            if (!((Boolean)obj).booleanValue()) continue;
            return true;
        }
        return false;
    }

    private static boolean checkAllKeysExist(Jedis handle, String ... keys) {
        if (handle == null) {
            return false;
        }
        Pipeline pipeline = handle.pipelined();
        for (String key : keys) {
            pipeline.exists(key);
        }
        List pipelineResult = pipeline.syncAndReturnAll();
        for (Object obj : pipelineResult) {
            if (((Boolean)obj).booleanValue()) continue;
            return false;
        }
        return true;
    }

    private static StringBuilder append(Enums.RecommendationTypeEnum typeEnum, Enums.RecommendationTargetEnum target, int targetId, int limit, int offset, boolean refreshCache) {
        StringBuilder stb = new StringBuilder();
        stb.append("typeEnum=[").append(typeEnum).append("].");
        stb.append("target=[").append(target).append("].");
        stb.append("targetId=[").append(targetId).append("].");
        stb.append("limit=[").append(limit).append("].");
        stb.append("offset=[").append(offset).append("]");
        stb.append("refreshCache=[").append(refreshCache).append("].");
        return stb;
    }

    private static RecommendationData extractRequestedPageFromRecommendation(List<RecommendationItem> recommendations, int requestedOffset, int requestedLimit) {
        RecommendationData result = new RecommendationData();
        result.setRecommendations(RecommendationDeliveryUtils.sortAndSliceWithSideEffect(recommendations, ORDER_BY_SCORE_DESC_COMPARATOR, requestedOffset, requestedLimit));
        return result;
    }

    protected static List<RecommendationItem> getCachedRecommendations(Enums.RecommendationTypeEnum typeEnum, Enums.RecommendationTargetEnum target, int targetId, String subtype) {
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.RECOMMENDATIONS_CACHE_ENABLED)) {
            return null;
        }
        if (typeEnum == null || target == null) {
            return null;
        }
        String cacheKey = subtype == null ? String.format("%s:%s:%d", typeEnum.toString(), target.toString(), targetId) : String.format("%s:%s:%d:%s", typeEnum.toString(), target.toString(), targetId, subtype);
        List<RecommendationItem> cachedItems = recommendationsCache.get(cacheKey);
        return cachedItems == null ? null : new ArrayList<RecommendationItem>(cachedItems);
    }

    protected static List<RecommendationItem> getCachedRecommendations(Enums.RecommendationTypeEnum typeEnum, Enums.RecommendationTargetEnum target, int targetId) {
        return RecommendationDeliveryUtils.getCachedRecommendations(typeEnum, target, targetId, null);
    }

    protected static void clearCachedRecommendations() {
        recommendationsCache.clear();
    }

    protected static boolean setCachedRecommendations(List<RecommendationItem> items, Enums.RecommendationTypeEnum typeEnum, Enums.RecommendationTargetEnum target, int targetId, String subtype) {
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.RECOMMENDATIONS_CACHE_ENABLED)) {
            return false;
        }
        if (items == null || items.size() == 0) {
            return false;
        }
        String cacheKey = subtype == null ? String.format("%s:%s:%d", typeEnum.toString(), target.toString(), targetId) : String.format("%s:%s:%d:%s", typeEnum.toString(), target.toString(), targetId, subtype);
        recommendationsCache.put(cacheKey, new ArrayList<RecommendationItem>(items));
        return true;
    }

    protected static boolean setCachedRecommendations(List<RecommendationItem> items, Enums.RecommendationTypeEnum typeEnum, Enums.RecommendationTargetEnum target, int targetId) {
        return RecommendationDeliveryUtils.setCachedRecommendations(items, typeEnum, target, targetId, null);
    }

    protected static boolean refreshCacheExpiry() {
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.RECOMMENDATIONS_CACHE_ENABLED)) {
            return false;
        }
        long expiryInSeconds = SystemProperty.getLong(SystemPropertyEntities.RecommendationServiceSettings.RECOMMENDATIONS_CACHE_EXPIRY_IN_SECONDS);
        recommendationsCache.setExpiryTimeInMilliseconds(expiryInSeconds * 1000L);
        return true;
    }

    private static List<RecommendationItem> combineFeaturedRecommendation(List<RecommendationItem> original, Enums.RecommendationTypeEnum type, Enums.RecommendationTargetEnum target, int targetId) {
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.FEATURED_RECOMMENDATIONS_ENABLED)) {
            return original;
        }
        Collection<String> featuredRecommendations = RecommendationDeliveryUtils.getFeaturedRecommendations(type, target, targetId);
        String reason = null;
        if (type == Enums.RecommendationTypeEnum.USERRECOMMENDATION) {
            reason = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.GLOBAL_USER_RECOMMENDATION_REASON);
        }
        boolean isReasonEmpty = StringUtils.isEmpty(reason);
        ArrayList<RecommendationItem> featuredItems = new ArrayList<RecommendationItem>(featuredRecommendations.size());
        for (String featuredItemName : featuredRecommendations) {
            RecommendationItem featuredItem = new RecommendationItem(featuredItemName, Integer.MAX_VALUE);
            if (!isReasonEmpty) {
                featuredItem.setReason(reason);
            }
            featuredItem.setFeature(true);
            featuredItems.add(featuredItem);
        }
        original.addAll(0, featuredItems);
        return original;
    }

    private static Collection<String> getFeaturedRecommendations(Enums.RecommendationTypeEnum type, Enums.RecommendationTargetEnum target, int targetId) {
        String[] featuredRecommendations = Enums.RecommendationTargetEnum.COUNTRY == target ? SystemProperty.getArray(SystemPropertyEntities.RecommendationServiceSettings.FEATURED.forRecommendationType(type).forTargetType(target).forCountryId(targetId)) : SystemProperty.getArray(SystemPropertyEntities.RecommendationServiceSettings.FEATURED.forRecommendationType(type));
        return new LinkedHashSet<String>(Arrays.asList(featuredRecommendations));
    }

    protected static List<RecommendationItem> applyCustomFilterForRecommendation(List<RecommendationItem> original, Enums.RecommendationTypeEnum type, Enums.RecommendationTargetEnum target, int targetId) {
        if (type == Enums.RecommendationTypeEnum.ADDRESSBOOKCONTACTS) {
            return RecommendationDeliveryUtils.applyFilterForAddressBookContactsRecommendation(original);
        }
        if (type == Enums.RecommendationTypeEnum.HASH_TAGS && target == Enums.RecommendationTargetEnum.COUNTRY) {
            try {
                for (RecommendationItem recommendationItem : original) {
                    MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                    HashTagData htd = misEJB.getHashTagData(recommendationItem.getValue(), targetId);
                    if (htd == null) continue;
                    recommendationItem.setDescription(htd.getDescription());
                }
            }
            catch (Exception e) {
                log.warn((Object)"Unable to get description for hashtag", (Throwable)e);
            }
        }
        return original;
    }

    private static List<RecommendationItem> applyFilterForAddressBookContactsRecommendation(List<RecommendationItem> original) {
        ArrayList<RecommendationItem> filteredResult = new ArrayList<RecommendationItem>();
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            for (RecommendationItem item : original) {
                UserProfileData.StatusEnum status;
                Integer userid = Integer.valueOf(item.getValue());
                String username = userEJB.getUsernameByUserid(userid, null);
                if (username == null || (status = userEJB.getUserProfileStatus(username)) == null || status != UserProfileData.StatusEnum.PUBLIC) continue;
                filteredResult.add(item);
            }
        }
        catch (Exception ex) {
            log.error((Object)("Exception while calling applyFilterForAddressBookContactsRecommendation:" + ex.toString()), (Throwable)ex);
            return null;
        }
        return filteredResult;
    }

    protected static List<RecommendationItem> filterBlackListedItemsFromRecommendation(List<RecommendationItem> original, Enums.RecommendationTypeEnum type, Enums.RecommendationTargetEnum target, int targetId) {
        int i;
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.RECOMMENDATIONS_BLACKLIST_FILTER_ENABLED)) {
            return original;
        }
        Set<String> blacklistedSet = RecommendationDeliveryUtils.getBlackListedRecommendations(type);
        ArrayList<RecommendationItem> itemsToRemove = new ArrayList<RecommendationItem>();
        if (blacklistedSet.size() > 0) {
            for (i = 0; i < original.size(); ++i) {
                RecommendationItem item = original.get(i);
                if (!blacklistedSet.contains(RecommendationDeliveryUtils.normalizeItemName(item.getValue()))) continue;
                itemsToRemove.add(item);
            }
        }
        for (i = 0; i < itemsToRemove.size(); ++i) {
            RecommendationItem itemToRemove = (RecommendationItem)itemsToRemove.get(i);
            original.remove(itemToRemove);
        }
        return original;
    }

    private static String normalizeItemName(String string) {
        return StringUtil.trimmedLowerCase(string);
    }

    private static Set<String> getBlackListedRecommendations(Enums.RecommendationTypeEnum type) {
        List<String> blacklistedRecommendations = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.RecommendationServiceSettings.BLACKLISTED.forRecommendationType(type)));
        LinkedHashSet<String> blackListedSet = new LinkedHashSet<String>(blacklistedRecommendations.size());
        for (String blacklistedName : blacklistedRecommendations) {
            blackListedSet.add(RecommendationDeliveryUtils.normalizeItemName(blacklistedName));
        }
        return blackListedSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean updateRecommendation(RecommendationData recommendationData, Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum target, Integer targetId, boolean replaceExisting) {
        boolean i$;
        String key = RecommendationDeliveryUtils.generateRecommendationKey(recommendationType, target, targetId);
        Jedis handle = null;
        try {
            handle = Redis.getMasterInstanceForEntityID(target.getRedisKeySpace(), targetId);
            if (replaceExisting) {
                handle.del(key);
            }
            for (RecommendationItem item : recommendationData.getRecommendations()) {
                handle.zadd(key, (double)item.getScore(), item.getValue());
            }
            i$ = true;
        }
        catch (Exception e) {
            boolean bl;
            try {
                log.error((Object)("Exception Caught while populating redis data:" + e.getMessage()), (Throwable)e);
                bl = false;
            }
            catch (Throwable throwable) {
                Redis.disconnect(handle, log);
                throw throwable;
            }
            Redis.disconnect(handle, log);
            return bl;
        }
        Redis.disconnect(handle, log);
        return i$;
    }

    public static RecommendationData getFeaturedRecommendation(Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum target, Integer targetId, int limit, int offset) {
        List<RecommendationItem> recommendations = RecommendationDeliveryUtils.combineFeaturedRecommendation(new ArrayList<RecommendationItem>(), recommendationType, target, targetId);
        return RecommendationDeliveryUtils.extractRequestedPageFromRecommendation(recommendations, offset, limit);
    }

    public static RecommendationData getBlacklistedRecommendation(Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum target, Integer targetId, int limit, int offset) {
        List<String> blacklistedRecommendations = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.RecommendationServiceSettings.BLACKLISTED.forRecommendationType(recommendationType)));
        ArrayList<RecommendationItem> items = new ArrayList<RecommendationItem>();
        for (int i = 0; i < blacklistedRecommendations.size(); ++i) {
            items.add(new RecommendationItem(blacklistedRecommendations.get(i), Integer.MAX_VALUE));
        }
        return RecommendationDeliveryUtils.extractRequestedPageFromRecommendation(items, offset, limit);
    }

    public static boolean updateFeaturedRecommendation(List<String> items, Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum target, Integer targetId, boolean replaceExisting) {
        LinkedHashSet<String> featuredItems = new LinkedHashSet<String>();
        if (!replaceExisting) {
            String[] currentfeaturedRecommendations = target == Enums.RecommendationTargetEnum.COUNTRY ? SystemProperty.getArray(SystemPropertyEntities.RecommendationServiceSettings.FEATURED.forRecommendationType(recommendationType).forTargetType(target).forCountryId(targetId)) : SystemProperty.getArray(SystemPropertyEntities.RecommendationServiceSettings.FEATURED.forRecommendationType(recommendationType));
            featuredItems.addAll(Arrays.asList(currentfeaturedRecommendations));
        }
        featuredItems.addAll(items);
        try {
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            if (target == Enums.RecommendationTargetEnum.COUNTRY) {
                misEJB.updateSystemProperty(SystemPropertyEntities.RecommendationServiceSettings.FEATURED.forRecommendationType(recommendationType).forTargetType(target).forCountryId(targetId).getName(), StringUtil.join(featuredItems, ";"));
            } else {
                misEJB.updateSystemProperty(SystemPropertyEntities.RecommendationServiceSettings.FEATURED.forRecommendationType(recommendationType).getName(), StringUtil.join(featuredItems, ";"));
            }
            SystemProperty.resetCachedProperties();
            return true;
        }
        catch (CreateException e) {
            log.error((Object)("EJB Create Exception caught: " + e.getMessage()), (Throwable)e);
            return false;
        }
        catch (RemoteException e) {
            log.error((Object)("Remote Exception caught: " + e.getMessage()), (Throwable)e);
            return false;
        }
    }

    public static boolean updateBlacklistedRecommendation(List<String> items, Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum target, Integer targetId, boolean replaceExisting) {
        LinkedHashSet<String> blacklistedItems = new LinkedHashSet<String>();
        if (!replaceExisting) {
            String[] currentfeaturedRecommendations = SystemProperty.getArray(SystemPropertyEntities.RecommendationServiceSettings.BLACKLISTED.forRecommendationType(recommendationType));
            blacklistedItems.addAll(Arrays.asList(currentfeaturedRecommendations));
        }
        blacklistedItems.addAll(items);
        try {
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            misEJB.updateSystemProperty(SystemPropertyEntities.RecommendationServiceSettings.BLACKLISTED.forRecommendationType(recommendationType).getName(), StringUtil.join(blacklistedItems, ";"));
            SystemProperty.resetCachedProperties();
            return true;
        }
        catch (CreateException e) {
            log.error((Object)("EJB Create Exception caught: " + e.getMessage()), (Throwable)e);
            return false;
        }
        catch (RemoteException e) {
            log.error((Object)("Remote Exception caught: " + e.getMessage()), (Throwable)e);
            return false;
        }
    }

    private static SystemPropertyEntryWithParent forView(SSOEnums.View viewType, SystemPropertyEntities.RecommendationServiceSettings.RecommendationTargetSpecificSetting targetSpecificSettingProp) {
        return viewType == null ? targetSpecificSettingProp : targetSpecificSettingProp.forViewType(viewType);
    }

    private static int getSetting(SystemPropertyEntities.RecommendationServiceSettings rootProperty, Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum targetType, SSOEnums.View viewType) {
        if (recommendationType == null) {
            throw new IllegalArgumentException("recommendationType is null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("targetType is null");
        }
        SystemPropertyEntryWithParent sysProp = RecommendationDeliveryUtils.forView(viewType, rootProperty.forRecommendationType(recommendationType).forTargetType(targetType));
        return SystemProperty.getInt(sysProp);
    }

    public static int getDefaultResultSize(Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum targetType, SSOEnums.View viewType) {
        return RecommendationDeliveryUtils.getSetting(SystemPropertyEntities.RecommendationServiceSettings.DEFAULT_RESULT_SIZE, recommendationType, targetType, viewType);
    }

    public static int getAllowedMaxResultSize(Enums.RecommendationTypeEnum recommendationType, Enums.RecommendationTargetEnum targetType, SSOEnums.View viewType) {
        return RecommendationDeliveryUtils.getSetting(SystemPropertyEntities.RecommendationServiceSettings.MAX_ALLOWED_RESULT_SIZE, recommendationType, targetType, viewType);
    }

    public static <T> List<T> sortAndSliceWithSideEffect(List<T> sourceList, Comparator<T> comparator, int requestedStartIndex, int requestedCount) {
        int startIndex;
        if (sourceList == null) {
            return null;
        }
        if (requestedCount <= 0) {
            return Collections.emptyList();
        }
        if (requestedStartIndex >= sourceList.size()) {
            return Collections.emptyList();
        }
        int requestedToIndexExcl = requestedStartIndex + requestedCount;
        if (requestedToIndexExcl < 0) {
            return Collections.emptyList();
        }
        int toIndexExcl = requestedToIndexExcl > sourceList.size() ? sourceList.size() : requestedToIndexExcl;
        int n = startIndex = requestedStartIndex < 0 ? 0 : requestedStartIndex;
        if (comparator != null) {
            Collections.sort(sourceList, comparator);
        }
        return sourceList.subList(startIndex, toIndexExcl);
    }

    static {
        recommendationCacheMaxSize = 5000;
        redisLocationLoggingEnabled = new LazyLoader<Boolean>("REDIS_LOCATION_ERROR_LOGGING_ENABLED", 60000L){

            @Override
            protected Boolean fetchValue() throws Exception {
                return SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.REDIS_LOCATION_ERROR_LOGGING_ENABLED);
            }
        };
        try {
            long recommendationCacheExpiryInSeconds = SystemProperty.getLong(SystemPropertyEntities.RecommendationServiceSettings.RECOMMENDATIONS_CACHE_EXPIRY_IN_SECONDS);
            recommendationsCache = new LRUCache(recommendationCacheMaxSize, recommendationCacheExpiryInSeconds);
        }
        catch (Exception e) {
            log.error((Object)("Unexpected exception while initializing LRUCache :" + e.getMessage()), (Throwable)e);
        }
        ORDER_BY_SCORE_DESC_COMPARATOR = new Comparator<RecommendationItem>(){

            @Override
            public final int compare(RecommendationItem o1, RecommendationItem o2) {
                return o2.getScore() - o1.getScore();
            }
        };
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class RecommendationTypeEvaluator<T> {
        public abstract T evaluate(RecommendationItem var1);
    }
}

