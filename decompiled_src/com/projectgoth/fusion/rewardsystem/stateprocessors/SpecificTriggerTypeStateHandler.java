/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.stateprocessors.RewardProgramStateHandler;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class SpecificTriggerTypeStateHandler<T extends RewardProgramTrigger>
extends RewardProgramStateHandler {
    private final Class<T> expectedTriggerClass;

    protected SpecificTriggerTypeStateHandler(Class<T> expectedTriggerClass) {
        this.expectedTriggerClass = expectedTriggerClass;
    }

    @Override
    public RewardProgramStateHandler.PerformReturn perform(RewardProgramData program, RewardProgramTrigger trigger, String stateData) {
        if (trigger == null || !this.expectedTriggerClass.isAssignableFrom(trigger.getClass())) {
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        return this.performWithSpecificTrigger(program, trigger, stateData);
    }

    protected abstract RewardProgramStateHandler.PerformReturn performWithSpecificTrigger(RewardProgramData var1, T var2, String var3);
}

