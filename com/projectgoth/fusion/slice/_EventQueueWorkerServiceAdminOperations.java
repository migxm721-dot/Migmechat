/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.EventQueueWorkerServiceStats;
import com.projectgoth.fusion.slice.FusionException;

public interface _EventQueueWorkerServiceAdminOperations {
    public EventQueueWorkerServiceStats getStats(Current var1) throws FusionException;
}

