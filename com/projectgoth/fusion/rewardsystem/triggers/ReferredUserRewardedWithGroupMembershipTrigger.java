/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.RewardedGroupMembershipData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;

public class ReferredUserRewardedWithGroupMembershipTrigger
extends ReferredUserRewardedBaseTrigger {
    private RewardedGroupMembershipData rewardedGroupMembership;

    public ReferredUserRewardedWithGroupMembershipTrigger(UserData referrerUserData, UserData referredUserData, RewardProgramData referredUserRewardProgram, RewardedGroupMembershipData rewardedGroupMembership) {
        super(RewardProgramData.TypeEnum.REFERRED_USER_REWARDED_WITH_GROUPMEMBERSHIP, referrerUserData, referredUserData, referredUserRewardProgram);
        this.rewardedGroupMembership = rewardedGroupMembership;
        this.amountDelta = 0.0;
        this.quantityDelta = 1;
    }

    public RewardedGroupMembershipData getRewardedGroupMembership() {
        return this.rewardedGroupMembership;
    }
}

