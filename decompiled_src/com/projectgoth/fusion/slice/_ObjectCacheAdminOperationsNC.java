/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCacheStats;

public interface _ObjectCacheAdminOperationsNC {
    public int ping();

    public ObjectCacheStats getStats() throws FusionException;

    public String[] getUsernames();

    public void reloadEmotes();

    public void setLoadWeightage(int var1);

    public int getLoadWeightage();
}

