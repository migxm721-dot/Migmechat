/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.common.JSONUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BasicRewardProgramOutcomeData
extends RewardProgramOutcomeData {
    private static final String CURRENT_DATA_FORMAT_VERSION = "1.0";
    public Double migCreditAmount;
    public String migCreditCurrency;
    public int scoreReward;
    public int migLevelReward;
    public String accountEntryRemarks;
    private static final String FIELD_MIG_CREDIT_AMOUNT = "migCrAmt";
    private static final String FIELD_MIG_CREDIT_CURRENCY = "migCrCcy";
    private static final String FIELD_SCORE_REWARD = "scoreRwd";
    private static final String FIELD_MIGLEVEL_REWARD = "migLvlRwd";
    private static final String FIELD_ACCOUNT_ENTRY_REMARKS = "acEntryRmk";

    public BasicRewardProgramOutcomeData() {
        super(RewardProgramOutcomeData.TypeEnum.BASIC);
    }

    @Override
    protected void serializeToJSONObject(JSONObject jsonObject) throws JSONException {
        if (this.migCreditAmount != null) {
            jsonObject.put(FIELD_MIG_CREDIT_AMOUNT, (Object)this.migCreditAmount);
            jsonObject.put(FIELD_MIG_CREDIT_CURRENCY, (Object)this.migCreditCurrency);
        }
        if (this.scoreReward > 0) {
            jsonObject.put(FIELD_SCORE_REWARD, this.scoreReward);
        }
        if (this.migLevelReward > 0) {
            jsonObject.put(FIELD_MIGLEVEL_REWARD, this.migLevelReward);
        }
        if (!StringUtil.isBlank(this.accountEntryRemarks)) {
            jsonObject.put(FIELD_ACCOUNT_ENTRY_REMARKS, (Object)this.accountEntryRemarks);
        }
    }

    @Override
    protected String currentDataFormatVersion() {
        return CURRENT_DATA_FORMAT_VERSION;
    }

    @Override
    protected void deserializeFromJSONObject(JSONObject jsonObject) throws JSONException {
        Double migCreditAmountDouble = JSONUtils.getDouble(jsonObject, FIELD_MIG_CREDIT_AMOUNT);
        String migCreditCcy = migCreditAmountDouble != null ? jsonObject.getString(FIELD_MIG_CREDIT_CURRENCY) : null;
        int scoreReward = jsonObject.optInt(FIELD_SCORE_REWARD, 0);
        int migLevelReward = jsonObject.optInt(FIELD_MIGLEVEL_REWARD, 0);
        String accEntryRemark = jsonObject.optString(FIELD_ACCOUNT_ENTRY_REMARKS);
        this.migCreditAmount = migCreditAmountDouble;
        this.migCreditCurrency = migCreditCcy;
        this.scoreReward = scoreReward;
        this.migLevelReward = migLevelReward;
        this.accountEntryRemarks = accEntryRemark;
    }

    @Override
    public void populateTemplateDataMap(int currentIndex, Map<String, String> templateDataMap) {
        if (this.migCreditAmount != null && this.migCreditAmount > 0.0) {
            this.setTemplateDataValue(currentIndex, templateDataMap, "migCreditAmount", this.migCreditAmount);
        }
        if (!StringUtil.isBlank(this.migCreditCurrency)) {
            this.setTemplateDataValue(currentIndex, templateDataMap, "migCreditCurrency", this.migCreditCurrency);
        }
        if (this.scoreReward > 0) {
            this.setTemplateDataValue(currentIndex, templateDataMap, "scoreReward", this.scoreReward);
        }
        if (this.migLevelReward > 0) {
            this.setTemplateDataValue(currentIndex, templateDataMap, "migLevelReward", this.migLevelReward);
        }
    }

    @Override
    public boolean requiresTemplateData() {
        return false;
    }
}

