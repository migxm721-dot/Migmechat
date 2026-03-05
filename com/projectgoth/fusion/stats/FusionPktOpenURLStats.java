/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.stats;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.monitor.MonitorHelpers;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionPktOpenURLStats {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktOpenURLStats.class));
    private static final int MAX_FAILURES_BY_URL = 20;
    private AtomicInteger totalAttempts = new AtomicInteger(0);
    private AtomicInteger totalFailures = new AtomicInteger(0);
    private AtomicLong totalSuccessesProcessingTime = new AtomicLong(0L);
    private final Cache<String, AtomicInteger> failuresByURLCache = CacheBuilder.newBuilder().maximumSize(100L).build();
    private final ConcurrentMap<String, AtomicInteger> failuresByURL = this.failuresByURLCache.asMap();
    private final Pattern imgURLPattern = Pattern.compile("(http.*?img.*?com)(.*?)");

    private FusionPktOpenURLStats() {
    }

    public static FusionPktOpenURLStats getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addSuccess(long processingTime) {
        this.totalAttempts.incrementAndGet();
        this.totalSuccessesProcessingTime.addAndGet(processingTime);
    }

    public void addFailure(String url) {
        this.totalAttempts.incrementAndGet();
        this.totalFailures.incrementAndGet();
        if (url != null) {
            try {
                String urlString = url.toString();
                Matcher imgURLMatcher = this.imgURLPattern.matcher(urlString);
                String urlStub = imgURLMatcher.matches() ? imgURLMatcher.group(1) : urlString.split("\\?")[0];
                AtomicInteger ai = (AtomicInteger)this.failuresByURL.get(urlStub);
                if (ai == null) {
                    this.failuresByURL.putIfAbsent(urlStub, new AtomicInteger(0));
                    ai = (AtomicInteger)this.failuresByURL.get(urlStub);
                }
                ai.incrementAndGet();
            }
            catch (Exception e) {
                log.error((Object)("Exception recording OPEN_URL failure by URL: e=" + e), (Throwable)e);
            }
        }
    }

    public int getTotalAttempts() {
        return this.totalAttempts.get();
    }

    public int getTotalFailures() {
        return this.totalFailures.get();
    }

    public long getTotalSuccessesProcessingTime() {
        return this.totalSuccessesProcessingTime.get();
    }

    public Map<String, Integer> getFailuresByURL() {
        Map<String, Integer> clone = this.cloneMap(this.failuresByURL);
        Map result = MonitorHelpers.sortDesc(clone, 20);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Returning failuresByURL results of size=" + (result != null ? Integer.valueOf(result.size()) : "null")));
        }
        return result;
    }

    private Map<String, Integer> cloneMap(ConcurrentMap<String, AtomicInteger> source) {
        HashMap<String, Integer> clone = new HashMap<String, Integer>();
        Set keys = source.keySet();
        for (String key : keys) {
            clone.put(key, ((AtomicInteger)source.get(key)).intValue());
        }
        return clone;
    }

    private static class SingletonHolder {
        public static final FusionPktOpenURLStats INSTANCE = new FusionPktOpenURLStats();

        private SingletonHolder() {
        }
    }
}

