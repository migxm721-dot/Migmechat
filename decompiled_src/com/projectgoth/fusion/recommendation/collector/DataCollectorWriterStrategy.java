/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector;

import com.projectgoth.fusion.recommendation.collector.DataCollectorInvocationCtx;
import com.projectgoth.fusion.recommendation.collector.ICollectorSink;
import com.projectgoth.fusion.recommendation.collector.ICollectorTransformation;
import com.projectgoth.fusion.slice.CollectedDataIce;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class DataCollectorWriterStrategy<TLoggable> {
    private final List<ICollectorSink<TLoggable>> collectorSinkList = new ArrayList<ICollectorSink<TLoggable>>();
    private final ICollectorTransformation<TLoggable> transformation;
    private final String name;

    public DataCollectorWriterStrategy(String name, ICollectorTransformation<TLoggable> transformation) {
        this.transformation = transformation;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public DataCollectorWriterStrategy<TLoggable> addSink(ICollectorSink<TLoggable> sink) {
        this.collectorSinkList.add(sink);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(CollectedDataIce dataIce, DataCollectorInvocationCtx invokeCtx) {
        String strategyName = this.getName();
        invokeCtx.beginStrategy(strategyName);
        try {
            Collection<TLoggable> loggables;
            String transformationName = this.transformation.getName();
            try {
                invokeCtx.beforeTransformation(strategyName, transformationName);
                loggables = this.transformation.toLoggables(dataIce);
                invokeCtx.afterTransformation(strategyName, transformationName, null);
            }
            catch (Exception ex) {
                invokeCtx.afterTransformation(strategyName, transformationName, ex);
                Object var11_8 = null;
                invokeCtx.afterStrategy(strategyName);
                return;
            }
            for (ICollectorSink<TLoggable> logSink : this.collectorSinkList) {
                String logSinkName = logSink.getName();
                try {
                    invokeCtx.beforeWritingToSink(strategyName, transformationName, logSinkName);
                    if (loggables != null && !loggables.isEmpty()) {
                        logSink.write(loggables);
                    }
                    invokeCtx.afterWritingToSink(strategyName, transformationName, logSinkName, null);
                }
                catch (Exception ex) {
                    invokeCtx.afterWritingToSink(strategyName, transformationName, logSinkName, ex);
                }
            }
            Object var11_9 = null;
            invokeCtx.afterStrategy(strategyName);
        }
        catch (Throwable throwable) {
            Object var11_10 = null;
            invokeCtx.afterStrategy(strategyName);
            throw throwable;
        }
    }
}

