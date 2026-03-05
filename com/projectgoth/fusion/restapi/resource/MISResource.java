/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.FormParam
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
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
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
@Path(value="/mis")
public class MISResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MISResource.class));

    @POST
    @Path(value="/chatroom/announce")
    @Produces(value={"application/json"})
    public DataHolder<Integer[]> doAnnounceToChatrooms(@FormParam(value="chatrooms") @DefaultValue(value="") String chatrooms, @FormParam(value="message") String message, @FormParam(value="all") @DefaultValue(value="0") Integer sendToAll, @FormParam(value="waittime") @DefaultValue(value="1000") Integer waitTime) throws FusionRestException {
        if (StringUtil.isBlank(chatrooms) && sendToAll != 1) {
            throw new FusionRestException(101, "No chatroom names provided.");
        }
        if (StringUtil.isBlank(message)) {
            throw new FusionRestException(101, "No message provided.");
        }
        String[] chatroomNames = null;
        Integer[] announcedChatrooms = null;
        try {
            MessageLocal msgLocal = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            if (sendToAll == 1) {
                announcedChatrooms = msgLocal.announceMessageToUserOwnedChatrooms(message, waitTime);
            } else {
                chatroomNames = chatrooms.split(";");
                announcedChatrooms = msgLocal.announceMessageToChatrooms(chatroomNames, message, waitTime);
            }
        }
        catch (Exception e) {
            throw new FusionRestException(101, e.getMessage());
        }
        return new DataHolder<Integer[]>(announcedChatrooms);
    }

    @POST
    @Path(value="/{username}/deletefilefromscrapbook")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> deleteFileFromScrapbook(@PathParam(value="username") String username, @QueryParam(value="id") int id) throws FusionRestException {
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.deleteFileFromScrapbook(username, id);
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in deletefilefromscrapbook username:%s", username), (Throwable)e);
            throw new FusionRestException(101, "Unable to deletefilefromscrapbook");
        }
    }
}

