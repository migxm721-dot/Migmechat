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
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.UserPrx;
import org.apache.log4j.Logger;

public class FusionPktIMLogoutOld
extends FusionRequest {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktIMLogoutOld.class));

    public FusionPktIMLogoutOld() {
        super((short)302);
    }

    public FusionPktIMLogoutOld(short transactionId) {
        super((short)302, transactionId);
    }

    public FusionPktIMLogoutOld(FusionPacket packet) {
        super(packet);
    }

    public Byte getIMType() {
        return this.getByteField((short)1);
    }

    public void setIMType(byte imType) {
        this.setField((short)1, imType);
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
            userPrx = connection.getUserPrx();
            userPrx.otherIMLogout(imType.value());
            return new FusionPacket[]{new FusionPktOk(this.transactionId)};
        }
        catch (LocalException e) {
            this.reportException(connection, imType, userPrx, (Exception)((Object)e));
            return new FusionPktInternalServerError(this.transactionId, (Exception)((Object)e), "Failed to logout from IM").toArray();
        }
        catch (Exception e) {
            this.reportException(connection, imType, userPrx, e);
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to logout from IM - " + e.getMessage());
            return new FusionPacket[]{pktError};
        }
    }

    private void reportException(ConnectionI cxn, ImType imType, UserPrx userPrx, Exception e) {
        log.error((Object)("An error occurred during IM logout [IMTYPE:" + imType + "] [SID:" + cxn.getSessionID() + "] userPrx=" + userPrx + "e=" + e), (Throwable)e);
    }
}

