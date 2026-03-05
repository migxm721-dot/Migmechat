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
import com.projectgoth.fusion.slice.FusionException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _BotChannelDel
extends _ObjectDel {
    public void startBot(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void stopBot(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void stopAllBots(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void botKilled(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void sendMessageToBots(String var1, String var2, long var3, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void putBotMessage(String var1, String var2, String var3, String[] var4, boolean var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void putBotMessageToUsers(String var1, String[] var2, String var3, String[] var4, boolean var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void putBotMessageToAllUsers(String var1, String var2, String[] var3, boolean var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void sendGamesHelpToUser(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public boolean isParticipant(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public String[] getParticipants(String var1, Map<String, String> var2) throws LocalExceptionWrapper;
}

