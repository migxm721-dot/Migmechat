/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.context.ApplicationContext
 */
package com.projectgoth.fusion.jobscheduling;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.jobscheduling.JobSchedulingService;
import com.projectgoth.fusion.jobscheduling.JobSchedulingServiceContext;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;

public class JobSchedulingServiceMain {
    private static final String APP_NAME = "JobSchedulingService";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "JobSchedulingService.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(JobSchedulingServiceMain.class));
    private static ApplicationContext context;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        log.info((Object)"JobSchedulingService version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        context = JobSchedulingServiceContext.getContext();
        JobSchedulingService app = (JobSchedulingService)((Object)context.getBean("jobSchedulingService"));
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

