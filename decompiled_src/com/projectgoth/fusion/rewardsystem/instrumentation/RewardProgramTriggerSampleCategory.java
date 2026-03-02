/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.instrumentation;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.instrumentation.SampleCategory;

public class RewardProgramTriggerSampleCategory
implements SampleCategory {
    private final RewardProgramData.TypeEnum type;

    public RewardProgramTriggerSampleCategory(RewardProgramData.TypeEnum type) {
        this.type = type;
    }

    public int hashCode() {
        return this.type.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof RewardProgramTriggerSampleCategory) {
            return this.type.equals(((RewardProgramTriggerSampleCategory)obj).type);
        }
        return false;
    }

    public int intValue() {
        return this.type.value();
    }

    public String toString() {
        return "RewardProgramTriggerSampleCategory:type=[" + this.type + "]";
    }
}

