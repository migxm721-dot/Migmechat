/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.clientsession.LoginException;
import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.clientsession.SSOLoginData;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktLoginOld;
import com.projectgoth.fusion.gateway.packet.FusionPktLoginResponseOld;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktCreateSessionOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktCreateSessionOld.class));
    private static final String LOGIN_DISABLED = "You cannot login at this time, please try again";
    private static final Short DEFAULT_PROTOCOL_VERSION = new Short(1);
    private static final byte DEFAULT_STREAM_USER_EVENT = 1;

    public FusionPktCreateSessionOld() {
        super((short)211, (short)0);
    }

    public FusionPktCreateSessionOld(short transactionId) {
        super((short)211, transactionId);
    }

    public FusionPktCreateSessionOld(FusionPacket packet) {
        super(packet);
    }

    public Short getProtocolVersion() {
        return this.getShortField((short)1);
    }

    public void setProtocolVersion(short protocolVersion) {
        this.setField((short)1, protocolVersion);
    }

    public Byte getClientType() {
        return this.getByteField((short)2);
    }

    public void setClientType(byte clientType) {
        this.setField((short)2, clientType);
    }

    public String getSessionID() {
        return this.getStringField((short)3);
    }

    public void setSessionID(String sid) {
        this.setField((short)3, sid);
    }

    public Byte getStreamUserEvents() {
        return this.getByteField((short)4);
    }

    public void setStreamUserEvents(byte streamUserEvents) {
        this.setField((short)4, streamUserEvents);
    }

    public Short getClientVersion() {
        return this.getShortField((short)5);
    }

    public void setClientVersion(short clientVersion) {
        this.setField((short)5, clientVersion);
    }

    public String getUserAgent() {
        return this.getStringField((short)6);
    }

    public void setUserAgent(String userAgent) {
        this.setField((short)6, userAgent);
    }

    public String getMobileDevice() {
        return this.getStringField((short)7);
    }

    public void setMobileDevice(String mobileDevice) {
        this.setField((short)7, mobileDevice);
    }

    public Byte getInitialPresence() {
        return this.getByteField((short)8);
    }

    public void setInitialPresence(byte initialPresence) {
        this.setField((short)8, initialPresence);
    }

    @Deprecated
    public Byte getVoiceCapability() {
        return (byte)ContactData.VoiceCapabilityEnum.NOT_AVAILABLE.value();
    }

    @Deprecated
    public void setVoiceCapability(byte voiceCapability) {
    }

    public Integer getFontHeight() {
        return this.getIntField((short)10);
    }

    public void setFontHeight(int fontHeight) {
        this.setField((short)10, fontHeight);
    }

    public Integer getScreenWidth() {
        return this.getIntField((short)11);
    }

    public void setScreenWidth(int screenWidth) {
        this.setField((short)11, screenWidth);
    }

    public Integer getScreenHeight() {
        return this.getIntField((short)12);
    }

    public void setScreenHeight(int screenHeight) {
        this.setField((short)12, screenHeight);
    }

    public Integer getWallpaperId() {
        return this.getIntField((short)13);
    }

    public void setWallpaperId(int wallpaperId) {
        this.setField((short)13, wallpaperId);
    }

    public Integer getThemeId() {
        return this.getIntField((short)14);
    }

    public void setThemeID(int themeId) {
        this.setField((short)14, themeId);
    }

    public String getLanguage() {
        return this.getStringField((short)15);
    }

    public void setLanguage(String language) {
        this.setField((short)15, language);
    }

    public Integer getApplicationMenuVersion() {
        return this.getIntField((short)16);
    }

    public void setApplicationMenuVersion(int appMenuVersionId) {
        this.setField((short)16, appMenuVersionId);
    }

    public String getVASTrackingId() {
        return this.getStringField((short)17);
    }

    public void setVASTrackingId(String vasTrackingId) {
        this.setField((short)17, vasTrackingId);
    }

    public Integer getVGSize() {
        return this.getIntField((short)18);
    }

    public void setVGSize(int vgSize) {
        this.setField((short)18, vgSize);
    }

    public Integer getStickerSize() {
        return this.getIntField((short)19);
    }

    public void setStickerSize(int vgSize) {
        this.setField((short)19, vgSize);
    }

    public boolean sessionRequired() {
        return false;
    }

    private FusionPktLoginOld createDummyLoginPacket(SSOLoginData loginData) throws FusionException {
        Integer stickerSize;
        Integer vgSize;
        String vasTrackingId;
        Integer applicationMenuVersion;
        String language;
        Integer wallpaperId;
        Integer fontHeight;
        Integer screenWidth;
        Byte streamUserEvents;
        Byte deviceType;
        FusionPktLoginOld login = new FusionPktLoginOld();
        Short protocolVersion = this.getProtocolVersion();
        if (protocolVersion == null) {
            protocolVersion = DEFAULT_PROTOCOL_VERSION;
        }
        login.setProtocolVersion(protocolVersion);
        Short clientVersion = this.getClientVersion();
        if (clientVersion == null) {
            clientVersion = loginData.clientVersion;
        }
        if (clientVersion != null) {
            login.setClientVersion(clientVersion);
        }
        if ((deviceType = this.getClientType()) == null && loginData.deviceType != null) {
            deviceType = loginData.deviceType.byteValue();
        }
        if (deviceType == null) {
            throw new FusionException("Client/Device Type not supplied");
        }
        login.setClientType(deviceType);
        login.setUsername(loginData.username);
        String userAgent = this.getUserAgent();
        if (StringUtil.isBlank(userAgent)) {
            userAgent = loginData.userAgent;
        }
        login.setUserAgent(userAgent);
        String mobileDevice = this.getMobileDevice();
        if (StringUtil.isBlank(mobileDevice)) {
            mobileDevice = loginData.mobileDevice;
        }
        login.setMobileDevice(mobileDevice);
        Byte initialPresenceValue = this.getInitialPresence();
        if (initialPresenceValue == null && loginData.presence != null) {
            initialPresenceValue = loginData.presence.byteValue();
        }
        PresenceType presence = null;
        if (initialPresenceValue != null) {
            presence = PresenceType.fromValue(initialPresenceValue);
        }
        if (presence != null) {
            login.setInitialPresence(presence.value());
        }
        if ((streamUserEvents = this.getStreamUserEvents()) == null) {
            streamUserEvents = 1;
        }
        login.setStreamUserEvents(streamUserEvents);
        Integer screenHeight = this.getScreenHeight();
        if (screenHeight != null) {
            login.setScreenHeight(screenHeight);
        }
        if ((screenWidth = this.getScreenWidth()) != null) {
            login.setScreenWidth(screenWidth);
        }
        if ((fontHeight = this.getFontHeight()) != null) {
            login.setFontHeight(fontHeight);
        }
        if ((wallpaperId = this.getWallpaperId()) != null) {
            login.setWallpaperId(wallpaperId);
        }
        if (!StringUtil.isBlank(language = this.getLanguage())) {
            login.setLanguage(language);
        }
        if ((applicationMenuVersion = this.getApplicationMenuVersion()) != null) {
            login.setApplicationMenuVersion(applicationMenuVersion);
        }
        if (!StringUtil.isBlank(vasTrackingId = this.getVASTrackingId())) {
            login.setVASTrackingId(vasTrackingId);
        }
        if ((vgSize = this.getVGSize()) != null) {
            login.setVGSize(vgSize);
        }
        if ((stickerSize = this.getStickerSize()) != null) {
            login.setStickerSize(stickerSize);
        }
        return login;
    }

    private static SSOLoginData refreshSSOLoginData(String sid, FusionPktLoginOld dummyLoginPkt, String username, Integer userID, Integer passwordHash, Long sessionStartTimeInMillies) {
        SSOLoginData userData = new SSOLoginData();
        userData.username = username;
        userData.userID = userID;
        userData.presence = dummyLoginPkt.getInitialPresence() != null ? Integer.valueOf(dummyLoginPkt.getInitialPresence().intValue()) : null;
        userData.clientVersion = dummyLoginPkt.getClientVersion();
        userData.deviceType = dummyLoginPkt.getClientType() != null ? Integer.valueOf(dummyLoginPkt.getClientType().intValue()) : null;
        userData.userAgent = dummyLoginPkt.getUserAgent();
        userData.mobileDevice = dummyLoginPkt.getMobileDevice();
        userData.voiceCapability = ContactData.VoiceCapabilityEnum.NOT_AVAILABLE.value();
        userData.passwordHash = passwordHash;
        if (sessionStartTimeInMillies == null) {
            sessionStartTimeInMillies = System.currentTimeMillis();
        }
        userData.sessionStartTimeInMillis = sessionStartTimeInMillies;
        SSOLogin.setLoginDataInMemcache(sid, userData);
        return userData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected FusionPacket[] processRequest(ConnectionI connection) {
        boolean succeeded;
        String sid;
        ArrayList<FusionPacket> packetsToReturn;
        block29: {
            if (!SSOLogin.isSSOLoginEnabled()) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login is disabled.").toArray();
            }
            if (null == this.getClientType()) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unknown device.").toArray();
            }
            Short protoVersion = this.getProtocolVersion();
            if (protoVersion == null) {
                protoVersion = DEFAULT_PROTOCOL_VERSION;
            }
            if (!connection.getGateway().verifyProtocolVersion(protoVersion.intValue())) {
                return new FusionPacket[]{new FusionPktError(this.transactionId, FusionPktError.Code.UNSUPPORTED_PROTOCOL, "Protocol version " + protoVersion + " is no longer supported.")};
            }
            packetsToReturn = new ArrayList<FusionPacket>();
            FusionPktError pktError = null;
            String eid = this.getSessionID();
            sid = null;
            if (SSOLogin.isEncryptedSessionID(eid)) {
                sid = SSOLogin.getSessionIDFromEncryptedSessionID(eid);
                if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.LOG_EID_DECRYPTION_FAILED_REMOTE_ADDRESS_ENABLED) && sid == null) {
                    log.error((Object)("Decryption failure of encrypted session id " + eid + " was from remote address=" + connection.getRemoteAddress()));
                }
            } else {
                sid = eid;
            }
            if (sid == null) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "No login session is found.").toArray();
            }
            succeeded = false;
            try {
                log.info((Object)("Creating ice session for [SID:" + sid + "] " + "[IP:" + connection.getRemoteAddress() + "] " + "[MD:" + this.getMobileDevice() + "] [CV:" + this.getClientVersion() + "] " + "cxn args: [SID:" + connection.getSessionID() + "] [DT:" + (Object)((Object)connection.getDeviceType()) + " ] " + "[CV:" + connection.getClientVersion() + "] [MD:" + connection.getMobileDevice() + "]"));
                SSOLoginData loginData = SSOLogin.getLoginDataFromMemcache(sid);
                if (loginData != null) {
                    ClientType device = ClientType.fromValue((int)this.getClientType().byteValue());
                    if (null == device || !SSOLogin.isFusionSessionAllowedForDeviceType(device.value())) {
                        throw new Exception("Unable to create session for this device.");
                    }
                    loginData.deviceType = device.value();
                    if (SystemProperty.getBool(SystemPropertyEntities.Default.MAX_CONCURRENT_SESSIONS_BREACHED_LOCKDOWN_ENABLED) && null != MemCachedClientWrapper.get(MemCachedKeySpaces.RateLimitKeySpace.MAX_CONCURRENT_SESSIONS_BREACHED, loginData.username)) {
                        pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, LOGIN_DISABLED);
                        return new FusionPacket[]{pktError};
                    }
                    FusionPktLoginOld loginPacket = this.createDummyLoginPacket(loginData);
                    boolean loginLockAcquired = false;
                    try {
                        loginLockAcquired = SSOLogin.getLoginSerializationLock(loginData.username, 0L);
                        if (!loginLockAcquired) {
                            log.warn((Object)String.format("Disallowed create-session from user '%s' due to concurrent login/create-session sessions.", loginData.username));
                            throw new LoginException("Unable to login now. Please try again later.");
                        }
                        loginData = FusionPktCreateSessionOld.refreshSSOLoginData(sid, loginPacket, loginData.username, loginData.userID, loginData.passwordHash, loginData.sessionStartTimeInMillis);
                        SSOLogin.setLoginDataInMemcache(sid, loginData);
                        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                        UserData userData = userEJB.loadUser(loginData.username, false, false);
                        MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                        CurrencyData currencyData = misEJB.getCurrency(userData.currency);
                        if (currencyData == null) {
                            throw new Exception("Invalid currency code " + userData.currency);
                        }
                        CountryData countryData = misEJB.getCountry(userData.countryID);
                        if (countryData == null || countryData.iddCode == null) {
                            throw new Exception("Unable to determine user's country and IDD code");
                        }
                        if (!connection.isOnLoginCalled()) {
                            loginPacket.setSessionId(sid);
                            connection = connection.onLogin(loginPacket);
                        } else {
                            log.info((Object)String.format("re-connection of a logged-in session %s, will skip onLogin", sid));
                        }
                        if (connection.getSessionPrx() == null) {
                            connection.setDeviceType(device);
                            FusionPktLoginResponseOld.createSessionObject(connection, userData, loginPacket);
                        } else {
                            log.info((Object)String.format("re-connection of a logged-in session %s, will skip creating session", sid));
                        }
                        ClientType deviceType = connection.getDeviceType();
                        if (deviceType != null && SystemProperty.isValueInArray(deviceType.name(), SystemPropertyEntities.SSO.DEVICES_REQUIRING_FULL_LOGIN_RESPONSE_ON_CREATE_SESSION)) {
                            FusionPktLoginResponseOld.populatePacketsOnLogin(packetsToReturn, connection, misEJB, this.transactionId, userData, countryData, currencyData, loginPacket, loginData.presence);
                        } else {
                            FusionPktLoginResponseOld.populatePacketsOnSlimLogin(packetsToReturn, connection, misEJB, this.transactionId, userData, countryData, loginPacket, loginData.presence);
                        }
                        succeeded = true;
                        log.info((Object)String.format("created session for [%s]", sid));
                        Object var19_22 = null;
                        if (!loginLockAcquired) break block29;
                    }
                    catch (Throwable throwable) {
                        Object var19_23 = null;
                        if (loginLockAcquired) {
                            SSOLogin.releaseLoginSerializationLock(loginData.username);
                        }
                        throw throwable;
                    }
                    SSOLogin.releaseLoginSerializationLock(loginData.username);
                    break block29;
                }
                log.warn((Object)("Attempt to create session without valid sso session for [" + sid + "]"));
                pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "No active session detected.");
                packetsToReturn.add(pktError);
            }
            catch (CreateException e) {
                log.error((Object)("Login failed for [" + sid + "]" + e.getMessage()), (Throwable)e);
                pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
                packetsToReturn.add(pktError);
            }
            catch (RemoteException e) {
                log.error((Object)("Login failed for [" + sid + "]" + e.getMessage()), (Throwable)e);
                pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
                packetsToReturn.add(pktError);
            }
            catch (LoginException e) {
                log.error((Object)("Login failed for [" + sid + "]" + e.getMessage()), (Throwable)e);
                pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - " + e.getMessage());
                packetsToReturn.add(pktError);
            }
            catch (Exception e) {
                log.error((Object)("Login failed for [" + sid + "]" + e.getMessage()), (Throwable)e);
                pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
                packetsToReturn.add(pktError);
            }
        }
        if (!succeeded) {
            log.warn((Object)("Removing connection ice adaptor due to create session failure for [" + sid + "]"));
            connection.removeConnectionFromIceAdaptor();
        }
        return packetsToReturn.toArray(new FusionPacket[packetsToReturn.size()]);
    }
}

