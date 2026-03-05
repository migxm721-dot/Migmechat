/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.datagrid;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedDistributedLock;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.datagrid.DataGrid;
import com.projectgoth.fusion.objectcache.ChatUserState;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class LocalFauxDataGrid
extends DataGrid {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(LocalFauxDataGrid.class));
    private final Cache<String, Map> mapsData = CacheBuilder.newBuilder().concurrencyLevel(this.getMapConcurrencyLevel()).expireAfterWrite((long)this.getMapExpirySeconds(), TimeUnit.SECONDS).build();
    private final ConcurrentMap<String, Map> mapsMap = this.mapsData.asMap();
    private final ExecutorService execService = Executors.newCachedThreadPool();

    protected abstract int getMapExpirySeconds();

    protected abstract int getMapConcurrencyLevel();

    protected LocalFauxDataGrid() {
    }

    @Override
    public void prepare() {
    }

    @Override
    public ExecutorService getExecutorService(String name) {
        return this.execService;
    }

    @Override
    public Lock getLock(Object key) {
        return new MemcachedLock(key);
    }

    @Override
    public Map<String, ChatUserState> getUsersMap() throws FusionException {
        throw new FusionException("Not implemented");
    }

    @Override
    public <K, V> void configMap(String name, int ttlSeconds) {
        Cache data = CacheBuilder.newBuilder().concurrencyLevel(this.getMapConcurrencyLevel()).expireAfterWrite((long)ttlSeconds, TimeUnit.SECONDS).build();
        ConcurrentMap map = data.asMap();
        this.mapsMap.put(name, map);
    }

    @Override
    public Map<String, Integer> getStringIntMap(String name) {
        return (Map)this.mapsMap.get(name);
    }

    @Override
    public void destroyLock(Lock lock) {
    }

    @Override
    public void destroyMap(String name) {
        this.mapsMap.remove(name);
    }

    @Override
    public String getStats() {
        try {
            int values = 0;
            for (Map m : this.mapsMap.values()) {
                if (m == null) continue;
                values += m.size();
            }
            return "LocalFauxDataGrid: map count=" + this.mapsMap.size() + " containing " + values + " total values";
        }
        catch (Exception e) {
            log.error((Object)("Unable to get LocalFauxDataGrid stats: e=" + e), (Throwable)e);
            return "Unable to get LocalFauxDataGrid stats";
        }
    }

    private class MemcachedLock
    implements Lock {
        private final String key;

        public MemcachedLock(Object key) {
            this.key = key.toString();
        }

        private String getLockID() {
            return MemCachedKeySpaces.CommonKeySpace.FAUX_DATA_GRID + this.key;
        }

        public void lock() {
            MemCachedDistributedLock.getDistributedLock(this.getLockID());
        }

        public void lockInterruptibly() throws InterruptedException {
            throw new RuntimeException("Not supported");
        }

        public boolean tryLock() {
            return MemCachedDistributedLock.getDistributedLock(this.getLockID(), 0L);
        }

        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return MemCachedDistributedLock.getDistributedLock(this.getLockID(), unit.toMillis(time));
        }

        public void unlock() {
            MemCachedDistributedLock.releaseDistributedLock(this.getLockID());
        }

        public Condition newCondition() {
            throw new RuntimeException("Not supported");
        }
    }
}

