/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.BotChannelPrx;
import com.projectgoth.fusion.slice.BotInstance;
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _BotServiceDel
extends _ObjectDel {
    public BotInstance addBotToChannel(BotChannelPrx var1, String var2, String var3, boolean var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void removeBot(String var1, boolean var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void sendMessageToBot(String var1, String var2, String var3, long var4, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void sendMessageToBotsInChannel(String var1, String var2, String var3, long var4, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void sendNotificationToBotsInChannel(String var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;
}

