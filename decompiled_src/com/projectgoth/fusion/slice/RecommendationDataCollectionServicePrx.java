/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface RecommendationDataCollectionServicePrx
extends ObjectPrx {
    public void logData(CollectedDataIce var1) throws FusionExceptionWithRefCode;

    public void logData(CollectedDataIce var1, Map<String, String> var2) throws FusionExceptionWithRefCode;
}

