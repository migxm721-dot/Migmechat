/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.restapi.data.SSOEnums;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SSOSessionMetrics {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SSOSessionMetrics.class));
    Map<String, Integer> counters;
    Map<String, String> records;
    String sessionID;
    SSOEnums.View view;
    Long lastupdatedTimestampInSec;
    Long sessionStartTimeInSec;

    private SSOSessionMetrics(String sessionID, SSOEnums.View view, Long sessionStartTime) throws Exception {
        this.sessionID = sessionID;
        this.view = view;
        this.sessionStartTimeInSec = sessionStartTime;
        this.counters = new HashMap<String, Integer>();
        this.records = new HashMap<String, String>();
    }

    public static SSOSessionMetrics fromMap(String sessionID, SSOEnums.View view, Long sessionStartTime, Map<String, String> map) throws Exception {
        SSOSessionMetrics metrics = new SSOSessionMetrics(sessionID, view, sessionStartTime);
        String timestampStr = map.get("timestamp");
        try {
            metrics.lastupdatedTimestampInSec = Long.parseLong(timestampStr);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Unable to parse timestamp information [%s]", timestampStr), (Throwable)e);
            metrics.lastupdatedTimestampInSec = new Long(System.currentTimeMillis() / 1000L);
        }
        map.remove("sessionStartTime");
        map.remove("timestamp");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                metrics.counters.put(key, Integer.parseInt(value));
            }
            catch (NumberFormatException nfe) {
                metrics.records.put(key, value);
            }
        }
        return metrics;
    }

    public static SSOSessionMetrics fromJSONString(String sessionID, SSOEnums.View view, String username, Long sessionStartTime, String jsonStrPost) throws Exception {
        JSONObject r;
        log.debug((Object)String.format("Received [%s][%s][%s][%d][%s]", sessionID, view.toString(), username, sessionStartTime, jsonStrPost));
        SSOSessionMetrics metrics = new SSOSessionMetrics(sessionID, view, sessionStartTime);
        JSONObject jsonPost = new JSONObject(jsonStrPost);
        metrics.lastupdatedTimestampInSec = jsonPost.optLong("timestamp", System.currentTimeMillis() / 1000L);
        metrics.counters = new HashMap<String, Integer>();
        metrics.records = new HashMap<String, String>();
        JSONObject c = jsonPost.optJSONObject("counters");
        if (c != null) {
            Iterator it = c.keys();
            while (it.hasNext()) {
                String key = (String)it.next();
                metrics.counters.put(key, new Integer(c.getInt(key)));
            }
        }
        if ((r = jsonPost.optJSONObject("records")) != null) {
            Iterator it = r.keys();
            while (it.hasNext()) {
                String key = (String)it.next();
                metrics.records.put(key, r.getString(key));
            }
        }
        metrics.records.put("username", username);
        return metrics;
    }

    public String toJSONString() {
        JSONObject o = new JSONObject();
        try {
            o.put("sessionID", (Object)this.sessionID);
            o.put("view", (Object)this.view.toString());
            o.put("sessionStartTime", (Object)this.sessionStartTimeInSec);
            o.put("timestamp", (Object)this.lastupdatedTimestampInSec);
            JSONObject c = new JSONObject();
            for (Map.Entry<String, Integer> entry : this.counters.entrySet()) {
                c.put(entry.getKey(), (Object)entry.getValue());
            }
            o.put("counters", (Object)c);
            JSONObject r = new JSONObject();
            for (Map.Entry<String, String> entry : this.records.entrySet()) {
                r.put(entry.getKey(), (Object)entry.getValue());
            }
            o.put("records", (Object)r);
        }
        catch (JSONException jse) {
            log.warn((Object)"Unable to convert to JSON string :", (Throwable)jse);
        }
        return o.toString();
    }

    public Long getSessionStartTime() {
        return this.sessionStartTimeInSec;
    }

    public void setSessionStartTime(Long sessionStartTime) {
        this.sessionStartTimeInSec = sessionStartTime;
    }

    public Long getTimestamp() {
        return this.lastupdatedTimestampInSec;
    }

    public void setTimestamp(Long timestamp) {
        this.lastupdatedTimestampInSec = timestamp;
    }

    public Map<String, Integer> getCounters() {
        return this.counters;
    }

    public void setCounters(Map<String, Integer> counters) {
        this.counters = counters;
    }

    public Map<String, String> getRecords() {
        return this.records;
    }

    public void setRecords(Map<String, String> records) {
        this.records = records;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public SSOEnums.View getView() {
        return this.view;
    }
}

