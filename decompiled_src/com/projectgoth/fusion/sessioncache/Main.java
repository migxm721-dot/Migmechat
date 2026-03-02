/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.context.ApplicationContext
 */
package com.projectgoth.fusion.sessioncache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.sessioncache.SessionCache;
import com.projectgoth.fusion.sessioncache.SessionCacheApplicationContext;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;

public class Main {
    private static final String APP_NAME = "SessionCache";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "SessionCache.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Main.class));
    private static ApplicationContext context;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        log.info((Object)"SessionCache version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        context = SessionCacheApplicationContext.getContext();
        SessionCache app = (SessionCache)((Object)context.getBean("sessionCache"));
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

