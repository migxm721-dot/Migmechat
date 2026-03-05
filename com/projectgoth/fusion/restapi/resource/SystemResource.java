/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.payment.PaymentData;
import com.projectgoth.fusion.payment.PaymentException;
import com.projectgoth.fusion.payment.PaymentFactory;
import com.projectgoth.fusion.payment.PaymentInterface;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.slice.RegistryAdminPrx;
import com.projectgoth.fusion.slice.RegistryStats;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.CreateException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/system")
public class SystemResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SystemResource.class));
    private static LazyLoader<String> StatsBuilder = new LazyLoader<String>("SystemResource.StatsBuilder", 60000L){

        @Override
        protected String fetchValue() throws Exception {
            try {
                JSONObject json = new JSONObject();
                RegistryAdminPrx registryAdminPrx = EJBIcePrxFinder.getRegistryAdmin();
                RegistryStats stats = registryAdminPrx.getStats();
                json.put("users", stats.numUserProxies);
                json.put("maxusers", stats.maxUserProxies);
                json.put("groups", stats.numGroupChatProxies);
                json.put("maxgroups", stats.maxGroupChatProxies);
                json.put("rooms", stats.numChatRoomProxies);
                json.put("maxrooms", stats.maxChatRoomProxies);
                return json.toString();
            }
            catch (Exception e) {
                log.error((Object)("Error updating system status:" + e.getMessage()), (Throwable)e);
                return "";
            }
        }
    };

    @GET
    @Path(value="/property/{namespace}/{propertyname}")
    @Produces(value={"application/json"})
    public DataHolder<String> getProperty(@PathParam(value="namespace") String namespace, @PathParam(value="propertyname") String propertyName, @QueryParam(value="default") String defaultValue) throws FusionRestException {
        String value;
        boolean isDefaultNS = "default".equalsIgnoreCase(namespace);
        String namespacedPropertyName = isDefaultNS ? propertyName : SystemPropertyEntities.getNameWithNamespace(namespace, propertyName);
        SystemPropertyEntities.SystemPropertyEntryInterface entry = SystemPropertyEntities.getEntry(namespacedPropertyName);
        if (entry == null) {
            if (isDefaultNS) {
                value = SystemProperty.get(propertyName, defaultValue);
                if (value == null && defaultValue == null) {
                    throw new FusionRestException(FusionRestException.RestException.UNKNOWN_SYSTEM_PROPERTY, String.format("Unrecogized default system property %s", propertyName));
                }
            } else {
                value = SystemProperty.get(namespacedPropertyName, defaultValue);
                if (value == null && defaultValue == null) {
                    throw new FusionRestException(FusionRestException.RestException.UNKNOWN_SYSTEM_PROPERTY, String.format("Unrecogized non-default system property %s:%s", namespace, propertyName));
                }
            }
        } else {
            value = SystemProperty.get(entry);
        }
        return new DataHolder<String>(value);
    }

    @GET
    @Path(value="/payment/options")
    @Produces(value={"application/json"})
    public DataHolder<String[]> getPaymentOptions(@QueryParam(value="view") String viewStr, @QueryParam(value="username") String username) throws FusionRestException {
        if (StringUtil.isBlank(viewStr)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "view param not supplied");
        }
        if (StringUtil.isBlank(username)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "username param not supplied");
        }
        PaymentData.ViewEnum view = PaymentData.ViewEnum.fromName(viewStr);
        if (view != null) {
            try {
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userEJB.loadUser(username, false, false);
                if (userData == null) {
                    throw new FusionRestException(FusionRestException.RestException.UNKNOWN_USER_NAME);
                }
                SystemPropertyEntities.Payments availPaymentVendorListSystemKey = SystemPropertyEntities.Payments.getSystemPropertyKeyForEnabledVendors(view.name());
                ArrayList<String> returnOptions = new ArrayList<String>();
                if (availPaymentVendorListSystemKey != null) {
                    String[] paymentsAvailable = SystemProperty.getArray(availPaymentVendorListSystemKey);
                    if (paymentsAvailable != null) {
                        for (String paymentType : paymentsAvailable) {
                            try {
                                PaymentInterface pDAO = PaymentFactory.getPaymentDAO(paymentType);
                                if (!pDAO.isAccessAllowed(userData)) continue;
                                returnOptions.add(paymentType);
                            }
                            catch (PaymentException paymentException) {
                                ErrorCause cause = paymentException.getErrorCause();
                                if (cause == ErrorCause.PaymentErrorReasonType.UNKNOWN_PAYMENT_VENDOR) {
                                    log.error((Object)("[" + paymentType + "] is unknown. Consider amending or removing from system property " + availPaymentVendorListSystemKey));
                                    continue;
                                }
                                if (cause == ErrorCause.PaymentErrorReasonType.PAYMENT_INTERFACE_NOT_REGISTERED) {
                                    log.error((Object)("Misconfiguration? No payment interface is registered for payment type [" + paymentType + "]. Unable to check permission for username " + username));
                                    continue;
                                }
                                throw paymentException;
                            }
                        }
                    }
                } else {
                    log.warn((Object)("Enabled-vendors system property key for view [" + view.name() + "] is not available"));
                }
                return new DataHolder<String[]>(returnOptions.toArray(new String[returnOptions.size()]));
            }
            catch (FusionRestException e) {
                log.error((Object)("Unknown user [" + username + "]."), (Throwable)e);
                throw e;
            }
            catch (CreateException e) {
                log.error((Object)("Unable to get available views for view " + viewStr + " [" + username + "]."), (Throwable)e);
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to retrieve available payment options for " + viewStr);
            }
            catch (Exception ex) {
                log.error((Object)("Unable to get available views for view " + viewStr + " [" + username + "]."), (Throwable)ex);
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to retrieve available payment options for " + viewStr);
            }
        }
        throw new FusionRestException(FusionRestException.RestException.UNSUPPORTED_PAYMENT_VIEW_TYPE);
    }

    @GET
    @Path(value="/payment/currency")
    @Produces(value={"application/json"})
    public DataHolder<String> getPaymentOptionCurrency(@QueryParam(value="paymentType") String paymentTypeStr, @QueryParam(value="username") String username) throws FusionRestException {
        if (StringUtil.isBlank(paymentTypeStr)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Payment type is not supplied");
        }
        if (StringUtil.isBlank(username)) {
            throw new FusionRestException(FusionRestException.RestException.MANDATORY_FIELD_MISSING, "Username is not supplied");
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userEJB.loadUser(username, false, false);
            if (userData == null) {
                throw new FusionRestException(FusionRestException.RestException.UNKNOWN_USER_NAME);
            }
            PaymentInterface pDAO = null;
            try {
                pDAO = PaymentFactory.getPaymentDAO(paymentTypeStr);
            }
            catch (PaymentException ex) {
                ErrorCause cause = ex.getErrorCause();
                if (cause == ErrorCause.PaymentErrorReasonType.UNKNOWN_PAYMENT_VENDOR) {
                    throw new FusionRestException(FusionRestException.RestException.INVALID_PAYMENT_VENDOR);
                }
                throw ex;
            }
            if (!pDAO.isAccessAllowed(userData)) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unauthorized access.");
            }
            String currency = pDAO.getCurrencyForUser(userData);
            if (currency == null) {
                currency = SystemProperty.get(SystemPropertyEntities.Payments.DEFAULT_CURRENCY);
            }
            return new DataHolder<String>(currency);
        }
        catch (FusionRestException e) {
            log.error((Object)("Unknown user [" + username + "]."), (Throwable)e);
            throw e;
        }
        catch (CreateException e) {
            log.error((Object)("Unable to get currency for " + paymentTypeStr + " [" + username + "]."), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to continue with payment.");
        }
        catch (Exception e) {
            log.error((Object)("Unable to get currency for " + paymentTypeStr + " [" + username + "]."), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to continue with payment.");
        }
    }

    @GET
    @Path(value="/status")
    @Produces(value={"application/json"})
    public DataHolder<String> getStatus() throws FusionRestException {
        return new DataHolder<String>(StatsBuilder.getValue());
    }

    @GET
    @Path(value="/languagelist")
    @Produces(value={"application/json"})
    public DataHolder<List> getLanguageList() throws FusionRestException {
        try {
            Web webBean = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
            return new DataHolder<List>(webBean.getLanguages());
        }
        catch (Exception e) {
            log.error((Object)"Error in languagelist", (Throwable)e);
            throw new FusionRestException(101, "Unable to languagelist");
        }
    }
}

