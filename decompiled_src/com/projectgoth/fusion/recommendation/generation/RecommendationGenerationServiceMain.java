/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.recommendation.generation;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.recommendation.generation.RecommendationGenerationService;
import com.projectgoth.fusion.recommendation.generation.RecommendationGenerationServiceI;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class RecommendationGenerationServiceMain {
    private static final String APP_NAME = "RecommendationGenerationService";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "RecommendationGenerationService.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RecommendationGenerationServiceMain.class));

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((Object)"RecommendationGenerationService version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        RecommendationGenerationServiceI servant = new RecommendationGenerationServiceI();
        RecommendationGenerationService app = new RecommendationGenerationService(servant);
        if (args.length >= 1) {
            log.info((Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        log.info((Object)"Exiting application");
        System.exit(status);
    }
}

