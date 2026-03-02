/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.context.ApplicationContext
 */
package com.projectgoth.fusion.botservice;

import com.projectgoth.fusion.botservice.BotService;
import com.projectgoth.fusion.botservice.BotServiceContext;
import com.projectgoth.fusion.common.ConfigUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;

public class BotServiceMain {
    private static final String APP_NAME = "BotService";
    private static final String CONFIG_FILE = "BotService.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BotServiceMain.class));
    private static ApplicationContext context;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((Object)"BotService version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        context = BotServiceContext.getContext();
        BotService app = (BotService)((Object)context.getBean("botService"));
        if (args.length >= 1) {
            log.info((Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        log.info((Object)"Exiting the BotService application");
        System.exit(status);
    }
}

