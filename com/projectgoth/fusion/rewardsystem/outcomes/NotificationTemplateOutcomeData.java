/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class NotificationTemplateOutcomeData
extends RewardProgramOutcomeData {
    private static final String CURRENT_DATA_FORMAT_VERSION = "1.0";
    private static final String FIELD_SUBJECT_TEMPLATE = "subjTmplt";
    private static final String FIELD_BODY_TEMPLATE = "bodyTmplt";
    private String subjectTemplate;
    private String contentTemplate;

    protected NotificationTemplateOutcomeData(RewardProgramOutcomeData.TypeEnum type, String subjectTemplate, String contentTemplate) {
        this(type);
        this.subjectTemplate = contentTemplate;
        this.contentTemplate = contentTemplate;
    }

    public String getContentTemplate() {
        return this.contentTemplate;
    }

    public String getSubjectTemplate() {
        return this.subjectTemplate;
    }

    protected NotificationTemplateOutcomeData(RewardProgramOutcomeData.TypeEnum type) {
        super(type);
    }

    protected String currentDataFormatVersion() {
        return CURRENT_DATA_FORMAT_VERSION;
    }

    protected void serializeToJSONObject(JSONObject jsonObject) throws JSONException {
        if (!StringUtil.isBlank(this.contentTemplate)) {
            jsonObject.put(FIELD_BODY_TEMPLATE, (Object)this.contentTemplate);
        }
        if (!StringUtil.isBlank(this.subjectTemplate)) {
            jsonObject.put(FIELD_SUBJECT_TEMPLATE, (Object)this.subjectTemplate);
        }
    }

    protected void deserializeFromJSONObject(JSONObject jsonObject) throws JSONException {
        this.subjectTemplate = jsonObject.optString(FIELD_SUBJECT_TEMPLATE, "");
        this.contentTemplate = jsonObject.optString(FIELD_BODY_TEMPLATE, "");
    }

    public final boolean requiresTemplateData() {
        return true;
    }
}

