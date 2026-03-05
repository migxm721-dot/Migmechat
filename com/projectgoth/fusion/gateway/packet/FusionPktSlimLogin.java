/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.clientsession.SSOLoginData;
import com.projectgoth.fusion.common.Captcha;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DataUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedDistributedLock;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataSlimLogin;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktCaptcha;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktLogin;
import com.projectgoth.fusion.gateway.packet.FusionPktLoginChallenge;
import com.projectgoth.fusion.gateway.packet.FusionPktSlimLoginOk;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;

public class FusionPktSlimLogin
extends FusionPktDataSlimLogin {
    private static final String INCORRECT_CREDENTIAL = "Username and password do not match";
    private static final long MILLIS_IN_DAY = 86400000L;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktSlimLogin.class));
    private static final long pow2_31 = 0x80000000L;
    private static final long pow2_32 = 0x100000000L;

    public FusionPktSlimLogin() {
    }

    public FusionPktSlimLogin(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktSlimLogin(FusionPacket packet) {
        super(packet);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected FusionPacket[] processRequest(ConnectionI connection) {
        String loginLockId;
        boolean loginLockAcquired;
        FusionPktLogin loginPacket;
        FusionPktError pktError;
        block28: {
            FusionPacket[] fusionPacketArray;
            block29: {
                String exFLAKey;
                String username;
                UserData userData;
                block26: {
                    FusionPacket[] fusionPacketArray2;
                    block27: {
                        long failedLoginAttempts;
                        if (!SSOLogin.isSSOLoginEnabled()) {
                            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login is disabled.").toArray();
                        }
                        try {
                            if (connection.getSessionID() != null && SSOLogin.sessionIsActive(connection.getSessionID())) {
                                throw new Exception("You currently have an active session.");
                            }
                        }
                        catch (Exception e) {
                            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - " + e.getMessage()).toArray();
                        }
                        pktError = null;
                        userData = null;
                        loginPacket = null;
                        loginLockAcquired = false;
                        loginLockId = "";
                        username = this.getUsername();
                        if (username == null || username.length() == 0) {
                            throw new Exception(INCORRECT_CREDENTIAL);
                        }
                        FusionPktLogin login = this.createDummyLoginPacket();
                        connection = connection.onLogin(login);
                        Integer passwordHash = this.calculatePasswordHash(this.getPassword(), connection.getLoginChallenge());
                        this.setPasswordHash(passwordHash);
                        exFLAKey = username + "/" + connection.getRemoteAddress();
                        if (this.skipCaptchaCheck() || (failedLoginAttempts = MemCachedClientWrapper.getCounter(MemCachedKeySpaces.RateLimitKeySpace.FAILED_LOGIN_ATTEMPTS, exFLAKey)) <= (long)connection.getGateway().getMaxFailedLoginAttempts()) break block26;
                        ArrayList<FusionPacket> responsePackets = new ArrayList<FusionPacket>();
                        FusionPktLoginChallenge loginChallenge = new FusionPktLoginChallenge(this.transactionId);
                        loginChallenge.setSessionId(connection.getSessionID());
                        responsePackets.add(loginChallenge);
                        Captcha captcha = connection.createCaptcha(this, 4);
                        FusionPktCaptcha captchaPkt = new FusionPktCaptcha(this.transactionId, "Please enter the letters as shown in the image below.", captcha, connection);
                        responsePackets.add(captchaPkt);
                        fusionPacketArray2 = responsePackets.toArray(new FusionPacket[responsePackets.size()]);
                        Object var19_28 = null;
                        if (!loginLockAcquired) break block27;
                        MemCachedDistributedLock.releaseDistributedLock(loginLockId);
                    }
                    return fusionPacketArray2;
                }
                loginLockId = MemCachedKeyUtils.getFullKeyFromStrings("LS", this.getUsername());
                loginLockAcquired = MemCachedDistributedLock.getDistributedLock(loginLockId, 0L);
                if (!loginLockAcquired) {
                    log.warn((Object)String.format("Disallowed login from user '%s' due to concurrent login sessions.", this.getUsername()));
                    throw new Exception("Unable to login now. Please try again later.");
                }
                User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                userData = userEJB.loadUser(username, false, false);
                if (userData == null) {
                    throw new Exception(INCORRECT_CREDENTIAL);
                }
                if (userData.status != UserData.StatusEnum.ACTIVE) {
                    throw new Exception("Account is not active");
                }
                if (!userEJB.userAttemptedVerification(username, SystemProperty.getInt("AllowedVerificationWindowInHours", 96)) && !userData.mobileVerified.booleanValue() && SystemProperty.getBool("UnauthUserLoginBlockEnabled", false)) {
                    Date today = new Date();
                    Date dateRegistered = userData.dateRegistered;
                    long dateDiffInMsec = today.getTime() - dateRegistered.getTime();
                    if (dateDiffInMsec / 86400000L > (long)SystemProperty.getInt("UnauthUserAllowLoginInDays", 90)) {
                        String message = SystemProperty.get("UnauthUserLoginBlockMessage", "Authenticate yr account at http://m.mig.me/auth by stating yr USER ID, MOBILE NO & COUNTRY");
                        throw new Exception(message);
                    }
                }
                MemCachedClientWrapper.delete(MemCachedKeySpaces.RateLimitKeySpace.FAILED_LOGIN_ATTEMPTS, exFLAKey);
                MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                CountryData countryData = misEJB.getCountry(userData.countryID);
                if (countryData == null || countryData.iddCode == null) {
                    throw new Exception("Unable to determine user's country and IDD code");
                }
                CurrencyData currencyData = misEJB.getCurrency(userData.currency);
                if (currencyData == null) {
                    throw new Exception("Invalid currency code " + userData.currency);
                }
                if (!this.createSession(connection, userData)) break block28;
                FusionPktSlimLoginOk loginOk = new FusionPktSlimLoginOk(this.transactionId);
                loginOk.setSessionId(connection.getSessionID());
                userEJB.loginSucceeded(userData.username, DataUtils.truncateMobileDevice(this.getDeviceName()), DataUtils.truncateUserAgent(this.getUserAgent()), userData.language);
                fusionPacketArray = loginOk.toArray();
                Object var19_29 = null;
                if (!loginLockAcquired) break block29;
                MemCachedDistributedLock.releaseDistributedLock(loginLockId);
            }
            return fusionPacketArray;
        }
        try {
            try {
                throw new Exception("Unable to create session. Please try again later.");
            }
            catch (FusionException e) {
                pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - " + e.message);
                Object var19_30 = null;
                if (loginLockAcquired) {
                    MemCachedDistributedLock.releaseDistributedLock(loginLockId);
                }
            }
            catch (Exception e) {
                if (e.getMessage() == null) {
                    log.error((Object)("Unexpected exception without message: " + this.getUserInfoForLogging(loginPacket) + e.getClass().getName()), (Throwable)e);
                    pktError = new FusionPktInternalServerError(this.transactionId, e, "Login failed");
                } else {
                    log.error((Object)("Login failed for " + this.getUserInfoForLogging(loginPacket)), (Throwable)e);
                    pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - " + e.getMessage());
                }
                Object var19_31 = null;
                if (loginLockAcquired) {
                    MemCachedDistributedLock.releaseDistributedLock(loginLockId);
                }
            }
        }
        catch (Throwable throwable) {
            Object var19_32 = null;
            if (loginLockAcquired) {
                MemCachedDistributedLock.releaseDistributedLock(loginLockId);
            }
            throw throwable;
        }
        try {
            connection.removeConnectionFromIceAdaptor();
        }
        catch (Exception e) {
            // empty catch block
        }
        return pktError.toArray();
    }

    private String getUserInfoForLogging(FusionPktLogin loginPacket) {
        if (loginPacket == null) {
            return "";
        }
        return "user:[" + loginPacket.getUsername() + "], sessionId [" + loginPacket.getSessionId() + "], useragent [" + loginPacket.getUserAgent() + "] ";
    }

    private boolean createSession(ConnectionI connection, UserData userData) throws Exception {
        String sessionID = connection.getSessionID();
        if (sessionID == null) {
            throw new Exception(INCORRECT_CREDENTIAL);
        }
        Integer presence = 1;
        if (this.getInitialPresence() != null) {
            presence = this.getInitialPresence().value();
        }
        SSOLoginData loginData = new SSOLoginData();
        loginData.username = userData.username;
        loginData.userID = userData.userID;
        loginData.presence = presence;
        loginData.clientVersion = this.getClientVersion();
        loginData.userAgent = this.getUserAgent();
        loginData.mobileDevice = this.getDeviceName();
        loginData.voiceCapability = ContactData.VoiceCapabilityEnum.NOT_AVAILABLE.value();
        loginData.passwordHash = this.calculatePasswordHash(this.getPassword(), connection.getLoginChallenge());
        loginData.sessionStartTimeInMillis = new Long(System.currentTimeMillis());
        return SSOLogin.setLoginDataInMemcache(sessionID, loginData);
    }

    private FusionPktLogin createDummyLoginPacket() {
        FusionPktLogin login = new FusionPktLogin();
        login.setProtocolVersion(this.getProtocolVersion());
        login.setClientVersion(this.getClientVersion());
        login.setServiceType(this.getServiceType());
        login.setUsername(this.getUsername());
        login.setUserAgent(this.getUserAgent());
        login.setDeviceName(this.getDeviceName());
        login.setInitialPresence(this.getInitialPresence());
        login.setStreamUserEvents(false);
        return login;
    }

    private long to_int32(long i) {
        return i < Integer.MIN_VALUE || i > Integer.MAX_VALUE ? (i + 0x80000000L) % 0x100000000L - 0x80000000L : i;
    }

    private int calculatePasswordHash(String password, String challenge) {
        long hash = 0L;
        for (char c : (challenge + password).toCharArray()) {
            hash = this.to_int32(hash * 31L) + (long)c;
        }
        return (int)hash;
    }
}

