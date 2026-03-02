/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.payment.paypal;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentMetaDetails;
import com.projectgoth.fusion.payment.PaymentSummaryData;
import com.projectgoth.fusion.payment.paypal.PaypalInitialValueProvider;
import com.projectgoth.fusion.payment.paypal.PaypalRateLimitKeySpace;
import org.apache.log4j.Logger;

public class PaypalInitialValueProviders {
    public static final long DOLLAR_MULTIPLIER = 10000L;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AllCountPerPaypalUser
    extends PaypalInitialValueProvider<String> {
        public static final AllCountPerPaypalUser INSTANCE = new AllCountPerPaypalUser();
        private static final Logger LOG = Logger.getLogger((String)ConfigUtils.getLoggerName(AllValuePerCountry.class));

        public AllCountPerPaypalUser() {
            super(PaypalRateLimitKeySpace.TOTAL_SUCCESS_COUNT_PER_VENDOR_USERID);
        }

        @Override
        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, String paypalUserID) {
            try {
                PaypalInitialValueProvider.QueryStartDateEndDateParam queryStartDateEndDate = this.getQueryStartDateEndDate();
                AccountLocal accountLocal = AllCountPerPaypalUser.getAccountLocal();
                PaymentSummaryData summaryData = accountLocal.getPaymentSummaryByMetaDetails(PaymentData.TypeEnum.PAYPAL.value(), PaymentData.StatusEnum.APPROVED.value(), PaymentMetaDetails.MetaType.PAYPAL_ACCOUNT.code(), paypalUserID, queryStartDateEndDate.startDate, queryStartDateEndDate.endDate);
                return summaryData.count;
            }
            catch (Exception ex) {
                this.getLog().error((Object)"getInitialValue error", (Throwable)ex);
                return -1L;
            }
        }

