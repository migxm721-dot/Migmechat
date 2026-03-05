/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.data;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface JSONSerializable<T> {
    public JSONObject toJSONObject() throws JSONException;

    public T fromJSONObject(JSONObject var1) throws JSONException;
}

