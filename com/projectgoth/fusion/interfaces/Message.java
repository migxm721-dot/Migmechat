/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import com.projectgoth.fusion.data.EmailTemplateData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MobilePrefixData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.fdl.enums.MessageType;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBObject;

public interface Message
extends EJBObject {
    public MessageData saveSentMessage(MessageData var1) throws RemoteException;

    public MessageData saveReceivedMessage(MessageData var1, Integer var2) throws RemoteException;

    public MessageData sendSMS(MessageData var1, AccountEntrySourceData var2) throws RemoteException;

    public void sendSystemSMS(SystemSMSData var1, AccountEntrySourceData var2) throws RemoteException;

    public void sendSystemSMSNoTransaction(SystemSMSData var1, AccountEntrySourceData var2) throws RemoteException;

    public void sendSystemSMS(SystemSMSData var1, long var2, AccountEntrySourceData var4) throws RemoteException;

    public void systemSMSFailed(int var1, Integer var2, String var3) throws RemoteException;

    public void smsFailed(int var1, Integer var2, String var3, AccountEntrySourceData var4) throws RemoteException;

    public int getSystemSMSCount(SystemSMSData.SubTypeEnum var1, String var2) throws RemoteException;

    public int getSystemSMSCount(SystemSMSData.SubTypeEnum var1, String var2, String var3) throws RemoteException;

    public String cleanPhoneNumber(String var1) throws RemoteException;

    public String cleanAndValidatePhoneNumber(String var1, boolean var2) throws RemoteException;

    public Integer getIDDCode(String var1) throws RemoteException;

    public MobilePrefixData getMobilePrefixData(int var1, int var2) throws RemoteException;

    public int getMinimumMobileNumberLength(int var1) throws RemoteException;

    public boolean isMobileNumber(String var1, boolean var2) throws RemoteException;

    public void changePendingSystemSMSToSent(int var1, int var2, int var3, String var4, int var5, String var6, String var7, boolean var8) throws RemoteException;

    public void changePendingMessageToSent(MessageType var1, int var2, int var3, Integer var4, Integer var5, String var6, String var7) throws RemoteException;

    public MessageData getMessage(int var1) throws RemoteException;

    public List getMessages(String var1, Integer var2, Integer var3, Date var4, Integer var5, Integer var6) throws RemoteException;

    public SystemSMSData getSystemSMS(String var1, String var2) throws RemoteException;

    public List getSystemSMS(String var1, Integer var2, Date var3, Integer var4) throws RemoteException;

    public List getSMSGateways() throws RemoteException;

    public ChatRoomData getSimpleChatRoomData(Integer var1, Connection var2) throws RemoteException;

    public ChatRoomData getSimpleChatRoomData(String var1, Connection var2) throws RemoteException;

    public List getLoginChatroomCategories() throws Exception, RemoteException;

    public ChatroomCategoryData getChatroomCategory(Integer var1) throws Exception, RemoteException;

    public Map getChatroomNamesPerCategory(boolean var1) throws Exception, RemoteException;

    public String[] getChatroomNamesInCategory(int var1) throws Exception, RemoteException;

    public String[] getChatroomNamesInCategory(int var1, boolean var2) throws Exception, RemoteException;

    public ChatRoomData getChatRoom(String var1) throws RemoteException;

    public void updateRoomExtraData(ChatRoomData var1) throws RemoteException;

    public void updateRoomDetails(String var1, String var2, String var3, String var4) throws RemoteException;

    public void updateRoomKickingRule(String var1, String var2, boolean var3) throws RemoteException;

    public void updateRoomAdultOnlyFlag(String var1, String var2, boolean var3) throws RemoteException;

    public void updateRoomKeywords(String var1, String var2, String var3, int var4) throws RemoteException;

    public List getChatRooms(int var1, String var2) throws RemoteException;

    public List getChatRooms(int var1, String var2, String var3, boolean var4, boolean var5) throws RemoteException;

    public List getChatRoomsV2(int var1, String var2, String var3, boolean var4, boolean var5) throws RemoteException;

    public List getFavouriteChatRooms(String var1) throws RemoteException;

    public List getRecentChatRooms(String var1) throws RemoteException;

    public int getRecentlyAccessedChatRoomCount() throws RemoteException;

    public int getActiveGroupsCount() throws RemoteException;

    public void createChatRoom(ChatRoomData var1, String var2) throws RemoteException;

    public void chatRoomAccessed(int var1, String var2, Integer var3, Integer var4) throws RemoteException;

    public void addFavouriteChatRoom(String var1, String var2) throws RemoteException;

    public void removeFavouriteChatRoom(String var1, String var2) throws RemoteException;

    public void logMessageStats(Date var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17, int var18) throws RemoteException;

    public void sendSystemEmail(String var1, String var2, String var3) throws RemoteException;

    public void sendSystemEmail(String var1, String var2, String var3, UserEmailAddressData.UserEmailAddressTypeEnum var4) throws RemoteException;

    public void sendEmailFromNoReply(String var1, String var2, String var3) throws RemoteException;

    public String getUserEmailAddress(String var1) throws RemoteException;

    public void sendEmail(String var1, String var2, String var3, String var4, String var5) throws RemoteException;

    public void updateRoomDescriptions(String[] var1, String var2) throws RemoteException;

    public String[] getGroupChatRooms(int var1) throws RemoteException;

    public Integer[] announceMessageToChatrooms(String[] var1, String var2, int var3) throws RemoteException;

    public Integer[] announceMessageToUserOwnedChatrooms(String var1, int var2) throws RemoteException;

    public void updateRoomDescription(String var1, String var2) throws RemoteException;

    public void sendChangeRoomOwnerEmail(String var1, String var2, String var3) throws RemoteException;

    public void changeRoomOwner(String var1, String var2, String var3) throws RemoteException;

    public void addRoomModerator(String var1, String var2, String var3) throws RemoteException;

    public void resetRoomModerators(String var1) throws RemoteException;

    public void removeRoomModerator(String var1, String var2, String var3) throws RemoteException;

    public void banGroupMember(String var1, GroupData var2, String var3) throws RemoteException;

    public void unbanGroupMember(String var1, GroupData var2, String var3) throws RemoteException;

    public boolean isUserBlackListedInGroup(String var1, int var2) throws RemoteException;

    public boolean isModeratorOfChatRoom(String var1, String var2) throws RemoteException;

    public void addChatRoomEmoteLog(ChatRoomEmoteLogData var1) throws RemoteException;

    public void banUserFromRoom(String var1, String var2, String var3) throws RemoteException;

    public boolean updateChatroomBannedList(String var1, String var2) throws RemoteException;

    public void unbanUserFromRoom(String var1, String var2, String var3) throws RemoteException;

    public List getBots() throws RemoteException;

    public BotData getBot(int var1) throws RemoteException;

    public BotData getBotFromCommandName(String var1) throws RemoteException;

    public EmailTemplateData getEmailTemplateData(int var1) throws RemoteException;

    public void setChatRoomMaxSize(String var1, int var2) throws FusionEJBException, RemoteException;
}

