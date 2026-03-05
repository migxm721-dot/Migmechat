/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.outcomes.BasicRewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleFactorOfBasicTriggerValues
extends RewardProgramOutcomeProcessor {
    private static final Pattern TERM_SEPARATOR_PATTERN = Pattern.compile("\\s*\\*\\s*");
    public static final String PARAM_CREDIT_REWARD = "credit.reward";
    public static final String PARAM_SCORE_REWARD = "score.reward";
    public static final String PARAM_MIGLEVEL_REWARD = "miglevel.reward";
    public static final String PARAM_ACC_ENTRY_REMARKS = "acc.entry.remarks";
    public static final String FIELD_TRIGGER_AMOUNT = "trigger.amt";
    public static final String FIELD_TRIGGER_QUANTITY = "trigger.qty";

    @Override
    public RewardProgramOutcomeData getOutcome(RewardProgramData data, RewardProgramTrigger trigger) {
        Double reward;
        String creditRewardRule = data.getStringParam(PARAM_CREDIT_REWARD, "");
        String scoreRewardRule = data.getStringParam(PARAM_SCORE_REWARD, "");
        String migLevelRewardRule = data.getStringParam(PARAM_MIGLEVEL_REWARD, "");
        String accEntryRewardRemarks = data.getStringParam(PARAM_ACC_ENTRY_REMARKS, "");
        BasicRewardProgramOutcomeData outcomeData = new BasicRewardProgramOutcomeData();
        Map<String, Double> triggerValuesMap = this.getTriggerValuesMap(trigger);
        if (!StringUtil.isBlank(creditRewardRule) && (reward = this.eval(creditRewardRule, triggerValuesMap)) != null) {
            outcomeData.migCreditAmount = reward;
            outcomeData.migCreditCurrency = trigger.currency;
        }
        if (!StringUtil.isBlank(scoreRewardRule) && (reward = this.eval(scoreRewardRule, triggerValuesMap)) != null) {
            outcomeData.scoreReward = reward.intValue();
        }
        if (!StringUtil.isBlank(migLevelRewardRule) && (reward = this.eval(migLevelRewardRule, triggerValuesMap)) != null) {
            outcomeData.migLevelReward = reward.intValue();
        }
        outcomeData.accountEntryRemarks = false == StringUtil.isBlank(accEntryRewardRemarks) ? accEntryRewardRemarks : null;
        return outcomeData;
    }

    private Map<String, Double> getTriggerValuesMap(RewardProgramTrigger trigger) {
        HashMap<String, Double> triggerValues = new HashMap<String, Double>();
        if (trigger.amountDelta > 0.0) {
            triggerValues.put(FIELD_TRIGGER_AMOUNT, trigger.amountDelta);
        }
        if (trigger.quantityDelta > 0) {
            triggerValues.put(FIELD_TRIGGER_QUANTITY, Double.valueOf(trigger.quantityDelta));
        }
        return triggerValues;
    }

    private Double eval(String multiplicationTerm, Map<String, Double> triggerValues) {
        String[] term = TERM_SEPARATOR_PATTERN.split(multiplicationTerm);
        if (term != null && term.length == 2) {
            double factor;
            try {
                factor = Double.parseDouble(term[0].trim());
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Unable to evaluate:[" + multiplicationTerm + "]", nfe);
            }
            String triggerField = term[1].trim();
            Double value = triggerValues.get(triggerField);
            if (value != null) {
                return factor * value;
            }
            return null;
        }
        throw new IllegalArgumentException("Unable to evaluate:[" + multiplicationTerm + "]");
    }
}

