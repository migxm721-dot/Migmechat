/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 */
package com.projectgoth.fusion.cache;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.MemCachedUtils;

public class SurgeMail {
    public static final String SURGEMAIL_PASSWORD_NAMESPACE = "SMP";

    public static String getPasswordKey(String username) {
        return MemCachedUtils.getCacheKeyInNamespace(SURGEMAIL_PASSWORD_NAMESPACE, username);
    }

    public static String getPassword(MemCachedClient instance, String username) {
        return (String)instance.get(SurgeMail.getPasswordKey(username));
    }

    public static boolean setPassword(MemCachedClient instance, String username, String password) {
        return instance.set(SurgeMail.getPasswordKey(username), (Object)password);
    }

    public static boolean deleteSurgeMailPassword(MemCachedClient instance, String username) {
        return instance.delete(SurgeMail.getPasswordKey(username));
    }
}

