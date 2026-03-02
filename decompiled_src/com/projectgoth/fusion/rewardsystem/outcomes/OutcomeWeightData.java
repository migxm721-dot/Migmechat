/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.data.JSONSerializable;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OutcomeWeightData
implements JSONSerializable<OutcomeWeightData> {
    private String outcome;
    private int weight;

    OutcomeWeightData() {
    }

    public OutcomeWeightData(String outcome, int weight) {
        this.outcome = outcome;
        this.weight = weight;
    }

    public String getOutcome() {
        return this.outcome;
    }

    public int getWeight() {
        return this.weight;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(this.outcome, this.weight);
        return jsonObject;
    }

    @Override
    public OutcomeWeightData fromJSONObject(JSONObject jsonObject) throws JSONException {
        Iterator iter = jsonObject.keys();
        if (iter.hasNext()) {
            String outcome = iter.next().toString();
            int weight = jsonObject.optInt(outcome, 0);
            this.outcome = outcome;
            this.weight = weight;
            return this;
        }
        return this;
    }
}

