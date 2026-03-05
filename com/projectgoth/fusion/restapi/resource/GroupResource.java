/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.interfaces.Group;
import com.projectgoth.fusion.interfaces.GroupHome;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.GroupMembersListData;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/group")
public class GroupResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GroupResource.class));

    @POST
    @Path(value="/{groupid}/moderator/{username}")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> giveGroupAdminRights(@PathParam(value="groupid") int groupid, @PathParam(value="username") String username) throws FusionRestException {
        return this.updateModeratorStatus(groupid, username, true);
    }

    @DELETE
    @Path(value="/{groupid}/moderator/{username}")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> removeGroupAdminRights(@PathParam(value="groupid") int groupid, @PathParam(value="username") String username) throws FusionRestException {
        return this.updateModeratorStatus(groupid, username, false);
    }

    private DataHolder<BooleanData> updateModeratorStatus(int groupid, String username, boolean promote) throws FusionRestException {
        if (StringUtil.isBlank(username)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid username" + username);
        }
        try {
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
            if (userPrx == null) {
                return new DataHolder<BooleanData>(new BooleanData(true));
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            String[] chatrooms = messageEJB.getGroupChatRooms(groupid);
            ChatRoomPrx[] chatRoomProxies = null;
            if (chatrooms != null && chatrooms.length > 0) {
                chatRoomProxies = EJBIcePrxFinder.findChatRoomProxies(chatrooms);
            }
            if (log.isDebugEnabled()) {
                if (chatRoomProxies != null) {
                    log.debug((Object)("For group:" + groupid + " ,user[" + username + "] is in " + chatRoomProxies.length + " chatrooms"));
                } else {
                    log.debug((Object)("For group:" + groupid + " ,user[" + username + "] :active chatroomproxies is null"));
                }
            }
            if (chatRoomProxies != null) {
                for (ChatRoomPrx chatroomPrx : chatRoomProxies) {
                    if (chatroomPrx == null) continue;
                    chatroomPrx.updateGroupModeratorStatus(username, promote);
                }
            }
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (CreateException e) {
            log.error((Object)("Unable to create ejb for user or message bean to change moderator status user:" + username + " to " + promote));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (EJBException e) {
            log.error((Object)("Unable to change moderator status of user " + username + " to " + promote + ":" + e.getMessage()));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            log.error((Object)("Exception in changing moderator status for user " + username + " to " + promote + ":" + e.getMessage()));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path(value="/{groupid}/members")
    @Produces(value={"application/json"})
    public DataHolder<GroupMembersListData> getGroupMembers(@PathParam(value="groupid") int groupId) throws FusionRestException {
        try {
            Group groupEJB = (Group)EJBHomeCache.getObject("ejb/Group", GroupHome.class);
            List members = groupEJB.getGroupMembers(groupId);
            return new DataHolder<GroupMembersListData>(new GroupMembersListData(members));
        }
        catch (CreateException e) {
            log.error((Object)"Create Exception", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (EJBException e) {
            log.error((Object)e.getMessage());
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            log.error((Object)"Unhandled Exception", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }
}

