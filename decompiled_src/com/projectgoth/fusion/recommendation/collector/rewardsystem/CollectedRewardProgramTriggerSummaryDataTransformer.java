/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector.rewardsystem;

import com.projectgoth.fusion.recommendation.collector.CollectedDataTypeEnum;
import com.projectgoth.fusion.recommendation.collector.CollectorTransformationException;
import com.projectgoth.fusion.recommendation.collector.ICollectorTransformation;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.CollectedRewardProgramTriggerSummaryDataIce;
import java.util.Arrays;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CollectedRewardProgramTriggerSummaryDataTransformer
implements ICollectorTransformation<CollectedRewardProgramTriggerSummaryDataIce> {
    private final String name;

    public CollectedRewardProgramTriggerSummaryDataTransformer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Collection<CollectedRewardProgramTriggerSummaryDataIce> toLoggables(CollectedDataIce dataIce) throws CollectorTransformationException {
        if (dataIce.dataType != CollectedDataTypeEnum.REWARD_PROGRAM_TRIGGER_SUMMARY.getCode()) {
            throw new CollectorTransformationException("Don't understand dataIce.type " + dataIce.dataType);
        }
        return Arrays.asList((CollectedRewardProgramTriggerSummaryDataIce)dataIce);
    }
}

