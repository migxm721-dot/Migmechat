/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.exception.GroupChatNoLongerInChatSyncException;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataLeaveGroupChat;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.messageswitchboard.MessageSwitchboardDispatcher;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;

public class FusionPktLeaveGroupChat
extends FusionPktDataLeaveGroupChat {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktLeaveGroupChat.class));

    public FusionPktLeaveGroupChat() {
    }

    public FusionPktLeaveGroupChat(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktLeaveGroupChat(FusionPacket packet) {
        super(packet);
    }

    private FusionPacket[] leaveFusionGroupChat(ConnectionI connection) throws Exception {
        GroupChatPrx groupChatPrx;
        RegistryPrx registryPrx = connection.findRegistry();
        if (registryPrx == null) {
            throw new Exception("Unable to locate registry");
        }
        String groupChatId = this.getGroupChatId();
        if (groupChatId == null) {
            throw new Exception("You must specify a group chat ID");
        }
        try {
            groupChatPrx = registryPrx.findGroupChatObject(groupChatId);
        }
        catch (ObjectNotFoundException e) {
            groupChatPrx = this.restoreFusionGroupChat(connection, groupChatId);
        }
        catch (LocalException e) {
            groupChatPrx = this.restoreFusionGroupChat(connection, groupChatId);
        }
        boolean participantFound = false;
        if (groupChatPrx != null) {
            participantFound = groupChatPrx.removeParticipant(connection.getUsername());
        }
        if (!participantFound) {
            RegistryPrx regy = connection.getGatewayContext().getRegistryPrx();
            MessageSwitchboardDispatcher.getInstance().onLeaveGroupChat(regy, connection.getUsername(), connection.getUserID(), this.getGroupChatId(), connection.getUserPrx());
        }
        return new FusionPktOk(this.transactionId).toArray();
    }

    private GroupChatPrx restoreFusionGroupChat(ConnectionI connection, String groupChatId) throws Exception {
        if (SystemProperty.getBool(SystemPropertyEntities.PersistentGroupChatSettings.ENABLED)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("FusionPktLeaveGroupChat: restoring group chatID=" + groupChatId));
            }
            try {
                return connection.getSessionPrx().findGroupChatObject(groupChatId);
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("FusionPktLeaveGroupChat: failed to restore group chatID=" + this.getGroupChatId() + " exception=" + e));
                }
                throw new GroupChatNoLongerInChatSyncException(groupChatId);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("FusionPktLeaveGroupChat: not restoring group chatID=" + groupChatId + " as persistent group chats disabled"));
        }
        throw new ObjectNotFoundException();
    }

    private FusionPacket[] leaveOtherIMConferenceChat(ConnectionI connection, ImType imType) throws Exception {
        String conferenceId = this.getGroupChatId();
        if (conferenceId == null) {
            throw new Exception("You must specify a conference ID");
        }
        UserPrx userPrx = connection.getUserPrx();
        if (userPrx == null) {
            throw new Exception("You are no longer logged in");
        }
        userPrx.otherIMLeaveConference(imType.value(), conferenceId);
        return new FusionPktOk(this.transactionId).toArray();
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            ImType imType = this.getImType();
            switch (imType != null ? imType : ImType.FUSION) {
                case FUSION: {
                    return this.leaveFusionGroupChat(connection);
                }
                case MSN: 
                case YAHOO: {
                    return this.leaveOtherIMConferenceChat(connection, imType);
                }
            }
            throw new Exception("Group chat is not supported for IM type " + imType);
        }
        catch (FusionException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to leave group chat - " + e.message).toArray();
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to leave group chat").toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to leave group chat - " + e.getMessage()).toArray();
        }
    }
}

