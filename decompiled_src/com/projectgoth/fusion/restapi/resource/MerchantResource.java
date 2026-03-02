/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.BasicMerchantTagDetailsData;
import com.projectgoth.fusion.data.FullMerchantTagDetailsData;
import com.projectgoth.fusion.data.MerchantDetailsData;
import com.projectgoth.fusion.data.MerchantLocationData;
import com.projectgoth.fusion.data.MerchantTagData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.AccountLocal;
import com.projectgoth.fusion.interfaces.AccountLocalHome;
import com.projectgoth.fusion.interfaces.MerchantsLocal;
import com.projectgoth.fusion.interfaces.MerchantsLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.interfaces.WebLocal;
import com.projectgoth.fusion.interfaces.WebLocalHome;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.MerchantUsernameColorData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/merchant")
public class MerchantResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MerchantResource.class));

    @GET
    @Path(value="/{userid}/details")
    @Produces(value={"application/json"})
    public DataHolder<MerchantDetailsData> getMerchantDetails(@PathParam(value="userid") int userIdStr) throws FusionRestException {
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userEJB.getUsernameByUserid(userIdStr, null);
            MerchantDetailsData data = userEJB.getFullMerchantDetails(username);
            return new DataHolder<MerchantDetailsData>(data);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while fetching merchant details: " + e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @POST
    @Path(value="/{userid}/details/username_color")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<MerchantDetailsData> setUsernameColor(@PathParam(value="userid") Integer userId, DataHolder<MerchantUsernameColorData> jsonData) throws FusionRestException {
        try {
            MerchantDetailsData.UserNameColorTypeEnum colorType = MerchantDetailsData.UserNameColorTypeEnum.fromValue(((MerchantUsernameColorData)jsonData.data).color);
            if (colorType == null) {
                throw new Exception("Invalid value for color [" + ((MerchantUsernameColorData)jsonData.data).color + "]");
            }
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userEJB.getUsernameByUserid(userId, null);
            if (username == null) {
                throw new Exception("Unknown user [" + userId + "]");
            }
            MerchantDetailsData merchantData = userEJB.getFullMerchantDetails(username);
            if (!userEJB.setMerchantColorType(merchantData.id, colorType)) {
                throw new Exception("Unable to update username color type for user " + merchantData.userName);
            }
            merchantData.usernameColorType = colorType;
            return new DataHolder<MerchantDetailsData>(merchantData);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/{userid}/tags")
    @Produces(value={"application/json"})
    public DataHolder<Map<String, Object>> getMerchantTags(@PathParam(value="userid") String userIdStr, @QueryParam(value="page") String pageStr, @QueryParam(value="numRecords") String numRecordsStr) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, 0);
        if (userId <= 0) {
            throw new FusionRestException(101, String.format("Invalid userId provided.", new Object[0]));
        }
        int page = StringUtil.toIntOrDefault(pageStr, 1);
        if (page < 0) {
            throw new FusionRestException(101, String.format("Invalid page parameter [%s]", pageStr));
        }
        int numRecords = StringUtil.toIntOrDefault(numRecordsStr, 50);
        if (numRecords < 0) {
            throw new FusionRestException(101, String.format("Invalid number of records parameter [%s]", numRecordsStr));
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            Map details = userEJB.getTaggedUsers(userId, page, numRecords);
            return new DataHolder<Map<String, Object>>(details);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/{userid}/tags/expiring")
    @Produces(value={"application/json"})
    public DataHolder<Map<String, FullMerchantTagDetailsData>> getMerchantTags(@PathParam(value="userid") String userIdStr, @QueryParam(value="days") String daysStr) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, 0);
        if (userId <= 0) {
            throw new FusionRestException(101, String.format("Invalid userId provided.", new Object[0]));
        }
        int days = StringUtil.toIntOrDefault(daysStr, 7);
        if (days < 0) {
            throw new FusionRestException(101, String.format("Invalid page parameter [%s]", daysStr));
        }
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            Map details = userEJB.getExpiringTaggedUsers(userId, days);
            return new DataHolder<Map<String, FullMerchantTagDetailsData>>(details);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/tag/minimum/{countryId}")
    @Produces(value={"application/json"})
    public DataHolder<Map<String, Object>> getMinimumNonTopMerchantTagDetailsInCountry(@PathParam(value="countryId") String countryIdStr, @QueryParam(value="currency") String currency) throws FusionRestException {
        int countryId = 0;
        try {
            countryId = Integer.parseInt(countryIdStr);
        }
        catch (Exception e) {
            throw new FusionRestException(101, String.format("Invalid country id parameter [%s]", countryIdStr));
        }
        try {
            HashMap<String, Object> minTagDetails = new HashMap<String, Object>();
            minTagDetails.put("validity", MerchantTagData.TypeEnum.NON_TOP_MERCHANT_TAG.validity());
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            Map minimum = accountBean.getMinTopMerchantToNonTopMerchantTagAmount(countryId, currency);
            minTagDetails.put("amount", (Double)minimum.get("amount"));
            minTagDetails.put("currency", (String)minimum.get("currency"));
            return new DataHolder<Map<String, Object>>(minTagDetails);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/tag/top/minimum")
    @Produces(value={"application/json"})
    public DataHolder<Map<String, Object>> getMinimumTopMerchantTagDetails(@QueryParam(value="currency") String currency) throws FusionRestException {
        try {
            HashMap<String, Object> minTagDetails = new HashMap<String, Object>();
            minTagDetails.put("validity", MerchantTagData.TypeEnum.TOP_MERCHANT_TAG.validity());
            double minRequirementUSD = SystemProperty.getDouble("MinMerchantMerchantTagAmountUSD", 100.0);
            if (currency != null && !currency.equals("USD")) {
                AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
                minTagDetails.put("amount", accountBean.convertCurrency(minRequirementUSD, "USD", currency));
                minTagDetails.put("currency", currency);
            } else {
                minTagDetails.put("amount", minRequirementUSD);
                minTagDetails.put("currency", "USD");
            }
            return new DataHolder<Map<String, Object>>(minTagDetails);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/tag/{username}")
    @Produces(value={"application/json"})
    public DataHolder<MerchantTagData> getTagFromUsername(@PathParam(value="username") String username) throws FusionRestException {
        try {
            AccountLocal accountBean = (AccountLocal)EJBHomeCache.getLocalObject("AccountLocal", AccountLocalHome.class);
            BasicMerchantTagDetailsData tag = accountBean.getMerchantTagFromUsername(null, username, true);
            return new DataHolder<MerchantTagData>(tag);
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
            throw new FusionRestException(101, e.getMessage());
        }
    }

    @GET
    @Path(value="/country/{id}")
    @Produces(value={"application/json"})
    public DataHolder<List<MerchantLocationData>> getMerchantByCountryId(@QueryParam(value="requestingUserid") int requestingUserid, @PathParam(value="id") int id, @QueryParam(value="offset") int offset, @QueryParam(value="limit") int limit) throws FusionRestException {
        try {
            MerchantsLocal merchantsBean = (MerchantsLocal)EJBHomeCache.getLocalObject("MerchantsLocal", MerchantsLocalHome.class);
            List results = merchantsBean.getMerchantsByCountry(requestingUserid, id, offset, limit, true);
            return new DataHolder<List<MerchantLocationData>>(results);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while getting merchant location " + e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal Server Error: Could not fetch merchant location.");
        }
    }

    @GET
    @Path(value="/country/search")
    @Produces(value={"application/json"})
    public DataHolder<List<MerchantLocationData>> getMerchantByCountry(@QueryParam(value="requestingUserid") int requestingUserid, @QueryParam(value="name") String name, @QueryParam(value="offset") int offset, @QueryParam(value="limit") int limit) throws FusionRestException {
        try {
            MerchantsLocal merchantsBean = (MerchantsLocal)EJBHomeCache.getLocalObject("MerchantsLocal", MerchantsLocalHome.class);
            List results = merchantsBean.getMerchantsByCountry(requestingUserid, name, offset, limit, true);
            return new DataHolder<List<MerchantLocationData>>(results);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while getting merchant location " + e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Internal Server Error: Could not fetch merchant location.");
        }
    }

    @GET
    @Path(value="/countries")
    @Produces(value={"application/json"})
    public DataHolder<List> getCountries() throws FusionRestException {
        try {
            WebLocal webBean = (WebLocal)EJBHomeCache.getLocalObject("WebLocal", WebLocalHome.class);
            webBean.getCountriesWithMerchants();
            return new DataHolder<List>(webBean.getCountriesWithMerchants());
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while removing user from blacklist: " + e.getMessage()));
            throw new FusionRestException(101, "Internal Server Error: Could not fetch merchant countries");
        }
    }

    @POST
    @Path(value="/{username}/resetmerchantpin")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> resetMerchantPin(@PathParam(value="username") String username) throws FusionRestException {
        try {
            Web webBean = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
            webBean.resetMerchantPin(username);
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in resetmerchantpin chatroom:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to resetmerchantpin");
        }
    }
}

