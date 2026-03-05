/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONObject
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.ejb;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.cache.GiftsReceivedCounter;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.cache.RecentChatRoomList;
import com.projectgoth.fusion.clientsession.SSOLogin;
import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.ExceptionHelper;
import com.projectgoth.fusion.common.FloodControl;
import com.projectgoth.fusion.common.HashObjectUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.Numerics;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.WebCommon;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.AffiliateData;
import com.projectgoth.fusion.data.BankTransferIntentData;
import com.projectgoth.fusion.data.BasicMerchantTagDetailsData;
import com.projectgoth.fusion.data.BlueLabelVoucherData;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.data.ContentData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.CreditCardPaymentData;
import com.projectgoth.fusion.data.CreditTransferData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.DiscountTierData;
import com.projectgoth.fusion.data.EmoticonData;
import com.projectgoth.fusion.data.EmoticonPackData;
import com.projectgoth.fusion.data.ExternalDownloadLinkData;
import com.projectgoth.fusion.data.FileData;
import com.projectgoth.fusion.data.GroupAnnouncementData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupEventData;
import com.projectgoth.fusion.data.GroupInvitationData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.GroupModuleData;
import com.projectgoth.fusion.data.GroupPostData;
import com.projectgoth.fusion.data.HandsetData;
import com.projectgoth.fusion.data.HandsetVendorPrefixesData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.MobileOriginatedSMSData;
import com.projectgoth.fusion.data.MoneyTransferData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.ScrapbookData;
import com.projectgoth.fusion.data.StoreItemData;
import com.projectgoth.fusion.data.SubscriptionData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.data.ThemeData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserPostData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.UserRegistrationContextData;
import com.projectgoth.fusion.data.UserSettingData;
import com.projectgoth.fusion.data.VirtualGiftData;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.gateway.packet.FusionPacketFactory;
import com.projectgoth.fusion.gateway.packet.FusionPktRecharge;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.ContactLocal;
import com.projectgoth.fusion.interfaces.ContactLocalHome;
import com.projectgoth.fusion.interfaces.ContentLocal;
import com.projectgoth.fusion.interfaces.ContentLocalHome;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.VoiceLocal;
import com.projectgoth.fusion.interfaces.VoiceLocalHome;
import com.projectgoth.fusion.interfaces.VoucherLocal;
import com.projectgoth.fusion.interfaces.VoucherLocalHome;
import com.projectgoth.fusion.interfaces.WebLocal;
import com.projectgoth.fusion.interfaces.WebLocalHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.paintwars.ItemData;
import com.projectgoth.fusion.paintwars.Painter;
import com.projectgoth.fusion.paintwars.PainterStats;
import com.projectgoth.fusion.restapi.enums.RegistrationType;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.EventSystemPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.SMSUserNotification;
import com.projectgoth.fusion.slice.UserEventIce;
import com.projectgoth.fusion.slice.UserNotification;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.smsengine.SMSControl;
import com.projectgoth.fusion.userevent.EventTextTranslator;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sun.rowset.CachedRowSetImpl;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.constant.Constable;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WebBean
implements SessionBean {
    private static final Logger log = Logger.getLogger(WebBean.class);
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private static MemCachedClient recentChatRoomMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.recentChatRooms);
    private SessionContext context;

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
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Exception e) {
            log.error((Object)"Unable to create Web EJB", (Throwable)e);
            throw new CreateException("Unable to create Web EJB: " + e.getMessage());
        }
    }

    public Vector getCountries() {
        Vector countries = new Vector();
        List countrylist = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countrylist = misBean.getCountries();
            for (CountryData country : countrylist) {
                Hashtable<String, Object> countryHash = new Hashtable<String, Object>();
                countryHash.put("id", String.valueOf(country.id));
                countryHash.put("iddCode", String.valueOf(country.iddCode));
                if (country.isoCountryCode != null && country.isoCountryCode.length() > 0) {
                    countryHash.put("isoCountryCode", country.isoCountryCode);
                }
                countryHash.put("name", country.name);
                countryHash.put("currency", country.currency);
                countryHash.put("creditCardCurrency", country.creditCardCurrency);
                countryHash.put("bankTransferCurrency", country.bankTransferCurrency);
                countryHash.put("westernUnionCurrency", country.westernUnionCurrency);
                countryHash.put("allowCreditCard", country.allowCreditCard.value());
                countries.add(countryHash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return countries;
    }

    public Vector getCurrencies() {
        Vector currencies = new Vector();
        List currencyList = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            currencyList = misBean.getCurrencies();
            for (CurrencyData currency : currencyList) {
                Hashtable<String, String> currencyHash = new Hashtable<String, String>();
                currencyHash.put("code", currency.code);
                currencyHash.put("name", currency.name);
                currencyHash.put("exchangeRate", String.valueOf(currency.exchangeRate));
                currencies.add(currencyHash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return currencies;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getRateGrid() {
        Vector rateGrid;
        block28: {
            rateGrid = new Vector();
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select id, name, iddcode, callSignallingFee, mobileSignallingFee, mobileRate, callRate from country order by name");
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> gridHash = new Hashtable<String, Object>();
                gridHash.put("id", String.valueOf(rs.getInt("id")));
                gridHash.put("country", rs.getString("name"));
                gridHash.put("iddcode", rs.getInt("iddcode"));
                gridHash.put("callSignallingFee", String.valueOf(rs.getDouble("callSignallingFee")));
                gridHash.put("mobileSignallingFee", String.valueOf(rs.getDouble("mobileSignallingFee")));
                gridHash.put("mobileRate", String.valueOf(rs.getDouble("mobileRate")));
                gridHash.put("callRate", String.valueOf(rs.getDouble("callRate")));
                rateGrid.add(gridHash);
            }
            Object var8_7 = null;
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
                break block28;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block28;
            {
                catch (SQLException e) {
                    Vector vector = ExceptionHelper.getRootMessageAsVector(e);
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
                    return vector;
                }
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
        return rateGrid;
    }

    public Hashtable getHistoryEntry(long id, String type) {
        Hashtable entryDataHash = new Hashtable();
        try {
            if (type.equals("acct")) {
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                return HashObjectUtils.dataObjectToHashtable(accountBean.getAccountEntry(id));
            }
            if (type.equals("sms")) {
                MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                MessageData messageData = messageBean.getMessage((int)id);
                Hashtable hashEntry = HashObjectUtils.dataObjectToHashtable(messageData);
                if (messageData.messageDestinations.size() == 1) {
                    MessageDestinationData messageDestinationData = messageData.messageDestinations.get(0);
                    hashEntry.put("destination", messageDestinationData.destination);
                }
                return hashEntry;
            }
            if (type.equals("call")) {
                VoiceLocal voiceBean = (VoiceLocal)EJBHomeCache.getLocalObject("VoiceLocal", VoiceLocalHome.class);
                return HashObjectUtils.dataObjectToHashtable(voiceBean.getCallEntryWithCost((int)id));
            }
            if (type.equals("tt")) {
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                return HashObjectUtils.dataObjectToHashtable(accountBean.getMoneyTransferEntry((int)id));
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        return entryDataHash;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public String setEmailAlert(String username, boolean flag) {
        block38: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block33: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update user set emailalert = ? where username = ?");
                if (flag) {
                    ps.setInt(1, 1);
                } else {
                    ps.setInt(1, 0);
                }
                ps.setString(2, username);
                if (ps.executeUpdate() >= 1) break block33;
                String string = ExceptionHelper.setErrorMessage("Could not update emailalert: User does not exist");
                Object var9_8 = null;
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
                break block38;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block38;
            {
                catch (SQLException e) {
                    String string = ExceptionHelper.setErrorMessage("Could not update emailalert");
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
                    return string;
                }
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
        return "TRUE";
    }

    public String setEmailAlertSent(String username, boolean flag) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.setEmailAlertSent(username, flag);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String sendSMS(String fromUsername, String fromMobilePhone, String toMobilePhone, String messageText, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            MessageData message = new MessageData();
            message.username = fromUsername;
            message.dateCreated = new Date(System.currentTimeMillis());
            message.messageText = messageText;
            message.sendReceive = MessageData.SendReceiveEnum.SEND;
            message.source = fromMobilePhone;
            message.type = MessageType.SMS;
            MessageDestinationData messageDest = new MessageDestinationData();
            messageDest.type = MessageDestinationData.TypeEnum.INDIVIDUAL;
            messageDest.destination = messageBean.cleanAndValidatePhoneNumber(toMobilePhone, true);
            message.messageDestinations = new LinkedList<MessageDestinationData>();
            message.messageDestinations.add(messageDest);
            messageBean.sendSMS(message, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String sendEmail(String senderUsername, String senderPassword, String to, String subject, String content) {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.sendEmail(senderUsername, senderPassword, to, subject, content);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String sendEmailFromNoReply(String destinationAddress, String subject, String content) {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.sendEmailFromNoReply(destinationAddress, subject, content);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String makeCall(String[] keys, String[] values) {
        CallData callData = new CallData();
        try {
            VoiceLocal voiceBean = (VoiceLocal)EJBHomeCache.getLocalObject("VoiceLocal", VoiceLocalHome.class);
            HashObjectUtils.stringArrayToDataObject(keys, values, callData);
            String username = callData.username;
            if (StringUtil.isBlank(username)) {
                throw new Exception("Username cannot be empty");
            }
            UserPrx userProxy = EJBIcePrxFinder.findUserPrx(username);
            try {
                FloodControl.detectFlooding(username, userProxy, new FloodControl.Action[]{FloodControl.Action.PHONE_CALL.setMaxHits(SystemProperty.getLong("PhoneCallUserPerSecondRateLimit", 3L))});
            }
            catch (Exception e) {
                log.info((Object)("[" + username + "] user disconnected and suspended for 1 hour, exceeded 3/second rate limit. Destination[" + callData.destination + "] from source[" + callData.source + "]"));
                throw e;
            }
            if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PS", "DEST", callData.destination), SystemProperty.getLong("PhoneCallDestinationPerSecondRateLimit", 1L), 1000L) || !MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PH", "DEST", callData.destination), SystemProperty.getLong("PhoneCallDestinationPerHourRateLimit", 60L), 3600000L)) {
                if (SystemProperty.getBool("SuspendPhoneCallDestinationRateLimitOffender", false)) {
                    log.info((Object)(username + ", user disconnected and suspended for 1 hour. Destination Rate Limit exceeded. Destination [" + callData.destination + "] from source[" + callData.source + "]"));
                    if (userProxy != null) {
                        userProxy.disconnectFlooder("Flooding. Broke Phone Call Destination Rate Limit. Destination [" + callData.destination + "] from source[" + callData.source + "]");
                    }
                    throw new Exception("You have been disconnected.");
                }
                log.info((Object)(username + ", call dropped, exceeded rate limit to destination[" + callData.destination + "] from source[" + callData.source + "]"));
                throw new Exception("System busy. Please try again later.");
            }
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userBean.loadUser(username, false, false);
            if (!(callData.source == null || callData.source.equals(userData.mobilePhone) || MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PS", "SRC", callData.source), SystemProperty.getLong("PhoneCallSourcePerSecondRateLimit", 1L), 1000L) && MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.PHONECALL.toString(), MemCachedKeyUtils.getFullKeyFromStrings("PH", "SRC", callData.source), SystemProperty.getLong("PhoneCallSourcePerHourRateLimit", 60L), 3600000L))) {
                if (SystemProperty.getBool("SuspendPhoneCallSourceRateLimitOffender", false)) {
                    log.info((Object)("[" + username + "] user disconnected and suspended for 1 hour. Destination Rate Limit exceeded. Destination [" + callData.destination + "] from source[" + callData.source + "]"));
                    if (userProxy != null) {
                        userProxy.disconnectFlooder("Flooding. Broke Phone Call Destination Rate Limit. Destination [" + callData.destination + "] from source[" + callData.source + "]");
                    }
                    throw new Exception("You have been disconnected.");
                }
                log.info((Object)("[" + username + "] call dropped, exceeded rate limit for source[" + callData.source + "] from destination[" + callData.destination + "]"));
                throw new Exception("System busy. Please try again later.");
            }
            voiceBean.initiatePhoneCall(callData);
            return "TRUE";
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    public Hashtable evaluateCall(String[] keys, String[] values) {
        CallData callData = new CallData();
        Hashtable<String, String> callDetailHash = new Hashtable<String, String>();
        Double rate = 0.0;
        Double signallingfee = null;
        try {
            VoiceLocal voiceBean = (VoiceLocal)EJBHomeCache.getLocalObject("VoiceLocal", VoiceLocalHome.class);
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            HashObjectUtils.stringArrayToDataObject(keys, values, callData);
            CallData callDetailData = voiceBean.evaluatePhoneCall(callData);
            CurrencyData currencyData = accountBean.getUsersLocalCurrency(callData.username);
            rate = currencyData.convert(callDetailData.rate);
            signallingfee = currencyData.convert(callDetailData.signallingFee);
            String maxduration = WebCommon.toNiceDuration((long)callDetailData.maxCallDuration * 1000L);
            callDetailHash.put("signallingFee", String.valueOf(signallingfee));
            callDetailHash.put("rate", String.valueOf(rate));
            callDetailHash.put("maxDuration", maxduration);
            return callDetailHash;
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
    }

    /*
     * Loose catch block
     */
    public Vector getContactList(String username) throws EJBException {
        Vector<Hashtable> contactList;
        block22: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            contactList = new Vector<Hashtable>();
            conn = this.dataSourceSlave.getConnection();
            String sql = "select * from contact, user where contact.username = ? and contact.fusionusername = user.username and contact.status = ? ";
            sql = sql + "order by contact.displayname";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, ContactData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            while (rs.next()) {
                ContactData contact = new ContactData(rs);
                contactList.add(HashObjectUtils.dataObjectToHashtable(contact));
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
        return contactList;
    }

    /*
     * Loose catch block
     */
    public Vector getContactListEmailActivated(String username, boolean emailOnly) throws EJBException {
        Vector<Hashtable> contactList;
        block24: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            contactList = new Vector<Hashtable>();
            conn = this.dataSourceSlave.getConnection();
            String sql = "select * from contact, user where contact.username = ? and contact.fusionusername = user.username and contact.status = ? ";
            if (emailOnly) {
                sql = sql + "and user.emailactivated = ? ";
            }
            sql = sql + "order by contact.displayname";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, ContactData.StatusEnum.ACTIVE.value());
            if (emailOnly) {
                ps.setInt(3, 1);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                ContactData contact = new ContactData(rs);
                contactList.add(HashObjectUtils.dataObjectToHashtable(contact));
            }
            Object var10_10 = null;
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
                Object var10_11 = null;
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
        return contactList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getAccountEntries(String username, int page, int numEntries) {
        Connection connSlave = null;
        Statement ps = null;
        ResultSet rs = null;
        int maxAEPeriodBeforeArchival = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS);
        AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
        double balance = accountBean.getAccountBalance((String)username).balance;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select * from accountentry use index (fk_accountentry_1) where username = ? and amount != 0 and datecreated >= date_sub(curdate(), interval ? day) order by id desc limit ? offset ?");
        ps.setString(1, username);
        ps.setInt(2, maxAEPeriodBeforeArchival);
        ps.setInt(3, numEntries + 1);
        ps.setInt(4, page * numEntries);
        rs = ps.executeQuery();
        Vector accountEntries = new Vector();
        boolean hasMore = false;
        while (rs.next()) {
            if (rs.getRow() > numEntries) {
                hasMore = true;
                break;
            }
            Hashtable<String, Object> accountEntryDataHash = new Hashtable<String, Object>();
            SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
            accountEntryDataHash.put("id", rs.getLong("id"));
            accountEntryDataHash.put("username", rs.getString("username"));
            accountEntryDataHash.put("dateCreated", df.format(rs.getTimestamp("dateCreated")));
            accountEntryDataHash.put("type", AccountEntryData.TypeEnum.fromValue(rs.getInt("type")).toString());
            accountEntryDataHash.put("reference", rs.getString("reference"));
            accountEntryDataHash.put("description", rs.getString("description"));
            accountEntryDataHash.put("currency", rs.getString("currency"));
            accountEntryDataHash.put("exchangeRate", rs.getDouble("exchangeRate"));
            accountEntryDataHash.put("amount", rs.getDouble("amount"));
            accountEntryDataHash.put("tax", rs.getDouble("tax"));
            accountEntryDataHash.put("runningBalance", balance);
            accountEntries.add(accountEntryDataHash);
            balance -= rs.getDouble("amount");
        }
        Hashtable<String, Constable> markerHash = new Hashtable<String, Constable>();
        markerHash.put("page", Integer.valueOf(page));
        markerHash.put("hasMore", Boolean.valueOf(hasMore));
        accountEntries.add(0, markerHash);
        Vector vector = accountEntries;
        Object var16_16 = null;
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
        return vector;
        catch (Exception e) {
            Vector vector2;
            try {
                vector2 = ExceptionHelper.getRootMessageAsVector(e);
                Object var16_17 = null;
            }
            catch (Throwable throwable) {
                Object var16_18 = null;
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
            return vector2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getMoneyTransferEntries(String username, int page, int numEntries) {
        Vector moneyEntries;
        block33: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            moneyEntries = new Vector();
            int numRows = 0;
            int startEntry = page * numEntries + 1;
            int endEntry = startEntry + numEntries - 1;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select * from moneytransfer where username = ? order by DateCreated desc");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                ++numRows;
            }
            rs.beforeFirst();
            Hashtable<String, Integer> markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page);
            markerHash.put("numEntries", numRows - 1);
            if (numRows == 0) {
                markerHash.put("numPages", 0);
            }
            if (numRows > 0 && numRows / numEntries == 0) {
                markerHash.put("numPages", 1);
            } else {
                markerHash.put("numPages", numRows / numEntries);
            }
            moneyEntries.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            while (rs.next()) {
                if (rs.getRow() < startEntry) continue;
                Hashtable<String, Object> moneyEntryHash = new Hashtable<String, Object>();
                SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
                moneyEntryHash.put("id", rs.getInt("id"));
                moneyEntryHash.put("username", rs.getString("username"));
                moneyEntryHash.put("dateCreated", df.format(rs.getTimestamp("dateCreated")));
                moneyEntryHash.put("type", MoneyTransferData.TypeEnum.fromValue(rs.getInt("type")).toString());
                moneyEntryHash.put("receiptNumber", rs.getString("receiptNumber"));
                moneyEntryHash.put("fullName", rs.getString("fullName"));
                moneyEntryHash.put("amount", rs.getDouble("amount"));
                moneyEntries.add(moneyEntryHash);
                if (rs.getRow() < endEntry) continue;
                break;
            }
            Object var15_16 = null;
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
                break block33;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block33;
            {
                catch (SQLException e) {
                    Vector vector = ExceptionHelper.getRootMessageAsVector(e);
                    Object var15_17 = null;
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
                    return vector;
                }
            }
            catch (Throwable throwable) {
                Object var15_18 = null;
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
        return moneyEntries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getSMSHistory(String username, int page, int numEntries) {
        Vector smsEntries;
        block33: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            smsEntries = new Vector();
            int numRows = 0;
            int startEntry = page * numEntries + 1;
            int endEntry = startEntry + numEntries - 1;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select message.id, message.datecreated, message.messagetext, messagedestination.destination, messagedestination.status from message, messagedestination\twhere message.id = messagedestination.messageid and message.type = 2 and username = ? order by datecreated desc");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                ++numRows;
            }
            rs.beforeFirst();
            Hashtable<String, Integer> markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page);
            markerHash.put("numEntries", numRows);
            if (numRows == 0) {
                markerHash.put("numPages", 0);
            }
            if (numRows > 0 && numRows / numEntries == 0) {
                markerHash.put("numPages", 1);
            } else {
                markerHash.put("numPages", numRows / numEntries);
            }
            smsEntries.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            while (rs.next()) {
                if (rs.getRow() < startEntry) continue;
                Hashtable<String, Object> smsEntryHash = new Hashtable<String, Object>();
                SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
                smsEntryHash.put("id", rs.getInt("id"));
                smsEntryHash.put("dateCreated", df.format(rs.getTimestamp("dateCreated")));
                smsEntryHash.put("messageText", rs.getString("messageText"));
                smsEntryHash.put("destination", rs.getString("destination"));
                smsEntryHash.put("status", MessageDestinationData.StatusEnum.fromValue(rs.getInt("status")).toString());
                smsEntries.add(smsEntryHash);
                if (rs.getRow() < endEntry) continue;
                break;
            }
            Object var15_16 = null;
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
                break block33;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block33;
            {
                catch (SQLException e) {
                    Vector vector = ExceptionHelper.getRootMessageAsVector(e);
                    Object var15_17 = null;
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
                    return vector;
                }
            }
            catch (Throwable throwable) {
                Object var15_18 = null;
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
        return smsEntries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getCallHistory(String username, int page, int numEntries) {
        Vector callEntries;
        block33: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            callEntries = new Vector();
            int numRows = 0;
            int startEntry = page * numEntries + 1;
            int endEntry = startEntry + numEntries - 1;
            int maxAEPeriodBeforeArchival = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS);
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select p.id, p.datecreated, p.source, if(p.type = ?, u.username, p.destination) destination, p.billedduration, a.amount, a.currency from phonecall p inner join accountentry a on (p.username = a.username and p.id = a.reference and a.type = ?) left outer join user u on (p.destination = u.mobilephone) where p.status = ? and p.username = ? and a.datecreated >= date_sub(curdate(), interval ? day) order by p.datecreated desc");
            ps.setInt(1, CallData.TypeEnum.MIDLET_ANONYMOUS_CALLBACK.value());
            ps.setInt(2, AccountEntryData.TypeEnum.CALL_CHARGE.value());
            ps.setInt(3, CallData.StatusEnum.COMPLETED.value());
            ps.setString(4, username);
            ps.setInt(5, maxAEPeriodBeforeArchival);
            rs = ps.executeQuery();
            while (rs.next()) {
                ++numRows;
            }
            rs.beforeFirst();
            Hashtable<String, Integer> markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page);
            markerHash.put("numEntries", numRows);
            if (numRows == 0) {
                markerHash.put("numPages", 0);
            }
            if (numRows > 0 && numRows / numEntries == 0) {
                markerHash.put("numPages", 1);
            } else {
                markerHash.put("numPages", numRows / numEntries);
            }
            callEntries.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            while (rs.next()) {
                if (rs.getRow() < startEntry) continue;
                Hashtable<String, Object> callEntryHash = new Hashtable<String, Object>();
                SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
                callEntryHash.put("id", rs.getInt("id"));
                callEntryHash.put("dateCreated", df.format(rs.getTimestamp("dateCreated")));
                callEntryHash.put("source", rs.getString("source"));
                callEntryHash.put("destination", rs.getString("destination"));
                callEntryHash.put("billedDuration", rs.getDouble("billedDuration"));
                callEntryHash.put("amount", rs.getDouble("amount"));
                callEntryHash.put("currency", rs.getString("currency"));
                callEntries.add(callEntryHash);
                if (rs.getRow() < endEntry) continue;
                break;
            }
            Object var16_17 = null;
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
                break block33;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block33;
            {
                catch (SQLException e) {
                    Vector vector = ExceptionHelper.getRootMessageAsVector(e);
                    Object var16_18 = null;
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
                    return vector;
                }
            }
            catch (Throwable throwable) {
                Object var16_19 = null;
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
        return callEntries;
    }

    public Hashtable registerUser(String[] keys, String[] values, String[] profileKeys, String[] profileValues, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        return this.registerUser(keys, values, profileKeys, profileValues, null, ipAddress, sessionID, mobileDevice, userAgent);
    }

    public Hashtable registerUser(String[] keys, String[] values, String[] profileKeys, String[] profileValues, String referrerUsername, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        return this.registerUser(keys, values, profileKeys, profileValues, null, null, ipAddress, sessionID, mobileDevice, userAgent);
    }

    public Hashtable registerUser(String[] keys, String[] values, String[] profileKeys, String[] profileValues, String referrerUsername, String campaign, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        Hashtable userDataSOAP = new Hashtable();
        UserData userData = new UserData();
        UserProfileData userProfileData = new UserProfileData();
        HashObjectUtils.stringArrayToDataObject(keys, values, userData);
        HashObjectUtils.stringArrayToDataObject(profileKeys, profileValues, userProfileData);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            if (!StringUtil.isBlank(referrerUsername)) {
                try {
                    userBean.inviteFriend(referrerUsername, referrerUsername, userData.mobilePhone, null, null, null, null, null);
                }
                catch (FusionEJBException e) {
                    log.warn((Object)("Creating a user referral record failed with: " + e.getMessage()));
                }
            }
            RegistrationType registrationType = StringUtil.isBlank(userData.mobilePhone) ? RegistrationType.EMAIL_LEGACY : RegistrationType.MOBILE_REGISTRATION;
            userData = userBean.createUser(userData, userProfileData, true, new UserRegistrationContextData(campaign, false, registrationType), new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        if (userData == null) {
            return ExceptionHelper.setErrorMessageAsHashtable("The username entered does not exist.");
        }
        userDataSOAP = HashObjectUtils.dataObjectToHashtable(userData);
        return userDataSOAP;
    }

    public Hashtable registerUserMerchant(String[] keys, String[] values, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        Hashtable userDataSOAP = new Hashtable();
        UserData userData = new UserData();
        UserProfileData userProfileData = new UserProfileData();
        HashObjectUtils.stringArrayToDataObject(keys, values, userData);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userData = userBean.createUserMerchant(userData, userProfileData, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
        }
        if (userData == null) {
            return ExceptionHelper.setErrorMessageAsHashtable("The username entered does not exist.");
        }
        userDataSOAP = HashObjectUtils.dataObjectToHashtable(userData);
        return userDataSOAP;
    }

    public Hashtable loadUserDetails(String username) {
        Hashtable userDataSOAP = null;
        UserData userData = null;
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userData = userBean.loadUser(username, false, false);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        if (userData == null) {
            return ExceptionHelper.setErrorMessageAsHashtable("The username entered does not exist.");
        }
        userDataSOAP = HashObjectUtils.dataObjectToHashtable(userData);
        return userDataSOAP;
    }

    public Hashtable loadUserDetailsFromMobilePhone(String mobilePhone) {
        Hashtable userDataSOAP = null;
        UserData userData = null;
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userData = userBean.loadUserFromMobilePhone(mobilePhone);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        if (userData == null) {
            return ExceptionHelper.setErrorMessageAsHashtable("The username entered does not exist.");
        }
        userDataSOAP = HashObjectUtils.dataObjectToHashtable(userData);
        return userDataSOAP;
    }

    public Hashtable loadUserProfile(String requestingUsername, String targetUsername) {
        UserProfileData userProfileData = null;
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userProfileData = userBean.getUserProfile(requestingUsername, targetUsername, false);
        }
        catch (Exception e) {
            userProfileData = null;
        }
        if (userProfileData == null) {
            userProfileData = new UserProfileData();
            userProfileData.username = targetUsername;
            userProfileData.status = UserProfileData.StatusEnum.PRIVATE;
        }
        return HashObjectUtils.dataObjectToHashtable(userProfileData);
    }

    public Hashtable getAccountBalance(String username) {
        try {
            Hashtable balance = (Hashtable)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.ACCOUNT_BALANCE, username);
            if (balance == null) {
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                balance = HashObjectUtils.dataObjectToHashtable(accountBean.getAccountBalance(username));
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.ACCOUNT_BALANCE, username, balance);
            }
            return balance;
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
    }

    public String getMerchantTagFromUsername(String username) {
        try {
            String tag = (String)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.MERCHANT_TAG, username);
            if (tag == null) {
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                BasicMerchantTagDetailsData tagData = accountBean.getMerchantTagFromUsername(null, username, true);
                if (tagData != null) {
                    UserData merchantUserData = userBean.loadUserFromID(tagData.merchantUserID);
                    MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MERCHANT_TAG, username, merchantUserData.username);
                    tag = merchantUserData.username;
                }
            }
            return tag;
        }
        catch (Exception e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public Vector searchUserProfiles(String username, String countryIDstring, String minAgeString, String maxAgeString, String homeTown, String keyword, String keywordTypeString, int page, int numEntries, String showAvatar, String gender) {
        return ExceptionHelper.setErrorMessageAsVector("Profile searching has been disabled while we work on a much better system. Check back soon");
    }

    /*
     * Loose catch block
     */
    public Vector getUsersWhoViewed(String username) throws EJBException {
        Vector<String> userList;
        block22: {
            userList = new Vector<String>();
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select distinct usernameviewing from userprofileview where usernameviewed = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                userList.add(rs.getString(1));
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
        return userList;
    }

    public String updateUser(String[] detailKeys, String[] detailValues, String[] profileKeys, String[] profileValues) {
        UserData userData = new UserData();
        UserProfileData userProfileData = new UserProfileData();
        HashObjectUtils.stringArrayToDataObject(detailKeys, detailValues, userData);
        HashObjectUtils.stringArrayToDataObject(profileKeys, profileValues, userProfileData);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.updateUserDetail(userData);
            userBean.updateUserProfile(userProfileData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String updateUserDisplayPicture(String username, String displayPictureId) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.updateDisplayPicture(username, displayPictureId);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String updateUserDetails(String[] detailKeys, String[] detailValues) {
        UserData userData = new UserData();
        HashObjectUtils.stringArrayToDataObject(detailKeys, detailValues, userData);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.updateUserDetail(userData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String updateUserProfile(String[] profileKeys, String[] profileValues) {
        UserProfileData userProfileData = new UserProfileData();
        HashObjectUtils.stringArrayToDataObject(profileKeys, profileValues, userProfileData);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.updateUserProfile(userProfileData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String updateUserStatusMessage(int userID, String username, String statusMessage) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.updateStatusMessage(userID, username, statusMessage, ClientType.AJAX1, null);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String referFriendViaGame(String username, String displayName, String friendsNumber, String gameName, String hashKey, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData invitee = userBean.loadUserFromMobilePhone(friendsNumber);
            if (invitee != null) {
                ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
                AuthenticationServicePrx prx = EJBIcePrxFinder.getAuthenticationServiceProxy();
                int inviterUserID = prx.userIDForFusionUsername(username);
                ContactData contactData = new ContactData();
                contactData.username = username;
                contactData.fusionUsername = invitee.username;
                int showVisible = Math.min(4, invitee.username.length() / 2);
                contactData.displayName = StringUtil.maskString(invitee.username, showVisible, 'X');
                contactData.displayOnPhone = true;
                contactData.mobilePhone = friendsNumber;
                contactData = contactBean.addPendingFusionContact(inviterUserID, contactData);
                return "Congratulations! This person is already on migme and we have sent user " + contactData.displayName + " your friend request";
            }
            userBean.inviteFriend(username, displayName, friendsNumber, null, null, gameName, hashKey, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public String referFriend(String username, String displayName, String friendsNumber, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        return this.referFriendViaGame(username, displayName, friendsNumber, null, null, ipAddress, sessionID, mobileDevice, userAgent);
    }

    public Hashtable transferCredit(String fromUsername, String toUsername, String amountString, String pin, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        Hashtable accountEntryDataHash = new Hashtable();
        double amount = Double.valueOf(amountString);
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            CreditTransferData creditData = accountBean.transferCredit(fromUsername, toUsername, amount, false, pin, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            accountEntryDataHash = HashObjectUtils.dataObjectToHashtable(creditData.getAccountEntryData());
            accountEntryDataHash.put("balance", creditData.getAccountBalanceData().balance);
            accountEntryDataHash.put("balanceWithCode", creditData.getAccountBalanceData().formatWithCode());
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessageAsHashtable(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
        }
        return accountEntryDataHash;
    }

    public String cleanAndValidatePhoneNumber(String number) {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.cleanAndValidatePhoneNumber(number, false);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public Vector getScrapbook(String username, int page, int numEntries) {
        return this.getScrapbook(username, page, numEntries, false);
    }

    public Vector getScrapbook(String username, int page, int numEntries, boolean publishedOnly) {
        Vector<Hashtable> vec = new Vector<Hashtable>();
        List scrapbookList = null;
        int numRows = 0;
        int startEntry = --page * numEntries;
        int endEntry = startEntry + numEntries;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            scrapbookList = misBean.getScrapbook(username, publishedOnly);
            numRows = scrapbookList.size();
            Hashtable<String, Integer> markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page + 1);
            markerHash.put("numEntries", numRows);
            if (numRows == 0) {
                markerHash.put("numPages", 0);
            }
            if (numRows > 0 && numRows / numEntries == 0) {
                markerHash.put("numPages", 1);
            } else {
                double numRowsD = numRows;
                double numEntriesD = numEntries;
                markerHash.put("numPages", (int)Math.ceil(numRowsD / numEntriesD));
            }
            vec.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            for (int i = startEntry; i < endEntry; ++i) {
                ScrapbookData scrapbookData = (ScrapbookData)scrapbookList.get(i);
                Hashtable hash = HashObjectUtils.dataObjectToHashtable(scrapbookData);
                vec.add(hash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return vec;
    }

    public Vector getGallery(String requestingUsername, String targetUsername, int page, int numEntries) {
        Vector<Hashtable> vec = new Vector<Hashtable>();
        List scrapbookList = null;
        int numRows = 0;
        int startEntry = --page * numEntries;
        int endEntry = startEntry + numEntries;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            scrapbookList = misBean.getGallery(requestingUsername, targetUsername);
            numRows = scrapbookList.size();
            Hashtable<String, Integer> markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page + 1);
            markerHash.put("numEntries", numRows);
            if (numRows == 0) {
                markerHash.put("numPages", 0);
            }
            if (numRows > 0 && numRows / numEntries == 0) {
                markerHash.put("numPages", 1);
            } else {
                double numRowsD = numRows;
                double numEntriesD = numEntries;
                markerHash.put("numPages", (int)Math.ceil(numRowsD / numEntriesD));
            }
            vec.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            for (int i = startEntry; i < endEntry; ++i) {
                ScrapbookData scrapbookData = (ScrapbookData)scrapbookList.get(i);
                Hashtable hash = HashObjectUtils.dataObjectToHashtable(scrapbookData);
                vec.add(hash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return vec;
    }

    public boolean reportPhotoAbuse(String reporterUsername, String offenderUsername, int id) {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData reporterUserData = userEJB.loadUser(reporterUsername, false, false);
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            if (reporterUserData.chatRoomAdmin.booleanValue()) {
                misBean.deleteFileFromScrapbook(offenderUsername, id);
            } else {
                misBean.setFileReportedFromScrapbook(offenderUsername, id);
            }
        }
        catch (CreateException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getWall(String username, int page, int numEntries, boolean reportedOnly) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select scrapbook.id, scrapbook.datecreated, scrapbook.receivedfrom, scrapbook.description, scrapbook.fileid, file.width, file.height, country.name from scrapbook, file, user, country where scrapbook.username = ? and (scrapbook.status = ? or scrapbook.status = ?) and scrapbook.fileid = file.id and user.username = scrapbook.receivedfrom and country.id = user.countryid order by scrapbook.id desc limit ?, ?";
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setInt(2, reportedOnly ? ScrapbookData.StatusEnum.REPORTED.value() : ScrapbookData.StatusEnum.PUBLIC.value());
        ps.setInt(3, ScrapbookData.StatusEnum.REPORTED.value());
        ps.setInt(4, (page - 1) * numEntries);
        ps.setInt(5, numEntries);
        rs = ps.executeQuery();
        Vector wall = new Vector();
        while (rs.next()) {
            Hashtable<String, String> wallItem = new Hashtable<String, String>();
            String value = rs.getString("id");
            if (value != null) {
                wallItem.put("id", value);
            }
            if ((value = rs.getString("dateCreated")) != null) {
                wallItem.put("dateCreated", value);
            }
            if ((value = rs.getString("receivedFrom")) != null) {
                wallItem.put("receivedFrom", value);
            }
            if ((value = rs.getString("description")) != null) {
                wallItem.put("description", value);
            }
            if ((value = rs.getString("fileID")) != null) {
                wallItem.put("file.id", value);
            }
            if ((value = rs.getString("file.width")) != null) {
                wallItem.put("file.width", value);
            }
            if ((value = rs.getString("file.height")) != null) {
                wallItem.put("file.height", value);
            }
            if ((value = rs.getString("name")) != null) {
                wallItem.put("countryName", value);
            }
            wall.add(wallItem);
        }
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("page", Integer.valueOf(page));
        hash.put("wall", wall);
        Hashtable<String, Serializable> hashtable = hash;
        Object var13_14 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var13_15 = null;
            }
            catch (Throwable throwable) {
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return hashtable2;
        }
    }

    public Hashtable getPhoto(int id, String username, String viewusername) {
        ScrapbookData scrapbookData = null;
        int total = 0;
        int position = 0;
        int nextId = 0;
        int prevId = 0;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            List scrapbookList = username.equals(viewusername) ? misBean.getScrapbook(username) : misBean.getGallery(username, viewusername);
            total = scrapbookList.size();
            for (int i = 0; i < scrapbookList.size(); ++i) {
                ScrapbookData sc = (ScrapbookData)scrapbookList.get(i);
                if (sc.id != id) continue;
                if (!username.equals(viewusername)) {
                    if (sc.status == ScrapbookData.StatusEnum.PRIVATE) {
                        return null;
                    }
                    if (sc.status == ScrapbookData.StatusEnum.CONTACTS_ONLY && !this.isContactFriend(username, viewusername)) {
                        return null;
                    }
                }
                scrapbookData = sc;
                position = i + 1;
                if (i > 0) {
                    prevId = ((ScrapbookData)scrapbookList.get((int)(i - 1))).id;
                }
                if (i < scrapbookList.size() - 1) {
                    nextId = ((ScrapbookData)scrapbookList.get((int)(i + 1))).id;
                }
                break;
            }
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        if (scrapbookData == null) {
            throw new EJBException("Unable to find photo with id " + id);
        }
        Hashtable scrapbookDataHash = HashObjectUtils.dataObjectToHashtable(scrapbookData);
        scrapbookDataHash.put("total", total);
        scrapbookDataHash.put("page", position);
        scrapbookDataHash.put("nextId", nextId);
        scrapbookDataHash.put("prevId", prevId);
        return scrapbookDataHash;
    }

    public Hashtable getScrapbookEntry(int scrapbookID) {
        ScrapbookData scrapbookData = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            scrapbookData = misBean.getFileFromScrapbook(scrapbookID);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        Hashtable scrapbookDataHash = HashObjectUtils.dataObjectToHashtable(scrapbookData);
        return scrapbookDataHash;
    }

    public String updateScrapbookEntry(String[] scrapbookKeys, String[] scrapbookValues) {
        ScrapbookData scrapbookData = new ScrapbookData();
        HashObjectUtils.stringArrayToDataObject(scrapbookKeys, scrapbookValues, scrapbookData);
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.updateFileFromScrapbook(scrapbookData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        return "TRUE";
    }

    public Hashtable getFile(String fileID) {
        FileData fileData = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            fileData = misBean.getFile(fileID);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        Hashtable fileDataHash = HashObjectUtils.dataObjectToHashtable(fileData);
        return fileDataHash;
    }

    public String newFileID() {
        String fileID = "";
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            fileID = misBean.newFileID();
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return fileID;
    }

    public String saveFileToScrapbook(String[] keys, String[] values) {
        FileData fileData = new FileData();
        HashObjectUtils.stringArrayToDataObject(keys, values, fileData);
        fileData.dateCreated = new Date(System.currentTimeMillis());
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.saveFile(fileData, null);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String saveExistingFileToScrapbooks(String sender, String[] destinations, String fileID, String description) throws EJBException {
        block39: {
            ResultSet rs;
            Statement ps;
            Connection conn;
            block33: {
                conn = null;
                ps = null;
                rs = null;
                if (destinations.length != 1 || !"wall200712041".equalsIgnoreCase(destinations[0])) break block33;
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                ReputationLevelData levelData = userBean.getReputationLevel(sender);
                if (levelData.addToPhotoWall == null || !levelData.addToPhotoWall.booleanValue()) {
                    throw new EJBException("Invalid user reputation level for this function. User level: [" + levelData.level + "] level name: [" + levelData.name + "]");
                }
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select count(*) from scrapbook where username = ? and receivedfrom = ? and fileid = ? and scrapbook.status = ?");
                ps.setString(1, "wall200712041");
                ps.setString(2, sender);
                ps.setString(3, fileID);
                ps.setInt(4, ScrapbookData.StatusEnum.PUBLIC.value());
                rs = ps.executeQuery();
                if (!rs.next() || rs.getInt(1) <= 0) break block33;
                String string = "TRUE";
                Object var12_12 = null;
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
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.saveFileToScrapbooks(sender, destinations, fileID, description);
            Object var12_13 = null;
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
                break block39;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block39;
            {
                catch (Exception e) {
                    String string = ExceptionHelper.getRootMessage(e);
                    Object var12_14 = null;
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
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
        return "TRUE";
    }

    public String publishFileFromScrapbook(String username, int id, String description, boolean contactOnly) {
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.publishFileFromScrapbook(username, id, description, contactOnly);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public String unpublishFileFromScrapbook(String username, int id) {
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.unpublishFileFromScrapbook(username, id);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public String deleteFileFromScrapbook(String username, int id) {
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.deleteFileFromScrapbook(username, id);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public Hashtable getContact(int contactID) {
        ContactData returnContactData = null;
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            returnContactData = contactBean.getContact(contactID);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        return HashObjectUtils.dataObjectToHashtable(returnContactData);
    }

    public Hashtable getContact(String username, String contactName) {
        ContactData returnContactData = null;
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            returnContactData = contactBean.getContact(username, contactName);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        return HashObjectUtils.dataObjectToHashtable(returnContactData);
    }

    public Hashtable addContact(int userID, String[] keys, String[] values) {
        ContactData contactData = new ContactData();
        ContactData returnContactData = new ContactData();
        HashObjectUtils.stringArrayToDataObject(keys, values, contactData);
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            if (contactData.fusionUsername != null) {
                if (SystemProperty.getBool(SystemPropertyEntities.Contacts.FOLLOW_ON_ADD_CONTACT_ENABLED)) {
                    boolean followOnMiniblog = true;
                    returnContactData = contactBean.addFusionUserAsContact(userID, contactData, followOnMiniblog);
                } else {
                    contactData = contactBean.addPendingFusionContact(userID, contactData);
                }
            } else {
                returnContactData = contactBean.addPhoneContact(userID, contactData);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        return HashObjectUtils.dataObjectToHashtable(returnContactData);
    }

    public String updateContact(int userID, String[] keys, String[] values) {
        ContactData contactData = new ContactData();
        HashObjectUtils.stringArrayToDataObject(keys, values, contactData);
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            contactBean.updateContactDetail(userID, contactData);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public String blockContact(int userID, String username, String blockUsername) {
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            contactBean.blockContact(userID, username, blockUsername);
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
        return "TRUE";
    }

    public Hashtable getContactGroup(int groupID) {
        ContactGroupData returnContactGroupData = new ContactGroupData();
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            returnContactGroupData = contactBean.getGroup(groupID);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        return HashObjectUtils.dataObjectToHashtable(returnContactGroupData);
    }

    public Hashtable addGroup(int userID, String[] keys, String[] values) {
        ContactGroupData contactGroupData = new ContactGroupData();
        ContactGroupData returnContactGroupData = new ContactGroupData();
        HashObjectUtils.stringArrayToDataObject(keys, values, contactGroupData);
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            returnContactGroupData = contactBean.addGroup(userID, contactGroupData, true);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        return HashObjectUtils.dataObjectToHashtable(returnContactGroupData);
    }

    public String updateGroup(int userID, String[] keys, String[] values) {
        ContactGroupData contactGroupData = new ContactGroupData();
        HashObjectUtils.stringArrayToDataObject(keys, values, contactGroupData);
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            contactBean.updateGroupDetail(userID, contactGroupData);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public Hashtable creditCardPayment(String[] keys, String[] values, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        CreditCardPaymentData paymentData = new CreditCardPaymentData();
        CreditCardPaymentData returnPaymentData = new CreditCardPaymentData();
        HashObjectUtils.stringArrayToDataObject(keys, values, paymentData);
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            returnPaymentData = accountBean.creditCardPayment(paymentData, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        return HashObjectUtils.dataObjectToHashtable(returnPaymentData);
    }

    public String sendTTNotification(String[] keys, String[] values) {
        MoneyTransferData moneyTransferData = new MoneyTransferData();
        HashObjectUtils.stringArrayToDataObject(keys, values, moneyTransferData);
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.moneyTransferTopup(moneyTransferData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public Hashtable redeemVoucher(String username, String voucherNumber, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            VoucherData voucherData = voucherBean.redeemVoucher(username, voucherNumber, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return HashObjectUtils.dataObjectToHashtable(voucherData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessageAsHashtable(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
        }
    }

    public Hashtable redeemBlueLabelVoucher(String username, String voucherNumber, String voucherValue) {
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            BlueLabelVoucherData voucherData = voucherBean.redeemBlueLabelVoucher(username, voucherNumber, voucherValue);
            return HashObjectUtils.dataObjectToHashtable(voucherData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessageAsHashtable(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
        }
    }

    public Hashtable getAffiliateOverview(String username, String currency) {
        Hashtable<String, String> affiliateOverview = new Hashtable<String, String>();
        CachedRowSetImpl crs = null;
        int rsSize = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            crs = voucherBean.affiliateOverview(username);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        if (crs == null) {
            return ExceptionHelper.setErrorMessageAsHashtable("You have no voucher history available");
        }
        String totalNumberOfVouchers = null;
        String totalValueInVouchers = null;
        String totalNumberOfActiveVouchers = null;
        String totalValueOfActiveVouchers = null;
        String totalNumberOfCancelledVouchers = null;
        String totalValueOfCancelledVouchers = null;
        String totalNumberOfRedeemedVouchers = null;
        String totalValueOfRedeemedVouchers = null;
        try {
            if (crs.last()) {
                rsSize = crs.getRow();
            }
            if (rsSize > 0) {
                crs.beforeFirst();
                while (crs.next()) {
                    if (crs.getString("Totalvouchers") != null && Integer.parseInt(crs.getString("Totalvouchers")) >= 0) {
                        totalNumberOfVouchers = totalNumberOfVouchers == null ? crs.getString("Totalvouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "") : totalNumberOfVouchers + " | " + crs.getString("Totalvouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "");
                    }
                    if (crs.getString("amount") != null) {
                        totalValueInVouchers = totalValueInVouchers == null ? df.format(crs.getDouble("amount")) + " " + crs.getString("Currency") : totalValueInVouchers + " & " + df.format(crs.getDouble("amount")) + " " + crs.getString("currency");
                    }
                    if (crs.getString("activevouchers") != null && Integer.parseInt(crs.getString("activevouchers")) >= 0) {
                        totalNumberOfActiveVouchers = totalNumberOfActiveVouchers == null ? crs.getString("activevouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "") : totalNumberOfActiveVouchers + " | " + crs.getString("activevouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "");
                    }
                    if (crs.getString("activetotalamount") != null) {
                        totalValueOfActiveVouchers = totalValueOfActiveVouchers == null ? df.format(crs.getDouble("activetotalamount")) + " " + crs.getString("Currency") : totalValueOfActiveVouchers + " & " + df.format(crs.getDouble("activetotalamount")) + " " + crs.getString("currency");
                    }
                    if (crs.getString("cancelledvouchers") != null && Integer.parseInt(crs.getString("cancelledvouchers")) >= 0) {
                        totalNumberOfCancelledVouchers = totalNumberOfCancelledVouchers == null ? crs.getString("cancelledvouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "") : totalNumberOfCancelledVouchers + " | " + crs.getString("cancelledvouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "");
                    }
                    if (crs.getString("cancelledtotalamount") != null) {
                        totalValueOfCancelledVouchers = totalValueOfCancelledVouchers == null ? df.format(crs.getDouble("cancelledtotalamount")) + " " + crs.getString("Currency") : totalValueOfCancelledVouchers + " & " + df.format(crs.getDouble("cancelledtotalamount")) + " " + crs.getString("currency");
                    }
                    if (crs.getString("redeemedvouchers") != null && Integer.parseInt(crs.getString("redeemedvouchers")) >= 0) {
                        totalNumberOfRedeemedVouchers = totalNumberOfRedeemedVouchers == null ? crs.getString("redeemedvouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "") : totalNumberOfRedeemedVouchers + " | " + crs.getString("redeemedvouchers") + (rsSize > 1 ? " (with " + crs.getString("Currency") + ")" : "");
                    }
                    if (crs.getString("redeemedtotalamount") == null) continue;
                    if (totalValueOfRedeemedVouchers == null) {
                        totalValueOfRedeemedVouchers = df.format(crs.getDouble("redeemedtotalamount")) + " " + crs.getString("Currency");
                        continue;
                    }
                    totalValueOfRedeemedVouchers = totalValueOfRedeemedVouchers + " & " + df.format(crs.getDouble("redeemedtotalamount")) + " " + crs.getString("currency");
                }
            }
        }
        catch (SQLException e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        affiliateOverview.put("Total Number of Vouchers created", totalNumberOfVouchers != null ? totalNumberOfVouchers : "0.00 " + currency);
        affiliateOverview.put("Total Value in Vouchers created", totalValueInVouchers != null ? totalValueInVouchers : "0.00 " + currency);
        affiliateOverview.put("Total Number of Active Vouchers", totalNumberOfActiveVouchers != null ? totalNumberOfActiveVouchers : "0.00 " + currency);
        affiliateOverview.put("Total Value in Active Vouchers", totalValueOfActiveVouchers != null ? totalValueOfActiveVouchers : "0.00 " + currency);
        affiliateOverview.put("Total Number of Cancelled Vouchers", totalNumberOfCancelledVouchers != null ? totalNumberOfCancelledVouchers : "0.00 " + currency);
        affiliateOverview.put("Total Value in Cancelled Vouchers", totalValueOfCancelledVouchers != null ? totalValueOfCancelledVouchers : "0.00 " + currency);
        affiliateOverview.put("Total Number of Redeemed Vouchers", totalNumberOfRedeemedVouchers != null ? totalNumberOfRedeemedVouchers : "0.00 " + currency);
        affiliateOverview.put("Total Value in Redeemed Vouchers", totalValueOfRedeemedVouchers != null ? totalValueOfRedeemedVouchers : "0.00 " + currency);
        return affiliateOverview;
    }

    public Vector getAffiliateRecentActivities(String username, String currency) {
        Vector recentActivities = new Vector();
        CachedRowSetImpl crs = null;
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yy HH:mm");
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            crs = voucherBean.recentActivities(username, 10);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        if (crs == null) {
            return ExceptionHelper.setErrorMessageAsVector("There are no recent activities.");
        }
        try {
            while (crs.next()) {
                Hashtable<String, String> hash = new Hashtable<String, String>();
                hash.put("lastupdated", formatter.format(crs.getDate("lastupdated")));
                hash.put("number", crs.getString("number"));
                hash.put("status", crs.getInt("status") == 1 ? "Active" : (crs.getInt("status") == 2 ? "Cancelled" : (crs.getInt("status") == 3 ? "Redeemed" : (crs.getInt("status") == 4 ? "Expired" : (crs.getInt("status") == 0 ? "Inactive" : "Unknown")))));
                hash.put("notes", crs.getString("notes") != null ? crs.getString("notes") : "none");
                recentActivities.add(hash);
            }
        }
        catch (SQLException e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return recentActivities;
    }

    public Hashtable getAffiliateMoreStatistics(String username, String currency) {
        Hashtable<String, Integer> moreStatistics = new Hashtable<String, Integer>();
        int redeemNumberToday = 0;
        int redeemNumberLast7 = 0;
        int redeemNumberLast30 = 0;
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            redeemNumberToday = voucherBean.recentRedeem(username, 0);
            redeemNumberLast7 = voucherBean.recentRedeem(username, 7);
            redeemNumberLast30 = voucherBean.recentRedeem(username, 30);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        moreStatistics.put("Number of vouchers redeemed today", redeemNumberToday);
        moreStatistics.put("Number of vouchers redeemed last 7 days", redeemNumberLast7);
        moreStatistics.put("Number of vouchers redeemed last 30 days", redeemNumberLast30);
        return moreStatistics;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getVoucherBatches(String username, int id, int page, int numEntries) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        Statement ps = null;
        Vector voucherBatches = new Vector();
        int numRows = 0;
        int startEntry = page * numEntries + 1;
        int endEntry = startEntry + numEntries - 1;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select vb.datecreated,vb.id, vb.currency, vb.amount, vb.numvoucher,\tvb.notes, vb.expirydate, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 1) as active, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 2) as cancelled, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 3) as redeemed, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 4) as expired from voucherbatch vb\twhere vb.username = ? ";
        if (id != -1) {
            sql = sql + "and vb.id = ? ";
        }
        sql = sql + "order by vb.datecreated desc";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        if (id != -1) {
            ps.setInt(2, id);
        }
        rs = ps.executeQuery();
        while (rs.next()) {
            ++numRows;
        }
        rs.beforeFirst();
        Hashtable<String, Integer> markerHash = new Hashtable<String, Integer>();
        markerHash.put("page", page);
        markerHash.put("numEntries", numRows);
        markerHash.put("numPages", numRows / numEntries);
        voucherBatches.add(markerHash);
        if (endEntry > numRows) {
            endEntry = numRows;
        }
        while (rs.next()) {
            if (rs.getRow() < startEntry) continue;
            Hashtable<String, Object> voucherBatchHash = new Hashtable<String, Object>();
            SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
            voucherBatchHash.put("id", rs.getInt("id"));
            voucherBatchHash.put("dateCreated", df.format(rs.getTimestamp("dateCreated")));
            voucherBatchHash.put("currency", rs.getString("currency"));
            voucherBatchHash.put("amount", rs.getDouble("amount"));
            voucherBatchHash.put("numvoucher", rs.getInt("numvoucher"));
            if (rs.getTimestamp("expirydate") != null) {
                voucherBatchHash.put("expirydate", df.format(rs.getTimestamp("expirydate")));
            }
            if (rs.getString("notes") != null) {
                voucherBatchHash.put("notes", rs.getString("notes"));
            }
            voucherBatchHash.put("num_active", rs.getInt("active"));
            voucherBatchHash.put("num_cancelled", rs.getInt("cancelled"));
            voucherBatchHash.put("num_redeemed", rs.getInt("redeemed"));
            voucherBatchHash.put("num_expired", rs.getInt("expired"));
            voucherBatches.add(voucherBatchHash);
            if (rs.getRow() < endEntry) continue;
            break;
        }
        Vector vector = voucherBatches;
        Object var17_18 = null;
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
        return vector;
        catch (Exception e) {
            Vector vector2;
            try {
                vector2 = ExceptionHelper.getRootMessageAsVector(e);
                Object var17_19 = null;
            }
            catch (Throwable throwable) {
                Object var17_20 = null;
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
            return vector2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getAllVoucherBatches(String username) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector voucherBatches = new Vector();
        conn = this.dataSourceMaster.getConnection();
        String sql = "select vb.datecreated,vb.id, vb.currency, vb.amount, vb.numvoucher,\tvb.notes, vb.expirydate, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 1) as active, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 2) as cancelled, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 3) as redeemed, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 4) as expired, (select count(*) from voucher where voucher.voucherbatchid = vb.id and voucher.status = 0) as inactive from voucherbatch vb where vb.username = ? order by vb.id desc";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        rs = ps.executeQuery();
        while (rs.next()) {
            Hashtable<String, Object> voucherBatchHash = new Hashtable<String, Object>();
            SimpleDateFormat df = new SimpleDateFormat("d MMM yy");
            voucherBatchHash.put("id", rs.getInt("id"));
            voucherBatchHash.put("dateCreated", df.format(rs.getTimestamp("dateCreated")));
            voucherBatchHash.put("currency", rs.getString("currency"));
            voucherBatchHash.put("amount", rs.getDouble("amount"));
            voucherBatchHash.put("numvoucher", rs.getInt("numvoucher"));
            if (rs.getTimestamp("expirydate") != null) {
                voucherBatchHash.put("expirydate", df.format(rs.getTimestamp("expirydate")));
            }
            if (rs.getString("notes") != null) {
                voucherBatchHash.put("notes", rs.getString("notes"));
            }
            voucherBatchHash.put("num_active", rs.getInt("active"));
            voucherBatchHash.put("num_cancelled", rs.getInt("cancelled"));
            voucherBatchHash.put("num_redeemed", rs.getInt("redeemed"));
            voucherBatchHash.put("num_expired", rs.getInt("expired"));
            voucherBatchHash.put("num_inactive", rs.getInt("inactive"));
            voucherBatches.add(voucherBatchHash);
        }
        Vector vector = voucherBatches;
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
        return vector;
        catch (Exception e) {
            Vector vector2;
            try {
                vector2 = ExceptionHelper.getRootMessageAsVector(e);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
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
            return vector2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getVouchers(String username, int batchid, int type, int page, int numEntries, String sortCol, String sortDir) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Vector vouchers = new Vector();
        int numRows = 0;
        int startEntry = page * numEntries + 1;
        int endEntry = startEntry + numEntries - 1;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select amount, currency, numvoucher, notes, (select count(*) from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid = voucherbatch.id and voucher.voucherbatchid = ? and voucher.status = 1) as active, (select count(*) from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid = voucherbatch.id and voucher.voucherbatchid = ? and voucher.status = 2) as cancelled, (select count(*) from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid = voucherbatch.id and voucher.voucherbatchid = ? and voucher.status = 3) as redeemed, (select count(*) from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid = voucherbatch.id and voucher.voucherbatchid = ? and voucher.status = 4) as expired, (select count(*) from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid = voucherbatch.id and voucher.voucherbatchid = ? and voucher.status = 0) as inactive from voucherbatch where id=? and username=?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setInt(2, batchid);
        ps.setString(3, username);
        ps.setInt(4, batchid);
        ps.setString(5, username);
        ps.setInt(6, batchid);
        ps.setString(7, username);
        ps.setInt(8, batchid);
        ps.setString(9, username);
        ps.setInt(10, batchid);
        ps.setInt(11, batchid);
        ps.setString(12, username);
        rs = ps.executeQuery();
        Hashtable<String, Object> markerHash = new Hashtable<String, Object>();
        if (rs.first()) {
            markerHash.put("amount", rs.getDouble("amount"));
            markerHash.put("currency", rs.getString("currency"));
            markerHash.put("num_vouchers", rs.getInt("numvoucher"));
            markerHash.put("num_active", rs.getInt("active"));
            markerHash.put("num_cancelled", rs.getInt("cancelled"));
            markerHash.put("num_redeemed", rs.getInt("redeemed"));
            markerHash.put("num_expired", rs.getInt("expired"));
            markerHash.put("num_inactive", rs.getInt("inactive"));
            markerHash.put("notes", rs.getString("notes"));
        }
        rs.close();
        ps.close();
        sql = "select voucher.id, voucher.voucherbatchid, voucher.number, voucher.lastupdated, voucher.status, voucher.notes from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid =  voucherbatch.id and voucher.voucherbatchid = ? ";
        if (type != -1) {
            sql = sql + " and voucher.status = ? ";
        }
        sql = sortCol != null && sortCol.length() > 0 ? sql + "order by voucher." + sortCol : sql + "order by voucher.lastupdated";
        sql = sortDir != null && sortDir.length() > 0 ? sql + " " + sortDir : sql + " desc";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setInt(2, batchid);
        if (type != -1) {
            ps.setInt(3, type);
        }
        rs = ps.executeQuery();
        while (rs.next()) {
            ++numRows;
        }
        if (endEntry > numRows) {
            endEntry = numRows;
        }
        markerHash.put("page", page);
        markerHash.put("numEntries", endEntry - startEntry + 1);
        markerHash.put("totalEntries", numRows);
        markerHash.put("numPages", numRows / numEntries);
        vouchers.add(markerHash);
        rs.beforeFirst();
        while (rs.next()) {
            if (rs.getRow() < startEntry) continue;
            Hashtable<String, Object> voucherHash = new Hashtable<String, Object>();
            SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
            voucherHash.put("id", rs.getInt("id"));
            voucherHash.put("voucherbatchid", rs.getInt("voucherbatchid"));
            voucherHash.put("number", rs.getString("number"));
            if (rs.getTimestamp("lastupdated") != null) {
                voucherHash.put("lastupdated", df.format(rs.getTimestamp("lastupdated")));
            }
            voucherHash.put("status", VoucherData.StatusEnum.fromValue(rs.getInt("status")).toString());
            if (rs.getString("notes") != null) {
                voucherHash.put("notes", rs.getString("notes"));
            }
            vouchers.add(voucherHash);
            if (rs.getRow() < endEntry) continue;
            break;
        }
        Vector vector = vouchers;
        Object var20_21 = null;
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
        return vector;
        catch (Exception e) {
            Vector vector2;
            try {
                vector2 = ExceptionHelper.getRootMessageAsVector(e);
                Object var20_22 = null;
            }
            catch (Throwable throwable) {
                Object var20_23 = null;
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
            return vector2;
        }
    }

    public String exportVoucherBatchToCSV(String username, int exportType, int batchID) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yy HH:mm");
        String csvString = "";
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            csvString = "Voucher Batch ID,Voucher Number,Last Updated,Status,Notes\r\n";
            List vList = null;
            vList = voucherBean.retrieveVouchers(username, batchID, exportType);
            if (vList != null) {
                for (VoucherData voucher : vList) {
                    csvString = csvString + voucher.voucherBatchID + "," + voucher.number + "," + (voucher.lastUpdated == null ? "" : formatter.format(voucher.lastUpdated)) + "," + voucher.status.toString() + "," + (voucher.notes == null ? "" : voucher.notes) + "\r\n";
                }
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        return csvString;
    }

    public String createVoucherBatch(String username, String currency, String amount, int numVoucher, String notes, boolean initiallyInactive, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            int voucherBatchId = voucherBean.createVoucherBatch(username, currency, amount, numVoucher, notes, initiallyInactive, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return Integer.toString(voucherBatchId);
        }
        catch (CreateException e) {
            return "Internal server error";
        }
        catch (FusionEJBException e) {
            return ExceptionHelper.getRootMessage(e);
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
    }

    public Hashtable searchForVoucher(String username, String vouchernumber) throws EJBException {
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            VoucherData voucher = voucherBean.searchForVoucher(username, vouchernumber);
            if (voucher == null) {
                return ExceptionHelper.setErrorMessageAsHashtable("Voucher Not Found");
            }
            SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
            Hashtable<String, Object> voucherHashEntry = new Hashtable<String, Object>();
            voucherHashEntry.put("id", voucher.id);
            voucherHashEntry.put("voucherbatchid", voucher.voucherBatchID);
            voucherHashEntry.put("number", voucher.number);
            if (voucher.lastUpdated != null) {
                voucherHashEntry.put("lastupdated", df.format(voucher.lastUpdated));
            }
            if (voucher.notes != null) {
                voucherHashEntry.put("notes", voucher.notes);
            }
            voucherHashEntry.put("status", voucher.status.toString());
            return voucherHashEntry;
        }
        catch (Exception e) {
            return ExceptionHelper.setErrorMessageAsHashtable("Search failed: " + e.getMessage());
        }
    }

    public String cancelVoucher(String username, int voucherID, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            voucherBean.cancelVoucher(username, voucherID, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "TRUE";
        }
        catch (CreateException e) {
            return "Internal server error";
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
    }

    public String cancelVoucherBatch(String username, int voucherBatchID, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            VoucherLocal voucherBean = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            voucherBean.cancelVoucherBatch(username, voucherBatchID, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "TRUE";
        }
        catch (CreateException e) {
            return "Internal server error";
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public String changeActiveVoucherToInactive(String username, int voucherId) throws EJBException {
        block25: {
            PreparedStatement ps;
            Connection conn;
            block22: {
                conn = null;
                ps = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update voucher, voucherbatch set voucher.status = ?, voucher.LastUpdated = now() where voucher.ID = ? and voucher.status = ? and voucher.voucherbatchid=voucherbatch.id and voucherbatch.username = ?");
                ps.setInt(1, VoucherData.StatusEnum.INACTIVE.value());
                ps.setInt(2, voucherId);
                ps.setInt(3, VoucherData.StatusEnum.ACTIVE.value());
                ps.setString(4, username);
                int numRowsUpdated = ps.executeUpdate();
                if (numRowsUpdated >= 1) break block22;
                String string = ExceptionHelper.setErrorMessage("Unable to mark voucher as INACTIVE. Only vouchers you created that are currently ACTIVE may be marked as INACTIVE");
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
                }
                catch (SQLException e2) {
                    conn = null;
                }
                return string;
            }
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
                break block25;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block25;
            {
                catch (Exception e) {
                    String string = ExceptionHelper.setErrorMessage("Unable to mark voucher as INACTIVE: " + e.getMessage());
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
                    return string;
                }
            }
            catch (Throwable throwable) {
                Object var8_12 = null;
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
        return "TRUE";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public String changeInactiveVoucherToActive(String username, int voucherId) throws EJBException {
        block25: {
            PreparedStatement ps;
            Connection conn;
            block22: {
                conn = null;
                ps = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update voucher, voucherbatch set voucher.status = ?, voucher.LastUpdated = now() where voucher.ID = ? and voucher.status = ? and voucher.voucherbatchid=voucherbatch.id and voucherbatch.username = ?");
                ps.setInt(1, VoucherData.StatusEnum.ACTIVE.value());
                ps.setInt(2, voucherId);
                ps.setInt(3, VoucherData.StatusEnum.INACTIVE.value());
                ps.setString(4, username);
                int numRowsUpdated = ps.executeUpdate();
                if (numRowsUpdated >= 1) break block22;
                String string = ExceptionHelper.setErrorMessage("Unable to mark voucher as ACTIVE. Only vouchers you created that are currently INACTIVE may be marked as ACTIVE");
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
                }
                catch (SQLException e2) {
                    conn = null;
                }
                return string;
            }
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
                break block25;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block25;
            {
                catch (Exception e) {
                    String string = ExceptionHelper.setErrorMessage("Unable to mark voucher as ACTIVE: " + e.getMessage());
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
                    return string;
                }
            }
            catch (Throwable throwable) {
                Object var8_12 = null;
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
        return "TRUE";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String changeActiveVouchersInBatchToInactive(String username, int batchId) throws EJBException {
        block19: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update voucher, voucherbatch set voucher.status = ?, voucher.LastUpdated = now() where voucherbatch.ID = ? and voucher.status = ? and voucher.voucherbatchid=voucherbatch.id and voucherbatch.username = ?");
            ps.setInt(1, VoucherData.StatusEnum.INACTIVE.value());
            ps.setInt(2, batchId);
            ps.setInt(3, VoucherData.StatusEnum.ACTIVE.value());
            ps.setString(4, username);
            ps.executeUpdate();
            Object var8_5 = null;
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
                    String string = ExceptionHelper.setErrorMessage("Unable to change ACTIVE vouchers to INACTIVE: " + e.getMessage());
                    Object var8_6 = null;
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
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
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
        return "TRUE";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String changeInactiveVouchersInBatchToActive(String username, int batchId) throws EJBException {
        block19: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update voucher, voucherbatch set voucher.status = ?, voucher.LastUpdated = now() where voucherbatch.ID = ? and voucher.status = ? and voucher.voucherbatchid=voucherbatch.id and voucherbatch.username = ?");
            ps.setInt(1, VoucherData.StatusEnum.ACTIVE.value());
            ps.setInt(2, batchId);
            ps.setInt(3, VoucherData.StatusEnum.INACTIVE.value());
            ps.setString(4, username);
            ps.executeUpdate();
            Object var8_5 = null;
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
                    String string = ExceptionHelper.setErrorMessage("Unable to change INACTIVE vouchers to ACTIVE: " + e.getMessage());
                    Object var8_6 = null;
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
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
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
        return "TRUE";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public String updateVoucherBatchNotes(String username, int voucherBatchId, String notes) throws EJBException {
        block25: {
            PreparedStatement ps;
            Connection conn;
            block22: {
                conn = null;
                ps = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update voucherbatch set notes = ? where id = ? and username = ?");
                ps.setString(1, notes);
                ps.setInt(2, voucherBatchId);
                ps.setString(3, username);
                int numRowsUpdated = ps.executeUpdate();
                if (numRowsUpdated >= 1) break block22;
                String string = ExceptionHelper.setErrorMessage("Only voucher batches created by yourself may be updated");
                Object var9_10 = null;
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
                if (conn != null) {
                    conn.close();
                }
                break block25;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block25;
            {
                catch (Exception e) {
                    String string = ExceptionHelper.setErrorMessage("Unable to update voucher batch: " + e.getMessage());
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
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    return string;
                }
            }
            catch (Throwable throwable) {
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
        return "TRUE";
    }

    public int[] creditCardPaymentFromMidlet(int[] packet, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            byte[] ba = new byte[packet.length];
            for (int i = 0; i < packet.length; ++i) {
                ba[i] = (byte)packet[i];
            }
            FusionPacket fusionPkt = new FusionPacket();
            ByteArrayInputStream in = new ByteArrayInputStream(ba);
            fusionPkt.read(in);
            FusionRequest fusionRequest = FusionPacketFactory.getSpecificRequest(fusionPkt);
            if (fusionRequest == null) {
                return new int[0];
            }
            if (fusionRequest instanceof FusionPktRecharge) {
                FusionPktRecharge rechargePkt = (FusionPktRecharge)fusionRequest;
                FusionPacket[] returnPkts = rechargePkt.processRequest(new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
                byte[] returnContent = FusionPacket.toByteArray(returnPkts);
                int[] returnPacket = new int[returnContent.length];
                for (int i = 0; i < returnContent.length; ++i) {
                    returnPacket[i] = returnContent[i];
                }
                return returnPacket;
            }
            return new int[0];
        }
        catch (Exception e) {
            return new int[0];
        }
    }

    public boolean processSMSDeliveryReport(String providerTransactionID, String destination, int status, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        block4: {
            try {
                MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                SystemSMSData systemSMSData = messageEJB.getSystemSMS(providerTransactionID, destination);
                if (systemSMSData == null) {
                    return false;
                }
                if (systemSMSData.type != SystemSMSData.TypeEnum.PREMIUM || systemSMSData.gateway != SystemProperty.getInt("IndosatPremiumSMSGatewayID")) break block4;
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                List subscriptions = accountEJB.getSubscriptions(systemSMSData.username, SystemProperty.getInt("IndosatServiceID"));
                for (SubscriptionData subscriptionData : subscriptions) {
                    if (subscriptionData.status != SubscriptionData.StatusEnum.PENDING) continue;
                    accountEJB.updateSubscriptionBillingStatus(subscriptionData.username, subscriptionData.id, status == 2, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
                    break;
                }
            }
            catch (Exception e) {
                throw new EJBException(e.getMessage());
            }
        }
        return true;
    }

    public String processMobileOrignatedSMS(String receiver, String sender, String text, boolean isPrepaidNumber, String ipAddress) {
        String helpSMS = SystemProperty.get("MobileOriginatedSMSHelpText", "");
        String username = null;
        boolean mobileVerified = false;
        AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(WebBean.class);
        accountEntrySourceData.ipAddress = ipAddress;
        try {
            MobileOriginatedSMSData moSMSData = this.parseAndLogMobileOriginatedSMS(receiver, sender, text);
            if (sender.equals(SystemProperty.get("TwoWaySMSNumber"))) {
                return "TRUE";
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userEJB.loadUserFromMobilePhone(sender);
            if (userData != null) {
                username = userData.username;
                mobileVerified = userData.mobileVerified;
            }
            if (userData != null && !userData.mobileVerified.booleanValue() && userData.merchantCreated != null) {
                userEJB.activateAccount(userData.username, userData.verificationCode, false, accountEntrySourceData);
                mobileVerified = true;
            }
            if (moSMSData.requiresAuthenticatedAccount() && !mobileVerified) {
                log.info((Object)("Unable to process MO SMS. Invalid user. Sender: " + sender + ". Text: " + text + ". Username: " + username + ". Authenticated: " + mobileVerified));
                return "TRUE";
            }
            switch (moSMSData.type) {
                case BALANCE_REQUEST: {
                    this.sendBalanceSMS(userData.username, sender, accountEntrySourceData);
                    break;
                }
                case CALLBACK: {
                    this.processMobileOriginatedSMSCallback(username, userData.password, sender, text);
                    break;
                }
                case VOUCHER_REDEMPTION: {
                    this.processMobileOriginatedSMSVoucherRedemption(username, sender, text, accountEntrySourceData);
                    break;
                }
                case INDOSAT_URL_DOWNLOAD: {
                    this.sendIndosatURLDownloadSMS(username, sender, SystemProperty.get("IndosatURLDownloadSMS"), accountEntrySourceData);
                    break;
                }
                case INDOSAT_SUBSCRIPTION: {
                    this.processIndosatSubscription(username, sender, mobileVerified, isPrepaidNumber, accountEntrySourceData);
                    break;
                }
                case INDOSAT_CANCEL_SUBSCRIPTION: {
                    this.cancelIndosatSubscription(username, sender, accountEntrySourceData);
                    break;
                }
                default: {
                    if (moSMSData.text.length() != 0) break;
                    throw new Exception("No text in the message received");
                }
            }
            return "TRUE";
        }
        catch (CreateException e) {
            log.warn((Object)("Unable to process MO SMS. Sender: " + sender + ". Text: " + text), (Throwable)e);
            helpSMS = helpSMS.replaceAll("%1", "Server error");
        }
        catch (EJBException e) {
            log.warn((Object)("Unable to process MO SMS. Sender: " + sender + ". Text: " + text), (Throwable)e);
            helpSMS = helpSMS.replaceAll("%1", "Server error");
        }
        catch (Exception e) {
            log.warn((Object)("Unable to process MO SMS. Sender: " + sender + ". Text: " + text), (Throwable)e);
            helpSMS = helpSMS.replaceAll("%1", e.getMessage());
        }
        if (username != null && mobileVerified && helpSMS.length() > 0) {
            this.sendHelpSMS(username, sender, helpSMS, accountEntrySourceData);
        }
        return "FALSE";
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    private MobileOriginatedSMSData parseAndLogMobileOriginatedSMS(String receiver, String sender, String text) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        log.info((Object)("MO SMS Received. Receiver: " + receiver + " Sender: " + sender + " Text: " + text));
        MobileOriginatedSMSData moSMSData = new MobileOriginatedSMSData();
        moSMSData.dateCreated = new Date();
        moSMSData.receiver = receiver;
        moSMSData.sender = sender;
        moSMSData.text = text;
        String string = text = text == null ? "" : this.stripExcessChars(text).trim().toLowerCase();
        if (text.length() == 0 || text.equals("yes")) {
            moSMSData.type = MobileOriginatedSMSData.TypeEnum.UNKNOWN;
        }
        moSMSData.type = text.matches("^(v|voucher)\\s*[0-9]+") ? MobileOriginatedSMSData.TypeEnum.VOUCHER_REDEMPTION : ((text.startsWith("mi") || text.startsWith("ni")) && SystemProperty.get("IndosatShortCode").equals(receiver) ? MobileOriginatedSMSData.TypeEnum.INDOSAT_SUBSCRIPTION : ((text.startsWith("unreg") || text.startsWith("stop")) && SystemProperty.get("IndosatShortCode").equals(receiver) ? MobileOriginatedSMSData.TypeEnum.INDOSAT_CANCEL_SUBSCRIPTION : (text.equals("bal") || text.startsWith("balance") ? MobileOriginatedSMSData.TypeEnum.BALANCE_REQUEST : MobileOriginatedSMSData.TypeEnum.CALLBACK)));
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("insert into mobileoriginatedsms (datecreated, type, receiver, sender, text) values (?,?,?,?,?)", 1);
        ps.setTimestamp(1, new Timestamp(moSMSData.dateCreated.getTime()));
        ps.setInt(2, moSMSData.type.value());
        ps.setString(3, moSMSData.receiver);
        ps.setString(4, moSMSData.sender);
        ps.setString(5, moSMSData.text);
        ps.executeUpdate();
        rs = ps.getGeneratedKeys();
        if (!rs.next()) {
            throw new EJBException("Unable to write to MobileOriginatedSMS table");
        }
        moSMSData.id = rs.getInt(1);
        moSMSData.text = text;
        MobileOriginatedSMSData mobileOriginatedSMSData = moSMSData;
        Object var10_11 = null;
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
            return mobileOriginatedSMSData;
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            catch (NoSuchFieldException e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var10_12 = null;
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

    private void processMobileOriginatedSMSVoucherRedemption(String username, String sender, String text, AccountEntrySourceData accountEntrySourceData) {
        try {
            String voucherNumber = text.replaceAll("[^0-9]", "");
            if (username == null) {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userEJB.createPrepaidCardUser(sender, voucherNumber, accountEntrySourceData);
                userEJB.changeMobilePhone(userData.username, sender, accountEntrySourceData);
                username = userData.username;
                log.info((Object)("SMS voucher recharge success. Account " + userData.username + " created and voucher redeemed. Sender: " + sender + " Text: " + text));
            } else {
                VoucherLocal voucherEJB = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
                voucherEJB.redeemVoucher(username, voucherNumber, accountEntrySourceData);
                log.info((Object)("SMS voucher recharge success. Voucher redeemed. Sender: " + sender + " Text: " + text));
            }
            this.sendVoucherRechargeSMS(username, sender, voucherNumber, accountEntrySourceData);
        }
        catch (Exception e) {
            log.warn((Object)("SMS voucher recharge failed. Sender: " + sender + ". Text: " + text), (Throwable)e);
        }
    }

    private void processMobileOriginatedSMSCallback(String username, String password, String sender, String text) throws Exception {
        try {
            String destination;
            String origin;
            if (text.indexOf(42) == -1 && text.indexOf(35) == -1) {
                origin = sender;
                destination = text;
            } else {
                String[] parts = text.split("[\\*#]");
                if (parts.length < 2 || parts.length > 3) {
                    throw new Exception("Bad instruction");
                }
                if (!parts[0].equalsIgnoreCase(password)) {
                    throw new Exception("Bad password");
                }
                origin = parts.length == 3 ? parts[2] : sender;
                destination = parts[1];
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            try {
                destination = messageEJB.cleanAndValidatePhoneNumber(this.getNumberOnly(destination), false);
                origin = messageEJB.cleanAndValidatePhoneNumber(this.getNumberOnly(origin), false);
            }
            catch (Exception e) {
                throw new Exception("Bad number");
            }
            this.initiateCallback(username, origin, destination);
            log.info((Object)("SMS callback success. Sender: " + sender + " Text: " + text));
        }
        catch (EJBException e) {
            String exceptionMessage = ExceptionHelper.getRootMessage((Exception)((Object)e));
            if (exceptionMessage.indexOf("You do not have enough credit") != -1) {
                throw new Exception("Low balance");
            }
            if (exceptionMessage.indexOf("You need to authenticate") != -1) {
                throw new Exception("A/C inactive");
            }
            if (exceptionMessage.indexOf("Origin and destination") != -1) {
                throw new Exception("Same number");
            }
            throw e;
        }
    }

    private void sendHelpSMS(String username, String mobilePhone, String messageText, AccountEntrySourceData accountEntrySourceData) {
        if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.SMS_CALLBACK_HELP, username)) {
            return;
        }
        try {
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            int smsCount = messageEJB.getSystemSMSCount(SystemSMSData.SubTypeEnum.SMS_CALLBACK_HELP, mobilePhone);
            if (smsCount <= SystemProperty.getInt("MaxSMSCallbackHelpPerDay")) {
                SystemSMSData systemSMSData = new SystemSMSData();
                systemSMSData.username = username;
                systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                systemSMSData.subType = SystemSMSData.SubTypeEnum.SMS_CALLBACK_HELP;
                systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
                systemSMSData.destination = mobilePhone;
                systemSMSData.messageText = messageText;
                messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
            }
        }
        catch (Exception e) {
            System.out.println("SMS Callback Error 7: Unable to send help text to " + mobilePhone + ": " + e.getMessage());
        }
    }

    private void sendBalanceSMS(String username, String mobilePhone, AccountEntrySourceData accountEntrySourceData) {
        block4: {
            if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.SMS_CALLBACK_BALANCE, username)) {
                return;
            }
            try {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userEJB.loadUserFromMobilePhone(mobilePhone);
                if (userData == null) {
                    log.info((Object)("SMS callback failed. Unknown sender " + mobilePhone));
                    return;
                }
                DecimalFormat df = new DecimalFormat("0.00");
                String balance = df.format(userData.balance) + " " + userData.currency;
                SystemSMSData systemSMSData = new SystemSMSData();
                systemSMSData.username = username;
                systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
                systemSMSData.subType = SystemSMSData.SubTypeEnum.SMS_CALLBACK_BALANCE;
                systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
                systemSMSData.destination = mobilePhone;
                systemSMSData.messageText = "Your migme balance is " + balance;
                MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
                log.info((Object)("SMS callback success. Balance requested. Sender: " + mobilePhone));
            }
            catch (Exception e) {
                log.warn((Object)("SMS callback failed. Sender: " + mobilePhone), (Throwable)e);
                String helpSMS = SystemProperty.get("MobileOriginatedSMSHelpText", "");
                if (helpSMS.length() <= 0) break block4;
                this.sendHelpSMS(username, mobilePhone, helpSMS.replaceAll("%1", "Server error"), accountEntrySourceData);
            }
        }
    }

    private void sendVoucherRechargeSMS(String username, String mobilePhone, String voucherNumber, AccountEntrySourceData accountEntrySourceData) {
        if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.SMS_VOUCHER_RECHARGE, username)) {
            return;
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userEJB.loadUser(username, false, true);
            if (userData == null) {
                System.out.println("SMS Voucher Recharge Error: Unknown user " + username);
                return;
            }
            DecimalFormat df = new DecimalFormat("0.00");
            String balance = df.format(userData.balance) + " " + userData.currency;
            SystemSMSData systemSMSData = new SystemSMSData();
            systemSMSData.username = username;
            systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
            systemSMSData.subType = SystemSMSData.SubTypeEnum.SMS_VOUCHER_RECHARGE;
            systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
            systemSMSData.destination = mobilePhone;
            systemSMSData.messageText = "You redeemed voucher " + voucherNumber + ". Your migme balance is " + balance;
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
        }
        catch (Exception e) {
            String helpSMS = SystemProperty.get("MobileOriginatedSMSHelpText", "");
            if (helpSMS.length() > 0) {
                this.sendHelpSMS(username, mobilePhone, helpSMS.replaceAll("%1", "Server error"), accountEntrySourceData);
            }
            System.out.println("SMS Voucher Recharge Error: " + e.getMessage() + " Sender: " + mobilePhone);
        }
    }

    private void sendIndosatURLDownloadSMS(String username, String mobilePhone, String text, AccountEntrySourceData accountEntrySourceData) {
        if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.INDOSAT_URL_DOWNLOAD, username)) {
            return;
        }
        try {
            SystemSMSData systemSMSData = new SystemSMSData();
            systemSMSData.username = username;
            systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
            systemSMSData.subType = SystemSMSData.SubTypeEnum.INDOSAT_URL_DOWNLOAD;
            systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
            systemSMSData.destination = mobilePhone;
            systemSMSData.messageText = text;
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageEJB.sendSystemSMS(systemSMSData, accountEntrySourceData);
            log.info((Object)("Indosat URL download success. Sender: " + mobilePhone + ". Username: " + username));
        }
        catch (Exception e) {
            log.warn((Object)("Indosat URL download failed. Sender: " + mobilePhone + ". Username: " + username), (Throwable)e);
        }
    }

    private void processIndosatSubscription(String username, String mobilePhone, boolean mobileVerified, boolean isPrepaidNumber, AccountEntrySourceData accountEntrySourceData) {
        try {
            if (!isPrepaidNumber) {
                this.sendIndosatURLDownloadSMS(username, mobilePhone, "Mohon maaf, layanan ini hanya diperuntukkan untuk pelannggan Prepaid (IM3, Mentari)", accountEntrySourceData);
            } else if (mobileVerified) {
                int indosatServiceID = SystemProperty.getInt("IndosatServiceID");
                int indosatGroupID = SystemProperty.getInt("IndosatGroupID");
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                GroupMemberData groupMemberData = userEJB.getGroupMember(username, indosatGroupID);
                if (groupMemberData == null || groupMemberData.status == GroupMemberData.StatusEnum.INACTIVE) {
                    String[] indosatIPs = SystemProperty.getArray("IndosatIPs");
                    String indosatIPAddress = indosatIPs.length == 0 ? "" : indosatIPs[0];
                    this.joinGroup(username, indosatGroupID, 0, indosatIPAddress, accountEntrySourceData.sessionID, accountEntrySourceData.mobileDevice, accountEntrySourceData.userAgent, false, true, true, false, false, false);
                }
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                List subscriptions = accountEJB.getSubscriptions(username, indosatServiceID);
                SubscriptionData subscriptionData = null;
                for (SubscriptionData subscription : subscriptions) {
                    switch (subscription.status) {
                        case PENDING: {
                            this.sendHelpSMS(username, mobilePhone, "We are not able to subscribe you to Indosat VIP Access at this time. You already have a pending subscription", accountEntrySourceData);
                            return;
                        }
                        case ACTIVE: {
                            subscriptionData = subscription;
                            break;
                        }
                    }
                }
                if (subscriptionData == null) {
                    subscriptionData = accountEJB.subscribeService(username, indosatServiceID, accountEntrySourceData);
                }
                if (subscriptionData.type == SubscriptionData.TypeEnum.FREE_TRIAL) {
                    this.sendIndosatURLDownloadSMS(username, mobilePhone, "Terima kasih telah mendaftar 7 hari Indosat VIP Access. Klik ke http://m.mig.me/indosat/trial/ untuk keterangan lebih lanjut", accountEntrySourceData);
                } else {
                    this.sendIndosatURLDownloadSMS(username, mobilePhone, "Terimakasih telah berlangganan Indosat VIP Access. Sebentar lagi Anda akan menerima SMS untuk konfirmasi aktivasi akses 7 hari Anda", accountEntrySourceData);
                }
            } else {
                this.sendIndosatURLDownloadSMS(username, mobilePhone, "Selamat datang di Indosat VIP Access. Dapatkan GRATIS 7 hari pertama! Aktifkan sekarang di http://m.mig.me/indosat/vip/", accountEntrySourceData);
            }
        }
        catch (Exception e) {
            log.warn((Object)("Unable to process Indosat subscription for user " + username), (Throwable)e);
            this.sendHelpSMS(username, mobilePhone, "We are not able to subscribe you to Indosat VIP Access at this time. Server error", accountEntrySourceData);
        }
    }

    private void cancelIndosatSubscription(String username, String mobilePhone, AccountEntrySourceData accountEntrySourceData) {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            List subscriptions = accountEJB.getSubscriptions(username, SystemProperty.getInt("IndosatServiceID"));
            for (SubscriptionData subscription : subscriptions) {
                if (subscription.status != SubscriptionData.StatusEnum.ACTIVE) continue;
                accountEJB.cancelSubscription(username, subscription.id);
            }
            this.sendHelpSMS(username, mobilePhone, "Terima kasih. Anda tidak lagi berlangganan Indosat VIP Access di migme", accountEntrySourceData);
        }
        catch (Exception e) {
            log.warn((Object)("Unable to cancel Indosat subscription for user " + username), (Throwable)e);
            this.sendHelpSMS(username, mobilePhone, "We are not able to cancel your Indosat VIP Access at this time. Server error", accountEntrySourceData);
        }
    }

    private void initiateCallback(String username, String origin, String destination) throws CreateException, EJBException {
        VoiceLocal voiceEJB = (VoiceLocal)EJBHomeCache.getLocalObject("VoiceLocal", VoiceLocalHome.class);
        CallData callData = new CallData();
        callData.username = username;
        callData.source = origin;
        callData.destination = destination;
        callData.sourceType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.destinationType = CallData.SourceDestinationTypeEnum.PSTN_PHONE;
        callData.type = CallData.TypeEnum.SMS_CALLBACK;
        voiceEJB.initiatePhoneCall(callData);
    }

    private String stripExcessChars(String text) {
        char ch;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < text.length() && (ch = text.charAt(i)) != '\n' && ch != '\r'; ++i) {
            if (ch == '\"' || ch == '<' || ch == '>') continue;
            sb.append(ch);
        }
        return sb.toString();
    }

    private String getNumberOnly(String text) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch >= '0' && ch <= '9') {
                sb.append(ch);
                continue;
            }
            if (ch == '\n' || ch == '\r') break;
        }
        return sb.toString();
    }

    public Vector getHandsetVendors() throws EJBException {
        Vector vec = new Vector();
        List handsetVendors = new ArrayList();
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            handsetVendors = misBean.getHandsetVendors();
            for (int i = 0; i < handsetVendors.size(); ++i) {
                String vendor = (String)handsetVendors.get(i);
                Hashtable<String, String> hash = new Hashtable<String, String>();
                hash.put("vendor", vendor);
                vec.add(hash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return vec;
    }

    public Vector getHandsetVendorPrefixes() throws EJBException {
        Vector<Hashtable> vec = new Vector<Hashtable>();
        List handsetVendorPrefixList = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            handsetVendorPrefixList = misBean.getHandsetVendorPrefixes();
            for (int i = 0; i < handsetVendorPrefixList.size(); ++i) {
                HandsetVendorPrefixesData handsetVendorPrefixesData = (HandsetVendorPrefixesData)handsetVendorPrefixList.get(i);
                Hashtable hash = HashObjectUtils.dataObjectToHashtable(handsetVendorPrefixesData);
                vec.add(hash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return vec;
    }

    public Vector getHandsetDetails(String vendor) {
        Vector<Hashtable> vec = new Vector<Hashtable>();
        List handsetList = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            handsetList = misBean.getHandsetDetails(vendor);
            for (int i = 0; i < handsetList.size(); ++i) {
                HandsetData handsetData = (HandsetData)handsetList.get(i);
                Hashtable hash = HashObjectUtils.dataObjectToHashtable(handsetData);
                vec.add(hash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return vec;
    }

    public Vector getDefaultHandsetDetails() {
        Vector<Hashtable> vec = new Vector<Hashtable>();
        List handsetList = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            handsetList = misBean.getDefaultHandsetDetails();
            for (int i = 0; i < handsetList.size(); ++i) {
                HandsetData handsetData = (HandsetData)handsetList.get(i);
                Hashtable hash = HashObjectUtils.dataObjectToHashtableWithNulls(handsetData);
                vec.add(hash);
            }
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        return vec;
    }

    public String forgotPasswordWithMobileNumber(String mobile, String ipAddress, String mobileDevice, String userAgent) throws EJBException {
        log.info((Object)("forgotPasswordWithMobileNumber: IP [" + ipAddress + "] mobile no [" + mobile + "] device [" + mobileDevice + "] user agent [" + userAgent + "]"));
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.forgotPasswordWithMobileNumberOrEmail(mobile, false, new AccountEntrySourceData(ipAddress, null, mobileDevice, userAgent));
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        return "TRUE";
    }

    public String forgotPasswordWithEmail(String email, String ipAddress, String mobileDevice, String userAgent) throws EJBException {
        log.info((Object)("forgotPasswordWithEmail: IP [" + ipAddress + "] email [" + email + "] device [" + mobileDevice + "] user agent [" + userAgent + "]"));
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.forgotPasswordWithMobileNumberOrEmail(email, true, new AccountEntrySourceData(ipAddress, null, mobileDevice, userAgent));
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        return "TRUE";
    }

    public String changePassword(String username, String oldPassword, String newPassword) throws EJBException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.changePassword(username, oldPassword, newPassword);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        return "TRUE";
    }

    public Hashtable getCountryFromIPNumber(String ipNumber) {
        Hashtable countryDataSOAP = null;
        CountryData countryData = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countryData = misBean.getCountryFromIPNumber(Double.parseDouble(ipNumber));
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        if (countryData == null) {
            return null;
        }
        countryDataSOAP = HashObjectUtils.dataObjectToHashtable(countryData);
        return countryDataSOAP;
    }

    public boolean consoleOut(String message) {
        System.out.println("PHP: " + message);
        return true;
    }

    public String loginFailed(String username) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.loginFailed(username);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String loginSucceeded(String username) {
        return "TRUE";
    }

    public String loginSucceeded(String username, String mobileDevice, String userAgent, String language) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.loginSucceeded(username, mobileDevice, userAgent, language);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public String getBankTransferProductID(int countryID) {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            return String.valueOf(accountBean.getBankTransferProductID(countryID));
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public Hashtable bankTransfer(String username, int paymentProductID, int countryID, String surname, String fiscalNumber, float amount, String currency) {
        try {
            BankTransferIntentData bankTransferIntentData = new BankTransferIntentData();
            bankTransferIntentData.username = username;
            bankTransferIntentData.paymentProductID = paymentProductID;
            bankTransferIntentData.countryID = countryID == 0 ? null : Integer.valueOf(countryID);
            bankTransferIntentData.surname = surname;
            bankTransferIntentData.fiscalNumber = fiscalNumber;
            bankTransferIntentData.amount = amount;
            bankTransferIntentData.currency = currency;
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            bankTransferIntentData = accountBean.bankTransfer(bankTransferIntentData);
            return HashObjectUtils.dataObjectToHashtable(bankTransferIntentData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
    }

    public Hashtable bankTransfer(String username, int paymentProductID, int countryID, String firstname, String middlename, String surname, String fiscalNumber, float amount, String currency) {
        try {
            BankTransferIntentData bankTransferIntentData = new BankTransferIntentData();
            bankTransferIntentData.username = username;
            bankTransferIntentData.paymentProductID = paymentProductID;
            bankTransferIntentData.countryID = countryID == 0 ? null : Integer.valueOf(countryID);
            bankTransferIntentData.firstname = firstname;
            bankTransferIntentData.middlename = middlename;
            bankTransferIntentData.surname = surname;
            bankTransferIntentData.fiscalNumber = fiscalNumber;
            bankTransferIntentData.amount = amount;
            bankTransferIntentData.currency = currency;
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            bankTransferIntentData = accountBean.bankTransfer(bankTransferIntentData);
            return HashObjectUtils.dataObjectToHashtable(bankTransferIntentData);
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessageAsHashtable((Exception)((Object)e));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String registerMerchant(String[] keys, String[] values) {
        conn = null;
        ps = null;
        rs = null;
        affiliateData = new AffiliateData();
        userData = null;
        HashObjectUtils.stringArrayToDataObject(keys, values, affiliateData);
        registrationWithoutMobileEnabled = SystemProperty.getBool(SystemPropertyEntities.Default.MERCHANT_REGISTRATION_WITHOUT_MOBILE_ENABLED);
        try {
            block106: {
                block107: {
                    block105: {
                        block104: {
                            block102: {
                                block100: {
                                    block103: {
                                        block101: {
                                            block99: {
                                                block98: {
                                                    block97: {
                                                        block96: {
                                                            block95: {
                                                                block94: {
                                                                    if (affiliateData.username != null) break block94;
                                                                    var9_9 = ExceptionHelper.setErrorMessage("Please enter your migme Username");
                                                                    var13_16 = null;
                                                                    ** GOTO lbl156
                                                                }
                                                                if (affiliateData.firstName != null) break block95;
                                                                var9_10 = ExceptionHelper.setErrorMessage("Please enter a First Name");
                                                                ** GOTO lbl176
                                                            }
                                                            if (affiliateData.lastName != null) break block96;
                                                            var9_11 = ExceptionHelper.setErrorMessage("Please enter a Last Name");
                                                            ** GOTO lbl197
                                                        }
                                                        if (affiliateData.emailAddress != null) break block97;
                                                        var9_12 = ExceptionHelper.setErrorMessage("Please enter your Email Address");
                                                        ** GOTO lbl218
                                                    }
                                                    if (affiliateData.mobilePhone != null || registrationWithoutMobileEnabled.booleanValue()) break block98;
                                                    var9_13 = ExceptionHelper.setErrorMessage("Please enter your Mobile Phone");
                                                    ** GOTO lbl239
                                                }
                                                userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                                                userData = userBean.loadUser(affiliateData.username, false, true);
                                                if (userData != null) break block99;
                                                var10_50 = ExceptionHelper.setErrorMessage("You must be a migme user to register as a merchant");
                                                ** GOTO lbl260
                                            }
                                            if (!SystemProperty.getBool(SystemPropertyEntities.Temp.PT74760224_ENABLED)) break block100;
                                            if (!StringUtils.hasLength((String)affiliateData.password)) break block101;
                                            res = userBean.validateUserCredential(affiliateData.username, affiliateData.password, PasswordType.FUSION);
                                            if (res == AuthenticationServiceResponseCodeEnum.Success) break block102;
                                            var11_54 = ExceptionHelper.setErrorMessage("Please enter the correct password for your username");
                                            ** GOTO lbl281
                                        }
                                        loginData = SSOLogin.getLoginDataFromMemcache(affiliateData.sessionId);
                                        if (loginData != null) break block103;
                                        var11_55 = ExceptionHelper.setErrorMessage("Please login to migme first");
                                        ** GOTO lbl302
                                    }
                                    if (loginData.username.equals(affiliateData.username)) break block102;
                                    var11_56 = ExceptionHelper.setErrorMessage("Please login to migme first");
                                    ** GOTO lbl323
                                }
                                if (affiliateData.password.equals(userData.password)) break block102;
                                var10_52 = ExceptionHelper.setErrorMessage("Please enter the correct password for your username");
                                ** GOTO lbl344
                            }
                            if (userData.type == UserData.TypeEnum.MIG33) break block104;
                            var10_51 = ExceptionHelper.setErrorMessage("You cannot register as a merchant because you are already a merchant");
                            ** GOTO lbl365
                        }
                        if (SystemProperty.getBool(SystemPropertyEntities.Default.MERCHANT_REGISTRATION_NEW_USER_ENABLED) && affiliateData.fromUserRegistration != null && affiliateData.fromUserRegistration.equalsIgnoreCase("true") || AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.REGISTER_AS_MERCHANT, userData) || !SystemProperty.getBool("MerchantRegistrationDisabledForUnauthenticatedUsers", false)) break block105;
                        var10_51 = ExceptionHelper.setErrorMessage("Please login and authenticate your migme account before signing up for the merchant program.");
                        ** GOTO lbl386
                    }
                    if (registrationWithoutMobileEnabled.booleanValue() && affiliateData.registerWithoutMobile != null && affiliateData.registerWithoutMobile.equalsIgnoreCase("true")) break block106;
                    if (!StringUtil.isBlank(userData.mobilePhone)) break block107;
                    userBean.changeMobilePhone(userData.username, affiliateData.mobilePhone, true, new AccountEntrySourceData(affiliateData.registrationIpAddress, affiliateData.sessionId, affiliateData.mobileDevice, affiliateData.userAgent));
                    userData.mobilePhone = affiliateData.mobilePhone;
                    userBean.updateUserDetail(userData);
                    WebBean.log.info((Object)String.format("Updated mobile phone of user %s to %s as part of the merchant registration process", new Object[]{affiliateData.username, affiliateData.mobilePhone}));
                    break block106;
                }
                if (userData.mobilePhone.equals(affiliateData.mobilePhone)) break block106;
                var10_51 = ExceptionHelper.setErrorMessage("Please enter your correct Mobile Phone");
                ** GOTO lbl407
            }
            userData.type = UserData.TypeEnum.MIG33_MERCHANT;
            userBean.updateUserDetail(userData);
            affiliateData.dateRegistered = new Date();
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("insert into affiliate (username, emailaddress, firstname, lastname, additionalinfo, countryIdDetected, registrationIpAddress, dateRegistered)  values (?,?,?,?,?,?,?,?)");
            ps.setString(1, affiliateData.username);
            ps.setString(2, affiliateData.emailAddress);
            ps.setString(3, affiliateData.firstName);
            ps.setString(4, affiliateData.lastName);
            ps.setString(5, affiliateData.additionalInfo);
            ps.setObject(6, affiliateData.countryIdDetected);
            ps.setString(7, affiliateData.registrationIpAddress);
            ps.setTimestamp(8, new Timestamp(affiliateData.dateRegistered.getTime()));
            if (ps.executeUpdate() >= 1) ** GOTO lbl449
            var10_51 = ExceptionHelper.setErrorMessage("There was an error while entering your details");
            ** GOTO lbl428
        }
        catch (Exception e) {
            var10_53 = ExceptionHelper.getRootMessage(e);
            var13_31 = null;
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
                if (conn == null) return var10_53;
                conn.close();
                return var10_53;
            }
            catch (SQLException e) {
                return var10_53;
            }
        }
        {
            block139: {
                block138: {
                    block137: {
                        block136: {
                            block135: {
                                block134: {
                                    block133: {
                                        block132: {
                                            block131: {
                                                block130: {
                                                    block129: {
                                                        block128: {
                                                            block127: {
                                                                block126: {
                                                                    block125: {
                                                                        block124: {
                                                                            block123: {
                                                                                block122: {
                                                                                    block121: {
                                                                                        block120: {
                                                                                            block119: {
                                                                                                block118: {
                                                                                                    block117: {
                                                                                                        block116: {
                                                                                                            block115: {
                                                                                                                block114: {
                                                                                                                    block113: {
                                                                                                                        block112: {
                                                                                                                            block111: {
                                                                                                                                block110: {
                                                                                                                                    catch (Throwable var12_57) {
                                                                                                                                        block109: {
                                                                                                                                            block108: {
                                                                                                                                                var13_32 = null;
                                                                                                                                                ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl136:
                                                                                                                                                // 1 sources

                                                                                                                                                if (rs != null) {
                                                                                                                                                    rs.close();
                                                                                                                                                }
                                                                                                                                                break block108;
lbl139:
                                                                                                                                                // 1 sources

                                                                                                                                                catch (SQLException e) {
                                                                                                                                                    rs = null;
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                            ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl143:
                                                                                                                                            // 1 sources

                                                                                                                                            if (ps != null) {
                                                                                                                                                ps.close();
                                                                                                                                            }
                                                                                                                                            break block109;
lbl146:
                                                                                                                                            // 1 sources

                                                                                                                                            catch (SQLException e) {
                                                                                                                                                ps = null;
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                        ** try [egrp 4[TRYBLOCK] [33 : 888->901)] { 
lbl150:
                                                                                                                                        // 1 sources

                                                                                                                                        if (conn == null) throw var12_57;
                                                                                                                                        conn.close();
                                                                                                                                        throw var12_57;
lbl153:
                                                                                                                                        // 1 sources

                                                                                                                                        catch (SQLException e) {
                                                                                                                                            conn = null;
                                                                                                                                        }
                                                                                                                                        throw var12_57;
                                                                                                                                    }
lbl156:
                                                                                                                                    // 1 sources

                                                                                                                                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl157:
                                                                                                                                    // 1 sources

                                                                                                                                    if (rs != null) {
                                                                                                                                        rs.close();
                                                                                                                                    }
                                                                                                                                    break block110;
lbl160:
                                                                                                                                    // 1 sources

                                                                                                                                    catch (SQLException e) {
                                                                                                                                        rs = null;
                                                                                                                                    }
                                                                                                                                }
                                                                                                                                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl164:
                                                                                                                                // 1 sources

                                                                                                                                if (ps != null) {
                                                                                                                                    ps.close();
                                                                                                                                }
                                                                                                                                break block111;
lbl167:
                                                                                                                                // 1 sources

                                                                                                                                catch (SQLException e) {
                                                                                                                                    ps = null;
                                                                                                                                }
                                                                                                                            }
                                                                                                                            try {}
                                                                                                                            catch (SQLException e) {
                                                                                                                                return var9_9;
                                                                                                                            }
                                                                                                                            if (conn == null) return var9_9;
                                                                                                                            conn.close();
                                                                                                                            return var9_9;
lbl176:
                                                                                                                            // 1 sources

                                                                                                                            var13_17 = null;
                                                                                                                            ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl178:
                                                                                                                            // 1 sources

                                                                                                                            if (rs != null) {
                                                                                                                                rs.close();
                                                                                                                            }
                                                                                                                            break block112;
lbl181:
                                                                                                                            // 1 sources

                                                                                                                            catch (SQLException e) {
                                                                                                                                rs = null;
                                                                                                                            }
                                                                                                                        }
                                                                                                                        ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl185:
                                                                                                                        // 1 sources

                                                                                                                        if (ps != null) {
                                                                                                                            ps.close();
                                                                                                                        }
                                                                                                                        break block113;
lbl188:
                                                                                                                        // 1 sources

                                                                                                                        catch (SQLException e) {
                                                                                                                            ps = null;
                                                                                                                        }
                                                                                                                    }
                                                                                                                    try {}
                                                                                                                    catch (SQLException e) {
                                                                                                                        return var9_10;
                                                                                                                    }
                                                                                                                    if (conn == null) return var9_10;
                                                                                                                    conn.close();
                                                                                                                    return var9_10;
lbl197:
                                                                                                                    // 1 sources

                                                                                                                    var13_18 = null;
                                                                                                                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl199:
                                                                                                                    // 1 sources

                                                                                                                    if (rs != null) {
                                                                                                                        rs.close();
                                                                                                                    }
                                                                                                                    break block114;
lbl202:
                                                                                                                    // 1 sources

                                                                                                                    catch (SQLException e) {
                                                                                                                        rs = null;
                                                                                                                    }
                                                                                                                }
                                                                                                                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl206:
                                                                                                                // 1 sources

                                                                                                                if (ps != null) {
                                                                                                                    ps.close();
                                                                                                                }
                                                                                                                break block115;
lbl209:
                                                                                                                // 1 sources

                                                                                                                catch (SQLException e) {
                                                                                                                    ps = null;
                                                                                                                }
                                                                                                            }
                                                                                                            try {}
                                                                                                            catch (SQLException e) {
                                                                                                                return var9_11;
                                                                                                            }
                                                                                                            if (conn == null) return var9_11;
                                                                                                            conn.close();
                                                                                                            return var9_11;
lbl218:
                                                                                                            // 1 sources

                                                                                                            var13_19 = null;
                                                                                                            ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl220:
                                                                                                            // 1 sources

                                                                                                            if (rs != null) {
                                                                                                                rs.close();
                                                                                                            }
                                                                                                            break block116;
lbl223:
                                                                                                            // 1 sources

                                                                                                            catch (SQLException e) {
                                                                                                                rs = null;
                                                                                                            }
                                                                                                        }
                                                                                                        ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl227:
                                                                                                        // 1 sources

                                                                                                        if (ps != null) {
                                                                                                            ps.close();
                                                                                                        }
                                                                                                        break block117;
lbl230:
                                                                                                        // 1 sources

                                                                                                        catch (SQLException e) {
                                                                                                            ps = null;
                                                                                                        }
                                                                                                    }
                                                                                                    try {}
                                                                                                    catch (SQLException e) {
                                                                                                        return var9_12;
                                                                                                    }
                                                                                                    if (conn == null) return var9_12;
                                                                                                    conn.close();
                                                                                                    return var9_12;
lbl239:
                                                                                                    // 1 sources

                                                                                                    var13_20 = null;
                                                                                                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl241:
                                                                                                    // 1 sources

                                                                                                    if (rs != null) {
                                                                                                        rs.close();
                                                                                                    }
                                                                                                    break block118;
lbl244:
                                                                                                    // 1 sources

                                                                                                    catch (SQLException e) {
                                                                                                        rs = null;
                                                                                                    }
                                                                                                }
                                                                                                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl248:
                                                                                                // 1 sources

                                                                                                if (ps != null) {
                                                                                                    ps.close();
                                                                                                }
                                                                                                break block119;
lbl251:
                                                                                                // 1 sources

                                                                                                catch (SQLException e) {
                                                                                                    ps = null;
                                                                                                }
                                                                                            }
                                                                                            try {}
                                                                                            catch (SQLException e) {
                                                                                                return var9_13;
                                                                                            }
                                                                                            if (conn == null) return var9_13;
                                                                                            conn.close();
                                                                                            return var9_13;
lbl260:
                                                                                            // 1 sources

                                                                                            var13_21 = null;
                                                                                            ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl262:
                                                                                            // 1 sources

                                                                                            if (rs != null) {
                                                                                                rs.close();
                                                                                            }
                                                                                            break block120;
lbl265:
                                                                                            // 1 sources

                                                                                            catch (SQLException e) {
                                                                                                rs = null;
                                                                                            }
                                                                                        }
                                                                                        ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl269:
                                                                                        // 1 sources

                                                                                        if (ps != null) {
                                                                                            ps.close();
                                                                                        }
                                                                                        break block121;
lbl272:
                                                                                        // 1 sources

                                                                                        catch (SQLException e) {
                                                                                            ps = null;
                                                                                        }
                                                                                    }
                                                                                    try {}
                                                                                    catch (SQLException e) {
                                                                                        return var10_50;
                                                                                    }
                                                                                    if (conn == null) return var10_50;
                                                                                    conn.close();
                                                                                    return var10_50;
lbl281:
                                                                                    // 1 sources

                                                                                    var13_22 = null;
                                                                                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl283:
                                                                                    // 1 sources

                                                                                    if (rs != null) {
                                                                                        rs.close();
                                                                                    }
                                                                                    break block122;
lbl286:
                                                                                    // 1 sources

                                                                                    catch (SQLException e) {
                                                                                        rs = null;
                                                                                    }
                                                                                }
                                                                                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl290:
                                                                                // 1 sources

                                                                                if (ps != null) {
                                                                                    ps.close();
                                                                                }
                                                                                break block123;
lbl293:
                                                                                // 1 sources

                                                                                catch (SQLException e) {
                                                                                    ps = null;
                                                                                }
                                                                            }
                                                                            try {}
                                                                            catch (SQLException e) {
                                                                                return var11_54;
                                                                            }
                                                                            if (conn == null) return var11_54;
                                                                            conn.close();
                                                                            return var11_54;
lbl302:
                                                                            // 1 sources

                                                                            var13_23 = null;
                                                                            ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl304:
                                                                            // 1 sources

                                                                            if (rs != null) {
                                                                                rs.close();
                                                                            }
                                                                            break block124;
lbl307:
                                                                            // 1 sources

                                                                            catch (SQLException e) {
                                                                                rs = null;
                                                                            }
                                                                        }
                                                                        ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl311:
                                                                        // 1 sources

                                                                        if (ps != null) {
                                                                            ps.close();
                                                                        }
                                                                        break block125;
lbl314:
                                                                        // 1 sources

                                                                        catch (SQLException e) {
                                                                            ps = null;
                                                                        }
                                                                    }
                                                                    try {}
                                                                    catch (SQLException e) {
                                                                        return var11_55;
                                                                    }
                                                                    if (conn == null) return var11_55;
                                                                    conn.close();
                                                                    return var11_55;
lbl323:
                                                                    // 1 sources

                                                                    var13_24 = null;
                                                                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl325:
                                                                    // 1 sources

                                                                    if (rs != null) {
                                                                        rs.close();
                                                                    }
                                                                    break block126;
lbl328:
                                                                    // 1 sources

                                                                    catch (SQLException e) {
                                                                        rs = null;
                                                                    }
                                                                }
                                                                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl332:
                                                                // 1 sources

                                                                if (ps != null) {
                                                                    ps.close();
                                                                }
                                                                break block127;
lbl335:
                                                                // 1 sources

                                                                catch (SQLException e) {
                                                                    ps = null;
                                                                }
                                                            }
                                                            try {}
                                                            catch (SQLException e) {
                                                                return var11_56;
                                                            }
                                                            if (conn == null) return var11_56;
                                                            conn.close();
                                                            return var11_56;
lbl344:
                                                            // 1 sources

                                                            var13_25 = null;
                                                            ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl346:
                                                            // 1 sources

                                                            if (rs != null) {
                                                                rs.close();
                                                            }
                                                            break block128;
lbl349:
                                                            // 1 sources

                                                            catch (SQLException e) {
                                                                rs = null;
                                                            }
                                                        }
                                                        ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl353:
                                                        // 1 sources

                                                        if (ps != null) {
                                                            ps.close();
                                                        }
                                                        break block129;
lbl356:
                                                        // 1 sources

                                                        catch (SQLException e) {
                                                            ps = null;
                                                        }
                                                    }
                                                    try {}
                                                    catch (SQLException e) {
                                                        return var10_52;
                                                    }
                                                    if (conn == null) return var10_52;
                                                    conn.close();
                                                    return var10_52;
lbl365:
                                                    // 1 sources

                                                    var13_26 = null;
                                                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl367:
                                                    // 1 sources

                                                    if (rs != null) {
                                                        rs.close();
                                                    }
                                                    break block130;
lbl370:
                                                    // 1 sources

                                                    catch (SQLException e) {
                                                        rs = null;
                                                    }
                                                }
                                                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl374:
                                                // 1 sources

                                                if (ps != null) {
                                                    ps.close();
                                                }
                                                break block131;
lbl377:
                                                // 1 sources

                                                catch (SQLException e) {
                                                    ps = null;
                                                }
                                            }
                                            try {}
                                            catch (SQLException e) {
                                                return var10_51;
                                            }
                                            if (conn == null) return var10_51;
                                            conn.close();
                                            return var10_51;
lbl386:
                                            // 1 sources

                                            var13_27 = null;
                                            ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl388:
                                            // 1 sources

                                            if (rs != null) {
                                                rs.close();
                                            }
                                            break block132;
lbl391:
                                            // 1 sources

                                            catch (SQLException e) {
                                                rs = null;
                                            }
                                        }
                                        ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl395:
                                        // 1 sources

                                        if (ps != null) {
                                            ps.close();
                                        }
                                        break block133;
lbl398:
                                        // 1 sources

                                        catch (SQLException e) {
                                            ps = null;
                                        }
                                    }
                                    try {}
                                    catch (SQLException e) {
                                        return var10_51;
                                    }
                                    if (conn == null) return var10_51;
                                    conn.close();
                                    return var10_51;
lbl407:
                                    // 1 sources

                                    var13_28 = null;
                                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl409:
                                    // 1 sources

                                    if (rs != null) {
                                        rs.close();
                                    }
                                    break block134;
lbl412:
                                    // 1 sources

                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                }
                                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl416:
                                // 1 sources

                                if (ps != null) {
                                    ps.close();
                                }
                                break block135;
lbl419:
                                // 1 sources

                                catch (SQLException e) {
                                    ps = null;
                                }
                            }
                            try {}
                            catch (SQLException e) {
                                return var10_51;
                            }
                            if (conn == null) return var10_51;
                            conn.close();
                            return var10_51;
lbl428:
                            // 1 sources

                            var13_29 = null;
                            ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl430:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block136;
lbl433:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl437:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block137;
lbl440:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var10_51;
                    }
                    if (conn == null) return var10_51;
                    conn.close();
                    return var10_51;
lbl449:
                    // 1 sources

                    var13_30 = null;
                    ** try [egrp 2[TRYBLOCK] [31 : 848->863)] { 
lbl451:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block138;
lbl454:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [32 : 868->883)] { 
lbl458:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block139;
lbl461:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return "TRUE";
            if (conn == null) return "TRUE";
            conn.close();
            return "TRUE";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable loadMerchant(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block32: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select * from affiliate where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block32;
            AffiliateData affiliateData = new AffiliateData();
            affiliateData.code = rs.getString("code");
            affiliateData.password = rs.getString("password");
            affiliateData.id = rs.getInt("ID");
            affiliateData.name = rs.getString("name");
            affiliateData.commission = (Double)rs.getObject("commission");
            affiliateData.lastlogindate = rs.getTimestamp("lastlogindate");
            affiliateData.referredBy = rs.getString("referredBy");
            affiliateData.username = rs.getString("username");
            affiliateData.emailAddress = rs.getString("emailAddress");
            affiliateData.firstName = rs.getString("firstName");
            affiliateData.lastName = rs.getString("lastName");
            affiliateData.additionalInfo = rs.getString("additionalInfo");
            affiliateData.countryIdDetected = (Integer)rs.getObject("countryIdDetected");
            affiliateData.registrationIpAddress = rs.getString("registrationIpAddress");
            affiliateData.dateRegistered = rs.getTimestamp("dateRegistered");
            affiliateData.mobilePhone = null;
            Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(affiliateData);
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
            return hashtable;
        }
        Hashtable affiliateData = ExceptionHelper.setErrorMessageAsHashtable("Invalid merchant name " + username);
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
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return affiliateData;
        catch (SQLException e) {
            Hashtable hashtable;
            try {
                hashtable = ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
                Object var8_12 = null;
            }
            catch (Throwable throwable) {
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return hashtable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String isBuzzPossible(String senderUsername, int contactId) {
        block129: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block123: {
                String e222;
                block119: {
                    String recipientMobilePhone;
                    ContactData contact;
                    block115: {
                        block111: {
                            block107: {
                                block103: {
                                    block99: {
                                        block95: {
                                            block91: {
                                                conn = null;
                                                ps = null;
                                                rs = null;
                                                try {
                                                    ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
                                                    contact = contactBean.getContact(contactId);
                                                }
                                                catch (Exception e222) {
                                                    return ExceptionHelper.getRootMessage(e222);
                                                }
                                                if (contact == null || contact.fusionUsername == null || contact.fusionUsername.length() == 0) {
                                                    return ExceptionHelper.setErrorMessage("Please provide a valid migme contact");
                                                }
                                                conn = this.dataSourceSlave.getConnection();
                                                ps = conn.prepareStatement("select id from contact where username=? and fusionusername=? and status=1");
                                                ps.setString(1, contact.fusionUsername);
                                                ps.setString(2, senderUsername);
                                                rs = ps.executeQuery();
                                                if (rs.next()) break block91;
                                                e222 = ExceptionHelper.setErrorMessage(" ");
                                                Object var11_10 = null;
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
                                                catch (SQLException e3) {
                                                    ps = null;
                                                }
                                                try {
                                                    if (conn != null) {
                                                        conn.close();
                                                    }
                                                }
                                                catch (SQLException e3) {
                                                    conn = null;
                                                }
                                                return e222;
                                            }
                                            rs.close();
                                            ps.close();
                                            ps = conn.prepareStatement("select * from blocklist where username=? and blockusername=?");
                                            ps.setString(1, contact.fusionUsername);
                                            ps.setString(2, senderUsername);
                                            rs = ps.executeQuery();
                                            if (!rs.next()) break block95;
                                            e222 = ExceptionHelper.setErrorMessage(" ");
                                            Object var11_11 = null;
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
                                            catch (SQLException e3) {
                                                ps = null;
                                            }
                                            try {
                                                if (conn != null) {
                                                    conn.close();
                                                }
                                            }
                                            catch (SQLException e3) {
                                                conn = null;
                                            }
                                            return e222;
                                        }
                                        rs.close();
                                        ps.close();
                                        ps = conn.prepareStatement("select * from blocklist where username=? and blockusername=?");
                                        ps.setString(1, senderUsername);
                                        ps.setString(2, contact.fusionUsername);
                                        rs = ps.executeQuery();
                                        if (!rs.next()) break block99;
                                        e222 = ExceptionHelper.setErrorMessage("You have " + contact.fusionUsername + " on your block list");
                                        Object var11_12 = null;
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
                                        catch (SQLException e3) {
                                            ps = null;
                                        }
                                        try {
                                            if (conn != null) {
                                                conn.close();
                                            }
                                        }
                                        catch (SQLException e3) {
                                            conn = null;
                                        }
                                        return e222;
                                    }
                                    rs.close();
                                    ps.close();
                                    ps = conn.prepareStatement("select mobilephone, mobileverified, allowbuzz from user where username=?");
                                    ps.setString(1, contact.fusionUsername);
                                    rs = ps.executeQuery();
                                    if (rs.next()) break block103;
                                    e222 = ExceptionHelper.setErrorMessage("Sorry, an internal error occurred: Unable to load recipient's mobile phone number");
                                    Object var11_13 = null;
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
                                    catch (SQLException e3) {
                                        ps = null;
                                    }
                                    try {
                                        if (conn != null) {
                                            conn.close();
                                        }
                                    }
                                    catch (SQLException e3) {
                                        conn = null;
                                    }
                                    return e222;
                                }
                                if (rs.getBoolean("mobileverified")) break block107;
                                e222 = ExceptionHelper.setErrorMessage("You can only Buzz users with authenticated accounts");
                                Object var11_14 = null;
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
                                catch (SQLException e3) {
                                    ps = null;
                                }
                                try {
                                    if (conn != null) {
                                        conn.close();
                                    }
                                }
                                catch (SQLException e3) {
                                    conn = null;
                                }
                                return e222;
                            }
                            if (rs.getBoolean("allowbuzz")) break block111;
                            e222 = ExceptionHelper.setErrorMessage(" ");
                            Object var11_15 = null;
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
                            catch (SQLException e3) {
                                ps = null;
                            }
                            try {
                                if (conn != null) {
                                    conn.close();
                                }
                            }
                            catch (SQLException e3) {
                                conn = null;
                            }
                            return e222;
                        }
                        recipientMobilePhone = rs.getString("mobilephone");
                        rs.close();
                        ps.close();
                        ps = conn.prepareStatement("select count(*) from systemsms where username=? and type=1 and subtype=14 and datecreated > DATE_SUB(now(), INTERVAL 1 DAY)");
                        ps.setString(1, senderUsername);
                        rs = ps.executeQuery();
                        rs.next();
                        if (rs.getInt(1) < SystemProperty.getInt("MaxBuzzPerDay")) break block115;
                        e222 = ExceptionHelper.setErrorMessage("You have reached the limit of sending Buzzes for today");
                        Object var11_16 = null;
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
                        catch (SQLException e3) {
                            ps = null;
                        }
                        try {
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        catch (SQLException e3) {
                            conn = null;
                        }
                        return e222;
                    }
                    rs.close();
                    ps.close();
                    ps = conn.prepareStatement("select count(*) from systemsms where username=? and type=1 and subtype=14 and destination=? and datecreated > DATE_SUB(now(), INTERVAL 1 DAY)");
                    ps.setString(1, senderUsername);
                    ps.setString(2, recipientMobilePhone);
                    rs = ps.executeQuery();
                    rs.next();
                    if (rs.getInt(1) < SystemProperty.getInt("MaxBuzzToNumberPerDay")) break block119;
                    e222 = ExceptionHelper.setErrorMessage("You have reached the limit of sending a Buzz to " + contact.fusionUsername + " today");
                    Object var11_17 = null;
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
                    catch (SQLException e3) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    catch (SQLException e3) {
                        conn = null;
                    }
                    return e222;
                }
                rs.close();
                ps.close();
                ps = conn.prepareStatement("select balance/exchangerate from user, currency where user.currency=currency.code and user.username=?");
                ps.setString(1, senderUsername);
                rs = ps.executeQuery();
                rs.next();
                if (!(rs.getDouble(1) < SystemProperty.getDouble("BuzzSMSCost"))) break block123;
                e222 = ExceptionHelper.setErrorMessage("You do not have enough credit to send a Buzz");
                Object var11_18 = null;
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
                catch (SQLException e3) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e3) {
                    conn = null;
                }
                return e222;
            }
            rs.close();
            ps.close();
            Object var11_19 = null;
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
            catch (SQLException e3) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block129;
            }
            catch (SQLException e3) {
                conn = null;
            }
            break block129;
            {
                catch (Exception e) {
                    String string = ExceptionHelper.setErrorMessage("An internal error occurred: " + e.getMessage());
                    Object var11_20 = null;
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
                    catch (SQLException e3) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    }
                    catch (SQLException e3) {
                        conn = null;
                    }
                    return string;
                }
            }
            catch (Throwable throwable) {
                Object var11_21 = null;
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
                catch (SQLException e3) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e3) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return "TRUE";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String sendBuzz(String senderUsername, int contactId, String message, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        if (!SMSControl.isSendEnabledForSubtype(SystemSMSData.SubTypeEnum.BUZZ, "contact id=" + contactId)) {
            return "Not sent as BUZZ sms sending disabled";
        }
        conn = null;
        ps = null;
        rs = null;
        smsBuzzCost = 0.0;
        try {
            contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            contact = contactBean.getContact(contactId);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
        if (contact.fusionUsername == null) return ExceptionHelper.setErrorMessage("Invalid contact");
        if (contact.fusionUsername.length() == 0) {
            return ExceptionHelper.setErrorMessage("Invalid contact");
        }
        try {
            block77: {
                block76: {
                    block75: {
                        block74: {
                            block73: {
                                block72: {
                                    block71: {
                                        conn = this.dataSourceSlave.getConnection();
                                        ps = conn.prepareStatement("select id from contact where username=? and fusionusername=? and status=1");
                                        ps.setString(1, contact.fusionUsername);
                                        ps.setString(2, senderUsername);
                                        rs = ps.executeQuery();
                                        if (rs.next()) break block71;
                                        e = ExceptionHelper.setErrorMessage("Sorry, you are not permitted to Buzz " + contact.fusionUsername + " (they may not have you on their contact list, or they may not want to receive Buzz messages)");
                                        var19_17 = null;
                                        ** GOTO lbl145
                                    }
                                    rs.close();
                                    ps.close();
                                    ps = conn.prepareStatement("select * from blocklist where username=? and blockusername=?");
                                    ps.setString(1, contact.fusionUsername);
                                    ps.setString(2, senderUsername);
                                    rs = ps.executeQuery();
                                    if (!rs.next()) break block72;
                                    e = ExceptionHelper.setErrorMessage("Sorry, you are not permitted to Buzz " + contact.fusionUsername + " (they may not have you on their contact list, or they may not want to receive Buzz messages)");
                                    ** GOTO lbl165
                                }
                                rs.close();
                                ps.close();
                                ps = conn.prepareStatement("select * from blocklist where username=? and blockusername=?");
                                ps.setString(1, senderUsername);
                                ps.setString(2, contact.fusionUsername);
                                rs = ps.executeQuery();
                                if (!rs.next()) break block73;
                                e = ExceptionHelper.setErrorMessage("You have " + contact.fusionUsername + " on your block list");
                                ** GOTO lbl186
                            }
                            rs.close();
                            ps.close();
                            ps = conn.prepareStatement("select u.mobilephone as mobilephone, u.mobileverified as mobileverified, u.allowbuzz as allowbuzz, c.smsbuzzcost as smsbuzzcost from user u, country c where u.username=? and c.id=u.countryid");
                            ps.setString(1, contact.fusionUsername);
                            rs = ps.executeQuery();
                            if (rs.next()) break block74;
                            e = ExceptionHelper.setErrorMessage("Sorry, an internal error occurred: Unable to load recipient's mobile phone number");
                            ** GOTO lbl207
                        }
                        if (rs.getBoolean("mobileverified")) break block75;
                        e = ExceptionHelper.setErrorMessage("Sorry, you can only Buzz users with authenticated accounts");
                        ** GOTO lbl228
                    }
                    if (rs.getBoolean("allowbuzz")) break block76;
                    e = ExceptionHelper.setErrorMessage("Sorry, you are not permitted to Buzz " + contact.fusionUsername + " (they may not have you on their contact list, or they may not want to receive Buzz messages)");
                    ** GOTO lbl249
                }
                recipientMobilePhone = rs.getString("mobilephone");
                smsBuzzCost = rs.getDouble("smsbuzzcost");
                rs.close();
                ps.close();
                ps = conn.prepareStatement("select count(*) from systemsms where username=? and type=1 and subtype=14 and datecreated > DATE_SUB(now(), INTERVAL 1 DAY)");
                ps.setString(1, senderUsername);
                rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) < SystemProperty.getInt("MaxBuzzPerDay")) break block77;
                e = ExceptionHelper.setErrorMessage("You have reached the limit of sending Buzzes for today");
                ** GOTO lbl270
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select count(*) from systemsms where username=? and type=1 and subtype=14 and destination=? and datecreated > DATE_SUB(now(), INTERVAL 1 DAY)");
            ps.setString(1, senderUsername);
            ps.setString(2, recipientMobilePhone);
            rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) < SystemProperty.getInt("MaxBuzzToNumberPerDay")) ** GOTO lbl312
            e = ExceptionHelper.setErrorMessage("You have reached the limit of sending a Buzz to " + contact.fusionUsername + " today");
            ** GOTO lbl291
        }
        catch (Exception e) {
            var17_40 = ExceptionHelper.setErrorMessage("Sorry, an internal error occurred: " + e.getMessage());
            var19_26 = null;
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
                if (conn == null) return var17_40;
                conn.close();
                return var17_40;
            }
            catch (SQLException e) {
                return var17_40;
            }
        }
        {
            block97: {
                block96: {
                    block95: {
                        block94: {
                            block93: {
                                block92: {
                                    block91: {
                                        block90: {
                                            block89: {
                                                block88: {
                                                    block87: {
                                                        block86: {
                                                            block85: {
                                                                block84: {
                                                                    block83: {
                                                                        block82: {
                                                                            block81: {
                                                                                block80: {
                                                                                    catch (Throwable var18_42) {
                                                                                        block79: {
                                                                                            block78: {
                                                                                                var19_27 = null;
                                                                                                ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl125:
                                                                                                // 1 sources

                                                                                                if (rs != null) {
                                                                                                    rs.close();
                                                                                                }
                                                                                                break block78;
lbl128:
                                                                                                // 1 sources

                                                                                                catch (SQLException e) {
                                                                                                    rs = null;
                                                                                                }
                                                                                            }
                                                                                            ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl132:
                                                                                            // 1 sources

                                                                                            if (ps != null) {
                                                                                                ps.close();
                                                                                            }
                                                                                            break block79;
lbl135:
                                                                                            // 1 sources

                                                                                            catch (SQLException e) {
                                                                                                ps = null;
                                                                                            }
                                                                                        }
                                                                                        ** try [egrp 5[TRYBLOCK] [22 : 900->915)] { 
lbl139:
                                                                                        // 1 sources

                                                                                        if (conn == null) throw var18_42;
                                                                                        conn.close();
                                                                                        throw var18_42;
lbl142:
                                                                                        // 1 sources

                                                                                        catch (SQLException e) {
                                                                                            conn = null;
                                                                                        }
                                                                                        throw var18_42;
                                                                                    }
lbl145:
                                                                                    // 1 sources

                                                                                    ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl146:
                                                                                    // 1 sources

                                                                                    if (rs != null) {
                                                                                        rs.close();
                                                                                    }
                                                                                    break block80;
lbl149:
                                                                                    // 1 sources

                                                                                    catch (SQLException e) {
                                                                                        rs = null;
                                                                                    }
                                                                                }
                                                                                ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl153:
                                                                                // 1 sources

                                                                                if (ps != null) {
                                                                                    ps.close();
                                                                                }
                                                                                break block81;
lbl156:
                                                                                // 1 sources

                                                                                catch (SQLException e) {
                                                                                    ps = null;
                                                                                }
                                                                            }
                                                                            try {}
                                                                            catch (SQLException e) {
                                                                                return e;
                                                                            }
                                                                            if (conn == null) return e;
                                                                            conn.close();
                                                                            return e;
lbl165:
                                                                            // 1 sources

                                                                            var19_18 = null;
                                                                            ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl167:
                                                                            // 1 sources

                                                                            if (rs != null) {
                                                                                rs.close();
                                                                            }
                                                                            break block82;
lbl170:
                                                                            // 1 sources

                                                                            catch (SQLException e) {
                                                                                rs = null;
                                                                            }
                                                                        }
                                                                        ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl174:
                                                                        // 1 sources

                                                                        if (ps != null) {
                                                                            ps.close();
                                                                        }
                                                                        break block83;
lbl177:
                                                                        // 1 sources

                                                                        catch (SQLException e) {
                                                                            ps = null;
                                                                        }
                                                                    }
                                                                    try {}
                                                                    catch (SQLException e) {
                                                                        return e;
                                                                    }
                                                                    if (conn == null) return e;
                                                                    conn.close();
                                                                    return e;
lbl186:
                                                                    // 1 sources

                                                                    var19_19 = null;
                                                                    ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl188:
                                                                    // 1 sources

                                                                    if (rs != null) {
                                                                        rs.close();
                                                                    }
                                                                    break block84;
lbl191:
                                                                    // 1 sources

                                                                    catch (SQLException e) {
                                                                        rs = null;
                                                                    }
                                                                }
                                                                ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl195:
                                                                // 1 sources

                                                                if (ps != null) {
                                                                    ps.close();
                                                                }
                                                                break block85;
lbl198:
                                                                // 1 sources

                                                                catch (SQLException e) {
                                                                    ps = null;
                                                                }
                                                            }
                                                            try {}
                                                            catch (SQLException e) {
                                                                return e;
                                                            }
                                                            if (conn == null) return e;
                                                            conn.close();
                                                            return e;
lbl207:
                                                            // 1 sources

                                                            var19_20 = null;
                                                            ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl209:
                                                            // 1 sources

                                                            if (rs != null) {
                                                                rs.close();
                                                            }
                                                            break block86;
lbl212:
                                                            // 1 sources

                                                            catch (SQLException e) {
                                                                rs = null;
                                                            }
                                                        }
                                                        ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl216:
                                                        // 1 sources

                                                        if (ps != null) {
                                                            ps.close();
                                                        }
                                                        break block87;
lbl219:
                                                        // 1 sources

                                                        catch (SQLException e) {
                                                            ps = null;
                                                        }
                                                    }
                                                    try {}
                                                    catch (SQLException e) {
                                                        return e;
                                                    }
                                                    if (conn == null) return e;
                                                    conn.close();
                                                    return e;
lbl228:
                                                    // 1 sources

                                                    var19_21 = null;
                                                    ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl230:
                                                    // 1 sources

                                                    if (rs != null) {
                                                        rs.close();
                                                    }
                                                    break block88;
lbl233:
                                                    // 1 sources

                                                    catch (SQLException e) {
                                                        rs = null;
                                                    }
                                                }
                                                ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl237:
                                                // 1 sources

                                                if (ps != null) {
                                                    ps.close();
                                                }
                                                break block89;
lbl240:
                                                // 1 sources

                                                catch (SQLException e) {
                                                    ps = null;
                                                }
                                            }
                                            try {}
                                            catch (SQLException e) {
                                                return e;
                                            }
                                            if (conn == null) return e;
                                            conn.close();
                                            return e;
lbl249:
                                            // 1 sources

                                            var19_22 = null;
                                            ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl251:
                                            // 1 sources

                                            if (rs != null) {
                                                rs.close();
                                            }
                                            break block90;
lbl254:
                                            // 1 sources

                                            catch (SQLException e) {
                                                rs = null;
                                            }
                                        }
                                        ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl258:
                                        // 1 sources

                                        if (ps != null) {
                                            ps.close();
                                        }
                                        break block91;
lbl261:
                                        // 1 sources

                                        catch (SQLException e) {
                                            ps = null;
                                        }
                                    }
                                    try {}
                                    catch (SQLException e) {
                                        return e;
                                    }
                                    if (conn == null) return e;
                                    conn.close();
                                    return e;
lbl270:
                                    // 1 sources

                                    var19_23 = null;
                                    ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl272:
                                    // 1 sources

                                    if (rs != null) {
                                        rs.close();
                                    }
                                    break block92;
lbl275:
                                    // 1 sources

                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                }
                                ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl279:
                                // 1 sources

                                if (ps != null) {
                                    ps.close();
                                }
                                break block93;
lbl282:
                                // 1 sources

                                catch (SQLException e) {
                                    ps = null;
                                }
                            }
                            try {}
                            catch (SQLException e) {
                                return e;
                            }
                            if (conn == null) return e;
                            conn.close();
                            return e;
lbl291:
                            // 1 sources

                            var19_24 = null;
                            ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl293:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block94;
lbl296:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl300:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block95;
lbl303:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return e;
                    }
                    if (conn == null) return e;
                    conn.close();
                    return e;
lbl312:
                    // 1 sources

                    var19_25 = null;
                    ** try [egrp 3[TRYBLOCK] [20 : 860->875)] { 
lbl314:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block96;
lbl317:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 4[TRYBLOCK] [21 : 880->895)] { 
lbl321:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block97;
lbl324:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            ** try [egrp 5[TRYBLOCK] [22 : 900->915)] { 
lbl328:
            // 1 sources

            if (conn != null) {
                conn.close();
            }
lbl332:
            // 1 sources

            catch (SQLException e) {}
            conn = null;
        }
        try {
            messageText = message != null && message.length() > 0 ? SystemProperty.get("BuzzSMS").replaceAll("%1", senderUsername).replaceAll("%2", contact.fusionUsername).replaceAll("%3", message) : SystemProperty.get("BuzzSMSNoUserMessage").replaceAll("%1", senderUsername).replaceAll("%2", contact.fusionUsername);
            systemSMSData = new SystemSMSData();
            systemSMSData.type = SystemSMSData.TypeEnum.STANDARD;
            systemSMSData.subType = SystemSMSData.SubTypeEnum.BUZZ;
            systemSMSData.username = senderUsername;
            systemSMSData.source = SystemProperty.get("TwoWaySMSNumber");
            systemSMSData.destination = recipientMobilePhone;
            systemSMSData.messageText = messageText;
            systemSMSData.cost = smsBuzzCost;
            messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageEJB.sendSystemSMS(systemSMSData, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return messageText;
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    public String createLookout(String creatorUsername, String contactUsername) {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userEJB.createLookout(creatorUsername, contactUsername);
            return "TRUE";
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String isLookoutPossible(String creatorUsername, String contactUsername) {
        conn = null;
        ps = null;
        rs = null;
        try {
            block40: {
                block39: {
                    conn = this.dataSourceSlave.getConnection();
                    ps = conn.prepareStatement("select * from blocklist where username=? and blockusername=?");
                    ps.setString(1, creatorUsername);
                    ps.setString(2, contactUsername);
                    rs = ps.executeQuery();
                    if (!rs.next()) break block39;
                    var6_6 = ExceptionHelper.setErrorMessage("You have " + contactUsername + " on your block list");
                    var9_10 = null;
                    ** GOTO lbl86
                }
                rs.close();
                ps.close();
                ps = conn.prepareStatement("select * from blocklist where username=? and blockusername=?");
                ps.setString(1, contactUsername);
                ps.setString(2, creatorUsername);
                rs = ps.executeQuery();
                if (!rs.next()) break block40;
                var6_7 = ExceptionHelper.setErrorMessage(contactUsername + " may not have you on their contact list");
                ** GOTO lbl106
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select id from contact where username=? and fusionusername=? and status=1");
            ps.setString(1, contactUsername);
            ps.setString(2, creatorUsername);
            rs = ps.executeQuery();
            if (rs.next()) ** GOTO lbl148
            var6_8 = ExceptionHelper.setErrorMessage(contactUsername + " may not have you on their contact list");
            ** GOTO lbl127
        }
        catch (SQLException e) {
            var7_22 = ExceptionHelper.setErrorMessage("Sorry, an internal error occurred: " + e.getMessage());
            var9_14 = null;
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
                if (conn == null) return var7_22;
                conn.close();
                return var7_22;
            }
            catch (SQLException e) {
                return var7_22;
            }
        }
        {
            block50: {
                block49: {
                    block48: {
                        block47: {
                            block46: {
                                block45: {
                                    block44: {
                                        block43: {
                                            catch (Throwable var8_23) {
                                                block42: {
                                                    block41: {
                                                        var9_15 = null;
                                                        ** try [egrp 2[TRYBLOCK] [9 : 342->357)] { 
lbl66:
                                                        // 1 sources

                                                        if (rs != null) {
                                                            rs.close();
                                                        }
                                                        break block41;
lbl69:
                                                        // 1 sources

                                                        catch (SQLException e) {
                                                            rs = null;
                                                        }
                                                    }
                                                    ** try [egrp 3[TRYBLOCK] [10 : 362->377)] { 
lbl73:
                                                    // 1 sources

                                                    if (ps != null) {
                                                        ps.close();
                                                    }
                                                    break block42;
lbl76:
                                                    // 1 sources

                                                    catch (SQLException e) {
                                                        ps = null;
                                                    }
                                                }
                                                ** try [egrp 4[TRYBLOCK] [11 : 382->395)] { 
lbl80:
                                                // 1 sources

                                                if (conn == null) throw var8_23;
                                                conn.close();
                                                throw var8_23;
lbl83:
                                                // 1 sources

                                                catch (SQLException e) {
                                                    conn = null;
                                                }
                                                throw var8_23;
                                            }
lbl86:
                                            // 1 sources

                                            ** try [egrp 2[TRYBLOCK] [9 : 342->357)] { 
lbl87:
                                            // 1 sources

                                            if (rs != null) {
                                                rs.close();
                                            }
                                            break block43;
lbl90:
                                            // 1 sources

                                            catch (SQLException e) {
                                                rs = null;
                                            }
                                        }
                                        ** try [egrp 3[TRYBLOCK] [10 : 362->377)] { 
lbl94:
                                        // 1 sources

                                        if (ps != null) {
                                            ps.close();
                                        }
                                        break block44;
lbl97:
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
lbl106:
                                    // 1 sources

                                    var9_11 = null;
                                    ** try [egrp 2[TRYBLOCK] [9 : 342->357)] { 
lbl108:
                                    // 1 sources

                                    if (rs != null) {
                                        rs.close();
                                    }
                                    break block45;
lbl111:
                                    // 1 sources

                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                }
                                ** try [egrp 3[TRYBLOCK] [10 : 362->377)] { 
lbl115:
                                // 1 sources

                                if (ps != null) {
                                    ps.close();
                                }
                                break block46;
lbl118:
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
lbl127:
                            // 1 sources

                            var9_12 = null;
                            ** try [egrp 2[TRYBLOCK] [9 : 342->357)] { 
lbl129:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block47;
lbl132:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [10 : 362->377)] { 
lbl136:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block48;
lbl139:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return var6_8;
                    }
                    if (conn == null) return var6_8;
                    conn.close();
                    return var6_8;
lbl148:
                    // 1 sources

                    var9_13 = null;
                    ** try [egrp 2[TRYBLOCK] [9 : 342->357)] { 
lbl150:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block49;
lbl153:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [10 : 362->377)] { 
lbl157:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block50;
lbl160:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return "TRUE";
            if (conn == null) return "TRUE";
            conn.close();
            return "TRUE";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public int lookoutExists(String creatorUsername, String contactUsername) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block32: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select username from lookout where username=? and contactusername=?");
            ps.setString(1, creatorUsername);
            ps.setString(2, contactUsername);
            rs = ps.executeQuery();
            if (!rs.next()) break block32;
            int n = 1;
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
            return n;
        }
        int n = 0;
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
        return n;
        catch (SQLException e) {
            int n2;
            try {
                n2 = 0;
                Object var9_11 = null;
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return n2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String removeLookout(String creatorUsername, String contactUsername) {
        block27: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("delete from lookout where username=? and contactusername=?");
            ps.setString(1, creatorUsername);
            ps.setString(2, contactUsername);
            ps.executeUpdate();
            Object var9_6 = null;
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
                break block27;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block27;
            {
                catch (SQLException e) {
                    String string = ExceptionHelper.setErrorMessage("Sorry, an internal error occurred: " + e.getMessage());
                    Object var9_7 = null;
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
            }
            catch (Throwable throwable) {
                Object var9_8 = null;
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
        return "TRUE";
    }

    /*
     * Loose catch block
     */
    public Vector getLookouts(String username) throws EJBException {
        Vector<String> contacts;
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            contacts = new Vector<String>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select contactusername from lookout where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                contacts.add(rs.getString(1));
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
        return contacts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public String setAllowBuzz(String username, boolean allow) {
        block38: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block33: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update user set allowbuzz = ? where username = ?");
                if (allow) {
                    ps.setInt(1, 1);
                } else {
                    ps.setInt(1, 0);
                }
                ps.setString(2, username);
                if (ps.executeUpdate() >= 1) break block33;
                String string = ExceptionHelper.setErrorMessage("Sorry, an internal error occurred: User does not exist");
                Object var9_8 = null;
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
                break block38;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block38;
            {
                catch (SQLException e) {
                    String string = ExceptionHelper.setErrorMessage("Sorry, an internal error occurred: " + e.getMessage());
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
                    return string;
                }
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
        return "TRUE";
    }

    public Hashtable getApplicableDiscountTier(int paymentType, String username, double amount) {
        DiscountTierData returnDiscountTierData = new DiscountTierData();
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData user = userBean.loadUser(username, false, false);
            if (user == null) {
                throw new Exception("Invalid username " + username);
            }
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            CountryData country = misBean.getCountry(user.countryID);
            if (country == null) {
                throw new Exception("Invalid country ID " + user.countryID);
            }
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            CurrencyData currency = paymentType == Enums.PaymentEnum.BANK_TRANSFER.value() ? misBean.getCurrency(country.bankTransferCurrency) : (paymentType == Enums.PaymentEnum.WESTERN_UNION.value() ? misBean.getCurrency(country.westernUnionCurrency) : (paymentType == Enums.PaymentEnum.CREDIT_CARD.value() ? misBean.getCurrency(country.creditCardCurrency) : misBean.getCurrency(user.currency)));
            returnDiscountTierData = accountBean.getApplicableDiscountTier(Enums.PaymentEnum.fromValue(paymentType), username, amount, currency, true);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
        if (returnDiscountTierData == null) {
            return null;
        }
        return HashObjectUtils.dataObjectToHashtable(returnDiscountTierData);
    }

    public Vector getDiscountTiers(int paymentType, String username) {
        Vector<Hashtable> discountTiers = new Vector<Hashtable>();
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData user = userBean.loadUser(username, false, false);
            if (user == null) {
                throw new Exception("Invalid username " + username);
            }
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            CountryData country = misBean.getCountry(user.countryID);
            if (country == null) {
                throw new Exception("Invalid country ID " + user.countryID);
            }
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            CurrencyData currency = paymentType == Enums.PaymentEnum.BANK_TRANSFER.value() ? misBean.getCurrency(country.bankTransferCurrency) : (paymentType == Enums.PaymentEnum.WESTERN_UNION.value() ? misBean.getCurrency(country.westernUnionCurrency) : (paymentType == Enums.PaymentEnum.CREDIT_CARD.value() ? misBean.getCurrency(country.creditCardCurrency) : misBean.getCurrency(user.currency)));
            Vector discountTierDataObjects = accountBean.getEligibleDiscountTiers(Enums.PaymentEnum.fromValue(paymentType), username, currency);
            if (discountTierDataObjects == null) {
                return null;
            }
            for (int i = discountTierDataObjects.size() - 1; i >= 0; --i) {
                ((DiscountTierData)discountTierDataObjects.get((int)i)).displayMin = ((DiscountTierData)discountTierDataObjects.get((int)i)).displayMin - ((DiscountTierData)discountTierDataObjects.get((int)i)).displayMin * (((DiscountTierData)discountTierDataObjects.get((int)i)).percentageDiscount / 100.0);
                ((DiscountTierData)discountTierDataObjects.get((int)i)).displayMin = accountBean.discountTierRounding(((DiscountTierData)discountTierDataObjects.get((int)i)).displayMin);
                ((DiscountTierData)discountTierDataObjects.get((int)i)).max = ((DiscountTierData)discountTierDataObjects.get((int)i)).max - ((DiscountTierData)discountTierDataObjects.get((int)i)).max * (((DiscountTierData)discountTierDataObjects.get((int)i)).percentageDiscount / 100.0);
                ((DiscountTierData)discountTierDataObjects.get((int)i)).max = accountBean.discountTierRounding(((DiscountTierData)discountTierDataObjects.get((int)i)).max);
                discountTiers.add(HashObjectUtils.dataObjectToHashtable(discountTierDataObjects.get(i)));
            }
            return discountTiers;
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
    }

    public double[] getCreditCardPaymentAmounts(String currency) {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            return accountBean.getCreditCardPaymentAmounts(null, false, currency);
        }
        catch (Exception e) {
            return null;
        }
    }

    public double[] getCreditCardPaymentAmounts(String username, boolean isMerchant, String currency) {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            return accountBean.getCreditCardPaymentAmounts(username, isMerchant, currency);
        }
        catch (Exception e) {
            return null;
        }
    }

    /*
     * Loose catch block
     */
    public Vector getPossibleTransferRecipients(String merchantUsername) {
        Vector<String> possibleRecipients;
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            possibleRecipients = new Vector<String>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT username FROM user WHERE merchantcreated=? UNION SELECT DISTINCT LCASE(a1.username) username FROM accountentry a1, accountentry a2 WHERE a1.type=14 AND a1.reference=CAST(a2.id AS CHAR) AND a2.type=14 AND a2.username=? AND a2.amount<0 ORDER BY username");
            ps.setString(1, merchantUsername);
            ps.setString(2, merchantUsername);
            rs = ps.executeQuery();
            while (rs.next()) {
                possibleRecipients.add(rs.getString(1));
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
        return possibleRecipients;
    }

    /*
     * Loose catch block
     */
    public Vector getResellerStates(int countryId) throws EJBException {
        Vector<String> resellerStates;
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            resellerStates = new Vector<String>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select distinct state from reseller where countryid=? and status=1 order by state");
            ps.setInt(1, countryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                resellerStates.add(rs.getString("state"));
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
        return resellerStates;
    }

    /*
     * Loose catch block
     */
    public Vector getResellersInState(int countryId, String state) throws EJBException {
        Vector resellers;
        block27: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            resellers = new Vector();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from reseller where countryid=? and state=? and status=1 order by state, city");
            ps.setInt(1, countryId);
            ps.setString(2, state);
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> resellerHash = new Hashtable<String, Object>();
                resellerHash.put("id", rs.getInt("id"));
                resellerHash.put("state", rs.getString("state"));
                resellerHash.put("city", rs.getString("city"));
                resellerHash.put("name", rs.getString("name"));
                if (rs.getString("address") != null) {
                    resellerHash.put("address", rs.getString("address"));
                }
                if (rs.getString("phonenumber") != null) {
                    resellerHash.put("phonenumber", rs.getString("phonenumber"));
                }
                if (rs.getString("phonenumbertodisplay") != null) {
                    resellerHash.put("phonenumbertodisplay", rs.getString("phonenumbertodisplay"));
                }
                if (rs.getString("phonenumber2") != null) {
                    resellerHash.put("phonenumber2", rs.getString("phonenumber2"));
                }
                if (rs.getString("phonenumber2todisplay") != null) {
                    resellerHash.put("phonenumber2todisplay", rs.getString("phonenumber2todisplay"));
                }
                resellers.add(resellerHash);
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
        return resellers;
    }

    /*
     * Loose catch block
     */
    public Vector getResellers(int countryId) throws EJBException {
        Vector resellers;
        block27: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            resellers = new Vector();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from reseller where countryid=? and status=1 order by state, city");
            ps.setInt(1, countryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> resellerHash = new Hashtable<String, Object>();
                resellerHash.put("id", rs.getInt("id"));
                resellerHash.put("state", rs.getString("state"));
                resellerHash.put("city", rs.getString("city"));
                resellerHash.put("name", rs.getString("name"));
                if (rs.getString("address") != null) {
                    resellerHash.put("address", rs.getString("address"));
                }
                if (rs.getString("phonenumber") != null) {
                    resellerHash.put("phonenumber", rs.getString("phonenumber"));
                }
                if (rs.getString("phonenumbertodisplay") != null) {
                    resellerHash.put("phonenumbertodisplay", rs.getString("phonenumbertodisplay"));
                }
                if (rs.getString("phonenumber2") != null) {
                    resellerHash.put("phonenumber2", rs.getString("phonenumber2"));
                }
                if (rs.getString("phonenumber2todisplay") != null) {
                    resellerHash.put("phonenumber2todisplay", rs.getString("phonenumber2todisplay"));
                }
                resellers.add(resellerHash);
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
        return resellers;
    }

    /*
     * Loose catch block
     */
    public Vector getFixedCallRates(int countryId) throws EJBException {
        Vector fixedRates;
        block34: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            fixedRates = new Vector();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select DestinationCountryID, LandlineToLandline/ExchangeRate LandlineToLandline, LandlineToLandlineSignallingFee/ExchangeRate LandlineToLandlineSignallingFee, LandlineToMobile/ExchangeRate LandlineToMobile, LandlineToMobileSignallingFee/ExchangeRate LandlineToMobileSignallingFee, MobileToLandline/ExchangeRate MobileToLandline, MobileToLandlineSignallingFee/ExchangeRate MobileToLandlineSignallingFee, MobileToMobile/ExchangeRate MobileToMobile, MobileToMobileSignallingFee/ExchangeRate MobileToMobileSignallingFee, CallThroughToLandline/ExchangeRate CallThroughToLandline, CallThroughToLandlineSignallingFee/ExchangeRate CallThroughToLandlineSignallingFee, CallThroughToMobile/ExchangeRate CallThroughToMobile, CallThroughToMobileSignallingFee/ExchangeRate CallThroughToMobileSignallingFee from fixedcallrate, currency where fixedcallrate.currency=currency.code and fixedcallrate.sourcecountryid=?");
            ps.setInt(1, countryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Number> fixedRateHash = new Hashtable<String, Number>();
                fixedRateHash.put("DestinationCountryID", rs.getInt("DestinationCountryID"));
                if (rs.getObject("LandlineToLandline") != null) {
                    fixedRateHash.put("LandlineToLandline", rs.getDouble("LandlineToLandline"));
                }
                if (rs.getObject("LandlineToLandlineSignallingFee") != null) {
                    fixedRateHash.put("LandlineToLandlineSignallingFee", rs.getDouble("LandlineToLandlineSignallingFee"));
                }
                if (rs.getObject("LandlineToMobile") != null) {
                    fixedRateHash.put("LandlineToMobile", rs.getDouble("LandlineToMobile"));
                }
                if (rs.getObject("LandlineToMobileSignallingFee") != null) {
                    fixedRateHash.put("LandlineToMobileSignallingFee", rs.getDouble("LandlineToMobileSignallingFee"));
                }
                if (rs.getObject("MobileToLandline") != null) {
                    fixedRateHash.put("MobileToLandline", rs.getDouble("MobileToLandline"));
                }
                if (rs.getObject("MobileToLandlineSignallingFee") != null) {
                    fixedRateHash.put("MobileToLandlineSignallingFee", rs.getDouble("MobileToLandlineSignallingFee"));
                }
                if (rs.getObject("MobileToMobile") != null) {
                    fixedRateHash.put("MobileToMobile", rs.getDouble("MobileToMobile"));
                }
                if (rs.getObject("MobileToMobileSignallingFee") != null) {
                    fixedRateHash.put("MobileToMobileSignallingFee", rs.getDouble("MobileToMobileSignallingFee"));
                }
                if (rs.getObject("CallThroughToLandline") != null) {
                    fixedRateHash.put("CallThroughToLandline", rs.getDouble("CallThroughToLandline"));
                }
                if (rs.getObject("CallThroughToLandlineSignallingFee") != null) {
                    fixedRateHash.put("CallThroughToLandlineSignallingFee", rs.getDouble("CallThroughToLandlineSignallingFee"));
                }
                if (rs.getObject("CallThroughToMobile") != null) {
                    fixedRateHash.put("CallThroughToMobile", rs.getDouble("CallThroughToMobile"));
                }
                if (rs.getObject("CallThroughToMobileSignallingFee") != null) {
                    fixedRateHash.put("CallThroughToMobileSignallingFee", rs.getDouble("CallThroughToMobileSignallingFee"));
                }
                fixedRates.add(fixedRateHash);
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
                break block34;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block34;
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
        return fixedRates;
    }

    /*
     * Unable to fully structure code
     */
    public Vector getEmoticonPacks(String username, boolean owned, int groupID, int type) throws EJBException {
        block28: {
            conn = null;
            ps = null;
            rs = null;
            emoticonPacks = new Vector<Hashtable<K, V>>();
            conn = this.dataSourceSlave.getConnection();
            if (!owned) ** GOTO lbl19
            if (type == EmoticonPackData.TypeEnum.PREMIUM_PURCHASE.value()) {
                ps = conn.prepareStatement("select emoticonpack.id, emoticonpack.name, emoticonpack.groupid, emoticonpack.groupviponly, cast(count(*) / 3 as signed) numemoticons from emoticon, emoticonpack, emoticonpackowner where emoticonpack.id=emoticonpackowner.emoticonpackid and emoticon.emoticonpackid=emoticonpack.id and emoticonpackowner.username=? and emoticonpack.type=2 and status=1 group by emoticonpack.id");
                ps.setString(1, username);
            } else if (type == EmoticonPackData.TypeEnum.PREMIUM_SUBSCRIPTION.value()) {
                ps = conn.prepareStatement("select emoticonpack.id, emoticonpack.name, emoticonpack.groupid, emoticonpack.groupviponly, cast(count(*) / 3 as signed) numemoticons from emoticon, emoticonpack, service, subscription where subscription.serviceid=service.id and service.id=emoticonpack.serviceid and subscription.username=? and emoticonpack.type=3 and emoticonpack.status=1 and emoticon.emoticonpackid=emoticonpack.id and subscription.status=1 group by emoticonpack.id");
                ps.setString(1, username);
            } else {
                WebBean.log.error((Object)("getEmoticonPacks(): invalid type -  Username[" + username + "] Owned[" + owned + "] GroupID[" + groupID + "] Type[" + type + "]"));
                throw new EJBException("Unknown Emoticon Pack Type");
lbl19:
                // 1 sources

                sql = "select emoticonpack.id, emoticonpack.name, emoticonpack.groupid, emoticonpack.groupviponly, emoticonpack.price * currency.exchangerate price, currency.code currency, cast(count(*) / 3 as signed) numemoticons, if (A.username is null, 0, 1) purchased from emoticonpack left outer join (select * from emoticonpackowner where username=?) A on emoticonpack.id = A.emoticonpackid left outer join emoticon on emoticonpack.id = emoticon.emoticonpackid inner join user on user.username=? inner join currency on user.currency = currency.code where emoticonpack.status=1 and forsale=1 and emoticonpack.type=? ";
                sql = groupID > 0 ? sql + "and groupid=? " : sql + "and (groupid is null or groupid=0) ";
                sql = sql + "group by emoticonpack.id order by sortorder";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, username);
                ps.setInt(3, type);
                if (groupID > 0) {
                    ps.setInt(4, groupID);
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                pack = new Hashtable<String, Object>();
                pack.put("id", rs.getInt("id"));
                pack.put("name", rs.getString("name"));
                pack.put("groupid", rs.getInt("groupid"));
                pack.put("groupviponly", rs.getBoolean("groupviponly"));
                pack.put("numemoticons", rs.getInt("numemoticons"));
                if (!owned) {
                    pack.put("price", rs.getDouble("price"));
                    pack.put("currency", rs.getString("currency"));
                    pack.put("purchased", rs.getInt("purchased"));
                }
                emoticonPacks.add(pack);
            }
            var11_11 = null;
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
                break block28;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block28;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable var10_15) {
                var11_12 = null;
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
                throw var10_15;
            }
        }
        return emoticonPacks;
    }

    /*
     * Loose catch block
     */
    public Vector getEmoticonsInPack(int emoticonPackID) throws EJBException {
        Vector emoticons;
        block22: {
            Connection connSlave = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            emoticons = new Vector();
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select * from emoticon where emoticonpackid=? and width=16");
            ps.setInt(1, emoticonPackID);
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> emoticon = new Hashtable<String, Object>();
                emoticon.put("alias", rs.getString("alias"));
                emoticon.put("type", rs.getInt("type"));
                emoticons.add(emoticon);
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
                if (connSlave != null) {
                    connSlave.close();
                }
                break block22;
            }
            catch (SQLException e) {
                connSlave = null;
            }
            break block22;
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
        return emoticons;
    }

    public Vector getEmoticonDetailsFromHotkeys(String emoticonHotkeys) throws EJBException {
        Vector emoticons = new Vector();
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            String[] hotkeys = emoticonHotkeys.split(" ");
            for (int i = 0; i < hotkeys.length; ++i) {
                EmoticonData emoticonData = contentBean.getEmoticon(hotkeys[i], 16);
                if (emoticonData != null) {
                    Hashtable<String, String> emoticon = new Hashtable<String, String>();
                    emoticon.put("hotkey", hotkeys[i]);
                    emoticon.put("alias", emoticonData.alias);
                    emoticon.put("type", emoticonData.type.toString());
                    emoticon.put("location", emoticonData.locationPNG);
                    emoticons.add(emoticon);
                    continue;
                }
                VirtualGiftData virtualGiftData = contentBean.getVirtualGiftByHotKey(hotkeys[i]);
                if (virtualGiftData == null) continue;
                Hashtable<String, String> emoticon = new Hashtable<String, String>();
                emoticon.put("hotkey", hotkeys[i]);
                emoticon.put("alias", virtualGiftData.getName());
                emoticon.put("type", EmoticonData.TypeEnum.IMAGE.toString());
                emoticon.put("location", virtualGiftData.getLocation16x16PNG());
                emoticons.add(emoticon);
            }
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessageAsVector((Exception)((Object)e));
        }
        catch (FusionEJBException e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessageAsVector(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessageAsVector(e.getMessage());
        }
        return emoticons;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getEmoticonPack(String username, int emoticonPackId) throws EJBException {
        Hashtable<String, Object> emoticonPack;
        block34: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            emoticonPack = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select emoticonpack.id, emoticonpack.name, emoticonpack.description, emoticonpack.price * currency.exchangerate price, emoticonpack.groupid, emoticonpack.groupviponly, emoticonpack.type, emoticonpack.serviceid, currency.code currency, cast(count(*) / 3 as signed) numemoticons, if (A.username is null, 0, 1) purchased from emoticonpack left outer join (select * from emoticonpackowner where username=?) A on emoticonpack.id = A.emoticonpackid left outer join emoticon on emoticonpack.id = emoticon.emoticonpackid inner join user on user.username=? inner join currency on user.currency = currency.code where emoticonpack.status=1 and emoticonpack.type in (2,3) and emoticonpack.id=? group by emoticonpack.id");
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setInt(3, emoticonPackId);
            rs = ps.executeQuery();
            if (rs.next()) {
                emoticonPack.put("id", rs.getInt("id"));
                emoticonPack.put("name", rs.getString("name"));
                if (rs.getObject("description") != null) {
                    emoticonPack.put("description", rs.getString("description"));
                }
                emoticonPack.put("price", rs.getDouble("price"));
                emoticonPack.put("currency", rs.getString("currency"));
                emoticonPack.put("groupid", rs.getInt("groupid"));
                emoticonPack.put("groupviponly", rs.getBoolean("groupviponly"));
                emoticonPack.put("numemoticons", rs.getInt("numemoticons"));
                emoticonPack.put("purchased", rs.getInt("purchased"));
                emoticonPack.put("type", rs.getInt("type"));
                emoticonPack.put("serviceid", rs.getInt("serviceid"));
            }
            if (rs.getInt("type") == EmoticonPackData.TypeEnum.PREMIUM_SUBSCRIPTION.value()) {
                rs.close();
                rs = null;
                ps.close();
                ps = null;
                String sql = "SELECT service.*, service.Cost / currency_sub.ExchangeRate * currency_user.ExchangeRate price, user.Currency usercurrency, subscription.Status purchased FROM user INNER JOIN currency currency_user ON user.Currency=currency_user.Code, service INNER JOIN currency currency_sub ON service.CostCurrency=currency_sub.code LEFT OUTER JOIN subscription ON (subscription.ServiceID=service.ID AND subscription.username=?) WHERE user.Username=? AND service.ID=? AND subscription.Status=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, username);
                ps.setInt(3, (Integer)emoticonPack.get("serviceid"));
                ps.setInt(4, SubscriptionData.StatusEnum.ACTIVE.value());
                rs = ps.executeQuery();
                if (rs.next()) {
                    emoticonPack.put("freetrialdays", rs.getInt("freetrialdays"));
                    emoticonPack.put("durationdays", rs.getInt("durationdays"));
                    emoticonPack.put("price", Numerics.round(rs.getDouble("price"), 2));
                    emoticonPack.put("currency", rs.getString("usercurrency"));
                    emoticonPack.put("purchased", rs.getInt("purchased") == 1 ? 1 : 0);
                }
            }
            if (emoticonPack.get("groupid") != null && (Integer)emoticonPack.get("groupid") > 0) {
                rs.close();
                rs = null;
                ps.close();
                ps = null;
                conn.close();
                conn = null;
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                GroupData groupData = userEJB.getGroup((Integer)emoticonPack.get("groupid"));
                GroupMemberData groupMemberData = userEJB.getGroupMember(username, (Integer)emoticonPack.get("groupid"));
                if (groupMemberData == null || GroupMemberData.StatusEnum.ACTIVE.value() != groupMemberData.status.value() || !groupMemberData.vip.booleanValue() && ((Boolean)emoticonPack.get("groupviponly")).booleanValue()) {
                    String err = "You must be a ";
                    if (emoticonPack.get("groupviponly") != null && ((Boolean)emoticonPack.get("groupviponly")).booleanValue()) {
                        err = err + "VIP ";
                    }
                    err = err + "member of the " + groupData.name + " group to access the " + emoticonPack.get("name") + " emoticon pack";
                    throw new Exception(err);
                }
            }
            Object var12_13 = null;
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
                break block34;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block34;
            {
                catch (Exception e) {
                    log.error((Object)"Exception in getEmoticonPack()", (Throwable)e);
                    Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
                    Object var12_14 = null;
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
                    return hashtable;
                }
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
        return emoticonPack;
    }

    public String buyEmoticonPack(String username, int emoticonPackId, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            contentBean.buyEmoticonPack(username, emoticonPackId, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (FusionEJBException e) {
            return ExceptionHelper.getRootMessage(e);
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
        return "TRUE";
    }

    public Vector getAllEmoticons() throws EJBException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            List allEmoticons = contentBean.getAllEmoticons();
            Vector<Hashtable> v = new Vector<Hashtable>();
            for (EmoticonData e : allEmoticons) {
                if (e.width != 16) continue;
                v.add(HashObjectUtils.dataObjectToHashtable(e));
                for (String altHotkey : e.alternateHotKeys) {
                    Hashtable h = HashObjectUtils.dataObjectToHashtable(e);
                    h.put("hotKey", altHotkey);
                    v.add(h);
                }
            }
            return v;
        }
        catch (CreateException e) {
            throw new EJBException(ExceptionHelper.getRootMessage((Exception)((Object)e)));
        }
        catch (FusionEJBException e) {
            throw new EJBException(ExceptionHelper.getRootMessage(e));
        }
        catch (EJBException e) {
            throw new EJBException(e.getCausedByException().getMessage());
        }
    }

    private Vector getContentAsVector(List<ContentData> contents) {
        Vector<Hashtable> v = new Vector<Hashtable>();
        for (int i = 0; i < contents.size(); ++i) {
            Hashtable hash = HashObjectUtils.dataObjectToHashtable(contents.get(i));
            v.add(hash);
        }
        return v;
    }

    public Vector getTopWallpaper(int count) {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            List wallpapers = contentBean.getTopWallpaper(count);
            return this.getContentAsVector(wallpapers);
        }
        catch (Exception e) {
            throw new EJBException(ExceptionHelper.getRootMessage(e));
        }
    }

    public Vector getTopRingtones(int count) {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            List ringtones = contentBean.getTopRingtones(count);
            return this.getContentAsVector(ringtones);
        }
        catch (Exception e) {
            throw new EJBException(ExceptionHelper.getRootMessage(e));
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Vector getMobileContentCategories(String username, int parentContentCategoryID, int groupID, int page, int numEntries) throws EJBException {
        conn = null;
        ps = null;
        rs = null;
        categories = new Vector<Hashtable<K, V>>();
        numRows = 0;
        startEntry = (page - 1) * numEntries + 1;
        endEntry = startEntry + numEntries - 1;
        try {
            conn = this.dataSourceSlave.getConnection();
            sql = "select ContentCategory.ID, ContentCategory.Name, count(*) numitems from ContentCategory, content, user where user.username=? and contentcategory.id=content.contentcategoryid and content.status=1 and (content.countryid=user.countryid or content.countryid is null) ";
            sql = parentContentCategoryID > 0 ? sql + "and ParentContentCategoryID=? " : sql + "and ParentContentCategoryID is null ";
            sql = groupID > 0 ? sql + "and content.groupid=? " : sql + "and (content.groupid is null or content.groupid=0) ";
            sql = sql + "group by id, name order by Name";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            if (parentContentCategoryID > 0) {
                ps.setInt(2, parentContentCategoryID);
            }
            if (groupID > 0) {
                if (parentContentCategoryID > 0) {
                    ps.setInt(3, groupID);
                } else {
                    ps.setInt(2, groupID);
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                ++numRows;
            }
            rs.beforeFirst();
            markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page);
            markerHash.put("numEntries", numRows);
            markerHash.put("numPages", (numRows + numEntries - 1) / numEntries);
            categories.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            while (rs.next()) {
                if (rs.getRow() >= startEntry) {
                    category = new Hashtable<String, Object>();
                    category.put("id", rs.getInt("id"));
                    category.put("name", rs.getString("name"));
                    category.put("numitems", rs.getString("numitems"));
                    categories.add(category);
                }
                if (rs.getRow() < endEntry) continue;
                break;
            }
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        var17_17 = null;
        ** GOTO lbl83
        {
            block30: {
                block29: {
                    catch (Throwable var16_21) {
                        var17_18 = null;
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
                            if (conn == null) throw var16_21;
                            conn.close();
                            throw var16_21;
                        }
                        catch (SQLException e) {
                            conn = null;
                        }
                        throw var16_21;
                    }
lbl83:
                    // 1 sources

                    ** try [egrp 2[TRYBLOCK] [3 : 498->513)] { 
lbl84:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block29;
lbl87:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [4 : 518->533)] { 
lbl91:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block30;
lbl94:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            conn = null;
            return categories;
            if (conn == null) return categories;
            conn.close();
            return categories;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Vector getMobileContent(String username, int contentCategoryID, int groupID, int page, int numEntries) throws EJBException {
        conn = null;
        ps = null;
        rs = null;
        content = new Vector<Hashtable<K, V>>();
        numRows = 0;
        startEntry = page * numEntries + 1;
        endEntry = startEntry + numEntries - 1;
        try {
            conn = this.dataSourceSlave.getConnection();
            sql = "select content.id, content.contentcategoryid, content.type, content.name, content.artist, content.price / currency_content.exchangerate * currency_user.exchangerate price, currency_user.code currency, content.preview, content.groupid, content.groupviponly, content.thumbnail from content, user, currency currency_user, currency currency_content where user.username=? and user.currency=currency_user.code and content.currency=currency_content.code and contentcategoryid=? and content.status=1 and (content.countryid=user.countryid or content.countryid is null) ";
            sql = groupID > 0 ? sql + "and groupid=? " : sql + "and (groupid is null or groupid=0) ";
            sql = sql + "order by content.artist, content.name";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, contentCategoryID);
            if (groupID > 0) {
                ps.setInt(3, groupID);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                ++numRows;
            }
            rs.beforeFirst();
            markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page);
            markerHash.put("numEntries", numRows);
            markerHash.put("numPages", (numRows + numEntries - 1) / numEntries);
            content.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            while (rs.next()) {
                if (rs.getRow() >= startEntry) {
                    contentItem = new Hashtable<String, Object>();
                    contentItem.put("id", rs.getInt("id"));
                    contentItem.put("contentcategoryid", rs.getInt("contentcategoryid"));
                    contentItem.put("type", ContentData.TypeEnum.fromValue(rs.getInt("type")).toString());
                    contentItem.put("name", rs.getString("name"));
                    contentItem.put("artist", rs.getString("artist") == null ? "" : rs.getString("artist"));
                    contentItem.put("price", rs.getDouble("price"));
                    contentItem.put("currency", rs.getString("currency"));
                    contentItem.put("preview", rs.getString("preview") == null ? "" : rs.getString("preview"));
                    contentItem.put("thumbnail", rs.getString("thumbnail") == null ? "" : rs.getString("thumbnail"));
                    contentItem.put("groupid", rs.getInt("groupid"));
                    contentItem.put("groupviponly", rs.getBoolean("groupviponly"));
                    content.add(contentItem);
                }
                if (rs.getRow() < endEntry) continue;
                break;
            }
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        var17_17 = null;
        ** GOTO lbl94
        {
            block27: {
                block26: {
                    catch (Throwable var16_21) {
                        var17_18 = null;
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
                            if (conn == null) throw var16_21;
                            conn.close();
                            throw var16_21;
                        }
                        catch (SQLException e) {
                            conn = null;
                        }
                        throw var16_21;
                    }
lbl94:
                    // 1 sources

                    ** try [egrp 2[TRYBLOCK] [3 : 646->661)] { 
lbl95:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block26;
lbl98:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [4 : 666->681)] { 
lbl102:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block27;
lbl105:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            conn = null;
            return content;
            if (conn == null) return content;
            conn.close();
            return content;
        }
    }

    /*
     * Loose catch block
     */
    public Hashtable getMobileContentItem(String username, int contentID, boolean activeOnly) throws EJBException {
        Hashtable<String, Object> contentItem;
        block25: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            contentItem = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            String limitToActiveOnly = "";
            if (activeOnly) {
                limitToActiveOnly = " and content.status=1";
            }
            ps = conn.prepareStatement("select A.*, if (contentpurchased.id is null, false, true) purchased, contentpurchased.numdownloads, contentpurchased.downloadurl, TIMESTAMPDIFF(HOUR,datecreated,now()) hourssincepurchase from (select content.id, content.contentcategoryid, content.type, content.name, content.artist, content.price / currency_content.exchangerate * currency_user.exchangerate price, currency_user.code currency, content.preview, content.previewwidth, content.previewheight, content.groupid, content.groupviponly, content.thumbnail from content, user, currency currency_user, currency currency_content where user.username=? and user.currency=currency_user.code and content.currency=currency_content.code " + limitToActiveOnly + " and content.id=?) A " + "left outer join contentpurchased on " + "contentpurchased.contentid=A.id and contentpurchased.username=?");
            ps.setString(1, username);
            ps.setInt(2, contentID);
            ps.setString(3, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                contentItem.put("id", rs.getInt("id"));
                contentItem.put("contentcategoryid", rs.getInt("contentcategoryid"));
                contentItem.put("type", ContentData.TypeEnum.fromValue(rs.getInt("type")).toString());
                contentItem.put("name", rs.getString("name"));
                contentItem.put("artist", rs.getString("artist") == null ? "" : rs.getString("artist"));
                contentItem.put("price", rs.getDouble("price"));
                contentItem.put("currency", rs.getString("currency"));
                contentItem.put("preview", rs.getString("preview") == null ? "" : rs.getString("preview"));
                contentItem.put("previewwidth", rs.getString("previewwidth") == null ? "" : rs.getString("previewwidth"));
                contentItem.put("previewheight", rs.getString("previewheight") == null ? "" : rs.getString("previewheight"));
                contentItem.put("groupid", rs.getInt("groupid"));
                contentItem.put("groupviponly", rs.getBoolean("groupviponly"));
                contentItem.put("thumbnail", rs.getString("thumbnail") == null ? "" : rs.getString("thumbnail"));
                contentItem.put("numdownloads", rs.getString("numdownloads") == null ? "" : rs.getString("numdownloads"));
                contentItem.put("downloadurl", rs.getString("downloadurl") == null ? "" : rs.getString("downloadurl"));
                contentItem.put("hourssincepurchase", rs.getInt("hourssincepurchase"));
                if ((Integer)contentItem.get("groupid") > 0) {
                    rs.close();
                    rs = null;
                    ps.close();
                    ps = null;
                    conn.close();
                    conn = null;
                    UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    GroupData groupData = userEJB.getGroup((Integer)contentItem.get("groupid"));
                    GroupMemberData groupMemberData = userEJB.getGroupMember(username, (Integer)contentItem.get("groupid"));
                    if (groupMemberData == null || GroupMemberData.StatusEnum.ACTIVE.value() != groupMemberData.status.value()) {
                        String err = "You must be a ";
                        err = err + "member of the " + groupData.name + " group to access " + contentItem.get("name");
                        throw new Exception(err);
                    }
                }
            }
            Object var14_14 = null;
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
                catch (Exception e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
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
                throw throwable;
            }
        }
        return contentItem;
    }

    public String buyMobileContentItem(String username, int contentID, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            String downloadURL = contentBean.buyMobileContentItem(username, contentID, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            if (downloadURL == null) {
                downloadURL = "";
            }
            return downloadURL;
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            if (e.getCausedByException() != null) {
                return ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage());
            }
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public String getMobileContentDownloadURL(String username, int contentID) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select id, downloadurl from contentpurchased where username=? and contentid=?");
            ps.setString(1, username);
            ps.setInt(2, contentID);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            String string = rs.getString("downloadurl");
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
            return string;
        }
        String string = null;
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
        return string;
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

    public boolean processILoopAPICall(String providerTransactionId, String destAddr, String body, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            contentBean.processILoopAPICall(providerTransactionId, destAddr, body, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
        }
        catch (CreateException e) {
            throw new EJBException(ExceptionHelper.getRootMessage((Exception)((Object)e)));
        }
        catch (EJBException e) {
            throw new EJBException(ExceptionHelper.setErrorMessage(e.getCausedByException().getMessage()));
        }
        return true;
    }

    public boolean processOplayoAPICall(int oplayoID, int rputag, int progress, int licenceId, int timestamp) throws EJBException {
        return true;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Vector getPurchasedContent(String username, int page, int numEntries) throws EJBException {
        conn = null;
        ps = null;
        rs = null;
        content = new Vector<Hashtable<K, V>>();
        numRows = 0;
        startEntry = page * numEntries + 1;
        endEntry = startEntry + numEntries - 1;
        maxAEPeriodBeforeArchival = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS);
        try {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from (select contentpurchased.datecreated, content.type, content.id, content.name from contentpurchased inner join content on contentpurchased.contentid=content.id where contentpurchased.username=? union select accountentry.datecreated, 0, accountentry.reference id, emoticonpack.name from accountentry, emoticonpack where accountentry.username=? and accountentry.type=27 and accountentry.reference=emoticonpack.id and accountentry.datecreated >= date_sub(curdate(), interval ? day) ) A order by datecreated desc");
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setInt(3, maxAEPeriodBeforeArchival);
            rs = ps.executeQuery();
            while (rs.next()) {
                ++numRows;
            }
            rs.beforeFirst();
            markerHash = new Hashtable<String, Integer>();
            markerHash.put("page", page);
            markerHash.put("numEntries", numRows);
            markerHash.put("numPages", (numRows + numEntries - 1) / numEntries);
            content.add(markerHash);
            if (endEntry > numRows) {
                endEntry = numRows;
            }
            df = new SimpleDateFormat("d MMM yy");
            while (rs.next()) {
                if (rs.getRow() >= startEntry) {
                    contentItem = new Hashtable<String, Object>();
                    contentItem.put("dateCreated", df.format(rs.getTimestamp("dateCreated")));
                    contentItem.put("type", rs.getInt("type"));
                    contentItem.put("id", rs.getInt("id"));
                    contentItem.put("name", rs.getString("name"));
                    content.add(contentItem);
                }
                if (rs.getRow() < endEntry) continue;
                break;
            }
        }
        catch (SQLException e) {
            throw new EJBException(e.getMessage());
        }
        var16_16 = null;
        ** GOTO lbl78
        {
            block26: {
                block25: {
                    catch (Throwable var15_20) {
                        var16_17 = null;
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
                            if (conn == null) throw var15_20;
                            conn.close();
                            throw var15_20;
                        }
                        catch (SQLException e) {
                            conn = null;
                        }
                        throw var15_20;
                    }
lbl78:
                    // 1 sources

                    ** try [egrp 2[TRYBLOCK] [3 : 377->392)] { 
lbl79:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block25;
lbl82:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [4 : 397->412)] { 
lbl86:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block26;
lbl89:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            conn = null;
            return content;
            if (conn == null) return content;
            conn.close();
            return content;
        }
    }

    /*
     * Loose catch block
     */
    public Hashtable getSmallProfile(String usernameViewing, String usernameBeingViewed) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select user.displaypicture, user.mobilephone, user.emailactivated, user.chatroomadmin, user.registrationdate, country.name country, userprofile.city, userprofile.gender, DATE_FORMAT(NOW(), '%Y') - DATE_FORMAT(userprofile.dateofbirth, '%Y') - (DATE_FORMAT(NOW(), '00-%m-%d') < DATE_FORMAT(userprofile.dateofbirth, '00-%m-%d')) AS age, userprofile.relationshipstatus, userprofile.status userprofilestatus from user, userprofile, country where user.username=? and user.username=userprofile.username and user.countryid=country.id");
        ps.setString(1, usernameBeingViewed);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Profile not found");
        }
        Hashtable<String, Object> hash = new Hashtable<String, Object>();
        hash.put("displaypicture", rs.getString("displaypicture"));
        hash.put("mobilephone", rs.getString("mobilephone"));
        hash.put("emailactivated", rs.getInt("emailactivated"));
        hash.put("chatroomadmin", rs.getInt("chatroomadmin"));
        hash.put("registrationdate", rs.getTimestamp("registrationdate"));
        hash.put("country", rs.getString("country"));
        hash.put("city", rs.getString("city"));
        hash.put("gender", rs.getString("gender"));
        hash.put("age", rs.getString("age"));
        hash.put("relationshipstatus", rs.getString("relationshipstatus"));
        hash.put("userprofilestatus", rs.getString("userprofilestatus"));
        Hashtable<String, Object> hashtable = hash;
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
        return hashtable;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Hashtable getFriends(String usernameViewing, String usernameBeingViewed, String searchString, int pageNumber, int resultsPerPage) throws EJBException {
        Hashtable<String, Serializable> hashtable;
        Connection conn = null;
        Vector<Hashtable> friendsVector = new Vector<Hashtable>();
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        try {
            try {
                int startResultCount;
                int totalPageCount;
                double pages;
                conn = this.dataSourceSlave.getConnection();
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                Set friends = userBean.loadBroadcastList(usernameBeingViewed, conn);
                if (searchString.length() > 0) {
                    searchString = searchString.toLowerCase();
                    Iterator i = friends.iterator();
                    while (i.hasNext()) {
                        if (((String)i.next()).toLowerCase().contains(searchString)) continue;
                        i.remove();
                    }
                }
                if ((pages = (double)friends.size() / (double)resultsPerPage) < 1.0) {
                    pages = 1.0;
                }
                if (pageNumber > (totalPageCount = (int)Math.ceil(pages))) {
                    pageNumber = totalPageCount;
                }
                if ((startResultCount = (pageNumber - 1) * resultsPerPage) < 0) {
                    startResultCount = 0;
                }
                int endResultCount = startResultCount + resultsPerPage;
                Iterator it = friends.iterator();
                int rowPosition = 0;
                while (it.hasNext()) {
                    String username = (String)it.next();
                    if (rowPosition >= startResultCount && rowPosition < endResultCount) {
                        friendsVector.add(this.getUserContactDetails(usernameBeingViewed, username));
                    }
                    ++rowPosition;
                }
                hash.put("totalResults", Integer.valueOf(friends.size()));
                hash.put("pageNumber", Integer.valueOf(pageNumber));
                hash.put("totalPages", Integer.valueOf(totalPageCount));
                hash.put("startResult", Integer.valueOf(startResultCount));
                hash.put("endResult", Integer.valueOf(endResultCount));
                hash.put("friends", friendsVector);
                hashtable = hash;
                Object var20_21 = null;
            }
            catch (Exception ex) {
                Hashtable hashtable2 = new Hashtable();
                Object var20_22 = null;
                try {
                    if (conn == null) return hashtable2;
                    conn.close();
                    return hashtable2;
                }
                catch (SQLException e) {
                    return hashtable2;
                }
            }
        }
        catch (Throwable throwable) {
            Object var20_23 = null;
            try {}
            catch (SQLException e) {
                conn = null;
                throw throwable;
            }
            if (conn == null) throw throwable;
            conn.close();
            throw throwable;
        }
        try {}
        catch (SQLException e) {
            return hashtable;
        }
        if (conn == null) return hashtable;
        conn.close();
        return hashtable;
    }

    /*
     * Loose catch block
     */
    public Hashtable getCallRates(int sourceCountryId, int destinationCountryId, boolean sourceIsLandline, String currencyCode) {
        Hashtable<String, Object> callRates;
        block45: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            callRates = new Hashtable<String, Object>();
            boolean callThroughSupported = false;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select number from didnumber where countryid=? and status=1");
            ps.setInt(1, sourceCountryId);
            rs = ps.executeQuery();
            if (rs.next()) {
                callThroughSupported = true;
            }
            rs.close();
            ps.close();
            String sql = "select ";
            if (sourceIsLandline) {
                sql = sql + "country.callrate * currency.exchangerate sourcerate, ";
                sql = sql + "country.callsignallingfee*currency.exchangerate sourcesignallingfee, ";
            } else {
                sql = sql + "country.mobilerate * currency.exchangerate sourcerate, ";
                sql = sql + "country.mobilesignallingfee * currency.exchangerate sourcesignallingfee, ";
            }
            sql = sql + "country.callthroughrate * currency.exchangerate sourcecallthroughrate, ";
            sql = sql + "country.callthroughsignallingfee * currency.exchangerate sourcecallthroughsignallingfee, ";
            sql = sql + "dest.smscost * currency.exchangerate smscost, country.name ";
            sql = sql + "from country, currency, country dest where country.id=? and currency.code=? and dest.id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sourceCountryId);
            ps.setString(2, currencyCode);
            ps.setInt(3, destinationCountryId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Invalid source country or currency");
            }
            callRates.put("CallbackToLandlineRate", rs.getDouble("sourcerate"));
            callRates.put("CallbackToMobileRate", rs.getDouble("sourcerate"));
            callRates.put("CallbackToLandlineSignallingFee", rs.getDouble("sourcesignallingfee"));
            callRates.put("CallbackToMobileSignallingFee", rs.getDouble("sourcesignallingfee"));
            if (callThroughSupported) {
                callRates.put("CallThroughToLandlineRate", rs.getDouble("sourcecallthroughrate"));
                callRates.put("CallThroughToMobileRate", rs.getDouble("sourcecallthroughrate"));
                callRates.put("CallThroughToLandlineSignallingFee", rs.getDouble("sourcecallthroughsignallingfee"));
                callRates.put("CallThroughToMobileSignallingFee", rs.getDouble("sourcecallthroughsignallingfee"));
            }
            callRates.put("SMSCost", rs.getString("smscost"));
            callRates.put("SourceCountry", rs.getString("name"));
            rs.close();
            ps.close();
            sql = "select country.callrate * currency.exchangerate destinationlandlinerate, country.callsignallingfee * currency.exchangerate destinationlandlinesignallingfee, country.mobilerate * currency.exchangerate destinationmobilerate, country.mobilesignallingfee * currency.exchangerate destinationmobilesignallingfee, country.name from country, currency where country.id=? and currency.code=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, destinationCountryId);
            ps.setString(2, currencyCode);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Invalid destination country");
            }
            callRates.put("CallbackToLandlineRate", (Double)callRates.get("CallbackToLandlineRate") + rs.getDouble("destinationlandlinerate"));
            callRates.put("CallbackToMobileRate", (Double)callRates.get("CallbackToMobileRate") + rs.getDouble("destinationmobilerate"));
            callRates.put("CallbackToLandlineSignallingFee", (Double)callRates.get("CallbackToLandlineSignallingFee") + rs.getDouble("destinationlandlinesignallingfee"));
            callRates.put("CallbackToMobileSignallingFee", (Double)callRates.get("CallbackToMobileSignallingFee") + rs.getDouble("destinationmobilesignallingfee"));
            if (callThroughSupported) {
                callRates.put("CallThroughToLandlineRate", (Double)callRates.get("CallThroughToLandlineRate") + rs.getDouble("destinationlandlinerate"));
                callRates.put("CallThroughToMobileRate", (Double)callRates.get("CallThroughToMobileRate") + rs.getDouble("destinationmobilerate"));
                callRates.put("CallThroughToLandlineSignallingFee", (Double)callRates.get("CallThroughToLandlineSignallingFee") + rs.getDouble("destinationlandlinesignallingfee"));
                callRates.put("CallThroughToMobileSignallingFee", (Double)callRates.get("CallThroughToMobileSignallingFee") + rs.getDouble("destinationmobilesignallingfee"));
            }
            callRates.put("DestinationCountry", rs.getString("name"));
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select DestinationCountryID, LandlineToLandline / c1.ExchangeRate * c2.ExchangeRate LandlineToLandline, LandlineToLandlineSignallingFee / c1.ExchangeRate * c2.ExchangeRate LandlineToLandlineSignallingFee, LandlineToMobile / c1.ExchangeRate * c2.ExchangeRate LandlineToMobile, LandlineToMobileSignallingFee / c1.ExchangeRate * c2.ExchangeRate LandlineToMobileSignallingFee, MobileToLandline / c1.ExchangeRate * c2.ExchangeRate MobileToLandline, MobileToLandlineSignallingFee / c1.ExchangeRate * c2.ExchangeRate MobileToLandlineSignallingFee, MobileToMobile / c1.ExchangeRate * c2.ExchangeRate MobileToMobile, MobileToMobileSignallingFee / c1.ExchangeRate * c2.ExchangeRate MobileToMobileSignallingFee, CallThroughToLandline / c1.ExchangeRate * c2.ExchangeRate CallThroughToLandline, CallThroughToLandlineSignallingFee / c1.ExchangeRate * c2.ExchangeRate CallThroughToLandlineSignallingFee, CallThroughToMobile / c1.ExchangeRate * c2.ExchangeRate CallThroughToMobile, CallThroughToMobileSignallingFee / c1.ExchangeRate * c2.ExchangeRate CallThroughToMobileSignallingFee from fixedcallrate, currency c1, currency c2 where fixedcallrate.currency=c1.code and fixedcallrate.sourcecountryid=? and fixedcallrate.destinationcountryid=? and c2.code=?");
            ps.setInt(1, sourceCountryId);
            ps.setInt(2, destinationCountryId);
            ps.setString(3, currencyCode);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (sourceIsLandline) {
                    if (rs.getObject("LandlineToLandline") != null) {
                        callRates.put("CallbackToLandlineRate", rs.getDouble("LandlineToLandline"));
                    }
                    if (rs.getObject("LandlineToLandlineSignallingFee") != null) {
                        callRates.put("CallbackToLandlineSignallingFee", rs.getDouble("LandlineToLandlineSignallingFee"));
                    }
                    if (rs.getObject("LandlineToMobile") != null) {
                        callRates.put("CallbackToMobileRate", rs.getDouble("LandlineToMobile"));
                    }
                    if (rs.getObject("LandlineToMobileSignallingFee") != null) {
                        callRates.put("CallbackToMobileSignallingFee", rs.getDouble("LandlineToMobileSignallingFee"));
                    }
                } else {
                    if (rs.getObject("MobileToLandline") != null) {
                        callRates.put("CallbackToLandlineRate", rs.getDouble("MobileToLandline"));
                    }
                    if (rs.getObject("MobileToLandlineSignallingFee") != null) {
                        callRates.put("CallbackToLandlineSignallingFee", rs.getDouble("MobileToLandlineSignallingFee"));
                    }
                    if (rs.getObject("MobileToMobile") != null) {
                        callRates.put("CallbackToMobileRate", rs.getDouble("MobileToMobile"));
                    }
                    if (rs.getObject("MobileToMobileSignallingFee") != null) {
                        callRates.put("CallbackToMobileSignallingFee", rs.getDouble("MobileToMobileSignallingFee"));
                    }
                }
                if (callThroughSupported) {
                    if (rs.getObject("CallThroughToLandline") != null) {
                        callRates.put("CallThroughToLandlineRate", rs.getDouble("CallThroughToLandline"));
                    }
                    if (rs.getObject("CallThroughToLandlineSignallingFee") != null) {
                        callRates.put("CallThroughToLandlineSignallingFee", rs.getDouble("CallThroughToLandlineSignallingFee"));
                    }
                    if (rs.getObject("CallThroughToMobile") != null) {
                        callRates.put("CallThroughToMobileRate", rs.getDouble("CallThroughToMobile"));
                    }
                    if (rs.getObject("CallThroughToMobileSignallingFee") != null) {
                        callRates.put("CallThroughToMobileSignallingFee", rs.getDouble("CallThroughToMobileSignallingFee"));
                    }
                }
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select Cost / c1.ExchangeRate * c2.ExchangeRate smscost from fixedsmscost, currency c1, currency c2 where fixedsmscost.currency=c1.code and fixedsmscost.countryid=? and c2.code=?");
            ps.setInt(1, sourceCountryId);
            ps.setString(2, currencyCode);
            rs = ps.executeQuery();
            if (rs.next()) {
                callRates.put("SMSCost", rs.getString("smscost"));
            }
            callRates.put("Currency", currencyCode);
            Object var12_12 = null;
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
                break block45;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block45;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var12_13 = null;
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
        return callRates;
    }

    /*
     * Loose catch block
     */
    public int getUserProfilePrivacySetting(String username) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select status from userprofile where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
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
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return n;
        }
        int n = -1;
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
        return n;
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

    public boolean setUserProfilePrivacySetting(String username, int privacySetting) throws EJBException {
        if (privacySetting < 1 || privacySetting > 3) {
            throw new EJBException("Invalid privacy setting");
        }
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserProfileData userProfileData = userBean.getUserProfile(username, username, true);
            if (userProfileData == null) {
                userProfileData = new UserProfileData();
            }
            userProfileData.username = username;
            userProfileData.status = UserProfileData.StatusEnum.fromValue(privacySetting);
            userBean.updateUserProfile(userProfileData);
            return true;
        }
        catch (CreateException e) {
        }
        catch (EJBException e) {
        }
        catch (FusionEJBException fusionEJBException) {
            // empty catch block
        }
        return false;
    }

    /*
     * Loose catch block
     */
    public int getUserBlockedListCount(String username) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select count(*) from blocklist where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
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
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return n;
        }
        int n = 0;
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
        return n;
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

    /*
     * Loose catch block
     */
    public Hashtable getUserBlockedList(String username, String search, int pageNumber, int resultsPerPage, boolean orderByDate, boolean ascending) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        Vector<String> blockVector = new Vector<String>();
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        conn = this.dataSourceSlave.getConnection();
        String sql = "select BlockUsername from blocklist where username=?";
        if (search.length() > 0) {
            sql = sql + " and BlockUsername like ?";
        }
        sql = orderByDate ? sql + " order by BlockUsername " : sql + " order by BlockUsername ";
        sql = ascending ? sql + " asc" : sql + " desc";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        if (search.length() > 0) {
            search = search + "%";
            ps.setString(2, search);
        }
        if ((rs = ps.executeQuery()).next()) {
            rs.absolute((pageNumber - 1) * resultsPerPage + 1);
            for (int i = 0; i < resultsPerPage && !rs.isAfterLast(); ++i) {
                blockVector.add(rs.getString(1));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)resultsPerPage)));
        hash.put("page", Integer.valueOf(pageNumber));
        hash.put("blocked_users", blockVector);
        Hashtable<String, Serializable> hashtable = hash;
        Object var16_16 = null;
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
        return hashtable;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var16_17 = null;
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
    public boolean isContact(String userName, String contactName) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from contact where username=? and fusionusername=?");
            ps.setString(1, userName);
            ps.setString(2, contactName);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            boolean bl = true;
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
            return bl;
        }
        boolean bl = false;
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
        return bl;
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
    public boolean isContactFriend(String username, String contactname) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select c.id from Contact c, Contact x where c.username=? and c.fusionusername=? and x.username = c.fusionusername and x.fusionusername = c.username limit 1");
            ps.setString(1, username);
            ps.setString(2, contactname);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            boolean bl = true;
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
            return bl;
        }
        boolean bl = false;
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
        return bl;
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
    public Hashtable getUserContactDetails(String userName, String contactName) {
        Hashtable hashtable;
        Hashtable hash = new Hashtable();
        Connection connSlave = null;
        try {
            try {
                hash = this.getContact(userName, contactName);
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                connSlave = this.dataSourceSlave.getConnection();
                Set friends = userEJB.loadBroadcastList(contactName, connSlave);
                connSlave.close();
                connSlave = null;
                hash.put("isContact", friends.contains(userName));
                hash.put("privacy", userEJB.getUserProfileStatus(contactName).toString());
                hash.put("numFriends", friends.size());
                hashtable = hash;
                Object var9_9 = null;
            }
            catch (Exception e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var9_10 = null;
            try {
                if (connSlave == null) throw throwable;
                connSlave.close();
                throw throwable;
            }
            catch (SQLException e2) {
                throw throwable;
            }
        }
        try {}
        catch (SQLException e2) {
            // empty catch block
            return hashtable;
        }
        if (connSlave == null) return hashtable;
        connSlave.close();
        return hashtable;
    }

    public boolean unblockUser(String username, String unblockUsername) throws EJBException {
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            contactBean.unblockContact(username, unblockUsername, false);
        }
        catch (Exception ex) {
            throw new EJBException(ex.getMessage());
        }
        return true;
    }

    /*
     * Loose catch block
     */
    public boolean unblockAllUsers(String username) {
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select BlockUsername from blocklist where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                String blockname = rs.getString("blockusername");
                this.unblockUser(username, blockname);
            }
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
                catch (Exception ex) {
                    throw new EJBException(ex.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
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

    private Hashtable createPagingUserEventHashtable(UserEventIce[] userEvents, int pageNumber, int resultsPerPage) {
        try {
            int startPage;
            int endPage;
            Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
            int totalPages = (int)Math.ceil((double)userEvents.length / (double)resultsPerPage);
            if (pageNumber <= 0) {
                pageNumber = 0;
            }
            if (pageNumber > totalPages) {
                pageNumber = totalPages;
            }
            if ((endPage = (startPage = (pageNumber - 1) * resultsPerPage) + resultsPerPage) >= userEvents.length) {
                endPage = userEvents.length;
            }
            hash.put("totalEventCount", Integer.valueOf(userEvents.length));
            hash.put("totalPages", Integer.valueOf(totalPages));
            hash.put("page", Integer.valueOf(pageNumber));
            if (userEvents.length == 0) {
                hash.put("events", new Vector());
                return hash;
            }
            EventTextTranslator translator = new EventTextTranslator();
            Vector vect = new Vector();
            for (int i = startPage; i < endPage; ++i) {
                Hashtable<String, Object> eventHash = new Hashtable<String, Object>();
                UserEventIce event = userEvents[i];
                eventHash.put("eventType", UserEvent.getEventType(event).toString());
                eventHash.put("text", translator.translate(event, ClientType.MIDP2, null));
                eventHash.put("timestamp", event.timestamp);
                vect.add(eventHash);
            }
            hash.put("events", vect);
            return hash;
        }
        catch (Exception e) {
            throw new EJBException(e.toString());
        }
    }

    public Hashtable getPagingUserEventGeneratedByUser(String username, int pageNumber, int resultsPerPage) {
        Hashtable hash = null;
        if (!SystemProperty.getBool(SystemPropertyEntities.Default.EVENT_SYSTEM_ENABLED)) {
            return new Hashtable();
        }
        EventSystemPrx eventSystem = EJBIcePrxFinder.getEventSystemProxy();
        try {
            UserEventIce[] userEvents = eventSystem.getUserEventsGeneratedByUser(username);
            hash = this.createPagingUserEventHashtable(userEvents, pageNumber, resultsPerPage);
        }
        catch (FusionException fe) {
            throw new EJBException(fe.getMessage());
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return hash;
    }

    public Hashtable getPagingUserEvents(String username, int pageNumber, int resultsPerPage) {
        Hashtable hash = null;
        if (!SystemProperty.getBool(SystemPropertyEntities.Default.EVENT_SYSTEM_ENABLED)) {
            return new Hashtable();
        }
        EventSystemPrx eventSystem = EJBIcePrxFinder.getEventSystemProxy();
        try {
            UserEventIce[] userEvents = eventSystem.getUserEventsForUser(username);
            hash = this.createPagingUserEventHashtable(userEvents, pageNumber, resultsPerPage);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return hash;
    }

    /*
     * Loose catch block
     */
    public boolean editPhotoCaption(String username, int itemId, String description) {
        block15: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update scrapbook set description=? where id=?");
            ps.setString(1, description);
            ps.setInt(2, itemId);
            ps.execute();
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, String.valueOf(itemId));
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.SCRAPBOOK, username);
            Object var8_6 = null;
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
                break block15;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block15;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getThemes(String username, int pageNumber, int resultsPerPage) throws EJBException {
        int end;
        int start;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select * from theme where status = ? order by id desc");
        ps.setInt(1, ThemeData.StatusEnum.AVAILABLE.value());
        rs = ps.executeQuery();
        int totalResults = 0;
        while (rs.next()) {
            ++totalResults;
        }
        rs.beforeFirst();
        int totalPages = (int)Math.ceil((double)totalResults / (double)resultsPerPage);
        if (pageNumber > totalPages) {
            pageNumber = totalPages;
        }
        if ((start = (pageNumber - 1) * resultsPerPage) < 0) {
            start = 0;
        }
        if ((end = start + resultsPerPage - 1) > totalResults) {
            end = totalResults - 1;
        }
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        Vector themes = new Vector();
        int count = 0;
        while (rs.next()) {
            if (count >= start && count <= end) {
                Hashtable<String, Object> theme = new Hashtable<String, Object>();
                theme.put("id", rs.getInt("id"));
                theme.put("name", rs.getString("name"));
                theme.put("description", rs.getString("description"));
                themes.add(theme);
            }
            if (count > end) break;
            ++count;
        }
        hash.put("totalPages", Integer.valueOf(totalPages));
        hash.put("pageNumber", Integer.valueOf(pageNumber));
        hash.put("totalResults", Integer.valueOf(totalResults));
        hash.put("themes", themes);
        Hashtable<String, Serializable> hashtable = hash;
        Object var16_17 = null;
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
        return hashtable;
        catch (Exception e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var16_18 = null;
            }
            catch (Throwable throwable) {
                Object var16_19 = null;
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
            return hashtable2;
        }
    }

    /*
     * Loose catch block
     */
    public boolean changeTheme(String username, int themeID) throws EJBException {
        block25: {
            Connection connSlave = null;
            ResultSet rs = null;
            Statement ps = null;
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
            if (userPrx == null) {
                throw new EJBException("Unable to locate user proxy for " + username);
            }
            if (themeID == 0) {
                userPrx.themeChanged(null);
            } else {
                connSlave = this.dataSourceSlave.getConnection();
                ps = connSlave.prepareStatement("select * from theme where id = ? and status = ?");
                ps.setInt(1, themeID);
                ps.setInt(2, ThemeData.StatusEnum.AVAILABLE.value());
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EJBException("Theme ID " + themeID + " is not available to " + username);
                }
                String location = rs.getString("location");
                rs.close();
                ps.close();
                connSlave.close();
                userPrx.themeChanged(location);
            }
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            contentBean.incrementStoreItemSold(StoreItemData.TypeEnum.THEME, themeID, null);
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
                if (connSlave != null) {
                    connSlave.close();
                }
                break block25;
            }
            catch (SQLException e) {
                connSlave = null;
            }
            break block25;
            {
                catch (Exception e) {
                    throw new EJBException(e);
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
        return true;
    }

    /*
     * Loose catch block
     */
    public Vector getLocationsWithMerchantsInCountry(int countryID) throws EJBException {
        Vector locations;
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            locations = new Vector();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select id, name, sum(num) num from ( select l1.id, l1.name, count(*) num from location l1, merchantlocation where l1.countryid=? and l1.level=1 and l1.id=merchantlocation.locationid and merchantlocation.status=1 group by id, name union all select l1.id, l1.name, count(*) num from location l1, location l2, merchantlocation where l1.id=l2.parentlocationid and l2.countryid=? and l2.level=2 and l2.id=merchantlocation.locationid and merchantlocation.status=1 group by id, name union all select l1.id, l1.name, count(*) num from location l1, location l2, location l3, merchantlocation where l1.id=l2.parentlocationid and l2.id=l3.parentlocationid and l3.countryid=? and l3.level=3 and l3.id=merchantlocation.locationid and merchantlocation.status=1 group by id, name ) A group by id, name order by name");
            ps.setInt(1, countryID);
            ps.setInt(2, countryID);
            ps.setInt(3, countryID);
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> locationHash = new Hashtable<String, Object>();
                locationHash.put("id", rs.getInt("id"));
                locationHash.put("name", rs.getString("name"));
                locationHash.put("num", rs.getInt("num"));
                locations.add(locationHash);
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
        return locations;
    }

    /*
     * Loose catch block
     */
    public Vector getLocationsWithMerchantsInParentLocation(int parentLocationID) throws EJBException {
        Vector locations;
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            locations = new Vector();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select id, name, sum(num) num from ( select location.id, location.name, count(*) num from location, merchantlocation where location.parentlocationid=? and location.id=merchantlocation.locationid and merchantlocation.status=1 group by id, name union all select l1.id, l1.name, count(*) num from location l1, location l2, merchantlocation where l1.parentlocationid=? and l1.id=l2.parentlocationid and l2.id=merchantlocation.locationid and merchantlocation.status=1 group by id, name ) A group by id, name order by name");
            ps.setInt(1, parentLocationID);
            ps.setInt(2, parentLocationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> locationHash = new Hashtable<String, Object>();
                locationHash.put("id", rs.getInt("id"));
                locationHash.put("name", rs.getString("name"));
                locationHash.put("num", rs.getInt("num"));
                locations.add(locationHash);
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
        return locations;
    }

    /*
     * Loose catch block
     */
    public Vector getCountriesWithMerchants() throws EJBException {
        Vector countries;
        block22: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            countries = new Vector();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select country.id, country.name, count(*) num from country, location, merchantlocation where country.id = location.countryid and location.id=merchantlocation.locationid and merchantlocation.status=1 group by id, name order by country.name");
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> countryHash = new Hashtable<String, Object>();
                countryHash.put("id", rs.getInt("id"));
                countryHash.put("name", rs.getString("name"));
                countryHash.put("num", rs.getInt("num"));
                countries.add(countryHash);
            }
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
                Object var7_8 = null;
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
        return countries;
    }

    /*
     * Loose catch block
     */
    public Vector getLocationPath(int locationID) throws EJBException {
        Vector path;
        block24: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block21: {
                conn = null;
                ps = null;
                rs = null;
                path = new Vector();
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("SELECT l1.id AS id1, l1.name AS name1, l2.id AS id2, l2.name AS name2, l3.id AS id3, l3.name AS name3 FROM location AS l1 LEFT JOIN location AS l2 ON l2.id = l1.parentlocationid LEFT JOIN location AS l3 ON l3.id = l2.parentlocationid WHERE l1.id = ?");
                ps.setInt(1, locationID);
                rs = ps.executeQuery();
                if (!rs.next()) break block21;
                Hashtable<String, Object> locationHash = new Hashtable<String, Object>();
                if (rs.getInt(5) != 0) {
                    locationHash.put("id", rs.getInt(5));
                    locationHash.put("name", rs.getString(6));
                    path.add(locationHash);
                }
                locationHash = new Hashtable();
                if (rs.getInt(3) != 0) {
                    locationHash.put("id", rs.getInt(3));
                    locationHash.put("name", rs.getString(4));
                    path.add(locationHash);
                }
                locationHash = new Hashtable();
                if (rs.getInt(1) == 0) break block21;
                locationHash.put("id", rs.getInt(1));
                locationHash.put("name", rs.getString(2));
                path.add(locationHash);
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
        return path;
    }

    /*
     * Loose catch block
     */
    public Vector getMerchantsInLocation(int locationID) throws EJBException {
        Vector merchants;
        block27: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            merchants = new Vector();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from merchantlocation where locationid=? and status=1 order by name");
            ps.setInt(1, locationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, Object> merchantsHash = new Hashtable<String, Object>();
                merchantsHash.put("id", rs.getInt("id"));
                if (rs.getString("username") != null) {
                    merchantsHash.put("username", rs.getString("username"));
                }
                merchantsHash.put("name", rs.getString("name"));
                if (rs.getString("address") != null) {
                    merchantsHash.put("address", rs.getString("address"));
                }
                if (rs.getString("phonenumber") != null) {
                    merchantsHash.put("phonenumber", rs.getString("phonenumber"));
                }
                if (rs.getString("emailaddress") != null) {
                    merchantsHash.put("emailaddress", rs.getString("emailaddress"));
                }
                if (rs.getString("notes") != null) {
                    merchantsHash.put("notes", rs.getString("notes"));
                }
                merchants.add(merchantsHash);
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
        return merchants;
    }

    /*
     * Loose catch block
     */
    public Hashtable getMerchantLocation(int merchantID) throws EJBException {
        Hashtable<String, Object> merchantHash;
        block26: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            merchantHash = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from merchantlocation where id=? and status=1");
            ps.setInt(1, merchantID);
            rs = ps.executeQuery();
            while (rs.next()) {
                merchantHash.put("id", rs.getInt("id"));
                if (rs.getString("username") != null) {
                    merchantHash.put("username", rs.getString("username"));
                }
                merchantHash.put("name", rs.getString("name"));
                if (rs.getString("address") != null) {
                    merchantHash.put("address", rs.getString("address"));
                }
                if (rs.getString("phonenumber") != null) {
                    merchantHash.put("phonenumber", rs.getString("phonenumber"));
                }
                if (rs.getString("emailaddress") != null) {
                    merchantHash.put("emailaddress", rs.getString("emailaddress"));
                }
                if (rs.getString("notes") == null) continue;
                merchantHash.put("notes", rs.getString("notes"));
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
        return merchantHash;
    }

    /*
     * Loose catch block
     */
    public boolean countryHasMerchantLocations(int countryID) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from country, location, merchantlocation where country.id=location.countryid and location.id=merchantlocation.locationid and merchantlocation.status=1 and country.id=? limit 1");
            ps.setInt(1, countryID);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            boolean bl = true;
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
            return bl;
        }
        boolean bl = false;
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
        return bl;
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

    /*
     * Loose catch block
     */
    public String[] getMerchantsUserMayPurchaseFrom(String username) {
        ArrayList<String> possibleMerchants;
        String[] usernamesToReturn;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block42: {
            String sql;
            Random random;
            block38: {
                conn = null;
                ps = null;
                rs = null;
                usernamesToReturn = new String[4];
                possibleMerchants = new ArrayList<String>();
                random = new Random(System.currentTimeMillis());
                conn = this.dataSourceSlave.getConnection();
                sql = "select distinct ae_merchant.username from accountentry ae_user inner join accountentry ae_merchant on ae_user.reference=ae_merchant.id inner join user on ae_merchant.username=user.username where ae_user.type=14 and ae_user.amount > 0 and ae_user.username=? and user.type=3 and user.status=1 and user.fundedbalance > 0 and user.username != ? union distinct select distinct user.username from accountentry inner join voucher on accountentry.reference=voucher.id inner join voucherbatch on voucher.voucherbatchid=voucherbatch.id inner join user on voucherbatch.username=user.username where accountentry.type=2 and accountentry.username=? and user.type=3 and user.status=1 and user.fundedbalance > 0 and user.username != ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, username);
                ps.setString(3, username);
                ps.setString(4, username);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (possibleMerchants.size() == 0) {
                        possibleMerchants.add(rs.getString(1));
                        continue;
                    }
                    possibleMerchants.add(random.nextInt(possibleMerchants.size() + 1), rs.getString(1));
                }
                if (possibleMerchants.size() < 2) break block38;
                usernamesToReturn[0] = (String)possibleMerchants.get(0);
                usernamesToReturn[2] = (String)possibleMerchants.get(1);
                String[] stringArray = usernamesToReturn;
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                return stringArray;
            }
            rs.close();
            ps.close();
            sql = "select user.username from user inner join contact on user.username=contact.fusionusername where user.type=3 and user.status=1 and user.fundedbalance > 0 and contact.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (possibleMerchants.contains(rs.getString(1))) continue;
                if (possibleMerchants.size() == 0) {
                    possibleMerchants.add(rs.getString(1));
                    continue;
                }
                possibleMerchants.add(random.nextInt(possibleMerchants.size() + 1), rs.getString(1));
            }
            if (possibleMerchants.size() < 2) break block42;
            usernamesToReturn[0] = (String)possibleMerchants.get(0);
            usernamesToReturn[2] = (String)possibleMerchants.get(1);
            String[] stringArray = usernamesToReturn;
            Object var11_14 = null;
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
            return stringArray;
        }
        rs.close();
        ps.close();
        if (possibleMerchants.size() == 1) {
            usernamesToReturn[0] = (String)possibleMerchants.get(0);
        }
        String[] stringArray = usernamesToReturn;
        Object var11_15 = null;
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
        return stringArray;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_16 = null;
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

    public Hashtable getBuzzCost(String username, String contactUsername) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block20: {
            conn = null;
            ps = null;
            rs = null;
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select country.SMSBuzzCost / currency_buzz.ExchangeRate * currency_user.ExchangeRate as price, currency_user.Code Currency from user, user contact_user, currency currency_user, currency currency_buzz, country where currency_buzz.code =? and user.username=? and user.Currency=currency_user.Code and contact_user.username=? and country.ID = contact_user.countryID");
            ps.setString(1, CurrencyData.baseCurrency);
            ps.setString(2, username);
            ps.setString(3, contactUsername);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            hash.put("price", Double.parseDouble(rs.getString("price")));
            hash.put("currency", rs.getString("Currency"));
            Hashtable<String, Object> hashtable = hash;
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
            return hashtable;
        }
        try {
            try {
                throw new EJBException("Buzz cost not found.");
            }
            catch (Exception ex) {
                throw new EJBException(ex.getMessage());
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
    public String getSMSCost(String username, String phoneNumberWithIddCode) throws EJBException {
        String value;
        block35: {
            ResultSet rs;
            Statement ps;
            Connection conn;
            block29: {
                conn = null;
                ps = null;
                rs = null;
                value = "";
                if (!StringUtil.isBlank(username) && !StringUtil.isBlank(phoneNumberWithIddCode)) break block29;
                String string = "";
                Object var11_11 = null;
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
                return string;
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            CountryData countryData = misEJB.getCountryByIDDCode(messageEJB.getIDDCode(phoneNumberWithIddCode), phoneNumberWithIddCode);
            if (countryData == null) {
                throw new EJBException("Unable to determine the country for mobile phone " + phoneNumberWithIddCode);
            }
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select concat_ws(' ', format(? * c.exchangerate, 2), u.currency) from currency c, user u where u.username=? and u.currency=c.code");
            ps.setDouble(1, countryData.smsCost);
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Unable to calculate sms cost for destination: " + phoneNumberWithIddCode);
            }
            value = rs.getString(1);
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
                if (conn != null) {
                    conn.close();
                }
                break block35;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block35;
            {
                catch (CreateException e) {
                    log.error((Object)e);
                    throw new EJBException("Unable to calculate sms cost for destination: " + phoneNumberWithIddCode);
                }
                catch (SQLException e) {
                    log.error((Object)e);
                    throw new EJBException("Unable to calculate sms cost for destination: " + phoneNumberWithIddCode);
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
        return value;
    }

    /*
     * Loose catch block
     */
    public Hashtable getSMSCostTable(String search, int page, int numEntries) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        Vector sms_countries = new Vector();
        conn = this.dataSourceSlave.getConnection();
        String sql = "select SMSCost  / currency_lookout.ExchangeRate * currency_user.ExchangeRate as price, currency_user.Code Currency, country.name from country, currency currency_user, currency currency_lookout where currency_lookout.code = 'AUD' and currency_user.Code = 'USD'";
        if (search.length() > 0) {
            search = search + "%";
            sql = sql + " and country.name like ?";
        }
        ps = conn.prepareStatement(sql);
        if (search.length() > 0) {
            ps.setString(1, search);
        }
        if ((rs = ps.executeQuery()).next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                Hashtable<String, Object> h = new Hashtable<String, Object>();
                h.put("country", rs.getString("country.name"));
                h.put("cost", Float.valueOf(rs.getFloat("price")));
                sms_countries.add(h);
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("sms_countries", sms_countries);
        Hashtable<String, Serializable> hashtable = hash;
        Object var13_13 = null;
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
        return hashtable;
        catch (SQLException ex) {
            try {
                throw new EJBException(ex.getMessage());
            }
            catch (Throwable throwable) {
                Object var13_14 = null;
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

    public Hashtable getLocalSMSCost(String username, int countryId) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block20: {
            conn = null;
            ps = null;
            rs = null;
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select c.cost / cc.exchangerate * uc.exchangerate as price, u.currency from currency cc, currency uc, user u, ( \tselect \tif(f.cost is null, c.smscost, f.cost) cost, \tif(f.cost is null, 'AUD', f.currency) currency \tfrom \tcountry c left outer join \tfixedsmscost f on (c.id = f.countryid) \twhere \tc.id = ? ) c where c.currency = cc.code and u.currency = uc.code and u.username = ?");
            ps.setInt(1, countryId);
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            hash.put("price", Double.parseDouble(rs.getString("price")));
            hash.put("currency", rs.getString("Currency"));
            Hashtable<String, Object> hashtable = hash;
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
            return hashtable;
        }
        try {
            try {
                throw new EJBException("Local SMS cost not found.");
            }
            catch (SQLException ex) {
                throw new EJBException(ex.getMessage());
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

    public Hashtable getLookoutCost(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block20: {
            conn = null;
            ps = null;
            rs = null;
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select country.SMSLookoutCost / currency_lookout.ExchangeRate * currency_user.ExchangeRate as price, currency_user.Code Currency from user, currency currency_user, currency currency_lookout, country where currency_lookout.code = 'AUD' and user.username=? and user.Currency=currency_user.Code and user.countryID = country.ID");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            hash.put("price", Double.parseDouble(rs.getString("price")));
            hash.put("currency", rs.getString("Currency"));
            Hashtable<String, Object> hashtable = hash;
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
            return hashtable;
        }
        try {
            try {
                throw new EJBException("Lookout cost not found.");
            }
            catch (Exception ex) {
                throw new EJBException(ex.getMessage());
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

    public Hashtable getGroupSMSNotificationCost(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block20: {
            conn = null;
            ps = null;
            rs = null;
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select ? / currency_lookout.ExchangeRate * currency_user.ExchangeRate as price, currency_user.Code Currency from user, currency currency_user, currency currency_lookout where currency_lookout.code = 'AUD' and user.username=? and user.Currency=currency_user.Code");
            ps.setDouble(1, SystemProperty.getDouble("GroupSMSNotificationCost"));
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            hash.put("price", Double.parseDouble(rs.getString("price")));
            hash.put("currency", rs.getString("Currency"));
            Hashtable<String, Object> hashtable = hash;
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
            return hashtable;
        }
        try {
            try {
                throw new EJBException("Lookout cost not found.");
            }
            catch (Exception ex) {
                throw new EJBException(ex.getMessage());
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

    public Hashtable getKickCost(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block20: {
            conn = null;
            ps = null;
            rs = null;
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select ? / currency_lookout.ExchangeRate * currency_user.ExchangeRate as price, currency_user.Code Currency from user, currency currency_user, currency currency_lookout where currency_lookout.code = 'AUD' and user.username=? and user.Currency=currency_user.Code");
            ps.setDouble(1, SystemProperty.getDouble("ChatRoomKickCost"));
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            hash.put("price", Double.parseDouble(rs.getString("price")));
            hash.put("currency", rs.getString("Currency"));
            Hashtable<String, Object> hashtable = hash;
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
            return hashtable;
        }
        try {
            try {
                throw new EJBException("ChatRoomKickCost cost not found.");
            }
            catch (Exception ex) {
                throw new EJBException(ex.getMessage());
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

    public Hashtable getEmailAlertCost(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block20: {
            conn = null;
            ps = null;
            rs = null;
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select country.SMSEmailAlertCost / currency_emailalert.ExchangeRate * currency_user.ExchangeRate as price, currency_user.Code Currency from user, currency currency_user, currency currency_emailalert, country where currency_emailalert.code = 'AUD' and user.username=? and user.Currency=currency_user.Code and user.countryID = country.ID");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block20;
            hash.put("price", Double.parseDouble(rs.getString("price")));
            hash.put("currency", rs.getString("Currency"));
            Hashtable<String, Object> hashtable = hash;
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
            return hashtable;
        }
        try {
            try {
                throw new EJBException("Email alert cost not found.");
            }
            catch (Exception ex) {
                throw new EJBException(ex.getMessage());
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
    public Hashtable getTransactionSummaryByMonth(String username, int month, int year, String type) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        Hashtable<String, Number> hash = new Hashtable<String, Number>();
        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }
        int maxAEPeriodBeforeArchival = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS);
        GregorianCalendar accountEntryArchivalBoundary = new GregorianCalendar();
        accountEntryArchivalBoundary.setTime(new Date());
        ((Calendar)accountEntryArchivalBoundary).add(5, -1 * maxAEPeriodBeforeArchival);
        GregorianCalendar startCal = new GregorianCalendar(year, month - 1, 1);
        GregorianCalendar endCal = new GregorianCalendar(year, month, 1);
        if (startCal.before(accountEntryArchivalBoundary)) {
            startCal = accountEntryArchivalBoundary;
        }
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select count(*), -sum(amount) from accountentry where username = ? and type in (?,?) and datecreated >= ? and datecreated < ? and amount < 0");
        ps.setString(1, username);
        ps.setInt(2, AccountEntryData.TypeEnum.USER_TO_USER_TRANSFER.value());
        ps.setInt(3, AccountEntryData.TypeEnum.VOUCHERS_CREATED.value());
        ps.setTimestamp(4, new Timestamp(startCal.getTime().getTime()));
        ps.setTimestamp(5, new Timestamp(endCal.getTime().getTime()));
        rs = ps.executeQuery();
        rs.next();
        hash.put("totalSales", rs.getDouble(2));
        hash.put("numberOfSales", rs.getInt(1));
        rs.close();
        ps.close();
        ps = conn.prepareStatement("select count(*), sum(amount) from accountentry where username = ? and type in (?,?,?,?,?,?) and datecreated >= ? and datecreated < ? and amount > 0");
        ps.setString(1, username);
        ps.setInt(2, AccountEntryData.TypeEnum.CREDIT_CARD.value());
        ps.setInt(3, AccountEntryData.TypeEnum.VOUCHER_RECHARGE.value());
        ps.setInt(4, AccountEntryData.TypeEnum.TELEGRAPHIC_TRANSFER.value());
        ps.setInt(5, AccountEntryData.TypeEnum.BANK_TRANSFER.value());
        ps.setInt(6, AccountEntryData.TypeEnum.WESTERN_UNION.value());
        ps.setInt(7, AccountEntryData.TypeEnum.BLUE_LABEL_ONE_VOUCHER.value());
        ps.setTimestamp(8, new Timestamp(startCal.getTime().getTime()));
        ps.setTimestamp(9, new Timestamp(endCal.getTime().getTime()));
        rs = ps.executeQuery();
        rs.next();
        hash.put("totalCredits", rs.getDouble(2));
        hash.put("numberOfCredits", rs.getInt(1));
        Hashtable<String, Number> hashtable = hash;
        Object var15_15 = null;
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
        return hashtable;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var15_16 = null;
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
    protected List<AccountEntryData> getAccountEntriesForCustomerByDate(String username, String customername, Date startDate, Date endDate, String accountType) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double balance = 0.0;
        String typeString = "";
        if (accountType != null && accountType.length() != 0) {
            String[] types = accountType.split(",");
            for (int i = 0; i < types.length; ++i) {
                String addType = types[i].trim();
                typeString = typeString + (typeString.length() > 0 ? "," : "") + AccountEntryData.TypeEnum.valueOf(addType).value();
            }
        }
        int maxAEPeriodBeforeArchival = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS);
        GregorianCalendar accountEntryArchivalBoundary = new GregorianCalendar();
        accountEntryArchivalBoundary.setTime(new Date());
        ((Calendar)accountEntryArchivalBoundary).add(5, -1 * maxAEPeriodBeforeArchival);
        if (startDate.getTime() < accountEntryArchivalBoundary.getTimeInMillis()) {
            startDate = accountEntryArchivalBoundary.getTime();
        }
        conn = this.dataSourceSlave.getConnection();
        String sql = "SELECT a1.* FROM accountentry a1, accountentry a2 WHERE a1.datecreated>=? and a1.datecreated<=? and a1.type != 17 and a1.reference=CAST(a2.id AS CHAR) AND a2.username=? AND a2.amount<0 and a1.username=? union select accountentry.* from accountentry join voucher on voucher.voucherbatchid = accountentry.reference where username=? and type=17 and redeemedby=?";
        ps = conn.prepareStatement(sql);
        ps.setDate(1, new java.sql.Date(startDate.getTime()));
        ps.setDate(2, new java.sql.Date(endDate.getTime()));
        ps.setString(3, customername);
        ps.setString(4, username);
        ps.setString(5, username);
        ps.setString(6, customername);
        rs = ps.executeQuery();
        LinkedList<AccountEntryData> accountEntryList = new LinkedList<AccountEntryData>();
        while (rs.next()) {
            AccountEntryData accountEntry = new AccountEntryData();
            accountEntry.id = rs.getLong("id");
            accountEntry.username = rs.getString("username");
            accountEntry.dateCreated = new Date(rs.getTimestamp("dateCreated").getTime());
            accountEntry.type = AccountEntryData.TypeEnum.fromValue(rs.getInt("type"));
            accountEntry.reference = rs.getString("reference");
            accountEntry.description = rs.getString("description");
            accountEntry.currency = rs.getString("currency");
            accountEntry.exchangeRate = rs.getDouble("exchangerate");
            accountEntry.amount = rs.getDouble("amount");
            accountEntry.fundedAmount = rs.getDouble("fundedAmount");
            accountEntry.tax = rs.getDouble("tax");
            accountEntry.costOfGoodsSold = rs.getDouble("costOfGoodsSold");
            accountEntry.costOfTrial = rs.getDouble("costOfTrial");
            accountEntry.runningBalance = balance;
            balance += rs.getDouble("amount");
            accountEntryList.add(accountEntry);
        }
        LinkedList<AccountEntryData> linkedList = accountEntryList;
        Object var18_19 = null;
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
        return linkedList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var18_20 = null;
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

    public Hashtable getTransactionSummaryForCustomerByDate(String username, String customername, Date startDate, Date endDate, String type) {
        Hashtable<String, Number> hash = new Hashtable<String, Number>();
        try {
            double totalSales = 0.0;
            int numberOfSales = 0;
            List<AccountEntryData> accountEntries = this.getAccountEntriesForCustomerByDate(username, customername, startDate, endDate, type);
            block5: for (int i = 0; i < accountEntries.size(); ++i) {
                AccountEntryData.TypeEnum entryType = accountEntries.get((int)i).type;
                switch (entryType) {
                    case USER_TO_USER_TRANSFER: 
                    case VOUCHERS_CREATED: {
                        if (!(accountEntries.get((int)i).amount < 0.0)) continue block5;
                        totalSales += accountEntries.get((int)i).amount * accountEntries.get((int)i).exchangeRate;
                        ++numberOfSales;
                        continue block5;
                    }
                }
            }
            hash.put("totalSales", totalSales);
            hash.put("numberOfSales", numberOfSales);
            hash.put("totalCredits", 0);
            hash.put("numberOfCredits", 0);
            return hash;
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Hashtable getTransactionSummaryByMonthForCustomer(String username, String customername, int month, int year, String type) {
        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }
        try {
            GregorianCalendar startCal = new GregorianCalendar(year, month - 1, 1);
            GregorianCalendar endCal = new GregorianCalendar(year, month - 1, ((Calendar)startCal).getMaximum(5));
            return this.getTransactionSummaryForCustomerByDate(username, customername, startCal.getTime(), endCal.getTime(), type);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Hashtable getTransactionSummaryForCustomer(String username, String customername, String type) {
        GregorianCalendar startCal = new GregorianCalendar(1970, 1, 1);
        GregorianCalendar endCal = new GregorianCalendar();
        return this.getTransactionSummaryForCustomerByDate(username, customername, startCal.getTime(), endCal.getTime(), type);
    }

    public Hashtable getTransactions(String username, int pagenumber, int resultsperpage, String type) {
        GregorianCalendar startCal = new GregorianCalendar(1970, 1, 1);
        GregorianCalendar endCal = new GregorianCalendar();
        return this.getTransactionsByDate(username, startCal.getTime(), endCal.getTime(), pagenumber, resultsperpage, type);
    }

    public Hashtable getTransactionsSinceDate(String username, int fromDate, int pagenumber, int resultsperpage, String type) {
        GregorianCalendar endCal = new GregorianCalendar();
        Date fromd = new Date(fromDate);
        return this.getTransactionsByDate(username, fromd, endCal.getTime(), pagenumber, resultsperpage, type);
    }

    public Hashtable getTransactionsSinceDateAndType(String username, int fromDate, int pagenumber, int resultsperpage, int type) {
        GregorianCalendar endCal = new GregorianCalendar();
        Date fromd = new Date(fromDate);
        String typeString = "";
        switch (type) {
            case 1: {
                typeString = "CREDIT_CARD,VOUCHER_RECHARGE,TELEGRAPHIC_TRANSFER,BANK_TRANSFER,WESTERN_UNION,BLUE_LABEL_ONE_VOUCHER";
                break;
            }
            case 2: {
                typeString = "USER_TO_USER_TRANSFER,VOUCHERS_CREATED";
                break;
            }
            case 3: {
                typeString = "USER_TO_USER_TRANSFER,VOUCHERS_CREATED,CREDIT_CARD,VOUCHER_RECHARGE,TELEGRAPHIC_TRANSFER,BANK_TRANSFER,WESTERN_UNION,BLUE_LABEL_ONE_VOUCHER";
            }
        }
        return this.getTransactionsByDate(username, fromd, endCal.getTime(), pagenumber, resultsperpage, typeString);
    }

    /*
     * Loose catch block
     */
    public Hashtable getTransactionsByDate(String username, Date fromDate, Date toDate, int pagenumber, int resultsperpage, String accountType) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String typeString = "";
        if (accountType != null && accountType.length() != 0) {
            String[] types = accountType.split(",");
            for (int i = 0; i < types.length; ++i) {
                String addType = types[i].trim();
                typeString = typeString + (typeString.length() > 0 ? "," : "") + AccountEntryData.TypeEnum.valueOf(addType).value();
            }
        }
        int maxAEPeriodBeforeArchival = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS);
        GregorianCalendar accountEntryArchivalBoundary = new GregorianCalendar();
        accountEntryArchivalBoundary.setTime(new Date());
        ((Calendar)accountEntryArchivalBoundary).add(5, -1 * maxAEPeriodBeforeArchival);
        if (fromDate.getTime() < accountEntryArchivalBoundary.getTimeInMillis()) {
            fromDate = accountEntryArchivalBoundary.getTime();
        }
        conn = this.dataSourceSlave.getConnection();
        String sql = "select sourceaccount.*, destaccount.username destusername from accountentry as sourceaccount left join accountentry as destaccount on destaccount.id = sourceaccount.reference where sourceaccount.username=? and sourceaccount.datecreated>=? and sourceaccount.datecreated<=? and sourceaccount.amount != 0 ";
        if (typeString.length() > 0) {
            sql = sql + " and sourceaccount.type in (" + typeString + ") ";
        }
        sql = sql + "order by sourceaccount.datecreated desc limit ? offset ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setDate(2, new java.sql.Date(fromDate.getTime()));
        ps.setDate(3, new java.sql.Date(toDate.getTime() + 86400000L));
        ps.setInt(4, resultsperpage + 1);
        ps.setInt(5, (pagenumber - 1) * resultsperpage);
        rs = ps.executeQuery();
        Hashtable<String, Object> hash = new Hashtable<String, Object>();
        Vector<Hashtable> v = new Vector<Hashtable>();
        double balance = 0.0;
        boolean hasMore = false;
        while (rs.next()) {
            if (rs.getRow() > resultsperpage) {
                hasMore = true;
                break;
            }
            Hashtable acchash = HashObjectUtils.dataObjectToHashtable(new AccountEntryData(rs));
            acchash.put("destinationUsername", rs.getString("destusername"));
            acchash.put("runningBalance", balance);
            v.add(acchash);
            balance += rs.getDouble("sourceaccount.amount");
        }
        hash.put("totalResults", v.size());
        hash.put("page", pagenumber);
        hash.put("hasMore", hasMore);
        hash.put("accountEntries", v);
        Hashtable<String, Object> hashtable = hash;
        Object var21_22 = null;
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
        return hashtable;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var21_23 = null;
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
    public int getUserReferralCountByMonth(String username, int month, int year) {
        ResultSet rs;
        Statement ps;
        Connection conn;
        block28: {
            conn = null;
            ps = null;
            rs = null;
            if (month < 1) {
                month = 1;
            }
            if (month > 12) {
                month = 12;
            }
            GregorianCalendar startCal = new GregorianCalendar(year, month - 1, 1);
            GregorianCalendar endCal = new GregorianCalendar(year, month - 1, ((Calendar)startCal).getMaximum(5));
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select count(id) as referrals from userreferral where username=? and datecreated>=? and datecreated<=?");
            ps.setString(1, username);
            ps.setDate(2, new java.sql.Date(startCal.getTime().getTime()));
            ps.setDate(3, new java.sql.Date(endCal.getTime().getTime()));
            rs = ps.executeQuery();
            if (!rs.next()) break block28;
            int n = rs.getInt("referrals");
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            return n;
        }
        int n = 0;
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
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        return n;
        catch (SQLException ex) {
            try {
                throw new EJBException(ex.getMessage());
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
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
    public int getUserReferralCount(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select count(id) as referrals from userreferral where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            int n = rs.getInt("referrals");
            Object var7_8 = null;
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
            return n;
        }
        int n = 0;
        Object var7_9 = null;
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
        return n;
        catch (SQLException ex) {
            try {
                throw new EJBException(ex.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
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
    public Hashtable getUserReferral(String username, Date startDate, Date endDate, int pagenumber, int resultsperpage) {
        int end;
        int start;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select userreferral.mobilephone as phonenumber, user.username as username, userreferral.datecreated as datecreated from userreferral left join user on user.mobilephone = userreferral.mobilephone where userreferral.username=? and userreferral.datecreated>=? and userreferral.datecreated<=?");
        ps.setString(1, username);
        ps.setDate(2, new java.sql.Date(startDate.getTime()));
        ps.setDate(3, new java.sql.Date(endDate.getTime()));
        rs = ps.executeQuery();
        int totalresults = 0;
        while (rs.next()) {
            ++totalresults;
        }
        rs.beforeFirst();
        int remainder = totalresults % resultsperpage;
        int totalpages = totalresults / resultsperpage + (remainder != 0 ? 1 : 0);
        if (totalpages < 0) {
            totalpages = 0;
        }
        if (pagenumber > totalpages) {
            pagenumber = totalpages;
        }
        if ((start = (pagenumber - 1) * resultsperpage) < 0) {
            start = 0;
        }
        if ((end = start + resultsperpage - 1) >= totalresults) {
            end = totalresults - 1;
        }
        Vector v = new Vector();
        int count = 0;
        while (rs.next()) {
            if (count >= start && count <= end) {
                Hashtable<String, Object> referral = new Hashtable<String, Object>();
                referral.put("mobilephone", rs.getString("phonenumber"));
                referral.put("username", rs.getString("username") == null ? "" : rs.getString("username"));
                referral.put("datecreated", rs.getDate("datecreated").getTime());
                v.add(referral);
            }
            if (count > end) break;
            ++count;
        }
        hash.put("totalresults", Integer.valueOf(totalresults));
        hash.put("pagenumber", Integer.valueOf(pagenumber));
        hash.put("totalpages", Integer.valueOf(totalpages));
        hash.put("invitations", v);
        Hashtable<String, Serializable> hashtable = hash;
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
        }
        catch (SQLException e) {
            conn = null;
        }
        return hashtable;
        catch (SQLException ex) {
            try {
                throw new EJBException(ex.getMessage());
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
    }

    public Hashtable getUserReferralByMonth(String username, int month, int year, int pagenumber, int resultsperpage) {
        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }
        try {
            GregorianCalendar startCal = new GregorianCalendar(year, month - 1, 1);
            GregorianCalendar endCal = new GregorianCalendar(year, month, 1);
            return this.getUserReferral(username, startCal.getTime(), endCal.getTime(), pagenumber, resultsperpage);
        }
        catch (EJBException ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    public Hashtable getUserReferral(String username, int pagenumber, int resultsperpage) {
        try {
            GregorianCalendar startCal = new GregorianCalendar(1970, 1, 1);
            GregorianCalendar endCal = new GregorianCalendar();
            ((Calendar)endCal).add(6, 1);
            return this.getUserReferral(username, startCal.getTime(), endCal.getTime(), pagenumber, resultsperpage);
        }
        catch (EJBException ex) {
            throw new EJBException(ex.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public String getPreviousMobileNumber(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select activation.mobilephone as mobilephone from user left join activation on activation.username = user.username where user.mobileverified = 0 and user.username=? order by activation.datecreated desc limit 1");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            String string = rs.getString("mobilephone");
            Object var7_8 = null;
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
            return string;
        }
        String string = "";
        Object var7_9 = null;
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
        return string;
        catch (SQLException ex) {
            try {
                throw new EJBException(ex.getMessage());
            }
            catch (Throwable throwable) {
                Object var7_10 = null;
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

    public String changeMobileNumber(String username, String mobileNumber, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.changeMobilePhone(username, mobileNumber, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
    }

    public String cancelMobileNumberChange(String username, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.cancelChangeMobilePhoneRequest(username, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getUserVirtualGift(String session_username, String username, int giftid) {
        Connection conn = null;
        ResultSet rs = null;
        Statement ps = null;
        conn = this.dataSourceSlave.getConnection();
        String sql = "select virtualgiftreceived.id as id, virtualgiftreceived.virtualgiftid as giftid, virtualgiftreceived.datecreated as datecreated, virtualgiftreceived.sender as sender, virtualgift.name as name, virtualgift.location64x64png as location, virtualgiftreceived.message as message, virtualgiftreceived.removed as removed, virtualgiftreceived.private as private from virtualgiftreceived, virtualgift where username=? and virtualgift.id = virtualgiftreceived.virtualgiftid and virtualgiftreceived.id = ? ";
        if (!session_username.equals(username)) {
            sql = sql + " and virtualgiftreceived.private=0 ";
        }
        sql = sql + "order by virtualgiftreceived.datecreated desc";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setInt(2, giftid);
        rs = ps.executeQuery();
        Hashtable<String, Object> gift = new Hashtable<String, Object>();
        if (rs.next()) {
            gift.put("id", rs.getInt("id"));
            gift.put("giftid", rs.getInt("giftid"));
            gift.put("sender", rs.getString("sender"));
            gift.put("datecreated", rs.getTimestamp("datecreated").getTime());
            gift.put("location", rs.getString("location"));
            gift.put("name", rs.getString("name"));
            gift.put("message", rs.getString("message") == null ? "" : rs.getString("message"));
            gift.put("removed", rs.getString("removed"));
            gift.put("private", rs.getInt("private"));
        }
        Hashtable<String, Object> hashtable = gift;
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
        return hashtable;
        catch (Exception e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var11_13 = null;
            }
            catch (Throwable throwable) {
                Object var11_14 = null;
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
            return hashtable2;
        }
    }

    /*
     * Loose catch block
     */
    public boolean removeUserVirtualGift(String username, int giftID) {
        block26: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block23: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update virtualgiftreceived set removed=1 where username = ? and id=?");
                ps.setString(1, username);
                ps.setInt(2, giftID);
                if (ps.executeUpdate() != 1) break block23;
                if (SystemProperty.getBool("UseRedisDataStore", true)) {
                    try {
                        UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                        int userId = userEJB.getUserID(username, null);
                        if (userId == -1) {
                            log.error((Object)("Unable to retrieve User ID for username [" + username + "]"));
                            throw new EJBException("Invalid username specified");
                        }
                        GiftsReceivedCounter.decrementCacheCount(userId);
                        break block23;
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to decrement gifts received counter for username [" + username + "]: " + e));
                        throw new EJBException("Invalid username specified");
                    }
                }
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.NUM_VIRTUAL_GIFTS_RECEIVED, username);
            }
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
                break block26;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block26;
            {
                catch (SQLException ex) {
                    throw new EJBException(ex.getMessage());
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getVirtualGiftForUser(String session_username, String username, int pageNumber, int resultsPerPage) {
        int end;
        int start;
        Connection conn = null;
        ResultSet rs = null;
        Statement ps = null;
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        conn = this.dataSourceSlave.getConnection();
        String sql = "select virtualgiftreceived.id as id, virtualgiftreceived.virtualgiftid as giftid, virtualgiftreceived.datecreated as datecreated, virtualgiftreceived.sender as sender, virtualgift.name as name, virtualgift.location16x16gif as location, virtualgiftreceived.message as message, virtualgiftreceived.removed as removed, virtualgiftreceived.private as private from virtualgiftreceived, virtualgift where username=? and virtualgift.id = virtualgiftreceived.virtualgiftid and virtualgiftreceived.removed = 0 ";
        if (!session_username.equals(username)) {
            sql = sql + " and virtualgiftreceived.private=0 ";
        }
        sql = sql + "order by virtualgiftreceived.id desc limit 100";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        rs = ps.executeQuery();
        int totalResults = 0;
        while (rs.next()) {
            ++totalResults;
        }
        rs.beforeFirst();
        int totalPages = (int)Math.ceil((double)totalResults / (double)resultsPerPage);
        if (pageNumber > totalPages) {
            pageNumber = totalPages;
        }
        if ((start = (pageNumber - 1) * resultsPerPage) < 0) {
            start = 0;
        }
        if ((end = start + resultsPerPage - 1) > totalResults) {
            end = totalResults - 1;
        }
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        Vector gifts = new Vector();
        int count = 0;
        while (rs.next()) {
            if (count >= start && count <= end) {
                Hashtable<String, Object> gift = new Hashtable<String, Object>();
                gift.put("id", rs.getInt("id"));
                gift.put("giftid", rs.getInt("giftid"));
                gift.put("sender", rs.getString("sender"));
                gift.put("datecreated", rs.getTimestamp("datecreated").getTime());
                gift.put("location", rs.getString("location"));
                gift.put("name", rs.getString("name"));
                gift.put("message", rs.getString("message") == null ? "" : rs.getString("message"));
                gift.put("removed", rs.getInt("removed"));
                gift.put("private", rs.getInt("private"));
                gifts.add(gift);
            }
            if (count > end) break;
            ++count;
        }
        hash.put("totalPages", Integer.valueOf(totalPages));
        hash.put("pageNumber", Integer.valueOf(pageNumber));
        hash.put("totalResults", Integer.valueOf(totalResults));
        hash.put("virtualgifts", gifts);
        Hashtable<String, Serializable> hashtable = hash;
        Object var18_19 = null;
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
        return hashtable;
        catch (Exception e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var18_20 = null;
            }
            catch (Throwable throwable) {
                Object var18_21 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getVirtualGiftReceivedSummary(String session_username, String username) {
        Hashtable<String, Serializable> hashtable = new Hashtable<String, Serializable>();
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        String sql = "SELECT vin.id AS id, vgr.sender AS sender, vg.id AS giftid, vgr.datecreated AS datecreated, vin.giftcount AS giftcount, vg.location16x16gif AS location, vg.name AS NAME FROM virtualgiftreceived vgr, virtualgift vg, (SELECT MAX(id) AS id, COUNT(*) AS giftcount FROM virtualgiftreceived WHERE username=? AND removed = 0  GROUP BY virtualgiftid) vin WHERE vgr.id = vin.id AND vg.id = vgr.virtualgiftid ";
        if (!session_username.equals(username)) {
            sql = sql + " AND private = 0 ";
        }
        sql = sql + " ORDER BY vin.id DESC";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        rs = ps.executeQuery();
        int totalResults = 0;
        while (rs.next()) {
            ++totalResults;
        }
        rs.beforeFirst();
        Vector v = new Vector();
        while (rs.next()) {
            Hashtable<String, Object> hash = new Hashtable<String, Object>();
            hash.put("id", rs.getInt("id"));
            hash.put("giftid", rs.getInt("giftid"));
            hash.put("sender", rs.getString("sender"));
            hash.put("datecreated", rs.getDate("datecreated").getTime());
            hash.put("giftcount", rs.getInt("giftcount"));
            hash.put("location", rs.getString("location"));
            hash.put("name", rs.getString("name"));
            v.add(hash);
        }
        hashtable.put("total", Integer.valueOf(totalResults));
        hashtable.put("gifts", v);
        Hashtable<String, Serializable> hashtable2 = hashtable;
        Object var12_13 = null;
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
        return hashtable2;
        catch (Exception ex) {
            Hashtable hashtable3;
            try {
                hashtable3 = ExceptionHelper.getRootMessageAsHashtable(ex);
                Object var12_14 = null;
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
            return hashtable3;
        }
    }

    public Hashtable getVirtualGift(String username, int giftId) {
        Hashtable<String, Object> content = new Hashtable<String, Object>();
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            VirtualGiftData gift = contentBean.getVirtualGift(giftId, null, username);
            if (gift != null) {
                content.put("id", gift.getId());
                content.put("name", gift.getName());
                content.put("price", gift.getRoundedPrice());
                content.put("currency", gift.getCurrency());
                content.put("location", gift.getLocation16x16GIF());
                content.put("largelocation", gift.getLocation64x64PNG());
                content.put("status", gift.getStatus().toString());
            } else {
                content.put("error", "Unable to find gift");
            }
            return content;
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Hashtable getVirtualGiftContent(String username, int groupID, int pageNumber, int numEntries) throws EJBException {
        Hashtable<String, Serializable> content = new Hashtable<String, Serializable>();
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        try {
            int end;
            int start;
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            List virtualGifts = contentBean.getVirtualGifts(username, groupID, 0);
            int totalGifts = virtualGifts.size();
            int totalPages = (int)Math.ceil((double)totalGifts / (double)numEntries);
            if (pageNumber > totalPages) {
                pageNumber = totalPages;
            }
            if ((start = (pageNumber - 1) * numEntries) < 0) {
                start = 0;
            }
            if ((end = start + numEntries - 1) >= totalGifts) {
                end = totalGifts - 1;
            }
            content.put("page", Integer.valueOf(pageNumber));
            content.put("numEntries", Integer.valueOf(totalGifts));
            content.put("numPages", Integer.valueOf(totalPages));
            Vector v = new Vector();
            for (int i = start; i <= end; ++i) {
                VirtualGiftData gift = (VirtualGiftData)virtualGifts.get(i);
                Hashtable<String, Object> contentItem = new Hashtable<String, Object>();
                contentItem.put("id", gift.getId());
                contentItem.put("name", gift.getName());
                contentItem.put("price", gift.getRoundedPrice());
                contentItem.put("currency", gift.getCurrency());
                contentItem.put("location", gift.getLocation16x16GIF());
                v.add(contentItem);
            }
            content.put("gifts", v);
            return content;
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public String buyVirtualGift(String username, String receiverUsername, int giftId, int purchaseLocation, boolean privateGift, String message, String chatroomName, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            int rateLimitSeconds = SystemProperty.getInt("GiftSingleRateLimitInSeconds", 60);
            if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.VIRTUALGIFT.toString(), MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.VIRTUAL_GIFT_RATE_LIMIT, username, receiverUsername, Integer.toString(giftId)), 1L, (long)(rateLimitSeconds * 1000))) {
                return String.format("You can only send the same gift to %s every %s. Try sending a different gift.", receiverUsername, DateTimeUtils.timeInvertalInSecondsToPrettyString(rateLimitSeconds));
            }
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            contentBean.buyVirtualGift(username, receiverUsername.trim().toLowerCase(), giftId, purchaseLocation, privateGift, message, chatroomName, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "";
        }
        catch (EJBException e) {
            return "Unable to send gift now. Please try again later.";
        }
        catch (FusionEJBException e) {
            return e.getMessage();
        }
        catch (CreateException e) {
            return "Unable to send gift now. Please try again later.";
        }
    }

    public String buyAvatarItem(int buyerUserID, int recipientUserID, int avatarItemID, String ipAddress, String sessionID, String mobileDevice, String userAgent) {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            contentBean.buyAvatarItem(buyerUserID, recipientUserID, avatarItemID, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    /*
     * Loose catch block
     */
    public Vector getExternalClientDownloadDetail(String version) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<Hashtable> v = new Vector<Hashtable>();
        String sql = "select * from externaldownloadlink where status = 1";
        if (version.length() > 0) {
            sql = sql + " and version=?";
        }
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement(sql);
        if (version.length() > 0) {
            ps.setString(1, version);
        }
        rs = ps.executeQuery();
        int startRange = 1;
        while (rs.next()) {
            ExternalDownloadLinkData ext = new ExternalDownloadLinkData(rs);
            ext.setRange(startRange);
            startRange = ext.endRange + 1;
            v.add(HashObjectUtils.dataObjectToHashtable(ext));
        }
        Vector<Hashtable> vector = v;
        Object var10_10 = null;
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
        return vector;
        catch (SQLException ex) {
            try {
                throw new EJBException(ex.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_11 = null;
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
    public float getUnredeemedVoucherValue(String merchantUsername) {
        float amount;
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select u.currency as currency, sum(b.amount / bc.exchangerate * uc.exchangerate) as amount from user u, voucherbatch b, voucher v, currency uc, currency bc where u.username = b.username and b.id = v.voucherbatchid and u.currency = uc.code and b.currency = bc.code and b.username = ? and v.status = 1 group by u.currency");
            ps.setString(1, merchantUsername);
            rs = ps.executeQuery();
            if (rs.next()) break block26;
            float f = 0.0f;
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
            return f;
        }
        float f = amount = rs.getFloat("amount");
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
        return f;
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
    public Hashtable getMerchantCustomersAndFriends(String merchantUsername, int pagenumber, int resultsperpage) {
        int end;
        int start;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT username FROM user WHERE merchantcreated=? UNION select fusionusername from contact where username = ? and fusionusername is not null and status = 1 UNION SELECT DISTINCT LCASE(a1.username) username FROM accountentry a1, accountentry a2 WHERE a1.type=14 AND a1.reference=CAST(a2.id AS CHAR) AND a2.type=14 AND a2.username=? AND a2.amount<0 UNION select distinct lcase(accountentry.username) username from accountentry join voucher on voucher.voucherbatchid = accountentry.reference and voucher.status=3 where username=? and type=17 ORDER BY username");
        ps.setString(1, merchantUsername);
        ps.setString(2, merchantUsername);
        ps.setString(3, merchantUsername);
        ps.setString(4, merchantUsername);
        rs = ps.executeQuery();
        int totalresults = 0;
        while (rs.next()) {
            ++totalresults;
        }
        rs.beforeFirst();
        int totalpages = totalresults / resultsperpage + 1;
        if (totalpages < 1) {
            totalpages = 1;
        }
        if (pagenumber > totalpages) {
            pagenumber = totalpages;
        }
        if ((start = (pagenumber - 1) * resultsperpage) < 0) {
            start = 0;
        }
        if ((end = start + resultsperpage - 1) >= totalresults) {
            end = totalresults - 1;
        }
        int count = 0;
        Vector<String> possibleRecipients = new Vector<String>();
        while (rs.next()) {
            if (count >= start && count <= end) {
                possibleRecipients.add(rs.getString(1));
            }
            if (count > end) break;
            ++count;
        }
        hash.put("totalresults", Integer.valueOf(totalresults));
        hash.put("totalpages", Integer.valueOf(totalpages));
        hash.put("page", Integer.valueOf(pagenumber));
        hash.put("recipients", possibleRecipients);
        Hashtable<String, Serializable> hashtable = hash;
        Object var16_16 = null;
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
        return hashtable;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var16_17 = null;
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
    public Hashtable getMerchantCustomers(String merchantUsername, int pagenumber, int resultsperpage) {
        int end;
        int start;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT username FROM user WHERE merchantcreated=? UNION SELECT DISTINCT LCASE(a1.username) username FROM accountentry a1, accountentry a2 WHERE a1.type=14 AND a1.reference=CAST(a2.id AS CHAR) AND a2.type=14 AND a2.username=? AND a2.amount<0 UNION select distinct lcase(accountentry.username) username from accountentry join voucher on voucher.voucherbatchid = accountentry.reference and voucher.status=3 where username=? and type=17 ORDER BY username");
        ps.setString(1, merchantUsername);
        ps.setString(2, merchantUsername);
        ps.setString(3, merchantUsername);
        rs = ps.executeQuery();
        int totalresults = 0;
        while (rs.next()) {
            ++totalresults;
        }
        rs.beforeFirst();
        int totalpages = totalresults / resultsperpage + 1;
        if (totalpages < 1) {
            totalpages = 1;
        }
        if (pagenumber > totalpages) {
            pagenumber = totalpages;
        }
        if ((start = (pagenumber - 1) * resultsperpage) < 0) {
            start = 0;
        }
        if ((end = start + resultsperpage - 1) >= totalresults) {
            end = totalresults - 1;
        }
        int count = 0;
        Vector<String> possibleRecipients = new Vector<String>();
        while (rs.next()) {
            if (count >= start && count <= end) {
                possibleRecipients.add(rs.getString(1));
            }
            if (count > end) break;
            ++count;
        }
        hash.put("totalresults", Integer.valueOf(totalresults));
        hash.put("totalpages", Integer.valueOf(totalpages));
        hash.put("page", Integer.valueOf(pagenumber));
        hash.put("recipients", possibleRecipients);
        Hashtable<String, Serializable> hashtable = hash;
        Object var16_16 = null;
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
        return hashtable;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var16_17 = null;
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
    public Hashtable getMerchantCustomerTransactions(String merchantUsername, String username, int pagenumber, int resultsperpage) {
        int end;
        int start;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        int maxAEPeriodBeforeArchival = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_ACCOUNTENTRY_PERIOD_BEFORE_ARCHIVAL_IN_DAYS);
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT a1.* FROM accountentry a1, accountentry a2 WHERE a1.type=14 AND a1.reference=CAST(a2.id AS CHAR) AND a2.type=14 AND a2.username=? AND a2.amount<0 and a1.username=? and a1.datecreated >= date_sub(curdate(), interval ? day) union select accountentry.* from accountentry join voucher on voucher.voucherbatchid = accountentry.reference where username=? and type=17 and redeemedby=? and accountentry.datecreated >= date_sub(curdate(), interval ? day)");
        ps.setString(1, username);
        ps.setString(2, merchantUsername);
        ps.setInt(3, maxAEPeriodBeforeArchival);
        ps.setString(4, merchantUsername);
        ps.setString(5, username);
        ps.setInt(6, maxAEPeriodBeforeArchival);
        rs = ps.executeQuery();
        int totalresults = 0;
        while (rs.next()) {
            ++totalresults;
        }
        rs.beforeFirst();
        int totalpages = totalresults / resultsperpage + 1;
        if (totalpages < 1) {
            totalpages = 1;
        }
        if (pagenumber > totalpages) {
            pagenumber = totalpages;
        }
        if ((start = (pagenumber - 1) * resultsperpage) < 0) {
            start = 0;
        }
        if ((end = start + resultsperpage - 1) >= totalresults) {
            end = totalresults - 1;
        }
        int count = 0;
        Vector<Hashtable> entries = new Vector<Hashtable>();
        while (rs.next()) {
            if (count >= start && count <= end) {
                AccountEntryData aed = new AccountEntryData(rs);
                entries.add(HashObjectUtils.dataObjectToHashtable(aed));
            }
            if (count > end) break;
            ++count;
        }
        hash.put("totalresults", Integer.valueOf(totalresults));
        hash.put("totalpages", Integer.valueOf(totalpages));
        hash.put("page", Integer.valueOf(pagenumber));
        hash.put("account_entries", entries);
        Hashtable<String, Serializable> hashtable = hash;
        Object var18_18 = null;
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
        return hashtable;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var18_19 = null;
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

    public String getMostRecentlyVisitedChatroom(String username, String chatroomPrefix) throws EJBException {
        List<String> recentChatRoomList = RecentChatRoomList.getRecentChatRoomList(recentChatRoomMemcache, username);
        if (recentChatRoomList != null) {
            for (String recentChatRoom : recentChatRoomList) {
                if (!recentChatRoom.startsWith(chatroomPrefix)) continue;
                return recentChatRoom;
            }
        }
        return "";
    }

    public Hashtable getGroup(int id) throws EJBException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            GroupData groupData = userEJB.getGroup(id);
            if (groupData == null) {
                return new Hashtable();
            }
            return HashObjectUtils.dataObjectToHashtable(groupData);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupAnnouncements(int groupID, int page, int numEntries) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select a.*, u.displaypicture from groupannouncement a, user u where a.createdby = u.username and a.groupid = ? and a.status = ? order by id desc");
        ps.setInt(1, groupID);
        ps.setInt(2, GroupAnnouncementData.StatusEnum.ACTIVE.value());
        Vector<Hashtable> groupAnnouncements = new Vector<Hashtable>();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                GroupAnnouncementData announcementData = new GroupAnnouncementData(rs);
                announcementData.picture = rs.getString("displaypicture");
                groupAnnouncements.add(HashObjectUtils.dataObjectToHashtable(announcementData));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("group_announcements", groupAnnouncements);
        Hashtable<String, Serializable> hashtable = hash;
        Object var12_13 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupAnnouncement(int id) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block32: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select a.*, u.displaypicture from groupannouncement a, user u where a.createdby = u.username and a.id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (!rs.next()) break block32;
            GroupAnnouncementData announcementData = new GroupAnnouncementData(rs);
            announcementData.picture = rs.getString("displaypicture");
            Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(announcementData);
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
            return hashtable;
        }
        Hashtable announcementData = new Hashtable();
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
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return announcementData;
        catch (SQLException e) {
            Hashtable hashtable;
            try {
                hashtable = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var8_12 = null;
            }
            catch (Throwable throwable) {
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return hashtable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable createGroupAnnouncement(String username, int groupID, String title, String text, String smsText) throws EJBException {
        GroupAnnouncementData announcementData;
        ResultSet rs;
        Statement ps;
        Connection connMaster;
        block41: {
            connMaster = null;
            ps = null;
            rs = null;
            announcementData = new GroupAnnouncementData();
            announcementData.groupID = groupID;
            announcementData.dateCreated = new Date();
            announcementData.createdBy = username;
            announcementData.title = title;
            announcementData.text = text;
            announcementData.smsText = smsText == null || smsText.length() == 0 ? null : smsText;
            announcementData.lastModifiedDate = announcementData.dateCreated;
            announcementData.lastModifiedBy = username;
            announcementData.status = GroupAnnouncementData.StatusEnum.ACTIVE;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("insert into groupannouncement (groupid, datecreated, createdby, title, text, smsText, lastmodifieddate, lastmodifiedby, status) values (?,?,?,?,?,?,?,?,?)", 1);
            ps.setObject(1, announcementData.groupID);
            ps.setTimestamp(2, new Timestamp(announcementData.dateCreated.getTime()));
            ps.setString(3, announcementData.createdBy);
            ps.setString(4, announcementData.title);
            ps.setString(5, announcementData.text);
            ps.setString(6, announcementData.smsText);
            ps.setTimestamp(7, new Timestamp(announcementData.lastModifiedDate.getTime()));
            ps.setString(8, announcementData.lastModifiedBy);
            ps.setObject(9, announcementData.status == null ? null : Integer.valueOf(announcementData.status.value()));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) break block41;
            Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable("Failed to insert group announcement into database");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return hashtable;
        }
        announcementData.id = rs.getInt(1);
        rs.close();
        ps.close();
        connMaster.close();
        try {
            UserNotification note;
            UserNotificationServicePrx userNotificationService = EJBIcePrxFinder.getUserNotificationServiceProxy();
            try {
                note = new EmailUserNotification();
                note.subject = title;
                note.message = text;
                userNotificationService.notifyFusionGroupAnnouncementViaEmail(groupID, (EmailUserNotification)note);
            }
            catch (FusionException fe) {
                log.warn((Object)"FusionException while notifying group members of a new announcement via email", (Throwable)((Object)fe));
            }
            catch (Exception e) {
                log.warn((Object)"Exception while notifying group members of a new announcement via email", (Throwable)e);
            }
            if (StringUtils.hasLength((String)smsText)) {
                try {
                    note = new SMSUserNotification();
                    ((SMSUserNotification)note).message = smsText;
                    ((SMSUserNotification)note).smsSubType = SystemSMSData.SubTypeEnum.GROUP_ANNOUNCEMENT_NOTIFICATION.value();
                    userNotificationService.notifyFusionGroupAnnouncementViaSMS(groupID, (SMSUserNotification)note);
                }
                catch (FusionException fe) {
                    log.warn((Object)"FusionException while notifying group members of a new announcement via SMS", (Throwable)((Object)fe));
                }
                catch (Exception e) {
                    log.warn((Object)"Exception while notifying group members of a new announcement via SMS", (Throwable)e);
                }
            }
        }
        catch (Exception e) {
            log.warn((Object)"Exception while notifying group members of a new announcement", (Throwable)e);
        }
        Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(announcementData);
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var13_17 = null;
            }
            catch (Throwable throwable) {
                Object var13_18 = null;
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable updateGroupAnnouncement(int announcementID, String username, int groupID, String title, String text) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("update groupannouncement set title = ?, text = ?, lastmodifieddate = ?, lastmodifiedby = ? where id = ?");
        ps.setString(1, title);
        ps.setString(2, text);
        ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        ps.setString(4, username);
        ps.setInt(5, announcementID);
        if (ps.executeUpdate() != 1) {
            ExceptionHelper.setErrorMessageAsHashtable("Failed to update group announcement");
        }
        Hashtable hashtable = this.getGroupAnnouncement(announcementID);
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
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_12 = null;
            }
            catch (Throwable throwable) {
                Object var12_13 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupChatroomCategoriesAndStadiums(int groupID, int parentChatRoomCategoryID, int page, int numEntries) throws EJBException {
        Hashtable<String, Object> hash;
        Connection connSlave = null;
        Statement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        String sql = "select cc.id, cc.name from chatroomcategory cc, chatroom cr where cr.chatroomcategoryid=cc.id and cr.groupid=? and cr.status=1 and cc.status=1 and cc.groupeventonly=0 ";
        if (parentChatRoomCategoryID > 0) {
            sql = sql + "and cc.parentchatroomcategoryid=? ";
        }
        sql = sql + "union select cc.id, cc.name from chatroomcategory cc, groupevent ge where cc.id = ge.chatroomcategoryid and cc.status=1 and ge.status=1 and ge.groupid=? AND UNIX_TIMESTAMP(now()) >= UNIX_TIMESTAMP(date_add(ge.starttime, interval -2 day)) and UNIX_TIMESTAMP(now()) <= (UNIX_TIMESTAMP(ge.starttime) + (ge.durationminutes*60)) ";
        if (parentChatRoomCategoryID > 0) {
            sql = sql + "and cc.parentchatroomcategoryid=? ";
        }
        sql = sql + "group by id, name order by id";
        ps = connSlave.prepareStatement(sql);
        if (parentChatRoomCategoryID > 0) {
            ps.setInt(1, groupID);
            ps.setInt(2, parentChatRoomCategoryID);
            ps.setInt(3, groupID);
            ps.setInt(4, parentChatRoomCategoryID);
        } else {
            ps.setInt(1, groupID);
            ps.setInt(2, groupID);
        }
        Vector chatroomCategories = new Vector();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                hash = new Hashtable();
                hash.put("id", rs.getInt("id"));
                hash.put("name", rs.getString("name"));
                chatroomCategories.add(hash);
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        hash = new Hashtable<String, Object>();
        hash.put("totalresults", size);
        hash.put("totalpages", Math.ceil((double)size / (double)numEntries));
        hash.put("page", page);
        hash.put("chatroom_categories", chatroomCategories);
        rs.close();
        ps.close();
        Vector<Hashtable> stadiums = new Vector<Hashtable>();
        ps = connSlave.prepareStatement("select * from chatroom where groupid = ? and status = 1 and type = 2 order by datecreated");
        ps.setInt(1, groupID);
        rs = ps.executeQuery();
        while (rs.next()) {
            ChatRoomData chatRoomData = new ChatRoomData(rs);
            stadiums.add(HashObjectUtils.dataObjectToHashtable(chatRoomData));
        }
        hash.put("stadiums", stadiums);
        Hashtable<String, Object> hashtable = hash;
        Object var15_16 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var15_17 = null;
            }
            catch (Throwable throwable) {
                Object var15_18 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupChatrooms(int groupID, int chatRoomCategoryID, int page, int numEntries) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = chatRoomCategoryID > 0 ? conn.prepareStatement("select * from chatroom where groupid = ? and status = 1 and chatroomcategoryid = ? order by type desc, datecreated") : conn.prepareStatement("select * from chatroom where groupid = ? and status = 1 and chatroomcategoryid is null order by type desc, datecreated");
        ps.setInt(1, groupID);
        if (chatRoomCategoryID > 0) {
            ps.setInt(2, chatRoomCategoryID);
        }
        Vector<Hashtable> chatrooms = new Vector<Hashtable>();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                ChatRoomData chatRoomData = new ChatRoomData(rs);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomData.name);
                if (chatRoomPrx != null) {
                    chatRoomData.size = chatRoomPrx.getNumParticipants();
                }
                chatrooms.add(HashObjectUtils.dataObjectToHashtable(chatRoomData));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("group_chatrooms", chatrooms);
        Hashtable<String, Serializable> hashtable = hash;
        Object var13_14 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var13_15 = null;
            }
            catch (Throwable throwable) {
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return hashtable2;
        }
    }

    public Hashtable getGroupDonators(int groupID, int page, int numEntries) throws EJBException {
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(0));
        hash.put("totalpages", Integer.valueOf(0));
        hash.put("page", Integer.valueOf(0));
        hash.put("group_donators", new Vector());
        return hash;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public String joinGroup(String username, int groupID, int locationID, String ipAddress, String sessionID, String mobileDevice, String userAgent, boolean smsNotification, boolean emailNotification, boolean eventNotification, boolean smsGroupEventNotification, boolean emailThreadUpdateNotification, boolean eventThreadUpdateNotification) throws EJBException {
        block109: {
            block105: {
                block101: {
                    block97: {
                        block93: {
                            block89: {
                                block85: {
                                    block81: {
                                        connSlave = null;
                                        ps = null;
                                        rs = null;
                                        if (groupID != SystemProperty.getInt("IndosatGroupID") || this.isIndosatIP(ipAddress)) break block81;
                                        var17_17 = ExceptionHelper.setErrorMessage("You must be using Indosat network to join this group");
                                        var24_20 = null;
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
                                        return var17_17;
                                    }
                                    userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                                    userData = userBean.loadUser(username, false, false);
                                    if (userData != null && userData.status == UserData.StatusEnum.ACTIVE) break block85;
                                    var19_43 = ExceptionHelper.setErrorMessage("Invalid username " + username);
                                    var24_21 = null;
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
                                    return var19_43;
                                }
                                if (AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.JOIN_GROUP, userData) || !SystemProperty.getBool("JoinGroupDisabledForUnauthenticatedUsers", false)) break block89;
                                var19_44 = ExceptionHelper.setErrorMessage("You must be authenticated before joining a group.");
                                var24_22 = null;
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
                                return var19_44;
                            }
                            groupData = userBean.getGroup(groupID);
                            if (groupData != null && groupData.status == GroupData.StatusEnum.ACTIVE) break block93;
                            var20_46 = ExceptionHelper.setErrorMessage("Invalid group ID " + groupID);
                            var24_23 = null;
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
                            return var20_46;
                        }
                        if (groupData.countryID == null || groupData.countryID.equals(userData.countryID)) break block97;
                        var20_47 = ExceptionHelper.setErrorMessage("You are not allowed to join a group from a different country");
                        var24_24 = null;
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
                        return var20_47;
                    }
                    maxGroupMembership = SystemProperty.getInt("MaxGroupMembership", 200);
                    connSlave = this.dataSourceSlave.getConnection();
                    ps = connSlave.prepareStatement("select count(*) from groupmember where username = ? and status = ?");
                    ps.setString(1, username);
                    ps.setInt(2, GroupMemberData.StatusEnum.ACTIVE.value());
                    rs = ps.executeQuery();
                    if (!rs.next() || rs.getInt(1) < maxGroupMembership) break block101;
                    var21_49 = ExceptionHelper.setErrorMessage("You cannot be a member of more than " + maxGroupMembership + " groups. Please leave some groups first");
                    var24_25 = null;
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
                    return var21_49;
                }
                rs.close();
                ps.close();
                connSlave.close();
                if (!groupData.isClosedGroup()) ** GOTO lbl222
                connSlave = this.dataSourceSlave.getConnection();
                ps = connSlave.prepareStatement("select count(*) from groupinvitation where username = ? and groupid = ?");
                ps.setString(1, username);
                ps.setInt(2, groupID);
                rs = ps.executeQuery();
                if (!rs.next() || rs.getInt(1) != 0) break block105;
                var21_50 = ExceptionHelper.setErrorMessage("You are not allowed to join this group without an invitation");
                var24_26 = null;
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
                return var21_50;
            }
            rs.close();
            ps.close();
            connSlave.close();
lbl222:
            // 2 sources

            if (!(messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class)).isUserBlackListedInGroup(username, groupID)) break block109;
            var22_52 = ExceptionHelper.setErrorMessage("You have been blacklisted from this group. Please contact the admin or a moderator of this group.");
            var24_27 = null;
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
            return var22_52;
        }
        this.joinGroupWithoutValidation(userData, groupID, locationID, ipAddress, sessionID, mobileDevice, userAgent, smsNotification, emailNotification, eventNotification, smsGroupEventNotification, emailThreadUpdateNotification, eventThreadUpdateNotification);
        var22_53 = "TRUE";
        var24_28 = null;
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
        return var22_53;
        catch (Exception e) {
            try {
                var18_42 = ExceptionHelper.getRootMessage(e);
                var24_29 = null;
            }
            catch (Throwable var23_54) {
                var24_30 = null;
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
                throw var23_54;
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
                if (connSlave != null) {
                    connSlave.close();
                }
            }
            catch (SQLException e) {
                connSlave = null;
            }
            return var18_42;
        }
    }

    /*
     * Unable to fully structure code
     */
    public boolean joinGroupWithoutValidation(UserData userData, int groupID, int locationID, String ipAddress, String sessionID, String mobileDevice, String userAgent, boolean smsNotification, boolean emailNotification, boolean eventNotification, boolean smsGroupEventNotification, boolean emailThreadUpdateNotification, boolean eventThreadUpdateNotification) throws EJBException {
        block40: {
            block37: {
                block33: {
                    connMaster = null;
                    ps = null;
                    rs = null;
                    isOldMember = false;
                    connMaster = this.dataSourceMaster.getConnection();
                    ps = connMaster.prepareStatement("select status from groupmember where username = ? and groupid = ?");
                    ps.setString(1, userData.username);
                    ps.setInt(2, groupID);
                    rs = ps.executeQuery();
                    if (!rs.next()) ** GOTO lbl45
                    status = rs.getInt("status");
                    if (status == GroupMemberData.StatusEnum.BANNED.value()) {
                        throw new EJBException("You are banned from joining this group");
                    }
                    if (status != GroupMemberData.StatusEnum.ACTIVE.value()) break block33;
                    var19_23 = false;
                    var21_24 = null;
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
                    return var19_23;
                }
                isOldMember = true;
lbl45:
                // 2 sources

                rs.close();
                ps.close();
                ps = connMaster.prepareStatement("update groups set nummembers=nummembers+1 where id=?");
                ps.setInt(1, groupID);
                ps.executeUpdate();
                ps.close();
                if (isOldMember) {
                    ps = connMaster.prepareStatement("update groupmember set dateleft = null, locationid = ?, smsnotification = ?, emailnotification = ?, eventnotification = ?, smsgroupeventnotification = ?, emailthreadupdatenotification = ?, eventthreadupdatenotification = ?, status = ? where username = ? and groupid = ?");
                    ps.setObject(1, locationID > 0 ? Integer.valueOf(locationID) : null);
                    ps.setInt(2, smsNotification != false ? 1 : 0);
                    ps.setInt(3, emailNotification != false ? 1 : 0);
                    ps.setInt(4, eventNotification != false ? 1 : 0);
                    ps.setInt(5, smsGroupEventNotification != false ? 1 : 0);
                    ps.setInt(6, emailThreadUpdateNotification != false ? 1 : 0);
                    ps.setInt(7, eventThreadUpdateNotification != false ? 1 : 0);
                    ps.setInt(8, GroupMemberData.StatusEnum.ACTIVE.value());
                    ps.setString(9, userData.username);
                    ps.setInt(10, groupID);
                } else {
                    ps = connMaster.prepareStatement("insert into groupmember (username, groupid, locationid, datecreated, type, smsnotification, emailnotification, eventnotification, smsgroupeventnotification, emailthreadupdatenotification, eventthreadupdatenotification, status) values (?,?,?,?,?,?,?,?,?,?,?,?)");
                    ps.setString(1, userData.username);
                    ps.setInt(2, groupID);
                    ps.setObject(3, locationID > 0 ? Integer.valueOf(locationID) : null);
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                    ps.setInt(5, GroupMemberData.TypeEnum.REGULAR.value());
                    ps.setInt(6, smsNotification != false ? 1 : 0);
                    ps.setInt(7, emailNotification != false ? 1 : 0);
                    ps.setInt(8, eventNotification != false ? 1 : 0);
                    ps.setInt(9, smsGroupEventNotification != false ? 1 : 0);
                    ps.setInt(10, emailThreadUpdateNotification != false ? 1 : 0);
                    ps.setInt(11, eventThreadUpdateNotification != false ? 1 : 0);
                    ps.setInt(12, GroupMemberData.StatusEnum.ACTIVE.value());
                }
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Failed to add or update group member ship");
                }
                ps.close();
                ps = connMaster.prepareStatement("delete from groupinvitation where username = ? and groupid = ?");
                ps.setString(1, userData.username);
                ps.setInt(2, groupID);
                ps.executeUpdate();
                ps.close();
                this.removeGroupInviteNotification(userData.userID, groupID);
                if (isOldMember || !userData.mobileVerified.booleanValue() || groupID != SystemProperty.getInt("IndosatGroupID")) break block37;
                ps = connMaster.prepareStatement("select id from mobileoriginatedsms s, user u where s.sender = u.mobilephone and u.username = ? and s.type = ?");
                ps.setString(1, userData.username);
                ps.setInt(2, MobileOriginatedSMSData.TypeEnum.INDOSAT_SUBSCRIPTION.value());
                rs = ps.executeQuery();
                if (rs.next()) {
                    accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                    accountEJB.subscribeService(userData.username, SystemProperty.getInt("IndosatServiceID"), new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
                }
                rs.close();
                ps.close();
            }
            var21_25 = null;
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
                break block40;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block40;
            {
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
            catch (Throwable var20_30) {
                var21_26 = null;
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
                throw var20_30;
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String leaveGroup(String username, int groupID) throws EJBException {
        Connection connMaster = null;
        PreparedStatement ps = null;
        connMaster = this.dataSourceMaster.getConnection();
        ps = connMaster.prepareStatement("update groupmember set dateleft = ?, status = ?, type = ? where username = ? and groupid = ?");
        ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
        ps.setInt(2, GroupMemberData.StatusEnum.INACTIVE.value());
        ps.setInt(3, GroupMemberData.TypeEnum.REGULAR.value());
        ps.setString(4, username);
        ps.setInt(5, groupID);
        ps.executeUpdate();
        ps = connMaster.prepareStatement("update groups set nummembers=nummembers-1 where id=?");
        ps.setInt(1, groupID);
        ps.executeUpdate();
        String string = "TRUE";
        Object var8_7 = null;
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
        return string;
        catch (SQLException e) {
            String string2;
            try {
                string2 = ExceptionHelper.getRootMessage(e);
                Object var8_8 = null;
            }
            catch (Throwable throwable) {
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
            return string2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String declineAllGroupInvitations(String username) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("delete from groupinvitation where username = ?");
        ps.setString(1, username);
        ps.executeUpdate();
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userBean.loadUser(username, false, false);
            if (userData != null && userData.status == UserData.StatusEnum.ACTIVE) {
                int userId = userData.userID;
                UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                unsProxy.clearAllNotificationsByTypeForUser(userId, Enums.NotificationTypeEnum.GROUP_INVITE.getType());
            }
        }
        catch (Exception e) {
            log.warn((Object)("Failed to remove pending group invite notfication for user: " + username + ", reason: " + e.getLocalizedMessage()));
        }
        String e = "TRUE";
        Object var9_11 = null;
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
        return e;
        catch (SQLException e2) {
            String string;
            try {
                string = ExceptionHelper.getRootMessage(e2);
                Object var9_12 = null;
            }
            catch (Throwable throwable) {
                Object var9_13 = null;
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
            return string;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String declineGroupInvitation(String username, int groupID) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("delete from groupinvitation where username = ? and groupid = ?");
        ps.setString(1, username);
        ps.setInt(2, groupID);
        ps.executeUpdate();
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userBean.loadUser(username, false, false);
            if (userData != null && userData.status == UserData.StatusEnum.ACTIVE) {
                this.removeGroupInviteNotification(userData.userID, groupID);
            }
        }
        catch (Exception e) {
            log.warn((Object)("Failed to remove pending group invite notfication for user: " + username + ", reason: " + e.getLocalizedMessage()));
        }
        String e = "TRUE";
        Object var8_10 = null;
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
        return e;
        catch (SQLException e2) {
            String string;
            try {
                string = ExceptionHelper.getRootMessage(e2);
                Object var8_11 = null;
            }
            catch (Throwable throwable) {
                Object var8_12 = null;
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
            return string;
        }
    }

    private void removeGroupInviteNotification(int userId, int groupId) {
        try {
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            unsProxy.clearNotificationsForUser(userId, Enums.NotificationTypeEnum.GROUP_INVITE.getType(), new String[]{userId + "/" + groupId});
        }
        catch (Exception e) {
            log.warn((Object)("Failed to remove pending group invite notfication for user: " + userId + ", reason: " + e.getLocalizedMessage()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String setGroupMemberOptions(String username, int groupID, int locationID, boolean smsNotification, boolean emailNotification, boolean eventNotification, boolean smsGroupEventNotification, boolean emailThreadUpdateNotification, boolean eventThreadUpdateNotification) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("update groupmember set locationID = ?, smsnotification = ?, emailnotification = ?, eventnotification = ?, smsgroupeventnotification = ?, emailthreadupdatenotification = ?, eventthreadupdatenotification = ? where username = ? and groupid = ?");
        ps.setObject(1, locationID > 0 ? Integer.valueOf(locationID) : null);
        ps.setInt(2, smsNotification ? 1 : 0);
        ps.setInt(3, emailNotification ? 1 : 0);
        ps.setInt(4, eventNotification ? 1 : 0);
        ps.setInt(5, smsGroupEventNotification ? 1 : 0);
        ps.setInt(6, emailThreadUpdateNotification ? 1 : 0);
        ps.setInt(7, eventThreadUpdateNotification ? 1 : 0);
        ps.setString(8, username);
        ps.setInt(9, groupID);
        ps.executeUpdate();
        String string = "TRUE";
        Object var15_14 = null;
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
            String string2;
            try {
                string2 = ExceptionHelper.getRootMessage(e);
                Object var15_15 = null;
            }
            catch (Throwable throwable) {
                Object var15_16 = null;
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
            return string2;
        }
    }

    public Hashtable makeGroupDonation(String username, int groupID, String externalVoucherNumber, boolean visible) throws EJBException {
        return new Hashtable();
    }

    public String inviteMobilePhoneToGroup(String username, String displayName, String mobilePhone, int groupID, String groupName, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.inviteFriend(username, displayName, mobilePhone, groupID, groupName, null, null, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (FusionEJBException e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String inviteUserToGroup(String inviterUsername, String inviteeUsername, int groupID) throws EJBException {
        block124: {
            block123: {
                block122: {
                    block100: {
                        block121: {
                            block120: {
                                block119: {
                                    block98: {
                                        block118: {
                                            block117: {
                                                block116: {
                                                    block97: {
                                                        block115: {
                                                            block114: {
                                                                block113: {
                                                                    block96: {
                                                                        block112: {
                                                                            block111: {
                                                                                block110: {
                                                                                    block95: {
                                                                                        block109: {
                                                                                            block108: {
                                                                                                block107: {
                                                                                                    block93: {
                                                                                                        connMaster = null;
                                                                                                        connSlave = null;
                                                                                                        ps = null;
                                                                                                        rs = null;
                                                                                                        try {
                                                                                                            try {
                                                                                                                block99: {
                                                                                                                    block94: {
                                                                                                                        if (inviterUsername != null && inviterUsername.length() == 0) {
                                                                                                                            inviterUsername = null;
                                                                                                                        }
                                                                                                                        try {
                                                                                                                            userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                                                                                                                            inviteeUserId = userEJB.getUserID(inviteeUsername, null);
                                                                                                                        }
                                                                                                                        catch (Exception excep) {
                                                                                                                            throw new EJBException("Invalid username specified");
                                                                                                                        }
                                                                                                                        try {
                                                                                                                            userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                                                                                                                            if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.SEND_GROUP_INVITE, userBean.getUserAuthenticatedAccessControlParameter(inviterUsername)) && SystemProperty.getBool("SendGroupInviteDisabledForUnauthenticatedUsers", false)) {
                                                                                                                                var10_15 = ExceptionHelper.setErrorMessage("You must authenticate your account before sending a group invite.");
                                                                                                                            }
                                                                                                                            ** GOTO lbl-1000
                                                                                                                        }
                                                                                                                        catch (Exception e) {
                                                                                                                            var10_17 = ExceptionHelper.setErrorMessage("Unable to send invite at this time. Please try again later.");
                                                                                                                            var18_20 = null;
                                                                                                                            try {
                                                                                                                                if (rs != null) {
                                                                                                                                    rs.close();
                                                                                                                                }
                                                                                                                            }
                                                                                                                            catch (SQLException e) {
                                                                                                                                // empty catch block
                                                                                                                            }
                                                                                                                            try {
                                                                                                                                if (ps != null) {
                                                                                                                                    ps.close();
                                                                                                                                }
                                                                                                                            }
                                                                                                                            catch (SQLException e) {
                                                                                                                                // empty catch block
                                                                                                                            }
                                                                                                                            try {
                                                                                                                                if (connMaster != null) {
                                                                                                                                    connMaster.close();
                                                                                                                                }
                                                                                                                            }
                                                                                                                            catch (SQLException e) {
                                                                                                                                // empty catch block
                                                                                                                            }
                                                                                                                            try {
                                                                                                                                if (connSlave == null) return var10_17;
                                                                                                                                connSlave.close();
                                                                                                                                return var10_17;
                                                                                                                            }
                                                                                                                            catch (SQLException e) {
                                                                                                                                // empty catch block
                                                                                                                            }
                                                                                                                            return var10_17;
                                                                                                                        }
                                                                                                                        var18_18 = null;
                                                                                                                        break block93;
lbl-1000:
                                                                                                                        // 1 sources

                                                                                                                        {
                                                                                                                            if (AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.RECEIVE_GROUP_INVITE, userBean.getUserAuthenticatedAccessControlParameter(inviteeUsername)) || !SystemProperty.getBool("ReceiveGroupInviteDisabledForUnauthenticatedUsers", false)) break block94;
                                                                                                                            var10_16 = ExceptionHelper.setErrorMessage("You can only invite authenticated users to a group.");
                                                                                                                        }
                                                                                                                        break block95;
                                                                                                                    }
                                                                                                                    connMaster = this.dataSourceMaster.getConnection();
                                                                                                                    ps = connMaster.prepareStatement("select * from groupmember where username = ? and groupid = ? and status != ?");
                                                                                                                    ps.setString(1, inviteeUsername);
                                                                                                                    ps.setInt(2, groupID);
                                                                                                                    ps.setInt(3, GroupMemberData.StatusEnum.INACTIVE.value());
                                                                                                                    rs = ps.executeQuery();
                                                                                                                    if (rs.next()) {
                                                                                                                        e = ExceptionHelper.setErrorMessage(inviteeUsername + " is already a member of this group");
                                                                                                                        break block96;
                                                                                                                    }
                                                                                                                    rs.close();
                                                                                                                    ps.close();
                                                                                                                    ps = connMaster.prepareStatement("select * from groupinvitation where username = ? and groupid = ? and status = ?");
                                                                                                                    ps.setString(1, inviteeUsername);
                                                                                                                    ps.setInt(2, groupID);
                                                                                                                    ps.setInt(3, GroupInvitationData.StatusEnum.PENDING.value());
                                                                                                                    rs = ps.executeQuery();
                                                                                                                    if (rs.next()) {
                                                                                                                        e = "TRUE";
                                                                                                                        break block97;
                                                                                                                    }
                                                                                                                    rs.close();
                                                                                                                    ps.close();
                                                                                                                    ps = connMaster.prepareStatement("insert into groupinvitation (username, groupid, datecreated, inviter, status) values (?,?,?,?,?)");
                                                                                                                    ps.setString(1, inviteeUsername);
                                                                                                                    ps.setInt(2, groupID);
                                                                                                                    currentTimeMillis = System.currentTimeMillis();
                                                                                                                    ps.setTimestamp(3, new Timestamp(currentTimeMillis));
                                                                                                                    ps.setString(4, inviterUsername);
                                                                                                                    ps.setInt(5, GroupInvitationData.StatusEnum.PENDING.value());
                                                                                                                    if (ps.executeUpdate() != 1) {
                                                                                                                        var11_36 = ExceptionHelper.setErrorMessage("Failed send invitation to inviterUsername");
                                                                                                                        break block98;
                                                                                                                    }
                                                                                                                    parameters = new HashMap<String, String>();
                                                                                                                    try {
                                                                                                                        userPrx = EJBIcePrxFinder.findUserPrx(inviteeUsername);
                                                                                                                        if (userPrx != null) {
                                                                                                                            groupInvitationAlert = SystemProperty.get("GroupInvitationAlert", "");
                                                                                                                            if (groupInvitationAlert.length() > 0) {
                                                                                                                                userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                                                                                                                                groupData = userEJB.getGroup(groupID);
                                                                                                                                groupInvitationAlert = groupInvitationAlert.replaceAll("%inviter%", inviterUsername).replaceAll("%groupname%", groupData.name);
                                                                                                                                parameters.put("alertMessage", groupInvitationAlert);
                                                                                                                            }
                                                                                                                            break block99;
                                                                                                                        }
                                                                                                                        groupInvitation = new GroupInvitationData();
                                                                                                                        groupInvitation.id = null;
                                                                                                                        groupInvitation.username = inviteeUsername;
                                                                                                                        groupInvitation.groupID = groupID;
                                                                                                                        groupInvitation.dateCreated = new Date(currentTimeMillis);
                                                                                                                        groupInvitation.inviter = inviterUsername;
                                                                                                                        groupInvitation.status = GroupInvitationData.StatusEnum.PENDING;
                                                                                                                        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.GROUP_INVITATION, inviteeUsername, groupInvitation);
                                                                                                                    }
                                                                                                                    catch (Exception e) {
                                                                                                                        WebBean.log.warn((Object)("Unable to send group invitation alert message to " + inviteeUsername), (Throwable)e);
                                                                                                                    }
                                                                                                                }
                                                                                                                try {
                                                                                                                    unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                                                                                                                    userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                                                                                                                    groupData = userEJB.getGroup(groupID);
                                                                                                                    inviterUserId = userEJB.getUserID(inviterUsername, null);
                                                                                                                    parameters.put("groupId", Integer.toString(groupID));
                                                                                                                    parameters.put("inviterUserId", Integer.toString(inviterUserId));
                                                                                                                    parameters.put("groupName", groupData.name);
                                                                                                                    parameters.put("groupPicture", groupData.picture);
                                                                                                                    key = inviteeUserId + "/" + groupID;
                                                                                                                    unsProxy.notifyFusionUser(new Message(key, inviteeUserId, inviteeUsername, Enums.NotificationTypeEnum.GROUP_INVITE.getType(), currentTimeMillis, parameters));
                                                                                                                }
                                                                                                                catch (Exception e) {
                                                                                                                    WebBean.log.error((Object)("Failed to push group invitation notification to user [" + inviteeUsername + "]"), (Throwable)e);
                                                                                                                }
                                                                                                                var12_38 = "TRUE";
                                                                                                                break block100;
                                                                                                            }
                                                                                                            catch (SQLException e) {
                                                                                                                block103: {
                                                                                                                    block102: {
                                                                                                                        block101: {
                                                                                                                            var9_12 = ExceptionHelper.setErrorMessage(e.getMessage());
                                                                                                                            var18_25 = null;
                                                                                                                            ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl145:
                                                                                                                            // 1 sources

                                                                                                                            if (rs != null) {
                                                                                                                                rs.close();
                                                                                                                            }
                                                                                                                            break block101;
lbl148:
                                                                                                                            // 1 sources

                                                                                                                            catch (SQLException e) {
                                                                                                                                // empty catch block
                                                                                                                            }
                                                                                                                        }
                                                                                                                        ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl152:
                                                                                                                        // 1 sources

                                                                                                                        if (ps != null) {
                                                                                                                            ps.close();
                                                                                                                        }
                                                                                                                        break block102;
lbl155:
                                                                                                                        // 1 sources

                                                                                                                        catch (SQLException e) {
                                                                                                                            // empty catch block
                                                                                                                        }
                                                                                                                    }
                                                                                                                    ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl159:
                                                                                                                    // 1 sources

                                                                                                                    if (connMaster != null) {
                                                                                                                        connMaster.close();
                                                                                                                    }
                                                                                                                    break block103;
lbl162:
                                                                                                                    // 1 sources

                                                                                                                    catch (SQLException e) {
                                                                                                                        // empty catch block
                                                                                                                    }
                                                                                                                }
                                                                                                                ** try [egrp 10[TRYBLOCK] [24 : 971->986)] { 
lbl166:
                                                                                                                // 1 sources

                                                                                                                if (connSlave == null) return var9_12;
                                                                                                                connSlave.close();
                                                                                                                return var9_12;
lbl169:
                                                                                                                // 1 sources

                                                                                                                catch (SQLException e) {
                                                                                                                    // empty catch block
                                                                                                                }
                                                                                                                return var9_12;
                                                                                                            }
                                                                                                        }
                                                                                                        catch (Throwable var17_46) {
                                                                                                            block106: {
                                                                                                                block105: {
                                                                                                                    block104: {
                                                                                                                        var18_26 = null;
                                                                                                                        ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl176:
                                                                                                                        // 1 sources

                                                                                                                        if (rs != null) {
                                                                                                                            rs.close();
                                                                                                                        }
                                                                                                                        break block104;
lbl179:
                                                                                                                        // 1 sources

                                                                                                                        catch (SQLException e) {
                                                                                                                            // empty catch block
                                                                                                                        }
                                                                                                                    }
                                                                                                                    ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl183:
                                                                                                                    // 1 sources

                                                                                                                    if (ps != null) {
                                                                                                                        ps.close();
                                                                                                                    }
                                                                                                                    break block105;
lbl186:
                                                                                                                    // 1 sources

                                                                                                                    catch (SQLException e) {
                                                                                                                        // empty catch block
                                                                                                                    }
                                                                                                                }
                                                                                                                ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl190:
                                                                                                                // 1 sources

                                                                                                                if (connMaster != null) {
                                                                                                                    connMaster.close();
                                                                                                                }
                                                                                                                break block106;
lbl193:
                                                                                                                // 1 sources

                                                                                                                catch (SQLException e) {
                                                                                                                    // empty catch block
                                                                                                                }
                                                                                                            }
                                                                                                            try {}
                                                                                                            catch (SQLException e) {
                                                                                                                throw var17_46;
                                                                                                            }
                                                                                                            if (connSlave == null) throw var17_46;
                                                                                                            connSlave.close();
                                                                                                            throw var17_46;
                                                                                                        }
                                                                                                    }
                                                                                                    ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl204:
                                                                                                    // 1 sources

                                                                                                    if (rs != null) {
                                                                                                        rs.close();
                                                                                                    }
                                                                                                    break block107;
lbl207:
                                                                                                    // 1 sources

                                                                                                    catch (SQLException e) {
                                                                                                        // empty catch block
                                                                                                    }
                                                                                                }
                                                                                                ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl211:
                                                                                                // 1 sources

                                                                                                if (ps != null) {
                                                                                                    ps.close();
                                                                                                }
                                                                                                break block108;
lbl214:
                                                                                                // 1 sources

                                                                                                catch (SQLException e) {
                                                                                                    // empty catch block
                                                                                                }
                                                                                            }
                                                                                            ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl218:
                                                                                            // 1 sources

                                                                                            if (connMaster != null) {
                                                                                                connMaster.close();
                                                                                            }
                                                                                            break block109;
lbl221:
                                                                                            // 1 sources

                                                                                            catch (SQLException e) {
                                                                                                // empty catch block
                                                                                            }
                                                                                        }
                                                                                        try {}
                                                                                        catch (SQLException e) {
                                                                                            // empty catch block
                                                                                            return var10_15;
                                                                                        }
                                                                                        if (connSlave == null) return var10_15;
                                                                                        connSlave.close();
                                                                                        return var10_15;
                                                                                    }
                                                                                    var18_19 = null;
                                                                                    ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl234:
                                                                                    // 1 sources

                                                                                    if (rs != null) {
                                                                                        rs.close();
                                                                                    }
                                                                                    break block110;
lbl237:
                                                                                    // 1 sources

                                                                                    catch (SQLException e) {
                                                                                        // empty catch block
                                                                                    }
                                                                                }
                                                                                ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl241:
                                                                                // 1 sources

                                                                                if (ps != null) {
                                                                                    ps.close();
                                                                                }
                                                                                break block111;
lbl244:
                                                                                // 1 sources

                                                                                catch (SQLException e) {
                                                                                    // empty catch block
                                                                                }
                                                                            }
                                                                            ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl248:
                                                                            // 1 sources

                                                                            if (connMaster != null) {
                                                                                connMaster.close();
                                                                            }
                                                                            break block112;
lbl251:
                                                                            // 1 sources

                                                                            catch (SQLException e) {
                                                                                // empty catch block
                                                                            }
                                                                        }
                                                                        try {}
                                                                        catch (SQLException e) {
                                                                            // empty catch block
                                                                            return var10_16;
                                                                        }
                                                                        if (connSlave == null) return var10_16;
                                                                        connSlave.close();
                                                                        return var10_16;
                                                                    }
                                                                    var18_21 = null;
                                                                    ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl264:
                                                                    // 1 sources

                                                                    if (rs != null) {
                                                                        rs.close();
                                                                    }
                                                                    break block113;
lbl267:
                                                                    // 1 sources

                                                                    catch (SQLException e) {
                                                                        // empty catch block
                                                                    }
                                                                }
                                                                ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl271:
                                                                // 1 sources

                                                                if (ps != null) {
                                                                    ps.close();
                                                                }
                                                                break block114;
lbl274:
                                                                // 1 sources

                                                                catch (SQLException e) {
                                                                    // empty catch block
                                                                }
                                                            }
                                                            ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl278:
                                                            // 1 sources

                                                            if (connMaster != null) {
                                                                connMaster.close();
                                                            }
                                                            break block115;
lbl281:
                                                            // 1 sources

                                                            catch (SQLException e) {
                                                                // empty catch block
                                                            }
                                                        }
                                                        ** try [egrp 10[TRYBLOCK] [24 : 971->986)] { 
lbl285:
                                                        // 1 sources

                                                        if (connSlave == null) return e;
                                                        connSlave.close();
                                                        return e;
lbl288:
                                                        // 1 sources

                                                        catch (SQLException e) {
                                                            // empty catch block
                                                        }
                                                        return e;
                                                    }
                                                    var18_22 = null;
                                                    ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl294:
                                                    // 1 sources

                                                    if (rs != null) {
                                                        rs.close();
                                                    }
                                                    break block116;
lbl297:
                                                    // 1 sources

                                                    catch (SQLException e) {
                                                        // empty catch block
                                                    }
                                                }
                                                ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl301:
                                                // 1 sources

                                                if (ps != null) {
                                                    ps.close();
                                                }
                                                break block117;
lbl304:
                                                // 1 sources

                                                catch (SQLException e) {
                                                    // empty catch block
                                                }
                                            }
                                            ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl308:
                                            // 1 sources

                                            if (connMaster != null) {
                                                connMaster.close();
                                            }
                                            break block118;
lbl311:
                                            // 1 sources

                                            catch (SQLException e) {
                                                // empty catch block
                                            }
                                        }
                                        ** try [egrp 10[TRYBLOCK] [24 : 971->986)] { 
lbl315:
                                        // 1 sources

                                        if (connSlave == null) return e;
                                        connSlave.close();
                                        return e;
lbl318:
                                        // 1 sources

                                        catch (SQLException e) {
                                            // empty catch block
                                        }
                                        return e;
                                    }
                                    var18_23 = null;
                                    ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl324:
                                    // 1 sources

                                    if (rs != null) {
                                        rs.close();
                                    }
                                    break block119;
lbl327:
                                    // 1 sources

                                    catch (SQLException e) {
                                        // empty catch block
                                    }
                                }
                                ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl331:
                                // 1 sources

                                if (ps != null) {
                                    ps.close();
                                }
                                break block120;
lbl334:
                                // 1 sources

                                catch (SQLException e) {
                                    // empty catch block
                                }
                            }
                            ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl338:
                            // 1 sources

                            if (connMaster != null) {
                                connMaster.close();
                            }
                            break block121;
lbl341:
                            // 1 sources

                            catch (SQLException e) {
                                // empty catch block
                            }
                        }
                        ** try [egrp 10[TRYBLOCK] [24 : 971->986)] { 
lbl345:
                        // 1 sources

                        if (connSlave == null) return var11_36;
                        connSlave.close();
                        return var11_36;
lbl348:
                        // 1 sources

                        catch (SQLException e) {
                            // empty catch block
                        }
                        return var11_36;
                    }
                    var18_24 = null;
                    ** try [egrp 7[TRYBLOCK] [21 : 920->935)] { 
lbl354:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block122;
lbl357:
                    // 1 sources

                    catch (SQLException e) {
                        // empty catch block
                    }
                }
                ** try [egrp 8[TRYBLOCK] [22 : 937->952)] { 
lbl361:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block123;
lbl364:
                // 1 sources

                catch (SQLException e) {
                    // empty catch block
                }
            }
            ** try [egrp 9[TRYBLOCK] [23 : 954->969)] { 
lbl368:
            // 1 sources

            if (connMaster != null) {
                connMaster.close();
            }
            break block124;
lbl371:
            // 1 sources

            catch (SQLException e) {
                // empty catch block
            }
        }
        try {}
        catch (SQLException e) {
            // empty catch block
            return var12_38;
        }
        if (connSlave == null) return var12_38;
        connSlave.close();
        return var12_38;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String updateStadiumDescription(String stadiumName, String description) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("update chatroom set description = ? where type = ? and name = ?");
        ps.setString(1, description);
        ps.setInt(2, ChatRoomData.TypeEnum.STADIUM.value());
        ps.setString(3, stadiumName);
        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated == 1) {
            ChatRoomUtils.invalidateChatRoomCache(stadiumName);
            ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(stadiumName);
            if (chatRoomPrx != null) {
                chatRoomPrx.setDescription(description);
            }
        }
        String string = "TRUE";
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
        }
        catch (SQLException e2) {
            conn = null;
        }
        return string;
        catch (SQLException e) {
            String string2;
            try {
                string2 = ExceptionHelper.getRootMessage(e);
                Object var8_10 = null;
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
            return string2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getLocations(int countryID, int parentLocationID, int page, int numEntries) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        if (parentLocationID > 0) {
            ps = conn.prepareStatement("select * from location where countryid = ? and parentlocationid = ? order by name");
            ps.setInt(1, countryID);
            ps.setInt(2, parentLocationID);
        } else {
            ps = conn.prepareStatement("select * from location where countryid = ? and parentlocationid is null order by name");
            ps.setInt(1, countryID);
        }
        Vector locations = new Vector();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                Hashtable<String, Object> ht = new Hashtable<String, Object>();
                ht.put("id", rs.getInt("id"));
                ht.put("name", rs.getString("name"));
                locations.add(ht);
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("locations", locations);
        hash.put("parentLocationID", Integer.valueOf(parentLocationID));
        Hashtable<String, Serializable> hashtable = hash;
        Object var13_14 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
                Object var13_15 = null;
            }
            catch (Throwable throwable) {
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getLocation(int id) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select * from location where id = ?");
        ps.setInt(1, id);
        Hashtable<String, Object> location = new Hashtable<String, Object>();
        rs = ps.executeQuery();
        if (rs.next()) {
            location.put("id", rs.getInt("id"));
            location.put("parentLocationID", rs.getInt("parentLocationID"));
            location.put("countryID", rs.getInt("countryID"));
            location.put("name", rs.getString("name"));
            location.put("level", rs.getInt("level"));
        }
        Hashtable<String, Object> hashtable = location;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
                Object var8_10 = null;
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getContactList(String username, int countryID, int page, int numEntries) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        if (countryID > 0) {
            ps = conn.prepareStatement("select contact.* from contact, user where contact.fusionusername = user.username and contact.username = ? and user.countryid = ? order by contact.displayname");
            ps.setString(1, username);
            ps.setInt(2, countryID);
        } else {
            ps = conn.prepareStatement("select contact.* from contact, user where contact.fusionusername = user.username and contact.username = ? order by contact.displayname");
            ps.setString(1, username);
        }
        Vector<Hashtable> contactList = new Vector<Hashtable>();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                contactList.add(HashObjectUtils.dataObjectToHashtable(new ContactData(rs)));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("contact_list", contactList);
        Hashtable<String, Serializable> hashtable = hash;
        Object var13_14 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.setErrorMessageAsHashtable(e.getMessage());
                Object var13_15 = null;
            }
            catch (Throwable throwable) {
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
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
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
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getGroupModules(int groupID) throws EJBException {
        Vector<Hashtable> groupModules;
        block28: {
            Connection connSlave = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            groupModules = new Vector<Hashtable>();
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select * from groupmodule where groupid=? and status=1 order by position");
            ps.setInt(1, groupID);
            rs = ps.executeQuery();
            while (rs.next()) {
                groupModules.add(HashObjectUtils.dataObjectToHashtable(new GroupModuleData(rs)));
            }
            Object var9_6 = null;
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
                break block28;
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            break block28;
            {
                catch (SQLException e) {
                    Vector vector = ExceptionHelper.getRootMessageAsVector(e);
                    Object var9_7 = null;
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
                    return vector;
                }
            }
            catch (Throwable throwable) {
                Object var9_8 = null;
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
        return groupModules;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupEvents(int groupID, int page, int numEntries) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector<Hashtable> groupEvents = new Vector<Hashtable>();
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select groupevent.*, chatroomcategory.name chatroomcategoryname from groupevent left outer join chatroomcategory on groupevent.chatroomcategoryid=chatroomcategory.id where groupid=? and groupevent.status=1 and (chatroomcategory.status is null or chatroomcategory.status=1) and (durationminutes is null or date_add(starttime, interval durationminutes minute) >= now()) order by starttime");
        ps.setInt(1, groupID);
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                groupEvents.add(HashObjectUtils.dataObjectToHashtable(GroupEventData.fromResultSetWithChatRoomCategoryName(rs)));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("group_events", groupEvents);
        Hashtable<String, Serializable> hashtable = hash;
        Object var12_13 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupModule(int groupModuleID) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block32: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select * from groupmodule where id=? and status=1");
            ps.setInt(1, groupModuleID);
            rs = ps.executeQuery();
            if (!rs.next()) break block32;
            Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(new GroupModuleData(rs));
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
            return hashtable;
        }
        Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable("Module not found");
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var8_10 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupPost(int groupPostID) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block32: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select * from grouppost where id=? and status>0");
            ps.setInt(1, groupPostID);
            rs = ps.executeQuery();
            if (!rs.next()) break block32;
            Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(new GroupPostData(rs));
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
            return hashtable;
        }
        Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable("Post not found");
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var8_10 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupPosts(int groupModuleID, int page, int numEntries) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select id, teaser, datecreated, createdby, length(body) > 0 as hasbody from grouppost where groupmoduleid=? and status=1 order by id desc");
        ps.setInt(1, groupModuleID);
        Vector groupPosts = new Vector();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            SimpleDateFormat df = new SimpleDateFormat("d MMM yy HH:mm");
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                Hashtable<String, Object> hash = new Hashtable<String, Object>();
                hash.put("id", rs.getInt("id"));
                hash.put("teaser", rs.getString("teaser"));
                hash.put("datecreated", df.format(rs.getTimestamp("datecreated")));
                hash.put("createdby", rs.getString("createdby"));
                hash.put("hasbody", rs.getBoolean("hasbody"));
                groupPosts.add(hash);
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("group_posts", groupPosts);
        Hashtable<String, Serializable> hashtable = hash;
        Object var12_15 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_16 = null;
            }
            catch (Throwable throwable) {
                Object var12_17 = null;
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
            return hashtable2;
        }
    }

    public Hashtable createGroupPost(String username, int groupModuleID, String title, String text) throws EJBException {
        return this.createGroupPost(username, groupModuleID, title, text, GroupPostData.StatusEnum.PREVIEW.value());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable createGroupPost(String username, int groupModuleID, String title, String text, int status) throws EJBException {
        GroupPostData postData;
        ResultSet rs;
        Statement ps;
        Connection connMaster;
        block32: {
            connMaster = null;
            ps = null;
            rs = null;
            postData = new GroupPostData();
            postData.groupModuleID = groupModuleID;
            postData.dateCreated = new Date();
            postData.createdBy = username;
            postData.teaser = title;
            postData.body = text;
            postData.lastModifiedDate = postData.dateCreated;
            postData.lastModifiedBy = username;
            postData.status = GroupPostData.StatusEnum.fromValue(status);
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("insert into grouppost (groupmoduleid, datecreated, createdby, teaser, body, lastmodifieddate, lastmodifiedby, status) values (?,?,?,?,?,?,?,?)", 1);
            ps.setObject(1, postData.groupModuleID);
            ps.setTimestamp(2, new Timestamp(postData.dateCreated.getTime()));
            ps.setString(3, postData.createdBy);
            ps.setString(4, postData.teaser);
            ps.setString(5, postData.body);
            ps.setTimestamp(6, new Timestamp(postData.lastModifiedDate.getTime()));
            ps.setString(7, postData.lastModifiedBy);
            ps.setObject(8, postData.status == null ? null : Integer.valueOf(postData.status.value()));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) break block32;
            Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable("Unable to create group post");
            Object var12_14 = null;
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return hashtable;
        }
        postData.id = rs.getInt(1);
        Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(postData);
        Object var12_15 = null;
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_16 = null;
            }
            catch (Throwable throwable) {
                Object var12_17 = null;
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String updateGroupPost(int groupPostID, String username, String title, String text) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block43: {
            block39: {
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("select groupmember.username from groupmember, groups, grouppost, groupmodule where grouppost.groupmoduleid=groupmodule.id and groupmodule.groupid=groups.id and groups.id=groupmember.groupid and groupmember.type=2 and groupmember.username=? and grouppost.id=?");
                ps.setString(1, username);
                ps.setInt(2, groupPostID);
                rs = ps.executeQuery();
                if (rs.next()) break block39;
                String string = ExceptionHelper.setErrorMessage("You must be an admin of the group");
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
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                return string;
            }
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("update grouppost set teaser=?, body=?, lastmodifieddate=?, lastmodifiedby=?, status=? where id=?");
            ps.setString(1, title);
            ps.setString(2, text);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, username);
            ps.setInt(5, GroupPostData.StatusEnum.PREVIEW.value());
            ps.setInt(6, groupPostID);
            if (ps.executeUpdate() == 1) break block43;
            String string = ExceptionHelper.setErrorMessage("Failed to update group post");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string;
        }
        String string = "TRUE";
        Object var11_14 = null;
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return string;
        catch (SQLException e) {
            String string2;
            try {
                string2 = ExceptionHelper.getRootMessage(e);
                Object var11_15 = null;
            }
            catch (Throwable throwable) {
                Object var11_16 = null;
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String publishGroupPost(int groupPostID, String username) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block32: {
            connMaster = null;
            ps = null;
            rs = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select groupmember.username from groupmember, groups, grouppost, groupmodule where grouppost.groupmoduleid=groupmodule.id and groupmodule.groupid=groups.id and groups.id=groupmember.groupid and groupmember.type=2 and groupmember.username=? and grouppost.id=?");
            ps.setString(1, username);
            ps.setInt(2, groupPostID);
            rs = ps.executeQuery();
            if (rs.next()) break block32;
            String string = ExceptionHelper.setErrorMessage("You must be an admin of the group");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string;
        }
        rs.close();
        ps.close();
        ps = connMaster.prepareStatement("update grouppost set status=1 where id=? and status=2");
        ps.setInt(1, groupPostID);
        ps.executeUpdate();
        String string = "TRUE";
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return string;
        catch (SQLException e) {
            String string2;
            try {
                string2 = ExceptionHelper.getRootMessage(e);
                Object var9_11 = null;
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public String deleteGroupPost(int groupPostID, String username) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block43: {
            block39: {
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("select groupmember.username from groupmember, group, grouppost where grouppost.groupid=group.id and group.id=groupmember.groupid and groupmember.type=2 and groupmember.username=? and grouppost.id=?");
                ps.setString(1, username);
                ps.setInt(2, groupPostID);
                rs = ps.executeQuery();
                if (rs.next()) break block39;
                String string = ExceptionHelper.setErrorMessage("You must be an admin of the group");
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
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                return string;
            }
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("update grouppost set status=0 where id=?");
            ps.setInt(1, groupPostID);
            if (ps.executeUpdate() == 1) break block43;
            String string = ExceptionHelper.setErrorMessage("Failed to update group post");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string;
        }
        String string = "TRUE";
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return string;
        catch (SQLException e) {
            String string2;
            try {
                string2 = ExceptionHelper.getRootMessage(e);
                Object var9_13 = null;
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string2;
        }
    }

    /*
     * Loose catch block
     */
    public Hashtable createGroupModule(String username, int groupID, String title, int position) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block27: {
            connMaster = null;
            ps = null;
            rs = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select groupmember.username from groupmember where username=? and groupid=? and type=2");
            ps.setString(1, username);
            ps.setInt(2, groupID);
            rs = ps.executeQuery();
            if (rs.next()) break block27;
            Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable("You must be an admin of the group");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return hashtable;
        }
        rs.close();
        ps.close();
        GroupModuleData groupModuleData = new GroupModuleData();
        groupModuleData.groupID = groupID;
        groupModuleData.title = title;
        groupModuleData.dateCreated = new Date();
        groupModuleData.createdBy = username;
        groupModuleData.lastModifiedDate = groupModuleData.dateCreated;
        groupModuleData.lastModifiedBy = username;
        groupModuleData.position = position;
        groupModuleData.type = GroupModuleData.TypeEnum.POSTS;
        groupModuleData.status = GroupModuleData.StatusEnum.ACTIVE;
        ps = connMaster.prepareStatement("update groupmodule set position=position+1 where groupid=? and position>=? and status=1");
        ps.setInt(1, groupID);
        ps.setInt(2, position);
        ps.executeUpdate();
        ps.close();
        ps = connMaster.prepareStatement("insert into groupmodule (groupid, title, datecreated, createdby, lastmodifieddate, lastmodifiedby, position, type, status) values (?,?,?,?,?,?,?,?,?)", 1);
        ps.setInt(1, groupModuleData.groupID);
        ps.setString(2, groupModuleData.title);
        ps.setTimestamp(3, new Timestamp(groupModuleData.dateCreated.getTime()));
        ps.setString(4, groupModuleData.createdBy);
        ps.setTimestamp(5, new Timestamp(groupModuleData.lastModifiedDate.getTime()));
        ps.setString(6, groupModuleData.lastModifiedBy);
        ps.setInt(7, groupModuleData.position);
        ps.setInt(8, groupModuleData.type.value());
        ps.setInt(9, groupModuleData.status.value());
        ps.executeUpdate();
        rs = ps.getGeneratedKeys();
        if (!rs.next()) {
            throw new EJBException("Unable to create group module");
        }
        groupModuleData.id = rs.getInt(1);
        Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(groupModuleData);
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return hashtable;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
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
    public String updateGroupModule(int groupModuleID, String username, String title, int position) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block39: {
            block35: {
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("select groupmember.username from groupmember, groupmodule where groupmodule.groupid=groupmember.groupid and groupmember.username=? and groupmodule.id=? and groupmember.type=2");
                ps.setString(1, username);
                ps.setInt(2, groupModuleID);
                rs = ps.executeQuery();
                if (rs.next()) break block35;
                String string = ExceptionHelper.setErrorMessage("You must be an admin of the group");
                Object var12_12 = null;
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
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                return string;
            }
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("select position, groupid from groupmodule where id=?");
            ps.setInt(1, groupModuleID);
            rs = ps.executeQuery();
            if (rs.next()) break block39;
            String string = ExceptionHelper.setErrorMessage("Module not found");
            Object var12_13 = null;
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string;
        }
        int oldPosition = rs.getInt("position");
        int groupID = rs.getInt("groupid");
        rs.close();
        ps.close();
        if (position != oldPosition) {
            ps = connMaster.prepareStatement("update groupmodule set position=position-1 where groupid=? and position<=? and position>? and status=1");
            ps.setInt(1, groupID);
            ps.setInt(2, position);
            ps.setInt(3, oldPosition);
            ps.executeUpdate();
            ps.close();
        }
        ps = connMaster.prepareStatement("update groupmodule set title=?, position=?, lastmodifieddate=?, lastmodifiedby=? where id=?");
        ps.setString(1, title);
        ps.setInt(2, position);
        ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        ps.setString(4, username);
        ps.setInt(5, groupModuleID);
        if (ps.executeUpdate() != 1) {
            throw new EJBException("Failed to update group module");
        }
        String string = "TRUE";
        Object var12_14 = null;
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
    public String deleteGroupModule(int groupModuleID, String username) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block38: {
            block34: {
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("select groupmember.username from groupmember, groupmodule where groupmodule.groupid=groupmember.groupid and groupmember.username=? and groupmodule.id=? and groupmember.type=2");
                ps.setString(1, username);
                ps.setInt(2, groupModuleID);
                rs = ps.executeQuery();
                if (rs.next()) break block34;
                String string = ExceptionHelper.setErrorMessage("You must be an admin of the group");
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
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                return string;
            }
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("select position, groupid from groupmodule where id=?");
            ps.setInt(1, groupModuleID);
            rs = ps.executeQuery();
            if (rs.next()) break block38;
            String string = ExceptionHelper.setErrorMessage("Module not found");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string;
        }
        int position = rs.getInt("position");
        int groupID = rs.getInt("groupid");
        rs.close();
        ps.close();
        ps = connMaster.prepareStatement("update groupmodule set position=position-1 where groupid=? and position>? and status=1");
        ps.setInt(1, groupID);
        ps.setInt(2, position);
        ps.executeUpdate();
        ps.close();
        ps = connMaster.prepareStatement("update groupmodule set status=0, position=0 where id=?");
        ps.setInt(1, groupModuleID);
        if (ps.executeUpdate() != 1) {
            throw new EJBException("Failed to delete group module");
        }
        String string = "TRUE";
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable createGroupUserPost(String username, int groupID, String body, int parentGroupPostID) throws EJBException {
        Hashtable hashtable;
        ResultSet rs;
        Statement ps;
        Connection connSlave;
        block75: {
            block71: {
                block67: {
                    UserData userData;
                    block63: {
                        connSlave = null;
                        ps = null;
                        rs = null;
                        UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                        userData = userBean.loadUser(username, false, false);
                        if (userData != null) break block63;
                        log.error((Object)String.format("Unable to create user post in group - user '%s' does not exist", username));
                        Hashtable hashtable2 = ExceptionHelper.setErrorMessageAsHashtable("Invalid username");
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
                            if (connSlave != null) {
                                connSlave.close();
                            }
                        }
                        catch (SQLException e2) {
                            connSlave = null;
                        }
                        return hashtable2;
                    }
                    if (AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.CREATE_USER_POST_IN_GROUPS, userData)) break block67;
                    Hashtable hashtable3 = ExceptionHelper.setErrorMessageAsHashtable("You must be authenticated to create a post");
                    Object var14_18 = null;
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
                    return hashtable3;
                }
                if (MemCacheOrEJB.getUserReputationLevel(username) >= SystemProperty.getInt(SystemPropertyEntities.MigLevel.GROUP_CREATE_USER_POST_MIN)) break block71;
                Hashtable hashtable4 = ExceptionHelper.setErrorMessageAsHashtable("You current mig level is not high enough to create a post");
                Object var14_19 = null;
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
                return hashtable4;
            }
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select count(*) from userpost where username=? and datecreated >= date_sub(now(), interval 1 hour)");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next() || rs.getInt(1) <= SystemProperty.getInt("MaxUserPostsPerHour")) break block75;
            Hashtable hashtable5 = ExceptionHelper.setErrorMessageAsHashtable("You are creating too many posts. Please slow down :)");
            Object var14_20 = null;
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
            return hashtable5;
        }
        rs.close();
        ps.close();
        connSlave.close();
        UserPostData postData = new UserPostData();
        postData.username = username;
        postData.body = body;
        postData.dateCreated = new Date();
        postData.numReplies = 0;
        postData.lastReplyDate = postData.dateCreated;
        if (parentGroupPostID > 0) {
            postData.parentUserPostID = parentGroupPostID;
        }
        postData.status = UserPostData.StatusEnum.ACTIVE;
        WebLocal webBean = (WebLocal)EJBHomeCache.getLocalObject("WebLocal", WebLocalHome.class);
        postData = webBean.createGroupUserPostTransaction(groupID, postData);
        if (parentGroupPostID > 0) {
            this.notifyUsersOfNewGroupUserPost(postData, groupID);
        }
        Hashtable hashtable6 = HashObjectUtils.dataObjectToHashtable(postData);
        Object var14_21 = null;
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
        return hashtable6;
        catch (SQLException e) {
            hashtable = ExceptionHelper.getRootMessageAsHashtable(e);
            Object var14_22 = null;
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
            return hashtable;
        }
        catch (Exception e) {
            hashtable = ExceptionHelper.getRootMessageAsHashtable(e);
            Object var14_23 = null;
            {
                catch (Throwable throwable) {
                    Object var14_24 = null;
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
            return hashtable;
        }
    }

    /*
     * Loose catch block
     */
    public UserPostData createGroupUserPostTransaction(int groupID, UserPostData postData) throws Exception {
        Connection connMaster = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connMaster = this.dataSourceMaster.getConnection();
        ps = connMaster.prepareStatement("insert into userpost (username, body, datecreated, numreplies, lastreplydate, parentuserpostid, status) values (?,?,?,?,?,?,?)", 1);
        ps.setString(1, postData.username);
        ps.setString(2, postData.body);
        ps.setTimestamp(3, new Timestamp(postData.dateCreated.getTime()));
        ps.setInt(4, postData.numReplies);
        ps.setTimestamp(5, new Timestamp(postData.lastReplyDate.getTime()));
        ps.setObject(6, postData.parentUserPostID);
        ps.setInt(7, postData.status.value());
        ps.executeUpdate();
        rs = ps.getGeneratedKeys();
        if (!rs.next()) {
            throw new EJBException("Unable to create post");
        }
        postData.id = rs.getInt(1);
        ps.close();
        if (postData.parentUserPostID != null) {
            ps = connMaster.prepareStatement("update userpost set numreplies=numreplies+1, lastreplydate=now() where id=?");
            ps.setInt(1, postData.parentUserPostID);
            ps.executeUpdate();
        }
        ps = connMaster.prepareStatement("insert into groupuserpost (groupid, userpostid) values (?,?)");
        ps.setInt(1, groupID);
        ps.setInt(2, postData.id);
        ps.executeUpdate();
        UserPostData userPostData = postData;
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return userPostData;
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

    private void notifyUsersOfNewGroupUserPost(UserPostData postData, int groupID) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupUserPosts(int groupID, int page, int numEntries) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select userpost.* from userpost, groupuserpost where userpost.id=groupuserpost.userpostid and groupuserpost.groupid=? and userpost.status=1 and userpost.parentuserpostid is null order by lastreplydate desc");
        ps.setInt(1, groupID);
        Vector<Hashtable> groupUserPosts = new Vector<Hashtable>();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                UserPostData post = new UserPostData(rs);
                groupUserPosts.add(HashObjectUtils.dataObjectToHashtable(post));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("group_user_posts", groupUserPosts);
        Hashtable<String, Serializable> hashtable = hash;
        Object var12_13 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getUserPost(int userPostID) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block32: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select * from userpost where id=? and status>0");
            ps.setInt(1, userPostID);
            rs = ps.executeQuery();
            if (!rs.next()) break block32;
            Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(new UserPostData(rs));
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
            return hashtable;
        }
        Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable("Post not found");
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var8_10 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getUserPostReplies(int userPostID, int page, int numEntries) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select userpost.* from userpost where userpost.parentuserpostid=? and userpost.status=1 order by datecreated desc");
        ps.setInt(1, userPostID);
        Vector<Hashtable> groupUserPosts = new Vector<Hashtable>();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                UserPostData post = new UserPostData(rs);
                groupUserPosts.add(HashObjectUtils.dataObjectToHashtable(post));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("group_user_posts", groupUserPosts);
        Hashtable<String, Serializable> hashtable = hash;
        Object var12_13 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
            return hashtable2;
        }
    }

    /*
     * Loose catch block
     */
    public String deleteGroupUserPost(int groupID, int userPostID, String username) throws EJBException {
        int parentUserPostID;
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block40: {
            block36: {
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("select groupmember.username, userpost.parentuserpostid from groupmember, groupuserpost, userpost where groupuserpost.groupid=groupmember.groupid and groupmember.type=2 and groupmember.username=? and groupuserpost.userpostid=userpost.id and userpost.id=? and groupuserpost.groupid=?");
                ps.setString(1, username);
                ps.setInt(2, userPostID);
                ps.setInt(3, groupID);
                rs = ps.executeQuery();
                if (rs.next()) break block36;
                String string = ExceptionHelper.setErrorMessage("You must be an admin of the group");
                Object var11_10 = null;
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
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                return string;
            }
            parentUserPostID = rs.getInt("parentuserpostid");
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("update userpost set status=0 where id=?");
            ps.setInt(1, userPostID);
            if (ps.executeUpdate() == 1) break block40;
            String string = ExceptionHelper.setErrorMessage("Unable to remove group post");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return string;
        }
        ps.close();
        if (parentUserPostID > 0) {
            ps = connMaster.prepareStatement("select count(*) numreplies, max(datecreated) lastreplydate from userpost where status=1 and parentuserpostid=?");
            ps.setInt(1, parentUserPostID);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Failed to delete group post");
            }
            int numReplies = rs.getInt("numreplies");
            Timestamp lastReplyDate = rs.getTimestamp("lastreplydate");
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("update userpost set numreplies=?, lastreplydate=? where id=?");
            ps.setInt(1, numReplies);
            ps.setTimestamp(2, lastReplyDate);
            ps.setInt(3, parentUserPostID);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Failed to update group post");
            }
        }
        rs.close();
        ps.close();
        connMaster.close();
        String string = "TRUE";
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return string;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
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

    public String createChatroom(String username, String chatRoomName, String language, String description, String keywords, boolean allowKicking) {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            ChatRoomData chatRoom = new ChatRoomData();
            chatRoom.creator = username;
            chatRoom.name = chatRoomName;
            chatRoom.language = language;
            chatRoom.description = description;
            chatRoom.allowKicking = allowKicking;
            chatRoom.allowBots = true;
            chatRoom.userOwned = true;
            messageBean.createChatRoom(chatRoom, keywords);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String updateRoomDetails(String username, String chatRoomName, String language, String description) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.updateRoomDetails(username, chatRoomName, language, description);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String updateRoomKickingRule(String username, String chatRoomName, boolean allowKicking) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.updateRoomKickingRule(username, chatRoomName, allowKicking);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String addRoomModerator(String ownerUsername, String chatRoomName, String moderatorUsername) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.addRoomModerator(ownerUsername, chatRoomName, moderatorUsername);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String removeRoomModerator(String ownerUsername, String chatRoomName, String moderatorUsername) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.removeRoomModerator(ownerUsername, chatRoomName, moderatorUsername);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String banUserFromRoom(String ownerUsername, String chatRoomName, String bannedUsername) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.banUserFromRoom(ownerUsername, chatRoomName, bannedUsername);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String unbanUserFromRoom(String ownerUsername, String chatRoomName, String bannedUsername) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.unbanUserFromRoom(ownerUsername, chatRoomName, bannedUsername);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String sendChangeRoomOwnerEmail(String oldOwnerUsername, String chatRoomName, String newOwnerUsername) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.sendChangeRoomOwnerEmail(oldOwnerUsername, chatRoomName, newOwnerUsername);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    public String changeRoomOwner(String oldOwnerUsername, String chatRoomName, String newOwnerUsername) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.changeRoomOwner(oldOwnerUsername, chatRoomName, newOwnerUsername);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getRoomModerators(String username, String chatRoomName) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select chatroommoderator.username from chatroom, chatroommoderator where chatroom.name=? and chatroom.status=1 and chatroom.id=chatroommoderator.chatroomid order by username");
        ps.setString(1, chatRoomName);
        rs = ps.executeQuery();
        Vector<String> moderators = new Vector<String>();
        while (rs.next()) {
            moderators.add(rs.getString("username"));
        }
        Vector<String> vector = moderators;
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
        return vector;
        catch (SQLException e) {
            Vector vector2;
            try {
                vector2 = ExceptionHelper.getRootMessageAsVector(e);
                Object var9_11 = null;
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
            return vector2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getRoomBannedUsers(String username, String chatRoomName, int page, int numEntries) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        String sql = "select chatroombanneduser.username from chatroom, chatroombanneduser where chatroom.name=? and chatroom.creator=? and chatroom.status=1 and chatroom.id=chatroombanneduser.chatroomid union select chatroombanneduser.username from chatroom, chatroombanneduser, chatroommoderator where chatroom.name=? and chatroommoderator.username=? and chatroom.id=chatroommoderator.chatroomid and chatroom.status=1 and chatroom.id=chatroombanneduser.chatroomid order by username";
        ps = connSlave.prepareStatement(sql);
        ps.setString(1, chatRoomName);
        ps.setString(2, username);
        ps.setString(3, chatRoomName);
        ps.setString(4, username);
        Vector<String> bannedUsers = new Vector<String>();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                bannedUsers.add(rs.getString(1));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("banned_users", bannedUsers);
        Hashtable<String, Serializable> hashtable = hash;
        Object var14_15 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var14_16 = null;
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
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
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
            return hashtable2;
        }
    }

    public String updateRoomKeywords(String username, String chatRoomName, String keywords, int allowUserKeywords) throws EJBException {
        try {
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.updateRoomKeywords(username, chatRoomName, keywords, allowUserKeywords);
            return "TRUE";
        }
        catch (CreateException e) {
            return ExceptionHelper.getRootMessage((Exception)((Object)e));
        }
        catch (EJBException e) {
            return ExceptionHelper.setErrorMessage(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getChatroom(String chatRoomName) {
        ResultSet rs;
        PreparedStatement ps;
        Connection connSlave;
        block32: {
            connSlave = null;
            ps = null;
            rs = null;
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select c.*, (select group_concat(keyword) from chatroomkeyword ck, keyword k where ck.keywordid = k.id and ck.chatroomid = c.id) as keywords from chatroom c where c.name = ? and c.status = ?");
            ps.setString(1, chatRoomName);
            ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            if (!rs.next()) break block32;
            Hashtable hashtable = HashObjectUtils.dataObjectToHashtable(new ChatRoomData(rs));
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
            return hashtable;
        }
        Hashtable hashtable = ExceptionHelper.setErrorMessageAsHashtable("Chat room not found");
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var8_10 = null;
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
            return hashtable2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getUserOwnedChatrooms(String username, int page, int numEntries) {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select c.*, (select group_concat(keyword) from chatroomkeyword ck, keyword k where ck.keywordid = k.id and ck.chatroomid = c.id) as keywords from chatroom c where c.userowned = 1 and c.creator = ? and c.status = ?");
        ps.setString(1, username);
        ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
        Vector<Hashtable> chatrooms = new Vector<Hashtable>();
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                chatrooms.add(HashObjectUtils.dataObjectToHashtable(new ChatRoomData(rs)));
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("chatrooms", chatrooms);
        Hashtable<String, Serializable> hashtable = hash;
        Object var12_13 = null;
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
        return hashtable;
        catch (SQLException e) {
            Hashtable hashtable2;
            try {
                hashtable2 = ExceptionHelper.getRootMessageAsHashtable(e);
                Object var12_14 = null;
            }
            catch (Throwable throwable) {
                Object var12_15 = null;
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
            return hashtable2;
        }
    }

    public boolean isModeratorOfChatRoom(String userName, String chatRoomName) throws EJBException {
        try {
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            return messageEJB.isModeratorOfChatRoom(userName, chatRoomName);
        }
        catch (Exception e) {
            throw new EJBException(ExceptionHelper.getRootMessage(e));
        }
    }

    public Hashtable searchChatroom(int countryId, String search, String language, boolean includeAdultOnly, boolean searchKeywords, int page, int numberOfEntries) {
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        try {
            int start;
            int end;
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            List chatrooms = messageBean.getChatRooms(countryId, search, language, includeAdultOnly, searchKeywords);
            int total_results = chatrooms.size();
            double total_pages = Math.ceil((double)total_results / (double)numberOfEntries);
            if ((double)page > total_pages) {
                page = (int)total_pages;
            }
            if (page < 1) {
                page = 1;
            }
            if ((end = (start = (page - 1) * numberOfEntries) + numberOfEntries) > total_results) {
                end = total_results;
            }
            Vector<Hashtable> v = new Vector<Hashtable>();
            for (int i = start; i < end; ++i) {
                ChatRoomData chatroom = (ChatRoomData)chatrooms.get(i);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatroom.name);
                if (chatRoomPrx != null) {
                    chatroom.size = chatRoomPrx.getNumParticipants();
                }
                v.add(HashObjectUtils.dataObjectToHashtable(chatroom));
            }
            hash.put("totalresults", Integer.valueOf(total_results));
            hash.put("totalpages", Double.valueOf(total_pages));
            hash.put("page", Integer.valueOf(page));
            hash.put("chatrooms", v);
            return hash;
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Hashtable getPendingContact(String username, int index) {
        try {
            Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
            if (index < 1) {
                return hash;
            }
            ContactLocal contactEJB = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            Vector singleContact = new Vector();
            LinkedList pendingContacts = new LinkedList(contactEJB.getPendingContacts(username));
            if (SystemProperty.getBool(SystemPropertyEntities.Contacts.PUSH_NEW_FOLLOWERS_AS_PENDING_CONTACTS_ENABLED)) {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                int userID = userEJB.getUserID(username, null);
                pendingContacts.addAll(contactEJB.getRecentFollowers(userID));
            }
            int numberOfPendingContacts = pendingContacts.size();
            if (index - 1 < pendingContacts.size()) {
                singleContact.add(pendingContacts.get(index - 1));
            }
            hash.put("totalresults", Integer.valueOf(numberOfPendingContacts));
            hash.put("totalpages", Integer.valueOf(numberOfPendingContacts));
            hash.put("page", Integer.valueOf(index));
            hash.put("pending_contacts", singleContact);
            if (log.isDebugEnabled()) {
                log.debug((Object)("getPendingContact - page[" + index + "] total[" + numberOfPendingContacts + "] pending_contacts[" + singleContact + "]"));
            }
            return hash;
        }
        catch (Exception e) {
            log.error((Object)("Unexpected exception caught : " + e.getMessage()), (Throwable)e);
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
    }

    /*
     * Loose catch block
     */
    public int getPendingContactCount(String username) {
        ResultSet rs;
        PreparedStatement ps;
        Connection connMaster;
        block26: {
            connMaster = null;
            ps = null;
            rs = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select count(username) as pendingcount from pendingcontact where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            int n = rs.getInt("pendingcount");
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
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            return n;
        }
        int n = -1;
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
            if (connMaster != null) {
                connMaster.close();
            }
        }
        catch (SQLException e2) {
            connMaster = null;
        }
        return n;
        catch (Exception e) {
            try {
                throw new EJBException(e);
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

    public String acceptPendingContact(int userID, String username, String contactname, int groupid, boolean shareMobilePhone) {
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            ContactData contactData = new ContactData();
            contactData.username = username;
            contactData.fusionUsername = contactname;
            contactData.contactGroupId = groupid > 0 ? Integer.valueOf(groupid) : null;
            contactData.displayOnPhone = true;
            contactData.shareMobilePhone = shareMobilePhone;
            contactBean.acceptContactRequest(userID, contactData, false);
            return "TRUE";
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    public Vector getContactGroupList(String username) {
        try {
            Vector<Hashtable> v = new Vector<Hashtable>();
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            List groups = contactBean.getGroupList(username);
            for (int i = 0; i < groups.size(); ++i) {
                v.add(HashObjectUtils.dataObjectToHashtable(groups.get(i)));
            }
            return v;
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsVector(e);
        }
    }

    public String rejectContactInvitation(int userID, String username, String contactname) {
        try {
            ContactLocal contactBean = (ContactLocal)EJBHomeCache.getLocalObject("ContactLocal", ContactLocalHome.class);
            contactBean.rejectContactRequest(userID, username, contactname);
            return "TRUE";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public int getAnonymousCallSetting(String username) throws EJBException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            return userEJB.getAnonymousCallSetting(username).value();
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
    }

    public boolean updateAnonymousCallSetting(String username, int value) throws EJBException {
        try {
            UserSettingData.AnonymousCallEnum settingData = UserSettingData.AnonymousCallEnum.fromValue(value);
            if (settingData == null) {
                throw new EJBException("Invalid setting value " + value);
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userEJB.updateAnonymousCallSetting(username, settingData);
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
        return true;
    }

    public int getMessageSetting(String username) throws EJBException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            return userEJB.getMessageSetting(username).value();
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
    }

    public boolean updateMessageSetting(String username, int value) throws EJBException {
        try {
            UserSettingData.MessageEnum settingData = UserSettingData.MessageEnum.fromValue(value);
            if (settingData == null) {
                throw new EJBException("Invalid setting value " + value);
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userEJB.updateMessageSetting(username, settingData);
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
        return true;
    }

    public String addIMDetail(String username, int imType, String imUsername, String imPassword) throws EJBException {
        try {
            UserPrx userPrx;
            ImType type = ImType.fromValue(imType);
            if (type == null) {
                return ExceptionHelper.setErrorMessage("Invalid IM type " + imType);
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userEJB.updateOtherIMDetail(username, type, imUsername, imPassword);
            if (!StringUtil.isBlank(imUsername) && (userPrx = EJBIcePrxFinder.findUserPrx(username)) != null) {
                userPrx.otherIMLogout(imType);
                userPrx.otherIMLogin(imType, PresenceType.AVAILABLE.value(), false);
            }
            return "TRUE";
        }
        catch (FusionException e) {
            return ExceptionHelper.setErrorMessage(e.message);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    public Hashtable getIMContacts(String username, int page, int numEntries) throws EJBException {
        try {
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
            if (userPrx == null) {
                throw new EJBException("You are not online");
            }
            ContactDataIce[] otherIMContacts = userPrx.getOtherIMContacts();
            Vector offlineContats = new Vector();
            Vector onlineContacts = new Vector();
            int start = (page - 1) * numEntries;
            int end = Math.min(page * numEntries, otherIMContacts.length);
            for (int i = start; i < end; ++i) {
                ContactData contact = new ContactData(otherIMContacts[i]);
                PresenceType presence = PresenceType.OFFLINE;
                Hashtable<String, Object> hash = new Hashtable<String, Object>();
                if (contact.isMSNOnly()) {
                    hash.put("type", ImType.MSN.value());
                    hash.put("username", contact.msnUsername);
                    presence = contact.msnPresence;
                } else if (contact.isYahooOnly()) {
                    hash.put("type", ImType.YAHOO.value());
                    hash.put("username", contact.yahooUsername);
                    presence = contact.yahooPresence;
                } else if (contact.isAIMOnly()) {
                    hash.put("type", ImType.AIM.value());
                    hash.put("username", contact.aimUsername);
                    presence = contact.aimPresence;
                } else if (contact.isGTalkOnly()) {
                    hash.put("type", ImType.GTALK.value());
                    hash.put("username", contact.gtalkUsername);
                    presence = contact.gtalkPresence;
                } else if (contact.isFacebookOnly()) {
                    hash.put("type", ImType.FACEBOOK.value());
                    hash.put("username", contact.facebookUsername);
                    presence = contact.facebookPresence;
                }
                hash.put("displayName", contact.displayName);
                hash.put("presence", presence.value());
                if (presence == PresenceType.OFFLINE) {
                    offlineContats.add(hash);
                    continue;
                }
                onlineContacts.add(hash);
            }
            onlineContacts.addAll(offlineContats);
            Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
            hash.put("totalresults", Integer.valueOf(otherIMContacts.length));
            hash.put("totalpages", Double.valueOf(Math.ceil((double)otherIMContacts.length / (double)numEntries)));
            hash.put("page", Integer.valueOf(page));
            hash.put("im_contacts", onlineContacts);
            return hash;
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessageAsHashtable(e);
        }
    }

    public String inviteIMContact(String username, int imType, String imContact) throws EJBException {
        try {
            ImType type = ImType.fromValue(imType);
            if (type == null) {
                throw new EJBException("Invalid IM type " + imType);
            }
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
            if (userPrx == null) {
                return ExceptionHelper.setErrorMessage("You are not online");
            }
            userPrx.otherIMSendMessage(imType, imContact, SystemProperty.get("OtherIMInvitationMessage").replaceAll("%im", type.toString()));
            return "TRUE";
        }
        catch (FusionException e) {
            return ExceptionHelper.setErrorMessage(e.message);
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    public boolean isIndosatIP(String ipAddress) throws EJBException {
        for (String indosatIP : SystemProperty.get("IndosatIPs", "").split(";")) {
            if (!ipAddress.startsWith(indosatIP)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Vector getActiveSubscriptions(String username) throws EJBException {
        Vector<Hashtable> subscriptions;
        block28: {
            Connection connSlave = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            subscriptions = new Vector<Hashtable>();
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select subscription.*, service.name servicename from service, subscription where service.id=subscription.serviceid and username=? and subscription.status=1");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                subscriptions.add(HashObjectUtils.dataObjectToHashtable(new SubscriptionData(rs)));
            }
            Object var9_6 = null;
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
                break block28;
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            break block28;
            {
                catch (SQLException e) {
                    Vector vector = ExceptionHelper.getRootMessageAsVector(e);
                    Object var9_7 = null;
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
                    return vector;
                }
            }
            catch (Throwable throwable) {
                Object var9_8 = null;
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
        return subscriptions;
    }

    public String cancelSubscription(String username, int subscriptionID) {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.cancelSubscription(username, subscriptionID);
            return "TRUE";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Hashtable getGroupHasExclusiveContent(int groupID) {
        Hashtable<String, String> hash;
        block28: {
            hash = new Hashtable<String, String>();
            Connection connSlave = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            connSlave = this.dataSourceMaster.getConnection();
            String sql = "select 'Emoticons' as item, count(*) c from emoticonpack where groupid=? and status=1 union select 'Virtual Gifts' as item, count(*) c from virtualgift where groupid=? and status=1 union select 'Ringtones' as item, count(*) c from content where groupid=? and type=? and status=1 union select 'Wallpapers' as item, count(*) c from content where groupid=? and type=? and status=1 union select 'Games' as item, count(*) c from content where groupid=? and type=? and status=1";
            ps = connSlave.prepareStatement(sql);
            ps.setInt(1, groupID);
            ps.setInt(2, groupID);
            ps.setInt(3, groupID);
            ps.setInt(4, ContentData.TypeEnum.RINGTONE.value());
            ps.setInt(5, groupID);
            ps.setInt(6, ContentData.TypeEnum.WALLPAPER.value());
            ps.setInt(7, groupID);
            ps.setInt(8, ContentData.TypeEnum.APPLICATION.value());
            rs = ps.executeQuery();
            while (rs.next()) {
                hash.put(rs.getString("item"), String.valueOf(rs.getInt("c")));
            }
            Object var9_8 = null;
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
                break block28;
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            break block28;
            {
                catch (SQLException e) {
                    Hashtable hashtable = ExceptionHelper.getRootMessageAsHashtable(e);
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
                    return hashtable;
                }
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
        return hash;
    }

    /*
     * Loose catch block
     */
    public Vector getLanguages() throws EJBException {
        Vector languages;
        block22: {
            Connection connSlave = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            languages = new Vector();
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("select code, name from language where status=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                Hashtable<String, String> languageHash = new Hashtable<String, String>();
                languageHash.put("code", rs.getString("code"));
                languageHash.put("name", rs.getString("name"));
                languages.add(languageHash);
            }
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
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
                break block22;
            }
            catch (SQLException e) {
                connSlave = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
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
        return languages;
    }

    public Hashtable getUserLevel(String username) {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            ReputationLevelData userLevel = userBean.getReputationLevel(username);
            return HashObjectUtils.dataObjectToHashtable(userLevel);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public Hashtable getBotList(int page, int numEntries) throws EJBException {
        Connection connSlave = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Vector bots = new Vector();
        connSlave = this.dataSourceSlave.getConnection();
        ps = connSlave.prepareStatement("select * from bot where status = 1 order by game");
        rs = ps.executeQuery();
        if (rs.next()) {
            rs.absolute((page - 1) * numEntries + 1);
            for (int i = 0; i < numEntries && !rs.isAfterLast(); ++i) {
                BotData botData = new BotData(rs);
                Hashtable<String, Object> h = new Hashtable<String, Object>();
                h.put("id", botData.getId());
                h.put("displayName", botData.getGame());
                h.put("description", botData.getDescription());
                bots.add(h);
                rs.next();
            }
        }
        int size = rs.last() ? rs.getRow() : 0;
        Hashtable<String, Serializable> hash = new Hashtable<String, Serializable>();
        hash.put("totalresults", Integer.valueOf(size));
        hash.put("totalpages", Double.valueOf(Math.ceil((double)size / (double)numEntries)));
        hash.put("page", Integer.valueOf(page));
        hash.put("bots", bots);
        Hashtable<String, Serializable> hashtable = hash;
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
        return hashtable;
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

    public Hashtable chargeUserForGameItem(String username, String reference, String description, double amount, String currency, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            AccountEntryData accountEntryData = accountEJB.chargeUserForGameItem(username, reference, description, amount, currency, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return HashObjectUtils.dataObjectToHashtable(accountEntryData);
        }
        catch (Exception e) {
            String error = e.getMessage().split(";")[0];
            return ExceptionHelper.setErrorMessageAsHashtable(error);
        }
    }

    public Hashtable giveGameReward(String username, String reference, String description, double amount, double fundedAmount, String currency, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            AccountEntryData accountEntryData = accountEJB.giveGameReward(username, reference, description, amount, fundedAmount, currency, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return HashObjectUtils.dataObjectToHashtable(accountEntryData);
        }
        catch (Exception e) {
            String error = e.getMessage().split(";")[0];
            return ExceptionHelper.setErrorMessageAsHashtable(error);
        }
    }

    public Hashtable thirdPartyAPIDebit(String username, String reference, String description, double amount, String currency, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            AccountEntryData accountEntryData = accountEJB.thirdPartyAPIDebit(username, reference, description, amount, currency, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return HashObjectUtils.dataObjectToHashtable(accountEntryData);
        }
        catch (Exception e) {
            String error = e.getMessage().split(";")[0];
            return ExceptionHelper.setErrorMessageAsHashtable(error);
        }
    }

    public String activateAccount(String username, String verificationCode, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.activateAccount(username, verificationCode, false, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "TRUE";
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public boolean updateEmoticonPackStatus(String username, int emoticonPackId, int status) throws EJBException {
        try {
            ContentLocal contentBean = (ContentLocal)EJBHomeCache.getLocalObject("ContentLocal", ContentLocalHome.class);
            contentBean.updateEmoticonPackStatus(username, emoticonPackId, status);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return true;
    }

    public String approveCreditCardPayment(String staffUsername, int creditCardPaymentId, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.approveCreditCardPayment(staffUsername, creditCardPaymentId, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "TRUE";
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public String rejectCreditCardPayment(String staffUsername, int creditCardPaymentId, String reason, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.rejectCreditCardPayment(staffUsername, creditCardPaymentId, reason, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return "TRUE";
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public String creditUserAndSendSMS(String username, double amountSent, double amountCredit, String cashReceiptID, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.creditAndNotifyUser(username, amountSent, amountCredit, cashReceiptID, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent), true);
            return "TRUE";
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public Vector getCreditCardTransactions(String startDate, String endDate, String sortBy, String sortOrder, String showAuth, String showPend, String showRej, String username, int displayLimit) throws Exception {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            return accountEJB.getCreditCardTransactions(startDate, endDate, sortBy, sortOrder, showAuth, showPend, showRej, username, displayLimit);
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public String disconnectUserIce(String username, String comment) throws EJBException {
        try {
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
            if (userPrx != null) {
                userPrx.disconnect(comment);
            }
            return "TRUE";
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public String updateUserDetailsIce(String username) throws EJBException {
        UserData userData = null;
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userData = userBean.loadUser(username, false, true);
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(userData.username);
            userPrx.userDetailChanged(userData.toIceObject());
            return "TRUE";
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public String deregisterChatroomIce(String chatroom) throws EJBException {
        try {
            EJBIcePrxFinder.getRegistry().deregisterChatRoomObject(chatroom);
            return "TRUE";
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public String resetMerchantPin(String username) throws EJBException {
        try {
            AuthenticationServiceCredentialResponse credential;
            AuthenticationServicePrx prx = EJBIcePrxFinder.getAuthenticationServiceProxy();
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            int userId = prx.userIDForFusionUsername(username);
            misBean.sendMerchantResetPinNotification(userId);
            misBean.removeMerchantPinAuthentication(userId);
            if (prx.exists(userId, (byte)15) == AuthenticationServiceResponseCodeEnum.Success) {
                credential = prx.getCredential(userId, (byte)15);
                prx.removeCredential(credential.userCredential);
            }
            if (prx.exists(userId, (byte)16) == AuthenticationServiceResponseCodeEnum.Success) {
                credential = prx.getCredential(userId, (byte)16);
                prx.removeCredential(credential.userCredential);
            }
            return "true";
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
    }

    public boolean resendVerificationCode(String username, String mobilePhone, String ipAddress, String sessionID, String mobileDevice, String userAgent) throws EJBException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            userBean.resendVerificationCode(username, mobilePhone, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
        }
        catch (Exception e) {
            throw new EJBException(e.getMessage());
        }
        return true;
    }

    public String blacklistUsersFromGroup(int groupId, String blacklistedByUser, String[] blacklistedUsernames) throws EJBException {
        try {
            GroupData groupData;
            if (blacklistedUsernames == null || blacklistedUsernames.length == 0) {
                throw new IllegalArgumentException("Please provide the usernames to blacklist");
            }
            if (StringUtil.isBlank(blacklistedByUser)) {
                throw new IllegalArgumentException("Please provide the blacklisting user");
            }
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            if (blacklistedUsernames.length == 1) {
                userBean.getUserID(blacklistedUsernames[0], null);
            }
            if ((groupData = userBean.getGroup(groupId)) != null) {
                if (!groupData.isOpenGroup()) {
                    throw new EJBException(groupData.name + " is not a public group. Users can be blacklisted only from public groups");
                }
                MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                ArrayList<String> failed = new ArrayList<String>();
                for (String username : blacklistedUsernames) {
                    try {
                        messageBean.banGroupMember(blacklistedByUser, groupData, username);
                    }
                    catch (Exception e) {
                        failed.add(username);
                    }
                }
                if (!failed.isEmpty()) {
                    throw new EJBException("Failed to blacklist the following users: " + failed);
                }
            } else {
                throw new EJBException("Group doesn't exist");
            }
            return "TRUE";
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    public String removeUsersFromGroupBlacklist(int groupId, String removingUser, String[] usersToBeRemoved) throws EJBException {
        try {
            if (usersToBeRemoved == null || usersToBeRemoved.length == 0) {
                throw new IllegalArgumentException("Please provide the usernames to be removed from blacklist");
            }
            if (StringUtil.isBlank(removingUser)) {
                throw new IllegalArgumentException("Please provide the session user");
            }
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            GroupData groupData = userBean.getGroup(groupId);
            if (groupData != null) {
                if (!groupData.isOpenGroup()) {
                    throw new EJBException(groupData.name + " is not a public group.");
                }
                MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                ArrayList<String> failed = new ArrayList<String>();
                for (String username : usersToBeRemoved) {
                    try {
                        messageBean.unbanGroupMember(removingUser, groupData, username);
                    }
                    catch (Exception e) {
                        failed.add(username);
                    }
                }
                if (!failed.isEmpty()) {
                    throw new EJBException("Failed to remove the following users from the blacklist: " + failed);
                }
            } else {
                throw new EJBException("Group doesn't exist");
            }
            return "TRUE";
        }
        catch (Exception e) {
            return ExceptionHelper.getRootMessage(e);
        }
    }

    public String sendApplicationEvent(String username, String applicationID, String jsonEncodedActivity) throws EJBException {
        try {
            String templateParams;
            JSONObject activity = new JSONObject(jsonEncodedActivity);
            String eventTitle = activity.getString("title");
            JSONArray jsonEncodedDeviceURLs = activity.optJSONArray("deviceCustomUrls");
            HashMap<String, String> eventDeviceURLs = new HashMap<String, String>();
            if (jsonEncodedDeviceURLs != null) {
                for (int i = 0; i < jsonEncodedDeviceURLs.length(); ++i) {
                    JSONObject jsonEncodedDeviceURL = jsonEncodedDeviceURLs.getJSONObject(i);
                    String type = jsonEncodedDeviceURL.getString("type");
                    String url = jsonEncodedDeviceURL.getString("url");
                    eventDeviceURLs.put(type, url);
                }
            }
            if ((templateParams = activity.optString("templateParams")) == null || templateParams.length() == 0) {
                if (SystemProperty.getBool(SystemPropertyEntities.Default.EVENT_SYSTEM_ENABLED)) {
                    EventSystemPrx eventSystem = EJBIcePrxFinder.getEventSystemProxy();
                    eventSystem.genericApplicationEvent(username, applicationID, eventTitle, eventDeviceURLs);
                }
            } else {
                String mediaItems = activity.getString("mediaItems");
                String applicationInfo = activity.getString("applicationInfo");
            }
        }
        catch (Exception e) {
            log.error((Object)("Failed to sendApplicationEvent for username [" + username + "] applicationID [" + applicationID + "] Activity [" + jsonEncodedActivity + "]"), (Throwable)e);
            return ExceptionHelper.getRootMessage(e);
        }
        return "TRUE";
    }

    public Hashtable getPaintWarsStats(String username) throws EJBException {
        PainterStats stats = null;
        try {
            stats = Painter.getStats(username);
        }
        catch (FusionException e) {
            log.error((Object)("Unable to retrieve user stats: " + e.message));
            throw new EJBException("Unable to retrieve stats. Please try again later.");
        }
        Hashtable<String, Integer> statsData = new Hashtable<String, Integer>();
        statsData.put("TotalPaintWarsPoints", stats.getTotalPaintWarsPoints());
        statsData.put("TotalPaintsSent", stats.getTotalPaintsSent());
        statsData.put("TotalPaintsReceived", stats.getTotalPaintsReceived());
        statsData.put("TotalCleansSent", stats.getTotalCleansSent());
        statsData.put("TotalCleansReceived", stats.getTotalCleansReceived());
        statsData.put("PaintsRemaining", stats.getPaintsRemaining());
        statsData.put("CleansRemaining", stats.getCleansRemaining());
        return statsData;
    }

    public String getPaintWarsUserIdenticonIndex(String username) throws EJBException {
        try {
            return Painter.getUserIdenticonIndex(username);
        }
        catch (FusionException e) {
            log.error((Object)("Unable to retrieve Paint Wars icon: " + e.message));
            throw new EJBException("Unable to retrieve Paint Wars icon. Please try again later.");
        }
    }

    public String executePaintWarsPaint(String username, String targetUsername) throws EJBException {
        try {
            String message = "";
            if (!username.equals(targetUsername)) {
                if (Painter.isClean(targetUsername)) {
                    if (!Painter.isPaintProof(targetUsername)) {
                        if (!Painter.hadInteraction(username, targetUsername)) {
                            if (!Painter.hasFreePaintCredits(username)) {
                                try {
                                    Painter.buyPaintCredit(username);
                                }
                                catch (FusionException e) {
                                    throw new EJBException(e.message);
                                }
                            }
                            int points = Painter.paint(username, targetUsername);
                            return MessageFormat.format("{0} has painted {1}. {0} received {2} points.", username, targetUsername, points);
                        }
                        throw new EJBException("You have already interacted with " + targetUsername + " today. Please try again in 24 hours.");
                    }
                    throw new EJBException(targetUsername + " is currently paint proof");
                }
                throw new EJBException(targetUsername + " has already been painted");
            }
            throw new EJBException("You cannot paint yourself");
        }
        catch (FusionException e) {
            log.error((Object)(username + " was unable to paint " + targetUsername + ": " + e.message));
            throw new EJBException("Unable to paint " + targetUsername + ". Please try again later.");
        }
    }

    public String executePaintWarsClean(String username, String targetUsername) throws EJBException {
        try {
            String message = "";
            if (!Painter.isClean(targetUsername)) {
                if (!Painter.hadInteraction(username, targetUsername)) {
                    if (!Painter.hasFreeCleanCredits(username)) {
                        try {
                            Painter.buyCleanCredit(username);
                        }
                        catch (FusionException e) {
                            throw new EJBException(e.message);
                        }
                    }
                    int points = Painter.clean(username, targetUsername);
                    return MessageFormat.format("{0} has cleaned paint on {1}. {0} received {2} points.", username, targetUsername, points);
                }
                throw new EJBException("You have already interacted with " + targetUsername + " today. Please try again in 24 hours.");
            }
            throw new EJBException(targetUsername + " is already clean");
        }
        catch (FusionException e) {
            log.error((Object)(username + " was unable to clean " + targetUsername + ": " + e.message));
            throw new EJBException("Unable to clean " + targetUsername + ". Please try again later.");
        }
    }

    public String isPaintWarsClean(String username) throws EJBException {
        try {
            if (Painter.isClean(username)) {
                return "TRUE";
            }
            return "FALSE";
        }
        catch (FusionException e) {
            log.error((Object)("Unable to check if user [" + username + "] is clean: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public String hadPaintWarsInteraction(String username1, String username2) throws EJBException {
        try {
            if (Painter.hadInteraction(username1, username2)) {
                return "TRUE";
            }
            return "FALSE";
        }
        catch (FusionException e) {
            log.error((Object)("Unable to check if user [" + username1 + "] had an interaction with [" + username2 + "]: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public String hasPaintWarsFreePaintCredits(String username) throws EJBException {
        try {
            if (Painter.hasFreePaintCredits(username)) {
                return "TRUE";
            }
            return "FALSE";
        }
        catch (FusionException e) {
            log.error((Object)("Unable to check if user [" + username + "] has free paint credits: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public String hasPaintWarsFreeCleanCredits(String username) throws EJBException {
        try {
            if (Painter.hasFreeCleanCredits(username)) {
                return "TRUE";
            }
            return "FALSE";
        }
        catch (FusionException e) {
            log.error((Object)("Unable to check if user [" + username + "] has free clean credits: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public String getPaintWarsPriceOfPaint() {
        return Painter.getPriceOfPaint();
    }

    public String getPaintWarsPriceOfClean() {
        return Painter.getPriceOfClean();
    }

    public String getPaintWarsPriceOfIdenticon() {
        return Painter.getPriceOfIdenticon();
    }

    public Vector getPaintWarsUserPaint(String username) throws EJBException {
        try {
            return Painter.getUserPaint(username);
        }
        catch (FusionException e) {
            log.error((Object)("Unable to retrieve Paint Wars icon: " + e.message));
            throw new EJBException("Unable to retrieve Paint Wars icon. Please try again later.");
        }
    }

    public String buyPaintWarsIdenticon(String username) throws EJBException {
        try {
            Painter.buyIdenticon(username);
        }
        catch (FusionException e) {
            throw new EJBException(e.message);
        }
        return "TRUE";
    }

    public Vector getPaintWarsStatsDetails(String username, int type, int offset, int numberOfEntries) throws EJBException {
        try {
            return Painter.getStatsDetails(username, type, offset, numberOfEntries);
        }
        catch (FusionException e) {
            log.error((Object)("Unable to retrieve user [" + username + "] stats details: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public Vector getPaintWarsSpecialItems() throws EJBException {
        try {
            Vector<ItemData> specialItems = Painter.getSpecialItems();
            Iterator<ItemData> itr = specialItems.iterator();
            Vector itemVector = new Vector();
            while (itr.hasNext()) {
                ItemData itemData = itr.next();
                Hashtable<String, String> item = new Hashtable<String, String>();
                item.put("ID", Integer.toString(itemData.getId()));
                item.put("Name", itemData.getName());
                item.put("Description", itemData.getDescription());
                item.put("Currency", itemData.getCurrency());
                item.put("Price", Double.toString(itemData.getPrice()));
                itemVector.add(item);
            }
            return itemVector;
        }
        catch (FusionException e) {
            log.error((Object)("Unable to retrieve item details: " + e.message));
            throw new EJBException("Unable to retrieve item details. Please try again later.");
        }
    }

    public Vector getPaintWarsUserInventory(String username) throws EJBException {
        try {
            return Painter.getUserInventory(username);
        }
        catch (FusionException e) {
            log.error((Object)("Unable to retrieve user [" + username + "] inventory: " + e.message));
            throw new EJBException("Unable to retrieve inventory details. Please try again later.");
        }
    }

    public String buyPaintWarsSpecialItem(String username, int itemId) throws EJBException {
        try {
            Painter.buySpecialItem(username, itemId);
        }
        catch (FusionException e) {
            throw new EJBException(e.message);
        }
        return "TRUE";
    }

    public String usePaintWarsSpecialItem(String username, int itemId) throws EJBException {
        try {
            Painter.useSpecialItem(username, itemId);
        }
        catch (FusionException e) {
            throw new EJBException(e.message);
        }
        return "TRUE";
    }

    public String isPaintWarsPaintProof(String username) throws EJBException {
        try {
            if (Painter.isPaintProof(username)) {
                return "TRUE";
            }
            return "FALSE";
        }
        catch (FusionException e) {
            log.error((Object)("Unable to check if user [" + username + "] is paint proof: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public String hasPaintWarsDualPaint(String username) throws EJBException {
        try {
            if (Painter.hasDualPaint(username)) {
                return "TRUE";
            }
            return "FALSE";
        }
        catch (FusionException e) {
            log.error((Object)("Unable to check if user [" + username + "] has dual paints: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public String hasPaintWarsStealthPaint(String username) throws EJBException {
        try {
            if (Painter.hasStealthPaint(username)) {
                return "TRUE";
            }
            return "FALSE";
        }
        catch (FusionException e) {
            log.error((Object)("Unable to check if user [" + username + "] has stealth paint: " + e.message));
            throw new EJBException("Unable to retrieve user details. Please try again later.");
        }
    }

    public String giveUnfundedCredits(String username, String reference, String description, double amount, String currency, String ipAddress, String userAgent) throws EJBException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            if (accountBean.giveUnfundedCredits(username, reference, description, amount, currency, new AccountEntrySourceData(ipAddress, null, null, userAgent)) != null) {
                return "TRUE";
            }
        }
        catch (CreateException e) {
            throw new EJBException("Unable to give unfunded credits to user [" + username + "]: " + e.getMessage());
        }
        catch (EJBException e) {
            log.error((Object)("Unable to give unfunded credits to user [" + username + "]: " + e.getMessage()));
            throw new EJBException("Unable to give unfunded credits to user [" + username + "]: " + e.getMessage());
        }
        return "FALSE";
    }
}

