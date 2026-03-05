/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.rewardsystem.outcomes;

import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnlockedStoreItemsRewardProgramOutcomeData
extends RewardProgramOutcomeData {
    private static final String CURRENT_DATA_FORMAT_VERSION = "1.0";
    private static final String FIELD_UNLOCKED_STORE_ITEMS = "ulstoreitems";
    private Map<Integer, Integer> storeItemIDToCountsMap;

    public UnlockedStoreItemsRewardProgramOutcomeData(Map<Integer, Integer> storeItemIDToCountsMap) {
        this();
        this.storeItemIDToCountsMap = storeItemIDToCountsMap;
    }

    public UnlockedStoreItemsRewardProgramOutcomeData() {
        super(RewardProgramOutcomeData.TypeEnum.UNLOCKED_STORE_ITEMS);
    }

    @Override
    protected String currentDataFormatVersion() {
        return CURRENT_DATA_FORMAT_VERSION;
    }

    @Override
    protected void serializeToJSONObject(JSONObject jsonObject) throws JSONException {
        if (this.storeItemIDToCountsMap != null && !this.storeItemIDToCountsMap.isEmpty()) {
            jsonObject.put(FIELD_UNLOCKED_STORE_ITEMS, (Object)new JSONObject(this.storeItemIDToCountsMap));
        }
    }

    @Override
    protected void deserializeFromJSONObject(JSONObject jsonObject) throws JSONException {
        JSONObject storeItemIDToCountsMapJSON = (JSONObject)jsonObject.opt(FIELD_UNLOCKED_STORE_ITEMS);
        HashMap<Integer, Integer> storeItemIDToCountsMap = new HashMap<Integer, Integer>(jsonObject.length());
        Iterator keyIterator = storeItemIDToCountsMapJSON.keys();
        while (keyIterator.hasNext()) {
            String keyStr = (String)keyIterator.next();
            int value = storeItemIDToCountsMapJSON.getInt(keyStr);
            if (value == -1) continue;
            storeItemIDToCountsMap.put(Integer.parseInt(keyStr), value);
        }
        this.storeItemIDToCountsMap = storeItemIDToCountsMap;
    }

    public Map<Integer, Integer> getUnlockedStoreItems() {
        return this.storeItemIDToCountsMap;
    }

    @Override
    public boolean requiresTemplateData() {
        return false;
    }
}

