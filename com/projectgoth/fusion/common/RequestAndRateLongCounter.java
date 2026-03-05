/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RequestAndRateLongCounter
extends TimerTask {
    private AtomicInteger intervalRequests = new AtomicInteger(0);
    private AtomicLong totalRequests = new AtomicLong(0L);
    private int updateInterval;
    private int requestsPerSecond = 0;
    private int maxRequestsPerSecond = 0;
    private Date dateOfMaxRequestsPerSecond = new Date();

    public RequestAndRateLongCounter() {
        this.updateInterval = 10;
        this.start();
    }

    public RequestAndRateLongCounter(int updateIntervalSeonds) {
        this.updateInterval = updateIntervalSeonds;
        this.start();
    }

    public void start() {
        Timer timer = new Timer(true);
        timer.schedule((TimerTask)this, this.updateInterval * 1000, (long)(this.updateInterval * 1000));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void run() {
        RequestAndRateLongCounter requestAndRateLongCounter = this;
        synchronized (requestAndRateLongCounter) {
            int tempIntervalRequests = this.intervalRequests.intValue();
            this.requestsPerSecond = tempIntervalRequests == 0 ? 0 : tempIntervalRequests / this.updateInterval;
            this.intervalRequests.set(0);
            if (this.requestsPerSecond > this.maxRequestsPerSecond) {
                this.maxRequestsPerSecond = this.requestsPerSecond;
                this.dateOfMaxRequestsPerSecond.setTime(System.currentTimeMillis());
            }
        }
    }

    public void add() {
        this.intervalRequests.incrementAndGet();
        this.totalRequests.incrementAndGet();
    }

    public synchronized int getRequestsPerSecond() {
        return this.requestsPerSecond;
    }

    public synchronized int getMaxRequestsPerSecond() {
        return this.maxRequestsPerSecond;
    }

    public synchronized Date getDateOfMaxRequestsPerSecond() {
        return this.dateOfMaxRequestsPerSecond;
    }

    public long getTotalRequests() {
        return this.totalRequests.longValue();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("RPS ").append(this.requestsPerSecond).append(", maxRPS ").append(this.maxRequestsPerSecond);
        return buffer.toString();
    }

    public static void main(String[] args) {
        int i;
        int i2;
        RequestAndRateLongCounter counter = new RequestAndRateLongCounter();
        int threadCount = 1000;
        int pollThreadCount = 5;
        int iterations = 200000;
        Thread[] pollThreads = new Thread[pollThreadCount];
        Thread[] threads = new Thread[threadCount];
        for (i2 = 0; i2 < threadCount; ++i2) {
            if (i2 == 0) {
                RequestAndRateLongCounter requestAndRateLongCounter = counter;
                requestAndRateLongCounter.getClass();
                threads[i2] = requestAndRateLongCounter.new TestThread(counter, iterations, true);
                continue;
            }
            RequestAndRateLongCounter requestAndRateLongCounter = counter;
            requestAndRateLongCounter.getClass();
            threads[i2] = requestAndRateLongCounter.new TestThread(counter, iterations, false);
        }
        for (i2 = 0; i2 < pollThreadCount; ++i2) {
            RequestAndRateLongCounter requestAndRateLongCounter = counter;
            requestAndRateLongCounter.getClass();
            pollThreads[i2] = requestAndRateLongCounter.new TestPollThread(counter);
            pollThreads[i2].start();
        }
        long start = System.currentTimeMillis();
        for (i = 0; i < threadCount; ++i) {
            threads[i].start();
        }
        for (i = 0; i < threadCount; ++i) {
            try {
                threads[i].join();
                continue;
            }
            catch (InterruptedException e) {
                // empty catch block
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("total requests " + counter.getTotalRequests());
        System.out.println("rps " + counter.getRequestsPerSecond());
        System.out.println("max rps " + counter.getRequestsPerSecond());
        System.out.println("end - start " + (end - start));
    }

    public class TestPollThread
    extends Thread {
        private RequestAndRateLongCounter counter;

        public TestPollThread(RequestAndRateLongCounter counter) {
            this.counter = counter;
            this.setDaemon(true);
        }

        public void run() {
            while (true) {
                this.counter.getDateOfMaxRequestsPerSecond();
                this.counter.getMaxRequestsPerSecond();
                this.counter.getRequestsPerSecond();
                this.counter.getTotalRequests();
                try {
                    TestPollThread.sleep(1000L);
                }
                catch (InterruptedException interruptedException) {
                }
            }
        }
    }

    public class TestThread
    extends Thread {
        private RequestAndRateLongCounter counter;
        private int iterations;
        private boolean printStats;

        public TestThread(RequestAndRateLongCounter counter, int iterations, boolean printStats) {
            this.counter = counter;
            this.iterations = iterations;
            this.printStats = printStats;
        }

        public void run() {
            for (int i = 0; i < this.iterations; ++i) {
                this.counter.add();
                if (!this.printStats || i % 1000 != 0) continue;
                System.out.println(i);
            }
        }
    }
}

