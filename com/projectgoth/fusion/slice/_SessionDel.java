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
public interface _SessionDel
extends _ObjectDel {
    public void sendMessage(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void setPresence(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void endSession(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public void endSessionOneWay(Map<String, String> var1) throws LocalExceptionWrapper;

    public void touch(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putMessageOneWay(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void sendMessageBackToUserAsEmote(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void putAlertMessageOneWay(String var1, String var2, short var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public String getParentUsername(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public UserPrx getUserProxy(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void profileEdited(Map<String, String> var1) throws LocalExceptionWrapper;

    public void groupChatJoined(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void groupChatJoinedMultiple(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void chatroomJoined(ChatRoomPrx var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void statusMessageSet(Map<String, String> var1) throws LocalExceptionWrapper;

    public void photoUploaded(Map<String, String> var1) throws LocalExceptionWrapper;

    public void friendInvitedByPhoneNumber(Map<String, String> var1) throws LocalExceptionWrapper;

    public void friendInvitedByUsername(Map<String, String> var1) throws LocalExceptionWrapper;

    public void themeUpdated(Map<String, String> var1) throws LocalExceptionWrapper;

    public void silentlyDropIncomingPackets(Map<String, String> var1) throws LocalExceptionWrapper;

    public String getSessionID(Map<String, String> var1) throws LocalExceptionWrapper;

    public String getRemoteIPAddress(Map<String, String> var1) throws LocalExceptionWrapper;

    public String getMobileDeviceIce(Map<String, String> var1) throws LocalExceptionWrapper;

    public String getUserAgentIce(Map<String, String> var1) throws LocalExceptionWrapper;

    public short getClientVersionIce(Map<String, String> var1) throws LocalExceptionWrapper;

    public int getDeviceTypeAsInt(Map<String, String> var1) throws LocalExceptionWrapper;

    public void setLanguage(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void notifyUserLeftChatRoomOneWay(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void notifyUserJoinedChatRoomOneWay(String var1, String var2, boolean var3, boolean var4, Map<String, String> var5) throws LocalExceptionWrapper;

    public void notifyUserLeftGroupChat(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void sendGroupChatParticipants(String var1, byte var2, String var3, String var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public void sendGroupChatParticipantArrays(String var1, byte var2, String[] var3, String[] var4, Map<String, String> var5) throws LocalExceptionWrapper, FusionException;

    public int getChatListVersion(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public void setChatListVersion(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putSerializedPacket(byte[] var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putSerializedPacketOneWay(byte[] var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public GroupChatPrx findGroupChatObject(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public MessageSwitchboardPrx getMessageSwitchboard(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public boolean privateChattedWith(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public SessionMetricsIce getSessionMetrics(Map<String, String> var1) throws LocalExceptionWrapper;

    public void setCurrentChatListGroupChatSubset(ChatListIce var1, Map<String, String> var2) throws LocalExceptionWrapper;
}

