/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.UserDataDAOChain;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class UserDataDAO {
    private UserDataDAOChain readChain;
    private UserDataDAOChain writeChain;
    private static final Pattern USERALIAS_PATTERN = Pattern.compile("^[a-zA-Z](\\.?[\\w-])+$");
    private final LazyLoader<Pattern> banned_usernames_pattern = new LazyLoader<Pattern>("BANNED_USERNAMES_PATTERN", SystemProperty.getLong(SystemPropertyEntities.EJBCacheDuration.MIS_CLIENT_TEXT) * 1000L){

        @Override
        protected Pattern fetchValue() throws DAOException {
            return Pattern.compile(SystemProperty.get(SystemPropertyEntities.DAOSettings.BANNED_USERNAMES_PATTERN), 2);
        }
    };

    public UserDataDAO(UserDataDAOChain readChain, UserDataDAOChain writeChain) {
        this.readChain = readChain;
        this.writeChain = writeChain;
    }

    public UserData getUserData(UserObject user, boolean fullyLoadUserObject, boolean loadFromMasterDB) throws DAOException {
        return this.readChain.getUserData(user, fullyLoadUserObject, loadFromMasterDB);
    }

    public Set<String> getBroadcastList(UserObject user) throws DAOException {
        return this.readChain.getBroadcastList(user);
    }

    public List<UserSettingData> getUserSettings(UserObject user) throws DAOException {
        return this.readChain.getUserSettings(user);
    }

    public List<ContactGroupData> getGroupList(UserObject user) throws DAOException {
        return this.readChain.getGroupList(user);
    }

    public Set<ContactData> getContactList(UserObject user) throws DAOException {
        return this.readChain.getContactList(user);
    }

    public void assignDisplayPictureAndStatusMessageToContacts(Collection<ContactData> contactList) throws DAOException {
        this.readChain.assignDisplayPictureAndStatusMessageToContacts(contactList);
    }

    public int getUserID(UserObject user, boolean throwExceptionWhenNotFound) throws DAOException {
        return this.readChain.getUserID(user, throwExceptionWhenNotFound);
    }

    public String getUsername(int userid) throws DAOException {
        return this.readChain.getUsername(userid);
    }

    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int userid, boolean mustUseMaster, boolean skipCacheCheck) throws DAOException {
        return this.readChain.getReputationScoreAndLevel(userid, mustUseMaster, skipCacheCheck);
    }

    public ReputationLevelData getReputationLevel(int level, boolean skipCacheCheck) throws DAOException {
        return this.readChain.getReputationLevel(level, skipCacheCheck);
    }

    public MerchantDetailsData getBasicMerchantDetails(UserObject user) throws DAOException {
        return this.readChain.getBasicMerchantDetails(user);
    }

    public List<EmoticonData> getEmoticons(UserObject user) throws DAOException {
        return this.readChain.getEmoticons(user);
    }

    public List<Integer> getEmoticonPacks(UserObject user) throws DAOException {
        return this.readChain.getEmoticonPacks(user);
    }

    public boolean isUserInMigboAccessList(UserObject user, int accessListType, int guardCapabilityType) throws DAOException {
        return this.readChain.isUserInMigboAccessList(user, accessListType, guardCapabilityType);
    }

    public AccountBalanceData getAccountBalance(UserObject user) throws DAOException {
        return this.readChain.getAccountBalance(user);
    }

    public GroupMemberData getGroupMember(UserObject user, int groupID) throws DAOException {
        return this.readChain.getGroupMember(user, groupID);
    }

    public boolean isUserBlackListedInGroup(UserObject user, int groupId) throws DAOException {
        return this.readChain.isUserBlackListedInGroup(user, groupId);
    }

    public void setAlias(UserObject user, String alias) throws DAOException {
        this.checkUserAlias(alias, user.getUsername().equalsIgnoreCase(alias));
        this.writeChain.setAlias(user, alias);
    }

    private void checkUserAlias(String alias, boolean sameAsUsername) throws DAOException {
        Matcher m;
        if (StringUtil.isBlank(alias)) {
            throw new DAOException("Empty alias");
        }
        if (alias.length() < (sameAsUsername ? 3 : 6) || !USERALIAS_PATTERN.matcher(alias).matches()) {
            throw new DAOException("The alias must start with a letter, and contain at least 6 letters, numbers, periods (.), hyphens (-), or underscores (_)");
        }
        int maxUserAliasLength = SystemProperty.getInt("MaxUserAliasLength", 20);
        if (alias.length() > maxUserAliasLength) {
            throw new DAOException("The alias must not contain more than " + maxUserAliasLength + " characters");
        }
        if (!sameAsUsername && (m = this.banned_usernames_pattern.getValue().matcher(alias)).find()) {
            throw new DAOException(String.format("You cannot use [%s] in the alias", m.group()));
        }
    }
}

