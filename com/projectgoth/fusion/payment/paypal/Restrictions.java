/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  javax.ejb.CreateException
 */
package com.projectgoth.fusion.payment.paypal;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.payment.PaymentException;
import java.util.Date;
import java.util.HashMap;
import javax.ejb.CreateException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Restrictions {
    private static MemCachedClient MEMCACHE_CLIENT_RATE_LIMIT = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.rateLimit);

    public static boolean hitIndividualTransactionCount(UserData userData) {
        int threshold = Restrictions.getThreshold(userData.type, RateLimit.INDIVIDUAL_USER_TRANSACTIONS_COUNT, Integer.class);
        return threshold > 0 && MEMCACHE_CLIENT_RATE_LIMIT.getCounter(RateLimit.INDIVIDUAL_USER_TRANSACTIONS_COUNT.getMemcacheKey(userData.username)) >= (long)threshold;
    }

    public static boolean hitIndividualTransactionAmount(UserData userData, double amount) throws CreateException {
        AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
        double threshold = Restrictions.getThreshold(userData.type, RateLimit.INDIVIDUAL_TRANSACTIONS_AMOUNT, Double.class);
        return (threshold = accountEJB.convertCurrency(threshold, "AUD", "USD")) > 0.0 && Double.valueOf(MEMCACHE_CLIENT_RATE_LIMIT.get(RateLimit.INDIVIDUAL_TRANSACTIONS_AMOUNT.getMemcacheKey(userData.username)).toString()) + amount > threshold;
    }

    public static boolean hitCountryTransactionCount(UserData userData) {
        int threshold = Restrictions.getThreshold(userData.type, RateLimit.COUNTRY_TRANSACTIONS_COUNT, Integer.class);
        return threshold > 0 && MEMCACHE_CLIENT_RATE_LIMIT.getCounter(RateLimit.COUNTRY_TRANSACTIONS_COUNT.getMemcacheKey(userData.countryID)) > (long)threshold;
    }

    public static boolean hitCountryTransactionAmount(UserData userData, double amount) throws CreateException {
        AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
        double threshold = Restrictions.getThreshold(userData.type, RateLimit.COUNTRY_TRANSACTIONS_AMOUNT, Double.class);
        return (threshold = accountEJB.convertCurrency(threshold, "AUD", "USD")) > 0.0 && Double.valueOf(MEMCACHE_CLIENT_RATE_LIMIT.get(RateLimit.COUNTRY_TRANSACTIONS_AMOUNT.getMemcacheKey(userData.countryID)).toString()) + amount > threshold;
    }

    public static boolean hitAllTransactionCount(UserData userData) {
        int threshold = Restrictions.getThreshold(userData.type, RateLimit.ALL_USER_TRANSACTIONS_COUNT, Integer.class);
        return threshold > 0 && MEMCACHE_CLIENT_RATE_LIMIT.getCounter(RateLimit.ALL_USER_TRANSACTIONS_COUNT.getMemcacheKey()) > (long)threshold;
    }

    public static boolean hitAllTransactionAmount(UserData userData, double amount) throws CreateException {
        AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
        double threshold = Restrictions.getThreshold(userData.type, RateLimit.ALL_USER_TRANSACTIONS_AMOUNT, Double.class);
        return (threshold = accountEJB.convertCurrency(threshold, "AUD", "USD")) > 0.0 && Double.valueOf(MEMCACHE_CLIENT_RATE_LIMIT.get(RateLimit.ALL_USER_TRANSACTIONS_AMOUNT.getMemcacheKey()).toString()) + amount > threshold;
    }

    public static boolean hitPaypalAccountCount(UserData userData, String paypalAccount) {
        int threshold = Restrictions.getThreshold(userData.type, RateLimit.PAYPAL_ACCOUNT_TRANSACTIONS_COUNT, Integer.class);
        return threshold > 0 && MEMCACHE_CLIENT_RATE_LIMIT.getCounter(RateLimit.PAYPAL_ACCOUNT_TRANSACTIONS_COUNT.getMemcacheKey(paypalAccount)) > (long)threshold;
    }

    private static <T extends Number> T getThreshold(UserData.TypeEnum type, RateLimit rateLimit, Class<T> valueType) {
        String propValue = "";
        if (type == UserData.TypeEnum.MIG33) {
            propValue = SystemProperty.get(UserRestrictionsLookup.fromRateLimit(rateLimit.rateLimitVal).getSysPropEntry());
        } else if (type == UserData.TypeEnum.MIG33_MERCHANT) {
            propValue = SystemProperty.get(MerchantRestrictionsLookup.fromRateLimit(rateLimit.rateLimitVal).getSysPropEntry());
        } else if (type == UserData.TypeEnum.MIG33_TOP_MERCHANT) {
            propValue = SystemProperty.get(TopMerchantRestrictionsLookup.fromRateLimit(rateLimit.rateLimitVal).getSysPropEntry());
        } else {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
        }
        if (Double.class.equals(valueType)) {
            return (T)Double.valueOf(propValue);
        }
        if (Integer.class.equals(valueType)) {
            return (T)Integer.valueOf(propValue);
        }
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
    }

    public static double getMinimumAmount(UserData.TypeEnum type) {
        if (type == UserData.TypeEnum.MIG33) {
            return SystemProperty.getDouble(SystemPropertyEntities.Payments_PAYPAL.MIN_AMOUNT_USD_MIG33);
        }
        if (type == UserData.TypeEnum.MIG33_MERCHANT) {
            return SystemProperty.getDouble(SystemPropertyEntities.Payments_PAYPAL.MIN_AMOUNT_USD_MIG33_MERCHANT);
        }
        if (type == UserData.TypeEnum.MIG33_TOP_MERCHANT) {
            return SystemProperty.getDouble(SystemPropertyEntities.Payments_PAYPAL.MIN_AMOUNT_USD_MIG33_TOP_MERCHANT);
        }
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
    }

    public static double getMaximumAmount(UserData.TypeEnum type) {
        if (type == UserData.TypeEnum.MIG33) {
            return SystemProperty.getDouble(SystemPropertyEntities.Payments_PAYPAL.MAX_AMOUNT_USD_MIG33);
        }
        if (type == UserData.TypeEnum.MIG33_MERCHANT) {
            return SystemProperty.getDouble(SystemPropertyEntities.Payments_PAYPAL.MAX_AMOUNT_USD_MIG33_MERCHANT);
        }
        if (type == UserData.TypeEnum.MIG33_TOP_MERCHANT) {
            return SystemProperty.getDouble(SystemPropertyEntities.Payments_PAYPAL.MAX_AMOUNT_USD_MIG33_TOP_MERCHANT);
        }
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RateLimit {
        INDIVIDUAL_USER_TRANSACTIONS_COUNT(1, "PAYPAL_TX/USR_TXNS_CTR", 86400000L),
        ALL_USER_TRANSACTIONS_COUNT(2, "PAYPAL_TX/USR_ALL_TXNS_CTR", 600000L),
        PAYPAL_ACCOUNT_TRANSACTIONS_COUNT(3, "PAYPAL_TX/ACCT_TXNS_CTR", 600000L),
        COUNTRY_TRANSACTIONS_COUNT(4, "PAYPAL_TX/COUNTRY_TXNS_CTR", 600000L),
        INDIVIDUAL_TRANSACTIONS_AMOUNT(5, "PAYPAL_TX/USR_TTL_AMNT", 86400000L),
        ALL_USER_TRANSACTIONS_AMOUNT(6, "PAYPAL_TX/ALL_USR_TXNS_AMNT", 600000L),
        COUNTRY_TRANSACTIONS_AMOUNT(7, "PAYPAL_TX/COUNTRY_TXNS_AMNT", 600000L);

        private int rateLimitVal;
        private String memcacheKey;
        private long expiry;

        private RateLimit(int rateLimitVal, String memcacheKey, Long expiry) {
            this.rateLimitVal = rateLimitVal;
            this.memcacheKey = memcacheKey;
            this.expiry = expiry;
        }

        public int value() {
            return this.rateLimitVal;
        }

        public String getMemcacheKey() {
            return this.memcacheKey;
        }

        public String getMemcacheKey(String key) {
            return this.memcacheKey + "/" + key;
        }

        public String getMemcacheKey(int key) {
            return this.memcacheKey + "/" + key;
        }

        public Date getExpirationDate() {
            return new Date(System.currentTimeMillis() + this.expiry);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum TopMerchantRestrictionsLookup implements EnumUtils.IEnumValueGetter<Integer>
    {
        INDIVIDUAL_USER_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.INDIVIDUAL_USER_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_TRANSACTIONS_MIG33_TOP_MERCHANT),
        ALL_USER_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.ALL_USER_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_ALL_TRANSACTIONS_MIG33_TOP_MERCHANT),
        PAYPAL_ACCOUNT_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.PAYPAL_ACCOUNT_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_MIG33_TOP_MERCHANT),
        COUNTRY_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.COUNTRY_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_COUNTRY_TRANSACTIONS_MIG33_TOP_MERCHANT),
        INDIVIDUAL_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.INDIVIDUAL_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_USD_MIG33_TOP_MERCHANT),
        ALL_USER_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.ALL_USER_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_MIG33_TOP_MERCHANT),
        COUNTRY_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.COUNTRY_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_MIG33_TOP_MERCHANT);

        private Integer rateLimit;
        private SystemPropertyEntities.SystemPropertyEntryInterface sysPropEntry;
        private static HashMap<Integer, TopMerchantRestrictionsLookup> lookupByRateLimit;

        private TopMerchantRestrictionsLookup(int rateLimit, SystemPropertyEntities.SystemPropertyEntryInterface sysPropEntry) {
            this.rateLimit = rateLimit;
            this.sysPropEntry = sysPropEntry;
        }

        public SystemPropertyEntities.SystemPropertyEntryInterface getSysPropEntry() {
            return this.sysPropEntry;
        }

        public Integer getEnumValue() {
            return this.rateLimit;
        }

        public static TopMerchantRestrictionsLookup fromRateLimit(Integer rateLimit) {
            return lookupByRateLimit.get(rateLimit);
        }

        static {
            lookupByRateLimit = new HashMap();
            EnumUtils.populateLookUpMap(lookupByRateLimit, TopMerchantRestrictionsLookup.class);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum MerchantRestrictionsLookup implements EnumUtils.IEnumValueGetter<Integer>
    {
        INDIVIDUAL_USER_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.INDIVIDUAL_USER_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_TRANSACTIONS_MIG33_MERCHANT),
        ALL_USER_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.ALL_USER_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_ALL_TRANSACTIONS_MIG33_MERCHANT),
        PAYPAL_ACCOUNT_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.PAYPAL_ACCOUNT_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_MIG33_MERCHANT),
        COUNTRY_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.COUNTRY_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_COUNTRY_TRANSACTIONS_MIG33_MERCHANT),
        INDIVIDUAL_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.INDIVIDUAL_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_USD_MIG33_MERCHANT),
        ALL_USER_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.ALL_USER_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_MIG33_MERCHANT),
        COUNTRY_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.COUNTRY_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_MIG33_MERCHANT);

        private Integer rateLimit;
        private SystemPropertyEntities.SystemPropertyEntryInterface sysPropEntry;
        private static HashMap<Integer, MerchantRestrictionsLookup> lookupByRateLimit;

        private MerchantRestrictionsLookup(int rateLimit, SystemPropertyEntities.SystemPropertyEntryInterface sysPropEntry) {
            this.rateLimit = rateLimit;
            this.sysPropEntry = sysPropEntry;
        }

        public SystemPropertyEntities.SystemPropertyEntryInterface getSysPropEntry() {
            return this.sysPropEntry;
        }

        public Integer getEnumValue() {
            return this.rateLimit;
        }

        public static MerchantRestrictionsLookup fromRateLimit(Integer rateLimit) {
            return lookupByRateLimit.get(rateLimit);
        }

        static {
            lookupByRateLimit = new HashMap();
            EnumUtils.populateLookUpMap(lookupByRateLimit, MerchantRestrictionsLookup.class);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum UserRestrictionsLookup implements EnumUtils.IEnumValueGetter<Integer>
    {
        INDIVIDUAL_USER_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.INDIVIDUAL_USER_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_TRANSACTIONS_MIG33),
        ALL_USER_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.ALL_USER_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_ALL_TRANSACTIONS_MIG33),
        PAYPAL_ACCOUNT_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.PAYPAL_ACCOUNT_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_PAYPAL_ACCOUNT_TRANSACTIONS_MIG33),
        COUNTRY_TRANSACTIONS_COUNT(RateLimit.access$000(RateLimit.COUNTRY_TRANSACTIONS_COUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.NUMBER_COUNTRY_TRANSACTIONS_MIG33),
        INDIVIDUAL_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.INDIVIDUAL_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_USD_MIG33),
        ALL_USER_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.ALL_USER_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_ALL_TRANSACTIONS_USD_MIG33),
        COUNTRY_TRANSACTIONS_AMOUNT(RateLimit.access$000(RateLimit.COUNTRY_TRANSACTIONS_AMOUNT), SystemPropertyEntities.Payments_PAYPAL_RateLimit.TOTAL_AMOUNT_COUNTRY_TRANSACTIONS_USD_MIG33);

        private Integer rateLimit;
        private SystemPropertyEntities.SystemPropertyEntryInterface sysPropEntry;
        private static HashMap<Integer, UserRestrictionsLookup> lookupByRateLimit;

        private UserRestrictionsLookup(int rateLimit, SystemPropertyEntities.SystemPropertyEntryInterface sysPropEntry) {
            this.rateLimit = rateLimit;
            this.sysPropEntry = sysPropEntry;
        }

        public SystemPropertyEntities.SystemPropertyEntryInterface getSysPropEntry() {
            return this.sysPropEntry;
        }

        public Integer getEnumValue() {
            return this.rateLimit;
        }

        public static UserRestrictionsLookup fromRateLimit(Integer rateLimit) {
            return lookupByRateLimit.get(rateLimit);
        }

        static {
            lookupByRateLimit = new HashMap();
            EnumUtils.populateLookUpMap(lookupByRateLimit, UserRestrictionsLookup.class);
        }
    }
}

