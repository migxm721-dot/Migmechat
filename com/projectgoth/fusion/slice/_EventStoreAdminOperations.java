/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.EventStoreStats;
import com.projectgoth.fusion.slice.FusionException;

public interface _EventStoreAdminOperations {
    public EventStoreStats getStats(Current var1) throws FusionException;
}

