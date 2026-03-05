/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectAdapter
 *  Ice.ObjectAdapterI
 *  Ice.Request
 *  Ice.Util
 *  IceInternal.Instance
 *  IceInternal.ThreadPool
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.stats;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.ObjectAdapterI;
import Ice.Request;
import Ice.Util;
import IceInternal.Instance;
import IceInternal.ThreadPool;
import com.projectgoth.fusion.chatsync.WallclockTime;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ConfigurableExecutor;
import com.projectgoth.fusion.common.LazyStats;
import com.projectgoth.fusion.slice.BaseServiceStats;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IceStats
extends LazyStats {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(IceStats.class));
    private AtomicReference<ObjectAdapter> iceObjectAdapter = new AtomicReference();
    private AtomicReference<Communicator> iceCommunicator = new AtomicReference();
    private AtomicReference<ConfigurableExecutor> iceAmdExecutor = new AtomicReference();
    private volatile long requestCount = 0L;
    private ConcurrentHashMap<String, AtomicLong> requestCountByOrigin = new ConcurrentHashMap();
    private ConcurrentHashMap<String, AtomicLong> requestCountByOperation = new ConcurrentHashMap();
    private ConcurrentHashMap<String, WallclockTime> requestProcessingWallclockTimeByOp = new ConcurrentHashMap();
    private static final long HIGH_WATERMARK_RESET_INTERVAL_MILLIS = 900000L;
    private ResettingHighWatermark izeObjectAdapterThreadPoolInUseHighWatermark = new ResettingHighWatermark();
    private ResettingHighWatermark izeClientThreadPoolInUseHighWatermark = new ResettingHighWatermark();
    private ResettingHighWatermark amdObjectAdapterThreadPoolInUseHighWatermark = new ResettingHighWatermark();

    @Override
    protected boolean isStatsEnabled() {
        return false;
    }

    @Override
    protected int getStatsIntervalMinutes() {
        return 0;
    }

    @Override
    protected void doLog() {
    }

    private IceStats() {
    }

    public static IceStats getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setIceObjects(Communicator communicator, ObjectAdapter adapter, ConfigurableExecutor exec) {
        this.iceObjectAdapter.compareAndSet(null, adapter);
        this.iceCommunicator.compareAndSet(null, communicator);
        this.iceAmdExecutor.compareAndSet(null, exec);
    }

    public void addRequest(Request request) {
        String origin;
        if (request.isCollocated()) {
            origin = "COLLOCATED";
        } else {
            String cxn = request.getCurrent().con.toString();
            String pattern = "remote address = ";
            origin = cxn.substring(cxn.indexOf("remote address = ") + "remote address = ".length());
            origin = origin.substring(0, origin.indexOf(58));
        }
        ++this.requestCount;
        this.incrementRequestCountByOrigin(origin);
        this.incrementRequestCountByOperation(request.getCurrent().operation);
    }

    public void onRequestDispatched(Request request, long startTimeMillis) {
        String op = request.getCurrent().operation;
        if (this.requestProcessingWallclockTimeByOp.get(op) == null) {
            this.requestProcessingWallclockTimeByOp.putIfAbsent(op, new WallclockTime());
        }
        this.requestProcessingWallclockTimeByOp.get(op).addRequest(System.currentTimeMillis() - startTimeMillis);
    }

    private void incrementRequestCountByOrigin(String origin) {
        if (this.requestCountByOrigin.get(origin) == null) {
            this.requestCountByOrigin.putIfAbsent(origin, new AtomicLong());
        }
        AtomicLong count = this.requestCountByOrigin.get(origin);
        count.incrementAndGet();
    }

    private void incrementRequestCountByOperation(String op) {
        if (this.requestCountByOperation.get(op) == null) {
            this.requestCountByOperation.putIfAbsent(op, new AtomicLong());
        }
        AtomicLong count = this.requestCountByOperation.get(op);
        count.incrementAndGet();
    }

    public Map<String, Long> getRequestCountByOrigin() {
        return this.cloneMap(this.requestCountByOrigin);
    }

    public Map<String, Long> getRequestCountByOperation() {
        return this.cloneMap(this.requestCountByOperation);
    }

    private Map<String, Long> cloneMap(ConcurrentHashMap<String, AtomicLong> source) {
        HashMap<String, Long> clone = new HashMap<String, Long>();
        Set keys = source.keySet();
        for (String key : keys) {
            clone.put(key, source.get(key).longValue());
        }
        return clone;
    }

    private Map<String, Double> cloneMeans(ConcurrentHashMap<String, WallclockTime> source) {
        HashMap<String, Double> clone = new HashMap<String, Double>();
        Set keys = source.keySet();
        for (String key : keys) {
            clone.put(key, source.get(key).getAverageWallclockTime());
        }
        return clone;
    }

    private Map<String, Double> cloneMaxes(ConcurrentHashMap<String, WallclockTime> source) {
        HashMap<String, Double> clone = new HashMap<String, Double>();
        Set keys = source.keySet();
        for (String key : keys) {
            clone.put(key, source.get(key).getMaxWallclockTime());
        }
        return clone;
    }

    private Map<String, Double> cloneStdevs(ConcurrentHashMap<String, WallclockTime> source) {
        HashMap<String, Double> clone = new HashMap<String, Double>();
        Set keys = source.keySet();
        for (String key : keys) {
            clone.put(key, source.get(key).getRunningStdev());
        }
        return clone;
    }

    private Map<String, Double> clone95thPercentiles(ConcurrentHashMap<String, WallclockTime> source) {
        HashMap<String, Double> clone = new HashMap<String, Double>();
        Set keys = source.keySet();
        for (String key : keys) {
            clone.put(key, source.get(key).getStats().getPercentile95th());
        }
        return clone;
    }

    private Map<String, Double> cloneTotals(ConcurrentHashMap<String, WallclockTime> source) {
        HashMap<String, Double> clone = new HashMap<String, Double>();
        Set keys = source.keySet();
        for (String key : keys) {
            clone.put(key, source.get(key).getTotalWallclockTime());
        }
        return clone;
    }

    public <TStats extends BaseServiceStats> void initializeRequestStats(TStats stats) {
        stats.izeRequestCount = this.requestCount;
        stats.izeRequestCountByOrigin = this.getRequestCountByOrigin();
        stats.izeRequestCountByOperation = this.getRequestCountByOperation();
        stats.izeRequestMeanProcessingTimeByOperation = this.cloneMeans(this.requestProcessingWallclockTimeByOp);
        stats.izeRequestMaxProcessingTimeByOperation = this.cloneMaxes(this.requestProcessingWallclockTimeByOp);
        stats.izeRequestStdevProcessingTimeByOperation = this.cloneStdevs(this.requestProcessingWallclockTimeByOp);
        stats.izeRequest95thPercentileProcessingTimeByOperation = this.clone95thPercentiles(this.requestProcessingWallclockTimeByOp);
        stats.izeRequestTotalProcessingTimeByOperation = this.cloneTotals(this.requestProcessingWallclockTimeByOp);
    }

    public <TStats extends BaseServiceStats> void initializeThreadStats(TStats stats) {
        ThreadPoolPrivateFields fields;
        if (this.iceObjectAdapter.get() != null) {
            try {
                ObjectAdapterI oai = (ObjectAdapterI)this.iceObjectAdapter.get();
                ThreadPool pool = oai.getThreadPool();
                fields = new ThreadPoolPrivateFields(pool);
                fields.setObjectAdapterThreadStats(stats);
            }
            catch (Exception e) {
                log.error((Object)("Unable to initialize ice objectadapter threadpool stats: " + e), (Throwable)e);
            }
        }
        if (this.iceCommunicator.get() != null) {
            try {
                Instance instance = Util.getInstance((Communicator)this.iceCommunicator.get());
                ThreadPool iceClientPool = instance.clientThreadPool();
                fields = new ThreadPoolPrivateFields(iceClientPool);
                fields.setClientThreadPoolStats(stats);
            }
            catch (Exception e) {
                log.error((Object)("Unable to initialize ice client threadpool stats: " + e), (Throwable)e);
            }
        }
        if (this.iceAmdExecutor.get() != null) {
            stats.amdObjectAdapterThreadPoolRunning = this.iceAmdExecutor.get().getPoolSize();
            int inUse = this.iceAmdExecutor.get().getActiveCount();
            stats.amdObjectAdapterThreadPoolInUse = inUse;
            this.amdObjectAdapterThreadPoolInUseHighWatermark.casIfHigher(inUse);
            stats.amdObjectAdapterThreadPoolInUseHighWatermark = this.amdObjectAdapterThreadPoolInUseHighWatermark.get();
            stats.amdObjectAdapterThreadPoolSize = this.iceAmdExecutor.get().getCorePoolSize();
            stats.amdObjectAdapterThreadPoolSizeMax = this.iceAmdExecutor.get().getMaximumPoolSize();
            stats.amdObjectAdapterThreadPoolQueueLength = this.iceAmdExecutor.get().getQueueLength();
        }
    }

    private static class SingletonHolder {
        public static final IceStats INSTANCE = new IceStats();

        private SingletonHolder() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class ThreadPoolPrivateFields {
        private ThreadPool pool;
        private int running;
        private int inUse;
        private double load;
        private int size;
        private int sizeMax;
        private int sizeWarn;

        public ThreadPoolPrivateFields(ThreadPool pool) throws Exception {
            this.pool = pool;
            this.running = this.getInt("_running");
            this.inUse = this.getInt("_inUse");
            this.load = this.getDouble("_load");
            this.size = this.getInt("_size");
            this.sizeMax = this.getInt("_sizeMax");
            this.sizeWarn = this.getInt("_sizeWarn");
        }

        private int getInt(String name) throws Exception {
            Field f = ThreadPool.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.getInt(this.pool);
        }

        private double getDouble(String name) throws Exception {
            Field f = ThreadPool.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.getDouble(this.pool);
        }

        public <TStats extends BaseServiceStats> void setObjectAdapterThreadStats(TStats stats) throws Exception {
            stats.izeThreadStatsEnabled = true;
            stats.izeObjectAdapterThreadPoolRunning = this.running;
            stats.izeObjectAdapterThreadPoolInUse = this.inUse;
            stats.izeObjectAdapterThreadPoolLoad = this.load;
            stats.izeObjectAdapterThreadPoolSize = this.size;
            stats.izeObjectAdapterThreadPoolSizeMax = this.sizeMax;
            stats.izeObjectAdapterThreadPoolSizeWarn = this.sizeWarn;
            IceStats.this.izeObjectAdapterThreadPoolInUseHighWatermark.casIfHigher(this.inUse);
            stats.izeObjectAdapterThreadPoolInUseHighWatermark = IceStats.this.izeObjectAdapterThreadPoolInUseHighWatermark.get();
        }

        public <TStats extends BaseServiceStats> void setClientThreadPoolStats(TStats stats) throws Exception {
            stats.izeClientThreadPoolStatsEnabled = true;
            stats.izeClientThreadPoolRunning = this.running;
            stats.izeClientThreadPoolInUse = this.inUse;
            stats.izeClientThreadPoolLoad = this.load;
            stats.izeClientThreadPoolSize = this.size;
            stats.izeClientThreadPoolSizeMax = this.sizeMax;
            stats.izeClientThreadPoolSizeWarn = this.sizeWarn;
            IceStats.this.izeClientThreadPoolInUseHighWatermark.casIfHigher(this.inUse);
            stats.izeClientThreadPoolInUseHighWatermark = IceStats.this.izeClientThreadPoolInUseHighWatermark.get();
        }
    }

    private class ResettingHighWatermark {
        private AtomicInteger highWatermark = new AtomicInteger(0);
        private AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());

        private ResettingHighWatermark() {
        }

        public int get() {
            return this.highWatermark.get();
        }

        private void casIfHigher(int newValue) {
            int current = this.highWatermark.get();
            long lastResetLocal = this.lastResetTime.get();
            boolean reset = false;
            long now = System.currentTimeMillis();
            if (now - lastResetLocal > 900000L) {
                reset = this.lastResetTime.compareAndSet(lastResetLocal, now);
            }
            if (newValue > current || reset) {
                this.highWatermark.compareAndSet(current, newValue);
            }
        }
    }
}

