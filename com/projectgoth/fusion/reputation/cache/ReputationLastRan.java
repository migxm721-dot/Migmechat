/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 */
package com.projectgoth.fusion.reputation.cache;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.MemCachedUtils;

public class ReputationLastRan {
    public static final String REPUTATION_NAMESPACE = "REP";
    public static final String SESSION_ARCHIVE_LAST_ID_KEY = "SAID";
    public static final String ACCOUNT_ENTRY_LAST_ID_KEY = "AEID";
    public static final String VIRTUAL_GIFT_LAST_ID_KEY = "VGID";
    public static final String PHONE_CALL_LAST_ID_KEY = "PCID";
    public static final String LAST_RUN_DATE_KEY = "LRD";
    public static final String SESSION_ARCHIVE_TABLE_NAME_KEY = "SATN";

    public static Integer getSessionArchiveLastId(MemCachedClient instance) {
        return (Integer)instance.get(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, SESSION_ARCHIVE_LAST_ID_KEY));
    }

    public static boolean setSessionArchiveLastId(MemCachedClient instance, Integer id) {
        return instance.set(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, SESSION_ARCHIVE_LAST_ID_KEY), (Object)id);
    }

    public static Integer getAccountEntryLastId(MemCachedClient instance) {
        return (Integer)instance.get(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, ACCOUNT_ENTRY_LAST_ID_KEY));
    }

    public static boolean setAccountEntryLastId(MemCachedClient instance, Integer id) {
        return instance.set(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, ACCOUNT_ENTRY_LAST_ID_KEY), (Object)id);
    }

    public static Integer getVirtualGiftLastId(MemCachedClient instance) {
        return (Integer)instance.get(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, VIRTUAL_GIFT_LAST_ID_KEY));
    }

    public static boolean setVirtualGiftLastId(MemCachedClient instance, Integer id) {
        return instance.set(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, VIRTUAL_GIFT_LAST_ID_KEY), (Object)id);
    }

    public static Integer getPhoneCallLastId(MemCachedClient instance) {
        return (Integer)instance.get(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, PHONE_CALL_LAST_ID_KEY));
    }

    public static boolean setPhoneCallLastId(MemCachedClient instance, Integer id) {
        return instance.set(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, PHONE_CALL_LAST_ID_KEY), (Object)id);
    }

    public static long getLastRunDate(MemCachedClient instance) {
        Object result = instance.get(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, LAST_RUN_DATE_KEY));
        if (result == null) {
            return 0L;
        }
        return (Long)result;
    }

    public static boolean setLastRunDate(MemCachedClient instance, long lastRunDate) {
        return instance.set(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, LAST_RUN_DATE_KEY), (Object)lastRunDate);
    }

    public static String getSessionArchiveTableName(MemCachedClient instance) {
        return (String)instance.get(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, SESSION_ARCHIVE_TABLE_NAME_KEY));
    }

    public static boolean setSessionArchiveTableName(MemCachedClient instance, String tableName) {
        return instance.set(MemCachedUtils.getCacheKeyInNamespace(REPUTATION_NAMESPACE, SESSION_ARCHIVE_TABLE_NAME_KEY), (Object)tableName);
    }
}

