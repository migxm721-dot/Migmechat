/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SMSEngineStats;

public interface _SMSEngineAdminOperations {
    public SMSEngineStats getStats(Current var1) throws FusionException;
}

