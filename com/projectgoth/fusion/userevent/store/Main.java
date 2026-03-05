/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 *  org.springframework.context.ApplicationContext
 */
package com.projectgoth.fusion.userevent.store;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.userevent.store.EventStore;
import com.projectgoth.fusion.userevent.store.EventStoreApplicationContext;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;

public class Main {
    private static final String APP_NAME = "EventStore";
    private static final String CONFIG_FILE = ConfigUtils.getConfigDirectory() + "EventStore.cfg";
    private static Logger log = Logger.getLogger(Main.class);
    private static ApplicationContext context;

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)"log4j.xml");
        log.info((Object)"EventStore version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        context = EventStoreApplicationContext.getContext();
        EventStore app = (EventStore)((Object)context.getBean("eventStore"));
        if (args.length >= 1) {
            log.info((Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        System.exit(status);
    }
}

