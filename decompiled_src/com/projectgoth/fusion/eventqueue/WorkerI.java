/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.eventqueue;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.RequestAndRateLongCounter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.eventqueue.EventQueue;
import com.projectgoth.fusion.eventqueue.queues.EventQueueClient;
import com.projectgoth.fusion.slice._EventQueueWorkerDisp;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class WorkerI
extends _EventQueueWorkerDisp {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(WorkerI.class));
    private ExecutorService executor;
    private int ID;
    private boolean isRunning;
    private static final long serialVersionUID = 1L;
    private RequestAndRateLongCounter totalCounter;
    private ConcurrentHashMap<Enums.EventTypeEnum, RequestAndRateLongCounter> eventCounters;
    private long queueSize;
    private long maxQueueSize;
    private long lastQueueCheckTS;
    private int poolSize;
    private CountDownLatch taskStartSignal;

    public WorkerI(int workerID) {
        this.ID = workerID;
        this.isRunning = false;
        this.taskStartSignal = new CountDownLatch(1);
        this.totalCounter = new RequestAndRateLongCounter(5);
        this.eventCounters = new ConcurrentHashMap();
        for (Enums.EventTypeEnum e : Enums.EventTypeEnum.getAllTypes()) {
            this.eventCounters.put(e, new RequestAndRateLongCounter(5));
        }
        this.queueSize = 0L;
        this.maxQueueSize = 0L;
        this.lastQueueCheckTS = 0L;
        this.poolSize = SystemProperty.getInt(SystemPropertyEntities.EventQueueSettings.WORKER_THREAD_POOL_SIZE);
        this.executor = Executors.newFixedThreadPool(this.poolSize);
        for (int i = 0; i < this.poolSize; ++i) {
            int threadID = workerID * 10000 + i;
            this.executor.execute(new WorkerTask(threadID));
        }
        this.taskStartSignal.countDown();
        this.setRunning(true);
        log.info((Object)(this.poolSize + " WorkerTask threads added to executor service"));
    }

    public long getQueueSize() {
        return this.queueSize;
    }

    public long getMaxQueueSize() {
        return this.maxQueueSize;
    }

    public RequestAndRateLongCounter getTotalCounter() {
        return this.totalCounter;
    }

    public RequestAndRateLongCounter getEventCounter(Enums.EventTypeEnum type) {
        return this.eventCounters.get((Object)type);
    }

    private synchronized void setRunning(boolean b) {
        this.isRunning = b;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void shutdownAllWorkerThreads() throws Exception {
        this.setRunning(false);
        this.executor.shutdown();
        this.executor.awaitTermination(SystemProperty.getInt(SystemPropertyEntities.EventQueueSettings.SHUTDOWN_WAIT_IN_SECONDS), TimeUnit.SECONDS);
        if (!this.executor.isTerminated()) {
            this.executor.shutdownNow();
        }
    }

    static /* synthetic */ Logger access$000() {
        return log;
    }

    static /* synthetic */ CountDownLatch access$100(WorkerI x0) {
        return x0.taskStartSignal;
    }

    static /* synthetic */ boolean access$200(WorkerI x0) {
        return x0.isRunning;
    }

    static /* synthetic */ long access$300(WorkerI x0) {
        return x0.lastQueueCheckTS;
    }

    static /* synthetic */ long access$402(WorkerI x0, long x1) {
        x0.queueSize = x1;
        return x0.queueSize;
    }

    static /* synthetic */ long access$400(WorkerI x0) {
        return x0.queueSize;
    }

    static /* synthetic */ long access$500(WorkerI x0) {
        return x0.maxQueueSize;
    }

    static /* synthetic */ long access$502(WorkerI x0, long x1) {
        x0.maxQueueSize = x1;
        return x0.maxQueueSize;
    }

    static /* synthetic */ ConcurrentHashMap access$600(WorkerI x0) {
        return x0.eventCounters;
    }

    static /* synthetic */ RequestAndRateLongCounter access$700(WorkerI x0) {
        return x0.totalCounter;
    }

    static /* synthetic */ ExecutorService access$800(WorkerI x0) {
        return x0.executor;
    }

    private class WorkerTask
    implements Runnable {
        int threadID;
        EventQueueClient client;

        public WorkerTask(int threadID) {
            this.threadID = threadID;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void run() {
            block29: {
                try {
                    WorkerI.access$000().info((Object)("WorkerTask thread [" + this.threadID + "]: thread starting up."));
                    WorkerI.access$100(WorkerI.this).await();
                    WorkerI.access$000().info((Object)("WorkerTask thread [" + this.threadID + "]: thread started."));
                    this.client = EventQueue.getClient();
                    if (this.client == null) {
                        throw new Exception("WorkerTask thread [" + this.threadID + "]: Unable to get eventqueue client.");
                    }
                    this.client.setProcessingQueueID(this.threadID);
                    while (WorkerI.access$200(WorkerI.this)) {
                        WorkerI.access$000().info((Object)String.format("WorkerTask thread [" + this.threadID + "]: waiting for events from queue", new Object[0]));
                        inProcess = this.client.numberOfEventsInProcess();
                        while (inProcess > 0) {
                            WorkerI.access$000().info((Object)String.format("WorkerTask thread [" + this.threadID + "]: Found %d events in processing queue. requeuing", new Object[]{inProcess}));
                            this.client.requeueEventInProcess();
                            inProcess = this.client.numberOfEventsInProcess();
                        }
                        totalPending = this.client.numberOfEventsPending();
                        WorkerI.access$000().info((Object)String.format("WorkerTask thread [" + this.threadID + "]: There are %d events pending in queue", new Object[]{totalPending}));
                        if (totalPending > (long)SystemProperty.getInt(SystemPropertyEntities.EventQueueSettings.MAXIMUM_WORKER_QUEUE_SIZE)) {
                            WorkerI.access$000().info((Object)"Attempting to auto purge events from queue");
                            cleared = this.client.flushPendingQueue();
                            if (cleared > 0) {
                                WorkerI.access$000().info((Object)String.format("WorkerTask thread [" + this.threadID + "]: Successfully cleared events from queue", new Object[]{cleared}));
                            } else {
                                WorkerI.access$000().error((Object)("WorkerTask thread [" + this.threadID + "]: Unable to clear pending events."));
                            }
                        }
                        event = this.client.getForProcessing();
                        if (System.currentTimeMillis() - WorkerI.access$300(WorkerI.this) > (long)SystemProperty.getInt(SystemPropertyEntities.EventQueueSettings.QUEUE_CHECK_INTERVAL_IN_SECONDS) * 1000L) {
                            WorkerI.access$402(WorkerI.this, this.client.numberOfEventsPending());
                            if (WorkerI.access$400(WorkerI.this) > WorkerI.access$500(WorkerI.this)) {
                                WorkerI.access$502(WorkerI.this, WorkerI.access$400(WorkerI.this));
                            }
                        }
                        if (event == null) {
                            WorkerI.access$000().info((Object)String.format("WorkerTask thread [" + this.threadID + "]: Received null message. Ignoring.", new Object[0]));
                        } else {
                            WorkerI.access$000().info((Object)String.format("WorkerTask thread [" + this.threadID + "]: Processing event from queue: [%s] [%s] [%d]", new Object[]{event.type.description, event.eventSubject, event.timestamp}));
                            if (WorkerI.access$600(WorkerI.this).containsKey((Object)event.type)) {
                                WorkerI.access$700(WorkerI.this).add();
                                ((RequestAndRateLongCounter)WorkerI.access$600(WorkerI.this).get((Object)event.type)).add();
                            }
                            if (event.execute()) {
                                WorkerI.access$000().info((Object)("WorkerTask thread [" + this.threadID + "]: Done. Acknowledging message."));
                                this.client.processingCompleted(true);
                                WorkerI.access$000().info((Object)("WorkerTask thread [" + this.threadID + "]: Acknowledged"));
                            } else if (event.retryOnFailure()) {
                                WorkerI.access$000().error((Object)String.format("WorkerTask thread [" + this.threadID + "]: Failed to execute event. requeueing it for retry [%s] [%s] [%d]", new Object[]{event.type.description, event.eventSubject, event.timestamp}));
                                this.client.requeueEventInProcess();
                                WorkerI.access$000().info((Object)("WorkerTask thread [" + this.threadID + "]: ReQueued"));
                            } else {
                                WorkerI.access$000().error((Object)String.format("WorkerTask thread [" + this.threadID + "]: Failed to execute event. dropping it [%s] [%s] [%d]", new Object[]{event.type.description, event.eventSubject, event.timestamp}));
                                this.client.processingCompleted(false);
                                WorkerI.access$000().info((Object)("WorkerTask thread [" + this.threadID + "]: Ignored"));
                            }
                        }
                        if (SystemProperty.getBool(SystemPropertyEntities.EventQueueSettings.PERSISTENT_REDIS_CONNECTION_ENABLED)) continue;
                        this.client.disconnectClient();
                        this.client = EventQueue.getClient();
                    }
                    var6_9 = null;
                    if (this.client == null) break block29;
                    ** GOTO lbl82
                }
                catch (Exception e) {
                    WorkerI.access$000().error((Object)("WorkerTask thread [" + this.threadID + "]: terminated due to exception - " + e.getMessage()), (Throwable)e);
                    var6_10 = null;
                    if (this.client != null) {
                        try {
                            this.client.disconnectClient();
                        }
                        catch (Exception e) {
                            this.client = null;
                        }
                    }
                    break block29;
                }
                {
                    catch (Throwable var5_15) {
                        var6_11 = null;
                        if (this.client == null) throw var5_15;
                        ** try [egrp 2[TRYBLOCK] [4 : 1117->1129)] { 
lbl77:
                        // 1 sources

                        this.client.disconnectClient();
                        throw var5_15;
lbl79:
                        // 1 sources

                        catch (Exception e) {
                            this.client = null;
                        }
                        throw var5_15;
                    }
lbl82:
                    // 1 sources

                    ** try [egrp 2[TRYBLOCK] [4 : 1117->1129)] { 
lbl83:
                    // 1 sources

                    this.client.disconnectClient();
lbl85:
                    // 1 sources

                    catch (Exception e) {}
                    this.client = null;
                }
            }
            if (WorkerI.access$200(WorkerI.this) == false) return;
            try {
                try {
                    retryIntervalInSeconds = SystemProperty.getInt(SystemPropertyEntities.EventQueueSettings.RETRY_INTERVAL_IN_SECONDS);
                    WorkerI.access$000().info((Object)("WorkerTask thread [" + this.threadID + "]: Attempting another restart in " + retryIntervalInSeconds + " seconds"));
                    Thread.sleep(retryIntervalInSeconds * 1000);
                }
                catch (InterruptedException var1_5) {
                    var9_17 = null;
                    WorkerI.access$800(WorkerI.this).execute(this);
                    return;
                }
                var9_16 = null;
            }
            catch (Throwable var8_19) {
                var9_18 = null;
                WorkerI.access$800(WorkerI.this).execute(this);
                throw var8_19;
            }
            WorkerI.access$800(WorkerI.this).execute(this);
        }
    }
}

