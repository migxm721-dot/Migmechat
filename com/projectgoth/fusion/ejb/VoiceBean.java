/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ejb;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VoiceGatewayData;
import com.projectgoth.fusion.data.VoiceRouteData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.slice.CallDataIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class VoiceBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(VoiceBean.class));
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
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
            log.error((Object)"Unable to create Voice EJB", (Throwable)e);
            throw new CreateException("Unable to create Voice EJB: " + e.getMessage());
        }
    }

    /*
     * Loose catch block
     */
    public String getDIDNumber(int countryID) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String number = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.DID_NUMBER, String.valueOf(countryID));
        if (number == null) {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select number from didnumber where countryid = ? and status = 1");
            ps.setInt(1, countryID);
            rs = ps.executeQuery();
            number = rs.next() ? rs.getString("number") : "0";
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.DID_NUMBER, String.valueOf(countryID), number);
        }
        String string = number.equals("0") ? null : number;
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
        return string;
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

    public String getFullDIDNumber(int countryID) throws EJBException {
        try {
            String didNumber = this.getDIDNumber(countryID);
            if (didNumber != null) {
                while (didNumber.charAt(0) == '0') {
                    didNumber = didNumber.substring(1);
                }
                MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                CountryData countryData = misBean.getCountry(countryID);
                if (!didNumber.startsWith(countryData.iddCode.toString())) {
                    didNumber = countryData.iddCode.toString() + didNumber;
                }
            }
            return didNumber;
        }
        catch (CreateException e) {
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CountryData getVoiceRate(Connection conn, String phoneNumber) throws SQLException, CreateException {
        ResultSet rs;
        Statement ps;
        block25: {
            CountryData countryData;
            block22: {
                CountryData countryData2;
                ps = null;
                rs = null;
                try {
                    MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
                    Integer iddCode = messageBean.getIDDCode(phoneNumber);
                    MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                    countryData = misBean.getCountryByIDDCode(iddCode, phoneNumber);
                    if (countryData != null) break block22;
                    countryData2 = null;
                    Object var11_12 = null;
                }
                catch (Throwable throwable) {
                    Object var11_15 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e) {
                        rs = null;
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
                return countryData2;
            }
            ps = conn.prepareStatement("select * from country where id = ?");
            ps.setInt(1, countryData.id);
            rs = ps.executeQuery();
            if (!rs.next()) break block25;
            CountryData countryData3 = new CountryData(rs);
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
            return countryData3;
        }
        CountryData countryData = null;
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
        return countryData;
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public CallData evaluatePhoneCall(CallData callData) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean sourceIsMobileNumber = false;
        boolean destinationIsMobileNumber = false;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select user.type, user.mobileverified, user.balance, user.status, country.name, country.allowphonecall, currency.exchangerate from user, country, currency where user.countryid = country.id and user.currency = currency.code and user.username = ?");
        ps.setString(1, callData.username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Invalid user " + callData.username);
        }
        if (rs.getInt("status") != UserData.StatusEnum.ACTIVE.value()) {
            throw new EJBException("Your account is suspended. Please contact migme");
        }
        if (!rs.getBoolean("mobileverified") && rs.getInt("type") != UserData.TypeEnum.MIG33_PREPAID_CARD.value()) {
            throw new EJBException("You need to authenticate your account first");
        }
        if (!rs.getBoolean("allowphonecall")) {
            throw new EJBException("Calls from " + rs.getString("name") + " are temporarily unavailable.  We apologize for the inconvenience");
        }
        double balance = rs.getDouble("balance") / rs.getDouble("exchangerate");
        MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
        callData.rate = 0.0;
        callData.signallingFee = 0.0;
        callData.retries = 0;
        callData.initialLeg = CallData.InitialLegEnum.SOURCE;
        CountryData sourceCountryData = null;
        CountryData destinationCountryData = null;
        if (callData.isCallThrough()) {
            if (callData.source == null || callData.source.equalsIgnoreCase("UNKNOWN")) {
                callData.source = "UNKNOWN";
                callData.sourceIDDCode = null;
            } else {
                callData.source = messageBean.cleanAndValidatePhoneNumber(callData.source, false);
                callData.sourceIDDCode = messageBean.getIDDCode(callData.source);
            }
            if (callData.didNumber == null) {
                throw new EJBException("DID number not specified");
            }
            sourceCountryData = this.getVoiceRate(conn, callData.didNumber);
            if (sourceCountryData == null) {
                throw new EJBException("Unable to determine country from DID number " + callData.didNumber);
            }
            if (sourceCountryData.callThroughSignallingFee == null || sourceCountryData.callThroughRate == null) {
                throw new EJBException("Unable to determine the call through rate for " + sourceCountryData.name);
            }
            callData.signallingFee = sourceCountryData.callThroughSignallingFee;
            callData.rate = sourceCountryData.callThroughRate;
        }
        if (callData.isCallback() && callData.sourceType == CallData.SourceDestinationTypeEnum.PSTN_PHONE) {
            callData.source = messageBean.cleanAndValidatePhoneNumber(callData.source, false);
            sourceCountryData = this.getVoiceRate(conn, callData.source);
            if (sourceCountryData == null) {
                throw new EJBException("Unable to determine country from source number " + callData.source);
            }
            callData.sourceIDDCode = sourceCountryData.iddCode;
            if (messageBean.isMobileNumber(callData.source, false)) {
                callData.signallingFee = sourceCountryData.mobileSignallingFee;
                callData.rate = sourceCountryData.mobileRate;
                sourceIsMobileNumber = true;
            } else {
                callData.signallingFee = sourceCountryData.callSignallingFee;
                callData.rate = sourceCountryData.callRate;
            }
            if (callData.signallingFee == null || callData.rate == null) {
                throw new EJBException("Unable to determine the call rate to " + callData.source);
            }
            callData.retries = sourceCountryData.callRetries;
        }
        if (callData.destinationType == CallData.SourceDestinationTypeEnum.PSTN_PHONE) {
            if (callData.destination == null || callData.destination.equals("UNKNOWN")) {
                if (callData.type != CallData.TypeEnum.MISSED_CALL_CALLBACK) {
                    throw new EJBException("Destination not specified");
                }
                callData.destination = "UNKNOWN";
                callData.destinationIDDCode = null;
            } else {
                Double secondLegRate;
                callData.destination = messageBean.cleanAndValidatePhoneNumber(callData.destination, false);
                destinationCountryData = this.getVoiceRate(conn, callData.destination);
                if (destinationCountryData == null) {
                    throw new EJBException("Unable to determine country from destination number " + callData.destination);
                }
                callData.destinationIDDCode = destinationCountryData.iddCode;
                if (messageBean.isMobileNumber(callData.destination, false)) {
                    secondLegRate = destinationCountryData.mobileRate;
                    destinationIsMobileNumber = true;
                } else {
                    secondLegRate = destinationCountryData.callRate;
                }
                if (secondLegRate == null) {
                    throw new EJBException("Unable to determine the call rate to " + callData.destination);
                }
                CallData callData2 = callData;
                callData2.rate = callData2.rate + secondLegRate;
            }
        }
        if (SystemProperty.getBool("VoiceRouteWhitelistEnabled", false)) {
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select * from voiceroutewhitelist where sourceidd = ? and destinationidd = ?");
            ps.setInt(1, sourceCountryData != null && sourceCountryData.iddCode != null ? sourceCountryData.iddCode : -1);
            ps.setInt(2, destinationCountryData != null && destinationCountryData.iddCode != null ? destinationCountryData.iddCode : -1);
            rs = ps.executeQuery();
            if (!rs.next()) {
                StringBuffer message = new StringBuffer();
                message.append("Voice call ");
                if (!StringUtil.isBlank(sourceCountryData.name)) {
                    message.append("from " + sourceCountryData.name + " ");
                }
                if (!StringUtil.isBlank(destinationCountryData.name)) {
                    message.append("to " + destinationCountryData.name + " ");
                }
                message.append("is not supported");
                throw new EJBException(message.toString());
            }
        }
        if (sourceCountryData != null && destinationCountryData != null) {
            rs.close();
            ps.close();
            String fixedRateSQL = null;
            if (callData.isCallback()) {
                if (!sourceIsMobileNumber && !destinationIsMobileNumber) {
                    fixedRateSQL = "select LandlineToLandline/ExchangeRate Rate, LandlineToLandlineSignallingFee/ExchangeRate SignallingFee";
                } else if (!sourceIsMobileNumber && destinationIsMobileNumber) {
                    fixedRateSQL = "select LandlineToMobile/ExchangeRate Rate, LandlineToMobileSignallingFee/ExchangeRate SignallingFee";
                } else if (sourceIsMobileNumber && !destinationIsMobileNumber) {
                    fixedRateSQL = "select MobileToLandline/ExchangeRate Rate, MobileToLandlineSignallingFee/ExchangeRate SignallingFee";
                } else if (sourceIsMobileNumber && destinationIsMobileNumber) {
                    fixedRateSQL = "select MobileToMobile/ExchangeRate Rate, MobileToMobileSignallingFee/ExchangeRate SignallingFee";
                }
            } else if (callData.isCallThrough()) {
                fixedRateSQL = !destinationIsMobileNumber ? "select CallThroughToLandline/ExchangeRate Rate, CallThroughToLandlineSignallingFee/ExchangeRate SignallingFee" : "select CallThroughToMobile/ExchangeRate Rate, CallThroughToMobileSignallingFee/ExchangeRate SignallingFee";
            }
            fixedRateSQL = fixedRateSQL + " from fixedcallrate, currency where fixedcallrate.sourcecountryid=? and fixedcallrate.destinationcountryid=? and fixedcallrate.currency=currency.code";
            ps = conn.prepareStatement(fixedRateSQL);
            ps.setInt(1, sourceCountryData.id);
            ps.setInt(2, destinationCountryData.id);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getObject("Rate") != null) {
                    callData.rate = rs.getDouble("Rate");
                }
                if (rs.getObject("SignallingFee") != null) {
                    callData.signallingFee = rs.getDouble("SignallingFee");
                }
            }
        }
        if (callData.isCallback() && callData.source.equals(callData.destination)) {
            throw new EJBException("Origin and destination numbers cannot be the same");
        }
        if (callData.rate == 0.0) {
            callData.maxCallDuration = Short.MAX_VALUE;
            if (balance < callData.signallingFee) {
                throw new EJBException("You do not have enough credit. Please recharge your account");
            }
        } else {
            int billingBlock = SystemProperty.getInt("CallBillingBlock", 1);
            callData.maxCallDuration = (int)Math.floor((balance - callData.signallingFee) / (callData.rate * (double)billingBlock / 60.0)) * billingBlock;
            if (callData.maxCallDuration < 1) {
                throw new EJBException("You do not have enough credit. Please recharge your account");
            }
        }
        CallData callData3 = callData;
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
        {
            return callData3;
            catch (CreateException e) {
                throw new EJBException("Call failed: " + e.getMessage());
            }
            catch (SQLException e) {
                throw new EJBException("Call failed: " + e.getMessage());
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

    /*
     * Loose catch block
     */
    public CallData initiatePhoneCall(CallData callData) throws EJBException {
        block33: {
            block32: {
                Connection conn = null;
                Statement ps = null;
                ResultSet rs = null;
                callData = this.evaluatePhoneCall(callData);
                conn = this.dataSourceMaster.getConnection();
                callData.contactID = null;
                callData.dateCreated = new Date();
                ps = conn.prepareStatement("insert into phonecall (Username, ContactID, DateCreated, Source, SourceType, SourceIDDCode, Destination, DestinationType, DestinationIDDCode, MakeReceive, InitialLeg, SignallingFee, Rate, Type, Claimable, Gateway, Status) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 1);
                ps.setString(1, callData.username);
                ps.setObject(2, callData.contactID);
                ps.setTimestamp(3, new Timestamp(callData.dateCreated.getTime()));
                ps.setString(4, callData.source);
                ps.setObject(5, callData.sourceType == null ? null : Integer.valueOf(callData.sourceType.value()));
                ps.setObject(6, callData.sourceIDDCode);
                ps.setString(7, callData.destination);
                ps.setObject(8, callData.destinationType == null ? null : Integer.valueOf(callData.destinationType.value()));
                ps.setObject(9, callData.destinationIDDCode);
                ps.setObject(10, CallData.MakeReceiveEnum.MAKE.value());
                ps.setObject(11, callData.initialLeg == null ? null : Integer.valueOf(callData.initialLeg.value()));
                ps.setDouble(12, callData.signallingFee);
                ps.setDouble(13, callData.rate);
                ps.setObject(14, callData.type == null ? null : Integer.valueOf(callData.type.value()));
                ps.setObject(15, callData.claimable == null ? Boolean.FALSE : callData.claimable);
                ps.setObject(16, callData.gateway);
                ps.setInt(17, CallData.StatusEnum.PENDING.value());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    throw new EJBException("Unable to obtain the ID of the inserted call record");
                }
                callData.id = rs.getInt(1);
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
                    break block32;
                }
                catch (SQLException e) {
                    conn = null;
                }
                break block32;
                {
                    catch (SQLException e) {
                        throw new EJBException("Unable to initiate the callback: " + e.getMessage());
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
            if (callData.isCallback() && callData.type != CallData.TypeEnum.MISSED_CALL_CALLBACK) {
                CallDataIce callDataIce = EJBIcePrxFinder.getCallMaker().requestCallback(callData.toIceObject(), callData.maxCallDuration, callData.retries);
                callData = new CallData(callDataIce);
                Object var10_14 = null;
                try {
                    this.updateCallDetail(callData);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break block33;
                {
                    catch (FusionException fe) {
                        callData.status = CallData.StatusEnum.FAILED;
                        callData.failReason = fe.message;
                        throw new EJBException("Unable to request the callback: " + fe.message);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        callData.status = CallData.StatusEnum.FAILED;
                        callData.failReason = e.getMessage();
                        if (callData.failReason == null) {
                            callData.failReason = "Exception: " + e.getClass().getName();
                        }
                        throw new EJBException("Unable to request the callback: Internal server error");
                    }
                }
                catch (Throwable throwable) {
                    Object var10_15 = null;
                    try {
                        this.updateCallDetail(callData);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    throw throwable;
                }
            }
        }
        return callData;
    }

    /*
     * Loose catch block
     */
    public CallData getCallEntryWithCost(int callEntryID) throws EJBException {
        ResultSet rs;
        Statement ps;
        Connection conn;
        block26: {
            conn = null;
            ps = null;
            rs = null;
            String sql = "select phonecall.*,accountentry.amount, accountentry.currency from phonecall,accountentry where phonecall.username = accountentry.username and accountentry.reference = ? and phonecall.status = 2 and accountentry.type = " + AccountEntryData.TypeEnum.CALL_CHARGE.value() + " " + "and phonecall.id = ?";
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, String.valueOf(callEntryID));
            ps.setInt(2, callEntryID);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            CallData callData = new CallData(rs);
            callData.amount = rs.getDouble("amount");
            callData.currency = rs.getString("currency");
            CallData callData2 = callData;
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
            return callData2;
        }
        CallData callData = null;
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
        return callData;
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

    public List<CallData> getCallEntries(String username) throws EJBException {
        return this.getCallEntries(username, null);
    }

    /*
     * Loose catch block
     */
    public List<CallData> getCallEntriesWithCost(String username) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String sql = "select phonecall.*,accountentry.amount, accountentry.currency from phonecall,accountentry where phonecall.username = accountentry.username and phonecall.id = accountentry.reference and phonecall.status = 2 and phonecall.username = ? and accountentry.type = " + AccountEntryData.TypeEnum.CALL_CHARGE.value() + " " + "order by " + "phonecall.datecreated desc";
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement(sql);
        if (username != null) {
            ps.setString(1, username);
        }
        rs = ps.executeQuery();
        LinkedList<CallData> callEntries = new LinkedList<CallData>();
        while (rs.next()) {
            CallData callData = new CallData(rs);
            callData.amount = rs.getDouble("amount");
            callData.currency = rs.getString("currency");
            callEntries.add(callData);
        }
        LinkedList<CallData> linkedList = callEntries;
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
        return linkedList;
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
     * Loose catch block
     */
    public List<CallData> getCallEntries(String username, Integer status) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String sql = "select * from phonecall";
        String criteria = "";
        if (username != null) {
            criteria = "username = ?";
        }
        if (status != null) {
            if (criteria.length() != 0) {
                criteria = criteria + " and ";
            }
            criteria = criteria + "status = " + status;
        }
        if (criteria.length() > 0) {
            sql = sql + " where " + criteria;
        }
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement(sql);
        if (username != null) {
            ps.setString(1, username);
        }
        rs = ps.executeQuery();
        LinkedList<CallData> callEntries = new LinkedList<CallData>();
        while (rs.next()) {
            callEntries.add(new CallData(rs));
        }
        LinkedList<CallData> linkedList = callEntries;
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
        return linkedList;
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

    /*
     * Loose catch block
     */
    public void updateCallDetail(CallData callData) throws EJBException {
        block15: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update phonecall set Destination=?, DestinationType=?, DestinationIDDCode=?, SignallingFee=?, Rate=?, SourceDuration=?, DestinationDuration=?, BilledDuration=?, Gateway=?, SourceProvider=?, DestinationProvider=?, FailReasonCode=?, FailReason=?, Status=? where ID=? and Status not in (?,?)");
            ps.setObject(1, callData.destination);
            ps.setObject(2, callData.destinationType == null ? null : Integer.valueOf(callData.destinationType.value()));
            ps.setObject(3, callData.destinationIDDCode);
            ps.setObject(4, callData.signallingFee);
            ps.setObject(5, callData.rate);
            ps.setObject(6, callData.sourceDuration);
            ps.setObject(7, callData.destinationDuration);
            ps.setObject(8, callData.billedDuration);
            ps.setObject(9, callData.gateway);
            ps.setObject(10, callData.sourceProvider);
            ps.setObject(11, callData.destinationProvider);
            ps.setObject(12, callData.failReasonCode);
            ps.setObject(13, callData.failReason);
            ps.setInt(14, callData.status.value());
            ps.setInt(15, callData.id);
            ps.setInt(16, CallData.StatusEnum.COMPLETED.value());
            ps.setInt(17, CallData.StatusEnum.FAILED.value());
            ps.executeUpdate();
            Object var6_4 = null;
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
                Object var6_5 = null;
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

    private void notifyCallCompletion(CallData callData, AccountEntryData accountEntryData) throws CreateException, FusionException {
        UserPrx userPrx = EJBIcePrxFinder.findUserPrx(callData.username);
        if (userPrx != null) {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            String message = null;
            message = callData.billedDuration == 0L ? misBean.getInfoText(21) : misBean.getInfoText(28);
            if (message != null) {
                String failReason = callData.failReason;
                if (failReason == null || failReason.length() == 0) {
                    failReason = "Unknown reason";
                }
                String cost = accountEntryData == null ? "0 " + userPrx.getUserData().currency : new DecimalFormat("0.00").format(-accountEntryData.amount.doubleValue()) + " " + accountEntryData.currency;
                message = message.replaceAll("%s", callData.source).replaceAll("%d", callData.destination).replaceAll("%r", failReason).replaceAll("%t", callData.billedDuration / 60L + " min " + callData.billedDuration % 60L + " sec").replaceAll("%c", cost);
                userPrx.putAlertMessage(message, null, (short)0);
            }
        }
    }

    /*
     * Loose catch block
     */
    public void chargeCall(CallData callData, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block35: {
            Connection conn = null;
            Statement ps = null;
            ResultSet rs = null;
            callData.status = CallData.StatusEnum.COMPLETED;
            if (callData.sourceDuration == null || callData.destinationDuration == null) {
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select duration, billsec from cdr where userfield = ? and (src = ? or src = ?)");
                ps.setString(1, callData.id.toString());
                ps.setString(2, callData.id.toString());
                ps.setString(3, callData.source);
                rs = ps.executeQuery();
                callData.sourceDuration = 0L;
                callData.destinationDuration = 0L;
                if (rs.next()) {
                    if (callData.initialLeg == CallData.InitialLegEnum.SOURCE) {
                        callData.sourceDuration = rs.getInt("duration");
                        callData.destinationDuration = rs.getInt("billsec");
                    } else {
                        callData.sourceDuration = rs.getInt("billsec");
                        callData.destinationDuration = rs.getInt("duration");
                    }
                }
                rs.close();
                rs = null;
                ps.close();
                ps = null;
                conn.close();
                conn = null;
            }
            if (callData.sourceDuration > 0L && callData.destinationDuration > 0L) {
                callData.billedDuration = Math.max(callData.sourceDuration, callData.destinationDuration);
                callData.failReasonCode = null;
                callData.failReason = null;
            } else {
                callData.billedDuration = 0L;
            }
            this.updateCallDetail(callData);
            int billingBlock = SystemProperty.getInt("CallBillingBlock", 1);
            double billingAmount = callData.rate * (double)billingBlock / 60.0 * Math.ceil((double)callData.billedDuration.longValue() / (double)billingBlock);
            if (callData.sourceDuration > 0L || callData.destinationDuration > 0L) {
                billingAmount += callData.signallingFee.doubleValue();
            }
            AccountEntryData accountEntryData = null;
            double wholesaleCost = 0.0;
            if (callData.sourceType == CallData.SourceDestinationTypeEnum.PSTN_PHONE && callData.sourceProvider != null) {
                wholesaleCost += this.getWholesaleCost(callData.sourceProvider, callData.sourceIDDCode, callData.source, callData.sourceDuration);
            }
            if (callData.destinationType == CallData.SourceDestinationTypeEnum.PSTN_PHONE && callData.destinationProvider != null) {
                wholesaleCost += this.getWholesaleCost(callData.destinationProvider, callData.destinationIDDCode, callData.destination, callData.destinationDuration);
            }
            if (callData.type == CallData.TypeEnum.MIDLET_ANONYMOUS_CALLBACK) {
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select username from user where mobilephone = ?");
                ps.setString(1, callData.destination);
                rs = ps.executeQuery();
                callData.destination = rs.next() ? rs.getString("username") : "unknown user";
                rs.close();
                rs = null;
                ps.close();
                ps = null;
                conn.close();
                conn = null;
            }
            if (billingAmount > 0.0 || wholesaleCost > 0.0) {
                StringBuilder description = new StringBuilder();
                description.append(callData.isCallback() ? "Callback" : "Call-through").append(" from ").append(callData.source).append(" to ").append(callData.destination);
                description.append(" (").append(callData.billedDuration / 60L).append(" min ").append(callData.billedDuration % 60L).append(" sec)");
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                accountEntryData = accountBean.chargeUserForCall(callData.username, callData.id.toString(), description.toString(), billingAmount, wholesaleCost, accountEntrySourceData);
            }
            try {
                this.notifyCallCompletion(callData, accountEntryData);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
                    throw new EJBException("Unable to charge user: " + e.getMessage());
                }
                catch (SQLException e) {
                    throw new EJBException("Unable to charge user: " + e.getMessage());
                }
            }
            catch (Throwable throwable) {
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
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private double getWholesaleCost(int providerID, int iddCode, String phoneNumber, long duration) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block25: {
            double d;
            conn = null;
            ps = null;
            rs = null;
            try {
                if (duration != 0L) break block25;
                d = 0.0;
                Object var14_10 = null;
            }
            catch (Throwable throwable) {
                Object var14_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
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
            return d;
        }
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select voicewholesalerate.rate / currency.exchangerate rate from voicewholesalerate, currency where voicewholesalerate.currency = currency.code and voicewholesalerate.providerid = ? and voicewholesalerate.iddCode = ? and (voicewholesalerate.areacode = '' or ? like concat(voicewholesalerate.iddcode, voicewholesalerate.areacode, '%')) order by voicewholesalerate.areacode desc");
        ps.setInt(1, providerID);
        ps.setInt(2, iddCode);
        ps.setString(3, phoneNumber);
        rs = ps.executeQuery();
        double rate = 0.0;
        if (rs.next()) {
            rate = rs.getDouble("rate");
        }
        double d = rate / 60.0 * (double)duration;
        Object var14_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
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
        return d;
    }

    /*
     * Loose catch block
     */
    public List<VoiceGatewayData> getVoiceGateways() throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select voicegateway.*, voiceroute.*, voiceprovider.dialcommand from voicegateway left outer join voiceroute on voicegateway.id = voiceroute.gatewayid left outer join voiceprovider on voiceroute.providerid = voiceprovider.id where voicegateway.status = ? order by voicegateway.id, voiceroute.iddcode, voiceroute.areacode, voiceroute.priority";
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, VoiceGatewayData.StatusEnum.ACTIVE.value());
        rs = ps.executeQuery();
        LinkedList<VoiceGatewayData> gateways = new LinkedList<VoiceGatewayData>();
        int previousGatewayID = Integer.MIN_VALUE;
        VoiceGatewayData gateway = null;
        while (rs.next()) {
            int gatewayID = rs.getInt("id");
            if (gatewayID != previousGatewayID) {
                gateway = new VoiceGatewayData(rs);
                gateway.voiceRoutes = new LinkedList<VoiceRouteData>();
                gateways.add(gateway);
                previousGatewayID = gatewayID;
            }
            if (rs.getInt("iddCode") == 0) continue;
            gateway.voiceRoutes.add(new VoiceRouteData(rs));
        }
        LinkedList<VoiceGatewayData> linkedList = gateways;
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
        return linkedList;
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
    public List<VoiceGatewayData> getVoiceGateways(int iddCode) throws EJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select voicegateway.* from voicegateway inner join voiceroute on voicegateway.id = voiceroute.gatewayid where voicegateway.status = ? and voiceroute.iddcode = ? and voiceroute.areacode = '' order by voiceroute.priority, rand()";
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, VoiceGatewayData.StatusEnum.ACTIVE.value());
        ps.setInt(2, iddCode);
        rs = ps.executeQuery();
        LinkedList<VoiceGatewayData> gateways = new LinkedList<VoiceGatewayData>();
        while (rs.next()) {
            gateways.add(new VoiceGatewayData(rs));
        }
        LinkedList<VoiceGatewayData> linkedList = gateways;
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
        return linkedList;
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
}

