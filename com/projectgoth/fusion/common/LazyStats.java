/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;

public abstract class LazyStats {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(LazyStats.class));
    protected static final double GIGA = 1.0E9;
    private AtomicLong lastStatsLoggingTime = new AtomicLong(0L);

    protected boolean isPeakTime() {
        int hr = Calendar.getInstance().get(11);
        return hr >= 6 && hr < 18;
    }

    public long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (!bean.isCurrentThreadCpuTimeSupported()) {
            return 0L;
        }
        if (!bean.isThreadCpuTimeEnabled()) {
            return 0L;
        }
        return bean.getCurrentThreadCpuTime();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void logStatsPeriodically() {
        if (this.isStatsEnabled() && System.currentTimeMillis() - this.lastStatsLoggingTime.get() > (long)(this.getStatsIntervalMinutes() * 60 * 1000)) {
            LazyStats lazyStats = this;
            synchronized (lazyStats) {
                if (System.currentTimeMillis() - this.lastStatsLoggingTime.get() > (long)(this.getStatsIntervalMinutes() * 60 * 1000)) {
                    this.doLog();
                    this.lastStatsLoggingTime.set(System.currentTimeMillis());
                }
            }
        }
    }

    protected abstract boolean isStatsEnabled();

    protected abstract int getStatsIntervalMinutes();

    protected abstract void doLog();
}

