/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  com.danga.MemCached.MemCachedClient
 *  com.projectgoth.leto.common.event.CampaignParticipation
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.lang.StringUtils
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.keyczar.Crypter
 *  org.keyczar.exceptions.KeyczarException
 *  org.springframework.util.StringUtils
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Pipeline
 *  redis.clients.jedis.Response
 *  redis.clients.jedis.exceptions.JedisException
 */
package com.projectgoth.fusion.ejb;

import Ice.LocalException;
import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlParameter;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.cache.BroadcastList;
import com.projectgoth.fusion.cache.BroadcastListPersisted;
import com.projectgoth.fusion.cache.GiftsReceivedCounter;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.cache.SurgeMail;
import com.projectgoth.fusion.cache.UserReferrerCache;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DataUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import com.projectgoth.fusion.common.EmailUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedHelper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.NotificationUtils;
import com.projectgoth.fusion.common.PasswordUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.URLUtil;
import com.projectgoth.fusion.common.UsernameUtils;
import com.projectgoth.fusion.common.UsernameValidationException;
import com.projectgoth.fusion.common.WebCommon;
import com.projectgoth.fusion.common.jdbc.ConnectionCreator;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.data.CampaignData;
import com.projectgoth.fusion.data.CampaignParticipantData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.DisplayPictureAndStatusMessage;
import com.projectgoth.fusion.data.FullMerchantTagDetailsData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.MerchantDetailsData;
import com.projectgoth.fusion.data.MerchantTagData;
import com.projectgoth.fusion.data.RegistrationContextData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.SecurityQuestion;
import com.projectgoth.fusion.data.ServiceData;
import com.projectgoth.fusion.data.SubscriptionData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.data.ThirdPartyApplicationData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserDataAndRegistrationContextData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.UserProfileLabelsData;
import com.projectgoth.fusion.data.UserRegistrationContextData;
import com.projectgoth.fusion.data.UserReputationScoreAndLevelData;
import com.projectgoth.fusion.data.UserReputationScoreData;
import com.projectgoth.fusion.data.UserSettingData;
import com.projectgoth.fusion.data.ValidateCredentialResult;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.ejb.ConnectionHolder;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.eventqueue.Event;
import com.projectgoth.fusion.eventqueue.EventQueue;
import com.projectgoth.fusion.eventqueue.events.StatusUpdateEvent;
import com.projectgoth.fusion.eventqueue.events.ThirdPartySiteCredentialUpdatedEvent;
import com.projectgoth.fusion.eventqueue.events.UserDataUpdatedEvent;
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.ContactLocal;
import com.projectgoth.fusion.interfaces.ContactLocalHome;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.VoucherLocal;
import com.projectgoth.fusion.interfaces.VoucherLocalHome;
import com.projectgoth.fusion.invitation.CreateInvitationsResult;
import com.projectgoth.fusion.invitation.InvitationData;
import com.projectgoth.fusion.invitation.InvitationMetadata;
import com.projectgoth.fusion.invitation.InvitationResponseData;
import com.projectgoth.fusion.invitation.InvitationStatusEnum;
import com.projectgoth.fusion.invitation.InvitationUtils;
import com.projectgoth.fusion.invitation.restapi.data.InvitationDetailsData;
import com.projectgoth.fusion.invitation.restapi.data.SendingInvitationData;
import com.projectgoth.fusion.leaderboard.Leaderboard;
import com.projectgoth.fusion.reputation.ReputationLevelScoreRanges;
import com.projectgoth.fusion.restapi.data.RegistrationTokenData;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.data.UserActivationData;
import com.projectgoth.fusion.restapi.data.UserMigboProfileData;
import com.projectgoth.fusion.restapi.data.UserVerificationData;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import com.projectgoth.fusion.restapi.enums.RegistrationType;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.triggers.ConsecutiveLoginTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ExternalEmailVerifiedTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.InvitationRespondedTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.LastLoginTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.UserFirstAuthenticatedTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.UserReferralActivationTrigger;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.FusionBusinessException;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.smsengine.SMSControl;
import com.projectgoth.fusion.uns.UserNotificationServiceI;
import com.projectgoth.leto.common.event.CampaignParticipation;
import java.io.Serializable;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UserBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserBean.class));
    private static final Integer UNKNOWN_EMAIL_ADDRESS = -1;
    private static final Integer HALFWAY_REGISTERED_EMAIL_ADDRESS = 0;
    private static MemCachedClient broadcastListMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.broadcastList);
    private static MemCachedClient bclPersistedMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.bclPersisted);
    private static MemCachedClient displayPictureAndStatusMessageMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.userDisplayPictureAndStatus);
    private static MemCachedClient surgeMailMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.surgeMail);
    private static final DateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int INITIAL_SCORE = 0;
    private static final int INITIAL_LEVEL = 1;
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private DataSource userRegistrationMaster;
    private DataSource userRegistrationSlave;
    private SecureRandom randomGen;
    private SessionContext context;
    private static final Pattern USERALIAS_PATTERN = Pattern.compile("^[a-zA-Z](\\.?[\\w-])+$");

    public void setSessionContext(SessionContext newContext) throws EJBException {
        this.context = newContext;
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws CreateException {
        try {
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            this.userRegistrationMaster = LookupUtil.getRegistrationMasterDataSource();
            this.userRegistrationSlave = LookupUtil.getRegistrationSlaveDataSource();
            this.randomGen = new SecureRandom();
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Exception e) {
            log.error((Object)"Unable to create User EJB", (Throwable)e);
            throw new CreateException("Unable to create User EJB: " + e.getMessage());
        }
    }

    private void checkUserAlias(String alias, boolean sameAsUsername) throws FusionEJBException {
        if (StringUtil.isBlank(alias)) {
            throw new FusionEJBException("Empty alias");
        }
        if (alias.length() < (sameAsUsername ? 3 : 6) || !USERALIAS_PATTERN.matcher(alias).matches()) {
            throw new FusionEJBException("The alias must start with a letter, and contain at least 6 letters, numbers, periods (.), hyphens (-), or underscores (_)");
        }
        int maxUserAliasLength = SystemProperty.getInt("MaxUserAliasLength", 20);
        if (alias.length() > maxUserAliasLength) {
            throw new FusionEJBException("The alias must not contain more than " + maxUserAliasLength + " characters");
        }
        if (!sameAsUsername) {
            String[] bannedUsernames;
            for (String bannedUsername : bannedUsernames = SystemProperty.get("BannedUsernames", "").split(";")) {
                if (alias.indexOf(bannedUsername) == -1) continue;
                throw new FusionEJBException("You cannot use " + bannedUsername + " in the alias");
            }
        }
    }

    public void checkUsername(String username) throws EJBException, NoSuchFieldException {
        try {
            String normalizedUsername = UsernameUtils.validateUsernameCharacters(username, false);
            UsernameUtils.validateAgainstBannedWords(normalizedUsername, SystemProperty.get("BannedUsernames").split(";"));
        }
        catch (UsernameValidationException e) {
            throw new EJBExceptionWithErrorCause(e.getErrorCause(), e.getErrorMsgArgs());
        }
    }

    private void checkPassword(String username, String password) throws EJBException, NoSuchFieldException {
        ValidateCredentialResult result = PasswordUtils.validatePassword(username, password);
        if (null == result) {
            throw new EJBException("Unable to validate username/password");
        }
        if (!result.valid) {
            throw new EJBException(result.reason);
        }
    }

    private String newVerificationCode() {
        return String.format("%1$05d", this.randomGen.nextInt(90000) + 10000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addUserToDB(Connection conn, UserData userData, UserRegistrationContextData userRegContextData, AccountEntrySourceData accountEntrySourceData) throws SQLException {
        Statement ps = null;
        try {
            RegistrationContextData regContextData;
            Map<Integer, String> regContextValues;
            userData.dateRegistered = SystemProperty.getBool(SystemPropertyEntities.Registration.ALLOW_OVERRIDING_USER_REGISTRATION_DATE_FOR_UNIT_TESTING) ? (userData.dateRegistered != null ? userData.dateRegistered : new Date()) : new Date();
            userData.statusMessage = null;
            userData.statusTimeStamp = userData.dateRegistered;
            userData.chatRoomAdmin = false;
            userData.chatRoomBans = 0;
            userData.firstLoginDate = null;
            userData.lastLoginDate = null;
            userData.failedLoginAttempts = 0;
            userData.failedActivationAttempts = 0;
            userData.mobileVerified = false;
            userData.verificationCode = this.newVerificationCode();
            userData.emailActivated = false;
            userData.emailAlert = false;
            userData.emailAlertSent = false;
            userData.allowBuzz = true;
            userData.emailActivationDate = null;
            userData.bonusProgramID = null;
            userData.balance = 0.0;
            userData.fundedBalance = 0.0;
            userData.status = UserData.StatusEnum.ACTIVE;
            String statement = "insert into user ";
            statement = statement + "(username, dateregistered, password, displayname, displaypicture, statusmessage, statustimestamp, countryid, language, emailaddress, onmailinglist, chatroomadmin, chatroombans, registrationipaddress, registrationdevice, firstlogindate, lastlogindate, failedloginattempts, failedactivationattempts, mobilephone, mobiledevice, useragent, mobileverified, verificationcode, emailActivated, emailAlert, emailAlertSent, emailActivationDate, allowBuzz, utcoffset, type, affiliateid, merchantCreated, referredby, bonusprogramid, currency, balance, notes, status) ";
            statement = statement + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(statement);
            ps.setString(1, userData.username);
            ps.setTimestamp(2, new Timestamp(userData.dateRegistered.getTime()));
            if (!SystemProperty.getBool(SystemPropertyEntities.Temp.PT73368964_ENABLED)) {
                ps.setString(3, userData.password);
            } else {
                ps.setString(3, "");
            }
            ps.setString(4, userData.displayName);
            ps.setString(5, userData.displayPicture);
            ps.setString(6, userData.statusMessage);
            ps.setTimestamp(7, new Timestamp(userData.statusTimeStamp.getTime()));
            ps.setObject(8, userData.countryID);
            ps.setString(9, userData.language);
            ps.setString(10, userData.emailAddress);
            ps.setObject(11, userData.onMailingList == null ? null : Integer.valueOf(userData.onMailingList != false ? 1 : 0));
            ps.setObject(12, userData.chatRoomAdmin == null ? null : Integer.valueOf(userData.chatRoomAdmin != false ? 1 : 0));
            ps.setObject(13, userData.chatRoomBans);
            ps.setString(14, userData.registrationIPAddress);
            ps.setString(15, userData.registrationDevice);
            ps.setTimestamp(16, userData.firstLoginDate == null ? null : new Timestamp(userData.firstLoginDate.getTime()));
            ps.setTimestamp(17, userData.lastLoginDate == null ? null : new Timestamp(userData.lastLoginDate.getTime()));
            ps.setObject(18, userData.failedLoginAttempts);
            ps.setObject(19, userData.failedActivationAttempts);
            ps.setString(20, userData.mobilePhone);
            ps.setString(21, DataUtils.truncateMobileDevice(userData.mobileDevice, true, String.format("in create new user '%s'", userData.username)));
            ps.setString(22, DataUtils.truncateUserAgent(userData.userAgent, true, String.format("in create new user '%s'", userData.username)));
            ps.setObject(23, userData.mobileVerified == null ? null : Integer.valueOf(userData.mobileVerified != false ? 1 : 0));
            ps.setString(24, userData.verificationCode);
            ps.setObject(25, userData.emailActivated == null ? null : Integer.valueOf(userData.emailActivated != false ? 1 : 0));
            ps.setObject(26, userData.emailAlert == null ? null : Integer.valueOf(userData.emailAlert != false ? 1 : 0));
            ps.setObject(27, userData.emailAlertSent == null ? null : Integer.valueOf(userData.emailAlertSent != false ? 1 : 0));
            ps.setTimestamp(28, userData.emailActivationDate == null ? null : new Timestamp(userData.emailActivationDate.getTime()));
            ps.setObject(29, userData.allowBuzz == null ? null : Integer.valueOf(userData.allowBuzz != false ? 1 : 0));
            ps.setObject(30, userData.UTCOffset);
            ps.setObject(31, userData.type == null ? null : Integer.valueOf(userData.type.value()));
            ps.setObject(32, userData.affiliateID);
            ps.setString(33, userData.merchantCreated);
            ps.setString(34, userData.referredBy);
            ps.setObject(35, userData.bonusProgramID);
            ps.setString(36, userData.currency);
            ps.setObject(37, userData.balance);
            ps.setString(38, userData.notes);
            ps.setObject(39, userData.status == null ? null : Integer.valueOf(userData.status.value()));
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to add a new user '" + userData.username + "' to database");
            }
            ps.close();
            ps = conn.prepareStatement("insert into userid (username) values (?)", 1);
            ps.setString(1, userData.username);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Unable to create user account");
            }
            userData.userID = rs.getInt(1);
            rs.close();
            ps.close();
            ps = conn.prepareStatement("insert into registrationdevice (userid, mobiledevice, useragent, imei) values (?,?,?,?)");
            ps.setInt(1, userData.userID);
            ps.setString(2, accountEntrySourceData.mobileDevice);
            ps.setString(3, accountEntrySourceData.userAgent);
            ps.setString(4, accountEntrySourceData.imei);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to record registration device");
            }
            ps.close();
            if (SystemProperty.getBool(SystemPropertyEntities.Registration.REGISTRATION_CONTEXT_ENABLED) && !(regContextValues = (regContextData = new RegistrationContextData(userData, userRegContextData, accountEntrySourceData)).toIntegerAndStringMap()).isEmpty()) {
                ps = conn.prepareStatement("insert into registrationcontext (userid, type, value) values (?,?,?)");
                for (Map.Entry<Integer, String> entry : regContextValues.entrySet()) {
                    ps.setInt(1, regContextData.userid);
                    ps.setInt(2, entry.getKey());
                    ps.setString(3, entry.getValue());
                    ps.addBatch();
                }
                int[] batchResults = ps.executeBatch();
                if (batchResults == null || batchResults.length != regContextValues.size()) {
                    throw new SQLException("Unable to record registration context");
                }
                for (int batchResult : batchResults) {
                    if (batchResult == 1) continue;
                    throw new SQLException("Unable to record registration context");
                }
                ps.close();
            }
            if (!StringUtil.isBlank(userData.emailAddress)) {
                statement = "INSERT INTO useremailaddress(userid, emailaddress, type) VALUES (?,?,?)";
                if (userRegContextData.verified) {
                    statement = "INSERT INTO useremailaddress(userid, emailaddress, type, verified, dateverified) VALUES (?,?,?, 1, NOW())";
                }
                ps = conn.prepareStatement(statement);
                ps.setInt(1, userData.userID);
                ps.setString(2, userData.emailAddress);
                ps.setInt(3, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Unable to record email address");
                }
                userData.emailVerified = true;
            }
            Object var16_15 = null;
        }
        catch (Throwable throwable) {
            Object var16_16 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int addUserToUserRegistrationTable(Connection conn, UserData userData, UserRegistrationContextData userRegContextData, AccountEntrySourceData accountEntrySourceData) throws SQLException {
        int n;
        Statement ps = null;
        ResultSet rs = null;
        try {
            userData.dateRegistered = new Date();
            userData.failedActivationAttempts = 0;
            userData.emailActivationDate = null;
            RegistrationContextData regContextData = new RegistrationContextData(userData, userRegContextData, accountEntrySourceData);
            JSONObject contextJson = null;
            try {
                contextJson = regContextData.toJSONObject();
            }
            catch (JSONException e) {
                // empty catch block
            }
            String statement = "insert into userRegistration (username, password, countryid, language, emailaddress, registrationipaddress, registrationdevice,  useragent, type, referredby, currency, registrationType,mobiledevice,imei,context) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(statement, 1);
            ps.setString(1, userData.username);
            ps.setString(2, userData.password);
            ps.setObject(3, userData.countryID);
            ps.setString(4, userData.language);
            ps.setString(5, userData.emailAddress);
            ps.setString(6, userData.registrationIPAddress);
            ps.setString(7, userData.registrationDevice);
            ps.setString(8, DataUtils.truncateUserAgent(accountEntrySourceData.userAgent, true, String.format("in create new user '%s'", userData.username)));
            ps.setObject(9, userData.type == null ? null : Integer.valueOf(userData.type.value()));
            ps.setString(10, userData.referredBy);
            ps.setString(11, userData.currency);
            ps.setString(12, userRegContextData.registrationType.value());
            ps.setString(13, accountEntrySourceData.mobileDevice);
            ps.setString(14, accountEntrySourceData.imei);
            if (contextJson == null || contextJson.length() < 1) {
                ps.setNull(15, 12);
            } else {
                ps.setString(15, contextJson.toString());
            }
            if (ps.executeUpdate() != 1) {
                log.error((Object)("Unable to add new user [" + userData.username + "] to the userregistration table"));
                throw new SQLException("Unable to add new user. Please try again later");
            }
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Failed to add a new user '" + userData.username + "' to userRegistration table");
            }
            n = rs.getInt(1);
            Object var12_12 = null;
        }
        catch (Throwable throwable) {
            Object var12_13 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            throw throwable;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateUserRegistrationWithToken(Connection conn, int id, String token) throws SQLException {
        PreparedStatement ps = null;
        try {
            String statement = "update userRegistration set verificationToken=? where id=?";
            ps = conn.prepareStatement(statement);
            ps.setString(1, token);
            ps.setInt(2, id);
            if (ps.executeUpdate() != 1) {
                log.error((Object)("Unable to updated userregistration table for id [" + id + "] with token [" + token + "]"));
                throw new SQLException("Unable to create user. Please try again later");
            }
            Object var7_6 = null;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
    }

    private boolean isEmailRegistrationV2Enabled() {
        return SystemProperty.getBool(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_PATH1_ENABLED) || SystemProperty.getBool(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_PATH2_ENABLED);
    }

    /*
     * Loose catch block
     */
    public UserDataAndRegistrationContextData getUserDataFromUserRegistrationTable(String username, UserData oldUserData) throws EJBException {
        if (!this.isEmailRegistrationV2Enabled()) {
            throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
        }
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        conn = this.userRegistrationSlave.getConnection();
        ps = conn.prepareStatement("SELECT * FROM userregistration WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMax() + ",now()) AND username=?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Failed to find user in registration table: " + username);
        }
        UserData userData = new UserData(oldUserData.toIceObject());
        userData.username = rs.getString("username");
        userData.password = rs.getString("password");
        userData.countryID = rs.getInt("countryid");
        userData.language = rs.getString("language");
        userData.emailAddress = rs.getString("emailaddress");
        userData.registrationIPAddress = rs.getString("registrationipaddress");
        userData.registrationDevice = rs.getString("registrationdevice");
        userData.userAgent = rs.getString("useragent");
        userData.UTCOffset = rs.getDouble("utcoffset");
        userData.type = UserData.TypeEnum.fromValue(rs.getInt("type"));
        userData.referredBy = rs.getString("referredby");
        userData.referralLevel = rs.getInt("referrallevel");
        userData.currency = rs.getString("currency");
        userData.mobileDevice = rs.getString("mobiledevice");
        String contextJson = rs.getString("context");
        RegistrationContextData regContextData = null;
        try {
            regContextData = new RegistrationContextData(contextJson);
        }
        catch (JSONException e) {
            log.error((Object)String.format("Unable to parse json string of userregistration.context column for '%s', e: %s", new Object[]{contextJson, e}));
        }
        UserDataAndRegistrationContextData userDataAndRegistrationContextData = new UserDataAndRegistrationContextData(userData, regContextData);
        Object var11_12 = null;
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return userDataAndRegistrationContextData;
        catch (SQLException e) {
            try {
                log.error((Object)("Unable to get userregistration data from table: " + e.getMessage()));
                throw new EJBException("Unable to get user registration data");
            }
            catch (Throwable throwable) {
                Object var11_13 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public UserActivationData getVerificationDataFromUserRegistrationTable(String username) throws EJBException {
        if (!this.isEmailRegistrationV2Enabled()) {
            throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
        }
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        conn = this.userRegistrationSlave.getConnection();
        ps = conn.prepareStatement("SELECT * FROM userregistration WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMax() + ",now()) AND username=?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Failed to find user in registration table: " + username);
        }
        UserActivationData userActivationData = new UserActivationData();
        userActivationData.emailAddress = rs.getString("emailaddress");
        userActivationData.token = rs.getString("verificationtoken");
        userActivationData.registrationType = RegistrationType.fromValue(rs.getString("registrationtype"));
        UserActivationData userActivationData2 = userActivationData;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return userActivationData2;
        catch (SQLException e) {
            try {
                log.error((Object)("Unable to get userregistration data from table: " + e.getMessage()));
                throw new EJBException("Unable to get user registration data");
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deleteUserFromRegistrationDB(Connection conn, int userRegistrationId) throws SQLException {
        PreparedStatement ps = null;
        try {
            if (userRegistrationId > 0) {
                ps = conn.prepareStatement("delete from userregistration where id = ?");
                ps.setInt(1, userRegistrationId);
                if (ps.executeUpdate() != 1) {
                    log.info((Object)String.format("Failed to delete userRegistrationId:%s from regdb.userregistration table, we might not have the record yet, check \"select * from userregistration where id = %s\" for more details, ingoring...", userRegistrationId, userRegistrationId));
                }
            }
            Object var5_4 = null;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deleteUserFromFusionDB(Connection conn, UserData userData) throws SQLException {
        PreparedStatement ps = null;
        try {
            if (userData.userID != null) {
                ps = conn.prepareStatement("delete ignore from useremailaddress where userid = ?");
                ps.setInt(1, userData.userID);
                ps.executeUpdate();
                ps.close();
                ps = conn.prepareStatement("delete from registrationdevice where userid = ?");
                ps.setInt(1, userData.userID);
                ps.executeUpdate();
                ps.close();
                if (SystemProperty.getBool(SystemPropertyEntities.Registration.REGISTRATION_CONTEXT_ENABLED)) {
                    ps = conn.prepareStatement("delete from registrationcontext where userid = ?");
                    ps.setInt(1, userData.userID);
                    ps.executeUpdate();
                    ps.close();
                }
                ps = conn.prepareStatement("delete ignore from credential where userID = ?");
                ps.setInt(1, userData.userID);
                ps.executeUpdate();
                ps.close();
                ps = conn.prepareStatement("delete from userid where username = ?");
                ps.setString(1, userData.username);
                if (ps.executeUpdate() != 1) {
                    log.info((Object)String.format("Failed to delete user:%s from userid table, we might not have the record yet, check \"select * from userid where username = %s\" for more details, ingoring...", userData.username, userData.username));
                }
                ps.close();
            }
            ps = conn.prepareStatement("delete from userprofile where username = ?");
            ps.setString(1, userData.username);
            if (ps.executeUpdate() != 1) {
                log.info((Object)String.format("Failed to delete user:%s from userprofile table, we might not have the record yet, check \"select * from userprofile where username = %s\" for more details, ingoring...", userData.username, userData.username));
            }
            ps.close();
            ps = conn.prepareStatement("delete from user where username = ?");
            ps.setString(1, userData.username);
            if (ps.executeUpdate() != 1) {
                log.info((Object)String.format("Failed to delete user:%s from user table, we might not have the record yet, check \"select * from user where user = %s\" for more details, ingoring...", userData.username, userData.username));
            }
            Object var5_4 = null;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
    }

    /*
     * Loose catch block
     */
    private int getMobilePhoneCount(Connection conn, String mobilePhone) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        block21: {
            ps = null;
            rs = null;
            ps = conn.prepareStatement("select count(*) from user where mobilephone=?");
            ps.setString(1, mobilePhone);
            rs = ps.executeQuery();
            if (!rs.next()) {
                log.error((Object)("Unable to determine mobile phone count for " + mobilePhone));
                throw new SQLException("Unable to determine mobile phone count");
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("====== UserBean.GETMOBILEPHONECOUNT: Number: " + mobilePhone + ", MOBILE count: " + rs.getInt(1)));
            }
            if (rs.getInt(1) <= 0) break block21;
            int n = rs.getInt(1);
            Object var7_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            return n;
        }
        int n = this.getActivatedNumberCount(conn, mobilePhone);
        Object var7_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        return n;
        catch (Exception e) {
            try {
                throw new SQLException("Unable to determine mobile phone count");
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    private int getActivatedNumberCount(Connection conn, String mobilePhone) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = conn.prepareStatement("select count(*) from activation where mobilephone=? and datecreated >=date_sub(curdate(), interval ? day)");
        ps.setString(1, mobilePhone);
        ps.setInt(2, SystemProperty.getInt("ActivationDisabledInterval"));
        rs = ps.executeQuery();
        if (!rs.next()) {
            log.error((Object)("Unable to determine mobile phone count for " + mobilePhone));
            throw new SQLException("Unable to determine mobile phone count");
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("====== UserBean.GETMOBILEPHONECOUNT: Number: " + mobilePhone + ", ACTIVATION count: " + rs.getInt(1)));
        }
        int n = rs.getInt(1);
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        return n;
        catch (Exception e) {
            try {
                throw new SQLException("Unable to determine mobile phone count");
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public UserData createPrepaidCardUser(String didNumber, String voucherNumber, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        VoucherLocal voucherEJB = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
        VoucherData voucherData = voucherEJB.getVoucher(voucherNumber);
        if (voucherData == null || voucherData.status != VoucherData.StatusEnum.ACTIVE) {
            throw new EJBException("Invalid voucher number " + voucherNumber);
        }
        MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
        Integer iddCode = messageEJB.getIDDCode(didNumber);
        if (iddCode == null) {
            throw new EJBException("Unable to determine IDD code from DID number " + didNumber);
        }
        MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
        CountryData countryData = misEJB.getCountryByIDDCode(iddCode, didNumber);
        if (countryData == null) {
            throw new EJBException("Unable to determine country from IDD code " + iddCode);
        }
        UserData userData = new UserData();
        userData.type = UserData.TypeEnum.MIG33_PREPAID_CARD;
        userData.username = userData.type.toString().toLowerCase() + "_" + voucherNumber;
        userData.password = this.generatePassword();
        userData.countryID = countryData.id;
        userData.currency = countryData.currency;
        userData.language = "ENG";
        conn = this.dataSourceMaster.getConnection();
        this.addUserToDB(conn, userData, new UserRegistrationContextData(null, false, RegistrationType.MOBILE_REGISTRATION), accountEntrySourceData);
        voucherEJB.redeemVoucher(userData.username, voucherNumber, accountEntrySourceData);
        UserData userData2 = userData;
        Object var16_17 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        {
            return userData2;
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            catch (CreateException e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var16_18 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
    }

    /*
     * Loose catch block
     */
    public Map<String, Integer> getRecentMobileActivationCountsWithMobilePrefixByIPAddress(String mobilePrefix, int recentActivationsToCheck) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        if (log.isDebugEnabled()) {
            log.debug((Object)("getRecentMobileActivationCountsWithMobilePrefixByIPAddress called with mobilePrefix[" + mobilePrefix + "] recentActivationsToCheck[" + recentActivationsToCheck + "]"));
        }
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select max(id) as maxid from activation");
        rs = ps.executeQuery();
        int maxActivationID = 0;
        if (!rs.next()) {
            throw new EJBException("Unable to determine activation max ID");
        }
        maxActivationID = rs.getInt("maxid");
        rs.close();
        ps.close();
        ps = conn.prepareStatement("select count(*) as numActivations, IPAddress from activation where id > ? and substring(mobilephone,1,?) = ? group by ipaddress");
        ps.setInt(1, Math.max(0, maxActivationID - recentActivationsToCheck));
        ps.setInt(2, mobilePrefix.length());
        ps.setString(3, mobilePrefix);
        rs = ps.executeQuery();
        while (rs.next()) {
            String IPAddress = rs.getString("IPAddress");
            int numActivations = rs.getInt("numActivations");
            result.put(IPAddress, numActivations);
        }
        HashMap<String, Integer> hashMap = result;
        Object var11_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return hashMap;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public boolean checkRecentCommonPrefixActivations(Integer iddCode, String mobilePhone) {
        boolean result = true;
        try {
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            int minimumLengthForMobilePhone = messageEJB.getMinimumMobileNumberLength(iddCode);
            if (minimumLengthForMobilePhone == -1) {
                log.warn((Object)String.format("Unable to determine minimum length of mobile number iddCode[%d] mobilephone[%s] - check skipped", iddCode, mobilePhone));
            } else {
                int maxCommonPrefixActivationsAllowed = SystemProperty.getInt(SystemPropertyEntities.Registration.MAX_COMMON_PREFIX_RECENT_ACTIVATIONS_ALLOWED);
                int recentActivationsToCheck = SystemProperty.getInt(SystemPropertyEntities.Registration.NUMBER_OF_RECENT_ACTIVATIONS_FOR_COMMON_PREFIX_CHECK);
                String commonPrefix = mobilePhone.substring(0, minimumLengthForMobilePhone);
                Map<String, Integer> commonPrefixActivations = this.getRecentMobileActivationCountsWithMobilePrefixByIPAddress(commonPrefix, recentActivationsToCheck);
                if (commonPrefixActivations.size() == 1) {
                    String IPAddress = commonPrefixActivations.keySet().iterator().next();
                    Integer commonPrefixActivationsFromSingleIPAddress = commonPrefixActivations.values().iterator().next();
                    String[] ipWhitelistPrefixes = SystemProperty.getArray(SystemPropertyEntities.Registration.IPWHITELIST_PREFIX_FOR_COMMON_PREFIX_CHECK);
                    boolean ipWhitelisted = false;
                    for (String ipWhitelistPrefix : ipWhitelistPrefixes) {
                        if (!IPAddress.startsWith(ipWhitelistPrefix)) continue;
                        ipWhitelisted = true;
                        break;
                    }
                    if (!ipWhitelisted && commonPrefixActivationsFromSingleIPAddress >= maxCommonPrefixActivationsAllowed) {
                        log.warn((Object)String.format("getMinimumMobileNumberLength failed - multy-ID suspected mobilenumber [%s] IPAddress [%s] commonPrefixActivations[%d] maxCommonPrefixActivationsAllowed[%d]", mobilePhone, IPAddress, (int)commonPrefixActivationsFromSingleIPAddress, maxCommonPrefixActivationsAllowed));
                        result = false;
                    }
                }
                log.info((Object)String.format("checkRecentCommonPrefixActivations result[%s] setsize[%d] IPAddresses[%s] mobilenumber [%s]  commonPrefix[%s] maxCommonPrefixActivationsAllowed[%d] recentActivationsToCheck[%d]", result ? "PASS" : "BLOCKED", commonPrefixActivations.size(), commonPrefixActivations, mobilePhone, commonPrefix, maxCommonPrefixActivationsAllowed, recentActivationsToCheck));
            }
        }
        catch (Exception e) {
            log.warn((Object)("Unexpected exception while checking common prefix activations for mobile registration - check skipped: " + e.getMessage()), (Throwable)e);
        }
        return result;
    }

    public UserData createUser(UserData userData, UserProfileData userProfileData, boolean sendVerificationCode, UserRegistrationContextData regContextData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        return this.createUser(userData, userProfileData, sendVerificationCode, regContextData, accountEntrySourceData, true, true, true);
    }

    /*
     * Loose catch block
     */
    public UserData createUser(UserData userData, UserProfileData userProfileData, boolean sendVerificationCode, UserRegistrationContextData regContextData, AccountEntrySourceData accountEntrySourceData, boolean performUsernameCharValidation, boolean performEmailRegRateLimitCheck, boolean performSendEmailVerificationToken) throws EJBException {
        int idUserRegistration;
        boolean userAddedToRegistrationDB;
        boolean userAddedToDB;
        ResultSet rs;
        Statement ps;
        Connection connRegistrationDB;
        Connection connFusion;
        block78: {
            Object mobilePhone;
            CountryData countryData;
            connFusion = null;
            connRegistrationDB = null;
            ps = null;
            rs = null;
            userAddedToDB = false;
            userAddedToRegistrationDB = false;
            idUserRegistration = -1;
            if (SystemProperty.getBool(SystemPropertyEntities.Registration.REGISTRATION_DISABLED)) {
                throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
            }
            if (!SystemProperty.getBool(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_PATH1_ENABLED) && regContextData.registrationType == RegistrationType.EMAIL_REGISTRATION_PATH1) {
                throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
            }
            if (!SystemProperty.getBool(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_PATH2_ENABLED) && regContextData.registrationType == RegistrationType.EMAIL_REGISTRATION_PATH2) {
                throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
            }
            if (StringUtil.isBlank(userData.mobilePhone) && StringUtil.isBlank(userData.emailAddress)) {
                throw new EJBException("Mobile number or email address is mandatory for registration");
            }
            if (regContextData.isEmailBased()) {
                EmailUtils.EmailValidatationResult evr = EmailUtils.externalEmailIsValid(userData.emailAddress);
                if (SystemProperty.getBool(SystemPropertyEntities.Registration.STRIP_PERIODS_FROM_GMAIL_ADDRESS_ENABLED) && evr.result == EmailUtils.EmailValidatationEnum.PERIODS_EXCEED_IN_GMAIL) {
                    userData.emailAddress = EmailUtils.stripPeriodsFromGmailAddress(userData.emailAddress);
                } else if (evr.result != EmailUtils.EmailValidatationEnum.VALID) {
                    throw new EJBException(evr.reason);
                }
            }
            if (regContextData.verified || regContextData.registrationType != RegistrationType.EMAIL_REGISTRATION_PATH2) {
                userData.password = userData.password.trim();
                try {
                    this.checkPassword(userData.username, userData.password);
                }
                catch (NoSuchFieldException e) {
                    throw new EJBException(e.getMessage(), (Exception)e);
                }
            }
            if (regContextData.isEmailBased()) {
                if (performEmailRegRateLimitCheck) {
                    this.checkEmailRegistrationRateLimit(userData.emailAddress, userData.registrationIPAddress, regContextData.verified);
                } else {
                    log.info((Object)("createUser:[" + userData.username + "] performEmailRegRateLimitCheck disabled"));
                }
            } else if (SystemProperty.getBool(SystemPropertyEntities.Registration.MOBILE_REGISTRATION_DISABLED) && regContextData.registrationType == RegistrationType.MOBILE_REGISTRATION) {
                throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.MOBILE_REGISTRATION_DISABLED_MESSAGE));
            }
            if (userData.username != null) {
                userData.username = userData.username.toLowerCase();
            }
            if (performUsernameCharValidation) {
                this.checkUsername(userData.username);
            } else {
                log.info((Object)("createUser:[" + userData.username + "] performUsernameCharValidation disabled."));
            }
            connFusion = this.dataSourceMaster.getConnection();
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            if (!regContextData.verified || regContextData.registrationType == RegistrationType.MOBILE_REGISTRATION) {
                if (this.usernameExists(connFusion, userData.username)) {
                    throw new EJBException("Username has already been registered");
                }
                if (!StringUtil.isBlank(userData.emailAddress) && this.emailAddressExists(connFusion, userData.emailAddress)) {
                    throw new EJBException("The email address you entered is already linked to an existing migme account! Please enter a different email address.");
                }
            }
            try {
                countryData = misEJB.getCountryFromIPNumber(WebCommon.toIPNumber(userData.registrationIPAddress));
            }
            catch (Exception e) {
                countryData = null;
            }
            if (regContextData.isEmailBased()) {
                CountryData userSelectedCountry;
                if (userData.countryID != null && (userSelectedCountry = misEJB.getCountry(userData.countryID)) != null) {
                    countryData = userSelectedCountry;
                }
            } else {
                Integer iddCode;
                userData.mobilePhone = messageEJB.cleanPhoneNumber(userData.mobilePhone);
                try {
                    iddCode = messageEJB.getIDDCode(userData.mobilePhone);
                }
                catch (Exception e) {
                    iddCode = null;
                }
                if (!(iddCode != null && messageEJB.isMobileNumber(userData.mobilePhone, true) || userData.registrationIPAddress == null || countryData == null || countryData.id == 169 || countryData.iddCode.equals(iddCode) || !messageEJB.isMobileNumber((String)(mobilePhone = countryData.iddCode.toString() + userData.mobilePhone), true))) {
                    iddCode = countryData.iddCode;
                    userData.mobilePhone = mobilePhone;
                }
                userData.mobilePhone = messageEJB.cleanAndValidatePhoneNumber(userData.mobilePhone, true);
                countryData = misEJB.getCountryByIDDCode(iddCode, userData.mobilePhone);
                if (countryData == null) {
                    throw new EJBException("Unable to determine the country for mobile phone " + userData.mobilePhone);
                }
                if (userData.mobilePhone != null && this.getMobilePhoneCount(connFusion, userData.mobilePhone) > 0) {
                    throw new EJBException("The mobile number you entered, " + userData.mobilePhone + ",  is already linked to an existing migme account! Please enter another mobile number.");
                }
                if (SystemProperty.getBool(SystemPropertyEntities.Registration.COMMON_PREFIX_CHECK_FOR_MOBILE_REGISTRATION_ENABLED) && !this.checkRecentCommonPrefixActivations(iddCode, userData.mobilePhone)) {
                    log.warn((Object)String.format("Blocking registration for user[%s] mobilenumber[%s] iddCode[%d]", userData.username, userData.mobilePhone, iddCode));
                    throw new EJBException("Unable to register your account right now. Please try again later");
                }
            }
            if ((userData.countryID == null || userData.countryID == 0) && countryData != null) {
                userData.countryID = countryData.id;
            }
            if (userData.currency == null) {
                userData.currency = countryData.currency;
            }
            if (userData.language == null) {
                userData.language = "ENG";
            }
            if (regContextData.verified || regContextData.registrationType != RegistrationType.EMAIL_REGISTRATION_PATH1 && regContextData.registrationType != RegistrationType.EMAIL_REGISTRATION_PATH2) break block78;
            String token = this.generateEmailVerificationToken(userData.emailAddress);
            connRegistrationDB = this.userRegistrationMaster.getConnection();
            idUserRegistration = this.addUserToUserRegistrationTable(connRegistrationDB, userData, regContextData, accountEntrySourceData);
            userAddedToRegistrationDB = true;
            if (token == null) {
                log.error((Object)("Unable to generate email verification token for user: " + userData.username));
                throw new EJBException("Internal server error. Please try again later");
            }
            this.updateUserRegistrationWithToken(connRegistrationDB, idUserRegistration, token);
            this.sendVerificationToken(userData.username, userData.emailAddress, token, regContextData.registrationType, performSendEmailVerificationToken);
            mobilePhone = userData;
            Object var24_27 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connFusion != null) {
                    connFusion.close();
                }
            }
            catch (SQLException e2) {
                connFusion = null;
            }
            try {
                if (connRegistrationDB != null) {
                    connRegistrationDB.close();
                }
            }
            catch (SQLException e2) {
                connRegistrationDB = null;
            }
            return mobilePhone;
        }
        this.addUserToDB(connFusion, userData, regContextData, accountEntrySourceData);
        userAddedToDB = true;
        Credential userCredential = new Credential(userData.userID, userData.username, userData.password, PasswordType.FUSION.value());
        AuthenticationServiceResponseCodeEnum response = EJBIcePrxFinder.getAuthenticationServiceProxy().createCredential(userCredential);
        if (response != AuthenticationServiceResponseCodeEnum.Success) {
            log.error((Object)("Failed to register user [" + userData.username + "] with id [" + userData.userID + "] response " + response));
            throw new Exception("Unable to complete user registration");
        }
        UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
        UserData userData2 = userEJB.postCreateUser(userData, userProfileData, sendVerificationCode, accountEntrySourceData, regContextData, performSendEmailVerificationToken);
        Object var24_28 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connFusion != null) {
                connFusion.close();
            }
        }
        catch (SQLException e2) {
            connFusion = null;
        }
        try {
            if (connRegistrationDB != null) {
                connRegistrationDB.close();
            }
        }
        catch (SQLException e2) {
            connRegistrationDB = null;
        }
        return userData2;
        catch (Exception e) {
            try {
                log.error((Object)("Failed to create user [" + userData.username + "]"), (Throwable)e);
                if (userAddedToDB) {
                    try {
                        this.deleteUserFromFusionDB(connFusion, userData);
                    }
                    catch (Exception ie) {
                        log.error((Object)("failed to delete user from DB: " + userData.username), (Throwable)ie);
                    }
                }
                if (userAddedToRegistrationDB) {
                    try {
                        this.deleteUserFromRegistrationDB(connRegistrationDB, idUserRegistration);
                    }
                    catch (Exception ie) {
                        log.error((Object)("failed to delete user from Registration DB: " + userData.username + " id:" + idUserRegistration), (Throwable)ie);
                    }
                }
                if (e instanceof EJBException) {
                    throw (EJBException)((Object)e);
                }
                throw new EJBException(e.getMessage(), e);
            }
            catch (Throwable throwable) {
                Object var24_29 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connFusion != null) {
                        connFusion.close();
                    }
                }
                catch (SQLException e2) {
                    connFusion = null;
                }
                try {
                    if (connRegistrationDB != null) {
                        connRegistrationDB.close();
                    }
                }
                catch (SQLException e2) {
                    connRegistrationDB = null;
                }
                throw throwable;
            }
        }
    }

    private void checkEmailRegistrationRateLimit(String emailAddress, String registrationIPAddress, boolean isStep2) throws EJBException {
        MemCachedRateLimiter.NameSpace rateLimitNamespace;
        String key;
        String rateLimitPattern = SystemProperty.get(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_IP);
        if (!StringUtil.isBlank(rateLimitPattern)) {
            if (!SystemProperty.isValueInArray(registrationIPAddress, SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_IP_WHITELIST)) {
                try {
                    MemCachedRateLimiter.NameSpace rateLimitNamespace2 = isStep2 ? MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_IP_STEP_2 : MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_IP;
                    MemCachedRateLimiter.hit(rateLimitNamespace2, registrationIPAddress, rateLimitPattern);
                }
                catch (MemCachedRateLimiter.LimitExceeded e) {
                    log.info((Object)String.format("rate limit of email registration exceeded for IP %s: %s", registrationIPAddress, rateLimitPattern));
                    throw new EJBException("You have exceeded the number of registration attempts for this session. Please come back and try again later.");
                }
                catch (MemCachedRateLimiter.FormatError e) {
                    log.error((Object)("Formatting error in rate limiter expression when checking rate limit for email registration by IP: " + e.getMessage()));
                    throw new EJBException("Internal error. Please try again later.");
                }
            } else {
                log.info((Object)String.format("rate limit of email registration IP %s skipped due to whitelisting", registrationIPAddress));
            }
        }
        String emailDomain = "";
        try {
            emailDomain = EmailUtils.getDomain(emailAddress);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        rateLimitPattern = SystemProperty.get(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_DOMAIN);
        if (!StringUtil.isBlank(rateLimitPattern)) {
            if (!SystemProperty.isValueInArray(emailDomain, SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_DOMAIN_WHITELIST)) {
                try {
                    MemCachedRateLimiter.NameSpace rateLimitNamespace3 = isStep2 ? MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_DOMAIN_STEP_2 : MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_DOMAIN;
                    MemCachedRateLimiter.hit(rateLimitNamespace3, emailDomain, rateLimitPattern);
                }
                catch (MemCachedRateLimiter.LimitExceeded e) {
                    log.info((Object)String.format("rate limit of email registration exceeded for domain %s: %s", emailDomain, rateLimitPattern));
                    throw new EJBException("You have exceeded the number of registration attempts for this session. Please come back and try again later.");
                }
                catch (MemCachedRateLimiter.FormatError e) {
                    log.error((Object)String.format("Formatting error in rate limiter expression when checking rate limit for email registration by domain [%s]: %s", emailDomain, e.getMessage()));
                    throw new EJBException("Internal error. Please try again later.");
                }
            } else {
                log.info((Object)String.format("rate limit of email registration domain %s skipped due to whitelisting", emailDomain));
            }
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Registration.SANITIZED_EMAIL_LOCAL_PART_AND_DOMAIN_RATE_LIMIT_ENABLED)) {
            rateLimitPattern = SystemProperty.get(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_SANITIZIED_LOCAL_AND_DOMAIN);
            key = EmailUtils.getSanitizedEmailAddressLocalPart(emailAddress) + "/" + emailDomain;
            if (!StringUtil.isBlank(rateLimitPattern)) {
                try {
                    rateLimitNamespace = isStep2 ? MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_DOMAIN_STEP_2 : MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_DOMAIN;
                    MemCachedRateLimiter.hit(rateLimitNamespace, key, rateLimitPattern);
                }
                catch (MemCachedRateLimiter.LimitExceeded e) {
                    log.info((Object)String.format("rate limit of email registration (sanitized local email part / domain) exceeded for email address [%s] key [%s] : %s", emailAddress, key, rateLimitPattern));
                    throw new EJBException("You have exceeded the number of registration attempts for this session. Please come back and try again later.");
                }
                catch (MemCachedRateLimiter.FormatError e) {
                    log.error((Object)String.format("Formatting error in rate limiter expression when checking rate limit for sanitized email local part and domain [%s]: %s", key, e.getMessage()));
                    throw new EJBException("We are unable to process your request at the moment. Please try again later.");
                }
            }
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Registration.SANITIZED_EMAIL_LOCAL_PART_AND_IP_RATE_LIMIT_ENABLED)) {
            rateLimitPattern = SystemProperty.get(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_SANITIZIED_LOCAL_AND_IP);
            key = EmailUtils.getSanitizedEmailAddressLocalPart(emailAddress) + "/" + registrationIPAddress;
            if (!StringUtil.isBlank(rateLimitPattern)) {
                try {
                    rateLimitNamespace = isStep2 ? MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_AND_IP_STEP_2 : MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_SANITIZED_EMAIL_LOCAL_AND_IP;
                    MemCachedRateLimiter.hit(rateLimitNamespace, key, rateLimitPattern);
                }
                catch (MemCachedRateLimiter.LimitExceeded e) {
                    log.info((Object)String.format("rate limit of email registration (sanitized local email part/ip address) exceeded for email address [%s] key [%s] : %s", emailAddress, key, rateLimitPattern));
                    throw new EJBException("You have exceeded the number of registration attempts for this session. Please come back and try again later.");
                }
                catch (MemCachedRateLimiter.FormatError e) {
                    log.error((Object)String.format("Formatting error in rate limiter expression when checking rate limit for sanitized email local part and domain [%s]: %s", key, e.getMessage()));
                    throw new EJBException("We are unable to process your request at the moment. Please try again later.");
                }
            }
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Registration.NON_WHITELISTED_DOMAINS_TOP_LEVEL_PART_RATE_LIMIT_ENABLED) && !SystemProperty.isValueInArray(emailDomain, SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_DOMAIN_WHITELIST)) {
            rateLimitPattern = SystemProperty.get(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_RATE_LIMIT_BY_NONWHITELISTED_DOMAINS_TOP_LEVEL_PART);
            String topLevelDomain = "";
            try {
                topLevelDomain = EmailUtils.getTopLevelDomain(emailAddress);
            }
            catch (Exception e) {
                throw new EJBException(e.getMessage());
            }
            String key2 = topLevelDomain;
            if (!StringUtil.isBlank(rateLimitPattern)) {
                try {
                    MemCachedRateLimiter.NameSpace rateLimitNamespace4 = isStep2 ? MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_NON_WHITELISTED_DOMAIN_TOP_LEVEL_PART_STEP_2 : MemCachedRateLimiter.NameSpace.EMAIL_REG_RATE_LIMIT_NON_WHITELISTED_DOMAIN_TOP_LEVEL_PART;
                    MemCachedRateLimiter.hit(rateLimitNamespace4, key2, rateLimitPattern);
                }
                catch (MemCachedRateLimiter.LimitExceeded e) {
                    log.info((Object)String.format("rate limit of email registration (non whitelisted domain top level part) exceeded for email address [%s] key [%s] : %s", emailAddress, key2, rateLimitPattern));
                    throw new EJBException("You have exceeded the number of registration attempts for this session. Please come back and try again later.");
                }
                catch (MemCachedRateLimiter.FormatError e) {
                    log.error((Object)String.format("Formatting error in rate limiter expression when checking rate limit for non whitelisted domain top level part [%s]: %s", key2, e.getMessage()));
                    throw new EJBException("We are unable to process your request at the moment. Please try again later.");
                }
            }
        }
    }

    public UserData postCreateUser(UserData userData, UserProfileData userProfileData, boolean sendVerificationCode, AccountEntrySourceData accountEntrySourceData, UserRegistrationContextData userRegContextData) throws EJBException {
        return this.postCreateUser(userData, userProfileData, sendVerificationCode, accountEntrySourceData, userRegContextData, true);
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public UserData postCreateUser(UserData userData, UserProfileData userProfileData, boolean sendVerificationCode, AccountEntrySourceData accountEntrySourceData, UserRegistrationContextData userRegContextData, boolean performSendEmailVerification) throws EJBException {
        Serializable invitationID;
        Connection connMaster = null;
        Statement ps = null;
        ResultSet rs = null;
        connMaster = this.dataSourceMaster.getConnection();
        userProfileData.id = null;
        userProfileData.username = userData.username;
        this.updateUserProfile(userProfileData);
        if (!StringUtil.isBlank(userData.mobilePhone)) {
            ps = connMaster.prepareStatement("select userid.id userid, user.username, user.displayname, user.mobilephone,userreferral.paid paid from userreferral, user, userid  where  userreferral.username = user.username  and user.username = userid.username  and userreferral.mobilephone = ?  order by userreferral.id desc limit 1");
            ps.setString(1, userData.mobilePhone);
            rs = ps.executeQuery();
            if (rs.next()) {
                int referrerUserID = rs.getInt("userid");
                String referrerDisplayName = rs.getString("displayname");
                String referrerUsername = rs.getString("username");
                String referrerMobilePhone = rs.getString("mobilephone");
                int paid = rs.getInt("paid");
                if (paid == 0) {
                    ContactLocal contactEJB = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
                    contactEJB.makeReferrerAndReferreeFriends(userData.userID, userData.username, userData.mobilePhone, referrerUserID, referrerUsername, referrerDisplayName, referrerMobilePhone);
                }
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            if (sendVerificationCode && messageEJB.isMobileNumber(userData.mobilePhone, true)) {
                String messagePattern = "";
                messagePattern = userData.registrationDevice != null && userData.registrationDevice.equals("Web") ? SystemProperty.get("VerificationCodeWebSMS") : SystemProperty.get("VerificationCodeSMS");
                this.sendVerificationCode(userData.username, userData.password, userData.registrationIPAddress, userData.mobilePhone, userData.verificationCode, messagePattern, accountEntrySourceData);
            }
        } else if (userRegContextData.registrationType == RegistrationType.EMAIL_LEGACY && !StringUtil.isBlank(userData.emailAddress)) {
            String token = this.generateEmailVerificationToken(userData.emailAddress);
            if (token == null) {
                throw new EJBException("Unable to generate email verification token");
            }
            if (!this.cacheEmailVerificationTokenDetails(token, userData.userID, userData.emailAddress)) {
                throw new EJBException("Unable to store email verification token");
            }
            this.sendVerificationToken(userData.username, userData.emailAddress, token, userRegContextData.registrationType, performSendEmailVerification);
        }
        if (userRegContextData.verified) {
            try {
                RewardCentre.getInstance().sendTrigger(new UserFirstAuthenticatedTrigger(userData, new RegistrationContextData(userData, userRegContextData, accountEntrySourceData)));
            }
            catch (Exception e) {
                log.warn((Object)"Unable to notify reward system to send UserFirstAuthenticatedTrigger", (Throwable)e);
            }
            invitationID = userRegContextData.invitationID;
            if (invitationID != null) {
                Timestamp actionTime = new Timestamp(System.currentTimeMillis());
                InvitationData invitationData = this.getAndValidateSignUpInvitationData(connMaster, (Integer)invitationID, actionTime);
                if (invitationData != null && InvitationUtils.isInvitationEngineEnabled(invitationData.channel)) {
                    InvitationResponseData invitationResponseData = this.logInvitationResponse(connMaster, actionTime, invitationData, InvitationResponseData.ResponseType.SIGN_UP_VERIFIED, userData.username, InvitationData.StatusFieldValue.CLOSED);
                    log.info((Object)String.format("Invitation Response: invitaionID:%s, inviteeID:%s, inviterID:%s, response:%s, activity:%s", invitationData.id, userData.userID, invitationData.inviterUserId, invitationResponseData.responseType, invitationData.type));
                }
            }
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Default.BCLPERSISTED_ENABLED)) {
            BroadcastListPersisted.setBroadcastListPersisted(bclPersistedMemcache, userData.username, 1);
        }
        MemCachedHelper.setUsernameIdMapping(userData.username, userData.userID);
        this.sendMigAlertsToNewUser(userData, null);
        invitationID = userData;
        Object var17_21 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e) {
            connMaster = null;
        }
        {
            return invitationID;
            catch (NoSuchFieldException e) {
                throw new EJBException(e.getMessage());
            }
            catch (CreateException e) {
                throw new EJBException(e.getMessage());
            }
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var17_22 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e) {
                connMaster = null;
            }
            throw throwable;
        }
    }

    private InvitationData getAndValidateSignUpInvitationData(Connection conn, int invitationID, Date actionTime) {
        if (invitationID <= 0) {
            log.error((Object)("processing invitation id will be ignored. Invitation ID value is " + invitationID));
            return null;
        }
        InvitationData invitationData = this.getInvitationData(invitationID, true, conn);
        if (invitationData == null) {
            log.error((Object)("processing invitation id will be ignored.Invitation ID " + invitationID + " does not exist"));
            return null;
        }
        InvitationData.StatusFieldValue invitationStatus = InvitationUtils.getInvitationStatus(invitationData, actionTime);
        if (invitationStatus != InvitationData.StatusFieldValue.NO_RESPONSE) {
            log.error((Object)("processing invitation id will be ignored.Invitation ID " + invitationID + " invitationStatusEnum is " + invitationStatus));
            return null;
        }
        return invitationData;
    }

    public void sendVerificationToken(String username, String emailAddress, String token, RegistrationType type) throws EJBException {
        this.sendVerificationToken(username, emailAddress, token, type, true);
    }

    private void sendVerificationToken(String username, String emailAddress, String token, RegistrationType type, boolean sendVerificationToken) throws EJBException {
        try {
            if (!StringUtil.isBlank(emailAddress)) {
                String urlParams = String.format("userName=%s&email=%s", URLEncoder.encode(username, "UTF-8"), URLEncoder.encode(emailAddress, "UTF-8"));
                if (SystemProperty.getBool(SystemPropertyEntities.Email.ENABLED_SEND_VERIFICATION_TOKEN_WITH_TEMPLATE)) {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("username", username);
                    String emailVerifyLink = SystemProperty.get("VerifyEmailActivateAccountLink", "http://www.mig33.com/sites/corporate/registration/email_verification?token=");
                    String sep = "&";
                    if (type == RegistrationType.EMAIL_REGISTRATION_PATH1) {
                        emailVerifyLink = SystemProperty.get("VerifyEmailActivateAccountLinkPath1", "https://register.mig.me/verify/");
                        sep = "?";
                    } else if (type == RegistrationType.EMAIL_REGISTRATION_PATH2) {
                        emailVerifyLink = SystemProperty.get("VerifyEmailActivateAccountLinkPath2", "https://register.mig.me/verify/step2/");
                        sep = "?";
                    }
                    params.put("verification_link", emailVerifyLink + token + sep + urlParams);
                    if (sendVerificationToken) {
                        UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                        unsProxy.sendTemplatizedEmailFromNoReply(emailAddress, SystemProperty.getInt(SystemPropertyEntities.Default.VERIFICATION_EMAIL_TEMPLATE_ID), params);
                    } else {
                        log.info((Object)("sendVerificationToken(with template) [" + username + "],[" + emailAddress + "],token[" + token + "],regType:[" + (Object)((Object)type) + "],params:[" + params + "] disabled."));
                    }
                } else if (!StringUtil.isBlank(emailAddress)) {
                    String emailVerifyLink = SystemProperty.get("VerifyEmailActivateAccountLink", "http://mig.me/sites/corporate/registration/email_verification?token=");
                    String subject = SystemProperty.get("VerifyEmailActivateAccountSubject", "Activate your migme account");
                    String content = SystemProperty.get("VerifyEmailActivateAccountContent", "Hi {0},\n\nThank you for signing up with migme! \n\n You are one step closer to being part of our awesome community. Please click on this link to activate your account now: {1}\n\n--The migme Team\n\n(if clicking the link in this message does not work, copy and paste it into the address bar of your browser)");
                    content = MessageFormat.format(content, username, emailVerifyLink + token + "&" + urlParams);
                    if (sendVerificationToken) {
                        log.info((Object)("Sending verification email to [" + username + "] : subject: [" + subject + "] content: [" + content + "]"));
                        MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                        messageEJB.sendEmailFromNoReply(emailAddress, subject, content);
                    } else {
                        log.info((Object)("sendVerificationToken(without template) [" + username + "],[" + emailAddress + "],token[" + token + "],regType:[" + (Object)((Object)type) + "] disabled."));
                    }
                }
            }
        }
        catch (Exception e) {
            log.error((Object)("could not create email verification token for: " + username), (Throwable)e);
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean isUsernameAvailable(String username) {
        boolean bl;
        Connection conn = null;
        try {
            try {
                this.checkUsername(username);
                conn = this.dataSourceSlave.getConnection();
                bl = !this.usernameExists(conn, username);
                Object var6_7 = null;
            }
            catch (EJBException e) {
                log.debug((Object)("Username [" + username + "] is not available for registration: " + (Object)((Object)e)));
                boolean bl2 = false;
                Object var6_8 = null;
                try {
                    if (conn == null) return bl2;
                    conn.close();
                    return bl2;
                }
                catch (SQLException e2) {
                    return bl2;
                }
            }
            catch (SQLException e) {
                log.debug((Object)e);
                boolean bl3 = false;
                Object var6_9 = null;
                try {}
                catch (SQLException e2) {
                    return bl3;
                }
                if (conn == null) return bl3;
                conn.close();
                return bl3;
            }
            catch (NoSuchFieldException e) {
                log.debug((Object)("Username [" + username + "] is not valid: " + e));
                boolean bl4 = false;
                Object var6_10 = null;
                try {}
                catch (SQLException e2) {
                    return bl4;
                }
                if (conn == null) return bl4;
                conn.close();
                return bl4;
            }
        }
        catch (Throwable throwable) {
            Object var6_11 = null;
            try {}
            catch (SQLException e2) {
                conn = null;
                throw throwable;
            }
            if (conn == null) throw throwable;
            conn.close();
            throw throwable;
        }
        try {}
        catch (SQLException e2) {
            return bl;
        }
        if (conn == null) return bl;
        conn.close();
        return bl;
    }

    private void sendMigAlertsToNewUser(UserData userData, String alerts_message) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Alert.MIGBO_SYS_ALERTS_FOR_NEW_USER_ENABLED)) {
            return;
        }
        String alerts_str = null;
        try {
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            String string = alerts_str = alerts_message == null ? SystemProperty.get(SystemPropertyEntities.Alert.MIGBO_SYS_ALERTS_FOR_NEW_USER) : alerts_message;
            if (StringUtils.isEmpty((String)alerts_str)) {
                return;
            }
            log.info((Object)("Sending migalerts to new user:" + alerts_str));
            JSONArray alerts_array = new JSONArray(alerts_str);
            for (int i = 0; i < alerts_array.length(); ++i) {
                JSONObject alert = alerts_array.getJSONObject(i);
                if (!alert.has("alert_key") || !alert.has("alert_content")) {
                    log.warn((Object)String.format("Failed to parse mig alerts for new user: %s, alert: %s, should at lease have alert_key and alert_content", userData.userID, alert));
                    continue;
                }
                log.info((Object)"Sending migalerts to new user");
                unsProxy.notifyFusionUser(new Message(alert.getString("alert_key"), userData.userID, userData.username, Enums.NotificationTypeEnum.SYS_ALERT.getType(), System.currentTimeMillis(), this.parseMigAlertsFromJSONArrayToMap(alert)));
            }
        }
        catch (JSONException e) {
            log.warn((Object)String.format("Failed to parse mig alerts for new user: %s, alert: %s", userData.userID, alerts_str), (Throwable)e);
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to create mig alerts for new user: %s", userData.userID), (Throwable)e);
        }
    }

    private void sendMigAlertToInviterWhenInviteeRegister(UserData inviterUserData, UserData inviteeUserData) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Alert.MIGBO_SYS_ALERTS_FOR_INVITER_ENABLED)) {
            return;
        }
        if (inviterUserData == null || inviteeUserData == null || StringUtil.isBlank(inviteeUserData.emailAddress)) {
            return;
        }
        try {
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("alert_key", "inform_inviter_with_invitee_email");
            parameters.put("alert_content", inviteeUserData.emailAddress + " has just joined migme as Username:%{invitee_username}.");
            parameters.put("invitee_username_linktype", "invitee_username");
            parameters.put("invitee_username_label", inviteeUserData.username);
            unsProxy.notifyFusionUser(new Message((String)parameters.get("alert_key"), inviterUserData.userID, inviterUserData.username, Enums.NotificationTypeEnum.SYS_ALERT.getType(), System.currentTimeMillis(), parameters));
            log.info((Object)String.format("Sending sys alert to inviter:%s, informing inviter that invitee:%s with emailaddress:%s has joined migme", inviterUserData.username, inviteeUserData.username, inviteeUserData.emailAddress));
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to create mig alerts for inviter: %s", inviterUserData.userID), (Throwable)e);
        }
    }

    private void sendMigAlertToInviterWhenInviteeAcceptInvitation(UserData inviterUserData, UserData inviteeUserData) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Alert.MIGBO_SYS_ALERTS_FOR_INVITER_ENABLED_WHEN_INVITEE_ACCEPT_INVITATION)) {
            return;
        }
        if (inviterUserData == null || inviteeUserData == null) {
            return;
        }
        try {
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("alert_key", "invitee_accpet_invitation");
            parameters.put("alert_content", "%{invitee_username} has already accepted your invitation.");
            parameters.put("invitee_username_linktype", "invitee_username");
            parameters.put("invitee_username_label", inviteeUserData.username);
            unsProxy.notifyFusionUser(new Message((String)parameters.get("alert_key"), inviterUserData.userID, inviterUserData.username, Enums.NotificationTypeEnum.SYS_ALERT.getType(), System.currentTimeMillis(), parameters));
            log.info((Object)String.format("Sending sys alert to inviter:%s, informing inviter that invitee:%s has already accepted invitation", inviterUserData.username, inviteeUserData.username));
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to create mig alerts for inviter: %s", inviterUserData.userID), (Throwable)e);
        }
    }

    private Map<String, String> parseMigAlertsFromJSONArrayToMap(JSONObject alert) throws JSONException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("alert_content", alert.getString("alert_content"));
        Iterator keys = alert.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            if (key.equals("alert_key") || key.equals("alert_content")) continue;
            parameters.put(key, alert.getString(key));
        }
        return parameters;
    }

    /*
     * Loose catch block
     */
    private boolean usernameExists(Connection conn, String username) throws EJBException {
        Connection userRegistrationConn;
        ResultSet rs;
        PreparedStatement ps;
        block38: {
            String statement;
            block34: {
                if (StringUtil.isBlank(username)) {
                    return true;
                }
                ps = null;
                rs = null;
                userRegistrationConn = null;
                statement = "select username from user where username = ? or username=(select distinct username from useralias where alias= ?)";
                ps = conn.prepareStatement(statement);
                ps.setString(1, username);
                ps.setString(2, username);
                rs = ps.executeQuery();
                if (!rs.next()) break block34;
                boolean bl = true;
                Object var9_11 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (userRegistrationConn != null) {
                        userRegistrationConn.close();
                    }
                }
                catch (SQLException e2) {
                    userRegistrationConn = null;
                }
                return bl;
            }
            if (!this.isEmailRegistrationV2Enabled()) break block38;
            userRegistrationConn = this.userRegistrationSlave.getConnection();
            statement = "SELECT DISTINCT username FROM (SELECT * FROM userregistration WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMax() + ",now()) AND username=?) AS t " + "WHERE verified=1 OR updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMin() + ",now())";
            ps = userRegistrationConn.prepareStatement(statement);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block38;
            boolean bl = true;
            Object var9_12 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (userRegistrationConn != null) {
                    userRegistrationConn.close();
                }
            }
            catch (SQLException e2) {
                userRegistrationConn = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var9_13 = null;
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (userRegistrationConn != null) {
                userRegistrationConn.close();
            }
        }
        catch (SQLException e2) {
            userRegistrationConn = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                log.info((Object)e);
                throw new EJBException("Unable to check if username exists");
            }
            catch (Throwable throwable) {
                Object var9_14 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (userRegistrationConn != null) {
                        userRegistrationConn.close();
                    }
                }
                catch (SQLException e2) {
                    userRegistrationConn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean emailAddressExists(String emailAddress) throws EJBException {
        boolean bl;
        Connection conn = null;
        try {
            try {
                conn = this.dataSourceSlave.getConnection();
                bl = this.emailAddressExists(conn, emailAddress);
                Object var5_5 = null;
            }
            catch (SQLException e) {
                log.warn((Object)("Unable to check if email address exists: " + e));
                throw new EJBException("Unable to check if email address exists");
            }
        }
        catch (Throwable throwable) {
            Object var5_6 = null;
            try {
                if (conn == null) throw throwable;
                conn.close();
                throw throwable;
            }
            catch (SQLException e2) {
                conn = null;
                throw throwable;
            }
        }
        try {}
        catch (SQLException e2) {
            return bl;
        }
        if (conn == null) return bl;
        conn.close();
        return bl;
    }

    /*
     * Loose catch block
     */
    private boolean emailAddressExists(Connection conn, String emailAddress) throws EJBException {
        Connection userRegistrationConn;
        ResultSet rs;
        PreparedStatement ps;
        block38: {
            String statement;
            block34: {
                if (StringUtil.isBlank(emailAddress)) {
                    throw new EJBException("Email address is blank");
                }
                ps = null;
                rs = null;
                userRegistrationConn = null;
                statement = "select emailaddress from useremailaddress where emailaddress = ?";
                ps = conn.prepareStatement(statement);
                ps.setString(1, emailAddress);
                rs = ps.executeQuery();
                if (!rs.next()) break block34;
                boolean bl = true;
                Object var9_11 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (userRegistrationConn != null) {
                        userRegistrationConn.close();
                    }
                }
                catch (SQLException e2) {
                    userRegistrationConn = null;
                }
                return bl;
            }
            if (!this.isEmailRegistrationV2Enabled()) break block38;
            userRegistrationConn = this.userRegistrationSlave.getConnection();
            statement = "SELECT DISTINCT emailaddress FROM (SELECT * FROM userregistration WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMax() + ",now()) AND emailaddress=?) AS t " + "WHERE verified=1 OR updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMin() + ",now())";
            ps = userRegistrationConn.prepareStatement(statement);
            ps.setString(1, emailAddress);
            rs = ps.executeQuery();
            if (!rs.next()) break block38;
            boolean bl = true;
            Object var9_12 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (userRegistrationConn != null) {
                    userRegistrationConn.close();
                }
            }
            catch (SQLException e2) {
                userRegistrationConn = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var9_13 = null;
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (userRegistrationConn != null) {
                userRegistrationConn.close();
            }
        }
        catch (SQLException e2) {
            userRegistrationConn = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                log.info((Object)e);
                throw new EJBException("Unable to check if email address exists");
            }
            catch (Throwable throwable) {
                Object var9_14 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (userRegistrationConn != null) {
                        userRegistrationConn.close();
                    }
                }
                catch (SQLException e2) {
                    userRegistrationConn = null;
                }
                throw throwable;
            }
        }
    }

    public UserVerificationData getVerificationDataFromToken(String token, boolean includingExpiredToken) throws EJBException {
        ResultSet rs;
        Statement ps;
        Connection conn;
        block24: {
            if (!this.isEmailRegistrationV2Enabled()) {
                throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
            }
            conn = null;
            ps = null;
            rs = null;
            conn = this.userRegistrationSlave.getConnection();
            String statement = null;
            statement = !includingExpiredToken ? "SELECT username,emailaddress,updatedtime,registrationtype,verified,context FROM (SELECT * FROM userregistration WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMax() + ",now()) AND verificationtoken=?) AS t " + "WHERE verified=1 OR updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMin() + ",now())" : "SELECT username,emailaddress,updatedtime,registrationtype,verified,context FROM userregistration where verificationtoken=? LIMIT 1";
            ps = conn.prepareStatement(statement);
            ps.setString(1, token);
            rs = ps.executeQuery();
            if (!rs.next()) break block24;
            String username = rs.getString(1);
            String emailAddress = rs.getString(2);
            Long updatedTime = rs.getTimestamp(3).getTime();
            String type = rs.getString(4);
            Boolean isVerified = rs.getBoolean(5);
            String registrationContext = rs.getString(6);
            String campaign = null;
            if (!StringUtil.isBlank(registrationContext)) {
                try {
                    JSONObject jsonObj = new JSONObject(registrationContext);
                    campaign = jsonObj.optString(Integer.toString(RegistrationContextData.RegistrationContextTypeEnum.CAMPAIGN.value()), null);
                }
                catch (JSONException e) {
                    log.warn((Object)("Unable to parse JSON string: " + (Object)((Object)e)));
                }
            }
            UserVerificationData userVerificationData = new UserVerificationData(username, emailAddress, type, isVerified, updatedTime, campaign);
            Object var16_17 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            return userVerificationData;
        }
        try {
            try {
                throw new EJBException("Token does not exist");
            }
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var16_18 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
    }

    public UserVerificationData getVerificationDataFromToken(String token) throws EJBException {
        return this.getVerificationDataFromToken(token, false);
    }

    public boolean validateRegistrationToken(UserVerificationData data) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(12, -1 * SystemProperty.getInt(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_TIMELIMIT_IN_MINUTES));
        Date verificationTimeLimit = calendar.getTime();
        if (data.updatedTime > verificationTimeLimit.getTime()) {
            try {
                this.verifyEmailRegistration(data.username, data.emailAddress);
            }
            catch (EJBException e) {
                log.error((Object)("Unable to update verification status in the DB: " + (Object)((Object)e)));
            }
            return true;
        }
        return false;
    }

    /*
     * Loose catch block
     */
    public void insertPartnerUser(int partnerId, int userId) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("insert into ussdpartneruser (userid, ussdpartnerid) values (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, partnerId);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Creating partner user record failed");
            }
            Object var7_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public UserData loadStaff(String username) throws EJBException {
        UserData userData;
        block22: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block19: {
                userData = null;
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select * from staff where username = ?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                if (!rs.next()) break block19;
                userData = new UserData();
                userData.username = rs.getString("username");
                userData.password = rs.getString("password");
                ps.close();
                rs.close();
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return userData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<String> loadStringListForUser(String username, String field, String tableName, Connection connection) throws SQLException {
        HashSet<String> hashSet;
        Statement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("select " + field + " from " + tableName + " where username = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            HashSet<String> set = new HashSet<String>();
            while (resultSet.next()) {
                set.add(resultSet.getString(field));
            }
            hashSet = set;
            Object var10_9 = null;
        }
        catch (Throwable throwable) {
            Object var10_10 = null;
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            catch (SQLException e) {
                resultSet = null;
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
            catch (SQLException e) {
                preparedStatement = null;
            }
            throw throwable;
        }
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        catch (SQLException e) {
            resultSet = null;
        }
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        catch (SQLException e) {
            preparedStatement = null;
        }
        return hashSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Set<String> loadBroadcastList(String username, Connection connection) throws SQLException {
        Set<String> broadcastList;
        block6: {
            broadcastList = BroadcastList.getBroadcastList(broadcastListMemcache, username);
            if (broadcastList == null) {
                ConnectionHolder ch = null;
                try {
                    ch = new ConnectionHolder(this.dataSourceSlave, connection);
                    broadcastList = this.loadStringListForUser(username, "broadcastUsername", "broadcastlist", ch.getConnection());
                    if (!broadcastList.isEmpty()) {
                        BroadcastList.setBroadcastList(broadcastListMemcache, username, broadcastList);
                    }
                    Object var6_5 = null;
                    if (ch == null) break block6;
                }
                catch (Throwable throwable) {
                    Object var6_6 = null;
                    if (ch != null) {
                        ch.close();
                    }
                    throw throwable;
                }
                ch.close();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("loaded " + broadcastList.size() + " entries for broadcast list"));
        }
        return broadcastList;
    }

    public Set<String> checkAndLoadBroadcastList(String username, Connection connection) throws SQLException, CreateException, EJBException, NoSuchFieldException {
        if (SystemProperty.getBool(SystemPropertyEntities.Default.CHECK_AND_POPULATEBCL)) {
            ContactLocal contactEJB = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            Set broadcastList = contactEJB.checkAndPopulateBCL(username, connection);
            if (log.isDebugEnabled()) {
                log.debug((Object)("found " + broadcastList.size() + " BCL entries for user [" + username + "] after checkAndPopulate"));
            }
            return broadcastList;
        }
        return this.loadBroadcastList(username, connection);
    }

    private Set<String> loadBlockList(String username, Connection connection) throws SQLException {
        Set<String> blockList = (Set<String>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.BLOCK_LIST, username);
        if (blockList == null) {
            blockList = this.loadStringListForUser(username, "blockUsername", "blocklist", connection);
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.BLOCK_LIST, username, blockList);
        }
        return blockList;
    }

    private Set<String> loadPendingContacts(String username, Connection connection) throws SQLException {
        Set<String> pendingContacts = this.loadStringListForUser(username, "pendingContact", "pendingcontact", connection);
        if (log.isDebugEnabled()) {
            log.debug((Object)("loaded " + pendingContacts.size() + " entries for pending contact list"));
        }
        return pendingContacts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<UserSettingData> getUserSettings(String username) throws SQLException {
        ArrayList<UserSettingData> arrayList;
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        try {
            ArrayList<UserSettingData> settings = (ArrayList<UserSettingData>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.USER_SETTING, username);
            if (settings == null) {
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select * from usersetting where username = ?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                settings = new ArrayList<UserSettingData>();
                while (rs.next()) {
                    settings.add(new UserSettingData(rs));
                }
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_SETTING, username, settings);
            }
            arrayList = settings;
            Object var8_7 = null;
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void populateUserSettings(UserData userData, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ArrayList<UserSettingData> settings = (ArrayList<UserSettingData>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.USER_SETTING, userData.username);
            if (settings == null) {
                ps = conn.prepareStatement("select * from usersetting where username = ?");
                ps.setString(1, userData.username);
                rs = ps.executeQuery();
                settings = new ArrayList<UserSettingData>();
                while (rs.next()) {
                    settings.add(new UserSettingData(rs));
                }
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_SETTING, userData.username, settings);
            }
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
            Object var9_8 = null;
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
    }

    /*
     * Loose catch block
     */
    public void checkUserAliasByUserid(int userid, String alias, boolean sameAsUsername, Connection conn) throws EJBException {
        block14: {
            ConnectionHolder ch = null;
            try {
                this.checkUserAlias(alias, sameAsUsername);
            }
            catch (FusionEJBException e) {
                throw new EJBException("Invalid alias");
            }
            ch = new ConnectionHolder(this.dataSourceSlave, null);
            int aliasUserid = this.getUseridByAlias(alias, ch.getConnection());
            if (aliasUserid != -1) {
                if (aliasUserid != userid) {
                    throw new EJBException("Not available");
                }
                throw new EJBException("Already set");
            }
            if (!sameAsUsername && (aliasUserid = this.getUserID(alias, ch.getConnection(), false)) != -1 && aliasUserid != userid) {
                throw new EJBException("Not available");
            }
            Object var8_9 = null;
            try {
                if (ch != null) {
                    ch.close();
                }
                break block14;
            }
            catch (SQLException e) {
                ch = null;
            }
            break block14;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void setUserAliasByUserid(int userid, String alias) throws EJBException {
        block24: {
            ConnectionHolder ch = null;
            Statement ps = null;
            ResultSet rs = null;
            ch = new ConnectionHolder(this.dataSourceMaster, null);
            String username = this.getUsernameByUserid(userid, ch.getConnection());
            if (StringUtil.isBlank(username)) {
                throw new EJBException("Invalid user");
            }
            this.checkUserAliasByUserid(userid, alias, username.equalsIgnoreCase(alias), ch.getConnection());
            if (this.getUserAliasByUserid(userid, ch.getConnection()) != null) {
                throw new EJBException("Already set");
            }
            ps = ch.getConnection().prepareStatement("insert into useralias (username, alias, dateupdated) values (?, ?, now())");
            ps.setString(1, username);
            ps.setString(2, alias);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to set alias");
            }
            MemCachedHelper.setUserAlias(username, userid, alias);
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
                break block24;
            }
            catch (SQLException e) {
                ch = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public String getUserAliasByUsername(String username, Connection conn) throws EJBException {
        String alias;
        ResultSet rs;
        Statement ps;
        ConnectionHolder ch;
        block29: {
            if (StringUtil.isBlank(username)) {
                return null;
            }
            ch = null;
            ps = null;
            rs = null;
            alias = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.USER_ALIAS_BY_USERNAME, username.toLowerCase());
            if (alias == null) {
                ch = new ConnectionHolder(this.dataSourceSlave, conn);
                ps = ch.getConnection().prepareStatement("select id,alias from userid uid, useralias ua where uid.username = ua.username and ua.username = ?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                int userid = -1;
                if (rs.next()) {
                    userid = rs.getInt("id");
                    alias = rs.getString("alias");
                }
                MemCachedHelper.setUserAlias(username, userid, alias);
                break block29;
            }
            if (!StringUtil.isBlank(alias)) break block29;
            String string = null;
            Object var9_11 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
            }
            catch (SQLException e2) {
                ch = null;
            }
            return string;
        }
        String string = alias;
        Object var9_12 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public String getUsernameByAlias(String alias, Connection conn) throws EJBException {
        String username;
        ResultSet rs;
        Statement ps;
        ConnectionHolder ch;
        block29: {
            if (StringUtil.isBlank(alias)) {
                return null;
            }
            ch = null;
            ps = null;
            rs = null;
            username = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.USER_NAME_BY_ALIAS, alias.toLowerCase());
            if (username == null) {
                ch = new ConnectionHolder(this.dataSourceSlave, conn);
                ps = ch.getConnection().prepareStatement("select ua.username as username,id from userid uid, useralias ua where uid.username = ua.username and ua.alias = ?");
                ps.setString(1, alias);
                rs = ps.executeQuery();
                int userid = -1;
                if (rs.next()) {
                    userid = rs.getInt("id");
                    username = rs.getString("username");
                }
                MemCachedHelper.setUserAlias(username, userid, alias);
                break block29;
            }
            if (!StringUtil.isBlank(username)) break block29;
            String string = null;
            Object var9_11 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
            }
            catch (SQLException e2) {
                ch = null;
            }
            return string;
        }
        String string = username;
        Object var9_12 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public int getUseridByAlias(String alias, Connection conn) throws EJBException {
        if (StringUtil.isBlank(alias)) {
            return -1;
        }
        ConnectionHolder ch = null;
        Statement ps = null;
        ResultSet rs = null;
        Integer userid = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.USER_ID_BY_ALIAS, alias.toLowerCase());
        if (userid == null) {
            ch = new ConnectionHolder(this.dataSourceSlave, conn);
            ps = ch.getConnection().prepareStatement("select ua.username as username,id from userid uid, useralias ua where uid.username = ua.username and ua.alias = ?");
            ps.setString(1, alias);
            rs = ps.executeQuery();
            String username = null;
            userid = -1;
            if (rs.next()) {
                userid = rs.getInt("id");
                username = rs.getString("username");
            }
            MemCachedHelper.setUserAlias(username, userid, alias);
        }
        int n = userid;
        Object var9_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return n;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public SecurityQuestion getSecurityQuestion(int userid) throws EJBException {
        SecurityQuestion sq;
        block22: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block19: {
                conn = null;
                ps = null;
                rs = null;
                sq = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select sq.ID as id, sq.question as question from usersetting us join userid on us.username = userid.username join securityquestion sq on us.value = sq.id where userid.id = ? and us.type = 3 ");
                ps.setInt(1, userid);
                rs = ps.executeQuery();
                if (!rs.next()) break block19;
                sq = new SecurityQuestion(rs);
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return sq;
    }

    /*
     * Loose catch block
     */
    public String getUserAliasByUserid(int userid, Connection conn) throws EJBException {
        String alias;
        ResultSet rs;
        Statement ps;
        ConnectionHolder ch;
        block28: {
            ch = null;
            ps = null;
            rs = null;
            alias = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.USER_ALIAS_BY_USERID, "" + userid);
            if (alias == null) {
                ch = new ConnectionHolder(this.dataSourceSlave, conn);
                ps = ch.getConnection().prepareStatement("select ua.username as username,alias from userid uid, useralias ua where uid.username = ua.username and uid.id = ?");
                ps.setInt(1, userid);
                rs = ps.executeQuery();
                String username = null;
                if (rs.next()) {
                    username = rs.getString("username");
                    alias = rs.getString("alias");
                }
                MemCachedHelper.setUserAlias(username, userid, alias);
                break block28;
            }
            if (!StringUtil.isBlank(alias)) break block28;
            String string = null;
            Object var9_10 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
            }
            catch (SQLException e2) {
                ch = null;
            }
            return string;
        }
        String string = alias;
        Object var9_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    public String getUsernameByUserid(int userid, Connection conn) throws EJBException {
        block26: {
            ch = null;
            ps = null;
            rs = null;
            username = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.USER_NAME_BY_ID, "" + userid);
            if (username != null) ** GOTO lbl41
            ch = new ConnectionHolder(this.dataSourceSlave, conn);
            ps = ch.getConnection().prepareStatement("select username from userid where id = ?");
            ps.setInt(1, userid);
            rs = ps.executeQuery();
            if (rs.next()) break block26;
            var7_8 = null;
            var9_10 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
            }
            catch (SQLException e) {
                ch = null;
            }
            return var7_8;
        }
        username = rs.getString("username");
        MemCachedHelper.setUsernameIdMapping(username, userid);
lbl41:
        // 2 sources

        var7_9 = username;
        var9_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e) {
            ch = null;
        }
        return var7_9;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable var8_16) {
                var9_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw var8_16;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean isBounceEmailAddress(String recipient) throws FusionEJBException {
        if (!SystemProperty.getBool(SystemPropertyEntities.UserNotificationServiceSettings.CHECK_EMAIL_BOUNCEDB_ENABLED)) {
            return false;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        String sql = "SELECT count(*) FROM bouncedb WHERE emailaddress = ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, recipient);
        rs = ps.executeQuery();
        int result = 0;
        if (rs.next()) {
            result = rs.getInt(1);
        }
        boolean bl = result != 0;
        Object var9_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return bl;
        catch (Exception e) {
            try {
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Integer> getEmailUserIdMapping(Collection<String> emailAddresses) throws FusionEJBException {
        HashMap<String, Integer> emailUserIDMap = new HashMap<String, Integer>();
        for (String emailAddress : emailAddresses) {
            emailUserIDMap.put(StringUtil.normalizeEmailAddress(emailAddress), UNKNOWN_EMAIL_ADDRESS);
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        int emailAddressesCount = emailUserIDMap.size();
        int foundExistingUserCount = 0;
        try {
            conn = this.dataSourceSlave.getConnection();
            try {
                String sql = "select userid, emailaddress from useremailaddress where emailaddress in (%s) group by emailaddress";
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < emailAddressesCount; ++i) {
                    if (i > 0) {
                        builder.append(",");
                    }
                    builder.append("?");
                }
                ps = conn.prepareStatement(String.format(sql, builder.toString()));
                try {
                    int paramIdx = 1;
                    for (String emailAddress : emailUserIDMap.keySet()) {
                        ps.setString(paramIdx, emailAddress);
                        ++paramIdx;
                    }
                    rs = ps.executeQuery();
                    try {
                        while (rs.next()) {
                            emailUserIDMap.put(StringUtil.normalizeEmailAddress(rs.getString("emailaddress")), rs.getInt("userid"));
                            ++foundExistingUserCount;
                        }
                        Object var14_18 = null;
                    }
                    catch (Throwable throwable) {
                        Object var14_19 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var16_22 = null;
                }
                catch (Throwable throwable) {
                    Object var16_23 = null;
                    ps.close();
                    throw throwable;
                }
                ps.close();
                Object var18_26 = null;
            }
            catch (Throwable throwable) {
                Object var18_27 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            if (foundExistingUserCount < emailAddressesCount) {
                conn = this.userRegistrationSlave.getConnection();
                try {
                    int timelimit = SystemProperty.getInt(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_TIMELIMIT_IN_MINUTES);
                    int gracePeriod = SystemProperty.getInt(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_GRACE_PERIOD_IN_MINUTES);
                    String sql = "(select emailaddress, 1 as type from userregistration where verified = 1 and updatedTime > TIMESTAMPADD(minute," + -1 * (timelimit + gracePeriod) + ",now()) and emailaddress in (%s) ) " + "union" + " (select emailaddress,2 as type from userregistration " + "where verified = 0 and updatedTime > TIMESTAMPADD(minute," + -1 * timelimit + ",now()) and emailaddress in (%s) )";
                    StringBuilder builder = new StringBuilder();
                    int remainingUnknownEmailAddressCount = emailAddressesCount - foundExistingUserCount;
                    for (int i = 0; i < remainingUnknownEmailAddressCount; ++i) {
                        if (i > 0) {
                            builder.append(",");
                        }
                        builder.append("?");
                    }
                    ps = conn.prepareStatement(String.format(sql, builder.toString(), builder.toString()));
                    try {
                        int paramIdx = 1;
                        for (Map.Entry emailToUserIDEntry : emailUserIDMap.entrySet()) {
                            if (emailToUserIDEntry.getValue() != UNKNOWN_EMAIL_ADDRESS) continue;
                            ps.setString(paramIdx, (String)emailToUserIDEntry.getKey());
                            ++paramIdx;
                        }
                        for (Map.Entry emailToUserIDEntry : emailUserIDMap.entrySet()) {
                            if (emailToUserIDEntry.getValue() != UNKNOWN_EMAIL_ADDRESS) continue;
                            ps.setString(paramIdx, (String)emailToUserIDEntry.getKey());
                            ++paramIdx;
                        }
                        rs = ps.executeQuery();
                        try {
                            while (rs.next()) {
                                emailUserIDMap.put(StringUtil.normalizeEmailAddress(rs.getString("emailaddress")), HALFWAY_REGISTERED_EMAIL_ADDRESS);
                            }
                            Object var20_29 = null;
                        }
                        catch (Throwable throwable) {
                            Object var20_30 = null;
                            rs.close();
                            throw throwable;
                        }
                        rs.close();
                        Object var22_32 = null;
                    }
                    catch (Throwable throwable) {
                        Object var22_33 = null;
                        ps.close();
                        throw throwable;
                    }
                    ps.close();
                    Object var24_35 = null;
                }
                catch (Throwable throwable) {
                    Object var24_36 = null;
                    conn.close();
                    throw throwable;
                }
                conn.close();
                {
                }
            }
            return emailUserIDMap;
        }
        catch (SQLException e) {
            throw new FusionEJBException(e.getMessage());
        }
    }

    public CreateInvitationsResult createInvitation(int userId, SendingInvitationData data, Connection conn) throws EJBException, FusionEJBException {
        UserData inviterUserData = this.loadUserFromID(userId);
        if (inviterUserData == null) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNKNOWN_USER, new Object[0]);
        }
        if (!InvitationUtils.isUserMobileOrEmailVerified(inviterUserData)) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNVERIFIED_USER, new Object[0]);
        }
        InvitationData.ActivityType activityType = InvitationData.ActivityType.fromTypeCode(data.type);
        if (activityType == null) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_ACTIVITY_TYPE, data.type);
        }
        InvitationData.ChannelType channelType = InvitationData.ChannelType.fromTypeCode(data.channel);
        if (channelType == null) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_CHANNEL_TYPE, data.channel);
        }
        if ((activityType == InvitationData.ActivityType.PLAY_A_GAME || activityType == InvitationData.ActivityType.GAME_HELP) && (StringUtil.isBlank(data.invitationMetadata.gameId) || StringUtil.isBlank(data.invitationMetadata.returnUrl))) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.LACK_OF_INFORMATION_FOR_GAME_INVITATION, data.invitationMetadata.gameId, data.invitationMetadata.returnUrl);
        }
        if (activityType == InvitationData.ActivityType.SHARE_PROFILE && StringUtil.isBlank(data.invitationMetadata.sharedUserID)) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.LACK_OF_INFORMATION_FOR_SHARE_PROFILE_INVITATION, data.invitationMetadata.sharedUserID);
        }
        if (channelType == InvitationData.ChannelType.EMAIL) {
            return this.handleEmailChannelForInvitationEngine(activityType, userId, data, conn);
        }
        if (channelType == InvitationData.ChannelType.FB) {
            return this.handleFBChannelForInvitationEngine(activityType, userId, data, conn);
        }
        if (channelType == InvitationData.ChannelType.INTERNAL) {
            return this.handleInternalChannelForInvitationEngine(activityType, userId, data, conn);
        }
        if (channelType == InvitationData.ChannelType.MIGBO) {
            return this.handleMigboChannelForInvitationEngine(activityType, userId, data, conn);
        }
        if (channelType == InvitationData.ChannelType.CHAT) {
            return this.handleChatChannelForInvitationEngine(activityType, userId, data, conn);
        }
        throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_CHANNEL_TYPE, data.channel);
    }

    private CreateInvitationsResult handleEmailChannelForInvitationEngine(InvitationData.ActivityType activityType, int userId, SendingInvitationData data, Connection conn) throws EJBExceptionWithErrorCause, FusionEJBException {
        if (activityType != InvitationData.ActivityType.JOIN_MIG33 && activityType != InvitationData.ActivityType.PLAY_A_GAME && activityType != InvitationData.ActivityType.GAME_HELP && activityType != InvitationData.ActivityType.SHARE_PROFILE) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_ACTIVITY_TYPE, data.type);
        }
        HashSet<String> invalidEmails = new HashSet<String>();
        CreateInvitationsResult invalidEmailMap = new CreateInvitationsResult();
        for (String emailAddr : data.destinations) {
            if (EmailUtils.externalEmailIsValid((String)emailAddr).result == EmailUtils.EmailValidatationEnum.VALID) continue;
            invalidEmails.add(emailAddr);
            invalidEmailMap.put(emailAddr, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.INVALID_DESTINATION));
            log.error((Object)String.format("Invalid Email address, Failed to send invitation from userid:%s to %s,", userId, emailAddr));
        }
        for (String emailAddr : invalidEmails) {
            data.destinations.remove(emailAddr);
        }
        if (data.destinations.size() == 0) {
            return invalidEmailMap;
        }
        CreateInvitationsResult result = new CreateInvitationsResult();
        result.putAll(invalidEmailMap);
        Map<String, Integer> emailUserIdMap = this.getEmailUserIdMapping(data.destinations);
        ArrayList<String> externalUsersEmails = new ArrayList<String>();
        ArrayList<String> existingUsersEmails = new ArrayList<String>();
        ArrayList<String> existingUserIds = new ArrayList<String>();
        ArrayList<String> halfWayRegisterUserEmails = new ArrayList<String>();
        for (Map.Entry<String, Integer> emailUserIDEntry : emailUserIdMap.entrySet()) {
            String email = emailUserIDEntry.getKey();
            Integer existingUserId = emailUserIDEntry.getValue();
            if (existingUserId > 0) {
                existingUserIds.add(existingUserId + "");
                existingUsersEmails.add(email);
                result.put(email, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.SEND_FOLLOWING_ME_REQUEST_TO_EXISTING_USER, existingUserId));
                continue;
            }
            if (existingUserId == HALFWAY_REGISTERED_EMAIL_ADDRESS) {
                halfWayRegisterUserEmails.add(email);
                result.put(email, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.NOTHING_HAPPENS_TO_HALFWAY_REGISTERED_USER));
                continue;
            }
            if (existingUserId != UNKNOWN_EMAIL_ADDRESS) continue;
            externalUsersEmails.add(email);
            result.put(email, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.SUCCESS_SEND_INVITATION));
        }
        if (activityType == InvitationData.ActivityType.JOIN_MIG33) {
            for (String inviteeId : existingUserIds) {
                this.triggerFollowingAnUserAndSendingFollowingMeRequest(userId, Integer.valueOf(inviteeId));
            }
        } else if (activityType == InvitationData.ActivityType.PLAY_A_GAME || activityType == InvitationData.ActivityType.GAME_HELP) {
            Map<String, Integer> exitingUserIdToInvitationIdMap = this.doCreateInvitation(existingUserIds, userId, data.type, InvitationData.ChannelType.INTERNAL.getTypeCode());
            this.doCreateMetadataForInvitation(exitingUserIdToInvitationIdMap.values(), data.invitationMetadata);
            block4: for (String inviteeId : existingUserIds) {
                if (exitingUserIdToInvitationIdMap.containsKey(inviteeId)) {
                    this.triggerSendGameInvitationNotification(userId, Integer.valueOf(inviteeId), data, exitingUserIdToInvitationIdMap.get(inviteeId), activityType);
                    continue;
                }
                int inviteeIdInt = Integer.valueOf(inviteeId);
                for (String key : emailUserIdMap.keySet()) {
                    if (emailUserIdMap.get(key) != inviteeIdInt) continue;
                    result.put(key, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.FAILED_TO_SEND_INVITATION_TO_DESTINATION));
                    this.triggerSendGameInvitationNotification(userId, Integer.valueOf(inviteeId), data, -1, activityType);
                    continue block4;
                }
            }
        } else if (activityType == InvitationData.ActivityType.SHARE_PROFILE) {
            for (String inviteeId : existingUserIds) {
                this.triggerFollowingAnUserAndSendingFollowingMeRequest(userId, Integer.valueOf(inviteeId));
            }
            externalUsersEmails.addAll(existingUsersEmails);
        }
        if (externalUsersEmails.size() == 0) {
            return result;
        }
        Map<String, Integer> emailIdToInvitationIdMap = this.doCreateInvitation(externalUsersEmails, userId, data.type, data.channel);
        this.doCreateMetadataForInvitation(emailIdToInvitationIdMap.values(), data.invitationMetadata);
        for (String destinationEmail : externalUsersEmails) {
            CreateInvitationsResult.CreateInvitationDetails createInvitationDetails = (CreateInvitationsResult.CreateInvitationDetails)result.get(destinationEmail);
            if (emailIdToInvitationIdMap.containsKey(destinationEmail)) {
                createInvitationDetails.invitationID = emailIdToInvitationIdMap.get(destinationEmail);
                createInvitationDetails.sendInvitationResult = InvitationUtils.SendInvitationResultEnum.SUCCESS_SEND_INVITATION;
                continue;
            }
            createInvitationDetails.invitationID = -1;
            createInvitationDetails.sendInvitationResult = InvitationUtils.SendInvitationResultEnum.FAILED_TO_SEND_INVITATION_TO_DESTINATION;
        }
        InvitationUtils.sendInvitationEmails(userId, emailIdToInvitationIdMap, data, activityType);
        return result;
    }

    private CreateInvitationsResult handleFBChannelForInvitationEngine(InvitationData.ActivityType activityType, int userId, SendingInvitationData data, Connection conn) throws EJBExceptionWithErrorCause, FusionEJBException {
        if (activityType != InvitationData.ActivityType.SHARE_PROFILE && activityType != InvitationData.ActivityType.JOIN_MIG33) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_ACTIVITY_TYPE, activityType);
        }
        CreateInvitationsResult result = new CreateInvitationsResult();
        Map<String, Integer> facebookUserIdToInvitationIdMap = this.doCreateInvitation(data.destinations, userId, data.type, data.channel);
        this.doCreateMetadataForInvitation(facebookUserIdToInvitationIdMap.values(), data.invitationMetadata);
        for (String facebookUserId : data.destinations) {
            if (facebookUserIdToInvitationIdMap.containsKey(facebookUserId) && facebookUserIdToInvitationIdMap.get(facebookUserId) > 0) {
                result.put(facebookUserId, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.SUCCESS_SEND_INVITATION));
                continue;
            }
            result.put(facebookUserId, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.FAILED_TO_SEND_INVITATION_TO_DESTINATION));
        }
        return result;
    }

    private CreateInvitationsResult handleInternalChannelForInvitationEngine(InvitationData.ActivityType activityType, int userId, SendingInvitationData data, Connection conn) throws EJBExceptionWithErrorCause, FusionEJBException {
        if (activityType != InvitationData.ActivityType.PLAY_A_GAME && activityType != InvitationData.ActivityType.GAME_HELP) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_ACTIVITY_TYPE, activityType);
        }
        CreateInvitationsResult result = new CreateInvitationsResult();
        Map<String, Integer> existingUserIdToInvitationIdMap = this.doCreateInvitation(data.destinations, userId, data.type, data.channel);
        this.doCreateMetadataForInvitation(existingUserIdToInvitationIdMap.values(), data.invitationMetadata);
        for (String des : data.destinations) {
            if (existingUserIdToInvitationIdMap.containsKey(des) && existingUserIdToInvitationIdMap.get(des) > 0) {
                result.put(des, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.SUCCESS_SEND_INVITATION));
                this.triggerSendGameInvitationNotification(userId, Integer.valueOf(des), data, existingUserIdToInvitationIdMap.get(des), activityType);
                continue;
            }
            result.put(des, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.FAILED_TO_SEND_INVITATION_TO_DESTINATION));
            this.triggerSendGameInvitationNotification(userId, Integer.valueOf(des), data, -1, activityType);
        }
        return result;
    }

    private CreateInvitationsResult handleMigboChannelForInvitationEngine(InvitationData.ActivityType activityType, int userId, SendingInvitationData data, Connection conn) throws EJBExceptionWithErrorCause, FusionEJBException {
        if (activityType != InvitationData.ActivityType.SHARE_PROFILE) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_ACTIVITY_TYPE, activityType);
        }
        Map<String, Integer> destinationToInvitationIdMap = this.doCreateInvitation(data.destinations, userId, data.type, data.channel);
        this.doCreateMetadataForInvitation(destinationToInvitationIdMap.values(), data.invitationMetadata);
        CreateInvitationsResult result = new CreateInvitationsResult();
        for (String des : data.destinations) {
            if (destinationToInvitationIdMap.containsKey(des) && destinationToInvitationIdMap.get(des) > 0) {
                result.put(des, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.SUCCESS_SEND_INVITATION));
                continue;
            }
            result.put(des, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.FAILED_TO_SEND_INVITATION_TO_DESTINATION));
        }
        return result;
    }

    private CreateInvitationsResult handleChatChannelForInvitationEngine(InvitationData.ActivityType activityType, int userId, SendingInvitationData data, Connection conn) throws EJBExceptionWithErrorCause, FusionEJBException {
        if (activityType != InvitationData.ActivityType.SHARE_PROFILE) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_ACTIVITY_TYPE, activityType);
        }
        Map<String, Integer> destinationToInvitationIdMap = this.doCreateInvitation(data.destinations, userId, data.type, data.channel);
        this.doCreateMetadataForInvitation(destinationToInvitationIdMap.values(), data.invitationMetadata);
        CreateInvitationsResult result = new CreateInvitationsResult();
        for (String des : data.destinations) {
            if (destinationToInvitationIdMap.containsKey(des) && destinationToInvitationIdMap.get(des) > 0) {
                result.put(des, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.SUCCESS_SEND_INVITATION));
                continue;
            }
            result.put(des, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.FAILED_TO_SEND_INVITATION_TO_DESTINATION));
        }
        return result;
    }

    public CreateInvitationsResult createInvitationForFBInvite(int userId, SendingInvitationData data, Connection conn) {
        if (InvitationData.ChannelType.fromTypeCode(data.channel) != InvitationData.ChannelType.FB) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_CHANNEL_TYPE, data.channel);
        }
        if (InvitationData.ActivityType.fromTypeCode(data.type) != InvitationData.ActivityType.JOIN_MIG33) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNSUPPORTED_ACTIVITY_TYPE, data.type);
        }
        UserData inviterUserData = this.loadUserFromID(userId);
        if (inviterUserData == null) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNKNOWN_USER, new Object[0]);
        }
        if (!InvitationUtils.isUserMobileOrEmailVerified(inviterUserData)) {
            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.UNVERIFIED_USER, new Object[0]);
        }
        CreateInvitationsResult result = new CreateInvitationsResult();
        Map<String, Integer> facebookUserIdToInvitationIdMap = this.doCreateInvitation(data.destinations, userId, data.type, data.channel);
        this.doCreateMetadataForInvitation(facebookUserIdToInvitationIdMap.values(), data.invitationMetadata);
        for (String facebookUserId : data.destinations) {
            if (facebookUserIdToInvitationIdMap.containsKey(facebookUserId) && facebookUserIdToInvitationIdMap.get(facebookUserId) > 0) {
                result.put(facebookUserId, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.SUCCESS_SEND_INVITATION));
                continue;
            }
            result.put(facebookUserId, new CreateInvitationsResult.CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum.FAILED_TO_SEND_INVITATION_TO_DESTINATION));
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private Map<String, Integer> doCreateInvitation(List<String> destinations, int userId, int type, int channel) {
        ConnectionHolder ch = null;
        Statement ps = null;
        ResultSet rs = null;
        HashMap<String, Integer> destinationToInvitationIdMap = new HashMap<String, Integer>();
        ArrayList<Integer> generatedInvitationIDs = new ArrayList<Integer>();
        ch = new ConnectionHolder(this.dataSourceMaster, null);
        ps = ch.getConnection().prepareStatement("insert into invitation(inviterUserId,type,channel,destination,status,expireTime) values(?,?,?,?,?,?)", new String[]{"id"});
        try {
            int expireDays = SystemProperty.getInt(SystemPropertyEntities.Invitation.REFERRAL_EXPIRE_PERIOD_IN_DAY);
            Date expireDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expireDate);
            calendar.add(5, expireDays);
            expireDate = calendar.getTime();
            for (String destination : destinations) {
                ps.setInt(1, userId);
                ps.setInt(2, type);
                ps.setInt(3, channel);
                ps.setString(4, destination);
                ps.setInt(5, InvitationData.StatusFieldValue.NO_RESPONSE.getTypeCode());
                ps.setString(6, dateFormate.format(expireDate));
                ps.addBatch();
            }
            ps.executeBatch();
            rs = ps.getGeneratedKeys();
            try {
                while (rs.next()) {
                    generatedInvitationIDs.add(rs.getInt(1));
                }
                Object var16_22 = null;
            }
            catch (Throwable throwable) {
                Object var16_23 = null;
                rs.close();
                throw throwable;
            }
            rs.close();
            Object var18_26 = null;
        }
        catch (Throwable throwable) {
            Object var18_27 = null;
            ps.close();
            throw throwable;
        }
        ps.close();
        boolean needDataBaseLookUp = false;
        if (generatedInvitationIDs.size() == destinations.size()) {
            for (int i = 0; i < destinations.size(); ++i) {
                if ((Integer)generatedInvitationIDs.get(i) <= 0) {
                    destinationToInvitationIdMap.clear();
                    needDataBaseLookUp = true;
                    break;
                }
                destinationToInvitationIdMap.put(destinations.get(i), (Integer)generatedInvitationIDs.get(i));
            }
        } else {
            needDataBaseLookUp = true;
        }
        if (needDataBaseLookUp) {
            int i;
            String retrieveIdToDestinationSQL = "select id, destination from invitation where id in (%s)";
            StringBuilder retrieveIdToDestinationSQLParamPlaceHolder = new StringBuilder();
            int generatedInvitationIDCount = generatedInvitationIDs.size();
            for (i = 0; i < generatedInvitationIDCount; ++i) {
                if (i > 0) {
                    retrieveIdToDestinationSQLParamPlaceHolder.append(",");
                }
                retrieveIdToDestinationSQLParamPlaceHolder.append("?");
            }
            ps = ch.getConnection().prepareStatement(String.format(retrieveIdToDestinationSQL, retrieveIdToDestinationSQLParamPlaceHolder.toString()));
            for (i = 0; i < generatedInvitationIDCount; ++i) {
                ps.setInt(i + 1, (Integer)generatedInvitationIDs.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                String destination = rs.getString("destination");
                Integer invitationId = rs.getInt("id");
                destinationToInvitationIdMap.put(destination, invitationId);
            }
        }
        HashMap<String, Integer> hashMap = destinationToInvitationIdMap;
        Object var20_29 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return hashMap;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage(), (Exception)e);
            }
            catch (Throwable throwable) {
                Object var20_30 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    private void doCreateMetadataForInvitation(Collection<Integer> invitationIds, InvitationMetadata invitationMetadata) {
        block19: {
            ConnectionHolder ch = null;
            Statement ps = null;
            if (invitationMetadata == null) {
                return;
            }
            ch = new ConnectionHolder(this.dataSourceMaster, null);
            ps = ch.getConnection().prepareStatement("insert into invitationparameters(invitationId,type,value) values(?,?,?)");
            for (Integer invitationId : invitationIds) {
                if (!StringUtil.isBlank(invitationMetadata.returnUrl)) {
                    ps.setInt(1, invitationId);
                    ps.setInt(2, InvitationData.ParamType.RETURN_URL.getTypeCode());
                    ps.setString(3, invitationMetadata.returnUrl);
                    ps.addBatch();
                }
                if (!StringUtil.isBlank(invitationMetadata.facebookRequestId)) {
                    ps.setInt(1, invitationId);
                    ps.setInt(2, InvitationData.ParamType.FACEBOOK_REQUEST_ID.getTypeCode());
                    ps.setString(3, invitationMetadata.facebookRequestId);
                    ps.addBatch();
                }
                if (StringUtil.isBlank(invitationMetadata.gameId)) continue;
                ps.setInt(1, invitationId);
                ps.setInt(2, InvitationData.ParamType.GAMEID.getTypeCode());
                ps.setString(3, invitationMetadata.gameId);
                ps.addBatch();
            }
            ps.executeBatch();
            Object var8_8 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
                break block19;
            }
            catch (SQLException e) {
                ch = null;
            }
            break block19;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage(), (Exception)e);
                }
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Exception decompiling
     */
    public InvitationData getInvitationData(int invitationID, boolean loadExtraParameters, Connection conn) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    public ThirdPartyApplicationData getThirdPartyApplicationData(int thirdPartyID, Connection conn) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public InvitationData getInvitationDataForFBInvite(String facebookRequestId, String facebookUserId, boolean fetchExtraParams, Connection conn) {
        int invitationId = this.getInvitationIdForFBInvite(facebookRequestId, facebookUserId, conn);
        return this.getInvitationData(invitationId, fetchExtraParams, conn);
    }

    /*
     * Exception decompiling
     */
    private int getInvitationIdForFBInvite(String facebookRequestId, String facebookUserId, Connection conn) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InvitationResponseData logInvitationResponse(Connection conn, Date actionTime, InvitationData invitationData, InvitationResponseData.ResponseType responseType, String username, InvitationData.StatusFieldValue newStatusFieldValue) {
        if (responseType == null) {
            throw new IllegalArgumentException("responseType cannot be null");
        }
        if (StringUtil.isBlank(username)) {
            throw new IllegalArgumentException("username cannot be null");
        }
        int invitationId = invitationData.id;
        Timestamp actionTimestamp = new Timestamp(actionTime.getTime());
        InvitationResponseData invitationResponseData = new InvitationResponseData();
        invitationResponseData.id = -1L;
        invitationResponseData.invitationId = invitationId;
        invitationResponseData.responseTime = actionTime;
        invitationResponseData.responseType = responseType;
        invitationResponseData.username = username;
        try {
            ConnectionHolder ch = new ConnectionHolder(this.dataSourceMaster, conn);
            try {
                PreparedStatement ps;
                block24: {
                    if (newStatusFieldValue != null) {
                        String updateSql = "UPDATE invitation SET status = ? WHERE id=? AND status = ? AND (expireTime IS NULL OR expireTime > ?)";
                        ps = ch.getConnection().prepareStatement("UPDATE invitation SET status = ? WHERE id=? AND status = ? AND (expireTime IS NULL OR expireTime > ?)");
                        try {
                            ps.setInt(1, newStatusFieldValue.getTypeCode());
                            ps.setInt(2, invitationId);
                            ps.setInt(3, InvitationData.StatusFieldValue.NO_RESPONSE.getTypeCode());
                            ps.setTimestamp(4, actionTimestamp);
                            int updateCount = ps.executeUpdate();
                            if (updateCount != 1) {
                                throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.INVITATION_STATUS_CHANGE_NOT_ALLOWED, invitationId);
                            }
                            invitationData.status = newStatusFieldValue;
                            this.postLogInvitationResponse(invitationData, username, invitationResponseData);
                            Object var15_17 = null;
                        }
                        catch (Throwable throwable) {
                            Object var15_18 = null;
                            ps.close();
                            throw throwable;
                        }
                        ps.close();
                        {
                            break block24;
                        }
                    }
                    String checkSql = "SELECT status FROM invitation WHERE id=? AND status = ? AND (expireTime IS NULL OR expireTime > ?)";
                    ps = ch.getConnection().prepareStatement("SELECT status FROM invitation WHERE id=? AND status = ? AND (expireTime IS NULL OR expireTime > ?)");
                    try {
                        ps.setInt(1, invitationId);
                        ps.setInt(2, InvitationData.StatusFieldValue.NO_RESPONSE.getTypeCode());
                        ps.setTimestamp(3, actionTimestamp);
                        ResultSet rs = ps.executeQuery();
                        try {
                            if (rs.next()) {
                                int currentStatus = rs.getInt(1);
                                if (InvitationData.StatusFieldValue.fromTypeCode(currentStatus) != InvitationData.StatusFieldValue.NO_RESPONSE) {
                                    throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.LOG_INVITATION_RESPONSE_NOT_ALLOWED, invitationId);
                                }
                            } else {
                                throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.LOG_INVITATION_RESPONSE_NOT_ALLOWED, invitationId);
                            }
                            Object var17_22 = null;
                        }
                        catch (Throwable throwable) {
                            Object var17_23 = null;
                            rs.close();
                            throw throwable;
                        }
                        rs.close();
                        Object var19_25 = null;
                    }
                    catch (Throwable throwable) {
                        Object var19_26 = null;
                        ps.close();
                        throw throwable;
                    }
                    ps.close();
                    {
                    }
                }
                String insertSql = "INSERT INTO INVITATIONRESPONSE(invitationId,responseTime,responseType,username) VALUES (?,?,?,?)";
                ps = ch.getConnection().prepareStatement("INSERT INTO INVITATIONRESPONSE(invitationId,responseTime,responseType,username) VALUES (?,?,?,?)", 1);
                try {
                    ps.setInt(1, invitationId);
                    ps.setTimestamp(2, actionTimestamp);
                    ps.setInt(3, responseType.getTypeCode());
                    ps.setString(4, username);
                    ps.addBatch();
                    if (responseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED && invitationData.type != InvitationData.ActivityType.JOIN_MIG33) {
                        ps.setInt(1, invitationId);
                        ps.setTimestamp(2, actionTimestamp);
                        ps.setInt(3, InvitationResponseData.ResponseType.ACCEPT_INVITATION.getTypeCode());
                        ps.setString(4, username);
                        ps.addBatch();
                    }
                    int[] batchResult = ps.executeBatch();
                    for (int i = 0; i < batchResult.length; ++i) {
                        if (batchResult[i] == 1) continue;
                        throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.LOG_INVITATION_RESPONSE_FAILED, responseType, username, invitationId);
                    }
                    ResultSet rs = ps.getGeneratedKeys();
                    try {
                        if (!rs.next()) {
                            throw new EJBExceptionWithErrorCause(ErrorCause.InvitationErrorReasonType.LOG_INVITATION_RESPONSE_FAILED, responseType, username, invitationId);
                        }
                        invitationResponseData.id = rs.getLong(1);
                        Object var21_28 = null;
                    }
                    catch (Throwable throwable) {
                        Object var21_29 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var23_31 = null;
                }
                catch (Throwable throwable) {
                    Object var23_32 = null;
                    ps.close();
                    throw throwable;
                }
                ps.close();
                Object var25_34 = null;
            }
            catch (Throwable throwable) {
                Object var25_35 = null;
                ch.close();
                throw throwable;
            }
            ch.close();
            {
            }
        }
        catch (SQLException ex) {
            throw new EJBExceptionWithErrorCause(ex, ErrorCause.InvitationErrorReasonType.LOG_INVITATION_RESPONSE_FAILED, responseType, username, invitationId);
        }
        return invitationResponseData;
    }

    private void postLogInvitationResponse(InvitationData invitationData, String inviteeUserName, InvitationResponseData invitationResponseData) {
        int inviterId = invitationData.inviterUserId;
        UserData inviterUserData = this.loadUserFromID(inviterId);
        boolean loadInviteeFromMaster = SystemPropertyEntities.Temp.Cache.se423LoadInviteeUsesMasterDB.getValue();
        if (log.isDebugEnabled()) {
            log.debug((Object)("postLogInvitationResponse:loadInviteeFromMasterDB:" + loadInviteeFromMaster));
        }
        UserData inviteeUserData = this.loadUser(inviteeUserName, false, loadInviteeFromMaster);
        if (invitationResponseData.responseType == InvitationResponseData.ResponseType.ACCEPT_INVITATION) {
            this.sendMigAlertToInviterWhenInviteeAcceptInvitation(inviterUserData, inviteeUserData);
        } else if (invitationResponseData.responseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED || invitationResponseData.responseType == InvitationResponseData.ResponseType.LOGIN_USING_EXISTING_ACCOUNT) {
            try {
                String postData;
                String pathPrefix;
                MigboApiUtil apiUtil;
                if (SystemProperty.getBool(SystemPropertyEntities.Invitation.ENABLE_REFERRAL_ACK_EMAIL) && !(apiUtil = MigboApiUtil.getInstance()).postAndCheckOk(pathPrefix = String.format("/user/%s/email/%s?follower=%s", inviterUserData.userID, Enums.EmailTypeEnum.REFERRAL_ACK.value(), inviteeUserData.username), postData = "{}")) {
                    log.warn((Object)String.format("Failed to send referral ack email to inviter:%s, invitee:%s", inviterUserData.userID, inviteeUserData.userID));
                }
                if (SystemProperty.getBool(SystemPropertyEntities.Invitation.ENABLE_MUTUAL_FOLLOWING_FOR_ALL_INVITERS)) {
                    this.addMutualFollowingAndTriggerMigAlertsToAllInviters(invitationData, inviteeUserData);
                } else {
                    this.addMutualFollowingAndTriggerMigAlerts(inviterUserData, inviteeUserData);
                }
            }
            catch (Exception e) {
                log.error((Object)("addMutualFollowingAndTriggerMigAlerts failed for inviter userId:[" + invitationData.inviterUserId + "], invitee username [" + inviteeUserData.username + "].Error message:" + e.getMessage()), (Throwable)e);
            }
        }
        this.triggerMarketingMechanicsForInvitation(inviterUserData, inviteeUserData, invitationData, invitationResponseData);
    }

    private void triggerMarketingMechanicsForInvitation(UserData inviterUserData, UserData inviteeUserData, InvitationData invitationData, InvitationResponseData invitationResponseData) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Invitation.MARKETING_MECHANICS_ENABLED)) {
            return;
        }
        if (!SystemProperty.getBool(SystemPropertyEntities.Temp.ER78_ENABLED) && !UserReferrerCache.isWithinCapAllowed(inviterUserData, inviteeUserData)) {
            return;
        }
        if (invitationResponseData.responseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED && SystemProperty.getBool(SystemPropertyEntities.Invitation.MARKETING_MECHANICS_FOR_REFERRAL_ENABLED)) {
            InvitationRespondedTrigger trigger;
            try {
                trigger = new InvitationRespondedTrigger(inviteeUserData, false, invitationData, invitationResponseData, inviterUserData);
                this.populateTriggerWithCampaignParticipationInfo(trigger, CampaignData.TypeEnum.INVITE_FRIENDS_TO_SIGN_UP);
                RewardCentre.getInstance().sendTrigger(trigger);
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
            try {
                trigger = new InvitationRespondedTrigger(inviterUserData, true, invitationData, invitationResponseData, inviteeUserData);
                this.populateTriggerWithCampaignParticipationInfo(trigger, CampaignData.TypeEnum.INVITE_FRIENDS_TO_SIGN_UP);
                RewardCentre.getInstance().sendTrigger(trigger);
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
        }
        if (invitationResponseData.responseType == InvitationResponseData.ResponseType.ACCEPT_INVITATION && SystemProperty.getBool(SystemPropertyEntities.Invitation.MARKETING_MECHANICS_FOR_ACCEPT_INVITATION_ENABLED)) {
            try {
                RewardCentre.getInstance().sendTrigger(new InvitationRespondedTrigger(inviteeUserData, false, invitationData, invitationResponseData, inviterUserData));
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
            try {
                RewardCentre.getInstance().sendTrigger(new InvitationRespondedTrigger(inviterUserData, true, invitationData, invitationResponseData, inviteeUserData));
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
        }
        if (invitationResponseData.responseType == InvitationResponseData.ResponseType.LOGIN_USING_EXISTING_ACCOUNT && SystemProperty.getBool(SystemPropertyEntities.Invitation.MARKETING_MECHANICS_FOR_LOGIN_USING_EXISTING_ACCOUNT_ENABLED)) {
            try {
                RewardCentre.getInstance().sendTrigger(new InvitationRespondedTrigger(inviteeUserData, false, invitationData, invitationResponseData, inviterUserData));
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
            try {
                RewardCentre.getInstance().sendTrigger(new InvitationRespondedTrigger(inviterUserData, true, invitationData, invitationResponseData, inviteeUserData));
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Invitation.MARKETING_MECHANICS_CHAIN_ENABLED) && invitationResponseData.responseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED && invitationData.type != InvitationData.ActivityType.JOIN_MIG33) {
            try {
                RewardCentre.getInstance().sendTrigger(new InvitationRespondedTrigger(inviteeUserData, false, invitationData, invitationResponseData, inviterUserData));
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
            try {
                RewardCentre.getInstance().sendTrigger(new InvitationRespondedTrigger(inviterUserData, true, invitationData, invitationResponseData, inviteeUserData));
            }
            catch (Exception e) {
                log.error((Object)("Unable to notify reward system to send InvitationRespondedTrigger." + e), (Throwable)e);
            }
        }
    }

    private void populateTriggerWithCampaignParticipationInfo(InvitationRespondedTrigger trigger, CampaignData.TypeEnum campaignType) throws DAOException {
        if (SystemPropertyEntities.Temp.Cache.se504Enabled.getValue().booleanValue()) {
            int userId = trigger.userData.userID;
            List<CampaignParticipantData> participationDataList = DAOFactory.getInstance().getCampaignDAO().getActiveCampaignParticipantDataByType(userId, campaignType.getEnumValue());
            if (log.isDebugEnabled()) {
                log.debug((Object)("UserId=[" + userId + "];isInviter=[" + trigger.isInviter() + "];participationDataList.size=[" + participationDataList.size() + "];participationDataList=[" + participationDataList + "]"));
            }
            Map<Integer, CampaignParticipation> participatedCampaigns = trigger.getParticipatedCampaigns();
            if (participationDataList != null) {
                for (CampaignParticipantData participantData : participationDataList) {
                    participatedCampaigns.put(participantData.getCampaignId(), participantData);
                }
            }
        } else if (log.isDebugEnabled()) {
            int userId = trigger.userData.userID;
            log.debug((Object)("Campaign participation data extraction disabled for userId [" + userId + "]"));
        }
    }

    public int getUserID(String username, Connection conn) throws EJBException {
        return this.getUserID(username, conn, true);
    }

    /*
     * Unable to fully structure code
     */
    public int getUserID(String username, Connection conn, boolean throwExceptionWhenNotFound) throws EJBException {
        block29: {
            if (StringUtil.isBlank(username)) {
                if (throwExceptionWhenNotFound) {
                    throw new EJBException("Invalid username " + username);
                }
                return -1;
            }
            ch = null;
            ps = null;
            rs = null;
            userID = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.USER_ID, username.toLowerCase());
            if (userID != null) ** GOTO lbl47
            ch = new ConnectionHolder(this.dataSourceSlave, conn);
            ps = ch.getConnection().prepareStatement("select id from userid where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) break block29;
            if (throwExceptionWhenNotFound) {
                throw new EJBException("Invalid username " + username);
            }
            var8_9 = -1;
            var10_11 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
            }
            catch (SQLException e) {
                ch = null;
            }
            return var8_9;
        }
        userID = rs.getInt("id");
        MemCachedHelper.setUsernameIdMapping(username, userID);
lbl47:
        // 2 sources

        var8_10 = userID;
        var10_12 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e) {
            ch = null;
        }
        return var8_10;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable var9_17) {
                var10_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw var9_17;
            }
        }
    }

    /*
     * Loose catch block
     */
    public Map<String, Integer> getLastLoggedInUsersWithVerifiedEmail(UserEmailAddressData.UserEmailAddressTypeEnum emailType, Timestamp minimum, Timestamp maximum, Connection conn) throws EJBException {
        if (minimum.after(maximum)) {
            throw new EJBException(String.format("Minimum Date [%s] should be less than Maximum Date [%s]", minimum, maximum));
        }
        ConnectionHolder ch = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ch = new ConnectionHolder(this.dataSourceSlave, conn);
        ps = ch.getConnection().prepareStatement("select uid.username as username, uid.id as id from userid uid, user u, useremailaddress ue where u.status=1 and u.username = uid.username and u.lastlogindate >= ? and u.lastlogindate < ? and ue.type = ? and ue.verified=1");
        log.info((Object)String.format("select uid.username as username, uid.id as id from userid uid, user u, useremailaddress ue where u.username = uid.username and u.lastlogindate >= %s and u.lastlogindate < %s and ue.type = %d and ue.verified=1", minimum.toString(), maximum.toString(), emailType.value));
        ps.setTimestamp(1, minimum);
        ps.setTimestamp(2, maximum);
        ps.setInt(3, emailType.value);
        rs = ps.executeQuery();
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        while (rs.next()) {
            result.put(rs.getString("username"), rs.getInt("id"));
        }
        HashMap<String, Integer> hashMap = result;
        Object var11_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return hashMap;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public UserData loadUserByUsernameOrAlias(String usernameOrAlias, boolean fullyLoadUserObject, boolean loadFromMasterDB) throws EJBException {
        UserData userData;
        block11: {
            String username;
            block10: {
                userData = this.loadUser(usernameOrAlias, fullyLoadUserObject, loadFromMasterDB);
                if (userData != null) break block11;
                log.info((Object)String.format("could not load user by username '%s', trying alias", usernameOrAlias));
                Connection conn = null;
                username = null;
                conn = loadFromMasterDB ? this.dataSourceMaster.getConnection() : this.dataSourceSlave.getConnection();
                username = this.getUsernameByAlias(usernameOrAlias, conn);
                Object var9_7 = null;
                try {
                    if (conn != null) {
                        conn.close();
                    }
                    break block10;
                }
                catch (SQLException e) {
                    conn = null;
                }
                break block10;
                {
                    catch (SQLException e) {
                        throw new EJBException(e.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    Object var9_8 = null;
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    catch (SQLException e) {
                        conn = null;
                    }
                    throw throwable;
                }
            }
            if (username == null) {
                return null;
            }
            log.info((Object)String.format("loading user by username '%s' from alias '%s'", username, usernameOrAlias));
            return this.loadUser(username, fullyLoadUserObject, loadFromMasterDB);
        }
        return userData;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public UserData loadUser(String username, boolean fullyLoadUserObject, boolean loadFromMasterDB) throws EJBException {
        block33: {
            block32: {
                block27: {
                    block31: {
                        block30: {
                            block26: {
                                conn = null;
                                ps = null;
                                rs = null;
                                try {
                                    try {
                                        conn = loadFromMasterDB != false ? this.dataSourceMaster.getConnection() : this.dataSourceSlave.getConnection();
                                        ps = conn.prepareStatement("select u.*, uid.id as uid, a.headuuid as avatar, a.bodyuuid as fullbodyavatar, ua.alias,uv.type accountType, uv.verified accountVerified, uv.description verifiedProfile, uea.emailaddress primaryEmail, uea.verified emailVerified from user u, userid uid LEFT OUTER JOIN avataruserbody a ON uid.id = a.userid and a.used = 1 LEFT OUTER JOIN useralias ua ON uid.username=ua.username LEFT OUTER JOIN userverified uv ON uv.userid = uid.id LEFT OUTER JOIN useremailaddress uea ON uea.userid = uid.id and uea.type = ? where u.username = uid.username and u.username = ? ");
                                        ps.setInt(1, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
                                        ps.setString(2, username);
                                        rs = ps.executeQuery();
                                        if (!rs.next() || !username.toLowerCase().equals(rs.getString("u.username"))) {
                                            var7_7 = null;
                                            var10_12 = null;
                                            break block26;
                                        }
                                        userData = new UserData(rs);
                                        if (fullyLoadUserObject) {
                                            rs.close();
                                            ps.close();
                                            conn.close();
                                            conn = this.dataSourceSlave.getConnection();
                                            userData.pendingContacts = this.loadPendingContacts(userData.username, conn);
                                            userData.blockList = this.loadBlockList(userData.username, conn);
                                            userData.broadcastList = this.checkAndLoadBroadcastList(userData.username, conn);
                                        }
                                        this.populateUserSettings(userData, conn);
                                        MemCachedHelper.setUserAlias(username, userData.userID, userData.alias);
                                        var8_18 = userData;
                                        break block27;
                                    }
                                    catch (SQLException e) {
                                        throw new EJBException(e.getMessage());
                                    }
                                    catch (CreateException e) {
                                        throw new EJBException(e.getMessage());
                                    }
                                    catch (NoSuchFieldException e) {
                                        throw new EJBException(e.getMessage());
                                    }
                                }
                                catch (Throwable var9_19) {
                                    var10_14 = null;
                                    try {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                    try {
                                        if (ps != null) {
                                            ps.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        ps = null;
                                    }
                                    try {
                                        if (conn == null) throw var9_19;
                                        conn.close();
                                        throw var9_19;
                                    }
                                    catch (SQLException e) {
                                        conn = null;
                                        throw var9_19;
                                    }
                                }
                            }
                            ** try [egrp 2[TRYBLOCK] [9 : 304->319)] { 
lbl62:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block30;
lbl65:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [10 : 324->339)] { 
lbl69:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block31;
lbl72:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var7_7;
                    }
                    if (conn == null) return var7_7;
                    conn.close();
                    return var7_7;
                }
                var10_13 = null;
                ** try [egrp 2[TRYBLOCK] [9 : 304->319)] { 
lbl84:
                // 1 sources

                if (rs != null) {
                    rs.close();
                }
                break block32;
lbl87:
                // 1 sources

                catch (SQLException e) {
                    rs = null;
                }
            }
            ** try [egrp 3[TRYBLOCK] [10 : 324->339)] { 
lbl91:
            // 1 sources

            if (ps != null) {
                ps.close();
            }
            break block33;
lbl94:
            // 1 sources

            catch (SQLException e) {
                ps = null;
            }
        }
        try {}
        catch (SQLException e) {
            return var8_18;
        }
        if (conn == null) return var8_18;
        conn.close();
        return var8_18;
    }

    /*
     * Loose catch block
     */
    public UserData loadUserFromID(int id) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select u.*, uid.id as uid, a.headuuid as avatar, a.bodyuuid as fullbodyavatar, ua.alias, uv.type accountType, uv.verified accountVerified, uv.description verifiedProfile, uea.emailaddress primaryEmail, uea.verified emailVerified from user u, userid uid LEFT OUTER JOIN avataruserbody a ON uid.id = a.userid and a.used = 1 LEFT OUTER JOIN useralias ua ON uid.username=ua.username LEFT OUTER JOIN userverified uv ON uv.userid = uid.id LEFT OUTER JOIN useremailaddress uea ON uea.userid = uid.id and uea.type = ? where u.username = uid.username and uid.id = ? ");
            ps.setInt(1, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
            ps.setInt(2, id);
            rs = ps.executeQuery();
            if (rs.next()) break block26;
            UserData userData = null;
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return userData;
        }
        UserData userData = new UserData(rs);
        this.populateUserSettings(userData, conn);
        MemCachedHelper.setUserAlias(userData.username, userData.userID, userData.alias);
        UserData userData2 = userData;
        Object var8_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return userData2;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public UserData loadUserFromMobilePhone(String mobilePhone) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block27: {
            if (StringUtil.isBlank(mobilePhone)) {
                throw new EJBException("Blank mobile phone not allowed");
            }
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select u.*, uid.id as uid, a.headuuid as avatar, a.bodyuuid as fullbodyavatar, ua.alias, uv.type accountType, uv.verified accountVerified, uv.description verifiedProfile, uea.emailaddress primaryEmail, uea.verified emailVerified from user u, userid uid LEFT OUTER JOIN avataruserbody a ON uid.id = a.userid and a.used = 1 LEFT OUTER JOIN useralias ua ON uid.username=ua.username LEFT OUTER JOIN userverified uv ON uv.userid = uid.id LEFT OUTER JOIN useremailaddress uea ON uea.userid = uid.id and uea.type = ? where u.username = uid.username and u.mobilephone = ? LIMIT 0,1");
            ps.setInt(1, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
            ps.setString(2, mobilePhone);
            rs = ps.executeQuery();
            if (rs.next()) break block27;
            UserData userData = null;
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return userData;
        }
        UserData userData = new UserData(rs);
        MemCachedHelper.setUserAlias(userData.username, userData.userID, userData.alias);
        UserData userData2 = userData;
        Object var8_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return userData2;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public UserData loadUserFromVoucherNumber(String voucherNumber) throws EJBException {
        try {
            VoucherLocal voucherEJB = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            VoucherData voucherData = voucherEJB.getVoucher(voucherNumber);
            if (voucherData == null) {
                throw new EJBException("Invalid voucher number " + voucherNumber);
            }
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            AccountEntryData accountEntryData = accountEJB.getAccountEntryFromReference(AccountEntryData.TypeEnum.VOUCHER_RECHARGE, voucherData.id.toString());
            if (accountEntryData == null) {
                return null;
            }
            return this.loadUser(accountEntryData.username, false, false);
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public void updateUserDetail(UserData userData) throws EJBException {
        block24: {
            Connection conn = null;
            Statement ps = null;
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(userData.username);
            UserData oldUserData = this.loadUser(userData.username, false, false);
            String statement = "update user set ";
            statement = statement + "emailaddress=?, onmailinglist=?, utcoffset=?, notes=?, type=?, chatroomadmin=?, chatroombans=?, emailactivated=?, emailalert=?, emailactivationdate=?, allowBuzz=?, bonusprogramid=? ";
            statement = statement + "where username=?";
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement(statement);
            ps.setString(1, userData.emailAddress);
            ps.setObject(2, userData.onMailingList == null ? null : Integer.valueOf(userData.onMailingList != false ? 1 : 0));
            ps.setObject(3, userData.UTCOffset);
            ps.setString(4, userData.notes);
            ps.setInt(5, userData.type.value());
            ps.setObject(6, userData.chatRoomAdmin == null ? null : Integer.valueOf(userData.chatRoomAdmin != false ? 1 : 0));
            ps.setInt(7, userData.chatRoomBans);
            ps.setObject(8, userData.emailActivated == null ? null : Integer.valueOf(userData.emailActivated != false ? 1 : 0));
            ps.setObject(9, userData.emailAlert == null ? null : Integer.valueOf(userData.emailAlert != false ? 1 : 0));
            ps.setTimestamp(10, userData.emailActivationDate == null ? null : new Timestamp(userData.emailActivationDate.getTime()));
            ps.setObject(11, userData.allowBuzz == null ? null : Integer.valueOf(userData.allowBuzz != false ? 1 : 0));
            ps.setObject(12, userData.bonusProgramID);
            ps.setString(13, userData.username);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to update user detail for " + userData.username);
            }
            if (userData.displayPicture == null && oldUserData.displayPicture != null || userData.displayPicture != null && !userData.displayPicture.equals(oldUserData.displayPicture)) {
                log.info((Object)("Updating displayPicture from [" + oldUserData.displayPicture + "] to [" + userData.displayPicture + "]"));
                this.updateDisplayPicture(userData.username, userData.displayPicture);
            } else {
                log.info((Object)("No change in displayPicture, old: [" + oldUserData.displayPicture + "] new: [" + userData.displayPicture + "]... skipping update"));
            }
            if (userPrx != null) {
                userPrx.userDetailChanged(userData.toIceObject());
            }
            EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(userData.username, UserDataUpdatedEvent.TypeEnum.USER_DETAIL));
            Object var8_9 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block24;
            {
                catch (LocalException e) {
                    Object var8_10 = null;
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block24;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block24;
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateOtherIMDetail(String username, ImType imType, String imUsername, String imPassword) throws EJBException {
        block49: {
            ResultSet rs;
            Statement psDeleteRow;
            Statement psGetIDs;
            Connection conn;
            block45: {
                UserPrx userPrx;
                UserData userData;
                conn = null;
                psGetIDs = null;
                psDeleteRow = null;
                rs = null;
                if (imUsername == null || imUsername.trim().length() == 0) {
                    imUsername = null;
                    imPassword = null;
                }
                if ((userData = this.loadUser(username, false, false)) == null) {
                    throw new EJBException("Invalid username " + username);
                }
                AuthenticationServicePrx authenticationServicePrx = EJBIcePrxFinder.getAuthenticationServiceProxy();
                PasswordType passwordType = PasswordType.forIMEnum(imType);
                Credential newCredential = new Credential(userData.userID, imUsername, imPassword, passwordType.value());
                AuthenticationServiceCredentialResponse response = authenticationServicePrx.getCredential(userData.userID, passwordType.value());
                if (response.code == AuthenticationServiceResponseCodeEnum.Success) {
                    authenticationServicePrx.removeCredential(response.userCredential);
                    if (imUsername != null) {
                        authenticationServicePrx.createCredential(newCredential);
                    }
                } else if (response.code == AuthenticationServiceResponseCodeEnum.UnknownCredential) {
                    if (imUsername != null) {
                        authenticationServicePrx.createCredential(newCredential);
                    }
                } else {
                    throw new EJBException("Authentication service error " + response.code);
                }
                if ((userPrx = EJBIcePrxFinder.findUserPrx(username)) != null) {
                    userPrx.userDetailChanged(userData.toIceObject());
                }
                if (imUsername == null) {
                    String sqlGetIDs;
                    switch (imType) {
                        case MSN: {
                            sqlGetIDs = "select id from contact where username = ? and msnusername is not null and fusionusername is null and aimusername is null and yahoousername is null and icqusername is null and jabberusername is null and emailaddress is null and mobilephone is null and homephone is null and officephone is null";
                            break;
                        }
                        case YAHOO: {
                            sqlGetIDs = "select id from contact where username = ? and yahoousername is not null and fusionusername is null and aimusername is null and msnusername is null and icqusername is null and jabberusername is null and emailaddress is null and mobilephone is null and homephone is null and officephone is null";
                            break;
                        }
                        case AIM: {
                            sqlGetIDs = "select id from contact where username = ? and aimusername is not null and fusionusername is null and yahoousername is null and msnusername is null and icqusername is null and jabberusername is null and emailaddress is null and mobilephone is null and homephone is null and officephone is null";
                            break;
                        }
                        case GTALK: {
                            sqlGetIDs = "select id from contact where username = ? and jabberusername is not null and fusionusername is null and yahoousername is null and msnusername is null and icqusername is null and aimusername is null and emailaddress is null and mobilephone is null and homephone is null and officephone is null";
                            break;
                        }
                        case FACEBOOK: {
                            sqlGetIDs = "select id from contact where username = ? and icqusername is not null and fusionusername is null and yahoousername is null and msnusername is null and  jabberusername is null and aimusername is null and emailaddress is null and mobilephone is null and homephone is null and officephone is null";
                            break;
                        }
                        default: {
                            throw new EJBException("Unknown IM type " + imType);
                        }
                    }
                    conn = this.dataSourceMaster.getConnection();
                    psGetIDs = conn.prepareStatement(sqlGetIDs);
                    psGetIDs.setString(1, username);
                    rs = psGetIDs.executeQuery();
                    psDeleteRow = conn.prepareStatement("delete from contact where id = ?");
                    while (rs.next()) {
                        psDeleteRow.setInt(1, rs.getInt(1));
                        if (psDeleteRow.executeUpdate() != 0) continue;
                        throw new EJBException("Unable to remove IM contact");
                    }
                    if (userPrx != null) {
                        userPrx.otherIMRemoved(imType.value());
                    }
                }
                if (imType != ImType.FACEBOOK) break block45;
                log.info((Object)"Adding ThirdPartySiteCredentialUpdatedEvent to clear migbo dataservice credential cache");
                EventQueue.enqueueSingleEvent(new ThirdPartySiteCredentialUpdatedEvent(userData.userID));
            }
            Object var17_19 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (psGetIDs != null) {
                    psGetIDs.close();
                }
            }
            catch (SQLException e) {
                psGetIDs = null;
            }
            try {
                if (psDeleteRow != null) {
                    psDeleteRow.close();
                }
            }
            catch (SQLException e) {
                psDeleteRow = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block49;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block49;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (FusionException e) {
                    throw new EJBException(e.message);
                }
                catch (LocalException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var17_20 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (psGetIDs != null) {
                        psGetIDs.close();
                    }
                }
                catch (SQLException e) {
                    psGetIDs = null;
                }
                try {
                    if (psDeleteRow != null) {
                        psDeleteRow.close();
                    }
                }
                catch (SQLException e) {
                    psDeleteRow = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void updateStatusMessage(int userID, String username, String statusMessage, ClientType deviceType, SSOEnums.View ssoView) throws EJBException {
        conn = null;
        ps = null;
        userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
        try {
            block22: {
                conn = this.dataSourceMaster.getConnection();
                statusMessage = StringUtil.stripHTML(statusMessage);
                timeStamp = System.currentTimeMillis();
                ps = conn.prepareStatement("update user set statusmessage=?, statustimestamp=? where username=? and !(statusmessage<=>?)");
                ps.setString(1, statusMessage);
                ps.setTimestamp(2, new Timestamp(timeStamp));
                ps.setString(3, username);
                ps.setString(4, statusMessage);
                if (ps.executeUpdate() == 1) break block22;
                var13_12 = null;
                ** GOTO lbl67
            }
            ps.close();
            eventList = new LinkedList<Event>();
            eventList.add(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.STATUS_MESSAGE));
            if (!StringUtil.isBlank(statusMessage) && SystemProperty.getBool("migboPostFromStatusUpdateEnabled", false)) {
                eventList.add(new StatusUpdateEvent(username, statusMessage, deviceType == null ? -1 : (int)deviceType.value(), ssoView == null ? -1 : ssoView.value()));
            }
            EventQueue.enqueueMultipleEvents(eventList);
            DisplayPictureAndStatusMessage.deleteDisplayPictureAndStatusMessage(UserBean.displayPictureAndStatusMessageMemcache, username);
            if (userPrx != null) {
                userPrx.userStatusMessageChanged(statusMessage, timeStamp);
            }
            ** GOTO lbl80
        }
        catch (LocalException e) {
            var13_14 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn == null) return;
                conn.close();
                return;
            }
            catch (SQLException e) {
                return;
            }
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        {
            block25: {
                block24: {
                    catch (Throwable var12_21) {
                        block23: {
                            var13_15 = null;
                            ** try [egrp 2[TRYBLOCK] [8 : 268->283)] { 
lbl54:
                            // 1 sources

                            if (ps != null) {
                                ps.close();
                            }
                            break block23;
lbl57:
                            // 1 sources

                            catch (SQLException e) {
                                ps = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [9 : 288->303)] { 
lbl61:
                        // 1 sources

                        if (conn == null) throw var12_21;
                        conn.close();
                        throw var12_21;
lbl64:
                        // 1 sources

                        catch (SQLException e) {
                            conn = null;
                        }
                        throw var12_21;
                    }
lbl67:
                    // 1 sources

                    ** try [egrp 2[TRYBLOCK] [8 : 268->283)] { 
lbl68:
                    // 1 sources

                    if (ps != null) {
                        ps.close();
                    }
                    break block24;
lbl71:
                    // 1 sources

                    catch (SQLException e) {
                        ps = null;
                    }
                }
                try {}
                catch (SQLException e) {
                    return;
                }
                if (conn == null) return;
                conn.close();
                return;
lbl80:
                // 1 sources

                var13_13 = null;
                ** try [egrp 2[TRYBLOCK] [8 : 268->283)] { 
lbl82:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block25;
lbl85:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (conn == null) return;
            conn.close();
            return;
        }
    }

    /*
     * Loose catch block
     */
    public void updateDisplayPicture(String username, String displayPicture) throws EJBException {
        block23: {
            if (displayPicture != null && !Pattern.matches("^[0-9a-zA-Z._-]*$", displayPicture)) {
                throw new EJBException("Invalid display picture [" + displayPicture + "]");
            }
            Connection conn = null;
            Statement ps = null;
            UserPrx userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
            conn = this.dataSourceMaster.getConnection();
            long timeStamp = System.currentTimeMillis();
            ps = conn.prepareStatement("set foreign_key_checks = 0");
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("update user set displaypicture=?, statustimestamp=? where username=?");
            ps.setString(1, displayPicture);
            ps.setTimestamp(2, new Timestamp(timeStamp));
            ps.setString(3, username);
            int rowsUpdated = ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement("set foreign_key_checks = 1");
            ps.execute();
            ps.close();
            if (rowsUpdated != 1) {
                throw new EJBException("Failed to update display picture for " + username);
            }
            DisplayPictureAndStatusMessage.deleteDisplayPictureAndStatusMessage(displayPictureAndStatusMessageMemcache, username);
            URLUtil.purgeVanishCache("/u/" + username);
            if (userPrx != null) {
                userPrx.userDisplayPictureChanged(displayPicture, timeStamp);
            }
            EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.DISPLAY_PICTURE));
            Object var10_10 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block23;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block23;
            {
                catch (LocalException e) {
                    Object var10_11 = null;
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block23;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block23;
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var10_12 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void removeDisplayPicture(String displayPicture) throws EJBException {
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select username from user where displaypicture = ?");
            ps.setString(1, displayPicture);
            rs = ps.executeQuery();
            while (rs.next()) {
                this.updateDisplayPicture(rs.getString("username"), null);
            }
            Object var7_5 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public UserProfileData.StatusEnum getUserProfileStatus(String username) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        UserProfileData.StatusEnum status = (UserProfileData.StatusEnum)((Object)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE_STATUS, username));
        if (status == null) {
            Integer intVal;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select status from userprofile where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next() && (intVal = (Integer)rs.getObject("status")) != null) {
                status = UserProfileData.StatusEnum.fromValue(intVal);
            }
            if (status == null) {
                status = UserProfileData.StatusEnum.PRIVATE;
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE_STATUS, username, (Object)status);
        }
        UserProfileData.StatusEnum statusEnum = status;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return statusEnum;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    public UserProfileData getUserProfile(String requestingUsername, String targetUsername, boolean checkAccessRight) throws EJBException, FusionEJBException {
        block31: {
            conn = null;
            ps = null;
            rs = null;
            targetUserProfileData = (UserProfileData)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE, targetUsername);
            if (targetUserProfileData != null) ** GOTO lbl45
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from userprofile where username = ?");
            ps.setString(1, targetUsername);
            rs = ps.executeQuery();
            if (rs.next()) break block31;
            var8_9 = null;
            var10_11 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            return var8_9;
        }
        targetUserProfileData = new UserProfileData(rs);
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE, targetUsername, targetUserProfileData);
        rs.close();
        ps.close();
        conn.close();
lbl45:
        // 2 sources

        if (checkAccessRight && !requestingUsername.equals(targetUsername)) {
            if (UserProfileData.StatusEnum.PRIVATE.equals((Object)targetUserProfileData.status)) {
                UserBean.log.info((Object)("Private profile of [" + targetUsername + "] viewed by [" + requestingUsername + "]"));
                throw new FusionEJBException("The profile you have selected is 'Private'. Only the user can view this profile.");
            }
            if (UserProfileData.StatusEnum.CONTACTS_ONLY.equals((Object)targetUserProfileData.status)) {
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select id from contact where username = ? and fusionusername = ?");
                ps.setString(1, targetUsername);
                ps.setString(2, requestingUsername);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    ps = conn.prepareStatement("select username from pendingcontact where username = ? and pendingcontact = ?");
                    ps.setString(1, requestingUsername);
                    ps.setString(2, targetUsername);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        UserBean.log.info((Object)("FriendsOnly profile of [" + targetUsername + "] viewed by [" + requestingUsername + "]"));
                        throw new FusionEJBException("The profile you have selected is 'Friends Only'. Only the user's friends can view this profile.");
                    }
                }
            }
        }
        var8_10 = targetUserProfileData;
        var10_12 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        return var8_10;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable var9_17) {
                var10_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw var9_17;
            }
        }
    }

    private void onProfileUpdated(String username) {
        try {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE, username);
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_PROFILE_STATUS, username);
        }
        catch (Exception e) {
            log.error((Object)("Unable to delete profile status from memcached user [" + username + "]"), (Throwable)e);
        }
        EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.PROFILE));
    }

    /*
     * Loose catch block
     */
    public boolean updateUserProfile(UserProfileData userProfileData) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        UserProfileData oldProfileData = null;
        try {
            oldProfileData = this.getUserProfile(userProfileData.username, userProfileData.username, true);
        }
        catch (Exception e) {
            oldProfileData = null;
        }
        boolean needToUpdate = true;
        if (oldProfileData == null) {
            ps = conn.prepareStatement("insert into userprofile (firstname, lastname, hometown, city, state, dateofbirth, gender, jobs, schools, hobbies, likes, dislikes, aboutme, relationshipstatus, status, username) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
        } else if (userProfileData.isDifferent(oldProfileData)) {
            ps = conn.prepareStatement("update userprofile set firstname=?, lastname=?, hometown=?, city=?, state=?, dateofbirth=?, gender=?, jobs=?, schools=?, hobbies=?, likes=?, dislikes=?, aboutme=?, relationshipstatus=?, status=? where username=?");
        } else {
            needToUpdate = false;
        }
        if (needToUpdate) {
            if (userProfileData.status == null) {
                userProfileData.status = UserProfileData.StatusEnum.PUBLIC;
            }
            ps.setString(1, userProfileData.firstName);
            ps.setString(2, userProfileData.lastName);
            ps.setString(3, userProfileData.homeTown);
            ps.setString(4, userProfileData.city);
            ps.setString(5, userProfileData.state);
            ps.setTimestamp(6, userProfileData.dateOfBirth == null ? null : new Timestamp(userProfileData.dateOfBirth.getTime()));
            ps.setString(7, userProfileData.gender == null ? null : userProfileData.gender.value());
            ps.setString(8, userProfileData.jobs);
            ps.setString(9, userProfileData.schools);
            ps.setString(10, userProfileData.hobbies);
            ps.setString(11, userProfileData.likes);
            ps.setString(12, userProfileData.dislikes);
            ps.setString(13, userProfileData.aboutMe);
            ps.setObject(14, userProfileData.relationshipStatus == null ? null : Integer.valueOf(userProfileData.relationshipStatus.value()));
            ps.setObject(15, userProfileData.status == null ? null : Integer.valueOf(userProfileData.status.value()));
            ps.setString(16, userProfileData.username);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to update user profile for " + userProfileData.username);
            }
            if (oldProfileData == null) {
                rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    throw new EJBException("Failed to create user profile for " + userProfileData.username);
                }
                userProfileData.id = rs.getInt(1);
                rs.close();
            } else {
                userProfileData.id = oldProfileData.id;
            }
            ps.close();
            this.onProfileUpdated(userProfileData.username);
            Enums.UserProfileKeywordEnum[] types = new Enums.UserProfileKeywordEnum[]{Enums.UserProfileKeywordEnum.JOBS, Enums.UserProfileKeywordEnum.SCHOOLS, Enums.UserProfileKeywordEnum.HOBBIES, Enums.UserProfileKeywordEnum.LIKES, Enums.UserProfileKeywordEnum.DISLIKES};
            String[] newKeywords = new String[]{userProfileData.jobs, userProfileData.schools, userProfileData.hobbies, userProfileData.likes, userProfileData.dislikes};
            String[] oldKeywords = oldProfileData == null ? new String[]{"", "", "", "", ""} : new String[]{oldProfileData.jobs, oldProfileData.schools, oldProfileData.hobbies, oldProfileData.likes, oldProfileData.dislikes};
            ps = conn.prepareStatement("delete from userprofilekeyword where userprofileid = ? and type = ?");
            ps.setInt(1, userProfileData.id);
            for (int i = 0; i < types.length; ++i) {
                String newKeyword;
                String oldKeyword = oldKeywords[i] == null ? "" : oldKeywords[i];
                String string = newKeyword = newKeywords[i] == null ? "" : newKeywords[i];
                if (oldKeyword.equalsIgnoreCase(newKeyword)) continue;
                if (oldKeyword.length() > 0) {
                    ps.setInt(2, types[i].value());
                    ps.executeUpdate();
                }
                if (newKeyword.length() <= 0) continue;
                this.addUserProfileKeywords(conn, userProfileData.id, types[i], newKeyword.split(","));
            }
        }
        boolean bl = needToUpdate;
        Object var14_16 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var14_17 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addUserProfileKeywords(Connection conn, int profileID, Enums.UserProfileKeywordEnum type, String[] keywords) throws SQLException, EJBException {
        PreparedStatement psGetKeyword = null;
        PreparedStatement psAddKeyword = null;
        PreparedStatement psAddKeywordLink = null;
        ResultSet rs = null;
        try {
            psGetKeyword = conn.prepareStatement("select id from keyword where keyword = ?");
            psAddKeyword = conn.prepareStatement("insert into keyword (keyword) values (?)", 1);
            psAddKeywordLink = conn.prepareStatement("insert into userprofilekeyword (userprofileid, keywordid, type) values (?,?,?)");
            psAddKeywordLink.setInt(1, profileID);
            HashSet<String> keywordSet = new HashSet<String>();
            keywordSet.addAll(Arrays.asList(keywords));
            for (String keyword : keywordSet) {
                int keywordID;
                int len;
                if (keyword.length() <= 0 || (len = (keyword = keyword.trim()).length()) == 0) continue;
                if (len > 64) {
                    keyword = keyword.substring(0, 64);
                }
                psGetKeyword.setString(1, keyword);
                rs = psGetKeyword.executeQuery();
                if (rs.next()) {
                    keywordID = rs.getInt("id");
                    rs.close();
                } else {
                    psAddKeyword.setString(1, keyword);
                    psAddKeyword.executeUpdate();
                    rs.close();
                    rs = psAddKeyword.getGeneratedKeys();
                    if (!rs.next()) {
                        throw new EJBException("Failed to add keyword " + keyword + " to database");
                    }
                    keywordID = rs.getInt(1);
                    rs.close();
                }
                psAddKeywordLink.setInt(2, keywordID);
                psAddKeywordLink.setInt(3, type.value());
                if (psAddKeywordLink.executeUpdate() == 1) continue;
                throw new EJBException("Failed to link keyword " + keyword + " to user profile");
            }
            Object var15_14 = null;
        }
        catch (Throwable throwable) {
            Object var15_15 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (psGetKeyword != null) {
                    psGetKeyword.close();
                }
            }
            catch (SQLException e) {
                psGetKeyword = null;
            }
            try {
                if (psAddKeyword != null) {
                    psAddKeyword.close();
                }
            }
            catch (SQLException e) {
                psAddKeyword = null;
            }
            try {
                if (psAddKeywordLink != null) {
                    psAddKeywordLink.close();
                }
            }
            catch (SQLException e) {
                psAddKeywordLink = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (psGetKeyword != null) {
                psGetKeyword.close();
            }
        }
        catch (SQLException e) {
            psGetKeyword = null;
        }
        try {
            if (psAddKeyword != null) {
                psAddKeyword.close();
            }
        }
        catch (SQLException e) {
            psAddKeyword = null;
        }
        try {
            if (psAddKeywordLink != null) {
                psAddKeywordLink.close();
            }
        }
        catch (SQLException e) {
            psAddKeywordLink = null;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void activateAccount(String username, String verificationCode, boolean loadUserFromMasterDb, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        conn = null;
        ps = null;
        rs = null;
        userPrx = EJBIcePrxFinder.findUserPrx(username);
        try {
            block46: {
                userData = this.loadUser(username, false, loadUserFromMasterDb);
                if (userData == null) {
                    throw new EJBException("Invalid user " + username);
                }
                if (userData.mobilePhone == null) {
                    throw new EJBException("User does not have a mobile phone number");
                }
                if (!userData.mobileVerified.booleanValue()) break block46;
                var19_11 = null;
                ** GOTO lbl145
            }
            if (userData.failedActivationAttempts >= SystemProperty.getInt("MaxActivationAttempts")) {
                throw new EJBException("Too many failed authentication attempts");
            }
            if (!userData.verificationCode.equals(verificationCode)) {
                userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                userEJB.activateAccountFailed(username);
                throw new EJBException("Incorrect code entered");
            }
            conn = this.dataSourceMaster.getConnection();
            affiliateReferralDate = new Timestamp(0L);
            ps = conn.prepareStatement("select affiliateid, datecreated from affiliatereferral where mobilephone = ? order by datecreated desc");
            ps.setString(1, userData.mobilePhone);
            rs = ps.executeQuery();
            if (rs.next()) {
                userData.affiliateID = rs.getInt("affiliateId");
                userData.referralLevel = 1;
                affiliateReferralDate = rs.getTimestamp("datecreated");
            }
            rs.close();
            ps.close();
            referrerUserID = null;
            ps = conn.prepareStatement("select uid.id, u.username, u.countryid, u.affiliateid, u.referrallevel from userreferral ur inner join user u on (ur.username = u.username) inner join userid uid on (u.username = uid.username) where ur.mobilephone = ? and ur.datecreated > ? order by ur.datecreated desc");
            ps.setString(1, userData.mobilePhone);
            ps.setTimestamp(2, affiliateReferralDate);
            rs = ps.executeQuery();
            if (rs.next()) {
                referrerUserID = rs.getInt("id");
                userData.affiliateID = (Integer)rs.getObject("affiliateid");
                userData.referredBy = rs.getString("username");
                userData.referralLevel = (Integer)rs.getObject("referrallevel");
                if (userData.referralLevel == null) {
                    userData.referralLevel = 1;
                } else {
                    var12_20 = userData;
                    var12_20.referralLevel = var12_20.referralLevel + 1;
                }
            }
            userData.mobileVerified = true;
            ps = conn.prepareStatement("update user set mobileverified = 1, affiliateId = ?, referredBy = ?, referralLevel = ? where username = ? and verificationcode = ? and mobileverified = 0 and failedactivationattempts < ?");
            ps.setObject(1, userData.affiliateID);
            ps.setString(2, userData.referredBy);
            ps.setObject(3, userData.referralLevel);
            ps.setString(4, userData.username);
            ps.setString(5, verificationCode);
            ps.setInt(6, SystemProperty.getInt("MaxActivationAttempts"));
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Too many failed authentication attempts");
            }
            ps.close();
            ps = conn.prepareStatement("select id from activation where username = ? or mobilephone = ? limit 1");
            ps.setString(1, userData.username);
            ps.setString(2, userData.mobilePhone);
            rs = ps.executeQuery();
            firstActivation = rs.next() == false;
            rs.close();
            ps.close();
            ps = conn.prepareStatement("insert into activation (username, mobilephone, datecreated, ipaddress) values (?,?,?,?)", 1);
            ps.setString(1, username);
            ps.setString(2, userData.mobilePhone);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, accountEntrySourceData.ipAddress);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new EJBException("Failed to log authentication attempt");
            }
            activationID = rs.getInt(1);
            rs.close();
            ps.close();
            accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            if (firstActivation) {
                if (!this.isUserEmailVerified(userData.username)) {
                    accountEJB.giveActivationCredit(userData.username, userData.countryID, Integer.toString(activationID), accountEntrySourceData);
                }
                if (referrerUserID != null) {
                    try {
                        referrerUserData = this.loadUserFromID(referrerUserID);
                        trigger = new UserReferralActivationTrigger(referrerUserData);
                        trigger.quantityDelta = 1;
                        trigger.amountDelta = 0.0;
                        RewardCentre.getInstance().sendTrigger(trigger);
                        Leaderboard.updateReferrerLeaderboards(userData.referredBy, referrerUserID);
                    }
                    catch (Exception e) {
                        UserBean.log.warn((Object)"Unable to notify reward system to send UserReferallActivationTrigger", (Throwable)e);
                    }
                }
            }
            try {
                accountEJB.activatePendingMerchantTag(conn, userData.username);
            }
            catch (EJBException e) {
                UserBean.log.warn((Object)("Unable to activate merchant tag for user: " + username));
            }
            conn.close();
            if (userPrx != null) {
                userPrx.userDetailChanged(userData.toIceObject());
            }
            v0 = doInAsync = SystemProperty.getBool(SystemPropertyEntities.Registration.DO_SYNCHRONOUS_PROFILE_CACHE_INVALIDATION_ON_MIGBO_UPON_MOBILE_AUTH) == false;
            if (!doInAsync) {
                try {
                    api = MigboApiUtil.getInstance();
                    obj = api.delete(String.format("/user/%d/cache/profile", new Object[]{userData.userID}));
                    UserBean.log.debug((Object)String.format("Received JSON Response from migbo-datsvc : %s ", new Object[]{obj.toString()}));
                }
                catch (Exception e) {
                    UserBean.log.error((Object)String.format("Exception caught while invalidating profile cache on migbo for user [%s] due to mobile authentication, falling back to async via EventQueueWorker", new Object[]{username}), (Throwable)e);
                    doInAsync = true;
                }
            }
            if (doInAsync) {
                EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.PROFILE));
            }
            ** GOTO lbl165
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        {
            block50: {
                block49: {
                    block48: {
                        block47: {
                            catch (Throwable var18_31) {
                                var19_13 = null;
                                try {
                                    if (rs != null) {
                                        rs.close();
                                    }
                                }
                                catch (SQLException e) {
                                    rs = null;
                                }
                                try {
                                    if (ps != null) {
                                        ps.close();
                                    }
                                }
                                catch (SQLException e) {
                                    ps = null;
                                }
                                try {
                                    if (conn == null) throw var18_31;
                                    conn.close();
                                    throw var18_31;
                                }
                                catch (SQLException e) {
                                    conn = null;
                                }
                                throw var18_31;
                            }
lbl145:
                            // 1 sources

                            ** try [egrp 5[TRYBLOCK] [8 : 1141->1156)] { 
lbl146:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block47;
lbl149:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 6[TRYBLOCK] [9 : 1161->1176)] { 
lbl153:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block48;
lbl156:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return;
                    }
                    if (conn == null) return;
                    conn.close();
                    return;
lbl165:
                    // 1 sources

                    var19_12 = null;
                    ** try [egrp 5[TRYBLOCK] [8 : 1141->1156)] { 
lbl167:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block49;
lbl170:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 6[TRYBLOCK] [9 : 1161->1176)] { 
lbl174:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block50;
lbl177:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (conn == null) return;
            conn.close();
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean mobileActivated(Connection conn, String mobilePhone) throws SQLException {
        boolean bl;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("select id from activation where mobilephone = ?");
            ps.setString(1, mobilePhone);
            rs = ps.executeQuery();
            bl = rs.next();
            Object var7_6 = null;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        return bl;
    }

    /*
     * Loose catch block
     */
    public void inviteFriend(String username, String displayName, String mobilePhone, Integer groupID, String groupName, String gameName, String hashKey, AccountEntrySourceData accountEntrySourceData) throws EJBException, FusionEJBException {
        block56: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            if (displayName == null || displayName.length() == 0) {
                throw new FusionEJBException("Invalid referrer name");
            }
            UserData userData = this.loadUser(username = username.toLowerCase().trim(), false, false);
            if (userData == null) {
                log.error((Object)String.format("Unable to invite friend - inviter user '%s' does not exist, displayName '%s' mobilePhone %s", username, displayName, mobilePhone));
                throw new FusionEJBException("Invalid referrer username");
            }
            if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.INVITE_FRIEND, userData)) {
                throw new FusionEJBException("You must authenticate your account before inviting friends");
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            Integer iddCode = messageEJB.getIDDCode(mobilePhone = messageEJB.cleanAndValidatePhoneNumber(mobilePhone, true));
            if (iddCode == null) {
                throw new FusionEJBException("Unable to determine IDD code for phone number " + mobilePhone);
            }
            if (!messageEJB.isMobileNumber(mobilePhone, true)) {
                throw new FusionEJBException("Invalid mobile number " + mobilePhone);
            }
            MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            CountryData countryData = misEJB.getCountryByIDDCode(iddCode, mobilePhone);
            if (countryData == null) {
                throw new FusionEJBException("Unable to determine country for IDD code " + iddCode);
            }
            double referralCredit = countryData.referralCredit == null ? 0.0 : countryData.referralCredit;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select username from user where mobilephone=?");
            ps.setString(1, mobilePhone);
            rs = ps.executeQuery();
            if (rs.next()) {
                throw new FusionEJBException("The mobile number belongs to a registered migme user, " + rs.getString("username") + ". Please go back to Contacts and add " + rs.getString("username") + " as a contact");
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select mobilephone,registrationdevice from user where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new FusionEJBException("Invalid user " + username);
            }
            String inviterMobilePhone = rs.getString("mobilephone");
            if (inviterMobilePhone != null && inviterMobilePhone.equals(mobilePhone)) {
                throw new FusionEJBException("You cannot refer yourself");
            }
            String registrationdevice = rs.getString("registrationdevice");
            rs.close();
            ps.close();
            if (this.getMobilePhoneCount(conn, mobilePhone) > 0) {
                throw new FusionEJBException("Mobile number cannot be used now.");
            }
            boolean referredViaGame = false;
            String smsText = null;
            String inviterContact = inviterMobilePhone;
            if (inviterContact == null) {
                inviterContact = userData.emailAddress;
            }
            if (accountEntrySourceData != null) {
                if (!StringUtil.isBlank(gameName) && !StringUtil.isBlank(hashKey)) {
                    referredViaGame = true;
                    smsText = MessageFormat.format(SystemProperty.get("GameReferralSMS"), StringUtil.truncateWithEllipsis(username, 18), StringUtil.truncateWithEllipsis(displayName, 30), StringUtil.truncateWithEllipsis(gameName, 30), hashKey);
                } else if (groupID == null || groupName == null) {
                    smsText = SystemProperty.get("ReferralSMS");
                    if (registrationdevice != null) {
                        ps = conn.prepareStatement("select id,smsmsg from partnerbuild where useragent=?");
                        ps.setString(1, registrationdevice);
                        rs = ps.executeQuery();
                        if (rs.next() && org.springframework.util.StringUtils.hasLength((String)rs.getString("smsmsg"))) {
                            smsText = rs.getString("smsmsg");
                            smsText = smsText.replaceAll("%4", String.valueOf(rs.getInt("id")));
                        }
                    }
                } else {
                    ps = conn.prepareStatement("select referralsms from groups where id=?");
                    ps.setInt(1, groupID);
                    rs = ps.executeQuery();
                    if (rs.next() && org.springframework.util.StringUtils.hasLength((String)rs.getString("referralsms"))) {
                        smsText = rs.getString("referralsms");
                    }
                    rs.close();
                    ps.close();
                    if (smsText == null) {
                        smsText = SystemProperty.get("GroupReferralSMS").replaceAll("%4", groupName).replaceAll("%5", groupID.toString());
                    }
                }
                smsText = smsText.replaceAll("%1", displayName).replaceAll("%2", inviterContact).replaceAll("%3", mobilePhone);
                if (smsText.length() > 160) {
                    throw new FusionEJBException("The name you entered is too long");
                }
                int smsCount = messageEJB.getSystemSMSCount(SystemSMSData.SubTypeEnum.USER_REFERRAL, username, mobilePhone);
                if (smsCount >= SystemProperty.getInt("MaxUserReferralRetryPerDay")) {
                    throw new FusionEJBException("You have reached the limit for referring the same number for today. We have sent an SMS to your friend telling them about migme, and you will receive bonus credit when they authenticate their migme account");
                }
                ps = conn.prepareStatement("select count(*) from userreferral where username = ? and datecreated > curdate()");
                ps.setString(1, username);
                rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) >= SystemProperty.getInt("MaxUserReferralPerDay")) {
                    throw new FusionEJBException("You have reached the limit of referrals for today. We have sent SMS to your friends telling them about migme, and you will receive bonus credit when they authenticate their migme accounts");
                }
                rs.close();
                ps.close();
                ps = conn.prepareStatement("select count(*) from systemsms where subtype = ? and destination = ? and datecreated > curdate()");
                ps.setInt(1, SystemSMSData.SubTypeEnum.USER_REFERRAL.value());
                ps.setString(2, mobilePhone);
                rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) >= SystemProperty.getInt("MaxUserReferralPerMobilePhone")) {
                    throw new FusionEJBException("The mobile number " + mobilePhone + " cannot receive any more invitation today");
                }
                rs.close();
                ps.close();
                int longerLimitPerMobile = SystemProperty.getInt("MaxUserReferralPerMobilePhone90Days", -1);
                if (longerLimitPerMobile > 0) {
                    ps = conn.prepareStatement("select count(*) from (select destination from systemsms where subtype = ? and destination = ? and datecreated > now() - interval 1 month limit ?) s");
                    ps.setInt(1, SystemSMSData.SubTypeEnum.USER_REFERRAL.value());
                    ps.setString(2, mobilePhone);
                    ps.setInt(3, longerLimitPerMobile);
                    rs = ps.executeQuery();
                    rs.next();
                    if (rs.getInt(1) >= longerLimitPerMobile) {
                        throw new FusionEJBException("The mobile number " + mobilePhone + " has already been invited and cannot receive further invitations");
                    }
                    rs.close();
                    ps.close();
                }
            }
            ps = conn.prepareStatement("select id from userreferral where username = ? and mobilephone = ?");
            ps.setString(1, username);
            ps.setString(2, mobilePhone);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                rs.close();
                ps.close();
                ps = conn.prepareStatement("update userreferral set datecreated=?, amount=? where id=?");
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setDouble(2, referralCredit);
                ps.setInt(3, id);
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Failed to update user referral for " + mobilePhone);
                }
            } else {
                rs.close();
                ps.close();
                ps = conn.prepareStatement("insert into userreferral (username, referrername, datecreated, mobilephone, amount, paid) values (?, ?, ?, ?, ?, ?)");
                ps.setString(1, username);
                ps.setString(2, displayName);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setString(4, mobilePhone);
                ps.setDouble(5, referralCredit);
                ps.setInt(6, 0);
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Failed to insert user referral for " + mobilePhone);
                }
            }
            if (accountEntrySourceData != null && SMSControl.isSendEnabledForSubtype(referredViaGame ? SystemSMSData.SubTypeEnum.USER_REFERRAL_VIA_GAMES : SystemSMSData.SubTypeEnum.USER_REFERRAL, username)) {
                SystemSMSData systemSMSData = new SystemSMSData();
                systemSMSData.username = username;
                systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                systemSMSData.subType = referredViaGame ? SystemSMSData.SubTypeEnum.USER_REFERRAL_VIA_GAMES : SystemSMSData.SubTypeEnum.USER_REFERRAL;
                systemSMSData.source = inviterContact;
                systemSMSData.destination = mobilePhone;
                systemSMSData.messageText = smsText;
                messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
            }
            try {
                this.getUserReferralSuccessRate(username, true);
            }
            catch (Exception e) {
                log.warn((Object)("Unable to update user referral success rate for user : [" + username + "]"), (Throwable)e);
            }
            Object var27_30 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block56;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block56;
            {
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (NoSuchFieldException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var27_31 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public void sendVerificationCode(String username, String password, String registrationIP, String mobilePhone, String verificationCode, String messagePattern, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.ACTIVATION_CODE, username)) {
            return;
        }
        if (StringUtil.isBlank(messagePattern)) {
            throw new EJBException("Empty messagePattern provided for verification code SMS");
        }
        try {
            SystemSMSData systemSMSData = new SystemSMSData();
            systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
            systemSMSData.subType = SystemSMSData.SubTypeEnum.ACTIVATION_CODE;
            systemSMSData.username = username;
            systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
            systemSMSData.destination = mobilePhone;
            systemSMSData.messageText = messagePattern.replaceAll("%1", verificationCode).replaceAll("%2", username).replaceAll("%3", password);
            systemSMSData.registrationIP = registrationIP;
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
        catch (NoSuchFieldException e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public void resendVerificationCode(String username, String mobilePhone, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block30: {
            ResultSet rs;
            Statement ps;
            Connection conn;
            block27: {
                conn = null;
                ps = null;
                rs = null;
                MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                mobilePhone = messageEJB.cleanAndValidatePhoneNumber(mobilePhone, true);
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select password, mobilephone, mobileverified, verificationcode, registrationipaddress from user where username = ?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EJBException("Invalid user " + username);
                }
                if (rs.getInt("mobileverified") == 1) {
                    throw new EJBException("Account is already authenticated");
                }
                int smsCount = messageEJB.getSystemSMSCount(SystemSMSData.SubTypeEnum.ACTIVATION_CODE, username);
                if (smsCount >= SystemProperty.getInt("MaxVerificationCodeRequestPerDay")) {
                    throw new EJBException("You have already requested your authentication code today. If you do not receive the SMS containing your code, please email contact@mig.me");
                }
                if (!mobilePhone.equals(rs.getString("mobilephone"))) {
                    this.changeMobilePhone(username, mobilePhone, accountEntrySourceData);
                    break block27;
                }
                String verificationCode = rs.getString("verificationcode");
                if (verificationCode == null) {
                    throw new EJBException("No verification code available");
                }
                if (!messageEJB.isMobileNumber(mobilePhone, true)) {
                    throw new EJBException("Invalid mobile number " + mobilePhone);
                }
                String messagePattern = SystemProperty.get("ResendVerificationCodeSMS");
                this.sendVerificationCode(username, rs.getString("password"), rs.getString("registrationipaddress"), mobilePhone, verificationCode, messagePattern, accountEntrySourceData);
            }
            Object var12_14 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block30;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block30;
            {
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (NoSuchFieldException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public void sendMerchantActivatedUserSMS(String username, String password, String mobilePhone, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        try {
            if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.MERCHANT_USER_ACTIVATION, username)) {
                return;
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            if (messageEJB.isMobileNumber(mobilePhone, true)) {
                SystemSMSData systemSMSData = new SystemSMSData();
                systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                systemSMSData.subType = SystemSMSData.SubTypeEnum.MERCHANT_USER_ACTIVATION;
                systemSMSData.username = username;
                systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
                systemSMSData.destination = mobilePhone;
                systemSMSData.messageText = SystemProperty.get("MerchantActivatedUserSMS").replaceAll("%1", username).replaceAll("%2", password);
                messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
            }
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
        catch (NoSuchFieldException e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public void sendEmailVerification(int userId, String emailAddress) throws EJBException {
        block31: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block28: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("SELECT verified FROM useremailaddress WHERE userid = ? AND emailaddress = ?");
                ps.setInt(1, userId);
                ps.setString(2, emailAddress);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (rs.getBoolean("verified")) {
                        throw new FusionEJBException("Email address " + emailAddress + " has already been verified.");
                    }
                } else {
                    log.error((Object)(userId + " tried to request for email verification on email address " + emailAddress));
                    throw new FusionEJBException("Unknown email address " + emailAddress);
                }
                EmailUtils.EmailValidatationResult evr = EmailUtils.externalEmailIsValid(emailAddress);
                if (evr.result != EmailUtils.EmailValidatationEnum.VALID) {
                    log.error((Object)("Sending of email verification failed for [" + userId + "]: invalid email address [" + emailAddress + "]."));
                    throw new EJBException(evr.reason);
                }
                String token = this.generateEmailVerificationToken(emailAddress);
                if (token == null) {
                    throw new EJBException("Unable to generate email verification token");
                }
                if (!this.cacheEmailVerificationTokenDetails(token, userId, emailAddress)) {
                    throw new EJBException("Unable to store email verification token");
                }
                if (SystemProperty.getBool(SystemPropertyEntities.Email.ENABLED_EMAIL_ADDRESS_VRIFICATION_WITH_TEMPLATE)) {
                    UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("base_url", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL));
                    params.put("verify_link", SystemProperty.get(SystemPropertyEntities.Email.EXTERNAL_EMAIL_ADDRESS_VERIFICATION_LINK) + token);
                    unsProxy.sendTemplatizedEmailFromNoReply(emailAddress, SystemProperty.getInt(SystemPropertyEntities.Email.EMAIL_ADDRESS_VRIFICATION_TEMPLATE_ID), params);
                    break block28;
                }
                String emailVerifyLink = SystemProperty.get("ExternalEmailAddressVerificationLink", "http://www.mig33.com/sites/ajax/settings/account_email_verify?token=");
                String subject = SystemProperty.get("ExternalEmailAddressVerificationEmailSubject", "Activate your email in mig33");
                String content = "Hi,\nThank you for providing your email address. Please click the link below to verify your email:\n%l%s\n\n--The mig33 Team\n(if clicking the link in this message does not work, copy and paste it into the address bar of your browser.)";
                content = content.replace("%l", emailVerifyLink);
                content = SystemProperty.get("ExternalEmailAddressVerificationEmailContent", content);
                content = content.replace("%s", token);
                log.info((Object)("Sending verification email to [" + userId + "];email[" + emailAddress + "];token[" + token + "]: subject: [" + subject + "] content: [" + content + "]"));
                MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                messageEJB.sendEmailFromNoReply(emailAddress, subject, content);
            }
            Object var13_17 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block31;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block31;
            {
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var13_18 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    private String generateEmailVerificationToken(String emailAddress) {
        String tokenSalt = SystemProperty.get("ExternalEmailVerificationTokenSalt", "asd783jsok3@1%sdf%klsdfgsklsdfer");
        String token = new String(Base64.encodeBase64((byte[])HashUtils.sha256(emailAddress + tokenSalt + System.currentTimeMillis() % 59999L)));
        return token.replaceAll("[^A-Za-z0-9]", "");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean cacheEmailVerificationTokenDetails(String token, int userId, String emailAddress) {
        if (SystemProperty.getBool(SystemPropertyEntities.Default.EMAIL_VERIFICATION_WITHOUT_USERNAME_ENABLED)) {
            if (MemCachedClientWrapper.add(MemCachedKeySpaces.CommonKeySpace.EXTERNAL_EMAIL_VERIFICATION_TOKEN, token, String.format("%s|%d", emailAddress, userId), (long)SystemProperty.getInt(SystemPropertyEntities.Default.EMAIL_VERIFICATION_TOKEN_EXPIRY_IN_SECONDS) * 1000L)) return true;
            log.error((Object)String.format("Unable to store email verification token for userid %s email %s to memcached: key (token) is already in use '%s'", userId, emailAddress, token));
            return false;
        }
        String key = UserBean.getRedisKeyForEmailVerificationToken(userId, token);
        Jedis masterInstance = null;
        try {
            try {
                masterInstance = Redis.getMasterInstanceForUserID(userId);
                masterInstance.connect();
                masterInstance.setex(key, SystemProperty.getInt(SystemPropertyEntities.Default.EMAIL_VERIFICATION_TOKEN_EXPIRY_IN_SECONDS), emailAddress);
            }
            catch (Exception e) {
                log.error((Object)String.format("Unable to store email verification token for userid %d email %s with token %s to redis, key is %s", userId, emailAddress, token, key));
                boolean bl = false;
                Object var9_7 = null;
                Redis.disconnect(masterInstance, log);
                return bl;
            }
            Object var9_6 = null;
        }
        catch (Throwable throwable) {
            Object var9_8 = null;
            Redis.disconnect(masterInstance, log);
            throw throwable;
        }
        Redis.disconnect(masterInstance, log);
        return true;
    }

    public void changeMobilePhone(String username, String mobilePhone, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        this.changeMobilePhone(username, mobilePhone, false, accountEntrySourceData);
    }

    /*
     * Loose catch block
     */
    public void changeMobilePhone(String username, String mobilePhone, boolean skipSendVerificationCode, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block50: {
            Exception e22;
            boolean mobilePhoneUpdated;
            boolean ok;
            UserData userData;
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block47: {
                block46: {
                    String currency;
                    int countryID;
                    MessageLocal messageEJB;
                    log.info((Object)("Change Mobilephone request: IP[" + accountEntrySourceData.ipAddress + "] username [" + username + "] User-agent [" + accountEntrySourceData.userAgent + "] to [" + mobilePhone + "]"));
                    conn = null;
                    ps = null;
                    rs = null;
                    UserPrx userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
                    userData = null;
                    ok = false;
                    mobilePhoneUpdated = false;
                    userData = this.loadUser(username, false, false);
                    if (userData == null) {
                        throw new EJBException("Invalid username " + username);
                    }
                    conn = this.dataSourceMaster.getConnection();
                    if (mobilePhone != null && this.getMobilePhoneCount(conn, mobilePhone = (messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class)).cleanAndValidatePhoneNumber(mobilePhone, true)) > 0) {
                        throw new EJBException("Mobile phone " + mobilePhone + " already in use");
                    }
                    if (mobilePhone == null && userData.type != UserData.TypeEnum.MIG33_PREPAID_CARD) {
                        throw new EJBException("Not allowed to deregister mobile phone");
                    }
                    if (mobilePhone == null || userData.type == UserData.TypeEnum.MIG33_PREPAID_CARD) {
                        countryID = userData.countryID;
                        currency = userData.currency;
                    } else {
                        MessageLocal messageEJB2 = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                        MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                        CountryData countryData = misEJB.getCountryByIDDCode(messageEJB2.getIDDCode(mobilePhone), mobilePhone);
                        if (countryData == null) {
                            throw new EJBException("Unable to determine the country for mobile phone " + mobilePhone);
                        }
                        if (!userData.countryID.equals(countryData.id)) {
                            ps = conn.prepareStatement("select * from activation where username = ? limit 1");
                            ps.setString(1, username);
                            rs = ps.executeQuery();
                            boolean hasMobileVerified = rs.next();
                            rs.close();
                            ps.close();
                            if (hasMobileVerified || this.isUserMobileOrEmailVerified(username)) {
                                Integer iddCodeOld = null;
                                if (StringUtil.isBlank(userData.mobilePhone)) {
                                    CountryData countryDataUser = misEJB.getCountry(userData.countryID);
                                    if (countryDataUser == null) {
                                        throw new EJBException("Unable to determine the country of the user");
                                    }
                                    iddCodeOld = countryDataUser.iddCode;
                                } else {
                                    iddCodeOld = messageEJB2.getIDDCode(userData.mobilePhone);
                                }
                                throw new EJBException("You can only change to a number starting with " + iddCodeOld);
                            }
                        }
                        countryID = countryData.id;
                        currency = countryData.currency;
                    }
                    String verificationCode = this.newVerificationCode();
                    boolean mobileVerified = userData.type == UserData.TypeEnum.MIG33_PREPAID_CARD && mobilePhone != null;
                    ps = conn.prepareStatement("update user set mobilephone=?, mobileverified=?, verificationcode=?, countryid=? where username=?");
                    ps.setString(1, mobilePhone);
                    ps.setInt(2, mobileVerified ? 1 : 0);
                    ps.setString(3, verificationCode);
                    ps.setInt(4, countryID);
                    ps.setString(5, username);
                    if (ps.executeUpdate() != 1) {
                        throw new EJBException("Failed to change mobile phone for " + username);
                    }
                    mobilePhoneUpdated = true;
                    if (mobilePhone != null && this.getMobilePhoneCount(conn, mobilePhone) > 1) {
                        throw new EJBException("Mobile phone already in use");
                    }
                    UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    userEJB.postChangeMobilePhone(userData, username, mobilePhone, verificationCode, currency, skipSendVerificationCode, accountEntrySourceData);
                    ok = true;
                    if (userPrx == null) break block46;
                    userPrx.userDetailChanged(this.loadUser(username, false, true).toIceObject());
                }
                Object var21_25 = null;
                if (ok || !mobilePhoneUpdated) break block47;
                try {
                    ps.setString(1, userData.mobilePhone);
                    ps.setObject(2, userData.mobileVerified == null ? null : Integer.valueOf(userData.mobileVerified != false ? 1 : 0));
                    ps.setString(3, userData.verificationCode);
                    ps.setObject(4, userData.countryID);
                    ps.setString(5, username);
                    ps.executeUpdate();
                }
                catch (Exception e22) {
                    // empty catch block
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e22) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e22) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block50;
            }
            catch (SQLException e22) {
                conn = null;
            }
            break block50;
            {
                catch (CreateException e3) {
                    throw new EJBException(e3.getMessage());
                }
                catch (LocalException e4) {
                    Object var21_26 = null;
                    if (!ok && mobilePhoneUpdated) {
                        try {
                            ps.setString(1, userData.mobilePhone);
                            ps.setObject(2, userData.mobileVerified == null ? null : Integer.valueOf(userData.mobileVerified != false ? 1 : 0));
                            ps.setString(3, userData.verificationCode);
                            ps.setObject(4, userData.countryID);
                            ps.setString(5, username);
                            ps.executeUpdate();
                        }
                        catch (Exception e22) {
                            // empty catch block
                        }
                    }
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e22) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e22) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block50;
                    }
                    catch (SQLException e22) {
                        conn = null;
                    }
                    break block50;
                }
                catch (SQLException e5) {
                    throw new EJBException(e5.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var21_27 = null;
                if (!ok && mobilePhoneUpdated) {
                    try {
                        ps.setString(1, userData.mobilePhone);
                        ps.setObject(2, userData.mobileVerified == null ? null : Integer.valueOf(userData.mobileVerified != false ? 1 : 0));
                        ps.setString(3, userData.verificationCode);
                        ps.setObject(4, userData.countryID);
                        ps.setString(5, username);
                        ps.executeUpdate();
                    }
                    catch (Exception e22) {
                        // empty catch block
                    }
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e22) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e22) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e22) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void postChangeMobilePhone(UserData userData, String username, String mobilePhone, String verificationCode, String currency, boolean skipSendVerificationCode, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block16: {
            Statement ps;
            Connection conn;
            block14: {
                MessageLocal messageEJB;
                conn = null;
                ps = null;
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                accountEJB.setUsersLocalCurrency(username, currency, accountEntrySourceData);
                if (skipSendVerificationCode || mobilePhone == null || userData.type == UserData.TypeEnum.MIG33_PREPAID_CARD || !(messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class)).isMobileNumber(mobilePhone, true)) break block14;
                this.resendVerificationCode(username, mobilePhone, accountEntrySourceData);
            }
            Object var13_13 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var13_14 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void cancelChangeMobilePhoneRequest(String username, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        conn = null;
        ps = null;
        rs = null;
        userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
        try {
            block31: {
                block30: {
                    conn = this.dataSourceMaster.getConnection();
                    ps = conn.prepareStatement("select activation.mobilephone, user.mobileverified, user.verificationcode from user left outer join activation on (user.username = activation.username) where user.username = ? order by id desc limit 1");
                    ps.setString(1, username);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new EJBException("Invalid username " + username);
                    }
                    originalMobilePhone = rs.getString("mobilephone");
                    if (originalMobilePhone == null) break block30;
                    if (rs.getInt("mobileverified") != 1) break block31;
                }
                var13_10 = null;
                ** GOTO lbl70
            }
            rs.close();
            ps.close();
            messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countryData = misEJB.getCountryByIDDCode(messageEJB.getIDDCode(originalMobilePhone), originalMobilePhone);
            if (countryData == null) {
                throw new EJBException("Unable to determine the country for mobile phone " + originalMobilePhone);
            }
            if (this.getMobilePhoneCount(conn, originalMobilePhone) > 0) {
                throw new EJBException("Mobile phone already in use");
            }
            ps = conn.prepareStatement("update user set mobilephone = ?, mobileverified = ?, countryid = ? where username = ?");
            ps.setString(1, originalMobilePhone);
            ps.setInt(2, 1);
            ps.setObject(3, countryData.id);
            ps.setString(4, username);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to cancel mobile phone change request for user " + username);
            }
            accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountEJB.setUsersLocalCurrency(username, countryData.currency, accountEntrySourceData);
            if (userPrx != null) {
                userPrx.userDetailChanged(this.loadUser(username, false, true).toIceObject());
            }
            ** GOTO lbl90
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        {
            block35: {
                block34: {
                    block33: {
                        block32: {
                            catch (Throwable var12_20) {
                                var13_12 = null;
                                try {
                                    if (rs != null) {
                                        rs.close();
                                    }
                                }
                                catch (SQLException e) {
                                    rs = null;
                                }
                                try {
                                    if (ps != null) {
                                        ps.close();
                                    }
                                }
                                catch (SQLException e) {
                                    ps = null;
                                }
                                try {
                                    if (conn == null) throw var12_20;
                                    conn.close();
                                    throw var12_20;
                                }
                                catch (SQLException e) {
                                    conn = null;
                                }
                                throw var12_20;
                            }
lbl70:
                            // 1 sources

                            ** try [egrp 2[TRYBLOCK] [7 : 433->448)] { 
lbl71:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block32;
lbl74:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [8 : 453->468)] { 
lbl78:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block33;
lbl81:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return;
                    }
                    if (conn == null) return;
                    conn.close();
                    return;
lbl90:
                    // 1 sources

                    var13_11 = null;
                    ** try [egrp 2[TRYBLOCK] [7 : 433->448)] { 
lbl92:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block34;
lbl95:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [8 : 453->468)] { 
lbl99:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block35;
lbl102:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (conn == null) return;
            conn.close();
            return;
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public String forgotPasswordViaEmail(String username, String emailAddress) throws FusionEJBException {
        log.info((Object)("A request to change password has been triggered: username [" + username + "] emailAddress [" + emailAddress + "]"));
        if (StringUtil.isBlank(username)) {
            throw new FusionEJBException("Please provide a valid username.");
        }
        EmailUtils.EmailValidatationResult evr = EmailUtils.externalEmailIsValid(emailAddress);
        if (evr.result != EmailUtils.EmailValidatationEnum.VALID) {
            throw new FusionEJBException(evr.reason);
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        String sql = "SELECT COUNT(*) `match` FROM useremailaddress uea, \t   userid ui, \t   user u WHERE ui.username = u.username AND uea.userid = ui.id AND ui.username = ? AND uea.type = ? AND uea.verified = 1 AND u.status = ? AND uea.emailAddress = ? ";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setInt(2, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
        ps.setInt(3, UserData.StatusEnum.ACTIVE.value());
        ps.setString(4, emailAddress);
        rs = ps.executeQuery();
        if (!rs.next() || rs.next() && rs.getInt("match") < 1) {
            throw new FusionEJBException("Sorry, we cannot find your account. Please fill in the correct information below to help us identify your account.");
        }
        String token = StringUtil.generateRandomWord(SystemProperty.get(SystemPropertyEntities.TokenizerSettings.DEFAULT_CHARSET), SystemProperty.getInt(SystemPropertyEntities.TokenizerSettings.MINIMUM_LENGTH));
        MemCachedClientWrapper.set(MemCachedKeySpaces.TokenKeySpace.FORGOT_PASSWORD, username, token, SystemProperty.getLong(SystemPropertyEntities.ForgotPassword.TOKEN_EXPIRATION_IN_MILLIS));
        log.info((Object)("Created token for reset password - user [" + username + "] email [" + emailAddress + "] token [" + token + "]"));
        if (SystemProperty.getBool(SystemPropertyEntities.Email.ENABLED_FORGOT_PASSWORD_EMAIL_WITH_TEMPLATE)) {
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            HashMap<String, String> params = new HashMap<String, String>();
            String changePasswordLink = SystemProperty.get(SystemPropertyEntities.ForgotPassword.MIGME_CHANGE_PASSWORD_URL).replaceAll("%1", username).replaceAll("%2", token);
            params.put("base_url", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL));
            params.put("change_password_link", changePasswordLink);
            params.put("username", username);
            unsProxy.sendTemplatizedEmailFromNoReply(emailAddress, SystemProperty.getInt(SystemPropertyEntities.Email.FORGOT_PASSWORD_EMAIL_TEMPLATE_ID), params);
        } else {
            String changePasswordLink = SystemProperty.get(SystemPropertyEntities.ForgotPassword.CHANGE_PASSWORD_URL).replaceAll("%1", username).replaceAll("%2", token);
            String subject = SystemProperty.get(SystemPropertyEntities.ForgotPassword.EMAIL_SUBJECT);
            String content = SystemProperty.get(SystemPropertyEntities.ForgotPassword.EMAIL_CONTENT).replaceAll("%1", username).replaceAll("%2", changePasswordLink);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Sending token via email: [" + emailAddress + "] subject [" + subject + "] content [" + content + "]"));
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageEJB.sendEmailFromNoReply(emailAddress, subject, content);
        }
        this.logForgotPassword(this.getUserID(username, null), Enums.Mig33UserActionMisLogEnum.FORGOT_PASSWORD, "EMAIL");
        String string = token;
        Object var14_17 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        {
            return string;
            catch (SQLException e) {
                log.error((Object)"Unable to process forgot password request", (Throwable)e);
                throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
            }
            catch (Exception e) {
                log.error((Object)"Unable to process forgot password request", (Throwable)e);
                throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
            }
        }
        catch (Throwable throwable) {
            Object var14_18 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
    }

    private void logForgotPassword(int userID, Enums.Mig33UserActionMisLogEnum action, String desc) {
        try {
            MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misEJB.logMig33UserAction(userID, action, String.format(Enums.Mig33UserActionMisLogEnum.FORGOT_PASSWORD.getDescriptionPattern(), desc));
        }
        catch (Exception e) {
            log.warn((Object)("Unable to log change in password for user [" + userID + "], due to:" + e), (Throwable)e);
        }
    }

    public String forgotPasswordViaSecurityQuestion(String username, String answer, int sqID) throws FusionEJBException, EJBException {
        log.info((Object)("A request to change password has been triggered: username [" + username + "] security answer [" + answer + "]"));
        if (StringUtil.isBlank(username)) {
            throw new FusionEJBException("Please provide a valid username.");
        }
        if (StringUtil.isBlank(answer)) {
            throw new FusionEJBException("Please provide a valid answer.");
        }
        if (sqID < 1) {
            throw new FusionEJBException("Please provide a valid sequrity question.");
        }
        int userID = 0;
        try {
            userID = this.getUserID(username, null);
        }
        catch (EJBException e) {
            log.warn((Object)("Attempt to change password for username [" + username + "] but no userid detected. error msg: " + (Object)((Object)e)), (Throwable)e);
            throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
        }
        SecurityQuestion sq = this.getSecurityQuestion(userID);
        if (sq == null) {
            throw new FusionEJBException("Can not find security question");
        }
        if (sq.id != sqID) {
            throw new FusionEJBException("Please check that you have provided the correct security question and answer in order to proceed.");
        }
        try {
            AuthenticationServiceCredentialResponse authSvcCredResp = EJBIcePrxFinder.getAuthenticationServiceProxy().getCredential(userID, PasswordType.SECURITY_QUESTION.value());
            if (authSvcCredResp == null || authSvcCredResp.userCredential == null || authSvcCredResp.userCredential.password == null) {
                log.warn((Object)"Can not find security question");
                throw new FusionEJBException("Can not find security question");
            }
            if (!answer.equalsIgnoreCase(authSvcCredResp.userCredential.password)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)String.format("Expected security answer:%s, actual answer:%s", authSvcCredResp.userCredential.password, answer));
                }
                throw new FusionEJBException("Please check that you have provided the correct security question and answer in order to proceed.");
            }
            String token = StringUtil.generateRandomWord(SystemProperty.get(SystemPropertyEntities.TokenizerSettings.DEFAULT_CHARSET), SystemProperty.getInt(SystemPropertyEntities.TokenizerSettings.MINIMUM_LENGTH));
            MemCachedClientWrapper.set(MemCachedKeySpaces.TokenKeySpace.FORGOT_PASSWORD, username, token, SystemProperty.getLong(SystemPropertyEntities.ForgotPassword.TOKEN_EXPIRATION_IN_MILLIS));
            log.info((Object)("Created token for reset password - user [" + username + "] security answer [" + answer + "] token [" + token + "]"));
            this.logForgotPassword(userID, Enums.Mig33UserActionMisLogEnum.FORGOT_PASSWORD, "SECURITY QUESTION");
            return token;
        }
        catch (Exception e) {
            log.error((Object)("Unable to process forgot password request:" + e), (Throwable)e);
            if (e instanceof FusionEJBException) {
                throw (FusionEJBException)e;
            }
            throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
        }
    }

    public boolean changePassword(String token, String username, String newPassword, String ipAddress) throws FusionEJBException {
        log.info((Object)("initiated password change with the following info: username [" + username + "] token [" + token + "] ipAddress [" + ipAddress + "]"));
        if (StringUtil.isBlank(token)) {
            throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
        }
        if (StringUtil.isBlank(newPassword)) {
            throw new FusionEJBException("Please provide a valid password.");
        }
        ValidateCredentialResult validationResult = PasswordUtils.validatePassword(username, newPassword);
        if (!validationResult.valid) {
            throw new FusionEJBException(validationResult.reason);
        }
        if (StringUtil.isBlank(ipAddress)) {
            throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
        }
        if (StringUtil.isBlank(username)) {
            throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
        }
        String memcacheToken = MemCachedClientWrapper.getString(MemCachedKeySpaces.TokenKeySpace.FORGOT_PASSWORD, username);
        if (StringUtil.isBlank(memcacheToken) || !memcacheToken.equals(token)) {
            log.warn((Object)("Invalid token entered: token [" + token + "] username [" + username + "] ipAddress [" + ipAddress + "]"));
            throw new FusionEJBException("Invalid token detected.");
        }
        int userID = 0;
        try {
            userID = this.getUserID(username, null);
        }
        catch (EJBException e) {
            log.warn((Object)("Attempt to change password for username [" + username + "] but no userid detected."));
            throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
        }
        try {
            AuthenticationServiceCredentialResponse authSvcCredResp = EJBIcePrxFinder.getAuthenticationServiceProxy().getCredential(userID, PasswordType.FUSION.value());
            if (authSvcCredResp.userCredential == null) {
                log.warn((Object)("Attempted to change password for user [" + username + "] but no fusion password detected"));
                throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
            }
            String oldPassword = authSvcCredResp.userCredential.password;
            this.changePassword(username, oldPassword, newPassword);
            MemCachedClientWrapper.delete(MemCachedKeySpaces.TokenKeySpace.FORGOT_PASSWORD, username);
        }
        catch (FusionException e) {
            log.warn((Object)"Unable to retrieve user credential", (Throwable)((Object)e));
            throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
        }
        catch (EJBException e) {
            log.warn((Object)"Unable to change user password", (Throwable)e);
            throw new FusionEJBException("Unable to process you request at the moment. Please try again later.");
        }
        return true;
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public String forgotPasswordViaSMS(String username, AccountEntrySourceData accountEntrySourceData) throws FusionEJBException {
        if (StringUtil.isBlank(username)) {
            throw new FusionEJBException("Please provide a valid username.");
        }
        log.info((Object)("A request to change password via SMS has been triggered: username [" + username + "]"));
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        String sql = "select mobilephone from user where username = ?;";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            log.error((Object)String.format("Failed to retrieve password via sms, due to unable to locate user:%s", username));
            throw new FusionEJBException("Sorry, we cannot find your account. Please fill in the correct information below to help us identify your account.");
        }
        String mobile = rs.getString("mobilephone");
        if (StringUtil.isBlank(mobile)) {
            log.warn((Object)String.format("Failed to retrieve password via sms, due to User:%s, does not have a mobile number", username));
            throw new FusionEJBException("Sorry, there is no mobilephone attached to the user");
        }
        if (SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_CHECK_FORGOT_PASSWORD_VIA_SMS_MAX_RETRIEVE_TIME)) {
            rs.close();
            ps.close();
            sql = "select retrieveTimes from SMSRetrievePWStatus where username = ?;";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt("retrieveTimes") > SystemProperty.getInt(SystemPropertyEntities.ForgotPassword.FORGOT_PASSWORD_VIA_SMS_MAX_RETRIEVE_TIME)) {
                log.error((Object)String.format("Exceed max retrieve time, user:%s with mobile phone:%s", username, mobile));
                throw new FusionEJBException("Sorry, you have exceeded the max retrieve time, please contact our customer service");
            }
        }
        String token = StringUtil.generateRandomWord(SystemProperty.get(SystemPropertyEntities.TokenizerSettings.DEFAULT_CHARSET), SystemProperty.getInt(SystemPropertyEntities.TokenizerSettings.MINIMUM_LENGTH));
        token = token.substring(token.length() - 4, token.length());
        MemCachedClientWrapper.set(MemCachedKeySpaces.TokenKeySpace.FORGOT_PASSWORD, username, token, SystemProperty.getLong(SystemPropertyEntities.ForgotPassword.TOKEN_EXPIRATION_IN_MILLIS));
        log.info((Object)("Created token for reset password - user [" + username + "] mobile [" + mobile + "] token [" + token + "]"));
        String content = String.format(SystemProperty.get(SystemPropertyEntities.ForgotPassword.FORGOT_PASSWORD_SMS_MESSAGE), token);
        SystemSMSData systemSMSData = new SystemSMSData();
        systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
        systemSMSData.subType = SystemSMSData.SubTypeEnum.FORGOT_PASSWORD;
        systemSMSData.username = username;
        systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
        systemSMSData.destination = mobile;
        systemSMSData.messageText = content;
        MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
        messageEJB.sendSystemSMS(systemSMSData, SystemProperty.getInt("ForgotPasswordSMSDelay"), accountEntrySourceData);
        this.logForgotPasswordViaSMS(username);
        this.logForgotPassword(this.getUserID(username, null), Enums.Mig33UserActionMisLogEnum.FORGOT_PASSWORD, "SMS");
        String string = token;
        Object var14_15 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        {
            return string;
            catch (SQLException e) {
                log.error((Object)"Unable to process forgot password request", (Throwable)e);
                throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
            }
            catch (Exception e) {
                log.error((Object)"Unable to process forgot password request", (Throwable)e);
                throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
            }
        }
        catch (Throwable throwable) {
            Object var14_16 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean allowForgotPasswordViaSMS(String username) throws FusionEJBException {
        block53: {
            block52: {
                block45: {
                    block51: {
                        block50: {
                            block44: {
                                block49: {
                                    block48: {
                                        block43: {
                                            if (StringUtil.isBlank(username)) {
                                                throw new FusionEJBException("Please provide a valid username.");
                                            }
                                            if (!SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_CHECK_COUNTRY_FOR_FORGOT_PASSWORD_VIA_SMS) && !SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_CHECK_RETRIEVE_TIME_FOR_FORGOT_PASSWORD_VIA_SMS)) {
                                                return true;
                                            }
                                            conn = null;
                                            ps = null;
                                            rs = null;
                                            try {
                                                try {
                                                    conn = this.dataSourceSlave.getConnection();
                                                    if (SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_CHECK_COUNTRY_FOR_FORGOT_PASSWORD_VIA_SMS)) {
                                                        sql = "select CountryID from user where username = ?;";
                                                        ps = conn.prepareStatement(sql);
                                                        ps.setString(1, username);
                                                        rs = ps.executeQuery();
                                                        if (!rs.next()) {
                                                            UserBean.log.error((Object)String.format("Unable to retrive country id for user:%s", new Object[]{username}));
                                                            throw new FusionEJBException("Unable to retrive country id.");
                                                        }
                                                        countryIDInteger = (Integer)rs.getObject("CountryID");
                                                        if (countryIDInteger == null) {
                                                            UserBean.log.error((Object)String.format("NO country information for user:%s", new Object[]{username}));
                                                            throw new FusionEJBException("NO country information.");
                                                        }
                                                        countryID = countryIDInteger;
                                                        isWhitelisted = false;
                                                        for (int cid : SystemProperty.getIntArray(SystemPropertyEntities.ForgotPassword.WHITELIST_COUNTRIES_FOR_SMS)) {
                                                            if (cid != countryID) continue;
                                                            isWhitelisted = true;
                                                            break;
                                                        }
                                                        if (!isWhitelisted) {
                                                            var9_16 = false;
                                                            var14_20 = null;
                                                            break block43;
                                                        }
                                                    }
                                                    if (SystemProperty.getBool(SystemPropertyEntities.ForgotPassword.ENABLED_CHECK_FORGOT_PASSWORD_VIA_SMS_MAX_RETRIEVE_TIME)) {
                                                        try {
                                                            if (rs != null) {
                                                                rs.close();
                                                            }
                                                        }
                                                        catch (SQLException e) {
                                                            rs = null;
                                                        }
                                                        try {
                                                            if (ps != null) {
                                                                ps.close();
                                                            }
                                                        }
                                                        catch (SQLException e) {
                                                            ps = null;
                                                        }
                                                        sql = "select retrieveTimes from SMSRetrievePWStatus where username = ?;";
                                                        ps = conn.prepareStatement(sql);
                                                        ps.setString(1, username);
                                                        rs = ps.executeQuery();
                                                        if (rs.next() && rs.getInt("retrieveTimes") > SystemProperty.getInt(SystemPropertyEntities.ForgotPassword.FORGOT_PASSWORD_VIA_SMS_MAX_RETRIEVE_TIME)) {
                                                            var6_12 = false;
                                                            break block44;
                                                        }
                                                    }
                                                    sql = true;
                                                    break block45;
                                                }
                                                catch (SQLException e) {
                                                    UserBean.log.error((Object)"Unable to process forgot password request", (Throwable)e);
                                                    throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
                                                }
                                                catch (Exception e) {
                                                    UserBean.log.error((Object)"Unable to process forgot password request", (Throwable)e);
                                                    throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
                                                }
                                            }
                                            catch (Throwable var13_28) {
                                                var14_23 = null;
                                                try {
                                                    if (rs != null) {
                                                        rs.close();
                                                    }
                                                }
                                                catch (SQLException e) {
                                                    rs = null;
                                                }
                                                try {
                                                    if (ps != null) {
                                                        ps.close();
                                                    }
                                                }
                                                catch (SQLException e) {
                                                    ps = null;
                                                }
                                                try {
                                                    if (conn == null) throw var13_28;
                                                    conn.close();
                                                    throw var13_28;
                                                }
                                                catch (SQLException e) {
                                                    conn = null;
                                                    throw var13_28;
                                                }
                                            }
                                        }
                                        ** try [egrp 4[TRYBLOCK] [12 : 440->455)] { 
lbl90:
                                        // 1 sources

                                        if (rs != null) {
                                            rs.close();
                                        }
                                        break block48;
lbl93:
                                        // 1 sources

                                        catch (SQLException e) {
                                            rs = null;
                                        }
                                    }
                                    ** try [egrp 5[TRYBLOCK] [13 : 460->473)] { 
lbl97:
                                    // 1 sources

                                    if (ps != null) {
                                        ps.close();
                                    }
                                    break block49;
lbl100:
                                    // 1 sources

                                    catch (SQLException e) {
                                        ps = null;
                                    }
                                }
                                try {}
                                catch (SQLException e) {
                                    return var9_16;
                                }
                                if (conn == null) return var9_16;
                                conn.close();
                                return var9_16;
                            }
                            var14_21 = null;
                            ** try [egrp 4[TRYBLOCK] [12 : 440->455)] { 
lbl112:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block50;
lbl115:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 5[TRYBLOCK] [13 : 460->473)] { 
lbl119:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block51;
lbl122:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var6_12;
                    }
                    if (conn == null) return var6_12;
                    conn.close();
                    return var6_12;
                }
                var14_22 = null;
                ** try [egrp 4[TRYBLOCK] [12 : 440->455)] { 
lbl134:
                // 1 sources

                if (rs != null) {
                    rs.close();
                }
                break block52;
lbl137:
                // 1 sources

                catch (SQLException e) {
                    rs = null;
                }
            }
            ** try [egrp 5[TRYBLOCK] [13 : 460->473)] { 
lbl141:
            // 1 sources

            if (ps != null) {
                ps.close();
            }
            break block53;
lbl144:
            // 1 sources

            catch (SQLException e) {
                ps = null;
            }
        }
        try {}
        catch (SQLException e) {
            return sql;
        }
        if (conn == null) return sql;
        conn.close();
        return sql;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void logForgotPasswordViaSMS(String username) {
        block19: {
            Connection conn = null;
            Statement ps = null;
            conn = this.dataSourceMaster.getConnection();
            String sql = "insert into SMSRetrievePWStatus(username) values(?) on duplicate key update retrieveTimes = retrieveTimes+1, retrieveDate = now();";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
            Object var6_6 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block19;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block19;
            {
                catch (Exception e) {
                    log.error((Object)("Failed to log forgot password via SMS, due to: " + e), (Throwable)e);
                    Object var6_7 = null;
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block19;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                }
            }
            catch (Throwable throwable) {
                Object var6_8 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    @Deprecated
    public void forgotPasswordWithMobileNumberOrEmail(String mobileOrEmail, boolean email, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block34: {
            ResultSet rs;
            Statement ps;
            Connection conn;
            block31: {
                String forgotPasswordSMSPassword;
                int smsCount;
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                if (email) {
                    ps = conn.prepareStatement("select u.username as username from userid u, useremailaddress ue where ue.emailaddress = ? and u.id = ue.userid and ue.type = ?");
                    ps.setString(1, mobileOrEmail);
                    ps.setInt(2, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new EJBException("Invalid email address: " + mobileOrEmail);
                    }
                } else {
                    if (!messageEJB.isMobileNumber(mobileOrEmail = messageEJB.cleanAndValidatePhoneNumber(mobileOrEmail, true), true)) {
                        throw new EJBException("Invalid mobile number: " + mobileOrEmail);
                    }
                    ps = conn.prepareStatement("select username from user where mobilephone = ? and status = 1");
                    ps.setString(1, mobileOrEmail);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new EJBException("Invalid mobile number: " + mobileOrEmail);
                    }
                }
                String username = rs.getString("username");
                rs.close();
                ps.close();
                if (!email && (smsCount = messageEJB.getSystemSMSCount(SystemSMSData.SubTypeEnum.FORGOT_PASSWORD, username)) >= SystemProperty.getInt("MaxPasswordRequestPerDay")) {
                    throw new EJBException("You have already requested your password today. If you do not receive the SMS containing your password, please email contact@mig.me");
                }
                Integer attempts = MemCachedClientWrapper.getInt(MemCachedKeySpaces.RateLimitKeySpace.FORGOT_PASSWORD_ATTEMPTS, MemCachedKeyUtils.getFullKeyFromStrings(mobileOrEmail, accountEntrySourceData.ipAddress));
                if (attempts != null && attempts > SystemProperty.getInt("MaxForgotPasswordAttempts", 5)) {
                    throw new EJBException("You have already exceeded the maximum number of reset passwords today. If you have not requested for your password to be reset, please email contact@mig.me.");
                }
                ps = conn.prepareStatement("select username from usersetting where username = ? AND type = ?");
                ps.setString(1, username);
                ps.setInt(2, UserSettingData.TypeEnum.SECURITY_QUESTION.value());
                rs = ps.executeQuery();
                if (rs.next()) break block31;
                Crypter crypter = new Crypter(ConfigUtils.getConfigDirectory() + "/aeskeys/");
                String token = PasswordUtils.encrypt(UUID.randomUUID().toString(), crypter);
                token = token.substring(token.length() - 8, token.length());
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.SECURITY_QUESTION, token, mobileOrEmail, System.currentTimeMillis() + SystemProperty.getLong("SecurityTokenExpiryInSeconds", 86400L) * 1000L);
                if (MemCachedClientWrapper.incr(MemCachedKeySpaces.RateLimitKeySpace.FORGOT_PASSWORD_ATTEMPTS, MemCachedKeyUtils.getFullKeyFromStrings(mobileOrEmail, accountEntrySourceData.ipAddress)) < 0L) {
                    MemCachedClientWrapper.set(MemCachedKeySpaces.RateLimitKeySpace.FORGOT_PASSWORD_ATTEMPTS, MemCachedKeyUtils.getFullKeyFromStrings(mobileOrEmail, accountEntrySourceData.ipAddress), 1);
                }
                if ((forgotPasswordSMSPassword = SystemProperty.get("ForgotPasswordSMSPassword", "")).length() <= 0) break block31;
                if (email) {
                    log.debug((Object)("Sending token via email: [" + mobileOrEmail + "] " + token));
                    messageEJB.sendEmailFromNoReply(mobileOrEmail, "migme account password reset token", forgotPasswordSMSPassword.replaceAll("%1", token));
                    break block31;
                }
                SystemSMSData systemSMSData = new SystemSMSData();
                systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                systemSMSData.subType = SystemSMSData.SubTypeEnum.FORGOT_PASSWORD;
                systemSMSData.username = username;
                systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
                systemSMSData.destination = mobileOrEmail;
                systemSMSData.messageText = forgotPasswordSMSPassword.replaceAll("%1", token);
                messageEJB.sendSystemSMS(systemSMSData, SystemProperty.getInt("ForgotPasswordSMSDelay"), accountEntrySourceData);
            }
            Object var15_19 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block34;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block34;
            {
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (NoSuchFieldException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (KeyczarException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var15_20 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public AuthenticationServiceResponseCodeEnum validateUserCredential(String username, String password, PasswordType passwordType) {
        AuthenticationServicePrx authPrx = EJBIcePrxFinder.getAuthenticationServiceProxy();
        return authPrx.checkCredentialByUsername(username, password, passwordType.value());
    }

    /*
     * Loose catch block
     */
    public void changePassword(String username, String oldPassword, String newPassword) throws EJBException {
        block43: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            UserPrx userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
            AuthenticationServicePrx authPrx = EJBIcePrxFinder.getAuthenticationServiceProxy();
            log.info((Object)("attempting to update password for user [" + username + "]"));
            newPassword = newPassword.trim();
            this.checkPassword(username, newPassword);
            if (SystemProperty.getBool(SystemPropertyEntities.Temp.PT73368964_UseAuthForChangePwd_ENABLED, true)) {
                AuthenticationServiceResponseCodeEnum res = authPrx.checkCredentialByUsername(username, oldPassword, PasswordType.FUSION.value());
                if (res != AuthenticationServiceResponseCodeEnum.Success) {
                    throw new EJBException("Incorrect username or password");
                }
            } else {
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select username from user where username=? and password=?");
                ps.setString(1, username);
                ps.setString(2, oldPassword);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EJBException("Incorrect username or password");
                }
                rs.close();
                ps.close();
            }
            SurgeMail.deleteSurgeMailPassword(surgeMailMemcache, username);
            try {
                AuthenticationServiceResponseCodeEnum responseCode = authPrx.updateFusionCredential(new Credential(0, username, newPassword, PasswordType.FUSION.value()), oldPassword);
                if (responseCode != AuthenticationServiceResponseCodeEnum.Success) {
                    log.error((Object)("Failed to change password with authentication service " + responseCode.toString()));
                    throw new EJBException("Unable to update password for " + username);
                }
            }
            catch (FusionBusinessException e) {
                throw new EJBException("Unable to update password for " + username + " : " + e.message);
            }
            catch (FusionException e) {
                log.error((Object)("failed to update password for user [" + username + "]"), (Throwable)((Object)e));
                throw new EJBException("Unable to update password for " + username);
            }
            if (!SystemProperty.getBool(SystemPropertyEntities.Temp.PT73368964_UseAuthForChangePwd_ENABLED, true)) {
                if (conn == null) {
                    conn = this.dataSourceMaster.getConnection();
                }
                ps = conn.prepareStatement("update user set password=? where username=?");
                ps.setString(1, newPassword);
                ps.setString(2, username);
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Unable to update password for " + username);
                }
            }
            if (userPrx != null) {
                userPrx.userDetailChanged(this.loadUser(username, false, true).toIceObject());
            }
            try {
                int userID = this.getUserID(username, null);
                Enums.Mig33UserActionMisLogEnum action = Enums.Mig33UserActionMisLogEnum.CHANGE_PASSWORD;
                MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                misEJB.logMig33UserAction(userID, action, action.getDescriptionPattern());
            }
            catch (Exception e) {
                log.warn((Object)("Unable to log change in password for user [" + username + "]"), (Throwable)e);
            }
            Object var13_19 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block43;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block43;
            {
                catch (LocalException e) {
                    Object var13_20 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block43;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block43;
                }
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (NoSuchFieldException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var13_21 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void loginSucceeded(String username, String mobileDevice, String userAgent, String language) throws EJBException {
        block20: {
            Connection conn = null;
            Statement ps = null;
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            try {
                UserData userData = this.loadUser(username, false, false);
                ConsecutiveLoginTrigger clTrigger = new ConsecutiveLoginTrigger(userData);
                clTrigger.amountDelta = 0.0;
                clTrigger.quantityDelta = 1;
                clTrigger.currency = userData.currency;
                clTrigger.lastLoginDate = userData.lastLoginDate;
                RewardCentre.getInstance().sendTrigger(clTrigger);
                LastLoginTrigger llTrigger = new LastLoginTrigger(userData, currentTime);
                llTrigger.amountDelta = 0.0;
                llTrigger.quantityDelta = 1;
                llTrigger.currency = userData.currency;
                llTrigger.lastLoginDate = userData.lastLoginDate;
                RewardCentre.getInstance().sendTrigger(llTrigger);
            }
            catch (Exception e1) {
                log.warn((Object)("Unable to send reward program trigger for login [" + username + "] :" + e1.getMessage()), (Throwable)e1);
            }
            try {
                Locale locale = new Locale(language.substring(0, 2));
                language = locale.getISO3Language().toUpperCase();
            }
            catch (Exception e) {
                language = Locale.ENGLISH.getISO3Language().toUpperCase();
            }
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set mobiledevice = ?, useragent = ?, language = ?, firstlogindate = if(firstlogindate is null and lastlogindate is null, now(), firstlogindate), lastlogindate = now() where username = ?");
            ps.setString(1, mobileDevice);
            ps.setString(2, userAgent);
            ps.setString(3, language);
            ps.setString(4, username);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated != 1) {
                throw new EJBException("Failed to update user's successful login attempt");
            }
            Object var12_15 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block20;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block20;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var12_16 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void loginFailed(String username) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set failedloginattempts = failedloginattempts + 1 where username = ?");
            ps.setString(1, username);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated != 1) {
                throw new EJBException("Failed to update user's failed login attempt");
            }
            Object var6_6 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var6_7 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void activateAccountFailed(String username) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set failedactivationattempts = failedactivationattempts + 1 where username = ?");
            ps.setString(1, username);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated != 1) {
                throw new EJBException("Failed to update user's failed authentication attempt");
            }
            Object var6_6 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var6_7 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public AlertMessageData getLatestAlertMessage(int midletVersion, AlertMessageData.TypeEnum type, int countryId, Date minimumDate, AlertContentType alertContentType, int clientType) throws EJBException {
        block46: {
            block45: {
                block38: {
                    block44: {
                        block43: {
                            block37: {
                                block42: {
                                    block41: {
                                        block36: {
                                            conn = null;
                                            ps = null;
                                            rs = null;
                                            try {
                                                try {
                                                    key = null;
                                                    key = alertContentType == null ? clientType + "/" + midletVersion + "/" + (Object)type + "/" + countryId : clientType + "/" + midletVersion + "/" + (Object)type + "/" + countryId + "/" + alertContentType.value();
                                                    alertMessages = (ArrayList<AlertMessageData>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.ALERT_MESSAGE, key);
                                                    if (alertMessages == null) {
                                                        sql = "select * from alertmessage where MinMidletVersion <= ? and MaxMidletVersion >= ? and Type = ? and (CountryID = ? or CountryID is null) and StartDate <= now() and ExpiryDate > now() and Status = ? and clientType = ?";
                                                        if (alertContentType != null) {
                                                            sql = sql + " and ContentType = ?";
                                                        }
                                                        sql = sql + " order by CountryID";
                                                        conn = this.dataSourceSlave.getConnection();
                                                        ps = conn.prepareStatement(sql);
                                                        ps.setInt(1, midletVersion);
                                                        ps.setInt(2, midletVersion);
                                                        ps.setInt(3, type.value());
                                                        ps.setInt(4, countryId);
                                                        ps.setInt(5, AlertMessageData.StatusEnum.ACTIVE.value());
                                                        ps.setInt(6, clientType);
                                                        if (alertContentType != null) {
                                                            ps.setInt(7, alertContentType.value());
                                                        }
                                                        rs = ps.executeQuery();
                                                        alertMessages = new ArrayList<AlertMessageData>();
                                                        while (rs.next()) {
                                                            alertMessages.add(new AlertMessageData(rs));
                                                        }
                                                        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.ALERT_MESSAGE, key, alertMessages);
                                                    }
                                                    if (alertMessages.size() == 0) {
                                                        sql = null;
                                                        var22_14 = null;
                                                        break block36;
                                                    }
                                                    accumWeightList = new ArrayList<Double>();
                                                    totalWeight = 0.0;
                                                    for (AlertMessageData alertMessage : alertMessages) {
                                                        expired = alertMessage.expiryDate.before(new Date());
                                                        v0 = neverSeen = minimumDate == null || alertMessage.dateCreated.after(minimumDate) != false;
                                                        if (!(expired || alertMessage.onceOnly.booleanValue() && !neverSeen)) {
                                                            totalWeight += alertMessage.weighting.doubleValue();
                                                        }
                                                        accumWeightList.add(totalWeight);
                                                    }
                                                    if (totalWeight > 0.0) {
                                                        pick = this.randomGen.nextDouble();
                                                        for (i = 0; i < accumWeightList.size(); ++i) {
                                                            accumWeight = (Double)accumWeightList.get(i);
                                                            if (!(accumWeight > 0.0) || !(accumWeight / totalWeight >= pick)) continue;
                                                            var20_29 = (AlertMessageData)alertMessages.get(i);
                                                            break block37;
                                                        }
                                                    }
                                                    var15_23 = null;
                                                    break block38;
                                                }
                                                catch (SQLException e) {
                                                    throw new EJBException(e.getMessage());
                                                }
                                            }
                                            catch (Throwable var21_30) {
                                                var22_17 = null;
                                                try {
                                                    if (rs != null) {
                                                        rs.close();
                                                    }
                                                }
                                                catch (SQLException e) {
                                                    rs = null;
                                                }
                                                try {
                                                    if (ps != null) {
                                                        ps.close();
                                                    }
                                                }
                                                catch (SQLException e) {
                                                    ps = null;
                                                }
                                                try {
                                                    if (conn == null) throw var21_30;
                                                    conn.close();
                                                    throw var21_30;
                                                }
                                                catch (SQLException e) {
                                                    conn = null;
                                                    throw var21_30;
                                                }
                                            }
                                        }
                                        ** try [egrp 2[TRYBLOCK] [7 : 646->661)] { 
lbl86:
                                        // 1 sources

                                        if (rs != null) {
                                            rs.close();
                                        }
                                        break block41;
lbl89:
                                        // 1 sources

                                        catch (SQLException e) {
                                            rs = null;
                                        }
                                    }
                                    ** try [egrp 3[TRYBLOCK] [8 : 666->681)] { 
lbl93:
                                    // 1 sources

                                    if (ps != null) {
                                        ps.close();
                                    }
                                    break block42;
lbl96:
                                    // 1 sources

                                    catch (SQLException e) {
                                        ps = null;
                                    }
                                }
                                try {}
                                catch (SQLException e) {
                                    return sql;
                                }
                                if (conn == null) return sql;
                                conn.close();
                                return sql;
                            }
                            var22_15 = null;
                            ** try [egrp 2[TRYBLOCK] [7 : 646->661)] { 
lbl108:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block43;
lbl111:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [8 : 666->681)] { 
lbl115:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block44;
lbl118:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var20_29;
                    }
                    if (conn == null) return var20_29;
                    conn.close();
                    return var20_29;
                }
                var22_16 = null;
                ** try [egrp 2[TRYBLOCK] [7 : 646->661)] { 
lbl130:
                // 1 sources

                if (rs != null) {
                    rs.close();
                }
                break block45;
lbl133:
                // 1 sources

                catch (SQLException e) {
                    rs = null;
                }
            }
            ** try [egrp 3[TRYBLOCK] [8 : 666->681)] { 
lbl137:
            // 1 sources

            if (ps != null) {
                ps.close();
            }
            break block46;
lbl140:
            // 1 sources

            catch (SQLException e) {
                ps = null;
            }
        }
        try {}
        catch (SQLException e) {
            return var15_23;
        }
        if (conn == null) return var15_23;
        conn.close();
        return var15_23;
    }

    /*
     * Loose catch block
     */
    public void setAccountStatus(String username, boolean status) throws EJBException {
        block18: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set status = ? where username = ?");
            if (status) {
                ps.setInt(1, UserData.StatusEnum.ACTIVE.value());
            } else {
                ps.setInt(1, UserData.StatusEnum.INACTIVE.value());
            }
            ps.setString(2, username);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Could not update user status: User does not exist");
            }
            Object var7_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block18;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block18;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public boolean getAccountStatus(String username) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block20: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT status from user where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            Integer result = rs.getInt("status");
            rs.close();
            rs = null;
            if (result == null) break block20;
            boolean bl = result.equals(UserData.StatusEnum.ACTIVE.value());
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            return bl;
        }
        try {
            try {
                throw new EJBException("Unable to load account status from the DB for the user " + username);
            }
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
    }

    /*
     * Loose catch block
     */
    public void setFailedActivationAttempts(String username, int failedActivationAttempts) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set failedactivationattempts = ? where username = ?");
            ps.setInt(1, failedActivationAttempts);
            ps.setString(2, username);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to set " + username + "'s failed activation attempts to " + failedActivationAttempts);
            }
            Object var7_5 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block16;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_6 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void bannedFromChatRoom(String room, String instigator, String target, String reason) throws EJBException {
        block23: {
            PreparedStatement ps;
            Connection conn;
            block21: {
                conn = null;
                ps = null;
                UserPrx targetUserPrx = EJBIcePrxFinder.findOnewayUserPrx(target);
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update user set chatroombans = chatroombans + 1 where username = ?");
                ps.setString(1, target);
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Failed to log chat room ban");
                }
                UserData userData = this.loadUser(target, false, true);
                try {
                    int maxChatRoomBans;
                    EmailUserNotification note = null;
                    int bansBeforeSuspension = SystemProperty.getInt(SystemPropertyEntities.Default.CHATROOM_BANS_BEFORE_SUSPENSION);
                    if (bansBeforeSuspension > 0 && userData.chatRoomBans == bansBeforeSuspension) {
                        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BAN, target, "1", SystemProperty.getInt("GlobalChatRoomSuspensionDuration", 24) * 60 * 60 * 1000);
                    }
                    if ((maxChatRoomBans = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_CHATROOM_BANS)) > 0 && userData.chatRoomBans >= maxChatRoomBans) {
                        note = new EmailUserNotification();
                        note.message = target + " banned from chat room " + room + " by " + instigator + ". Permanent suspension from entering chat rooms";
                    }
                    if (note != null) {
                        note.subject = "[BANNED FROM CHATROOM] " + target;
                        note.emailAddress = SystemProperty.get("ReportChatRoomBanEmail");
                        if (note.emailAddress.length() > 0) {
                            EJBIcePrxFinder.getUserNotificationServiceProxy().notifyUserViaEmail(note);
                        }
                    }
                }
                catch (Exception e) {
                    log.warn((Object)"Unable to send chat room ban notification email", (Throwable)e);
                }
                if (targetUserPrx == null) break block21;
                targetUserPrx.userDetailChanged(userData.toIceObject());
            }
            Object var13_14 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block23;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block23;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var13_15 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void decrementChatroomBanCounter(String target) {
        block17: {
            PreparedStatement ps;
            Connection conn;
            block15: {
                conn = null;
                ps = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("UPDATE user SET chatroombans=chatroombans-1 WHERE username=? AND chatroombans > 0");
                ps.setString(1, target);
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Failed update chatroomban counter");
                }
                UserData userData = this.loadUser(target, false, true);
                UserPrx targetUserPrx = EJBIcePrxFinder.findOnewayUserPrx(target);
                if (targetUserPrx == null) break block15;
                targetUserPrx.userDetailChanged(userData.toIceObject());
            }
            Object var7_7 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block17;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block17;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public String processEmailNotification(String username, String from, String subject, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        String password;
        block29: {
            password = null;
            String mobilePhone = null;
            boolean emailAlert = false;
            boolean emailAlertSent = false;
            double smsEmailAlertCost = 0.0;
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select u.password, u.mobilephone, u.emailalert, u.emailalertsent, c.smsemailalertcost from user u, country c where u.username = ? and c.id = u.countryid");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                password = rs.getString("password");
                mobilePhone = rs.getString("mobilephone");
                Integer intVal = (Integer)rs.getObject("emailAlert");
                if (intVal != null) {
                    boolean bl = emailAlert = intVal != 0;
                }
                if ((intVal = (Integer)rs.getObject("emailAlertSent")) != null) {
                    emailAlertSent = intVal != 0;
                }
                smsEmailAlertCost = rs.getDouble("smsemailalertcost");
                rs.close();
                rs = null;
                if (SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.EMAIL_ALERT, username) && emailAlert && !emailAlertSent) {
                    try {
                        MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                        SystemSMSData systemSMSData = new SystemSMSData();
                        systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                        systemSMSData.subType = SystemSMSData.SubTypeEnum.EMAIL_ALERT;
                        systemSMSData.username = username;
                        systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
                        systemSMSData.destination = mobilePhone;
                        systemSMSData.cost = smsEmailAlertCost;
                        String smsText = SystemProperty.get("EmailAlertSMS").replaceAll("%1", username).replaceAll("%2", subject);
                        if (smsText.length() > 160) {
                            smsText = smsText.substring(0, 159);
                        }
                        systemSMSData.messageText = smsText;
                        messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
                    }
                    catch (CreateException e) {
                        throw new EJBException("Unable to send an SMS email alert to the user " + username + ": " + e.getMessage());
                    }
                    this.setEmailAlertSent(username, true);
                }
            } else {
                throw new EJBException("Unable to load details from the DB for the user " + username);
            }
            Object var19_19 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block29;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block29;
            {
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var19_20 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return password;
    }

    /*
     * Loose catch block
     */
    public void setEmailAlertSent(String username, boolean emailAlertSent) throws EJBException {
        block24: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set emailAlertSent = ? where username = ?");
            if (emailAlertSent) {
                ps.setInt(1, 1);
            } else {
                ps.setInt(1, 0);
            }
            ps.setString(2, username);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not update EmailAlertSent: User does not exist");
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public UserData createUserMerchant(UserData userData, UserProfileData userProfileData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        if (SystemProperty.getBool(SystemPropertyEntities.Registration.REGISTRATION_DISABLED)) {
            throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
        }
        try {
            UserData merchantUserData = this.loadUser(userData.merchantCreated, false, false);
            if (merchantUserData == null) {
                throw new EJBException("Invalid merchant username provided");
            }
            if (merchantUserData.type.value() == UserData.TypeEnum.MIG33.value()) {
                throw new EJBException("You must be a migme merchant to create user accounts");
            }
            if (merchantUserData.status.value() == UserData.StatusEnum.INACTIVE.value()) {
                throw new EJBException("Your account must be active to create users");
            }
            userData.password = this.generatePassword();
            userData = this.createUser(userData, userProfileData, false, new UserRegistrationContextData(null, false, RegistrationType.MOBILE_REGISTRATION), accountEntrySourceData);
            if (merchantUserData.type == UserData.TypeEnum.MIG33_TOP_MERCHANT) {
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                accountBean.tagMerchantPending(null, userData.userID, userData.username, merchantUserData.userID, merchantUserData.username);
            }
            this.sendMerchantActivatedUserSMS(userData.username, userData.password, userData.mobilePhone, accountEntrySourceData);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return userData;
    }

    private String generatePassword() {
        String randomPassword = String.format("%1$04d", this.randomGen.nextInt(9000) + 1000);
        String charSet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 2; ++i) {
            randomPassword = randomPassword + charSet.charAt(this.randomGen.nextInt(charSet.length()));
        }
        return this.scrambleString(randomPassword);
    }

    private String scrambleString(String word) {
        StringBuilder builder = new StringBuilder(word.length());
        boolean[] used = new boolean[word.length()];
        for (int i = 0; i < word.length(); ++i) {
            int rndIndex;
            while (used[rndIndex = new Random().nextInt(word.length())]) {
            }
            used[rndIndex] = true;
            builder.append(word.charAt(rndIndex));
        }
        return builder.toString();
    }

    /*
     * Loose catch block
     */
    public void sendLookouts(String username, AccountEntrySourceData accountEntrySourceData) {
        block39: {
            if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.LOOKOUT, username)) {
                return;
            }
            Connection connMaster = null;
            Connection connSlave = null;
            PreparedStatement psSlave = null;
            Statement psMaster = null;
            ResultSet rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            psSlave = connSlave.prepareStatement("select lookout.username, user.mobilephone, country.smslookoutcost from lookout, user, contact, currency, country where lookout.contactusername=? and lookout.username=user.username and lookout.contactusername=contact.username and lookout.username=contact.fusionusername and user.currency=currency.code and country.id = user.countryid and user.balance/currency.exchangerate> country.smslookoutcost and lookout.username not in (select blockusername from blocklist where username=?) and (lookout.datelastsent is null or lookout.datelastsent < DATE_SUB(now(), INTERVAL ? SECOND))");
            psSlave.setString(1, username);
            psSlave.setString(2, username);
            psSlave.setInt(3, SystemProperty.getInt("MinSecondsBetweenLookoutSMS"));
            rs = psSlave.executeQuery();
            HashMap<String, SystemSMSData> stalkers = new HashMap<String, SystemSMSData>();
            while (rs.next()) {
                String stalkerUsername = rs.getString(1);
                String stalkerMobilePhone = rs.getString(2);
                double smsLookoutCost = rs.getDouble(3);
                SystemSMSData systemSMSData = new SystemSMSData();
                systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                systemSMSData.subType = SystemSMSData.SubTypeEnum.LOOKOUT;
                systemSMSData.username = stalkerUsername;
                systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
                systemSMSData.destination = stalkerMobilePhone;
                systemSMSData.messageText = SystemProperty.get("LookoutSMS").replaceAll("%1", username).replaceAll("%2", stalkerUsername);
                systemSMSData.cost = smsLookoutCost;
                stalkers.put(stalkerUsername, systemSMSData);
            }
            rs.close();
            rs = null;
            psSlave.close();
            psSlave = null;
            connSlave.close();
            connSlave = null;
            if (stalkers.size() > 0) {
                connMaster = this.dataSourceMaster.getConnection();
                psMaster = connMaster.prepareStatement("update lookout set DateLastSent=now() where username=? and contactusername=?");
                for (String stalkerUsername : stalkers.keySet()) {
                    SystemSMSData systemSMSData = (SystemSMSData)stalkers.get(stalkerUsername);
                    try {
                        MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                        messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
                        psMaster.setString(1, stalkerUsername);
                        psMaster.setString(2, username);
                        psMaster.executeUpdate();
                    }
                    catch (Exception e) {}
                }
            }
            Object var15_17 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (psSlave != null) {
                    psSlave.close();
                }
            }
            catch (SQLException e) {
                psSlave = null;
            }
            try {
                if (psMaster != null) {
                    psMaster.close();
                }
            }
            catch (SQLException e) {
                psMaster = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e) {
                connMaster = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
                break block39;
            }
            catch (SQLException e) {
                connSlave = null;
            }
            break block39;
            {
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var15_18 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (psSlave != null) {
                        psSlave.close();
                    }
                }
                catch (SQLException e) {
                    psSlave = null;
                }
                try {
                    if (psMaster != null) {
                        psMaster.close();
                    }
                }
                catch (SQLException e) {
                    psMaster = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean midletRegistrationDisabled(String username, String password, String registrationIPAddress) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block37: {
            block33: {
                connSlave = null;
                ps = null;
                rs = null;
                connSlave = this.dataSourceSlave.getConnection();
                ps = connSlave.prepareStatement("select ip from blockedregistrationip where ? like ip limit 1");
                ps.setString(1, registrationIPAddress);
                rs = ps.executeQuery();
                if (!rs.next()) break block33;
                boolean bl = true;
                Object var9_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                return bl;
            }
            rs.close();
            ps.close();
            ps = connSlave.prepareStatement("select password from blockedregistrationpassword where password = ?");
            ps.setString(1, password);
            rs = ps.executeQuery();
            if (!rs.next()) break block37;
            boolean bl = true;
            Object var9_12 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return bl;
        }
        boolean bl = SystemProperty.getBool("midletRegistrationDisabled", false);
        Object var9_13 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_14 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public GroupMemberData getGroupMember(String username, int groupID) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block26: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            String sql = "select m.*, u.displaypicture, sub.id vipsubscriptionid from groupmember m inner join user u on m.username=u.username inner join groups g on m.groupid=g.id left outer join service on (g.vipserviceid=service.id and service.status=?) left outer join subscription sub on (sub.serviceid=service.id and sub.status=? and sub.username=u.username) where m.username=? and m.groupid=? and m.status in (?, ?)";
            ps = connSlave.prepareStatement(sql);
            ps.setInt(1, ServiceData.StatusEnum.ACTIVE.value());
            ps.setInt(2, SubscriptionData.StatusEnum.ACTIVE.value());
            ps.setString(3, username);
            ps.setInt(4, groupID);
            ps.setInt(5, GroupMemberData.StatusEnum.ACTIVE.value());
            ps.setInt(6, GroupMemberData.StatusEnum.BANNED.value());
            rs = ps.executeQuery();
            if (rs.next()) break block26;
            GroupMemberData groupMemberData = null;
            Object var10_10 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return groupMemberData;
        }
        GroupMemberData memberData = new GroupMemberData(rs);
        memberData.displayPicture = rs.getString("displayPicture");
        GroupMemberData groupMemberData = memberData;
        Object var10_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return groupMemberData;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public GroupData getGroup(int id) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block26: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select groups.*, service.status vipservicestatus from groups LEFT OUTER JOIN service ON (groups.vipserviceid=service.id and service.status=1) where groups.id=? and groups.status=1");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) break block26;
            GroupData groupData = null;
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return groupData;
        }
        GroupData groupData = new GroupData(rs);
        rs.close();
        ps.close();
        GroupData groupData2 = groupData;
        Object var8_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return groupData2;
        catch (SQLException e) {
            try {
                log.warn((Object)("Exception: " + e.getMessage()), (Throwable)e);
                throw new EJBException(e.getMessage(), (Exception)e);
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean isUserMobileVerified(String username) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select mobileverified from user where username = ?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Unknown user " + username);
        }
        boolean bl = rs.getBoolean(1);
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    public boolean isUserMobileOrEmailVerified(String username) throws EJBException {
        return this.checkIsUserMobileOrEmailVerifiedFromDB(username);
    }

    /*
     * Loose catch block
     */
    private boolean checkIsUserMobileOrEmailVerifiedFromDB(String username) {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select u.mobileverified mobile, uea.verified email from user u, userid uid LEFT OUTER JOIN useremailaddress uea ON uid.id = uea.userid and uea.type = ? where u.username = uid.username and u.username = ?");
        ps.setInt(1, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
        ps.setString(2, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Unknown user " + username);
        }
        boolean bl = rs.getBoolean(1) || rs.getBoolean(2);
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    public boolean isUserMobileOrEmailVerifiedWithTxSupported(String username) throws EJBException {
        return this.checkIsUserMobileOrEmailVerifiedFromDB(username);
    }

    public boolean isUserEmailVerified(String username) throws FusionEJBException {
        return this.checkIsUserEmailVerified(username);
    }

    /*
     * Loose catch block
     */
    private boolean checkIsUserEmailVerified(String username) throws FusionEJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select uea.verified emailVerified from userid uid LEFT OUTER JOIN useremailaddress uea ON uid.id = uea.userid and uea.type = ? and uea.verified = true where uid.username = ? limit 1");
        ps.setInt(1, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
        ps.setString(2, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new FusionEJBException("Unknown user " + username);
        }
        boolean bl = rs.getBoolean(1);
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    public boolean isUserEmailVerifiedWithTxSupport(String username) throws FusionEJBException {
        return this.checkIsUserEmailVerified(username);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public AuthenticatedAccessControlParameter getUserAuthenticatedAccessControlParameter(String username) throws EJBException {
        block31: {
            block30: {
                block25: {
                    block29: {
                        block28: {
                            block24: {
                                connSlave = null;
                                ps = null;
                                rs = null;
                                try {
                                    try {
                                        connSlave = this.dataSourceSlave.getConnection();
                                        ps = connSlave.prepareStatement("select u.mobileverified mobile, uea.verified email from user u, userid uid LEFT OUTER JOIN useremailaddress uea ON uid.id = uea.userid and uea.type = ? where u.username = uid.username and u.username = ?");
                                        ps.setInt(1, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY.value);
                                        ps.setString(2, username);
                                        rs = ps.executeQuery();
                                        if (rs.next()) {
                                            var5_5 = new AuthenticatedAccessControlParameter(username, rs.getBoolean("mobile"), rs.getBoolean("email"));
                                            var7_9 = null;
                                            break block24;
                                        }
                                        var5_6 = null;
                                        break block25;
                                    }
                                    catch (SQLException e) {
                                        throw new EJBException(e.getMessage(), (Exception)e);
                                    }
                                    catch (IllegalArgumentException e) {
                                        throw new EJBException(e.getMessage(), (Exception)e);
                                    }
                                }
                                catch (Throwable var6_15) {
                                    var7_11 = null;
                                    try {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                    try {
                                        if (ps != null) {
                                            ps.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        ps = null;
                                    }
                                    try {
                                        if (connSlave == null) throw var6_15;
                                        connSlave.close();
                                        throw var6_15;
                                    }
                                    catch (SQLException e) {
                                        connSlave = null;
                                        throw var6_15;
                                    }
                                }
                            }
                            ** try [egrp 2[TRYBLOCK] [7 : 155->170)] { 
lbl49:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block28;
lbl52:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [8 : 175->188)] { 
lbl56:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block29;
lbl59:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var5_5;
                    }
                    if (connSlave == null) return var5_5;
                    connSlave.close();
                    return var5_5;
                }
                var7_10 = null;
                ** try [egrp 2[TRYBLOCK] [7 : 155->170)] { 
lbl71:
                // 1 sources

                if (rs != null) {
                    rs.close();
                }
                break block30;
lbl74:
                // 1 sources

                catch (SQLException e) {
                    rs = null;
                }
            }
            ** try [egrp 3[TRYBLOCK] [8 : 175->188)] { 
lbl78:
            // 1 sources

            if (ps != null) {
                ps.close();
            }
            break block31;
lbl81:
            // 1 sources

            catch (SQLException e) {
                ps = null;
            }
        }
        try {}
        catch (SQLException e) {
            return var5_6;
        }
        if (connSlave == null) return var5_6;
        connSlave.close();
        return var5_6;
    }

    /*
     * Loose catch block
     */
    public boolean userAttemptedVerification(String username, int hours) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select if (count(*)<>0,1,0) from systemsms where username=? and type=1 and subtype=1 and datecreated > DATE_SUB(now(), INTERVAL ? HOUR)");
        ps.setString(1, username);
        ps.setInt(2, hours);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Unknown user " + username);
        }
        boolean bl = rs.getBoolean(1);
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    private UserSettingData getUserSetting(String username, UserSettingData.TypeEnum type) throws EJBException {
        if (SystemProperty.getBool(SystemPropertyEntities.Temp.WW519_EMAIL_NOTIFICATION_USER_SETTINGS_ENABLED)) {
            List<UserSettingData> userSettings = null;
            try {
                userSettings = this.getUserSettings(username);
            }
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            if (null != userSettings) {
                for (UserSettingData setting : userSettings) {
                    if (setting.type != type) continue;
                    return setting;
                }
            }
            return null;
        }
        return this.getUserSetting_beforeWW519(username, type);
    }

    /*
     * Loose catch block
     */
    private UserSettingData getUserSetting_beforeWW519(String username, UserSettingData.TypeEnum type) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block26: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select * from usersetting where username = ? and type = ?");
            ps.setString(1, username);
            ps.setInt(2, type.value());
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            UserSettingData userSettingData = new UserSettingData(rs);
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return userSettingData;
        }
        UserSettingData userSettingData = null;
        Object var8_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return userSettingData;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateUserSetting(String username, UserSettingData.TypeEnum type, int value) throws EJBException {
        block16: {
            Connection connMaster = null;
            Statement ps = null;
            connMaster = this.dataSourceMaster.getConnection();
            String sql = "insert into usersetting (username, type, value) values (?,?,?) ON DUPLICATE KEY UPDATE value = ?";
            ps = connMaster.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, type.value());
            ps.setInt(3, value);
            ps.setInt(4, value);
            if (ps.executeUpdate() == 0) {
                throw new EJBException("Unable to update user setting for " + username);
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.USER_SETTING, username);
            EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.SETTINGS));
            Object var8_8 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
                break block16;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block16;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                throw throwable;
            }
        }
    }

    public UserSettingData.AnonymousCallEnum getAnonymousCallSetting(String username) throws EJBException {
        UserSettingData userSettingData = this.getUserSetting(username, UserSettingData.TypeEnum.ANONYMOUS_CALL);
        if (userSettingData == null) {
            return UserSettingData.AnonymousCallEnum.defaultValue();
        }
        return UserSettingData.AnonymousCallEnum.fromValue(userSettingData.value);
    }

    public void updateAnonymousCallSetting(String username, UserSettingData.AnonymousCallEnum setting) throws EJBException {
        this.updateUserSetting(username, UserSettingData.TypeEnum.ANONYMOUS_CALL, setting.value());
        UserPrx userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
        if (userPrx != null) {
            userPrx.anonymousCallSettingChanged(setting.value());
        }
    }

    public UserSettingData.MessageEnum getMessageSetting(String username) throws EJBException {
        UserSettingData userSettingData = this.getUserSetting(username, UserSettingData.TypeEnum.MESSAGE);
        if (userSettingData == null) {
            return UserSettingData.MessageEnum.defaultValue();
        }
        return UserSettingData.MessageEnum.fromValue(userSettingData.value);
    }

    public void updateMessageSetting(String username, UserSettingData.MessageEnum setting) throws EJBException {
        this.updateUserSetting(username, UserSettingData.TypeEnum.MESSAGE, setting.value());
        UserPrx userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
        if (userPrx != null) {
            userPrx.messageSettingChanged(setting.value());
        }
    }

    public UserSettingData.EmailSettingEnum getEmailNotificationSetting(String username, UserSettingData.TypeEnum emailType) throws EJBException {
        UserSettingData userSettingData = this.getUserSetting(username, emailType);
        if (userSettingData == null) {
            return UserSettingData.EmailSettingEnum.defaultValue();
        }
        return UserSettingData.EmailSettingEnum.fromValue(userSettingData.value);
    }

    public void updateEmailNotificationSetting(String username, UserSettingData.TypeEnum emailType, UserSettingData.EmailSettingEnum setting) throws EJBException {
        if (UserSettingData.isValidEmailTypeEnum(emailType)) {
            this.updateUserSetting(username, emailType, setting.value());
        }
    }

    public ReputationLevelData getReputationLevelByUserid(int userId) throws EJBException {
        return this.getReputationLevelByUserid(userId, false);
    }

    public ReputationLevelData getReputationLevelByUserid(int userId, boolean skipCacheCheck) throws EJBException {
        try {
            UserReputationScoreAndLevelData data = this.getReputationScoreAndLevel(userId, null, skipCacheCheck);
            return MemCacheOrEJB.getReputationLevelDataForLevel(data.level, skipCacheCheck);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public ReputationLevelData getReputationLevel(String username) throws EJBException {
        return this.getReputationLevel(username, false);
    }

    public ReputationLevelData getReputationLevel(String username, boolean skipCacheCheck) throws EJBException {
        int userid = this.getUserID(username, null);
        return this.getReputationLevelByUserid(userid, skipCacheCheck);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void addReputationScore(int userid, int score, Connection suppliedConn) throws EJBException {
        block23: {
            ConnectionHolder ch = null;
            Statement ps = null;
            ResultSet rs = null;
            ch = new ConnectionHolder(this.dataSourceMaster, suppliedConn);
            Connection c = ch.getConnection();
            try {
                this.updateReputationScore(c, userid, score, true);
                this.invalidateCacheAndNotifyReputationScoreUpdated(userid);
                Object var9_9 = null;
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                c.close();
                throw throwable;
            }
            c.close();
            Object var11_12 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
                break block23;
            }
            catch (SQLException e) {
                ch = null;
            }
            break block23;
            {
                catch (Exception e) {
                    throw new EJBException(e.getMessage(), e);
                }
            }
            catch (Throwable throwable) {
                Object var11_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    private void updateReputationScore(Connection conn, int userid, int score, boolean isScoreIncrement) throws SQLException, EJBException, Exception {
        this.updateReputationScoreAndGet(conn, userid, score, isScoreIncrement, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UserReputationScoreAndLevelData updateReputationScoreAndGet(Connection conn, int userid, int score, boolean isScoreIncrement, boolean getScoreAfterUpdate) throws SQLException, EJBException, Exception {
        String sql = isScoreIncrement ? "INSERT INTO score VALUES(?,?,now()) ON DUPLICATE KEY UPDATE SCORE = SCORE + ?, lastUpdated=now()" : "INSERT INTO score VALUES(?,?,now()) ON DUPLICATE KEY UPDATE SCORE = ?, lastUpdated=now()";
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, userid);
            ps.setInt(2, score);
            ps.setInt(3, score);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                log.info((Object)("Awarded new " + score + " reputation score for userid: " + userid));
            } else if (rowsAffected > 1) {
                if (isScoreIncrement) {
                    log.info((Object)("Awarded additional " + score + " reputation score for userid: " + userid));
                } else {
                    log.info((Object)("Awarded with " + score + " reputation score for userid: " + userid));
                }
            } else {
                throw new EJBException("Unable to update score for userid: " + userid + " rowsAffected:" + rowsAffected);
            }
            Object var10_10 = null;
        }
        catch (Throwable throwable) {
            Object var10_11 = null;
            ps.close();
            throw throwable;
        }
        ps.close();
        if (getScoreAfterUpdate) {
            UserReputationScoreAndLevelData result;
            UserReputationScoreData userReputationScoreData = this.getUserReputationScoreFromDB(conn, userid);
            ReputationLevelScoreRanges.LevelScoreRangeEntry entry = ReputationLevelScoreRanges.getInstance().getLevelScoreRange(userReputationScoreData.score, new ConnectionCreator.FromDataSource(this.dataSourceSlave));
            if (entry == null) {
                log.warn((Object)("Unable to get LevelScoreRangeEntry for user [ " + userid + "]"));
                result = null;
            } else {
                result = new UserReputationScoreAndLevelData(userid, userReputationScoreData.score, entry.level, userReputationScoreData.lastUpdated, true);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Update score userid:[" + userid + "],score:[" + score + "],isIncrement:[" + isScoreIncrement + "],current score and leveldata:[" + result + "]"));
            }
            return result;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateReputationScore(int userid, int score, boolean isScoreIncrement) throws EJBException {
        try {
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                this.updateReputationScore(conn, userid, score, isScoreIncrement);
                Object var6_8 = null;
            }
            catch (Throwable throwable) {
                Object var6_9 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            UserBean.invalidateReputationScore(userid);
        }
        catch (EJBException ex) {
            throw ex;
        }
        catch (SQLException ex) {
            throw new EJBException("Failed to update score for userid: " + userid + " score:" + score + ". Exception:" + ex, (Exception)ex);
        }
        catch (Exception ex) {
            throw new EJBException("Unhandled exception:" + ex, ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UserReputationScoreAndLevelData updateReputationScoreAndGet(int userid, int score, boolean isScoreIncrement) throws EJBException {
        try {
            UserReputationScoreAndLevelData result;
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                result = this.updateReputationScoreAndGet(conn, userid, score, isScoreIncrement, true);
                Object var7_9 = null;
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            UserBean.invalidateReputationScore(userid);
            return result;
        }
        catch (EJBException ex) {
            throw ex;
        }
        catch (SQLException ex) {
            throw new EJBException("Failed to update score for userid: " + userid + " score:" + score + ". Exception:" + ex, (Exception)ex);
        }
        catch (Exception ex) {
            throw new EJBException("Unhandled exception:" + ex, ex);
        }
    }

    private static void invalidateReputationScore(int userid) {
        String userIdStr = Integer.toString(userid);
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Invalidating reputation score cache [" + MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, userIdStr) + "]"));
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, userIdStr);
        }
        catch (Exception e) {
            log.error((Object)("unable to invalidate reputation score cache [" + MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, userIdStr) + "].Exception:" + e), (Throwable)e);
        }
    }

    public void invalidateCacheAndNotifyReputationScoreUpdated(int userid) {
        UserBean.invalidateReputationScore(userid);
        String username = this.getUsernameByUserid(userid, null);
        try {
            UserPrx userPrx = EJBIcePrxFinder.findOnewayUserPrx(username);
            if (null != userPrx) {
                userPrx.userReputationChanged();
            }
        }
        catch (Exception e) {
            log.error((Object)("unable to notify a reputation score change for user object proxy for [" + username + "].Exception:" + e), (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UserReputationScoreData getUserReputationScoreFromDB(Connection conn, int userid) throws SQLException {
        UserReputationScoreData userReputationScoreData;
        PreparedStatement pstmt = conn.prepareStatement("select * from score s where s.userid=?");
        try {
            pstmt.setInt(1, userid);
            ResultSet rs = pstmt.executeQuery();
            try {
                if (rs.next()) {
                    userReputationScoreData = UserReputationScoreData.fromResultSet(rs);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("User [" + userid + "] score [" + userReputationScoreData + "]"));
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("User [" + userid + "] has no entries in the score table. Assigning with initial score"));
                    }
                    userReputationScoreData = new UserReputationScoreData(userid, 0, new Timestamp(0L));
                }
                Object var7_6 = null;
            }
            catch (Throwable throwable) {
                Object var7_7 = null;
                rs.close();
                throw throwable;
            }
            rs.close();
            Object var9_9 = null;
        }
        catch (Throwable throwable) {
            Object var9_10 = null;
            pstmt.close();
            throw throwable;
        }
        pstmt.close();
        return userReputationScoreData;
    }

    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int userid, Connection conn) {
        return this.getReputationScoreAndLevel(userid, conn, false);
    }

    public UserReputationScoreAndLevelData getReputationScoreAndLevel(int userid, Connection conn, boolean skipCacheCheck) throws EJBException {
        return this.getReputationScoreAndLevel(false, userid, skipCacheCheck);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UserReputationScoreAndLevelData getReputationScoreAndLevel(boolean mustUseMaster, int userid, boolean skipCacheCheck) throws EJBException {
        try {
            UserReputationScoreAndLevelData result;
            boolean needToRefreshCache;
            UserReputationScoreAndLevelData cachedScoreData;
            if (skipCacheCheck) {
                cachedScoreData = null;
                needToRefreshCache = true;
            } else {
                cachedScoreData = (UserReputationScoreAndLevelData)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, Integer.toString(userid));
                if (log.isDebugEnabled()) {
                    log.debug((Object)("ScoreData from cache:" + cachedScoreData));
                }
                boolean bl = needToRefreshCache = cachedScoreData == null || false == cachedScoreData.isCompatible(mustUseMaster);
            }
            if (needToRefreshCache) {
                UserReputationScoreAndLevelData refreshedScoreData;
                Connection conn = (mustUseMaster ? this.dataSourceMaster : this.dataSourceSlave).getConnection();
                try {
                    refreshedScoreData = this.getReputationScoreAndLevelFromDB(conn, userid, mustUseMaster);
                    Object var10_9 = null;
                }
                catch (Throwable throwable) {
                    Object var10_10 = null;
                    conn.close();
                    throw throwable;
                }
                conn.close();
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, Integer.toString(userid), refreshedScoreData);
                result = refreshedScoreData;
            } else {
                result = cachedScoreData;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)((needToRefreshCache ? "(DB)" : "(Cached)") + " reputation data " + result));
            }
            return result;
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private UserReputationScoreAndLevelData getReputationScoreAndLevelFromDB(Connection conn, int userid, boolean fromMaster) throws SQLException {
        UserReputationScoreAndLevelData userReputationScoreAndLevelData;
        String sql = "select s.*, max(r.level) as level from score s, reputationscoretolevel r where s.userid = ? and s.score >= r.score;";
        PreparedStatement ps = conn.prepareStatement("select s.*, max(r.level) as level from score s, reputationscoretolevel r where s.userid = ? and s.score >= r.score;");
        try {
            ps.setInt(1, userid);
            ResultSet rs = ps.executeQuery();
            try {
                UserReputationScoreAndLevelData scoreAndLevelData;
                if (rs.next() && rs.getInt("level") != 0) {
                    scoreAndLevelData = new UserReputationScoreAndLevelData(rs, fromMaster);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Found score data for user: " + userid + " score: " + scoreAndLevelData.score));
                    }
                } else {
                    scoreAndLevelData = new UserReputationScoreAndLevelData(userid, 0, 1, new Date(0L), fromMaster);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("No score data found for user : " + userid + " using default values"));
                    }
                }
                userReputationScoreAndLevelData = scoreAndLevelData;
                Object var10_9 = null;
            }
            catch (Throwable throwable) {
                Object var10_10 = null;
                rs.close();
                throw throwable;
            }
            rs.close();
            Object var12_11 = null;
        }
        catch (Throwable throwable) {
            Object var12_12 = null;
            ps.close();
            throw throwable;
        }
        ps.close();
        return userReputationScoreAndLevelData;
    }

    /*
     * Loose catch block
     */
    public Map<Integer, UserReputationScoreAndLevelData> getReputationScoreAndLevelForUsers(List<Integer> userIDs, Connection conn) throws EJBException {
        Serializable sql;
        ConnectionHolder ch = null;
        Statement ps = null;
        ResultSet rs = null;
        HashMap<Integer, UserReputationScoreAndLevelData> resultMap = new HashMap<Integer, UserReputationScoreAndLevelData>();
        LinkedList<String> uncachedKeys = new LinkedList<String>();
        String[] keys = new String[userIDs.size()];
        for (int i = 0; i < keys.length; ++i) {
            keys[i] = userIDs.get(i).toString();
        }
        Map<String, Object> map = MemCachedClientWrapper.getMulti(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, keys);
        for (String s : map.keySet()) {
            UserReputationScoreAndLevelData scoreData = (UserReputationScoreAndLevelData)map.get(s);
            if (scoreData == null) {
                uncachedKeys.add(s);
                continue;
            }
            resultMap.put(Integer.parseInt(s), scoreData);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Found [" + resultMap.size() + "] cached entries for getReputationScoreAndLevelForUsers()"));
            log.debug((Object)("Found [" + uncachedKeys.size() + "] uncached entries for getReputationScoreAndLevelForUsers()"));
        }
        if (uncachedKeys.size() > 0) {
            int i;
            ch = new ConnectionHolder(this.dataSourceSlave, conn);
            sql = new StringBuilder();
            ((StringBuilder)sql).append("select s.*, max(r.level) as level from score s, reputationscoretolevel r where s.userid in (");
            for (i = 0; i < uncachedKeys.size(); ++i) {
                ((StringBuilder)sql).append('?');
                if (i >= uncachedKeys.size() - 1) continue;
                ((StringBuilder)sql).append(',');
            }
            ((StringBuilder)sql).append(") and s.score >= r.score group by s.userid;");
            ps = ch.getConnection().prepareStatement(((StringBuilder)sql).toString());
            for (i = 0; i < uncachedKeys.size(); ++i) {
                ps.setInt(i + 1, Integer.parseInt((String)uncachedKeys.get(i)));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("level") == 0) continue;
                UserReputationScoreAndLevelData scoreData = new UserReputationScoreAndLevelData(rs, false);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Found score data in the database for user: " + scoreData.userID + " score: " + scoreData.score));
                }
                resultMap.put(scoreData.userID, scoreData);
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, Integer.toString(scoreData.userID), scoreData);
            }
            for (String s : uncachedKeys) {
                if (resultMap.containsKey(Integer.parseInt(s))) continue;
                UserReputationScoreAndLevelData scoreData = new UserReputationScoreAndLevelData(Integer.parseInt(s), 0, 1, new Date(0L), false);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("No score data found for user : " + s + " using default values"));
                }
                resultMap.put(scoreData.userID, scoreData);
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.REPUTATION_SCORE, Integer.toString(scoreData.userID), scoreData);
            }
        }
        sql = resultMap;
        Object var15_19 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return sql;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var15_20 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void createLookout(String creatorUsername, String contactUsername) throws EJBException {
        block23: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select username from lookout where username=? and contactusername=?");
            ps.setString(1, creatorUsername);
            ps.setString(2, contactUsername);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                ps = conn.prepareStatement("insert into lookout (username, contactusername) values (?, ?)");
                ps.setString(1, creatorUsername);
                ps.setString(2, contactUsername);
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Failed to create Lookout");
                }
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block23;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block23;
            {
                catch (SQLException e) {
                    throw new EJBException("Sorry, an internal error occurred: " + e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public double getPriceInUserCurrency(double originalPrice, String originalCurrencyCode, String username) throws FusionEJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        String sql = "SELECT ? / original_currency.ExchangeRate * currency_user.ExchangeRate Price, user.Currency UserCurrency FROM user, currency currency_user, currency original_currency WHERE user.Username=? and user.Currency=currency_user.Code and original_currency.Code=?";
        ps = connSlave.prepareStatement(sql);
        ps.setDouble(1, originalPrice);
        ps.setString(2, username);
        ps.setString(3, originalCurrencyCode);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new FusionEJBException(String.format("Unknown user '%s' or incorrect currency '%s'", username, originalCurrencyCode));
        }
        double d = rs.getDouble(1);
        Object var12_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return d;
        catch (SQLException e) {
            try {
                log.error((Object)"Exception occured in getPriceInUserCurrency: ", (Throwable)e);
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var12_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    public UserMigboProfileData.AdminEnum getAdminLabel(String username) throws FusionEJBException {
        if (SystemProperty.getBool(SystemPropertyEntities.UserProfileSettings.GET_ADMIN_LABELS_VERSION_1)) {
            return this.getAdminLabelV1(username);
        }
        return this.getAdminLabelV2(username);
    }

    /*
     * Loose catch block
     */
    private UserMigboProfileData.AdminEnum getAdminLabelV1(String username) throws FusionEJBException {
        UserMigboProfileData.AdminEnum adminEnum;
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block77: {
            String sql;
            block73: {
                UserMigboProfileData.AdminEnum e4;
                block69: {
                    block65: {
                        connSlave = null;
                        ps = null;
                        rs = null;
                        connSlave = this.dataSourceSlave.getConnection();
                        sql = "SELECT chatroomadmin FROM user WHERE username=?";
                        ps = connSlave.prepareStatement(sql);
                        ps.setString(1, username);
                        rs = ps.executeQuery();
                        if (!rs.next() || rs.getInt(1) != 1) break block65;
                        UserMigboProfileData.AdminEnum adminEnum2 = UserMigboProfileData.AdminEnum.GLOBAL_ADMIN;
                        Object var8_15 = null;
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        catch (SQLException e2) {
                            rs = null;
                        }
                        try {
                            if (ps != null) {
                                ps.close();
                            }
                        }
                        catch (SQLException e2) {
                            ps = null;
                        }
                        try {
                            if (connSlave != null) {
                                connSlave.close();
                            }
                        }
                        catch (SQLException e2) {
                            connSlave = null;
                        }
                        return adminEnum2;
                    }
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e3) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e4) {
                        ps = null;
                    }
                    sql = "SELECT createdby FROM groups WHERE createdby=? LIMIT 1";
                    ps = connSlave.prepareStatement(sql);
                    ps.setString(1, username);
                    rs = ps.executeQuery();
                    if (!rs.next()) break block69;
                    e4 = UserMigboProfileData.AdminEnum.GROUP_ADMIN;
                    Object var8_16 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                    }
                    catch (SQLException e2) {
                        connSlave = null;
                    }
                    return e4;
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e5) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e6) {
                    ps = null;
                }
                sql = "SELECT username FROM chatroommoderator WHERE username=? LIMIT 1";
                ps = connSlave.prepareStatement(sql);
                ps.setString(1, username);
                rs = ps.executeQuery();
                if (!rs.next()) break block73;
                e4 = UserMigboProfileData.AdminEnum.CHATROOM_ADMIN;
                Object var8_17 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                return e4;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            sql = "SELECT creator FROM chatroom WHERE creator=? LIMIT 1";
            ps = connSlave.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block77;
            adminEnum = UserMigboProfileData.AdminEnum.CHATROOM_ADMIN;
            Object var8_18 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return adminEnum;
        }
        adminEnum = UserMigboProfileData.AdminEnum.NOT_ADMIN;
        Object var8_19 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return adminEnum;
        catch (SQLException e) {
            try {
                log.error((Object)("SQLException occurred in getAdminLabel: " + e));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_20 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    private UserMigboProfileData.AdminEnum getAdminLabelV2(String username) throws FusionEJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block20: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            String sql = "SELECT CASE WHEN u.chatroomadmin = 1 THEN 'A' WHEN (SELECT 1 FROM groups g WHERE g.createdby = u.username LIMIT 1) IS NOT NULL THEN 'GA' WHEN (SELECT 1 FROM chatroommoderator cm WHERE cm.username = u.username LIMIT 1) IS NOT NULL THEN 'CA' WHEN (SELECT 1 FROM chatroom c WHERE c.creator = u.username LIMIT 1) IS NOT NULL THEN 'CA' ELSE 'N' END adminType FROM user u WHERE username = ?";
            ps = connSlave.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            UserMigboProfileData.AdminEnum adminEnum = UserMigboProfileData.AdminEnum.fromValue(rs.getString("adminType"));
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e) {
                connSlave = null;
            }
            return adminEnum;
        }
        try {
            try {
                log.error((Object)("Unable to get admin label for user [" + username + "]."));
                throw new FusionEJBException("Unable to get user label");
            }
            catch (SQLException e) {
                log.error((Object)("SQLException occurred in getAdminLabel: " + e));
                throw new FusionEJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e) {
                connSlave = null;
            }
            throw throwable;
        }
    }

    /*
     * Loose catch block
     */
    public UserProfileLabelsData getUserLabels(String username) throws Exception {
        Connection connSlave = null;
        Statement ps = null;
        ResultSet rs = null;
        String sql = null;
        UserProfileLabelsData userProfileLabels = new UserProfileLabelsData();
        userProfileLabels.admin = this.getAdminLabel(username).value();
        if (SystemProperty.getBool(SystemPropertyEntities.UserProfileSettings.GET_USER_MERCHANT_LABELS_ENABLED)) {
            userProfileLabels.merchant = this.getMerchantLabel(username).value();
        }
        sql = "SELECT IFNULL(verified, 0) verified FROM userid u      , userverified uv WHERE u.id = uv.userid AND u.username = ?";
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement(sql);
        ps.setString(1, username);
        rs = ps.executeQuery();
        userProfileLabels.isVerified = rs.next() ? rs.getBoolean("verified") : false;
        UserProfileLabelsData userProfileLabelsData = userProfileLabels;
        Object var9_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return userProfileLabelsData;
        catch (SQLException e) {
            try {
                log.error((Object)("SQLException occurred in getUserLabels: " + e));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public UserMigboProfileData.MerchantEnum getMerchantLabel(String username) throws FusionEJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block26: {
            connSlave = null;
            ps = null;
            rs = null;
            String sql = null;
            sql = "SELECT IFNULL(CASE WHEN m.mentor = 'mentor' THEN 'MT' \t      ELSE 'M' END, 'N') merchantType FROM userid u      , merchantdetails m WHERE u.id = m.id AND u.username = ?";
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            UserMigboProfileData.MerchantEnum merchantEnum = UserMigboProfileData.MerchantEnum.fromValue(rs.getString("merchantType"));
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return merchantEnum;
        }
        UserMigboProfileData.MerchantEnum merchantEnum = UserMigboProfileData.MerchantEnum.NOT_MERCHANT;
        Object var8_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return merchantEnum;
        catch (SQLException e) {
            try {
                log.error((Object)("SQLException occurred in getUserLabels: " + e));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public MerchantDetailsData getBasicMerchantDetails(String username) throws FusionEJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block26: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            String sql = "SELECT * FROM merchantdetails WHERE id=(SELECT id FROM userid WHERE username=?)";
            ps = connSlave.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            log.debug((Object)("MerchantDetails found for username [" + username + "]"));
            MerchantDetailsData merchantDetailsData = new MerchantDetailsData(rs, false);
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return merchantDetailsData;
        }
        log.debug((Object)("MerchantDetails not found for username [" + username + "]"));
        MerchantDetailsData merchantDetailsData = null;
        Object var8_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return merchantDetailsData;
        catch (SQLException e) {
            try {
                log.error((Object)("SQLException occurred in getBasicMerchantDetails: " + e));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean isMerchantMentor(String username) throws FusionEJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block26: {
            String value;
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            String sql = "SELECT mentor FROM merchantdetails join userid on merchantdetails.id = userid.id where userid.username = ?";
            ps = connSlave.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next() || rs.wasNull() || (value = rs.getString(1)) == null || !value.equals("mentor")) break block26;
            boolean bl = true;
            Object var9_10 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var9_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                log.error((Object)("SQLException occurred in getMerchantLabel: " + e));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    public double getUserReferralSuccessRate(String username, boolean refreshCache) throws FusionEJBException {
        block29: {
            connSlave = null;
            ps = null;
            rs = null;
            minSystemSMSID = SystemProperty.getInt(SystemPropertyEntities.Default.MIN_SYSTEMSMS_ID_FOR_30_DAY_SCAN);
            minAccountEntryID = SystemProperty.getInt(SystemPropertyEntities.Default.MIN_ACCOUNTENTRY_ID_FOR_30_DAY_SCAN);
            result = MemCachedClientWrapper.getDouble(MemCachedKeySpaces.CommonKeySpace.USER_REFERRAL_SUCCESS_RATE, username);
            if (!refreshCache && result != null) ** GOTO lbl65
            windowDurationInDays = SystemProperty.getInt(SystemPropertyEntities.Default.REFERRAL_SUCCESS_RATIO_EVALUATION_WINDOW_IN_DAYS);
            connSlave = this.dataSourceSlave.getConnection();
            getNumReferralsSQL = "select count(*) from systemsms s where s.type=? and s.subtype=? and s.username=? and s.datecreated > date_sub(now(), interval ? day) and s.id > ?";
            numReferrals = 0;
            ps = connSlave.prepareStatement(getNumReferralsSQL);
            ps.setInt(1, SystemSMSData.TypeEnum.STANDARD.value());
            ps.setInt(2, SystemSMSData.SubTypeEnum.USER_REFERRAL.value());
            ps.setString(3, username);
            ps.setInt(4, windowDurationInDays);
            ps.setInt(5, minSystemSMSID);
            rs = ps.executeQuery();
            if (rs.next()) {
                numReferrals = rs.getInt(1);
            }
            rs.close();
            ps.close();
            if (numReferrals >= 1) break block29;
            var12_14 = -1.0;
            var15_16 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e) {
                connSlave = null;
            }
            return var12_14;
        }
        getNumReferralsAwardedSQL = "select count(*) from accountentry ae where ae.type=? and ae.username=? and ae.datecreated > date_sub(now(), interval ? day) and ae.id > ?";
        numReferralsAwarded = 0;
        ps = connSlave.prepareStatement(getNumReferralsAwardedSQL);
        ps.setInt(1, AccountEntryData.TypeEnum.REFERRAL_CREDIT.value());
        ps.setString(2, username);
        ps.setInt(3, windowDurationInDays);
        ps.setInt(4, minAccountEntryID);
        rs = ps.executeQuery();
        if (rs.next()) {
            numReferralsAwarded = rs.getInt(1);
        }
        result = new Double(1.0 * (double)numReferralsAwarded / (double)numReferrals);
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.USER_REFERRAL_SUCCESS_RATE, username, result.toString());
lbl65:
        // 2 sources

        if (UserBean.log.isDebugEnabled()) {
            UserBean.log.debug((Object)("referral success rate [" + username + "] [" + result + "]"));
        }
        var9_11 = result;
        var15_17 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e) {
            connSlave = null;
        }
        return var9_11;
        catch (Exception e) {
            try {
                UserBean.log.error((Object)("Unexpected exception: " + e.getMessage()), (Throwable)e);
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable var14_23) {
                var15_18 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e) {
                    connSlave = null;
                }
                throw var14_23;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean isUserInMigboAccessList(int userId, int accessListType, int guardCapabilityType) throws FusionEJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block26: {
            int value;
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            String sql = "SELECT COUNT(*) ctr_capability FROM guardcapability gc, guardsetcapability gsc, guardsetmember gsm, guardset gs WHERE gc.id = gsc.guardcapabilityid AND gsm.guardsetid = gsc.guardsetid AND gsm.guardsetid = gs.id AND gsm.membertype = ? AND gc.id = ? AND gsm.memberid = ?";
            ps = connSlave.prepareStatement(sql);
            ps.setInt(1, accessListType);
            ps.setInt(2, guardCapabilityType);
            ps.setInt(3, userId);
            rs = ps.executeQuery();
            if (!rs.next() || (value = rs.getInt(1)) <= 0) break block26;
            boolean bl = true;
            Object var11_11 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var11_12 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                log.error((Object)("SQLException occurred in isUserLevelAllowedMigboAccess: " + e));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean isUserLevelAllowedMigboAccess(int userId, int guardCapabilityId) throws FusionEJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block30: {
            int value;
            int MEMBERSHIP_TYPE = 3;
            String username = "";
            try {
                username = this.getUsernameByUserid(userId, null);
            }
            catch (EJBException e) {
                log.error((Object)("Unable to retrieve username: " + (Object)((Object)e)));
                throw new FusionEJBException(e.getMessage());
            }
            int userLevel = -1;
            try {
                userLevel = this.getReputationLevel((String)username).level;
            }
            catch (EJBException e) {
                log.error((Object)("Unable to retrieve user level: " + (Object)((Object)e)));
                throw new FusionEJBException(e.getMessage());
            }
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            String sql = "SELECT gsm.memberid FROM guardcapability gc, guardsetcapability gsc, guardsetmember gsm, guardset gs WHERE gc.id = gsc.guardcapabilityid AND gsm.guardsetid = gsc.guardsetid AND gsm.guardsetid = gs.id AND gsm.membertype = ? AND gc.id = ?";
            ps = connSlave.prepareStatement(sql);
            ps.setInt(1, MEMBERSHIP_TYPE);
            ps.setInt(2, guardCapabilityId);
            rs = ps.executeQuery();
            if (!rs.next() || userLevel < (value = rs.getInt(1))) break block30;
            boolean bl = true;
            Object var13_15 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var13_16 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e2) {
            connSlave = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                log.error((Object)("SQLException occurred in isUserLevelAllowedMigboAccess: " + e));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var13_17 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean addUserEmailAddress(int userId, String emailAddress, UserEmailAddressData.UserEmailAddressTypeEnum type) throws FusionEJBException {
        block32: {
            if (emailAddress == null) {
                throw new FusionEJBException("Please provie a valid email address.");
            }
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            EmailUtils.EmailValidatationResult evr = EmailUtils.externalEmailIsValid(emailAddress);
            if (SystemProperty.getBool(SystemPropertyEntities.Registration.STRIP_PERIODS_FROM_GMAIL_ADDRESS_ENABLED) && evr.result == EmailUtils.EmailValidatationEnum.PERIODS_EXCEED_IN_GMAIL) {
                emailAddress = EmailUtils.stripPeriodsFromGmailAddress(emailAddress);
            } else if (evr.result != EmailUtils.EmailValidatationEnum.VALID) {
                throw new EJBException(evr.reason);
            }
            String username = "";
            try {
                username = this.getUsernameByUserid(userId, null);
            }
            catch (EJBException e) {
                log.error((Object)("Invalid userId in setting email address: " + userId));
                throw new FusionEJBException(e.getMessage());
            }
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("SELECT userid FROM useremailaddress WHERE emailaddress = ? ");
            ps.setString(1, emailAddress);
            rs = ps.executeQuery();
            if (rs.next()) {
                log.error((Object)(username + " is trying to register an email address that's already in use: " + emailAddress));
                throw new FusionEJBException("The email address " + emailAddress + " is already in use.");
            }
            rs.close();
            ps = conn.prepareStatement("INSERT INTO useremailaddress(userid, emailaddress, type) VALUES (?,?,?)");
            ps.setInt(1, userId);
            ps.setString(2, emailAddress);
            ps.setInt(3, type.value);
            if (ps.executeUpdate() != 1) {
                log.error((Object)("Unable to add emailaddress " + emailAddress + " to " + username));
                throw new FusionEJBException("Unable to add emailaddress " + emailAddress);
            }
            EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.USER_DETAIL));
            try {
                Enums.Mig33UserActionMisLogEnum action = Enums.Mig33UserActionMisLogEnum.ADD_EMAIL_ADDRESS;
                MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                misEJB.logMig33UserAction(userId, action, String.format(action.getDescriptionPattern(), emailAddress));
            }
            catch (Exception e) {
                log.warn((Object)String.format("Unable to log adding of email address for user [%s] email address: [%s]", username, emailAddress), (Throwable)e);
            }
            Object var12_15 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block32;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block32;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var12_16 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return true;
    }

    /*
     * Loose catch block
     */
    public boolean removeUserEmailAddressByType(int userId, UserEmailAddressData.UserEmailAddressTypeEnum type) throws FusionEJBException {
        block25: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            if (type == UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY) {
                log.error((Object)("Attempt to remove primary meail address detected for [" + userId + "]"));
                throw new FusionEJBException("You are not allowed to remove your primary email address. Try modifying it instead.");
            }
            String username = "";
            try {
                username = this.getUsernameByUserid(userId, null);
            }
            catch (EJBException e) {
                log.error((Object)("Invalid id detected in removing email address: " + userId));
                throw new FusionEJBException(e.getMessage());
            }
            log.info((Object)("Removing all emailaddress of user [" + username + "] of type " + type.value + "]"));
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("DELETE FROM useremailaddress WHERE userid = ? AND type = ?");
            ps.setInt(1, userId);
            ps.setInt(2, type.value);
            ps.executeUpdate();
            EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.USER_DETAIL));
            Object var9_10 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block25;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block25;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return true;
    }

    /*
     * Loose catch block
     */
    public boolean removeUserEmailAddress(int userId, String emailAddress) throws FusionEJBException {
        block27: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            String username = "";
            try {
                username = this.getUsernameByUserid(userId, null);
            }
            catch (EJBException e) {
                log.error((Object)("Invalid userId in setting email address: " + userId));
                throw new FusionEJBException(e.getMessage());
            }
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("SELECT userid FROM useremailaddress WHERE emailaddress = ? AND type = 1");
            ps.setString(1, emailAddress);
            rs = ps.executeQuery();
            if (rs.next()) {
                log.error((Object)(username + " is trying to remove promary email address: " + emailAddress));
                throw new FusionEJBException("You are not allowed to remove your primary email address. Try modifying it instead.");
            }
            log.info((Object)("Removing emailaddress of user [" + username + "] " + emailAddress + "]"));
            ps = conn.prepareStatement("DELETE FROM useremailaddress WHERE userid = ? AND emailAddress = ?");
            ps.setInt(1, userId);
            ps.setString(2, emailAddress);
            ps.executeUpdate();
            EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.USER_DETAIL));
            try {
                Enums.Mig33UserActionMisLogEnum action = Enums.Mig33UserActionMisLogEnum.DELETE_EMAIL_ADDRESS;
                MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                misEJB.logMig33UserAction(userId, action, String.format(action.getDescriptionPattern(), emailAddress));
            }
            catch (Exception e) {
                log.warn((Object)String.format("Unable to log delete  email address [%s] for user [%s]", emailAddress, username), (Throwable)e);
            }
            Object var10_13 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block27;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block27;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var10_14 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return true;
    }

    /*
     * Loose catch block
     */
    public boolean updateUserEmailAddress(int userId, String oldEmailAddress, String newEmailAddress, UserEmailAddressData.UserEmailAddressTypeEnum type) throws FusionEJBException {
        block33: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            String username = "";
            try {
                username = this.getUsernameByUserid(userId, null);
            }
            catch (EJBException e) {
                log.error((Object)("Invalid userId in setting email address: " + userId));
                throw new FusionEJBException(e.getMessage());
            }
            if (StringUtil.isBlank(oldEmailAddress)) {
                throw new FusionEJBException("Please provide the email address to be modified.");
            }
            if (type == null) {
                throw new FusionEJBException("Unable to modify email address - unrecognized email.");
            }
            EmailUtils.EmailValidatationResult evr = EmailUtils.externalEmailIsValid(newEmailAddress);
            if (SystemProperty.getBool(SystemPropertyEntities.Registration.STRIP_PERIODS_FROM_GMAIL_ADDRESS_ENABLED) && evr.result == EmailUtils.EmailValidatationEnum.PERIODS_EXCEED_IN_GMAIL) {
                newEmailAddress = EmailUtils.stripPeriodsFromGmailAddress(newEmailAddress);
            } else if (evr.result != EmailUtils.EmailValidatationEnum.VALID) {
                throw new EJBException(evr.reason);
            }
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("SELECT userid FROM useremailaddress WHERE emailaddress = ? AND type = 1");
            ps.setString(1, newEmailAddress);
            rs = ps.executeQuery();
            if (rs.next()) {
                log.error((Object)(username + " is trying to register an email address that's already in use: " + newEmailAddress));
                throw new FusionEJBException("The email address " + newEmailAddress + " is already in use.");
            }
            ps = conn.prepareStatement("UPDATE useremailaddress SET emailaddress = ?,verified = 0,dateverified = NULL WHERE userid = ? AND type = ? AND emailaddress = ?");
            ps.setString(1, newEmailAddress);
            ps.setInt(2, userId);
            ps.setInt(3, type.value);
            ps.setString(4, oldEmailAddress);
            if (ps.executeUpdate() == 0) {
                throw new EJBException("Unable to modify user email address.");
            }
            EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.USER_DETAIL));
            try {
                Enums.Mig33UserActionMisLogEnum action = Enums.Mig33UserActionMisLogEnum.CHANGE_EMAIL_ADDRESS;
                MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                misEJB.logMig33UserAction(userId, action, String.format(action.getDescriptionPattern(), oldEmailAddress, newEmailAddress));
            }
            catch (Exception e) {
                log.warn((Object)String.format("Unable to log change in email address for user [%s] from [%s] to [%s]", username, oldEmailAddress, newEmailAddress), (Throwable)e);
            }
            Object var13_16 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block33;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block33;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var13_17 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public RegistrationTokenData verifyExternalEmailAddress(Integer requestorUserID, String token) throws FusionEJBException {
        RegistrationTokenData registrationTokenData;
        String requestLogStmt = "verifyExternalEmailAddress:UserID [" + requestorUserID + "] token [" + token + "]:";
        log.info((Object)(requestLogStmt + "attempting to verify token"));
        Jedis masterInstance = null;
        boolean useMemcached = SystemProperty.getBool(SystemPropertyEntities.Default.EMAIL_VERIFICATION_WITHOUT_USERNAME_ENABLED);
        try {
            try {
                int tokenOwnerUserID;
                String emailAddress = null;
                String keyRedis = null;
                if (useMemcached) {
                    String value = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.EXTERNAL_EMAIL_VERIFICATION_TOKEN, token);
                    if (value == null) {
                        log.error((Object)("External email verification failed for requestorUserID [" + requestorUserID + "]: invalid token [" + token + "] - value not found in memcached"));
                        throw new EJBExceptionWithErrorCause(ErrorCause.EmailVerificationErrorReasonType.TOKEN_DOES_NOT_EXIST, new Object[0]);
                    }
                    String[] values = value.split("\\|");
                    if (values.length != 2) {
                        log.error((Object)("External email verification failed for requestorUserID [" + requestorUserID + "]: invalid token [" + token + "] - invalid value found in memcached"));
                        throw new EJBExceptionWithErrorCause(ErrorCause.EmailVerificationErrorReasonType.TOKEN_REFERS_TO_AN_INVALID_VALUE, new Object[0]);
                    }
                    emailAddress = values[0];
                    try {
                        tokenOwnerUserID = Integer.parseInt(values[1]);
                    }
                    catch (NumberFormatException nfe) {
                        log.error((Object)("External email verification failed for requestorUserID [" + requestorUserID + "]: invalid token [" + token + "] - invalid userId in value found in memcached"));
                        throw new EJBExceptionWithErrorCause(ErrorCause.EmailVerificationErrorReasonType.TOKEN_REFERS_TO_UNPARSEABLE_USERID, new Object[0]);
                    }
                    if (SystemProperty.getBool(SystemPropertyEntities.Email.ENABLE_USER_ID_CHECK_WHEN_DEREFERENCING_TOKENS)) {
                        log.error((Object)("External email verification failed for requestorUserID [" + requestorUserID + "]: invalid token [" + token + "] - owner of token is [" + tokenOwnerUserID + "]"));
                        if (tokenOwnerUserID != requestorUserID) {
                            throw new EJBExceptionWithErrorCause(ErrorCause.EmailVerificationErrorReasonType.TOKEN_REFERS_TO_MISMATCHED_USERID, new Object[0]);
                        }
                    }
                } else {
                    tokenOwnerUserID = requestorUserID;
                    keyRedis = UserBean.getRedisKeyForEmailVerificationToken(tokenOwnerUserID, token);
                    masterInstance = Redis.getMasterInstanceForUserID(tokenOwnerUserID);
                    emailAddress = masterInstance.get(keyRedis);
                    if (emailAddress == null) {
                        log.error((Object)("External email verification failed for requestorUserID [" + requestorUserID + "] tokenOwnerUserID [" + tokenOwnerUserID + "] invalid token [" + token + "] - value not found in redis"));
                        throw new EJBExceptionWithErrorCause(ErrorCause.EmailVerificationErrorReasonType.TOKEN_DOES_NOT_EXIST, new Object[0]);
                    }
                }
                if (null == emailAddress) {
                    log.error((Object)("External email verification failed for userid [" + requestorUserID + "]: invalid token [" + token + "]. tokenOwnerUserID [" + tokenOwnerUserID + "]"));
                    throw new EJBException("Invalid token provided.");
                }
                this.saveVerifiedUserPrimaryEmailAddressOnDatabase(tokenOwnerUserID, emailAddress);
                log.info((Object)(requestLogStmt + "saved to database"));
                try {
                    if (useMemcached) {
                        UserBean.purgeEmailVerificationTokenFromMemcache(tokenOwnerUserID, token);
                    } else {
                        UserBean.purgeEmailVerificationTokenFromRedis(masterInstance, tokenOwnerUserID, token);
                    }
                }
                catch (Exception ex) {
                    log.error((Object)("Unable to delete verification token userid [" + tokenOwnerUserID + "]: invalid token [" + token + "]." + ex), (Throwable)ex);
                }
                String username = this.getUsernameByUserid(tokenOwnerUserID, null);
                EventQueue.enqueueSingleEvent(new UserDataUpdatedEvent(username, UserDataUpdatedEvent.TypeEnum.USER_DETAIL));
                try {
                    Enums.Mig33UserActionMisLogEnum action = Enums.Mig33UserActionMisLogEnum.VERIFY_EMAIL_ADDRESS;
                    MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                    misEJB.logMig33UserAction(tokenOwnerUserID, action, String.format(action.getDescriptionPattern(), emailAddress));
                }
                catch (Exception e) {
                    log.warn((Object)String.format("Unable to log verify  email address [%s] for user [%s]", emailAddress, username), (Throwable)e);
                }
                if (SystemProperty.getBool(SystemPropertyEntities.Email.ENABLE_EXTERNAL_EMAIL_VERIFIED_EVENT_TRIGGER)) {
                    UserData userData = this.loadUser(username, false, false);
                    RegistrationContextData regContextData = this.getRegistrationContextData(tokenOwnerUserID);
                    ExternalEmailVerifiedTrigger externalEmailVerifiedTrigger = new ExternalEmailVerifiedTrigger(userData, regContextData, emailAddress);
                    try {
                        RewardCentre.getInstance().sendTrigger(externalEmailVerifiedTrigger);
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to notify reward system to send ExternalEmailVerifiedTrigger.Trigger:[" + externalEmailVerifiedTrigger + "].Exception:" + e), (Throwable)e);
                    }
                }
                log.info((Object)(requestLogStmt + "completed"));
                registrationTokenData = new RegistrationTokenData("ok", username, emailAddress);
                Object var15_20 = null;
            }
            catch (EJBException e) {
                throw e;
            }
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            catch (Exception e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var15_21 = null;
            Redis.disconnect(masterInstance, log);
            throw throwable;
        }
        Redis.disconnect(masterInstance, log);
        return registrationTokenData;
    }

    public static String getRedisKeyForEmailVerificationToken(int requestorUserID, String token) {
        return Redis.KeySpace.EXTERNAL_EMAIL_VERIFICATION_TOKEN.toString() + requestorUserID + ":" + token;
    }

    public static void purgeEmailVerificationTokenFromMemcache(int requestorUserID, String token) {
        MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.EXTERNAL_EMAIL_VERIFICATION_TOKEN, token);
    }

    public static void purgeEmailVerificationTokenFromRedis(Jedis masterInstance, int requestorUserID, String token) {
        masterInstance.del(UserBean.getRedisKeyForEmailVerificationToken(requestorUserID, token));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveVerifiedUserPrimaryEmailAddressOnDatabase(int userid, String emailAddress) {
        try {
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                block12: {
                    PreparedStatement ps = conn.prepareStatement("UPDATE useremailaddress SET verified = 1, dateverified = NOW() WHERE userid = ? AND emailaddress = ? AND verified = ?");
                    try {
                        ps.setInt(1, userid);
                        ps.setString(2, emailAddress);
                        ps.setInt(3, 0);
                        if (ps.executeUpdate() < 1) {
                            throw new EJBException("Unable to verify email address.");
                        }
                        Object var6_7 = null;
                    }
                    catch (Throwable throwable) {
                        Object var6_8 = null;
                        ps.close();
                        throw throwable;
                    }
                    ps.close();
                    if (SystemProperty.getBool(SystemPropertyEntities.Email.ENABLE_UPDATE_EMAIL_ADDRESS_FIELD_ON_USER_TABLE)) {
                        try {
                            ps = conn.prepareStatement("UPDATE user u, userid ui SET u.emailaddress = ? WHERE u.username = ui.username AND ui.id = ?");
                            try {
                                ps.setString(1, emailAddress);
                                ps.setInt(2, userid);
                                ps.executeUpdate();
                                Object var8_10 = null;
                            }
                            catch (Throwable throwable) {
                                Object var8_11 = null;
                                ps.close();
                                throw throwable;
                            }
                            ps.close();
                            {
                            }
                        }
                        catch (Exception ex) {
                            log.error((Object)("Partial failure updating the database.Unable to update user.emailaddress field for userid [" + userid + "] and emailaddress [" + emailAddress + "]." + ex), (Throwable)ex);
                            if (!SystemProperty.getBool(SystemPropertyEntities.Email.THROW_ON_FAILED_UPDATE_EMAIL_ADDRESS_FIELD_ON_USER_TABLE)) break block12;
                            throw new EJBException("Partial failure updating the database", ex);
                        }
                    }
                }
                Object var10_13 = null;
            }
            catch (Throwable throwable) {
                Object var10_14 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            {
            }
        }
        catch (SQLException sqlEx) {
            throw new EJBException("Database update error." + sqlEx, (Exception)sqlEx);
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    private boolean verifyEmailRegistration(String username, String emailAddress) {
        if (!this.isEmailRegistrationV2Enabled()) {
            throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
        }
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        conn = this.userRegistrationMaster.getConnection();
        String statement = "UPDATE userregistration SET verified=1, verificationtime=NOW() WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMin() + ",now()) AND username=? AND emailaddress=?";
        ps = conn.prepareStatement(statement);
        ps.setString(1, username);
        ps.setString(2, emailAddress);
        if (ps.executeUpdate() < 1) {
            log.warn((Object)"Unable to update verification time of email");
        }
        boolean bl = true;
        Object var9_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        {
            return bl;
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            catch (Exception e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var9_11 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
    }

    private int getEmailRegistrationTimelimitMin() {
        return SystemProperty.getInt(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_TIMELIMIT_IN_MINUTES);
    }

    private int getEmailRegistrationTimelimitMax() {
        return SystemProperty.getInt(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_TIMELIMIT_IN_MINUTES) + SystemProperty.getInt(SystemPropertyEntities.Registration.EMAIL_REGISTRATION_GRACE_PERIOD_IN_MINUTES);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean isEmailRegistrationNotVerifiedForUsername(String username) {
        block31: {
            block30: {
                block25: {
                    block29: {
                        block28: {
                            block24: {
                                ps = null;
                                rs = null;
                                conn = null;
                                try {
                                    try {
                                        conn = this.userRegistrationSlave.getConnection();
                                        ps = conn.prepareStatement("SELECT * FROM userregistration WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMin() + ",now()) AND username=? AND verified=0");
                                        ps.setString(1, username);
                                        rs = ps.executeQuery();
                                        if (rs.next()) {
                                            var5_5 = true;
                                            var7_9 = null;
                                            break block24;
                                        }
                                        var5_6 = false;
                                        break block25;
                                    }
                                    catch (SQLException e) {
                                        throw new EJBException(e.getMessage());
                                    }
                                    catch (Exception e) {
                                        throw new EJBException(e.getMessage());
                                    }
                                }
                                catch (Throwable var6_15) {
                                    var7_11 = null;
                                    try {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                    try {
                                        if (ps != null) {
                                            ps.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        ps = null;
                                    }
                                    try {
                                        if (conn == null) throw var6_15;
                                        conn.close();
                                        throw var6_15;
                                    }
                                    catch (SQLException e) {
                                        conn = null;
                                        throw var6_15;
                                    }
                                }
                            }
                            ** try [egrp 2[TRYBLOCK] [7 : 138->151)] { 
lbl48:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block28;
lbl51:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [8 : 155->168)] { 
lbl55:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block29;
lbl58:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var5_5;
                    }
                    if (conn == null) return var5_5;
                    conn.close();
                    return var5_5;
                }
                var7_10 = null;
                ** try [egrp 2[TRYBLOCK] [7 : 138->151)] { 
lbl70:
                // 1 sources

                if (rs != null) {
                    rs.close();
                }
                break block30;
lbl73:
                // 1 sources

                catch (SQLException e) {
                    rs = null;
                }
            }
            ** try [egrp 3[TRYBLOCK] [8 : 155->168)] { 
lbl77:
            // 1 sources

            if (ps != null) {
                ps.close();
            }
            break block31;
lbl80:
            // 1 sources

            catch (SQLException e) {
                ps = null;
            }
        }
        try {}
        catch (SQLException e) {
            return var5_6;
        }
        if (conn == null) return var5_6;
        conn.close();
        return var5_6;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean isEmailRegistrationNotVerifiedForUsername(String username, String emailAddress) {
        block32: {
            block31: {
                block26: {
                    block30: {
                        block29: {
                            block25: {
                                if (!this.isEmailRegistrationV2Enabled()) {
                                    throw new EJBException(SystemProperty.get(SystemPropertyEntities.Registration.REGISTRATION_DISABLED_ERROR_MESSAGE));
                                }
                                ps = null;
                                rs = null;
                                conn = null;
                                try {
                                    try {
                                        conn = this.userRegistrationSlave.getConnection();
                                        ps = conn.prepareStatement("SELECT * FROM userregistration WHERE updatedtime > timestampadd(MINUTE," + -1 * this.getEmailRegistrationTimelimitMin() + ",now()) AND username=? AND emailAddress=? AND verified=0");
                                        ps.setString(1, username);
                                        ps.setString(2, emailAddress);
                                        rs = ps.executeQuery();
                                        if (rs.next()) {
                                            var6_6 = true;
                                            var8_10 = null;
                                            break block25;
                                        }
                                        var6_7 = false;
                                        break block26;
                                    }
                                    catch (SQLException e) {
                                        throw new EJBException(e.getMessage());
                                    }
                                    catch (Exception e) {
                                        throw new EJBException(e.getMessage());
                                    }
                                }
                                catch (Throwable var7_16) {
                                    var8_12 = null;
                                    try {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                    try {
                                        if (ps != null) {
                                            ps.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        ps = null;
                                    }
                                    try {
                                        if (conn == null) throw var7_16;
                                        conn.close();
                                        throw var7_16;
                                    }
                                    catch (SQLException e) {
                                        conn = null;
                                        throw var7_16;
                                    }
                                }
                            }
                            ** try [egrp 2[TRYBLOCK] [7 : 170->185)] { 
lbl51:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block29;
lbl54:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [8 : 190->203)] { 
lbl58:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block30;
lbl61:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var6_6;
                    }
                    if (conn == null) return var6_6;
                    conn.close();
                    return var6_6;
                }
                var8_11 = null;
                ** try [egrp 2[TRYBLOCK] [7 : 170->185)] { 
lbl73:
                // 1 sources

                if (rs != null) {
                    rs.close();
                }
                break block31;
lbl76:
                // 1 sources

                catch (SQLException e) {
                    rs = null;
                }
            }
            ** try [egrp 3[TRYBLOCK] [8 : 190->203)] { 
lbl80:
            // 1 sources

            if (ps != null) {
                ps.close();
            }
            break block32;
lbl83:
            // 1 sources

            catch (SQLException e) {
                ps = null;
            }
        }
        try {}
        catch (SQLException e) {
            return var6_7;
        }
        if (conn == null) return var6_7;
        conn.close();
        return var6_7;
    }

    /*
     * Loose catch block
     */
    public boolean isExternalEmailVerified(int userId, String emailAddress) throws FusionEJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT verified FROM useremailaddress WHERE userid = ? AND emailaddress = ?");
        ps.setInt(1, userId);
        ps.setString(2, emailAddress);
        rs = ps.executeQuery();
        boolean emailVerified = false;
        if (!rs.next()) {
            throw new FusionEJBException("Unknown email address.");
        }
        emailVerified = rs.getBoolean("verified");
        boolean bl = emailVerified;
        Object var9_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public UserEmailAddressData getUserEmailAddressByType(int userId, UserEmailAddressData.UserEmailAddressTypeEnum type) throws FusionEJBException {
        if (userId <= 0) {
            throw new FusionEJBException("Unknown user.");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM useremailaddress WHERE userid = ? AND type = ? ";
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, type.value);
        rs = ps.executeQuery();
        UserEmailAddressData emailAddress = null;
        if (rs.next()) {
            emailAddress = new UserEmailAddressData(rs);
        }
        UserEmailAddressData userEmailAddressData = emailAddress;
        Object var10_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return userEmailAddressData;
        catch (SQLException e) {
            try {
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public String getUsernameByEmailAddress(String emailAddress, UserEmailAddressData.UserEmailAddressTypeEnum type) throws FusionEJBException {
        EmailUtils.EmailValidatationResult evr = EmailUtils.externalEmailIsValid(emailAddress);
        if (evr.result != EmailUtils.EmailValidatationEnum.VALID) {
            throw new FusionEJBException(evr.reason);
        }
        if (type == null) {
            log.warn((Object)("User email address type is null. email address [" + emailAddress + "]"));
            throw new FusionEJBException("Please provide the email address type.");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        String sql = "SELECT ui.username FROM userid ui, \t   useremailaddress uea WHERE uea.userid = ui.id AND uea.type = ? AND uea.verified = 1 AND uea.emailaddress = ? ";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, type.value);
        ps.setString(2, emailAddress);
        rs = ps.executeQuery();
        String username = null;
        if (rs.next()) {
            username = rs.getString("username");
        }
        String string = username;
        Object var11_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public void forgotUsernameViaEmailAddress(String emailAddress) throws FusionEJBException {
        EmailUtils.EmailValidatationResult evr = EmailUtils.externalEmailIsValid(emailAddress);
        if (evr.result != EmailUtils.EmailValidatationEnum.VALID) {
            throw new FusionEJBException(evr.reason);
        }
        try {
            String username = this.getUsernameByEmailAddress(emailAddress, UserEmailAddressData.UserEmailAddressTypeEnum.PRIMARY);
            if (StringUtil.isBlank(username)) {
                throw new FusionEJBException("Sorry, we cannot find a migme account with email address " + emailAddress + ". Please provide the email address you have registered with us.");
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Email.ENABLED_FORGOT_USERNAME_EMAIL_WITH_TEMPLATE)) {
                UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("base_url", SystemProperty.get(SystemPropertyEntities.Default.MIG33_WEB_BASE_URL));
                params.put("username", username);
                unsProxy.sendTemplatizedEmailFromNoReply(emailAddress, SystemProperty.getInt(SystemPropertyEntities.Email.FORGOT_USERNAME_EMAIL_TEMPLATE_ID), params);
            } else {
                String subject = SystemProperty.get(SystemPropertyEntities.ForgotUsername.EMAIL_SUBJECT).replaceAll("%1", username);
                String content = SystemProperty.get(SystemPropertyEntities.ForgotUsername.EMAIL_CONTENT).replaceAll("%1", username).replaceAll("%2", username);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Sending token via email: [" + emailAddress + "] subject [" + subject + "] content [" + content + "]"));
                }
                MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                messageEJB.sendEmailFromNoReply(emailAddress, subject, content);
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to send forgot username request emailaddress [" + emailAddress + "]"), (Throwable)e);
            throw new FusionEJBException("Sorry, we are unable to process your request at the moment. please try again later.");
        }
    }

    /*
     * Loose catch block
     */
    public String[] getUsersInUserCategory(int categoryId) throws EJBException {
        ArrayList<String> usernames;
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            usernames = new ArrayList<String>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT ui.username FROM usertousercategory uc, userid ui WHERE uc.userid = ui.id AND uc.usercategoryid = ?");
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return usernames.toArray(new String[usernames.size()]);
    }

    /*
     * Loose catch block
     */
    public Map<Integer, String[]> getUserCategoryNames(int userId) throws FusionEJBException {
        HashMap<Integer, ArrayList> categories;
        block24: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            categories = new HashMap<Integer, ArrayList>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT uc.id, uc.name, uc.type FROM usertousercategory u2uc, userid ui, usercategory uc WHERE u2uc.userid = ui.id AND uc.id = u2uc.usercategoryid AND u2uc.userid = ?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            ArrayList currentCategoryList = new ArrayList();
            while (rs.next()) {
                if (!categories.containsKey(rs.getInt("type"))) {
                    categories.put(rs.getInt("type"), new ArrayList());
                }
                currentCategoryList = (ArrayList)categories.get(rs.getInt("type"));
                currentCategoryList.add(rs.getString("name"));
                categories.put(rs.getInt("type"), currentCategoryList);
            }
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new FusionEJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        HashMap<Integer, String[]> returnCategories = new HashMap<Integer, String[]>();
        for (Map.Entry categoryType : categories.entrySet()) {
            returnCategories.put((Integer)categoryType.getKey(), ((ArrayList)categoryType.getValue()).toArray(new String[((ArrayList)categoryType.getValue()).size()]));
        }
        return returnCategories;
    }

    /*
     * Loose catch block
     */
    public boolean blacklistUser(int userId) {
        block60: {
            ResultSet rs;
            PreparedStatement ps;
            Connection connMaster;
            Connection connSlave;
            block52: {
                block47: {
                    connSlave = null;
                    connMaster = null;
                    ps = null;
                    rs = null;
                    connSlave = this.dataSourceSlave.getConnection();
                    ps = connSlave.prepareStatement("SELECT * FROM guardsetmember WHERE (guardsetid=(SELECT guardsetid FROM guardsetcapability WHERE guardcapabilityid=?) AND memberid=? AND membertype=?)");
                    ps.setInt(1, GuardCapabilityEnum.MIGBO_ACCESS.value());
                    ps.setInt(2, userId);
                    ps.setInt(3, MigboAccessMemberTypeEnum.BLACKLIST.value());
                    rs = ps.executeQuery();
                    if (!rs.next()) break block47;
                    boolean bl = true;
                    Object var8_12 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                    }
                    catch (SQLException e2) {
                        connSlave = null;
                    }
                    try {
                        if (connMaster != null) {
                            connMaster.close();
                        }
                    }
                    catch (SQLException e2) {
                        connMaster = null;
                    }
                    return bl;
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("INSERT INTO guardsetmember (guardsetid, memberid, membertype) VALUES((SELECT guardsetid FROM guardsetcapability WHERE guardcapabilityid=?), ?, ?)");
                ps.setInt(1, GuardCapabilityEnum.MIGBO_ACCESS.value());
                ps.setInt(2, userId);
                ps.setInt(3, MigboAccessMemberTypeEnum.BLACKLIST.value());
                if (ps.executeUpdate() == 1) break block52;
                boolean e = false;
                Object var8_13 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                return e;
            }
            try {
                Object var8_14 = null;
            }
            catch (Throwable throwable) {
                Object var8_15 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
                break block60;
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            break block60;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
        }
        UserData userData = new UserData();
        userData = this.loadUserFromID(userId);
        this.disconnectUser(userData.username, "blacklist user");
        return true;
    }

    /*
     * Loose catch block
     */
    public boolean removeUserFromBlacklist(int userId) {
        PreparedStatement ps;
        Connection connMaster;
        block19: {
            connMaster = null;
            ps = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("DELETE FROM guardsetmember WHERE (guardsetid=(SELECT guardsetid FROM guardsetcapability WHERE guardcapabilityid=?) AND memberid=? AND membertype=?)");
            ps.setInt(1, GuardCapabilityEnum.MIGBO_ACCESS.value());
            ps.setInt(2, userId);
            ps.setInt(3, MigboAccessMemberTypeEnum.BLACKLIST.value());
            if (ps.executeUpdate() != 1) break block19;
            boolean bl = true;
            Object var6_7 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var6_8 = null;
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var6_9 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public boolean banUser(int userId) {
        block22: {
            PreparedStatement ps;
            Connection connMaster;
            block18: {
                connMaster = null;
                ps = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("update user join userid on user.username = userid.username set user.status = 0 where userid.id = ?");
                ps.setInt(1, userId);
                if (ps.executeUpdate() == 1) break block18;
                boolean bl = false;
                Object var6_7 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                return bl;
            }
            try {
                Object var6_8 = null;
            }
            catch (Throwable throwable) {
                Object var6_9 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                throw throwable;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
                break block22;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
        }
        UserData userData = new UserData();
        userData = this.loadUserFromID(userId);
        this.disconnectUser(userData.username, "ban user");
        return true;
    }

    public boolean suspendUser(int userId, int durationHours) {
        UserData userData = new UserData();
        userData = this.loadUserFromID(userId);
        int durationMilliseconds = durationHours * 60 * 60 * 1000;
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.LOGIN_BAN, userData.username, "1", durationMilliseconds);
        this.disconnectUser(userData.username, "suspend user");
        if (SystemProperty.getBool(SystemPropertyEntities.Default.LOG_USER_SUSPENSION)) {
            try {
                Enums.Mig33UserActionMisLogEnum action = Enums.Mig33UserActionMisLogEnum.SUSPEND_USER;
                MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                misEJB.logMig33UserAction(userId, action, String.format(action.getDescriptionPattern(), userData.username + "," + durationHours));
            }
            catch (Exception e) {
                log.warn((Object)String.format("Unable to log suspension of user [%s] ", userData.username), (Throwable)e);
            }
        }
        return true;
    }

    public boolean disconnectUser(String username, String comment) {
        UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
        if (userPrx != null) {
            try {
                userPrx.disconnect(comment);
            }
            catch (Exception e) {
                log.error((Object)("Failed to disconnect user: " + e.getMessage()));
                return false;
            }
            return true;
        }
        return false;
    }

    /*
     * Loose catch block
     */
    public int getChatroomsOwnedCount(int userId) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        Integer count = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.CHATROOMS_OWNED_COUNT, "" + userId);
        if (count == null) {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) as c from userid u, chatroom ch WHERE ch.creator=u.username and u.id=?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("c");
            }
            if (count == null) {
                count = new Integer(0);
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CHATROOMS_OWNED_COUNT, "" + userId, count);
        }
        int n = count;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return n;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public int getGiftsReceivedCount(int userId) {
        Integer count;
        boolean useRedis;
        block26: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block23: {
                useRedis = SystemProperty.getBool("UseRedisDataStore", true);
                Integer n = count = useRedis ? GiftsReceivedCounter.getCacheCount(userId) : MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.GIFTS_RECEIVED_COUNT, "" + userId);
                if (count != null) {
                    return count;
                }
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("SELECT COUNT(*) as c FROM userid u, virtualgiftreceived vgr WHERE vgr.username=u.username and u.id=?");
                ps.setInt(1, userId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    count = rs.getInt("c");
                }
                if (count != null) break block23;
                count = new Integer(0);
            }
            Object var9_7 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block26;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block26;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var9_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        if (useRedis) {
            GiftsReceivedCounter.setCacheCount(userId, count);
        } else {
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.GIFTS_RECEIVED_COUNT, "" + userId, count);
        }
        return count;
    }

    /*
     * Loose catch block
     */
    public int getGroupsJoinedCount(int userId) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        Integer count = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.GROUPS_JOINED_COUNT, "" + userId);
        if (count == null) {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT count(*) as c FROM userid u, groupmember gm WHERE gm.username=u.username and u.id=?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("c");
            }
            if (count == null) {
                count = new Integer(0);
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.GROUPS_JOINED_COUNT, "" + userId, count);
        }
        int n = count;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return n;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public int getPhotosUploadedCount(int userId) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        Integer count = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.PHOTOS_UPLOADED_COUNT, "" + userId);
        if (count == null) {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT count(*) as c from userid u, scrapbook sb where sb.username=u.username and u.id=?");
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("c");
            }
            if (count == null) {
                count = new Integer(0);
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.PHOTOS_UPLOADED_COUNT, "" + userId, count);
        }
        int n = count;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return n;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public RegistrationContextData getRegistrationContextData(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT * from registrationcontext where userid = ?");
        ps.setInt(1, userId);
        rs = ps.executeQuery();
        RegistrationContextData registrationContextData = new RegistrationContextData(rs);
        Object var7_7 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return registrationContextData;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public Map<String, Object> getTaggedUsers(int merchantUserId, int page, int numRecords) throws FusionEJBException {
        HashMap<String, Object> tags;
        block27: {
            ResultSet rs;
            Statement ps;
            Connection conn;
            block24: {
                if (!SystemProperty.getBool(SystemPropertyEntities.MerchantTagSettings.GET_MERCHANT_TAG_DETAILS_ENABLED)) {
                    return null;
                }
                conn = null;
                ps = null;
                rs = null;
                tags = new HashMap<String, Object>();
                tags.put("totalTags", 0);
                tags.put("tags", null);
                conn = this.dataSourceSlave.getConnection();
                String qry = "";
                Integer totalTags = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.MERCHANT_TAG_COUNT, "" + merchantUserId);
                if (totalTags == null) {
                    qry = "SELECT COUNT(DISTINCT userid) totalTags FROM merchanttag WHERE merchantuserid = ?";
                    ps = conn.prepareStatement(qry);
                    ps.setInt(1, merchantUserId);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        totalTags = rs.getInt("totalTags");
                        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MERCHANT_TAG_COUNT, "" + merchantUserId, totalTags);
                    }
                }
                tags.put("totalTags", totalTags);
                tags.put("tags", new ArrayList());
                if (totalTags <= 0) break block24;
                String taggerUsername = this.getUsernameByUserid(merchantUserId, null);
                int offset = page * numRecords - numRecords;
                qry = "SELECT mt.id id FROM merchanttag mt     , ( SELECT MAX(id) id          FROM merchanttag          WHERE merchantuserid = ?          GROUP BY userid) mtOrd WHERE mt.id = mtOrd.id ORDER BY status DESC, mt.id DESC LIMIT ?, ?";
                ps = conn.prepareStatement(qry);
                ps.setInt(1, merchantUserId);
                ps.setInt(2, offset);
                ps.setInt(3, numRecords);
                rs = ps.executeQuery();
                ArrayList<String> ids = new ArrayList<String>();
                while (rs.next()) {
                    ids.add(rs.getString("id"));
                }
                Map<String, FullMerchantTagDetailsData> tmpTags = this.getMerchantTagDetailsData(ids, taggerUsername);
                tags.put("tags", tmpTags);
            }
            Object var15_16 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block27;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block27;
            {
                catch (SQLException e) {
                    throw new FusionEJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new FusionEJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var15_17 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return tags;
    }

    /*
     * Loose catch block
     */
    public Map<String, Object> getExpiringTaggedUsers(int merchantUserId, int daysToExpire) throws FusionEJBException {
        HashMap<String, Object> tags;
        block24: {
            if (!SystemProperty.getBool(SystemPropertyEntities.MerchantTagSettings.GET_MERCHANT_TAG_DETAILS_ENABLED)) {
                return null;
            }
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            tags = new HashMap<String, Object>();
            tags.put("totalTags", 0);
            tags.put("tags", null);
            Calendar nonTopMerchantTagOffset = Calendar.getInstance();
            nonTopMerchantTagOffset.add(5, -(MerchantTagData.TypeEnum.NON_TOP_MERCHANT_TAG.validity() / 1440 - daysToExpire));
            Calendar topMerchantTagOffset = Calendar.getInstance();
            topMerchantTagOffset.add(5, -(MerchantTagData.TypeEnum.TOP_MERCHANT_TAG.validity() / 1440 - daysToExpire));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            conn = this.dataSourceSlave.getConnection();
            String qry = "SELECT mt.id id        , CASE WHEN u.type = ? THEN DATE_ADD(mt.lastsalesdate, INTERVAL ? MINUTE)          ELSE DATE_ADD(mt.lastsalesdate, INTERVAL ? MINUTE) END expirydate FROM merchanttag mt, userid uid, user u WHERE mt.userid = uid.id AND uid.username = u.username AND mt.merchantuserid = ? AND mt.status = ? AND ((u.type = ? AND mt.lastsalesdate < ?)      OR (u.type < ? AND mt.lastsalesdate < ?)) ORDER BY expirydate";
            ps = conn.prepareStatement(qry);
            ps.setInt(1, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
            ps.setInt(2, MerchantTagData.TypeEnum.TOP_MERCHANT_TAG.validity());
            ps.setInt(3, MerchantTagData.TypeEnum.NON_TOP_MERCHANT_TAG.validity());
            ps.setInt(4, merchantUserId);
            ps.setInt(5, MerchantTagData.StatusEnum.ACTIVE.value());
            ps.setInt(6, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
            ps.setString(7, df.format(topMerchantTagOffset.getTime()));
            ps.setInt(8, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
            ps.setString(9, df.format(nonTopMerchantTagOffset.getTime()));
            rs = ps.executeQuery();
            ArrayList<String> ids = new ArrayList<String>();
            Integer rowCount = 0;
            while (rs.next()) {
                ids.add(rs.getString("id"));
                Integer n = rowCount;
                Integer n2 = rowCount = Integer.valueOf(rowCount + 1);
            }
            tags.put("totalTags", rowCount);
            String taggerUsername = this.getUsernameByUserid(merchantUserId, null);
            Map<String, FullMerchantTagDetailsData> tmpTags = this.getMerchantTagDetailsData(ids, taggerUsername);
            tags.put("tags", tmpTags);
            Object var16_17 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block24;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new FusionEJBException(e.getMessage());
                }
                catch (Exception e) {
                    throw new FusionEJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var16_18 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return tags;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Map<String, FullMerchantTagDetailsData> getMerchantTagDetailsData(List<String> tagsIds, String taggerUsername) throws FusionEJBException {
        block36: {
            block35: {
                block30: {
                    block34: {
                        block33: {
                            block29: {
                                conn = null;
                                ps = null;
                                rs = null;
                                try {
                                    try {
                                        tmpTags = new HashMap<String, FullMerchantTagDetailsData>();
                                        if (tagsIds.size() == 0) {
                                            var7_9 = tmpTags;
                                            var16_11 = null;
                                            break block29;
                                        }
                                        maxTaggedUsersToReturn = SystemProperty.getInt("MaxTaggedUsersToReturn", 50);
                                        ids /* !! */  = new LinkedList<String>(tagsIds);
                                        if (tagsIds.size() > maxTaggedUsersToReturn) {
                                            ids /* !! */  = ids /* !! */ .subList(0, maxTaggedUsersToReturn);
                                        }
                                        query = "SELECT mt.id        ,ui.username        ,ui.id userID        ,mt.dateCreated        ,mt.lastSalesDate        ,CASE WHEN u.type = ? THEN ADDDATE(mt.lastsalesdate, INTERVAL ? MINUTE)         WHEN u.type < ? THEN ADDDATE(mt.lastsalesdate, INTERVAL ? MINUTE) END expiry        ,CASE WHEN u.type = ? AND ADDDATE(mt.lastsalesdate, INTERVAL ? MINUTE) > NOW() AND mt.status = 1 THEN 1         WHEN u.type < ? AND ADDDATE(mt.lastsalesdate, INTERVAL ? MINUTE) > NOW() AND mt.status = 1 THEN 1         ELSE 0 END status        ,u.type usertype        ,u.displaypicture        ,mt.merchantUserID        ,a.amount        ,a.currency        ,mu.username merchantusername \t   ,c.name country \t   ,up.gender \t   ,(COUNT(ct.username) > 0) isContact \t   ,up.status profileStatus \t   ,up.aboutMe FROM merchanttag mt LEFT JOIN accountentry a ON mt.accountentryid = a.id \t ,userid ui \t ,user u\t LEFT JOIN contact ct ON ct.username = u.username AND ct.fusionusername = ? \t LEFT JOIN userprofile up ON u.username = up.username \t ,userid mu \t ,country c WHERE mt.userid = ui.id AND mt.merchantuserid = mu.id AND ui.username = u.username AND u.countryid = c.id AND mt.id IN (" + StringUtil.join(ids /* !! */ , ",") + ") " + "GROUP BY u.username " + "ORDER BY status";
                                        conn = this.dataSourceSlave.getConnection();
                                        ps = conn.prepareStatement(query);
                                        ps.setInt(1, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
                                        ps.setInt(2, MerchantTagData.TypeEnum.TOP_MERCHANT_TAG.validity());
                                        ps.setInt(3, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
                                        ps.setInt(4, MerchantTagData.TypeEnum.NON_TOP_MERCHANT_TAG.validity());
                                        ps.setInt(5, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
                                        ps.setInt(6, MerchantTagData.TypeEnum.TOP_MERCHANT_TAG.validity());
                                        ps.setInt(7, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
                                        ps.setInt(8, MerchantTagData.TypeEnum.NON_TOP_MERCHANT_TAG.validity());
                                        ps.setString(9, taggerUsername);
                                        rs = ps.executeQuery();
                                        getUserLabelsEnabled = SystemProperty.getBool(SystemPropertyEntities.UserProfileSettings.GET_USER_LABELS_ENABLED);
                                        while (rs.next()) {
                                            username = rs.getString("username");
                                            reputationLevelData = null;
                                            userProfileLabelsData = null;
                                            try {
                                                reputationLevelData = this.getReputationLevel(username);
                                            }
                                            catch (EJBException e) {
                                                UserBean.log.error((Object)("Failed to retrieve reputation level data: " + (Object)e));
                                            }
                                            if (getUserLabelsEnabled) {
                                                userProfileLabelsData = this.getUserLabels(username);
                                            }
                                            tagDetails = new FullMerchantTagDetailsData(rs, reputationLevelData, userProfileLabelsData);
                                            tmpTags.put(username, tagDetails);
                                        }
                                        var11_20 = tmpTags;
                                        break block30;
                                    }
                                    catch (SQLException e) {
                                        throw new FusionEJBException(e.getMessage());
                                    }
                                    catch (Exception e) {
                                        throw new FusionEJBException(e.getMessage());
                                    }
                                }
                                catch (Throwable var15_25) {
                                    var16_13 = null;
                                    try {
                                        if (rs != null) {
                                            rs.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                    try {
                                        if (ps != null) {
                                            ps.close();
                                        }
                                    }
                                    catch (SQLException e) {
                                        ps = null;
                                    }
                                    try {
                                        if (conn == null) throw var15_25;
                                        conn.close();
                                        throw var15_25;
                                    }
                                    catch (SQLException e) {
                                        conn = null;
                                        throw var15_25;
                                    }
                                }
                            }
                            ** try [egrp 3[TRYBLOCK] [8 : 448->463)] { 
lbl78:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block33;
lbl81:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 4[TRYBLOCK] [9 : 468->483)] { 
lbl85:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block34;
lbl88:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var7_9;
                    }
                    if (conn == null) return var7_9;
                    conn.close();
                    return var7_9;
                }
                var16_12 = null;
                ** try [egrp 3[TRYBLOCK] [8 : 448->463)] { 
lbl100:
                // 1 sources

                if (rs != null) {
                    rs.close();
                }
                break block35;
lbl103:
                // 1 sources

                catch (SQLException e) {
                    rs = null;
                }
            }
            ** try [egrp 4[TRYBLOCK] [9 : 468->483)] { 
lbl107:
            // 1 sources

            if (ps != null) {
                ps.close();
            }
            break block36;
lbl110:
            // 1 sources

            catch (SQLException e) {
                ps = null;
            }
        }
        try {}
        catch (SQLException e) {
            return var11_20;
        }
        if (conn == null) return var11_20;
        conn.close();
        return var11_20;
    }

    public MerchantDetailsData getFullMerchantDetails(String username) throws FusionEJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block21: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            String query = "SELECT  user.username        ,user.dateregistered AS dateregistered        ,user.displaypicture        ,userid.id        ,merchantdetails.mentor        ,merchantdetails.referrer        ,merchantdetails.username_color_type        ,user.mobilephone        ,CASE WHEN user.type=2 THEN 1         WHEN user.type=3 THEN 2 END type FROM  user      ,merchantdetails      ,userid WHERE user.username = userid.username AND merchantdetails.id = userid.id AND userid.username=? AND user.type IN (?, ?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setInt(2, UserData.TypeEnum.MIG33_MERCHANT.value());
            ps.setInt(3, UserData.TypeEnum.MIG33_TOP_MERCHANT.value());
            rs = ps.executeQuery();
            if (!rs.next()) break block21;
            MerchantDetailsData merchant = new MerchantDetailsData(rs, true);
            rs.close();
            ps.close();
            query = "SELECT datecreated AS lasttransfer FROM accountentry WHERE username = ? AND type = ? AND amount < 0 ORDER BY id DESC LIMIT 1";
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setInt(2, AccountEntryData.TypeEnum.USER_TO_USER_TRANSFER.value());
            rs = ps.executeQuery();
            if (rs.next()) {
                merchant.lastTransfer = DateTimeUtils.getStringForMigcore(rs.getDate("lasttransfer"));
            }
            MerchantDetailsData merchantDetailsData = merchant;
            Object var9_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            return merchantDetailsData;
        }
        try {
            try {
                throw new Exception("User " + username + " is not a merchant.");
            }
            catch (Exception e) {
                throw new FusionEJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var9_10 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
    }

    /*
     * Loose catch block
     */
    public boolean setMerchantColorType(Integer merchantId, MerchantDetailsData.UserNameColorTypeEnum color) throws FusionEJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        String query = "UPDATE merchantdetails SET username_color_type = ? WHERE id = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, color.toString());
        ps.setInt(2, merchantId);
        if (ps.executeUpdate() != 1) {
            throw new Exception("Unable to set username color for user id [" + merchantId + "]");
        }
        boolean bl = true;
        Object var9_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return bl;
        catch (Exception e) {
            try {
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void responseToAlert(int notificationTypeCode, String alertKey, int userId, InvitationResponseData.ResponseType responseType) throws FusionEJBException {
        Jedis handle;
        block16: {
            block17: {
                block15: {
                    if (!Enums.NotificationTypeEnum.isForPersistent(notificationTypeCode)) {
                        log.error((Object)String.format("Can not response to a not-persistant alert, notification type:%s, userid:%s, key:%s", notificationTypeCode, userId, alertKey));
                        throw new FusionEJBException(String.format("Can not response to a not-persistant alert, notification type:%s, userid:%s, key:%s", notificationTypeCode, userId, alertKey));
                    }
                    handle = null;
                    Enums.NotificationTypeEnum notificationTypeEnum = Enums.NotificationTypeEnum.fromType(notificationTypeCode);
                    try {
                        try {
                            handle = Redis.getMasterInstanceForUserID(userId);
                            String unReadCountkey = UserNotificationServiceI.getUnreadCountUnsKey(userId, notificationTypeCode);
                            String hashMapName = UserNotificationServiceI.getUnsKey(userId, notificationTypeCode);
                            String hashMapKey = alertKey;
                            log.info((Object)String.format("Response to alert, userid:%s, alertType:%s, alertKey:%s, responseType:%s", userId, hashMapName, hashMapKey, responseType));
                            if (StringUtil.isBlank(alertKey)) {
                                if (!Enums.NotificationTypeEnum.ACCUMULATED_SET.contains((Object)notificationTypeEnum)) {
                                    log.error((Object)String.format("Can not response to reset a non-accumulated alert, notification type:%s, userid:%s, key:%s", notificationTypeCode, userId, alertKey));
                                    throw new FusionEJBException(String.format("Can not response to reset a non-accumulated alert, notification type:%s, userid:%s, key:%s", notificationTypeCode, userId, alertKey));
                                }
                                handle.del(unReadCountkey);
                                Object var17_12 = null;
                                break block15;
                            }
                            Pipeline pipeline = handle.pipelined();
                            Response rawAlertResponse = pipeline.hget(hashMapName, hashMapKey);
                            pipeline.decr(unReadCountkey);
                            pipeline.sync();
                            if (rawAlertResponse == null || StringUtil.isBlank((String)rawAlertResponse.get())) break block16;
                            InvitationData.ActivityType activityType = null;
                            if (notificationTypeEnum == Enums.NotificationTypeEnum.GAME_INVITE) {
                                activityType = InvitationData.ActivityType.PLAY_A_GAME;
                            } else {
                                if (notificationTypeEnum != Enums.NotificationTypeEnum.GAME_HELP) {
                                    log.info((Object)"Do not need to response to invitation");
                                    break block17;
                                }
                                activityType = InvitationData.ActivityType.GAME_HELP;
                            }
                            Message message = NotificationUtils.getMessageFromString((String)rawAlertResponse.get());
                            if (SystemProperty.getBool(SystemPropertyEntities.Invitation.ENABLE_LOG_INVITATION_RESPONSE_FOR_ALL_INVITERS_OF_COLLAPSE_ALERT) && message.parameters.containsKey("collapseInviterUserIdKey")) {
                                String rawInvitersStr = message.parameters.get("collapseInviterUserIdKey");
                                String[] inviters = rawInvitersStr.split(":");
                                this.doResponseToInvitationForAlert(null, userId, activityType.getTypeCode(), responseType, inviters);
                                break block16;
                            }
                            if (message.parameters.containsKey("inviterUserId")) {
                                this.doResponseToInvitationForAlert(null, userId, activityType.getTypeCode(), responseType, message.parameters.get("inviterUserId"));
                            }
                            break block16;
                        }
                        catch (JedisException je) {
                            log.error((Object)String.format("Failed to decrease unread count for user:%s, notification type:%s, notification key:%s", userId, notificationTypeCode, alertKey), (Throwable)je);
                            throw new FusionEJBException(String.format("Failed to decrease unread count for user:%s, notification type:%s, notification key:%s", userId, notificationTypeCode, alertKey), je);
                        }
                        catch (Exception e) {
                            if (e instanceof FusionEJBException) {
                                throw (FusionEJBException)e;
                            }
                            log.error((Object)String.format("Failed to decrease unread count for user:%s, notification type:%s, notification key:%s", userId, notificationTypeCode, alertKey), (Throwable)e);
                            throw new FusionEJBException(String.format("Failed to decrease unread count for user:%s, notification type:%s, notification key:%s", userId, notificationTypeCode, alertKey), e);
                        }
                    }
                    catch (Throwable throwable) {
                        Object var17_15 = null;
                        Redis.disconnect(handle, log);
                        throw throwable;
                    }
                }
                Redis.disconnect(handle, log);
                return;
            }
            Object var17_13 = null;
            Redis.disconnect(handle, log);
            return;
        }
        Object var17_14 = null;
        Redis.disconnect(handle, log);
    }

    /*
     * Loose catch block
     */
    private void doResponseToInvitationForAlert(Connection conn, int invitee, int activityType, InvitationResponseData.ResponseType responseType, String ... inviters) {
        block26: {
            ConnectionHolder ch = null;
            Statement ps = null;
            ResultSet rs = null;
            if (!InvitationUtils.isInvitationEngineEnabled(InvitationData.ChannelType.INTERNAL)) {
                return;
            }
            Timestamp actionTime = new Timestamp(System.currentTimeMillis());
            ch = new ConnectionHolder(this.dataSourceSlave, conn);
            String sql = "select id from invitation where destination = ? and type = ? and channel = 4 and status = 1 and inviterUserId in (%s)";
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < inviters.length; ++i) {
                if (i > 0) {
                    builder.append(",");
                }
                builder.append("?");
            }
            ps = ch.getConnection().prepareStatement(String.format(sql, builder.toString()));
            ps.setString(1, invitee + "");
            ps.setInt(2, activityType);
            int paramIndex = 3;
            for (int i = 0; i < inviters.length; ++i) {
                ps.setInt(paramIndex, Integer.valueOf(inviters[i]));
                ++paramIndex;
            }
            rs = ps.executeQuery();
            UserData userData = this.loadUserFromID(invitee);
            while (rs.next()) {
                InvitationData invitationData = this.getAndValidateSignUpInvitationData(null, rs.getInt("id"), actionTime);
                if (invitationData == null) continue;
                InvitationResponseData invitationResponseData = this.logInvitationResponse(null, actionTime, invitationData, responseType, userData.username, InvitationData.StatusFieldValue.CLOSED);
                log.info((Object)String.format("Invitation Response: invitaionID:%s, inviteeID:%s, inviterID:%s, response:%s, activity:%s", invitationData.id, userData.userID, invitationData.inviterUserId, invitationResponseData.responseType, invitationData.type));
            }
            Object var17_18 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
                break block26;
            }
            catch (SQLException e) {
                ch = null;
            }
            break block26;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var17_19 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    public void addMutualFollowing(int inviter, int invitee) throws FusionEJBException {
        try {
            MigboApiUtil api = MigboApiUtil.getInstance();
            String pathPrefix = String.format("/user/%s/following_request/%s?requestingUserid=%s&action=mutualfollow", inviter, invitee, inviter);
            api.postOneWay(pathPrefix, "");
        }
        catch (Exception e) {
            throw new FusionEJBException("addMutualFollowing failed. " + e.getMessage(), e);
        }
    }

    public void addMutualFollowingAndTriggerMigAlerts(UserData inviterUserData, UserData inviteeUserData) throws FusionEJBException {
        this.addMutualFollowing(inviterUserData.userID, inviteeUserData.userID);
        this.sendMigAlertToInviterWhenInviteeRegister(inviterUserData, inviteeUserData);
    }

    /*
     * Loose catch block
     */
    public void addMutualFollowingAndTriggerMigAlertsToAllInviters(InvitationData invitationData, UserData inviteeUserData) throws FusionEJBException {
        block22: {
            ConnectionHolder ch = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            ch = new ConnectionHolder(this.dataSourceSlave, null);
            ps = ch.getConnection().prepareStatement("select distinct inviteruserid from invitation where destination = ?");
            ps.setString(1, invitationData.destination);
            rs = ps.executeQuery();
            while (rs.next()) {
                int inviterUserID = rs.getInt("inviteruserid");
                UserData inviterUserData = this.loadUserFromID(inviterUserID);
                this.addMutualFollowingAndTriggerMigAlerts(inviterUserData, inviteeUserData);
            }
            Object var9_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (ch != null) {
                    ch.close();
                }
                break block22;
            }
            catch (SQLException e) {
                ch = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage(), (Exception)e);
                }
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    public void triggerFollowingAnUserAndSendingFollowingMeRequest(int inviter, int invitee) throws FusionEJBException {
        try {
            MigboApiUtil api = MigboApiUtil.getInstance();
            String pathPrefix = String.format("/user/%s/following_request/%s?requestingUserid=%s&action=follow", inviter, invitee, inviter);
            api.postOneWay(pathPrefix, "");
            UserData inviteeUserData = this.loadUserFromID(invitee);
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            HashMap<String, String> parameters = new HashMap<String, String>();
            long timestamp = System.currentTimeMillis();
            parameters.put("requestedTimestamp", String.valueOf(timestamp));
            String key = Integer.toString(inviter);
            unsProxy.notifyFusionUser(new Message(key, inviteeUserData.userID, inviteeUserData.username, Enums.NotificationTypeEnum.FOLLOWING_REQUEST.getType(), timestamp, parameters));
        }
        catch (Exception e) {
            log.error((Object)"Failed to triggerFollowingAnUserAndSendingFollowingMeRequest", (Throwable)e);
            throw new FusionEJBException(e.getMessage());
        }
    }

    public void triggerSendGameInvitationNotification(int inviter, int invitee, SendingInvitationData data, int invitationId, InvitationData.ActivityType activityType) throws FusionEJBException {
        try {
            Enums.NotificationTypeEnum notificationTypeEnum = null;
            if (activityType == InvitationData.ActivityType.PLAY_A_GAME) {
                notificationTypeEnum = Enums.NotificationTypeEnum.GAME_INVITE;
            } else if (activityType == InvitationData.ActivityType.GAME_HELP) {
                notificationTypeEnum = Enums.NotificationTypeEnum.GAME_HELP;
            } else {
                throw new FusionEJBException(String.format("ActivityType:%s is not supported in triggerSendGameInvitationNotification, expect PLAY_A_GAME or PLAY_A_GAME", activityType));
            }
            HashMap<String, String> parameters = new HashMap<String, String>();
            long timestamp = System.currentTimeMillis();
            parameters.put("timestamp", String.valueOf(timestamp));
            parameters.put("inviterUserId", inviter + "");
            parameters.put("invitationId", invitationId + "");
            if (StringUtil.isBlank(data.invitationMetadata.gameId)) {
                throw new FusionEJBException("SendingInvitationData.invitationMetadata.gameId can not be empty");
            }
            parameters.put("gameId", data.invitationMetadata.gameId);
            if (StringUtil.isBlank(data.invitationMetadata.returnUrl)) {
                throw new FusionEJBException("SendingInvitationData.invitationMetadata.returnUrl can not be empty");
            }
            parameters.put("returnURL", data.invitationMetadata.returnUrl);
            UserData inviteeUserData = this.loadUserFromID(invitee);
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            unsProxy.notifyFusionUser(new Message(data.invitationMetadata.gameId, inviteeUserData.userID, inviteeUserData.username, notificationTypeEnum.getType(), timestamp, parameters));
            log.info((Object)String.format("Sending game invite invitation to user:%s, from inviter:%s, with invitationID:%s, gameId:%s, returnURL:%s", invitee, inviter, invitationId, data.invitationMetadata.gameId, data.invitationMetadata.returnUrl));
        }
        catch (Exception e) {
            throw new FusionEJBException(e.getMessage());
        }
    }

    public InvitationDetailsData getInvitationDetailsData(String invitationTokenCode, boolean fetchExtraParameters, Date timeOfAction) throws FusionEJBException {
        InvitationDetailsData invitationDetailsData = new InvitationDetailsData();
        int invitationId = InvitationUtils.decryptReferralInvitation(invitationTokenCode);
        if (invitationId < 0) {
            invitationDetailsData.invitationID = invitationId;
            invitationDetailsData.status = InvitationStatusEnum.INVALID.getTypeCode();
        } else {
            InvitationData invitationData = this.getInvitationData(invitationId, fetchExtraParameters, null);
            invitationDetailsData = this.convertInvitationDataToInvitationDetailsData(invitationData, fetchExtraParameters, timeOfAction);
        }
        return invitationDetailsData;
    }

    public InvitationDetailsData getInvitationDetailsDataForFBInvite(String facebookRequestId, String facebookUserId, boolean fetchExtraParameters, Date timeOfAction) throws FusionEJBException {
        InvitationData invitationData = this.getInvitationDataForFBInvite(facebookRequestId, facebookUserId, fetchExtraParameters, null);
        return this.convertInvitationDataToInvitationDetailsData(invitationData, fetchExtraParameters, timeOfAction);
    }

    public InvitationDetailsData convertInvitationDataToInvitationDetailsData(InvitationData invitationData, boolean fetchExtraParameters, Date timeOfAction) throws FusionEJBException {
        InvitationDetailsData invitationDetailsData = new InvitationDetailsData();
        if (invitationData == null) {
            invitationDetailsData.invitationID = -1;
        } else {
            invitationDetailsData.invitationID = invitationData.id;
            try {
                invitationDetailsData.invitationToken = InvitationUtils.encryptReferralInvitation(invitationData.id);
            }
            catch (Exception e) {
                log.error((Object)String.format("Failed to encrypt invitation id into invitation token, invitation id : %s", invitationData.id), (Throwable)e);
                throw new FusionEJBException("Failed to encrypt invitation id into invitationToken: ", e);
            }
            invitationDetailsData.activityType = invitationData.status.getTypeCode();
            invitationDetailsData.channelType = invitationData.channel.getTypeCode();
            invitationDetailsData.destination = invitationData.destination;
            if (fetchExtraParameters) {
                invitationDetailsData.extraParameters = new HashMap();
                for (Map.Entry<InvitationData.ParamType, String> paramEntry : invitationData.getParameterEntries()) {
                    invitationDetailsData.extraParameters.put(paramEntry.getKey().getTypeCode(), paramEntry.getValue());
                }
            } else {
                invitationDetailsData.extraParameters = null;
            }
            invitationDetailsData.inviterUserID = invitationData.inviterUserId;
            invitationDetailsData.assignCreatedTime(invitationData.createdTime);
            invitationDetailsData.assignExpiredTime(invitationData.expireTime);
        }
        invitationDetailsData.status = InvitationUtils.getInvitationStatus(invitationData, timeOfAction).getTypeCode();
        return invitationDetailsData;
    }

    /*
     * Loose catch block
     */
    public String getDisplayPicture(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT displaypicture from user where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            String string = rs.getString(1);
            Object var7_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return string;
        }
        String string = null;
        Object var7_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    private UserData getMobileInviter(int userid) {
        UserData inviteeUserData = this.loadUserFromID(userid);
        if (inviteeUserData == null) {
            return null;
        }
        if (!StringUtil.isBlank(inviteeUserData.referredBy)) {
            return this.loadUser(inviteeUserData.referredBy, false, false);
        }
        return null;
    }

    /*
     * Exception decompiling
     */
    private UserData getJoinMig33Inviter(int userid) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK], 1[TRYBLOCK]], but top level block is 8[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public UserData getInviterForSignUp(int userid) {
        UserData mobileInviter;
        if (SystemProperty.getBool(SystemPropertyEntities.Invitation.ENABLE_GET_INVITER_FROM_MOBILE_REFERRER) && (mobileInviter = this.getMobileInviter(userid)) != null) {
            return mobileInviter;
        }
        return this.getJoinMig33Inviter(userid);
    }
}

