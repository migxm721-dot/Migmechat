/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 */
package com.projectgoth.fusion.recommendation.collector;

import Ice.Communicator;
import com.projectgoth.fusion.common.BasicIceServerApp;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.recommendation.collector.RecommendationDataCollectionServiceAdminI;
import com.projectgoth.fusion.recommendation.collector.RecommendationDataCollectionServiceI;
import java.util.Properties;

public class RDCSIceServerApp
extends BasicIceServerApp {
    public static final String ICE_SERVER_NAME = "RDCSIceServerApp";
    public static final String SERVICE_ADAPTER_ENDPOINT_NAME = "RecommendationDataCollectionServiceAdapter";
    public static final String ADMIN_ADAPTER_ENDPOINT_NAME = "RecommendationDataCollectionServiceAdminAdapter";
    public static final String RDCS_IDENTITY_NAME = "RecommendationDataCollectionService";
    public static final String RDCSADMIN_IDENTITY_NAME = "RecommendationDataCollectionServiceAdmin";
    private final RecommendationDataCollectionServiceI rdcsI;
    private final RecommendationDataCollectionServiceAdminI rdcsAdminI;

    public RDCSIceServerApp(String localName, String configPath, Properties overrideProperties, RecommendationDataCollectionServiceI rdcsI, RecommendationDataCollectionServiceAdminI rdcsAdminI) {
        super(RDCSIceServerApp.getRDCSInstanceName(localName), configPath, overrideProperties);
        this.rdcsI = rdcsI;
        this.rdcsAdminI = rdcsAdminI;
        this.configure();
    }

    public RDCSIceServerApp(String localName, Communicator communicator, RecommendationDataCollectionServiceI rdcsI, RecommendationDataCollectionServiceAdminI rdcsAdminI) {
        super(RDCSIceServerApp.getRDCSInstanceName(localName), communicator);
        this.rdcsI = rdcsI;
        this.rdcsAdminI = rdcsAdminI;
        this.configure();
    }

    public static String getRDCSInstanceName(String localName) {
        if (StringUtil.isBlank(localName)) {
            return ICE_SERVER_NAME;
        }
        return "RDCSIceServerApp(" + localName + ")";
    }

    private void configure() {
        this.addServantObject(SERVICE_ADAPTER_ENDPOINT_NAME, this.rdcsI, RDCS_IDENTITY_NAME);
        this.addServantObject(ADMIN_ADAPTER_ENDPOINT_NAME, this.rdcsAdminI, RDCSADMIN_IDENTITY_NAME);
    }
}

