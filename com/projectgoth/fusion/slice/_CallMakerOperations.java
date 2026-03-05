/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.CallDataIce;
import com.projectgoth.fusion.slice.FusionException;

public interface _CallMakerOperations {
    public CallDataIce requestCallback(CallDataIce var1, int var2, int var3, Current var4) throws FusionException;
}

