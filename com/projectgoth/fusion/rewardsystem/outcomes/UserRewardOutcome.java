/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.outcome.Outcomes
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.data.JSONSerializable;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeDataDeserializer;
import com.projectgoth.leto.common.outcome.Outcomes;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UserRewardOutcome
implements Outcomes,
Serializable,
JSONSerializable<UserRewardOutcome> {
    private static final String DATA_FORMAT_VERSION_1_2 = "1.2";
    @Deprecated
    private static final String DATA_FORMAT_VERSION_1_1 = "1.1";
    @Deprecated
    private static final String DATA_FORMAT_VERSION_1_0 = "1.0";
    private static final String FIELD_VERSION = "_v";
    private static final String FIELD_USERID = "uid";
    private static final String FIELD_OUTCOME_DATA_LIST = "ocdLst";
    private static final String FIELD_TEMPLATE_DATA = "tmpltData";
    private static final String FIELD_CREATE_TS = "createTs";
    private static final String FIELD_ORIGIN = "origin";
    private static final String FIELD_ID = "id";
    private int userid;
    private List<RewardProgramOutcomeData> outcomeDataList;
    private Map<String, String> templateData;
    private String id;
    private String origin;
    private long createTs;
    private int outcomeType = 1;

    public UserRewardOutcome() {
    }

    public UserRewardOutcome(String id, String origin, long createTs) {
        this();
        this.id = id;
        this.origin = origin;
        this.createTs = createTs;
    }

    public List<RewardProgramOutcomeData> getOutcomeDataList() {
        return this.outcomeDataList;
    }

    public void setOutcomeDataList(List<RewardProgramOutcomeData> outcomeDataList) {
        this.outcomeDataList = outcomeDataList;
    }

    public int getUserid() {
        return this.userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Map<String, String> getTemplateData() {
        return this.templateData;
    }

    public void setTemplateData(Map<String, String> templateData) {
        this.templateData = templateData;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public long getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(long createTs) {
        this.createTs = createTs;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        return this.toJSONObject_1_2();
    }

    protected final JSONObject toJSONObject_1_2() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_ID, (Object)this.id);
        jsonObject.put("type", this.outcomeType);
        jsonObject.put(FIELD_CREATE_TS, this.createTs);
        jsonObject.put(FIELD_ORIGIN, (Object)this.origin);
        jsonObject.put(FIELD_VERSION, (Object)DATA_FORMAT_VERSION_1_2);
        jsonObject.put(FIELD_USERID, this.userid);
        if (this.outcomeDataList != null) {
            JSONArray outcomeDataJsonArray = new JSONArray();
            for (RewardProgramOutcomeData outcomeData : this.outcomeDataList) {
                if (outcomeData == null) continue;
                outcomeDataJsonArray.put((Object)outcomeData.toJSONObject());
            }
            if (outcomeDataJsonArray.length() != 0) {
                jsonObject.put(FIELD_OUTCOME_DATA_LIST, (Object)outcomeDataJsonArray);
            }
        }
        if (this.templateData != null && this.templateData.size() > 0) {
            jsonObject.put(FIELD_TEMPLATE_DATA, (Object)new JSONObject(this.templateData));
        }
        return jsonObject;
    }

    @Deprecated
    protected final JSONObject toJSONObject_1_1() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_ID, (Object)this.id);
        jsonObject.put(FIELD_CREATE_TS, this.createTs);
        jsonObject.put(FIELD_ORIGIN, (Object)this.origin);
        jsonObject.put(FIELD_VERSION, (Object)DATA_FORMAT_VERSION_1_1);
        jsonObject.put(FIELD_USERID, this.userid);
        if (this.outcomeDataList != null) {
            JSONArray outcomeDataJsonArray = new JSONArray();
            for (RewardProgramOutcomeData outcomeData : this.outcomeDataList) {
                if (outcomeData == null) continue;
                outcomeDataJsonArray.put((Object)outcomeData.toJSONObject());
            }
            if (outcomeDataJsonArray.length() != 0) {
                jsonObject.put(FIELD_OUTCOME_DATA_LIST, (Object)outcomeDataJsonArray);
            }
        }
        if (this.templateData != null && this.templateData.size() > 0) {
            jsonObject.put(FIELD_TEMPLATE_DATA, (Object)new JSONObject(this.templateData));
        }
        return jsonObject;
    }

    @Deprecated
    protected final JSONObject toJSONObject_1_0() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_VERSION, (Object)DATA_FORMAT_VERSION_1_0);
        jsonObject.put(FIELD_USERID, this.userid);
        if (this.outcomeDataList != null) {
            JSONArray outcomeDataJsonArray = new JSONArray();
            for (RewardProgramOutcomeData outcomeData : this.outcomeDataList) {
                if (outcomeData == null) continue;
                outcomeDataJsonArray.put((Object)outcomeData.toJSONObject());
            }
            if (outcomeDataJsonArray.length() != 0) {
                jsonObject.put(FIELD_OUTCOME_DATA_LIST, (Object)outcomeDataJsonArray);
            }
        }
        if (this.templateData != null && this.templateData.size() > 0) {
            jsonObject.put(FIELD_TEMPLATE_DATA, (Object)new JSONObject(this.templateData));
        }
        return jsonObject;
    }

    @Override
    public UserRewardOutcome fromJSONObject(JSONObject jsonObject) throws JSONException {
        String version = jsonObject.getString(FIELD_VERSION);
        if (StringUtil.equals(DATA_FORMAT_VERSION_1_2, version)) {
            return this.fromJSONObject_1_2(jsonObject);
        }
        if (StringUtil.equals(DATA_FORMAT_VERSION_1_1, version)) {
            return this.fromJSONObject_1_1(jsonObject);
        }
        if (StringUtil.equals(DATA_FORMAT_VERSION_1_0, version)) {
            return this.fromJSONObject_1_0(jsonObject);
        }
        throw new JSONException("Unsupported version [" + version + "]");
    }

    protected final UserRewardOutcome fromJSONObject_1_2(JSONObject jsonObject) throws JSONException {
        HashMap<String, String> templateDataMap;
        JSONObject templateDataJSON;
        ArrayList<RewardProgramOutcomeData> outcomeDataList;
        int outcomeType = jsonObject.optInt("type", 1);
        if (outcomeType != 1) {
            throw new JSONException("Expected value for field [type] is [1] found [" + outcomeType + "] instead");
        }
        String id = jsonObject.getString(FIELD_ID);
        String origin = jsonObject.getString(FIELD_ORIGIN);
        long createTs = jsonObject.getLong(FIELD_CREATE_TS);
        int userid = jsonObject.getInt(FIELD_USERID);
        JSONArray outcomeDataJsonArray = jsonObject.optJSONArray(FIELD_OUTCOME_DATA_LIST);
        if (outcomeDataJsonArray != null && outcomeDataJsonArray.length() != 0) {
            outcomeDataList = new ArrayList<RewardProgramOutcomeData>(outcomeDataJsonArray.length());
            for (int i = 0; i < outcomeDataJsonArray.length(); ++i) {
                JSONObject outcomeDataJson = outcomeDataJsonArray.getJSONObject(i);
                outcomeDataList.add(RewardProgramOutcomeDataDeserializer.deserialize(outcomeDataJson));
            }
        } else {
            outcomeDataList = null;
        }
        if ((templateDataJSON = jsonObject.optJSONObject(FIELD_TEMPLATE_DATA)) != null && templateDataJSON.length() > 0) {
            templateDataMap = new HashMap<String, String>();
            Iterator jsonKeys = templateDataJSON.keys();
            while (jsonKeys.hasNext()) {
                String jsonKey = (String)jsonKeys.next();
                templateDataMap.put(jsonKey, templateDataJSON.getString(jsonKey));
            }
        } else {
            templateDataMap = null;
        }
        this.id = id;
        this.origin = origin;
        this.createTs = createTs;
        this.userid = userid;
        this.outcomeDataList = outcomeDataList;
        this.templateData = templateDataMap;
        return this;
    }

    @Deprecated
    protected final UserRewardOutcome fromJSONObject_1_1(JSONObject jsonObject) throws JSONException {
        HashMap<String, String> templateDataMap;
        JSONObject templateDataJSON;
        ArrayList<RewardProgramOutcomeData> outcomeDataList;
        String id = jsonObject.getString(FIELD_ID);
        String origin = jsonObject.getString(FIELD_ORIGIN);
        long createTs = jsonObject.getLong(FIELD_CREATE_TS);
        int userid = jsonObject.getInt(FIELD_USERID);
        JSONArray outcomeDataJsonArray = jsonObject.optJSONArray(FIELD_OUTCOME_DATA_LIST);
        if (outcomeDataJsonArray != null && outcomeDataJsonArray.length() != 0) {
            outcomeDataList = new ArrayList<RewardProgramOutcomeData>(outcomeDataJsonArray.length());
            for (int i = 0; i < outcomeDataJsonArray.length(); ++i) {
                JSONObject outcomeDataJson = outcomeDataJsonArray.getJSONObject(i);
                outcomeDataList.add(RewardProgramOutcomeDataDeserializer.deserialize(outcomeDataJson));
            }
        } else {
            outcomeDataList = null;
        }
        if ((templateDataJSON = jsonObject.optJSONObject(FIELD_TEMPLATE_DATA)) != null && templateDataJSON.length() > 0) {
            templateDataMap = new HashMap<String, String>();
            Iterator jsonKeys = templateDataJSON.keys();
            while (jsonKeys.hasNext()) {
                String jsonKey = (String)jsonKeys.next();
                templateDataMap.put(jsonKey, templateDataJSON.getString(jsonKey));
            }
        } else {
            templateDataMap = null;
        }
        this.id = id;
        this.origin = origin;
        this.createTs = createTs;
        this.userid = userid;
        this.outcomeDataList = outcomeDataList;
        this.templateData = templateDataMap;
        return this;
    }

    @Deprecated
    protected final UserRewardOutcome fromJSONObject_1_0(JSONObject jsonObject) throws JSONException {
        HashMap<String, String> templateDataMap;
        JSONObject templateDataJSON;
        ArrayList<RewardProgramOutcomeData> outcomeDataList;
        int userid = jsonObject.getInt(FIELD_USERID);
        JSONArray outcomeDataJsonArray = jsonObject.optJSONArray(FIELD_OUTCOME_DATA_LIST);
        if (outcomeDataJsonArray != null && outcomeDataJsonArray.length() != 0) {
            outcomeDataList = new ArrayList<RewardProgramOutcomeData>(outcomeDataJsonArray.length());
            for (int i = 0; i < outcomeDataJsonArray.length(); ++i) {
                JSONObject outcomeDataJson = outcomeDataJsonArray.getJSONObject(i);
                outcomeDataList.add(RewardProgramOutcomeDataDeserializer.deserialize(outcomeDataJson));
            }
        } else {
            outcomeDataList = null;
        }
        if ((templateDataJSON = jsonObject.optJSONObject(FIELD_TEMPLATE_DATA)) != null && templateDataJSON.length() > 0) {
            templateDataMap = new HashMap<String, String>();
            Iterator jsonKeys = templateDataJSON.keys();
            while (jsonKeys.hasNext()) {
                String jsonKey = (String)jsonKeys.next();
                templateDataMap.put(jsonKey, templateDataJSON.getString(jsonKey));
            }
        } else {
            templateDataMap = null;
        }
        this.userid = userid;
        this.outcomeDataList = outcomeDataList;
        this.templateData = templateDataMap;
        return this;
    }

    public String getMetadataVersion() {
        return DATA_FORMAT_VERSION_1_2;
    }

    public Timestamp getCreateTimestamp() {
        return new Timestamp(this.createTs);
    }

    public int getOutcomeType() {
        return this.outcomeType;
    }

    public void setOutcomeType(int outcomeType) {
        this.outcomeType = outcomeType;
    }
}

