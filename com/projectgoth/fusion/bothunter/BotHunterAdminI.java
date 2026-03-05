/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.bothunter;

import Ice.Current;
import com.projectgoth.fusion.bothunter.StatsCollector;
import com.projectgoth.fusion.slice.BotHunterStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BotHunterAdminDisp;

public class BotHunterAdminI
extends _BotHunterAdminDisp {
    public BotHunterStats getStats(Current __current) throws FusionException {
        try {
            return StatsCollector.getInstance().getStats();
        }
        catch (Exception e) {
            FusionException fe = new FusionException();
            fe.message = "Initialisation incomplete";
            throw fe;
        }
    }
}

