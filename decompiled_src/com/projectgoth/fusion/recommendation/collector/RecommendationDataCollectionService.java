/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.recommendation.collector;

import Ice.Application;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.HostUtils;
import com.projectgoth.fusion.recommendation.collector.RDCSIceServerApp;
import com.projectgoth.fusion.recommendation.collector.RecommendationDataCollectionServiceAdminI;
import com.projectgoth.fusion.recommendation.collector.RecommendationDataCollectionServiceI;
import org.apache.log4j.Logger;

public class RecommendationDataCollectionService
extends Application {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecommendationDataCollectionService.class));
    private final RecommendationDataCollectionServiceI rdcsI;
    private final RecommendationDataCollectionServiceAdminI rdcsAdminI;
    private final String instanceName;

    public RecommendationDataCollectionService(String instanceName, RecommendationDataCollectionServiceI rdcsI, RecommendationDataCollectionServiceAdminI rdcsAdminI) {
        this.instanceName = instanceName;
        this.rdcsI = rdcsI;
        this.rdcsAdminI = rdcsAdminI;
    }

    public int run(String[] arg0) {
        RDCSIceServerApp iceServerApp = new RDCSIceServerApp(this.instanceName, RecommendationDataCollectionService.communicator(), this.rdcsI, this.rdcsAdminI);
        iceServerApp.activateAdaptersAndWaitTillShutdown();
        if (RecommendationDataCollectionService.interrupted()) {
            log.fatal((Object)("RecommendationDataCollectionService@" + HostUtils.getHostname() + ": terminating"));
        }
        return 0;
    }
}

