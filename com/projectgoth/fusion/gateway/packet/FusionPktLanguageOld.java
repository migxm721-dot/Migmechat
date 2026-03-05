/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.FloodControl;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import org.apache.log4j.Logger;

public class FusionPktLanguageOld
extends FusionRequest {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktLanguageOld.class));

    public FusionPktLanguageOld() {
        super((short)925);
    }

    public FusionPktLanguageOld(short transactionId) {
        super((short)925, transactionId);
    }

    public FusionPktLanguageOld(FusionPacket packet) {
        super(packet);
    }

    public boolean sessionRequired() {
        return true;
    }

    public String getLanguage() {
        return this.getStringField((short)1);
    }

    public void setLanguage(String language) {
        this.setField((short)1, language);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        FusionPktError pktError = null;
        try {
            log.debug((Object)("Setting Language to [" + this.getLanguage() + "]"));
            FloodControl.detectFlooding(connection.getUsername(), connection.getUserPrx(), new FloodControl.Action[]{FloodControl.Action.DEFAULT_DAILY, FloodControl.Action.DEFAULT_PER_MINUTE});
            connection.setLanguage(this.getLanguage());
            connection.getSessionPrx().setLanguage(this.getLanguage());
            return new FusionPktOk(this.transactionId).toArray();
        }
        catch (Exception e) {
            log.error((Object)("FusionPktLanguage error: [" + connection.getUsername() + "] [" + this.getLanguage() + "]"), (Throwable)e);
            pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to process request. Please try again later.");
            return pktError.toArray();
        }
    }
}

