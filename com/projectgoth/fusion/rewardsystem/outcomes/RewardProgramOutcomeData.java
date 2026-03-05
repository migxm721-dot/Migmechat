/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.data.JSONSerializable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class RewardProgramOutcomeData
implements Serializable,
JSONSerializable<RewardProgramOutcomeData> {
    public static final String FIELD_TYPE = "_t";
    public static final String FIELD_VERSION = "_v";
    public TypeEnum type;
    public String version;

    protected RewardProgramOutcomeData(TypeEnum type) {
        this.type = type;
    }

    @Override
    public final JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_VERSION, (Object)this.currentDataFormatVersion());
        jsonObject.put(FIELD_TYPE, this.type.value);
        this.serializeToJSONObject(jsonObject);
        return jsonObject;
    }

    protected abstract String currentDataFormatVersion();

    protected abstract void serializeToJSONObject(JSONObject var1) throws JSONException;

    @Override
    public final RewardProgramOutcomeData fromJSONObject(JSONObject jsonObject) throws JSONException {
        this.deserializeFromJSONObject(jsonObject);
        this.type = TypeEnum.fromCode(jsonObject.getInt(FIELD_TYPE));
        this.version = jsonObject.getString(FIELD_VERSION);
        return this;
    }

    protected abstract void deserializeFromJSONObject(JSONObject var1) throws JSONException;

    protected void setTemplateDataValue(int currentIndex, Map<String, String> templateDataMap, String paramName, Object value) {
        if (value != null) {
            templateDataMap.put(this.type.name() + "[" + currentIndex + "]." + paramName, value.toString());
        }
    }

    public void populateTemplateDataMap(int currentIndex, Map<String, String> templateDataMap) {
    }

    public boolean requiresTemplateData() {
        return false;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        BASIC(1),
        UNLOCKED_STORE_ITEMS(2),
        EMAIL_TEMPLATE_ID(3),
        IMNOTIFICATION_TEMPLATE(4);

        private int value;

        private TypeEnum(int value) {
            this.value = value;
        }

        public Integer getEnumValue() {
            return this.value;
        }

        public int getCode() {
            return this.value;
        }

        public static TypeEnum fromCode(int value) {
            return SingletonHolder.LOOKUP_BY_CODE.get(value);
        }

        private static class SingletonHolder {
            public static final Map<Integer, TypeEnum> LOOKUP_BY_CODE = EnumUtils.buildLookUpMap(new HashMap(), TypeEnum.class);

            private SingletonHolder() {
            }
        }
    }
}

