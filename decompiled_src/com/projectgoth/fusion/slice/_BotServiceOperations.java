/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.BotChannelPrx;
import com.projectgoth.fusion.slice.BotInstance;
import com.projectgoth.fusion.slice.FusionException;

public interface _BotServiceOperations {
    public BotInstance addBotToChannel(BotChannelPrx var1, String var2, String var3, boolean var4, Current var5) throws FusionException;

    public void removeBot(String var1, boolean var2, Current var3) throws FusionException;

    public void sendMessageToBot(String var1, String var2, String var3, long var4, Current var6) throws FusionException;

    public void sendMessageToBotsInChannel(String var1, String var2, String var3, long var4, Current var6) throws FusionException;

    public void sendNotificationToBotsInChannel(String var1, String var2, int var3, Current var4) throws FusionException;
}

