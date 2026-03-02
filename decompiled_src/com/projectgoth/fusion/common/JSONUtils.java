/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.common;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
    public static boolean hasNonNullProperty(JSONObject jsonObj, String key) {
        return jsonObj.has(key) && !jsonObj.isNull(key);
    }

    public static String getString(JSONObject jsonObj, String key) throws JSONException {
        if (jsonObj.has(key)) {
            return jsonObj.getString(key);
        }
        return null;
    }

    public static Integer getInteger(JSONObject jsonObj, String key) throws JSONException {
        if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            return jsonObj.getInt(key);
        }
        return null;
    }

    public static int getInteger(JSONObject jsonObj, String key, int defaultValue) throws JSONException {
        Integer val = JSONUtils.getInteger(jsonObj, key);
        return val == null ? defaultValue : val;
    }

    public static Double getDouble(JSONObject jsonObj, String key) throws JSONException {
        if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            return jsonObj.getDouble(key);
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONObject jsonObj, String key) throws JSONException {
        if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            return jsonObj.getJSONObject(key);
        }
        return null;
    }

    public static double getDouble(JSONObject jsonObj, String key, double defaultValue) throws JSONException {
        Double val = JSONUtils.getDouble(jsonObj, key);
        return val == null ? defaultValue : val;
    }

    public static Long getLong(JSONObject jsonObj, String key) throws JSONException {
        if (jsonObj.has(key) && !jsonObj.isNull(key)) {
            return jsonObj.getLong(key);
        }
        return null;
    }
}

