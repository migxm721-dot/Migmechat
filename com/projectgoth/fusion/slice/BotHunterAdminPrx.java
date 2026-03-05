/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.BotHunterStats;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BotHunterAdminPrx
extends ObjectPrx {
    public BotHunterStats getStats() throws FusionException;

    public BotHunterStats getStats(Map<String, String> var1) throws FusionException;
}

