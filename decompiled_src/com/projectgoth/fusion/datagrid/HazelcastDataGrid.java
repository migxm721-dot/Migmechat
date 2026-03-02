/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.core.DistributedObject
 *  com.hazelcast.core.EntryEvent
 *  com.hazelcast.core.EntryListener
 *  com.hazelcast.core.Hazelcast
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IExecutorService
 *  com.hazelcast.core.ILock
 *  com.hazelcast.core.IMap
 *  com.hazelcast.core.IQueue
 *  com.hazelcast.core.ITopic
 *  com.hazelcast.core.MultiMap
 *  com.hazelcast.core.TransactionalMap
 *  com.hazelcast.core.TransactionalMultiMap
 *  com.hazelcast.core.TransactionalQueue
 *  com.hazelcast.monitor.LocalExecutorStats
 *  com.hazelcast.monitor.LocalMapStats
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.datagrid;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.monitor.LocalMapStats;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.datagrid.DataGrid;
import com.projectgoth.fusion.objectcache.ChatUserState;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HazelcastDataGrid
extends DataGrid {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(HazelcastDataGrid.class));
    private HazelcastInstance hz;

    public static HazelcastDataGrid getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private HazelcastDataGrid() {
        log.info((Object)"Starting Hazelcast instance...");
        this.hz = Hazelcast.newHazelcastInstance();
        log.info((Object)"Started Hazelcast instance...");
    }

    @Override
    public void prepare() {
        log.info((Object)"Warming up default executor service");
        this.getDefaultExecutorService();
        log.info((Object)"Warmed up default executor service");
    }

    @Override
    public ExecutorService getExecutorService(String name) {
        return this.hz.getExecutorService(name);
    }

    @Override
    public Lock getLock(Object key) {
        return this.hz.getLock(key);
    }

    @Override
    public Map<String, ChatUserState> getUsersMap() {
        return this.hz.getMap("ChatUserState");
    }

    @Override
    public <K, V> void configMap(String name, int ttlSeconds) {
        MapConfig mc = new MapConfig(name);
        mc.setTimeToLiveSeconds(ttlSeconds);
        this.hz.getConfig().addMapConfig(mc);
        IMap map = this.hz.getMap(name);
        map.addEntryListener(new HDCEntryListener(map), true);
    }

    @Override
    public Map<String, Integer> getStringIntMap(String name) {
        return this.hz.getMap(name);
    }

    @Override
    public void destroyLock(Lock lock) {
        ILock ilk = (ILock)lock;
        ilk.destroy();
    }

    @Override
    public void destroyMap(String name) {
        IMap map = this.hz.getMap(name);
        map.destroy();
    }

    @Override
    public String getStats() {
        if (this.hz == null) {
            return "Grid not yet initialized";
        }
        try {
            IExecutorService ies = this.hz.getExecutorService(SystemProperty.get(SystemPropertyEntities.DataGridSettings.DEFAULT_EXECUTOR_SERVICE));
            LocalExecutorStats les = ies.getLocalExecutorStats();
            StringBuilder sb = new StringBuilder();
            sb.append("Default executor service:");
            sb.append(" cancelled=" + les.getCancelledTaskCount());
            sb.append(" completed=" + les.getCompletedTaskCount());
            sb.append(" pending=" + les.getPendingTaskCount());
            sb.append(" started=" + les.getStartedTaskCount());
            sb.append(" total exec latency=" + les.getTotalExecutionLatency());
            sb.append(" total start latency=" + les.getTotalStartLatency());
            sb.append(" creation time=" + new Date(les.getCreationTime()));
            sb.append("\n");
            sb.append(this.getDistributedObjectStats());
            return sb.toString();
        }
        catch (Exception e) {
            log.error((Object)("Unable to get HazelcastDataGridStats: e=" + e), (Throwable)e);
            return "Unable to get HazelcastDataGridStats";
        }
    }

    private String getDistributedObjectStats() {
        try {
            Collection distObjs = this.hz.getDistributedObjects();
            int execServices = 0;
            int locks = 0;
            int maps = 0;
            int multimaps = 0;
            int queues = 0;
            int topics = 0;
            int txnlMaps = 0;
            int txnlMultimaps = 0;
            int txnlQueues = 0;
            long totalBackupEntryCount = 0L;
            long totalBackupEntryMemoryCost = 0L;
            long totalDirtyEntryCount = 0L;
            long totalGetOperationCount = 0L;
            long totalHits = 0L;
            long totalLockedEntryCount = 0L;
            long totalOwnedEntryCount = 0L;
            long totalOwnedEntryMemoryCost = 0L;
            long totalOtherOperationCount = 0L;
            long totalPutOperationCount = 0L;
            long totalRemoveOperationCount = 0L;
            long totalGetLatency = 0L;
            long totalPutLatency = 0L;
            for (DistributedObject distObj : distObjs) {
                if (distObj instanceof IMap) {
                    IMap map = (IMap)distObj;
                    ++maps;
                    LocalMapStats lms = map.getLocalMapStats();
                    totalBackupEntryCount += lms.getBackupEntryCount();
                    totalBackupEntryMemoryCost += lms.getBackupEntryMemoryCost();
                    totalDirtyEntryCount += lms.getDirtyEntryCount();
                    totalGetOperationCount += lms.getGetOperationCount();
                    totalHits += lms.getHits();
                    totalLockedEntryCount += lms.getLockedEntryCount();
                    totalOwnedEntryCount += lms.getOwnedEntryCount();
                    totalOwnedEntryMemoryCost += lms.getOwnedEntryMemoryCost();
                    totalOtherOperationCount += lms.getOtherOperationCount();
                    totalPutOperationCount += lms.getPutOperationCount();
                    totalRemoveOperationCount += lms.getRemoveOperationCount();
                    totalGetLatency += lms.getTotalGetLatency();
                    totalPutLatency += lms.getTotalPutLatency();
                    continue;
                }
                if (distObj instanceof IExecutorService) {
                    ++execServices;
                    continue;
                }
                if (distObj instanceof ILock) {
                    ++locks;
                    continue;
                }
                if (distObj instanceof MultiMap) {
                    ++multimaps;
                    continue;
                }
                if (distObj instanceof IQueue) {
                    ++queues;
                    continue;
                }
                if (distObj instanceof ITopic) {
                    ++topics;
                    continue;
                }
                if (distObj instanceof TransactionalMap) {
                    ++txnlMaps;
                    continue;
                }
                if (distObj instanceof TransactionalMultiMap) {
                    ++txnlMultimaps;
                    continue;
                }
                if (!(distObj instanceof TransactionalQueue)) continue;
                ++txnlQueues;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Maps (local stats):");
            sb.append(" map count=" + maps);
            sb.append(" totalBackupEntryCount=" + totalBackupEntryCount);
            sb.append(" totalBackupEntryMemoryCost=" + totalBackupEntryMemoryCost);
            sb.append(" totalDirtyEntryCount=" + totalDirtyEntryCount);
            sb.append(" totalGetOperationCount=" + totalGetOperationCount);
            sb.append(" totalHits=" + totalHits);
            sb.append(" totalLockedEntryCount=" + totalLockedEntryCount);
            sb.append(" totalOwnedEntryCount=" + totalOwnedEntryCount);
            sb.append(" totalOwnedEntryMemoryCost=" + totalOwnedEntryMemoryCost);
            sb.append(" totalOtherOperationCount=" + totalOtherOperationCount);
            sb.append(" totalPutOperationCount=" + totalPutOperationCount);
            sb.append(" totalRemoveOperationCount=" + totalRemoveOperationCount);
            double meanGetLatency = (double)totalGetLatency / (double)totalGetOperationCount;
            double meanPutLatency = (double)totalPutLatency / (double)totalPutOperationCount;
            sb.append(" meanGetLatency=" + meanGetLatency);
            sb.append(" meanPutLatency=" + meanPutLatency);
            sb.append("\n Executor services=" + execServices + " Locks=" + locks + " Multimaps=" + multimaps + " Queues=" + queues + " Topics=" + topics + " Txnl maps=" + txnlMaps + " Txnl multimaps=" + txnlMultimaps + " Txnl queues=" + txnlQueues);
            return sb.toString();
        }
        catch (Exception e) {
            return "Unable to get distributed object stats";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class HDCEntryListener<K, V>
    implements EntryListener<K, V> {
        private final IMap map;

        public HDCEntryListener(IMap map) {
            this.map = map;
        }

        public void entryAdded(EntryEvent<K, V> event) {
        }

        public void entryRemoved(EntryEvent<K, V> event) {
        }

        public void entryUpdated(EntryEvent<K, V> event) {
        }

        public void entryEvicted(EntryEvent<K, V> event) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Evicting entry: " + event + " from map: " + this.map.getName()));
            }
            try {
                if (this.map.size() == 0) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Destroying map as last entry evicted: " + this.map.getName()));
                    }
                    this.map.destroy();
                }
            }
            catch (Exception e) {
                log.error((Object)("Exception destroying map with name=" + this.map.getName() + " on eviction: e=" + e));
            }
        }
    }

    private static class SingletonHolder {
        public static final HazelcastDataGrid INSTANCE = new HazelcastDataGrid();

        private SingletonHolder() {
        }
    }
}

