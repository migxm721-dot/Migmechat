/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.userevent.store.buffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ARCCache<K, V> {
    public static final int CACHE_SIZE = 5;
    public static final int DOUBLE_CACHE_SIZE = 10;
    private List<K> listOnceTop = new ArrayList<K>();
    private List<K> listOnceBottom = new ArrayList<K>();
    private List<K> listTwiceTop = new ArrayList<K>();
    private List<K> listTwiceBottom = new ArrayList<K>();
    private ReentrantLock onceTopLock = new ReentrantLock();
    private ReentrantLock onceBottomLock = new ReentrantLock();
    private ReentrantLock twiceTopLock = new ReentrantLock();
    private ReentrantLock twiceBottomLock = new ReentrantLock();
    private final Map<List<K>, ReentrantLock> locks = new HashMap<List<K>, ReentrantLock>();
    private ConcurrentMap<K, V> cache = new ConcurrentHashMap();
    private int pages = 0;
    private ReentrantLock pagesLock;

    public ARCCache() {
        this.locks.put(this.listOnceTop, this.onceTopLock);
        this.locks.put(this.listOnceBottom, this.onceBottomLock);
        this.locks.put(this.listTwiceTop, this.twiceTopLock);
        this.locks.put(this.listTwiceBottom, this.twiceBottomLock);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private K removeLRU(List<K> list) {
        K k;
        ReentrantLock lock = this.locks.get(list);
        try {
            lock.lock();
            k = list.remove(list.size() - 1);
            Object var5_4 = null;
            lock.unlock();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            lock.unlock();
            throw throwable;
        }
        return k;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addMRU(List<K> list, K key) {
        ReentrantLock lock = this.locks.get(list);
        try {
            lock.lock();
            list.add(0, key);
            Object var5_4 = null;
            lock.unlock();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            lock.unlock();
            throw throwable;
        }
    }

    public abstract V getValueFromDB(K var1);

    public abstract void onCacheElementRemoval(K var1, V var2);

    private void replace(int p, boolean containedInBottomTwo) {
        int listOnceTopSize = this.listOnceTop.size();
        if (listOnceTopSize >= 1 && (containedInBottomTwo && listOnceTopSize == p || listOnceTopSize > p)) {
            K expiringItem = this.removeLRU(this.listOnceTop);
            this.addMRU(this.listOnceBottom, expiringItem);
            Object removedValue = this.cache.remove(expiringItem);
            this.onCacheElementRemoval(expiringItem, removedValue);
        } else {
            K expiringItem = this.removeLRU(this.listTwiceTop);
            this.addMRU(this.listTwiceBottom, expiringItem);
            Object removedValue = this.cache.remove(expiringItem);
            this.onCacheElementRemoval(expiringItem, removedValue);
        }
    }

    private int getListOnceSize() {
        return this.listOnceTop.size() + this.listOnceBottom.size();
    }

    private int getListTwiceSize() {
        return this.listTwiceTop.size() + this.listTwiceBottom.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private V case1(K key) {
        block7: {
            Object v;
            block6: {
                Object v2;
                try {
                    this.onceTopLock.lock();
                    if (!this.listOnceTop.remove(key)) break block6;
                    this.addMRU(this.listTwiceTop, key);
                    v2 = this.cache.get(key);
                    Object var4_4 = null;
                    this.onceTopLock.lock();
                }
                catch (Throwable throwable) {
                    Object var4_6 = null;
                    this.onceTopLock.lock();
                    throw throwable;
                }
                return v2;
            }
            Object var4_5 = null;
            this.onceTopLock.lock();
            try {
                this.twiceTopLock.lock();
                if (!this.listTwiceTop.remove(key)) break block7;
                this.addMRU(this.listTwiceTop, key);
                v = this.cache.get(key);
                Object var6_8 = null;
                this.twiceTopLock.lock();
            }
            catch (Throwable throwable) {
                Object var6_10 = null;
                this.twiceTopLock.lock();
                throw throwable;
            }
            return v;
        }
        Object var6_9 = null;
        this.twiceTopLock.lock();
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private V case2(K key) {
        boolean removed;
        try {
            this.onceBottomLock.lock();
            removed = this.listOnceBottom.remove(key);
            Object var4_3 = null;
            this.onceBottomLock.unlock();
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            this.onceBottomLock.unlock();
            throw throwable;
        }
        if (removed) {
            int bottomRatio = this.listTwiceBottom.size() / this.listOnceBottom.size();
            if (bottomRatio < 1) {
                bottomRatio = 1;
            }
            try {
                this.pagesLock.lock();
                this.pages += bottomRatio;
                if (this.pages > 5) {
                    this.pages = 5;
                }
                Object var6_7 = null;
                this.pagesLock.unlock();
            }
            catch (Throwable throwable) {
                Object var6_8 = null;
                this.pagesLock.unlock();
                throw throwable;
            }
            this.replace(this.pages, false);
            this.addMRU(this.listTwiceTop, key);
            V value = this.getValueFromDB(key);
            this.cache.put(key, value);
            return value;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private V case3(K key) {
        try {
            this.twiceBottomLock.lock();
            this.listTwiceBottom.remove(key);
            Object var3_2 = null;
            this.twiceBottomLock.unlock();
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            this.twiceBottomLock.unlock();
            throw throwable;
        }
        if (this.listTwiceBottom.remove(key)) {
            int bottomRatio = this.listOnceBottom.size() / this.listTwiceBottom.size();
            if (bottomRatio < 1) {
                bottomRatio = 1;
            }
            try {
                this.pagesLock.lock();
                this.pages += -1 * bottomRatio;
                if (this.pages < 0) {
                    this.pages = 0;
                }
                Object var5_6 = null;
                this.pagesLock.lock();
            }
            catch (Throwable throwable) {
                Object var5_7 = null;
                this.pagesLock.lock();
                throw throwable;
            }
            this.replace(this.pages, true);
            this.addMRU(this.listTwiceTop, key);
            V value = this.getValueFromDB(key);
            this.cache.put(key, value);
            return value;
        }
        return null;
    }

    private V case4(K key) {
        int listOnceSize = this.getListOnceSize();
        int listTwiceSize = 0;
        if (listOnceSize == 5) {
            if (this.listOnceTop.size() < 5) {
                this.removeLRU(this.listOnceBottom);
                this.replace(this.pages, false);
            } else {
                K removedKey = this.removeLRU(this.listOnceTop);
                Object removedValue = this.cache.remove(removedKey);
                this.onCacheElementRemoval(removedKey, removedValue);
            }
        } else if (listOnceSize < 5 && listOnceSize + (listTwiceSize = this.getListTwiceSize()) >= 5 && listOnceSize + listTwiceSize >= 10) {
            this.removeLRU(this.listTwiceBottom);
            this.replace(this.pages, false);
        }
        V value = this.getValueFromDB(key);
        this.addMRU(this.listOnceTop, key);
        this.cache.put(key, value);
        return value;
    }

    public V get(K key) {
        if (this.cache.containsKey(key)) {
            return this.case1(key);
        }
        V returnValue = this.case2(key);
        if (returnValue != null) {
            return returnValue;
        }
        returnValue = this.case3(key);
        if (returnValue != null) {
            return returnValue;
        }
        returnValue = this.case4(key);
        return returnValue;
    }
}

