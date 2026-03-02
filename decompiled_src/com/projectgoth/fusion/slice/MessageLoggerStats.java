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
import java.util.Arrays;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MessageLoggerStats
extends BaseServiceStats {
    private static ObjectFactory _factory = new __F();
    public static final String[] __ids = new String[]{"::Ice::Object", "::com::projectgoth::fusion::slice::BaseServiceStats", "::com::projectgoth::fusion::slice::MessageLoggerStats"};
    public float numMessagesReceivedPerSecond;
    public float maxMessagesReceivedPerSecond;
    public float numMessagesLoggedPerSecond;
    public float maxMessagesLoggedPerSecond;
    public int numMessagesQueued;
    public int maxMessagesQueued;

    public MessageLoggerStats() {
    }

    public MessageLoggerStats(String hostname, String version, long jvmTotalMemory, long jvmFreeMemory, long uptime, long lastUpdatedTime, boolean izeRequestStatsEnabled, long izeRequestCount, Map<String, Long> izeRequestCountByOrigin, Map<String, Long> izeRequestCountByOperation, Map<String, Double> izeRequestMeanProcessingTimeByOperation, Map<String, Double> izeRequestMaxProcessingTimeByOperation, Map<String, Double> izeRequestStdevProcessingTimeByOperation, Map<String, Double> izeRequest95thPercentileProcessingTimeByOperation, Map<String, Double> izeRequestTotalProcessingTimeByOperation, boolean izeThreadStatsEnabled, long izeObjectAdapterThreadPoolRunning, long izeObjectAdapterThreadPoolInUse, long izeObjectAdapterThreadPoolInUseHighWatermark, double izeObjectAdapterThreadPoolLoad, long izeObjectAdapterThreadPoolSize, long izeObjectAdapterThreadPoolSizeMax, long izeObjectAdapterThreadPoolSizeWarn, boolean izeClientThreadPoolStatsEnabled, long izeClientThreadPoolRunning, long izeClientThreadPoolInUse, long izeClientThreadPoolInUseHighWatermark, double izeClientThreadPoolLoad, long izeClientThreadPoolSize, long izeClientThreadPoolSizeMax, long izeClientThreadPoolSizeWarn, long amdObjectAdapterThreadPoolRunning, long amdObjectAdapterThreadPoolInUse, long amdObjectAdapterThreadPoolInUseHighWatermark, long amdObjectAdapterThreadPoolSize, long amdObjectAdapterThreadPoolSizeMax, long amdObjectAdapterThreadPoolQueueLength, String dataGridStats, float numMessagesReceivedPerSecond, float maxMessagesReceivedPerSecond, float numMessagesLoggedPerSecond, float maxMessagesLoggedPerSecond, int numMessagesQueued, int maxMessagesQueued) {
        super(hostname, version, jvmTotalMemory, jvmFreeMemory, uptime, lastUpdatedTime, izeRequestStatsEnabled, izeRequestCount, izeRequestCountByOrigin, izeRequestCountByOperation, izeRequestMeanProcessingTimeByOperation, izeRequestMaxProcessingTimeByOperation, izeRequestStdevProcessingTimeByOperation, izeRequest95thPercentileProcessingTimeByOperation, izeRequestTotalProcessingTimeByOperation, izeThreadStatsEnabled, izeObjectAdapterThreadPoolRunning, izeObjectAdapterThreadPoolInUse, izeObjectAdapterThreadPoolInUseHighWatermark, izeObjectAdapterThreadPoolLoad, izeObjectAdapterThreadPoolSize, izeObjectAdapterThreadPoolSizeMax, izeObjectAdapterThreadPoolSizeWarn, izeClientThreadPoolStatsEnabled, izeClientThreadPoolRunning, izeClientThreadPoolInUse, izeClientThreadPoolInUseHighWatermark, izeClientThreadPoolLoad, izeClientThreadPoolSize, izeClientThreadPoolSizeMax, izeClientThreadPoolSizeWarn, amdObjectAdapterThreadPoolRunning, amdObjectAdapterThreadPoolInUse, amdObjectAdapterThreadPoolInUseHighWatermark, amdObjectAdapterThreadPoolSize, amdObjectAdapterThreadPoolSizeMax, amdObjectAdapterThreadPoolQueueLength, dataGridStats);
        this.numMessagesReceivedPerSecond = numMessagesReceivedPerSecond;
        this.maxMessagesReceivedPerSecond = maxMessagesReceivedPerSecond;
        this.numMessagesLoggedPerSecond = numMessagesLoggedPerSecond;
        this.maxMessagesLoggedPerSecond = maxMessagesLoggedPerSecond;
        this.numMessagesQueued = numMessagesQueued;
        this.maxMessagesQueued = maxMessagesQueued;
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
        __os.writeTypeId(MessageLoggerStats.ice_staticId());
        __os.startWriteSlice();
        __os.writeFloat(this.numMessagesReceivedPerSecond);
        __os.writeFloat(this.maxMessagesReceivedPerSecond);
        __os.writeFloat(this.numMessagesLoggedPerSecond);
        __os.writeFloat(this.maxMessagesLoggedPerSecond);
        __os.writeInt(this.numMessagesQueued);
        __os.writeInt(this.maxMessagesQueued);
        __os.endWriteSlice();
        super.__write(__os);
    }

    @Override
    public void __read(BasicStream __is, boolean __rid) {
        if (__rid) {
            __is.readTypeId();
        }
        __is.startReadSlice();
        this.numMessagesReceivedPerSecond = __is.readFloat();
        this.maxMessagesReceivedPerSecond = __is.readFloat();
        this.numMessagesLoggedPerSecond = __is.readFloat();
        this.maxMessagesLoggedPerSecond = __is.readFloat();
        this.numMessagesQueued = __is.readInt();
        this.maxMessagesQueued = __is.readInt();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    @Override
    public void __write(OutputStream __outS) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::MessageLoggerStats was not generated with stream support";
        throw ex;
    }

    @Override
    public void __read(InputStream __inS, boolean __rid) {
        MarshalException ex = new MarshalException();
        ex.reason = "type com::projectgoth::fusion::slice::MessageLoggerStats was not generated with stream support";
        throw ex;
    }

    private static class __F
    implements ObjectFactory {
        private __F() {
        }

        public Object create(String type) {
            assert (type.equals(MessageLoggerStats.ice_staticId()));
            return new MessageLoggerStats();
        }

        public void destroy() {
        }
    }
}

