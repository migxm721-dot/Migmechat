/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectAdapter
 *  Ice.ObjectAdapterI
 *  IceInternal.ThreadPool
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ice;

import Ice.ObjectAdapter;
import Ice.ObjectAdapterI;
import IceInternal.ThreadPool;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IceThreadMonitor
extends TimerTask {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(IceThreadMonitor.class));
    private Timer timer;
    private ThreadPool threadPool;
    private List<Thread> threadList;
    private long nextLogTime = 0L;
    private static final int THREAD_DEPTH_TO_SKIP = 0;
    private static final int THREAD_DEPTH_TO_TRACE = 25;
    private final SystemPropertyEntities.IceThreadMonitorSettings threadCountProperty;
    private final SystemPropertyEntities.IceThreadMonitorSettings interDumpDurationProperty;

    public IceThreadMonitor(ObjectAdapter adapter, SystemPropertyEntities.IceThreadMonitorSettings threadCountProperty, SystemPropertyEntities.IceThreadMonitorSettings interDumpDurationProperty) {
        this.threadCountProperty = threadCountProperty;
        this.interDumpDurationProperty = interDumpDurationProperty;
        try {
            this.timer = new Timer();
            ObjectAdapterI oai = (ObjectAdapterI)adapter;
            this.threadPool = oai.getThreadPool();
            Field threadListField = ThreadPool.class.getDeclaredField("_threads");
            threadListField.setAccessible(true);
            this.threadList = (List)threadListField.get(this.threadPool);
            log.info((Object)"Tracing Ice Threads started");
        }
        catch (Exception e) {
            log.debug((Object)("Exception initializating IceThreadMonitor: " + e + " | " + e.getMessage()));
        }
    }

    private int getInt(String fieldName) {
        try {
            Field field = ThreadPool.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(this.threadPool);
        }
        catch (Exception e) {
            log.debug((Object)("Exception on getInt for " + fieldName + ": " + e + " | " + e.getMessage()));
            return 0;
        }
    }

    public static String dumpThreadPoolString(List<Thread> threadList) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dumping Ice Thread Pool: ");
        sb.append(threadList.size());
        sb.append(" threads\n");
        for (Thread t : threadList) {
            sb.append(String.format("Thread %s\n", t));
            StackTraceElement[] stackTrace = t.getStackTrace();
            int start = 0;
            int end = 25;
            if (end > stackTrace.length) {
                end = stackTrace.length;
            }
            for (int i = start; i < end; ++i) {
                sb.append("  ");
                sb.append(stackTrace[i].toString());
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        int maxThreads = SystemProperty.getInt(this.threadCountProperty);
        if (maxThreads < 0) {
            return;
        }
        if (System.currentTimeMillis() < this.nextLogTime) {
            return;
        }
        ArrayList<Thread> copyOfThreads = null;
        ThreadPool threadPool = this.threadPool;
        synchronized (threadPool) {
            int inUse = this.getInt("_inUse");
            if (inUse >= maxThreads) {
                copyOfThreads = new ArrayList<Thread>(this.threadList);
            }
        }
        if (copyOfThreads != null) {
            String logString = IceThreadMonitor.dumpThreadPoolString(copyOfThreads);
            log.info((Object)logString);
            this.nextLogTime = System.currentTimeMillis() + (long)(SystemProperty.getInt(this.interDumpDurationProperty) * 1000);
        }
    }

    public void start(int checkingIntervalInSeconds) {
        this.timer.schedule((TimerTask)this, 0L, (long)(checkingIntervalInSeconds * 1000));
    }
}

