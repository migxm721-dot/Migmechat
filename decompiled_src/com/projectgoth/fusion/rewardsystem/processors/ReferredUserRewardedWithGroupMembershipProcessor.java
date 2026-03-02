/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.ReferredUserRewardedBaseProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedWithGroupMembershipTrigger;

public class ReferredUserRewardedWithGroupMembershipProcessor
extends ReferredUserRewardedBaseProcessor {
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDED_GROUP_IDS_PARAM_KEY = "refdUsrRwdGrpIDs";
    @RewardProgramParamName
    public static final String REFERRED_USER_REWARDED_GROUP_IDS_IS_WHITELIST_PARAM_KEY = "refdUsrRwdGrpIDsIsWhiteLst";

    protected boolean processInternal(RewardProgramData programData, ReferredUserRewardedBaseTrigger trigger) throws Exception {
        if (!(trigger instanceof ReferredUserRewardedWithGroupMembershipTrigger)) {
            return false;
        }
        ReferredUserRewardedWithGroupMembershipTrigger rfdUserRewardedWithGroupMembershipTrigger = (ReferredUserRewardedWithGroupMembershipTrigger)trigger;
        return programData.matchesSetOfStringsConstraint(REFERRED_USER_REWARDED_GROUP_IDS_PARAM_KEY, REFERRED_USER_REWARDED_GROUP_IDS_PARAM_KEY, String.valueOf(rfdUserRewardedWithGroupMembershipTrigger.getRewardedGroupMembership().getGroupId()));
    }
}

