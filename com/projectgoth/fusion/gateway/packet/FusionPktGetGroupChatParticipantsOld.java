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
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.exception.GroupChatNoLongerInChatSyncException;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGroupChatParticipantsOld;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.UserPrx;
import org.apache.log4j.Logger;

public class FusionPktGetGroupChatParticipantsOld
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktGetGroupChatParticipantsOld.class));
    private static final String GROUP_CHAT_TIMED_OUT_OF_CHATSYNC_ERROR = "Unable to get group chat participants - this group chat was inactive for too many days and is no longer stored";

    public FusionPktGetGroupChatParticipantsOld() {
        super((short)754);
    }

    public FusionPktGetGroupChatParticipantsOld(short transactionId) {
        super((short)754, transactionId);
    }

    public FusionPktGetGroupChatParticipantsOld(FusionPacket packet) {
        super(packet);
    }

    public String getGroupChatId() {
        return this.getStringField((short)1);
    }

    public void setGroupChatId(String groupChatId) {
        this.setField((short)1, groupChatId);
    }

    public Byte getIMType() {
        return this.getByteField((short)2);
    }

    public void setIMType(byte imType) {
        this.setField((short)2, imType);
    }

    public boolean sessionRequired() {
        return true;
    }

    private FusionPacket[] getFusionGroupChatParticipants(ConnectionI connection) throws Exception {
        UserPrx userPrx;
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
            if (SystemProperty.getBool(SystemPropertyEntities.PersistentGroupChatSettings.ENABLED)) {
                groupChatPrx = this.restoreGroupChat(connection, groupChatId);
            }
            throw e;
        }
        FusionPktGroupChatParticipantsOld reply = new FusionPktGroupChatParticipantsOld(this.transactionId);
        reply.setGroupChatId(groupChatId);
        reply.setIMType(ImType.FUSION.value());
        Object[] participantArray = groupChatPrx.getParticipants(connection.getUsername());
        String participants = StringUtil.join(participantArray, ";");
        if (participants != null && participants.length() > 0) {
            reply.setParticipants(participants + ";");
        }
        if ((userPrx = connection.getUserPrx()) == null) {
            throw new Exception("You are no longer logged in");
        }
        String mutedUsers = StringUtil.join(userPrx.getBlockListFromUsernames((String[])participantArray), ";");
        if (mutedUsers != null && mutedUsers.length() > 0) {
            reply.setMutedParticipants(mutedUsers + ";");
        }
        return reply.toArray();
    }

    private FusionPacket[] getOtherIMConferenceParticipants(ConnectionI connection, ImType imType) throws Exception {
        String conferenceId = this.getGroupChatId();
        if (conferenceId == null) {
            throw new Exception("You must specify a conference ID");
        }
        UserPrx userPrx = connection.getUserPrx();
        if (userPrx == null) {
            throw new Exception("You are no longer logged in");
        }
        String participants = StringUtil.join(userPrx.getOtherIMConferenceParticipants(imType.value(), conferenceId), ";");
        FusionPktGroupChatParticipantsOld reply = new FusionPktGroupChatParticipantsOld(this.transactionId);
        reply.setGroupChatId(conferenceId);
        reply.setParticipants(participants + ";");
        reply.setIMType(imType.value());
        return reply.toArray();
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        ImType imType = null;
        try {
            imType = ImType.FUSION;
            Byte byteVal = this.getIMType();
            if (byteVal != null && (imType = ImType.fromValue(byteVal)) == null) {
                throw new Exception("Invalid IM type " + byteVal);
            }
            switch (imType) {
                case FUSION: {
                    return this.getFusionGroupChatParticipants(connection);
                }
                case MSN: 
                case YAHOO: {
                    return this.getOtherIMConferenceParticipants(connection, imType);
                }
            }
            throw new Exception("Group chat is not supported for IM type " + imType);
        }
        catch (GroupChatNoLongerInChatSyncException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.CHAT_SYNC_ENTITY_NOT_FOUND, GROUP_CHAT_TIMED_OUT_OF_CHATSYNC_ERROR).toArray();
        }
        catch (ObjectNotFoundException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get group chat participants - group chat not found").toArray();
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to get group chat participants").toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to get group chat participants - " + e.getMessage()).toArray();
        }
    }

    private GroupChatPrx restoreGroupChat(ConnectionI connection, String groupChatId) throws Exception {
        if (SystemProperty.getBool(SystemPropertyEntities.PersistentGroupChatSettings.ENABLED)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("FusionPktGetGroupChatParticipants: restoring group chatID=" + groupChatId));
            }
            try {
                return connection.getSessionPrx().findGroupChatObject(groupChatId);
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("FusionPktGetGroupChatParticipants: failed to restore group chatID=" + this.getGroupChatId() + " exception=" + e));
                }
                throw new GroupChatNoLongerInChatSyncException(groupChatId);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("FusionPktGetGroupChatParticipants: not restoring group chatID=" + groupChatId + " as persistent group chats disabled"));
        }
        throw new ObjectNotFoundException();
    }
}

