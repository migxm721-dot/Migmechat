/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.BotServiceStats;
import com.projectgoth.fusion.slice.FusionException;

public interface _BotServiceAdminOperationsNC {
    public int ping();

    public BotServiceStats getStats() throws FusionException;
}

