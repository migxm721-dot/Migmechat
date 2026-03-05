/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class LazyLoader<TValueType> {
    private volatile TValueType cachedValue = null;
    private final long localCacheTTLms;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile long lastUpdatedTime = -1L;
    private final Logger logger;

    public LazyLoader(long localCacheTTLMillies) {
        this(null, localCacheTTLMillies);
    }

    public LazyLoader(String logCategorySubName, long localCacheTTLMillies) {
        this.localCacheTTLms = localCacheTTLMillies;
        this.logger = StringUtil.isBlank(logCategorySubName) ? Logger.getLogger((String)ConfigUtils.getLoggerName(this.getClass())) : Logger.getLogger((String)(ConfigUtils.getLoggerName(LazyLoader.class) + "[" + logCategorySubName + "]"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final TValueType getValue() {
        if (this.lastUpdatedTime == -1L) {
            this.lock.lock();
            try {
                if (this.lastUpdatedTime != -1L && this.getCurrentTimeMillis() - this.lastUpdatedTime <= this.localCacheTTLms) return this.cachedValue;
                this.refreshCache();
                return this.cachedValue;
            }
            finally {
                this.lock.unlock();
            }
        }
        if (this.getCurrentTimeMillis() - this.lastUpdatedTime <= this.localCacheTTLms || !this.lock.tryLock()) return this.cachedValue;
        try {
            this.refreshCache();
            return this.cachedValue;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void refreshCache() {
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"Refreshing cache");
            }
            TValueType lastValue = this.cachedValue;
            this.cachedValue = this.fetchValue();
            this.lastUpdatedTime = this.getCurrentTimeMillis();
            if (this.cachedValue == null && lastValue != null || this.cachedValue != null && !this.cachedValue.equals(lastValue)) {
                this.onValueChanged(lastValue, this.cachedValue);
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Cache refreshed with cachedValue=[" + this.cachedValue + "].Last Updated Time=[" + this.lastUpdatedTime + "]"));
            }
        }
        catch (Exception ex) {
            this.logger.error((Object)"Unable to refresh cache ", (Throwable)ex);
        }
    }

    protected void onValueChanged(TValueType lastValue, TValueType currentValue) {
    }

    protected final int isLocked() {
        return this.lock.isLocked() ? 0 : 1;
    }

    protected long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    protected final long getLastUpdatedTime() {
        return this.lastUpdatedTime;
    }

    protected abstract TValueType fetchValue() throws Exception;

    public final void invalidateCache() {
        this.lastUpdatedTime = -1L;
        this.onInvalidateCache();
    }

    protected void onInvalidateCache() {
    }

    protected Logger getLogger() {
        return this.logger;
    }
}

