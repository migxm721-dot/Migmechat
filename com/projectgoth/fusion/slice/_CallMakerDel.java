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
import com.projectgoth.fusion.slice.CallDataIce;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _CallMakerDel
extends _ObjectDel {
    public CallDataIce requestCallback(CallDataIce var1, int var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;
}

