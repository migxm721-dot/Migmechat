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
import com.projectgoth.fusion.rewardsystem.triggers.MerchantTaggedUserMeetsRewardCriteriaTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;
import org.apache.log4j.Logger;

public class MerchantTaggedUserMeetsRewardCriteriaProcessor
extends ReferredUserRewardedBaseProcessor {
    @RewardProgramParamName
    public static final String MERCHANT_TAGGED_USER_IS_REWARDED = "mtTaggedUsrIsRewarded";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MerchantTaggedUserMeetsRewardCriteriaProcessor.class));

    protected boolean processInternal(RewardProgramData programData, ReferredUserRewardedBaseTrigger trigger) {
        if (!(trigger instanceof MerchantTaggedUserMeetsRewardCriteriaTrigger)) {
            return false;
        }
        MerchantTaggedUserMeetsRewardCriteriaTrigger taggedUserMeetsRewardCriteriaTrigger = (MerchantTaggedUserMeetsRewardCriteriaTrigger)trigger;
        if (programData.hasParameter(MERCHANT_TAGGED_USER_IS_REWARDED)) {
            boolean taggedUserIsRewardedConstraint = programData.getBoolParam(MERCHANT_TAGGED_USER_IS_REWARDED, false);
            boolean taggedUserIsRewarded = taggedUserMeetsRewardCriteriaTrigger.isRewarded();
            if (log.isDebugEnabled()) {
                log.debug((Object)("taggedUserIsRewardedConstraint:[" + taggedUserIsRewardedConstraint + "].referredUserIsRewarded:[" + taggedUserIsRewarded + "]"));
            }
            return taggedUserIsRewardedConstraint == taggedUserIsRewarded;
        }
        return true;
    }
}

