/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LRUCache<K, V> {
    private static final float hashTableLoadFactor = 0.75f;
    private LinkedHashMap<K, CacheItem> map;
    private int cacheSize;
    private long expiryTimeInMilliseconds = 0L;

    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        int hashTableCapacity = (int)Math.ceil((float)cacheSize / 0.75f) + 1;
        this.map = new LinkedHashMap<K, CacheItem>(hashTableCapacity, 0.75f, true){
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheItem> eldest) {
                return this.size() > LRUCache.this.cacheSize;
            }
        };
    }

    public LRUCache(int cacheSize, long expiryTimeInSeconds) {
        this.cacheSize = cacheSize;
        this.expiryTimeInMilliseconds = expiryTimeInSeconds * 1000L;
        int hashTableCapacity = (int)Math.ceil((float)cacheSize / 0.75f) + 1;
        this.map = new LinkedHashMap<K, CacheItem>(hashTableCapacity, 0.75f, true){
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheItem> eldest) {
                return this.size() > LRUCache.this.cacheSize;
            }
        };
    }

    public synchronized V get(K key) {
        CacheItem item = this.map.get(key);
        if (item == null) {
            return null;
        }
        return item.getValue();
    }

    public synchronized void put(K key, V value) {
        this.map.put(key, new CacheItem(key, value));
    }

    public synchronized V remove(K key) {
        CacheItem removedItem = (CacheItem)this.map.remove(key);
        if (removedItem != null) {
            return removedItem.getValue();
        }
        return null;
    }

    public synchronized void clear() {
        this.map.clear();
    }

    public void setExpiryTimeInMilliseconds(long timeInMs) {
        this.expiryTimeInMilliseconds = timeInMs;
    }

    public synchronized int usedEntries() {
        return this.map.size();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class CacheItem {
        private K key;
        private V value;
        private long timeWhenCached;

        public CacheItem(K key, V value) {
            this.key = key;
            this.value = value;
            this.timeWhenCached = System.currentTimeMillis();
        }

        public V getValue() {
            if (LRUCache.this.expiryTimeInMilliseconds == 0L || System.currentTimeMillis() - this.timeWhenCached < LRUCache.this.expiryTimeInMilliseconds) {
                return this.value;
            }
            LRUCache.this.remove(this.key);
            return null;
        }
    }
}

