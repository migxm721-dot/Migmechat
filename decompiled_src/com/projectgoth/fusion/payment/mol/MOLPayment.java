/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.entity.UrlEncodedFormEntity
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.message.BasicNameValuePair
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment.mol;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.common.HttpClientUtils;
import com.projectgoth.fusion.common.JSONUtils;
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
import com.projectgoth.fusion.payment.mol.MOLPaymentData;
import com.projectgoth.fusion.payment.mol.MOLQueryTransactionStatusResult;
import com.projectgoth.fusion.payment.mol.MOLXMLUtils;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MOLPayment
implements PaymentInterface {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MOLPayment.class));
    private static MOLPayment instance = new MOLPayment();
    public static final String MOL_QUERY_STATUS_SUCCESS = "1";
    private static final String URL_ENCODER_ENCODING = "UTF-8";

    private static String generateSignature(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] hashBytes = md.digest(data.getBytes());
        return HashUtils.asHex(hashBytes);
    }

    private MOLPayment() {
    }

    public static MOLPayment getInstance() {
        return instance;
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null) {
            return null;
        }
        return currency.trim().toUpperCase();
    }

    @Override
    public Map<String, Object> clientInitiatePayment(JSONObject paymentDetails) throws PaymentException, Exception {
        try {
            String currency;
            String username;
            if (JSONUtils.hasNonNullProperty(paymentDetails, "username")) {
                username = StringUtil.trimmedLowerCase(paymentDetails.getString("username"));
                if (StringUtil.isBlank(username)) {
                    throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Username is blank.");
                }
            } else {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Username is not supplied.");
            }
            if (!JSONUtils.hasNonNullProperty(paymentDetails, "amount")) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Amount is not supplied.");
            }
            double amount = paymentDetails.getDouble("amount");
            if (amount < 0.0 || Double.isNaN(amount)) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Amount should be a positive value");
            }
            if (JSONUtils.hasNonNullProperty(paymentDetails, "currency")) {
                currency = PaymentUtils.normalizeCurrency(paymentDetails.getString("currency"));
                if (StringUtil.isBlank(currency)) {
                    throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Currency code is blank.");
                }
            } else {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Currency code is not supplied.");
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userEJB.loadUserByUsernameOrAlias(username, false, false);
            if (userData == null) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, username);
            }
            boolean userHasAccess = this.isAccessAllowed(userData);
            if (!userHasAccess) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.NO_ACCESS_TO_PAYMENT_VENDOR, username, PaymentData.TypeEnum.MOL.displayName());
            }
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            int maximumPendingPaymentsCount = SystemProperty.getInt(SystemPropertyEntities.Payments_MOL.MAXIMUM_PENDING_PAYMENTS_COUNT);
            if (maximumPendingPaymentsCount > 0 && accountEJB.getPendingPaymentsCount(userData.userID, PaymentData.TypeEnum.MOL.value()) >= maximumPendingPaymentsCount) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.TOO_MANY_PENDING_PAYMENTS, PaymentData.TypeEnum.MOL.displayName());
            }
            String currencyMOL = this.getCurrencyForUser(userData);
            if (!currencyMOL.equalsIgnoreCase(currency)) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.INCORRECT_CURRENCY, new Object[0]);
            }
            double minAmount = accountEJB.convertCurrency(SystemProperty.getDouble(SystemPropertyEntities.Payments_MOL.MIN_AMOUNT), SystemProperty.get(SystemPropertyEntities.Payments_MOL.DEFAULT_CURRENCY), currency);
            if (amount < minAmount) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.PURCHASE_AMOUNT_BELOW_MINIMUM, currency, PaymentUtils.formatAmountInCurrency(minAmount));
            }
            double maxAmount = accountEJB.convertCurrency(SystemProperty.getDouble(SystemPropertyEntities.Payments_MOL.MAX_AMOUNT), SystemProperty.get(SystemPropertyEntities.Payments_MOL.DEFAULT_CURRENCY), currency);
            if (amount > maxAmount) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.PURCHASE_AMOUNT_ABOVE_MAXIMUM, currency, PaymentUtils.formatAmountInCurrency(maxAmount));
            }
            String merchantID = SystemProperty.get(SystemPropertyEntities.Payments_MOL.MERCHANT_ID);
            String vendorBaseURL = SystemProperty.get(SystemPropertyEntities.Payments_MOL.VENDOR_BASE_URL);
            String heartBeatPath = SystemProperty.get(SystemPropertyEntities.Payments_MOL.HEARTBEAT_PATH);
            log.debug((Object)"fetching heartbeat...");
            String heartBeat = MOLPayment.fetchHeartBeat(merchantID, vendorBaseURL, heartBeatPath);
            if (log.isDebugEnabled()) {
                log.debug((Object)("heartbeat " + heartBeat));
            }
            MOLPaymentData molPaymentData = new MOLPaymentData();
            molPaymentData.transDateTime = null;
            molPaymentData.assignCreatedTime(new Timestamp(System.currentTimeMillis()));
            molPaymentData.assignUpdatedTime(null);
            molPaymentData.username = userData.username;
            molPaymentData.userId = userData.userID;
            molPaymentData.amount = amount;
            molPaymentData.currency = currency;
            molPaymentData.merchantID = merchantID;
            molPaymentData.paymentDescription = "Payment for migme credits worth " + currency + " " + PaymentUtils.formatAmountInCurrency(amount) + ".";
            log.debug((Object)"creating payment");
            PaymentData storedPaymentData = accountEJB.createPayment(molPaymentData);
            String merchantPIN = SystemProperty.get(SystemPropertyEntities.Payments_MOL.MERCHANT_PIN);
            String purchasePath = SystemProperty.get(SystemPropertyEntities.Payments_MOL.PURCHASE_PATH);
            String merchantTransactionID = storedPaymentData.id.toString();
            log.debug((Object)"generating signature");
            String amountString = String.valueOf(molPaymentData.amount);
            String signature = MOLPayment.generateSignatureForPurchase(merchantID, merchantTransactionID, amountString, currency, merchantPIN, heartBeat);
            StringBuilder loginUrl = new StringBuilder(512).append(vendorBaseURL).append("/").append(purchasePath).append("?").append("MerchantID=").append(URLEncoder.encode(merchantID, URL_ENCODER_ENCODING)).append("&Amount=").append(URLEncoder.encode(amountString, URL_ENCODER_ENCODING)).append("&MRef_ID=").append(URLEncoder.encode(merchantTransactionID, URL_ENCODER_ENCODING)).append("&Description=").append(URLEncoder.encode(molPaymentData.paymentDescription, URL_ENCODER_ENCODING)).append("&Currency=").append(URLEncoder.encode(currency, URL_ENCODER_ENCODING)).append("&HeartBeat=").append(URLEncoder.encode(heartBeat, URL_ENCODER_ENCODING)).append("&Signature=").append(URLEncoder.encode(signature, URL_ENCODER_ENCODING));
            HashMap<String, Object> initiatePaymentResult = new HashMap<String, Object>();
            initiatePaymentResult.put("paymentData", storedPaymentData);
            initiatePaymentResult.put("loginUrl", loginUrl.toString());
            return initiatePaymentResult;
        }
        catch (CreateException ce) {
            throw new Exception("Unable to proceed with MOL payment.", ce);
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new Exception("Unable to proceed with MOL payment.", nsae);
        }
        catch (UnsupportedEncodingException uee) {
            throw new Exception("Unable to proceed with MOL payment.", uee);
        }
        catch (PaymentException pe) {
            throw pe;
        }
        catch (FusionRestException fe) {
            throw fe;
        }
        catch (Exception e) {
            throw new Exception("Unable to proceed with MOL payment.", e);
        }
    }

    private static Document getXMLResponseAsDocument(HttpUriRequest request) throws GeneralSecurityException, IOException {
        HttpClientUtils.HttpClientConfig clientConfig = new HttpClientUtils.HttpClientConfig();
        clientConfig.keepAliveInSecs = SystemProperty.getInt(SystemPropertyEntities.Payments_MOL.KEEP_ALIVE_IN_SEC);
        clientConfig.maxConnections = SystemProperty.getInt(SystemPropertyEntities.Payments_MOL.MAX_CONNECTIONS);
        clientConfig.strictHttpsCertCheck = SystemProperty.getBool(SystemPropertyEntities.Payments_MOL.STRICT_HTTPS_CERT_CHECK);
        clientConfig.timeOutInMillis = SystemProperty.getInt(SystemPropertyEntities.Payments_MOL.TIME_OUT_IN_MILLIS);
        return HttpClientUtils.getXMLResponseAsDocument(clientConfig, request);
    }

    public static String fetchHeartBeat(String merchantID, String vendorBaseURL, String heartBeatPath) throws Exception {
        try {
            URI heartBeatURI = new URL(new URL(vendorBaseURL), heartBeatPath).toURI();
            HttpPost httpPost = new HttpPost(heartBeatURI);
            ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
            nvps.add(new BasicNameValuePair("MerchantID", merchantID));
            httpPost.setEntity((HttpEntity)new UrlEncodedFormEntity(nvps));
            Document doc = MOLPayment.getXMLResponseAsDocument((HttpUriRequest)httpPost);
            if (doc == null) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "Returned XML document is null");
            }
            Element elem = (Element)doc.getFirstChild();
            String resCode = MOLXMLUtils.getChildNodeTagValue(elem, "ResCode");
            String heartBeat = "";
            if (resCode != null && Integer.parseInt(resCode.trim()) == MOLResultCodeEnum.TRANSACTION_SUCCESSFULLY_COMPLETED.getCode()) {
                heartBeat = MOLXMLUtils.getChildNodeTagValue(elem, "HB");
                if (heartBeat == null) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "HB tag not found");
                }
                if (heartBeat.length() == 0) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "HB tag has blank value");
                }
                if (heartBeat.equals("00000000-0000-0000-0000-000000000000")) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "HeartBeat value is invalid:" + heartBeat);
                }
            } else {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "Unable to fetch heartbeat to MOL Service Provider.Vendor Result Code=" + resCode);
            }
            return heartBeat;
        }
        catch (PaymentException pe) {
            throw new Exception("Unable to initiate MOL payment.", (Throwable)((Object)pe));
        }
        catch (MalformedURLException me) {
            throw new Exception("Unable to initiate MOL payment.", me);
        }
        catch (URISyntaxException ue) {
            throw new Exception("Unable to initiate MOL payment.", ue);
        }
        catch (UnsupportedEncodingException uee) {
            throw new Exception("Unable to initiate MOL payment.", uee);
        }
        catch (ClientProtocolException ce) {
            throw new Exception("Unable to initiate MOL payment.", ce);
        }
        catch (IOException ie) {
            throw new Exception("Unable to initiate MOL payment.", ie);
        }
    }

    @Override
    public boolean isAccessAllowed(UserData userData) throws PaymentException, Exception {
        if (userData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
        }
        if (userData.type == UserData.TypeEnum.MIG33_TOP_MERCHANT && SystemProperty.getBool(SystemPropertyEntities.Payments_MOL.NON_TOP_MERCHANTS_ACCESS_ONLY_ENABLED)) {
            return false;
        }
        if (!SystemProperty.getBool(SystemPropertyEntities.Payments_MOL.ENABLED_TO_ALL_USERS)) {
            try {
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                if (!accountEJB.isPaymentAllowedToUser(userData.userID, GuardCapabilityEnum.MOL_PAYMENT_ALLOWED.value())) {
                    return false;
                }
            }
            catch (CreateException e) {
                log.error((Object)"Unable to get guardset for MOL Payment.", (Throwable)e);
                throw new Exception("Unable to continue with payment.");
            }
            catch (FusionEJBException e) {
                log.error((Object)"Unable to get guardset for MOL Payment: ", (Throwable)e);
                throw new Exception("Unable to continue with payment.");
            }
        }
        return true;
    }

    @Override
    public String getCurrencyForUser(UserData userData) {
        String[] supportedCurrencies;
        for (String currency : supportedCurrencies = SystemProperty.getArray(SystemPropertyEntities.Payments_MOL.SUPPORTED_CURRENCIES)) {
            if (!(currency = PaymentUtils.normalizeCurrency(currency)).equalsIgnoreCase(userData.currency)) continue;
            return currency;
        }
        return SystemProperty.get(SystemPropertyEntities.Payments_MOL.DEFAULT_CURRENCY);
    }

    public static String generateQueryTransactionStatusRequestSignature(String merchantID, String merchantTransactionIDString, String merchantPIN, String heartBeat) throws NoSuchAlgorithmException {
        String concatInput = merchantID + merchantTransactionIDString + merchantPIN + heartBeat;
        return MOLPayment.generateSignature(concatInput.trim().toLowerCase());
    }

    public static MOLQueryTransactionStatusResult getVendorTransactionStatus(String transactionId) throws Exception {
        String merchantID = SystemProperty.get(SystemPropertyEntities.Payments_MOL.MERCHANT_ID);
        String merchantPIN = SystemProperty.get(SystemPropertyEntities.Payments_MOL.MERCHANT_PIN);
        String vendorBaseURL = SystemProperty.get(SystemPropertyEntities.Payments_MOL.VENDOR_BASE_URL);
        String heartBeatPath = SystemProperty.get(SystemPropertyEntities.Payments_MOL.HEARTBEAT_PATH);
        String queryTxStatusPath = SystemProperty.get(SystemPropertyEntities.Payments_MOL.QUERY_TRANSACTION_STATUS_PATH);
        String heartBeat = MOLPayment.fetchHeartBeat(merchantID, vendorBaseURL, heartBeatPath);
        MOLQueryTransactionStatusResult qryTxResult = MOLPayment.getVendorTransactionStatus(merchantID, transactionId, merchantPIN, heartBeat, vendorBaseURL, queryTxStatusPath);
        String calcQueryTxStatusResponse = MOLPayment.generateQueryTransactionStatusResponseSignature(qryTxResult.resCode, merchantID, transactionId, merchantPIN, qryTxResult.amount, qryTxResult.currency);
        if (!calcQueryTxStatusResponse.equals(qryTxResult.vendorSignature)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "Query Transaction Status result has an invalid signature. Supplied:[" + qryTxResult.vendorSignature + "].Calculated:[" + calcQueryTxStatusResponse + "]");
        }
        return qryTxResult;
    }

    public static MOLQueryTransactionStatusResult getVendorTransactionStatus(String merchantID, String transactionId, String merchantPIN, String heartBeat, String vendorBaseURL, String queryTxStatusPath) throws PaymentException, IOException, GeneralSecurityException, ParserConfigurationException, SAXException, URISyntaxException {
        String signature = MOLPayment.generateQueryTransactionStatusRequestSignature(merchantID, transactionId, merchantPIN, heartBeat);
        URI heartBeatURI = new URL(new URL(vendorBaseURL), queryTxStatusPath).toURI();
        HttpPost httpPost = new HttpPost(heartBeatURI);
        ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        nvps.add(new BasicNameValuePair("MerchantID", merchantID));
        nvps.add(new BasicNameValuePair("MRef_ID", transactionId));
        nvps.add(new BasicNameValuePair("HeartBeat", heartBeat));
        nvps.add(new BasicNameValuePair("Signature", signature));
        httpPost.setEntity((HttpEntity)new UrlEncodedFormEntity(nvps));
        Document doc = MOLPayment.getXMLResponseAsDocument((HttpUriRequest)httpPost);
        if (doc == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "Returned XML document is null");
        }
        return new MOLQueryTransactionStatusResult(doc);
    }

    private static String sanitizeStringForSignatureGeneration(String s) {
        if (s == null) {
            return "";
        }
        return s.toLowerCase();
    }

    public static String generateQueryTransactionStatusResponseSignature(String resultCode, String merchantID, String transactionID, String merchantPIN, String amount, String currency) throws NoSuchAlgorithmException {
        StringBuilder concatInput = new StringBuilder(512);
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(resultCode));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(merchantID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(transactionID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(merchantPIN));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(amount));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(currency));
        return MOLPayment.generateSignature(concatInput.toString());
    }

    public static String generateSignatureForOnlineNotification(String resultCode, String merchantID, String transactionID, String amount, String currency, String vendorTransactionID, String clientMOLUserName, String secretPIN) throws NoSuchAlgorithmException {
        StringBuilder concatInput = new StringBuilder(512);
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(resultCode));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(merchantID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(transactionID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(amount));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(currency));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(vendorTransactionID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(clientMOLUserName));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(secretPIN));
        return MOLPayment.generateSignature(concatInput.toString());
    }

    public static String generateSignatureForOfflineNotification(String resultCode, String merchantID, String transactionID, String amount, String currency, String vendorTransactionID, String clientMOLUserName, String secretPIN) throws NoSuchAlgorithmException {
        StringBuilder concatInput = new StringBuilder(512);
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(resultCode));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(merchantID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(transactionID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(amount));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(currency));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(vendorTransactionID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(clientMOLUserName));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(secretPIN));
        return MOLPayment.generateSignature(concatInput.toString());
    }

    public static String generateSignatureForPurchase(String merchantID, String merchantTransactionIDString, String amountString, String currency, String merchantPIN, String heartBeat) throws NoSuchAlgorithmException {
        StringBuilder concatInput = new StringBuilder(512);
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(merchantID));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(merchantTransactionIDString));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(amountString));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(currency));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(merchantPIN));
        concatInput.append(MOLPayment.sanitizeStringForSignatureGeneration(heartBeat));
        return MOLPayment.generateSignature(concatInput.toString());
    }

    @Override
    public Map<String, Object> updatePaymentStatus(JSONObject paymentDetails) throws Exception {
        String resCode = paymentDetails.getString("ResCode");
        String merchantID = paymentDetails.getString("MerchantID");
        String transactionIDString = paymentDetails.getString("MRef_ID");
        String amount = paymentDetails.getString("Amount");
        String currency = paymentDetails.getString("Currency");
        String vendorTransactionID = paymentDetails.getString("MOLOrderID");
        String clientMolUserName = paymentDetails.getString("MOLUsername");
        String transactionDateTime = paymentDetails.getString("TransDateTime");
        String vendorSignature = paymentDetails.getString("Signature");
        boolean isOnlineNotification = paymentDetails.getBoolean("IsOnline");
        JSONObject accountEntrySource = paymentDetails.getJSONObject("accountEntrySource");
        String ipAddress = accountEntrySource.getString("ipAddress");
        String sessionID = accountEntrySource.getString("sessionID");
        String mobileDevice = accountEntrySource.getString("mobileDevice");
        String userAgent = accountEntrySource.getString("userAgent");
        AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent);
        return this.updatePaymentStatus(isOnlineNotification, resCode, merchantID, transactionIDString, amount, currency, vendorTransactionID, clientMolUserName, transactionDateTime, vendorSignature, accountEntrySourceData);
    }

    private Map<String, Object> updatePaymentStatus(boolean isOnlineNotification, String resCode, String merchantID, String transactionIDString, String amount, String currency, String vendorTransactionID, String clientMolUserName, String transactionDateTime, String vendorSignature, AccountEntrySourceData accountEntrySourceData) throws PaymentException, Exception {
        PaymentData storedPaymentData;
        block16: {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            try {
                PaymentData paymentData = accountEJB.getPaymentById(Integer.parseInt(transactionIDString));
            }
            catch (EJBException e) {
                log.error((Object)("Payment with id [" + transactionIDString + "] not found."));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.PAYMENT_DOES_NOT_EXIST, new Object[0]);
            }
            int resCodeInt = -1;
            if (resCode == null) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "ResCode not present");
            }
            resCodeInt = Integer.parseInt(resCode.trim());
            PaymentData.StatusEnum paymentStatus = null;
            MOLResultCodeEnum molResultCode = MOLResultCodeEnum.getMOLResult(resCodeInt);
            paymentStatus = molResultCode != null ? molResultCode.getStatusOnSyncUpdate() : PaymentData.StatusEnum.PENDING;
            String merchantPIN = SystemProperty.get(SystemPropertyEntities.Payments_MOL.MERCHANT_PIN);
            String calcVendorSignature = isOnlineNotification ? MOLPayment.generateSignatureForOnlineNotification(resCode, merchantID, transactionIDString, amount, currency, vendorTransactionID, clientMolUserName, merchantPIN) : MOLPayment.generateSignatureForOfflineNotification(resCode, merchantID, transactionIDString, amount, currency, vendorTransactionID, clientMolUserName, merchantPIN);
            if (!calcVendorSignature.equals(vendorSignature)) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "UpdatePaymentStatus has an invalid signature. Supplied:[" + vendorSignature + "].Calculated:[" + calcVendorSignature + "]");
            }
            String configuredMerchantID = SystemProperty.get(SystemPropertyEntities.Payments_MOL.MERCHANT_ID);
            if (!configuredMerchantID.equalsIgnoreCase(merchantID)) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Wrong MerchantID:" + merchantID);
            }
            if (isOnlineNotification) {
                String vendorBaseURL = SystemProperty.get(SystemPropertyEntities.Payments_MOL.VENDOR_BASE_URL);
                String heartBeatPath = SystemProperty.get(SystemPropertyEntities.Payments_MOL.HEARTBEAT_PATH);
                String queryTxStatusPath = SystemProperty.get(SystemPropertyEntities.Payments_MOL.QUERY_TRANSACTION_STATUS_PATH);
                String heartBeat = MOLPayment.fetchHeartBeat(configuredMerchantID, vendorBaseURL, heartBeatPath);
                MOLQueryTransactionStatusResult queryTxStatusResult = MOLPayment.getVendorTransactionStatus(configuredMerchantID, transactionIDString, merchantPIN, heartBeat, vendorBaseURL, queryTxStatusPath);
                String calcQueryTxStatusResponse = MOLPayment.generateQueryTransactionStatusResponseSignature(queryTxStatusResult.resCode, configuredMerchantID, transactionIDString, merchantPIN, queryTxStatusResult.amount, queryTxStatusResult.currency);
                if (!calcQueryTxStatusResponse.equals(queryTxStatusResult.vendorSignature)) {
                    throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Failed to validate online notification.Query Transaction Status result has an invalid signature. Supplied:[" + queryTxStatusResult.vendorSignature + "].Calculated:[" + calcQueryTxStatusResponse + "]");
                }
                if (queryTxStatusResult.resCode != null && Integer.parseInt(queryTxStatusResult.resCode.trim()) == MOLResultCodeEnum.TRANSACTION_SUCCESSFULLY_COMPLETED.getCode()) {
                    if (!transactionIDString.equals(queryTxStatusResult.transactionId)) {
                        throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Failed to validate online notification.TransactionID supplied=[" + transactionIDString + "].TransactionID from query status=" + queryTxStatusResult.transactionId);
                    }
                    if (!vendorTransactionID.equals(queryTxStatusResult.vendorTransactionId)) {
                        throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Failed to validate online notification.VendorTransactionID supplied=[" + vendorTransactionID + "].VendorTransactionID from query status=" + queryTxStatusResult.vendorTransactionId);
                    }
                    if (Double.parseDouble(amount.trim()) != Double.parseDouble(queryTxStatusResult.amount.trim())) {
                        throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Failed to validate online notification.Amount supplied=[" + amount + "].Amount from query status=" + queryTxStatusResult.amount);
                    }
                    if (!MOLPayment.normalizeCurrency(currency).equalsIgnoreCase(MOLPayment.normalizeCurrency(queryTxStatusResult.currency))) {
                        throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Failed to validate online notification.Currency supplied=[" + currency + "].Currency from query status=" + queryTxStatusResult.currency);
                    }
                    if (queryTxStatusResult.status.equals(MOL_QUERY_STATUS_SUCCESS) ? molResultCode != MOLResultCodeEnum.TRANSACTION_SUCCESSFULLY_COMPLETED : molResultCode == MOLResultCodeEnum.TRANSACTION_SUCCESSFULLY_COMPLETED) {
                        throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "Query status returned inconsistent state molResult=" + molResultCode.name() + ",queryTxStatusResult=" + queryTxStatusResult.status);
                    }
                } else {
                    throw new IOException("Failed to validate online notification.Query transaction status Result Code = " + queryTxStatusResult.resCode);
                }
            }
            int transactionId = Integer.parseInt(transactionIDString);
            MOLPaymentData updPaymentData = (MOLPaymentData)accountEJB.getPaymentById(transactionId);
            updPaymentData.amount = Double.parseDouble(amount);
            updPaymentData.currency = MOLPayment.normalizeCurrency(currency);
            updPaymentData.vendorTransactionId = vendorTransactionID;
            updPaymentData.vendorType = PaymentData.TypeEnum.MOL;
            updPaymentData.status = paymentStatus;
            updPaymentData.assignUpdatedTime(new Timestamp(System.currentTimeMillis()));
            updPaymentData.transDateTime = transactionDateTime;
            updPaymentData.vendorStatusUpdResCode = resCode;
            updPaymentData.asynchStatusUpdResCode = null;
            storedPaymentData = null;
            try {
                storedPaymentData = accountEJB.updatePayment(updPaymentData, accountEntrySourceData);
                log.info((Object)("Successfully update transaction id " + updPaymentData.id + " status:" + updPaymentData.status + ". Received resCode:" + resCode));
            }
            catch (PaymentException pe) {
                if (pe.getErrorCause() != ErrorCause.PaymentErrorReasonType.DB_UPDATE_PAYMENT_FAILED) break block16;
                storedPaymentData = (MOLPaymentData)accountEJB.getPaymentById(transactionId);
                log.info((Object)("Unsuccessful status update to Transaction id " + storedPaymentData.id + " status is already :" + storedPaymentData.status + ". Received resCode:" + resCode));
            }
        }
        HashMap<String, Object> initiatePaymentResult = new HashMap<String, Object>();
        initiatePaymentResult.put("paymentData", storedPaymentData);
        return initiatePaymentResult;
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
    public static enum MOLResultCodeEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        TRANSACTION_SUCCESSFULLY_COMPLETED(100, PaymentData.StatusEnum.APPROVED),
        ORDER_NOT_FOUND(105, PaymentData.StatusEnum.INVALID),
        GENERIC_ERROR(200, PaymentData.StatusEnum.VENDOR_FAILED),
        FAILED_TO_DEDUCT_MOLPOINTS(201, PaymentData.StatusEnum.VENDOR_FAILED),
        UNEXPECTED_ERROR_OCCURS_DURING_GENERATE_ORDER_ID(202, PaymentData.StatusEnum.VENDOR_FAILED),
        FAILED_TO_REGISTER_USER(203, PaymentData.StatusEnum.VENDOR_FAILED),
        FAILED_TO_RETRIEVE_USER_WALLET(204, PaymentData.StatusEnum.VENDOR_FAILED),
        PASSWORD_HASH_NOT_MATCH(205, PaymentData.StatusEnum.REJECTED),
        FAILED_TO_ACTIVATE_USER_ACCOUNT(206, PaymentData.StatusEnum.VENDOR_FAILED),
        FAILED_TO_RETRIEVE_TRANSACTION_SUMMARY(207, PaymentData.StatusEnum.VENDOR_FAILED),
        FAILED_TO_GENERATE_HEARTBEAT(208, PaymentData.StatusEnum.VENDOR_FAILED),
        DUPLICATION_EMAIL_FOUND(209, PaymentData.StatusEnum.REJECTED),
        FAILED_TO_UPDATE_OFFLINE_MESSAGE(210, PaymentData.StatusEnum.PENDING),
        INCOMPLETE_PARAMETERS_ON_SUBMISSION(400, PaymentData.StatusEnum.REJECTED),
        INVALID_PARAMETERS_ON_SUBMISSION(401, PaymentData.StatusEnum.REJECTED),
        INVALID_AMOUNT(403, PaymentData.StatusEnum.REJECTED),
        INVALID_SHA1_CHECKSUM(405, PaymentData.StatusEnum.REJECTED),
        INVALID_MERCHANT_ID(600, PaymentData.StatusEnum.REJECTED),
        REQUEST_ARE_NOT_SUBMITTED_FROM_THE_MERCHANT_IP_ADDRESS(601, PaymentData.StatusEnum.REJECTED),
        DUPLICATED_MERCHANT_REFERENCE_ID(602, PaymentData.StatusEnum.REJECTED),
        UNABLE_TO_DETERMINE_MERCHANT_ACCOUNT(603, PaymentData.StatusEnum.VENDOR_FAILED),
        INSUFFICIENT_BALANCE_TO_CONTINUE_THE_ORDER(800, PaymentData.StatusEnum.REJECTED),
        USER_SESSION_HAS_EXPIRED(801, PaymentData.StatusEnum.REJECTED),
        USER_HAS_CHOSEN_TO_CANCEL_THE_TRANSACTION(802, PaymentData.StatusEnum.CANCELLED),
        USER_IS_SPENDING_TOO_MUCH_TIME_AT_A_CERTAIN_PAGE(803, PaymentData.StatusEnum.REJECTED),
        EXPIRED_HEARTBEAT_VALUE(999, PaymentData.StatusEnum.REJECTED);

        private Integer code;
        private PaymentData.StatusEnum statusOnSyncUpdate;
        private static final HashMap<Integer, MOLResultCodeEnum> lookUpMap;

        public int getCode() {
            return this.code;
        }

        public PaymentData.StatusEnum getStatusOnSyncUpdate() {
            return this.statusOnSyncUpdate;
        }

        private MOLResultCodeEnum(int code, PaymentData.StatusEnum onlineStatus) {
            this.code = code;
            this.statusOnSyncUpdate = onlineStatus;
        }

        public Integer getEnumValue() {
            return this.code;
        }

        public static MOLResultCodeEnum getMOLResult(int resCode) {
            return lookUpMap.get(resCode);
        }

        static {
            lookUpMap = new HashMap();
            EnumUtils.populateLookUpMap(lookUpMap, MOLResultCodeEnum.class);
        }
    }
}

