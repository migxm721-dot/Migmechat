/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;

public interface ChatSourceRoom {
    public boolean isParticipant(String var1) throws FusionException;

    public boolean isVisibleParticipant(String var1) throws FusionException;

    public String[] getParticipants(String var1);

    public String[] getAllParticipants(String var1);

    public void putMessage(MessageDataIce var1, String var2) throws FusionException;

    public int getMaximumMessageLength(String var1);

    public ChatRoomData getNewRoomData();

    public void sendGamesHelpToUser(String var1) throws FusionException;

    public void startBot(String var1, String var2) throws FusionException;

    public void stopAllBots(String var1, int var2) throws FusionException;

    public void stopBot(String var1, String var2) throws FusionException;
}

