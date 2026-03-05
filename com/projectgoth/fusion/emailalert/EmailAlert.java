/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  javax.mail.Folder
 *  javax.mail.Session
 *  javax.mail.Store
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emailalert;

import Ice.Application;
import Ice.ObjectAdapter;
import Ice.Util;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.emailalert.EmailAlertAdminI;
import com.projectgoth.fusion.emailalert.EmailAlertI;
import com.projectgoth.fusion.emailalert.SMTPServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import org.apache.log4j.Logger;

public class EmailAlert
extends Application {
    private static final String APP_NAME = "EmailAlert";
    private static final String CONFIG_FILE = "EmailAlert.cfg";
    public static Logger logger = Logger.getLogger((String)"EmailAlert");
    public static ObjectAdapter emailAlertAdapter = null;
    public static Ice.Properties properties = null;
    public static IcePrxFinder icePrxFinder = null;
    public static EmailAlertI emailAlert = null;
    public static SMTPServer smtpServer = null;
    public static String hostName = null;
    public static String mailDomain = null;
    public static String imapServerAddress = null;
    public static String aliasesToIgnore = null;
    public static long startTime = System.currentTimeMillis();
    public static RequestCounter receivedNotificationsCounter = new RequestCounter();
    public static RequestCounter processedNotificationsCounter = new RequestCounter();
    public static RequestCounter receivedGatewayQueriesCounter = new RequestCounter();
    public static RequestCounter processedGatewayQueriesCounter = new RequestCounter();
    public static RequestCounter discardedGatewayQueriesCounter = new RequestCounter();
    public static ThreadPoolExecutor gatewayQueriesPool;
    public static ThreadPoolExecutor notificationsThreadPool;

    public static void main(String[] args) {
        int status;
        logger.info((Object)"EmailAlert version @version@");
        logger.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        EmailAlert app = new EmailAlert();
        if (args.length >= 1) {
            logger.info((Object)("Using custom configuration file: " + args[0]));
            status = app.main(APP_NAME, args, args[0]);
        } else {
            status = app.main(APP_NAME, args, CONFIG_FILE);
        }
        System.exit(status);
    }

    public int run(String[] arg0) {
        properties = EmailAlert.communicator().getProperties();
        gatewayQueriesPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(properties.getPropertyAsIntWithDefault("GatewayQueriesThreadPoolSize", 10));
        notificationsThreadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(properties.getPropertyAsIntWithDefault("NotificationsThreadPoolSize", 10));
        icePrxFinder = new IcePrxFinder(EmailAlert.communicator(), properties);
        try {
            hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
        }
        catch (UnknownHostException e) {
            hostName = "UNKNOWN";
        }
        mailDomain = properties.getProperty("MailDomain").toLowerCase();
        if (mailDomain == null || mailDomain.length() == 0) {
            System.out.println("MailDomain must be specified in the configuration file");
            return 0;
        }
        logger.info((Object)("Mail domain: " + mailDomain));
        aliasesToIgnore = properties.getProperty("AliasesToIgnore");
        if (aliasesToIgnore == null || aliasesToIgnore.length() == 0) {
            System.out.println("AliasesToIgnore must be specified in the configuration file");
            return 0;
        }
        logger.info((Object)("Aliases to ignore: " + aliasesToIgnore));
        imapServerAddress = properties.getProperty("IMAPServerAddress");
        if (imapServerAddress == null || imapServerAddress.length() == 0) {
            System.out.println("IMAPServerAddress must be specified in the configuration file");
            return 0;
        }
        logger.info((Object)("IMAP server: " + imapServerAddress));
        emailAlertAdapter = EmailAlert.communicator().createObjectAdapter("EmailAlertAdapter");
        emailAlert = new EmailAlertI();
        emailAlertAdapter.add((Ice.Object)emailAlert, Util.stringToIdentity((String)APP_NAME));
        int port = properties.getPropertyAsIntWithDefault("SocketPort", 25);
        try {
            smtpServer = new SMTPServer(port);
        }
        catch (Exception e) {
            System.out.println("Could not listen on port " + port + ": " + e.getMessage());
            return 0;
        }
        ObjectAdapter emailAlertAdminAdapter = EmailAlert.communicator().createObjectAdapter("EmailAlertAdminAdapter");
        EmailAlertAdminI emailAlertAdmin = new EmailAlertAdminI();
        emailAlertAdminAdapter.add((Ice.Object)emailAlertAdmin, Util.stringToIdentity((String)"EmailAlertAdmin"));
        emailAlertAdminAdapter.activate();
        emailAlertAdapter.activate();
        logger.info((Object)"Service started");
        EmailAlert.communicator().waitForShutdown();
        if (EmailAlert.interrupted()) {
            logger.fatal((Object)("EmailAlert " + hostName + ": terminating"));
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int getUnreadEmailCountFromIMAP(String username, String password) throws Exception {
        int n;
        Store store;
        block4: {
            store = null;
            Folder folder = null;
            try {
                Session session = Session.getInstance((Properties)System.getProperties(), null);
                store = session.getStore("imap");
                store.connect(imapServerAddress, username, password);
                folder = store.getFolder("INBOX");
                folder.open(1);
                n = folder.getUnreadMessageCount();
                Object var7_6 = null;
                if (folder == null) break block4;
            }
            catch (Throwable throwable) {
                block5: {
                    Object var7_7 = null;
                    if (folder != null) {
                        folder.close(false);
                    }
                    if (store == null) break block5;
                    store.close();
                }
                throw throwable;
            }
            folder.close(false);
        }
        if (store != null) {
            store.close();
        }
        return n;
    }
}

