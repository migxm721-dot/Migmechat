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
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _RecommendationDataCollectionServiceAdminDel
extends _ObjectDel {
    public RecommendationDataCollectionServiceStats getStats(Map<String, String> var1) throws LocalExceptionWrapper, FusionExceptionWithRefCode;
}