        @Override
        public Logger getLog() {
            return LOG;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AllValuePerCountry
    extends PaypalInitialValueProvider<Param> {
        public static final AllValuePerCountry INSTANCE = new AllValuePerCountry();
        private static final Logger LOG = Logger.getLogger((String)ConfigUtils.getLoggerName(AllValuePerCountry.class));

        public AllValuePerCountry() {
            super(PaypalRateLimitKeySpace.TOTAL_SUCCESS_VALUE_PER_COUNTRY);
        }

        @Override
        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Param param) {
            try {
                PaypalInitialValueProvider.QueryStartDateEndDateParam queryStartDateEndDate = this.getQueryStartDateEndDate();
                AccountLocal accountLocal = AllValuePerCountry.getAccountLocal();
                PaymentSummaryData summaryData = accountLocal.getPaymentSummaryByCountryAndUserType(PaymentData.TypeEnum.PAYPAL.value(), param.countryID, param.userType.value(), PaymentData.StatusEnum.APPROVED.value(), queryStartDateEndDate.startDate, queryStartDateEndDate.endDate);
                return (long)(summaryData.cummValue * 10000.0);
            }
            catch (Exception ex) {
                this.getLog().error((Object)"getInitialValue error", (Throwable)ex);
                return -1L;
            }
        }

        @Override
        public Logger getLog() {
            return LOG;
        }

        public static class Param {
            public Integer countryID;
            public UserData.TypeEnum userType;

            public Param(Integer countryID, UserData.TypeEnum userType) {
                this.countryID = countryID;
                this.userType = userType;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AllCountPerCountry
    extends PaypalInitialValueProvider<Param> {
        public static final AllCountPerCountry INSTANCE = new AllCountPerCountry();
        private static final Logger LOG = Logger.getLogger((String)ConfigUtils.getLoggerName(AllCountPerCountry.class));

        public AllCountPerCountry() {
            super(PaypalRateLimitKeySpace.TOTAL_SUCCESS_COUNT_PER_COUNTRY);
        }

        @Override
        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Param param) {
            try {
                PaypalInitialValueProvider.QueryStartDateEndDateParam queryStartDateEndDate = this.getQueryStartDateEndDate();
                AccountLocal accountLocal = AllCountPerCountry.getAccountLocal();
                PaymentSummaryData summaryData = accountLocal.getPaymentSummaryByCountryAndUserType(PaymentData.TypeEnum.PAYPAL.value(), param.countryID, param.userType.value(), PaymentData.StatusEnum.APPROVED.value(), queryStartDateEndDate.startDate, queryStartDateEndDate.endDate);
                return summaryData.count;
            }
            catch (Exception ex) {
                this.getLog().error((Object)"getInitialValue error", (Throwable)ex);
                return -1L;
            }
        }

        @Override
        public Logger getLog() {
            return LOG;
        }

        public static class Param {
            public Integer countryID;
            public UserData.TypeEnum userType;

            public Param(Integer countryID, UserData.TypeEnum userType) {
                this.countryID = countryID;
                this.userType = userType;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AllValuePerUserType
    extends PaypalInitialValueProvider<UserData.TypeEnum> {
        public static final AllValuePerUserType INSTANCE = new AllValuePerUserType();
        private static final Logger LOG = Logger.getLogger((String)ConfigUtils.getLoggerName(AllValuePerUserType.class));

        public AllValuePerUserType() {
            super(PaypalRateLimitKeySpace.TOTAL_SUCCESS_VALUE_PER_USER_TYPE);
        }

        @Override
        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, UserData.TypeEnum userType) {
            try {
                PaypalInitialValueProvider.QueryStartDateEndDateParam queryStartDateEndDate = this.getQueryStartDateEndDate();
                AccountLocal accountLocal = AllValuePerUserType.getAccountLocal();
                PaymentSummaryData summaryData = accountLocal.getPaymentSummaryByUserType(PaymentData.TypeEnum.PAYPAL.value(), userType.value(), PaymentData.StatusEnum.APPROVED.value(), queryStartDateEndDate.startDate, queryStartDateEndDate.endDate);
                return (long)(summaryData.cummValue * 10000.0);
            }
            catch (Exception ex) {
                this.getLog().error((Object)"getInitialValue error", (Throwable)ex);
                return -1L;
            }
        }

        @Override
        public Logger getLog() {
            return LOG;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AllCountPerUserType
    extends PaypalInitialValueProvider<UserData.TypeEnum> {
        public static final AllCountPerUserType INSTANCE = new AllCountPerUserType();
        private static final Logger LOG = Logger.getLogger((String)ConfigUtils.getLoggerName(AllCountPerUserType.class));

        public AllCountPerUserType() {
            super(PaypalRateLimitKeySpace.TOTAL_SUCCESS_COUNT_PER_USERTYPE);
        }

        @Override
        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, UserData.TypeEnum userType) {
            try {
                PaypalInitialValueProvider.QueryStartDateEndDateParam queryStartDateEndDate = this.getQueryStartDateEndDate();
                AccountLocal accountLocal = AllCountPerUserType.getAccountLocal();
                PaymentSummaryData summaryData = accountLocal.getPaymentSummaryByUserType(PaymentData.TypeEnum.PAYPAL.value(), userType.value(), PaymentData.StatusEnum.APPROVED.value(), queryStartDateEndDate.startDate, queryStartDateEndDate.endDate);
                return summaryData.count;
            }
            catch (Exception ex) {
                this.getLog().error((Object)"getInitialValue error", (Throwable)ex);
                return -1L;
            }
        }

        @Override
        public Logger getLog() {
            return LOG;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AllValuePerUserID
    extends PaypalInitialValueProvider<Integer> {
        public static final AllValuePerUserID INSTANCE = new AllValuePerUserID();
        private static final Logger LOG = Logger.getLogger((String)ConfigUtils.getLoggerName(AllValuePerUserID.class));

        public AllValuePerUserID() {
            super(PaypalRateLimitKeySpace.TOTAL_SUCCESS_VALUE_PER_USERID);
        }

        @Override
        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Integer userID) {
            try {
                PaypalInitialValueProvider.QueryStartDateEndDateParam queryStartDateEndDate = this.getQueryStartDateEndDate();
                AccountLocal accountLocal = AllValuePerUserID.getAccountLocal();
                PaymentSummaryData summaryData = accountLocal.getPaymentSummaryByUserID(PaymentData.TypeEnum.PAYPAL.value(), userID, PaymentData.StatusEnum.APPROVED.value(), queryStartDateEndDate.startDate, queryStartDateEndDate.endDate);
                return (long)(summaryData.cummValue * 10000.0);
            }
            catch (Exception ex) {
                this.getLog().error((Object)"getInitialValue error", (Throwable)ex);
                return -1L;
            }
        }

        @Override
        public Logger getLog() {
            return LOG;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class AllCountPerUserID
    extends PaypalInitialValueProvider<Integer> {
        public static final AllCountPerUserID INSTANCE = new AllCountPerUserID();
        private static final Logger LOG = Logger.getLogger((String)ConfigUtils.getLoggerName(AllCountPerUserID.class));

        public AllCountPerUserID() {
            super(PaypalRateLimitKeySpace.TOTAL_SUCCESS_COUNT_PER_USERID);
        }

        @Override
        public long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, Integer userID) {
            try {
                PaypalInitialValueProvider.QueryStartDateEndDateParam queryStartDateEndDate = this.getQueryStartDateEndDate();
                AccountLocal accountLocal = AllCountPerUserID.getAccountLocal();
                PaymentSummaryData summaryData = accountLocal.getPaymentSummaryByUserID(PaymentData.TypeEnum.PAYPAL.value(), userID, PaymentData.StatusEnum.APPROVED.value(), queryStartDateEndDate.startDate, queryStartDateEndDate.endDate);
                return summaryData.count;
            }
            catch (Exception ex) {
                this.getLog().error((Object)"getInitialValue error", (Throwable)ex);
                return -1L;
            }
        }

        @Override
        public Logger getLog() {
            return LOG;
        }
    }
}

