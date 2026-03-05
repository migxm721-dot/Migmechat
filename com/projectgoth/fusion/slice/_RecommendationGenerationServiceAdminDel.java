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
import com.projectgoth.fusion.slice.RecommendationGenerationServiceStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _RecommendationGenerationServiceAdminDel
extends _ObjectDel {
    public RecommendationGenerationServiceStats getStats(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;
}

