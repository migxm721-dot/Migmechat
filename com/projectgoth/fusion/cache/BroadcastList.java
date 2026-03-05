/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 */
package com.projectgoth.fusion.cache;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedUtils;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BroadcastList {
    public static final String GROUP_BROADCAST_LIST_NAMESPACE = "GBL";
    public static final String BROADCAST_LIST_NAMESPACE = "BL";
    public static final String BROADCAST_LIST_DISTRIBUTED_LOCK_NAMESPACE = "BLDL";

    public static Set<String> newBroadcastList() {
        return new HashSet<String>();
    }

    public static Set<String> newBroadcastList(Collection<String> broadcastList) {
        if (broadcastList == null) {
            return null;
        }
        return new HashSet<String>(broadcastList);
    }

    public static String[] asArray(Set<String> broadcastList) {
        if (broadcastList == null) {
            return new String[0];
        }
        return broadcastList.toArray(new String[broadcastList.size()]);
    }

    public static String getKey(String username) {
        return MemCachedUtils.getCacheKeyInNamespace(BROADCAST_LIST_NAMESPACE, username);
    }

    public static String getGroupKey(int groupId) {
        return MemCachedUtils.getCacheKeyInNamespace(GROUP_BROADCAST_LIST_NAMESPACE, Integer.toString(groupId));
    }

    public static Set<String> getBroadcastList(MemCachedClient instance, String username) {
        if (instance == null) {
            instance = MemCachedClientWrapper.getMemCachedClient(MemCachedUtils.Instance.broadcastList);
        }
        return (Set)instance.get(BroadcastList.getKey(username));
    }

    public static Set<String> getGroupBroadcastList(MemCachedClient instance, int groupId) {
        return (Set)instance.get(BroadcastList.getGroupKey(groupId));
    }

    public static boolean setBroadcastList(MemCachedClient instance, String username, Set<String> bcl) {
        if (instance == null) {
            instance = MemCachedClientWrapper.getMemCachedClient(MemCachedUtils.Instance.broadcastList);
        }
        Calendar now = Calendar.getInstance();
        now.add(6, 5);
        return instance.set(BroadcastList.getKey(username), bcl, now.getTime());
    }

    public static boolean setGroupBroadcastList(MemCachedClient instance, int groupId, Set<String> bcl) {
        Calendar now = Calendar.getInstance();
        now.add(6, 5);
        return instance.set(BroadcastList.getGroupKey(groupId), bcl, now.getTime());
    }

    public static boolean deleteBroadcastList(MemCachedClient instance, String username) {
        return instance.delete(BroadcastList.getKey(username));
    }

    public static boolean deleteGroupBroadcastList(MemCachedClient instance, int groupId) {
        return instance.delete(BroadcastList.getGroupKey(groupId));
    }

    public static SortedSet<String> sort(Set<String> broadcastList) {
        TreeSet<String> sortedSet = new TreeSet<String>();
        sortedSet.addAll(broadcastList);
        return sortedSet;
    }
}

