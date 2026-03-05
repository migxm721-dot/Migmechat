/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.AMI_Connection_putMessageAsync;
import com.projectgoth.fusion.slice.ChatRoomDataIce;
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
public interface ConnectionPrx
extends ObjectPrx {
    public String getUsername();

    public String getUsername(Map<String, String> var1);

    public ChatRoomDataIce[] getPopularChatRooms() throws FusionException;

    public ChatRoomDataIce[] getPopularChatRooms(Map<String, String> var1) throws FusionException;

    public String getRemoteIPAddress();

    public String getRemoteIPAddress(Map<String, String> var1);

    public String getMobileDevice();

    public String getMobileDevice(Map<String, String> var1);

    public String getUserAgent();

    public String getUserAgent(Map<String, String> var1);

    public int getDeviceTypeAsInt();

    public int getDeviceTypeAsInt(Map<String, String> var1);

    public short getClientVersion();

    public short getClientVersion(Map<String, String> var1);

    public UserPrx getUserObject();

    public UserPrx getUserObject(Map<String, String> var1);

    public SessionPrx getSessionObject();

    public SessionPrx getSessionObject(Map<String, String> var1);

    public boolean processPacket(ConnectionPrx var1, byte[] var2) throws FusionException;

    public boolean processPacket(ConnectionPrx var1, byte[] var2, Map<String, String> var3) throws FusionException;

    public void packetProcessed(byte[] var1);

    public void packetProcessed(byte[] var1, Map<String, String> var2);

    public void disconnect(String var1) throws FusionException;

    public void disconnect(String var1, Map<String, String> var2) throws FusionException;

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5) throws FusionException;

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5, Map<String, String> var6) throws FusionException;

    public void contactChangedPresenceOneWay(int var1, int var2, int var3);

    public void contactChangedPresenceOneWay(int var1, int var2, int var3, Map<String, String> var4);

    public void contactChangedDisplayPictureOneWay(int var1, String var2, long var3);

    public void contactChangedDisplayPictureOneWay(int var1, String var2, long var3, Map<String, String> var5);

    public void contactChangedStatusMessageOneWay(int var1, String var2, long var3);

    public void contactChangedStatusMessageOneWay(int var1, String var2, long var3, Map<String, String> var5);

    public void contactRequest(String var1, int var2) throws FusionException;

    public void contactRequest(String var1, int var2, Map<String, String> var3) throws FusionException;

    public void contactRequestAccepted(ContactDataIce var1, int var2, int var3) throws FusionException;

    public void contactRequestAccepted(ContactDataIce var1, int var2, int var3, Map<String, String> var4) throws FusionException;

    public void contactRequestRejected(String var1, int var2) throws FusionException;

    public void contactRequestRejected(String var1, int var2, Map<String, String> var3) throws FusionException;

    public void contactGroupAdded(ContactGroupDataIce var1, int var2) throws FusionException;

    public void contactGroupAdded(ContactGroupDataIce var1, int var2, Map<String, String> var3) throws FusionException;

    public void contactGroupRemoved(int var1, int var2) throws FusionException;

    public void contactGroupRemoved(int var1, int var2, Map<String, String> var3) throws FusionException;

    public void contactAdded(ContactDataIce var1, int var2, boolean var3) throws FusionException;

    public void contactAdded(ContactDataIce var1, int var2, boolean var3, Map<String, String> var4) throws FusionException;

    public void contactRemoved(int var1, int var2) throws FusionException;

    public void contactRemoved(int var1, int var2, Map<String, String> var3) throws FusionException;

    public void otherIMLoggedIn(int var1) throws FusionException;

    public void otherIMLoggedIn(int var1, Map<String, String> var2) throws FusionException;

    public void otherIMLoggedOut(int var1, String var2) throws FusionException;

    public void otherIMLoggedOut(int var1, String var2, Map<String, String> var3) throws FusionException;

    public void otherIMConferenceCreated(int var1, String var2, String var3) throws FusionException;

    public void otherIMConferenceCreated(int var1, String var2, String var3, Map<String, String> var4) throws FusionException;

    public void privateChatNowAGroupChat(String var1, String var2) throws FusionException;

    public void privateChatNowAGroupChat(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void putEvent(UserEventIce var1) throws FusionException;

    public void putEvent(UserEventIce var1, Map<String, String> var2) throws FusionException;

    public void putMessage(MessageDataIce var1) throws FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void putMessageAsync(MessageDataIce var1) throws FusionException;

    public void putMessageAsync(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public boolean putMessageAsync_async(AMI_Connection_putMessageAsync var1, MessageDataIce var2);

    public boolean putMessageAsync_async(AMI_Connection_putMessageAsync var1, MessageDataIce var2, Map<String, String> var3);

    public void putMessageOneWay(MessageDataIce var1);

    public void putMessageOneWay(MessageDataIce var1, Map<String, String> var2);

    public void putMessages(MessageDataIce[] var1) throws FusionException;

    public void putMessages(MessageDataIce[] var1, Map<String, String> var2) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Map<String, String> var4) throws FusionException;

    public void putAlertMessageOneWay(String var1, String var2, short var3);

    public void putAlertMessageOneWay(String var1, String var2, short var3, Map<String, String> var4);

    public void putServerQuestion(String var1, String var2) throws FusionException;

    public void putServerQuestion(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5) throws FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5, Map<String, String> var6) throws FusionException;

    public void putAnonymousCallNotification(String var1, String var2) throws FusionException;

    public void putAnonymousCallNotification(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void putFileReceived(MessageDataIce var1) throws FusionException;

    public void putFileReceived(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void putGenericPacket(byte[] var1) throws FusionException;

    public void putGenericPacket(byte[] var1, Map<String, String> var2) throws FusionException;

    public void emailNotification(int var1) throws FusionException;

    public void emailNotification(int var1, Map<String, String> var2) throws FusionException;

    public void emoticonsChanged(String[] var1, String[] var2) throws FusionException;

    public void emoticonsChanged(String[] var1, String[] var2, Map<String, String> var3) throws FusionException;

    public void themeChanged(String var1) throws FusionException;

    public void themeChanged(String var1, Map<String, String> var2) throws FusionException;

    public void avatarChanged(String var1, String var2) throws FusionException;

    public void avatarChanged(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void silentlyDropIncomingPackets();

    public void silentlyDropIncomingPackets(Map<String, String> var1);

    public void pushNotification(Message var1) throws FusionException;

    public void pushNotification(Message var1, Map<String, String> var2) throws FusionException;

    public void logout();

    public void logout(Map<String, String> var1);

    public void putSerializedPacket(byte[] var1) throws FusionException;

    public void putSerializedPacket(byte[] var1, Map<String, String> var2) throws FusionException;

    public void putSerializedPacketOneWay(byte[] var1);

    public void putSerializedPacketOneWay(byte[] var1, Map<String, String> var2);

    public void putMessageStatusEvent(MessageStatusEventIce var1) throws FusionException;

    public void putMessageStatusEvent(MessageStatusEventIce var1, Map<String, String> var2) throws FusionException;

    public void putMessageStatusEvents(MessageStatusEventIce[] var1, short var2) throws FusionException;

    public void putMessageStatusEvents(MessageStatusEventIce[] var1, short var2, Map<String, String> var3) throws FusionException;
}

