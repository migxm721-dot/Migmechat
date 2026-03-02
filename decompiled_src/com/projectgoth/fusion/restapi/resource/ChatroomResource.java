/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
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
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.GroupData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.interfaces.Web;
import com.projectgoth.fusion.interfaces.WebHome;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.ChatroomSettingsMultiIDData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import java.util.Hashtable;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/chatroom")
public class ChatroomResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatroomResource.class));
    private static final Logger setMultiIDSettingsLog = Logger.getLogger((String)"SetMultiIDSettingsLog");

    @GET
    @Path(value="/{chatroomname}/settings/multiid")
    @Produces(value={"application/json"})
    public DataHolder<ChatroomSettingsMultiIDData> getMultiIDSettings(@PathParam(value="chatroomname") String chatroomName) throws FusionRestException {
        if (StringUtil.isBlank(chatroomName)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid chatroom name " + chatroomName);
        }
        try {
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            String normalizedChatroomName = ChatRoomUtils.normalizeChatRoomName(chatroomName);
            ChatRoomData chatRoomData = messageEJB.getSimpleChatRoomData(normalizedChatroomName, null);
            ChatroomSettingsMultiIDData data = new ChatroomSettingsMultiIDData();
            data.retrieveFromChatRoomData(chatRoomData);
            return new DataHolder<ChatroomSettingsMultiIDData>(data);
        }
        catch (CreateException e) {
            log.error((Object)String.format("Unable to create ejb for message bean to get multi id settings for chatroom %s", chatroomName));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (EJBException e) {
            log.error((Object)String.format("Unable to get multi id settings for chatroom %s: %s", chatroomName, e.getMessage()));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path(value="/{chatroomname}/settings/multiid")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setMultiIDSettings(@PathParam(value="chatroomname") String chatroomName, @QueryParam(value="requestingUserid") String strUserID, DataHolder<ChatroomSettingsMultiIDData> dataHolder) throws FusionRestException {
        ChatroomSettingsMultiIDData data = (ChatroomSettingsMultiIDData)dataHolder.data;
        if (data.minMigLevel != null && data.minMigLevel < 0) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid min mig level: " + data.minMigLevel);
        }
        if (data.rateLimitByIP != null && !MemCachedRateLimiter.isValidHitPerDuration(data.rateLimitByIP)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid rate limit pattern: " + data.rateLimitByIP);
        }
        int userID = StringUtil.toIntOrDefault(strUserID, -1);
        if (userID == -1) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Invalid requestingUserId specified: " + strUserID);
        }
        try {
            String normalizedChatroomName;
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserData userData = userEJB.loadUserFromID(userID);
            if (userData == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find user with ID '" + strUserID + "'");
            }
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            ChatRoomData chatRoomData = messageEJB.getSimpleChatRoomData(normalizedChatroomName = ChatRoomUtils.normalizeChatRoomName(chatroomName), null);
            if (chatRoomData == null) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to find chatroom '" + chatroomName + "'");
            }
            boolean blockAccess = true;
            String userLabel = "";
            if (userData.chatRoomAdmin.booleanValue()) {
                userLabel = "GlobalAdmin";
                blockAccess = false;
            } else if (chatRoomData.userOwned.booleanValue()) {
                if (userData.username.equals(chatRoomData.creator)) {
                    userLabel = "ChatroomOwner";
                    blockAccess = false;
                }
                if (blockAccess && chatRoomData.belongsToGroup()) {
                    GroupData groupData = userEJB.getGroup(chatRoomData.groupID);
                    if (groupData == null) {
                        throw new FusionRestException(FusionRestException.RestException.ERROR, "Unable to retrieve group information for chatroom '" + chatroomName + "'");
                    }
                    if (userData.username.equals(groupData.createdBy)) {
                        userLabel = "GroupOwner";
                        blockAccess = false;
                    }
                }
                if (blockAccess && messageEJB.isModeratorOfChatRoom(userData.username, chatRoomData.name)) {
                    userLabel = "ChatroomModerator";
                    blockAccess = false;
                }
            }
            if (blockAccess) {
                throw new FusionRestException(FusionRestException.RestException.ERROR, "User '" + userData.username + "' does not have access to chatroom '" + chatroomName + "'");
            }
            data.updateToChatRoomData(chatRoomData);
            messageEJB.updateRoomExtraData(chatRoomData);
            StringBuilder sb = new StringBuilder();
            sb.append(userData.username).append(",").append(userLabel).append(",").append(chatRoomData.name).append(",").append(chatRoomData.userOwned != false ? "UserOwned" : "Global").append(",").append(data.minMigLevel == null ? "None" : data.minMigLevel).append(",").append(StringUtil.isBlank(data.rateLimitByIP) ? "None" : data.rateLimitByIP);
            setMultiIDSettingsLog.info((Object)sb.toString());
            return Response.ok().entity(new DataHolder<String>("ok")).build();
        }
        catch (CreateException e) {
            log.error((Object)String.format("Unable to create ejb for message bean to get multi id settings for chatroom %s", chatroomName));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (EJBException e) {
            log.error((Object)String.format("Unable to get multi id settings for chatroom %s: %s", chatroomName, e.getMessage()));
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path(value="/{chatroomname}/maxsize")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> setMaxSize(@PathParam(value="chatroomname") String chatRoomName, @QueryParam(value="maxsize") int maxSize) throws FusionRestException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("ChatroomResource.setMaxSize chatroomname=" + chatRoomName + " maxsize=" + maxSize));
        }
        try {
            MessageLocal messageEJB = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            String normalizedChatroomName = ChatRoomUtils.normalizeChatRoomName(chatRoomName);
            messageEJB.setChatRoomMaxSize(normalizedChatroomName, maxSize);
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)("Unable to set chatroom maximum size for chatroom=" + chatRoomName + ": " + e), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path(value="/{chatroom}/deregisterchatroomice")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> deregisterChatroomIce(@PathParam(value="chatroom") String chatroom) throws FusionRestException {
        try {
            EJBIcePrxFinder.getRegistry().deregisterChatRoomObject(chatroom);
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in deregisterchatroomice chatroom:%s", chatroom), (Throwable)e);
            throw new FusionRestException(101, "Unable to deregisterchatroomice");
        }
    }

    @GET
    @Path(value="/{chatroomname}/getchatroom")
    @Produces(value={"application/json"})
    public DataHolder<Hashtable> getChatroom(@PathParam(value="chatroomname") String chatRoomName) throws FusionRestException {
        try {
            Web webBean = (Web)EJBHomeCache.getObject("ejb/Web", WebHome.class);
            return new DataHolder<Hashtable>(webBean.getChatroom(chatRoomName));
        }
        catch (Exception e) {
            log.error((Object)String.format("Error in getChatroom chatRoomName:%s", chatRoomName), (Throwable)e);
            throw new FusionRestException(101, "Unable to getChatroom");
        }
    }
}

