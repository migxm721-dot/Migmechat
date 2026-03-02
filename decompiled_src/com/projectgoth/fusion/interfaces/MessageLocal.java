/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MobilePrefixData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import java.sql.Connection;
import java.util.List;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface MessageLocal
extends EJBLocalObject {
    public MessageData sendSMS(MessageData var1, AccountEntrySourceData var2) throws EJBException;

    public void sendSystemSMS(SystemSMSData var1, AccountEntrySourceData var2) throws EJBException;

    public void sendSystemSMSNoTransaction(SystemSMSData var1, AccountEntrySourceData var2) throws EJBException;

    public void sendSystemSMS(SystemSMSData var1, long var2, AccountEntrySourceData var4) throws EJBException;

    public int getSystemSMSCount(SystemSMSData.SubTypeEnum var1, String var2) throws EJBException;

    public int getSystemSMSCount(SystemSMSData.SubTypeEnum var1, String var2, String var3) throws EJBException;

    public String cleanPhoneNumber(String var1) throws EJBException;

    public String cleanAndValidatePhoneNumber(String var1, boolean var2) throws EJBException;

    public Integer getIDDCode(String var1) throws EJBException;

    public MobilePrefixData getMobilePrefixData(int var1, int var2) throws EJBException;

    public int getMinimumMobileNumberLength(int var1) throws EJBException;

    public boolean isMobileNumber(String var1, boolean var2) throws EJBException;

    public MessageData getMessage(int var1) throws EJBException;

    public SystemSMSData getSystemSMS(String var1, String var2) throws EJBException;

    public ChatRoomData getSimpleChatRoomData(Integer var1, Connection var2) throws EJBException;

    public ChatRoomData getSimpleChatRoomData(String var1, Connection var2) throws EJBException;

    public void updateRoomExtraData(ChatRoomData var1) throws EJBException;

    public void updateRoomDetails(String var1, String var2, String var3, String var4) throws EJBException;

    public void updateRoomKickingRule(String var1, String var2, boolean var3) throws EJBException;

    public void updateRoomAdultOnlyFlag(String var1, String var2, boolean var3) throws EJBException;

    public void updateRoomKeywords(String var1, String var2, String var3, int var4) throws EJBException;

    public List getChatRooms(int var1, String var2) throws EJBException;

    public List getChatRooms(int var1, String var2, String var3, boolean var4, boolean var5) throws EJBException;

    public List getChatRoomsV2(int var1, String var2, String var3, boolean var4, boolean var5) throws EJBException;

    public void createChatRoom(ChatRoomData var1, String var2) throws EJBException;

    public void sendSystemEmail(String var1, String var2, String var3) throws EJBException;

    public void sendSystemEmail(String var1, String var2, String var3, UserEmailAddressData.UserEmailAddressTypeEnum var4) throws EJBException;

    public void sendEmailFromNoReply(String var1, String var2, String var3) throws EJBException;

    public String getUserEmailAddress(String var1) throws EJBException;

    public void sendEmail(String var1, String var2, String var3, String var4, String var5);

    public String[] getGroupChatRooms(int var1) throws EJBException;

    public Integer[] announceMessageToChatrooms(String[] var1, String var2, int var3) throws EJBException;

    public Integer[] announceMessageToUserOwnedChatrooms(String var1, int var2) throws EJBException;

    public void sendChangeRoomOwnerEmail(String var1, String var2, String var3) throws EJBException;

    public void changeRoomOwner(String var1, String var2, String var3) throws EJBException;

    public void addRoomModerator(String var1, String var2, String var3) throws EJBException;

    public void resetRoomModerators(String var1) throws EJBException;

    public void removeRoomModerator(String var1, String var2, String var3) throws EJBException;

    public void banGroupMember(String var1, GroupData var2, String var3) throws EJBException;

    public void unbanGroupMember(String var1, GroupData var2, String var3) throws EJBException;

    public boolean isUserBlackListedInGroup(String var1, int var2) throws EJBException;

    public boolean isModeratorOfChatRoom(String var1, String var2);

    public void addChatRoomEmoteLog(ChatRoomEmoteLogData var1) throws EJBException;

    public void banUserFromRoom(String var1, String var2, String var3) throws EJBException;

    public boolean updateChatroomBannedList(String var1, String var2) throws EJBException;

    public void unbanUserFromRoom(String var1, String var2, String var3) throws EJBException;

    public void setChatRoomMaxSize(String var1, int var2) throws FusionEJBException;
}

