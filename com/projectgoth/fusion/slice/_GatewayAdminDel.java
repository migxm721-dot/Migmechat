/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GatewayStats;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _GatewayAdminDel
extends _ObjectDel {
    public GatewayStats getStats(Map<String, String> var1) throws LocalExceptionWrapper, FusionException;

    public void sendAlertToAllConnections(String var1, String var2, Map<String, String> var3) throws LocalExceptionWrapper;
}

