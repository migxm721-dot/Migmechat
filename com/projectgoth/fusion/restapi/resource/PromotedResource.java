/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.PromotedPostData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/promoted")
public class PromotedResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PromotedResource.class));

    @POST
    @Path(value="/post")
    @Consumes(value={"application/json"})
    public void updatePromotedPost(List<PromotedPostData> promotedPostDatas) throws FusionRestException {
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.updatePromotedPost(promotedPostDatas);
        }
        catch (Exception e) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unabled to update promoted post");
        }
    }

    @GET
    @Path(value="/post")
    @Produces(value={"application/json"})
    public List<PromotedPostData> getPromotedPost(@QueryParam(value="limit") int limit, @QueryParam(value="archived") boolean archived, @QueryParam(value="slot") int slot) throws FusionRestException {
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            return misBean.getPromotedPost(limit, archived, slot);
        }
        catch (Exception e) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Unabled to get promoted post");
        }
    }
}

