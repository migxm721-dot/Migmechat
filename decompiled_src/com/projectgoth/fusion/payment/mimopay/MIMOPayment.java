/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.xml.bind.JAXBException
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.StringEntity
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment.mimopay;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.common.HttpClientUtils;
import com.projectgoth.fusion.common.JSONUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MessageBundle;
import com.projectgoth.fusion.common.PaymentUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.PaymentIResponse;
import com.projectgoth.fusion.payment.PaymentInterface;
import com.projectgoth.fusion.payment.mimopay.CreditReloadRequest;
import com.projectgoth.fusion.payment.mimopay.CreditReloadResponse;
import com.projectgoth.fusion.payment.mimopay.MIMOVoucherData;
import com.projectgoth.fusion.payment.mimopay.MIMOXMLUtils;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.ejb.CreateException;
import javax.xml.bind.JAXBException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MIMOPayment
implements PaymentInterface {
    private static final Integer COUNTRY_ID_INDONESIA = 107;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MIMOPayment.class));
    private static final MIMOPayment INSTANCE = new MIMOPayment();
    private static final String RESOURCE_MESSAGE_BUNDLE_NAME = "resource.Credit_Notification_Messages";
    private static final String RESOURCE_MESSAGE_RET_CODE_PREFIX_KEY = "MIMOPAY_RETCODE_";
    private static final String RESOURCE_MESSAGE_UNKNOWN_RET_CODE_KEY = "MIMOPAY_RETCODE_UNKNOWN";
    private static final String RESOURCE_MESSAGE_NULL_RET_CODE_KEY = "MIMOPAY_RETCODE_NULL";
    private static final String RESOURCE_MESSAGE_BUSY_KEY = "MIMOPAY_BUSY_PROCESSING";
    private static int REQUIRED_TRANSACTION_ID_LENGTH = 10;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private MIMOPayment() {
    }

    public static MIMOPayment getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isAccessAllowed(UserData userData) throws PaymentException, Exception {
        if (userData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
        }
        if ((userData.countryID == null || !userData.countryID.equals(COUNTRY_ID_INDONESIA)) && SystemProperty.getBool(SystemPropertyEntities.Payments_MIMOPAY.ONLY_FOR_INDONESIAN_USERS)) {
            return false;
        }
        if (userData.type == UserData.TypeEnum.MIG33_TOP_MERCHANT && SystemProperty.getBool(SystemPropertyEntities.Payments_MIMOPAY.NON_TOP_MERCHANTS_ACCESS_ONLY_ENABLED)) {
            return false;
        }
        if (!SystemProperty.getBool(SystemPropertyEntities.Payments_MIMOPAY.ENABLED_TO_ALL_USERS)) {
            try {
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                if (!accountEJB.isPaymentAllowedToUser(userData.userID, GuardCapabilityEnum.MIMO_PAYMENT_ALLOWED.value())) {
                    return false;
                }
            }
            catch (CreateException e) {
                log.error((Object)"Unable to get guardset for MIMO Payment.", (Throwable)e);
                throw new Exception("Unable to continue with payment.");
            }
            catch (FusionEJBException e) {
                log.error((Object)"Unable to get guardset for MIMO Payment: ", (Throwable)e);
                throw new Exception("Unable to continue with payment.");
            }
        }
        return true;
    }

    @Override
    public Map<String, Object> clientInitiatePayment(JSONObject paymentDetails) throws PaymentException, Exception {
        if (!JSONUtils.hasNonNullProperty(paymentDetails, "userID")) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "userID");
        }
        int userID = paymentDetails.getInt("userID");
        if (!JSONUtils.hasNonNullProperty(paymentDetails, "rechargeType")) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "rechargeType");
        }
        String rechargeTypeName = paymentDetails.getString("rechargeType");
        RechargeTypeEnum rechargeTypeEnum = RechargeTypeEnum.fromTypeName(rechargeTypeName);
        if (rechargeTypeEnum == RechargeTypeEnum.RELOAD) {
            JSONObject accountEntrySource = paymentDetails.getJSONObject("accountEntrySource");
            if (!JSONUtils.hasNonNullProperty(paymentDetails, "accountEntrySource")) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "accountEntrySource");
            }
            String ipAddress = accountEntrySource.getString("ipAddress");
            String sessionID = accountEntrySource.getString("sessionID");
            String mobileDevice = accountEntrySource.getString("mobileDevice");
            String userAgent = accountEntrySource.getString("userAgent");
            AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent);
            String reloadKey = null;
            if (!JSONUtils.hasNonNullProperty(paymentDetails, "reloadKey")) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "reloadKey");
            }
            reloadKey = paymentDetails.getString("reloadKey");
            return this.clientRequestCreditReload(userID, reloadKey, accountEntrySourceData);
        }
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_CREDIT_RECHARGE_TYPE, rechargeTypeEnum);
    }

    private static void validateReloadKey(String reloadKey) throws PaymentException {
        if (SystemProperty.getBool(SystemPropertyEntities.Payments_MIMOPAY.ENABLE_RELOAD_KEY_VALIDATION) && SystemProperty.getInt(SystemPropertyEntities.Payments_MIMOPAY.EXPECTED_RELOAD_KEY_LENGTH) != reloadKey.trim().length()) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_RELOAD_KEY, reloadKey);
        }
    }

    public static String formatOurTransactionID(int paymentID) throws PaymentException {
        String ourTransactionId = String.valueOf(paymentID);
        if (ourTransactionId.length() > REQUIRED_TRANSACTION_ID_LENGTH) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.GENERATED_PAYMENT_ID_TOO_LONG, new Object[0]);
        }
        return StringUtil.padLeft(ourTransactionId, '0', REQUIRED_TRANSACTION_ID_LENGTH);
    }

    public static long getUnixTimestamp(long currentTimeMillies) {
        return currentTimeMillies / 1000L;
    }

    public static String generateHashKey(String merchantCode, String ourFormattedTransactionID, String secretKey) throws GeneralSecurityException {
        String data = merchantCode + ourFormattedTransactionID + secretKey;
        byte[] hashBytes = HashUtils.md5(data);
        return HashUtils.asHex(hashBytes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <TResponse> TResponse dispatchRequest(Object requestData, Class<TResponse> expectedResponseType) throws IOException, JAXBException, GeneralSecurityException, PaymentException {
        HttpClientUtils.HttpClientConfig clientConfig = new HttpClientUtils.HttpClientConfig();
        clientConfig.keepAliveInSecs = SystemProperty.getInt(SystemPropertyEntities.Payments_MIMOPAY.KEEP_ALIVE_IN_SEC);
        clientConfig.maxConnections = SystemProperty.getInt(SystemPropertyEntities.Payments_MIMOPAY.MAX_CONNECTIONS);
        clientConfig.strictHttpsCertCheck = SystemProperty.getBool(SystemPropertyEntities.Payments_MIMOPAY.STRICT_HTTPS_CERT_CHECK);
        clientConfig.timeOutInMillis = SystemProperty.getInt(SystemPropertyEntities.Payments_MIMOPAY.TIME_OUT_IN_MILLIS);
        clientConfig.username = SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.GATEWAY_USERNAME);
        clientConfig.password = SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.GATEWAY_PASSWORD);
        String xmlRequestData = MIMOXMLUtils.serializeToXML(requestData);
        String xmlResponseData = null;
        TResponse result = null;
        String url = SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.VENDOR_BASE_URL) + SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.CREDIT_RELOAD_PATH);
        boolean enableGatewayRequestResponseLogging = SystemProperty.getBool(SystemPropertyEntities.Payments_MIMOPAY.ENABLE_GATEWAY_REQUEST_RESPONSE_LOGGING);
        try {
            HttpPost postRequest = new HttpPost(url);
            postRequest.addHeader("Content-Type", "application/xml");
            postRequest.setEntity((HttpEntity)new StringEntity(xmlRequestData, UTF_8.name()));
            xmlResponseData = MIMOPayment.getResponseString(clientConfig, postRequest);
            StringReader reader = new StringReader(xmlResponseData);
            try {
                result = MIMOXMLUtils.deserializeFromXML(reader, expectedResponseType);
                Object var11_14 = null;
            }
            catch (Throwable throwable) {
                Object var11_15 = null;
                ((Reader)reader).close();
                throw throwable;
            }
            ((Reader)reader).close();
            if (enableGatewayRequestResponseLogging) {
                log.info((Object)("URI:[" + url + "]\nRequest:[" + xmlRequestData + "]\nResponse:[" + xmlResponseData + "]"));
            }
        }
        catch (GeneralSecurityException ex) {
            MIMOPayment.logOutcomes(url, xmlRequestData, xmlResponseData, ex);
            throw ex;
        }
        catch (PaymentException ex) {
            MIMOPayment.logOutcomes(url, xmlRequestData, xmlResponseData, (Exception)((Object)ex));
            throw ex;
        }
        catch (Exception ex) {
            MIMOPayment.logOutcomes(url, xmlRequestData, xmlResponseData, ex);
            IOException ioEx = new IOException("Unable to complete request to vendor gateway");
            ioEx.initCause(ex);
            throw ioEx;
        }
        return result;
    }

    private static String getResponseString(HttpClientUtils.HttpClientConfig clientConfig, HttpPost postRequest) throws GeneralSecurityException, PaymentException {
        String xmlResponseData;
        try {
            xmlResponseData = HttpClientUtils.getResponseString(clientConfig, (HttpUriRequest)postRequest, UTF_8);
        }
        catch (GeneralSecurityException e) {
            throw e;
        }
        catch (IOException e) {
            throw new PaymentException(e, ErrorCause.PaymentErrorReasonType.FAILED_TO_RETRIEVE_RESPONSE_FROM_VENDOR, e.getMessage());
        }
        if (xmlResponseData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_VENDOR_DATA, "Received a blank response");
        }
        return xmlResponseData;
    }

    private static void logOutcomes(String url, String xmlRequestData, String xmlResponseData, Exception ex) {
        log.error((Object)("URI:[" + url + "]\nRequest:[" + xmlRequestData + "]\nResponse:[" + xmlResponseData + "]" + "\nException:[" + ex + "]"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Object> clientRequestCreditReload(int userID, String reloadKey, AccountEntrySourceData accountEntrySourceData) throws PaymentException, Exception {
        MIMOPayment.validateReloadKey(reloadKey);
        String reloadKeyLockKey = userID + ":" + StringUtil.trimmedLowerCase(reloadKey);
        if (MemCachedClientWrapper.addOrIncr(MemCachedKeySpaces.CommonKeySpace.MIMOPAY_LOCK_RELOADKEY, reloadKeyLockKey) == 1L) {
            HashMap<String, Object> hashMap;
            try {
                MIMOVoucherData storedMIMOPaymentData;
                block14: {
                    UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    UserData userData = userEJB.loadUserFromID(userID);
                    if (userData == null) {
                        throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
                    }
                    if (!this.isAccessAllowed(userData)) {
                        throw new PaymentException(ErrorCause.PaymentErrorReasonType.NO_ACCESS_TO_PAYMENT_VENDOR, userData.username, PaymentData.TypeEnum.MIMOPAY.displayName());
                    }
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    int reloadKeyRecyclePeriodDays = SystemProperty.getInt(SystemPropertyEntities.Payments_MIMOPAY.RELOAD_KEY_RECYCLE_PERIOD_DAYS);
                    AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                    storedMIMOPaymentData = (MIMOVoucherData)accountEJB.getPaymentByVoucher(PaymentData.TypeEnum.MIMOPAY.value(), PaymentData.StatusEnum.PENDING.value(), reloadKey, currentTimestamp, reloadKeyRecyclePeriodDays);
                    if (storedMIMOPaymentData == null) {
                        String merchantCode = SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.MERCHANT_CODE);
                        String creditReloadPType = SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.CREDIT_RELOAD_PTYPE);
                        String gameCode = SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.GAME_CODE);
                        MIMOVoucherData newMIMOVoucherData = new MIMOVoucherData();
                        newMIMOVoucherData.assignCreatedTime(new Timestamp(System.currentTimeMillis()));
                        newMIMOVoucherData.assignUpdatedTime(null);
                        newMIMOVoucherData.username = userData.username;
                        newMIMOVoucherData.userId = userData.userID;
                        newMIMOVoucherData.amount = 0.0;
                        newMIMOVoucherData.currency = SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.RELOAD_VALUE_CURRENY);
                        newMIMOVoucherData.voucherCode = reloadKey;
                        newMIMOVoucherData.status = PaymentData.StatusEnum.ONHOLD;
                        newMIMOVoucherData.merchantCode = merchantCode;
                        newMIMOVoucherData.creditReloadPType = creditReloadPType;
                        newMIMOVoucherData.vendorStatusCode = null;
                        newMIMOVoucherData.gameCode = gameCode;
                        newMIMOVoucherData.vendorRemark = null;
                        newMIMOVoucherData.rvalue = null;
                        storedMIMOPaymentData = (MIMOVoucherData)accountEJB.createPayment(newMIMOVoucherData);
                        String formattedTransactionId = MIMOPayment.formatOurTransactionID(storedMIMOPaymentData.id);
                        CreditReloadRequest creditReloadRequest = new CreditReloadRequest();
                        creditReloadRequest.serviceName = ServiceNameEnum.CREDIT_RELOAD.getServiceCode();
                        creditReloadRequest.merchantCode = merchantCode;
                        creditReloadRequest.gameCode = gameCode;
                        creditReloadRequest.userID = String.valueOf(userID);
                        creditReloadRequest.transID = formattedTransactionId;
                        creditReloadRequest.reloadCardKey = reloadKey;
                        creditReloadRequest.pType = creditReloadPType;
                        creditReloadRequest.timestamp = MIMOPayment.getUnixTimestamp(System.currentTimeMillis());
                        creditReloadRequest.hashKey = MIMOPayment.generateHashKey(merchantCode, formattedTransactionId, SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.GATEWAY_SHARED_SECRET));
                        CreditReloadResponse creditReloadResponse = null;
                        try {
                            creditReloadResponse = MIMOPayment.dispatchRequest(creditReloadRequest, CreditReloadResponse.class);
                            storedMIMOPaymentData = MIMOPayment.saveResponse(accountEJB, storedMIMOPaymentData, creditReloadResponse, accountEntrySourceData);
                        }
                        catch (Exception e) {
                            log.error((Object)"Exception occurred: ", (Throwable)e);
                            storedMIMOPaymentData.status = PaymentData.StatusEnum.PENDING;
                            newMIMOVoucherData.assignUpdatedTime(new Timestamp(System.currentTimeMillis()));
                            try {
                                storedMIMOPaymentData = (MIMOVoucherData)accountEJB.updatePayment(storedMIMOPaymentData, accountEntrySourceData);
                                log.info((Object)("Successfully update transaction id " + storedMIMOPaymentData.id + " status:" + storedMIMOPaymentData.status + "."));
                                break block14;
                            }
                            catch (PaymentException pe) {
                                if (pe.getErrorCause() == ErrorCause.PaymentErrorReasonType.DB_UPDATE_PAYMENT_FAILED) {
                                    storedMIMOPaymentData = (MIMOVoucherData)accountEJB.getPaymentById(storedMIMOPaymentData.id);
                                    log.info((Object)("Unsuccessful status update to Transaction id " + storedMIMOPaymentData.id + " status is already :" + storedMIMOPaymentData.status + "."));
                                }
                                break block14;
                            }
                        }
                    }
                    if (storedMIMOPaymentData.userId != userID) {
                        throw new PaymentException(ErrorCause.PaymentErrorReasonType.RELOAD_CARD_USED_BY_ANOTHER_USER, new Object[0]);
                    }
                }
                HashMap<String, Object> initiatePaymentResult = new HashMap<String, Object>();
                initiatePaymentResult.put("paymentData", storedMIMOPaymentData);
                initiatePaymentResult.put("statusMessage", MIMOPayment.getStatusMessage(storedMIMOPaymentData.vendorStatusCode, storedMIMOPaymentData.vendorTransactionId));
                hashMap = initiatePaymentResult;
                Object var21_26 = null;
            }
            catch (Throwable throwable) {
                Object var21_27 = null;
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.MIMOPAY_LOCK_RELOADKEY, reloadKeyLockKey);
                throw throwable;
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.MIMOPAY_LOCK_RELOADKEY, reloadKeyLockKey);
            return hashMap;
        }
        HashMap<String, Object> initiatePaymentResult = new HashMap<String, Object>();
        AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        int reloadKeyRecyclePeriodDays = SystemProperty.getInt(SystemPropertyEntities.Payments_MIMOPAY.RELOAD_KEY_RECYCLE_PERIOD_DAYS);
        MIMOVoucherData storedMIMOPaymentData = (MIMOVoucherData)accountEJB.getPaymentByVoucher(PaymentData.TypeEnum.MIMOPAY.value(), null, reloadKey, currentTimestamp, reloadKeyRecyclePeriodDays);
        if (storedMIMOPaymentData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.PAYMENT_DOES_NOT_EXIST, new Object[0]);
        }
        if (storedMIMOPaymentData.userId != userID) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RELOAD_CARD_USED_BY_ANOTHER_USER, new Object[0]);
        }
        String statusMessage = storedMIMOPaymentData.status == PaymentData.StatusEnum.ONHOLD ? MessageBundle.getMessage(RESOURCE_MESSAGE_BUSY_KEY) : MIMOPayment.getStatusMessage(storedMIMOPaymentData.vendorStatusCode, storedMIMOPaymentData.vendorTransactionId);
        initiatePaymentResult.put("paymentData", storedMIMOPaymentData);
        initiatePaymentResult.put("statusMessage", statusMessage);
        return initiatePaymentResult;
    }

    private static String getStatusMessage(Integer retCode, String vendorTransactionID) {
        MIMOResultCodeEnum resultCodeEnum;
        String key = retCode != null ? ((resultCodeEnum = MIMOResultCodeEnum.getMIMOResult(retCode)) != null ? RESOURCE_MESSAGE_RET_CODE_PREFIX_KEY + retCode : RESOURCE_MESSAGE_UNKNOWN_RET_CODE_KEY) : RESOURCE_MESSAGE_NULL_RET_CODE_KEY;
        Locale locale = null;
        locale = SystemProperty.getBool(SystemPropertyEntities.Payments_MIMOPAY.ONLY_FOR_INDONESIAN_USERS) ? MessageBundle.INDONESIAN_LOCALE : MessageBundle.DEFAULT_LOCALE;
        return MessageBundle.getMessage(RESOURCE_MESSAGE_BUNDLE_NAME, locale, key, retCode, vendorTransactionID);
    }

    private static MIMOVoucherData saveResponse(AccountLocal accountEJB, MIMOVoucherData currentMIMOVoucherData, CreditReloadResponse vendorResponse, AccountEntrySourceData accountEntrySourceData) throws PaymentException {
        MIMOVoucherData storedMIMOPaymentData;
        ServiceNameEnum serviceName = ServiceNameEnum.fromServiceCode(vendorResponse.serviceName);
        if (serviceName != ServiceNameEnum.CREDIT_RELOAD) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_VENDOR_DATA, "Wrong service name " + serviceName);
        }
        if (vendorResponse.retCode == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "CreditReloadResponse.retCode");
        }
        if (StringUtil.isBlank(vendorResponse.transID)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "CreditReloadResponse.transID");
        }
        int transID = Integer.parseInt(vendorResponse.transID);
        if (currentMIMOVoucherData.id != transID) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_VENDOR_DATA, "Wrong CreditReloadResponse.transID");
        }
        MIMOResultCodeEnum mimoResultCodeEnum = MIMOResultCodeEnum.getMIMOResult(vendorResponse.retCode);
        PaymentData.StatusEnum paymentStatus = mimoResultCodeEnum == null ? PaymentData.StatusEnum.PENDING : mimoResultCodeEnum.getStatusOnSyncUpdate();
        if (mimoResultCodeEnum == MIMOResultCodeEnum.SUCCESSFUL && StringUtil.isBlank(vendorResponse.mimoTransactionID)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "CreditReloadResponse.mimoTransactionID");
        }
        currentMIMOVoucherData.amount = vendorResponse.reloadValue == null ? 0.0 : vendorResponse.reloadValue;
        currentMIMOVoucherData.assignUpdatedTime(new Timestamp(System.currentTimeMillis()));
        currentMIMOVoucherData.rvalue = vendorResponse.reloadValue;
        currentMIMOVoucherData.status = paymentStatus;
        currentMIMOVoucherData.vendorRemark = vendorResponse.remark;
        currentMIMOVoucherData.vendorStatusCode = vendorResponse.retCode;
        currentMIMOVoucherData.vendorTimestamp = vendorResponse.timestamp;
        currentMIMOVoucherData.vendorTransactionId = StringUtil.isBlank(vendorResponse.mimoTransactionID) ? "" : vendorResponse.mimoTransactionID;
        try {
            storedMIMOPaymentData = (MIMOVoucherData)accountEJB.updatePayment(currentMIMOVoucherData, accountEntrySourceData);
        }
        catch (PaymentException pe) {
            if (pe.getErrorCause() == ErrorCause.PaymentErrorReasonType.DB_UPDATE_PAYMENT_FAILED) {
                storedMIMOPaymentData = (MIMOVoucherData)accountEJB.getPaymentById(currentMIMOVoucherData.id);
                log.info((Object)("Unsuccessful status update to Transaction id " + storedMIMOPaymentData.id + " status is already :" + storedMIMOPaymentData.status + "."));
            }
            throw pe;
        }
        return storedMIMOPaymentData;
    }

    @Override
    public Map<String, Object> updatePaymentStatus(JSONObject paymentDetails) throws Exception {
        throw new UnsupportedOperationException("Not applicable for this vendor");
    }

    @Override
    public String getCurrencyForUser(UserData userData) {
        String[] supportedCurrencies;
        for (String currency : supportedCurrencies = SystemProperty.getArray(SystemPropertyEntities.Payments_MIMOPAY.SUPPORTED_CURRENCIES)) {
            if (!(currency = PaymentUtils.normalizeCurrency(currency)).equalsIgnoreCase(userData.currency)) continue;
            return currency;
        }
        return SystemProperty.get(SystemPropertyEntities.Payments_MIMOPAY.DEFAULT_CURRENCY);
    }

    @Override
    public <T extends PaymentData> PaymentIResponse clientInitiatePayment(T paymentDetails) throws PaymentException, Exception {
        return null;
    }

    @Override
    public <T extends PaymentData> PaymentIResponse updatePaymentStatus(T paymentDetails) throws PaymentException, Exception {
        return null;
    }

    @Override
    public PaymentIResponse onPaymentAuthorized(String token, JSONObject params) throws PaymentException, Exception {
        return null;
    }

    @Override
    public <T extends PaymentData> PaymentIResponse approve(T paymentDetails, String username) throws PaymentException, Exception {
        return null;
    }

    @Override
    public <T extends PaymentData> PaymentIResponse reject(T paymentDetails, String username) throws PaymentException, Exception {
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ServiceNameEnum implements EnumUtils.IEnumValueGetter<String>
    {
        CREDIT_RELOAD("GV_TOPUP"),
        CREDIT_ELOAD("GV_ELOAD"),
        PENDING_STATUS_INQUIRY("GV_INQUIRY");

        private String serviceCode;
        private static HashMap<String, ServiceNameEnum> lookupByValue;

        private ServiceNameEnum(String serviceCode) {
            this.serviceCode = StringUtil.trimmedUpperCase(serviceCode);
        }

        public String getEnumValue() {
            return this.getServiceCode();
        }

        public static ServiceNameEnum fromServiceCode(String serviceCode) {
            if (serviceCode == null) {
                return null;
            }
            return lookupByValue.get(StringUtil.trimmedUpperCase(serviceCode));
        }

        public String getServiceCode() {
            return this.serviceCode;
        }

        static {
            lookupByValue = new HashMap();
            EnumUtils.populateLookUpMap(lookupByValue, ServiceNameEnum.class);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum MIMOResultCodeEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        SUCCESSFUL(50000, PaymentData.StatusEnum.APPROVED),
        PENDING_TRANSACTION(50001, PaymentData.StatusEnum.PENDING),
        RECORD_NOT_FOUND(50002, PaymentData.StatusEnum.REJECTED),
        INVALID_TRANSACTION_ID(50003, PaymentData.StatusEnum.REJECTED),
        RETRY_REQUIRED_BY_CLIENT(50004, PaymentData.StatusEnum.TIMEOUT),
        INVALID_RELOAD_CARD(51001, PaymentData.StatusEnum.REJECTED),
        RELOAD_CARD_EXPIRED(51002, PaymentData.StatusEnum.REJECTED),
        USED_RELOAD_CARD(51003, PaymentData.StatusEnum.REJECTED),
        UNKNOWN_ERROR(51999, PaymentData.StatusEnum.PENDING),
        INVALID_XML(80000, PaymentData.StatusEnum.REJECTED),
        XML_PARAMETER_VALIDATION_FAILED(80001, PaymentData.StatusEnum.REJECTED),
        DATABASE_CONNECTION_TIMEOUT(81001, PaymentData.StatusEnum.VENDOR_FAILED),
        DATABASE_ERROR(81002, PaymentData.StatusEnum.VENDOR_FAILED),
        TIMEOUT(82001, PaymentData.StatusEnum.VENDOR_FAILED),
        USERID_TEMPORARILY_BLOCKED(10000, PaymentData.StatusEnum.REJECTED),
        VOUCHER_CODE_TEMPORARILY_BLOCKED(10001, PaymentData.StatusEnum.REJECTED);

        private Integer code;
        private PaymentData.StatusEnum statusOnSyncUpdate;
        private static final HashMap<Integer, MIMOResultCodeEnum> lookUpMap;

        public int getCode() {
            return this.code;
        }

        public PaymentData.StatusEnum getStatusOnSyncUpdate() {
            return this.statusOnSyncUpdate;
        }

        private MIMOResultCodeEnum(int code, PaymentData.StatusEnum onlineStatus) {
            this.code = code;
            this.statusOnSyncUpdate = onlineStatus;
        }

        public Integer getEnumValue() {
            return this.code;
        }

        public static MIMOResultCodeEnum getMIMOResult(int resCode) {
            return lookUpMap.get(resCode);
        }

        static {
            lookUpMap = new HashMap();
            EnumUtils.populateLookUpMap(lookUpMap, MIMOResultCodeEnum.class);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum RechargeTypeEnum implements EnumUtils.IEnumValueGetter<String>
    {
        RELOAD("RELOAD", ServiceNameEnum.CREDIT_RELOAD),
        ELOAD("ELOAD", ServiceNameEnum.CREDIT_ELOAD);

        private String typeName;
        private ServiceNameEnum serviceName;
        private static HashMap<String, RechargeTypeEnum> lookupByValue;

        public static RechargeTypeEnum fromTypeName(String name) {
            if (name == null) {
                return null;
            }
            return lookupByValue.get(StringUtil.trimmedUpperCase(name));
        }

        private RechargeTypeEnum(String typeName, ServiceNameEnum serviceName) {
            this.typeName = StringUtil.trimmedUpperCase(typeName);
            this.serviceName = serviceName;
        }

        public String getEnumValue() {
            return this.typeName;
        }

        public String getTypeName() {
            return this.typeName;
        }

        public ServiceNameEnum getServiceName() {
            return this.serviceName;
        }

        static {
            lookupByValue = new HashMap();
            EnumUtils.populateLookUpMap(lookupByValue, RechargeTypeEnum.class);
        }
    }
}

