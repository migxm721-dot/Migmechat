/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice._BotChannelOperationsNC;

public interface _GroupChatOperationsNC
extends _BotChannelOperationsNC {
    public void addParticipantInner(String var1, String var2, boolean var3) throws FusionException;

    public void addParticipant(String var1, String var2) throws FusionException;

    public boolean removeParticipant(String var1) throws FusionException;

    public void putMessage(MessageDataIce var1) throws FusionException;

    public void putFileReceived(MessageDataIce var1) throws FusionException;

    public void sendInitialMessages();

    public int getNumParticipants();

    public boolean supportsBinaryMessage(String var1);

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3) throws FusionException;

    public String getId();

    public String getCreatorUsername();

    public int getCreatorUserID();

    public int getPrivateChatPartnerUserID();

    public String listOfParticipants();

    public int[] getParticipantUserIDs();

    public void addParticipants(String var1, String[] var2) throws FusionException;

    public void addUserToGroupChatDebug(String var1, boolean var2, boolean var3) throws FusionException;
}

