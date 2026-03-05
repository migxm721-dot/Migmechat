/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.BotServiceStats;
import com.projectgoth.fusion.slice.FusionException;

public interface _BotServiceAdminOperations {
    public int ping(Current var1);

    public BotServiceStats getStats(Current var1) throws FusionException;
}

