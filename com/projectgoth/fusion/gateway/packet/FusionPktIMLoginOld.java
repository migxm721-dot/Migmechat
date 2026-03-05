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
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import org.apache.log4j.Logger;

public class FusionPktIMLoginOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktIMLoginOld.class));

    public FusionPktIMLoginOld() {
        super((short)206);
    }

    public FusionPktIMLoginOld(short transactionId) {
        super((short)206, transactionId);
    }

    public FusionPktIMLoginOld(FusionPacket packet) {
        super(packet);
    }

    public Byte getIMType() {
        return this.getByteField((short)1);
    }

    public void setIMType(byte imType) {
        this.setField((short)1, imType);
    }

    public Byte getInitialPresence() {
        return this.getByteField((short)2);
    }

    public void setInitialPresence(byte initialPresence) {
        this.setField((short)2, initialPresence);
    }

    public Byte getShowOfflineContacts() {
        return this.getByteField((short)3);
    }

    public void setShowOfflineContacts(byte showOfflineContacts) {
        this.setField((short)3, showOfflineContacts);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        ImType imType = null;
        UserPrx userPrx = null;
        try {
            Byte byteVal = this.getIMType();
            if (byteVal == null) {
                throw new Exception("No IM type specified");
            }
            imType = ImType.fromValue(byteVal);
            if (imType == null) {
                throw new Exception("Unsupported IM type " + byteVal);
            }
            byteVal = this.getInitialPresence();
            PresenceType presence = null;
            if (byteVal != null) {
                presence = PresenceType.fromValue(byteVal);
            }
            if (byteVal == null) {
                presence = PresenceType.AVAILABLE;
            }
            boolean showOfflineContacts = (byteVal = this.getShowOfflineContacts()) != null && byteVal.intValue() == 1;
            userPrx = connection.getUserPrx();
            if (userPrx == null) {
                throw new Exception("You are no longer logged in");
            }
            UserPrxHelper.checkedCast(userPrx.ice_timeout(connection.getGateway().getImLoginTimeout())).otherIMLogin(imType.value(), presence.value(), showOfflineContacts);
            return new FusionPacket[]{new FusionPktOk(this.transactionId)};
        }
        catch (LocalException e) {
            log.error((Object)("An error occurred during IM login [IMTYPE:" + imType + "] [SID:" + connection.getSessionID() + "] userPrx=" + userPrx + "e=" + (Object)((Object)e)), (Throwable)e);
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to sign into " + imType);
            return new FusionPacket[]{pktError};
        }
        catch (FusionException e) {
            log.error((Object)("An error occurred during IM login [IMTYPE:" + imType + "]"), (Throwable)((Object)e));
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to sign into " + imType + " - " + e.message);
            return new FusionPacket[]{pktError};
        }
        catch (Exception e) {
            log.error((Object)("An error occurred during IM login [IMTYPE:" + imType + "]"), (Throwable)e);
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to sign into other IM - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
    }
}

