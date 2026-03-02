/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.TimeoutException
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import Ice.TimeoutException;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.authentication.DecayingFailedAuthsByIPScore;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.clientsession.LoginException;
import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.clientsession.SSOLoginClientContext;
import com.projectgoth.fusion.clientsession.SSOLoginSessionInfo;
import com.projectgoth.fusion.common.Captcha;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ContentUtils;
import com.projectgoth.fusion.common.DataUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.URLUtil;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.data.ApplicationMenuOptionData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupInvitationData;
import com.projectgoth.fusion.data.MenuData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.exception.IceLocalExceptionAndContext;
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImDetailType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.fdl.enums.UserType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataImSessionStatus;
import com.projectgoth.fusion.fdl.packets.FusionPktDataLoginResponse;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.packet.FusionPktAccountBalance;
import com.projectgoth.fusion.gateway.packet.FusionPktApplicationMenu;
import com.projectgoth.fusion.gateway.packet.FusionPktApplicationMenuComplete;
import com.projectgoth.fusion.gateway.packet.FusionPktAvatar;
import com.projectgoth.fusion.gateway.packet.FusionPktCaptcha;
import com.projectgoth.fusion.gateway.packet.FusionPktDynamicMenu;
import com.projectgoth.fusion.gateway.packet.FusionPktEmoticonHotKeysOld;
import com.projectgoth.fusion.gateway.packet.FusionPktEmoticonHotkeys;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktIMAvailableOld;
import com.projectgoth.fusion.gateway.packet.FusionPktIMSessionStatusOld;
import com.projectgoth.fusion.gateway.packet.FusionPktImAvailable;
import com.projectgoth.fusion.gateway.packet.FusionPktImSessionStatus;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktLogin;
import com.projectgoth.fusion.gateway.packet.FusionPktLoginOk;
import com.projectgoth.fusion.gateway.packet.FusionPktMailInfo;
import com.projectgoth.fusion.gateway.packet.FusionPktMidletTab;
import com.projectgoth.fusion.gateway.packet.FusionPktServerQuestion;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.interfaces.Voice;
import com.projectgoth.fusion.interfaces.VoiceHome;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.ShortTextStatusUserEventIce;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.userevent.domain.ShortTextStatusUserEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FusionPktLoginResponse
extends FusionPktDataLoginResponse
implements SSOLoginClientContext {
    private static final String INCORRECT_CREDENTIAL = "Username and password do not match";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktLoginResponse.class));
    private FusionPktError pktError = null;
    private List<FusionPacket> packetsToReturn = new ArrayList<FusionPacket>();

    public FusionPktLoginResponse() {
    }

    public FusionPktLoginResponse(short transactionId) {
        super(transactionId);
    }

    public FusionPktLoginResponse(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktLoginResponse(FusionPacket packet) {
        super(packet);
    }

    @Override
    protected FusionPacket[] processRequest(ConnectionI connection) {
        if (connection.getSessionPrx() != null) {
            this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Session already exists");
        } else {
            FusionPktLogin loginPacket = connection.getLoginPacket();
            if (loginPacket == null) {
                this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
            } else {
                String username = loginPacket.getUsername();
                ClientType clientTypeEnum = loginPacket.getClientType();
                SSOEnums.View view = SSOEnums.View.fromFusionDeviceEnum(clientTypeEnum);
                SSOLoginSessionInfo sessionInfo = new SSOLoginSessionInfo(view, clientTypeEnum.value(), loginPacket.getClientVersion(), connection.getSessionID(), loginPacket.getInitialPresenceValue(), connection.getRemoteIPAddress(), loginPacket.getDeviceName(), loginPacket.getUserAgent(), loginPacket.getLanguage(), connection);
                SSOLogin.SSOLoginResult loginResult = SSOLogin.doLogin(username, connection.getLoginChallenge(), this.getPasswordHash(), sessionInfo, this.skipCaptchaCheck(), this, false);
                if (loginResult == SSOLogin.SSOLoginResult.OK) {
                    if (this.pktError == null) {
                        return this.packetsToReturn.toArray(new FusionPacket[this.packetsToReturn.size()]);
                    }
                } else if (loginResult == SSOLogin.SSOLoginResult.REQUIRE_CAPTCHA) {
                    if (this.pktError == null) {
                        return this.packetsToReturn.toArray(new FusionPacket[this.packetsToReturn.size()]);
                    }
                } else if (loginResult == SSOLogin.SSOLoginResult.ERROR) {
                    if (this.pktError == null) {
                        log.error((Object)String.format("SSO Login failed, but no pktError packet was set, for %s", this.getUserInfoForLogging(loginPacket)));
                        this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
                    }
                    this.pktError = this.applyResponseDelayIfFailedAuthsExceedThreshold(connection, this.pktError);
                } else if (loginResult == SSOLogin.SSOLoginResult.SESSION_ACTIVE) {
                    this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Session already exists");
                } else {
                    log.error((Object)String.format("SSO Login failed - unknown result %s, for %s", loginResult.name(), this.getUserInfoForLogging(loginPacket)));
                    this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
                }
            }
        }
        connection.removeConnectionFromIceAdaptor();
        if (SystemPropertyEntities.Temp.Cache.se606NpeFixEnabled.getValue().booleanValue()) {
            return null != this.pktError ? this.pktError.toArray() : new FusionPacket[]{};
        }
        return this.pktError.toArray();
    }

    private FusionPktError applyResponseDelayIfFailedAuthsExceedThreshold(ConnectionI connection, FusionPktError pktError) {
        try {
            String ip = connection.getRemoteAddress();
            DecayingFailedAuthsByIPScore score = DecayingFailedAuthsByIPScore.load(ip);
            int threshold = SystemProperty.getInt(SystemPropertyEntities.SSO.FAILED_AUTHS_PER_IP_THRESHOLD);
            if (score.getScore() > (double)threshold) {
                String warnStub = "[IP:" + ip + "] [USER:" + connection.getUsername() + "] " + "Failed auths score=" + score.getScore() + " exceed ";
                if (score.getScore() < (double)SystemProperty.getInt(SystemPropertyEntities.SSO.FAILED_AUTHS_PER_IP_PACKET_DROP_THRESHOLD)) {
                    log.warn((Object)(warnStub + "lower threshold, delaying returning LOGIN_RESPONSE result to user"));
                    this.setResponseDelay(score.getLoginResponseResultDelayMillis(threshold));
                    return pktError;
                }
                log.warn((Object)(warnStub + "upper threshold, returning no LOGIN_RESPONSE result to user"));
                return null;
            }
        }
        catch (Exception e) {
            log.error((Object)("Exception applying failed auth login response delay: e=" + e), (Throwable)e);
        }
        return pktError;
    }

    @Override
    public void ssoSessionCreated(SSOLoginSessionInfo sessionInfo, UserData userData) throws RemoteException, CreateException, FusionException, LoginException {
        boolean isReconnecting;
        if (sessionInfo.connection == null) {
            log.error((Object)String.format("sessionInfo.connection is null, for login of user %s", userData.username));
            throw new LoginException("Login failed - Internal server error, please try again");
        }
        ConnectionI connection = sessionInfo.connection;
        FusionPktLogin loginPacket = connection.getLoginPacket();
        MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
        CountryData countryData = misEJB.getCountry(userData.countryID);
        if (countryData == null || countryData.iddCode == null) {
            throw new LoginException("Unable to determine user's country and IDD code");
        }
        CurrencyData currencyData = misEJB.getCurrency(userData.currency);
        if (currencyData == null) {
            throw new LoginException("Invalid currency code " + userData.currency);
        }
        FusionPktLoginResponse.createSessionObject(connection, userData, loginPacket);
        this.packetsToReturn = new ArrayList<FusionPacket>();
        boolean bl = isReconnecting = loginPacket.getSessionId() != null;
        if (isReconnecting) {
            FusionPktLoginResponse.populatePacketsOnSlimLogin(this.packetsToReturn, connection, misEJB, this.transactionId, userData, countryData, loginPacket, null);
            FusionPktLoginResponse.populateFusionPktMailInfo(this.packetsToReturn, connection, userData, countryData);
        } else {
            FusionPktLoginResponse.populatePacketsOnLogin(this.packetsToReturn, connection, misEJB, this.transactionId, userData, countryData, currencyData, loginPacket, null);
        }
    }

    private String getUserInfoForLogging(FusionPktLogin loginPacket) {
        if (loginPacket == null) {
            return "";
        }
        return "user:[" + loginPacket.getUsername() + "], sessionId [" + loginPacket.getSessionId() + "], useragent [" + loginPacket.getUserAgent() + "] ";
    }

    public static SessionPrx createSessionObject(ConnectionI connection, UserData userData, FusionPktLogin loginPacket) throws LoginException, ObjectExistsException, ObjectNotFoundException, FusionException {
        String sessionID = connection.getSessionID();
        if (sessionID == null) {
            throw new LoginException(INCORRECT_CREDENTIAL);
        }
        RegistryPrx registryPrx = connection.findRegistry();
        if (registryPrx == null) {
            throw new LoginException("Unable to locate registry");
        }
        UserPrx userPrx = null;
        try {
            userPrx = registryPrx.findUserObject(userData.username);
        }
        catch (ObjectNotFoundException e) {
            userPrx = null;
        }
        if (userPrx == null) {
            ObjectCachePrx objectCachePrx = registryPrx.getLowestLoadedObjectCache();
            userPrx = objectCachePrx.createUserObject(userData.username);
        }
        connection.onCreatingSession();
        try {
            SessionPrx sessionPrx = userPrx.createSession(sessionID, loginPacket.getInitialPresenceValue(), loginPacket.getClientType().value(), connection.getServerType() == Gateway.ServerType.TCP ? Enums.ConnectionEnum.TCP.value() : Enums.ConnectionEnum.HTTP.value(), ImType.FUSION.value(), connection.getPort(), connection.getRemotePort(), connection.getRemoteAddress(), DataUtils.truncateMobileDevice(loginPacket.getDeviceName(), true, String.format("creating new user session for user '%s'", userData.username)), DataUtils.truncateUserAgent(loginPacket.getUserAgent(), true, String.format("creating new user session for user '%s'", userData.username)), loginPacket.getClientVersion(), connection.getLanguage(), connection.getConnectionPrx());
            connection.onSessionCreated(userPrx, sessionPrx, userData);
            return sessionPrx;
        }
        catch (TimeoutException ite) {
            throw new IceLocalExceptionAndContext((LocalException)((Object)ite), userPrx);
        }
    }

    private static void streamInitialUserEvents(ConnectionI connection, UserData userData) {
        try {
            if (SystemProperty.getBool(SystemPropertyEntities.Default.EVENT_SYSTEM_ENABLED) && connection.findEventSystem() != null) {
                connection.findEventSystem().streamEventsToLoggingInUser(userData.username, connection.getConnectionPrx());
            } else {
                ShortTextStatusUserEvent event = new ShortTextStatusUserEvent();
                event.setGeneratingUsername("migme");
                event.setStatus(SystemProperty.get(SystemPropertyEntities.Default.EVENT_SYSTEM_WELCOME_MESSAGE));
                event.setTimestamp(System.currentTimeMillis());
                ShortTextStatusUserEventIce eventIce = event.toIceEvent();
                connection.putEvent(eventIce);
            }
        }
        catch (Exception e) {
            log.warn((Object)("Failed to ask EventSystem to start streaming events for user [" + userData.username + "]"), (Throwable)e);
        }
    }

    private static int getUnreadEmailCount(ConnectionI connection, CountryData countryData, UserData userData) {
        try {
            if (countryData.allowEmail == null || !countryData.allowEmail.booleanValue() || userData.emailActivated == null || !userData.emailActivated.booleanValue()) {
                return 0;
            }
            UserPrx userPrx = connection.getUserPrx();
            if (userPrx == null) {
                log.error((Object)"UserPrx is null");
                return 0;
            }
            int unreadEmailCount = userPrx.getUnreadEmailCount();
            if (unreadEmailCount < 0) {
                connection.findEmailAlert().requestUnreadEmailCount(userData.username, userData.password, connection.getUserPrx());
            }
            return unreadEmailCount;
        }
        catch (Exception e) {
            log.warn((Object)("Failed to get unread email count for user [" + userData.username + "]"), (Throwable)e);
            return 0;
        }
    }

    private static ImDetailType getDetailType(byte[] availablePasswordTypes, int[] connectedIMs, ImType imType) {
        PasswordType passwordType = PasswordType.forIMEnum(imType);
        if (Arrays.binarySearch(availablePasswordTypes, passwordType.value()) < 0) {
            return ImDetailType.UNREGISTERED;
        }
        return Arrays.binarySearch(connectedIMs, (int)imType.value()) < 0 ? ImDetailType.DISCONNECTED : ImDetailType.CONNECTED;
    }

    private static FusionPktLoginOk constructSlimLoginOk(short transactionId, ConnectionI connection, UserData userData, FusionPktLogin loginPacket) throws RemoteException, CreateException, FusionException {
        FusionPktLoginOk loginOk = new FusionPktLoginOk(transactionId);
        UserPrx userPrx = connection.getUserPrx();
        if (userPrx == null) {
            throw new FusionException("You are no longer logged in");
        }
        int[] connectedIMs = userPrx.getConnectedOtherIMs();
        if (connectedIMs == null) {
            connectedIMs = new int[]{};
        } else {
            Arrays.sort(connectedIMs);
        }
        byte[] availablePasswordTypes = connection.findAuthenticationService().availableCredentialTypes(userData.userID);
        loginOk.setMsnDetail(FusionPktLoginResponse.getDetailType(availablePasswordTypes, connectedIMs, ImType.MSN));
        loginOk.setYahooDetail(FusionPktLoginResponse.getDetailType(availablePasswordTypes, connectedIMs, ImType.YAHOO));
        loginOk.setAimDetail(FusionPktLoginResponse.getDetailType(availablePasswordTypes, connectedIMs, ImType.AIM));
        loginOk.setGtalkDetail(FusionPktLoginResponse.getDetailType(availablePasswordTypes, connectedIMs, ImType.GTALK));
        loginOk.setFacebookDetail(FusionPktLoginResponse.getDetailType(availablePasswordTypes, connectedIMs, ImType.FACEBOOK));
        loginOk.setContactListVersion(userPrx.getContactListVersion());
        if (loginPacket != null && SSOLogin.isSSOLoginEnabledClient(loginPacket.getClientType().value(), loginPacket.getClientVersion().shortValue())) {
            loginOk.setUserId(userData.userID);
        }
        loginOk.setUsername(userData.username);
        boolean flag = MessageSwitchboardDispatcher.getInstance().isFeatureEnabled();
        loginOk.setSupportsChatSync(flag);
        boolean stickersFlag = ContentUtils.isStickersEnabled() && ContentUtils.userCanSendStickers(userData.userID);
        loginOk.setSupportsStickers(stickersFlag);
        loginOk.setServerTime(System.currentTimeMillis());
        return loginOk;
    }

    public static FusionPktLoginOk constructLoginOk(short transactionId, ConnectionI connection, UserData userData, CountryData countryData, CurrencyData currencyData, FusionPktLogin loginPacket) throws RemoteException, CreateException, FusionException {
        String mailURL;
        boolean anonymousCalling;
        Voice voiceEJB;
        String pageletURL;
        String imageServerURL;
        AlertMessageData alertMessage;
        FusionPktLoginOk loginOk = FusionPktLoginResponse.constructSlimLoginOk(transactionId, connection, userData, loginPacket);
        if (userData.mobilePhone != null) {
            loginOk.setMobilePhone(userData.mobilePhone);
        }
        loginOk.setMobileVerified(AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.RETURN_VERIFIED_UPON_LOGIN, userData));
        loginOk.setUserType(UserType.fromValue(userData.type.value()));
        User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        boolean groupInvitationAlertPushed = false;
        try {
            GroupInvitationData groupInvitation = (GroupInvitationData)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.GROUP_INVITATION, userData.username);
            if (groupInvitation != null && groupInvitation.groupID != null && connection.getDeviceType() == ClientType.MIDP2) {
                if (groupInvitation.inviter == null) {
                    String groupJoinPageletURL = URLUtil.replaceViewTypeToken(SystemProperty.get("GroupJoinURL", ""), connection.getDeviceType());
                    if (!StringUtil.isBlank(groupJoinPageletURL)) {
                        groupJoinPageletURL = groupJoinPageletURL + groupInvitation.groupID;
                        loginOk.setAlertContentType(AlertContentType.URL);
                        loginOk.setAlert(groupJoinPageletURL);
                        groupInvitationAlertPushed = true;
                    }
                } else {
                    String groupInvitationAlert = SystemProperty.get("GroupInvitationAlert", "");
                    if (groupInvitationAlert.length() > 0) {
                        GroupData groupData = userEJB.getGroup(groupInvitation.groupID);
                        groupInvitationAlert = groupInvitationAlert.replaceAll("%inviter%", groupInvitation.inviter).replaceAll("%groupname%", groupData.name);
                        loginOk.setAlertContentType(AlertContentType.TEXT);
                        loginOk.setAlert(groupInvitationAlert);
                        groupInvitationAlertPushed = true;
                    }
                }
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.GROUP_INVITATION, userData.username);
            }
        }
        catch (Exception e) {
            log.warn((Object)("Exception occurred when handling a group invitation for " + userData.username), (Throwable)e);
        }
        if (!groupInvitationAlertPushed && (alertMessage = connection.getDeviceType() == ClientType.MIDP1 ? userEJB.getLatestAlertMessage(connection.getClientVersion(), AlertMessageData.TypeEnum.LOGIN, userData.countryID, userData.lastLoginDate, AlertContentType.TEXT, ClientType.MIDP2.value()) : userEJB.getLatestAlertMessage(connection.getClientVersion(), AlertMessageData.TypeEnum.LOGIN, userData.countryID, userData.lastLoginDate, null, connection.getDeviceType().value())) != null) {
            if (alertMessage.contentType == AlertContentType.URL_WITH_CONFIRMATION) {
                if (alertMessage.url != null) {
                    connection.setServerQuestion(new FusionPktServerQuestion(alertMessage));
                }
            } else {
                loginOk.setAlertContentType(alertMessage.contentType);
                loginOk.setAlert(alertMessage.content);
            }
        }
        loginOk.setCurrency(userData.currency);
        if (!CurrencyData.baseCurrency.equalsIgnoreCase(userData.currency)) {
            loginOk.setExchangeRate(currencyData.formatExchangeRate());
        }
        if ((imageServerURL = SystemProperty.get("ImageServerURL", "")).length() > 0) {
            loginOk.setImageServerUrl(imageServerURL);
        }
        if ((pageletURL = SystemProperty.get("PageletURL", "")).length() > 0) {
            loginOk.setPageletUrl(pageletURL);
        }
        loginOk.setLocalDidSupport((voiceEJB = (Voice)EJBHomeCache.getObject("ejb/Voice", VoiceHome.class)).getDIDNumber(userData.countryID) != null);
        if (connection.isMidletVersionAndAbove(405) && (anonymousCalling = SystemProperty.getBool("AnonymousCalling", false))) {
            loginOk.setAnonymousCalling(true);
        }
        if (countryData.allowEmail != null && countryData.allowEmail.booleanValue() && (mailURL = SystemProperty.get("MailURL", "")).length() > 0) {
            loginOk.setMailUrl(mailURL);
            loginOk.setMailCount(0);
        }
        loginOk.setUserEventsToKeep(connection.getGateway().getNumberOfEventsToKeep());
        loginOk.setEmoticonHeight(connection.getOptimalEmoticonHeight());
        loginOk.setVirtualGiftSize(connection.getOptimalVirtualGiftSize());
        if (connection.isMidletVersionAndAbove(420) || connection.isBrowserDevice()) {
            ReputationLevelData levelData = MemCacheOrEJB.getUserReputationLevelData(userData, userEJB);
            loginOk.setReputationLevel((short)levelData.level.intValue());
            if (connection.isBrowserDevice()) {
                loginOk.setReputationImagePath(levelData.image);
            } else if (connection.isMobileClientV2()) {
                String reputationImagePath = SystemProperty.get("ReputationImagePath", "");
                loginOk.setReputationImagePath(reputationImagePath + levelData.image + "-42x42.png");
            } else {
                String reputationImagePath = SystemProperty.get("ReputationImagePath", "");
                loginOk.setReputationImagePath(reputationImagePath + levelData.image + "-12x12.png");
            }
        }
        if (SystemProperty.getBool("TCPTunnelling", false)) {
            loginOk.setTcpTunneling(true);
        }
        return loginOk;
    }

    @Override
    public void captchaRequired(SSOLoginSessionInfo sessionInfo) {
        if (sessionInfo.connection == null) {
            this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
        } else if (sessionInfo.connection.isMidletVersionAndAbove(430) || sessionInfo.connection.isBrowserDevice()) {
            Captcha captcha = sessionInfo.connection.createCaptcha(this, 4);
            try {
                this.packetsToReturn.add(new FusionPktCaptcha(this.transactionId, "Please enter the letters as shown in the image below.", captcha, sessionInfo.connection));
            }
            catch (IOException e) {
                log.error((Object)String.format("Failed to create captcha packet for " + this.getUserInfoForLogging(sessionInfo.connection.getLoginPacket()), new Object[0]));
                this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Login failed - Internal server error, please try again");
            }
        } else {
            this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "You have too many failed login attempts. Please login using latest version of migme, Web, or WAP");
        }
    }

    @Override
    public void errorOccurred(String errorMessage) {
        this.pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, errorMessage);
    }

    @Override
    public void errorOccurred(FusionPktError.Code errorCode, String errorMessage) {
        this.pktError = new FusionPktError(this.transactionId, errorCode, errorMessage);
    }

    @Override
    public void errorOccurred(String errorMessage, Exception errorCause) {
        this.pktError = errorCause != null ? new FusionPktInternalServerError(this.transactionId, errorCause, errorMessage) : new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, errorMessage);
    }

    @Override
    public AuthenticationServicePrx findAuthenticationServiceProxy(SSOLoginSessionInfo sessionInfo) {
        return sessionInfo.connection.findAuthenticationService();
    }

    public static void populateFusionPktMailInfo(Collection<FusionPacket> packetsToReturn, ConnectionI connection, UserData userData, CountryData countryData) {
        int unreadEmailCount = FusionPktLoginResponse.getUnreadEmailCount(connection, countryData, userData);
        if (unreadEmailCount > 0) {
            packetsToReturn.add(new FusionPktMailInfo(unreadEmailCount));
        }
    }

    public static void populatePacketsOnSlimLogin(Collection<FusionPacket> packetsToReturn, ConnectionI connection, MIS misEJB, short loginPktTransactionId, UserData userData, CountryData countryData, FusionPktLogin loginPacket, Integer fusionPresence) throws RemoteException, FusionException, CreateException {
        FusionPktLoginOk loginOk = FusionPktLoginResponse.constructSlimLoginOk(loginPktTransactionId, connection, userData, loginPacket);
        if (fusionPresence != null) {
            loginOk.setFusionPresence(PresenceType.fromValue(fusionPresence));
        }
        packetsToReturn.add(loginOk);
    }

    public static void populatePacketsOnLogin(Collection<FusionPacket> packetsToReturn, ConnectionI connection, MIS misEJB, short loginPktTransactionId, UserData userData, CountryData countryData, CurrencyData currencyData, FusionPktLogin loginPacket, Integer fusionPresence) throws RemoteException, FusionException, CreateException {
        Boolean streamUserEvents;
        boolean isNewUser;
        List menus;
        FusionPacket emoticonHotKeys;
        FusionPktLoginOk loginOk = FusionPktLoginResponse.constructLoginOk(loginPktTransactionId, connection, userData, countryData, currencyData, loginPacket);
        if (fusionPresence != null) {
            loginOk.setFusionPresence(PresenceType.fromValue(fusionPresence));
        }
        packetsToReturn.add(loginOk);
        if (SystemPropertyEntities.Temp.Cache.fi83Week3UseAutogeneratedPacketsEnabled.getValue().booleanValue()) {
            emoticonHotKeys = new FusionPktEmoticonHotkeys(loginPktTransactionId, connection.getUserPrx());
            packetsToReturn.add(emoticonHotKeys);
        } else {
            emoticonHotKeys = new FusionPktEmoticonHotKeysOld(loginPktTransactionId, connection.getUserPrx());
            packetsToReturn.add(emoticonHotKeys);
        }
        if (connection.isMidletVersionAndAbove(300) && (menus = misEJB.getMenus(userData.countryID, connection.getClientVersion(), connection.getDeviceType().value())) != null) {
            for (MenuData menuData : menus) {
                packetsToReturn.add(new FusionPktDynamicMenu(loginPktTransactionId, menuData));
            }
        }
        if (connection.isMobileClientV2AndNewVersion()) {
            List menuOptions;
            int appMenuVersion = misEJB.getApplicationMenuVersion(loginPacket.getClientType().value(), loginPacket.getClientVersion(), loginPacket.getVasTrackingId());
            Integer loginPacketAppMenuVer = loginPacket.getApplicationMenuVersion();
            if (loginPacketAppMenuVer != null && appMenuVersion > loginPacketAppMenuVer && (menuOptions = misEJB.getApplicationMenuOptions(appMenuVersion)) != null) {
                for (ApplicationMenuOptionData menu : menuOptions) {
                    packetsToReturn.add(new FusionPktApplicationMenu(menu));
                }
                FusionPktApplicationMenuComplete menuCompletePacket = new FusionPktApplicationMenuComplete();
                menuCompletePacket.setMenuVersionId(appMenuVersion);
                packetsToReturn.add(menuCompletePacket);
            }
        }
        Short migLevel = loginOk.getReputationLevel() == null ? (short)0 : loginOk.getReputationLevel();
        boolean bl = isNewUser = migLevel <= SystemProperty.getInt(SystemPropertyEntities.MigLevel.NEW_USER_MAX);
        if (connection.isMidletVersionAndAbove(400) || connection.isAjax()) {
            packetsToReturn.add(new FusionPktAvatar(loginPktTransactionId, userData));
            packetsToReturn.add(new FusionPktAccountBalance(loginPktTransactionId, userData, currencyData));
            if (connection.isMidletVersionAndAbove(430) || connection.isAjax()) {
                FusionPktLoginResponse.populateImPackets(packetsToReturn, connection, loginPktTransactionId, loginOk);
            } else if (connection.isMidletVersionAndAbove(420)) {
                FusionPktLoginResponse.populateImPacketsForMidlet420(packetsToReturn, connection, loginPktTransactionId, loginOk);
            }
            if (connection.isMidletVersionAndAbove(420) && isNewUser) {
                connection.addMidletTabAfterLogin(FusionPktMidletTab.newUserRegistrationWizardTab(loginPktTransactionId, loginOk.getMobileVerified(), connection.getDeviceType()));
            }
            if (connection.isMidletVersionBelow(450)) {
                if (connection.getMidletTabsAfterLogin().size() == 0) {
                    connection.addMidletTabAfterLogin(FusionPktMidletTab.newMigWorldTab(loginPktTransactionId, connection.getRemoteAddress(), connection.getDeviceType()));
                }
                connection.addMidletTabAfterLogin(FusionPktMidletTab.newHomeTab(loginPktTransactionId));
            } else if (!isNewUser) {
                connection.addMidletTabAfterLogin(FusionPktMidletTab.newMigWorldTab(loginPktTransactionId, connection.getRemoteAddress(), connection.getDeviceType()));
            }
            if (!connection.isAjax() && userData.type == UserData.TypeEnum.MIG33_TOP_MERCHANT) {
                connection.addMidletTabAfterLogin(FusionPktMidletTab.newMerchantHomePage(loginPktTransactionId, connection.getDeviceType()));
            }
            if (!connection.isAjax() && userData.type != UserData.TypeEnum.MIG33_TOP_MERCHANT && !userData.chatRoomAdmin.booleanValue()) {
                connection.addMidletTabAfterLogin(FusionPktMidletTab.newMigGamesPage(loginPktTransactionId, connection.getDeviceType()));
            }
            if (misEJB.isVASBuild(connection.getUserAgent())) {
                connection.addMidletTabAfterLogin(FusionPktMidletTab.newVASPortalTab(loginPktTransactionId, connection.getDeviceType()));
            }
        }
        if ((streamUserEvents = loginPacket.getStreamUserEvents()) == null && connection.isMidletVersionAndAbove(400) || streamUserEvents != null && streamUserEvents.booleanValue()) {
            FusionPktLoginResponse.streamInitialUserEvents(connection, userData);
        }
        FusionPktLoginResponse.populateFusionPktMailInfo(packetsToReturn, connection, userData, countryData);
    }

    private static void populateImPackets(Collection<FusionPacket> packetsToReturn, ConnectionI connection, short loginPktTransactionId, FusionPktLoginOk loginOk) {
        for (Enums.IMEnum otherIM : Enums.IMEnum.values()) {
            FusionPacket imAvailablePkt;
            if (otherIM == Enums.IMEnum.AIM || otherIM == Enums.IMEnum.FUSION) continue;
            ImDetailType imDetail = null;
            switch (otherIM) {
                case MSN: {
                    imDetail = loginOk.getMsnDetail();
                    break;
                }
                case AIM: {
                    break;
                }
                case YAHOO: {
                    imDetail = loginOk.getYahooDetail();
                    break;
                }
                case GTALK: {
                    imDetail = loginOk.getGtalkDetail();
                    break;
                }
                case FACEBOOK: {
                    imDetail = loginOk.getFacebookDetail();
                }
            }
            if (SystemPropertyEntities.Temp.Cache.fi83Week3UseAutogeneratedPacketsEnabled.getValue().booleanValue()) {
                imAvailablePkt = new FusionPktImAvailable(loginPktTransactionId, otherIM, imDetail == null ? ImDetailType.UNREGISTERED : imDetail);
                packetsToReturn.add(imAvailablePkt);
                continue;
            }
            imAvailablePkt = new FusionPktIMAvailableOld(loginPktTransactionId, otherIM, imDetail == null ? (byte)0 : imDetail.value());
            packetsToReturn.add(imAvailablePkt);
        }
    }

    private static void populateImPacketsForMidlet420(Collection<FusionPacket> packetsToReturn, ConnectionI connection, short loginPktTransactionId, FusionPktLoginOk loginOk) {
        if (SystemPropertyEntities.Temp.Cache.fi83Week3UseAutogeneratedPacketsEnabled.getValue().booleanValue()) {
            if (loginOk.getMsnDetail() == ImDetailType.CONNECTED) {
                packetsToReturn.add(new FusionPktImSessionStatus(loginPktTransactionId, ImType.MSN, FusionPktDataImSessionStatus.StatusType.LOGGED_IN, null));
            }
            if (loginOk.getYahooDetail() == ImDetailType.CONNECTED) {
                packetsToReturn.add(new FusionPktImSessionStatus(loginPktTransactionId, ImType.YAHOO, FusionPktDataImSessionStatus.StatusType.LOGGED_IN, null));
            }
        } else {
            if (loginOk.getMsnDetail() == ImDetailType.CONNECTED) {
                packetsToReturn.add(new FusionPktIMSessionStatusOld(loginPktTransactionId, ImType.MSN, 1, null));
            }
            if (loginOk.getYahooDetail() == ImDetailType.CONNECTED) {
                packetsToReturn.add(new FusionPktIMSessionStatusOld(loginPktTransactionId, ImType.YAHOO, 1, null));
            }
        }
    }

    @Override
    public Gateway.ThreadPoolName getThreadPool() {
        if (this.getResponseDelay() > 0L) {
            return Gateway.ThreadPoolName.DELAYED_LOGIN_RESPONSE_RESULT;
        }
        return super.getThreadPool();
    }
}

