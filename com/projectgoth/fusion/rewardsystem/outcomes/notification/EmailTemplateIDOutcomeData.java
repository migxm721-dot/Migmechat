/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes.notification;

import com.projectgoth.fusion.rewardsystem.outcomes.NotificationTemplateOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import org.json.JSONException;
import org.json.JSONObject;

public class EmailTemplateIDOutcomeData
extends NotificationTemplateOutcomeData {
    public EmailTemplateIDOutcomeData(int emailTemplateId) {
        super(RewardProgramOutcomeData.TypeEnum.EMAIL_TEMPLATE_ID, "" + emailTemplateId, "" + emailTemplateId);
    }

    private EmailTemplateIDOutcomeData() {
        super(RewardProgramOutcomeData.TypeEnum.EMAIL_TEMPLATE_ID);
    }

    public static EmailTemplateIDOutcomeData create(JSONObject jsonObject) throws JSONException {
        return (EmailTemplateIDOutcomeData)new EmailTemplateIDOutcomeData().fromJSONObject(jsonObject);
    }
}

