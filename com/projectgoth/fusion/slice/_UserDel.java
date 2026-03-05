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
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _UserDel
extends _ObjectDel {
    public SessionPrx createSession(String var1, int var2, int var3, int var4, int var5, int var6, int var7, String var8, String var9, String var10, short var11, String var12, ConnectionPrx var13, Map<String, String> var14) throws LocalExceptionWrapper, FusionException;

    public void putMessage(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void contactChangedPresenceOneWay(int var1, String var2, int var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public void contactChangedDisplayPictureOneWay(String var1, String var2, long var3, Map<String, String> var5) throws LocalExceptionWrapper;

    public void contactChangedStatusMessageOneWay(String var1, String var2, long var3, Map<String, String> var5) throws LocalExceptionWrapper;

    public int getOverallFusionPresence(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public int getContactListVersion(Map<String, String> var1) throws LocalExceptionWrapper;

    public ContactList getContactList(Map<String, String> var1) throws LocalExceptionWrapper;

    public ContactDataIce[] getContacts(Map<String, String> var1) throws LocalExceptionWrapper;

    public ContactDataIce[] getOtherIMContacts(Map<String, String> var1) throws LocalExceptionWrapper;

    public Credential[] getOtherIMCredentials(Map<String, String> var1) throws LocalExceptionWrapper;

    public String[] getOtherIMConferenceParticipants(int var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public UserDataIce getUserData(Map<String, String> var1) throws LocalExceptionWrapper;

    public boolean isOnContactList(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public boolean isOnBlockList(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void otherIMLogin(int var1, int var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void otherIMLogout(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void otherIMSendMessage(int var1, String var2, String var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public String otherIMInviteToConference(int var1, String var2, String var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void otherIMLeaveConference(int var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void otherIMAddContact(int var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void otherIMRemoveContact(int var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void otherIMRemoved(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public String[] getBlockList(Map<String, String> var1) throws LocalExceptionWrapper;

    public String[] getBlockListFromUsernames(String[] var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public String[] getBroadcastList(Map<String, String> var1) throws LocalExceptionWrapper;

    public void accountBalanceChanged(double var1, double var3, CurrencyDataIce var5, Map<String, String> var6) throws LocalExceptionWrapper;

    public void privateChatNowAGroupChat(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putEvent(UserEventIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void putAlertMessage(String var1, String var2, short var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void putServerQuestion(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putWebCallNotification(String var1, String var2, int var3, String var4, int var5, Map<String, String> var6) throws LocalExceptionWrapper, FusionException;

    public void putAnonymousCallNotification(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void putFileReceived(MessageDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void contactDetailChanged(ContactDataIce var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void contactGroupDetailChanged(ContactGroupDataIce var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void contactGroupDeleted(int var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void userDetailChanged(UserDataIce var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void userReputationChanged(Map<String, String> var1) throws LocalExceptionWrapper;

    public void userDisplayPictureChanged(String var1, long var2, Map<String, String> var4) throws LocalExceptionWrapper;

    public void userStatusMessageChanged(String var1, long var2, Map<String, String> var4) throws LocalExceptionWrapper;

    public void messageSettingChanged(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void anonymousCallSettingChanged(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public int[] getConnectedOtherIMs(Map<String, String> var1) throws LocalExceptionWrapper;

    public boolean supportsBinaryMessage(Map<String, String> var1) throws LocalExceptionWrapper;

    public int getUnreadEmailCount(Map<String, String> var1) throws LocalExceptionWrapper;

    public void emailNotification(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public String[] getEmoticonHotKeys(Map<String, String> var1) throws LocalExceptionWrapper;

    public String[] getEmoticonAlternateKeys(Map<String, String> var1) throws LocalExceptionWrapper;

    public void emoticonPackActivated(int var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void themeChanged(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void addContact(ContactDataIce var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void addToContactAndBroadcastLists(ContactDataIce var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void addPendingContact(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public ContactDataIce acceptContactRequest(ContactDataIce var1, UserPrx var2, int var3, int var4, Map<String, String> var5) throws LocalExceptionWrapper;

    public ContactDataIce contactRequestWasAccepted(ContactDataIce var1, String var2, String var3, int var4, int var5, Map<String, String> var6) throws LocalExceptionWrapper;

    public void blockUser(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void unblockUser(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void contactRequestWasRejected(String var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void rejectContactRequest(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void stopBroadcastingTo(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void removeContact(int var1, int var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public PresenceAndCapabilityIce contactUpdated(ContactDataIce var1, String var2, boolean var3, boolean var4, UserPrx var5, int var6, Map<String, String> var7) throws LocalExceptionWrapper, FusionException;

    public void oldUserContactUpdated(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void newUserContactUpdated(String var1, boolean var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void notifySessionsOfNewContact(ContactDataIce var1, int var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public SessionPrx[] getSessions(Map<String, String> var1) throws LocalExceptionWrapper;

    public void disconnect(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void disconnectFlooder(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public boolean privateChattedWith(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public UserErrorResponse userCanContactMe(String var1, MessageDataIce var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void enteringGroupChat(boolean var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void leavingGroupChat(Map<String, String> var1) throws LocalExceptionWrapper;

    public void pushNotification(Message var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public int getOnlineContactsCount(Map<String, String> var1) throws LocalExceptionWrapper;

    public int executeEmoteCommandWithState(String var1, MessageDataIce var2, SessionPrx var3, Map<String, String> var4) throws LocalExceptionWrapper, FusionException;

    public void addToCurrentChatroomList(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void removeFromCurrentChatroomList(String var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public String[] getCurrentChatrooms(Map<String, String> var1) throws LocalExceptionWrapper;

    public int getReputationDataLevel(Map<String, String> var1) throws LocalExceptionWrapper;

    public void notifyUserLeftGroupChat(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;

    public void notifyUserJoinedGroupChat(String var1, String var2, boolean var3, Map<String, String> var4) throws LocalExceptionWrapper;

    public void setCurrentChatListGroupChatSubset(ChatListIce var1, Map<String, String> var2) throws LocalExceptionWrapper;

    public void putMessageStatusEvent(MessageStatusEventIce var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public SessionPrx findSession(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;
}

