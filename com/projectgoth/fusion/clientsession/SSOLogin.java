/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 */
package com.projectgoth.fusion.clientsession;

import Ice.LocalException;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.clientsession.CodeIgniterEncryptionUtil;
import com.projectgoth.fusion.clientsession.LoginException;
import com.projectgoth.fusion.clientsession.SSOEncryptedSessionIDInfo;
import com.projectgoth.fusion.clientsession.SSOLoginClientContext;
import com.projectgoth.fusion.clientsession.SSOLoginData;
import com.projectgoth.fusion.clientsession.SSOLoginSessionInfo;
import com.projectgoth.fusion.clientsession.SSOLogoutClientContext;
import com.projectgoth.fusion.clientsession.SSOLogoutSessionInfo;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DataUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedDistributedLock;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.UsernameUtils;
import com.projectgoth.fusion.common.UsernameValidationException;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.exception.IceLocalExceptionAndContext;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.data.SSOSessionMetrics;
import com.projectgoth.fusion.restapi.util.RedisDataUtil;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.FusionCredential;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SSOLogin {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SSOLogin.class));
    private static final Logger vasLog = Logger.getLogger((String)"vaslog");
    private static final int[] DEFAULT_FUSION_SESSION_ALLOWED_DEVICE_TYPES = new int[]{ClientType.AJAX1.value(), ClientType.AJAX2.value()};
    private static final int[] DEFAULT_SSO_SESSION_ALLOWED_VIEWS = new int[]{SSOEnums.View.MIGBO_WEB.value(), SSOEnums.View.MIGBO_WAP.value()};
    public static final String LOGIN_FAILED_INTERNAL_ERROR_MESSAGE = "Login failed - Internal server error, please try again";
    public static final String INTERNAL_ERROR_MESSAGE = "Internal server error, please try again";
    private static final String INCORRECT_CREDENTIAL = "Username and password do not match";
    public static final String EMAIL_REGISTERED_USER_NOT_VERIFIED = "Please activate your account first or go to m.mig.me/resend to resend token";
    private static final long pow2_31 = 0x80000000L;
    private static final long pow2_32 = 0x100000000L;

    public static boolean isSSOLoginEnabled() {
        return SystemProperty.getBool(SystemPropertyEntities.Default.SSO_LOGIN_ENABLED);
    }

    public static boolean isSSOLoginEnabledClient(int deviceType, int clientVersion) {
        ClientType clientTypeEnum = ClientType.fromValue(deviceType);
        if (clientTypeEnum == null) {
            return false;
        }
        switch (clientTypeEnum) {
            case AJAX1: 
            case AJAX2: 
            case WAP: 
            case BLAAST: 
            case BLACKBERRY: 
            case IOS: {
                return true;
            }
            case ANDROID: {
                int androidSupportedVersion = SystemProperty.getInt(SystemPropertyEntities.SSO.SUPPORT_ANDROID_VERSION);
                return androidSupportedVersion > 0 && clientVersion >= androidSupportedVersion;
            }
            case MIDP1: 
            case MIDP2: {
                int midletSupportedVersion = SystemProperty.getInt(SystemPropertyEntities.SSO.SUPPORT_MIDLET_VERSION);
                return midletSupportedVersion > 0 && clientVersion >= midletSupportedVersion;
            }
            case MRE: {
                return true;
            }
        }
        return false;
    }

    public static boolean isSSOLoginEnabledView(SSOEnums.View view, int clientVersion) {
        int[] SSO_SESSION_ALLOWED_VIEWS;
        if (view == null) {
            return false;
        }
        for (int allowedView : SSO_SESSION_ALLOWED_VIEWS = SystemProperty.getIntArray(SystemPropertyEntities.SSO.SSO_SESSION_ALLOWED_VIEWS, DEFAULT_SSO_SESSION_ALLOWED_VIEWS)) {
            if (view.value() != allowedView) continue;
            return true;
        }
        ClientType clientTypeEnum = view.toFusionDeviceEnum();
        return SSOLogin.isSSOLoginEnabledClient(clientTypeEnum == null ? (byte)0 : clientTypeEnum.value(), clientVersion);
    }

    public static boolean isFusionSessionAllowedForDeviceType(int deviceType) {
        int[] FUSION_SESSION_ALLOWED_DEVICE_TYPES;
        for (int type : FUSION_SESSION_ALLOWED_DEVICE_TYPES = SystemProperty.getIntArray(SystemPropertyEntities.SSO.FUSION_SESSION_ALLOWED_VIEWS, DEFAULT_FUSION_SESSION_ALLOWED_DEVICE_TYPES)) {
            if (deviceType != type) continue;
            return true;
        }
        return false;
    }

    public static boolean isEncryptedSessionIDSupported(int deviceType, int clientVersion) {
        ClientType clientTypeEnum = ClientType.fromValue(deviceType);
        if (clientTypeEnum == null) {
            return false;
        }
        switch (clientTypeEnum) {
            case AJAX1: 
            case AJAX2: 
            case WAP: 
            case BLAAST: 
            case BLACKBERRY: 
            case IOS: {
                return true;
            }
            case ANDROID: {
                int androidSupportedVersion = SystemProperty.getInt(SystemPropertyEntities.SSO.FUSION_EID_ANDROID_VERSION);
                return androidSupportedVersion > 0 && clientVersion >= androidSupportedVersion;
            }
            case MIDP1: 
            case MIDP2: {
                int midletSupportedVersion = SystemProperty.getInt(SystemPropertyEntities.SSO.FUSION_EID_MIDLET_VERSION);
                return midletSupportedVersion > 0 && clientVersion >= midletSupportedVersion;
            }
            case MRE: {
                return true;
            }
        }
        return false;
    }

    public static byte[] getSSOEIDEncryptionKeyInBytes() {
        String encryptionKey = SystemProperty.get(SystemPropertyEntities.SSO.EID_ENCRPYTION_KEY);
        String encryptionKeyHex = HashUtils.asHex(HashUtils.md5(encryptionKey));
        log.debug((Object)String.format("encryption key: %s , after hash: %s", encryptionKey, encryptionKeyHex));
        return encryptionKeyHex.getBytes();
    }

    public static String generateEncryptedSessionID(SSOEncryptedSessionIDInfo eidInfo) {
        return CodeIgniterEncryptionUtil.encryptToBase64(eidInfo.toJSON());
    }

    public static String getSessionIDFromEncryptedSessionID(String encryptedSessionID) {
        SSOEncryptedSessionIDInfo eidInfo = SSOEncryptedSessionIDInfo.fromJSON(CodeIgniterEncryptionUtil.decryptFromBase64(encryptedSessionID));
        if (eidInfo != null) {
            return eidInfo.sid;
        }
        log.error((Object)String.format("Unable to decode encrypted session id %s", encryptedSessionID));
        return null;
    }

    public static boolean isEncryptedSessionID(String encryptedSessionID) {
        return !StringUtil.isBlank(encryptedSessionID) && encryptedSessionID.length() > 70;
    }

    public static boolean setLoginDataInMemcache(String sid, SSOLoginData ld) {
        boolean result;
        if (StringUtil.isBlank(sid)) {
            log.warn((Object)String.format("Trying to set login data to memcache for empty sid", new Object[0]));
            return false;
        }
        if (ld == null) {
            log.warn((Object)String.format("Trying to set empty login data to memcache for sid", sid));
            return false;
        }
        try {
            result = MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.LOGIN_DATA, sid, ld.toString(), SystemProperty.getLong(SystemPropertyEntities.SSO.SESSION_EXPIRY_IN_SECONDS) * 1000L);
        }
        catch (Exception e) {
            result = false;
        }
        log.info((Object)("Setting login data [" + MemCachedKeySpaces.CommonKeySpace.LOGIN_DATA + "/" + sid + "] [" + ld.username + "] [" + ld + "] success?[" + result + "]"));
        return result;
    }

    public static SSOLoginData getLoginDataFromMemcache(String sid) {
        if (StringUtil.isBlank(sid)) {
            log.warn((Object)String.format("Trying to get login data from memcache for empty sid", new Object[0]));
            return null;
        }
        try {
            String data = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.LOGIN_DATA, sid);
            if (!StringUtil.isBlank(data)) {
                return new SSOLoginData(data);
            }
        }
        catch (Exception e) {
            log.error((Object)("Unexpected exception while retrieving session data for [" + sid + "] from memcache: " + e.getMessage()));
        }
        return null;
    }

    public static Map<String, String> getMultipleLoginDataStrFromMemcache(String[] sids) {
        return MemCachedClientWrapper.getMultiString(MemCachedKeySpaces.CommonKeySpace.LOGIN_DATA, sids);
    }

    public static boolean sessionIsActive(String sid) throws Exception {
        try {
            SSOLoginData sessionData = SSOLogin.getLoginDataFromMemcache(sid);
            log.info((Object)("Getting session for [" + sid + "] data " + sessionData));
            return sessionData != null;
        }
        catch (Exception e) {
            throw new Exception("Unable to retrieve session status: " + e.getMessage());
        }
    }

    public static boolean removeDataFromMemcache(String sid) throws Exception {
        try {
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.LOGIN_DATA, sid);
            log.info((Object)("Cleared session for [" + sid + "]"));
        }
        catch (Exception e) {
            throw new Exception("Unable to clear session: " + e.getMessage());
        }
        return true;
    }

    public static void logSSOClientSession(String sid, SSOEnums.View view, String username, Long sessionStartTimeInSeconds, String jsonStrPost) throws Exception {
        SSOSessionMetrics metrics = null;
        if (SystemProperty.getBool(SystemPropertyEntities.SSO.SESSION_ARCHIVE_ENABLED)) {
            try {
                log.debug((Object)String.format("Logging session sid[%s] view [%s] username[%s] sessionStart [%s] jsondata [%s]", sid, view.toString(), username, new Date(sessionStartTimeInSeconds * 1000L).toString(), jsonStrPost));
                metrics = SSOSessionMetrics.fromJSONString(sid, view, username, sessionStartTimeInSeconds, jsonStrPost);
            }
            catch (Exception e) {
                throw new Exception(String.format("Error Parsing Input [%s]", new Object[0]));
            }
            if (metrics != null) {
                RedisDataUtil.logSSOClientSession(metrics);
                log.info((Object)String.format("Session logging successful for sid[%s] view [%s] username[%s] sessionStart [%s]", sid, view.toString(), username, new Date(sessionStartTimeInSeconds * 1000L).toString()));
            } else {
                log.warn((Object)String.format("Error logging session sid[%s] view [%s] username[%s] sessionStart [%s] jsondata [%s]", sid, view.toString(), username, new Date(sessionStartTimeInSeconds * 1000L).toString(), jsonStrPost));
            }
        }
    }

    public static boolean doLogout(SSOLogoutSessionInfo sessionInfo, SSOLogoutClientContext context) {
        try {
            SSOLogin.removeDataFromMemcache(sessionInfo.sessionID);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unable to remove memcached entry to logout session %s, still proceeding to logout", sessionInfo.sessionID));
        }
        context.postLogout(sessionInfo);
        if (SystemPropertyEntities.Temp.Cache.se501ClearMemcachedOnSsoDisconnectWapEnabled.getValue().booleanValue()) {
            try {
                SSOLogin.removeSessionFromRecentWebOnlySidsKey(sessionInfo.connection.getUserID(), sessionInfo.sessionID);
            }
            catch (Exception e) {
                log.error((Object)("Failed to clear session [sid:" + sessionInfo.sessionID + "] from recent web only sids key e=" + e), (Throwable)e);
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void removeSessionFromRecentWebOnlySidsKey(int userID, String sessionId) throws Exception {
        Jedis masterInstance = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("calling removeSessionFromRecentWebOnlySidsKey for userid: [%s] member:[%s]", userID, sessionId));
            }
            masterInstance = Redis.getMasterInstanceForUserID(userID);
            String key = Redis.getRecentWebOnlySidsKey(userID);
            long count = masterInstance.zrem(key, new String[]{sessionId});
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("removeSessionFromRecentWebOnlySidsKey: removed count: [%s]", count));
            }
            Object var7_5 = null;
        }
        catch (Throwable throwable) {
            Object var7_6 = null;
            Redis.disconnect(masterInstance, log);
            throw throwable;
        }
        Redis.disconnect(masterInstance, log);
    }

    public static boolean getLoginSerializationLock(String username, long waitTimeMillis) {
        return MemCachedDistributedLock.getDistributedLock(MemCachedKeyUtils.getFullKeyFromStrings("LS", username), waitTimeMillis);
    }

    public static void releaseLoginSerializationLock(String username) {
        MemCachedDistributedLock.releaseDistributedLock(MemCachedKeyUtils.getFullKeyFromStrings("LS", username));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static SSOLoginResult doLogin(String username, String loginChallenge, Integer passwordHash, SSOLoginSessionInfo sessionInfo, boolean skipCaptchaCheck, SSOLoginClientContext context, boolean skipAuthenticationCheck) {
        SSOLoginResult sSOLoginResult;
        boolean loginLockAcquired;
        block57: {
            Object today;
            block56: {
                SSOLoginResult sSOLoginResult2;
                block55: {
                    SSOLoginResult sSOLoginResult3;
                    block54: {
                        SSOLoginResult sSOLoginResult4;
                        block53: {
                            SSOLoginResult e3;
                            block52: {
                                SSOLoginResult failedLoginAttempts2;
                                block51: {
                                    SSOLoginResult sSOLoginResult5;
                                    block49: {
                                        SSOLoginResult sSOLoginResult6;
                                        block48: {
                                            SSOLoginResult errorCode2;
                                            block47: {
                                                block46: {
                                                    if (!SSOLogin.isSSOLoginEnabled()) {
                                                        context.errorOccurred("Login is disabled.");
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    if (SystemProperty.getBool(SystemPropertyEntities.SSO.ENABLE_PRELOGIN_USERNAME_CHAR_VALIDATION)) {
                                                        try {
                                                            UsernameUtils.validateUsernameAsSearchKeyword(username);
                                                        }
                                                        catch (UsernameValidationException ex) {
                                                            if (SystemProperty.getBool(SystemPropertyEntities.SSO.LOG_INVALID_USERNAME_ON_PRELOGIN)) {
                                                                String errorCode2 = ex.getErrorCause() != null ? ex.getErrorCause().getCode() : "";
                                                                log.info((Object)("Received an invalid username.RemoteAddress:[" + sessionInfo.remoteAddress + "].ErrorCode:[" + errorCode2 + "].Username[" + MemCachedKeyUtils.getFullKeyFromStrings(username, new String[0]) + "]"));
                                                            }
                                                            context.errorOccurred(FusionPktError.Code.INCORRECT_CREDENTIAL, INCORRECT_CREDENTIAL);
                                                            return SSOLoginResult.ERROR;
                                                        }
                                                    }
                                                    if (!StringUtil.isBlank(sessionInfo.sessionID)) {
                                                        try {
                                                            if (SSOLogin.sessionIsActive(sessionInfo.sessionID)) {
                                                                return SSOLoginResult.SESSION_ACTIVE;
                                                            }
                                                            break block46;
                                                        }
                                                        catch (Exception e2) {
                                                            log.error((Object)String.format("Unable to check sso session - %s", e2.getMessage()));
                                                            context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, e2);
                                                            return SSOLoginResult.ERROR;
                                                        }
                                                    }
                                                    sessionInfo.sessionID = ConnectionI.newSessionID();
                                                    log.info((Object)String.format("Created new session id %s for user %s", sessionInfo.sessionID, username));
                                                }
                                                loginLockAcquired = false;
                                                try {
                                                    try {
                                                        String exFLAKey;
                                                        block50: {
                                                            if (username == null || username.length() == 0) {
                                                                context.errorOccurred(FusionPktError.Code.INCORRECT_CREDENTIAL, INCORRECT_CREDENTIAL);
                                                                errorCode2 = SSOLoginResult.ERROR;
                                                                Object var22_22 = null;
                                                                if (!loginLockAcquired) return errorCode2;
                                                                break block47;
                                                            }
                                                            exFLAKey = SSOLogin.getFailedLoginAttemptKey(username, sessionInfo.remoteAddress);
                                                            if (!skipCaptchaCheck) {
                                                                long failedLoginAttempts2 = MemCachedClientWrapper.getCounter(MemCachedKeySpaces.RateLimitKeySpace.FAILED_LOGIN_ATTEMPTS, exFLAKey);
                                                                if (failedLoginAttempts2 > (long)SystemProperty.getInt(SystemPropertyEntities.SSO.MAX_FAILED_LOGIN_ATTEMPTS)) {
                                                                    context.captchaRequired(sessionInfo);
                                                                    sSOLoginResult6 = SSOLoginResult.REQUIRE_CAPTCHA;
                                                                    break block48;
                                                                }
                                                                if (SystemProperty.getBool(SystemPropertyEntities.Default.CAPTCHA_REQUIRED_ENABLED) && MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CAPTCHA_REQUIRED, StringUtil.normalizeUsername(username)) != null) {
                                                                    context.captchaRequired(sessionInfo);
                                                                    sSOLoginResult5 = SSOLoginResult.REQUIRE_CAPTCHA;
                                                                    break block49;
                                                                }
                                                            }
                                                            if (!skipAuthenticationCheck) {
                                                                try {
                                                                    if (SSOLogin.authenticateUser(context, username, loginChallenge, passwordHash, sessionInfo)) break block50;
                                                                    failedLoginAttempts2 = SSOLoginResult.ERROR;
                                                                    break block51;
                                                                }
                                                                catch (EJBException e3) {
                                                                    log.error((Object)"Unable to find authentication service proxy to perform login", (Throwable)e3);
                                                                    context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, (Exception)((Object)e3));
                                                                    SSOLoginResult sSOLoginResult7 = SSOLoginResult.ERROR;
                                                                    Object var22_26 = null;
                                                                    if (!loginLockAcquired) return sSOLoginResult7;
                                                                    SSOLogin.releaseLoginSerializationLock(username);
                                                                    return sSOLoginResult7;
                                                                }
                                                            }
                                                            log.info((Object)("Login requested by " + username + " without authentication. [sid: " + sessionInfo.sessionID + ", remoteIp: " + sessionInfo.remoteAddress + ", view: " + sessionInfo.view.name() + "]"));
                                                        }
                                                        loginLockAcquired = SSOLogin.getLoginSerializationLock(username, 0L);
                                                        if (!loginLockAcquired) {
                                                            log.warn((Object)String.format("Disallowed login from user '%s' due to concurrent login sessions.", username));
                                                            context.errorOccurred("Unable to login now. Please try again later.");
                                                            e3 = SSOLoginResult.ERROR;
                                                            break block52;
                                                        }
                                                        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                                                        UserData userData = userEJB.loadUser(username, false, false);
                                                        if (userData == null) {
                                                            context.errorOccurred(FusionPktError.Code.INCORRECT_CREDENTIAL, INCORRECT_CREDENTIAL);
                                                            sSOLoginResult4 = SSOLoginResult.ERROR;
                                                            break block53;
                                                        }
                                                        if (userData.status != UserData.StatusEnum.ACTIVE) {
                                                            context.errorOccurred("Account is not active");
                                                            sSOLoginResult3 = SSOLoginResult.ERROR;
                                                            break block54;
                                                        }
                                                        if (!userEJB.userAttemptedVerification(username, SystemProperty.getInt(SystemPropertyEntities.Default.ALLOWED_VERIFICATION_WINDOW_IN_HOURS)) && !AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.LOGIN_AFTER_90DAYS, userData)) {
                                                            today = new Date();
                                                            Date dateRegistered = userData.dateRegistered;
                                                            long dateDiffInMsec = today.getTime() - dateRegistered.getTime();
                                                            if (dateDiffInMsec / DateTimeUtils.TWENTY_FOUR_HOURS_IN_MS > (long)SystemProperty.getInt(SystemPropertyEntities.Default.UNAUTH_USER_ALLOW_LOGIN_IN_DAYS)) {
                                                                String message = SystemProperty.get(SystemPropertyEntities.Default.UNAUTH_USER_LOGIN_BLOCK_MESSAGE);
                                                                context.errorOccurred(message);
                                                                sSOLoginResult2 = SSOLoginResult.ERROR;
                                                                break block55;
                                                            }
                                                        }
                                                        if (userData.mobilePhone == null && !StringUtil.isBlank(userData.emailAddress) && !userData.emailVerified.booleanValue() && SystemProperty.getBool(SystemPropertyEntities.SSO.EMAIL_UNAUTH_USER_LOGIN_DISABLED)) {
                                                            log.warn((Object)String.format("Login denied due to email registered user not verified, username %s, email %s", username, userData.emailAddress));
                                                            context.errorOccurred(EMAIL_REGISTERED_USER_NOT_VERIFIED);
                                                            today = SSOLoginResult.ERROR;
                                                            break block56;
                                                        }
                                                        MemCachedClientWrapper.delete(MemCachedKeySpaces.RateLimitKeySpace.FAILED_LOGIN_ATTEMPTS, exFLAKey);
                                                        MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CAPTCHA_REQUIRED, StringUtil.normalizeUsername(username));
                                                        MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                                                        CountryData countryData = misEJB.getCountry(userData.countryID);
                                                        if (countryData == null || countryData.iddCode == null) {
                                                            log.error((Object)String.format("Unable to determine user's country or IDD code for user %s, countryID %d", username, userData.countryID));
                                                            throw new LoginException(INTERNAL_ERROR_MESSAGE);
                                                        }
                                                        CurrencyData currencyData = misEJB.getCurrency(userData.currency);
                                                        if (currencyData == null) {
                                                            log.error((Object)String.format("Invalid currency code for user %s, currency %s", username, userData.currency));
                                                            throw new LoginException(INTERNAL_ERROR_MESSAGE);
                                                        }
                                                        if (!SSOLogin.createSSOSession(sessionInfo, passwordHash, userData)) throw new LoginException("Unable to create session. Please try again later.");
                                                        context.ssoSessionCreated(sessionInfo, userData);
                                                        boolean sameMobileDevice = false;
                                                        if (userData.mobileDevice != null && sessionInfo.mobileDevice != null) {
                                                            sameMobileDevice = userData.mobileDevice.equals(DataUtils.truncateMobileDevice(sessionInfo.mobileDevice));
                                                        }
                                                        boolean sameUserAgent = false;
                                                        if (userData.userAgent != null && sessionInfo.userAgent != null) {
                                                            sameUserAgent = userData.userAgent.equals(DataUtils.truncateUserAgent(sessionInfo.userAgent));
                                                        }
                                                        boolean minLoginTimeExceeded = false;
                                                        int minLoginUpdatePeriod = SystemProperty.getInt(SystemPropertyEntities.SSO.MIN_LOGIN_UPDATE_PERIOD_IN_SECONDS);
                                                        if (userData.lastLoginDate != null) {
                                                            boolean bl = minLoginTimeExceeded = Calendar.getInstance().getTimeInMillis() - userData.lastLoginDate.getTime() > (long)(minLoginUpdatePeriod * 1000);
                                                        }
                                                        if (!sameMobileDevice || !sameUserAgent || minLoginTimeExceeded) {
                                                            userEJB.loginSucceeded(userData.username, DataUtils.truncateMobileDevice(sessionInfo.mobileDevice), DataUtils.truncateUserAgent(sessionInfo.userAgent), sessionInfo.language);
                                                        } else {
                                                            log.warn((Object)("User: [" + userData.username + "] logged in multiple times within the space of [" + minLoginUpdatePeriod + "] seconds. Mobile Device: [" + sessionInfo.mobileDevice + "], UserAgent: [" + sessionInfo.userAgent + "], Remote IP: [" + sessionInfo.remoteAddress + "]"));
                                                        }
                                                        if (SystemProperty.getBool(SystemPropertyEntities.SSO.VAS_LOG_ENABLED) && sessionInfo.connection != null && !StringUtil.isBlank(sessionInfo.connection.getVasID())) {
                                                            vasLog.info((Object)(sessionInfo.sessionID + "\t" + sessionInfo.connection.getVasID()));
                                                        }
                                                        if (sessionInfo.view.isMigboView() || sessionInfo.view == SSOEnums.View.UNKNOWN) {
                                                            long sessionStartTime = System.currentTimeMillis() / 1000L;
                                                            String jsonStrPost = String.format("{'timestamp':%d,'records':{'username':'%s','clientType':%d,'clientVersion':%d}}", sessionStartTime, username, sessionInfo.getClientType(), sessionInfo.getClientVersion());
                                                            SSOLogin.logSSOClientSession(sessionInfo.sessionID, sessionInfo.view, username, sessionStartTime, jsonStrPost);
                                                        }
                                                        sSOLoginResult = SSOLoginResult.OK;
                                                        break block57;
                                                    }
                                                    catch (CreateException e4) {
                                                        log.error((Object)"Unable to create bean", (Throwable)e4);
                                                        context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, (Exception)((Object)e4));
                                                        Object var22_33 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (RemoteException e5) {
                                                        log.error((Object)("Remote exception: " + e5.getClass().getName()), (Throwable)e5);
                                                        context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, e5);
                                                        Object var22_34 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (ObjectNotFoundException e6) {
                                                        log.error((Object)"User object not found in Object cache: ", (Throwable)((Object)e6));
                                                        context.errorOccurred("Login failed - Please try again", (Exception)((Object)e6));
                                                        Object var22_35 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (ObjectExistsException e7) {
                                                        log.error((Object)"User object already exists in object cache: ", (Throwable)((Object)e7));
                                                        context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, (Exception)((Object)e7));
                                                        Object var22_36 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (FusionException e8) {
                                                        log.error((Object)"FusionException : ", (Throwable)((Object)e8));
                                                        context.errorOccurred("Login failed - " + e8.message, (Exception)((Object)e8));
                                                        Object var22_37 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (IceLocalExceptionAndContext e9) {
                                                        log.error((Object)("Unable to login due to Ice exception " + e9.getRootException().ice_name() + ", proxy=" + e9.getProxy() + ", " + e9.getMessage()), (Throwable)e9);
                                                        context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, e9);
                                                        Object var22_38 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (LocalException e10) {
                                                        log.error((Object)("Unable to login due to Ice exception " + e10.ice_name() + ", " + e10.getMessage()), (Throwable)e10);
                                                        context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, (Exception)((Object)e10));
                                                        Object var22_39 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (LoginException e11) {
                                                        log.warn((Object)("Login failed for " + SSOLogin.getUserInfoForLogging(username, sessionInfo) + ", Exception: " + e11.getMessage()), (Throwable)e11);
                                                        context.errorOccurred("Login failed - " + e11.getMessage(), e11);
                                                        Object var22_40 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                    catch (Exception e12) {
                                                        if (e12.getMessage() == null) {
                                                            log.error((Object)("Unexpected exception without message: " + SSOLogin.getUserInfoForLogging(username, sessionInfo) + e12.getClass().getName()), (Throwable)e12);
                                                        } else {
                                                            log.error((Object)("Login failed for " + SSOLogin.getUserInfoForLogging(username, sessionInfo)), (Throwable)e12);
                                                        }
                                                        context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, e12);
                                                        Object var22_41 = null;
                                                        if (!loginLockAcquired) return SSOLoginResult.ERROR;
                                                        SSOLogin.releaseLoginSerializationLock(username);
                                                        return SSOLoginResult.ERROR;
                                                    }
                                                }
                                                catch (Throwable throwable) {
                                                    Object var22_42 = null;
                                                    if (!loginLockAcquired) throw throwable;
                                                    SSOLogin.releaseLoginSerializationLock(username);
                                                    throw throwable;
                                                }
                                            }
                                            SSOLogin.releaseLoginSerializationLock(username);
                                            return errorCode2;
                                        }
                                        Object var22_23 = null;
                                        if (!loginLockAcquired) return sSOLoginResult6;
                                        SSOLogin.releaseLoginSerializationLock(username);
                                        return sSOLoginResult6;
                                    }
                                    Object var22_24 = null;
                                    if (!loginLockAcquired) return sSOLoginResult5;
                                    SSOLogin.releaseLoginSerializationLock(username);
                                    return sSOLoginResult5;
                                }
                                Object var22_25 = null;
                                if (!loginLockAcquired) return failedLoginAttempts2;
                                SSOLogin.releaseLoginSerializationLock(username);
                                return failedLoginAttempts2;
                            }
                            Object var22_27 = null;
                            if (!loginLockAcquired) return e3;
                            SSOLogin.releaseLoginSerializationLock(username);
                            return e3;
                        }
                        Object var22_28 = null;
                        if (!loginLockAcquired) return sSOLoginResult4;
                        SSOLogin.releaseLoginSerializationLock(username);
                        return sSOLoginResult4;
                    }
                    Object var22_29 = null;
                    if (!loginLockAcquired) return sSOLoginResult3;
                    SSOLogin.releaseLoginSerializationLock(username);
                    return sSOLoginResult3;
                }
                Object var22_30 = null;
                if (!loginLockAcquired) return sSOLoginResult2;
                SSOLogin.releaseLoginSerializationLock(username);
                return sSOLoginResult2;
            }
            Object var22_31 = null;
            if (!loginLockAcquired) return today;
            SSOLogin.releaseLoginSerializationLock(username);
            return today;
        }
        Object var22_32 = null;
        if (!loginLockAcquired) return sSOLoginResult;
        SSOLogin.releaseLoginSerializationLock(username);
        return sSOLoginResult;
    }

    private static String getFailedLoginAttemptKey(String username, String remoteAddress) {
        return username + "/" + remoteAddress;
    }

    private static String getUserInfoForLogging(String username, SSOLoginSessionInfo sessionInfo) {
        if (sessionInfo == null) {
            return String.format("user:[%s]", username);
        }
        return String.format("user:[%s], sessionId [%s], useragent [%s]", username, sessionInfo.sessionID, sessionInfo.userAgent);
    }

    private static boolean authenticateUser(SSOLoginClientContext context, String username, String loginChallenge, Integer passwordHash, SSOLoginSessionInfo sessionInfo) throws LoginException, FusionException, EJBException {
        String exFLAKey = SSOLogin.getFailedLoginAttemptKey(username, sessionInfo.remoteAddress);
        if (passwordHash == null) {
            MemCachedClientWrapper.addOrIncr(MemCachedKeySpaces.RateLimitKeySpace.FAILED_LOGIN_ATTEMPTS, exFLAKey);
            context.errorOccurred(INCORRECT_CREDENTIAL);
            return false;
        }
        AuthenticationServiceResponseCodeEnum responseCode = context.findAuthenticationServiceProxy(sessionInfo).authenticate(new FusionCredential(0, username, null, PasswordType.FUSION.value(), passwordHash, loginChallenge), sessionInfo.remoteAddress);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Response code from auth service for user [" + username + "] is " + responseCode));
        }
        if (responseCode != AuthenticationServiceResponseCodeEnum.Success) {
            if (responseCode != AuthenticationServiceResponseCodeEnum.UnknownUsername) {
                MemCachedClientWrapper.addOrIncr(MemCachedKeySpaces.RateLimitKeySpace.FAILED_LOGIN_ATTEMPTS, exFLAKey);
            } else {
                try {
                    User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                    if (userEJB.isEmailRegistrationNotVerifiedForUsername(username)) {
                        context.errorOccurred(EMAIL_REGISTERED_USER_NOT_VERIFIED);
                        return false;
                    }
                }
                catch (CreateException e) {
                    log.error((Object)"Unable to create bean", (Throwable)e);
                    context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, (Exception)((Object)e));
                }
                catch (RemoteException e) {
                    log.error((Object)"Unable to create bean", (Throwable)e);
                    context.errorOccurred(LOGIN_FAILED_INTERNAL_ERROR_MESSAGE, e);
                }
            }
            context.errorOccurred(FusionPktError.Code.INCORRECT_CREDENTIAL, INCORRECT_CREDENTIAL);
            return false;
        }
        if (MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.LOGIN_BAN, username) != null) {
            context.errorOccurred("Your account is suspended");
            return false;
        }
        if (MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.TEMP_LOGIN_SUSPENSION, username) != null) {
            context.errorOccurred(SystemProperty.get(SystemPropertyEntities.SSO.TEMP_LOGIN_SUSPENSION_ERROR_MESSAGE));
            return false;
        }
        return true;
    }

    private static long to_int32(long i) {
        return i < Integer.MIN_VALUE || i > Integer.MAX_VALUE ? (i + 0x80000000L) % 0x100000000L - 0x80000000L : i;
    }

    public static int calculatePasswordHash(String password, String challenge) {
        long hash = 0L;
        for (char c : (challenge + password).toCharArray()) {
            hash = SSOLogin.to_int32(hash * 31L) + (long)c;
        }
        return (int)hash;
    }

    private static boolean createSSOSession(SSOLoginSessionInfo sessionInfo, Integer passwordHash, UserData userData) throws LoginException {
        if (sessionInfo.sessionID == null) {
            throw new LoginException(INCORRECT_CREDENTIAL);
        }
        SSOLoginData loginData = new SSOLoginData();
        loginData.username = userData.username;
        loginData.userID = userData.userID;
        loginData.presence = sessionInfo.initialPresence;
        loginData.clientVersion = sessionInfo.getClientVersion();
        loginData.deviceType = sessionInfo.getClientType();
        loginData.userAgent = sessionInfo.userAgent;
        loginData.mobileDevice = sessionInfo.mobileDevice;
        loginData.voiceCapability = ContactData.VoiceCapabilityEnum.NOT_AVAILABLE.value();
        loginData.passwordHash = passwordHash;
        loginData.sessionStartTimeInMillis = new Long(System.currentTimeMillis());
        return SSOLogin.setLoginDataInMemcache(sessionInfo.sessionID, loginData);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SSOLoginResult {
        OK,
        ERROR,
        SESSION_ACTIVE,
        REQUIRE_CAPTCHA;

    }
}

