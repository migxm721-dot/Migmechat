/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.recommendation.collector;

import com.projectgoth.fusion.common.AppStartupInfo;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.recommendation.collector.DataCollectorInvocationCtx;
import com.projectgoth.fusion.recommendation.collector.DataCollectorStatsCounter;
import com.projectgoth.fusion.recommendation.collector.DataCollectorUtils;
import com.projectgoth.fusion.recommendation.collector.DataCollectorWriterStrategy;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithErrorCauseCode;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DataCollectorContext {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DataCollectorContext.class));
    private final Map<Integer, List<DataCollectorWriterStrategy<?>>> dataTypeToStrategyImplementationsMap = new HashMap();
    private final DataCollectorStatsCounter statsCounter;

    public DataCollectorContext(AppStartupInfo appStartupInfo) {
        this.statsCounter = new DataCollectorStatsCounter(appStartupInfo);
    }

    public DataCollectorStatsCounter getStatsCounter() {
        return this.statsCounter;
    }

    public DataCollectorContext addStrategy(int dataType, DataCollectorWriterStrategy<?> strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy is null");
        }
        List<DataCollectorWriterStrategy<?>> strategies = this.dataTypeToStrategyImplementationsMap.get(dataType);
        if (strategies == null) {
            strategies = new ArrayList();
            this.dataTypeToStrategyImplementationsMap.put(dataType, strategies);
        }
        strategies.add(strategy);
        return this;
    }

    public void write(CollectedDataIce dataIce, DataCollectorInvocationCtx invokeCtx, boolean throwWhenErrorDataListIsNotEmpty) throws FusionExceptionWithRefCode {
        try {
            this.statsCounter.updateOnDataReceived(dataIce, System.currentTimeMillis());
            try {
                List<DataCollectorInvocationCtx.ErrorData> errorDataList;
                this.process(dataIce, invokeCtx);
                this.statsCounter.updateOnDataProcessed(dataIce, null, System.currentTimeMillis());
                if (throwWhenErrorDataListIsNotEmpty && !(errorDataList = invokeCtx.getErrorDataList()).isEmpty()) {
                    throw DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, "Error while writing collected data", errorDataList.toString(), ErrorCause.DataCollectorErrorReasonType.LOGGING_FAILED);
                }
            }
            catch (FusionExceptionWithErrorCauseCode ex) {
                this.statsCounter.updateOnDataProcessed(dataIce, ex, System.currentTimeMillis());
                throw ex;
            }
        }
        catch (FusionExceptionWithRefCode ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, "Unhandled exception", ErrorCause.DataCollectorErrorReasonType.UNHANDLED_EXCEPTION, ex);
        }
    }

    public void write(CollectedDataIce dataIce) throws FusionExceptionWithRefCode {
        this.write(dataIce, new DataCollectorInvocationCtx(), true);
    }

    public void clearStats() {
        this.statsCounter.clear();
    }

    private void process(CollectedDataIce dataIce, DataCollectorInvocationCtx invokeCtx) throws FusionExceptionWithRefCode {
        block8: {
            if (dataIce == null) {
                throw DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, "Required parameter is missing", "'dataIce' is null", ErrorCause.DataCollectorErrorReasonType.ILLEGAL_PARAMETER_VALUE);
            }
            if (invokeCtx == null) {
                throw DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, "Required parameter is missing", "'invokeCtx' is null", ErrorCause.DataCollectorErrorReasonType.ILLEGAL_PARAMETER_VALUE);
            }
            Integer dataType = DataCollectorUtils.getDataType(dataIce);
            if (dataType == DataCollectorUtils.UNSPECIFIED_DATA_TYPE) {
                throw DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, "Invalid data type", "Received data type:" + dataIce.dataType, ErrorCause.DataCollectorErrorReasonType.INVALID_DATA_TYPE);
            }
            try {
                List<DataCollectorWriterStrategy<?>> strategyList = this.dataTypeToStrategyImplementationsMap.get(dataType);
                if (strategyList != null) {
                    for (DataCollectorWriterStrategy<?> strategy : strategyList) {
                        strategy.write(dataIce, invokeCtx);
                    }
                    break block8;
                }
                throw DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, "Unsupported data type", "Received data type:" + dataIce.dataType, ErrorCause.DataCollectorErrorReasonType.UNSUPPORTED_DATA_TYPE);
            }
            catch (FusionExceptionWithRefCode ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, "Internal error", ErrorCause.DataCollectorErrorReasonType.INTERNAL_ERROR, ex);
            }
        }
    }
}

