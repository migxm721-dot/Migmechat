/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SingleThreadedBoundedExecutor
extends AbstractExecutorService {
    public static final int DEFAULT_MAX_TASK_SIZE = 10;
    private final ThreadPoolExecutor executor;
    private final int maxTaskSize;
    private AtomicInteger currentInvokerCount = new AtomicInteger(0);

    public SingleThreadedBoundedExecutor() {
        this(10);
    }

    public SingleThreadedBoundedExecutor(int maxTaskSize) {
        this.maxTaskSize = maxTaskSize;
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public int getMaxTaskSize() {
        return this.maxTaskSize;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.awaitTermination(timeout, unit);
    }

    @Override
    public boolean isShutdown() {
        return this.executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.executor.isTerminated();
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.executor.shutdownNow();
    }

    @Override
    public void execute(final Runnable command) {
        int maxTaskSize;
        int waitingTaskSize = this.executor.getQueue().size();
        if (waitingTaskSize + 1 > (maxTaskSize = this.getMaxTaskSize())) {
            throw new RejectedExecutionException("Pending task full. number of waiting tasks:" + waitingTaskSize + ".Max task size:" + maxTaskSize);
        }
        int currentSize = this.currentInvokerCount.incrementAndGet();
        if (currentSize > maxTaskSize) {
            this.currentInvokerCount.decrementAndGet();
            throw new RejectedExecutionException("Too many task sender:" + currentSize + ".Max task size:" + maxTaskSize);
        }
        this.executor.execute(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                try {
                    command.run();
                    Object var2_1 = null;
                    SingleThreadedBoundedExecutor.this.currentInvokerCount.decrementAndGet();
                }
                catch (Throwable throwable) {
                    Object var2_2 = null;
                    SingleThreadedBoundedExecutor.this.currentInvokerCount.decrementAndGet();
                    throw throwable;
                }
            }
        });
    }

    public int getCurrentInvokerCount() {
        return this.currentInvokerCount.intValue();
    }
}

