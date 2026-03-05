/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.ejb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.UserDataDAOChain;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.app.dao.db.fusiondb.FusionDbUserDataDAOChain;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.MerchantDetailsData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.data.UserSettingData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EJBUserDataDAOChain
extends UserDataDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbUserDataDAOChain.class);

    @Override
    public UserData getUserData(UserObject user, boolean fullyLoadUserObject, boolean loadFromMasterDB) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETUSERDATA)) {
            return super.getUserData(user, fullyLoadUserObject, loadFromMasterDB);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserData data = userEJB.loadUser(user.getUsername(), fullyLoadUserObject, loadFromMasterDB);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getUserData:[%s] for user:%s, fullyLoadUserObject:%s, loadFromMasterDB:%s", data, user, fullyLoadUserObject, loadFromMasterDB));
            }
            return data;
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to retrieve UserData, username:%s", user), (Throwable)e);
            return super.getUserData(user, fullyLoadUserObject, loadFromMasterDB);
        }
    }

    @Override
    public Set<String> getBroadcastList(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETBCL)) {
            return super.getBroadcastList(user);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            Set bcl = userEJB.checkAndLoadBroadcastList(user.getUsername(), null);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getBroadcastList:[%s] for user:%s", bcl, user));
            }
            return bcl;
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to retrieve UserData, username:%s", user), (Throwable)e);
            return super.getBroadcastList(user);
        }
    }

    @Override
    public List<ContactGroupData> getGroupList(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETGROUPLIST)) {
            return super.getGroupList(user);
        }
        try {
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            List groupList = contactEJB.getGroupList(user.getUsername());
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getGroupList:[%s] for user:%s", groupList, user));
            }
            return groupList;
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to retrieve group list for username:%s", user), (Throwable)e);
            return super.getGroupList(user);
        }
    }

    @Override
    public void assignDisplayPictureAndStatusMessageToContacts(Collection<ContactData> contactList) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_ASSIGNDPSM)) {
            super.assignDisplayPictureAndStatusMessageToContacts(contactList);
            return;
        }
        try {
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            contactEJB.assignDisplayPictureAndStatusMessageToContacts(null, contactList);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to assign picture + status message + status time stamp for contacts:%s", contactList), (Throwable)e);
            super.assignDisplayPictureAndStatusMessageToContacts(contactList);
        }
    }

    @Override
    public int getUserID(UserObject user, boolean throwExceptionWhenNotFound) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETUSERID)) {
            return super.getUserID(user, throwExceptionWhenNotFound);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            return userEJB.getUserID(user.getUsername(), null, throwExceptionWhenNotFound);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get userid for user:%s", user), (Throwable)e);
            return super.getUserID(user, throwExceptionWhenNotFound);
        }
    }

    @Override
    public String getUsername(int userid) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETUSERNAME)) {
            return super.getUsername(userid);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            return userEJB.getUsernameByUserid(userid, null);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get username for user:%s", userid), (Throwable)e);
            return super.getUsername(userid);
        }
    }

    @Override
    public Set<ContactData> getContactList(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GECONTACTLIST)) {
            return super.getContactList(user);
        }
        try {
            Contact contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            Set contacts = contactEJB.getContactList(user.getUsername());
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getContactList:[%s] for user:%s", contacts, user));
            }
            return contacts;
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to retrieve contact list for user:%s", user), (Throwable)e);
            return super.getContactList(user);
        }
    }

    @Override
    public List<UserSettingData> getUserSettings(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETUSERSETTINGS)) {
            return super.getUserSettings(user);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            List settings = userEJB.getUserSettings(user.getUsername());
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getUserSettings:[%s] for user:%s", settings, user));
            }
            return settings;
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get usersettings for user:%s", user), (Throwable)e);
            return super.getUserSettings(user);
        }
    }

    @Override
    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int userid, boolean mustUseMaster, boolean skipCacheCheck) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETUSERREPUTATIONSCOREANDLEVEL)) {
            return super.getReputationScoreAndLevel(userid, mustUseMaster, skipCacheCheck);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserReputationScoreAndLevelData data = userEJB.getReputationScoreAndLevel(mustUseMaster, userid, skipCacheCheck);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getReputationScoreAndLevel:[%s] for user:%s, mustUseMaster:%s, skipCacheCheck:%s", data, userid, mustUseMaster, skipCacheCheck));
            }
            return data;
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get UserReputationScoreAndLevelData for user:%s", userid), (Throwable)e);
            return super.getReputationScoreAndLevel(userid, mustUseMaster, skipCacheCheck);
        }
    }

    @Override
    public ReputationLevelData getReputationLevel(int level, boolean skipCacheCheck) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETREPUTATIONLEVELDATA)) {
            return super.getReputationLevel(level, skipCacheCheck);
        }
        try {
            MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
            return misEJB.getReputationLevelDataForLevel(level);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get ReputationLevelData for level:%s", level), (Throwable)e);
            return super.getReputationLevel(level, skipCacheCheck);
        }
    }

    @Override
    public MerchantDetailsData getBasicMerchantDetails(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETBASICMERCHANTDETAILS)) {
            return super.getBasicMerchantDetails(user);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            return userEJB.getBasicMerchantDetails(user.getUsername());
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get MerchantDetailsData for user:%s", user), (Throwable)e);
            return super.getBasicMerchantDetails(user);
        }
    }

    @Override
    public boolean isUserInMigboAccessList(UserObject user, int accessListType, int guardCapabilityType) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_ISUSERINMIGBOACCESSLIST)) {
            return super.isUserInMigboAccessList(user, accessListType, guardCapabilityType);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            return userEJB.isUserInMigboAccessList(user.getUserID(), accessListType, guardCapabilityType);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to check isUserInMigboAccessList for user:%s, accessListType:%s, guardCapabilityType:%s", user, accessListType, guardCapabilityType), (Throwable)e);
            return super.isUserInMigboAccessList(user, accessListType, guardCapabilityType);
        }
    }

    @Override
    public AccountBalanceData getAccountBalance(UserObject user) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETACCOUNTBALANCE)) {
            return super.getAccountBalance(user);
        }
        try {
            Account AccountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            return AccountEJB.getAccountBalance(user.getUsername());
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get AccountBalance Data for user:%s", user), (Throwable)e);
            return super.getAccountBalance(user);
        }
    }

    @Override
    public GroupMemberData getGroupMember(UserObject user, int groupID) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_GETGROUPMEMBER)) {
            return super.getGroupMember(user, groupID);
        }
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            return userEJB.getGroupMember(user.getUsername(), groupID);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to get GroupMemberData for user:%s, group:%s", user, groupID), (Throwable)e);
            return super.getGroupMember(user, groupID);
        }
    }

    @Override
    public boolean isUserBlackListedInGroup(UserObject user, int groupId) throws DAOException {
        if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EJB_NODE_ISUSERBLACKLISTEDINGROUP)) {
            return super.isUserBlackListedInGroup(user, groupId);
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.isUserBlackListedInGroup(user.getUsername(), groupId);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Failed to check isUserBlackListedInGroup for user:%s, group:%s", user, groupId), (Throwable)e);
            return super.isUserBlackListedInGroup(user, groupId);
        }
    }
}

