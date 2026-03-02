/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.base;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.DAOChain;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.MerchantDetailsData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.data.UserSettingData;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UserDataDAOChain
implements DAOChain {
    private UserDataDAOChain nextRead;
    private UserDataDAOChain nextWrite;

    @Override
    public void setNextRead(DAOChain a) {
        this.nextRead = (UserDataDAOChain)a;
    }

    @Override
    public void setNextWrite(DAOChain a) {
        this.nextWrite = (UserDataDAOChain)a;
    }

    public UserData getUserData(UserObject user, boolean fullyLoadUserObject, boolean loadFromMasterDB) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getUserData(user, fullyLoadUserObject, loadFromMasterDB);
        }
        throw new DAOException(String.format("Unable to retrieve UserData for user%s", user));
    }

    public Set<String> getBroadcastList(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getBroadcastList(user);
        }
        throw new DAOException(String.format("Unable to retrieve broadcast list for user:%s", user));
    }

    public List<UserSettingData> getUserSettings(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getUserSettings(user);
        }
        throw new DAOException(String.format("Unable to retrieve user settings for user:%s", user));
    }

    public List<ContactGroupData> getGroupList(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getGroupList(user);
        }
        throw new DAOException(String.format("Unable to retrieve group list for user:%s", user));
    }

    public Set<ContactData> getContactList(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getContactList(user);
        }
        throw new DAOException(String.format("Unable to retrieve contact list for user:%s", user));
    }

    public void assignDisplayPictureAndStatusMessageToContacts(Collection<ContactData> contactList) throws DAOException {
        if (this.nextRead != null) {
            this.nextRead.assignDisplayPictureAndStatusMessageToContacts(contactList);
            return;
        }
        throw new DAOException(String.format("Unable to assign picture + status message + status time stamp to contact list:%s", contactList));
    }

    public int getUserID(UserObject user, boolean throwExceptionWhenNotFound) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getUserID(user, throwExceptionWhenNotFound);
        }
        if (throwExceptionWhenNotFound) {
            throw new DAOException(String.format("Unable to retrieve userid for user:%s", user));
        }
        return -1;
    }

    public String getUsername(int userid) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getUsername(userid);
        }
        throw new DAOException(String.format("Unable to retrieve username for user:%s", userid));
    }

    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int userid, boolean mustUseMaster, boolean skipCacheCheck) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getReputationScoreAndLevel(userid, mustUseMaster, skipCacheCheck);
        }
        throw new DAOException(String.format("Unable to retrieve UserReputationScoreAndLevelData for user:%s", userid));
    }

    public ReputationLevelData getReputationLevel(int level, boolean skipCacheCheck) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getReputationLevel(level, skipCacheCheck);
        }
        throw new DAOException(String.format("Unable to retrieve ReputationLevelData for level:%s", level));
    }

    public MerchantDetailsData getBasicMerchantDetails(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getBasicMerchantDetails(user);
        }
        throw new DAOException(String.format("Unable to retrieve MerchantDetailsData for user:%s", user));
    }

    public List<EmoticonData> getEmoticons(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getEmoticons(user);
        }
        throw new DAOException(String.format("Unable to retrieve EmoticonDatas for user:%s", user));
    }

    public List<Integer> getEmoticonPacks(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getEmoticonPacks(user);
        }
        throw new DAOException(String.format("Unable to retrieve EmoticonIDs for user:%s", user));
    }

    public boolean isUserInMigboAccessList(UserObject user, int accessListType, int guardCapabilityType) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.isUserInMigboAccessList(user, accessListType, guardCapabilityType);
        }
        throw new DAOException(String.format("Unable to check isUserInMigboAccessList for user:%s, accessListType:%s, guardCapabilityType:%s", user, accessListType, guardCapabilityType));
    }

    public AccountBalanceData getAccountBalance(UserObject user) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getAccountBalance(user);
        }
        throw new DAOException(String.format("Unable to retrieve AccountBalance data for user:%s", user));
    }

    public GroupMemberData getGroupMember(UserObject user, int groupID) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.getGroupMember(user, groupID);
        }
        throw new DAOException(String.format("Unable to retrieve GroupMemberData data for user:%s, group:%s", user, groupID));
    }

    public boolean isUserBlackListedInGroup(UserObject user, int groupId) throws DAOException {
        if (this.nextRead != null) {
            return this.nextRead.isUserBlackListedInGroup(user, groupId);
        }
        throw new DAOException(String.format("Unable to check isUserBlackListedInGroup for user:%s, group:%s", user, groupId));
    }

    public void setAlias(UserObject user, String alias) throws DAOException {
        if (this.nextWrite != null) {
            this.nextWrite.setAlias(user, alias);
        }
    }
}

