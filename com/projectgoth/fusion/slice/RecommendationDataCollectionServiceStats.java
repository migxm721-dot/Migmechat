/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.InputStream
 *  Ice.MarshalException
 *  Ice.Object
 *  Ice.ObjectFactory
 *  Ice.OutputStream
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.InputStream;
import Ice.MarshalException;
import Ice.Object;
import Ice.ObjectFactory;
import Ice.OutputStream;
import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.BaseServiceStats;
import com.projectgoth.fusion.slice.IntToServiceStatsLongFieldValueMapHelper;
import com.projectgoth.fusion.slice.ServiceStatsLongFieldValue;
import com.projectgoth.fusion.slice.StringToServiceStatsLongFieldValueMapHelper;
import java.util.Arrays;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RecommendationDataCollectionServiceStats
extends BaseServiceStats {
    private static ObjectFactory _factory = new __F();
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::BaseServiceStats", "::com::projectgoth::fusion::slice::RecommendationDataCollectionServiceStats"};
    public ServiceStatsLongFieldValue totalReceivedDataCount;
    public Map<Integer, ServiceStatsLongFieldValue> totalReceivedDataCountByDataType;
    public ServiceStatsLongFieldValue totalSuccessfullyProcessedDataCount;
    public Map<Integer, ServiceStatsLongFieldValue> totalSuccessfullyProcessedDataCountByDataType;
    public ServiceStatsLongFieldValue totalFailedProcessedDataCount;
    public Map<String, ServiceStatsLongFieldValue> totalFailedProcessedDataCountByErrorCauseCode;

    public RecommendationDataCollectionServiceStats() {
    }

    public RecommendationDataCollectionServiceStats(String hostname, String version, long jvmTotalMemory, long jvmFreeMemory, long uptime, long lastUpdatedTime, boolean izeRequestStatsEnabled, long izeRequestCount, Map<String, Long> izeRequestCountByOrigin, Map<String, Long> izeRequestCountByOperation, Map<String, Double> izeRequestMeanProcessingTimeByOperation, Map<String, Double> izeRequestMaxProcessingTimeByOperation, Map<String, Double> izeRequestStdevProcessingTimeByOperation, Map<String, Double> izeRequest95thPercentileProcessingTimeByOperation, Map<String, Double> izeRequestTotalProcessingTimeByOperation, boolean izeThreadStatsEnabled, long izeObjectAdapterThreadPoolRunning, long izeObjectAdapterThreadPoolInUse, long izeObjectAdapterThreadPoolInUseHighWatermark, double izeObjectAdapterThreadPoolLoad, long izeObjectAdapterThreadPoolSize, long izeObjectAdapterThreadPoolSizeMax, long izeObjectAdapterThreadPoolSizeWarn, boolean izeClientThreadPoolStatsEnabled, long izeClientThreadPoolRunning, long izeClientThreadPoolInUse, long izeClientThreadPoolInUseHighWatermark, double izeClientThreadPoolLoad, long izeClientThreadPoolSize, long izeClientThreadPoolSizeMax, long izeClientThreadPoolSizeWarn, long amdObjectAdapterThreadPoolRunning, long amdObjectAdapterThreadPoolInUse, long amdObjectAdapterThreadPoolInUseHighWatermark, long amdObjectAdapterThreadPoolSize, long amdObjectAdapterThreadPoolSizeMax, long amdObjectAdapterThreadPoolQueueLength, String dataGridStats, ServiceStatsLongFieldValue totalReceivedDataCount, Map<Integer, ServiceStatsLongFieldValue> totalReceivedDataCountByDataType, ServiceStatsLongFieldValue totalSuccessfullyProcessedDataCount, Map<Integer, ServiceStatsLongFieldValue> totalSuccessfullyProcessedDataCountByDataType, ServiceStatsLongFieldValue totalFailedProcessedDataCount, Map<String, ServiceStatsLongFieldValue> totalFailedProcessedDataCountByErrorCauseCode) {
        super(hostname, version, jvmTotalMemory, jvmFreeMemory, uptime, lastUpdatedTime, izeRequestStatsEnabled, izeRequestCount, izeRequestCountByOrigin, izeRequestCountByOperation, izeRequestMeanProcessingTimeByOperation, izeRequestMaxProcessingTimeByOperation, izeRequestStdevProcessingTimeByOperation, izeRequest95thPercentileProcessingTimeByOperation, izeRequestTotalProcessingTimeByOperation, izeThreadStatsEnabled, izeObjectAdapterThreadPoolRunning, izeObjectAdapterThreadPoolInUse, izeObjectAdapterThreadPoolInUseHighWatermark, izeObjectAdapterThreadPoolLoad, izeObjectAdapterThreadPoolSize, izeObjectAdapterThreadPoolSizeMax, izeObjectAdapterThreadPoolSizeWarn, izeClientThreadPoolStatsEnabled, izeClientThreadPoolRunning, izeClientThreadPoolInUse, izeClientThreadPoolInUseHighWatermark, izeClientThreadPoolLoad, izeClientThreadPoolSize, izeClientThreadPoolSizeMax, izeClientThreadPoolSizeWarn, amdObjectAdapterThreadPoolRunning, amdObjectAdapterThreadPoolInUse, amdObjectAdapterThreadPoolInUseHighWatermark, amdObjectAdapterThreadPoolSize, amdObjectAdapterThreadPoolSizeMax, amdObjectAdapterThreadPoolQueueLength, dataGridStats);
        this.totalReceivedDataCount = totalReceivedDataCount;
        this.totalReceivedDataCountByDataType = totalReceivedDataCountByDataType;
        this.totalSuccessfullyProcessedDataCount = totalSuccessfullyProcessedDataCount;
        this.totalSuccessfullyProcessedDataCountByDataType = totalSuccessfullyProcessedDataCountByDataType;
        this.totalFailedProcessedDataCount = totalFailedProcessedDataCount;
        this.totalFailedProcessedDataCountByErrorCauseCode = totalFailedProcessedDataCountByErrorCauseCode;
    }

    public static ObjectFactory ice_factory() {
        return _factory;
    }

    @Override
    public boolean ice_isA(String s) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    @Override
    public boolean ice_isA(String s, Current __current) {
        return Arrays.binarySearch(__ids, s) >= 0;
    }

    @Override
    public String[] ice_ids() {
        return __ids;
    }

    @Override
    public String[] ice_ids(Current __current) {
        return __ids;
    }

    @Override
    public String ice_id() {
        return __ids[2];
    }

    @Override
    public String ice_id(Current __current) {
        return __ids[2];
    }

    public static String ice_staticId() {
        return __ids[2];
    }

    @Override
    public void __write(BasicStream __os) {
        __os.writeTypeId(RecommendationDataCollectionServiceStats.ice_staticId());
        __os.startWriteSlice();
        this.totalReceivedDataCount.__write(__os);
        IntToServiceStatsLongFieldValueMapHelper.write(__os, this.totalReceivedDataCountByDataType);
        this.totalSuccessfullyProcessedDataCount.__write(__os);
        IntToServiceStatsLongFieldValueMapHelper.write(__os, this.totalSuccessfullyProcessedDataCountByDataType);
        this.totalFailedProcessedDataCount.__write(__os);
        StringToServiceStatsLongFieldValueMapHelper.write(__os, this.totalFailedProcessedDataCountByErrorCauseCode);
        __os.endWriteSlice();
        super.__write(__os);
    }

    @Override
    public void __read(BasicStream __is, boolean __rid) {
        if (__rid) {
            __is.readTypeId();
        }
        __is.startReadSlice();
        this.totalReceivedDataCount = new ServiceStatsLongFieldValue();
        this.totalReceivedDataCount.__read(__is);
        this.totalReceivedDataCountByDataType = IntToServiceStatsLongFieldValueMapHelper.read(__is);
        this.totalSuccessfullyProcessedDataCount = new ServiceStatsLongFieldValue();
        this.totalSuccessfullyProcessedDataCount.__read(__is);
        this.totalSuccessfullyProcessedDataCountByDataType = IntToServiceStatsLongFieldValueMapHelper.read(__is);
        this.totalFailedProcessedDataCount = new ServiceStatsLongFieldValue();
        this.totalFailedProcessedDataCount.__read(__is);
        this.totalFailedProcessedDataCountByErrorCauseCode = StringToServiceStatsLongFieldValueMapHelper.read(__is);
        __is.endReadSlice();
        super.__read(__is, true);
    }

    @Override
    public void __write(OutputStream __outS) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::RecommendationDataCollectionServiceStats was not generated with stream support";
        throw ex;
    }

    @Override
    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::RecommendationDataCollectionServiceStats was not generated with stream support";
        throw ex;
    }

    private static class __F
    implements ObjectFactory {
        private __F() {
        }

        public Object create(String type) {
            assert (type.equals(RecommendationDataCollectionServiceStats.ice_staticId()));
            return new RecommendationDataCollectionServiceStats();
        }

        public void destroy() {
        }
    }
}

