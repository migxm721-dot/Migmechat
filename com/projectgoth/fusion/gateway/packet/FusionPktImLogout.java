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
import com.projectgoth.fusion.fdl.packets.FusionPktDataImLogout;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktInternalServerError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.UserPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;

public class FusionPktImLogout
extends FusionPktDataImLogout {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktImLogout.class));

    public FusionPktImLogout(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktImLogout(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        ImType imType = null;
        UserPrx userPrx = null;
        try {
            imType = this.getImType();
            if (imType == null) {
                throw new Exception("Unsupported IM type");
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

