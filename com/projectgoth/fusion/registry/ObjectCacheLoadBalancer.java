/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.registry;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.registry.Filter;
import com.projectgoth.fusion.registry.ObjectCacheRef;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjectCacheLoadBalancer {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ObjectCacheLoadBalancer.class));

    public static double getLoad(ObjectCacheStats stats, int filterMask) {
        if (null == stats) {
            return Double.MAX_VALUE;
        }
        double load = 0.0;
        for (Filter filter : Filter.getAdditiveFilters()) {
            if ((filter.getValue() & filterMask) != filter.getValue()) continue;
            load = filter.getImpl().apply(load, stats);
            if (!log.isDebugEnabled()) continue;
            log.debug((Object)("applied additive filter : " + (Object)((Object)filter) + ": load : " + load));
        }
        if (load > 0.0) {
            for (Filter filter : Filter.getMultiplicativeFilters()) {
                if ((filter.getValue() & filterMask) != filter.getValue()) continue;
                load = filter.getImpl().apply(load, stats);
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("applied multiplicative filter : " + (Object)((Object)filter) + ": load : " + load));
            }
        }
        return load;
    }

    public static double getLoad(ObjectCacheStats stats) {
        return ObjectCacheLoadBalancer.getLoad(stats, SystemProperty.getInt(SystemPropertyEntities.ObjectCacheLoadBalancing.FILTER_MASK));
    }

    public static ObjectCacheRef getLowestLoaded(Collection<ObjectCacheRef> objectCacheRefs) {
        return ObjectCacheLoadBalancer.getLowestLoaded(objectCacheRefs, SystemProperty.getInt(SystemPropertyEntities.ObjectCacheLoadBalancing.FILTER_MASK));
    }

    public static ObjectCacheRef getLowestLoaded(Collection<ObjectCacheRef> objectCaches, int filterMask) {
        try {
            return ObjectCacheLoadBalancer.getLowestLoaded_Distribution(objectCaches, filterMask);
        }
        catch (Exception e) {
            log.error((Object)"While getting lowest loaded objc: ", (Throwable)e);
            if (e.getCause() != null) {
                log.error((Object)"with root cause= ", e.getCause());
            }
            return null;
        }
    }

    private static ObjectCacheRef getLowestLoaded_Distribution(Collection<ObjectCacheRef> objectCaches, int filterMask) {
        int i;
        ArrayList<ObjectCacheRef> objectCacheList = new ArrayList<ObjectCacheRef>(objectCaches);
        double[] weights = new double[objectCacheList.size()];
        double total = 0.0;
        int maxTimeInMilliseconds = SystemProperty.getInt(SystemPropertyEntities.ObjectCacheLoadBalancing.OLDEST_ADMISSIBLE_STATS_SECONDS) * 1000;
        for (int i2 = 0; i2 < weights.length; ++i2) {
            double load;
            ObjectCacheStats stats;
            ObjectCacheRef objectCacheRef = (ObjectCacheRef)objectCacheList.get(i2);
            double weight = null == objectCacheRef || !objectCacheRef.isOnline() ? 0.0 : ((stats = objectCacheRef.getStats()) != null && System.currentTimeMillis() - stats.lastUpdatedTime > (long)maxTimeInMilliseconds ? 0.0 : ((load = ObjectCacheLoadBalancer.getLoad(stats, filterMask)) < 1.0 ? 1.0 : 1.0 / load));
            weights[i2] = weight;
            total += weight;
        }
        if (total == 0.0) {
            return null;
        }
        double random = Math.random() * total;
        double cumulative = 0.0;
        for (i = 0; i < weights.length; ++i) {
            if (!((cumulative += weights[i]) > random)) continue;
            return (ObjectCacheRef)objectCacheList.get(i);
        }
        for (i = weights.length - 1; i >= 0; --i) {
            if (weights[i] == 0.0) continue;
            return (ObjectCacheRef)objectCacheList.get(i);
        }
        log.warn((Object)"Unable to find object cache using distribution mechanism");
        return null;
    }
}

