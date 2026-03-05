/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.log4j.Log4JUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.outcomes.OutcomeWeightData;
import com.projectgoth.fusion.rewardsystem.outcomes.OutcomeWeightsDataList;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeDataDeserializer;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.security.SecureRandom;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class WeightedRandomOutcomeProcessors
extends RewardProgramOutcomeProcessor {
    public static String PARAM_KEY_POSSIBLE_OUTCOMES = "possibleOutcomes";
    private static final Logger log = Log4JUtils.getLogger(WeightedRandomOutcomeProcessors.class);
    private static final ThreadLocal<SecureRandom> randomGeneratorTLS = new ThreadLocal<SecureRandom>(){

        @Override
        protected SecureRandom initialValue() {
            return new SecureRandom();
        }
    };

    public RewardProgramOutcomeData getOutcome(RewardProgramData rewardProgramData, RewardProgramTrigger trigger) throws Exception {
        if (rewardProgramData.hasParameter(PARAM_KEY_POSSIBLE_OUTCOMES)) {
            JSONObject jsonObject;
            OutcomeWeightsDataList possibleOutcomes;
            OutcomeWeightData pickedOutcome;
            String possibleOutcomesJSONStr = rewardProgramData.getStringParam(PARAM_KEY_POSSIBLE_OUTCOMES, null);
            if (log.isDebugEnabled()) {
                log.debug((Object)("possibleOutcomesJSONStr:" + possibleOutcomesJSONStr));
            }
            if ((pickedOutcome = WeightedRandomOutcomeProcessors.randomlyPick(possibleOutcomes = OutcomeWeightsDataList.create(jsonObject = new JSONObject(possibleOutcomesJSONStr)))) != null) {
                String outcomeRewardParameterName = pickedOutcome.getOutcome();
                String rewardProgramOutcomeJSONStr = rewardProgramData.getStringParam(outcomeRewardParameterName, null);
                if (!StringUtil.isBlank(rewardProgramOutcomeJSONStr)) {
                    return RewardProgramOutcomeDataDeserializer.deserialize(new JSONObject(rewardProgramOutcomeJSONStr));
                }
                log.error((Object)("Missing reward parameter:[" + outcomeRewardParameterName + "] for reward program [" + rewardProgramData.id + "]"));
            } else {
                return null;
            }
        }
        return null;
    }

    public static OutcomeWeightData randomlyPick(OutcomeWeightsDataList possibleOutcomes) throws JSONException {
        if (possibleOutcomes.count() == 0) {
            return null;
        }
        SecureRandom randomGenerator = randomGeneratorTLS.get();
        int randomNum = randomGenerator.nextInt(possibleOutcomes.sumOfWeights()) + 1;
        int accumulatedWeights = 0;
        for (int i = 0; i < possibleOutcomes.count(); ++i) {
            OutcomeWeightData outcome = possibleOutcomes.get(i);
            if (randomNum > (accumulatedWeights += outcome.getWeight())) continue;
            return outcome;
        }
        log.warn((Object)("Couldn't get any possible outcome from " + possibleOutcomes.toJSONObject() + ".Bug?"));
        return possibleOutcomes.get(possibleOutcomes.count() - 1);
    }
}

