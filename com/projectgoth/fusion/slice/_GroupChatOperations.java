/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice._BotChannelOperations;

public interface _GroupChatOperations
extends _BotChannelOperations {
    public void addParticipantInner(String var1, String var2, boolean var3, Current var4) throws FusionException;

    public void addParticipant(String var1, String var2, Current var3) throws FusionException;

    public boolean removeParticipant(String var1, Current var2) throws FusionException;

    public void putMessage(MessageDataIce var1, Current var2) throws FusionException;

    public void putFileReceived(MessageDataIce var1, Current var2) throws FusionException;

    public void sendInitialMessages(Current var1);

    public int getNumParticipants(Current var1);

    public boolean supportsBinaryMessage(String var1, Current var2);

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Current var4) throws FusionException;

    public String getId(Current var1);

    public String getCreatorUsername(Current var1);

    public int getCreatorUserID(Current var1);

    public int getPrivateChatPartnerUserID(Current var1);

    public String listOfParticipants(Current var1);

    public int[] getParticipantUserIDs(Current var1);

    public void addParticipants(String var1, String[] var2, Current var3) throws FusionException;

    public void addUserToGroupChatDebug(String var1, boolean var2, boolean var3, Current var4) throws FusionException;
}

