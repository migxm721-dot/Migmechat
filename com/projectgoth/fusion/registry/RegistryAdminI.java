/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.registry;

import Ice.Current;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.registry.Registry;
import com.projectgoth.fusion.registry.RegistryContext;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryNodePrx;
import com.projectgoth.fusion.slice.RegistryStats;
import com.projectgoth.fusion.slice._RegistryAdminDisp;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryAdminI
extends _RegistryAdminDisp {
    private RegistryContext applicationContext;

    public RegistryAdminI(RegistryContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public RegistryStats getStats(Current __current) throws FusionException {
        RegistryStats stats = ServiceStatsFactory.getRegistryStats(this.applicationContext.getHostName(), Registry.startTime);
        try {
            stats = this.applicationContext.getRegistry().getStats(stats);
            stats.otherRegistries = "";
            ConcurrentHashMap<String, RegistryNodePrx> otherRegistries = this.applicationContext.getRegistryNode().otherRegistries;
            Iterator i = otherRegistries.keySet().iterator();
            while (i.hasNext()) {
                String ref = (String)i.next();
                stats.otherRegistries = stats.otherRegistries + ref;
                if (!i.hasNext()) continue;
                stats.otherRegistries = stats.otherRegistries + "; ";
            }
            stats.requestsPerSecond = this.applicationContext.getRegistry().requestCounter.getRequestsPerSecond();
            stats.maxRequestsPerSecond = this.applicationContext.getRegistry().requestCounter.getMaxRequestsPerSecond();
        }
        catch (Exception e) {
            FusionException fe = new FusionException();
            fe.message = "Initialisation incomplete";
            throw fe;
        }
        return stats;
    }
}

