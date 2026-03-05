/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCacheStats;

public interface _ObjectCacheAdminOperations {
    public int ping(Current var1);

    public ObjectCacheStats getStats(Current var1) throws FusionException;

    public String[] getUsernames(Current var1);

    public void reloadEmotes(Current var1);

    public void setLoadWeightage(int var1, Current var2);

    public int getLoadWeightage(Current var1);
}

