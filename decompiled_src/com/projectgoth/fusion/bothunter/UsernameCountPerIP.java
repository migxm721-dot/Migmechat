/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.projectgoth.fusion.common.ConfigUtils;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

public class UsernameCountPerIP {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UsernameCountPerIP.class));
    private final Cache<String, AtomicInteger> data = CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(10000L).expireAfterWrite(30L, TimeUnit.MINUTES).build();
    private final ConcurrentMap<String, AtomicInteger> map = this.data.asMap();

    private UsernameCountPerIP() {
    }

    public static UsernameCountPerIP getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public int incrementAndGet(String ip) {
        if (this.map.get(ip) == null) {
            this.map.put(ip, new AtomicInteger(0));
        }
        return ((AtomicInteger)this.map.get(ip)).incrementAndGet();
    }

    public int get(String ip) {
        if (this.map.get(ip) == null) {
            return 0;
        }
        return ((AtomicInteger)this.map.get(ip)).get();
    }

    private static class SingletonHolder {
        public static final UsernameCountPerIP INSTANCE = new UsernameCountPerIP();

        private SingletonHolder() {
        }
    }
}

