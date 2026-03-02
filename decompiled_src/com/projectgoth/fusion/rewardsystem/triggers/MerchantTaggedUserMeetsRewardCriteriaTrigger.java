/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;

public class MerchantTaggedUserMeetsRewardCriteriaTrigger
extends ReferredUserRewardedBaseTrigger {
    private final boolean rewarded;

    public MerchantTaggedUserMeetsRewardCriteriaTrigger(UserData merchantTaggerUserData, UserData merchantTaggedUserData, RewardProgramData merchantTaggedUserRewardProgram, boolean hasRewards) {
        super(RewardProgramData.TypeEnum.MERCHANT_TAGGED_USER_MEETS_REWARD_CRITERIA, merchantTaggerUserData, merchantTaggedUserData, merchantTaggedUserRewardProgram);
        this.rewarded = hasRewards;
        this.amountDelta = 0.0;
        this.quantityDelta = 1;
    }

    public boolean isRewarded() {
        return this.rewarded;
    }
}

