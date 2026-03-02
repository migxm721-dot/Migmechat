/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ObjectCacheAdminPrx
extends ObjectPrx {
    public int ping();

    public int ping(Map<String, String> var1);

    public ObjectCacheStats getStats() throws FusionException;

    public ObjectCacheStats getStats(Map<String, String> var1) throws FusionException;

    public String[] getUsernames();

    public String[] getUsernames(Map<String, String> var1);

    public void reloadEmotes();

    public void reloadEmotes(Map<String, String> var1);

    public void setLoadWeightage(int var1);

    public void setLoadWeightage(int var1, Map<String, String> var2);

    public int getLoadWeightage();

    public int getLoadWeightage(Map<String, String> var1);
}

