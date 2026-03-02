/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GatewayStats;

public interface _GatewayAdminOperationsNC {
    public GatewayStats getStats() throws FusionException;

    public void sendAlertToAllConnections(String var1, String var2);
}

