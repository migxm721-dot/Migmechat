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
import com.projectgoth.fusion.fdl.packets.FusionPktDataImLogin;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;

public class FusionPktImLogin
extends FusionPktDataImLogin {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktImLogin.class));

    public FusionPktImLogin(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktImLogin(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        ImType imType = null;
        UserPrx userPrx = null;
        try {
            Boolean boolVal;
            imType = this.getImType();
            if (imType == null) {
                throw new Exception("Unsupported IM type");
            }
            PresenceType presence = this.getInitialPresence();
            if (presence == null) {
                presence = PresenceType.AVAILABLE;
            }
            boolean showOfflineContacts = (boolVal = this.getShowOfflineContacts()) != null && boolVal != false;
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

