/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
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
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface UserPrx
extends ObjectPrx {
    public SessionPrx createSession(String var1, int var2, int var3, int var4, int var5, int var6, int var7, String var8, String var9, String var10, short var11, String var12, ConnectionPrx var13) throws FusionException;

    public SessionPrx createSession(String var1, int var2, int var3, int var4, int var5, int var6, int var7, String var8, String var9, String var10, short var11, String var12, ConnectionPrx var13, Map<String, String> var14) throws FusionException;

    public void putMessage(MessageDataIce var1) throws FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void contactChangedPresenceOneWay(int var1, String var2, int var3);

    public void contactChangedPresenceOneWay(int var1, String var2, int var3, Map<String, String> var4);

    public void contactChangedDisplayPictureOneWay(String var1, String var2, long var3);

    public void contactChangedDisplayPictureOneWay(String var1, String var2, long var3, Map<String, String> var5);

    public void contactChangedStatusMessageOneWay(String var1, String var2, long var3);

    public void contactChangedStatusMessageOneWay(String var1, String var2, long var3, Map<String, String> var5);

    public int getOverallFusionPresence(String var1);

    public int getOverallFusionPresence(String var1, Map<String, String> var2);

    public int getContactListVersion();

    public int getContactListVersion(Map<String, String> var1);

    public ContactList getContactList();

    public ContactList getContactList(Map<String, String> var1);

    public ContactDataIce[] getContacts();

    public ContactDataIce[] getContacts(Map<String, String> var1);

    public ContactDataIce[] getOtherIMContacts();

    public ContactDataIce[] getOtherIMContacts(Map<String, String> var1);

    public Credential[] getOtherIMCredentials();

    public Credential[] getOtherIMCredentials(Map<String, String> var1);

    public String[] getOtherIMConferenceParticipants(int var1, String var2);

    public String[] getOtherIMConferenceParticipants(int var1, String var2, Map<String, String> var3);

    public UserDataIce getUserData();

    public UserDataIce getUserData(Map<String, String> var1);

    public boolean isOnContactList(String var1);

    public boolean isOnContactList(String var1, Map<String, String> var2);

    public boolean isOnBlockList(String var1);

    public boolean isOnBlockList(String var1, Map<String, String> var2);

    public void otherIMLogin(int var1, int var2, boolean var3) throws FusionException;

    public void otherIMLogin(int var1, int var2, boolean var3, Map<String, String> var4) throws FusionException;

    public void otherIMLogout(int var1);

    public void otherIMLogout(int var1, Map<String, String> var2);

    public void otherIMSendMessage(int var1, String var2, String var3) throws FusionException;

    public void otherIMSendMessage(int var1, String var2, String var3, Map<String, String> var4) throws FusionException;

    public String otherIMInviteToConference(int var1, String var2, String var3) throws FusionException;

    public String otherIMInviteToConference(int var1, String var2, String var3, Map<String, String> var4) throws FusionException;

    public void otherIMLeaveConference(int var1, String var2);

    public void otherIMLeaveConference(int var1, String var2, Map<String, String> var3);

    public void otherIMAddContact(int var1, String var2) throws FusionException;

    public void otherIMAddContact(int var1, String var2, Map<String, String> var3) throws FusionException;

    public void otherIMRemoveContact(int var1) throws FusionException;

    public void otherIMRemoveContact(int var1, Map<String, String> var2) throws FusionException;

    public void otherIMRemoved(int var1);

    public void otherIMRemoved(int var1, Map<String, String> var2);

    public String[] getBlockList();

    public String[] getBlockList(Map<String, String> var1);

    public String[] getBlockListFromUsernames(String[] var1);

    public String[] getBlockListFromUsernames(String[] var1, Map<String, String> var2);

    public String[] getBroadcastList();

    public String[] getBroadcastList(Map<String, String> var1);

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5);

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5, Map<String, String> var6);

    public void privateChatNowAGroupChat(String var1, String var2) throws FusionException;

    public void privateChatNowAGroupChat(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void putEvent(UserEventIce var1) throws FusionException;

    public void putEvent(UserEventIce var1, Map<String, String> var2) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3) throws FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Map<String, String> var4) throws FusionException;

    public void putServerQuestion(String var1, String var2) throws FusionException;

    public void putServerQuestion(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5) throws FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5, Map<String, String> var6) throws FusionException;

    public void putAnonymousCallNotification(String var1, String var2) throws FusionException;

    public void putAnonymousCallNotification(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void putFileReceived(MessageDataIce var1) throws FusionException;

    public void putFileReceived(MessageDataIce var1, Map<String, String> var2) throws FusionException;

    public void contactDetailChanged(ContactDataIce var1, int var2);

    public void contactDetailChanged(ContactDataIce var1, int var2, Map<String, String> var3);

    public void contactGroupDetailChanged(ContactGroupDataIce var1, int var2);

    public void contactGroupDetailChanged(ContactGroupDataIce var1, int var2, Map<String, String> var3);

    public void contactGroupDeleted(int var1, int var2);

    public void contactGroupDeleted(int var1, int var2, Map<String, String> var3);

    public void userDetailChanged(UserDataIce var1);

    public void userDetailChanged(UserDataIce var1, Map<String, String> var2);

    public void userReputationChanged();

    public void userReputationChanged(Map<String, String> var1);

    public void userDisplayPictureChanged(String var1, long var2);

    public void userDisplayPictureChanged(String var1, long var2, Map<String, String> var4);

    public void userStatusMessageChanged(String var1, long var2);

    public void userStatusMessageChanged(String var1, long var2, Map<String, String> var4);

    public void messageSettingChanged(int var1);

    public void messageSettingChanged(int var1, Map<String, String> var2);

    public void anonymousCallSettingChanged(int var1);

    public void anonymousCallSettingChanged(int var1, Map<String, String> var2);

    public int[] getConnectedOtherIMs();

    public int[] getConnectedOtherIMs(Map<String, String> var1);

    public boolean supportsBinaryMessage();

    public boolean supportsBinaryMessage(Map<String, String> var1);

    public int getUnreadEmailCount();

    public int getUnreadEmailCount(Map<String, String> var1);

    public void emailNotification(int var1);

    public void emailNotification(int var1, Map<String, String> var2);

    public String[] getEmoticonHotKeys();

    public String[] getEmoticonHotKeys(Map<String, String> var1);

    public String[] getEmoticonAlternateKeys();

    public String[] getEmoticonAlternateKeys(Map<String, String> var1);

    public void emoticonPackActivated(int var1);

    public void emoticonPackActivated(int var1, Map<String, String> var2);

    public void themeChanged(String var1) throws FusionException;

    public void themeChanged(String var1, Map<String, String> var2) throws FusionException;

    public void addContact(ContactDataIce var1, int var2);

    public void addContact(ContactDataIce var1, int var2, Map<String, String> var3);

    public void addToContactAndBroadcastLists(ContactDataIce var1, int var2);

    public void addToContactAndBroadcastLists(ContactDataIce var1, int var2, Map<String, String> var3);

    public void addPendingContact(String var1);

    public void addPendingContact(String var1, Map<String, String> var2);

    public ContactDataIce acceptContactRequest(ContactDataIce var1, UserPrx var2, int var3, int var4);

    public ContactDataIce acceptContactRequest(ContactDataIce var1, UserPrx var2, int var3, int var4, Map<String, String> var5);

    public ContactDataIce contactRequestWasAccepted(ContactDataIce var1, String var2, String var3, int var4, int var5);

    public ContactDataIce contactRequestWasAccepted(ContactDataIce var1, String var2, String var3, int var4, int var5, Map<String, String> var6);

    public void blockUser(String var1, int var2);

    public void blockUser(String var1, int var2, Map<String, String> var3);

    public void unblockUser(String var1);

    public void unblockUser(String var1, Map<String, String> var2);

    public void contactRequestWasRejected(String var1, int var2);

    public void contactRequestWasRejected(String var1, int var2, Map<String, String> var3);

    public void rejectContactRequest(String var1);

    public void rejectContactRequest(String var1, Map<String, String> var2);

    public void stopBroadcastingTo(String var1);

    public void stopBroadcastingTo(String var1, Map<String, String> var2);

    public void removeContact(int var1, int var2);

    public void removeContact(int var1, int var2, Map<String, String> var3);

    public PresenceAndCapabilityIce contactUpdated(ContactDataIce var1, String var2, boolean var3, boolean var4, UserPrx var5, int var6) throws FusionException;

    public PresenceAndCapabilityIce contactUpdated(ContactDataIce var1, String var2, boolean var3, boolean var4, UserPrx var5, int var6, Map<String, String> var7) throws FusionException;

    public void oldUserContactUpdated(String var1) throws FusionException;

    public void oldUserContactUpdated(String var1, Map<String, String> var2) throws FusionException;

    public void newUserContactUpdated(String var1, boolean var2);

    public void newUserContactUpdated(String var1, boolean var2, Map<String, String> var3);

    public void notifySessionsOfNewContact(ContactDataIce var1, int var2, boolean var3);

    public void notifySessionsOfNewContact(ContactDataIce var1, int var2, boolean var3, Map<String, String> var4);

    public SessionPrx[] getSessions();

    public SessionPrx[] getSessions(Map<String, String> var1);

    public void disconnect(String var1);

    public void disconnect(String var1, Map<String, String> var2);

    public void disconnectFlooder(String var1);

    public void disconnectFlooder(String var1, Map<String, String> var2);

    public boolean privateChattedWith(String var1);

    public boolean privateChattedWith(String var1, Map<String, String> var2);

    public UserErrorResponse userCanContactMe(String var1, MessageDataIce var2);

    public UserErrorResponse userCanContactMe(String var1, MessageDataIce var2, Map<String, String> var3);

    public void enteringGroupChat(boolean var1) throws FusionException;

    public void enteringGroupChat(boolean var1, Map<String, String> var2) throws FusionException;

    public void leavingGroupChat();

    public void leavingGroupChat(Map<String, String> var1);

    public void pushNotification(Message var1) throws FusionException;

    public void pushNotification(Message var1, Map<String, String> var2) throws FusionException;

    public int getOnlineContactsCount();

    public int getOnlineContactsCount(Map<String, String> var1);

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3) throws FusionException;

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Map<String, String> var4) throws FusionException;

    public void addToCurrentChatroomList(String var1) throws FusionException;

    public void addToCurrentChatroomList(String var1, Map<String, String> var2) throws FusionException;

    public void removeFromCurrentChatroomList(String var1);

    public void removeFromCurrentChatroomList(String var1, Map<String, String> var2);

    public String[] getCurrentChatrooms();

    public String[] getCurrentChatrooms(Map<String, String> var1);

    public int getReputationDataLevel();

    public int getReputationDataLevel(Map<String, String> var1);

    public void notifyUserLeftGroupChat(String var1, String var2);

    public void notifyUserLeftGroupChat(String var1, String var2, Map<String, String> var3);

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3);

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3, Map<String, String> var4);

    public void setCurrentChatListGroupChatSubset(ChatListIce var1);

    public void setCurrentChatListGroupChatSubset(ChatListIce var1, Map<String, String> var2);

    public void putMessageStatusEvent(MessageStatusEventIce var1) throws FusionException;

    public void putMessageStatusEvent(MessageStatusEventIce var1, Map<String, String> var2) throws FusionException;

    public SessionPrx findSession(String var1) throws FusionException;

    public SessionPrx findSession(String var1, Map<String, String> var2) throws FusionException;
}

