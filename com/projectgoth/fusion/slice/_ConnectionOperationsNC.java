/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.AMD_Connection_putMessageAsync;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactGroupDataIce;
import com.projectgoth.fusion.slice.CurrencyDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageStatusEventIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserPrx;

public interface _ConnectionOperationsNC {
    public String getUsername();

    public ChatRoomDataIce[] getPopularChatRooms() throws FusionException;

    public String getRemoteIPAddress();

    public String getMobileDevice();

    public String getUserAgent();

    public int getDeviceTypeAsInt();

    public short getClientVersion();

    public UserPrx getUserObject();

    public SessionPrx getSessionObject();

    public boolean processPacket(ConnectionPrx var1, byte[] var2) throws FusionException;

    public void packetProcessed(byte[] var1);

    public void disconnect(String var1) throws FusionException;

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5) throws FusionException;

    public void contactChangedPresenceOneWay(int var1, int var2, int var3);

    public void contactChangedDisplayPictureOneWay(int var1, String var2, long var3);

    public void contactChangedStatusMessageOneWay(int var1, String var2, long var3);

    public void contactRequest(String var1, int var2) throws FusionException;

    public void contactRequestAccepted(ContactDataIce var1, int var2, int var3) throws FusionException;

    public void contactRequestRejected(String var1, int var2) throws FusionException;

    public void contactGroupAdded(ContactGroupDataIce var1, int var2) throws FusionException;

    public void contactGroupRemoved(int var1, int var2) throws FusionException;

    public void contactAdded(ContactDataIce var1, int var2, boolean var3) throws FusionException;

    public void contactRemoved(int var1, int var2) throws FusionException;

    public void otherIMLoggedIn(int var1) throws FusionException;

    public void otherIMLoggedOut(int var1, String var2) throws FusionException;

    public void otherIMConferenceCreated(int var1, String var2, String var3) throws FusionException;

    public void privateChatNowAGroupChat(String var1, String var2) throws FusionException;

    public void putEvent(UserEventIce var1) throws FusionException;

    public void putMessage(MessageDataIce var1) throws FusionException;

    public void putMessageAsync_async(AMD_Connection_putMessageAsync var1, MessageDataIce var2) throws FusionException;

    public void putMessageOneWay(MessageDataIce var1);

    public void putMessages(MessageDataIce[] var1) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3) throws FusionException;

    public void putAlertMessageOneWay(String var1, String var2, short var3);

    public void putServerQuestion(String var1, String var2) throws FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5) throws FusionException;

    public void putAnonymousCallNotification(String var1, String var2) throws FusionException;

    public void putFileReceived(MessageDataIce var1) throws FusionException;

    public void putGenericPacket(byte[] var1) throws FusionException;

    public void emailNotification(int var1) throws FusionException;

    public void emoticonsChanged(String[] var1, String[] var2) throws FusionException;

    public void themeChanged(String var1) throws FusionException;

    public void avatarChanged(String var1, String var2) throws FusionException;

    public void silentlyDropIncomingPackets();

    public void pushNotification(Message var1) throws FusionException;

    public void logout();

    public void putSerializedPacket(byte[] var1) throws FusionException;

    public void putSerializedPacketOneWay(byte[] var1);

    public void putMessageStatusEvent(MessageStatusEventIce var1) throws FusionException;

    public void putMessageStatusEvents(MessageStatusEventIce[] var1, short var2) throws FusionException;
}

