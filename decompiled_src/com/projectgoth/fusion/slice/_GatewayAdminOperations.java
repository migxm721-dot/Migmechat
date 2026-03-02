/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GatewayStats;

public interface _GatewayAdminOperations {
    public GatewayStats getStats(Current var1) throws FusionException;

    public void sendAlertToAllConnections(String var1, String var2, Current var3);
}

