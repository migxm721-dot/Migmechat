/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.AMD_User_createSession;
import com.projectgoth.fusion.slice.AMD_User_otherIMLogin;
import com.projectgoth.fusion.slice.AMD_User_putMessage;
import com.projectgoth.fusion.slice.ChatListIce;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactGroupDataIce;
import com.projectgoth.fusion.slice.ContactList;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.CurrencyDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.MessageStatusEventIce;
import com.projectgoth.fusion.slice.PresenceAndCapabilityIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserErrorResponse;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserPrx;

public interface _UserOperations {
    public void createSession_async(AMD_User_createSession var1, String var2, int var3, int var4, int var5, int var6, int var7, int var8, String var9, String var10, String var11, short var12, String var13, ConnectionPrx var14, Current var15) throws FusionException;

    public void putMessage_async(AMD_User_putMessage var1, MessageDataIce var2, Current var3) throws FusionException;

    public void contactChangedPresenceOneWay(int var1, String var2, int var3, Current var4);

    public void contactChangedDisplayPictureOneWay(String var1, String var2, long var3, Current var5);

    public void contactChangedStatusMessageOneWay(String var1, String var2, long var3, Current var5);

    public int getOverallFusionPresence(String var1, Current var2);

    public int getContactListVersion(Current var1);

    public ContactList getContactList(Current var1);

    public ContactDataIce[] getContacts(Current var1);

    public ContactDataIce[] getOtherIMContacts(Current var1);

    public Credential[] getOtherIMCredentials(Current var1);

    public String[] getOtherIMConferenceParticipants(int var1, String var2, Current var3);

    public UserDataIce getUserData(Current var1);

    public boolean isOnContactList(String var1, Current var2);

    public boolean isOnBlockList(String var1, Current var2);

    public void otherIMLogin_async(AMD_User_otherIMLogin var1, int var2, int var3, boolean var4, Current var5) throws FusionException;

    public void otherIMLogout(int var1, Current var2);

    public void otherIMSendMessage(int var1, String var2, String var3, Current var4) throws FusionException;

    public String otherIMInviteToConference(int var1, String var2, String var3, Current var4) throws FusionException;

    public void otherIMLeaveConference(int var1, String var2, Current var3);

    public void otherIMAddContact(int var1, String var2, Current var3) throws FusionException;

    public void otherIMRemoveContact(int var1, Current var2) throws FusionException;

    public void otherIMRemoved(int var1, Current var2);

    public String[] getBlockList(Current var1);

    public String[] getBlockListFromUsernames(String[] var1, Current var2);

    public String[] getBroadcastList(Current var1);

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5, Current var6);

    public void privateChatNowAGroupChat(String var1, String var2, Current var3) throws FusionException;

    public void putEvent(UserEventIce var1, Current var2) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Current var4) throws FusionException;

    public void putServerQuestion(String var1, String var2, Current var3) throws FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5, Current var6) throws FusionException;

    public void putAnonymousCallNotification(String var1, String var2, Current var3) throws FusionException;

    public void putFileReceived(MessageDataIce var1, Current var2) throws FusionException;

    public void contactDetailChanged(ContactDataIce var1, int var2, Current var3);

    public void contactGroupDetailChanged(ContactGroupDataIce var1, int var2, Current var3);

    public void contactGroupDeleted(int var1, int var2, Current var3);

    public void userDetailChanged(UserDataIce var1, Current var2);

    public void userReputationChanged(Current var1);

    public void userDisplayPictureChanged(String var1, long var2, Current var4);

    public void userStatusMessageChanged(String var1, long var2, Current var4);

    public void messageSettingChanged(int var1, Current var2);

    public void anonymousCallSettingChanged(int var1, Current var2);

    public int[] getConnectedOtherIMs(Current var1);

    public boolean supportsBinaryMessage(Current var1);

    public int getUnreadEmailCount(Current var1);

    public void emailNotification(int var1, Current var2);

    public String[] getEmoticonHotKeys(Current var1);

    public String[] getEmoticonAlternateKeys(Current var1);

    public void emoticonPackActivated(int var1, Current var2);

    public void themeChanged(String var1, Current var2) throws FusionException;

    public void addContact(ContactDataIce var1, int var2, Current var3);

    public void addToContactAndBroadcastLists(ContactDataIce var1, int var2, Current var3);

    public void addPendingContact(String var1, Current var2);

    public ContactDataIce acceptContactRequest(ContactDataIce var1, UserPrx var2, int var3, int var4, Current var5);

    public ContactDataIce contactRequestWasAccepted(ContactDataIce var1, String var2, String var3, int var4, int var5, Current var6);

    public void blockUser(String var1, int var2, Current var3);

    public void unblockUser(String var1, Current var2);

    public void contactRequestWasRejected(String var1, int var2, Current var3);

    public void rejectContactRequest(String var1, Current var2);

    public void stopBroadcastingTo(String var1, Current var2);

    public void removeContact(int var1, int var2, Current var3);

    public PresenceAndCapabilityIce contactUpdated(ContactDataIce var1, String var2, boolean var3, boolean var4, UserPrx var5, int var6, Current var7) throws FusionException;

    public void oldUserContactUpdated(String var1, Current var2) throws FusionException;

    public void newUserContactUpdated(String var1, boolean var2, Current var3);

    public void notifySessionsOfNewContact(ContactDataIce var1, int var2, boolean var3, Current var4);

    public SessionPrx[] getSessions(Current var1);

    public void disconnect(String var1, Current var2);

    public void disconnectFlooder(String var1, Current var2);

    public boolean privateChattedWith(String var1, Current var2);

    public UserErrorResponse userCanContactMe(String var1, MessageDataIce var2, Current var3);

    public void enteringGroupChat(boolean var1, Current var2) throws FusionException;

    public void leavingGroupChat(Current var1);

    public void pushNotification(Message var1, Current var2) throws FusionException;

    public int getOnlineContactsCount(Current var1);

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Current var4) throws FusionException;

    public void addToCurrentChatroomList(String var1, Current var2) throws FusionException;

    public void removeFromCurrentChatroomList(String var1, Current var2);

    public String[] getCurrentChatrooms(Current var1);

    public int getReputationDataLevel(Current var1);

    public void notifyUserLeftGroupChat(String var1, String var2, Current var3);

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3, Current var4);

    public void setCurrentChatListGroupChatSubset(ChatListIce var1, Current var2);

    public void putMessageStatusEvent(MessageStatusEventIce var1, Current var2) throws FusionException;

    public SessionPrx findSession(String var1, Current var2) throws FusionException;
}

