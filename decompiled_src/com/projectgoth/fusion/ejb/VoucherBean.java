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

import com.projectgoth.fusion.bl1.BlueLabelResponseCodes;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EJBExceptionWithErrorCause;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountBalanceData;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.BlueLabelVoucherData;
import com.projectgoth.fusion.data.CallData;
import com.projectgoth.fusion.data.CurrencyData;
import com.projectgoth.fusion.data.ReputationLevelData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.VoucherBatchData;
import com.projectgoth.fusion.data.VoucherData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.VoucherLocal;
import com.projectgoth.fusion.interfaces.VoucherLocalHome;
import com.projectgoth.fusion.merchant.VoucherIssuanceUtility;
import com.projectgoth.fusion.recommendation.collector.DataCollectorUtils;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.BlueLabelServicePrx;
import com.projectgoth.fusion.slice.WebServiceResponse;
import com.sun.rowset.CachedRowSetImpl;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.security.SecureRandom;
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
public class VoucherBean
implements SessionBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(VoucherBean.class));
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private SessionContext context;

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws CreateException {
        try {
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Exception e) {
            log.error((Object)"Unable to create Voucher EJB", (Throwable)e);
            throw new CreateException("Unable to create Voucher EJB: " + e.getMessage());
        }
    }

    public void setSessionContext(SessionContext newContext) throws EJBException {
        this.context = newContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int createVoucherBatch(String username, String currency, String amountStr, int numVoucher, String notes, boolean initiallyInactive, AccountEntrySourceData accountEntrySourceData) throws EJBException, FusionEJBException {
        if (!SystemProperty.getBool(SystemPropertyEntities.Mig33Voucher.CREATE_BATCH_ENABLED)) {
            log.info((Object)String.format("[DISABLED] Failed to create voucher batch for [%s] :: %s %s, %s vouchers", username, currency, amountStr, numVoucher));
            throw new FusionEJBException("Unable to create voucher batch. Please try again later.");
        }
        try {
            int n;
            amountStr = amountStr.replaceAll(",", "").replaceAll(" ", "");
            try {
                if (!StringUtil.isValidMoneyFormat(amountStr)) {
                    log.info((Object)String.format("[Invalid Amount] Failed to create voucher batch for [%s] :: %s %s, %s vouchers", username, currency, amountStr, numVoucher));
                    throw new FusionEJBException("Please enter a valid amount (e.g. 1000, 98.5, 897.94).");
                }
            }
            catch (IllegalArgumentException e) {
                throw new FusionEJBException(e.getMessage());
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Mig33Voucher_Issuance.ENABLE_VOUCHER_ISSUANCE_CHECK)) {
                VoucherBean.voucherIssuanceEntitlementCheck(username);
            }
            BigDecimal amount = new BigDecimal(amountStr);
            double minvalue = SystemProperty.getDouble("MinVoucherValue");
            Connection conn = this.dataSourceMaster.getConnection();
            try {
                int voucherBatchID;
                PreparedStatement ps = conn.prepareStatement("select id from phonecall where username = ? and status = ?");
                try {
                    ps.setString(1, username);
                    ps.setInt(2, CallData.StatusEnum.IN_PROGRESS.value());
                    ResultSet rs = ps.executeQuery();
                    try {
                        if (rs.next()) {
                            throw new EJBException("You can't create vouchers when you have a call in progress");
                        }
                        Object var15_16 = null;
                    }
                    catch (Throwable throwable) {
                        Object var15_17 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var17_20 = null;
                }
                catch (Throwable throwable) {
                    Object var17_21 = null;
                    ps.close();
                    throw throwable;
                }
                ps.close();
                Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
                AccountBalanceData userBalance = accountEJB.getAccountBalance(username);
                double usercurrencyvalue = userBalance.currency.convert(minvalue);
                DecimalFormat df = new DecimalFormat("0.00");
                if (amount.doubleValue() < usercurrencyvalue) {
                    log.info((Object)("unable to create voucher batch, voucher value is less than minimum: amount [" + amount.doubleValue() + "] minimum [" + usercurrencyvalue + "]"));
                    throw new EJBException("Each voucher must be at least " + df.format(userBalance.currency.convert(minvalue)) + " " + userBalance.currency.code);
                }
                double totalCostInVoucherCurrency = amount.doubleValue() * (double)numVoucher;
                MISLocal misEJB = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
                CurrencyData voucherCurrency = misEJB.getCurrency(currency);
                double totalCostInUserCurrency = userBalance.currency.convertFrom(amount.doubleValue() * (double)numVoucher, voucherCurrency);
                if (userBalance.balance < totalCostInUserCurrency) {
                    throw new EJBException("Total amount required exceeds account balance. You need to recharge first or create fewer vouchers");
                }
                double minBalanceAfterTransfer = userBalance.currency.convert(SystemProperty.getDouble("MinBalanceAfterTransfer"));
                if (userBalance.balance - minBalanceAfterTransfer < totalCostInUserCurrency) {
                    throw new EJBException("You must leave at least " + df.format(minBalanceAfterTransfer) + " " + userBalance.currency.code + " in your account");
                }
                PreparedStatement ps2 = conn.prepareStatement("insert into voucherbatch (username, datecreated, currency, amount, numvoucher, notes) values (?,?,?,?,?,?)", 1);
                try {
                    ps2.setString(1, username);
                    ps2.setTimestamp(2, new Timestamp(new Date(System.currentTimeMillis()).getTime()));
                    ps2.setString(3, currency);
                    ps2.setBigDecimal(4, amount);
                    ps2.setInt(5, numVoucher);
                    if (notes != null) {
                        ps2.setString(6, notes);
                    }
                    ps2.executeUpdate();
                    ResultSet rs = ps2.getGeneratedKeys();
                    try {
                        if (!rs.next()) {
                            throw new EJBException("Failed to create Voucher Batch");
                        }
                        voucherBatchID = rs.getInt(1);
                        Object var29_32 = null;
                    }
                    catch (Throwable throwable) {
                        Object var29_33 = null;
                        rs.close();
                        throw throwable;
                    }
                    rs.close();
                    Object var31_38 = null;
                }
                catch (Throwable throwable) {
                    Object var31_39 = null;
                    ps2.close();
                    throw throwable;
                }
                ps2.close();
                SecureRandom random = new SecureRandom();
                PreparedStatement ps3 = conn.prepareStatement("insert into voucher (VoucherBatchID, Number, Status) values (?,?,?)");
                try {
                    for (int i = 0; i < numVoucher; ++i) {
                        do {
                            ps3.setInt(1, voucherBatchID);
                            ps3.setString(2, "" + (Math.abs(random.nextLong()) % 9000000000L + 1000000000L));
                            if (initiallyInactive) {
                                ps3.setInt(3, VoucherData.StatusEnum.INACTIVE.value());
                                continue;
                            }
                            ps3.setInt(3, VoucherData.StatusEnum.ACTIVE.value());
                        } while (ps3.executeUpdate() != 1);
                    }
                    Object var33_41 = null;
                }
                catch (Throwable throwable) {
                    Object var33_42 = null;
                    ps3.close();
                    throw throwable;
                }
                ps3.close();
                AccountEntryData accEntry = new AccountEntryData();
                accEntry.username = username;
                accEntry.type = AccountEntryData.TypeEnum.VOUCHERS_CREATED;
                accEntry.reference = String.valueOf(voucherBatchID);
                accEntry.description = "Voucher Creation - " + username + " created " + df.format(totalCostInVoucherCurrency) + " " + currency + " worth of vouchers";
                accEntry.amount = -totalCostInVoucherCurrency;
                accEntry.currency = currency;
                accEntry.tax = 0.0;
                AccountLocal accEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                accEJB.createAccountEntry(null, accEntry, accountEntrySourceData);
                n = voucherBatchID;
                Object var35_44 = null;
            }
            catch (Throwable throwable) {
                Object var35_45 = null;
                conn.close();
                throw throwable;
            }
            conn.close();
            return n;
        }
        catch (Exception e) {
            if (e instanceof FusionEJBException) {
                log.warn((Object)("Failed to create Voucher Batch. Exception:" + e));
                throw (FusionEJBException)e;
            }
            if (e instanceof EJBException) {
                throw (EJBException)((Object)e);
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            String errorID = DataCollectorUtils.newErrorID();
            log.error((Object)("[" + errorID + "]" + "Failed to create Voucher Batch.Exception:" + e), (Throwable)e);
            throw new EJBException("Failed to create Voucher Batch. ErrorID:[" + errorID + "]");
        }
    }

    private static void voucherIssuanceEntitlementCheck(String issuerUsername) throws FusionEJBException, CreateException, IOException {
        VoucherIssuanceUtility voucherUtility = VoucherIssuanceUtility.getInstance();
        if (voucherUtility.userTypeCheckEnabled()) {
            UserLocal user = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = user.loadUser(issuerUsername, false, false);
            UserData.TypeEnum userType = userData.type;
            if (!voucherUtility.userTypeAllowedToIssueVoucher(userType)) {
                throw new FusionEJBException(voucherUtility.getDisallowedUserTypeErrorMessage(userType));
            }
        }
        if (voucherUtility.migLevelCheckEnabled() && VoucherBean.getMigLevel(issuerUsername) < voucherUtility.getMinMigLevel()) {
            throw new FusionEJBException(voucherUtility.getInsufficientMigLevelErrorMessage());
        }
    }

    private static int getMigLevel(String username) throws CreateException, EJBException {
        boolean skipCachedScore;
        UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
        ReputationLevelData reputation = userBean.getReputationLevel(username, skipCachedScore = SystemProperty.getBool(SystemPropertyEntities.Mig33Voucher_Issuance.SKIP_FETCH_CACHED_REPUTATION_SCORE));
        int senderReputationLevel = reputation != null && reputation.level != null ? reputation.level : 1;
        return senderReputationLevel;
    }

    /*
     * Loose catch block
     */
    public VoucherBatchData getVoucherBatch(int voucherBatchID) throws FusionEJBException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("SELECT * FROM voucherbatch WHERE id = ?");
        ps.setInt(1, voucherBatchID);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Voucher batch " + voucherBatchID + "does not exist.");
        }
        VoucherBatchData voucherBatchData = new VoucherBatchData(rs);
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
        return voucherBatchData;
        catch (SQLException e) {
            try {
                log.error((Object)"Unable to retrieve voucher batch", (Throwable)e);
                throw new FusionEJBException("Unable to retrieve voucher batch with id " + voucherBatchID);
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
    public void cancelVoucherBatch(String username, int voucherBatchId, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block27: {
            Connection conn = null;
            ResultSet rs = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select * from voucherbatch where username = ? and id = ?");
            ps.setString(1, username);
            ps.setInt(2, voucherBatchId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("You can't cancel vouchers that were not created by you");
            }
            VoucherBatchData vbEntry = new VoucherBatchData(rs);
            ps = conn.prepareStatement("update voucher set status = ?, lastupdated = ? where VoucherBatchID = ? and status in (?, ?)");
            ps.setInt(1, VoucherData.StatusEnum.CANCELLED.value());
            ps.setTimestamp(2, new Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setInt(3, voucherBatchId);
            ps.setInt(4, VoucherData.StatusEnum.ACTIVE.value());
            ps.setInt(5, VoucherData.StatusEnum.INACTIVE.value());
            int numRowsUpdated = ps.executeUpdate();
            if (numRowsUpdated < 1) {
                throw new EJBException("The status of these vouchers are already cancelled");
            }
            ps = conn.prepareStatement("update voucherbatch set notes = ? where ID = ?");
            if (vbEntry.notes == null) {
                ps.setString(1, "CANCELLED BATCH ");
            } else {
                ps.setString(1, vbEntry.notes + " | CANCELLED BATCH ");
            }
            ps.setInt(2, vbEntry.id);
            if (ps.executeUpdate() < 1) {
                throw new EJBException("Could not update notes field");
            }
            double amountToRefund = (double)numRowsUpdated * vbEntry.amount;
            Account accEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            AccountEntryData oldEntry = accEJB.getAccountEntryFromReference(AccountEntryData.TypeEnum.VOUCHERS_CREATED, vbEntry.id.toString());
            if (oldEntry == null) {
                throw new EJBException("Could not retrieve original account entry for batch creation");
            }
            AccountEntryData accEntry = new AccountEntryData();
            DecimalFormat df = new DecimalFormat("0.00");
            accEntry.username = username;
            accEntry.type = AccountEntryData.TypeEnum.VOUCHERS_CANCELLED;
            accEntry.reference = String.valueOf(vbEntry.id);
            accEntry.description = "Voucher Batch Cancellation - " + username + " refunded for " + df.format(amountToRefund) + " " + vbEntry.currency + " worth of vouchers";
            accEntry.amount = amountToRefund;
            accEntry.fundedAmount = accEntry.amount * oldEntry.fundedAmount / oldEntry.amount;
            accEntry.currency = vbEntry.currency;
            accEntry.tax = 0.0;
            accEJB.createAccountEntry(null, accEntry, accountEntrySourceData);
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
                break block27;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block27;
            {
                catch (Exception e) {
                    throw new EJBException("Failed to cancel Voucher Batch: " + e.getMessage());
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
    public void cancelVoucher(String username, int voucherId, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        block26: {
            Connection conn = null;
            ResultSet rs = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select * from voucher where id = ?");
            ps.setInt(1, voucherId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Invalid Voucher ID");
            }
            VoucherData vEntry = new VoucherData(rs);
            ps = conn.prepareStatement("select * from voucherbatch where id = ?");
            ps.setInt(1, vEntry.voucherBatchID);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new EJBException("Internal Error");
            }
            VoucherBatchData vbEntry = new VoucherBatchData(rs);
            if (!vbEntry.username.equalsIgnoreCase(username)) {
                throw new EJBException("You can't cancel a voucher that was not created by you");
            }
            ps = conn.prepareStatement("update voucher set status = ?, LastUpdated = ? where ID = ? and status in (?,?)");
            ps.setInt(1, VoucherData.StatusEnum.CANCELLED.value());
            ps.setTimestamp(2, new Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setInt(3, vEntry.id);
            ps.setInt(4, VoucherData.StatusEnum.ACTIVE.value());
            ps.setInt(5, VoucherData.StatusEnum.INACTIVE.value());
            int numRowsUpdated = ps.executeUpdate();
            if (numRowsUpdated < 1) {
                throw new EJBException("The voucher has already been cancelled");
            }
            double amountToRefund = vbEntry.amount;
            Account accEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            AccountEntryData oldEntry = accEJB.getAccountEntryFromReference(AccountEntryData.TypeEnum.VOUCHERS_CREATED, vbEntry.id.toString());
            if (oldEntry == null) {
                throw new EJBException("Could not retrieve original account entry for batch creation");
            }
            AccountEntryData accEntry = new AccountEntryData();
            DecimalFormat df = new DecimalFormat("0.00");
            accEntry.username = username;
            accEntry.type = AccountEntryData.TypeEnum.VOUCHERS_CANCELLED;
            accEntry.reference = String.valueOf(vbEntry.id);
            accEntry.description = "Voucher Cancellation - " + username + " refunded for " + df.format(amountToRefund) + " " + vbEntry.currency;
            accEntry.amount = amountToRefund;
            accEntry.fundedAmount = accEntry.amount * oldEntry.fundedAmount / oldEntry.amount;
            accEntry.currency = vbEntry.currency;
            accEntry.tax = 0.0;
            accEJB.createAccountEntry(null, accEntry, accountEntrySourceData);
            Object var17_16 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
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
                catch (Exception e) {
                    throw new EJBException("Failed to cancel Voucher: " + e.getMessage());
                }
            }
            catch (Throwable throwable) {
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
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    public BlueLabelVoucherData redeemBlueLabelVoucher(String username, String voucherId, String voucherValue) throws EJBException {
        try {
            BlueLabelServicePrx blueLabelServicePrx = EJBIcePrxFinder.getBlueLabelService();
            BlueLabelOneVoucher oneVoucher = new BlueLabelOneVoucher();
            oneVoucher.number = voucherId;
            oneVoucher.value = voucherValue;
            oneVoucher.amountRedeemed = voucherValue;
            WebServiceResponse authResponse = blueLabelServicePrx.authenticate(username);
            if (BlueLabelResponseCodes.fromValue(authResponse.responseCode) == BlueLabelResponseCodes.SUCCESS) {
                WebServiceResponse webResponse = blueLabelServicePrx.fullVoucherRedemption(username, authResponse.responseData, oneVoucher);
                System.out.println("BL CODE: " + webResponse.responseCode);
                System.out.println("BL DATA: " + webResponse.responseData);
                if (webResponse.responseCode == BlueLabelResponseCodes.SUCCESS.value()) {
                    try {
                        AccountLocal accEJB = (AccountLocal)EJBHomeCache.getObject("AccountLocal", AccountLocalHome.class);
                        double amount = Double.valueOf(oneVoucher.amountRedeemed);
                        accEJB.sendRechargeCreditRewardProgramTrigger(username, amount, oneVoucher.currency, 0);
                    }
                    catch (Exception e) {
                        log.warn((Object)("Unable to send recharge credit reward program trigger for user: " + username));
                    }
                }
                return new BlueLabelVoucherData(webResponse.responseCode, webResponse.responseData);
            }
            return new BlueLabelVoucherData(authResponse.responseCode, authResponse.responseData);
        }
        catch (Exception e) {
            log.error((Object)"failed to redeem bl1 voucher", (Throwable)e);
            throw new EJBException(e.getMessage());
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public VoucherData redeemVoucher(String username, String number, AccountEntrySourceData accountEntrySourceData) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = this.dataSourceMaster.getConnection();
        ps = conn.prepareStatement("select Type, MobileVerified, if(timestampdiff(HOUR, LastFailedVoucherRecharge, now()) <= 24, FailedVoucherRecharges, 0) FailedVoucherRechargesInLast24Hours from user where username = ?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("Invalid username " + username);
        }
        boolean allowEmailVerifiedUser = SystemProperty.getBool(SystemPropertyEntities.Mig33Voucher_Redemption.ALLOW_EMAIL_VERIFIED_USER);
        if (rs.getInt("MobileVerified") == 0 && rs.getInt("Type") != UserData.TypeEnum.MIG33_PREPAID_CARD.value()) {
            if (allowEmailVerifiedUser) {
                UserLocal userLocal = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                boolean isEmailOrMobileVerified = userLocal.isUserEmailVerifiedWithTxSupport(username);
                if (!isEmailOrMobileVerified) {
                    throw new EJBExceptionWithErrorCause(ErrorCause.Mig33VoucherRedemptionErrorReasonType.USER_NOT_VERIFIED, new Object[0]);
                }
            } else {
                throw new EJBExceptionWithErrorCause(ErrorCause.Mig33VoucherRedemptionErrorReasonType.USER_NOT_VERIFIED, new Object[0]);
            }
        }
        if (rs.getInt("FailedVoucherRechargesInLast24Hours") > 0) {
            int maxFails = SystemProperty.getInt("MaxVoucherFailsPerDay");
            if (rs.getInt("FailedVoucherRechargesInLast24Hours") >= maxFails) {
                throw new EJBException("Too many invalid numbers entered. Try again in 24 hours");
            }
        }
        rs.close();
        ps.close();
        ps = conn.prepareStatement("select voucher.*, voucherbatch.currency, voucherbatch.amount, user.status userstatus from voucherbatch, voucher, user where voucherbatch.id = voucher.voucherbatchid and voucherbatch.username = user.username and voucher.number = ?");
        ps.setString(1, number);
        rs = ps.executeQuery();
        if (!rs.next()) {
            VoucherLocal voucherEJB = (VoucherLocal)EJBHomeCache.getLocalObject("VoucherLocal", VoucherLocalHome.class);
            voucherEJB.voucherRechargeFailed(username);
            throw new EJBException("Voucher number does not exist");
        }
        if (rs.getInt("userstatus") != UserData.StatusEnum.ACTIVE.value()) {
            throw new EJBException("Voucher creator's account is suspended");
        }
        int voucherStatus = rs.getInt("status");
        if (voucherStatus != VoucherData.StatusEnum.ACTIVE.value()) {
            if (voucherStatus == VoucherData.StatusEnum.CANCELLED.value()) {
                throw new EJBException("Voucher has been canceled");
            }
            if (voucherStatus == VoucherData.StatusEnum.REDEEMED.value()) {
                throw new EJBException("Voucher has already been used");
            }
            if (voucherStatus == VoucherData.StatusEnum.INACTIVE.value()) {
                throw new EJBException("Voucher number is not valid");
            }
            throw new EJBException("Voucher is no longer valid");
        }
        VoucherData voucherData = new VoucherData();
        voucherData.id = rs.getInt("id");
        voucherData.voucherBatchID = rs.getInt("voucherbatchid");
        voucherData.number = number;
        voucherData.lastUpdated = new Date();
        voucherData.status = VoucherData.StatusEnum.REDEEMED;
        voucherData.notes = "Redeemed by " + username;
        voucherData.amount = rs.getDouble("amount");
        voucherData.currency = rs.getString("currency");
        rs.close();
        ps.close();
        ps = conn.prepareStatement("select voucher.number from user usercreator, user userredeemer, voucherbatch, voucher, country where voucher.voucherbatchid=voucherbatch.id and voucherbatch.username=usercreator.username and voucher.number=? and userredeemer.username=? and usercreator.countryid=country.id and (country.allowusertransfertoothercountry=1 or (usercreator.countryid=userredeemer.countryid))");
        ps.setString(1, number);
        ps.setString(2, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("This voucher can not currently be used in your country. Please contact customer service for assistance");
        }
        rs.close();
        ps.close();
        ps = conn.prepareStatement("select voucher.number from user usercreator, user userredeemer, voucherbatch, voucher, country where voucher.voucherbatchid=voucherbatch.id and voucherbatch.username=usercreator.username and voucher.number=? and userredeemer.username=? and userredeemer.countryid=country.id and (country.allowusertransferfromothercountry=1 or (usercreator.countryid=userredeemer.countryid))");
        ps.setString(1, number);
        ps.setString(2, username);
        rs = ps.executeQuery();
        if (!rs.next()) {
            throw new EJBException("This voucher may not be redeemed in your country at this time. Please contact customer service for assistance");
        }
        rs.close();
        ps.close();
        ps = conn.prepareStatement("update voucher set status = ?, lastupdated = ?, notes = ? where ID = ? and status = ?");
        ps.setInt(1, voucherData.status.value());
        ps.setTimestamp(2, new Timestamp(voucherData.lastUpdated.getTime()));
        ps.setString(3, voucherData.notes);
        ps.setInt(4, voucherData.id);
        ps.setInt(5, VoucherData.StatusEnum.ACTIVE.value());
        if (ps.executeUpdate() != 1) {
            throw new EJBException("Failed to redeem voucher. The voucher cannot be found or is not active.");
        }
        Account accEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
        AccountEntryData oldEntry = accEJB.getAccountEntryFromReference(AccountEntryData.TypeEnum.VOUCHERS_CREATED, voucherData.voucherBatchID.toString());
        if (oldEntry == null) {
            throw new EJBException("Could not retrieve original account entry for batch creation");
        }
        AccountEntryData accEntry = new AccountEntryData();
        DecimalFormat df = new DecimalFormat("0.00");
        accEntry.username = username;
        accEntry.type = AccountEntryData.TypeEnum.VOUCHER_RECHARGE;
        accEntry.reference = String.valueOf(voucherData.id);
        accEntry.description = "Voucher/Prepaid Card Recharge of " + df.format(voucherData.amount) + " " + voucherData.currency;
        accEntry.amount = voucherData.amount;
        accEntry.fundedAmount = accEntry.amount * oldEntry.fundedAmount / oldEntry.amount;
        accEntry.currency = voucherData.currency;
        accEntry.tax = 0.0;
        accEJB.createAccountEntry(null, accEntry, accountEntrySourceData);
        ps.close();
        ps = conn.prepareStatement("update user set FailedVoucherRecharges = 0 where username = ?");
        ps.setString(1, username);
        ps.executeUpdate();
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)"accountBean.sendRechargeCreditRewardProgramTrigger()");
            }
            accEJB.sendRechargeCreditRewardProgramTrigger(username, voucherData.amount, voucherData.currency, 0);
        }
        catch (Exception e) {
            log.warn((Object)("Unable to send recharge credit reward program trigger for user: " + username));
        }
        VoucherData voucherData2 = voucherData;
        Object var16_21 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
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
            return voucherData2;
            catch (EJBException e) {
                log.warn((Object)("Unable to redeem voucher(username=[" + username + "] voucher=[" + number + "].Exception:[" + (Object)((Object)e) + "]"), (Throwable)e);
                throw e;
            }
            catch (Exception e) {
                log.error((Object)("Unhandled exception on redeeming voucher(username=[" + username + "] voucher=[" + number + "].Exception:[" + e + "]"), (Throwable)e);
                throw new EJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var16_22 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public void voucherRechargeFailed(String username) throws EJBException {
        block19: {
            Connection conn = null;
            PreparedStatement ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("update user set FailedVoucherRecharges = FailedVoucherRecharges + 1, LastFailedVoucherRecharge = now() where username = ?");
            ps.setString(1, username);
            ps.executeUpdate();
            Object var6_4 = null;
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
                    Object var6_5 = null;
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
    public List<VoucherData> retrieveVouchers(String username, int batchid, int type) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        Statement ps = null;
        LinkedList<VoucherData> voucherList = null;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select voucher.id, voucher.voucherbatchid, voucher.number, voucher.lastupdated, voucher.status, voucher.notes from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid =  voucherbatch.id ";
        if (batchid != 0) {
            sql = sql + " and voucher.voucherbatchid = ? ";
        }
        if (type != 0) {
            sql = sql + " and voucher.status = ? ";
        }
        sql = sql + "order by voucher.lastupdated desc";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        if (batchid != 0) {
            ps.setInt(2, batchid);
        }
        if (type != 0) {
            if (batchid != 0) {
                ps.setInt(3, type);
            } else {
                ps.setInt(2, type);
            }
        }
        if ((rs = ps.executeQuery()).next()) {
            voucherList = new LinkedList<VoucherData>();
            do {
                voucherList.add(new VoucherData(rs));
            } while (rs.next());
        }
        LinkedList<VoucherData> linkedList = voucherList;
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
        catch (Exception e) {
            try {
                throw new EJBException("Retrieve Vouchers failed: " + e.getMessage());
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
    public VoucherData searchForVoucher(String username, String vouchernumber) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        VoucherData vEntry = null;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select voucher.id, voucher.voucherbatchid, voucher.number, voucher.status, voucher.lastupdated, voucher.notes from voucherbatch, voucher where voucherbatch.username = ? and voucher.voucherbatchid = voucherbatch.id and voucher.number = ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, vouchernumber);
        rs = ps.executeQuery();
        if (rs.next()) {
            vEntry = new VoucherData(rs);
        }
        VoucherData voucherData = vEntry;
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
        return voucherData;
        catch (Exception e) {
            try {
                throw new EJBException("Search Voucher failed: " + e.getMessage());
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
    public VoucherData getVoucher(String voucherNumber) throws EJBException {
        VoucherData voucherData;
        PreparedStatement ps;
        ResultSet rs;
        Connection conn;
        block26: {
            conn = null;
            rs = null;
            ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select * from voucher where number = ?");
            ps.setString(1, voucherNumber);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            voucherData = new VoucherData(rs);
            rs.close();
            ps.close();
            ps = conn.prepareStatement("select * from voucherbatch where id = ?");
            ps.setInt(1, voucherData.voucherBatchID);
            rs = ps.executeQuery();
            if (!rs.next()) break block26;
            voucherData.amount = rs.getDouble("amount");
            voucherData.currency = rs.getString("currency");
            VoucherData voucherData2 = voucherData;
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
            return voucherData2;
        }
        voucherData = null;
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
        return voucherData;
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
    public List<VoucherBatchData> retrieveVoucherBatches(String username, int id) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        Statement ps = null;
        LinkedList<VoucherBatchData> voucherBatchList = null;
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
        if ((rs = ps.executeQuery()).next()) {
            voucherBatchList = new LinkedList<VoucherBatchData>();
            do {
                VoucherBatchData vbEntry = new VoucherBatchData(rs);
                vbEntry.num_active = rs.getInt("active");
                vbEntry.num_cancelled = rs.getInt("cancelled");
                vbEntry.num_redeemed = rs.getInt("redeemed");
                vbEntry.num_expired = rs.getInt("expired");
                voucherBatchList.add(vbEntry);
            } while (rs.next());
        }
        LinkedList<VoucherBatchData> linkedList = voucherBatchList;
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
        return linkedList;
        catch (Exception e) {
            try {
                throw new EJBException("Retrieve Voucher Batch failed: " + e.getMessage());
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
    public VoucherData activateVoucher(String username) throws EJBException {
        PreparedStatement ps;
        ResultSet rs;
        Connection conn;
        block27: {
            conn = null;
            rs = null;
            ps = null;
            conn = this.dataSourceMaster.getConnection();
            ps = conn.prepareStatement("select voucher.*, voucherbatch.currency, voucherbatch.amount from voucherbatch, voucher where voucherbatch.id = voucher.voucherbatchid and voucherbatch.username = ? and status = ? limit 1");
            ps.setString(1, username);
            ps.setInt(2, VoucherData.StatusEnum.INACTIVE.value());
            rs = ps.executeQuery();
            if (rs.next()) break block27;
            VoucherData voucherData = null;
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
            return voucherData;
        }
        VoucherData voucherData = new VoucherData(rs);
        voucherData.amount = rs.getDouble("amount");
        voucherData.currency = rs.getString("currency");
        rs.close();
        ps.close();
        ps = conn.prepareStatement("update voucher set status = ?, lastupdated = ? where id = ?");
        ps.setInt(1, VoucherData.StatusEnum.ACTIVE.value());
        ps.setTimestamp(2, new Timestamp(new Date(System.currentTimeMillis()).getTime()));
        ps.setInt(3, voucherData.id);
        if (ps.executeUpdate() != 1) {
            throw new EJBException("Unable to activate voucher " + voucherData.number);
        }
        VoucherData voucherData2 = voucherData;
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
        return voucherData2;
        catch (SQLException e) {
            try {
                throw new EJBException((Exception)e);
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
    public CachedRowSetImpl affiliateOverview(String username) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select sum(amount) AS 'Amount',Currency , sum(Number) As 'TotalVouchers', sum(activevouchers) AS 'ActiveVouchers', sum(activeamount) AS 'ActivetotalAmount', sum(cancelledvouchers) AS 'CancelledVouchers', sum(cancelledamount) AS 'CancelledtotalAmount', sum(redeemedvouchers) AS 'RedeemedVouchers', sum(redeemedamount) AS 'redeemedtotalAmount', sum(expiredvouchers) AS 'ExpiredVouchers', sum(expiredamount) AS 'ExpiredtotalAmount' from ( select (voucherbatch.amount * NumVoucher) AS Amount, voucherbatch.currency AS Currency, count(voucher.id) AS Number, count(case when voucher.status = 1 then 1 end) AS activevouchers, sum(case when voucher.status = 1 then voucherbatch.amount end) AS activeamount, count(case when voucher.status = 2 then 1 end) AS cancelledvouchers, sum(case when voucher.status = 2 then voucherbatch.amount end) AS cancelledamount, count(case when voucher.status = 3 then 1 end) AS redeemedvouchers, sum(case when voucher.status = 3 then voucherbatch.amount end) AS redeemedamount, count(case when voucher.status = 4 then 1 end) AS expiredvouchers, sum(case when voucher.status = 4 then voucherbatch.amount end) AS expiredamount from  voucherbatch, voucher where  voucherbatch.id = voucher.VoucherBatchID and voucherbatch.username = ? group by voucherbatch.id ) batch group by Currency";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        rs = ps.executeQuery();
        CachedRowSetImpl crs = new CachedRowSetImpl();
        crs.populate(rs);
        CachedRowSetImpl cachedRowSetImpl = crs;
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
        return cachedRowSetImpl;
        catch (Exception e) {
            try {
                throw new EJBException("Affiliate Overview generation failed: " + e.getMessage());
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
    public CachedRowSetImpl recentActivities(String username, int limit) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select v.id,v.voucherbatchid,v.number,v.lastupdated,v.status,v.notes from voucherbatch vb,voucher v where vb.id = v.voucherbatchid and vb.username = ? and v.lastupdated is not null order by v.lastupdated desc limit ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ps.setInt(2, limit);
        rs = ps.executeQuery();
        CachedRowSetImpl crs = new CachedRowSetImpl();
        crs.populate(rs);
        CachedRowSetImpl cachedRowSetImpl = crs;
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
        return cachedRowSetImpl;
        catch (Exception e) {
            try {
                throw new EJBException("Recent activity generation failed: " + e.getMessage());
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
    public int recentRedeem(String username, int days) throws EJBException {
        Connection conn = null;
        ResultSet rs = null;
        Statement ps = null;
        conn = this.dataSourceMaster.getConnection();
        String sql = "select count(*) from voucher v,voucherbatch vb where v.voucherbatchid = vb.id and vb.username = ? and v.status = 3 ";
        sql = days == 0 ? sql + "and v.lastupdated >= CURDATE() " : sql + "and v.lastupdated > subdate(CURRENT_DATE(), ?) ";
        sql = sql + "order by v.lastupdated desc";
        ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        if (days > 0) {
            ps.setInt(2, days);
        }
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
        catch (Exception e) {
            try {
                throw new EJBException("Recent activity generation failed: " + e.getMessage());
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

