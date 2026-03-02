/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.instrumentation;

import com.projectgoth.fusion.rewardsystem.instrumentation.ProcessingResultEnum;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleCategory;

public interface Sample {
    public SampleCategory getSampleCategory();

    public ProcessingResultEnum getProcessingResult();

    public long getReceivedTimestamp();

    public long getDequeuedTimestamp();

    public long getEndProcessTimestamp();
}

