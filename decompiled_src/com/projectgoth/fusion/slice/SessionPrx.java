/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SessionPrx
extends ObjectPrx {
    public void sendMessage(MessageDataIce var1) throws FusionException;

    public void sendMessage(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void setPresence(int var1) throws FusionException;

    public void setPresence(int var1, Map<String, String> var2) throws FusionException;

    public void endSession() throws FusionException;

    public void endSession(Map<String, String> var1) throws FusionException;

    public void endSessionOneWay();

    public void endSessionOneWay(Map<String, String> var1);

    public void touch() throws FusionException;

    public void touch(Map<String, String> var1) throws FusionException;

    public void putMessage(MessageDataIce var1) throws FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void putMessageOneWay(MessageDataIce var1);

    public void putMessageOneWay(MessageDataIce var1, Map<String, String> var2);

    public void sendMessageBackToUserAsEmote(MessageDataIce var1) throws FusionException;

    public void sendMessageBackToUserAsEmote(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Map<String, String> var4) throws FusionException;

    public void putAlertMessageOneWay(String var1, String var2, short var3);

    public void putAlertMessageOneWay(String var1, String var2, short var3, Map<String, String> var4);

    public String getParentUsername() throws FusionException;

    public String getParentUsername(Map<String, String> var1) throws FusionException;

    public UserPrx getUserProxy(String var1) throws FusionException;

    public UserPrx getUserProxy(String var1, Map<String, String> var2) throws FusionException;

    public void profileEdited();

    public void profileEdited(Map<String, String> var1);

    public void groupChatJoined(String var1);

    public void groupChatJoined(String var1, Map<String, String> var2);

    public void groupChatJoinedMultiple(String var1, int var2);

    public void groupChatJoinedMultiple(String var1, int var2, Map<String, String> var3);

    public void chatroomJoined(ChatRoomPrx var1, String var2);

    public void chatroomJoined(ChatRoomPrx var1, String var2, Map<String, String> var3);

    public void statusMessageSet();

    public void statusMessageSet(Map<String, String> var1);

    public void photoUploaded();

    public void photoUploaded(Map<String, String> var1);

    public void friendInvitedByPhoneNumber();

    public void friendInvitedByPhoneNumber(Map<String, String> var1);

    public void friendInvitedByUsername();

    public void friendInvitedByUsername(Map<String, String> var1);

    public void themeUpdated();

    public void themeUpdated(Map<String, String> var1);

    public void silentlyDropIncomingPackets();

    public void silentlyDropIncomingPackets(Map<String, String> var1);

    public String getSessionID();

    public String getSessionID(Map<String, String> var1);

    public String getRemoteIPAddress();

    public String getRemoteIPAddress(Map<String, String> var1);

    public String getMobileDeviceIce();

    public String getMobileDeviceIce(Map<String, String> var1);

    public String getUserAgentIce();

    public String getUserAgentIce(Map<String, String> var1);

    public short getClientVersionIce();

    public short getClientVersionIce(Map<String, String> var1);

    public int getDeviceTypeAsInt();

    public int getDeviceTypeAsInt(Map<String, String> var1);

    public void setLanguage(String var1);

    public void setLanguage(String var1, Map<String, String> var2);

    public void notifyUserLeftChatRoomOneWay(String var1, String var2);

    public void notifyUserLeftChatRoomOneWay(String var1, String var2, Map<String, String> var3);

    public void notifyUserJoinedChatRoomOneWay(String var1, String var2, boolean var3, boolean var4);

    public void notifyUserJoinedChatRoomOneWay(String var1, String var2, boolean var3, boolean var4, Map<String, String> var5);

    public void notifyUserLeftGroupChat(String var1, String var2) throws FusionException;

    public void notifyUserLeftGroupChat(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3) throws FusionException;

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3, Map<String, String> var4) throws FusionException;

    public void sendGroupChatParticipants(String var1, byte var2, String var3, String var4) throws FusionException;

    public void sendGroupChatParticipants(String var1, byte var2, String var3, String var4, Map<String, String> var5) throws FusionException;

    public void sendGroupChatParticipantArrays(String var1, byte var2, String[] var3, String[] var4) throws FusionException;

    public void sendGroupChatParticipantArrays(String var1, byte var2, String[] var3, String[] var4, Map<String, String> var5) throws FusionException;

    public int getChatListVersion() throws FusionException;

    public int getChatListVersion(Map<String, String> var1) throws FusionException;

    public void setChatListVersion(int var1) throws FusionException;

    public void setChatListVersion(int var1, Map<String, String> var2) throws FusionException;

    public void putSerializedPacket(byte[] var1) throws FusionException;

    public void putSerializedPacket(byte[] var1, Map<String, String> var2) throws FusionException;

    public void putSerializedPacketOneWay(byte[] var1);

    public void putSerializedPacketOneWay(byte[] var1, Map<String, String> var2);

    public GroupChatPrx findGroupChatObject(String var1) throws FusionException;

    public GroupChatPrx findGroupChatObject(String var1, Map<String, String> var2) throws FusionException;

    public MessageSwitchboardPrx getMessageSwitchboard() throws FusionException;

    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> var1) throws FusionException;

    public boolean privateChattedWith(String var1);

    public boolean privateChattedWith(String var1, Map<String, String> var2);

    public SessionMetricsIce getSessionMetrics();

    public SessionMetricsIce getSessionMetrics(Map<String, String> var1);

    public void setCurrentChatListGroupChatSubset(ChatListIce var1);

    public void setCurrentChatListGroupChatSubset(ChatListIce var1, Map<String, String> var2);
}

