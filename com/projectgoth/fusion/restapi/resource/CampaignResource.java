/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedDistributedLock;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.CampaignData;
import com.projectgoth.fusion.data.CampaignParticipantData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/campaign")
public class CampaignResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CampaignResource.class));

    @GET
    @Path(value="/{campaignid}")
    @Produces(value={"application/json"})
    public DataHolder<CampaignData> getCampaignData(@PathParam(value="campaignid") int campaignid) throws FusionRestException {
        try {
            CampaignData campaignData = DAOFactory.getInstance().getCampaignDAO().getCampaignData(campaignid);
            return new DataHolder<CampaignData>(campaignData);
        }
        catch (DAOException daoe) {
            log.error((Object)"Exception occurred on getCampaignData", (Throwable)daoe);
            throw new FusionRestException(FusionRestException.RestException.ERROR, daoe.getMessage());
        }
    }

    @GET
    @Path(value="/{campaignid}/participant")
    @Produces(value={"application/json"})
    public DataHolder<CampaignParticipantData> getCampaignParticipant(@QueryParam(value="requestingUserid") int userid, @PathParam(value="campaignid") int campaignid) throws FusionRestException {
        try {
            CampaignParticipantData campaignParticipantData = DAOFactory.getInstance().getCampaignDAO().getCampaignParticipantData(userid, campaignid);
            return new DataHolder<CampaignParticipantData>(campaignParticipantData);
        }
        catch (DAOException daoe) {
            log.error((Object)"Exception occurred on getCampaignParticipant", (Throwable)daoe);
            throw new FusionRestException(FusionRestException.RestException.ERROR, daoe.getMessage());
        }
    }

    private void checkRateLimit(String keyToRateLimit, String rateLimitStr) throws FusionRestException {
        String rateLimit = rateLimitStr;
        try {
            MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CAMPAIGN.toString(), MemCachedKeyUtils.getFullKeyForKeySpace(MemCachedKeySpaces.RateLimitKeySpace.CAMPAIGN_RATE_LIMIT, String.format("%s:%s", this.getClass().getSimpleName(), keyToRateLimit)), rateLimit);
        }
        catch (MemCachedRateLimiter.LimitExceeded e) {
            throw new FusionRestException(FusionRestException.RestException.RATE_LIMIT, "Sorry you only do " + e.getPrettyMessage());
        }
        catch (MemCachedRateLimiter.FormatError e) {
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR, "Internal Error");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @POST
    @Path(value="/join")
    @Produces(value={"application/json"})
    public Response joinCampaign(DataHolder<CampaignParticipantData> dataholder) throws FusionRestException {
        if (dataholder == null) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid Campaign");
        }
        this.checkRateLimit("s:" + ((CampaignParticipantData)dataholder.data).getUserId(), SystemProperty.get(SystemPropertyEntities.CampaignSetting.DEFAULT_RATE_LIMIT));
        try {
            CampaignData campaignData = DAOFactory.getInstance().getCampaignDAO().getCampaignData(((CampaignParticipantData)dataholder.data).getCampaignId());
            if (campaignData == null || !campaignData.isActive()) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid Campaign");
            }
            CampaignParticipantData campaignParticipantData = DAOFactory.getInstance().getCampaignDAO().getCampaignParticipantData(((CampaignParticipantData)dataholder.data).getUserId(), ((CampaignParticipantData)dataholder.data).getCampaignId());
            if (campaignParticipantData != null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("userid %s already join campaign id:", ((CampaignParticipantData)dataholder.data).getUserId(), ((CampaignParticipantData)dataholder.data).getCampaignId()));
            }
            String lockKey = "campaign/" + ((CampaignParticipantData)dataholder.data).getCampaignId() + "/" + ((CampaignParticipantData)dataholder.data).getMobilePhone();
            try {
                MemCachedDistributedLock.getDistributedLock(lockKey);
                CampaignParticipantData campaignParticipantData2 = DAOFactory.getInstance().getCampaignDAO().getCampaignParticipantDataByMobilePhone(((CampaignParticipantData)dataholder.data).getMobilePhone(), ((CampaignParticipantData)dataholder.data).getCampaignId());
                if (campaignParticipantData2 != null) {
                    throw new FusionRestException(FusionRestException.RestException.DUPLICATE_MOBILE, "Duplicate Mobile");
                }
                DAOFactory.getInstance().getCampaignDAO().joinCampaign((CampaignParticipantData)dataholder.data);
                Object var7_7 = null;
            }
            catch (Throwable throwable) {
                Object var7_8 = null;
                MemCachedDistributedLock.releaseDistributedLock(lockKey);
                throw throwable;
            }
            MemCachedDistributedLock.releaseDistributedLock(lockKey);
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (DAOException daoe) {
            log.error((Object)"Exception occurred on joinCampaign", (Throwable)daoe);
            throw new FusionRestException(FusionRestException.RestException.ERROR, daoe.getMessage());
        }
    }
}

