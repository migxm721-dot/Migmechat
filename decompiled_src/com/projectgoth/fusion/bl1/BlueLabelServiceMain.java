/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.context.ApplicationContext
 */
package com.projectgoth.fusion.bl1;

import com.projectgoth.fusion.bl1.BlueLabelService;
import com.projectgoth.fusion.bl1.BlueLabelServiceContext;
import com.projectgoth.fusion.common.ConfigUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;

public class BlueLabelServiceMain {
    private static final String APP_NAME = "BlueLabelService";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "BlueLabelService.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BlueLabelServiceMain.class));
    private static ApplicationContext context;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        log.info((Object)"BlueLabelService version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        context = BlueLabelServiceContext.getContext();
        BlueLabelService app = (BlueLabelService)((Object)context.getBean("blueLabelService"));
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

