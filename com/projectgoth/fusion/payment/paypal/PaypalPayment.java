/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.StringEntity
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment.paypal;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.HttpClientUtils;
import com.projectgoth.fusion.common.JSONUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedDistributedLock;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.PaymentUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
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
import com.projectgoth.fusion.payment.paypal.PaypalInitialValueProviders;
import com.projectgoth.fusion.payment.paypal.PaypalPaymentData;
import com.projectgoth.fusion.payment.paypal.PaypalPaymentIResponse;
import com.projectgoth.fusion.payment.paypal.PaypalTransactionTokenData;
import com.projectgoth.fusion.payment.paypal.Restrictions;
import com.projectgoth.fusion.payment.ratelimit.PaymentRateLimiter;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.CreateException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PaypalPayment
implements PaymentInterface {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PaypalPayment.class));
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String PAYPAL_PAYMENT_ERROR_MESSAGE = "Unable to continue with paypal payment. Please contact merchant@mig.me for more details.";
    private static final PaypalPayment INSTANCE = new PaypalPayment();
    private static final PaymentRateLimiter paymentRateLimiter = new PaymentRateLimiter(MemCachedKeySpaces.RateLimitKeySpace.PAYMENT_RATE_LIMIT, SystemPropertyEntities.Payments_PAYPAL_RateLimit.NAMESPACE);

    private PaypalPayment() {
    }

    public static PaypalPayment getInstance() {
        return INSTANCE;
    }

    @Override
    public Map<String, Object> clientInitiatePayment(JSONObject paymentDetails) throws PaymentException, Exception {
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNSUPPORTED_PAYMENT_METHOD, new Object[0]);
    }

    @Override
    public Map<String, Object> updatePaymentStatus(JSONObject paymentDetails) throws Exception {
        throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNSUPPORTED_PAYMENT_METHOD, new Object[0]);
    }

    @Override
    public <T extends PaymentData> PaypalPaymentIResponse clientInitiatePayment(T pData) throws PaymentException, Exception {
        if (SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL_RateLimit.ENABLED)) {
            String rateLimit = SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL_RateLimit.INITIATE_PAYMENT_ACCESS_RATELIMIT);
            try {
                MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.INITIATE_PAYPAL_PAYMENT.toString(), String.valueOf(pData.userId), rateLimit);
            }
            catch (MemCachedRateLimiter.LimitExceeded e) {
                log.warn((Object)String.format("user:%s has reached the INITIATE_PAYPAL_PAYMENT rate limit with rate limit %s ", pData.userId, rateLimit));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "login attempts");
            }
            catch (MemCachedRateLimiter.FormatError e) {
                log.error((Object)"Formatting error in rate limiter expression", (Throwable)e);
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
        }
        PaypalPaymentIResponse resp = new PaypalPaymentIResponse();
        try {
            Double thresholdUSD;
            PaypalPaymentData paymentData = (PaypalPaymentData)pData;
            this.preValidate(paymentData);
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userEJB.loadUserByUsernameOrAlias(paymentData.username, false, false);
            if (userData == null) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, paymentData.username);
            }
            if (!this.isAccessAllowed(userData)) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.NO_ACCESS_TO_PAYMENT_VENDOR, paymentData.username, PaymentData.TypeEnum.PAYPAL.displayName());
            }
            if (!PaypalPayment.isWhiteListed(userData.username) && PaypalPayment.isBlackListed(userData, paymentData.accountEntrySource.ipAddress, null)) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.NO_ACCESS_TO_PAYMENT_VENDOR, paymentData.username, PaymentData.TypeEnum.PAYPAL.displayName());
            }
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            Double thresholdAmount = accountEJB.convertCurrency(thresholdUSD = Double.valueOf(Restrictions.getMinimumAmount(userData.type)), "USD", paymentData.currency);
            if (paymentData.amount < thresholdAmount) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.PURCHASE_AMOUNT_BELOW_MINIMUM, paymentData.currency, PaymentUtils.formatAmountInCurrency(thresholdAmount));
            }
            thresholdUSD = Restrictions.getMaximumAmount(userData.type);
            thresholdAmount = accountEJB.convertCurrency(thresholdUSD, "USD", paymentData.currency);
            if (paymentData.amount > thresholdAmount) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.PURCHASE_AMOUNT_ABOVE_MAXIMUM, paymentData.currency, PaymentUtils.formatAmountInCurrency(thresholdAmount));
            }
            Double amountUSD = paymentData.amount;
            if (!paymentData.currency.toUpperCase().equals("USD")) {
                amountUSD = accountEJB.convertCurrency(paymentData.amount, paymentData.currency, "USD");
            }
            this.checkTransactionRateLimit(userData, paymentData, amountUSD);
            String returnUrl = SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.RETURN_URL);
            String cancelUrl = SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.CANCEL_URL);
            if (SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL.CLIENT_DEFINED_RETURN_URLS_ENABLED)) {
                if (!StringUtil.isBlank(paymentData.returnUrl)) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Overriding paypal return url with: " + paymentData.returnUrl));
                    }
                    returnUrl = paymentData.returnUrl;
                }
                if (!StringUtil.isBlank(paymentData.cancelUrl)) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Overriding paypal cancel url with: " + paymentData.cancelUrl));
                    }
                    cancelUrl = paymentData.cancelUrl;
                }
            }
            returnUrl = URLEncoder.encode(returnUrl, UTF_8.name());
            cancelUrl = URLEncoder.encode(cancelUrl, UTF_8.name());
            JSONObject paypalResponse = PaypalPayment.setExpressCheckout(paymentData.amount, paymentData.currency, paymentData.returnUrl, paymentData.cancelUrl);
            String paypalToken = paypalResponse.getString("TOKEN");
            PaypalPayment.insertTransactionTokenToCache(paypalToken, userData.username, paymentData.currency, paymentData.amount, null, null, new Timestamp(System.currentTimeMillis()));
            resp.token = paypalToken;
            resp.paymentData = paymentData;
        }
        catch (CreateException e) {
            log.error((Object)"unable to create beans", (Throwable)e);
            throw e;
        }
        catch (PaymentException e) {
            log.error((Object)"error occurred in initiating payment", (Throwable)((Object)e));
            throw e;
        }
        return resp;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public <T extends PaymentData> PaypalPaymentIResponse updatePaymentStatus(T pData) throws PaymentException, Exception {
        try {
            Double amountUSD;
            PaypalPaymentData updPaymentDataRequest;
            block25: {
                updPaymentDataRequest = (PaypalPaymentData)pData;
                this.preValidate(updPaymentDataRequest);
                if (StringUtil.isBlank(updPaymentDataRequest.action)) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "updPaymentDataRequest.action");
                }
                Timestamp now = new Timestamp(System.currentTimeMillis());
                PaypalTransactionTokenData storedTokenData = PaypalPayment.getTransactionTokenDataFromCache(updPaymentDataRequest.token, now);
                if (storedTokenData == null) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.ILLEGAL_TRANSACTION, "You do not have an active payment.");
                }
                if (StringUtil.isBlank(storedTokenData.paypalUserID)) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "storedTokenData.paypalUserID");
                }
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                PaypalPaymentData storedPaypalPaymentData = (PaypalPaymentData)accountEJB.getPaymentById(storedTokenData.paymentID);
                if (StringUtil.isBlank(storedPaypalPaymentData.paypalAccount)) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "storedPaypalPaymentData.paypalAccount");
                }
                updPaymentDataRequest.paypalAccount = storedPaypalPaymentData.paypalAccount;
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userEJB.loadUserByUsernameOrAlias(updPaymentDataRequest.username, false, false);
                if (userData == null) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
                }
                if (userData.userID != storedPaypalPaymentData.userId) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISMATCH_USER_INFORMATION, new Object[0]);
                }
                boolean userHasAccess = this.isAccessAllowed(userData);
                if (!userHasAccess) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.NO_ACCESS_TO_PAYMENT_VENDOR, updPaymentDataRequest.username, PaymentData.TypeEnum.PAYPAL.displayName());
                }
                amountUSD = updPaymentDataRequest.amount;
                if (!updPaymentDataRequest.currency.equalsIgnoreCase("USD")) {
                    amountUSD = accountEJB.convertCurrency(updPaymentDataRequest.amount, updPaymentDataRequest.currency, "USD");
                }
                if (updPaymentDataRequest.action.equalsIgnoreCase("CONFIRM")) {
                    List<PaymentRateLimiter.HitInstance> hits = this.preallocateRateLimit(userEJB, updPaymentDataRequest, updPaymentDataRequest.paypalAccount, amountUSD);
                    try {
                        boolean confirmed = false;
                        JSONObject paypalConfirmationResponse = this.confirmPayment(updPaymentDataRequest.token, updPaymentDataRequest.paypalAccount, updPaymentDataRequest.amount, updPaymentDataRequest.currency);
                        String paypalConfirmationResponseStatus = JSONUtils.getString(paypalConfirmationResponse, "status");
                        if (paypalConfirmationResponseStatus != null && paypalConfirmationResponseStatus.equalsIgnoreCase("ERROR")) {
                            updPaymentDataRequest.status = PaymentData.StatusEnum.VENDOR_FAILED;
                        } else {
                            confirmed = true;
                            JSONObject queryTransactionDetailsResponse = this.getPaypalTransactionDetails(updPaymentDataRequest.token);
                            String vendorTransactionID = JSONUtils.getString(queryTransactionDetailsResponse, "PAYMENTREQUEST_0_TRANSACTIONID");
                            if (StringUtil.isBlank(vendorTransactionID)) {
                                throw new PaymentException(ErrorCause.PaymentErrorReasonType.PAYMENT_STILL_PENDING_IN_VENDOR, "Paypal");
                            }
                            updPaymentDataRequest.vendorTransactionId = vendorTransactionID;
                            updPaymentDataRequest.status = PaymentData.StatusEnum.APPROVED;
                        }
                        this.releaseRateLimitPreallocation(confirmed, hits);
                    }
                    catch (Exception ex) {
                        this.releaseRateLimitPreallocation(false, hits);
                        if (ex instanceof PaymentException) {
                            PaymentException pex = (PaymentException)((Object)ex);
                            throw pex;
                        }
                        log.error((Object)"Error confirming transaction ", (Throwable)ex);
                        throw new PaymentException(ex, ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
                    }
                } else {
                    updPaymentDataRequest.status = updPaymentDataRequest.action.equalsIgnoreCase("CANCEL") ? PaymentData.StatusEnum.CANCELLED : PaymentData.StatusEnum.PENDING;
                }
                PaymentData storedPaymentData = null;
                updPaymentDataRequest.assignUpdatedTime(new Timestamp(System.currentTimeMillis()));
                try {
                    try {
                        storedPaymentData = accountEJB.updatePayment(updPaymentDataRequest, updPaymentDataRequest.accountEntrySource);
                        log.info((Object)("Successfully update transaction id " + storedPaymentData.id + " status:" + storedPaymentData.status + "."));
                    }
                    catch (PaymentException pe) {
                        if (pe.getErrorCause() == ErrorCause.PaymentErrorReasonType.DB_UPDATE_PAYMENT_FAILED) {
                            storedPaymentData = accountEJB.getPaymentById(updPaymentDataRequest.id);
                            log.info((Object)("Unsuccessful status update to Transaction id " + storedPaymentData.id + " status is already :" + storedPaymentData.status + "."));
                        }
                        Object var18_23 = null;
                        if (updPaymentDataRequest.status != PaymentData.StatusEnum.PENDING) {
                            PaypalPayment.deleteTransactionToken(updPaymentDataRequest.token);
                        }
                        break block25;
                    }
                    Object var18_22 = null;
                    if (updPaymentDataRequest.status == PaymentData.StatusEnum.PENDING) break block25;
                }
                catch (Throwable throwable) {
                    Object var18_24 = null;
                    if (updPaymentDataRequest.status != PaymentData.StatusEnum.PENDING) {
                        PaypalPayment.deleteTransactionToken(updPaymentDataRequest.token);
                    }
                    throw throwable;
                }
                PaypalPayment.deleteTransactionToken(updPaymentDataRequest.token);
            }
            PaypalPaymentIResponse resp = new PaypalPaymentIResponse();
            resp.token = updPaymentDataRequest.token;
            resp.amount = updPaymentDataRequest.amount;
            resp.currency = updPaymentDataRequest.currency;
            resp.amountUSD = amountUSD;
            resp.result = updPaymentDataRequest.status.name();
            return resp;
        }
        catch (CreateException e) {
            log.error((Object)"Create exception in updating paypal transaction.", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
        }
    }

    private static void logOutcomes(String url, String xmlRequestData, String xmlResponseData, Exception ex) {
        log.error((Object)("URI:[" + url + "]\nRequest:[" + xmlRequestData + "]\nResponse:[" + xmlResponseData + "]" + "\nException:[" + ex + "]"));
    }

    public static JSONObject setExpressCheckout(double amount, String currency, String returnUrl, String cancelUrl) throws PaymentException {
        try {
            String request = "PAYMENTREQUEST_0_AMT=" + String.valueOf(amount) + "&PAYMENTREQUEST_0_PAYMENTACTION=Sale" + "&RETURNURL=" + returnUrl + "&CANCELURL=" + cancelUrl + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currency + "&REQCONFIRMSHIPPING=0" + "&NOSHIPPING=1" + "&L_PAYMENTREQUEST_0_NAME0=migme credits" + "&L_PAYMENTREQUEST_0_AMT0=" + amount + "&L_PAYMENTREQUEST_0_ITEMCATEGORY0=Digital" + "&METHOD=SETEXPRESSCHECKOUT" + "&USER=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.USERNAME) + "&PWD=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.PASSWORD) + "&SIGNATURE=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.SIGNATURE) + "&VERSION=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.API_VERSION);
            String response = PaypalPayment.dispatchPaypalRequest(request);
            JSONObject jsonResponse = PaypalPayment.processResponseFromAPI(response);
            if (jsonResponse.has("L_ERRORCODE0")) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, jsonResponse.get("L_LONGMESSAGE0"));
            }
            return jsonResponse;
        }
        catch (UnsupportedEncodingException e) {
            log.error((Object)"malformed url in sending paypal request", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, PAYPAL_PAYMENT_ERROR_MESSAGE);
        }
        catch (JSONException e) {
            log.error((Object)"Unable to digest response form paypal", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
        }
        catch (IOException e) {
            log.error((Object)"Error in sending paypal request", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PaypalPaymentIResponse onPaymentAuthorized(String token, JSONObject params) throws PaymentException {
        if (StringUtil.isBlank(token)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "Token");
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PaypalTransactionTokenData storedTokenData = PaypalPayment.getTransactionTokenDataFromCache(token, now);
        if (storedTokenData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ILLEGAL_TRANSACTION, "You do not have an active payment.");
        }
        MemCachedDistributedLock.LockInstance tokenTransactionLock = MemCachedDistributedLock.getDistributedLock(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN_LOCK, token);
        if (tokenTransactionLock == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, "Timeout while trying to process token");
        }
        try {
            storedTokenData = PaypalPayment.getTransactionTokenDataFromCache(token, now);
            if (storedTokenData == null) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.ILLEGAL_TRANSACTION, "You do not have an active payment.");
            }
            JSONObject paypalTokenDetailsResponse = this.getPaypalTransactionDetails(token);
            String currency = storedTokenData.currency;
            double amount = storedTokenData.amount;
            String username = storedTokenData.username;
            String paypalUserID = paypalTokenDetailsResponse.getString("PAYERID");
            double usdAmount = storedTokenData.usdAmount;
            if (storedTokenData.paymentID == null) {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userEJB.loadUserByUsernameOrAlias(username, false, false);
                if (userData == null) {
                    throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
                }
                PaypalPaymentData paymentData = new PaypalPaymentData();
                paymentData.assignCreatedTime(new Timestamp(System.currentTimeMillis()));
                paymentData.assignUpdatedTime(null);
                paymentData.username = userData.username;
                paymentData.userId = userData.userID;
                paymentData.amount = amount;
                paymentData.currency = currency;
                paymentData.paypalAccount = paypalUserID;
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                usdAmount = !currency.toUpperCase().equals("USD") ? accountEJB.convertCurrency(amount, currency, "USD") : amount;
                this.checkTransactionRateLimit(userData, paymentData, usdAmount);
                this.checkPaypalAccountRateLimit(userData, paymentData, paymentData.paypalAccount);
                paymentData = (PaypalPaymentData)accountEJB.createPayment(paymentData);
                storedTokenData.paymentID = paymentData.id;
                storedTokenData.paypalUserID = paypalUserID;
                storedTokenData.usdAmount = usdAmount;
                now = new Timestamp(System.currentTimeMillis());
                PaypalPayment.updateTransactionToken(token, storedTokenData, now);
            }
            PaypalPaymentIResponse mthdResponse = new PaypalPaymentIResponse();
            mthdResponse.amount = amount;
            mthdResponse.amountUSD = usdAmount;
            mthdResponse.currency = currency;
            mthdResponse.token = token;
            PaypalPaymentIResponse paypalPaymentIResponse = mthdResponse;
            {
                Object var19_19 = null;
                tokenTransactionLock.release(false);
            }
            return paypalPaymentIResponse;
        }
        catch (Throwable throwable) {
            try {
                Object var19_20 = null;
                tokenTransactionLock.release(false);
                throw throwable;
            }
            catch (CreateException e) {
                log.error((Object)"Error occurred in retrieving paypal payment details", (Throwable)e);
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
            }
            catch (PaymentException e) {
                log.error((Object)"Payment exception occurred.", (Throwable)((Object)e));
                throw e;
            }
            catch (Exception e) {
                log.error((Object)"Malformed response from paypal", (Throwable)e);
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_VENDOR_DATA, PAYPAL_PAYMENT_ERROR_MESSAGE);
            }
        }
    }

    private JSONObject getPaypalTransactionDetails(String token) throws PaymentException {
        try {
            String request = "TOKEN=" + token + "&METHOD=GETEXPRESSCHECKOUTDETAILS" + "&USER=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.USERNAME) + "&PWD=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.PASSWORD) + "&SIGNATURE=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.SIGNATURE) + "&VERSION=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.API_VERSION);
            String response = PaypalPayment.dispatchPaypalRequest(request);
            JSONObject jsonResponse = PaypalPayment.processResponseFromAPI(response);
            if (jsonResponse.has("L_ERRORCODE0")) {
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, jsonResponse.get("L_LONGMESSAGE0"));
            }
            return jsonResponse;
        }
        catch (JSONException e) {
            log.error((Object)"Malformed response from transaction details", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, PAYPAL_PAYMENT_ERROR_MESSAGE);
        }
        catch (UnsupportedEncodingException e) {
            log.error((Object)"malformed url in sending paypal request", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, PAYPAL_PAYMENT_ERROR_MESSAGE);
        }
        catch (IOException e) {
            log.error((Object)"Error while sending paypal request", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
        }
    }

    public JSONObject confirmPayment(String token, String userID, double amount, String currency) throws PaymentException {
        try {
            String request = "&TOKEN=" + token + "&PAYERID=" + userID + "&PAYMENTREQUEST_0_PAYMENTACTION=Sale" + "&PAYMENTREQUEST_0_AMT=" + String.valueOf(amount) + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currency + "&METHOD=DOEXPRESSCHECKOUTPAYMENT" + "&USER=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.USERNAME) + "&PWD=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.PASSWORD) + "&SIGNATURE=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.SIGNATURE) + "&VERSION=" + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.API_VERSION);
            String response = PaypalPayment.dispatchPaypalRequest(request);
            JSONObject jsonResponse = PaypalPayment.processResponseFromAPI(response);
            try {
                JSONObject jsonDetails = new JSONObject();
                if (jsonResponse.has("ACK") && jsonResponse.getString("ACK").equalsIgnoreCase("SUCCESS")) {
                    jsonDetails.put("amount", jsonResponse.getDouble("PAYMENTINFO_0_AMT"));
                    jsonDetails.put("token", (Object)token);
                    jsonDetails.put("currency", (Object)jsonResponse.getString("PAYMENTINFO_0_CURRENCYCODE"));
                    jsonDetails.put("status", (Object)jsonResponse.getString("PAYMENTINFO_0_PAYMENTSTATUS"));
                    return jsonDetails;
                }
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, jsonResponse.get("L_LONGMESSAGE0"));
            }
            catch (JSONException e) {
                log.error((Object)"Malformed response from paypal", (Throwable)e);
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_VENDOR_DATA, "Unable to continue with paypal payment. Please contact mercahnt@mig.me.");
            }
        }
        catch (JSONException e) {
            log.error((Object)"malformed url in sending paypal request", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, PAYPAL_PAYMENT_ERROR_MESSAGE);
        }
        catch (UnsupportedEncodingException e) {
            log.error((Object)"malformed url in sending paypal request", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, PAYPAL_PAYMENT_ERROR_MESSAGE);
        }
        catch (IOException e) {
            log.error((Object)"malformed url in sending paypal request", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
        }
    }

    @Override
    public String getCurrencyForUser(UserData userData) {
        String[] supportedCurrencies;
        for (String currency : supportedCurrencies = SystemProperty.getArray(SystemPropertyEntities.Payments_PAYPAL.SUPPORTED_CURRENCIES)) {
            if (!(currency = PaymentUtils.normalizeCurrency(currency)).equalsIgnoreCase(userData.currency)) continue;
            return currency;
        }
        return SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.DEFAULT_CURRENCY);
    }

    @Override
    public boolean isAccessAllowed(UserData userData) throws PaymentException, Exception {
        if (userData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
        }
        if (userData.type == UserData.TypeEnum.MIG33_TOP_MERCHANT && SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL.NON_TOP_MERCHANTS_ACCESS_ONLY_ENABLED)) {
            return false;
        }
        if (!SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL.ENABLED_TO_ALL_USERS)) {
            try {
                AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                if (!accountEJB.isPaymentAllowedToUser(userData.userID, GuardCapabilityEnum.PAYPAL_PAYMENT_ALLOWED.value())) {
                    return false;
                }
            }
            catch (CreateException e) {
                log.error((Object)"Unable to get guardset for PAYPAL Payment.", (Throwable)e);
                throw new Exception("Unable to continue with payment.");
            }
            catch (FusionEJBException e) {
                log.error((Object)"Unable to get guardset for PAYPAL Payment: ", (Throwable)e);
                throw new Exception("Unable to continue with payment.");
            }
        }
        return true;
    }

    public boolean isBelowLimits(String username) {
        return true;
    }

    private static String getTransactionTokenLocalKey(String tokenStr) {
        return StringUtil.trimmedUpperCase(tokenStr);
    }

    public static PaypalTransactionTokenData insertTransactionTokenToCache(String token, String username, String currency, double amount, Integer paymentId, String paypalUserID, Date currentDate) throws JSONException {
        String tokenLocalKey = PaypalPayment.getTransactionTokenLocalKey(token);
        long tokenTTL = SystemProperty.getInt(SystemPropertyEntities.Payments_PAYPAL.EXPRESS_CHECKOUT_SESSION_TIMEOUT_MILLIS);
        long createTs = currentDate.getTime();
        long expireTs = createTs + tokenTTL;
        PaypalTransactionTokenData transactionTokenData = new PaypalTransactionTokenData();
        transactionTokenData.currency = currency;
        transactionTokenData.username = username;
        transactionTokenData.amount = amount;
        transactionTokenData.paymentID = paymentId;
        transactionTokenData.paypalUserID = paypalUserID;
        transactionTokenData.createDate = currentDate;
        transactionTokenData.expireDate = new Timestamp(expireTs);
        String tokenDataStr = transactionTokenData.toJSONString();
        log.info((Object)("Inserting " + MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey) + " => [" + tokenDataStr + "] TTL:[" + tokenTTL + "] ms"));
        if (!MemCachedClientWrapper.add(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey, tokenDataStr, tokenTTL)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_VENDOR_DATA, "Duplicate token received from PAYPAL. Please try again.");
        }
        return transactionTokenData;
    }

    public static void deleteTransactionToken(String token) {
        String tokenLocalKey = PaypalPayment.getTransactionTokenLocalKey(token);
        log.info((Object)("Deleting " + MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey)));
        MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey);
    }

    public static PaypalTransactionTokenData updateTransactionToken(String token, PaypalTransactionTokenData tokenData, Date currentDate) throws JSONException, ParseException {
        if (tokenData != null) {
            String tokenLocalKey = PaypalPayment.getTransactionTokenLocalKey(token);
            long tokenMaxTTL = SystemProperty.getInt(SystemPropertyEntities.Payments_PAYPAL.EXPRESS_CHECKOUT_SESSION_TIMEOUT_MILLIS);
            long newTokenTTL = tokenData.expireDate.getTime() - currentDate.getTime();
            if (newTokenTTL > 0L) {
                if (newTokenTTL > tokenMaxTTL) {
                    newTokenTTL = tokenMaxTTL;
                    tokenData.expireDate = new Timestamp(tokenData.createDate.getTime() + newTokenTTL);
                }
                String storedTokenDataStr = tokenData.toJSONString();
                log.info((Object)("Updating " + MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey) + " => [" + storedTokenDataStr + "] new TTL:[" + newTokenTTL + "] ms"));
                MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey, storedTokenDataStr, newTokenTTL);
            } else {
                log.error((Object)("Updating " + MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey) + " cancelled due to TTL has reached."));
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey);
            }
        }
        return tokenData;
    }

    public static PaypalTransactionTokenData getTransactionTokenDataFromCache(String token, Date currentDate) throws JSONException, ParseException {
        String tokenLocalKey = PaypalPayment.getTransactionTokenLocalKey(token);
        String storedTokenDataStr = (String)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey);
        if (storedTokenDataStr == null) {
            log.error((Object)("PAYPAL Token:" + token + " not in memcached."));
            return null;
        }
        PaypalTransactionTokenData storedTokenData = PaypalTransactionTokenData.createFromJSONString(storedTokenDataStr);
        if (currentDate.after(storedTokenData.expireDate)) {
            log.error((Object)("PAYPAL Token: " + token + " expireDate: [" + storedTokenData.expireDate + "], current action time [" + currentDate + "]. Will delete it from cache."));
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.PAYPAL_TXN, tokenLocalKey);
            return null;
        }
        return storedTokenData;
    }

    private static JSONObject processResponseFromAPI(String response) throws JSONException, UnsupportedEncodingException {
        String[] keyValPairs;
        String decodedResponse = URLDecoder.decode(response, UTF_8.name());
        JSONObject jsonResponse = new JSONObject();
        for (String responseElement : keyValPairs = decodedResponse.split("&")) {
            String[] elems = responseElement.split("=");
            jsonResponse.put(elems[0], (Object)elems[1]);
        }
        return jsonResponse;
    }

    public static boolean isWhiteListed(String username) {
        return SystemProperty.isValueInArray(username, SystemPropertyEntities.Payments_PAYPAL.WHITELIST_USERNAME);
    }

    public static boolean isBlackListed(UserData user, String ipAddress, String paypalAccount) {
        if (user != null && SystemProperty.isValueInArray(user.username, SystemPropertyEntities.Payments_PAYPAL.BLACKLIST_USERNAME)) {
            return true;
        }
        if (user != null && SystemProperty.isValueInIntegerArray(user.countryID, SystemPropertyEntities.Payments_PAYPAL.BLACKLIST_COUNTRY)) {
            return true;
        }
        if (ipAddress != null && SystemProperty.isValueInArray(ipAddress, SystemPropertyEntities.Payments_PAYPAL.BLACKLIST_IP_ADDRESS)) {
            return true;
        }
        return paypalAccount != null && SystemProperty.isValueInArray(paypalAccount, SystemPropertyEntities.Payments_PAYPAL.BLACKLIST_PAYPAL_ACCOUNT);
    }

    public void releaseRateLimitPreallocation(boolean doConfirm, List<PaymentRateLimiter.HitInstance> hitList) {
        for (PaymentRateLimiter.HitInstance hit : hitList) {
            hit.end(doConfirm);
        }
    }

    public List<PaymentRateLimiter.HitInstance> preallocateRateLimit(UserLocal userEJB, PaypalPaymentData paymentData, String paypalUserID, double amountUSD) throws PaymentException {
        long preallocationLife = 120000L;
        ArrayList<PaymentRateLimiter.HitInstance> hitList = new ArrayList<PaymentRateLimiter.HitInstance>();
        if (!SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL_RateLimit.ENABLED)) {
            return hitList;
        }
        UserData userData = userEJB.loadUserByUsernameOrAlias(paymentData.username, false, false);
        if (userData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
        }
        long amountValue = (long)amountUSD * 10000L;
        try {
            PaymentRateLimiter.HitInstance hit = paymentRateLimiter.beginHit(PaypalInitialValueProviders.AllCountPerCountry.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerCountryRateLimiter(userData), 1L, PaypalInitialValueProviders.AllCountPerCountry.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), preallocationLife, PaypalInitialValueProviders.AllCountPerCountry.INSTANCE, new PaypalInitialValueProviders.AllCountPerCountry.Param(userData.countryID, userData.type));
            if (hit == null) {
                log.warn((Object)("Paypal rate limit exceeded;limit type=[total transaction count];scope=[per country,per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
            }
            hitList.add(hit);
            hit = paymentRateLimiter.beginHit(PaypalInitialValueProviders.AllValuePerCountry.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerCountryRateLimiter(userData), amountValue, (long)(PaypalInitialValueProviders.AllValuePerCountry.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Double.class) * 10000.0), preallocationLife, PaypalInitialValueProviders.AllValuePerCountry.INSTANCE, new PaypalInitialValueProviders.AllValuePerCountry.Param(userData.countryID, userData.type));
            if (hit == null) {
                log.warn((Object)("Paypal rate limit exceeded;limit type=[total transaction amount];scope=[per country,per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "amount");
            }
            hitList.add(hit);
            hit = paymentRateLimiter.beginHit(PaypalInitialValueProviders.AllCountPerUserType.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserTypeRateLimiter(userData.type), 1L, PaypalInitialValueProviders.AllCountPerUserType.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), preallocationLife, PaypalInitialValueProviders.AllCountPerUserType.INSTANCE, userData.type);
            if (hit == null) {
                log.warn((Object)("Paypal rate limit exceeded;limit type=[total transaction count];scope=[per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
            }
            hitList.add(hit);
            hit = paymentRateLimiter.beginHit(PaypalInitialValueProviders.AllValuePerUserType.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserTypeRateLimiter(userData.type), amountValue, (long)(PaypalInitialValueProviders.AllValuePerUserType.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Double.class) * 10000.0), preallocationLife, PaypalInitialValueProviders.AllValuePerUserType.INSTANCE, userData.type);
            if (hit == null) {
                log.warn((Object)("Paypal rate limit exceeded;limit type=[total transaction amount];scope=[per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "amount");
            }
            hitList.add(hit);
            hit = paymentRateLimiter.beginHit(PaypalInitialValueProviders.AllValuePerUserID.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserIDRateLimiter(userData), amountValue, (long)(PaypalInitialValueProviders.AllValuePerUserID.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Double.class) * 10000.0), preallocationLife, PaypalInitialValueProviders.AllValuePerUserID.INSTANCE, userData.userID);
            if (hit == null) {
                log.warn((Object)("Paypal rate limit exceeded;limit type=[total transaction amount];scope=[per user ID];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "amount");
            }
            hitList.add(hit);
            hit = paymentRateLimiter.beginHit(PaypalInitialValueProviders.AllCountPerUserID.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserIDRateLimiter(userData), 1L, PaypalInitialValueProviders.AllCountPerUserID.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), preallocationLife, PaypalInitialValueProviders.AllCountPerUserID.INSTANCE, userData.userID);
            if (hit == null) {
                log.warn((Object)("Paypal rate limit exceeded;limit type=[total transaction count];scope=[per user ID];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
            }
            hitList.add(hit);
            hit = paymentRateLimiter.beginHit(PaypalInitialValueProviders.AllCountPerPaypalUser.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerVendorUserIDRateLimiter(paypalUserID), 1L, PaypalInitialValueProviders.AllCountPerPaypalUser.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), preallocationLife, PaypalInitialValueProviders.AllCountPerPaypalUser.INSTANCE, paypalUserID);
            if (hit == null) {
                log.warn((Object)("Paypal rate limit exceeded;limit type=[total transaction count];scope=[per user paypalUserID];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "];paypalUserID=[" + paypalUserID + "]"));
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
            }
            hitList.add(hit);
        }
        catch (Exception ex) {
            this.releaseRateLimitPreallocation(false, hitList);
            if (ex instanceof PaymentException) {
                PaymentException pex = (PaymentException)((Object)ex);
                log.warn((Object)("beginHit:" + pex.getMessage()));
                throw pex;
            }
            log.error((Object)"beginHit error", (Throwable)ex);
            throw new PaymentException(ex, ErrorCause.PaymentErrorReasonType.INTERNAL_SYSTEM_ERROR, new Object[0]);
        }
        return hitList;
    }

    public void checkTransactionRateLimit(UserData userData, PaypalPaymentData paymentData, double amountUSD) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL_RateLimit.ENABLED)) {
            return;
        }
        long amountValue = (long)amountUSD * 10000L;
        if (paymentRateLimiter.checkWithoutHit(PaypalInitialValueProviders.AllCountPerCountry.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerCountryRateLimiter(userData), 1L, PaypalInitialValueProviders.AllCountPerCountry.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), PaypalInitialValueProviders.AllCountPerCountry.INSTANCE, new PaypalInitialValueProviders.AllCountPerCountry.Param(userData.countryID, userData.type))) {
            log.warn((Object)("Paypal rate limit (check without hit);limit type=[total transaction count];scope=[per country,per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
        }
        if (paymentRateLimiter.checkWithoutHit(PaypalInitialValueProviders.AllValuePerCountry.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerCountryRateLimiter(userData), amountValue, (long)(PaypalInitialValueProviders.AllValuePerCountry.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Double.class) * 10000.0), PaypalInitialValueProviders.AllValuePerCountry.INSTANCE, new PaypalInitialValueProviders.AllValuePerCountry.Param(userData.countryID, userData.type))) {
            log.warn((Object)("Paypal rate limit (check without hit);limit type=[total transaction value];scope=[per country,per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "amount");
        }
        if (paymentRateLimiter.checkWithoutHit(PaypalInitialValueProviders.AllCountPerUserType.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserTypeRateLimiter(userData.type), 1L, PaypalInitialValueProviders.AllCountPerUserType.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), PaypalInitialValueProviders.AllCountPerUserType.INSTANCE, userData.type)) {
            log.warn((Object)("Paypal rate limit (check without hit);limit type=[total transaction count];scope=[per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
        }
        if (paymentRateLimiter.checkWithoutHit(PaypalInitialValueProviders.AllValuePerUserType.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserTypeRateLimiter(userData.type), amountValue, (long)(PaypalInitialValueProviders.AllValuePerUserType.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Double.class) * 10000.0), PaypalInitialValueProviders.AllValuePerUserType.INSTANCE, userData.type)) {
            log.warn((Object)("Paypal rate limit (check without hit);limit type=[total transaction value];scope=[per user type];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "amount");
        }
        if (paymentRateLimiter.checkWithoutHit(PaypalInitialValueProviders.AllValuePerUserID.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserIDRateLimiter(userData), amountValue, (long)(PaypalInitialValueProviders.AllValuePerUserID.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Double.class) * 10000.0), PaypalInitialValueProviders.AllValuePerUserID.INSTANCE, userData.userID)) {
            log.warn((Object)("Paypal rate limit (check without hit);limit type=[total transaction value];scope=[per userid];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "amount");
        }
        if (paymentRateLimiter.checkWithoutHit(PaypalInitialValueProviders.AllCountPerUserID.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerUserIDRateLimiter(userData), 1L, PaypalInitialValueProviders.AllCountPerUserID.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), PaypalInitialValueProviders.AllCountPerUserID.INSTANCE, userData.userID)) {
            log.warn((Object)("Paypal rate limit (check without hit);limit type=[total transaction count];scope=[per userid];countryID=[" + userData.countryID + "];userType=[" + (Object)((Object)userData.type) + "];userID=[" + userData.userID + "]"));
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
        }
    }

    private void checkPaypalAccountRateLimit(UserData userData, PaypalPaymentData paymentData, String paypalUserID) {
        if (!SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL_RateLimit.ENABLED)) {
            return;
        }
        if (userData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.UNKNOWN_USER, new Object[0]);
        }
        if (paymentRateLimiter.checkWithoutHit(PaypalInitialValueProviders.AllCountPerPaypalUser.INSTANCE.getRateLimitKeySpace(), PaypalPayment.getLocalKeyForPerVendorUserIDRateLimiter(paypalUserID), 1L, PaypalInitialValueProviders.AllCountPerPaypalUser.INSTANCE.getRateLimitKeySpace().getUserRateLimitValue(userData.type, Long.class), PaypalInitialValueProviders.AllCountPerPaypalUser.INSTANCE, paypalUserID)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.RATE_LIMIT_EXCEEDED, "number of requests");
        }
    }

    private static String getLocalKeyForPerCountryRateLimiter(UserData userData) {
        int countryID = userData.countryID == null ? -1 : userData.countryID;
        return MemCachedKeyUtils.getFullKeyFromStrings(String.valueOf(countryID), String.valueOf(userData.type.value()));
    }

    private static String getLocalKeyForPerUserIDRateLimiter(UserData userData) {
        int userID = userData.userID == null ? -1 : userData.userID;
        return MemCachedKeyUtils.getFullKeyFromStrings(String.valueOf(userID), new String[0]);
    }

    private static String getLocalKeyForPerUserTypeRateLimiter(UserData.TypeEnum userType) {
        return userType == null ? "" : String.valueOf(userType.value());
    }

    private static String getLocalKeyForPerVendorUserIDRateLimiter(String paypalUserID) {
        if (StringUtil.isBlank(paypalUserID)) {
            return "";
        }
        String paypalUIDKey = paypalUserID.toLowerCase().trim();
        if (log.isDebugEnabled()) {
            log.debug((Object)("PaypalUserID : " + paypalUserID + "->" + paypalUIDKey));
        }
        return paypalUIDKey;
    }

    private void preValidate(PaypalPaymentData paymentData) throws PaymentException {
        if (StringUtil.isBlank(paymentData.username)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "Username.");
        }
        if (StringUtil.isBlank(paymentData.currency)) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "Currency.");
        }
        if (paymentData.accountEntrySource == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.MISSING_FIELD, "AccountEntrySource.");
        }
    }

    public static String dispatchPaypalRequest(String requestData) throws IOException, PaymentException {
        HttpClientUtils.HttpClientConfig clientConfig = new HttpClientUtils.HttpClientConfig();
        clientConfig.keepAliveInSecs = SystemProperty.getInt(SystemPropertyEntities.Payments_PAYPAL.KEEP_ALIVE_IN_SEC);
        clientConfig.maxConnections = SystemProperty.getInt(SystemPropertyEntities.Payments_PAYPAL.MAX_CONNECTIONS);
        clientConfig.strictHttpsCertCheck = SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL.STRICT_HTTPS_CERT_CHECK);
        clientConfig.timeOutInMillis = SystemProperty.getInt(SystemPropertyEntities.Payments_PAYPAL.TIME_OUT_IN_MILLIS);
        String responseData = null;
        String url = SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.VENDOR_BASE_URL) + SystemProperty.get(SystemPropertyEntities.Payments_PAYPAL.SET_EXPRESS_CHECKOUT_URL);
        boolean enableGatewayRequestResponseLogging = SystemProperty.getBool(SystemPropertyEntities.Payments_PAYPAL.ENABLE_GATEWAY_REQUEST_RESPONSE_LOGGING);
        try {
            HttpPost postRequest = new HttpPost(url);
            postRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
            postRequest.setEntity((HttpEntity)new StringEntity(requestData, UTF_8.name()));
            responseData = PaypalPayment.getResponseString(clientConfig, postRequest);
            if (enableGatewayRequestResponseLogging) {
                log.info((Object)("URI:[" + url + "]\nRequest:[" + requestData + "]\nResponse:[" + responseData + "]"));
            }
        }
        catch (PaymentException ex) {
            PaypalPayment.logOutcomes(url, requestData, responseData, (Exception)((Object)ex));
            throw ex;
        }
        catch (Exception ex) {
            PaypalPayment.logOutcomes(url, requestData, responseData, ex);
            IOException ioEx = new IOException("Unable to complete request to vendor gateway");
            ioEx.initCause(ex);
            throw ioEx;
        }
        return responseData;
    }

    private static String getResponseString(HttpClientUtils.HttpClientConfig clientConfig, HttpPost postRequest) throws PaymentException {
        String responseData;
        try {
            responseData = HttpClientUtils.getResponseString(clientConfig, (HttpUriRequest)postRequest, UTF_8);
        }
        catch (GeneralSecurityException e) {
            log.error((Object)"security exception occured while making paypal payment", (Throwable)e);
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.ERROR, PAYPAL_PAYMENT_ERROR_MESSAGE);
        }
        catch (IOException e) {
            log.error((Object)"unable to send request to paypal", (Throwable)e);
            throw new PaymentException(e, ErrorCause.PaymentErrorReasonType.FAILED_TO_RETRIEVE_RESPONSE_FROM_VENDOR, e.getMessage());
        }
        if (responseData == null) {
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.INVALID_VENDOR_DATA, "Received a blank response");
        }
        return responseData;
    }

    @Override
    public <T extends PaymentData> PaymentIResponse approve(T paymentDetails, String username) throws PaymentException, Exception {
        return null;
    }

    @Override
    public <T extends PaymentData> PaymentIResponse reject(T paymentDetails, String username) throws PaymentException, Exception {
        return null;
    }
}

