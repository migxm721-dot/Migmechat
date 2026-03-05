/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.cache.BroadcastList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.MerchantDetailsData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserSettingData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatUserData {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatUserData.class));
    private UserData userData;
    private MerchantDetailsData merchantDetailsData = null;
    Integer messageColor = null;
    int unreadEmailCount;

    public ChatUserData(UserData userData) {
        this.userData = userData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getUsername() {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.username;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getPassword() {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.password;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getLanguage() {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.language;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer getUserID() {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.userID;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UserDataIce getUserDataIce() {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.toIceObject();
        }
    }

    public UserData getNewUserData() {
        return new UserData(this.getUserDataIce());
    }

    public UserData getUserData() {
        return this.userData;
    }

    public UserSettingData.MessageEnum getMessageSetting() {
        return this.userData.messageSetting;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void blockUser(String blockUsername) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.blockList.add(blockUsername);
            this.userData.broadcastList.remove(blockUsername);
            this.userData.pendingContacts.remove(blockUsername);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean unblockUser(String username) {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.blockList.remove(username);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isOnBlockList(String username) {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.blockList.contains(username);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isOnBroadcastList(String source) {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.broadcastList.contains(source);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToBroadcastList(String username) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.broadcastList.add(username);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFromBroadcastList(String username) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.broadcastList.remove(username);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(UserData newUserData) {
        UserData userData = this.userData;
        synchronized (userData) {
            newUserData.pendingContacts = this.userData.pendingContacts;
            newUserData.blockList = this.userData.blockList;
            newUserData.broadcastList = this.userData.broadcastList;
            newUserData.messageSetting = this.userData.messageSetting;
            newUserData.anonymousCallSetting = this.userData.anonymousCallSetting;
            this.userData = newUserData;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(UserDataIce user) {
        UserData userData = this.userData;
        synchronized (userData) {
            UserData newUserData = new UserData(user);
            newUserData.pendingContacts = this.userData.pendingContacts;
            newUserData.blockList = this.userData.blockList;
            newUserData.broadcastList = this.userData.broadcastList;
            newUserData.messageSetting = this.userData.messageSetting;
            newUserData.anonymousCallSetting = this.userData.anonymousCallSetting;
            this.userData = newUserData;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getBroadcastList() {
        String[] broadcastListArray;
        UserData userData = this.userData;
        synchronized (userData) {
            broadcastListArray = BroadcastList.asArray(this.userData.broadcastList);
        }
        return broadcastListArray;
    }

    public String getDisplayPicture() {
        return this.userData.displayPicture;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateStatusMessage(String statusMessage, long timeStamp) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.statusMessage = statusMessage;
            this.userData.statusTimeStamp = new Date(timeStamp);
        }
    }

    public String getStatusMessage() {
        return this.userData.statusMessage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateDisplayPicture(String displayPicture, long timeStamp) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.displayPicture = displayPicture;
            this.userData.statusTimeStamp = new Date(timeStamp);
        }
    }

    public int contactRequestAccept(ContactData newContact) {
        return this.contactRequestAccept(newContact.fusionUsername);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int contactRequestAccept(String username) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.pendingContacts.remove(username);
            this.userData.broadcastList.add(username);
            return this.userData.pendingContacts.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int contactRequestReject(String inviterUsername) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.broadcastList.remove(inviterUsername);
            this.userData.pendingContacts.remove(inviterUsername);
            return this.userData.pendingContacts.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] findAllowedWatchers() {
        String[] allowedWatchersArray;
        UserData userData = this.userData;
        synchronized (userData) {
            allowedWatchersArray = this.userData.broadcastList.toArray(new String[this.userData.broadcastList.size()]);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("found " + (allowedWatchersArray == null ? 0 : allowedWatchersArray.length) + " watchers for user [" + this.userData.username + "]"));
        }
        return allowedWatchersArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMessageAvatar(MessageDataIce message) {
        UserData userData = this.userData;
        synchronized (userData) {
            String displayPicture = this.userData.avatar;
            if (displayPicture != null) {
                message.sourceDisplayPicture = displayPicture;
            }
        }
    }

    public Integer getCountryID() {
        return this.userData.countryID;
    }

    public boolean isOfType(UserData.TypeEnum typeEnum) {
        return this.userData.type == typeEnum;
    }

    public boolean isMobileVerified() {
        return this.userData.mobileVerified == null ? false : this.userData.mobileVerified;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getBlockList() {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.blockList.toArray(new String[this.userData.blockList.size()]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPendingContact(String username) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.pendingContacts.add(username);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> getPendingContacts() {
        HashSet<String> userList = new HashSet<String>();
        UserData userData = this.userData;
        synchronized (userData) {
            userList.addAll(this.userData.pendingContacts);
        }
        return userList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void contactUpdated(String usernameThatWasModified, boolean acceptedContactRequest) {
        UserData userData = this.userData;
        synchronized (userData) {
            if (acceptedContactRequest) {
                this.userData.pendingContacts.remove(usernameThatWasModified);
            } else {
                this.userData.pendingContacts.add(usernameThatWasModified);
            }
            this.userData.broadcastList.add(usernameThatWasModified);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void contactUpdated(String usernameThatWasModified) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.pendingContacts.remove(usernameThatWasModified);
            this.userData.broadcastList.remove(usernameThatWasModified);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMessageSetting(UserSettingData.MessageEnum fromValue) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.messageSetting = fromValue;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAnonymousCallSetting(int setting) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.anonymousCallSetting = UserSettingData.AnonymousCallEnum.fromValue(setting);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateContact(String oldusername, String fusionUsername, boolean acceptedContactRequest) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.pendingContacts.remove(oldusername);
            if (acceptedContactRequest) {
                this.userData.broadcastList.add(fusionUsername);
            } else {
                this.userData.pendingContacts.add(fusionUsername);
                this.userData.broadcastList.remove(oldusername);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UserSettingData.AnonymousCallEnum getAnonymousCallSetting() {
        UserData userData = this.userData;
        synchronized (userData) {
            return this.userData.anonymousCallSetting;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getBlocklistFromUsernames(String[] usernames) {
        HashSet<String> blockList = new HashSet<String>(Arrays.asList(usernames));
        UserData userData = this.userData;
        synchronized (userData) {
            blockList.retainAll(this.userData.blockList);
        }
        return blockList.toArray(new String[blockList.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateBalance(double balance, double fundedBalance, String currency) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.balance = balance;
            this.userData.fundedBalance = fundedBalance;
            this.userData.currency = currency;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int rejectContactRequest(String inviterUsername) {
        UserData userData = this.userData;
        synchronized (userData) {
            this.userData.broadcastList.remove(inviterUsername);
            this.userData.pendingContacts.remove(inviterUsername);
            return this.userData.pendingContacts.size();
        }
    }

    public boolean isChatRoomAdmin() {
        return this.userData.chatRoomAdmin;
    }

    public int getUnreadEmailCount() {
        return this.unreadEmailCount;
    }

    public void setUnreadEmailCount(int unreadEmailCount) {
        this.unreadEmailCount = unreadEmailCount;
    }

    public UserData.TypeEnum getUserType() {
        return this.userData.type;
    }

    public double getBalance() {
        return this.userData.balance;
    }

    public String getAvatar() {
        return this.userData.avatar;
    }

    public void applyMessageColor(MessageDataIce message) {
        if (this.messageColor == null) {
            this.messageColor = message.messageColour;
            this.updateMessageColor();
        }
        message.sourceColour = this.messageColor;
    }

    private void updateMessageColor() {
        if (this.getUserType() == UserData.TypeEnum.MIG33_TOP_MERCHANT) {
            if (this.merchantDetailsData == null) {
                try {
                    if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
                        UserObject user = new UserObject(this.userData.username);
                        this.merchantDetailsData = user.getBasicMerchantDetails();
                        if (log.isDebugEnabled()) {
                            log.debug((Object)String.format("DAO: getBasicMerchantDetails for user:%s. data:%s ", user, this.merchantDetailsData));
                        }
                    } else {
                        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                        this.merchantDetailsData = userEJB.getBasicMerchantDetails(this.getUsername());
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            this.messageColor = this.merchantDetailsData != null ? Integer.valueOf(this.merchantDetailsData.getChatColorHex()) : Integer.valueOf(MerchantDetailsData.UserNameColorTypeEnum.DEFAULT.hex());
        } else if (this.isChatRoomAdmin()) {
            this.messageColor = MessageData.SourceTypeEnum.GLOBAL_ADMIN.colorHex();
        }
    }

    public void updatePendingContacts(Contact contactEJB) throws Exception {
        Set recentFollowers;
        if (SystemProperty.getBool(SystemPropertyEntities.Contacts.PUSH_NEW_FOLLOWERS_AS_PENDING_CONTACTS_ENABLED) && (recentFollowers = contactEJB.getRecentFollowers(this.userData.userID)) != null && recentFollowers.size() > 0) {
            if (this.userData.pendingContacts == null) {
                this.userData.pendingContacts = new HashSet<String>();
            }
            int maxPendingContacts = SystemProperty.getInt(SystemPropertyEntities.Contacts.MAX_PENDING_CONTACTS_TO_RETRIEVE);
            for (String recentFollower : recentFollowers) {
                if (this.userData.pendingContacts.size() >= maxPendingContacts) break;
                this.userData.pendingContacts.add(recentFollower);
            }
        }
    }

    public void updatePendingContacts(UserNotificationServicePrx uns) throws Exception {
        if (SystemProperty.getBool(SystemPropertyEntities.Contacts.PUSH_NEW_FOLLOWERS_AS_PENDING_CONTACTS_ENABLED)) {
            Map<String, Map<String, String>> newFollowerAlerts = uns.getPendingNotificationDataForUserByType(this.userData.userID, Enums.NotificationTypeEnum.NEW_FOLLOWER_ALERT.getType());
            HashSet<String> recentFollowers = new HashSet<String>();
            for (Map<String, String> p : newFollowerAlerts.values()) {
                String username = p.get("otherUsername");
                if (StringUtil.isBlank(username)) continue;
                recentFollowers.add(username);
            }
            if (recentFollowers != null && recentFollowers.size() > 0) {
                if (this.userData.pendingContacts == null) {
                    this.userData.pendingContacts = new HashSet<String>();
                }
                int maxPendingContacts = SystemProperty.getInt(SystemPropertyEntities.Contacts.MAX_PENDING_CONTACTS_TO_RETRIEVE);
                for (String recentFollower : recentFollowers) {
                    if (this.userData.pendingContacts.size() >= maxPendingContacts) break;
                    this.userData.pendingContacts.add(recentFollower);
                }
            }
        }
    }
}

