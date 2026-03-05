/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import Ice.Current;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GatewayStats;
import com.projectgoth.fusion.slice._GatewayAdminDisp;
import org.apache.log4j.Logger;

public class GatewayAdminI
extends _GatewayAdminDisp {
    private Gateway gateway;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GatewayAdminI.class));

    public GatewayAdminI(Gateway gateway) {
        this.gateway = gateway;
    }

    public GatewayStats getStats(Current __current) throws FusionException {
        try {
            return this.gateway.getStats();
        }
        catch (Exception e) {
            FusionException fe = new FusionException();
            fe.message = "Initialisation incomplete";
            throw fe;
        }
    }

    public void sendAlertToAllConnections(String message, String title, Current __current) {
        int exceptionCount = 0;
        int dispatchCount = 0;
        for (ConnectionI connection : this.gateway.getConnections()) {
            try {
                connection.putAlertMessage(message, title, (short)0);
                ++dispatchCount;
            }
            catch (Exception e) {
                ++exceptionCount;
            }
        }
        if (exceptionCount != 0) {
            log.error((Object)String.format("Caught %d exceptions while dispatching alert to all conections", exceptionCount));
        }
        log.info((Object)String.format("Message \"%s\" dispatched successfully to %d connections", message, dispatchCount));
    }
}

