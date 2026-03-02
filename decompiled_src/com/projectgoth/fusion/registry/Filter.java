/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.registry;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.registry.FilterImpl;
import com.projectgoth.fusion.registry.FilterType;
import com.projectgoth.fusion.registry.ObjectCacheLoadBalancer;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
enum Filter {
    USER_OBJECT_FILTER(new FilterImpl(FilterType.ADDITIVE){

        public double apply(double previous, ObjectCacheStats stats) {
            return previous + 1.getMultiplier(SystemPropertyEntities.ObjectCacheLoadBalancing.USER_OBJECT_MULTIPLIER) * (double)stats.numUserObjects;
        }
    }, 1),
    SESSION_OBJECT_FILTER(new FilterImpl(FilterType.ADDITIVE){

        public double apply(double previous, ObjectCacheStats stats) {
            return previous + 2.getMultiplier(SystemPropertyEntities.ObjectCacheLoadBalancing.SESSION_OBJECT_MULTIPLIER) * (double)stats.numSessionObjects;
        }
    }, 2),
    GROUP_CHAT_FILTER(new FilterImpl(FilterType.ADDITIVE){

        public double apply(double previous, ObjectCacheStats stats) {
            return previous + 3.getMultiplier(SystemPropertyEntities.ObjectCacheLoadBalancing.GC_MULTIPLIER) * (double)stats.numGroupChatObjects;
        }
    }, 4),
    CHAT_ROOM_FILTER(new FilterImpl(FilterType.ADDITIVE){

        public double apply(double previous, ObjectCacheStats stats) {
            return previous + 4.getMultiplier(SystemPropertyEntities.ObjectCacheLoadBalancing.CHATROOM_MULTIPLIER) * (double)stats.numChatRoomObjects;
        }
    }, 8),
    WEIGHTAGE_FILTER(new FilterImpl(FilterType.MULTIPLICATIVE){

        public double apply(double previous, ObjectCacheStats stats) {
            if (stats.weightage > 0) {
                return previous / (double)stats.weightage;
            }
            log.error((Object)("invalid weightage supplied" + stats.weightage));
            return previous;
        }
    }, 16),
    MEMORY_UTILIZATION_FILTER(new FilterImpl(FilterType.MULTIPLICATIVE){

        public double apply(double previous, ObjectCacheStats stats) {
            double utilization = 6.getMultiplier(SystemPropertyEntities.ObjectCacheLoadBalancing.MEMORY_UTILIZATION_MULTIPLIER) * (double)stats.jvmFreeMemory / (double)stats.jvmTotalMemory;
            if (utilization > 0.0) {
                return previous / utilization;
            }
            return previous / Math.abs(Double.MIN_VALUE);
        }
    }, 32);

    private static final Logger log;
    private FilterImpl filter;
    private int value;
    private static Set<Filter> additives;
    private static Set<Filter> multiplicatives;

    private Filter(FilterImpl filter, int value) {
        this.filter = filter;
        this.value = value;
    }

    public static Set<Filter> getAdditiveFilters() {
        return additives;
    }

    public static Set<Filter> getMultiplicativeFilters() {
        return multiplicatives;
    }

    public FilterImpl getImpl() {
        return this.filter;
    }

    public int getValue() {
        return this.value;
    }

    static {
        log = Logger.getLogger((String)ConfigUtils.getLoggerName(ObjectCacheLoadBalancer.class));
        additives = new HashSet<Filter>();
        multiplicatives = new HashSet<Filter>();
        block4: for (Filter filter : Filter.values()) {
            switch (filter.getImpl().getType()) {
                case ADDITIVE: {
                    additives.add(filter);
                    continue block4;
                }
                case MULTIPLICATIVE: {
                    multiplicatives.add(filter);
                    continue block4;
                }
            }
        }
    }
}

