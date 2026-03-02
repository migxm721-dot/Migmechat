/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.ReferredUserRewardedBaseProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserMeetsRewardCriteriaTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;
import org.apache.log4j.Logger;

public class ReferredUserMeetsRewardCriteriaProcessor
extends ReferredUserRewardedBaseProcessor {
    @RewardProgramParamName
    public static final String REFERRED_USER_IS_REWARDED = "refdUsrIsRewarded";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ReferredUserMeetsRewardCriteriaProcessor.class));

    protected boolean processInternal(RewardProgramData programData, ReferredUserRewardedBaseTrigger trigger) {
        if (!(trigger instanceof ReferredUserMeetsRewardCriteriaTrigger)) {
            return false;
        }
        ReferredUserMeetsRewardCriteriaTrigger rfdUserMeetsRewardCriteriaTrigger = (ReferredUserMeetsRewardCriteriaTrigger)trigger;
        if (programData.hasParameter(REFERRED_USER_IS_REWARDED)) {
            boolean referredUserIsRewardedConstraint = programData.getBoolParam(REFERRED_USER_IS_REWARDED, false);
            boolean referredUserIsRewarded = rfdUserMeetsRewardCriteriaTrigger.isRewarded();
            if (log.isDebugEnabled()) {
                log.debug((Object)("referredUserIsRewardedConstraint:[" + referredUserIsRewardedConstraint + "].referredUserIsRewarded:[" + referredUserIsRewarded + "]"));
            }
            return referredUserIsRewardedConstraint == referredUserIsRewarded;
        }
        return true;
    }
}

