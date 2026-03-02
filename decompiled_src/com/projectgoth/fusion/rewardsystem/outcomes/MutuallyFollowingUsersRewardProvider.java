/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.common.log4j.Log4JUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeDataDeserializer;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.MutuallyFollowingEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class MutuallyFollowingUsersRewardProvider
extends RewardProgramOutcomeProcessor {
    private static final Logger log = Log4JUtils.getLogger(MutuallyFollowingUsersRewardProvider.class);
    public static final String PARAM_INITIATOR_REWARD = "initiatorReward";
    public static final String PARAM_FOLLOW_BACKER_REWARD = "followBackerReward";

    public RewardProgramOutcomeData getOutcome(RewardProgramData data, RewardProgramTrigger trigger) throws JSONException {
        if (!(trigger instanceof MutuallyFollowingEventTrigger)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("does not support selecting outcome from trigger:[" + trigger + "]"));
            }
            return null;
        }
        MutuallyFollowingEventTrigger mfeTrigger = (MutuallyFollowingEventTrigger)trigger;
        String outcomeJSONString = mfeTrigger.isThisUserFollowedBacked() ? data.getStringParam(PARAM_FOLLOW_BACKER_REWARD, null) : data.getStringParam(PARAM_INITIATOR_REWARD, null);
        if (outcomeJSONString != null) {
            JSONObject jsonObject = new JSONObject(outcomeJSONString);
            return RewardProgramOutcomeDataDeserializer.deserialize(jsonObject);
        }
        return null;
    }
}

