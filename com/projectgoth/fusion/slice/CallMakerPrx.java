/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.CallDataIce;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface CallMakerPrx
extends ObjectPrx {
    public CallDataIce requestCallback(CallDataIce var1, int var2, int var3) throws FusionException;

    public CallDataIce requestCallback(CallDataIce var1, int var2, int var3, Map<String, String> var4) throws FusionException;
}

