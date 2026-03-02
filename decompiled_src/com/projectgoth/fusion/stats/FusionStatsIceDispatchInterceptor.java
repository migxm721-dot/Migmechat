/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.DispatchInterceptor
 *  Ice.DispatchStatus
 *  Ice.Object
 *  Ice.Request
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.stats;

import Ice.DispatchInterceptor;
import Ice.DispatchStatus;
import Ice.Object;
import Ice.Request;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.stats.IceStats;
import org.apache.log4j.Logger;

public class FusionStatsIceDispatchInterceptor
extends DispatchInterceptor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionStatsIceDispatchInterceptor.class));
    private Object servant;

    public FusionStatsIceDispatchInterceptor(Object servant) {
        this.servant = servant;
    }

    public DispatchStatus dispatch(Request request) {
        try {
            IceStats.getInstance().addRequest(request);
        }
        catch (Exception e) {
            log.warn((java.lang.Object)("Failed to add method invocation request " + request + " to IceStats: " + e), (Throwable)e);
        }
        long startTime = System.currentTimeMillis();
        DispatchStatus ds = this.servant.ice_dispatch(request, null);
        try {
            IceStats.getInstance().onRequestDispatched(request, startTime);
        }
        catch (Exception e) {
            log.warn((java.lang.Object)("Failed to add timing for method invocation request " + request + " to IceStats: " + e), (Throwable)e);
        }
        return ds;
    }
}

