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
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _ConnectionDel
extends _ObjectDel {
    public String getUsername(Map<String, String> var1) throws LocalExceptionWrapper;

    public ChatRoomDataIce[] getPopularChatRooms(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public String getRemoteIPAddress(Map<String, String> var1) throws LocalExceptionWrapper;

    public String getMobileDevice(Map<String, String> var1) throws LocalExceptionWrapper;

    public String getUserAgent(Map<String, String> var1) throws LocalExceptionWrapper;

    public int getDeviceTypeAsInt(Map<String, String> var1) throws LocalExceptionWrapper;

    public short getClientVersion(Map<String, String> var1) throws LocalExceptionWrapper;

    public UserPrx getUserObject(Map<String, String> var1) throws LocalExceptionWrapper;

    public SessionPrx getSessionObject(Map<String, String> var1) throws LocalExceptionWrapper;

    public boolean processPacket(ConnectionPrx var1, byte[] var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void packetProcessed(byte[] var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void disconnect(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void contactChangedPresenceOneWay(int var1, int var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public void contactChangedDisplayPictureOneWay(int var1, String var2, long var3, Map<String, String> var5) throws LocalExceptionWrapper;

    public void contactChangedStatusMessageOneWay(int var1, String var2, long var3, Map<String, String> var5) throws LocalExceptionWrapper;

    public void contactRequest(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void contactRequestAccepted(ContactDataIce var1, int var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void contactRequestRejected(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void contactGroupAdded(ContactGroupDataIce var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void contactGroupRemoved(int var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void contactAdded(ContactDataIce var1, int var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void contactRemoved(int var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void otherIMLoggedIn(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void otherIMLoggedOut(int var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void otherIMConferenceCreated(int var1, String var2, String var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void privateChatNowAGroupChat(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putEvent(UserEventIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putMessageAsync(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putMessageOneWay(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void putMessages(MessageDataIce[] var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void putAlertMessageOneWay(String var1, String var2, short var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public void putServerQuestion(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void putAnonymousCallNotification(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putFileReceived(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putGenericPacket(byte[] var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void emailNotification(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void emoticonsChanged(String[] var1, String[] var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void themeChanged(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void avatarChanged(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void silentlyDropIncomingPackets(Map<String, String> var1) throws LocalExceptionWrapper;

    public void pushNotification(Message var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void logout(Map<String, String> var1) throws LocalExceptionWrapper;

    public void putSerializedPacket(byte[] var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putSerializedPacketOneWay(byte[] var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void putMessageStatusEvent(MessageStatusEventIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putMessageStatusEvents(MessageStatusEventIce[] var1, short var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;
}

