/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import Ice.LocalException;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktGroupChat;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class FusionPktCreateGroupChatOld
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktCreateGroupChatOld.class));
    private static final short FIELD_OTHER_PARTY_LIST = 4;
    private static final String WEB_PREFIX = "web:";
    private static final String COMMA = ",";
    private static final Pattern COMMA_SPLITTER = Pattern.compile(",");

    public FusionPktCreateGroupChatOld() {
        super((short)751);
    }

    public FusionPktCreateGroupChatOld(short transactionId) {
        super((short)751, transactionId);
    }

    public FusionPktCreateGroupChatOld(FusionPacket packet) {
        super(packet);
    }

    public String getParticipant() {
        return this.getStringField((short)1);
    }

    public void setParticipant(String participant) {
        this.setField((short)1, participant);
    }

    public String getInvitedUser() {
        return this.getStringField((short)2);
    }

    public void setInvitedUser(String invitedUser) {
        this.setField((short)2, invitedUser);
    }

    public Byte getIMType() {
        return this.getByteField((short)3);
    }

    public void setIMType(byte imType) {
        this.setField((short)3, imType);
    }

    public String[] getOtherPartyList() {
        String asString = this.getStringField((short)4);
        if (asString != null && asString.startsWith(WEB_PREFIX)) {
            String remainder = asString.substring(WEB_PREFIX.length());
            String[] tokens = COMMA_SPLITTER.split(remainder);
            return tokens;
        }
        return this.getStringArrayField((short)4);
    }

    public void setOtherPartyList(String[] usernames) {
        this.setField((short)4, usernames);
    }

    public void setOtherPartyList(String usernames) {
        this.setField((short)4, usernames);
    }

    public boolean sessionRequired() {
        return true;
    }

    private FusionPacket[] createFusionGroupChat(ConnectionI connection) throws Exception {
        boolean newStyleInvalid;
        boolean oldStyleInvalid;
        if (System.currentTimeMillis() - connection.getLastGroupChatCreated() < (long)connection.getGateway().getGeneralCoolDown()) {
            throw new Exception("You recently created a group chat. Please wait a short while before creating another one");
        }
        RegistryPrx registryPrx = connection.findRegistry();
        if (registryPrx == null) {
            throw new Exception("Unable to locate registry");
        }
        String privateChatter = this.getParticipant();
        String invitedUser = this.getInvitedUser();
        String[] otherPartyList = this.getOtherPartyList();
        if (log.isDebugEnabled() && otherPartyList != null) {
            String logMe = "";
            for (String op : otherPartyList) {
                logMe = logMe + op + " ";
            }
            log.debug((Object)("otherPartyList=" + logMe));
        }
        boolean bl = oldStyleInvalid = (privateChatter == null || invitedUser == null) && otherPartyList == null;
        if (oldStyleInvalid) {
            throw new Exception("Private chatter and initial invited user should be specified");
        }
        boolean bl2 = newStyleInvalid = invitedUser != null && otherPartyList != null;
        if (newStyleInvalid) {
            throw new Exception("Initial invited user and participant list cannot both be specified");
        }
        String creator = connection.getUsername();
        ObjectCachePrx objectCachePrx = registryPrx.getLowestLoadedObjectCache();
        String groupChatId = connection.newSessionID();
        if (otherPartyList == null) {
            ArrayList<String> converted = new ArrayList<String>();
            converted.add(invitedUser);
            otherPartyList = new String[converted.size()];
            converted.toArray(otherPartyList);
        }
        String privateChatterIce = privateChatter == null ? "\u0000" : privateChatter;
        GroupChatPrx groupChatPrx = objectCachePrx.createGroupChatObject(groupChatId, creator, privateChatterIce, otherPartyList);
        FusionPktGroupChat pkt = new FusionPktGroupChat(this.transactionId);
        pkt.setGroupChatId(groupChatId);
        pkt.setCreator(creator);
        pkt.setIMType(ImType.FUSION.value());
        connection.sendFusionPacket(pkt);
        groupChatPrx.sendInitialMessages();
        connection.groupChatCreated();
        return null;
    }

    private FusionPacket[] createOtherIMConferenceChat(ConnectionI connection, ImType imType) throws Exception {
        String participant = this.getParticipant();
        if (participant == null) {
            throw new Exception("No participant specified");
        }
        String invitedUser = this.getInvitedUser();
        if (invitedUser == null) {
            throw new Exception("No invited user specified");
        }
        UserPrx userPrx = connection.getUserPrx();
        if (userPrx == null) {
            throw new Exception("You are no longer logged in");
        }
        String conferenceID = userPrx.otherIMInviteToConference(imType.value(), participant, invitedUser);
        int userID = connection.getUserID();
        byte passwordType = PasswordType.forIMEnum(imType).value();
        AuthenticationServiceCredentialResponse response = connection.findAuthenticationService().getCredential(userID, passwordType);
        if (response.code != AuthenticationServiceResponseCodeEnum.Success) {
            throw new Exception("Unable to retrieve " + imType + " username. " + response.code);
        }
        String creator = response.userCredential.username;
        FusionPktGroupChat pkt = new FusionPktGroupChat(this.transactionId);
        pkt.setGroupChatId(conferenceID);
        pkt.setCreator(creator);
        pkt.setIMType(imType.value());
        return pkt.toArray();
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            ImType imType = ImType.FUSION;
            Byte byteVal = this.getIMType();
            if (byteVal != null && (imType = ImType.fromValue(byteVal)) == null) {
                throw new Exception("Invalid IM type " + byteVal);
            }
            switch (imType) {
                case FUSION: {
                    return this.createFusionGroupChat(connection);
                }
                case MSN: 
                case YAHOO: {
                    return this.createOtherIMConferenceChat(connection, imType);
                }
            }
            throw new Exception("Group chat is not supported for IM type " + imType);
        }
        catch (ObjectNotFoundException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create group chat - Failed to find lowest loaded object cache").toArray();
        }
        catch (ObjectExistsException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create group chat - Group chat already exists in object cache").toArray();
        }
        catch (FusionException e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create group chat - " + e.message).toArray();
        }
        catch (LocalException e) {
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to create group chat").toArray();
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to create group chat - " + e.getMessage()).toArray();
        }
    }
}

