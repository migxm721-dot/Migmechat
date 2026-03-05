/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface RecommendationGenerationServicePrx
extends ObjectPrx {
    public void runTransformation(int var1);

    public void runTransformation(int var1, Map<String, String> var2);
}

