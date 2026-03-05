/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.recommendation.collector;

import com.projectgoth.fusion.common.AppStartupInfo;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.recommendation.collector.DataCollectorContext;
import com.projectgoth.fusion.recommendation.collector.DataCollectorUtils;
import com.projectgoth.fusion.recommendation.collector.RecommendationDataCollectionService;
import com.projectgoth.fusion.recommendation.collector.RecommendationDataCollectionServiceAdminI;
import com.projectgoth.fusion.recommendation.collector.RecommendationDataCollectionServiceI;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class RecommendationDataCollectionServiceMain {
    private static final String APP_NAME = "RecommendationDataCollectionServiceMain";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "RecommendationDataCollectionServiceMain.cfg";
    public static final String JVM_PROPERTY_RDCS_INSTANCE_NAME = "RDCS.Instance.Name";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecommendationDataCollectionServiceMain.class));

    public static void main(String[] args) {
        int status;
        System.out.println("Log Folder :[" + ConfigUtils.getRootLogFolder() + "]");
        ConfigUtils.mkdirsRootLogFolder();
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((Object)"RecommendationDataCollectionServiceMain version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        String rdcsInstanceName = System.getProperty(JVM_PROPERTY_RDCS_INSTANCE_NAME);
        AppStartupInfo appStartupInfo = new AppStartupInfo(System.currentTimeMillis(), args);
        DataCollectorContext dataCollectorCtx = DataCollectorUtils.createDefaultDataCollectorContext(appStartupInfo);
        RecommendationDataCollectionServiceAdminI rdcsAdminI = new RecommendationDataCollectionServiceAdminI(dataCollectorCtx);
        RecommendationDataCollectionServiceI rdcsI = new RecommendationDataCollectionServiceI(dataCollectorCtx);
        RecommendationDataCollectionService app = new RecommendationDataCollectionService(rdcsInstanceName, rdcsI, rdcsAdminI);
        if (args.length >= 1) {
            log.info((Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        log.info((Object)("Exiting application. Status=" + status));
        System.exit(status);
    }
}

