/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.LocalException
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.eventqueue;

import Ice.Application;
import Ice.LocalException;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.eventqueue.WorkerAdminI;
import com.projectgoth.fusion.eventqueue.WorkerI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class Worker
extends Application {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Worker.class));
    private int ID;
    public static String WORKER_ADMIN_ICE_APP_NAME = "EventQueueWorkerAdmin";
    public static String WORKER_ICE_APP_NAME = "EventQueueWorker";
    public static ObjectAdapter workerAdapter = null;
    public static ObjectAdapter workerAdminAdapter = null;
    public static Properties properties = null;
    public static String hostName;
    public static long startTime;
    private WorkerI worker;
    private WorkerAdminI workerAdmin;
    private static final String CONFIG_FILE = "EventQueue.cfg";

    public static void main(String[] args) {
        String configFile = args.length > 0 ? args[0] : CONFIG_FILE;
        Worker worker = new Worker();
        int status = worker.main("EventQueue", args, configFile);
        System.exit(status);
    }

    public boolean isRunning() {
        return this.worker != null && this.worker.isRunning();
    }

    public synchronized void shutdown() {
        try {
            if (this.isRunning()) {
                this.worker.shutdownAllWorkerThreads();
            }
        }
        catch (Exception e) {
            log.error((java.lang.Object)("Exception caught while shutting down worker threads: " + e.getMessage()), (Throwable)e);
        }
    }

    public int run(String[] args) {
        int returnStatus = 0;
        log.info((java.lang.Object)"Starting worker application");
        Worker.shutdownOnInterrupt();
        log.info((java.lang.Object)String.format("Starting eventqueue worker ...", new java.lang.Object[0]));
        try {
            int workerID;
            log.info((java.lang.Object)"Retrieving configuration properties from Communicator object");
            properties = Worker.communicator().getProperties();
            log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("EventQueueWorkerAdapter.Endpoints") + "]"));
            log.info((java.lang.Object)("Configured endpoint [" + properties.getProperty("EventQueueWorkerAdminAdapter.Endpoints") + "]"));
            log.info((java.lang.Object)("Configured worked ID [" + properties.getProperty("EventQueueWorkerAdapter.WorkerID") + "]"));
            try {
                workerID = Integer.parseInt(properties.getProperty("EventQueueWorkerAdapter.WorkerID"));
            }
            catch (Exception e) {
                log.error((java.lang.Object)String.format("Unable to determine workerID from arguments: %s. Please check your configuration file. Terminating worker.", e.getMessage()));
                return 1;
            }
            try {
                log.info((java.lang.Object)"Getting hostName");
                hostName = InetAddress.getLocalHost().getHostName().toUpperCase();
            }
            catch (UnknownHostException e) {
                log.error((java.lang.Object)"Unable to determine hostname for Worker", (Throwable)e);
                hostName = "UNKNOWN";
            }
            log.info((java.lang.Object)String.format("Hostname found [%s]", hostName));
            log.info((java.lang.Object)"Initialising EventQueueWorker interface");
            workerAdapter = Worker.communicator().createObjectAdapter(WORKER_ICE_APP_NAME + "Adapter");
            this.worker = new WorkerI(workerID);
            workerAdapter.add((Object)this.worker, Util.stringToIdentity((String)WORKER_ICE_APP_NAME));
            log.info((java.lang.Object)"Initialising WorkerAdmin interface");
            workerAdminAdapter = Worker.communicator().createObjectAdapter(WORKER_ADMIN_ICE_APP_NAME + "Adapter");
            this.workerAdmin = new WorkerAdminI(this.worker);
            workerAdminAdapter.add((Object)this.workerAdmin, Util.stringToIdentity((String)WORKER_ADMIN_ICE_APP_NAME));
            workerAdminAdapter.activate();
            workerAdapter.activate();
            log.info((java.lang.Object)"Service started");
            Worker.setInterruptHook((Thread)new ShutdownHook());
            Worker.communicator().waitForShutdown();
        }
        catch (LocalException e) {
            e.printStackTrace();
            returnStatus = 1;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            returnStatus = 1;
        }
        return returnStatus;
    }

    static {
        startTime = System.currentTimeMillis();
    }

    private class ShutdownHook
    extends Thread {
        private ShutdownHook() {
        }

        public void run() {
            Worker.this.shutdown();
        }
    }
}

