/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.botservice;

import Ice.Current;
import com.projectgoth.fusion.botservice.BotService;
import com.projectgoth.fusion.slice.BotServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BotServiceAdminDisp;

public class BotServiceAdminI
extends _BotServiceAdminDisp {
    private BotService botService;

    public BotServiceAdminI(BotService botService) {
        this.botService = botService;
    }

    public BotServiceStats getStats(Current current) throws FusionException {
        return this.botService.getStats();
    }

    public int ping(Current current) {
        return this.botService.getStats().numBotObjects;
    }
}

