/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.instrumentation;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.HostUtils;
import com.projectgoth.fusion.common.SingleThreadedBoundedExecutor;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Metrics;
import com.projectgoth.fusion.interfaces.MetricsHome;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleSummary;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleSummarySink;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EJBSampleSummarySink
extends SampleSummarySink {
    private ExecutorService executor = new SingleThreadedBoundedExecutor(){

        public int getMaxTaskSize() {
            return SystemProperty.getInt(SystemPropertyEntities.MechanicsEngineSettings.EJB_SAMPLE_SUMMARY_SINK_MAX_TASK_SIZE);
        }
    };

    @Override
    protected boolean writeSummary(Collection<SampleSummary> sampleSummaries) throws Throwable {
        return this.writeSummary(false, sampleSummaries);
    }

    private static Callable<Boolean> createLoggingTask(final Collection<SampleSummary> sampleSummaries) {
        return new Callable<Boolean>(){

            @Override
            public Boolean call() throws Exception {
                Metrics metricsEJB = (Metrics)EJBHomeCache.getObject("ejb/Metrics", MetricsHome.class);
                return metricsEJB.logMetricsSampleSummaries(HostUtils.getHostname(), ConfigUtils.getInstanceName(), sampleSummaries);
            }

            public String toString() {
                return "EJBSampleSummary:createLoggingTask(" + sampleSummaries + ")";
            }
        };
    }

    private <T> Future<T> submit(Callable<T> callable) {
        try {
            return this.executor.submit(callable);
        }
        catch (RejectedExecutionException ree) {
            log.error((Object)("EJBSampleSummary isn't able to submit jobs. Dropping Task:[ " + callable + "]"));
            return null;
        }
    }

    protected Future<Boolean> asynchWriteSummary(Collection<SampleSummary> sampleSummaries) {
        return this.submit(EJBSampleSummarySink.createLoggingTask(sampleSummaries));
    }

    protected boolean writeSummary(boolean wait, Collection<SampleSummary> sampleSummaries) throws InterruptedException, ExecutionException {
        Future<Boolean> resultFuture = this.submit(EJBSampleSummarySink.createLoggingTask(sampleSummaries));
        if (resultFuture == null) {
            return false;
        }
        Boolean opResult = resultFuture.get();
        return opResult == null ? false : opResult;
    }
}

