/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class UserReferralActivationTrigger
extends RewardProgramTrigger {
    public UserReferralActivationTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.USER_REFERRAL_AUTHENTICATED, userData);
    }
}

