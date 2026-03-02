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

public class IMNotificationTemplateOutcomeData
extends NotificationTemplateOutcomeData {
    public IMNotificationTemplateOutcomeData(String template) {
        super(RewardProgramOutcomeData.TypeEnum.IMNOTIFICATION_TEMPLATE, template, template);
    }

    private IMNotificationTemplateOutcomeData() {
        super(RewardProgramOutcomeData.TypeEnum.IMNOTIFICATION_TEMPLATE);
    }

    public static IMNotificationTemplateOutcomeData create(JSONObject jsonObject) throws JSONException {
        return (IMNotificationTemplateOutcomeData)new IMNotificationTemplateOutcomeData().fromJSONObject(jsonObject);
    }
}

