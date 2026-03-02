/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.BotChannelPrx;
import com.projectgoth.fusion.slice.BotInstance;
import com.projectgoth.fusion.slice.FusionException;

public interface _BotServiceOperationsNC {
    public BotInstance addBotToChannel(BotChannelPrx var1, String var2, String var3, boolean var4) throws FusionException;

    public void removeBot(String var1, boolean var2) throws FusionException;

    public void sendMessageToBot(String var1, String var2, String var3, long var4) throws FusionException;

    public void sendMessageToBotsInChannel(String var1, String var2, String var3, long var4) throws FusionException;

    public void sendNotificationToBotsInChannel(String var1, String var2, int var3) throws FusionException;
}

