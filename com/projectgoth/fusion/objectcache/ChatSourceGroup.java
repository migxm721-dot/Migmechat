/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;

public interface ChatSourceGroup {
    public void putMessage(MessageDataIce var1) throws FusionException;

    public String[] getParticipants(String var1);

    public boolean isParticipant(String var1) throws FusionException;

    public void sendGamesHelpToUser(String var1) throws FusionException;

    public void startBot(String var1, String var2) throws FusionException;

    public void stopAllBots(String var1, int var2) throws FusionException;

    public void stopBot(String var1, String var2) throws FusionException;
}

