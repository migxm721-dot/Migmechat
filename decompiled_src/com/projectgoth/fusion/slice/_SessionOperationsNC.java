/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.AMD_Session_endSession;
import com.projectgoth.fusion.slice.AMD_Session_putMessage;
import com.projectgoth.fusion.slice.AMD_Session_sendMessage;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.SessionMetricsIce;
import com.projectgoth.fusion.slice.UserPrx;

public interface _SessionOperationsNC {
    public void sendMessage_async(AMD_Session_sendMessage var1, MessageDataIce var2) throws FusionException;

    public void setPresence(int var1) throws FusionException;

    public void endSession_async(AMD_Session_endSession var1) throws FusionException;

    public void endSessionOneWay();

    public void touch() throws FusionException;

    public void putMessage_async(AMD_Session_putMessage var1, MessageDataIce var2) throws FusionException;

    public void putMessageOneWay(MessageDataIce var1);

    public void sendMessageBackToUserAsEmote(MessageDataIce var1) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3) throws FusionException;

    public void putAlertMessageOneWay(String var1, String var2, short var3);

    public String getParentUsername() throws FusionException;

    public UserPrx getUserProxy(String var1) throws FusionException;

    public void profileEdited();

    public void groupChatJoined(String var1);

    public void groupChatJoinedMultiple(String var1, int var2);

    public void chatroomJoined(ChatRoomPrx var1, String var2);

    public void statusMessageSet();

    public void photoUploaded();

    public void friendInvitedByPhoneNumber();

    public void friendInvitedByUsername();

    public void themeUpdated();

    public void silentlyDropIncomingPackets();

    public String getSessionID();

    public String getRemoteIPAddress();

    public String getMobileDeviceIce();

    public String getUserAgentIce();

    public short getClientVersionIce();

    public int getDeviceTypeAsInt();

    public void setLanguage(String var1);

    public void notifyUserLeftChatRoomOneWay(String var1, String var2);

    public void notifyUserJoinedChatRoomOneWay(String var1, String var2, boolean var3, boolean var4);

    public void notifyUserLeftGroupChat(String var1, String var2) throws FusionException;

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3) throws FusionException;

    public void sendGroupChatParticipants(String var1, byte var2, String var3, String var4) throws FusionException;

    public void sendGroupChatParticipantArrays(String var1, byte var2, String[] var3, String[] var4) throws FusionException;

    public int getChatListVersion() throws FusionException;

    public void setChatListVersion(int var1) throws FusionException;

    public void putSerializedPacket(byte[] var1) throws FusionException;

    public void putSerializedPacketOneWay(byte[] var1);

    public GroupChatPrx findGroupChatObject(String var1) throws FusionException;

    public MessageSwitchboardPrx getMessageSwitchboard() throws FusionException;

    public boolean privateChattedWith(String var1);

    public SessionMetricsIce getSessionMetrics();

    public void setCurrentChatListGroupChatSubset(ChatListIce var1);
}

