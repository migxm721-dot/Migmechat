/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionCacheStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SessionCacheAdminPrx
extends ObjectPrx {
    public SessionCacheStats getStats() throws FusionException;

    public SessionCacheStats getStats(Map<String, String> var1) throws FusionException;
}

