/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  com.danga.MemCached.MemCachedClient
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.ejb;

import Ice.LocalException;
import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.cache.ChatRoomSearch;
import com.projectgoth.fusion.cache.RecentChatRoomList;
import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.common.ChatRoomValidationException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.ChatroomCategoryData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.EmailTemplateData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.GroupMemberData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.MessageDestinationData;
import com.projectgoth.fusion.data.MobilePrefixData;
import com.projectgoth.fusion.data.PremiumSMSPaymentData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.SMSGatewayData;
import com.projectgoth.fusion.data.SMSRouteData;
import com.projectgoth.fusion.data.SystemSMSData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserEmailAddressData;
import com.projectgoth.fusion.ejb.ConnectionHolder;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.ejb.InvalidMig33EmailRecipientEJBException;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.rewardsystem.RewardCentre;
import com.projectgoth.fusion.rewardsystem.triggers.EmailSentTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.SMSSentTrigger;
import com.projectgoth.fusion.search.ChatRoomsIndex;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.EmailUserNotification;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.smsengine.SMSControl;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MessageBean.class));
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private SecureRandom secureRandom;
    private Map<Integer, Double> iddCodes;
    private Vector<Integer> iddCodesAllowZero;
    private AccountLocalHome accountLocalHome;
    private String adultWordFilter;
    private static MemCachedClient recentChatRoomMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.recentChatRooms);
    private static MemCachedClient chatRoomSearchMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.chatRoomSearch);
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
            InitialContext ctx = new InitialContext();
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            this.accountLocalHome = (AccountLocalHome)ctx.lookup("AccountLocal");
            ctx.close();
            this.secureRandom = new SecureRandom();
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Exception e) {
            log.error((Object)"Unable to create Message EJB", (Throwable)e);
            throw new CreateException("Unable to create Message EJB: " + e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    private Vector<Integer> getIDDCodesAllowZeroVector() throws EJBException {
        block23: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (this.iddCodesAllowZero == null || this.iddCodesAllowZero.isEmpty()) {
                this.iddCodesAllowZero = new Vector();
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select iddcode from country where allowzeroafteriddcode = 1");
                rs = ps.executeQuery();
                while (rs.next()) {
                    this.iddCodesAllowZero.add(rs.getInt(1));
                }
                Object var6_4 = null;
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
                        throw new EJBException("Unable to check db for countries where zero is allowed after iddcode: " + e.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    Object var6_5 = null;
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
        return this.iddCodesAllowZero;
    }

    /*
     * Loose catch block
     */
    private Map<Integer, Double> getIDDCodesHashMap() throws EJBException {
        block24: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (this.iddCodes == null || this.iddCodes.isEmpty()) {
                this.iddCodes = new HashMap<Integer, Double>();
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select IDDCode, SMSCost from country");
                rs = ps.executeQuery();
                while (rs.next()) {
                    this.iddCodes.put(rs.getInt("IDDCode"), rs.getDouble("SMSCost"));
                }
                if (this.iddCodes.size() == 0) {
                    throw new EJBException("Unable to load IDD and SMS cost details. No records found");
                }
                Object var6_4 = null;
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
                        throw new EJBException("Unable to load IDD and SMS cost details: " + e.getMessage());
                    }
                }
                catch (Throwable throwable) {
                    Object var6_5 = null;
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
        return this.iddCodes;
    }

    /*
     * Loose catch block
     */
    private String getAdultWordFilter() throws EJBException {
        block24: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (StringUtil.isBlank(this.adultWordFilter)) {
                StringBuilder builder = new StringBuilder();
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select * from adultword");
                rs = ps.executeQuery();
                if (rs.next()) {
                    builder.append(".*(").append(rs.getString("word"));
                    do {
                        builder.append("|").append(rs.getString("word"));
                    } while (rs.next());
                    builder.append(").*");
                }
                this.adultWordFilter = builder.toString();
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
                    break block24;
                }
                catch (SQLException e) {
                    conn = null;
                }
                break block24;
                {
                    catch (SQLException e) {
                        throw new EJBException("Unable to load adult words: " + e.getMessage());
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
        return this.adultWordFilter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private Double getFixedSMSCost(int countryID) {
        Double cost;
        block28: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block25: {
                conn = null;
                ps = null;
                rs = null;
                cost = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select fixedsmscost.Cost/currency.ExchangeRate Cost from fixedsmscost, currency where fixedsmscost.countryid=? and fixedsmscost.currency=currency.code");
                ps.setInt(1, countryID);
                rs = ps.executeQuery();
                if (!rs.next()) break block25;
                cost = rs.getDouble("Cost");
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
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
                    System.err.println("Unable to check whether there is a fixed SMS cost. Details: " + e.getMessage());
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
                }
            }
            catch (Throwable throwable) {
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
                throw throwable;
            }
        }
        return cost;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private double getWholesaleSMSCost(Connection conn, int smsGatewayID, int iddCode, String mobilePhone) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        block17: {
            double d;
            ps = null;
            rs = null;
            try {
                ps = conn.prepareStatement("select smswholesalecost.cost / currency.exchangerate cost from smswholesalecost, currency where smswholesalecost.currency = currency.code and smswholesalecost.gatewayid = ? and smswholesalecost.iddcode = ? and (smswholesalecost.areacode = '' or ? like concat(smswholesalecost.iddcode, smswholesalecost.areacode, '%')) order by smswholesalecost.areacode desc");
                ps.setInt(1, smsGatewayID);
                ps.setInt(2, iddCode);
                ps.setString(3, mobilePhone);
                rs = ps.executeQuery();
                if (!rs.next()) break block17;
                d = rs.getDouble("cost");
                Object var10_9 = null;
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
            return d;
        }
        double d = 0.0;
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
        return d;
    }

    /*
     * Loose catch block
     */
    public MessageData saveSentMessage(MessageData messageData) throws EJBException {
        block28: {
            if (messageData.messageDestinations == null) {
                throw new EJBException("The message must have at least one destination");
            }
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("insert into message (username, datecreated, type, messagetext, sendreceive, sourcecontactid, source) values (?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, messageData.username);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setObject(3, messageData.type == null ? null : new Integer(messageData.type.value()));
            ps.setString(4, messageData.messageText);
            ps.setObject(5, MessageData.SendReceiveEnum.SEND.value());
            ps.setObject(6, messageData.sourceContactID);
            ps.setString(7, messageData.source);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new EJBException("Unable to obtain the ID of the inserted message");
            }
            messageData.id = rs.getInt(1);
            rs.close();
            ps.close();
            ps = conn.prepareStatement("insert into messagedestination (messageid, contactid, type, destination, iddcode, cost, gateway, datedispatched, status) values (?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            for (MessageDestinationData messageDestData : messageData.messageDestinations) {
                if (messageData.type == MessageType.FUSION || messageData.type == MessageType.MSN || messageData.type == MessageType.YAHOO || messageData.type == MessageType.AIM) {
                    if (messageDestData.cost == null) {
                        messageDestData.cost = 0.0;
                    }
                    if (messageData.type == MessageType.FUSION && messageDestData.status == null) {
                        messageDestData.status = MessageDestinationData.StatusEnum.PENDING;
                    }
                }
                messageDestData.contactID = null;
                ps.setInt(1, messageData.id);
                ps.setObject(2, messageDestData.contactID);
                ps.setObject(3, messageDestData.type == null ? null : new Integer(messageDestData.type.value()));
                ps.setString(4, messageDestData.destination);
                ps.setObject(5, messageDestData.IDDCode);
                ps.setObject(6, messageDestData.cost);
                ps.setObject(7, messageDestData.gateway);
                ps.setTimestamp(8, messageDestData.dateDispatched == null ? null : new Timestamp(messageDestData.dateDispatched.getTime()));
                ps.setObject(9, messageDestData.status == null ? null : Integer.valueOf(messageDestData.status.value()));
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    throw new EJBException("Unable to obtain the ID of an inserted message destination");
                }
                messageDestData.id = rs.getInt(1);
                rs.close();
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
        return messageData;
    }

    /*
     * Loose catch block
     */
    public MessageData saveReceivedMessage(MessageData messageData, Integer sentMessageDestinationID) throws EJBException {
        block24: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block21: {
                if (messageData.messageDestinations != null) {
                    throw new EJBException("The received message must not have a MessageDestination attached");
                }
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("insert into message (username, datecreated, type, messagetext, sendreceive, sourcecontactid, source) values (?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, messageData.username);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setObject(3, messageData.type == null ? null : new Integer(messageData.type.value()));
                ps.setString(4, messageData.messageText);
                ps.setObject(5, MessageData.SendReceiveEnum.RECEIVE.value());
                ps.setObject(6, messageData.sourceContactID);
                ps.setString(7, messageData.source);
                ps.executeUpdate();
                if (sentMessageDestinationID == null) break block21;
                this.changePendingMessageToSent(conn, ps, messageData.type, messageData.id, sentMessageDestinationID, null, null, null, null);
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
                catch (CreateException e) {
                    throw new EJBException(e.getMessage());
                }
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
        return messageData;
    }

    public MessageData sendSMS(MessageData messageData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        double balance;
        int maxLen;
        if (messageData.messageDestinations == null) {
            throw new EJBException("The message must have at least one destination");
        }
        if (messageData.type != MessageType.SMS) {
            throw new EJBException("The message type must be SMS");
        }
        if (messageData.messageText.matches("^[\\x00-\\xFF]*$")) {
            maxLen = 160;
            if (messageData.messageText.length() > maxLen) {
                throw new EJBException("The message exceeded " + maxLen + " character limit");
            }
        } else {
            maxLen = 69;
            if (messageData.messageText.length() > maxLen) {
                throw new EJBException("Unicode message must not contain more than " + maxLen + " characters");
            }
        }
        UserData userData = null;
        if (!SystemProperty.getBool(SystemPropertyEntities.Temp.PT73368964_ENABLED)) {
            try {
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                userData = userBean.loadUser(messageData.username, false, false);
                if (messageData.messageText.toLowerCase().contains(userData.password.toLowerCase())) {
                    MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                    throw new EJBException(misBean.getInfoText(22));
                }
            }
            catch (CreateException e) {
                throw new EJBException("Unable to perform password check (CreateException): " + e.getMessage());
            }
        }
        messageData.source = userData.mobilePhone;
        String smsBrand = SystemProperty.get("SMSBrand", "");
        if (messageData.messageText.length() + smsBrand.length() < maxLen) {
            messageData.messageText = messageData.messageText + "\n" + smsBrand;
        }
        double totalBillingAmount = 0.0;
        List<String> blockedSMSDestinations = Arrays.asList(SystemProperty.getArray("BlockedSMSDestinations", new String[0]));
        for (MessageDestinationData messageDestData : messageData.messageDestinations) {
            if (blockedSMSDestinations.contains(messageDestData.destination)) {
                throw new EJBException("Unable to send SMS to " + messageDestData.destination);
            }
            messageDestData.status = MessageDestinationData.StatusEnum.PENDING;
            messageDestData = this.assignIDDAndCost(messageDestData, userData.countryID);
            totalBillingAmount += messageDestData.cost.doubleValue();
        }
        AccountLocal accountBean = null;
        try {
            accountBean = this.accountLocalHome.create();
        }
        catch (CreateException e) {
            throw new EJBException("Unable to charge user (CreateException): " + e.getMessage());
        }
        if (totalBillingAmount != 0.0 && (balance = accountBean.getAccountBalance(messageData.username).getBaseBalance()) < totalBillingAmount) {
            throw new EJBException("You do not have enough credit. Please recharge your account");
        }
        messageData = this.saveSentMessage(messageData);
        String accountEntryDescription = "SMS sent to ";
        accountEntryDescription = messageData.messageDestinations.size() == 1 ? accountEntryDescription + messageData.messageDestinations.get((int)0).destination : accountEntryDescription + messageData.messageDestinations.size() + " recipients";
        accountBean.chargeUserForSMS(messageData.username, messageData.id.toString(), accountEntryDescription, totalBillingAmount, accountEntrySourceData);
        this.sendSMSToSMSSender(messageData);
        this.sendSMSToMessageLogger(messageData, userData);
        try {
            SMSSentTrigger trigger = new SMSSentTrigger(userData);
            trigger.amountDelta = totalBillingAmount;
            trigger.currency = CurrencyData.baseCurrency;
            trigger.quantityDelta = 1;
            RewardCentre.getInstance().sendTrigger(trigger);
        }
        catch (Exception e) {
            log.warn((Object)"Unable to notify reward system", (Throwable)e);
        }
        return messageData;
    }

    private void sendSMSToSMSSender(MessageData messageData) {
        try {
            EJBIcePrxFinder.getSMSSender().sendSMS(messageData.toIceObject(), 0L);
        }
        catch (FusionException fe) {
            System.err.println("Unable to send SMS to the SMS sending application: " + fe.message);
        }
        catch (Exception e) {
            System.err.println("Unable to send SMS to the SMS sending application: " + e.getMessage());
        }
    }

    private void sendSMSToMessageLogger(MessageData messageData, UserData userData) {
        try {
            if (!EJBIcePrxFinder.logMessagesToFile()) {
                return;
            }
            EJBIcePrxFinder.getOnewayMessageLoggerPrx().logMessage(MessageToLog.TypeEnum.SMS.value(), userData.countryID, messageData.username, messageData.messageDestinations.get((int)0).destination, 1, messageData.messageText);
        }
        catch (Exception e) {
            System.err.println("Unable to send SMS to the MessageLogger application: " + e.getMessage());
        }
    }

    private void sendSystemSMSToSMSSender(SystemSMSData systemSMSData, long delay) {
        try {
            EJBIcePrxFinder.getSMSSender().sendSystemSMS(systemSMSData.toIceObject(), delay);
        }
        catch (FusionException fe) {
            System.err.println("Unable to send system SMS to the SMS sending application: " + fe.message);
        }
        catch (Exception e) {
            System.err.println("Unable to send system SMS to the SMS sending application: " + e.getMessage());
        }
    }

    public void sendSystemSMS(SystemSMSData systemSMSData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        this.sendSystemSMS(systemSMSData, 0L, accountEntrySourceData);
    }

    public void sendSystemSMSNoTransaction(SystemSMSData systemSMSData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        this.sendSystemSMS(systemSMSData, 0L, accountEntrySourceData);
    }

    /*
     * Loose catch block
     */
    public void sendSystemSMS(SystemSMSData systemSMSData, long delay, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block37: {
            double balance;
            String accountEntryDescription;
            double accountEntryBillingAmount;
            if (!SystemProperty.getBool(SystemPropertyEntities.SmsSettings.SMS_ENGINE_ENABLED)) {
                if (SystemProperty.getBool(SystemPropertyEntities.SmsSettings.LOG_REFUSED_TO_SEND)) {
                    log.info((Object)"sendSystemSMS: Not sending SMS : SMSEngine disabled");
                }
                return;
            }
            if (!SMSControl.isSendEnabledForSubtype(systemSMSData.subType)) {
                return;
            }
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            List<String> blockedSMSDestinations = Arrays.asList(SystemProperty.getArray("BlockedSMSDestinations", new String[0]));
            if (blockedSMSDestinations.contains(systemSMSData.destination)) {
                throw new EJBException("Unable to send SMS to " + systemSMSData.destination);
            }
            systemSMSData.destination = this.cleanAndValidatePhoneNumber(systemSMSData.destination, true);
            systemSMSData.dateCreated = new Date();
            systemSMSData.IDDCode = this.getIDDCode(systemSMSData.destination);
            systemSMSData.status = SystemSMSData.StatusEnum.PENDING;
            switch (systemSMSData.subType) {
                case EMAIL_ALERT: {
                    accountEntryBillingAmount = systemSMSData.cost;
                    accountEntryDescription = "Email Alert SMS sent to " + systemSMSData.destination;
                    break;
                }
                case BUZZ: {
                    accountEntryBillingAmount = systemSMSData.cost;
                    accountEntryDescription = "Buzz SMS sent";
                    break;
                }
                case LOOKOUT: {
                    accountEntryBillingAmount = systemSMSData.cost;
                    accountEntryDescription = "Lookout SMS sent to " + systemSMSData.destination;
                    break;
                }
                case GROUP_ANNOUNCEMENT_NOTIFICATION: {
                    accountEntryBillingAmount = SystemProperty.getDouble("GroupSMSNotificationCost");
                    accountEntryDescription = "Group announcement SMS sent to " + systemSMSData.destination;
                    break;
                }
                case GROUP_EVENT_NOTIFICATION: {
                    accountEntryBillingAmount = SystemProperty.getDouble("GroupSMSNotificationCost");
                    accountEntryDescription = "Group event SMS sent to " + systemSMSData.destination;
                    break;
                }
                default: {
                    accountEntryBillingAmount = 0.0;
                    accountEntryDescription = "System SMS sent to " + systemSMSData.destination;
                }
            }
            AccountLocal accountBean = this.accountLocalHome.create();
            if (accountEntryBillingAmount > 0.0 && (balance = accountBean.getAccountBalance(systemSMSData.username).getBaseBalance()) < accountEntryBillingAmount) {
                throw new EJBException("Insufficient credit");
            }
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("insert into systemsms (username, datecreated, type, subtype, source, destination, iddcode, messagetext, status) values (?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
            ps.setString(1, systemSMSData.username);
            ps.setTimestamp(2, new Timestamp(systemSMSData.dateCreated.getTime()));
            ps.setObject(3, systemSMSData.type == null ? null : new Integer(systemSMSData.type.value()));
            ps.setObject(4, systemSMSData.subType == null ? null : new Integer(systemSMSData.subType.value()));
            ps.setString(5, systemSMSData.source);
            ps.setString(6, systemSMSData.destination);
            ps.setObject(7, systemSMSData.IDDCode);
            ps.setString(8, systemSMSData.messageText);
            ps.setInt(9, systemSMSData.status.value());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new EJBException("Failed to create system SMS entry");
            }
            systemSMSData.id = rs.getInt(1);
            if (systemSMSData.username != null) {
                accountBean.chargeUserForSystemSMS(systemSMSData.username, systemSMSData.id.toString(), accountEntryDescription, accountEntryBillingAmount, accountEntrySourceData);
            }
            this.sendSystemSMSToSMSSender(systemSMSData, delay);
            Object var16_15 = null;
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
                break block37;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block37;
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
                Object var16_16 = null;
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
    public void systemSMSFailed(int id, Integer gateway, String source) throws EJBException {
        block15: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update systemsms set Status = ?, Gateway = ?, Source = ? where ID = ?");
            ps.setInt(1, SystemSMSData.StatusEnum.FAILED.value());
            ps.setObject(2, gateway);
            ps.setString(3, source);
            ps.setInt(4, id);
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement("update premiumsmspayment set Status = ? where SystemSMSID = ?");
            ps.setInt(1, PremiumSMSPaymentData.StatusEnum.FAILED.value());
            ps.setInt(2, id);
            ps.executeUpdate();
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
    }

    /*
     * Loose catch block
     */
    public void smsFailed(int id, Integer gateway, String username, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block23: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block20: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select destination, cost from messagedestination where ID = ?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                if (!rs.next()) break block20;
                String destination = rs.getString("destination");
                rs.close();
                ps.close();
                ps = conn.prepareStatement("update messagedestination set Status = ?, Gateway = ? where ID = ?");
                ps.setInt(1, MessageDestinationData.StatusEnum.FAILED.value());
                ps.setObject(2, gateway);
                ps.setInt(3, id);
                ps.executeUpdate();
                AccountLocal accountBean = this.accountLocalHome.create();
                accountBean.refundUserForSMS(id, String.valueOf(id), "Failed to deliver SMS to " + destination, accountEntrySourceData);
            }
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
                catch (CreateException e) {
                    throw new EJBException("Unable to refund user (CreateException): " + e.getMessage());
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
    }

    private MessageDestinationData assignIDDAndCost(MessageDestinationData messageDestData, int countryID) throws EJBException {
        if (messageDestData.destination == null || messageDestData.destination.length() == 0) {
            throw new EJBException("No destination mobile phone number was specified");
        }
        messageDestData.destination = this.cleanAndValidatePhoneNumber(messageDestData.destination, true);
        messageDestData.IDDCode = this.getIDDCode(messageDestData.destination);
        if (messageDestData.cost == null) {
            Double fixedCost = this.getFixedSMSCost(countryID);
            messageDestData.cost = fixedCost != null ? fixedCost : this.getIDDCodesHashMap().get(messageDestData.IDDCode);
        }
        return messageDestData;
    }

    public int getSystemSMSCount(SystemSMSData.SubTypeEnum subType, String username) throws EJBException {
        return this.getSystemSMSCount(subType, username, null);
    }

    /*
     * Loose catch block
     */
    public int getSystemSMSCount(SystemSMSData.SubTypeEnum subType, String username, String destination) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        if (destination == null) {
            ps = conn.prepareStatement("select count(*) from systemsms where username = ? and type = ? and subtype = ? and datecreated > curdate()");
        } else {
            ps = conn.prepareStatement("select count(*) from systemsms where username = ? and type = ? and subtype = ? and destination = ? and datecreated > curdate()");
            ps.setString(4, destination);
        }
        ps.setString(1, username);
        ps.setInt(2, SystemSMSData.TypeEnum.STANDARD.value());
        ps.setInt(3, subType.value());
        rs = ps.executeQuery();
        rs.next();
        int n = rs.getInt(1);
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

    public String cleanPhoneNumber(String phoneNumber) throws EJBException {
        if (!StringUtils.hasLength((String)phoneNumber)) {
            throw new EJBException("Invalid phone number");
        }
        String cleanNumber = phoneNumber;
        if (cleanNumber.charAt(0) == '+') {
            cleanNumber = cleanNumber.substring(1);
        }
        cleanNumber = cleanNumber.replaceAll("[()-\\. ]", "");
        return cleanNumber;
    }

    public String cleanAndValidatePhoneNumber(String phoneNumber, boolean isMobile) throws EJBException {
        String cleanNumber = this.cleanPhoneNumber(phoneNumber);
        Integer iddCode = this.getIDDCode(cleanNumber);
        if (cleanNumber.length() - iddCode.toString().length() < 5 || !this.isNumeric(cleanNumber)) {
            throw new EJBException(cleanNumber + " is not a valid phone number");
        }
        boolean allowZeroInIDD = this.getIDDCodesAllowZeroVector().contains(iddCode);
        if (!allowZeroInIDD && isMobile && cleanNumber.substring(iddCode.toString().length()).startsWith("0")) {
            throw new EJBException(cleanNumber + " is not a valid phone number. Please remove 0 after the international code");
        }
        if (isMobile && !this.isMobileNumber(cleanNumber, true)) {
            throw new EJBException(cleanNumber + " is not a valid mobile phone number");
        }
        return cleanNumber;
    }

    public Integer getIDDCode(String phoneNumber) throws EJBException {
        if (StringUtil.isBlank(phoneNumber)) {
            throw new EJBException("empty number");
        }
        if (phoneNumber.charAt(0) == '0') {
            throw new EJBException("A country code was not specified in the number " + phoneNumber);
        }
        for (int i = 4; i >= 0; --i) {
            Integer possibleIDDCode;
            try {
                possibleIDDCode = Integer.parseInt(phoneNumber.substring(0, i));
            }
            catch (Exception e) {
                throw new EJBException("Unable to determine the country code of the number " + phoneNumber);
            }
            if (!this.getIDDCodesHashMap().containsKey(possibleIDDCode)) continue;
            return possibleIDDCode;
        }
        throw new EJBException("Unable to determine the country code of the number " + phoneNumber);
    }

    /*
     * Loose catch block
     */
    public MobilePrefixData getMobilePrefixData(int IDDCode, int mobilePrefix) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from mobileprefix where IDDCode=? and prefix=?");
            ps.setInt(1, IDDCode);
            ps.setInt(2, mobilePrefix);
            rs = ps.executeQuery();
            if (rs.next()) break block26;
            MobilePrefixData mobilePrefixData = null;
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
            return mobilePrefixData;
        }
        MobilePrefixData mobilePrefixData = new MobilePrefixData(rs);
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
        return mobilePrefixData;
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
    public int getMinimumMobileNumberLength(int IDDCode) throws EJBException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select min(minlength) from mobileprefix where IDDCode=?");
            ps.setInt(1, IDDCode);
            rs = ps.executeQuery();
            if (rs.next()) break block26;
            int n = -1;
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
        int n = rs.getInt(1);
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
    public boolean isMobileNumber(String phoneNumber, boolean verifyLength) throws EJBException {
        int totalLength;
        Integer iddCode;
        ResultSet rs;
        Statement ps;
        Connection conn;
        block29: {
            conn = null;
            ps = null;
            rs = null;
            iddCode = this.getIDDCode(phoneNumber);
            totalLength = phoneNumber.length();
            phoneNumber = phoneNumber.substring(iddCode.toString().length());
            if (!iddCode.equals(1)) break block29;
            boolean bl = phoneNumber.length() == 10;
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
            return bl;
        }
        conn = this.dataSourceSlave.getConnection();
        if (verifyLength) {
            ps = conn.prepareStatement("select count(*), sum(if(cast(? as unsigned) like concat(prefix, '%') and ? >= minlength and ? <= maxlength, 1, 0)) from mobileprefix where iddcode = ?");
            ps.setString(1, phoneNumber);
            ps.setInt(2, totalLength);
            ps.setInt(3, totalLength);
            ps.setInt(4, iddCode);
        } else {
            ps = conn.prepareStatement("select count(*), sum(if(cast(? as unsigned) like concat(prefix, '%'), 1, 0)) from mobileprefix where iddcode = ?");
            ps.setString(1, phoneNumber);
            ps.setInt(2, iddCode);
        }
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Unable to query mobile prefix table");
        }
        boolean bl = rs.getInt(1) == 0 || rs.getInt(2) > 0;
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
        }
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        }
        catch (NumberFormatException err) {
            return false;
        }
    }

    /*
     * Loose catch block
     */
    public void changePendingSystemSMSToSent(int type, int id, int gateway, String source, int iddCode, String destination, String transactionID, boolean billed) throws EJBException {
        block16: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update systemsms set Status = ?, DateDispatched = ?, Gateway = ?, ProviderTransactionID = ?, Source = ? where ID = ? and Status = ?");
            ps.setInt(1, SystemSMSData.StatusEnum.SENT.value());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setObject(3, gateway);
            ps.setString(4, transactionID);
            ps.setString(5, source);
            ps.setInt(6, id);
            ps.setInt(7, SystemSMSData.StatusEnum.PENDING.value());
            ps.executeUpdate();
            AccountLocal accountBean = this.accountLocalHome.create();
            accountBean.updateWholesaleSystemSMSCost(String.valueOf(id), this.getWholesaleSMSCost(conn, gateway, iddCode, destination));
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
    public void changePendingMessageToSent(MessageType messageType, int messageID, int messageDestinationID, Integer gateway, Integer iddCode, String destination, String transactionID) throws EJBException {
        block16: {
            Connection conn = null;
            Statement ps = null;
            conn = this.dataSourceMaster.getConnection();
            this.changePendingMessageToSent(conn, (PreparedStatement)ps, messageType, messageID, messageDestinationID, gateway, iddCode, destination, transactionID);
            Object var12_10 = null;
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
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var12_11 = null;
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

    private void changePendingMessageToSent(Connection conn, PreparedStatement ps, MessageType messageType, int messageID, int messageDestinationID, Integer gateway, Integer iddCode, String destination, String transactionID) throws CreateException, SQLException {
        ps = conn.prepareStatement("update messagedestination set Status = ?, DateDispatched = ?, Gateway = ?, ProviderTransactionID = ? where ID = ? and Status = ?");
        ps.setInt(1, MessageDestinationData.StatusEnum.SENT.value());
        ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        ps.setObject(3, gateway);
        ps.setObject(4, transactionID);
        ps.setInt(5, messageDestinationID);
        ps.setInt(6, MessageDestinationData.StatusEnum.PENDING.value());
        ps.executeUpdate();
        if (messageType == MessageType.SMS) {
            AccountLocal accountBean = this.accountLocalHome.create();
            accountBean.updateWholesaleSMSCost(String.valueOf(messageID), this.getWholesaleSMSCost(conn, gateway, iddCode, destination));
        }
    }

    /*
     * Loose catch block
     */
    public MessageData getMessage(int messageID) throws EJBException {
        MessageData messageData;
        block28: {
            messageData = new MessageData();
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from message where id = ?");
            ps.setInt(1, messageID);
            rs = ps.executeQuery();
            if (rs.next()) {
                messageData.id = messageID;
                messageData.username = rs.getString("Username");
                messageData.dateCreated = rs.getTimestamp("dateCreated");
                Integer intVal = (Integer)rs.getObject("Type");
                if (intVal != null) {
                    messageData.type = MessageType.fromValue(intVal);
                }
                messageData.messageText = rs.getString("MessageText");
                intVal = (Integer)rs.getObject("SendReceive");
                if (intVal != null) {
                    messageData.sendReceive = MessageData.SendReceiveEnum.fromValue(intVal);
                }
                messageData.sourceContactID = (Integer)rs.getObject("SourceContactID");
                messageData.source = rs.getString("Source");
                rs.close();
                ps.close();
                ps = conn.prepareStatement("select * from messagedestination where MessageID = ?");
                ps.setInt(1, messageID);
                rs = ps.executeQuery();
                while (rs.next()) {
                    MessageDestinationData messageDestData = new MessageDestinationData();
                    messageDestData.id = (Integer)rs.getObject("ID");
                    messageDestData.messageID = messageID;
                    messageDestData.contactID = (Integer)rs.getObject("ContactID");
                    intVal = (Integer)rs.getObject("Type");
                    if (intVal != null) {
                        messageDestData.type = MessageDestinationData.TypeEnum.fromValue(intVal);
                    }
                    messageDestData.destination = rs.getString("Destination");
                    messageDestData.IDDCode = (Integer)rs.getObject("IDDCode");
                    messageDestData.cost = (Double)rs.getObject("Cost");
                    messageDestData.gateway = (Integer)rs.getObject("Gateway");
                    messageDestData.dateDispatched = rs.getDate("DateDispatched");
                    intVal = (Integer)rs.getObject("Status");
                    if (intVal != null) {
                        messageDestData.status = MessageDestinationData.StatusEnum.fromValue(intVal);
                    }
                    if (messageData.messageDestinations == null) {
                        messageData.messageDestinations = new LinkedList<MessageDestinationData>();
                    }
                    messageData.messageDestinations.add(messageDestData);
                }
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
        return messageData;
    }

    /*
     * Loose catch block
     */
    public List<MessageData> getMessages(String username, Integer type, Integer sendReceive, Date fromDate, Integer contactID, Integer status) throws EJBException {
        LinkedList<MessageData> messages;
        block45: {
            messages = null;
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            String sql = "select message.ID as MessageID, message.Username, message.DateCreated, message.Type as MessageType, message.MessageText, message.SendReceive, message.SourceContactID, message.Source, messagedestination.ID as MessageDestinationID, messagedestination.ContactID, messagedestination.Type as MessageDestType, messagedestination.Destination, messagedestination.IDDCode, messagedestination.Cost, messagedestination.Gateway, messagedestination.DateDispatched, messagedestination.Status from message left outer join messagedestination on message.ID = messagedestination.MessageID ";
            String criteria = "";
            if (username != null) {
                criteria = criteria + " message.username = ?";
            }
            if (type != null) {
                if (criteria.length() > 0) {
                    criteria = criteria + " and";
                }
                criteria = criteria + " message.Type = " + type;
            }
            if (sendReceive != null) {
                if (criteria.length() > 0) {
                    criteria = criteria + " and";
                }
                criteria = criteria + " message.SendReceive = " + sendReceive;
            }
            if (fromDate != null) {
                if (criteria.length() > 0) {
                    criteria = criteria + " and";
                }
                criteria = criteria + " message.DateCreated >= ?";
            }
            if (contactID != null) {
                if (criteria.length() > 0) {
                    criteria = criteria + " and";
                }
                criteria = criteria + " (message.SourceContactID = " + contactID + " or messagedestination.ContactID = " + contactID + ")";
            }
            if (status != null) {
                if (criteria.length() > 0) {
                    criteria = criteria + " and";
                }
                criteria = criteria + " messagedestination.Status = " + status;
            }
            if (criteria.length() > 0) {
                sql = sql + " where " + criteria;
            }
            sql = sql + " order by message.DateCreated desc";
            ps = conn.prepareStatement(sql);
            if (username == null) {
                if (fromDate != null) {
                    ps.setTimestamp(1, new Timestamp(fromDate.getTime()));
                }
            } else {
                ps.setString(1, username);
                if (fromDate != null) {
                    ps.setTimestamp(2, new Timestamp(fromDate.getTime()));
                }
            }
            rs = ps.executeQuery();
            MessageData messageData = null;
            while (rs.next()) {
                if (messages == null) {
                    messages = new LinkedList<MessageData>();
                }
                if (messageData == null || messageData.id.intValue() != ((Integer)rs.getObject("MessageID")).intValue()) {
                    messageData = new MessageData();
                    messageData.id = (Integer)rs.getObject("MessageID");
                    messageData.username = rs.getString("Username");
                    messageData.dateCreated = rs.getTimestamp("DateCreated");
                    Integer intVal = (Integer)rs.getObject("MessageType");
                    if (intVal != null) {
                        messageData.type = MessageType.fromValue(intVal);
                    }
                    messageData.messageText = rs.getString("MessageText");
                    intVal = (Integer)rs.getObject("SendReceive");
                    if (intVal != null) {
                        messageData.sendReceive = MessageData.SendReceiveEnum.fromValue(intVal);
                    }
                    messageData.sourceContactID = (Integer)rs.getObject("SourceContactID");
                    messageData.source = rs.getString("Source");
                    messages.add(messageData);
                }
                if (rs.getObject("MessageDestinationID") == null) continue;
                MessageDestinationData messageDestData = new MessageDestinationData();
                messageDestData.id = (Integer)rs.getObject("MessageDestinationID");
                messageDestData.messageID = messageData.id;
                messageDestData.contactID = (Integer)rs.getObject("ContactID");
                Integer intVal1 = (Integer)rs.getObject("MessageDestType");
                if (intVal1 != null) {
                    messageDestData.type = MessageDestinationData.TypeEnum.fromValue(intVal1);
                }
                messageDestData.destination = rs.getString("Destination");
                messageDestData.IDDCode = (Integer)rs.getObject("IDDCode");
                messageDestData.cost = (Double)rs.getObject("Cost");
                messageDestData.gateway = (Integer)rs.getObject("Gateway");
                messageDestData.dateDispatched = rs.getDate("DateDispatched");
                intVal1 = (Integer)rs.getObject("Status");
                if (intVal1 != null) {
                    messageDestData.status = MessageDestinationData.StatusEnum.fromValue(intVal1);
                }
                if (messageData.messageDestinations == null) {
                    messageData.messageDestinations = new LinkedList<MessageDestinationData>();
                }
                messageData.messageDestinations.add(messageDestData);
            }
            Object var17_17 = null;
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
        return messages;
    }

    /*
     * Loose catch block
     */
    public SystemSMSData getSystemSMS(String providerTransactionID, String destination) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select * from systemsms where providertransactionid = ? and destination = ?");
        ps.setString(1, providerTransactionID);
        ps.setString(2, destination);
        rs = ps.executeQuery();
        SystemSMSData systemSMSData = rs.next() ? new SystemSMSData(rs) : null;
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
        return systemSMSData;
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
    public List<SystemSMSData> getSystemSMS(String username, Integer type, Date fromDate, Integer status) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select * from systemsms";
        String criteria = "";
        if (username != null) {
            criteria = criteria + " username = ?";
        }
        if (type != null) {
            if (criteria.length() > 0) {
                criteria = criteria + " and";
            }
            criteria = criteria + " Type = " + type;
        }
        if (fromDate != null) {
            if (criteria.length() > 0) {
                criteria = criteria + " and";
            }
            criteria = criteria + " DateCreated >= ?";
        }
        if (status != null) {
            if (criteria.length() > 0) {
                criteria = criteria + " and";
            }
            criteria = criteria + " Status = " + status;
        }
        if (criteria.length() > 0) {
            sql = sql + " where " + criteria;
        }
        sql = sql + " order by DateCreated";
        ps = conn.prepareStatement(sql);
        if (username == null) {
            if (fromDate != null) {
                ps.setTimestamp(1, new Timestamp(fromDate.getTime()));
            }
        } else {
            ps.setString(1, username);
            if (fromDate != null) {
                ps.setTimestamp(2, new Timestamp(fromDate.getTime()));
            }
        }
        LinkedList<SystemSMSData> systemSMSList = new LinkedList<SystemSMSData>();
        rs = ps.executeQuery();
        while (rs.next()) {
            systemSMSList.add(new SystemSMSData(rs));
        }
        LinkedList<SystemSMSData> linkedList = systemSMSList;
        Object var13_13 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
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
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<SMSGatewayData> getSMSGateways() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select * from smsgateway inner join smsroute on smsgateway.id = smsroute.gatewayid where smsgateway.status = ? order by smsgateway.id, smsroute.iddcode, smsroute.priority";
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, SMSGatewayData.StatusEnum.ACTIVE.value());
        rs = ps.executeQuery();
        LinkedList<SMSGatewayData> gateways = new LinkedList<SMSGatewayData>();
        SMSGatewayData gateway = null;
        int previousGatewayID = Integer.MIN_VALUE;
        while (rs.next()) {
            int gatewayID = rs.getInt("id");
            if (gatewayID != previousGatewayID) {
                gateway = new SMSGatewayData();
                gateway.id = gatewayID;
                gateway.name = rs.getString("name");
                gateway.type = SMSGatewayData.TypeEnum.fromValue(rs.getInt("smsgateway.type"));
                gateway.url = rs.getString("url");
                gateway.port = (Integer)rs.getObject("port");
                gateway.method = SMSGatewayData.MethodEnum.fromValue(rs.getInt("method"));
                gateway.iddPrefix = rs.getString("iddPrefix");
                gateway.authorization = rs.getString("authorization");
                gateway.usernameParam = rs.getString("usernameParam");
                gateway.passwordParam = rs.getString("passwordParam");
                gateway.sourceParam = rs.getString("sourceParam");
                gateway.destinationParam = rs.getString("destinationParam");
                gateway.messageParam = rs.getString("messageParam");
                gateway.unicodeMessageParam = rs.getString("unicodeMessageParam");
                gateway.unicodeParam = rs.getString("unicodeParam");
                gateway.extraParam = rs.getString("extraParam");
                gateway.unicodeCharset = rs.getString("unicodeCharset");
                gateway.successPattern = rs.getString("successPattern");
                gateway.errorPattern = rs.getString("errorPattern");
                gateway.deliveryReporting = rs.getInt("deliveryReporting") != 0;
                gateway.status = SMSGatewayData.StatusEnum.ACTIVE;
                gateway.smsRoutes = new LinkedList<SMSRouteData>();
                gateways.add(gateway);
            }
            SMSRouteData route = new SMSRouteData();
            route.iddCode = rs.getInt("iddCode");
            route.areaCode = rs.getString("areaCode");
            route.type = SMSRouteData.TypeEnum.fromValue(rs.getInt("smsroute.type"));
            route.gatewayID = gatewayID;
            route.priority = rs.getInt("priority");
            gateway.smsRoutes.add(route);
            previousGatewayID = gatewayID;
        }
        LinkedList<SMSGatewayData> linkedList = gateways;
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
        return linkedList;
        catch (SQLException e) {
            try {
                log.error((Object)("Failed to generate SMSGatewayData, due to:" + e), (Throwable)e);
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
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public ChatRoomData getSimpleChatRoomData(Integer chatroomId, Connection conn) throws EJBException {
        block36: {
            ResultSet rs;
            PreparedStatement ps;
            ConnectionHolder slaveConnection;
            block31: {
                slaveConnection = new ConnectionHolder(this.dataSourceSlave, conn);
                ps = null;
                rs = null;
                ps = slaveConnection.getConnection().prepareStatement("select name from chatroom where id = ?");
                ps.setInt(1, chatroomId);
                rs = ps.executeQuery();
                if (!rs.next()) break block31;
                ChatRoomData chatRoomData = this.getSimpleChatRoomData(rs.getString("name"), conn);
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
                    if (slaveConnection != null) {
                        slaveConnection.close();
                    }
                }
                catch (SQLException e2) {
                    slaveConnection = null;
                }
                return chatRoomData;
            }
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
                if (slaveConnection != null) {
                    slaveConnection.close();
                }
                break block36;
            }
            catch (SQLException e2) {
                slaveConnection = null;
            }
            break block36;
            {
                catch (SQLException e) {
                    log.error((Object)("failed to retrieve chatroom data for chatroom id " + chatroomId), (Throwable)e);
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
                        if (slaveConnection != null) {
                            slaveConnection.close();
                        }
                        break block36;
                    }
                    catch (SQLException e2) {
                        slaveConnection = null;
                    }
                }
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
                    if (slaveConnection != null) {
                        slaveConnection.close();
                    }
                }
                catch (SQLException e2) {
                    slaveConnection = null;
                }
                throw throwable;
            }
        }
        return null;
    }

    /*
     * Unable to fully structure code
     */
    public ChatRoomData getSimpleChatRoomData(String normalizedChatRoomName, Connection conn) throws EJBException {
        block31: {
            c = new ConnectionHolder(this.dataSourceSlave, conn);
            ps = null;
            rs = null;
            negativeCacheEnabled = SystemProperty.getBool(SystemPropertyEntities.Chatroom.NEGATIVE_CACHE_ENABLED);
            if (negativeCacheEnabled && MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM_NEGATIVE_CACHE, normalizedChatRoomName) != null) {
                if (MessageBean.log.isDebugEnabled()) {
                    MessageBean.log.debug((Object)("Attempt to get invalid chatroom : " + normalizedChatRoomName + ",found in negative cache"));
                }
                return null;
            }
            room = (ChatRoomData)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM, normalizedChatRoomName);
            if (room != null) ** GOTO lbl62
            ps = c.getConnection().prepareStatement("select * from chatroom where name = ? and status = ?");
            ps.setString(1, normalizedChatRoomName);
            ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            if (rs.next() && normalizedChatRoomName.equalsIgnoreCase(rs.getString("name"))) break block31;
            if (negativeCacheEnabled) {
                MemCachedClientWrapper.add(MemCachedKeySpaces.CommonKeySpace.CHATROOM_NEGATIVE_CACHE, normalizedChatRoomName, 1);
            }
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
                if (c != null) {
                    c.close();
                }
            }
            catch (SQLException e) {
                c = null;
            }
            return var8_9;
        }
        room = new ChatRoomData(rs);
        rs.close();
        ps.close();
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.MULTI_ID_CONTROL_ENABLED)) {
            ps = c.getConnection().prepareStatement("select * from chatroomextradata where chatroomid = ?");
            ps.setInt(1, room.id);
            rs = ps.executeQuery();
            room.updateExtraData(rs);
            rs.close();
            ps.close();
        }
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CHATROOM, normalizedChatRoomName, room);
        if (SystemProperty.getBool(SystemPropertyEntities.ChatSyncSettings.CHATROOM_SYNC_ENABLED)) {
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BY_ID, Integer.toString(room.id), room);
        }
lbl62:
        // 4 sources

        var8_10 = room;
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
            if (c != null) {
                c.close();
            }
        }
        catch (SQLException e) {
            c = null;
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
                    if (c != null) {
                        c.close();
                    }
                }
                catch (SQLException e) {
                    c = null;
                }
                throw var9_17;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<ChatroomCategoryData> getLoginChatroomCategories() throws Exception {
        ArrayList<ChatroomCategoryData> chatroomCategories = new ArrayList<ChatroomCategoryData>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT id        ,name        ,itemscanbedeleted        ,initiallycollapsed        ,maxmiglevel        ,orderindex        ,refreshdisplaystring        ,status        ,refreshmethod FROM chatroomcategorylist WHERE status = ? ORDER BY OrderIndex DESC";
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, ChatroomCategoryData.StatusEnum.ACTIVE.value());
        rs = ps.executeQuery();
        while (rs.next()) {
            ChatroomCategoryData catroomCategory = new ChatroomCategoryData(rs);
            chatroomCategories.add(catroomCategory);
        }
        ArrayList<ChatroomCategoryData> arrayList = chatroomCategories;
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
        return arrayList;
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

    public ChatroomCategoryData getChatroomCategory(Integer categoryId) throws Exception {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block21: {
            if (categoryId == null) {
                throw new Exception("Id is required.");
            }
            conn = null;
            ps = null;
            rs = null;
            String sql = "SELECT * FROM chatroomcategorylist WHERE id = ?";
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();
            if (!rs.next()) break block21;
            ChatroomCategoryData chatroomCategoryData = new ChatroomCategoryData(rs);
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
            return chatroomCategoryData;
        }
        try {
            try {
                log.error((Object)("Error in retrieving chatroom category details for [" + categoryId + "]"));
                throw new Exception("Unable to retrieve chatroom category.");
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
    public Map<Integer, List<String>> getChatroomNamesPerCategory(boolean activeChatroomsAndCategoriesOnly) throws Exception {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        HashMap<Integer, List<String>> chatroomNamesPerCategory = new HashMap<Integer, List<String>>();
        String sql = "SELECT c2crl.chatroomcategorylistid, c.name FROM chatroom c     ,chatroomcategorylist crl \t   ,chatroomtochatroomcategorylist c2crl WHERE c.id = c2crl.chatroomid AND crl.id = c2crl.chatroomcategorylistid ";
        if (activeChatroomsAndCategoriesOnly) {
            sql = sql + "AND crl.status = ? AND c.status = ? ";
        }
        sql = sql + "ORDER BY c2crl.orderIndex DESC";
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement(sql);
        if (activeChatroomsAndCategoriesOnly) {
            ps.setInt(1, ChatroomCategoryData.StatusEnum.ACTIVE.value());
            ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
        }
        rs = ps.executeQuery();
        while (rs.next()) {
            int categoryID = rs.getInt("chatroomcategorylistid");
            if (!chatroomNamesPerCategory.containsKey(categoryID)) {
                chatroomNamesPerCategory.put(categoryID, new ArrayList());
            }
            ((List)chatroomNamesPerCategory.get(categoryID)).add(rs.getString("name"));
        }
        HashMap<Integer, List<String>> hashMap = chatroomNamesPerCategory;
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
        return hashMap;
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

    public String[] getChatroomNamesInCategory(int categoryId) throws Exception {
        return this.getChatroomNamesInCategory(categoryId, true);
    }

    /*
     * Loose catch block
     */
    public String[] getChatroomNamesInCategory(int categoryId, boolean activeChatroomsAndCategoriesOnly) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT c.name FROM chatroom c      ,chatroomcategorylist cl      ,chatroomtochatroomcategorylist c2cl WHERE c.id = c2cl.chatroomid AND cl.id = c2cl.chatroomcategorylistid AND cl.id = ? ";
        if (activeChatroomsAndCategoriesOnly) {
            sql = sql + "AND cl.status = ? AND c.status = ?";
        }
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, categoryId);
        if (activeChatroomsAndCategoriesOnly) {
            ps.setInt(2, ChatroomCategoryData.StatusEnum.ACTIVE.value());
            ps.setInt(3, ChatRoomData.StatusEnum.ACTIVE.value());
        }
        rs = ps.executeQuery();
        ArrayList<String> chatroomNames = new ArrayList<String>();
        while (rs.next()) {
            chatroomNames.add(rs.getString("name"));
        }
        String[] stringArray = chatroomNames.toArray(new String[chatroomNames.size()]);
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
        return stringArray;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
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
    public ChatRoomData getChatRoom(String name) throws EJBException {
        ChatRoomData room;
        String normalizedChatRoomName;
        ResultSet rs;
        Statement ps;
        Connection conn;
        block34: {
            conn = null;
            ps = null;
            rs = null;
            normalizedChatRoomName = ChatRoomUtils.normalizeChatRoomName(name);
            conn = this.dataSourceSlave.getConnection();
            room = this.getSimpleChatRoomData(normalizedChatRoomName, conn);
            if (room != null) break block34;
            ChatRoomData chatRoomData = null;
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
            return chatRoomData;
        }
        if (room.themeID != null) {
            room.theme = (Map)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM_THEME, String.valueOf(room.themeID));
            if (room.theme == null) {
                ps = conn.prepareStatement("select attributekey,attributevalue from chatroomtheme, chatroomthemeattribute where chatroomtheme.id=? and chatroomtheme.status=? and chatroomtheme.id=chatroomthemeattribute.chatroomthemeid");
                ps.setInt(1, room.themeID);
                ps.setInt(2, 1);
                rs = ps.executeQuery();
                room.theme = new HashMap<String, String>();
                while (rs.next()) {
                    room.theme.put(rs.getString("attributekey"), rs.getString("attributevalue"));
                }
                rs.close();
                ps.close();
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CHATROOM_THEME, String.valueOf(room.themeID), room.theme);
            }
        }
        if (room.userOwned.booleanValue()) {
            room.moderators = (HashSet)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CHATROOM_MODERATORS, normalizedChatRoomName);
            if (room.moderators == null) {
                ps = conn.prepareStatement("select username from chatroommoderator where chatroomid=?");
                ps.setInt(1, room.id);
                rs = ps.executeQuery();
                room.moderators = new HashSet<String>();
                while (rs.next()) {
                    room.moderators.add(rs.getString("username"));
                }
                rs.close();
                ps.close();
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CHATROOM_MODERATORS, normalizedChatRoomName, room.moderators);
            }
            room.bannedUsers = (HashSet)MemCachedClientWrapper.getPaged(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BANNED_USERS, normalizedChatRoomName);
            if (room.bannedUsers == null) {
                ps = conn.prepareStatement("select username from chatroombanneduser where chatroomid=?");
                ps.setInt(1, room.id);
                rs = ps.executeQuery();
                room.bannedUsers = new HashSet<String>();
                while (rs.next()) {
                    room.bannedUsers.add(rs.getString("username"));
                }
                Integer pageSize = SystemProperty.getInt(SystemPropertyEntities.Chatroom.BANNED_USERS_PAGE_SIZE);
                MemCachedClientWrapper.setPaged(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BANNED_USERS, normalizedChatRoomName, room.bannedUsers, pageSize);
            }
        }
        ChatRoomData chatRoomData = room;
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
        return chatRoomData;
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
    public void updateRoomExtraData(ChatRoomData newData) throws EJBException {
        block27: {
            ResultSet rs;
            Statement ps;
            Connection connMaster;
            block24: {
                if (!SystemProperty.getBool(SystemPropertyEntities.Chatroom.MULTI_ID_CONTROL_ENABLED)) {
                    return;
                }
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                String normalizedChatRoomName = ChatRoomUtils.normalizeChatRoomName(newData.name);
                ChatRoomData oldData = this.getSimpleChatRoomData(normalizedChatRoomName, connMaster);
                Map<Integer, String> m = newData.convertExtraDataDifferenceToIntegerAndStringMap(oldData);
                if (m.isEmpty()) break block24;
                ps = connMaster.prepareStatement("insert into chatroomextradata (chatroomid, type, value) values (?,?,?) on duplicate key update value=?");
                for (Map.Entry<Integer, String> entry : m.entrySet()) {
                    ps.setInt(1, newData.id);
                    ps.setInt(2, entry.getKey());
                    ps.setString(3, entry.getValue());
                    ps.setString(4, entry.getValue());
                    ps.addBatch();
                }
                int[] batchResults = ps.executeBatch();
                if (batchResults == null || batchResults.length != m.size()) {
                    throw new SQLException("Unable to update chatroom extra data");
                }
                for (int batchResult : batchResults) {
                    if (batchResult > 0) continue;
                    log.error((Object)String.format("batch result '%d' is not 1 when adding/updating chatroom extra data", batchResult));
                    throw new SQLException("Unable to update chatroom extra data");
                }
                ps.close();
                ChatRoomUtils.invalidateChatRoomCache(newData.name);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(newData.name);
                if (chatRoomPrx == null) break block24;
                chatRoomPrx.updateExtraData(newData.toIceObject());
            }
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block27;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block27;
            {
                catch (SQLException e) {
                    log.error((Object)"SQLException in updateChatRoomExtraData()", (Throwable)e);
                    throw new EJBException("Unable to update room extra data");
                }
                catch (LocalException e) {
                    log.error((Object)"Ice.LocalException in updateChatRoomExtraData()", (Throwable)e);
                    throw new EJBException("Unable to update room extra data");
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

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void updateRoomDetails(String username, String chatRoomName, String language, String description) throws EJBException {
        connMaster = null;
        ps = null;
        rs = null;
        try {
            block36: {
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("select id, language, description from chatroom where name=? and creator=? and userowned=1");
                ps.setString(1, chatRoomName);
                ps.setString(2, username);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EJBException("You cannot modify a room unless you own the room");
                }
                descriptionChange = description.equals(rs.getString("description")) == false;
                v0 = languageChange = StringUtils.hasLength((String)language) != false && language.equals(rs.getString("language")) == false;
                if (descriptionChange || languageChange) break block36;
                var15_12 = null;
                ** GOTO lbl85
            }
            rs.close();
            ps.close();
            if (StringUtils.hasLength((String)description) && description.length() > 128) {
                description = description.substring(0, 128);
            }
            sql = "update chatroom set description=? ";
            if (languageChange) {
                sql = sql + ", language=? ";
            }
            sql = sql + "where name=?";
            ps = connMaster.prepareStatement(sql);
            ps.setString(1, description);
            if (languageChange) {
                ps.setString(2, language);
            }
            ps.setString(languageChange != false ? 3 : 2, chatRoomName);
            rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 1) {
                ChatRoomUtils.invalidateChatRoomCache(chatRoomName);
                chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomName);
                if (chatRoomPrx != null) {
                    chatRoomPrx.setDescription(description);
                }
            }
            subject = "Chat room " + chatRoomName + ": ";
            if (descriptionChange) {
                subject = subject + "Description ";
            }
            if (languageChange) {
                if (descriptionChange) {
                    subject = subject + " and ";
                }
                subject = subject + "Language ";
            }
            subject = subject + "Changed";
            body = "Your chat room " + chatRoomName + " has been modified.\n\n";
            if (descriptionChange) {
                body = body + "New description: " + description + "\n\n";
            }
            if (languageChange) {
                body = body + "New language: " + language + "\n\n";
            }
            body = body + "If this was done in error or you want to change this please go to your room and select Room Settings from the menu.";
            this.sendSystemEmail(username, subject, body);
            ** GOTO lbl105
        }
        catch (SQLException e) {
            MessageBean.log.error((Object)"SQLException in updateRoomDetails()", (Throwable)e);
            throw new EJBException("Unable to update room");
        }
        catch (LocalException e) {
            MessageBean.log.error((Object)"Ice.LocalException in updateRoomDetails()", (Throwable)e);
            throw new EJBException("Unable to update room.");
        }
        {
            block40: {
                block39: {
                    block38: {
                        block37: {
                            catch (Throwable var14_22) {
                                var15_14 = null;
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
                                    if (connMaster == null) throw var14_22;
                                    connMaster.close();
                                    throw var14_22;
                                }
                                catch (SQLException e) {
                                    connMaster = null;
                                }
                                throw var14_22;
                            }
lbl85:
                            // 1 sources

                            ** try [egrp 2[TRYBLOCK] [7 : 678->693)] { 
lbl86:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block37;
lbl89:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [8 : 698->713)] { 
lbl93:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block38;
lbl96:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return;
                    }
                    if (connMaster == null) return;
                    connMaster.close();
                    return;
lbl105:
                    // 1 sources

                    var15_13 = null;
                    ** try [egrp 2[TRYBLOCK] [7 : 678->693)] { 
lbl107:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block39;
lbl110:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [8 : 698->713)] { 
lbl114:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block40;
lbl117:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (connMaster == null) return;
            connMaster.close();
            return;
        }
    }

    /*
     * Loose catch block
     */
    public void updateRoomKickingRule(String username, String chatRoomName, boolean allowKicking) throws EJBException {
        block25: {
            Connection connMaster = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select id from chatroom where name=? and creator=? and userowned=1");
            ps.setString(1, chatRoomName);
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("You cannot modify a room unless you own the room");
            }
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("update chatroom set allowkicking=? where name=?");
            ps.setInt(1, allowKicking ? 1 : 0);
            ps.setString(2, chatRoomName);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 1) {
                ChatRoomUtils.invalidateChatRoomCache(chatRoomName);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomName);
                if (chatRoomPrx != null) {
                    chatRoomPrx.setAllowKicking(allowKicking);
                }
            }
            String subject = "Chat room " + chatRoomName + ": Kicking has been " + (allowKicking ? "enabled" : "disabled");
            String body = "Your chat room " + chatRoomName + " has been modified.\n\n";
            body = body + "Kicking has been " + (allowKicking ? "enabled" : "disabled") + "\n\n";
            body = allowKicking ? body + "Users will be allowed to initiate a kick vote.\n\n" : body + "Users will not be allowed to initiate a kick vote.\n\n";
            body = body + "If this was done in error or you want to change this please go to your room and select Room Settings from the menu.";
            this.sendSystemEmail(username, subject, body);
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block25;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block25;
            {
                catch (SQLException e) {
                    log.error((Object)"SQLException in updateRoomDetails()", (Throwable)e);
                    throw new EJBException("Unable to update room");
                }
                catch (LocalException e) {
                    log.error((Object)"Ice.LocalException in updateRoomDetails()", (Throwable)e);
                    throw new EJBException("Unable to update room");
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

    /*
     * Loose catch block
     */
    public void updateRoomAdultOnlyFlag(String username, String chatRoomName, boolean adultOnly) throws EJBException {
        block28: {
            Connection connMaster = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select username, chatroom.id from user, chatroom where username=? and chatroomadmin=1 and chatroom.name=?");
            ps.setString(1, username);
            ps.setString(2, chatRoomName);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("You cannot modify room's adult only flag unless you are an admin");
            }
            int chatRoomID = rs.getInt("id");
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("update chatroom set adultonly=? where name=?");
            ps.setInt(1, adultOnly ? 1 : 0);
            ps.setString(2, chatRoomName);
            int rowsUpdated = ps.executeUpdate();
            connMaster.close();
            connMaster = null;
            if (rowsUpdated == 1) {
                ChatRoomUtils.invalidateChatRoomCache(chatRoomName);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomName);
                if (chatRoomPrx != null) {
                    chatRoomPrx.setAdultOnly(adultOnly);
                }
                if (!SystemProperty.getBool("DisableElasticSearch", false)) {
                    try {
                        ChatRoomsIndex.updateChatRoomAdultOnlyFlag(chatRoomID, chatRoomName, adultOnly);
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to update chat room '" + chatRoomName + "' adult only flag in ElasticSearch"), (Throwable)e);
                    }
                }
            }
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block28;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block28;
            {
                catch (SQLException e) {
                    throw new EJBException("Unable to update room");
                }
                catch (LocalException e) {
                    throw new EJBException("Unable to update room");
                }
            }
            catch (Throwable throwable) {
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

    /*
     * Loose catch block
     */
    public void updateRoomKeywords(String username, String chatRoomName, String keywords, int allowUserKeywords) throws EJBException {
        block46: {
            int chatRoomID;
            Connection connMaster = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean modifiedByModerator = false;
            boolean modifiedByUser = false;
            String ownerUsername = username;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select id from chatroom where name=? and creator=? and userowned=1");
            ps.setString(1, chatRoomName);
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                ps = connMaster.prepareStatement("SELECT chatroom.id, chatroom.creator, chatroom.allowuserkeywords, chatroommoderator.username moderatorusername FROM chatroom LEFT OUTER JOIN chatroommoderator ON chatroom.id=chatroommoderator.chatroomid AND chatroommoderator.username=? WHERE chatroom.name=? AND chatroom.status=1 AND chatroom.userowned=1");
                ps.setString(1, username);
                ps.setString(2, chatRoomName);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EJBException("Unable to modify chat room keywords");
                }
                if (rs.getInt("allowuserkeywords") == 0 && !StringUtils.hasLength((String)rs.getString("moderatorusername"))) {
                    throw new EJBException("You cannot modify the room's keywords unless you own the room or are a moderator");
                }
                chatRoomID = rs.getInt("id");
                ownerUsername = rs.getString("creator");
                if (StringUtils.hasLength((String)rs.getString("moderatorusername"))) {
                    modifiedByModerator = true;
                } else {
                    modifiedByUser = true;
                }
            } else {
                chatRoomID = rs.getInt("id");
            }
            rs.close();
            ps.close();
            HashSet<String> oldKeywords = new HashSet<String>();
            HashSet<String> newKeywords = new HashSet<String>();
            HashSet<String> addedKeywords = new HashSet<String>();
            HashSet<String> removedKeywords = new HashSet<String>();
            ps = connMaster.prepareStatement("select keyword.keyword from keyword, chatroomkeyword, chatroom where chatroom.name=? and chatroom.id=chatroomkeyword.chatroomid and chatroomkeyword.keywordid=keyword.id and chatroom.status=1");
            ps.setString(1, chatRoomName);
            rs = ps.executeQuery();
            while (rs.next()) {
                String keyword = rs.getString("keyword").trim().toLowerCase();
                oldKeywords.add(keyword);
                removedKeywords.add(keyword);
            }
            rs.close();
            ps.close();
            if (StringUtils.hasLength((String)keywords)) {
                String[] keywordsArray = keywords.split(",");
                for (int i = 0; i < keywordsArray.length; ++i) {
                    String keyword = keywordsArray[i].trim().toLowerCase();
                    if (keyword.length() > 64) {
                        keyword = keyword.substring(0, 64);
                    }
                    if (!StringUtils.hasLength((String)keyword)) continue;
                    newKeywords.add(keyword);
                    addedKeywords.add(keyword);
                }
            }
            addedKeywords.removeAll(oldKeywords);
            removedKeywords.removeAll(newKeywords);
            if (allowUserKeywords != -1 && !modifiedByUser) {
                ps = connMaster.prepareStatement("update chatroom set allowuserkeywords=? where name=?");
                ps.setBoolean(1, allowUserKeywords == 1);
                ps.setString(2, chatRoomName);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated == 1) {
                    ChatRoomUtils.invalidateChatRoomCache(chatRoomName);
                }
                ps.close();
            }
            if (!modifiedByUser) {
                ps = connMaster.prepareStatement("delete from chatroomkeyword where chatroomid=?");
                ps.setInt(1, chatRoomID);
                ps.executeUpdate();
            }
            this.addChatRoomKeywords(chatRoomID, keywords, connMaster);
            connMaster.close();
            connMaster = null;
            if (!SystemProperty.getBool("DisableElasticSearch", false)) {
                try {
                    ChatRoomsIndex.updateChatRoomTags(chatRoomID, chatRoomName, newKeywords);
                }
                catch (Exception e) {
                    log.error((Object)("Unable to update chat room '" + chatRoomName + "' tags in ElasticSearch"), (Throwable)e);
                }
            }
            String subject = "Chat room " + chatRoomName + ": Keywords have been modified";
            String body = "Your chat room " + chatRoomName + " has been modified.\n\n";
            if (modifiedByModerator) {
                body = body + "This change was made by the moderator " + username + "\n\n";
            }
            if (modifiedByUser) {
                body = body + "This change was made by the user " + username + "\n\n";
            }
            if (addedKeywords.size() > 0) {
                body = body + "The following keywords were added:\n";
                for (String keyword : addedKeywords) {
                    body = body + keyword + "\n";
                }
                body = body + "\n";
            }
            if (!modifiedByUser && removedKeywords.size() > 0) {
                body = body + "The following keywords were removed:\n";
                for (String keyword : removedKeywords) {
                    body = body + keyword + "\n";
                }
                body = body + "\n";
            }
            if (allowUserKeywords != -1) {
                body = body + "Users are ";
                if (allowUserKeywords != 1) {
                    body = body + " not ";
                }
                body = body + "allowed to add keywords.\n\n";
            }
            body = body + "If this was done in error or you want to change this please go to your room and select Room Settings from the menu.";
            this.sendSystemEmail(ownerUsername, subject, body);
            Object var21_26 = null;
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
                break block46;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block46;
            {
                catch (SQLException e) {
                    log.error((Object)"SQLException in updateRoomDetails()", (Throwable)e);
                    throw new EJBException("Unable to update room");
                }
                catch (LocalException e) {
                    log.error((Object)"Ice.LocalException in updateRoomDetails()", (Throwable)e);
                    throw new EJBException("Unable to update room");
                }
            }
            catch (Throwable throwable) {
                Object var21_27 = null;
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
    }

    public List<ChatRoomData> getChatRooms(int countryID, String search) throws EJBException {
        return this.getChatRooms(countryID, search, null, true, false);
    }

    /*
     * Loose catch block
     */
    public List<ChatRoomData> getChatRooms(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws EJBException {
        List<ChatRoomData> cachedChatRooms;
        if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.ENABLED_REFINED_SQL_FOR_SEARCHING_CHATROOM)) {
            return this.getChatRoomsV2(countryID, search, language, includeAdultOnly, searchKeywords);
        }
        Connection connSlave = null;
        Statement ps = null;
        ResultSet rs = null;
        boolean performingSearch = StringUtils.hasLength((String)search);
        LinkedList<ChatRoomData> chatRooms = new LinkedList();
        if (performingSearch && (cachedChatRooms = ChatRoomSearch.getChatRoomSearch(chatRoomSearchMemcache, countryID, search, language, includeAdultOnly, searchKeywords)) != null) {
            return cachedChatRooms;
        }
        int maxChatRoomsReturned = SystemProperty.getInt("MaxChatRoomsReturned");
        if (!SystemProperty.getBool("DisableElasticSearch", false) && !SystemProperty.getBool("DisableElasticSearchQueries", false)) {
            chatRooms = ChatRoomsIndex.searchChatRooms(countryID, search, language, includeAdultOnly, searchKeywords, maxChatRoomsReturned);
        } else {
            LinkedList<ChatRoomData> adultRooms = new LinkedList<ChatRoomData>();
            connSlave = this.dataSourceSlave.getConnection();
            if (performingSearch) {
                String searchLike = search.trim().replaceAll("[\\*%]", "");
                int minChatRoomSearchLength = SystemProperty.getInt("MinChatRoomSearchLength", 0);
                if (searchLike.length() < minChatRoomSearchLength) {
                    throw new EJBException("Search string must have at least " + minChatRoomSearchLength + " characters");
                }
                searchLike = searchLike.replaceAll("_", "\\\\_") + "%";
                String sql = "(select chatroom.*, null as keywords, 1 as sortorder from chatroom where name = ? and status = 1) union (select chatroom.*, null as keywords, 2 as sortorder from chatroom where name like ? and status = 1 ";
                if (!includeAdultOnly) {
                    sql = sql + "and chatroom.adultonly=0 ";
                }
                sql = sql + "order by datelastaccessed desc limit ?) ";
                if (searchKeywords) {
                    sql = sql + "union (select chatroom.*, group_concat(keyword.keyword) as keywords, 2 as sortorder from chatroom left join chatroomkeyword on chatroom.id=chatroomkeyword.chatroomid left join keyword on chatroomkeyword.keywordid=keyword.id where keyword.keyword = ? and chatroom.status = 1 ";
                    if (!includeAdultOnly) {
                        sql = sql + "and chatroom.adultonly=0 ";
                    }
                    sql = sql + "group by id, name, description, type, creator, chatroomcategoryid, primarycountryid, secondarycountryid, locationid, groupid, adultonly, maximumsize, userowned, allowkicking, allowuserkeywords, allowBots, language, datecreated, datelastaccessed, status order by datelastaccessed desc limit ?) ";
                }
                sql = sql + "order by sortorder, datelastaccessed desc limit ?";
                ps = connSlave.prepareStatement(sql);
                ps.setString(1, search);
                ps.setString(2, searchLike);
                ps.setInt(3, maxChatRoomsReturned);
                if (searchKeywords) {
                    ps.setString(4, search);
                    ps.setInt(5, maxChatRoomsReturned);
                    ps.setInt(6, maxChatRoomsReturned);
                } else {
                    ps.setInt(4, maxChatRoomsReturned);
                }
                rs = ps.executeQuery();
                boolean languageSearch = performingSearch && StringUtils.hasLength((String)language);
                LinkedList<ChatRoomData> languageMatchRooms = null;
                if (languageSearch) {
                    languageMatchRooms = new LinkedList<ChatRoomData>();
                }
                ChatRoomData exactMatch = null;
                while (rs.next()) {
                    ChatRoomData room = new ChatRoomData(rs);
                    if (room.name.equalsIgnoreCase(search)) {
                        exactMatch = room;
                        continue;
                    }
                    if (room.adultOnly == null || room.adultOnly.booleanValue()) {
                        adultRooms.add(room);
                        continue;
                    }
                    if (languageSearch && room.language != null && room.language.equals(language)) {
                        languageMatchRooms.add(room);
                        continue;
                    }
                    chatRooms.add(room);
                }
                if (languageSearch) {
                    chatRooms.addAll(0, languageMatchRooms);
                }
                if (exactMatch != null) {
                    chatRooms.add(0, exactMatch);
                }
            } else {
                ps = connSlave.prepareStatement("select chatroom.*, null as keywords from chatroom where status = ? order by datelastaccessed desc limit ?");
                ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
                ps.setInt(2, maxChatRoomsReturned);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ChatRoomData room = new ChatRoomData(rs);
                    if (room.adultOnly == null || room.adultOnly.booleanValue()) {
                        adultRooms.add(room);
                        continue;
                    }
                    chatRooms.add(room);
                }
            }
            if (adultRooms.size() > 0) {
                int chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomInitialPageSize");
                int chatRoomPageSize = SystemProperty.getInt("ChatRoomPageSize");
                int chatRoomCleanPages = SystemProperty.getInt("ChatRoomCleanPages");
                int cleanRoomsToShow = chatRoomInitialPageSize + chatRoomPageSize * chatRoomCleanPages;
                double adultRoomsPerPage = (double)adultRooms.size() / ((double)(chatRooms.size() - cleanRoomsToShow) / (double)chatRoomPageSize);
                double roundingRatio = adultRoomsPerPage - (double)((int)adultRoomsPerPage);
                for (int i = cleanRoomsToShow; i < chatRooms.size() && adultRooms.size() > 0; i += chatRoomPageSize) {
                    double noOfRooms = this.secureRandom.nextDouble() > roundingRatio ? Math.floor(adultRoomsPerPage) : Math.ceil(adultRoomsPerPage);
                    int j = 0;
                    while ((double)j < noOfRooms && adultRooms.size() > 0) {
                        chatRooms.add(i, (ChatRoomData)adultRooms.remove(0));
                        ++j;
                    }
                }
                chatRooms.addAll(adultRooms);
            }
        }
        if (performingSearch) {
            ChatRoomSearch.setChatRoomSearchList(chatRoomSearchMemcache, countryID, search, language, includeAdultOnly, searchKeywords, chatRooms);
        }
        LinkedList<ChatRoomData> linkedList = chatRooms;
        Object var26_29 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
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
        return linkedList;
        catch (Exception e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var26_30 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
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
    public List<ChatRoomData> getChatRoomsV2(int countryID, String search, String language, boolean includeAdultOnly, boolean searchKeywords) throws EJBException {
        block69: {
            block58: {
                block68: {
                    connSlave = null;
                    ps = null;
                    stmt = null;
                    rs = null;
                    performingSearch = StringUtils.hasLength((String)search);
                    chatRooms = new LinkedList<E>();
                    if (performingSearch && (cachedChatRooms = ChatRoomSearch.getChatRoomSearch(MessageBean.chatRoomSearchMemcache, countryID, search, language, includeAdultOnly, searchKeywords)) != null) {
                        return cachedChatRooms;
                    }
                    maxChatRoomsReturned = SystemProperty.getInt("MaxChatRoomsReturned");
                    if (SystemProperty.getBool("DisableElasticSearch", false) || SystemProperty.getBool("DisableElasticSearchQueries", false)) break block68;
                    chatRooms = ChatRoomsIndex.searchChatRooms(countryID, search, language, includeAdultOnly, searchKeywords, maxChatRoomsReturned);
                    ** GOTO lbl157
                }
                adultRooms = new LinkedList<ChatRoomData>();
                connSlave = this.dataSourceSlave.getConnection();
                if (!performingSearch) ** GOTO lbl126
                searchLike = search.trim().replaceAll("[\\*%]", "");
                minChatRoomSearchLength = SystemProperty.getInt("MinChatRoomSearchLength", 0);
                if (searchLike.length() < minChatRoomSearchLength) {
                    throw new EJBException("Search string must have at least " + minChatRoomSearchLength + " characters");
                }
                searchLike = searchLike.replaceAll("_", "\\\\_") + "%";
                retrieveChatroomID = "(select chatroom.id, chatroom.adultonly, datelastaccessed from chatroom where name like ? and status = 1 order by datelastaccessed desc limit ?) ";
                if (searchKeywords) {
                    retrieveChatroomID = retrieveChatroomID + " union all ";
                    retrieveChatroomID = retrieveChatroomID + "(select chatroom.id, chatroom.adultonly, datelastaccessed from chatroom join chatroomkeyword on chatroom.id=chatroomkeyword.chatroomid join keyword on chatroomkeyword.keywordid=keyword.id where keyword.keyword = ? and chatroom.status = 1 order by datelastaccessed desc limit ?) order by datelastaccessed desc";
                }
                ps = connSlave.prepareStatement(retrieveChatroomID);
                ps.setString(1, searchLike);
                ps.setInt(2, maxChatRoomsReturned);
                if (searchKeywords) {
                    ps.setString(3, search);
                    ps.setInt(4, maxChatRoomsReturned);
                }
                rs = ps.executeQuery();
                chatroomIDSet = new LinkedHashSet<Integer>();
                chatroomIdCount = 0;
                while (rs.next() && chatroomIdCount < maxChatRoomsReturned) {
                    if (false == includeAdultOnly && rs.getInt(2) == 1) continue;
                    chatroomIDSet.add(rs.getInt(1));
                    ++chatroomIdCount;
                }
                if (!chatroomIDSet.isEmpty()) break block58;
                var19_25 = Collections.emptyList();
                var27_29 = null;
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
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (SQLException e) {
                    stmt = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e) {
                    connSlave = null;
                }
                return var19_25;
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
            retrieveChatroomInfo = "select chatroom.*, group_concat(keyword.keyword) as keywords from chatroom left join chatroomkeyword on chatroom.id=chatroomkeyword.chatroomid left join keyword on chatroomkeyword.keywordid=keyword.id where chatroom.id in (%s) group by chatroom.id order by field(chatroom.id, %s);";
            sb = new StringBuilder();
            for (Integer id : chatroomIDSet) {
                sb.append(id + ",");
            }
            chatroomIDList = sb.substring(0, sb.length() - 1);
            retrieveChatroomInfo = String.format(retrieveChatroomInfo, new Object[]{chatroomIDList, chatroomIDList});
            stmt = connSlave.createStatement();
            rs = stmt.executeQuery(retrieveChatroomInfo);
            languageSearch = performingSearch != false && StringUtils.hasLength((String)language) != false;
            languageMatchRooms = null;
            if (languageSearch) {
                languageMatchRooms = new LinkedList<ChatRoomData>();
            }
            exactMatch = null;
            while (rs.next()) {
                room = new ChatRoomData(rs);
                if (room.name.equalsIgnoreCase(search)) {
                    exactMatch = room;
                    continue;
                }
                if (room.adultOnly == null || room.adultOnly.booleanValue()) {
                    adultRooms.add(room);
                    continue;
                }
                if (languageSearch && room.language != null && room.language.equals(language)) {
                    languageMatchRooms.add(room);
                    continue;
                }
                chatRooms.add(room);
            }
            if (languageSearch) {
                chatRooms.addAll(0, languageMatchRooms);
            }
            if (exactMatch != null) {
                chatRooms.add(0, exactMatch);
            }
            break block69;
lbl126:
            // 1 sources

            ps = connSlave.prepareStatement("select chatroom.*, null as keywords from chatroom where status = ? order by datelastaccessed desc limit ?");
            ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
            ps.setInt(2, maxChatRoomsReturned);
            rs = ps.executeQuery();
            while (rs.next()) {
                room = new ChatRoomData(rs);
                if (room.adultOnly.booleanValue()) {
                    adultRooms.add(room);
                    continue;
                }
                chatRooms.add(room);
            }
        }
        if (adultRooms.size() > 0) {
            chatRoomInitialPageSize = SystemProperty.getInt("ChatRoomInitialPageSize");
            chatRoomPageSize = SystemProperty.getInt("ChatRoomPageSize");
            chatRoomCleanPages = SystemProperty.getInt("ChatRoomCleanPages");
            cleanRoomsToShow = chatRoomInitialPageSize + chatRoomPageSize * chatRoomCleanPages;
            adultRoomsPerPage = (double)adultRooms.size() / ((double)(chatRooms.size() - cleanRoomsToShow) / (double)chatRoomPageSize);
            roundingRatio = adultRoomsPerPage - (double)((int)adultRoomsPerPage);
            for (i = cleanRoomsToShow; i < chatRooms.size() && adultRooms.size() > 0; i += chatRoomPageSize) {
                noOfRooms = this.secureRandom.nextDouble() > roundingRatio ? Math.floor(adultRoomsPerPage) : Math.ceil(adultRoomsPerPage);
                j = 0;
                while ((double)j < noOfRooms && adultRooms.size() > 0) {
                    chatRooms.add(i, (ChatRoomData)adultRooms.remove(0));
                    ++j;
                }
            }
            chatRooms.addAll(adultRooms);
        }
lbl157:
        // 4 sources

        if (performingSearch) {
            ChatRoomSearch.setChatRoomSearchList(MessageBean.chatRoomSearchMemcache, countryID, search, language, includeAdultOnly, searchKeywords, chatRooms);
        }
        var13_15 = chatRooms;
        var27_30 = null;
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
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            stmt = null;
        }
        try {
            if (connSlave != null) {
                connSlave.close();
            }
        }
        catch (SQLException e) {
            connSlave = null;
        }
        return var13_15;
        catch (Exception e) {
            try {
                MessageBean.log.error((Object)("Failed to getChatroom, due to:" + e), (Throwable)e);
                throw new EJBException(e.getMessage());
            }
            catch (Throwable var26_45) {
                var27_31 = null;
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
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (SQLException e) {
                    stmt = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e) {
                    connSlave = null;
                }
                throw var26_45;
            }
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public List<ChatRoomData> getFavouriteChatRooms(String username) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select c.* from chatroom c, chatroombookmark b where c.name = b.chatroomname and b.username = ? order by b.datecreated desc limit ?");
        ps.setString(1, username);
        ps.setInt(2, SystemProperty.getInt("MaxChatRoomBookmarks"));
        rs = ps.executeQuery();
        LinkedList<ChatRoomData> chatRooms = new LinkedList<ChatRoomData>();
        while (rs.next()) {
            chatRooms.add(new ChatRoomData(rs));
        }
        LinkedList<ChatRoomData> linkedList = chatRooms;
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
        {
            return linkedList;
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            catch (NoSuchFieldException e) {
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var8_10 = null;
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
    public List<ChatRoomData> getRecentChatRooms(String username) throws EJBException {
        List<String> recentChatRooms;
        ResultSet rs;
        Statement ps;
        Connection conn;
        block31: {
            String[] rooms;
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            recentChatRooms = RecentChatRoomList.getRecentChatRoomList(recentChatRoomMemcache, username);
            if (recentChatRooms == null) {
                recentChatRooms = RecentChatRoomList.newRecentChatRoomList();
                ps = conn.prepareStatement("select chatroomnames from recentchatrooms where username = ?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                while (rs.next()) {
                    for (String room : rooms = StringUtil.asArray(rs.getString("chatroomnames"))) {
                        recentChatRooms.add(room);
                    }
                }
                RecentChatRoomList.setRecentChatRoomList(recentChatRoomMemcache, username, recentChatRooms);
            }
            if (!recentChatRooms.isEmpty()) break block31;
            rooms = Collections.EMPTY_LIST;
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
            return rooms;
        }
        ps = conn.prepareStatement("select * from chatroom where status = ? and name in (" + RecentChatRoomList.asString(recentChatRooms) + ")");
        ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
        rs = ps.executeQuery();
        HashMap<String, ChatRoomData> chatRoomMap = new HashMap<String, ChatRoomData>();
        while (rs.next()) {
            ChatRoomData chatRoomData = new ChatRoomData(rs);
            chatRoomMap.put(chatRoomData.name.toLowerCase(), chatRoomData);
        }
        LinkedList<ChatRoomData> chatRoomList = new LinkedList<ChatRoomData>();
        for (String recentChatRoom : recentChatRooms) {
            ChatRoomData chatRoomData = (ChatRoomData)chatRoomMap.get(recentChatRoom.toLowerCase());
            if (chatRoomData == null) continue;
            chatRoomList.add(chatRoomData);
        }
        LinkedList<ChatRoomData> linkedList = chatRoomList;
        Object var12_16 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
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
     * Enabled aggressive exception aggregation
     */
    public int getRecentlyAccessedChatRoomCount() throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        Integer count = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.RECENT_CHATROOM_COUNT, "");
        if (count == null) {
            Calendar from = Calendar.getInstance();
            from.add(13, -SystemProperty.getInt("RecentlyAccessedChatRoomInterval"));
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select count(*) from chatroom where datelastaccessed > ?");
            ps.setTimestamp(1, new Timestamp(from.getTimeInMillis()));
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Unable to get recently accessed chat room count from database");
            }
            count = rs.getInt(1);
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.RECENT_CHATROOM_COUNT, "", count);
        }
        int n = count;
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
        {
            return n;
            catch (SQLException e) {
                throw new EJBException(e.getMessage());
            }
            catch (NoSuchFieldException e) {
                throw new EJBException(e.getMessage());
            }
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

    /*
     * Loose catch block
     */
    public int getActiveGroupsCount() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer count = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.GROUP_COUNT, "");
        if (count == null) {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select count(*) from groups where status=?");
            ps.setInt(1, GroupData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Unable to get groups count from database");
            }
            count = rs.getInt(1);
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.GROUP_COUNT, "", count);
        }
        int n = count;
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
        return n;
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

    private void onChatRoomCreated(String creator, String chatRoomName) {
    }

    /*
     * Loose catch block
     */
    public void createChatRoom(ChatRoomData chatRoom, String keywords) throws EJBException {
        block40: {
            Connection connMaster = null;
            Statement ps = null;
            ResultSet rs = null;
            chatRoom.name = chatRoom.name.trim();
            ChatRoomUtils.validateChatRoomNameForCreation(chatRoom.name);
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            ReputationLevelData levelData = userBean.getReputationLevel(chatRoom.creator);
            if (levelData.createChatRoom == null || !levelData.createChatRoom.booleanValue()) {
                throw new EJBException(SystemProperty.get(SystemPropertyEntities.Chatroom.INSUFFICIENT_MIGLEVEL_TO_CREATE_CHATROOM_MESSAGE));
            }
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select id from chatroom where name=?");
            ps.setString(1, chatRoom.name);
            rs = ps.executeQuery();
            if (rs.next()) {
                throw new EJBException("A chat room with the name " + chatRoom.name + " already exists. Please choose a different name");
            }
            rs.close();
            ps.close();
            boolean adultOnly = false;
            if (!StringUtil.isBlank(this.getAdultWordFilter())) {
                String nameToCheck = " " + chatRoom.name.toLowerCase().replaceAll("[^a-z0-9\\s]", "") + " ";
                adultOnly = nameToCheck.matches(this.adultWordFilter);
            }
            if (StringUtils.hasLength((String)chatRoom.description) && chatRoom.description.length() > 128) {
                chatRoom.description = chatRoom.description.substring(0, 128);
            }
            ps = connMaster.prepareStatement("insert into ChatRoom (name, description, type, creator, adultonly, maximumsize, userowned, allowkicking, allowuserkeywords, allowbots, language, datecreated, datelastaccessed, status) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, chatRoom.name);
            ps.setString(2, StringUtils.hasLength((String)chatRoom.description) ? chatRoom.description : null);
            ps.setInt(3, ChatRoomData.TypeEnum.CHATROOM.value());
            ps.setString(4, chatRoom.creator.toLowerCase());
            ps.setInt(5, adultOnly ? 1 : 0);
            if (levelData.chatRoomSize == null) {
                ps.setInt(6, SystemProperty.getInt("DefaultChatRoomSize"));
            } else {
                ps.setInt(6, levelData.chatRoomSize);
            }
            ps.setBoolean(7, chatRoom.userOwned == null ? false : chatRoom.userOwned);
            ps.setBoolean(8, chatRoom.allowKicking == null ? true : chatRoom.allowKicking);
            ps.setBoolean(9, chatRoom.allowUserKeywords == null ? false : chatRoom.allowUserKeywords);
            ps.setBoolean(10, chatRoom.allowBots == null ? false : chatRoom.allowBots);
            ps.setString(11, StringUtils.hasLength((String)chatRoom.language) ? chatRoom.language : null);
            Timestamp timeNow = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(12, timeNow);
            ps.setTimestamp(13, timeNow);
            ps.setInt(14, ChatRoomData.StatusEnum.ACTIVE.value());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated != 1) {
                log.warn((Object)("Unable to create chat room " + chatRoom.name + " (rowsUpdated != 1)"));
                throw new EJBException("Internal Server Error (Unable to create chat room)");
            }
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                log.warn((Object)("Unable to create chat room " + chatRoom.name + " (unable to obtain chat room ID)"));
                throw new EJBException("Internal Server Error (Unable to obtain chat room ID)");
            }
            chatRoom.id = rs.getInt(1);
            rs.close();
            ps.close();
            if (StringUtils.hasLength((String)keywords)) {
                this.addChatRoomKeywords(chatRoom.id, keywords, connMaster);
            }
            connMaster.close();
            connMaster = null;
            try {
                this.addFavouriteChatRoom(chatRoom.creator, chatRoom.name);
            }
            catch (EJBException e) {
                // empty catch block
            }
            if (!SystemProperty.getBool("DisableElasticSearch", false)) {
                HashSet<String> keywordSet = null;
                if (StringUtils.hasLength((String)keywords)) {
                    keywordSet = new HashSet<String>();
                    keywordSet.addAll(Arrays.asList(keywords.split(",")));
                }
                try {
                    ChatRoomsIndex.indexNewChatRoom(chatRoom.id, chatRoom.name, adultOnly, keywordSet);
                }
                catch (Exception e) {
                    log.error((Object)("Unable to add chat room '" + chatRoom.name + "' to ElasticSearch"), (Throwable)e);
                }
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.NEGATIVE_CACHE_ENABLED)) {
                String normalizeChatRoomName = ChatRoomUtils.normalizeChatRoomName(chatRoom.name);
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CHATROOM_NEGATIVE_CACHE, normalizeChatRoomName);
            }
            this.onChatRoomCreated(chatRoom.creator, chatRoom.name);
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
                catch (ChatRoomValidationException cve) {
                    throw new EJBException(cve.getMessage(), (Exception)cve);
                }
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
                Object var14_19 = null;
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
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int getKeywordID(Connection connMaster, String keyword) throws Exception {
        PreparedStatement psGetKeyword = null;
        PreparedStatement psAddKeyword = null;
        ResultSet rs = null;
        int keywordID = -1;
        try {
            psGetKeyword = connMaster.prepareStatement("select id from keyword where keyword = ?");
            psAddKeyword = connMaster.prepareStatement("insert into keyword (keyword) values (?)", 1);
            keyword = keyword.trim().toLowerCase();
            if (keyword.length() > 64) {
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
                    throw new Exception("Unable to add new keyword");
                }
                keywordID = rs.getInt(1);
                rs.close();
            }
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
                // empty catch block
            }
            try {
                if (psGetKeyword != null) {
                    psGetKeyword.close();
                }
            }
            catch (SQLException e) {
                // empty catch block
            }
            try {
                if (psAddKeyword != null) {
                    psAddKeyword.close();
                }
            }
            catch (SQLException e) {
                // empty catch block
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            if (psGetKeyword != null) {
                psGetKeyword.close();
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            if (psAddKeyword != null) {
                psAddKeyword.close();
            }
        }
        catch (SQLException e) {}
        return keywordID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void addChatRoomKeywords(int chatRoomID, String keywords, Connection connMaster) {
        block22: {
            PreparedStatement psAddKeywordToChatRoom = null;
            ResultSet rs = null;
            psAddKeywordToChatRoom = connMaster.prepareStatement("insert into chatroomkeyword (chatroomid, keywordid) values (?,?)");
            psAddKeywordToChatRoom.setInt(1, chatRoomID);
            HashSet<String> keywordSet = new HashSet<String>();
            keywordSet.addAll(Arrays.asList(keywords.split(",")));
            for (String keyword : keywordSet) {
                int keywordID;
                try {
                    keywordID = this.getKeywordID(connMaster, keyword);
                }
                catch (Exception e) {
                    continue;
                }
                psAddKeywordToChatRoom.setInt(2, keywordID);
                psAddKeywordToChatRoom.executeUpdate();
            }
            Object var12_12 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                // empty catch block
            }
            try {
                if (psAddKeywordToChatRoom != null) {
                    psAddKeywordToChatRoom.close();
                }
                break block22;
            }
            catch (SQLException e2) {}
            break block22;
            {
                catch (SQLException e) {
                    log.warn((Object)("Unable to add keywords to the chat room with ID " + chatRoomID), (Throwable)e);
                    Object var12_13 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        // empty catch block
                    }
                    try {
                        if (psAddKeywordToChatRoom != null) {
                            psAddKeywordToChatRoom.close();
                        }
                    }
                    catch (SQLException e2) {
                        // empty catch block
                    }
                    return;
                }
            }
            catch (Throwable throwable) {
                Object var12_14 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    // empty catch block
                }
                try {
                    if (psAddKeywordToChatRoom != null) {
                        psAddKeywordToChatRoom.close();
                    }
                }
                catch (SQLException e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void chatRoomAccessed(int chatRoomID, String chatRoomName, Integer primaryCountryID, Integer secondaryCountryID) throws EJBException {
        block24: {
            Connection connMaster = null;
            PreparedStatement ps = null;
            if (primaryCountryID != null && primaryCountryID <= 0) {
                primaryCountryID = null;
            }
            if (secondaryCountryID != null && secondaryCountryID <= 0) {
                secondaryCountryID = null;
            }
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("update chatroom set datelastaccessed = now(), primarycountryid = ?, secondarycountryid = ? where name = ?");
            ps.setObject(1, primaryCountryID);
            ps.setObject(2, secondaryCountryID);
            ps.setString(3, chatRoomName);
            ps.executeUpdate();
            ps.close();
            ps = null;
            connMaster.close();
            connMaster = null;
            if (!SystemProperty.getBool("DisableElasticSearch", false)) {
                try {
                    ChatRoomsIndex.chatRoomAccessed(chatRoomID, chatRoomName, primaryCountryID, secondaryCountryID);
                }
                catch (Exception e) {
                    log.error((Object)("Unable to update chat room '" + chatRoomName + "' last accessed in ElasticSearch"), (Throwable)e);
                }
            }
            Object var9_9 = null;
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
                break block24;
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    log.warn((Object)("chatRoomAccessed(" + chatRoomName + ", " + primaryCountryID + ", " + secondaryCountryID + ") threw SQLException"), (Throwable)e);
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
                        if (connMaster != null) {
                            connMaster.close();
                        }
                        break block24;
                    }
                    catch (SQLException e2) {
                        connMaster = null;
                    }
                }
            }
            catch (Throwable throwable) {
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
    public void addFavouriteChatRoom(String username, String chatRoomName) throws EJBException {
        block26: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select count(*), sum(if(chatroomname = ?, 1, 0)) from chatroombookmark where username = ?");
            ps.setString(1, chatRoomName);
            ps.setString(2, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Unable to retrieve chat room bookmarks");
            }
            if (rs.getInt(2) > 0) {
                throw new EJBException("Room " + chatRoomName + " is already in your favorite list");
            }
            int maxChatRoomBookmarks = SystemProperty.getInt("MaxChatRoomBookmarks");
            if (rs.getInt(1) >= maxChatRoomBookmarks) {
                throw new EJBException("You have reached maximum limit of " + maxChatRoomBookmarks + " favorite chat rooms");
            }
            rs.close();
            ps.close();
            ps = conn.prepareStatement("insert into chatroombookmark (username, chatroomname, datecreated) values (?,?,?)");
            ps.setString(1, username);
            ps.setString(2, chatRoomName);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to add new chat room bookmark");
            }
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
                catch (NoSuchFieldException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
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
    public void removeFavouriteChatRoom(String username, String chatRoomName) throws EJBException {
        block15: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("delete from chatroombookmark where username = ? and chatroomname = ?");
            ps.setString(1, username);
            ps.setString(2, chatRoomName);
            ps.executeUpdate();
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
    public void logMessageStats(Date dateOfStats, int countryID, int numPrivate, int numGroupChatSent, int numGroupChatReceived, int numChatRoomSent, int numChatRoomReceived, int numSMS, int numMSNSent, int numMSNReceived, int numYahooSent, int numYahooReceived, int numAIMSent, int numAIMReceived, int numGTalkSent, int numGTalkReceived, int numFacebookSent, int numFacebookReceived) throws EJBException {
        block17: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update MessageStats set Private=Private+?, GroupChatSent=GroupChatSent+?, GroupChatReceived=GroupChatReceived+?, ChatRoomSent=ChatRoomSent+?, ChatRoomReceived=ChatRoomReceived+?, SMS=SMS+?, MSNSent=MSNSent+?, MSNReceived=MSNReceived+?, YahooSent=YahooSent+?, YahooReceived=YahooReceived+?, AIMSent=AIMSent+?, AIMReceived=AIMReceived+?, GTalkSent=GTalkSent+?, GTalkReceived=GTalkReceived+?, FacebookSent=FacebookSent+?, FacebookReceived=FacebookReceived+? where StatsDate=? and CountryID=?");
            ps.setInt(1, numPrivate);
            ps.setInt(2, numGroupChatSent);
            ps.setInt(3, numGroupChatReceived);
            ps.setInt(4, numChatRoomSent);
            ps.setInt(5, numChatRoomReceived);
            ps.setInt(6, numSMS);
            ps.setInt(7, numMSNSent);
            ps.setInt(8, numMSNReceived);
            ps.setInt(9, numYahooSent);
            ps.setInt(10, numYahooReceived);
            ps.setInt(11, numAIMSent);
            ps.setInt(12, numAIMReceived);
            ps.setInt(13, numGTalkSent);
            ps.setInt(14, numGTalkReceived);
            ps.setInt(15, numFacebookSent);
            ps.setInt(16, numFacebookReceived);
            ps.setDate(17, new java.sql.Date(dateOfStats.getTime()));
            ps.setInt(18, countryID);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                ps.close();
                ps = conn.prepareStatement("insert into MessageStats (StatsDate, CountryID, Private, GroupChatSent, GroupChatReceived, ChatRoomSent, ChatRoomReceived, SMS, MSNSent, MSNReceived, YahooSent, YahooReceived, AIMSent, AIMReceived, GTalkSent, GTalkReceived, FacebookSent, FacebookReceived) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                ps.setDate(1, new java.sql.Date(dateOfStats.getTime()));
                ps.setInt(2, countryID);
                ps.setInt(3, numPrivate);
                ps.setInt(4, numGroupChatSent);
                ps.setInt(5, numGroupChatReceived);
                ps.setInt(6, numChatRoomSent);
                ps.setInt(7, numChatRoomReceived);
                ps.setInt(8, numSMS);
                ps.setInt(9, numMSNSent);
                ps.setInt(10, numMSNReceived);
                ps.setInt(11, numYahooSent);
                ps.setInt(12, numYahooReceived);
                ps.setInt(13, numAIMSent);
                ps.setInt(14, numAIMReceived);
                ps.setInt(15, numGTalkSent);
                ps.setInt(16, numGTalkReceived);
                ps.setInt(17, numFacebookSent);
                ps.setInt(18, numFacebookReceived);
                rowsUpdated = ps.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new EJBException("Unable to insert message stats");
                }
            }
            Object var23_23 = null;
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
                Object var23_24 = null;
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

    public void sendSystemEmail(String to, String subject, String content) throws EJBException {
        this.sendSystemEmail(to, subject, content, null);
    }

    public void sendSystemEmail(String to, String subject, String content, UserEmailAddressData.UserEmailAddressTypeEnum emailType) throws EJBException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Sending system email to " + to + ".\nSubject: " + subject + "\nContent: " + content + "\nType: " + (Object)((Object)emailType)));
        }
        UserEmailAddressData userEmailAddressData = null;
        if (emailType != null) {
            try {
                UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                int userid = userBean.getUserID(to, null, false);
                if (userid > 0) {
                    userEmailAddressData = userBean.getUserEmailAddressByType(userid, emailType);
                }
            }
            catch (Exception e) {
                log.warn((Object)("Unable to find verified external email address for [" + to + "] to send system email. Using @mig33.com email instead :" + e.getMessage()));
            }
        }
        EmailUserNotification note = new EmailUserNotification();
        note.subject = subject;
        note.message = content;
        if (userEmailAddressData != null && userEmailAddressData.verified) {
            note.emailAddress = userEmailAddressData.emailAddress;
            try {
                EJBIcePrxFinder.getUserNotificationServiceProxy().notifyUserViaEmail(note);
            }
            catch (FusionException e) {
                throw new EJBException(e.message);
            }
        }
        try {
            EJBIcePrxFinder.getUserNotificationServiceProxy().notifyFusionUserViaEmail(to, note);
        }
        catch (FusionException e) {
            throw new EJBException(e.message);
        }
    }

    public void sendEmailFromNoReply(String destinationAddress, String subject, String content) throws EJBException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Sending email from noreply to " + destinationAddress + ".\nSubject: " + subject + "\nContent: " + content));
        }
        try {
            EJBIcePrxFinder.getUserNotificationServiceProxy().sendEmailFromNoReply(destinationAddress, subject, content);
        }
        catch (FusionException e) {
            throw new EJBException(e.message);
        }
    }

    public String getUserEmailAddress(String username) throws EJBException {
        try {
            String emailAddress = username + "@" + SystemProperty.get("MailDomain");
            if (StringUtil.isValidEmail(emailAddress)) {
                return emailAddress;
            }
            throw new EJBException("Invalid email address");
        }
        catch (NoSuchFieldException e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void sendEmail(String senderUsername, String senderPassword, String to, String subject, String content) {
        conn = null;
        ps = null;
        rs = null;
        try {
            block44: {
                maxEmailLength = SystemProperty.getInt("MaxEmailLength");
                if (to.length() > maxEmailLength) throw new EJBException("TO, Subject and messagebody must not be greater than " + maxEmailLength + " chars");
                if (subject.length() > maxEmailLength) throw new EJBException("TO, Subject and messagebody must not be greater than " + maxEmailLength + " chars");
                if (content.length() > maxEmailLength) {
                    throw new EJBException("TO, Subject and messagebody must not be greater than " + maxEmailLength + " chars");
                }
                if (subject.contains(senderPassword)) {
                    throw new EJBException("You may not send your password in the subject of an email");
                }
                if (content.contains(senderPassword)) {
                    throw new EJBException("You may not send your password in the message body of an email");
                }
                recipientList = Arrays.asList(MessageBean.convertRecipientsToValidEmailAddresses(to.split("[,; ]")));
                if (!Collections.disjoint(recipientList, supportEmailAliases = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.Default.SUPPORT_EMAIL_ALIASES)))) {
                    rateLimitEmailContact = SystemProperty.get("RateLimitEmailContact", "10/1D");
                    try {
                        MemCachedRateLimiter.hit("EML_CT", senderUsername, rateLimitEmailContact);
                    }
                    catch (MemCachedRateLimiter.LimitExceeded e) {
                        throw new EJBException("You are not allowed to send more than " + String.format(e.getPrettyMessage(), new Object[]{"emails to contact"}), (Exception)e);
                    }
                    catch (MemCachedRateLimiter.FormatError e) {
                        throw new EJBException("Internal Error");
                    }
                }
                if ((userData = (userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class)).loadUser(senderUsername, false, false)) == null) {
                    MessageBean.log.error((Object)String.format("Unable to send email - user '%s' does not exist", new Object[]{senderUsername}));
                    throw new EJBException("Invalid sender username");
                }
                if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.SEND_MIG33_EMAIL, userData)) {
                    throw new EJBException("You must authenticate your account before you can send email");
                }
                senderID = userData.userID;
                senderCountryID = userData.countryID;
                reservedAliases = SystemProperty.getArray("MailReservedAliases");
                conn = this.dataSourceSlave.getConnection();
                sql = "select broadcastUsername from broadcastlist where username = ? and broadcastUsername in (";
                for (i = 0; i < recipientList.size(); ++i) {
                    sql = i == 0 ? sql + "?" : sql + ",?";
                }
                sql = sql + ")";
                ps = conn.prepareStatement(sql);
                ps.setString(1, senderUsername);
                for (i = 0; i < recipientList.size(); ++i) {
                    ps.setString(i + 2, recipientList.get(i).substring(0, recipientList.get(i).indexOf("@")));
                }
                rs = ps.executeQuery();
                broadcastList = new LinkedList<String>();
                while (rs.next()) {
                    broadcastList.add(rs.getString("broadcastUsername"));
                }
                MessageBean.log.debug((Object)("In broadcast list: " + broadcastList));
                finalRecipientList = new LinkedList<String>();
                block26: for (String recipient : recipientList) {
                    onReservedList = false;
                    for (z = 0; z < reservedAliases.length; ++z) {
                        if (!reservedAliases[z].equalsIgnoreCase(recipient.substring(0, recipient.indexOf("@")))) continue;
                        onReservedList = true;
                        finalRecipientList.add(recipient);
                        break;
                    }
                    if (onReservedList) continue;
                    for (String username : broadcastList) {
                        if (!username.equalsIgnoreCase(recipient.substring(0, recipient.indexOf("@")))) continue;
                        finalRecipientList.add(recipient);
                        continue block26;
                    }
                }
                MessageBean.log.debug((Object)("Final Recipient list: " + finalRecipientList));
                if (finalRecipientList.size() != recipientList.size()) {
                    throw new EJBException("Please ensure that all recipients entered are on your contact list");
                }
                if (finalRecipientList.size() != 0) break block44;
                var26_35 = null;
                ** GOTO lbl121
            }
            recipientArray = finalRecipientList.toArray(new String[finalRecipientList.size()]);
            note = new EmailUserNotification();
            note.subject = subject;
            note.message = content;
            EJBIcePrxFinder.getUserNotificationServiceProxy().notifyUsersViaFusionEmail(senderUsername, senderPassword, recipientArray, note);
            try {
                trigger = new EmailSentTrigger(userData);
                trigger.amountDelta = 0.0;
                trigger.quantityDelta = 1;
                RewardCentre.getInstance().sendTrigger(trigger);
            }
            catch (Exception e) {
                MessageBean.log.warn((Object)"Unable to notify reward system for email sent", (Throwable)e);
            }
            ** GOTO lbl141
        }
        catch (FusionException e) {
            throw new EJBException("There has been an error sending your mail: " + e.message, (Exception)e);
        }
        catch (NullPointerException e) {
            throw new EJBException("There has been an error sending your mail: Internal server error", (Exception)e);
        }
        catch (Exception e) {
            throw new EJBException("There has been an error sending your mail: " + e.getMessage(), e);
        }
        {
            block48: {
                block47: {
                    block46: {
                        block45: {
                            catch (Throwable var25_41) {
                                var26_37 = null;
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
                                    if (conn == null) throw var25_41;
                                    conn.close();
                                    throw var25_41;
                                }
                                catch (SQLException e) {
                                    conn = null;
                                }
                                throw var25_41;
                            }
lbl121:
                            // 1 sources

                            ** try [egrp 4[TRYBLOCK] [12 : 1080->1095)] { 
lbl122:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block45;
lbl125:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 5[TRYBLOCK] [13 : 1100->1115)] { 
lbl129:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block46;
lbl132:
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
lbl141:
                    // 2 sources

                    var26_36 = null;
                    ** try [egrp 4[TRYBLOCK] [12 : 1080->1095)] { 
lbl143:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block47;
lbl146:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 5[TRYBLOCK] [13 : 1100->1115)] { 
lbl150:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block48;
lbl153:
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

    public static String[] convertRecipientsToValidEmailAddresses(String[] rawRecipientList) throws NoSuchFieldException {
        if (rawRecipientList == null || rawRecipientList.length < 1) {
            return StringUtil.EMPTY_STRING_ARRAY;
        }
        String[] recipientList = new String[rawRecipientList.length];
        System.arraycopy(rawRecipientList, 0, recipientList, 0, rawRecipientList.length);
        String mailDomain = "@" + SystemProperty.get(SystemPropertyEntities.Default.DEFAULT_EMAIL_DOMAIN);
        List<String> whitelistedEmailDomains = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.Default.WHITELISTED_RECIPIENT_EMAIL_DOMAINS));
        for (int i = 0; i < recipientList.length; ++i) {
            if (StringUtil.isBlank(recipientList[i])) {
                throw new InvalidMig33EmailRecipientEJBException("Please ensure that all recipients entered are valid migme usernames");
            }
            int indexOfAtSign = recipientList[i].indexOf("@");
            if (indexOfAtSign < 0) {
                if (StringUtil.VALID_MIG33_USERNAME_OLD_STYLE.matcher(recipientList[i]).find()) {
                    int n = i;
                    recipientList[n] = recipientList[n] + mailDomain;
                    continue;
                }
                throw new InvalidMig33EmailRecipientEJBException("Please ensure that all recipients entered are valid migme usernames");
            }
            String recipientEmailDomain = recipientList[i].substring(indexOfAtSign + 1).trim().toLowerCase();
            if (!whitelistedEmailDomains.contains(recipientEmailDomain)) {
                throw new InvalidMig33EmailRecipientEJBException(String.format("Invalid recipient email address provided: %s", recipientList[i]));
            }
            if (StringUtil.isValidEmail(recipientList[i])) continue;
            throw new InvalidMig33EmailRecipientEJBException(String.format("Invalid recipient email address provided: %s", recipientList[i]));
        }
        return recipientList;
    }

    /*
     * Loose catch block
     */
    public void updateRoomDescriptions(String[] roomNames, String description) throws EJBException {
        block18: {
            ChatRoomPrx[] chatRoomProxies;
            Connection conn = null;
            Statement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update chatroom set description = ? where name in (" + StringUtil.asString(roomNames) + ")");
            ps.setString(1, description);
            ps.executeUpdate();
            for (String roomName : roomNames) {
                ChatRoomUtils.invalidateChatRoomCache(roomName);
            }
            for (ChatRoomPrx chatRoomPrx : chatRoomProxies = EJBIcePrxFinder.findChatRoomProxies(roomNames)) {
                if (chatRoomPrx == null) continue;
                chatRoomPrx.setDescription(description);
            }
            Object var11_14 = null;
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
                    throw new EJBException("Failed to update room description " + e.getMessage());
                }
                catch (LocalException e) {
                    throw new EJBException("Failed to update room description with objectcache " + e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var11_15 = null;
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
    public String[] getGroupChatRooms(int groupId) throws EJBException {
        String[] chatroomArr;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LinkedList<String> chatrooms = new LinkedList<String>();
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select name from chatroom where groupid = ? and status = ?");
        ps.setInt(1, groupId);
        ps.setInt(2, ChatRoomData.StatusEnum.ACTIVE.value());
        rs = ps.executeQuery();
        while (rs.next()) {
            chatrooms.add(rs.getString("name"));
        }
        rs.close();
        String[] stringArray = chatroomArr = chatrooms.toArray(new String[chatrooms.size()]);
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
        return stringArray;
        catch (SQLException e) {
            try {
                throw new EJBException("Failed to get GroupChatRooms: " + e.getMessage());
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
    public Integer[] announceMessageToChatrooms(String[] chatroomNames, String message, int waitTime) throws EJBException {
        LinkedList<Integer> chatroomsAnnounced;
        block27: {
            if (!SystemProperty.getBool("ChatroomAdminAnnouncementEnabled", false)) {
                throw new EJBException("Chatroom admin announcements are disabled.");
            }
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            chatroomsAnnounced = new LinkedList<Integer>();
            LinkedList<String> chatrooms = new LinkedList<String>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT name FROM chatroom WHERE name IN (" + StringUtil.asString(chatroomNames) + ") " + "AND status = ?");
            ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            while (rs.next()) {
                chatrooms.add(rs.getString("name"));
            }
            rs.close();
            if (chatrooms.size() > 0) {
                ChatRoomPrx[] chatRoomProxies;
                for (ChatRoomPrx chatRoomPrx : chatRoomProxies = EJBIcePrxFinder.findChatRoomProxies(chatrooms.toArray(new String[chatrooms.size()]))) {
                    if (chatRoomPrx == null) continue;
                    log.info((Object)("Sending announcement to [" + chatRoomPrx.getRoomData().name + "] :: " + message));
                    chatRoomPrx.adminAnnounce(message, waitTime);
                    chatroomsAnnounced.add(chatRoomPrx.getRoomData().id);
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
                    throw new EJBException("Failed to broadcast message: " + e.getMessage());
                }
                catch (LocalException e) {
                    throw new EJBException("Failed to broadcast message to chat rooms: " + e.getMessage());
                }
                catch (FusionException e) {
                    throw new EJBException("Failed to broadcast message to chat rooms: " + e.getMessage());
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
        return chatroomsAnnounced.toArray(new Integer[chatroomsAnnounced.size()]);
    }

    /*
     * Loose catch block
     */
    public Integer[] announceMessageToUserOwnedChatrooms(String message, int waitTime) throws EJBException {
        LinkedList<Integer> chatroomsAnnounced;
        block27: {
            if (!SystemProperty.getBool("ChatroomAdminAnnouncementEnabled", false)) {
                throw new EJBException("Chatroom admin announcements are disabled.");
            }
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            chatroomsAnnounced = new LinkedList<Integer>();
            LinkedList<String> chatrooms = new LinkedList<String>();
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("SELECT name FROM chatroom WHERE status = ? AND creator IS NOT NULL AND groupid IS NULL AND type = 1 AND userowned = 1");
            ps.setInt(1, ChatRoomData.StatusEnum.ACTIVE.value());
            rs = ps.executeQuery();
            while (rs.next()) {
                log.error((Object)rs.getString("name"));
                chatrooms.add(rs.getString("name"));
            }
            rs.close();
            if (chatrooms.size() > 0) {
                ChatRoomPrx[] chatRoomProxies;
                for (ChatRoomPrx chatRoomPrx : chatRoomProxies = EJBIcePrxFinder.findChatRoomProxies(chatrooms.toArray(new String[chatrooms.size()]))) {
                    if (chatRoomPrx == null) continue;
                    log.info((Object)("Sending announcement to [" + chatRoomPrx.getRoomData().name + "] :: " + message));
                    chatRoomPrx.adminAnnounce(message, waitTime);
                    chatroomsAnnounced.add(chatRoomPrx.getRoomData().id);
                }
            }
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
                break block27;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block27;
            {
                catch (SQLException e) {
                    throw new EJBException("Failed to announce message: " + e.getMessage());
                }
                catch (LocalException e) {
                    log.error((Object)e, (Throwable)e);
                    throw new EJBException("Failed to announce message to chat rooms: " + e.getMessage());
                }
                catch (FusionException e) {
                    throw new EJBException("Failed to broadcast message to chat rooms: " + e.getMessage());
                }
            }
            catch (Throwable throwable) {
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
                throw throwable;
            }
        }
        return chatroomsAnnounced.toArray(new Integer[chatroomsAnnounced.size()]);
    }

    public void updateRoomDescription(String roomName, String description) throws EJBException {
        this.updateRoomDescriptions(new String[]{roomName}, description);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void sendChangeRoomOwnerEmail(String oldOwner, String roomName, String newOwner) throws EJBException {
        connMaster = null;
        ps = null;
        rs = null;
        try {
            block30: {
                connMaster = this.dataSourceMaster.getConnection();
                userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                newOwnerData = userBean.loadUser(newOwner, false, false);
                if (newOwnerData == null) {
                    throw new EJBException("User '" + newOwner + "' does not exist");
                }
                if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.CHANGE_ROOM_OWNER_EMAIL, newOwnerData)) {
                    throw new EJBException("You cannot change room ownership to a non-authenticated user");
                }
                ps = connMaster.prepareStatement("select c.newowner, u.password from chatroom c, user u where c.creator=u.username and c.name=? and c.creator=? and c.userowned=1");
                ps.setString(1, roomName);
                ps.setString(2, oldOwner);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EJBException("You cannot change room ownership unless you own the room");
                }
                if (!newOwner.equals(rs.getString("newOwner"))) break block30;
                var12_12 = null;
                ** GOTO lbl66
            }
            oldOwnerPassword = rs.getString("password");
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("update chatroom set newowner = ? where name = ?");
            ps.setString(1, newOwner);
            ps.setString(2, roomName);
            if (ps.executeUpdate() != 1) {
                throw new EJBException("Unable to update new owner for chat room " + roomName);
            }
            ChatRoomUtils.invalidateChatRoomCache(roomName);
            email = SystemProperty.get("ChatRoomOwnershipChangeEmail").replaceAll("%OldOwner", oldOwner).replaceAll("%RoomName", roomName);
            this.sendEmail(oldOwner, oldOwnerPassword, newOwner, "Chat room ownership change", email);
            ** GOTO lbl86
        }
        catch (CreateException e) {
            throw new EJBException("Failed to send room ownership change email. " + e.getMessage());
        }
        catch (SQLException e) {
            throw new EJBException("Failed to send room ownership change email. " + e.getMessage());
        }
        catch (NoSuchFieldException e) {
            throw new EJBException("Failed to send room ownership change email. " + e.getMessage());
        }
        {
            block34: {
                block33: {
                    block32: {
                        block31: {
                            catch (Throwable var11_20) {
                                var12_14 = null;
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
                                    if (connMaster == null) throw var11_20;
                                    connMaster.close();
                                    throw var11_20;
                                }
                                catch (SQLException e) {
                                    connMaster = null;
                                }
                                throw var11_20;
                            }
lbl66:
                            // 1 sources

                            ** try [egrp 2[TRYBLOCK] [9 : 439->454)] { 
lbl67:
                            // 1 sources

                            if (rs != null) {
                                rs.close();
                            }
                            break block31;
lbl70:
                            // 1 sources

                            catch (SQLException e) {
                                rs = null;
                            }
                        }
                        ** try [egrp 3[TRYBLOCK] [10 : 459->474)] { 
lbl74:
                        // 1 sources

                        if (ps != null) {
                            ps.close();
                        }
                        break block32;
lbl77:
                        // 1 sources

                        catch (SQLException e) {
                            ps = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return;
                    }
                    if (connMaster == null) return;
                    connMaster.close();
                    return;
lbl86:
                    // 1 sources

                    var12_13 = null;
                    ** try [egrp 2[TRYBLOCK] [9 : 439->454)] { 
lbl88:
                    // 1 sources

                    if (rs != null) {
                        rs.close();
                    }
                    break block33;
lbl91:
                    // 1 sources

                    catch (SQLException e) {
                        rs = null;
                    }
                }
                ** try [egrp 3[TRYBLOCK] [10 : 459->474)] { 
lbl95:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block34;
lbl98:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (connMaster == null) return;
            connMaster.close();
            return;
        }
    }

    /*
     * Loose catch block
     */
    public void changeRoomOwner(String oldOwner, String roomName, String newOwner) throws EJBException {
        block24: {
            ResultSet rs;
            PreparedStatement ps;
            Connection connMaster;
            block21: {
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("select newowner from chatroom where name=? and creator=? and userowned=1");
                ps.setString(1, roomName);
                ps.setString(2, oldOwner);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new EJBException("You cannot change room ownership unless you own the room");
                }
                if (!newOwner.equals(rs.getString("newOwner"))) {
                    throw new EJBException(newOwner + " is not the newly appointed owner of the room");
                }
                rs.close();
                ps.close();
                ps = connMaster.prepareStatement("update chatroom set creator = ?, newowner = null where name = ?");
                ps.setString(1, newOwner);
                ps.setString(2, roomName);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated != 1) break block21;
                ChatRoomUtils.invalidateChatRoomCache(roomName);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(roomName);
                if (chatRoomPrx == null) break block21;
                chatRoomPrx.changeOwner(oldOwner, newOwner);
                chatRoomPrx.putSystemMessage(newOwner + " is the new owner of this room", null);
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block24;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block24;
            {
                catch (SQLException e) {
                    throw new EJBException("Failed to change room owner " + e.getMessage());
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

    /*
     * Loose catch block
     */
    public void addRoomModerator(String ownerUsername, String chatRoomName, String moderatorUsername) throws EJBException {
        block25: {
            Connection connMaster = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select id from chatroom where name=? and creator=? and userowned=1");
            ps.setString(1, chatRoomName);
            ps.setString(2, ownerUsername);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("You cannot add moderators to a room unless you own the room");
            }
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("insert ignore into chatroommoderator select chatroom.id, user.username from chatroom, user where chatroom.name=? and user.username=?");
            ps.setString(1, chatRoomName);
            ps.setString(2, moderatorUsername.toLowerCase());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 1) {
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CHATROOM_MODERATORS, chatRoomName);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomName);
                if (chatRoomPrx != null) {
                    chatRoomPrx.addModerator(moderatorUsername);
                    chatRoomPrx.putSystemMessage(moderatorUsername + " is now a moderator of this room", null);
                }
            }
            String subject = "Chat room " + chatRoomName + ": Moderator " + moderatorUsername + " added";
            String body = "Your chat room " + chatRoomName + " has been modified.\n\n";
            body = body + "The user " + moderatorUsername + " is now a moderator of the room.\n\n";
            body = body + "If this was done in error or you want to change this please go to your room and select Room Settings from the menu.";
            this.sendSystemEmail(ownerUsername, subject, body);
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block25;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block25;
            {
                catch (SQLException e) {
                    log.error((Object)"SQLException in addRoomModerator()", (Throwable)e);
                    throw new EJBException("Unable to add moderator");
                }
                catch (LocalException e) {
                    log.error((Object)"Ice.LocalException in addRoomModerator()", (Throwable)e);
                    throw new EJBException("Unable to add moderator");
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

    /*
     * Loose catch block
     */
    public void resetRoomModerators(String chatRoomName) throws EJBException {
        block22: {
            ResultSet rs;
            PreparedStatement ps;
            Connection connMaster;
            block19: {
                connMaster = null;
                ps = null;
                rs = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("delete chatroommoderator from chatroommoderator inner join chatroom on chatroommoderator.chatroomid=chatroom.id where chatroom.name=?");
                ps.setString(1, chatRoomName);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated != 1) break block19;
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CHATROOM_MODERATORS, chatRoomName);
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
                    log.error((Object)"SQLException in resetRoomModerators()", (Throwable)e);
                    throw new EJBException("Unable to reset moderators");
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

    /*
     * Loose catch block
     */
    public void removeRoomModerator(String ownerUsername, String chatRoomName, String moderatorUsername) throws EJBException {
        block25: {
            Connection connMaster = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            connMaster = this.dataSourceMaster.getConnection();
            ps = connMaster.prepareStatement("select id from chatroom where name=? and creator=? and userowned=1");
            ps.setString(1, chatRoomName);
            ps.setString(2, ownerUsername);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("You cannot remove moderators from a room unless you own the room");
            }
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("delete chatroommoderator from chatroommoderator inner join chatroom on chatroommoderator.chatroomid=chatroom.id where chatroommoderator.username=? and chatroom.name=?");
            ps.setString(1, moderatorUsername);
            ps.setString(2, chatRoomName);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 1) {
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CHATROOM_MODERATORS, chatRoomName);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomName);
                if (chatRoomPrx != null) {
                    chatRoomPrx.removeModerator(moderatorUsername);
                    chatRoomPrx.putSystemMessage(moderatorUsername + " is no longer a moderator of this room", null);
                }
            }
            String subject = "Chat room " + chatRoomName + ": Moderator " + moderatorUsername + " removed";
            String body = "Your chat room " + chatRoomName + " has been modified.\n\n";
            body = body + "The user " + moderatorUsername + " is no longer a moderator of the room.\n\n";
            body = body + "If this was done in error or you want to change this please go to your room and select Room Settings from the menu.";
            this.sendSystemEmail(ownerUsername, subject, body);
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block25;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block25;
            {
                catch (SQLException e) {
                    log.error((Object)"SQLException in removeRoomModerator()", (Throwable)e);
                    throw new EJBException("Unable to remove moderator");
                }
                catch (LocalException e) {
                    log.error((Object)"Ice.LocalException in removeRoomModerator()", (Throwable)e);
                    throw new EJBException("Unable to remove moderator");
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

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void banGroupMember(String bannedByUsername, GroupData groupData, String bannedUsername) throws EJBException {
        connMaster = null;
        connSlave = null;
        ps = null;
        rs = null;
        try {
            block40: {
                block41: {
                    bannedByUsername = bannedByUsername.toLowerCase();
                    bannedUsername = bannedUsername.toLowerCase();
                    instigatorIsGroupAdmin = false;
                    connSlave = this.dataSourceSlave.getConnection();
                    ps = connSlave.prepareStatement("select chatroomadmin from user where username = ?");
                    ps.setString(1, bannedUsername);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        MessageBean.log.warn((Object)(bannedByUsername + " is trying to ban a non-existant user: " + bannedUsername));
                        throw new EJBException("Unable to ban user " + bannedUsername);
                    }
                    if (rs.getBoolean("chatroomadmin")) {
                        throw new EJBException("You cannot ban an admin");
                    }
                    rs.close();
                    ps.close();
                    ps = connSlave.prepareStatement("select * from groupmember  where username = ? and groupid = ? and status = ?");
                    ps.setString(1, bannedByUsername);
                    ps.setInt(2, groupData.id);
                    ps.setInt(3, GroupMemberData.StatusEnum.ACTIVE.value());
                    rs = ps.executeQuery();
                    if (rs.next() == false) throw new EJBException("You cannot ban a user from the group unless you are an admin or moderator");
                    type = rs.getInt("type");
                    if (type != GroupMemberData.TypeEnum.ADMINISTRATOR.value() && type != GroupMemberData.TypeEnum.MODERATOR.value()) {
                        throw new EJBException("You cannot ban a user from the group unless you are an admin or moderator");
                    }
                    if (type == GroupMemberData.TypeEnum.ADMINISTRATOR.value()) {
                        instigatorIsGroupAdmin = true;
                    }
                    rs.close();
                    ps.close();
                    ps = connSlave.prepareStatement("select * from groupmember  where username = ? and groupid = ? and status in (?, ?)");
                    ps.setString(1, bannedUsername);
                    ps.setInt(2, groupData.id);
                    ps.setInt(3, GroupMemberData.StatusEnum.ACTIVE.value());
                    ps.setInt(4, GroupMemberData.StatusEnum.BANNED.value());
                    rs = ps.executeQuery();
                    connMaster = this.dataSourceMaster.getConnection();
                    if (!rs.next()) break block40;
                    type = rs.getInt("type");
                    status = rs.getInt("status");
                    if (GroupMemberData.StatusEnum.BANNED.value() != status) break block41;
                    var14_12 = null;
                    ** GOTO lbl119
                }
                if (type == GroupMemberData.TypeEnum.ADMINISTRATOR.value()) throw new EJBException("You cannot ban an group admin or moderator");
                if (type == GroupMemberData.TypeEnum.MODERATOR.value() && !instigatorIsGroupAdmin) {
                    throw new EJBException("You cannot ban an group admin or moderator");
                }
                updateQuery = new StringBuilder("update groupmember set status = ?");
                if (type == GroupMemberData.TypeEnum.MODERATOR.value() && instigatorIsGroupAdmin) {
                    updateQuery.append(", type = " + GroupMemberData.TypeEnum.REGULAR.value());
                }
                updateQuery.append(" where groupid = ? and username = ? and status = ?");
                ps = connMaster.prepareStatement(updateQuery.toString());
                ps.setInt(1, GroupMemberData.StatusEnum.BANNED.value());
                ps.setInt(2, groupData.id);
                ps.setString(3, bannedUsername);
                ps.setInt(4, GroupMemberData.StatusEnum.ACTIVE.value());
                count = ps.executeUpdate();
                ps.close();
                if (count > 0) {
                    ps = connMaster.prepareStatement("update groups set nummembers=nummembers-? where id=?");
                    ps.setInt(1, count);
                    ps.setInt(2, groupData.id);
                    ps.executeUpdate();
                    ps.close();
                }
                ** GOTO lbl146
            }
            if (!groupData.isOpenGroup()) {
                MessageBean.log.error((Object)(bannedUsername + " is not part of this group"));
                throw new EJBException(bannedUsername + " is not part of this group");
            }
            ps = connMaster.prepareStatement("insert ignore into groupblacklist values (?, ?, ?)");
            ps.setInt(1, groupData.id);
            ps.setString(2, bannedUsername);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            ps.close();
            ** GOTO lbl146
        }
        catch (SQLException e) {
            MessageBean.log.error((Object)"SQLException in banGroupMember()", (Throwable)e);
            throw new EJBException("Unable to ban group member");
        }
        {
            block47: {
                block46: {
                    block45: {
                        block44: {
                            block43: {
                                block42: {
                                    catch (Throwable var13_20) {
                                        var14_14 = null;
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
                                        try {
                                            if (connSlave == null) throw var13_20;
                                            connSlave.close();
                                            throw var13_20;
                                        }
                                        catch (SQLException e) {
                                            connSlave = null;
                                        }
                                        throw var13_20;
                                    }
lbl119:
                                    // 1 sources

                                    ** try [egrp 2[TRYBLOCK] [5 : 882->897)] { 
lbl120:
                                    // 1 sources

                                    if (rs != null) {
                                        rs.close();
                                    }
                                    break block42;
lbl123:
                                    // 1 sources

                                    catch (SQLException e) {
                                        rs = null;
                                    }
                                }
                                ** try [egrp 3[TRYBLOCK] [6 : 902->917)] { 
lbl127:
                                // 1 sources

                                if (ps != null) {
                                    ps.close();
                                }
                                break block43;
lbl130:
                                // 1 sources

                                catch (SQLException e) {
                                    ps = null;
                                }
                            }
                            ** try [egrp 4[TRYBLOCK] [7 : 922->937)] { 
lbl134:
                            // 1 sources

                            if (connMaster != null) {
                                connMaster.close();
                            }
                            break block44;
lbl137:
                            // 1 sources

                            catch (SQLException e) {
                                connMaster = null;
                            }
                        }
                        try {}
                        catch (SQLException e) {
                            return;
                        }
                        if (connSlave == null) return;
                        connSlave.close();
                        return;
lbl146:
                        // 2 sources

                        var14_13 = null;
                        ** try [egrp 2[TRYBLOCK] [5 : 882->897)] { 
lbl148:
                        // 1 sources

                        if (rs != null) {
                            rs.close();
                        }
                        break block45;
lbl151:
                        // 1 sources

                        catch (SQLException e) {
                            rs = null;
                        }
                    }
                    ** try [egrp 3[TRYBLOCK] [6 : 902->917)] { 
lbl155:
                    // 1 sources

                    if (ps != null) {
                        ps.close();
                    }
                    break block46;
lbl158:
                    // 1 sources

                    catch (SQLException e) {
                        ps = null;
                    }
                }
                ** try [egrp 4[TRYBLOCK] [7 : 922->937)] { 
lbl162:
                // 1 sources

                if (connMaster != null) {
                    connMaster.close();
                }
                break block47;
lbl165:
                // 1 sources

                catch (SQLException e) {
                    connMaster = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (connSlave == null) return;
            connSlave.close();
            return;
        }
    }

    /*
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void unbanGroupMember(String unbannedByUsername, GroupData groupData, String unbannedUsername) throws EJBException {
        Connection connMaster = null;
        Statement ps = null;
        ResultSet rs = null;
        unbannedByUsername = unbannedByUsername.toLowerCase();
        unbannedUsername = unbannedUsername.toLowerCase();
        connMaster = this.dataSourceMaster.getConnection();
        String sql = "select * from groupmember  where username = ? and groupid = ? and status = ?";
        ps = connMaster.prepareStatement(sql);
        ps.setString(1, unbannedByUsername);
        ps.setInt(2, groupData.id);
        ps.setInt(3, GroupMemberData.StatusEnum.ACTIVE.value());
        rs = ps.executeQuery();
        if (!rs.next()) throw new EJBException("You cannot unban a user from the group unless you are an admin or moderator");
        int type = rs.getInt("type");
        if (type != GroupMemberData.TypeEnum.ADMINISTRATOR.value() && type != GroupMemberData.TypeEnum.MODERATOR.value()) {
            throw new EJBException("You cannot unban a user from the group unless you are an admin or moderator");
        }
        rs.close();
        ps.setString(1, unbannedUsername);
        ps.setInt(2, groupData.id);
        ps.setInt(3, GroupMemberData.StatusEnum.BANNED.value());
        rs = ps.executeQuery();
        if (!rs.next()) {
            if (!groupData.isOpenGroup()) throw new EJBException(unbannedUsername + " is either active or not a member of this group");
            ps = connMaster.prepareStatement("delete from groupblacklist where groupid = ? and username = ?");
            ps.setInt(1, groupData.id);
            ps.setString(2, unbannedUsername);
            ps.executeUpdate();
            ps.close();
        } else {
            ps = connMaster.prepareStatement("update groupmember set status = ? where groupid = ? and username = ?");
            ps.setInt(1, GroupMemberData.StatusEnum.ACTIVE.value());
            ps.setInt(2, groupData.id);
            ps.setString(3, unbannedUsername);
            int count = ps.executeUpdate();
            ps.close();
            ps = connMaster.prepareStatement("update groups set nummembers=nummembers+? where id=?");
            ps.setInt(1, count);
            ps.setInt(2, groupData.id);
            ps.executeUpdate();
            ps.close();
        }
        rs.close();
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
            if (connMaster == null) return;
            connMaster.close();
            return;
        }
        catch (SQLException e) {
            return;
        }
        {
            catch (SQLException e) {
                log.error((Object)"SQLException in unbanGroupMember()", (Throwable)e);
                throw new EJBException("Unable to ban group member");
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
                if (connMaster == null) throw throwable;
                connMaster.close();
                throw throwable;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            throw throwable;
        }
    }

    /*
     * Loose catch block
     */
    public boolean isUserBlackListedInGroup(String username, int groupId) throws EJBException {
        block31: {
            ResultSet rs;
            PreparedStatement ps;
            Connection connSlave;
            block25: {
                connSlave = null;
                ps = null;
                rs = null;
                connSlave = this.dataSourceSlave.getConnection();
                ps = connSlave.prepareStatement("select * from groupblacklist where groupid = ? and username = ?");
                ps.setInt(1, groupId);
                ps.setString(2, username);
                rs = ps.executeQuery();
                if (!rs.next()) break block25;
                boolean bl = true;
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
                return bl;
            }
            try {
                Object var8_9 = null;
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
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
                break block31;
            }
            catch (SQLException e) {
                connSlave = null;
            }
            break block31;
            {
                catch (SQLException e) {
                    throw new EJBException((Exception)e);
                }
            }
        }
        return false;
    }

    /*
     * Loose catch block
     */
    public boolean isModeratorOfChatRoom(String userName, String chatRoomName) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select username from chatroom, chatroommoderator where chatroom.name=? and chatroom.id=chatroommoderator.chatroomid and chatroommoderator.username=?");
            ps.setString(1, chatRoomName);
            ps.setString(2, userName);
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
    public void addChatRoomEmoteLog(ChatRoomEmoteLogData data) throws EJBException {
        block20: {
            if (data == null) {
                throw new EJBException("ChatRoomEmoteLogData is null");
            }
            Connection connMaster = null;
            Statement ps = null;
            connMaster = this.dataSourceMaster.getConnection();
            String sql = "insert into chatroomemotelog (instigator, target, emote, chatroomid, groupid, reasoncode, datecreated, parameters) values (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = connMaster.prepareStatement(sql);
            ps.setString(1, data.getInstigator());
            ps.setString(2, data.getTarget());
            ps.setString(3, data.getEmote());
            ps.setInt(4, data.getChatroomId());
            if (data.getGroupId() > 0) {
                ps.setInt(5, data.getGroupId());
            } else {
                ps.setNull(5, 4);
            }
            if (data.getReasonCode() > 0) {
                ps.setInt(6, data.getReasonCode());
            } else {
                ps.setNull(6, 4);
            }
            ps.setTimestamp(7, new Timestamp(data.getDateCreated().getTime()));
            ps.setString(8, data.getParameters());
            ps.executeUpdate();
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block20;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block20;
            {
                catch (SQLException sqe) {
                    throw new EJBException(sqe.getMessage());
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

    public void banUserFromRoom(String bannedByUsername, String chatRoomName, String bannedUsername) throws EJBException {
        boolean modifiedByModerator = false;
        try {
            ChatRoomPrx chatRoomPrx;
            String ownerUsername;
            bannedByUsername = bannedByUsername.toLowerCase();
            bannedUsername = bannedUsername.toLowerCase();
            try {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData targetData = userEJB.loadUser(bannedUsername, false, false);
                if (null == targetData) {
                    throw new EJBException("User doesn't exist");
                }
                ChatRoomData roomData = this.getChatRoom(chatRoomName);
                if (null == roomData) {
                    throw new EJBException("Room doesn't exist");
                }
                ownerUsername = roomData.creator;
                if (!bannedByUsername.equals(ownerUsername) && !roomData.moderators.contains(bannedByUsername)) {
                    throw new EJBException("You do not have sufficient rights to ban in this room");
                }
                if (roomData.creator.equals(bannedUsername) || targetData.chatRoomAdmin.booleanValue() || roomData.moderators.contains(bannedUsername)) {
                    throw new EJBException("You do not have sufficient rights to ban the user " + bannedUsername);
                }
            }
            catch (CreateException e) {
                throw new EJBException("EJBCreation error in banUserFromRoom");
            }
            if (this.updateChatroomBannedList(chatRoomName, bannedUsername) && (chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomName)) != null) {
                chatRoomPrx.banUser(bannedUsername);
                chatRoomPrx.putSystemMessage(bannedUsername + " has been banned from this room by " + bannedByUsername, null);
            }
            if (SystemProperty.getBool("ChatroomBanEmailEnabled", true)) {
                String subject = "Chat room " + chatRoomName + ": User " + bannedUsername + " banned";
                String body = "Your chat room " + chatRoomName + " has been modified.\n\n";
                if (modifiedByModerator) {
                    body = body + "This change was made by the moderator " + bannedByUsername + "\n\n";
                }
                body = body + "The user " + bannedUsername + " has been banned from the room.\n\n";
                body = body + "If this was done in error or you want to change this please go to your room and select Room Settings from the menu.";
                this.sendSystemEmail(ownerUsername, subject, body);
            }
        }
        catch (LocalException e) {
            log.error((Object)"Ice.LocalException in banUserFromRoom()", (Throwable)e);
            throw new EJBException("Unable to ban user");
        }
    }

    /*
     * Loose catch block
     */
    public boolean updateChatroomBannedList(String chatRoomName, String bannedUsername) throws EJBException {
        block22: {
            PreparedStatement ps;
            Connection connMaster;
            block18: {
                connMaster = null;
                ps = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("insert ignore into chatroombanneduser select chatroom.id, user.username, ? from chatroom, user where chatroom.name=? and user.username=?");
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setString(2, chatRoomName);
                ps.setString(3, bannedUsername.toLowerCase());
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated != 1) break block18;
                MemCachedClientWrapper.deletePaged(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BANNED_USERS, chatRoomName);
                boolean bl = true;
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
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                return bl;
            }
            try {
                Object var8_9 = null;
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
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
                    log.error((Object)"SQLException in banUserFromRoom()", (Throwable)e);
                    throw new EJBException("Unable to ban user");
                }
            }
        }
        return false;
    }

    /*
     * Loose catch block
     */
    public void unbanUserFromRoom(String unbannedByUsername, String chatRoomName, String bannedUsername) throws EJBException {
        block27: {
            Connection connMaster = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean modifiedByModerator = false;
            connMaster = this.dataSourceMaster.getConnection();
            String sql = "SELECT id, creator, NULL AS moderatorusername FROM chatroom WHERE NAME=? AND creator=? AND userowned=1 UNION SELECT chatroom.id, chatroom.creator, chatroommoderator.username AS moderatorusername FROM chatroom, chatroommoderator WHERE chatroom.name=? AND chatroom.id=chatroommoderator.chatroomid AND chatroommoderator.username=?";
            ps = connMaster.prepareStatement(sql);
            ps.setString(1, chatRoomName);
            ps.setString(2, unbannedByUsername);
            ps.setString(3, chatRoomName);
            ps.setString(4, unbannedByUsername);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("You cannot unban a user from the room unless you are an admin or moderator");
            }
            if (StringUtils.hasLength((String)rs.getString("moderatorusername"))) {
                modifiedByModerator = true;
            }
            String ownerUsername = rs.getString("creator");
            rs.close();
            ps.close();
            ps = connMaster.prepareStatement("delete chatroombanneduser from chatroombanneduser inner join chatroom on chatroombanneduser.chatroomid=chatroom.id where chatroombanneduser.username=? and chatroom.name=?");
            ps.setString(1, bannedUsername);
            ps.setString(2, chatRoomName);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 1) {
                MemCachedClientWrapper.deletePaged(MemCachedKeySpaces.CommonKeySpace.CHATROOM_BANNED_USERS, chatRoomName);
                ChatRoomPrx chatRoomPrx = EJBIcePrxFinder.findChatRoomPrx(chatRoomName);
                if (chatRoomPrx != null) {
                    chatRoomPrx.unbanUser(bannedUsername);
                    chatRoomPrx.putSystemMessage(bannedUsername + " is no longer banned from this room (unbanned by " + unbannedByUsername + ")", null);
                }
            }
            String subject = "Chat room " + chatRoomName + ": User " + bannedUsername + " no longer banned";
            String body = "Your chat room " + chatRoomName + " has been modified.\n\n";
            if (modifiedByModerator) {
                body = body + "This change was made by the moderator " + unbannedByUsername + "\n\n";
            }
            body = body + "The user " + bannedUsername + " is no longer banned from the room.\n\n";
            body = body + "If this was done in error or you want to change this please go to your room and select Room Settings from the menu.";
            this.sendSystemEmail(ownerUsername, subject, body);
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
                if (connMaster != null) {
                    connMaster.close();
                }
                break block27;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block27;
            {
                catch (SQLException e) {
                    log.error((Object)"SQLException in unbanUserFromRoom()", (Throwable)e);
                    throw new EJBException("Unable to unban user");
                }
                catch (LocalException e) {
                    log.error((Object)"Ice.LocalException in unbanUserFromRoom()", (Throwable)e);
                    throw new EJBException("Unable to unban user");
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

    /*
     * Loose catch block
     */
    public List<BotData> getBots() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select * from bot where status = 1");
        rs = ps.executeQuery();
        LinkedList<BotData> bots = new LinkedList<BotData>();
        while (rs.next()) {
            bots.add(new BotData(rs));
        }
        LinkedList<BotData> linkedList = bots;
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
        return linkedList;
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
    public BotData getBot(int id) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select * from bot where id = ? and status = 1");
        ps.setInt(1, id);
        rs = ps.executeQuery();
        BotData botData = rs.next() ? new BotData(rs) : null;
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
        return botData;
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
    public BotData getBotFromCommandName(String commandName) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select * from bot where commandname = ? and status = 1");
        ps.setString(1, commandName);
        rs = ps.executeQuery();
        BotData botData = rs.next() ? new BotData(rs) : null;
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
        return botData;
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
    public EmailTemplateData getEmailTemplateData(int emailTemplateID) {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block28: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            String sql = " select  t.id as id, t.name as name, t.templatetype as templatetype, t.subjectTemplate as subjecttemplate,  t.bodyTemplate as bodytemplate,  t.mimeType as mimetype,  p.id as partid,  p.templateid as parenttemplateid,  p.sequence as partsequence,  p.contentTemplate as partcontenttemplate, p.mimeType as partmimetype  from emailtemplate t left join emailtemplatepart p on (t.id = p.templateid)  where t.id = ?  order by p.sequence desc;";
            ps = conn.prepareStatement(" select  t.id as id, t.name as name, t.templatetype as templatetype, t.subjectTemplate as subjecttemplate,  t.bodyTemplate as bodytemplate,  t.mimeType as mimetype,  p.id as partid,  p.templateid as parenttemplateid,  p.sequence as partsequence,  p.contentTemplate as partcontenttemplate, p.mimeType as partmimetype  from emailtemplate t left join emailtemplatepart p on (t.id = p.templateid)  where t.id = ?  order by p.sequence desc;");
            ps.setInt(1, emailTemplateID);
            rs = ps.executeQuery();
            if (!rs.next()) break block28;
            EmailTemplateData result = new EmailTemplateData(rs);
            if (result.addPartTemplate(rs)) {
                while (rs.next()) {
                    result.addPartTemplate(rs);
                }
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.EMAIL_TEMPLATE, Integer.toString(emailTemplateID), result);
            EmailTemplateData emailTemplateData = result;
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
            return emailTemplateData;
        }
        MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.EMAIL_TEMPLATE, Integer.toString(emailTemplateID), null);
        EmailTemplateData emailTemplateData = null;
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
        return emailTemplateData;
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
    public void setChatRoomMaxSize(String chatRoomName, int maxSize) throws FusionEJBException {
        block18: {
            PreparedStatement ps;
            Connection connMaster;
            block16: {
                connMaster = null;
                ps = null;
                connMaster = this.dataSourceMaster.getConnection();
                ps = connMaster.prepareStatement("update chatroom set MaximumSize=? where chatroom.name=?");
                ps.setInt(1, maxSize);
                ps.setString(2, chatRoomName);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated != 1) {
                    String err = "Failed to update maximum size of chatroom=" + chatRoomName + " update modified zero rows";
                    log.error((Object)err);
                    throw new FusionEJBException(err);
                }
                ChatRoomPrx chatRoomProxy = EJBIcePrxFinder.findChatRoomPrx(chatRoomName);
                if (chatRoomProxy == null) break block16;
                chatRoomProxy.setMaximumSize(maxSize);
            }
            Object var8_10 = null;
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
                break block18;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block18;
            {
                catch (FusionEJBException e) {
                    throw e;
                }
                catch (Exception e) {
                    log.error((Object)("Exception in setChatRoomMaximumSize() for room=" + chatRoomName + ": " + e), (Throwable)e);
                    throw new FusionEJBException("Unable to set maximum size of chatroom=" + chatRoomName, e);
                }
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
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
}

