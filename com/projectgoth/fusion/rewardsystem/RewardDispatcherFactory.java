/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardDispatcher;

public interface RewardDispatcherFactory {
    public RewardDispatcher getNewInstance(RewardProgramData var1);
}

