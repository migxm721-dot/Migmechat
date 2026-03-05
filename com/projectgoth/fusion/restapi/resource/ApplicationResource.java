/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.ThirdPartyApplicationData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.eventqueue.EventQueue;
import com.projectgoth.fusion.eventqueue.events.GameEvent;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/application")
public class ApplicationResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ApplicationResource.class));

    @POST
    @Path(value="/{applicationid}/gameevent/{userid}")
    @Produces(value={"application/json"})
    public Response sendApplicationEvent(@PathParam(value="applicationid") String applicationId, @PathParam(value="userid") String userIdStr, String gameEventJson) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, -1);
        if (userId == -1) {
            throw new FusionRestException(101, "Invalid User ID");
        }
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            String username = userBean.getUsernameByUserid(userId, null);
            if (SystemProperty.getBool("GameEventsToMigboEnabled", false)) {
                Web webBean = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
                webBean.sendApplicationEvent(username, applicationId, gameEventJson);
            } else {
                EventQueue.enqueueSingleEvent(new GameEvent(username, applicationId, gameEventJson));
            }
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (Exception e) {
            log.error((Object)("Unable to send application event: " + e));
            throw new FusionRestException(101, "Internal system error: Unable to send application event");
        }
    }

    @GET
    @Path(value="/thirdpartyapp_details/{thirdPartyAppId}")
    @Produces(value={"application/json"})
    public DataHolder<ThirdPartyApplicationData> getThirdPartyDetails(@PathParam(value="thirdPartyAppId") String thirdPartyAppIdStr) throws FusionRestException {
        int thirdPartyAppId = StringUtil.toIntOrDefault(thirdPartyAppIdStr, -1);
        if (thirdPartyAppId == -1) {
            throw new FusionRestException(101, "Invalid Third Party App ID");
        }
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            ThirdPartyApplicationData thirdPartyApplicationData = userBean.getThirdPartyApplicationData(thirdPartyAppId, null);
            return new DataHolder<ThirdPartyApplicationData>(thirdPartyApplicationData);
        }
        catch (Exception e) {
            log.error((Object)("Unable to send application event: " + e));
            throw new FusionRestException(101, "Internal system error: Unable to send application event");
        }
    }
}

