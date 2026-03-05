/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import com.projectgoth.fusion.packet.FusionPacket;

public class FusionPktOk
extends FusionPacket {
    public FusionPktOk() {
        super((short)1);
    }

    public FusionPktOk(short transactionId) {
        super((short)1, transactionId);
    }

    public FusionPktOk(short transactionId, String serverResponse) {
        super((short)1, transactionId);
        this.setServerResponse(serverResponse);
    }

    public FusionPktOk(short transactionId, String serverResponse, long responseLong) {
        super((short)1, transactionId);
        this.setServerResponse(serverResponse);
        this.setServerResponseLong(responseLong);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FusionPktOk(short transactionId, int infoId) {
        super((short)1, transactionId);
        String infoText = null;
        try {
            try {
                MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                infoText = misEJB.getInfoText(infoId);
            }
            catch (Exception e) {
                Object var6_7 = null;
                if (infoText == null) {
                    this.setServerResponse("Request successful");
                } else {
                    this.setServerResponse(infoText);
                }
            }
            Object var6_6 = null;
            if (infoText == null) {
                this.setServerResponse("Request successful");
            } else {
                this.setServerResponse(infoText);
            }
        }
        catch (Throwable throwable) {
            Object var6_8 = null;
            if (infoText == null) {
                this.setServerResponse("Request successful");
            } else {
                this.setServerResponse(infoText);
            }
            throw throwable;
        }
    }

    public FusionPktOk(FusionPacket packet) {
        super(packet);
    }

    public String getServerResponse() {
        return this.getStringField((short)1);
    }

    public void setServerResponse(String serverResponse) {
        this.setField((short)1, serverResponse);
    }

    public long getServerResponseLong() {
        return this.getLongField((short)2);
    }

    private void setServerResponseLong(long srLong) {
        this.setField((short)2, srLong);
    }
}

