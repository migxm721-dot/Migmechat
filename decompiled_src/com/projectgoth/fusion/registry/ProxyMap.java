/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.registry;

import Ice.ObjectPrx;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ProxyMap<T extends ObjectPrx> {
    private ConcurrentMap<String, T> proxies = new ConcurrentHashMap<String, T>();
    private AtomicInteger maxProxies = new AtomicInteger();

    public ConcurrentMap<String, T> getProxies() {
        return this.proxies;
    }

    public T register(String name, T prx) {
        ObjectPrx proxy = (ObjectPrx)this.proxies.put(name, prx);
        this.updateStats();
        return (T)proxy;
    }

    public T deregister(String name) {
        return (T)((ObjectPrx)this.proxies.remove(name));
    }

    public T remove(String name) {
        return (T)((ObjectPrx)this.proxies.remove(name));
    }

    public T find(String name) {
        return (T)((ObjectPrx)this.proxies.get(name));
    }

    public HashMap<String, T> getClone() {
        return new HashMap<String, T>(this.proxies);
    }

    public int getMaxProxies() {
        return this.maxProxies.get();
    }

    public int size() {
        return this.proxies.size();
    }

    private void updateStats() {
        int current;
        int users = this.proxies.size();
        while (users > (current = this.maxProxies.get())) {
            if (!this.maxProxies.compareAndSet(current, users)) continue;
            return;
        }
    }

    public void purgeExpired() {
        HashMap<String, T> clone = new HashMap<String, T>(this.proxies);
        this.purgeExpired(clone.entrySet());
    }

    public abstract void purgeExpired(Set<Map.Entry<String, T>> var1);
}

