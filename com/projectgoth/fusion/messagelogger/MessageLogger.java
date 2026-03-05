/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.messagelogger;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.messagelogger.MessageLoggerAdminI;
import com.projectgoth.fusion.messagelogger.MessageLoggerI;
import com.projectgoth.fusion.stats.IceStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class MessageLogger
extends Application {
    private static final String APP_NAME = "MessageLogger";
    private static final String CONFIG_FILE = "MessageLogger.cfg";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MessageLogger.class));
    public static ObjectAdapter messageLoggerAdapter = null;
    public static Properties properties = null;
    public static MessageLoggerI messageLogger = null;
    public static String hostName = null;
    public static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        int status;
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((java.lang.Object)"MessageLogger version @version@");
        log.info((java.lang.Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        MessageLogger app = new MessageLogger();
        if (args.length >= 1) {
            log.info((java.lang.Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        log.info((java.lang.Object)"Exiting application");
        System.exit(status);
    }

    public int run(String[] arg0) {
        properties = MessageLogger.communicator().getProperties();
        try {
            hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            hostName = "UNKNOWN";
        }
        log.debug((java.lang.Object)"Initialising MessageLogger interface");
        messageLoggerAdapter = MessageLogger.communicator().createObjectAdapter("MessageLoggerAdapter");
        messageLogger = new MessageLoggerI();
        messageLoggerAdapter.add((Object)messageLogger, Util.stringToIdentity((String)APP_NAME));
        IceStats.getInstance().setIceObjects(MessageLogger.communicator(), messageLoggerAdapter, null);
        log.debug((java.lang.Object)"Initialising MessageLoggerAdmin interface");
        ObjectAdapter messageLoggerAdminAdapter = MessageLogger.communicator().createObjectAdapter("MessageLoggerAdminAdapter");
        MessageLoggerAdminI messageLoggerAdmin = new MessageLoggerAdminI();
        messageLoggerAdminAdapter.add((Object)messageLoggerAdmin, Util.stringToIdentity((String)"MessageLoggerAdmin"));
        messageLoggerAdminAdapter.activate();
        messageLoggerAdapter.activate();
        log.info((java.lang.Object)"Service started");
        MessageLogger.communicator().waitForShutdown();
        if (MessageLogger.interrupted()) {
            log.fatal((java.lang.Object)("MessageLogger " + hostName + ": terminating"));
            messageLogger.terminating();
        }
        return 0;
    }
}

