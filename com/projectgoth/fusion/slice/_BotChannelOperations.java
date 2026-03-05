/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;

public interface _BotChannelOperations {
    public void startBot(String var1, String var2, Current var3) throws FusionException;

    public void stopBot(String var1, String var2, Current var3) throws FusionException;

    public void stopAllBots(String var1, int var2, Current var3) throws FusionException;

    public void botKilled(String var1, Current var2) throws FusionException;

    public void sendMessageToBots(String var1, String var2, long var3, Current var5) throws FusionException;

    public void putBotMessage(String var1, String var2, String var3, String[] var4, boolean var5, Current var6) throws FusionException;

    public void putBotMessageToUsers(String var1, String[] var2, String var3, String[] var4, boolean var5, Current var6) throws FusionException;

    public void putBotMessageToAllUsers(String var1, String var2, String[] var3, boolean var4, Current var5) throws FusionException;

    public void sendGamesHelpToUser(String var1, Current var2) throws FusionException;

    public boolean isParticipant(String var1, Current var2) throws FusionException;

    public String[] getParticipants(String var1, Current var2);
}

