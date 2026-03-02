/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ObjectCacheAdminDel
extends _ObjectDel {
    public int ping(Map<String, String> var1) throws LocalExceptionWrapper;

    public ObjectCacheStats getStats(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public String[] getUsernames(Map<String, String> var1) throws LocalExceptionWrapper;

    public void reloadEmotes(Map<String, String> var1) throws LocalExceptionWrapper;

    public void setLoadWeightage(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public int getLoadWeightage(Map<String, String> var1) throws LocalExceptionWrapper;
}

