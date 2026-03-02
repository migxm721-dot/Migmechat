/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.system.loadbalancing;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.userevent.system.loadbalancing.HashFunction;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ConsistentHash<T> {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ConsistentHash.class));
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
        for (T node : nodes) {
            this.add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < this.numberOfReplicas; ++i) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Adding node [" + node + i + "] as key [" + this.hashFunction.hash(node.toString() + i) + "]"));
            }
            this.circle.put(this.hashFunction.hash(node.toString() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < this.numberOfReplicas; ++i) {
            this.circle.remove(this.hashFunction.hash(node.toString() + i));
        }
    }

    public T get(String key) {
        if (this.circle.isEmpty()) {
            return null;
        }
        long hash = this.hashFunction.hash(key);
        log.debug((Object)("key hash is [" + hash + "]"));
        if (!this.circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = this.circle.tailMap(hash);
            hash = tailMap.isEmpty() ? this.circle.firstKey() : tailMap.firstKey();
            log.debug((Object)("circle did not contain the exact hash, the closest hash is [" + hash + "]"));
        }
        return (T)this.circle.get(hash);
    }
}

