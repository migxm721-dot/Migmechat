/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.instrumentation;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.rewardsystem.instrumentation.EJBSampleSummarySink;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleSummary;
import java.util.Collection;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SampleSummarySink {
    public static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SampleSummarySink.class));
    private static SampleSummarySink defaultInstance = SampleSummarySink.resetToDefaultInstance();
    public static final SampleSummarySink NULL_SAMPLE_SUMMARY_SINK = new SampleSummarySink(){

        @Override
        protected boolean writeSummary(Collection<SampleSummary> sampleSummary) throws Throwable {
            return true;
        }
    };

    public static SampleSummarySink getInstance() {
        return defaultInstance;
    }

    protected SampleSummarySink() {
    }

    public final boolean write(Collection<SampleSummary> sampleSummary) {
        try {
            if (SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.ENABLE_TRIGGER_METRICS_LOGGING)) {
                this.writeSummary(sampleSummary);
            }
            return true;
        }
        catch (Throwable t) {
            log.warn((Object)("Unable to write summary [" + sampleSummary + "].Exception:" + t), t);
            return false;
        }
    }

    protected boolean writeSummary(Collection<SampleSummary> sampleSummary) throws Throwable {
        return true;
    }

    public static SampleSummarySink resetToDefaultInstance() {
        defaultInstance = new EJBSampleSummarySink();
        return defaultInstance;
    }

    public static SampleSummarySink setInstance(SampleSummarySink instance) {
        SampleSummarySink oldInstance = defaultInstance;
        defaultInstance = instance;
        return oldInstance;
    }
}

