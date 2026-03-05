/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.userreward.ReputationScoreSourceTypeEnum
 *  com.projectgoth.leto.common.event.userreward.UserRewardedWithReputationScoreEvent
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.RewardedReputationData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.UserRewardedBaseTrigger;
import com.projectgoth.leto.common.event.userreward.ReputationScoreSourceTypeEnum;
import com.projectgoth.leto.common.event.userreward.UserRewardedWithReputationScoreEvent;
import java.util.Date;

public class UserRewardedWithReputationScoreTrigger
extends UserRewardedBaseTrigger
implements UserRewardedWithReputationScoreEvent {
    private final RewardedReputationData.ReputationRewardSourceEnum rewardSourceType;

    public UserRewardedWithReputationScoreTrigger(UserData userData, RewardProgramData fulfilledUserRewardProgram, int scoreDelta, RewardedReputationData.ReputationRewardSourceEnum rewardSourceType, Date rewardedTimestamp) {
        super(RewardProgramData.TypeEnum.USER_REWARDED_WITH_REPUTATION_SCORE, userData, fulfilledUserRewardProgram, rewardedTimestamp);
        this.quantityDelta = scoreDelta;
        this.amountDelta = 0.0;
        this.rewardSourceType = rewardSourceType;
    }

    public int getReputationScoreQuantityAwarded() {
        return this.quantityDelta;
    }

    public RewardedReputationData.ReputationRewardSourceEnum getRewardSourceType() {
        return this.rewardSourceType;
    }

    public ReputationScoreSourceTypeEnum getReputationScoreSourceType() {
        return this.rewardSourceType.toReputationScoreSourceTypeEnum();
    }
}

