/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Util
 *  org.apache.log4j.Logger
 *  org.apache.log4j.xml.DOMConfigurator
 */
package com.projectgoth.fusion.smsengine;

import Ice.Application;
import Ice.ObjectAdapter;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.slice.SMSEngineStats;
import com.projectgoth.fusion.smsengine.DispatchThread;
import com.projectgoth.fusion.smsengine.RetryPendingSMSTask;
import com.projectgoth.fusion.smsengine.RoutingTable;
import com.projectgoth.fusion.smsengine.SMSEngineAdminI;
import com.projectgoth.fusion.smsengine.SMSSenderI;
import com.projectgoth.fusion.stats.IceStats;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class SMSEngine
extends Application
implements Runnable {
    private static final String APP_NAME = "SMSEngine";
    private static final String CONFIG_FILE = "SMSEngine.cfg";
    private static final int THEAD_POOL_SIZE = 20;
    private static final int RETRY_PENDING_SMS_INTERVAL = 3;
    private static final int CHECK_CONSOLE_INTERVAL = 2000;
    private static final int PULL_MESSAGES_CREATED_BEFORE = 720;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SMSEngine.class));
    private ScheduledExecutorService pool;
    private ObjectAdapter smsEngineAdaptor;
    private boolean shutdown;
    private RequestCounter requestCounter;
    private long startTime;
    private int requestsReceived;
    private int requestsDispatched;

    public SMSEngineStats getStats() {
        SMSEngineStats stats = ServiceStatsFactory.getSMEngineStats(this.startTime);
        stats.requestsReceived = this.requestsReceived;
        stats.requestsDispatched = this.requestsDispatched;
        stats.requestsPerSecond = this.requestCounter.getRequestsPerSecond();
        stats.maxRequestsPerSecond = this.requestCounter.getMaxRequestsPerSecond();
        return stats;
    }

    private void checkConsoleInput() {
        try {
            while (System.in.available() > 0) {
                int c = System.in.read();
                if (c != 114 && c != 82) continue;
                log.info((Object)"Reloading routing table");
                RoutingTable.load();
                log.info((Object)"Routing table reloaded");
                System.in.skip(System.in.available());
            }
        }
        catch (IOException e) {
            log.warn((Object)("Exception caught while reading from System.in - " + e.toString()));
        }
        catch (Exception e) {
            log.warn((Object)("Failed to load SMS routing table - " + e.toString()));
        }
    }

    public int run(String[] arg0) {
        try {
            RoutingTable.load();
        }
        catch (Exception e) {
            log.fatal((Object)("Failed to load SMS routing table - " + e.toString()));
            System.exit(-1);
        }
        this.requestCounter = new RequestCounter();
        this.startTime = System.currentTimeMillis();
        this.shutdown = false;
        this.smsEngineAdaptor = SMSEngine.communicator().createObjectAdapter("SMSEngineAdapter");
        this.smsEngineAdaptor.add((Ice.Object)new SMSEngineAdminI(this), Util.stringToIdentity((String)"SMSEngineAdmin"));
        this.smsEngineAdaptor.add((Ice.Object)new SMSSenderI(this), Util.stringToIdentity((String)"SMSSender"));
        this.smsEngineAdaptor.activate();
        IceStats.getInstance().setIceObjects(SMSEngine.communicator(), this.smsEngineAdaptor, null);
        int threadPoolSize = SMSEngine.communicator().getProperties().getPropertyAsIntWithDefault("MaxThreadPoolSize", 20);
        this.pool = Executors.newScheduledThreadPool(threadPoolSize);
        long retryPendingSMSInterval = SMSEngine.communicator().getProperties().getPropertyAsIntWithDefault("RetryPendingSMSInterval", 3);
        int pullMessagesCreatedBefore = SMSEngine.communicator().getProperties().getPropertyAsIntWithDefault("pullMessagesCreatedBefore", 720);
        if (retryPendingSMSInterval == 0L) {
            log.info((Object)"Load pending SMS from database is disabled\n");
        } else {
            log.info((Object)("Load pending SMS from database every " + retryPendingSMSInterval + " minute(s)\n"));
            new Timer().schedule((TimerTask)new RetryPendingSMSTask(this, pullMessagesCreatedBefore), 0L, retryPendingSMSInterval * 60000L);
        }
        new Thread(this).start();
        SMSEngine.communicator().waitForShutdown();
        this.shutdown = true;
        this.pool.shutdown();
        return 0;
    }

    public void run() {
        while (!this.shutdown) {
            try {
                this.checkConsoleInput();
                Thread.sleep(2000L);
            }
            catch (Exception e) {
                log.warn((Object)("Exception caught while checking user input - " + e.getMessage()));
            }
        }
    }

    public synchronized void queueDispatchThread(DispatchThread thread) {
        this.queueDispatchThread(thread, 0L);
    }

    public synchronized void queueDispatchThread(DispatchThread thread, long delay) {
        if (!SystemProperty.getBool(SystemPropertyEntities.SmsSettings.SMS_ENGINE_ENABLED)) {
            if (SystemProperty.getBool(SystemPropertyEntities.SmsSettings.LOG_REFUSED_TO_SEND)) {
                log.info((Object)"Not sending SMS : SMSEngine disabled");
            }
            return;
        }
        try {
            ++this.requestsReceived;
            this.requestCounter.add();
            this.pool.schedule(thread, delay, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            log.warn((Object)("Failed to queue message to thread pool - " + e.toString()));
        }
    }

    public synchronized void onMessageSent() {
        ++this.requestsDispatched;
    }

    public static void main(String[] args) {
        DOMConfigurator.configureAndWatch((String)ConfigUtils.getDefaultLog4jConfigFilename());
        log.info((Object)"SMSEngine version @version@");
        log.info((Object)"Copyright (c) 2005-2008 Project Goth Inc. All rights reserved");
        SMSEngine smsEngine = new SMSEngine();
        String configFile = args.length > 0 ? args[0] : CONFIG_FILE;
        int status = smsEngine.main(smsEngine.getClass().getName(), args, configFile);
        System.exit(status);
    }
}

