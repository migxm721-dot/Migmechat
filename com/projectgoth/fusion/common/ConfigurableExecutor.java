/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LogFilter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class ConfigurableExecutor {
    private static final LogFilter log = new LogFilter(Logger.getLogger((String)ConfigUtils.getLoggerName(ConfigurableExecutor.class)), SystemPropertyEntities.ChatSyncSettings.LOG_EXCLUSION_FILTER);
    protected ThreadPoolExecutor executor;
    protected SystemPropertyEntities.SystemPropertyEntryInterface tpoolCoreSize;
    protected SystemPropertyEntities.SystemPropertyEntryInterface tpoolMaxSize;
    protected SystemPropertyEntities.SystemPropertyEntryInterface tpoolKeepAliveSeconds;
    protected SystemPropertyEntities.SystemPropertyEntryInterface refreshTpoolPropsEnabled;
    protected SystemPropertyEntities.SystemPropertyEntryInterface tpoolPropsRefreshIntervalSecs;

    public ConfigurableExecutor(SystemPropertyEntities.SystemPropertyEntryInterface tpoolCoreSize, SystemPropertyEntities.SystemPropertyEntryInterface tpoolMaxSize, SystemPropertyEntities.SystemPropertyEntryInterface tpoolKeepAliveSeconds, SystemPropertyEntities.SystemPropertyEntryInterface refreshTpoolPropsEnabled, SystemPropertyEntities.SystemPropertyEntryInterface tpoolPropsRefreshIntervalSecs) {
        this.tpoolCoreSize = tpoolCoreSize;
        this.tpoolMaxSize = tpoolMaxSize;
        this.tpoolKeepAliveSeconds = tpoolKeepAliveSeconds;
        this.refreshTpoolPropsEnabled = refreshTpoolPropsEnabled;
        this.tpoolPropsRefreshIntervalSecs = tpoolPropsRefreshIntervalSecs;
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        this.executor = new ThreadPoolExecutor(SystemProperty.getInt(tpoolCoreSize), SystemProperty.getInt(tpoolMaxSize), SystemProperty.getInt(tpoolKeepAliveSeconds), TimeUnit.SECONDS, queue);
        if (SystemProperty.getBool(refreshTpoolPropsEnabled)) {
            new ThreadPoolPropsRefreshThread().start();
        }
    }

    private void refreshThreadPoolExecutorProps() {
        int coreSizeProp = SystemProperty.getInt(this.tpoolCoreSize);
        int maxSizeProp = SystemProperty.getInt(this.tpoolMaxSize);
        int keepAliveSecondsProp = SystemProperty.getInt(this.tpoolKeepAliveSeconds);
        if (this.executor.getCorePoolSize() != coreSizeProp) {
            log.info("Thread pool core size param changed: updating to=" + coreSizeProp);
            this.executor.setCorePoolSize(coreSizeProp);
        }
        if (this.executor.getMaximumPoolSize() != maxSizeProp) {
            log.info("Thread pool max size param changed: updating to=" + maxSizeProp);
            this.executor.setMaximumPoolSize(maxSizeProp);
        }
        if (this.executor.getKeepAliveTime(TimeUnit.SECONDS) != (long)keepAliveSecondsProp) {
            log.info("Thread pool keepalive time param changed: updating to=" + keepAliveSecondsProp);
            this.executor.setKeepAliveTime(keepAliveSecondsProp, TimeUnit.SECONDS);
        }
    }

    public void execute(Runnable command) {
        this.executor.execute(command);
    }

    public ThreadPoolExecutor getExecutor() {
        return this.executor;
    }

    public int getPoolSize() {
        return this.executor.getPoolSize();
    }

    public int getActiveCount() {
        return this.executor.getActiveCount();
    }

    public int getCorePoolSize() {
        return this.executor.getCorePoolSize();
    }

    public int getMaximumPoolSize() {
        return this.executor.getMaximumPoolSize();
    }

    public int getQueueLength() {
        return this.executor.getQueue().size();
    }

    private class ThreadPoolPropsRefreshThread
    extends Thread {
        private ThreadPoolPropsRefreshThread() {
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ConfigurableExecutor.this.refreshThreadPoolExecutorProps();
                try {
                    Thread.sleep(1000 * SystemProperty.getInt(ConfigurableExecutor.this.tpoolPropsRefreshIntervalSecs));
                }
                catch (InterruptedException e) {
                    log.info("ThreadPoolPropsRefreshThread interrupted and shutting down");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            log.info("ThreadPoolPropsRefreshThread exiting");
        }
    }
}

