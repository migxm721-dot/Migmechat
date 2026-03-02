/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.payment.paypal;

import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.payment.paypal.PaypalRateLimitKeySpace;
import java.sql.Timestamp;
import java.util.Date;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class PaypalInitialValueProvider<T>
implements MemCachedClientWrapper.InitialCounterValueProvider<T> {
    private PaypalRateLimitKeySpace rateLimitKeySpace;

    public PaypalInitialValueProvider(PaypalRateLimitKeySpace rateLimitKeySpace) {
        this.rateLimitKeySpace = rateLimitKeySpace;
    }

    public abstract Logger getLog();

    public PaypalRateLimitKeySpace getRateLimitKeySpace() {
        return this.rateLimitKeySpace;
    }

    @Override
    public MemCachedKeySpaces.MemCachedKeySpaceInterface getInitialValueLockKeySpace(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, T ctx) {
        return MemCachedKeySpaces.CommonKeySpace.GET_INITIAL_COUNTER_VALUE_LOCK;
    }

    @Override
    public String getInitialValueLockKeyName(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, T ctx) {
        String localKeyName = MemCachedKeyUtils.getFullKeyForKeySpace(keySpace, key);
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("LocalKeyName lock:" + localKeyName));
        }
        return localKeyName;
    }

    @Override
    public abstract long getInitialValue(MemCachedKeySpaces.MemCachedKeySpaceInterface var1, String var2, T var3);

    @Override
    public long getInitialValueLockWaitTime(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, T ctx) {
        return 30000L;
    }

    @Override
    public long getInitialValueTimeToLive(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String key, T ctx) {
        return SystemProperty.getNumber(this.rateLimitKeySpace.getCachedHitExpiryKey(), Long.class);
    }

    protected static AccountLocal getAccountLocal() throws CreateException {
        return (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
    }

    protected static Date getStartDate(Date endDate, long intervalInMillis) {
        return new Timestamp(endDate.getTime() - intervalInMillis);
    }

    protected QueryStartDateEndDateParam getQueryStartDateEndDate() {
        Timestamp endDateTime = new Timestamp(System.currentTimeMillis());
        long intervalInMillis = SystemProperty.getNumber(this.getRateLimitKeySpace().getQuotaLimitIntervalKey(), Long.class);
        Date startDateTime = PaypalInitialValueProvider.getStartDate(endDateTime, intervalInMillis);
        return new QueryStartDateEndDateParam(startDateTime, endDateTime);
    }

    public static class QueryStartDateEndDateParam {
        public Date startDate;
        public Date endDate;

        public QueryStartDateEndDateParam(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}

