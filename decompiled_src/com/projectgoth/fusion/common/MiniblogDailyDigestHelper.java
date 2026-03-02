/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.PipelineBlock
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.invitation.InvitationUtils;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ejb.CreateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MiniblogDailyDigestHelper {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MiniblogDailyDigestHelper.class));
    public static final String DAILY_DIGEST_TO_USERS_BEING_FOLLOWED = "DailyDigestToUserBeingFollowed";
    public static final String HASH_TABLE_FOR_EARLIEST_FOLLOWER = "HashTableForEarlistFollower";
    private static final String postData = "{}";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static AddRecipientResultEnum addRecipientForRecentlyFollowedDailyDigest(final int followerUserid, int followeeUserid, long currentTimeStampInMS) {
        AddRecipientResultEnum addRecipientResultEnum;
        String member;
        long score;
        Jedis handle;
        block16: {
            if (!SystemProperty.getBool(SystemPropertyEntities.JobSchedulerSettings.DAILY_DIGEST_FOR_USERS_BEING_FOLLOWED_ENABLED)) {
                return AddRecipientResultEnum.DAILY_DIGEST_TO_USERS_BEING_FOLLOWED_IS_DISABLE;
            }
            if (followerUserid <= 0) {
                log.error((Object)String.format("Unknown follower userid:%s", followerUserid));
                return AddRecipientResultEnum.UNKNOWN_FOLLOWER_USERID;
            }
            if (followeeUserid <= 0) {
                log.error((Object)String.format("Unknown followee userid:%s", followeeUserid));
                return AddRecipientResultEnum.UNKNOWN_FOLLOWEE_USERID;
            }
            if (followeeUserid == followerUserid) {
                log.error((Object)String.format("follower and followee should not be the same user:%s", followeeUserid));
                return AddRecipientResultEnum.ERROR_SAME_FOLLOWER_FOLOOWEE;
            }
            try {
                String[] blacklistedEmailDomains;
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userBean.loadUserFromID(followeeUserid);
                if (userData == null || !userData.emailVerified.booleanValue() || StringUtils.isEmpty((CharSequence)userData.emailAddress)) {
                    return AddRecipientResultEnum.NO_AVAILABLE_EMAIL_ADDRESS_FOR_FOLLOWEE;
                }
                if (!userData.allowToSendNewFollowerEmail()) {
                    return AddRecipientResultEnum.USER_CHOOSE_NOT_TO_RECEIVE_EMAIL;
                }
                if (!SystemProperty.getBool(SystemPropertyEntities.DailyDigest.SEND_DIGEST_TO_BLACK_LIST_DOMAIN_USER_ENABLED) && (blacklistedEmailDomains = SystemProperty.getArray(SystemPropertyEntities.Default.BLACKLISTED_EXTERNAL_MAIL_DOMAINS)) != null && blacklistedEmailDomains.length != 0) {
                    for (String domain : blacklistedEmailDomains) {
                        if (!userData.emailAddress.endsWith(domain)) continue;
                        log.warn((Object)String.format("user:%s emailaddress:%s is from blacklisted email domain:%s", followeeUserid, userData.emailAddress, domain));
                        return AddRecipientResultEnum.BLACKLISTED_EMAIL_ADDRESS_FOR_FOLLOWEE;
                    }
                }
            }
            catch (CreateException e) {
                log.error((Object)String.format("Failed to verify user emailaddress for user:%s", followeeUserid), (Throwable)e);
                return AddRecipientResultEnum.INTERNAL_ERROR;
            }
            handle = null;
            handle = Redis.getGamesMasterInstance();
            int lookBackHours = SystemProperty.getInt(SystemPropertyEntities.Email.TIME_SCOPE_FOR_DAILY_DIGEST_TO_USERS_BEING_FOLLOWERED_IN_HOUR, 24);
            score = currentTimeStampInMS / 1000L;
            long OneDayAgoScore = score - (long)(lookBackHours * 60 * 60);
            String key = DAILY_DIGEST_TO_USERS_BEING_FOLLOWED;
            String hashKey = HASH_TABLE_FOR_EARLIEST_FOLLOWER;
            member = String.valueOf(followeeUserid);
            Double timeStamp = handle.zscore(DAILY_DIGEST_TO_USERS_BEING_FOLLOWED, member);
            if (timeStamp == null || timeStamp < (double)OneDayAgoScore) break block16;
            AddRecipientResultEnum addRecipientResultEnum2 = AddRecipientResultEnum.NO_NEED_TO_UPDATE;
            Object var16_20 = null;
            Redis.disconnect(handle, log);
            return addRecipientResultEnum2;
        }
        try {
            handle.pipelined(new PipelineBlock(){

                public void execute() {
                    this.zadd(MiniblogDailyDigestHelper.DAILY_DIGEST_TO_USERS_BEING_FOLLOWED, score, member);
                    this.hset(MiniblogDailyDigestHelper.HASH_TABLE_FOR_EARLIEST_FOLLOWER, member, String.valueOf(followerUserid));
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("zadd member:%s to key:%s with score:%s for daily digest recipient:%s, and hset key:%s, value:%s to hash table:%s", member, DAILY_DIGEST_TO_USERS_BEING_FOLLOWED, score, followeeUserid, followeeUserid, followerUserid, HASH_TABLE_FOR_EARLIEST_FOLLOWER));
            }
            addRecipientResultEnum = AddRecipientResultEnum.SUCCESS_ADD_RECEIPIENT;
            Object var16_21 = null;
        }
        catch (Exception e) {
            try {
                log.error((Object)("Failed to add recipient due to: " + e), (Throwable)e);
                Object var16_22 = null;
            }
            catch (Throwable throwable) {
                Object var16_23 = null;
                Redis.disconnect(handle, log);
                throw throwable;
            }
            Redis.disconnect(handle, log);
            return AddRecipientResultEnum.INTERNAL_ERROR;
        }
        Redis.disconnect(handle, log);
        return addRecipientResultEnum;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String getRecipientValueForDailyDigest(int recipientUserId) {
        String string;
        if (!SystemProperty.getBool(SystemPropertyEntities.JobSchedulerSettings.DAILY_DIGEST_FOR_USERS_BEING_FOLLOWED_ENABLED)) {
            return null;
        }
        if (recipientUserId <= 0) {
            log.error((Object)String.format("Unknown follower userid:%s", recipientUserId));
            return null;
        }
        Jedis handle = null;
        try {
            handle = Redis.getGamesMasterInstance();
            String hashKey = HASH_TABLE_FOR_EARLIEST_FOLLOWER;
            String field = String.valueOf(recipientUserId);
            String value = handle.hget(hashKey, field);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("Retrieve ealiest user:%s who follows recipient:%s within a certain time scope", value, recipientUserId));
            }
            string = value;
            Object var7_7 = null;
        }
        catch (Exception e) {
            try {
                log.error((Object)"Failed to get Redis handle", (Throwable)e);
                Object var7_8 = null;
            }
            catch (Throwable throwable) {
                Object var7_9 = null;
                Redis.disconnect(handle, log);
                throw throwable;
            }
            Redis.disconnect(handle, log);
            return null;
        }
        Redis.disconnect(handle, log);
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static List<String> getRecipientsForDailyDigest(long timeStampInMS, int offset, int count) {
        ArrayList recipients = null;
        Jedis handle = null;
        try {
            try {
                handle = Redis.getGamesSlaveInstance();
                int lookBackHours = SystemProperty.getInt(SystemPropertyEntities.Email.TIME_SCOPE_FOR_DAILY_DIGEST_TO_USERS_BEING_FOLLOWERED_IN_HOUR, 24);
                long maxScore = timeStampInMS / 1000L;
                long minScore = timeStampInMS / 1000L - (long)(lookBackHours * 60 * 60);
                String key = DAILY_DIGEST_TO_USERS_BEING_FOLLOWED;
                Set recipientsSet = handle.zrangeByScore(key, (double)minScore, (double)maxScore, offset, count);
                recipients = new ArrayList();
                recipients.addAll(recipientsSet);
            }
            catch (Exception e) {
                log.error((Object)"Failed to get Redis handle", (Throwable)e);
                Object var14_12 = null;
                Redis.disconnect(handle, log);
                return recipients;
            }
            Object var14_11 = null;
        }
        catch (Throwable throwable) {
            Object var14_13 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
        return recipients;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static List<String> getEarlistFollowersListForRecipients(List<String> recipients) {
        if (recipients == null || recipients.size() == 0) {
            log.info((Object)"empty daily digest recipient list");
            return null;
        }
        List followers = null;
        Jedis handle = null;
        try {
            try {
                handle = Redis.getGamesSlaveInstance();
                String key = HASH_TABLE_FOR_EARLIEST_FOLLOWER;
                followers = handle.hmget(key, recipients.toArray(new String[0]));
            }
            catch (Exception e) {
                log.error((Object)"Failed to get Redis handle", (Throwable)e);
                Object var5_6 = null;
                Redis.disconnect(handle, log);
                return followers;
            }
            Object var5_5 = null;
        }
        catch (Throwable throwable) {
            Object var5_7 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
        return followers;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void truncateSortedSetAndHashForDailyDigest() {
        Jedis handle = null;
        try {
            try {
                handle = Redis.getGamesMasterInstance();
                String key = DAILY_DIGEST_TO_USERS_BEING_FOLLOWED;
                String hashKey = HASH_TABLE_FOR_EARLIEST_FOLLOWER;
                handle.del(new String[]{DAILY_DIGEST_TO_USERS_BEING_FOLLOWED, HASH_TABLE_FOR_EARLIEST_FOLLOWER});
            }
            catch (Exception e) {
                log.error((Object)"Failed to get Redis handle", (Throwable)e);
                Object var4_5 = null;
                Redis.disconnect(handle, log);
                return;
            }
            Object var4_4 = null;
        }
        catch (Throwable throwable) {
            Object var4_6 = null;
            Redis.disconnect(handle, log);
            throw throwable;
        }
        Redis.disconnect(handle, log);
    }

    public static long sendRecentlyFollowedDailyDigest(long timeStamp) {
        int count = SystemProperty.getInt(SystemPropertyEntities.Email.MAX_RETRIEVE_COUNT);
        int offset = 0;
        List<String> recipients = null;
        long total = 0L;
        do {
            List<String> followers;
            if ((followers = MiniblogDailyDigestHelper.getEarlistFollowersListForRecipients(recipients = MiniblogDailyDigestHelper.getRecipientsForDailyDigest(timeStamp, offset, count))) == null || followers.size() == 0) {
                log.info((Object)"empty daily digest recipient list, exit sending daily digest");
                break;
            }
            if (recipients.size() != followers.size()) {
                log.error((Object)"Failed to get correct followers list for recipients, exit sending daily digest");
                break;
            }
            MigboApiUtil apiUtil = MigboApiUtil.getInstance();
            long totalProcessed = 0L;
            long totalSentOK = 0L;
            long totalSentFailed = 0L;
            long totalNoProcess = 0L;
            long startTime = System.currentTimeMillis();
            total += (long)recipients.size();
            for (int i = 0; i < recipients.size(); ++i) {
                String follower = followers.get(i);
                if (follower == null) {
                    ++totalNoProcess;
                    continue;
                }
                ++totalProcessed;
                String userid = recipients.get(i);
                String pathPrefix = String.format("/user/%s/email/%s?follower=%s", userid, Enums.EmailTypeEnum.DAILY_DIGEST_FOR_USERS_BEING_FOLLOWED.value(), follower);
                try {
                    if (apiUtil.postAndCheckOk(pathPrefix, postData)) {
                        ++totalSentOK;
                        if (!log.isDebugEnabled()) continue;
                        log.debug((Object)String.format("Successfully triggered daily digest email for [%s] via migbo-datasvc [%s]", userid, pathPrefix));
                        continue;
                    }
                    ++totalSentFailed;
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)String.format("Faled to trigger email for [%s] via migbo-datasvc [%s]", userid, pathPrefix));
                    continue;
                }
                catch (Exception e) {
                    ++totalSentFailed;
                    log.error((Object)String.format("Unable to send daily digest email to userid[%s]", userid), (Throwable)e);
                }
            }
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info((Object)String.format("Processed [%d] users.  Sent OK [%d]. Sent Failed [%d], Non-process [%d]", totalProcessed, totalSentOK, totalSentFailed, totalNoProcess));
            log.info((Object)String.format("Migbo Daily Digest Email Trigger [COMPLETE] Time taken: %d seconds", TimeUnit.MILLISECONDS.toSeconds(timeTaken)));
            offset += count;
        } while (recipients != null || recipients.size() == count);
        return total;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AddRecipientResultEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        SUCCESS_ADD_RECEIPIENT(1),
        NO_NEED_TO_UPDATE(2),
        USER_CHOOSE_NOT_TO_RECEIVE_EMAIL(3),
        UNKNOWN_FOLLOWER_USERID(-1),
        UNKNOWN_FOLLOWEE_USERID(-2),
        ERROR_SAME_FOLLOWER_FOLOOWEE(-3),
        DAILY_DIGEST_TO_USERS_BEING_FOLLOWED_IS_DISABLE(-4),
        INTERNAL_ERROR(-5),
        NO_AVAILABLE_EMAIL_ADDRESS_FOR_FOLLOWEE(-6),
        BLACKLISTED_EMAIL_ADDRESS_FOR_FOLLOWEE(-7);

        private static final Map<Integer, InvitationUtils.SendInvitationResultEnum> lookup;
        int value;

        private AddRecipientResultEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static InvitationUtils.SendInvitationResultEnum fromValue(int v) {
            return lookup.get(v);
        }

        public Integer getEnumValue() {
            return this.value();
        }

        static {
            lookup = new HashMap<Integer, InvitationUtils.SendInvitationResultEnum>();
            EnumUtils.populateLookUpMap(lookup, InvitationUtils.SendInvitationResultEnum.class);
        }
    }
}

