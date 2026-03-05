/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.common.RequestCounter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InstrumentedThreadPool
implements Executor {
    private ThreadPoolExecutor pool;
    private ScheduledThreadPoolExecutor scheduler;
    private RequestCounter requestCounter;
    private int maxQueueSize;

    public InstrumentedThreadPool() {
        this.pool = (ThreadPoolExecutor)Executors.newCachedThreadPool();
        this.scheduler = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1);
        this.requestCounter = new RequestCounter();
        this.maxQueueSize = Integer.MAX_VALUE;
    }

    public InstrumentedThreadPool(int nThreads, int maxQueueSize) {
        this.pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(nThreads);
        this.scheduler = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1);
        this.requestCounter = new RequestCounter();
        this.maxQueueSize = maxQueueSize;
    }

    public int getActiveCount() {
        return this.pool.getActiveCount();
    }

    public int getCorePoolSize() {
        return this.pool.getCorePoolSize();
    }

    public int getLargestPoolSize() {
        return this.pool.getLargestPoolSize();
    }

    public int getPoolSize() {
        return this.pool.getPoolSize();
    }

    public int getQueueSize() {
        return this.pool.getQueue().size();
    }

    public BlockingQueue<Runnable> getQueue() {
        return this.pool.getQueue();
    }

    public Task getEldestTaskOnQueue() {
        return (Task)this.pool.getQueue().peek();
    }

    public long getNumRequests() {
        return this.requestCounter.getNumRequests();
    }

    public float getRequestsPerSecond() {
        return this.requestCounter.getRequestsPerSecond();
    }

    public float getMaxRequestsPerSecond() {
        return this.requestCounter.getMaxRequestsPerSecond();
    }

    public long getCompletedTaskCount() {
        return this.pool.getCompletedTaskCount();
    }

    @Override
    public void execute(Runnable command) {
        if (this.pool.getQueue().size() >= this.maxQueueSize) {
            throw new RejectedExecutionException();
        }
        this.requestCounter.add();
        this.pool.execute(new Task(command));
    }

    public void schedule(final Runnable command, long delay, TimeUnit unit) {
        if (this.pool.getQueue().size() >= this.maxQueueSize) {
            throw new RejectedExecutionException();
        }
        this.requestCounter.add();
        this.scheduler.schedule(new Runnable(){

            public void run() {
                InstrumentedThreadPool.this.pool.execute(new Task(command));
            }
        }, delay, unit);
    }

    public void shutdown() {
        this.scheduler.shutdown();
        this.pool.shutdown();
    }

    public List<Task> shutdownNow() {
        LinkedList<Task> unexecutedTasks = new LinkedList<Task>();
        for (Runnable command : this.scheduler.shutdownNow()) {
            unexecutedTasks.add(new Task(command));
        }
        for (Runnable command : this.pool.shutdownNow()) {
            unexecutedTasks.add((Task)command);
        }
        return unexecutedTasks;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.pool.awaitTermination(timeout, unit);
    }

    public static final class Task
    implements Runnable {
        private final Runnable command;
        private final long timeCreated;

        public Task(Runnable command) {
            this.command = command;
            this.timeCreated = System.currentTimeMillis();
        }

        public long getTimeCreated() {
            return this.timeCreated;
        }

        public void run() {
            this.command.run();
        }
    }
}

