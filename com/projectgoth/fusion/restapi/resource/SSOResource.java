/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Pipeline
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.clientsession.SSOCaptchaState;
import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.clientsession.SSOLoginData;
import com.projectgoth.fusion.clientsession.SSOLoginSessionInfo;
import com.projectgoth.fusion.clientsession.SSOLogoutSessionInfo;
import com.projectgoth.fusion.clientsession.SimpleSSOLoginClientContext;
import com.projectgoth.fusion.clientsession.SimpleSSOLogoutClientContext;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.Redis;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.restapi.data.AuthenticationGetChallengeResponseData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.DisconnectUserSessionData;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.InternalLoginData;
import com.projectgoth.fusion.restapi.data.SSOCheckResponseData;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.restapi.data.SSOLoginResponseData;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionCredential;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/sso")
public class SSOResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SSOResource.class));

    @GET
    @Path(value="/logout/{sid}")
    @Produces(value={"application/json"})
    public DataHolder<String> logout(@PathParam(value="sid") String sid) throws FusionRestException {
        SSOLoginData userData;
        log.info((Object)String.format("SSO: logout request sid='%s'", sid));
        if (StringUtil.isBlank(sid)) {
            throw new FusionRestException(FusionRestException.RestException.MISSING_SESSION_ID);
        }
        if (SSOLogin.isEncryptedSessionID(sid)) {
            String eid = sid;
            sid = SSOLogin.getSessionIDFromEncryptedSessionID(eid);
        }
        if ((userData = SSOLogin.getLoginDataFromMemcache(sid)) == null) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "No session detected.");
        }
        SSOLogoutSessionInfo sessionInfo = new SSOLogoutSessionInfo(sid, null);
        SimpleSSOLogoutClientContext context = new SimpleSSOLogoutClientContext();
        SSOLogin.doLogout(sessionInfo, context);
        return new DataHolder<String>("ok");
    }

    @GET
    @Path(value="/logout")
    @Produces(value={"application/json"})
    public DataHolder<String> logoutWithEID(@QueryParam(value="eid") String eid) throws FusionRestException {
        log.info((Object)String.format("SSO: logout request eid='%s'", eid));
        return this.logout(eid);
    }

    @POST
    @Path(value="/login")
    @Produces(value={"application/json"})
    public DataHolder<SSOLoginResponseData> doSlimLogin(@DefaultValue(value="") @FormParam(value="eid") String eid, @DefaultValue(value="") @FormParam(value="sid") String sidForBackward, @DefaultValue(value="") @FormParam(value="username") String username, @DefaultValue(value="") @FormParam(value="password") String password, @DefaultValue(value="") @FormParam(value="captcha") String captcha, @DefaultValue(value="1") @FormParam(value="presence") byte presence, @DefaultValue(value="") @FormParam(value="useragent") String userAgent, @DefaultValue(value="") @FormParam(value="view") String viewStr, @DefaultValue(value="") @FormParam(value="clientVersion") String clientVersionStr, @DefaultValue(value="") @FormParam(value="ip") String remoteIp, @DefaultValue(value="") @FormParam(value="passwordType") String passwordTypeStr) throws FusionRestException {
        ClientType clientTypeEnum;
        if (StringUtil.isBlank(captcha) && (StringUtil.isBlank(username) || StringUtil.isBlank(password))) {
            throw new FusionRestException(FusionRestException.RestException.USER_CREDENTIALS_REQUIRED);
        }
        int clientVersion = StringUtil.toIntOrDefault(clientVersionStr, 0);
        log.info((Object)String.format("SSO: Login request for ['%s'] with presence ['%s'] and view ['%s']", username, presence, viewStr));
        SSOEnums.View view = SSOEnums.View.fromString(viewStr.toUpperCase());
        if (StringUtil.isBlank(viewStr)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Please provide a view.");
        }
        if (!SSOLogin.isSSOLoginEnabledView(view, clientVersion)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unauthorized access.");
        }
        byte passwordType = StringUtil.toByteOrDefault(passwordTypeStr, PasswordType.FUSION.value());
        if (passwordType != PasswordType.FUSION.value()) {
            if (passwordType == PasswordType.FACEBOOK_IM.value()) {
                Credential fusionCredential = this.getFusionCredentialsFromFacebookCredentials(username, passwordTypeStr);
                username = fusionCredential.username;
                password = fusionCredential.password;
            } else {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid password type");
            }
        }
        String sid = null;
        if (!StringUtil.isBlank(eid)) {
            sid = SSOLogin.getSessionIDFromEncryptedSessionID(eid);
        }
        boolean skipCaptchaCheck = false;
        String loginChallenge = null;
        int passwordHash = -1;
        if (!StringUtil.isBlank(captcha)) {
            if (StringUtil.isBlank(sid)) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Session Id is missing");
            }
            SSOCaptchaState captchaState = SimpleSSOLoginClientContext.validateCaptchaResponse(sid, captcha);
            if (captchaState == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid captcha");
            }
            if (captchaState.captchaId == null) {
                SSOLoginResponseData data = new SSOLoginResponseData();
                data.sessionId = eid;
                data.captcha = SimpleSSOLoginClientContext.getNextCaptchaImageAsString(sid, captchaState);
                data.message = "The letters you entered were incorrect. Please try again.";
                return new DataHolder<SSOLoginResponseData>(data);
            }
            skipCaptchaCheck = true;
            log.debug((Object)String.format("captcha is validated, reusing old username '%s' challenge '%s' pwdhash %d", captchaState.username, captchaState.loginChallenge, captchaState.passwordHash));
            username = captchaState.username;
            loginChallenge = captchaState.loginChallenge;
            passwordHash = captchaState.passwordHash;
        }
        SSOLoginSessionInfo sessionInfo = new SSOLoginSessionInfo(view, (clientTypeEnum = view.toFusionDeviceEnum()) == null ? null : Byte.valueOf(clientTypeEnum.value()), (short)clientVersion, sid, Integer.valueOf(presence), remoteIp, "1", userAgent, null, null);
        if (loginChallenge == null) {
            loginChallenge = ConnectionI.newChallengeString(username);
            passwordHash = SSOLogin.calculatePasswordHash(password.toLowerCase(), loginChallenge);
        }
        SimpleSSOLoginClientContext context = new SimpleSSOLoginClientContext(username, loginChallenge, passwordHash);
        SSOLoginResponseData loginResult = this.doLogin(username, loginChallenge, passwordHash, sessionInfo, skipCaptchaCheck, context, false);
        if (SystemPropertyEntities.Temp.Cache.se501ClearMemcachedOnSsoDisconnectWapEnabled.getValue().booleanValue()) {
            this.addSessionToRecentWebOnlySidsKey(username, SSOLogin.getSessionIDFromEncryptedSessionID(loginResult.sessionId));
        }
        return new DataHolder<SSOLoginResponseData>(loginResult);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void addSessionToRecentWebOnlySidsKey(String username, String sid) throws FusionRestException {
        Jedis masterInstance = null;
        try {
            try {
                long elementCountForKey;
                String key;
                long nowInSeconds;
                int userId;
                block10: {
                    UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    userId = userEJB.getUserID(username, null);
                    nowInSeconds = System.currentTimeMillis() / 1000L;
                    key = Redis.getRecentWebOnlySidsKey(userId);
                    elementCountForKey = 0L;
                    Jedis slaveInstanceForUserID = null;
                    try {
                        slaveInstanceForUserID = Redis.getSlaveInstanceForUserID(userId);
                        if (slaveInstanceForUserID != null) {
                            elementCountForKey = slaveInstanceForUserID.zcount(key, 0.0, (double)nowInSeconds);
                        }
                        Object var13_11 = null;
                        if (slaveInstanceForUserID == null) break block10;
                    }
                    catch (Throwable throwable) {
                        Object var13_12 = null;
                        if (slaveInstanceForUserID == null) throw throwable;
                        Redis.disconnect(slaveInstanceForUserID, log);
                        throw throwable;
                    }
                    Redis.disconnect(slaveInstanceForUserID, log);
                }
                masterInstance = Redis.getMasterInstanceForUserID(userId);
                long maxSessionIdCountForUser = SystemPropertyEntities.SSO.Cache.MAX_SESSIONID_COUNT_FOR_USER.getValue();
                if (elementCountForKey > maxSessionIdCountForUser) {
                    masterInstance.zremrangeByScore(key, 0.0, (double)(elementCountForKey - (maxSessionIdCountForUser + 1L)));
                }
                Pipeline pipelined = masterInstance.pipelined();
                if (log.isDebugEnabled()) {
                    log.debug((Object)String.format("adding sid to reids with key[%s] member[%s]", key, sid));
                }
                pipelined.zadd(key, (double)nowInSeconds, sid);
                pipelined.expire(key, SystemProperty.getInt(SystemPropertyEntities.SSO.SESSION_KEY_CONTAINER_EXPIRY_TIME_SECS));
                pipelined.sync();
            }
            catch (Exception e) {
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            Object var16_16 = null;
        }
        catch (Throwable throwable) {
            Object var16_17 = null;
            Redis.disconnect(masterInstance, log);
            throw throwable;
        }
        Redis.disconnect(masterInstance, log);
    }

    private Credential getFusionCredentialsFromFacebookCredentials(String username, String password) throws FusionRestException {
        if (SystemProperty.getBool(SystemPropertyEntities.FacebookConnect.ENABLED)) {
            AuthenticationServicePrx asp = EJBIcePrxFinder.getAuthenticationServiceProxy();
            if (asp == null) {
                log.error((Object)"Unable to find authentication service proxy for FB Connect SSO login");
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
            AuthenticationServiceCredentialResponse resp = asp.getLatestCredentialByUsernameAndPasswordType(username, PasswordType.FACEBOOK_IM.value());
            if (resp.code != AuthenticationServiceResponseCodeEnum.Success) {
                if (resp.code == AuthenticationServiceResponseCodeEnum.UnknownCredential) {
                    throw new FusionRestException(FusionRestException.RestException.USER_CREDENTIALS_REQUIRED, "Facebook account is not currently linked to any migme account");
                }
                log.error((Object)"Unable to check if credentials exist for FB Connect");
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
            if (!password.equals(resp.userCredential.password)) {
                log.info((Object)("User [" + resp.userCredential.userID + "] access tokens are different. Stored Access Token [" + resp.userCredential.password + "] != Input Access Token [" + password + "]"));
                resp.userCredential.password = password;
                try {
                    AuthenticationServiceResponseCodeEnum updateResp = asp.updateCredential(resp.userCredential);
                    if (updateResp != AuthenticationServiceResponseCodeEnum.Success) {
                        log.warn((Object)("Unable to update facebook credentials for user ID [" + resp.userCredential.userID + "]"));
                    }
                }
                catch (FusionException e) {
                    log.warn((Object)("Unable to update facebook credentials for user ID [" + resp.userCredential.userID + "]: " + (Object)((Object)e)));
                }
            }
            try {
                AuthenticationServiceCredentialResponse fusionCredentialResp = asp.getCredential(resp.userCredential.userID, PasswordType.FUSION.value());
                if (fusionCredentialResp.code != AuthenticationServiceResponseCodeEnum.Success) {
                    log.error((Object)("Unable to find user credentials for user ID [" + resp.userCredential.userID + "]"));
                    throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
                }
                return fusionCredentialResp.userCredential;
            }
            catch (FusionException e) {
                log.error((Object)("Unable to find user credentials for user ID [" + resp.userCredential.userID + "]: " + (Object)((Object)e)));
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
        }
        throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
    }

    @POST
    @Path(value="/check/{view}/{sid}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<SSOCheckResponseData> doCheckSessionWithMetrics(@PathParam(value="sid") String sid, @PathParam(value="view") String viewStr, @DefaultValue(value="0") @QueryParam(value="usertype") String requireUserTypeStr, String jsonStrPost) throws FusionRestException {
        String eid;
        log.debug((Object)String.format("SSO DEBUG: sid[%s] view[%s] jsonBody[%s]", sid, viewStr, jsonStrPost));
        if (StringUtil.isBlank(viewStr)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "The parameter 'view' is missing.");
        }
        SSOEnums.View view = SSOEnums.View.fromString(viewStr.toUpperCase());
        if (StringUtil.isBlank(jsonStrPost)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Missing post body");
        }
        if (StringUtil.isBlank(sid)) {
            throw new FusionRestException(FusionRestException.RestException.MISSING_SESSION_ID);
        }
        if (SSOLogin.isEncryptedSessionID(sid) && (sid = SSOLogin.getSessionIDFromEncryptedSessionID(eid = sid)) == null) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid session id");
        }
        SSOCheckResponseData response = this.checkSession(sid);
        boolean requireUserType = StringUtil.toBooleanOrDefault(requireUserTypeStr, false);
        if (requireUserType && response.type == null) {
            try {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userEJB.loadUserFromID(response.userid);
                if (userData.type != null) {
                    response.type = userData.type.value();
                }
            }
            catch (CreateException e) {
                log.error((Object)String.format("unable to create userbean ejb object to get user type for sso check - %s , user id %d, returning null", sid, response.userid), (Throwable)e);
            }
            catch (EJBException e) {
                log.error((Object)String.format("unable to get user type for sso check - %s , user id %d, returning null", sid, response.userid), (Throwable)e);
            }
        }
        try {
            SSOLogin.logSSOClientSession(sid, view, response.username, response.sessionStartTimeInSeconds, jsonStrPost);
        }
        catch (Exception e) {
            log.error((Object)("Session Archival Error: " + e.getMessage()));
        }
        return new DataHolder<SSOCheckResponseData>(response);
    }

    @POST
    @Path(value="/check/{view}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<SSOCheckResponseData> doCheckSessionWithMetricsWithEID(@QueryParam(value="eid") String eid, @PathParam(value="view") String viewStr, @DefaultValue(value="0") @QueryParam(value="usertype") String requireUserTypeStr, String jsonStrPost) throws FusionRestException {
        return this.doCheckSessionWithMetrics(eid, viewStr, requireUserTypeStr, jsonStrPost);
    }

    @GET
    @Path(value="/check/{view}/{sid}")
    @Produces(value={"application/json"})
    public DataHolder<SSOCheckResponseData> doCheckSessionBackward(@PathParam(value="sid") String sid, @PathParam(value="view") String viewStr, @DefaultValue(value="0") @QueryParam(value="usertype") String requireUserTypeStr) throws FusionRestException {
        return this.doCheckSessionWithMetrics(sid, viewStr, requireUserTypeStr, String.format("{'timestamp':%d}", System.currentTimeMillis() / 1000L));
    }

    @GET
    @Path(value="/check/{view}")
    @Produces(value={"application/json"})
    public DataHolder<SSOCheckResponseData> doCheckSession(@QueryParam(value="eid") String eid, @PathParam(value="view") String viewStr, @DefaultValue(value="0") @QueryParam(value="usertype") String requireUserTypeStr) throws FusionRestException {
        return this.doCheckSessionWithMetrics(eid, viewStr, requireUserTypeStr, String.format("{'timestamp':%d}", System.currentTimeMillis() / 1000L));
    }

    @POST
    @Path(value="/login/full/{sid}")
    @Produces(value={"application/json"})
    public DataHolder<String> doFusionLogin(@PathParam(value="sid") String sid, @DefaultValue(value="0") @FormParam(value="devicetype") int deviceType) throws FusionRestException {
        log.info((Object)String.format("SSO: Login request for ['%s'] with presence ['%s']", sid, deviceType));
        throw new FusionRestException(FusionRestException.RestException.ERROR, "full login from fusion-rest is no longer supported");
    }

    private SSOCheckResponseData checkSession(String sid) throws FusionRestException {
        SSOLoginData userData = SSOLogin.getLoginDataFromMemcache(sid);
        SSOCheckResponseData respData = null;
        if (userData != null) {
            try {
                SSOLogin.setLoginDataInMemcache(sid, userData);
                respData = userData.toSSOCheckResponseData();
            }
            catch (Exception e) {
                log.error((Object)("Unable to extend session for [" + userData.username + "] [" + userData + "] " + e.getMessage()));
            }
        } else if (SystemProperty.getBool(SystemPropertyEntities.SSO.CREATE_SSO_SESSION_FROM_FUSION)) {
            try {
                UserPrx userPrx;
                ConnectionPrx connectionPrx = EJBIcePrxFinder.findConnectionPrx(sid);
                if (connectionPrx != null && (userPrx = connectionPrx.getUserObject()) != null) {
                    log.info((Object)String.format("Re-creating sso session from fusion session sid '%s'", sid));
                    try {
                        userData = new SSOLoginData();
                        UserDataIce userDataIce = userPrx.getUserData();
                        userData.username = userDataIce.username;
                        userData.userID = userDataIce.userID;
                        userData.presence = userPrx.getOverallFusionPresence(null);
                        userData.clientVersion = connectionPrx.getClientVersion();
                        userData.deviceType = connectionPrx.getDeviceTypeAsInt();
                        userData.userAgent = connectionPrx.getUserAgent();
                        userData.mobileDevice = connectionPrx.getMobileDevice();
                        userData.voiceCapability = ContactData.VoiceCapabilityEnum.NOT_AVAILABLE.value();
                        userData.passwordHash = null;
                        userData.sessionStartTimeInMillis = new Long(System.currentTimeMillis());
                        SSOLogin.setLoginDataInMemcache(sid, userData);
                        respData = userData.toSSOCheckResponseData();
                        respData.type = userDataIce.type;
                    }
                    catch (Exception e) {
                        log.error((Object)String.format("Exception occurred while reconstructing sso session from fusion session sid '%s'", sid), (Throwable)e);
                        userData = null;
                    }
                }
            }
            catch (EJBException e) {
                // empty catch block
            }
        }
        if (userData == null) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid session id '%s'", sid));
        }
        return respData;
    }

    private String getFailedLoginAttemptKey(String username, String remoteAddress) {
        return username + "/" + remoteAddress;
    }

    @GET
    @Path(value="/authentication/{username}/getchallenge")
    @Produces(value={"application/json"})
    public DataHolder<AuthenticationGetChallengeResponseData> getLoginChallenge(@PathParam(value="username") String username, @QueryParam(value="ip") String remoteIp) throws FusionRestException {
        if (StringUtil.isBlank(username)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid username '%s'", username));
        }
        if (StringUtil.isBlank(remoteIp)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("IP address is not specified", new Object[0]));
        }
        String exFLAKey = this.getFailedLoginAttemptKey(username, remoteIp);
        long failedLoginAttempts = MemCachedClientWrapper.getCounter(MemCachedKeySpaces.RateLimitKeySpace.FAILED_AUTHENTICATION_ATTEMPTS, exFLAKey);
        if (failedLoginAttempts >= (long)SystemProperty.getInt(SystemPropertyEntities.SSO.MAX_FAILED_AUTHENTICATION_ATTEMPTS)) {
            throw new FusionRestException(FusionRestException.RestException.MAX_FAILED_AUTHENTICATION_REACHED);
        }
        AuthenticationGetChallengeResponseData respData = new AuthenticationGetChallengeResponseData();
        respData.username = username;
        respData.challenge = ConnectionI.newChallengeString(username);
        return new DataHolder<AuthenticationGetChallengeResponseData>(respData);
    }

    @GET
    @Path(value="/authentication/{username}/authenticate")
    @Produces(value={"application/json"})
    public DataHolder<String> authenticate(@PathParam(value="username") String username, @QueryParam(value="challenge") String challenge, @QueryParam(value="passwordHash") String passwordHashStr, @QueryParam(value="ip") String remoteIp) throws FusionRestException {
        if (StringUtil.isBlank(username)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid username '%s'", username));
        }
        if (StringUtil.isBlank(challenge)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid challenge '%s'", challenge));
        }
        int passwordHash = StringUtil.toIntOrDefault(passwordHashStr, Integer.MAX_VALUE);
        if (passwordHash == Integer.MAX_VALUE) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Invalid password hash '%s'", passwordHashStr));
        }
        if (StringUtil.isBlank(remoteIp)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("IP address is not specified", new Object[0]));
        }
        String exFLAKey = this.getFailedLoginAttemptKey(username, remoteIp);
        long failedLoginAttempts = MemCachedClientWrapper.getCounter(MemCachedKeySpaces.RateLimitKeySpace.FAILED_AUTHENTICATION_ATTEMPTS, exFLAKey);
        if (failedLoginAttempts >= (long)SystemProperty.getInt(SystemPropertyEntities.SSO.MAX_FAILED_AUTHENTICATION_ATTEMPTS)) {
            throw new FusionRestException(FusionRestException.RestException.MAX_FAILED_AUTHENTICATION_REACHED);
        }
        AuthenticationServicePrx authSvcPrx = EJBIcePrxFinder.getAuthenticationServiceProxy();
        if (authSvcPrx == null) {
            log.error((Object)String.format("Unable to find authentication service proxy to do authentication for user %s, challenge %s, password hash %s, ip %s", username, challenge, passwordHash, remoteIp));
            throw new FusionRestException(FusionRestException.RestException.ERROR);
        }
        try {
            AuthenticationServiceResponseCodeEnum responseCode = authSvcPrx.authenticate(new FusionCredential(0, username, null, PasswordType.FUSION.value(), passwordHash, challenge), remoteIp);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Response code from auth service for user [" + username + "] is " + responseCode));
            }
            if (responseCode != AuthenticationServiceResponseCodeEnum.Success) {
                if (responseCode != AuthenticationServiceResponseCodeEnum.UnknownUsername) {
                    MemCachedClientWrapper.addOrIncr(MemCachedKeySpaces.RateLimitKeySpace.FAILED_AUTHENTICATION_ATTEMPTS, exFLAKey, (long)SystemProperty.getInt(SystemPropertyEntities.SSO.AUTHENTICATION_FAILED_ATTEMPT_EXIPRY_IN_SECONDS) * 1000L);
                }
                throw new FusionRestException(FusionRestException.RestException.INCORRECT_AUTHENTICATION_DETAIL);
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.RateLimitKeySpace.FAILED_AUTHENTICATION_ATTEMPTS, exFLAKey);
            return new DataHolder<String>("ok");
        }
        catch (FusionException e) {
            log.error((Object)String.format("Error in authenticating user %s, challenge %s, password hash %s, ip %s - %s", username, challenge, passwordHash, remoteIp, e.getMessage()));
            throw new FusionRestException(FusionRestException.RestException.ERROR);
        }
    }

    @POST
    @Path(value="/login/{username}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<String> internalLogin(@PathParam(value="username") String usernameStr, DataHolder<InternalLoginData> internalLoginData) throws FusionRestException {
        String sessionID = null;
        InternalLoginData loginData = (InternalLoginData)internalLoginData.data;
        if (SystemProperty.getBool(SystemPropertyEntities.SSO.AUTO_LOGIN_ENABLED)) {
            if (loginData.reuseExistingSession()) {
                if (StringUtil.isBlank(loginData.sid)) {
                    throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Please provide the session id.");
                }
                try {
                    String sessID = SSOLogin.getSessionIDFromEncryptedSessionID(loginData.sid);
                    if (!SSOLogin.sessionIsActive(sessID)) {
                        throw new FusionRestException(FusionRestException.RestException.UNABLE_TO_CREATE_SESSION);
                    }
                }
                catch (Exception e) {
                    throw new FusionRestException(FusionRestException.RestException.UNABLE_TO_CREATE_SESSION);
                }
                sessionID = loginData.sid;
            } else {
                String rateLimit = SystemProperty.get(SystemPropertyEntities.SSO.AUTO_LOGIN_RATE_LIMIT);
                try {
                    log.info((Object)("Auto login request from [" + usernameStr + "]"));
                    MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.AUTO_LOGIN, usernameStr, rateLimit);
                }
                catch (MemCachedRateLimiter.LimitExceeded e) {
                    log.error((Object)("Rate limit breached for auto login [" + usernameStr + "] with rate limit [" + rateLimit + "]"));
                    throw new FusionRestException(FusionRestException.RestException.AUTO_LOGIN_RATE_LIMIT);
                }
                catch (MemCachedRateLimiter.FormatError e) {
                    log.error((Object)("Unable to automatically login user [" + usernameStr + "] due to invalid rate limit format"), (Throwable)e);
                    throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Unable to process your request at the moment, please try again later.");
                }
                SSOLoginSessionInfo sessionInfo = new SSOLoginSessionInfo(SSOEnums.View.fromString(loginData.view), loginData.deviceType, loginData.clientVersion, null, loginData.presence, loginData.remoteIpAddress, "1", loginData.userAgent, null, null);
                SimpleSSOLoginClientContext loginContext = new SimpleSSOLoginClientContext(usernameStr, null, 0);
                SSOLoginResponseData loginResult = this.doLogin(usernameStr, null, null, sessionInfo, true, loginContext, true);
                if (!StringUtil.isBlank(loginResult.captcha)) {
                    throw new FusionRestException(FusionRestException.RestException.UNABLE_TO_CREATE_SESSION);
                }
                sessionID = loginResult.sessionId;
            }
        } else {
            throw new FusionRestException(FusionRestException.RestException.DISABLED_FEATURE);
        }
        return new DataHolder<String>(sessionID);
    }

    private SSOLoginResponseData doLogin(String username, String loginChallenge, Integer passwordHash, SSOLoginSessionInfo sessionInfo, boolean skipCaptchaCheck, SimpleSSOLoginClientContext context, boolean skipAuthenticationCheck) throws FusionRestException {
        SSOLoginResponseData data = new SSOLoginResponseData();
        SSOLogin.SSOLoginResult loginResult = SSOLogin.doLogin(username, loginChallenge, passwordHash, sessionInfo, skipCaptchaCheck, context, skipAuthenticationCheck);
        String errorMessage = context.getErrorMessage();
        if (errorMessage != null) {
            log.error((Object)String.format("Failed to login for user '%s'", username));
            if (errorMessage.equals("Please activate your account first or go to m.mig.me/resend to resend token")) {
                throw new FusionRestException(FusionRestException.RestException.EMAIL_REGISTERED_USER_NOT_VERIFIED);
            }
            throw new FusionRestException(FusionRestException.RestException.ERROR, errorMessage);
        }
        if (loginResult == SSOLogin.SSOLoginResult.ERROR) {
            log.error((Object)String.format("SSO Login failed, but no error message was set, for %s", username));
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Login failed - Internal server error, please try again");
        }
        if (loginResult == SSOLogin.SSOLoginResult.OK) {
            data.sessionId = context.getEncryptedSessionID();
            log.debug((Object)String.format("login successful for user %s with eid=%s", username, data.sessionId));
        } else if (loginResult == SSOLogin.SSOLoginResult.REQUIRE_CAPTCHA) {
            data.sessionId = context.getEncryptedSessionID();
            data.captcha = context.getCaptchaImageAsString();
            data.message = "Please enter the letters as shown in the image below.";
        } else {
            if (loginResult == SSOLogin.SSOLoginResult.SESSION_ACTIVE) {
                throw new FusionRestException(FusionRestException.RestException.SESSION_ALREADY_EXISTS);
            }
            log.error((Object)String.format("SSO Login failed - unknown result %s, for %s", loginResult.name(), username));
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Login failed - Internal server error, please try again");
        }
        return data;
    }

    @POST
    @Path(value="/disconnect")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Boolean> disconnect(DataHolder<DisconnectUserSessionData> disconnectUserSessionPostData) throws FusionRestException {
        DisconnectUserSessionData disconnectUserSessionData = (DisconnectUserSessionData)disconnectUserSessionPostData.data;
        if (StringUtil.isBlank(disconnectUserSessionData.username)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Please provide the username.");
        }
        if (StringUtil.isBlank(disconnectUserSessionData.reason)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Please provide the reason why the user sessions are to be disconnected.");
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userEJB.loadUser(disconnectUserSessionData.username, false, false);
            if (userData == null) {
                throw new FusionRestException(FusionRestException.RestException.UNKNOWN_USER_NAME);
            }
            UserPrx userProxy = EJBIcePrxFinder.findUserPrx(disconnectUserSessionData.username);
            if (userProxy != null) {
                userProxy.disconnect(disconnectUserSessionData.reason);
            }
            if (SystemPropertyEntities.Temp.Cache.se501ClearMemcachedOnSsoDisconnectWapEnabled.getValue().booleanValue()) {
                this.removeWebOnlySessions(userData);
            }
        }
        catch (CreateException e) {
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, String.format("Unable to disconnect user session for %s", disconnectUserSessionData.username));
        }
        return new DataHolder<Boolean>(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void removeWebOnlySessions(UserData userData) throws FusionRestException {
        try {
            int userId = (int)userData.getUserId();
            Jedis masterInstance = null;
            try {
                String key;
                block8: {
                    key = Redis.getRecentWebOnlySidsKey(userId);
                    long nowInSeconds = System.currentTimeMillis() / 1000L;
                    Jedis slaveInstance = null;
                    try {
                        slaveInstance = Redis.getSlaveInstanceForUserID(userId);
                        if (slaveInstance != null) {
                            Set sessionIds = slaveInstance.zrange(key, 0L, nowInSeconds);
                            for (String sid : sessionIds) {
                                SSOLogin.removeDataFromMemcache(sid);
                            }
                        }
                        Object var12_11 = null;
                        if (slaveInstance == null) break block8;
                    }
                    catch (Throwable throwable) {
                        Object var12_12 = null;
                        if (slaveInstance == null) throw throwable;
                        Redis.disconnect(slaveInstance, log);
                        throw throwable;
                    }
                    Redis.disconnect(slaveInstance, log);
                }
                masterInstance = Redis.getMasterInstanceForUserID(userId);
                masterInstance.del(key);
                Object var14_14 = null;
            }
            catch (Throwable throwable) {
                Object var14_15 = null;
                Redis.disconnect(masterInstance, log);
                throw throwable;
            }
            Redis.disconnect(masterInstance, log);
            return;
        }
        catch (Exception e) {
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, String.format("%s, Unable to data from memcached, for session id %s", e.getMessage(), ""));
        }
    }
}

