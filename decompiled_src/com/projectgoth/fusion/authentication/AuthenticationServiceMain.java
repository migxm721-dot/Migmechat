/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.context.ApplicationContext
 */
package com.projectgoth.fusion.authentication;

import com.projectgoth.fusion.authentication.AuthenticationService;
import com.projectgoth.fusion.authentication.AuthenticationServiceContext;
import com.projectgoth.fusion.common.ConfigUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;

public class AuthenticationServiceMain {
    private static final String APP_NAME = "AuthenticationService";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "AuthenticationService.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AuthenticationServiceMain.class));
    private static ApplicationContext context;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        log.info((Object)"AuthenticationService version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        context = AuthenticationServiceContext.getContext();
        AuthenticationService app = (AuthenticationService)((Object)context.getBean("authenticationService"));
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

