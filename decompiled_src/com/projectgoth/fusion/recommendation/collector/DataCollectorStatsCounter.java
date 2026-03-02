/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector;

import com.projectgoth.fusion.common.AppStartupInfo;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.recommendation.collector.DataCollectorUtils;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithErrorCauseCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServiceStats;
import com.projectgoth.fusion.slice.ServiceStatsLongFieldValue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DataCollectorStatsCounter {
    private final StatsLongFieldValue totalReceived = new StatsLongFieldValue();
    private final StatsLongFieldValue totalSuccessfulProcess = new StatsLongFieldValue();
    private final StatsLongFieldValue totalFailedProcess = new StatsLongFieldValue();
    private final ConcurrentHashMap<Integer, StatsLongFieldValue> totalReceivedByDataTypeMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<Integer, StatsLongFieldValue> totalSuccessfulProcessByDataTypeMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, StatsLongFieldValue> totalFailedProcessByErrorCauseCodeMap = new ConcurrentHashMap();
    private final AppStartupInfo appStartupInfo;

    public DataCollectorStatsCounter(AppStartupInfo appStartupInfo) {
        this.appStartupInfo = appStartupInfo;
    }

    private static <K, V> V associate(ConcurrentHashMap<K, V> m, K k, V initialValue) {
        V previousValue = m.putIfAbsent(k, initialValue);
        return previousValue == null ? initialValue : previousValue;
    }

    public void updateOnDataReceived(CollectedDataIce data, long currentTimestamp) {
        Integer dataType = DataCollectorUtils.getDataType(data);
        this.totalReceived.increment(currentTimestamp);
        StatsLongFieldValue totalReceivedByDataType = DataCollectorStatsCounter.associate(this.totalReceivedByDataTypeMap, dataType, new StatsLongFieldValue());
        totalReceivedByDataType.increment(currentTimestamp);
    }

    public void updateOnDataProcessed(CollectedDataIce data, FusionExceptionWithErrorCauseCode ex, long currentTimestamp) {
        Integer dataType = DataCollectorUtils.getDataType(data);
        if (ex == null) {
            this.totalSuccessfulProcess.increment(currentTimestamp);
            StatsLongFieldValue totalSuccessfulProcessByDataType = DataCollectorStatsCounter.associate(this.totalSuccessfulProcessByDataTypeMap, dataType, new StatsLongFieldValue());
            totalSuccessfulProcessByDataType.increment(currentTimestamp);
        } else {
            this.totalFailedProcess.increment(currentTimestamp);
            StatsLongFieldValue totalFailedCountByErrorCauseCode = DataCollectorStatsCounter.associate(this.totalFailedProcessByErrorCauseCodeMap, StringUtil.isBlank(ex.errorCauseCode) ? "" : ex.errorCauseCode, new StatsLongFieldValue());
            totalFailedCountByErrorCauseCode.increment(currentTimestamp);
        }
    }

    public RecommendationDataCollectionServiceStats populate(RecommendationDataCollectionServiceStats rdcsStats) {
        ServiceStatsFactory.initializeServiceStats(rdcsStats, this.appStartupInfo.getStartTime());
        rdcsStats.totalSuccessfullyProcessedDataCount = this.totalSuccessfulProcess.toServiceStatsLongFieldValue();
        rdcsStats.totalFailedProcessedDataCount = this.totalFailedProcess.toServiceStatsLongFieldValue();
        rdcsStats.totalReceivedDataCount = this.totalReceived.toServiceStatsLongFieldValue();
        rdcsStats.totalReceivedDataCountByDataType = new HashMap<Integer, ServiceStatsLongFieldValue>();
        for (Map.Entry<Integer, StatsLongFieldValue> entry : this.totalReceivedByDataTypeMap.entrySet()) {
            rdcsStats.totalReceivedDataCountByDataType.put(entry.getKey(), entry.getValue().toServiceStatsLongFieldValue());
        }
        rdcsStats.totalSuccessfullyProcessedDataCountByDataType = new HashMap<Integer, ServiceStatsLongFieldValue>();
        for (Map.Entry<Integer, StatsLongFieldValue> entry : this.totalSuccessfulProcessByDataTypeMap.entrySet()) {
            rdcsStats.totalSuccessfullyProcessedDataCountByDataType.put(entry.getKey(), entry.getValue().toServiceStatsLongFieldValue());
        }
        rdcsStats.totalFailedProcessedDataCountByErrorCauseCode = new HashMap<String, ServiceStatsLongFieldValue>();
        for (Map.Entry<Object, StatsLongFieldValue> entry : this.totalFailedProcessByErrorCauseCodeMap.entrySet()) {
            rdcsStats.totalFailedProcessedDataCountByErrorCauseCode.put((String)entry.getKey(), entry.getValue().toServiceStatsLongFieldValue());
        }
        return rdcsStats;
    }

    public void clear() {
        this.totalFailedProcess.clear();
        this.totalFailedProcessByErrorCauseCodeMap.clear();
        this.totalReceived.clear();
        this.totalReceivedByDataTypeMap.clear();
        this.totalSuccessfulProcess.clear();
        this.totalSuccessfulProcessByDataTypeMap.clear();
    }

    private static final class StatsLongFieldValue {
        private static final long TIME_NOT_AVAILABLE = -1L;
        public final AtomicLong count = new AtomicLong(0L);
        public final AtomicLong lastUpdatedTime = new AtomicLong(-1L);

        private StatsLongFieldValue() {
        }

        public ServiceStatsLongFieldValue toServiceStatsLongFieldValue() {
            return new ServiceStatsLongFieldValue(this.count.longValue(), this.lastUpdatedTime.longValue());
        }

        public void increment(long currentTimestamp) {
            this.count.incrementAndGet();
            this.lastUpdatedTime.set(currentTimestamp);
        }

        public void clear() {
            this.count.set(0L);
            this.lastUpdatedTime.set(-1L);
        }
    }
}

