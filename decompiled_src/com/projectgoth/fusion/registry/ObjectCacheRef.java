/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.registry;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import org.apache.log4j.Logger;

public class ObjectCacheRef {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ObjectCacheRef.class));
    private String hostName;
    private boolean online;
    private ObjectCachePrx cacheProxy;
    private ObjectCacheAdminPrx adminProxy;
    private ObjectCacheStats stats;

    public ObjectCacheRef(String hostName, ObjectCachePrx cacheProxy, ObjectCacheAdminPrx adminProxy) {
        this.hostName = hostName;
        this.online = true;
        this.cacheProxy = cacheProxy;
        this.adminProxy = adminProxy;
        try {
            this.stats = adminProxy.getStats();
        }
        catch (Exception e) {
            log.error((Object)"Unable to query for objectcache stats.", (Throwable)e);
        }
    }

    public String getHostName() {
        return this.hostName;
    }

    public ObjectCachePrx getCacheProxy() {
        return this.cacheProxy;
    }

    public ObjectCacheAdminPrx getAdminProxy() {
        return this.adminProxy;
    }

    @Deprecated
    public synchronized int getLoad() {
        return null != this.stats ? this.stats.numUserObjects : Integer.MAX_VALUE;
    }

    public void setStats(ObjectCacheStats stats) {
        this.stats = stats;
    }

    public ObjectCacheStats getStats() {
        return this.stats;
    }

    public synchronized boolean isOnline() {
        return this.online;
    }

    public synchronized void setOnline(boolean online) {
        this.online = online;
    }
}

