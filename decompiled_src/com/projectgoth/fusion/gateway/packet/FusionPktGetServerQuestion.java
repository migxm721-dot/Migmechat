/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.data.AlertMessageData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.packets.FusionPktDataGetServerQuestion;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktServerQuestion;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import org.apache.log4j.Logger;

public class FusionPktGetServerQuestion
extends FusionPktDataGetServerQuestion {
    private static Logger log = Logger.getLogger(FusionPktGetServerQuestion.class);

    public FusionPktGetServerQuestion(short transactionId) {
        super(transactionId);
    }

    public FusionPktGetServerQuestion(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktGetServerQuestion(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            UserPrx userPrx = connection.getUserPrx();
            if (userPrx == null) {
                throw new Exception("You are no longer logged in");
            }
            UserDataIce userData = userPrx.getUserData();
            AlertMessageData alertMessage = userEJB.getLatestAlertMessage(connection.getClientVersion(), AlertMessageData.TypeEnum.LOGIN, userData.countryID, userData.lastLoginDate == Long.MIN_VALUE ? null : new Date(userData.lastLoginDate), null, connection.getDeviceType().value());
            if (alertMessage != null) {
                return new FusionPacket[]{new FusionPktServerQuestion(alertMessage, this.transactionId)};
            }
            return new FusionPacket[]{new FusionPktServerQuestion(this.transactionId)};
        }
        catch (Exception e) {
            log.error((Object)String.format("Failed to retrieve server question for transactionid:%s", this.transactionId), (Throwable)e);
            FusionPktError error = new FusionPktError(this.transactionId);
            error.setErrorDescription("Failed to retrieve server question");
            return new FusionPacket[]{error};
        }
    }
}

