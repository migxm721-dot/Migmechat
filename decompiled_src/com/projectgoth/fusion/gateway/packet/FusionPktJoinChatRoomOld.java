/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.ObjectNotExistException
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import Ice.ObjectNotExistException;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.common.ChatRoomUtils;
import com.projectgoth.fusion.common.ChatRoomValidationException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.data.ChatRoomData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.AlertContentType;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataAlert;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.exceptions.FusionRequestException;
import com.projectgoth.fusion.gateway.packet.FusionPktAlert;
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomOld;
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomParticipantsOld;
import com.projectgoth.fusion.gateway.packet.FusionPktChatRoomTheme;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionPktServerQuestionOld;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class FusionPktJoinChatRoomOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktJoinChatRoomOld.class));

    public FusionPktJoinChatRoomOld() {
        super((short)703);
    }

    public FusionPktJoinChatRoomOld(short transactionId) {
        super((short)703, transactionId);
    }

    public FusionPktJoinChatRoomOld(FusionPacket packet) {
        super(packet);
    }

    public String getChatRoomName() {
        return this.getStringField((short)1);
    }

    public void setChatRoomName(String chatRoomName) {
        this.setField((short)1, chatRoomName);
    }

    private String concat(String[] stringArray, String seperator) {
        StringBuilder builder = new StringBuilder();
        if (stringArray != null) {
            Arrays.sort(stringArray);
            for (String s : stringArray) {
                builder.append(s).append(seperator);
            }
        }
        return builder.toString();
    }

    public boolean sessionRequired() {
        return true;
    }

    public String getChatRoomNameForRateLimit() {
        return this.getChatRoomName();
    }

    protected void preValidate(ConnectionI connection) throws FusionRequestException {
        if (connection.isBannedFromChatrooms()) {
            throw new FusionRequestException(FusionRequestException.ExceptionType.PREVALIDATION, SystemProperty.get(SystemPropertyEntities.Default.CHATROOM_BAN_ERROR_MESSAGE));
        }
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            SessionPrx sessionPrx;
            UserDataIce userData;
            String chatRoomName = this.getChatRoomName();
            chatRoomName = ChatRoomUtils.validateChatRoomNameForAccess(chatRoomName);
            UserPrx userPrx = connection.getUserPrx();
            if (userPrx == null) {
                throw new FusionException("You are not logged in");
            }
            try {
                userData = userPrx.getUserData();
            }
            catch (ObjectNotExistException e) {
                log.info((Object)("JoinChatRoom " + chatRoomName + " failed for user:" + connection.getUsername() + " because:" + e.getMessage()), (Throwable)e);
                throw new FusionException("You are not logged in");
            }
            if (!AuthenticatedAccessControl.hasAccess(AuthenticatedAccessControlTypeEnum.ENTER_CHATROOM, new UserData(userData))) {
                throw new FusionException("You must authenticate your account");
            }
            RegistryPrx registryPrx = connection.findRegistry();
            if (registryPrx == null) {
                throw new Exception("Unable to locate registry");
            }
            ChatRoomPrx chatRoomPrx = null;
            try {
                chatRoomPrx = registryPrx.findChatRoomObject(chatRoomName);
            }
            catch (ObjectNotFoundException e) {
                // empty catch block
            }
            if (chatRoomPrx == null) {
                try {
                    ObjectCachePrx objectCachePrx = registryPrx.getLowestLoadedObjectCache();
                    chatRoomPrx = objectCachePrx.createChatRoomObject(chatRoomName);
                }
                catch (ObjectNotFoundException e) {
                    return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "No ObjectCache found").toArray();
                }
                catch (ObjectExistsException e) {
                    try {
                        chatRoomPrx = registryPrx.findChatRoomObject(chatRoomName);
                    }
                    catch (ObjectNotFoundException ie) {
                        return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Unable to join chat room").toArray();
                    }
                }
                catch (FusionException e) {
                    log.info((Object)("Attempt to join invalid chatroom : " + chatRoomName + " by user: " + userData.username));
                    throw e;
                }
            }
            if ((sessionPrx = connection.getSessionPrx()) == null) {
                throw new Exception("You are no longer logged in");
            }
            if (SystemProperty.getBool(SystemPropertyEntities.Chatroom.PARTICIPANT_CACHE_DATA, true)) {
                chatRoomPrx.addParticipant(userPrx, userData, sessionPrx, connection.getSessionID(), connection.getRemoteAddress(), connection.getMobileDevice(), connection.getUserAgent(), connection.getClientVersion(), connection.getDeviceType().value());
            } else {
                chatRoomPrx.addParticipantOld(userPrx, userData, sessionPrx, connection.getSessionID(), connection.getRemoteAddress(), connection.getMobileDevice(), connection.getUserAgent());
            }
            sessionPrx.chatroomJoined(chatRoomPrx, chatRoomName);
            ArrayList<FusionPacket> packetsToReturn = new ArrayList<FusionPacket>();
            if (connection.isAjax() || connection.isMobileClientV2()) {
                packetsToReturn.add(new FusionPktChatRoomOld(this.transactionId, new ChatRoomData(chatRoomPrx.getRoomData())));
            } else if (connection.isMidletVersionAndAbove(440)) {
                ChatRoomData chatroomData = new ChatRoomData(chatRoomPrx.getRoomData());
                if (chatroomData.themeID != null && chatRoomPrx.getTheme() != null) {
                    packetsToReturn.add(new FusionPktChatRoomTheme(this.transactionId, chatroomData.themeID, chatRoomPrx.getTheme()));
                }
                packetsToReturn.add(new FusionPktChatRoomOld(this.transactionId, chatroomData));
            } else {
                packetsToReturn.add(new FusionPktOk(this.transactionId));
            }
            if (connection.isMobileClientV2AndNewVersion()) {
                String mutedUsers;
                String administrators;
                FusionPktChatRoomParticipantsOld reply = new FusionPktChatRoomParticipantsOld(this.transactionId);
                reply.setChatRoomName(chatRoomName);
                String[] participantArray = chatRoomPrx.getParticipants(connection.getUsername());
                String participants = this.concat(participantArray, ";");
                if (participants != null && participants.length() > 0) {
                    reply.setParticipants(participants);
                }
                if ((administrators = this.concat(chatRoomPrx.getAdministrators(connection.getUsername()), ";")) != null && administrators.length() > 0) {
                    reply.setAdministrators(administrators);
                }
                if ((mutedUsers = this.concat(connection.getUserPrx().getBlockListFromUsernames(participantArray), ";")) != null && mutedUsers.length() > 0) {
                    reply.setMutedParticipants(mutedUsers);
                }
                packetsToReturn.add(reply);
            }
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            AlertMessageData alertMessage = connection.getDeviceType() == ClientType.MIDP1 ? userEJB.getLatestAlertMessage(connection.getClientVersion(), AlertMessageData.TypeEnum.CHAT_ROOM, userData.countryID, new Date(userData.lastLoginDate), AlertContentType.TEXT, ClientType.MIDP2.value()) : userEJB.getLatestAlertMessage(connection.getClientVersion(), AlertMessageData.TypeEnum.CHAT_ROOM, userData.countryID, new Date(userData.lastLoginDate), null, connection.getDeviceType().value());
            if (alertMessage != null) {
                if (alertMessage.contentType == AlertContentType.URL_WITH_CONFIRMATION) {
                    FusionPktServerQuestionOld questionPkt = new FusionPktServerQuestionOld(alertMessage);
                    connection.setServerQuestionOld(questionPkt);
                    packetsToReturn.add(questionPkt);
                } else {
                    FusionPktAlert alertPkt = new FusionPktAlert();
                    alertPkt.setAlertType(FusionPktDataAlert.AlertType.INFORMATION);
                    alertPkt.setContentType(AlertContentType.fromValue((int)alertMessage.contentType.value()));
                    alertPkt.setContent(alertMessage.content);
                    packetsToReturn.add(alertPkt);
                }
            }
            return packetsToReturn.toArray(new FusionPacket[packetsToReturn.size()]);
        }
        catch (ChatRoomValidationException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to join chat room - " + e.getMessage()).toArray();
        }
        catch (CreateException e) {
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (RemoteException e) {
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (FusionException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to join chat room - " + e.message).toArray();
        }
        catch (LocalException e) {
            log.error((Object)"Unknown Ice.LocalException occurred", (Throwable)e);
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Unable to join chat room").toArray();
        }
        catch (Exception e) {
            log.error((Object)"Caught Exception while trying to join chat room.", (Throwable)e);
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to join chat room - Internal Error").toArray();
        }
    }
}

