/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import java.util.Date;

public final class MemCachedKeySpaces {
    private static long convertTimeUnitToMilliSeconds(long cacheTime, TimeUnit cacheTimeUnit) throws RuntimeException {
        switch (cacheTimeUnit) {
            case MILLISECONDS: {
                return cacheTime;
            }
            case SECONDS: {
                return cacheTime * 1000L;
            }
            case MINUTES: {
                return cacheTime * 60000L;
            }
            case HOURS: {
                return cacheTime * 3600000L;
            }
            case DAYS: {
                return cacheTime * 86400000L;
            }
        }
        throw new RuntimeException("Invalid TimeUnit value provided");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TokenKeySpace implements MemCachedKeySpaceInterface
    {
        FORGOT_PASSWORD("FGT_PWD", 2L, TimeUnit.DAYS);

        private final String name;
        private final long cacheTime;
        private final Integer pageSize;
        private final MemCachedUtils.Instance memcachedInstance;
        private final String keyIdentifier = "TKN_ID";

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public long getCacheTime() {
            return this.cacheTime;
        }

        @Override
        public Integer getPageSize() {
            return this.pageSize;
        }

        @Override
        public Date getExpiryDate() {
            return new Date(System.currentTimeMillis() + this.cacheTime);
        }

        @Override
        public MemCachedUtils.Instance getMemCachedInstance() {
            return this.memcachedInstance;
        }

        private TokenKeySpace(String name, long cacheTime, TimeUnit cacheTimeUnit) {
            this(name, cacheTime, cacheTimeUnit, null);
        }

        private TokenKeySpace(String name, long cacheTime, TimeUnit cacheTimeUnit, Integer pageSize) {
            this.name = MemCachedKeyUtils.getFullKeyFromStrings("TKN_ID", name);
            this.cacheTime = MemCachedKeySpaces.convertTimeUnitToMilliSeconds(cacheTime, cacheTimeUnit);
            this.pageSize = pageSize;
            this.memcachedInstance = MemCachedUtils.Instance.rateLimit;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CommonKeySpace implements MemCachedKeySpaceInterface
    {
        ACCOUNT_BALANCE("BAL", 7L, TimeUnit.DAYS),
        ALERT_MESSAGE("AM", 5L, TimeUnit.MINUTES),
        AVATAR_COMMENTS_COUNT("ACC", 5L, TimeUnit.MINUTES),
        APPMENU("APM", 7L, TimeUnit.DAYS),
        APPMENU_OPTIONS("APMO", 7L, TimeUnit.DAYS),
        BOT_WARRIORS_BERSERK("BCH_BZ", 1L, TimeUnit.HOURS),
        BOT_WARRIORS_FEVER("BCH_FV", 1L, TimeUnit.HOURS),
        BOT_CHALLENGE_GAME_DATA("BCH_DATA", 7L, TimeUnit.DAYS),
        BLOCK_LIST("BL", 7L, TimeUnit.DAYS),
        CAMPAIGN("CMPGN", 1L, TimeUnit.DAYS),
        CAMPAIGN_PARTICIPANT("CMPGNP", 1L, TimeUnit.DAYS),
        CAMPAIGN_MOBILE("CMPGNM", 1L, TimeUnit.DAYS),
        CHATROOM("CHR", 7L, TimeUnit.DAYS),
        CHATROOMS_OWNED_COUNT("CHROC", 5L, TimeUnit.MINUTES),
        CHATROOM_BAN("CHB", 24L, TimeUnit.HOURS),
        CHATROOM_BLOCK_IP("CHBI", 30L, TimeUnit.MINUTES),
        CHATROOM_MODERATORS("CRM", 7L, TimeUnit.DAYS),
        CHATROOM_BANNED_USERS("CRBU", 7L, TimeUnit.DAYS, 10000),
        CHATROOM_NEGATIVE_CACHE("CRNC", 7L, TimeUnit.DAYS),
        CHATROOM_THEME("CRT", 7L, TimeUnit.DAYS),
        CONTACT_GROUP("CGR", 7L, TimeUnit.DAYS),
        CONTACT_LIST_VERSION("CLVV", 7L, TimeUnit.DAYS),
        CURRENCY("CUR", 15L, TimeUnit.MINUTES),
        DID_NUMBER("DID", 5L, TimeUnit.MINUTES),
        DISTRIBUTED_LOCK("LOCK", 30L, TimeUnit.SECONDS),
        EMOTE_GETMYLUCK("EGML", 1L, TimeUnit.DAYS),
        EMOTICON_PACKS_OWNED("EPO", 7L, TimeUnit.DAYS),
        EXTERNAL_EMAIL_VERIFICATION_TOKEN("EMVT", 604800L, TimeUnit.SECONDS),
        GET_INITIAL_COUNTER_VALUE_LOCK("GET_ICTR_LCK", 2L, TimeUnit.MINUTES),
        GIFTS_RECEIVED_COUNT("GRC", 5L, TimeUnit.MINUTES),
        GLOBAL_COLLECT_REPORT_FEED("GCRF", 7L, TimeUnit.DAYS),
        GROUP("GRP", 7L, TimeUnit.DAYS),
        GROUP_ANNOUNCEMENT("GA", 7L, TimeUnit.DAYS),
        GROUP_COUNT("GRPC", 1L, TimeUnit.HOURS),
        GROUPS_JOINED_COUNT("GRPJC", 5L, TimeUnit.MINUTES),
        GROUP_INVITATION("GI", 30L, TimeUnit.DAYS),
        GROUP_SUSPENSION("GRP_S", 1L, TimeUnit.HOURS),
        LOGIN_BAN("LB", 1L, TimeUnit.HOURS),
        MAX_SYSTEM_MIGLEVEL("MSML", 1L, TimeUnit.DAYS),
        MERCHANT_TAG_COUNT("MTTC", 1L, TimeUnit.DAYS),
        MERCHANT_TAG("MTT", 1L, TimeUnit.DAYS),
        MERCHANT_DETAILS("MTD", 1L, TimeUnit.DAYS),
        MERCHANT_GAME_LIMIT("MGLT", 1L, TimeUnit.DAYS),
        NUM_VIRTUAL_GIFTS_RECEIVED("NVGR", 7L, TimeUnit.DAYS),
        PAYPAL_TXN("PAYPAL_TXN", 3L, TimeUnit.HOURS),
        PAYPAL_TXN_LOCK("PAYPAL_TXNLCK", 2L, TimeUnit.MINUTES),
        PAINT_WARS_ITEMS("PWI", 7L, TimeUnit.DAYS),
        PHOTOS_UPLOADED_COUNT("PUC", 5L, TimeUnit.MINUTES),
        RECENT_CHATROOM_COUNT("RCC", 20L, TimeUnit.MINUTES),
        REPUTATION_SCORE("RSC", 1L, TimeUnit.HOURS),
        REPUTATION_LEVEL_DATA("RLD", 1L, TimeUnit.DAYS),
        SCRAPBOOK("SB", 7L, TimeUnit.DAYS),
        SSO_LOGIN_CAPTCHA_MAPPING("SLCM", 5L, TimeUnit.MINUTES),
        SYSTEM_PROPERTY("SYS", 1L, TimeUnit.MINUTES),
        TEMP_LOGIN_SUSPENSION("TL_S", (int)(Math.random() + 0.5) * 30, TimeUnit.SECONDS),
        USER_ALIAS_BY_USERID("UA_ID", 7L, TimeUnit.DAYS),
        USER_ALIAS_BY_USERNAME("UA_N", 7L, TimeUnit.DAYS),
        USER_ID("UID", 7L, TimeUnit.DAYS),
        USER_ID_BY_ALIAS("UID_A", 7L, TimeUnit.DAYS),
        USER_NAME_BY_ID("UN", 7L, TimeUnit.DAYS),
        USER_NAME_BY_ALIAS("UN_A", 7L, TimeUnit.DAYS),
        USER_PROFILE("UP", 7L, TimeUnit.DAYS),
        USER_PROFILE_STATUS("UPS", 7L, TimeUnit.DAYS),
        USER_SETTING("US", 7L, TimeUnit.DAYS),
        USER_REFERRAL_SUCCESS_RATE("URS", 1L, TimeUnit.DAYS),
        VAS_BUILD("VB", 5L, TimeUnit.MINUTES),
        VIRTUAL_GIFT_FEATURED_POPULAR_NEW("VG_FPN", 10L, TimeUnit.MINUTES),
        SECURITY_QUESTION("SEC_QN", 10L, TimeUnit.MINUTES),
        LOGIN_DATA("LG_DTA", 20L, TimeUnit.MINUTES),
        MIMOPAY_LOCK_RELOADKEY("MIMO_L_RK", 2L, TimeUnit.MINUTES),
        MINIMUM_CLIENT_VERSION("MCV", 5L, TimeUnit.MINUTES),
        EMAIL_TEMPLATE("EMT", 10L, TimeUnit.MINUTES),
        CHATROOM_BY_ID("CHRI", 7L, TimeUnit.DAYS),
        CAPTCHA_REQUIRED("CAP_RE", 7L, TimeUnit.DAYS),
        FAUX_DATA_GRID("FDG", 1L, TimeUnit.HOURS);

        private final String name;
        private final long cacheTime;
        private final Integer pageSize;
        private final MemCachedUtils.Instance memcachedInstance;

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public long getCacheTime() {
            return this.cacheTime;
        }

        @Override
        public Integer getPageSize() {
            return this.pageSize;
        }

        @Override
        public Date getExpiryDate() {
            return new Date(System.currentTimeMillis() + this.cacheTime);
        }

        @Override
        public MemCachedUtils.Instance getMemCachedInstance() {
            return this.memcachedInstance;
        }

        private CommonKeySpace(String name, long cacheTime, TimeUnit cacheTimeUnit) {
            this(name, cacheTime, cacheTimeUnit, null);
        }

        private CommonKeySpace(String name, long cacheTime, TimeUnit cacheTimeUnit, Integer pageSize) {
            this.name = name;
            this.cacheTime = MemCachedKeySpaces.convertTimeUnitToMilliSeconds(cacheTime, cacheTimeUnit);
            this.pageSize = pageSize;
            this.memcachedInstance = MemCachedUtils.Instance.common;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RateLimitKeySpace implements MemCachedKeySpaceInterface
    {
        FAILED_LOGIN_ATTEMPTS("FLA", 7L, TimeUnit.DAYS),
        FAILED_AUTHENTICATION_ATTEMPTS("FAA", 24L, TimeUnit.HOURS),
        FORGOT_PASSWORD_ATTEMPTS("FPA", 1440L, TimeUnit.MINUTES),
        GENERIC_EMOTE_RATE_LIMIT("GENERIC_EMOTE_RL", 1L, TimeUnit.MINUTES),
        GENERIC_FUSION_REQUEST_RATE_LIMIT("GENERIC_FUSION_REQUEST_RL", 10L, TimeUnit.MINUTES),
        MAX_CONCURRENT_SESSIONS_BREACHED("MCCSB", 1L, TimeUnit.HOURS),
        PAYMENT_RATE_LIMIT("PRL", 1L, TimeUnit.HOURS),
        RATE_LIMIT("RLT", 0L, TimeUnit.DAYS),
        USER_REFERRAL_RATE_LIMIT("UR_RL", 1L, TimeUnit.DAYS),
        VIRTUAL_GIFT_RATE_LIMIT("VG_RL", 1L, TimeUnit.MINUTES),
        CAMPAIGN_RATE_LIMIT("CAMPAIGN_RL", 1L, TimeUnit.MINUTES);

        private final String name;
        private final long cacheTime;
        private final Integer pageSize;
        private final MemCachedUtils.Instance memcachedInstance;

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public long getCacheTime() {
            return this.cacheTime;
        }

        @Override
        public Integer getPageSize() {
            return this.pageSize;
        }

        @Override
        public Date getExpiryDate() {
            return new Date(System.currentTimeMillis() + this.cacheTime);
        }

        @Override
        public MemCachedUtils.Instance getMemCachedInstance() {
            return this.memcachedInstance;
        }

        private RateLimitKeySpace(String name, long cacheTime, TimeUnit cacheTimeUnit) {
            this(name, cacheTime, cacheTimeUnit, null);
        }

        private RateLimitKeySpace(String name, long cacheTime, TimeUnit cacheTimeUnit, Integer pageSize) {
            this.name = name;
            this.cacheTime = MemCachedKeySpaces.convertTimeUnitToMilliSeconds(cacheTime, cacheTimeUnit);
            this.pageSize = pageSize;
            this.memcachedInstance = MemCachedUtils.Instance.rateLimit;
        }
    }

    public static interface MemCachedKeySpaceInterface {
        public String getName();

        public long getCacheTime();

        public Integer getPageSize();

        public Date getExpiryDate();

        public MemCachedUtils.Instance getMemCachedInstance();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TimeUnit {
        MILLISECONDS,
        SECONDS,
        MINUTES,
        HOURS,
        DAYS;

    }
}

