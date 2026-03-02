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
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RecommendationData;
import com.projectgoth.fusion.recommendation.delivery.Enums;
import com.projectgoth.fusion.recommendation.delivery.RecommendationDeliveryUtils;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/recommendation")
public class RecommendationResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecommendationResource.class));
    public static final String ERROR_MESSAGE_INVALID_LIMIT_PARAMETER = "please provide a valid limit";

    public static int parseLimit(String limitStr, Enums.RecommendationTypeEnum type, Enums.RecommendationTargetEnum target, SSOEnums.View viewType) {
        try {
            int limit;
            boolean useDefaultLimit = StringUtil.isBlank(limitStr);
            int n = limit = useDefaultLimit ? RecommendationDeliveryUtils.getDefaultResultSize(type, target, viewType) : Integer.parseInt(limitStr);
            if (limit < 0) {
                return -1;
            }
            int maxAllowedSize = RecommendationDeliveryUtils.getAllowedMaxResultSize(type, target, viewType);
            if (limit > maxAllowedSize) {
                if (useDefaultLimit) {
                    log.warn((Object)("default recommendation size [" + limit + "] is larger than max allowed [" + maxAllowedSize + "]"));
                } else {
                    log.warn((Object)("receive get recommendation request with limit:[" + limit + "] but max allowed is [" + maxAllowedSize + "]"));
                }
                return maxAllowedSize;
            }
            return limit;
        }
        catch (NumberFormatException nfe) {
            return -1;
        }
    }

    @GET
    @Path(value="/{recommendation_type}")
    @Produces(value={"application/json"})
    public DataHolder<RecommendationData> getRecommendation(@PathParam(value="recommendation_type") String typeStr, @QueryParam(value="targetType") String targetTypeStr, @QueryParam(value="targetID") Integer targetId, @QueryParam(value="subtype") String subtype, @QueryParam(value="limit") String limitStr, @QueryParam(value="offset") String offsetStr, @QueryParam(value="refreshCache") String refreshCacheStr, @QueryParam(value="view") String strView) throws FusionRestException {
        log.info((Object)String.format("getRecommendation(typeStr[%s],targetTypeStr=[%s],targetId=[%s],limitStr=[%s],offsetStr=[%s],refreshCacheStr=[%s],strView=[%s])", typeStr, targetTypeStr, targetId, limitStr, offsetStr, refreshCacheStr, strView));
        int offset = StringUtil.toIntOrDefault(offsetStr, 0);
        if (offset < 0) {
            throw new FusionRestException(101, "please provide a valid offset");
        }
        if (StringUtil.isBlank(typeStr)) {
            throw new FusionRestException(101, "recommendation type can not be null");
        }
        Enums.RecommendationTypeEnum recommendationType = Enums.RecommendationTypeEnum.fromValue(typeStr.toUpperCase());
        if (null == recommendationType) {
            throw new FusionRestException(101, "invalid recommendation type : " + typeStr);
        }
        if (StringUtil.isBlank(targetTypeStr)) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        Enums.RecommendationTargetEnum target = Enums.RecommendationTargetEnum.fromValue(targetTypeStr.toLowerCase());
        if (null == target) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        SSOEnums.View viewType = SSOEnums.View.fromValue(StringUtil.toIntOrDefault(strView, Integer.MIN_VALUE));
        int limit = RecommendationResource.parseLimit(limitStr, recommendationType, target, viewType);
        if (limit < 0) {
            throw new FusionRestException(101, ERROR_MESSAGE_INVALID_LIMIT_PARAMETER);
        }
        if (targetId == null) {
            throw new FusionRestException(101, "please provide a valid targetID");
        }
        boolean refreshCache = Boolean.parseBoolean(refreshCacheStr);
        try {
            RecommendationData recommendation = RecommendationDeliveryUtils.getRecommendation(recommendationType, target, targetId, subtype, limit, offset, refreshCache);
            return new DataHolder<RecommendationData>(recommendation);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error while retrieving recommendation for type %s targetType=%s, targetId=%d", typeStr, targetTypeStr, targetId), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving recommendations");
        }
    }

    @POST
    @Path(value="/{recommendation_type}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response updateRecommendation(@PathParam(value="recommendation_type") String typeStr, @QueryParam(value="targetType") String targetTypeStr, @QueryParam(value="targetID") Integer targetId, @QueryParam(value="replaceExisting") String replaceExistingStr, RecommendationData recommendationData) throws FusionRestException {
        if (StringUtil.isBlank(typeStr)) {
            throw new FusionRestException(101, "recommendation type can not be null");
        }
        Enums.RecommendationTypeEnum recommendationType = Enums.RecommendationTypeEnum.fromValue(typeStr.toUpperCase());
        if (null == recommendationType) {
            throw new FusionRestException(101, "invalid recommendation type : " + typeStr);
        }
        if (StringUtil.isBlank(targetTypeStr)) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        Enums.RecommendationTargetEnum target = Enums.RecommendationTargetEnum.fromValue(targetTypeStr.toLowerCase());
        if (null == target) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        if (targetId == null) {
            throw new FusionRestException(101, "please provide a valid targetID");
        }
        if (recommendationData == null) {
            throw new FusionRestException(101, "post body missing");
        }
        boolean replaceExisting = Boolean.parseBoolean(replaceExistingStr);
        if (recommendationData.getRecommendations() == null || recommendationData.getRecommendations().size() == 0) {
            throw new FusionRestException(101, "no recommendations found in post body");
        }
        log.info((Object)String.format("Updating recommendations for type %s targetType=%s, targetId=%d replaceExisting=%s", typeStr, targetTypeStr, targetId, replaceExisting ? "true" : "false"));
        try {
            RecommendationDeliveryUtils.updateRecommendation(recommendationData, recommendationType, target, targetId, replaceExisting);
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error while updating recommendation for type %s targetType=%s, targetId=%d", typeStr, targetTypeStr, targetId), (Throwable)e);
            throw new FusionRestException(101, "Internal error while updating recommendations");
        }
    }

    @GET
    @Path(value="/{recommendation_type}/featured")
    @Produces(value={"application/json"})
    public DataHolder<RecommendationData> getFeaturedRecommendation(@PathParam(value="recommendation_type") String typeStr, @QueryParam(value="targetType") String targetTypeStr, @QueryParam(value="targetID") Integer targetId, @QueryParam(value="limit") String limitStr, @QueryParam(value="offset") String offsetStr, @QueryParam(value="view") String strView) throws FusionRestException {
        log.info((Object)String.format("getFeaturedRecommendation(typeStr[%s],targetTypeStr=[%s],targetId=[%s],limitStr=[%s],offsetStr=[%s],strView=[%s])", typeStr, targetTypeStr, targetId, limitStr, offsetStr, strView));
        int offset = StringUtil.toIntOrDefault(offsetStr, 0);
        if (offset < 0) {
            throw new FusionRestException(101, "please provide a valid offset and limit");
        }
        if (StringUtil.isBlank(typeStr)) {
            throw new FusionRestException(101, "recommendation type can not be null");
        }
        Enums.RecommendationTypeEnum recommendationType = Enums.RecommendationTypeEnum.fromValue(typeStr.toUpperCase());
        if (null == recommendationType) {
            throw new FusionRestException(101, "invalid recommendation type : " + typeStr);
        }
        if (StringUtil.isBlank(targetTypeStr)) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        Enums.RecommendationTargetEnum target = Enums.RecommendationTargetEnum.fromValue(targetTypeStr.toLowerCase());
        if (null == target) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        SSOEnums.View viewType = SSOEnums.View.fromValue(StringUtil.toIntOrDefault(strView, Integer.MIN_VALUE));
        int limit = RecommendationResource.parseLimit(limitStr, recommendationType, target, viewType);
        if (limit < 0) {
            throw new FusionRestException(101, ERROR_MESSAGE_INVALID_LIMIT_PARAMETER);
        }
        if (targetId == null) {
            throw new FusionRestException(101, "please provide a valid targetID");
        }
        try {
            RecommendationData recommendation = RecommendationDeliveryUtils.getFeaturedRecommendation(recommendationType, target, targetId, limit, offset);
            return new DataHolder<RecommendationData>(recommendation);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error while retrieving featured recommendation for type %s targetType=%s, targetId=%d", typeStr, targetTypeStr, targetId), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving featured recommendations");
        }
    }

    @POST
    @Path(value="/{recommendation_type}/featured")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response updateFeaturedRecommendation(@PathParam(value="recommendation_type") String typeStr, @QueryParam(value="targetType") String targetTypeStr, @QueryParam(value="targetID") Integer targetId, @QueryParam(value="replaceExisting") String replaceExistingStr, String jsonBodyStr) throws FusionRestException {
        if (StringUtil.isBlank(typeStr)) {
            throw new FusionRestException(101, "recommendation type can not be null");
        }
        Enums.RecommendationTypeEnum recommendationType = Enums.RecommendationTypeEnum.fromValue(typeStr.toUpperCase());
        if (null == recommendationType) {
            throw new FusionRestException(101, "invalid recommendation type : " + typeStr);
        }
        if (StringUtil.isBlank(targetTypeStr)) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        Enums.RecommendationTargetEnum target = Enums.RecommendationTargetEnum.fromValue(targetTypeStr.toLowerCase());
        if (null == target) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        if (targetId == null) {
            throw new FusionRestException(101, "please provide a valid targetID");
        }
        if (StringUtil.isBlank(jsonBodyStr)) {
            throw new FusionRestException(101, "post body missing");
        }
        boolean replaceExisting = Boolean.parseBoolean(replaceExistingStr);
        ArrayList<String> items = new ArrayList<String>();
        try {
            JSONObject jsonBody = new JSONObject(jsonBodyStr);
            JSONArray jsonArr = jsonBody.getJSONArray("data");
            for (int i = 0; i < jsonArr.length(); ++i) {
                items.add(jsonArr.getString(i));
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to parse JSON Body: " + e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Unable to parse JSON Body :" + e.getMessage());
        }
        try {
            RecommendationDeliveryUtils.updateFeaturedRecommendation(items, recommendationType, target, targetId, replaceExisting);
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error while updating recommendation for type %s targetType=%s, targetId=%d", typeStr, targetTypeStr, targetId), (Throwable)e);
            throw new FusionRestException(101, "Internal error while updating recommendations");
        }
    }

    @GET
    @Path(value="/{recommendation_type}/blacklisted")
    @Produces(value={"application/json"})
    public DataHolder<RecommendationData> getBlacklistedRecommendation(@PathParam(value="recommendation_type") String typeStr, @QueryParam(value="targetType") String targetTypeStr, @QueryParam(value="targetID") Integer targetId, @QueryParam(value="limit") String limitStr, @QueryParam(value="offset") String offsetStr, @QueryParam(value="view") String strView) throws FusionRestException {
        int offset = StringUtil.toIntOrDefault(offsetStr, 0);
        if (offset < 0) {
            throw new FusionRestException(101, "please provide a valid offset");
        }
        if (StringUtil.isBlank(typeStr)) {
            throw new FusionRestException(101, "recommendation type can not be null");
        }
        Enums.RecommendationTypeEnum recommendationType = Enums.RecommendationTypeEnum.fromValue(typeStr.toUpperCase());
        if (null == recommendationType) {
            throw new FusionRestException(101, "invalid recommendation type : " + typeStr);
        }
        if (StringUtil.isBlank(targetTypeStr)) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        Enums.RecommendationTargetEnum target = Enums.RecommendationTargetEnum.fromValue(targetTypeStr.toLowerCase());
        if (null == target) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        SSOEnums.View viewType = SSOEnums.View.fromValue(StringUtil.toIntOrDefault(strView, Integer.MIN_VALUE));
        int limit = RecommendationResource.parseLimit(limitStr, recommendationType, target, viewType);
        if (limit < 0) {
            throw new FusionRestException(101, ERROR_MESSAGE_INVALID_LIMIT_PARAMETER);
        }
        if (targetId == null) {
            throw new FusionRestException(101, "please provide a valid targetID");
        }
        try {
            RecommendationData recommendation = RecommendationDeliveryUtils.getBlacklistedRecommendation(recommendationType, target, targetId, limit, offset);
            return new DataHolder<RecommendationData>(recommendation);
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error while retrieving blacklisted recommendation for type %s targetType=%s, targetId=%d", typeStr, targetTypeStr, targetId), (Throwable)e);
            throw new FusionRestException(101, "Internal error while retrieving blacklisted recommendations");
        }
    }

    @POST
    @Path(value="/{recommendation_type}/blacklisted")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response updateBlacklistedRecommendations(@PathParam(value="recommendation_type") String typeStr, @QueryParam(value="targetType") String targetTypeStr, @QueryParam(value="targetID") Integer targetId, @QueryParam(value="replaceExisting") String replaceExistingStr, String jsonBodyStr) throws FusionRestException {
        if (StringUtil.isBlank(typeStr)) {
            throw new FusionRestException(101, "recommendation type can not be null");
        }
        Enums.RecommendationTypeEnum recommendationType = Enums.RecommendationTypeEnum.fromValue(typeStr.toUpperCase());
        if (null == recommendationType) {
            throw new FusionRestException(101, "invalid recommendation type : " + typeStr);
        }
        if (StringUtil.isBlank(targetTypeStr)) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        Enums.RecommendationTargetEnum target = Enums.RecommendationTargetEnum.fromValue(targetTypeStr.toLowerCase());
        if (null == target) {
            throw new FusionRestException(101, "invalid target type : " + targetTypeStr);
        }
        if (targetId == null) {
            throw new FusionRestException(101, "please provide a valid targetID");
        }
        if (StringUtil.isBlank(jsonBodyStr)) {
            throw new FusionRestException(101, "post body missing");
        }
        boolean replaceExisting = Boolean.parseBoolean(replaceExistingStr);
        ArrayList<String> items = new ArrayList<String>();
        try {
            JSONObject jsonBody = new JSONObject(jsonBodyStr);
            JSONArray jsonArr = jsonBody.getJSONArray("data");
            for (int i = 0; i < jsonArr.length(); ++i) {
                items.add(jsonArr.getString(i));
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to parse JSON Body: " + e.getMessage()), (Throwable)e);
            throw new FusionRestException(101, "Unable to parse JSON Body :" + e.getMessage());
        }
        try {
            RecommendationDeliveryUtils.updateBlacklistedRecommendation(items, recommendationType, target, targetId, replaceExisting);
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)String.format("Unexpected error while updating recommendation for type %s targetType=%s, targetId=%d", typeStr, targetTypeStr, targetId), (Throwable)e);
            throw new FusionRestException(101, "Internal error while updating recommendations");
        }
    }
}

