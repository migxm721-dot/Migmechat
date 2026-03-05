/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.instrumentation;

import com.projectgoth.fusion.rewardsystem.instrumentation.MetricsSink;
import com.projectgoth.fusion.rewardsystem.instrumentation.Sample;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleSummarizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SummarizingMetricsSink
extends MetricsSink {
    private final SampleSummarizer summarizer = new SampleSummarizer();

    private SummarizingMetricsSink() {
    }

    public static SummarizingMetricsSink getInstance() {
        return SingletonHolder.getSummarizingMetricsSink();
    }

    protected void writeSample(Sample sample) throws Throwable {
        this.summarizer.append(sample);
    }

    public boolean flushMetricsSummary(boolean wait) throws ExecutionException, InterruptedException {
        Future<Boolean> flushResult = this.summarizer.flushMetricSummaries();
        if (wait) {
            if (flushResult == null) {
                return false;
            }
            Boolean opResult = flushResult.get();
            return opResult == null ? false : opResult;
        }
        return flushResult != null;
    }

    private static class SingletonHolder {
        private static final SummarizingMetricsSink summarizingMetricsSink = new SummarizingMetricsSink();

        private SingletonHolder() {
        }

        public static SummarizingMetricsSink getSummarizingMetricsSink() {
            return summarizingMetricsSink;
        }
    }
}

