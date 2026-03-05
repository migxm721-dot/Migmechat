/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Sampler;
import com.projectgoth.fusion.common.SharedRedisConnection;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class SharedRedisConnectionRegistry {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SharedRedisConnectionRegistry.class));
    private static SharedRedisConnectionRegistry theRegistry;
    final ConcurrentLinkedQueue<WeakReference<SharedRedisConnection>> connections = new ConcurrentLinkedQueue();
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Object initLock;

    SharedRedisConnectionRegistry() {
    }

    void startMaintenanceThread(int delayInMs) {
        try {
            this.scheduler.schedule(new MaintenanceThread(), (long)delayInMs, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            log.error((Object)("Unable to shedule maintenance thread run : " + e.getMessage()), (Throwable)e);
        }
    }

    void registerConnection(SharedRedisConnection conn) {
        if (conn != null) {
            WeakReference<SharedRedisConnection> ref = new WeakReference<SharedRedisConnection>(conn);
            this.connections.add(ref);
            log.info((Object)("SharedRedisConnection [" + conn.server + "] registered"));
        } else {
            log.warn((Object)"registerConnection is called with a null SharedRedisConnection instance");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static SharedRedisConnectionRegistry getRegistry() {
        if (theRegistry != null) {
            return theRegistry;
        }
        Object object = initLock;
        synchronized (object) {
            if (theRegistry != null) {
                return theRegistry;
            }
            theRegistry = new SharedRedisConnectionRegistry();
            theRegistry.startMaintenanceThread(100);
        }
        return theRegistry;
    }

    static void setRegistry(SharedRedisConnectionRegistry reg) {
        theRegistry = reg;
    }

    static {
        initLock = new Object();
        theRegistry = new SharedRedisConnectionRegistry();
        theRegistry.startMaintenanceThread(100);
    }

    class MaintenanceThread
    implements Runnable {
        private static final String SEPERATOR = ",";
        private static final String NEW_LINE = "\r\n";
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private DecimalFormat decimalFormat = new DecimalFormat("0.00");
        private SimpleDateFormat instrumentationDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private String instrumentationLogFile;

        public MaintenanceThread() {
            String logDir = System.getProperty("log.dir") == null ? "/usr/fusion/logs" : System.getProperty("log.dir");
            String logFilename = System.getProperty("log.filename") == null ? "default" : System.getProperty("log.filename");
            this.instrumentationLogFile = logDir + File.separator + logFilename + ".redis.inst.summary.";
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void run() {
            try {
                try {
                    log.info((Object)("SharedRedisConnectionRegistry maintenance run. Found [" + SharedRedisConnectionRegistry.this.connections.size() + "] registered connections"));
                    Iterator<WeakReference<SharedRedisConnection>> iter = SharedRedisConnectionRegistry.this.connections.iterator();
                    LinkedList<WeakReference<SharedRedisConnection>> forDeletion = new LinkedList<WeakReference<SharedRedisConnection>>();
                    while (iter.hasNext()) {
                        WeakReference<SharedRedisConnection> ref = iter.next();
                        SharedRedisConnection sharedRedisConnection = (SharedRedisConnection)ref.get();
                        if (sharedRedisConnection == null) {
                            forDeletion.add(ref);
                            continue;
                        }
                        this.recordMetrics(sharedRedisConnection);
                    }
                    log.info((Object)("SharedRedisConnectionRegistry maintenance run. Found [" + forDeletion.size() + "] dead connections for removal"));
                    for (WeakReference weakReference : forDeletion) {
                        SharedRedisConnectionRegistry.this.connections.remove(weakReference);
                    }
                    Object var6_8 = null;
                }
                catch (Exception e) {
                    log.error((Object)("Unexpected exception during SharedRedisConnectionRegistry maintenance run: " + e.getMessage()), (Throwable)e);
                    Object var6_9 = null;
                    long runDelayInMs = SystemProperty.getLong(SystemPropertyEntities.Default.SHARED_REDIS_CONNECTION_MAINTENANCE_TASK_RUN_INTERVAL_IN_MS);
                    log.info((Object)("SharedRedisConnectionRegistry maintenance run complete. scheduling next run in [" + runDelayInMs + "] ms"));
                    SharedRedisConnectionRegistry.this.scheduler.schedule(new MaintenanceThread(), runDelayInMs, TimeUnit.MILLISECONDS);
                    return;
                }
            }
            catch (Throwable throwable) {
                Object var6_10 = null;
                long runDelayInMs = SystemProperty.getLong(SystemPropertyEntities.Default.SHARED_REDIS_CONNECTION_MAINTENANCE_TASK_RUN_INTERVAL_IN_MS);
                log.info((Object)("SharedRedisConnectionRegistry maintenance run complete. scheduling next run in [" + runDelayInMs + "] ms"));
                SharedRedisConnectionRegistry.this.scheduler.schedule(new MaintenanceThread(), runDelayInMs, TimeUnit.MILLISECONDS);
                throw throwable;
            }
            long runDelayInMs = SystemProperty.getLong(SystemPropertyEntities.Default.SHARED_REDIS_CONNECTION_MAINTENANCE_TASK_RUN_INTERVAL_IN_MS);
            log.info((Object)("SharedRedisConnectionRegistry maintenance run complete. scheduling next run in [" + runDelayInMs + "] ms"));
            SharedRedisConnectionRegistry.this.scheduler.schedule(new MaintenanceThread(), runDelayInMs, TimeUnit.MILLISECONDS);
        }

        private void recordMetrics(SharedRedisConnection conn) {
            try {
                List<Sampler.Summary> summaries = conn.getSampler().summarize();
                FileOutputStream out = new FileOutputStream(this.instrumentationLogFile + this.instrumentationDateFormat.format(new Date()), true);
                for (Sampler.Summary summary : summaries) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(this.dateFormat.format(new Date())).append(SEPERATOR).append(summary.name).append(SEPERATOR).append(summary.count).append(SEPERATOR).append(summary.min).append(SEPERATOR).append(summary.median).append(SEPERATOR).append(summary.max).append(SEPERATOR).append(this.decimalFormat.format(summary.mean)).append(SEPERATOR).append(this.decimalFormat.format(summary.mean1Percentile)).append(SEPERATOR).append(this.decimalFormat.format(summary.mean5Percentile)).append(SEPERATOR).append(this.decimalFormat.format(summary.standardDeviation)).append(NEW_LINE);
                    out.write(builder.toString().getBytes());
                }
                out.close();
            }
            catch (Exception e) {
                log.error((Object)("Exception caught while recording shared redis connection metrics: " + e.getMessage()), (Throwable)e);
            }
        }
    }
}

