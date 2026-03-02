/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.payment;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.JSONUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentDataFactory;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.PaymentFactory;
import com.projectgoth.fusion.payment.PaymentIResponse;
import com.projectgoth.fusion.payment.PaymentInterface;
import com.projectgoth.fusion.payment.paypal.PaypalPayment;
import com.projectgoth.fusion.payment.paypal.PaypalTransactionTokenData;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/payment")
public class PaymentResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PaymentResource.class));

    @GET
    @Path(value="/credit_card")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Vector> getCreditCardTransactions(@QueryParam(value="startDate") String startDate, @QueryParam(value="endDate") String endDate, @QueryParam(value="sortBy") String sortBy, @QueryParam(value="sortOrder") String sortOrder, @QueryParam(value="showAuth") String showAuth, @QueryParam(value="showPend") String showPend, @QueryParam(value="showRej") String showRej, @QueryParam(value="username") String username, @QueryParam(value="displayLimit") @DefaultValue(value="30") int displayLimit) throws FusionRestException {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            Vector transactions = accountEJB.getCreditCardTransactions(startDate, endDate, sortBy, sortOrder, showAuth, showPend, showRej, username, displayLimit);
            return new DataHolder<Vector>(transactions);
        }
        catch (Exception e) {
            log.error((Object)"Exception while calling getCreditCardTransactions", (Throwable)e);
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/credit_card/hml")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public DataHolder<Vector> getCreditCardHMLTransactions(@QueryParam(value="startDate") String startDate, @QueryParam(value="endDate") String endDate, @QueryParam(value="sortBy") String sortBy, @QueryParam(value="sortOrder") String sortOrder, @QueryParam(value="showAuth") String showAuth, @QueryParam(value="showPend") String showPend, @QueryParam(value="showRej") String showRej, @QueryParam(value="username") String username, @QueryParam(value="displayLimit") @DefaultValue(value="30") int displayLimit) throws FusionRestException {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            Vector transactions = accountEJB.getCreditCardHMLTransactions(startDate, endDate, sortBy, sortOrder, showAuth, showPend, showRej, username, displayLimit);
            return new DataHolder<Vector>(transactions);
        }
        catch (Exception e) {
            log.error((Object)"Exception while calling getCreditCardTransactions", (Throwable)e);
            throw new FusionRestException(101, e.getMessage());
        }
    }

    public static boolean isPaymentVendorAccessibleFromViewType(PaymentData.TypeEnum paymentVendorTypeEnum, PaymentData.ViewEnum paymentViewTypeEnum) {
        SystemPropertyEntities.Payments enabledVendorsKeyEnum = SystemPropertyEntities.Payments.getSystemPropertyKeyForEnabledVendors(paymentViewTypeEnum.name());
        if (enabledVendorsKeyEnum != null) {
            String[] availablePaymentVendorList;
            for (String availablePaymentVendorStr : availablePaymentVendorList = SystemProperty.getArray(enabledVendorsKeyEnum)) {
                PaymentData.TypeEnum availablePaymentVendorTypeEnum = PaymentData.TypeEnum.fromCode(availablePaymentVendorStr);
                if (availablePaymentVendorTypeEnum != null) {
                    if (availablePaymentVendorTypeEnum != paymentVendorTypeEnum) continue;
                    return true;
                }
                log.warn((Object)(availablePaymentVendorStr + " is not known! Consider amending or removing it from the system property key " + enabledVendorsKeyEnum));
            }
        } else {
            log.info((Object)("Enabled-vendor-system property key for view type [" + paymentViewTypeEnum.name() + "] is not available/cannot be determined"));
        }
        return false;
    }

    @PUT
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="/{paymentVendorType}")
    public DataHolder<Map<String, Object>> createPayment(@PathParam(value="paymentVendorType") String paymentVendorTypeStr, String jsonDataString) throws FusionRestException {
        DataHolder<Map<String, Object>> result;
        try {
            boolean thirdPartyPaymentIsEnabled = SystemProperty.getBool(SystemPropertyEntities.Payments.THIRD_PARTY_PAYMENT_ENABLED);
            if (!thirdPartyPaymentIsEnabled) {
                throw new FusionRestException(FusionRestException.RestException.THIRD_PARTY_PAYMENT_DISABLED);
            }
            if (paymentVendorTypeStr == null) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment vendor type not supplied");
            }
            PaymentData.TypeEnum paymentVendorTypeEnum = PaymentData.TypeEnum.fromCode(paymentVendorTypeStr);
            if (paymentVendorTypeEnum == null) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_PAYMENT_VENDOR, "Payment vendor type is not supported " + paymentVendorTypeStr);
            }
            JSONObject jsonObject = new JSONObject(jsonDataString);
            String paymentViewTypeCode = StringUtil.trimmedLowerCase(JSONUtils.getString(jsonObject, "view"));
            if (StringUtil.isBlank(paymentViewTypeCode)) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment view type is blank");
            }
            PaymentData.ViewEnum paymentViewTypeEnum = PaymentData.ViewEnum.fromName(paymentViewTypeCode);
            if (paymentViewTypeEnum == null) {
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "Payment view is unsupported " + paymentViewTypeCode);
            }
            boolean accessAllowed = PaymentResource.isPaymentVendorAccessibleFromViewType(paymentVendorTypeEnum, paymentViewTypeEnum);
            if (!accessAllowed) {
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "Payment via " + paymentVendorTypeEnum.code().toUpperCase() + " is not accessible for this view type " + paymentViewTypeEnum.value());
            }
            PaymentInterface paymentDAO = PaymentFactory.getPaymentDAO(paymentVendorTypeEnum);
            if (paymentDAO == null) {
                log.error((Object)"Sanity check failed. PaymentDAO should have been assigned");
                throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
            }
            result = new DataHolder<Map<String, Object>>(paymentDAO.clientInitiatePayment(jsonObject));
        }
        catch (JSONException ex) {
            log.error((Object)("Invalid JSON Data:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA);
        }
        catch (CreateException ex) {
            log.error((Object)("EJB create failure. JSON Data:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.ERROR, ex.getMessage());
        }
        catch (PaymentException ex) {
            log.error((Object)("PaymentException occurred. JSON Data:" + jsonDataString), (Throwable)((Object)ex));
            throw new FusionRestException(FusionRestException.RestException.ERROR, ex.getMessage());
        }
        catch (FusionRestException ex) {
            log.error((Object)("FusionRestException occurred [" + ex.getMessage() + "] " + ". JSON Data:" + jsonDataString), (Throwable)ex);
            throw ex;
        }
        catch (Exception ex) {
            log.error((Object)("Unhandled exception clientInitatePayment [" + ex.getMessage() + "] " + ". JSON Data:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to initiate payment via vendor '" + paymentVendorTypeStr + "'");
        }
        return result;
    }

    @POST
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="/{paymentVendorType}")
    public DataHolder<Map<String, Object>> updatePayment(@PathParam(value="paymentVendorType") String paymentVendorTypeStr, String jsonDataString) throws FusionRestException {
        DataHolder<Map<String, Object>> result;
        log.info((Object)("Received paymentVendorType=[" + paymentVendorTypeStr + "] " + "jsonDataString=" + jsonDataString));
        try {
            if (paymentVendorTypeStr == null) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment vendor type not supplied");
            }
            PaymentData.TypeEnum paymentVendorTypeEnum = PaymentData.TypeEnum.fromCode(paymentVendorTypeStr);
            if (paymentVendorTypeEnum == null) {
                throw new FusionRestException(FusionRestException.RestException.INVALID_PAYMENT_VENDOR, "Payment vendor type is not supported " + paymentVendorTypeStr);
            }
            PaymentInterface paymentDAO = PaymentFactory.getPaymentDAO(paymentVendorTypeEnum);
            JSONObject jsonObject = new JSONObject(jsonDataString);
            result = new DataHolder<Map<String, Object>>(paymentDAO.updatePaymentStatus(jsonObject));
        }
        catch (JSONException ex) {
            log.error((Object)("Invalid json data input : " + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA);
        }
        catch (PaymentException ex) {
            log.error((Object)"Error in updating payment.", (Throwable)((Object)ex));
            throw new FusionRestException(FusionRestException.RestException.ERROR, ex.getMessage());
        }
        catch (FusionRestException ex) {
            log.error((Object)("Processing failed. Input:" + jsonDataString));
            throw ex;
        }
        catch (Exception ex) {
            log.error((Object)("Unhandled exception updatePaymentStatus.Input:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @PUT
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="/PAYPAL")
    public DataHolder<String> createPayment(String jsonDataString) throws FusionRestException {
        return this.createPayment(PaymentData.TypeEnum.fromCode("PAYPAL"), jsonDataString);
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="/PAYPAL/{token}")
    public DataHolder<String> getPaypalPaymentDetails(@PathParam(value="token") String tokenStr, @QueryParam(value="view") String viewStr) throws FusionRestException {
        return this.initiatePaymentAuthorization(PaymentData.TypeEnum.fromCode("PAYPAL"), tokenStr, viewStr, null);
    }

    @PUT
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="/v2/{paymentVendorType}")
    public DataHolder<String> createPaymentV2(@PathParam(value="paymentVendorType") String paymentVendorTypeStr, String jsonRequestData) throws FusionRestException {
        if (StringUtil.isBlank(paymentVendorTypeStr)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment vendor type not supplied");
        }
        PaymentData.TypeEnum paymentVendorTypeEnum = PaymentData.TypeEnum.fromCode(paymentVendorTypeStr);
        if (paymentVendorTypeEnum == null) {
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYMENT_VENDOR, "Payment vendor type is not supported " + paymentVendorTypeStr);
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonRequestData);
            return this.createPayment(paymentVendorTypeEnum, jsonObj.getString("data"));
        }
        catch (JSONException e) {
            log.warn((Object)("Improper payload format: " + jsonRequestData));
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA, "You may have accessd the feature improperly. Please contact merchant@mig.me.");
        }
    }

    @POST
    @Produces(value={"application/json"})
    @Path(value="/v2/{paymentVendorType}/{token}")
    public DataHolder<String> initiatePaymentAuthorization(@PathParam(value="paymentVendorType") String paymentVendorTypeStr, @PathParam(value="token") String tokenStr, String jsonRequestData) throws FusionRestException {
        if (StringUtil.isBlank(paymentVendorTypeStr)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment vendor type not supplied");
        }
        PaymentData.TypeEnum paymentVendorTypeEnum = PaymentData.TypeEnum.fromCode(paymentVendorTypeStr);
        if (paymentVendorTypeEnum == null) {
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYMENT_VENDOR, "Payment vendor type is not supported " + paymentVendorTypeStr);
        }
        JSONObject jsonData = null;
        String view = null;
        try {
            JSONObject jsonObj = new JSONObject(jsonRequestData);
            jsonData = jsonObj.getJSONObject("data");
            view = jsonData.getString("view");
        }
        catch (JSONException e) {
            log.warn((Object)("Invalid json format :: " + e.getMessage()));
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA);
        }
        return this.initiatePaymentAuthorization(paymentVendorTypeEnum, tokenStr, view, jsonData);
    }

    @POST
    @Produces(value={"application/json"})
    @Path(value="/{paymentId}/approve")
    public DataHolder<String> approvePayment(@PathParam(value="paymentId") Integer paymentIdStr, String requestData) throws FusionRestException {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            PaymentData paymentDetails = null;
            try {
                paymentDetails = accountEJB.getPaymentById(paymentIdStr);
            }
            catch (EJBException e) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to retrieve payment with id : " + paymentIdStr);
            }
            if (null == paymentDetails) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Payment does not exist.");
            }
            paymentDetails.accountEntrySource = new AccountEntrySourceData(PaymentResource.class);
            PaymentInterface paymentDAO = PaymentFactory.getPaymentDAO(paymentDetails.vendorType);
            String username = null;
            try {
                JSONObject jsonRequestData = new JSONObject(requestData);
                if (jsonRequestData.has("username")) {
                    username = jsonRequestData.getString("username");
                }
            }
            catch (JSONException e) {
                log.info((Object)("No staff name provided for approving payment with id [" + paymentIdStr + "]"));
            }
            PaymentIResponse response = paymentDAO.approve(paymentDetails, username);
            if (response != null) {
                return new DataHolder<String>(response.toJSON(PaymentIResponse.ReturnType.APPROVE));
            }
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "An error occurred in the request.");
        }
        catch (CreateException e) {
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (PaymentException e) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        catch (FusionRestException e) {
            throw e;
        }
        catch (Exception e) {
            log.warn((Object)"Unhandled exception in approving credit card", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "An error occurred in your request.");
        }
    }

    @POST
    @Produces(value={"application/json"})
    @Path(value="/{paymentId}/reject")
    public DataHolder<String> rejectPayment(@PathParam(value="paymentId") Integer paymentIdStr, String requestData) throws FusionRestException {
        try {
            AccountLocal accountEJB = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            PaymentData paymentDetails = null;
            try {
                paymentDetails = accountEJB.getPaymentById(paymentIdStr);
            }
            catch (EJBException e) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to retrieve payment with id : " + paymentIdStr);
            }
            if (null == paymentDetails) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Payment does not exist.");
            }
            paymentDetails.accountEntrySource = new AccountEntrySourceData(PaymentResource.class);
            PaymentInterface paymentDAO = PaymentFactory.getPaymentDAO(paymentDetails.vendorType);
            String username = null;
            try {
                JSONObject jsonRequestData = new JSONObject(requestData);
                if (jsonRequestData.has("username")) {
                    username = jsonRequestData.getString("username");
                }
            }
            catch (JSONException e) {
                log.info((Object)("No staff name provided for rejecting payment with id [" + paymentIdStr + "]"));
            }
            PaymentIResponse response = paymentDAO.reject(paymentDetails, username);
            if (response != null) {
                return new DataHolder<String>(response.toJSON(PaymentIResponse.ReturnType.REJECT));
            }
            throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, "An error occurred in the request.");
        }
        catch (CreateException e) {
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (PaymentException e) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        catch (FusionRestException e) {
            throw e;
        }
        catch (Exception e) {
            log.warn((Object)"Unhandled exception in approving credit card", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "An error occurred in your request.");
        }
    }

    private DataHolder<String> createPayment(PaymentData.TypeEnum paymentVendorTypeEnum, String jsonDataString) throws FusionRestException {
        try {
            boolean thirdPartyPaymentIsEnabled = SystemProperty.getBool(SystemPropertyEntities.Payments.THIRD_PARTY_PAYMENT_ENABLED);
            if (!thirdPartyPaymentIsEnabled) {
                throw new FusionRestException(FusionRestException.RestException.THIRD_PARTY_PAYMENT_DISABLED);
            }
            JSONObject jsonObject = new JSONObject(jsonDataString);
            String paymentViewTypeCode = StringUtil.trimmedLowerCase(JSONUtils.getString(jsonObject, "view"));
            if (StringUtil.isBlank(paymentViewTypeCode)) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment view type is blank");
            }
            PaymentData.ViewEnum paymentViewTypeEnum = PaymentData.ViewEnum.fromName(paymentViewTypeCode);
            if (paymentViewTypeEnum == null) {
                log.warn((Object)("Attempt to access " + paymentViewTypeEnum.name() + " from an invalid view: " + paymentViewTypeCode));
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "You may have accessed this page improperly. Please contact merchant@mig.me.");
            }
            boolean accessAllowed = PaymentResource.isPaymentVendorAccessibleFromViewType(PaymentData.TypeEnum.PAYPAL, paymentViewTypeEnum);
            if (!accessAllowed) {
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "Payment via " + PaymentData.TypeEnum.PAYPAL.code().toUpperCase() + " is not accessible for this view type " + paymentViewTypeEnum.value());
            }
            PaymentInterface paymentDAO = PaymentFactory.getPaymentDAO(paymentVendorTypeEnum);
            if (paymentDAO != null) {
                Object paymentDetails = PaymentDataFactory.getPayment(paymentVendorTypeEnum, jsonObject);
                PaymentIResponse response = paymentDAO.clientInitiatePayment(paymentDetails);
                if (response != null) {
                    return new DataHolder<String>(response.toJSON(PaymentIResponse.ReturnType.CREATE));
                }
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, new Object[0]);
            }
            log.error((Object)"Sanity check failed. PaymentDAO should have been assigned");
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (JSONException ex) {
            log.error((Object)("Invalid JSON Data:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA);
        }
        catch (PaymentException ex) {
            log.error((Object)("PaymentException occurred. JSON Data:" + jsonDataString), (Throwable)((Object)ex));
            throw new FusionRestException(FusionRestException.RestException.ERROR, ex.getMessage());
        }
        catch (FusionRestException ex) {
            log.error((Object)("FusionRestException occurred [" + ex.getMessage() + "] " + ". JSON Data:" + jsonDataString), (Throwable)ex);
            throw ex;
        }
        catch (Exception ex) {
            log.error((Object)("Unhandled exception clientInitatePayment [" + ex.getMessage() + "] " + ". JSON Data:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to initiate payment via vendor '" + PaymentData.TypeEnum.PAYPAL.code() + "'");
        }
    }

    private DataHolder<String> initiatePaymentAuthorization(PaymentData.TypeEnum paymentVendorTypeEnum, String tokenStr, String viewStr, JSONObject jsonParams) throws FusionRestException {
        try {
            boolean thirdPartyPaymentIsEnabled = SystemProperty.getBool(SystemPropertyEntities.Payments.THIRD_PARTY_PAYMENT_ENABLED);
            if (!thirdPartyPaymentIsEnabled) {
                throw new FusionRestException(FusionRestException.RestException.THIRD_PARTY_PAYMENT_DISABLED);
            }
            if (StringUtil.isBlank(viewStr)) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment view type is blank");
            }
            PaymentData.ViewEnum paymentViewTypeEnum = PaymentData.ViewEnum.fromName(viewStr);
            if (paymentViewTypeEnum == null) {
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "Payment view is unsupported " + viewStr);
            }
            boolean accessAllowed = PaymentResource.isPaymentVendorAccessibleFromViewType(PaymentData.TypeEnum.PAYPAL, paymentViewTypeEnum);
            if (!accessAllowed) {
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "Payment via " + PaymentData.TypeEnum.PAYPAL.code().toUpperCase() + " is not accessible for this view type " + paymentViewTypeEnum.value());
            }
            PaymentInterface paymentDAO = PaymentFactory.getPaymentDAO(paymentVendorTypeEnum);
            if (paymentDAO != null) {
                PaymentIResponse response = paymentDAO.onPaymentAuthorized(tokenStr, jsonParams);
                if (response != null) {
                    return new DataHolder<String>(response.toJSON(PaymentIResponse.ReturnType.GET_COMPACT_DETAILS));
                }
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, new Object[0]);
            }
            log.error((Object)"Sanity check failed. PaymentDAO should have been assigned");
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (PaymentException ex) {
            log.error((Object)"PaymentException occurred. JSON Data:", (Throwable)((Object)ex));
            throw new FusionRestException(FusionRestException.RestException.ERROR, ex.getMessage());
        }
        catch (FusionRestException ex) {
            log.error((Object)("FusionRestException occurred [" + ex.getMessage() + "] "), (Throwable)ex);
            throw ex;
        }
        catch (Exception ex) {
            log.error((Object)("Unhandled exception clientInitatePayment [" + ex.getMessage() + "] "), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to initiate payment via vendor '" + PaymentData.TypeEnum.PAYPAL.code() + "'");
        }
    }

    @POST
    @Produces(value={"application/json"})
    @Path(value="/PAYPAL/{token}")
    public DataHolder<String> updatePaypalPayment(@PathParam(value="token") String tokenStr, String jsonDataString) throws FusionRestException {
        try {
            boolean thirdPartyPaymentIsEnabled = SystemProperty.getBool(SystemPropertyEntities.Payments.THIRD_PARTY_PAYMENT_ENABLED);
            if (!thirdPartyPaymentIsEnabled) {
                throw new FusionRestException(FusionRestException.RestException.THIRD_PARTY_PAYMENT_DISABLED);
            }
            JSONObject jsonObject = new JSONObject(jsonDataString);
            String paymentViewTypeCode = StringUtil.trimmedLowerCase(JSONUtils.getString(jsonObject, "view"));
            if (StringUtil.isBlank(paymentViewTypeCode)) {
                throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment view type is blank");
            }
            PaymentData.ViewEnum paymentViewTypeEnum = PaymentData.ViewEnum.fromName(paymentViewTypeCode);
            if (paymentViewTypeEnum == null) {
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "Payment view is unsupported " + paymentViewTypeCode);
            }
            boolean accessAllowed = PaymentResource.isPaymentVendorAccessibleFromViewType(PaymentData.TypeEnum.PAYPAL, paymentViewTypeEnum);
            if (!accessAllowed) {
                throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE, "Payment via " + PaymentData.TypeEnum.PAYPAL.code().toUpperCase() + " is not accessible for this view type " + paymentViewTypeEnum.value());
            }
            PaymentInterface paymentDAO = PaymentFactory.getPaymentDAO(PaymentData.TypeEnum.PAYPAL);
            jsonObject.put("type", PaymentData.TypeEnum.PAYPAL.value());
            PaypalTransactionTokenData paypalTransactionTokenData = PaypalPayment.getTransactionTokenDataFromCache(tokenStr, new Timestamp(System.currentTimeMillis()));
            if (paypalTransactionTokenData == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "You do not have an existing payment request.");
            }
            jsonObject.put("token", (Object)tokenStr);
            jsonObject.put("currency", (Object)paypalTransactionTokenData.currency);
            jsonObject.put("amount", paypalTransactionTokenData.amount);
            jsonObject.put("username", (Object)paypalTransactionTokenData.username);
            jsonObject.put("id", (Object)paypalTransactionTokenData.paymentID);
            if (paymentDAO != null) {
                Object paymentData = PaymentDataFactory.getPayment(jsonObject);
                PaymentIResponse response = paymentDAO.updatePaymentStatus(paymentData);
                if (response != null) {
                    return new DataHolder<String>(response.toJSON(PaymentIResponse.ReturnType.GET_COMPACT_DETAILS));
                }
                throw new PaymentException(ErrorCause.PaymentErrorReasonType.VENDOR_SYSTEM_ERROR, new Object[0]);
            }
            log.error((Object)"Sanity check failed. PaymentDAO should have been assigned");
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (JSONException ex) {
            log.error((Object)("Invalid JSON Data:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.INVALID_PAYLOAD_DATA);
        }
        catch (PaymentException ex) {
            log.error((Object)("PaymentException occurred. JSON Data:" + jsonDataString), (Throwable)((Object)ex));
            throw new FusionRestException(FusionRestException.RestException.ERROR, ex.getMessage());
        }
        catch (FusionRestException ex) {
            log.error((Object)("FusionRestException occurred [" + ex.getMessage() + "] " + ". JSON Data:" + jsonDataString), (Throwable)ex);
            throw ex;
        }
        catch (Exception ex) {
            log.error((Object)("Unhandled exception clientInitatePayment [" + ex.getMessage() + "] " + ". JSON Data:" + jsonDataString), (Throwable)ex);
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to initiate payment via vendor '" + PaymentData.TypeEnum.PAYPAL.code() + "'");
        }
    }

    @POST
    @Path(value="/approvecreditcardpayment")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> approveCreditCardPayment(@QueryParam(value="staffusername") String staffUsername, @QueryParam(value="creditcardpaymentid") int creditCardPaymentId, @QueryParam(value="ipaddress") String ipAddress, @QueryParam(value="sessionid") String sessionID, @QueryParam(value="mobiledevice") String mobileDevice, @QueryParam(value="useragent") String userAgent) throws FusionRestException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.approveCreditCardPayment(staffUsername, creditCardPaymentId, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in approvecreditcardpayment staffUsername:%s", staffUsername), (Throwable)e);
            throw new FusionRestException(101, "Unable to approvecreditcardpayment");
        }
    }

    @POST
    @Path(value="/rejectcreditcardpayment")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> rejectCreditCardPayment(@QueryParam(value="staffusername") String staffUsername, @QueryParam(value="creditcardpaymentid") int creditCardPaymentId, @QueryParam(value="reason") String reason, @QueryParam(value="ipaddress") String ipAddress, @QueryParam(value="sessionid") String sessionID, @QueryParam(value="mobiledevice") String mobileDevice, @QueryParam(value="useragent") String userAgent) throws FusionRestException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.rejectCreditCardPayment(staffUsername, creditCardPaymentId, reason, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent));
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in rejectcreditcardpayment staffUsername:%s", staffUsername), (Throwable)e);
            throw new FusionRestException(101, "Unable to rejectcreditcardpayment");
        }
    }

    @POST
    @Path(value="/{username}/credituserandsendsms")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> creditUserAndSendSMS(@PathParam(value="username") String username, @QueryParam(value="amountsent") double amountSent, @QueryParam(value="amountcredit") double amountCredit, @QueryParam(value="cashreceiptid") String cashReceiptID, @QueryParam(value="ipaddress") String ipAddress, @QueryParam(value="sessionid") String sessionID, @QueryParam(value="mobiledevice") String mobileDevice, @QueryParam(value="useragent") String userAgent) throws FusionRestException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            accountBean.creditAndNotifyUser(username, amountSent, amountCredit, cashReceiptID, new AccountEntrySourceData(ipAddress, sessionID, mobileDevice, userAgent), true);
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in credituserandsendsms username:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to credituserandsendsms");
        }
    }
}

