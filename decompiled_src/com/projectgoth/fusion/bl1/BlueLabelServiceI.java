/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  javax.ejb.CreateException
 *  javax.xml.rpc.ServiceException
 *  mibli.Mibli
 *  mibli.MibliService
 *  mibli.MibliServiceLocator
 *  mibli.Response
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.annotation.Required
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.bl1;

import Ice.Current;
import com.projectgoth.fusion.bl1.BlueLabelError;
import com.projectgoth.fusion.bl1.BlueLabelResponseCodes;
import com.projectgoth.fusion.bl1.BlueLabelService;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.common.PasswordGenerator;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.dao.CountryDAO;
import com.projectgoth.fusion.dao.LiveIdCredentialDAO;
import com.projectgoth.fusion.data.AccountEntryData;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.domain.LiveIdCredential;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Account;
import com.projectgoth.fusion.interfaces.AccountHome;
import com.projectgoth.fusion.slice.BlueLabelOneVoucher;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.WebServiceResponse;
import com.projectgoth.fusion.slice._BlueLabelServiceDisp;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import javax.ejb.CreateException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import mibli.Mibli;
import mibli.MibliService;
import mibli.MibliServiceLocator;
import mibli.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

public class BlueLabelServiceI
extends _BlueLabelServiceDisp {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BlueLabelServiceI.class));
    static final int KEEP_ERRORS_FOR_HOURS = 6;
    private LiveIdCredentialDAO liveIdCredentialDAO;
    private CountryDAO countryDAO;
    private String webServiceKey;
    private String mibliUsername;
    private String mibliPassword;
    private String mibliWSDLURL;
    private long serviceKeyUpdateInterval = 82800000L;
    private MibliService serviceLocator;
    private boolean createAccountEntries;
    private PasswordGenerator passwordGenerator = new PasswordGenerator();
    private LinkedList<BlueLabelError> recentErrors = new LinkedList();

    public void configureService() throws Exception {
        log.info((Object)"initialising web service");
        this.serviceLocator = new MibliServiceLocator(this.mibliWSDLURL, new QName("urn:mibli", "MibliService"));
        log.info((Object)("WSDL location [" + this.serviceLocator.getWSDLDocumentLocation() + "] "));
        this.initializeSession();
        log.info((Object)("Web Service has been initialized, web service key is [" + this.webServiceKey + "]"));
        log.info((Object)("Account Entry creation [" + this.createAccountEntries + "]"));
        this.passwordGenerator.setAlphabet(PasswordGenerator.NONCONFUSING_ALPHABET);
        this.passwordGenerator.setFirstAlphabet(null);
        this.passwordGenerator.setLastAlphabet(null);
        this.passwordGenerator.setMaxRepetition(0);
        UpdateWebserviceKeyThread task = new UpdateWebserviceKeyThread(this);
        Timer timer = new Timer(true);
        timer.schedule((TimerTask)task, this.serviceKeyUpdateInterval, this.serviceKeyUpdateInterval);
    }

    public void shutdown() {
    }

    public String initializeSession() throws FusionException {
        try {
            Mibli stub = this.serviceLocator.getmibli();
            Response ticket = stub.initialize(this.mibliUsername, this.mibliPassword);
            this.webServiceKey = ticket.getResponseData();
            return this.webServiceKey;
        }
        catch (ServiceException e) {
            log.error((Object)"failed to contact mibli", (Throwable)e);
            throw new FusionException("failed to initialize session");
        }
        catch (RemoteException e) {
            log.error((Object)"failed to contact mibli", (Throwable)e);
            throw new FusionException("failed to initialize session");
        }
    }

    public WebServiceResponse registerAccount(String username, String password, int countryCode, String mobileNumber, int secretQuestionCode, String secretQuestionAnswer, String firstName, String lastName, String nickName, String dateOfBirth, String sex, String emailAddress, Current __current) throws FusionException {
        try {
            Mibli stub;
            Response response;
            if (log.isDebugEnabled()) {
                log.debug((Object)("attempting to register user with live id [" + username + "] p [" + password + "] country code [" + countryCode + "] mobile number [" + mobileNumber + "] question [" + secretQuestionCode + "] secretQuestionAnswer [" + secretQuestionAnswer + "] first name [" + firstName + "] last name [" + lastName + "] nick name [" + nickName + "] date of birth [" + dateOfBirth + "] sex [" + sex + "] email [" + emailAddress + "]"));
            }
            if ((response = (stub = this.serviceLocator.getmibli()).registerNewAccountSync(this.webServiceKey, username, password, countryCode, mobileNumber, secretQuestionCode, secretQuestionAnswer, firstName, lastName, nickName, dateOfBirth, sex, emailAddress)).getResponseCode() == 0) {
                log.info((Object)("account registration response data [" + response.getResponseData() + "] for username [" + username + "]"));
            }
            return new WebServiceResponse(response.getResponseData(), response.getResponseCode());
        }
        catch (ServiceException e) {
            log.error((Object)"failed to register account synchronously", (Throwable)e);
            throw new FusionException("failed to register account syncronously");
        }
        catch (RemoteException e) {
            log.error((Object)"failed to register account synchronously", (Throwable)e);
            throw new FusionException("failed to register account syncronously");
        }
    }

    public WebServiceResponse getAccountStatus(String username, Current __current) throws FusionException {
        try {
            LiveIdCredential liveIdCredential = this.liveIdCredentialDAO.getCredential(username);
            if (liveIdCredential != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("getting account status, found live id [" + liveIdCredential.getLiveId() + " for username [" + username + "]"));
                }
                Mibli stub = this.serviceLocator.getmibli();
                Response response = stub.getStatus(this.webServiceKey, liveIdCredential.getLiveId());
                if (log.isDebugEnabled()) {
                    log.debug((Object)("account status for username [" + username + "] and live id [" + liveIdCredential.getLiveId() + "] response code [" + response.getResponseCode() + "] and response data [" + response.getResponseData() + "]"));
                }
                return new WebServiceResponse(response.getResponseData(), response.getResponseCode());
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("no live id found for username [" + username + "]"));
            }
            return new WebServiceResponse(null, BlueLabelResponseCodes.USER_ACCOUNT_DOES_NOT_EXIST.value());
        }
        catch (ServiceException e) {
            log.error((Object)"failed to get account status", (Throwable)e);
            throw new FusionException("failed to get account status");
        }
        catch (RemoteException e) {
            log.error((Object)"failed to get account status", (Throwable)e);
            throw new FusionException("failed to get account status");
        }
    }

    private String liveIdForUsername(String username) {
        return HashUtils.asHex(HashUtils.md5(username));
    }

    public WebServiceResponse authenticate(String username, Current __current) throws FusionException {
        if (!StringUtils.hasLength((String)username)) {
            log.error((Object)"supplied username was empty");
            throw new FusionException("supplied username was empty");
        }
        try {
            Object response;
            LiveIdCredential liveIdCredential = this.liveIdCredentialDAO.getCredential(username);
            if (liveIdCredential == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("creating new live id credential for username [" + username + "]"));
                }
                liveIdCredential = new LiveIdCredential(username, this.liveIdForUsername(username), this.passwordGenerator.getPass(10));
                CountryData countryData = this.countryDAO.getCountryForUser(username);
                response = this.registerAccount(liveIdCredential.getLiveId(), liveIdCredential.getPassword(), countryData.iddCode, "1234567890", 0, "maidenname", "", "", "", "", "", "");
                if (response.responseCode == BlueLabelResponseCodes.SUCCESS.value()) {
                    liveIdCredential.setLiveId(response.responseData);
                    this.liveIdCredentialDAO.persistCredential(liveIdCredential);
                } else {
                    log.error((Object)("failed to register live id for username [" + username + "], response [" + (Object)((Object)BlueLabelResponseCodes.fromValue(response.responseCode)) + "]"));
                    throw new FusionException("failed to register account with mibli");
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("authenticating live id [" + liveIdCredential.getLiveId() + "] for username [" + username + "]"));
            }
            Mibli stub = this.serviceLocator.getmibli();
            response = stub.authenticate(this.webServiceKey, liveIdCredential.getLiveId(), liveIdCredential.getPassword());
            return new WebServiceResponse(response.getResponseData(), response.getResponseCode());
        }
        catch (ServiceException e) {
            log.error((Object)("failed to authenticate user [" + username + "] with mibli"), (Throwable)e);
            throw new FusionException("failed to authenticate with mibli");
        }
        catch (RemoteException e) {
            log.error((Object)("failed to authenticate user [" + username + "] with mibli"), (Throwable)e);
            throw new FusionException("failed to authenticate with mibli");
        }
    }

    public static String blueLabelOneVoucherToString(BlueLabelOneVoucher voucher) {
        StringBuilder stringBuilder = new StringBuilder("voucher: number [");
        stringBuilder.append(voucher.number).append("] amount redeemed [").append(voucher.amountRedeemed).append("] total value [").append(voucher.value).append("] currency [").append(voucher.currency).append("] transaction reference [").append(voucher.transactionReference).append("]");
        return stringBuilder.toString();
    }

    private void createAccountEntry(String username, BlueLabelOneVoucher voucher) throws Exception {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("user [" + username + "] redeemed " + BlueLabelServiceI.blueLabelOneVoucherToString(voucher)));
            }
            Account accountEJB = (Account)EJBHomeCache.getObject("ejb/Account", AccountHome.class);
            AccountEntryData accountEntry = new AccountEntryData();
            Double amount = Double.valueOf(voucher.amountRedeemed);
            log.debug((Object)("voucher amount for account entry " + amount));
            DecimalFormat df = new DecimalFormat("0.00");
            accountEntry.username = username;
            accountEntry.type = AccountEntryData.TypeEnum.BLUE_LABEL_ONE_VOUCHER;
            accountEntry.reference = String.valueOf(voucher.transactionReference);
            accountEntry.description = "Blue Label One Voucher[";
            accountEntry.description = accountEntry.description + voucher.number;
            accountEntry.description = accountEntry.description + "] redemption ";
            accountEntry.description = accountEntry.description + df.format(amount);
            accountEntry.description = accountEntry.description + " of total value " + df.format(Double.valueOf(voucher.value)) + " " + voucher.currency;
            accountEntry.amount = amount;
            accountEntry.fundedAmount = amount;
            accountEntry.currency = voucher.currency;
            accountEntry.tax = 0.0;
            accountEJB.createAccountEntry(null, accountEntry, new AccountEntrySourceData(BlueLabelService.class));
        }
        catch (CreateException e) {
            log.error((Object)"failed to contact account bean", (Throwable)e);
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public WebServiceResponse fullVoucherRedemption(String migUsername, String userTicket, BlueLabelOneVoucher voucher, Current __current) throws FusionException {
        try {
            voucher.value = StringUtil.scrubDoubleAmount(voucher.value);
            voucher.value = Double.valueOf(voucher.value).toString();
            if (!StringUtils.hasLength((String)userTicket)) {
                log.error((Object)"invalid empty user ticket supplied");
                throw new FusionException("invalid empty user ticket supplied");
            }
            voucher.amountRedeemed = voucher.value;
            voucher.currency = "ZAR";
            boolean isTestVoucher = voucher.currency.equals("ZAR") && voucher.number.startsWith("999999") || voucher.currency.equals("EUR") && voucher.number.startsWith("633718019") || voucher.currency.equals("GBP") && voucher.number.startsWith("633718009");
            Mibli stub = this.serviceLocator.getmibli();
            Response response = stub.fullVoucherRedemption(this.webServiceKey, userTicket, voucher.number, voucher.value);
            log.info((Object)("redemption results for user [" + migUsername + "]  and voucher [" + BlueLabelServiceI.blueLabelOneVoucherToString(voucher) + "] = [" + (Object)((Object)BlueLabelResponseCodes.fromValue(response.getResponseCode())) + " (" + response.getResponseCode() + ") ] and reference [" + response.getResponseData() + "] and is test voucher? " + (isTestVoucher ? "yes" : "no")));
            LinkedList<BlueLabelError> linkedList = this.recentErrors;
            synchronized (linkedList) {
                while (!this.recentErrors.isEmpty() && this.recentErrors.getFirst().isOlderThanHours(6)) {
                    this.recentErrors.removeFirst();
                }
            }
            if (response.getResponseCode() == BlueLabelResponseCodes.SUCCESS.value()) {
                if (StringUtils.hasLength((String)response.getResponseData())) {
                    try {
                        voucher.transactionReference = response.getResponseData();
                        if (this.createAccountEntries && !isTestVoucher) {
                            this.createAccountEntry(migUsername, voucher);
                        }
                    }
                    catch (Exception e) {
                        log.error((Object)("voucher successfully redeemed but failed to create account entry!! username [" + migUsername + "] " + BlueLabelServiceI.blueLabelOneVoucherToString(voucher)), (Throwable)e);
                    }
                } else {
                    log.error((Object)("voucher successfully redeemed but no transaction reference returned? not creating account entry. username [" + migUsername + "] " + BlueLabelServiceI.blueLabelOneVoucherToString(voucher)));
                }
            } else if (response.getResponseCode() != BlueLabelResponseCodes.INVALID_VOUCHER.value() && response.getResponseCode() != BlueLabelResponseCodes.INVALID_VOUCHER_CODE.value()) {
                log.info((Object)("Adding error [" + BlueLabelResponseCodes.fromValue(response.getResponseCode()).toString() + "] to recent errors, we now have " + (this.recentErrors.size() + 1) + " errors"));
                linkedList = this.recentErrors;
                synchronized (linkedList) {
                    this.recentErrors.add(new BlueLabelError(System.currentTimeMillis(), BlueLabelResponseCodes.fromValue(response.getResponseCode())));
                }
            }
            return new WebServiceResponse(response.getResponseData(), response.getResponseCode());
        }
        catch (ServiceException e) {
            log.error((Object)("failed to redeem voucher for username [" + migUsername + "] " + BlueLabelServiceI.blueLabelOneVoucherToString(voucher)), (Throwable)e);
        }
        catch (RemoteException e) {
            log.error((Object)("failed to redeem voucher for username [" + migUsername + "] " + BlueLabelServiceI.blueLabelOneVoucherToString(voucher)), (Throwable)e);
        }
        catch (NumberFormatException e) {
            log.error((Object)("failed to redeem voucher (invalid voucher value) for username [" + migUsername + "] " + BlueLabelServiceI.blueLabelOneVoucherToString(voucher)));
            return new WebServiceResponse("Invalid voucher amount", BlueLabelResponseCodes.INVALID_VOUCHER_VALUE.value());
        }
        throw new FusionException("Unable to redeem voucher, an unknown problem occurred");
    }

    @Required
    public void setMibliUsername(String mibliUsername) {
        this.mibliUsername = mibliUsername;
    }

    @Required
    public void setMibliPassword(String milbiPassword) {
        this.mibliPassword = milbiPassword;
    }

    @Required
    public void setMibliWSDLURL(String mibliServiceURL) {
        this.mibliWSDLURL = mibliServiceURL;
    }

    @Required
    public void setCreateAccountEntries(boolean createAccountEntries) {
        this.createAccountEntries = createAccountEntries;
    }

    @Required
    public void setLiveIdCredentialDAO(LiveIdCredentialDAO liveIdCredentialDAO) {
        this.liveIdCredentialDAO = liveIdCredentialDAO;
    }

    @Required
    public void setCountryDAO(CountryDAO countryDataDAO) {
        this.countryDAO = countryDataDAO;
    }

    public void setServiceKeyUpdateInterval(long serviceKeyUpdateInterval) {
        this.serviceKeyUpdateInterval = serviceKeyUpdateInterval;
    }

    public int getRecentErrorCount() {
        return this.recentErrors.size();
    }

    public class UpdateWebserviceKeyThread
    extends TimerTask {
        private BlueLabelServiceI blueLabelService;

        public UpdateWebserviceKeyThread(BlueLabelServiceI blueLabelService) {
            this.blueLabelService = blueLabelService;
        }

        public void run() {
            try {
                if (this.blueLabelService.initializeSession() != null) {
                    log.info((Object)"successfully updated the web service key!");
                }
            }
            catch (FusionException e) {
                log.fatal((Object)"failed to update web service key!!!");
            }
        }
    }
}

