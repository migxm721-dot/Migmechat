/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
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

public interface _SessionOperations {
    public void sendMessage_async(AMD_Session_sendMessage var1, MessageDataIce var2, Current var3) throws FusionException;

    public void setPresence(int var1, Current var2) throws FusionException;

    public void endSession_async(AMD_Session_endSession var1, Current var2) throws FusionException;

    public void endSessionOneWay(Current var1);

    public void touch(Current var1) throws FusionException;

    public void putMessage_async(AMD_Session_putMessage var1, MessageDataIce var2, Current var3) throws FusionException;

    public void putMessageOneWay(MessageDataIce var1, Current var2);

    public void sendMessageBackToUserAsEmote(MessageDataIce var1, Current var2) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Current var4) throws FusionException;

    public void putAlertMessageOneWay(String var1, String var2, short var3, Current var4);

    public String getParentUsername(Current var1) throws FusionException;

    public UserPrx getUserProxy(String var1, Current var2) throws FusionException;

    public void profileEdited(Current var1);

    public void groupChatJoined(String var1, Current var2);

    public void groupChatJoinedMultiple(String var1, int var2, Current var3);

    public void chatroomJoined(ChatRoomPrx var1, String var2, Current var3);

    public void statusMessageSet(Current var1);

    public void photoUploaded(Current var1);

    public void friendInvitedByPhoneNumber(Current var1);

    public void friendInvitedByUsername(Current var1);

    public void themeUpdated(Current var1);

    public void silentlyDropIncomingPackets(Current var1);

    public String getSessionID(Current var1);

    public String getRemoteIPAddress(Current var1);

    public String getMobileDeviceIce(Current var1);

    public String getUserAgentIce(Current var1);

    public short getClientVersionIce(Current var1);

    public int getDeviceTypeAsInt(Current var1);

    public void setLanguage(String var1, Current var2);

    public void notifyUserLeftChatRoomOneWay(String var1, String var2, Current var3);

    public void notifyUserJoinedChatRoomOneWay(String var1, String var2, boolean var3, boolean var4, Current var5);

    public void notifyUserLeftGroupChat(String var1, String var2, Current var3) throws FusionException;

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3, Current var4) throws FusionException;

    public void sendGroupChatParticipants(String var1, byte var2, String var3, String var4, Current var5) throws FusionException;

    public void sendGroupChatParticipantArrays(String var1, byte var2, String[] var3, String[] var4, Current var5) throws FusionException;

    public int getChatListVersion(Current var1) throws FusionException;

    public void setChatListVersion(int var1, Current var2) throws FusionException;

    public void putSerializedPacket(byte[] var1, Current var2) throws FusionException;

    public void putSerializedPacketOneWay(byte[] var1, Current var2);

    public GroupChatPrx findGroupChatObject(String var1, Current var2) throws FusionException;

    public MessageSwitchboardPrx getMessageSwitchboard(Current var1) throws FusionException;

    public boolean privateChattedWith(String var1, Current var2);

    public SessionMetricsIce getSessionMetrics(Current var1);

    public void setCurrentChatListGroupChatSubset(ChatListIce var1, Current var2);
}

