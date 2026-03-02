/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector.rewardsystem;

import com.projectgoth.fusion.recommendation.collector.rewardsystem.RewardProgramTriggerSummaryLogUtils;
import com.projectgoth.fusion.recommendation.collector.sinks.log4j.Log4JSink;
import com.projectgoth.fusion.slice.CollectedRewardProgramTriggerSummaryDataIce;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CollectedRewardProgramTriggerSummaryLog4jSink
extends Log4JSink<CollectedRewardProgramTriggerSummaryDataIce> {
    public CollectedRewardProgramTriggerSummaryLog4jSink(String name, String loggerSinkCategoryName) {
        super(name, loggerSinkCategoryName);
    }

    @Override
    protected String toString(CollectedRewardProgramTriggerSummaryDataIce record) {
        return RewardProgramTriggerSummaryLogUtils.toString(record);
    }
}

