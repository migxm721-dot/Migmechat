/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.EventSystemStats;
import com.projectgoth.fusion.slice.FusionException;

public interface _EventSystemAdminOperations {
    public EventSystemStats getStats(Current var1) throws FusionException;
}

