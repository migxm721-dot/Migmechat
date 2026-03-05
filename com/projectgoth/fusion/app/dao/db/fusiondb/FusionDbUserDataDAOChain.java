/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.app.dao.db.fusiondb;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.app.dao.base.UserDataDAOChain;
import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.common.MemCachedHelper;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.EmoticonPackData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.MerchantDetailsData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.ServiceData;
import com.projectgoth.fusion.data.SubscriptionData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.data.UserSettingData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionDbUserDataDAOChain
extends UserDataDAOChain {
    private static final Logger log = Logger.getLogger(FusionDbUserDataDAOChain.class);
    private static final String sql_retrieve_user_data = "select u.*, uid.id as uid, a.headuuid as avatar, a.bodyuuid as fullbodyavatar, ua.alias,uv.type accountType, uv.verified accountVerified, uv.description verifiedProfile, uea.emailaddress primaryEmail, uea.verified emailVerified from user u, userid uid LEFT OUTER JOIN avataruserbody a ON uid.id = a.userid and a.used = 1 LEFT OUTER JOIN useralias ua ON uid.username=ua.username LEFT OUTER JOIN userverified uv ON uv.userid = uid.id LEFT OUTER JOIN useremailaddress uea ON uea.userid = uid.id and uea.type = ? where u.username = uid.username and u.username = ? ";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UserData getUserData(UserObject user, boolean fullyLoadUserObject, boolean loadFromMasterDB) throws DAOException {
        UserData userData;
        String username;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block7: {
            conn = null;
            ps = null;
            rs = null;
            username = user.getUsername();
            conn = loadFromMasterDB ? DBUtils.getFusionWriteConnection() : DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement(sql_retrieve_user_data);
            ps.setInt(1, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (rs.next() && username.toLowerCase().equals(rs.getString("u.username"))) break block7;
            UserData userData2 = super.getUserData(user, fullyLoadUserObject, loadFromMasterDB);
            Object var11_12 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return userData2;
        }
        try {
            UserData userData3 = new UserData(rs);
            this.populateUserSettings(user, userData3);
            if (fullyLoadUserObject) {
                rs.close();
                ps.close();
                userData3.pendingContacts = this.loadPendingContacts(userData3.username, conn);
                userData3.blockList = this.loadBlockList(userData3.username, conn);
                userData3.broadcastList = DAOFactory.getInstance().getUserDataDAO().getBroadcastList(user);
            }
            MemCachedHelper.setUserAlias(username, userData3.userID, userData3.alias);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getUserData:[%s] for user:%s, fullyLoadUserObject:%s, loadFromMasterDB:%s", userData3, user, fullyLoadUserObject, loadFromMasterDB));
            }
            userData = userData3;
            Object var11_13 = null;
        }
        catch (Exception e) {
            UserData userData4;
            try {
                log.error((Object)String.format("Failed to retrieve UserData, username:%s", user), (Throwable)e);
                userData4 = super.getUserData(user, fullyLoadUserObject, loadFromMasterDB);
                Object var11_14 = null;
            }
            catch (Throwable throwable) {
                Object var11_15 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return userData4;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return userData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<UserSettingData> getUserSettings(UserObject user) throws DAOException {
        ArrayList<UserSettingData> arrayList;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from usersetting where username = ?");
            ps.setString(1, user.getUsername());
            rs = ps.executeQuery();
            ArrayList<UserSettingData> settings = new ArrayList<UserSettingData>();
            while (rs.next()) {
                settings.add(new UserSettingData(rs));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getUserSettings:[%s] for user:%s", settings, user));
            }
            arrayList = settings;
            Object var8_9 = null;
        }
        catch (SQLException e) {
            List<UserSettingData> list;
            try {
                log.error((Object)String.format("Unable to retrieve user setting data from user%s", user), (Throwable)e);
                list = super.getUserSettings(user);
                Object var8_10 = null;
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return arrayList;
    }

    private void populateUserSettings(UserObject user, UserData userData) throws DAOException {
        List<UserSettingData> settings = DAOFactory.getInstance().getUserDataDAO().getUserSettings(user);
        for (UserSettingData setting : settings) {
            if (setting.type == null) continue;
            switch (setting.type) {
                case MESSAGE: {
                    userData.messageSetting = UserSettingData.MessageEnum.fromValue(setting.value);
                    break;
                }
                case ANONYMOUS_CALL: {
                    userData.anonymousCallSetting = UserSettingData.AnonymousCallEnum.fromValue(setting.value);
                    break;
                }
                case EMAIL_ALL: {
                    userData.emailAllSetting = UserSettingData.EmailSettingEnum.fromValue(setting.value);
                    break;
                }
                case EMAIL_MENTION: {
                    userData.emailMentionSetting = UserSettingData.EmailSettingEnum.fromValue(setting.value);
                    break;
                }
                case EMAIL_NEW_FOLLOWER: {
                    userData.emailNewFollowerSetting = UserSettingData.EmailSettingEnum.fromValue(setting.value);
                    break;
                }
                case EMAIL_RECEIVE_GIFT: {
                    userData.emailReceiveGiftSetting = UserSettingData.EmailSettingEnum.fromValue(setting.value);
                    break;
                }
                case EMAIL_REPLY_TO_POST: {
                    userData.emailReplyToPostSetting = UserSettingData.EmailSettingEnum.fromValue(setting.value);
                    break;
                }
            }
        }
    }

    private Set<String> loadPendingContacts(String username, Connection connection) throws SQLException {
        return this.loadStringListForUser(username, "pendingContact", "pendingcontact", "username", connection);
    }

    private Set<String> loadBlockList(String username, Connection connection) throws SQLException {
        return this.loadStringListForUser(username, "blockUsername", "blocklist", "username", connection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Set<String> checkAndLoadBroadcastList(String username, Connection conn) throws DAOException {
        try {
            HashSet<String> hashSet;
            String FIELD = "fusionUsername";
            String QUERY = "select myContacts.fusionUsername from contact myContacts inner join contact contactsContacts on contactsContacts.fusionUsername = myContacts.username and contactsContacts.username = myContacts.fusionUsername left join blocklist myBlocklist on myBlocklist.username = myContacts.username and myBlocklist.blockusername = myContacts.fusionUsername left join blocklist contactsBlocklists on contactsBlocklists.blockusername = myContacts.username and contactsBlocklists.username = myContacts.fusionUsername where myContacts.username = ? and myBlocklist.blockusername is null and contactsBlocklists.blockusername is null";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("select myContacts.fusionUsername from contact myContacts inner join contact contactsContacts on contactsContacts.fusionUsername = myContacts.username and contactsContacts.username = myContacts.fusionUsername left join blocklist myBlocklist on myBlocklist.username = myContacts.username and myBlocklist.blockusername = myContacts.fusionUsername left join blocklist contactsBlocklists on contactsBlocklists.blockusername = myContacts.username and contactsBlocklists.username = myContacts.fusionUsername where myContacts.username = ? and myBlocklist.blockusername is null and contactsBlocklists.blockusername is null");
                ps.setString(1, username);
                rs = ps.executeQuery();
                HashSet<String> contacts = new HashSet<String>();
                while (rs.next()) {
                    contacts.add(rs.getString("fusionUsername"));
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)String.format("DAO: Loaded BCL of length=" + contacts.size() + " for username=" + username, new Object[0]));
                }
                hashSet = contacts;
                Object var10_10 = null;
            }
            catch (Throwable throwable) {
                Object var10_11 = null;
                DBUtils.closeResource(rs, ps, null, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, null, log);
            return hashSet;
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve broadcast list for username:%s", username), (Throwable)e);
            throw new DAOException(String.format("Failed to retrieve broadcast list for username:%s", username));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<String> loadStringListForUser(String whereValue, String field, String tableName, String whereCondition, Connection connection) throws SQLException {
        HashSet<String> hashSet;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement("select " + field + " from " + tableName + " where " + whereCondition + " = ?");
            ps.setString(1, whereValue);
            rs = ps.executeQuery();
            HashSet<String> set = new HashSet<String>();
            while (rs.next()) {
                set.add(rs.getString(field));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Load field:%s from table:%s where %s = %s, with result set as:%s", field, tableName, whereCondition, whereValue, set));
            }
            hashSet = set;
            Object var11_10 = null;
        }
        catch (Throwable throwable) {
            Object var11_11 = null;
            DBUtils.closeResource(rs, ps, null, log);
            throw throwable;
        }
        DBUtils.closeResource(rs, ps, null, log);
        return hashSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<String> getBroadcastList(UserObject user) throws DAOException {
        Set<String> set;
        Connection conn = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            Set<String> bcl = null;
            bcl = SystemProperty.getBool(SystemPropertyEntities.Default.CHECK_AND_POPULATEBCL) ? this.checkAndLoadBroadcastList(user.getUsername(), conn) : this.loadStringListForUser(user.getUsername(), "broadcastUsername", "broadcastlist", "username", conn);
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getBroadcastList:[%s] for user:%s", bcl, user));
            }
            set = bcl;
            Object var6_7 = null;
        }
        catch (Exception e) {
            Set<String> set2;
            try {
                log.error((Object)String.format("Failed to retrieve broadcast list for username:%s", user), (Throwable)e);
                set2 = super.getBroadcastList(user);
                Object var6_8 = null;
            }
            catch (Throwable throwable) {
                Object var6_9 = null;
                DBUtils.closeResource(null, null, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(null, null, conn, log);
            return set2;
        }
        DBUtils.closeResource(null, null, conn, log);
        return set;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ContactGroupData> getGroupList(UserObject user) throws DAOException {
        LinkedList<ContactGroupData> linkedList;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String username = user.getUsername();
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from contactgroup where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            LinkedList<ContactGroupData> groupList = new LinkedList<ContactGroupData>();
            while (rs.next()) {
                groupList.add(new ContactGroupData(rs));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getGroupList:[%s] for user:%s", groupList, user));
            }
            linkedList = groupList;
            Object var9_10 = null;
        }
        catch (SQLException e) {
            List<ContactGroupData> list;
            try {
                log.error((Object)String.format("Failed to retrieve group list for username:%s", username), (Throwable)e);
                list = super.getGroupList(user);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return linkedList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<ContactData> getContactList(UserObject user) throws DAOException {
        HashSet<ContactData> hashSet;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String username = user.getUsername();
        try {
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from contact where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            HashSet<ContactData> contacts = new HashSet<ContactData>();
            while (rs.next()) {
                contacts.add(new ContactData(rs));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getContactList:[%s] for user:%s", contacts, user));
            }
            hashSet = contacts;
            Object var9_10 = null;
        }
        catch (SQLException e) {
            Set<ContactData> set;
            try {
                log.error((Object)String.format("Failed to retrieve contact list for username:%s", username), (Throwable)e);
                set = super.getContactList(user);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return set;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return hashSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void assignDisplayPictureAndStatusMessageToContacts(Collection<ContactData> contactList) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            try {
                HashMap<String, ContactData> contactMap = new HashMap<String, ContactData>();
                for (ContactData contact : contactList) {
                    if (StringUtil.isBlank(contact.fusionUsername)) continue;
                    contactMap.put(contact.fusionUsername, contact);
                }
                conn = DBUtils.getFusionReadConnection();
                String parameters = StringUtil.generateQuestionMarksForSQLStatement(contactMap.size());
                ps = conn.prepareStatement("select username, displayPicture, statusMessage, statusTimeStamp, dateRegistered from user where username in (" + parameters + ")");
                int i = 1;
                for (String key : contactMap.keySet()) {
                    ps.setString(i++, key);
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    ContactData contact = (ContactData)contactMap.get(rs.getString("username"));
                    if (contact == null) continue;
                    contact.displayPicture = rs.getString("displayPicture");
                    contact.statusMessage = StringUtil.stripHTML(rs.getString("statusMessage"));
                    try {
                        contact.statusTimeStamp = rs.getTimestamp("statusTimeStamp");
                    }
                    catch (Exception ignored) {
                        contact.statusTimeStamp = rs.getTimestamp("dateRegistered");
                    }
                }
                Object var11_13 = null;
            }
            catch (SQLException e) {
                log.error((Object)String.format("Failed to assign picture + status message + status time stamp for contacts:%s", contactList), (Throwable)e);
                super.assignDisplayPictureAndStatusMessageToContacts(contactList);
                Object var11_14 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return;
            }
        }
        catch (Throwable throwable) {
            Object var11_15 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            throw throwable;
        }
        DBUtils.closeResource(rs, ps, conn, log);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getUserID(UserObject user, boolean throwExceptionWhenNotFound) throws DAOException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block9: {
            block8: {
                block7: {
                    conn = null;
                    ps = null;
                    rs = null;
                    conn = DBUtils.getFusionReadConnection();
                    ps = conn.prepareStatement("select id from userid where username = ?");
                    ps.setString(1, user.getUsername());
                    rs = ps.executeQuery();
                    if (!rs.next()) break block7;
                    int n = rs.getInt("id");
                    Object var9_10 = null;
                    DBUtils.closeResource(rs, ps, conn, log);
                    return n;
                }
                log.warn((Object)String.format("FIXME: Failed to find userid in userid table for user:%s in fusion database", user));
                if (throwExceptionWhenNotFound) break block8;
                int n = -1;
                Object var9_11 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return n;
            }
            if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHAIN_ON_IF_SHOULD_NOT_REACH_HERE_HAPPENS)) break block9;
            int n = super.getUserID(user, throwExceptionWhenNotFound);
            Object var9_12 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return n;
        }
        try {
            try {
                throw new DAOException(String.format("Failed to find userid in userid table for user:%s in fusion database", user));
            }
            catch (SQLException e) {
                log.error((Object)String.format("Failed to get userid for user:%s", user), (Throwable)e);
                int n = super.getUserID(user, throwExceptionWhenNotFound);
                Object var9_13 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return n;
            }
        }
        catch (Throwable throwable) {
            Object var9_14 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getUsername(int userid) throws DAOException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block7: {
            block6: {
                conn = null;
                ps = null;
                rs = null;
                conn = DBUtils.getFusionReadConnection();
                ps = conn.prepareStatement("select username from userid where id = ?");
                ps.setInt(1, userid);
                rs = ps.executeQuery();
                if (!rs.next()) break block6;
                String string = rs.getString("username");
                Object var8_8 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return string;
            }
            log.warn((Object)String.format("FIXME: Failed to find username in userid table for user:%s in fusion database", userid));
            if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHAIN_ON_IF_SHOULD_NOT_REACH_HERE_HAPPENS)) break block7;
            String string = super.getUsername(userid);
            Object var8_9 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return string;
        }
        try {
            try {
                throw new DAOException(String.format("Failed to find username for useridid:%s in fusion database", userid));
            }
            catch (SQLException e) {
                log.error((Object)String.format("Failed to get username for user:%s", userid), (Throwable)e);
                String string = super.getUsername(userid);
                Object var8_10 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return string;
            }
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int userid, boolean mustUseMaster, boolean skipCacheCheck) throws DAOException {
        UserReputationScoreAndLevelData userReputationScoreAndLevelData;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            UserReputationScoreAndLevelData data;
            conn = mustUseMaster ? DBUtils.getFusionWriteConnection() : DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select s.*, max(r.level) as level from score s, reputationscoretolevel r where s.userid = ? and s.score >= r.score;");
            ps.setInt(1, userid);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt("level") != 0) {
                data = new UserReputationScoreAndLevelData(rs, mustUseMaster);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Found score data for user: " + userid + " score: " + data.score));
                }
            } else {
                data = new UserReputationScoreAndLevelData(userid, 0, 1, new Date(0L), mustUseMaster);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("No score data found for user : " + userid + " using default values"));
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: Successfully getReputationScoreAndLevel:[%s] for user:%s, mustUseMaster:%s, skipCacheCheck:%s", data, userid, mustUseMaster, skipCacheCheck));
            }
            userReputationScoreAndLevelData = data;
            Object var10_11 = null;
        }
        catch (SQLException e) {
            UserReputationScoreAndLevelData userReputationScoreAndLevelData2;
            try {
                log.error((Object)String.format("Failed to get UserReputationScoreAndLevelData for user:%s", userid), (Throwable)e);
                userReputationScoreAndLevelData2 = super.getReputationScoreAndLevel(userid, mustUseMaster, skipCacheCheck);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return userReputationScoreAndLevelData2;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return userReputationScoreAndLevelData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public ReputationLevelData getReputationLevel(int level, boolean skipCacheCheck) throws DAOException {
        ReputationLevelData reputationLevelData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block8: {
            ReputationLevelData reputationLevelData2;
            block7: {
                conn = null;
                ps = null;
                rs = null;
                try {
                    try {
                        conn = DBUtils.getFusionReadConnection();
                        ps = conn.prepareStatement("select * from reputationscoretolevel where level = ?");
                        ps.setInt(1, level);
                        rs = ps.executeQuery();
                        ReputationLevelData data = null;
                        if (!rs.next()) {
                            log.warn((Object)String.format("FIXME: Failed to get result from table reputationscoretolevel where level = %s in fusion database", level));
                            if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHAIN_ON_IF_SHOULD_NOT_REACH_HERE_HAPPENS)) {
                                throw new DAOException(String.format("Failed to find ReputationLevelData for level:%s in fusion database", level));
                            }
                            reputationLevelData2 = super.getReputationLevel(level, skipCacheCheck);
                            Object var9_11 = null;
                            break block7;
                        }
                        reputationLevelData = data = new ReputationLevelData(rs);
                        break block8;
                    }
                    catch (SQLException e) {
                        log.error((Object)String.format("Failed to get ReputationLevelData for level:%s", level), (Throwable)e);
                        ReputationLevelData reputationLevelData3 = super.getReputationLevel(level, skipCacheCheck);
                        Object var9_13 = null;
                        DBUtils.closeResource(rs, ps, conn, log);
                        return reputationLevelData3;
                    }
                }
                catch (Throwable throwable) {
                    Object var9_14 = null;
                    DBUtils.closeResource(rs, ps, conn, log);
                    throw throwable;
                }
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return reputationLevelData2;
        }
        Object var9_12 = null;
        DBUtils.closeResource(rs, ps, conn, log);
        return reputationLevelData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MerchantDetailsData getBasicMerchantDetails(UserObject user) throws DAOException {
        MerchantDetailsData merchantDetailsData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            String sql = "select merchantdetails.* from merchantdetails join userid on merchantdetails.id = userid.id where userid.username = ?;";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            rs = ps.executeQuery();
            if (!rs.next()) break block5;
            MerchantDetailsData merchantDetailsData2 = new MerchantDetailsData(rs, false);
            Object var8_10 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return merchantDetailsData2;
        }
        try {
            merchantDetailsData = null;
            Object var8_11 = null;
        }
        catch (SQLException e) {
            MerchantDetailsData merchantDetailsData3;
            try {
                log.error((Object)String.format("Failed to get BasicMerchantDetails for user:%s", user), (Throwable)e);
                merchantDetailsData3 = super.getBasicMerchantDetails(user);
                Object var8_12 = null;
            }
            catch (Throwable throwable) {
                Object var8_13 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return merchantDetailsData3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return merchantDetailsData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Integer> getEmoticonPacks(UserObject user) throws DAOException {
        ArrayList<Integer> arrayList;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getFusionReadConnection();
            String sql = "select emoticonpackid from emoticonpackowner where username = ? and status = 1 union select p.id from emoticonpack p, subscription s where p.serviceid = s.serviceid and p.type = ? and s.username = ? and s.status = ?";
            String username = user.getUsername();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, EmoticonPackData.TypeEnum.PREMIUM_SUBSCRIPTION.value());
            ps.setString(3, username);
            ps.setInt(4, SubscriptionData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            ArrayList<Integer> emoIDs = new ArrayList<Integer>();
            while (rs.next()) {
                emoIDs.add(rs.getInt(1));
            }
            arrayList = emoIDs;
            Object var10_11 = null;
        }
        catch (SQLException e) {
            List<Integer> list;
            try {
                log.error((Object)String.format("Failed to get EmoticonIDs for user:%s", user), (Throwable)e);
                list = super.getEmoticonPacks(user);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return list;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return arrayList;
    }

    @Override
    public List<EmoticonData> getEmoticons(UserObject user) throws DAOException {
        try {
            List<Integer> emoticonPackIDs = user.getEmoticonPacks();
            emoticonPackIDs.add(0, 1);
            ArrayList<EmoticonData> emoticons = new ArrayList<EmoticonData>();
            Map<Integer, EmoticonPackData> emoPacksMap = DAOFactory.getInstance().getEmoAndStickerDAO().loadEmoticonPacks();
            for (Integer emoticonPackID : emoticonPackIDs) {
                if (!emoPacksMap.containsKey(emoticonPackID)) continue;
                emoticons.addAll(this.getEmoticons(emoPacksMap.get(emoticonPackID).getEmoticonIDs()));
            }
            return emoticons;
        }
        catch (DAOException e) {
            log.error((Object)String.format("Failed to get Emoticons for user:%s", user), (Throwable)e);
            return super.getEmoticons(user);
        }
    }

    private List<EmoticonData> getEmoticons(List<Integer> emoticonIDList) throws DAOException {
        ArrayList<EmoticonData> emoticonDatas = new ArrayList<EmoticonData>(emoticonIDList.size());
        Map<Integer, EmoticonData> emoticons = DAOFactory.getInstance().getEmoAndStickerDAO().loadEmoticons();
        for (Integer emoticonID : emoticonIDList) {
            EmoticonData emoticonData = emoticons.get(emoticonID);
            if (emoticonData == null) continue;
            emoticonDatas.add(emoticonData);
        }
        return emoticonDatas;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isUserInMigboAccessList(UserObject user, int accessListType, int guardCapabilityType) throws DAOException {
        boolean bl;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            int value;
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            String sql = "SELECT COUNT(*) ctr_capability FROM guardcapability gc, guardsetcapability gsc, guardsetmember gsm, guardset gs WHERE gc.id = gsc.guardcapabilityid AND gsm.guardsetid = gsc.guardsetid AND gsm.guardsetid = gs.id AND gsm.membertype = ? AND gc.id = ? AND gsm.memberid = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, accessListType);
            ps.setInt(2, guardCapabilityType);
            ps.setInt(3, user.getUserID());
            rs = ps.executeQuery();
            if (!rs.next() || (value = rs.getInt(1)) <= 0) break block5;
            boolean bl2 = true;
            Object var11_11 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return bl2;
        }
        try {
            bl = false;
            Object var11_12 = null;
        }
        catch (SQLException e) {
            try {
                log.error((Object)String.format("Failed to check isUserInMigboAccessList for user:%s, accessListType:%s, guardCapabilityType:%s", user, accessListType, guardCapabilityType), (Throwable)e);
                bl = super.isUserInMigboAccessList(user, accessListType, guardCapabilityType);
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return bl;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AccountBalanceData getAccountBalance(UserObject user) throws DAOException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block7: {
            block6: {
                conn = null;
                ps = null;
                rs = null;
                conn = DBUtils.getFusionReadConnection();
                ps = conn.prepareStatement("select user.balance, user.fundedbalance, currency.* from user, currency where user.currency = currency.code and username = ?");
                ps.setString(1, user.getUsername());
                rs = ps.executeQuery();
                if (!rs.next()) break block6;
                AccountBalanceData balanceData = new AccountBalanceData();
                balanceData.currency = new CurrencyData(rs);
                balanceData.balance = rs.getDouble("balance");
                balanceData.fundedBalance = rs.getDouble("fundedBalance");
                AccountBalanceData accountBalanceData = balanceData;
                Object var8_10 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return accountBalanceData;
            }
            log.warn((Object)String.format("FIXME: Failed to get AccountBalance data for user:%s in fusion db", user));
            if (!SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_CHAIN_ON_IF_SHOULD_NOT_REACH_HERE_HAPPENS)) break block7;
            AccountBalanceData balanceData = super.getAccountBalance(user);
            Object var8_11 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return balanceData;
        }
        try {
            try {
                throw new DAOException(String.format("Failed to get AccountBalanceData for user:%s", user));
            }
            catch (SQLException e) {
                log.error((Object)String.format("Failed to get AccountBalanceData for user:%s", user), (Throwable)e);
                AccountBalanceData accountBalanceData = super.getAccountBalance(user);
                Object var8_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                return accountBalanceData;
            }
        }
        catch (Throwable throwable) {
            Object var8_13 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GroupMemberData getGroupMember(UserObject user, int groupID) throws DAOException {
        GroupMemberData groupMemberData;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            String sql = "select m.*, u.displaypicture, sub.id vipsubscriptionid from groupmember m inner join user u on m.username=u.username inner join groups g on m.groupid=g.id left outer join service on (g.vipserviceid=service.id and service.status=?) left outer join subscription sub on (sub.serviceid=service.id and sub.status=? and sub.username=u.username) where m.username=? and m.groupid=? and m.status in (?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ServiceData.StatusEnum.ACTIVE.value());
            ps.setInt(2, SubscriptionData.StatusEnum.ACTIVE.value());
            ps.setString(3, user.getUsername());
            ps.setInt(4, groupID);
            ps.setInt(5, GroupMemberData.StatusEnum.ACTIVE.value());
            ps.setInt(6, GroupMemberData.StatusEnum.BANNED.value());
            rs = ps.executeQuery();
            if (rs.next()) break block5;
            GroupMemberData groupMemberData2 = null;
            Object var10_11 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return groupMemberData2;
        }
        try {
            GroupMemberData memberData = new GroupMemberData(rs);
            memberData.displayPicture = rs.getString("displayPicture");
            groupMemberData = memberData;
            Object var10_12 = null;
        }
        catch (SQLException e) {
            GroupMemberData groupMemberData3;
            try {
                log.error((Object)String.format("Failed to get GroupMemberData for user:%s, group:%s", user, groupID), (Throwable)e);
                groupMemberData3 = super.getGroupMember(user, groupID);
                Object var10_13 = null;
            }
            catch (Throwable throwable) {
                Object var10_14 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return groupMemberData3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return groupMemberData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isUserBlackListedInGroup(UserObject user, int groupId) throws DAOException {
        boolean bl;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block5: {
            conn = null;
            ps = null;
            rs = null;
            conn = DBUtils.getFusionReadConnection();
            ps = conn.prepareStatement("select * from groupblacklist where groupid = ? and username = ?");
            ps.setInt(1, groupId);
            ps.setString(2, user.getUsername());
            rs = ps.executeQuery();
            if (!rs.next()) break block5;
            boolean bl2 = true;
            Object var9_9 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            return bl2;
        }
        try {
            bl = false;
            Object var9_10 = null;
        }
        catch (SQLException e) {
            boolean bl3;
            try {
                log.error((Object)String.format("Failed to check isUserBlackListedInGroup for user:%s, group:%s", user, groupId), (Throwable)e);
                bl3 = super.isUserBlackListedInGroup(user, groupId);
                Object var9_11 = null;
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                DBUtils.closeResource(rs, ps, conn, log);
                throw throwable;
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return bl3;
        }
        DBUtils.closeResource(rs, ps, conn, log);
        return bl;
    }

    @Override
    public void setAlias(UserObject user, String alias) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            try {
                conn = DBUtils.getFusionWriteConnection();
                ps = conn.prepareStatement("INSERT IGNORE INTO useralias (username, alias, dateupdated) values (?, ?, now())");
                ps.setString(1, user.getUsername());
                ps.setString(2, alias);
                if (ps.executeUpdate() < 1) {
                    throw new DAOException(String.format("Failed to set alias:%s to user:%s, Due to either duplicate alias or user already has an alias", alias, user));
                }
                super.setAlias(user, alias);
            }
            catch (SQLException e) {
                log.error((Object)String.format("Failed to set alias:%s to user:%s", alias, user), (Throwable)e);
                throw new DAOException(String.format("Failed to set alias:%s to user:%s", alias, user), e);
            }
            Object var8_6 = null;
        }
        catch (Throwable throwable) {
            Object var8_7 = null;
            DBUtils.closeResource(rs, ps, conn, log);
            throw throwable;
        }
        DBUtils.closeResource(rs, ps, conn, log);
    }
}

