/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.eventqueue;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.eventqueue.EventFactory;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Event {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Event.class));
    public String eventSubject;
    public Long timestamp;
    public Enums.EventTypeEnum type;
    public HashMap<String, String> params;
    private static final String PARAMS_KEY = "params";
    private static final String TYPE_KEY = "type";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String SUBJECT_KEY = "subject";
    private boolean isBeingProcessed = false;

    protected Event(Enums.EventTypeEnum type) {
        this.eventSubject = "";
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.params = new HashMap();
    }

    protected Event(String eventSubject, Enums.EventTypeEnum type) {
        this.eventSubject = eventSubject;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.params = new HashMap();
    }

    public void putParameter(String key, String value) {
        this.params.put(key, value);
    }

    public String getParameter(String key) {
        return this.params.get(key);
    }

    public String toJSONString() {
        return this.toJSONString(false);
    }

    public String toJSONString(boolean pretty) {
        String jsonStr = "";
        try {
            JSONObject o = new JSONObject();
            o.put(SUBJECT_KEY, (Object)this.eventSubject);
            o.put(TIMESTAMP_KEY, (Object)this.timestamp);
            o.put(TYPE_KEY, this.type.value);
            JSONObject p = new JSONObject();
            for (Map.Entry<String, String> entry : this.params.entrySet()) {
                p.put(entry.getKey(), (Object)entry.getValue());
            }
            o.put(PARAMS_KEY, (Object)p);
            jsonStr = !pretty ? o.toString() : o.toString(4);
        }
        catch (JSONException e) {
            log.error((Object)"Unable to serialize event to JSON String", (Throwable)e);
        }
        return jsonStr;
    }

    public static Event fromJSONString(String jsonString) {
        Event e = null;
        try {
            JSONObject o = new JSONObject(jsonString);
            String subject = o.getString(SUBJECT_KEY);
            Long timestamp = o.getLong(TIMESTAMP_KEY);
            Enums.EventTypeEnum type = Enums.EventTypeEnum.fromValue(o.getInt(TYPE_KEY));
            JSONObject p = o.getJSONObject(PARAMS_KEY);
            Iterator keys = p.keys();
            HashMap<String, String> params = new HashMap<String, String>();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                params.put(key, p.getString(key));
            }
            e = EventFactory.createEvent(type);
            if (e != null) {
                e.eventSubject = subject;
                e.timestamp = timestamp;
                e.params = params;
            }
        }
        catch (JSONException ex) {
            log.error((Object)String.format("Error parsing JSON String [%s] [%s]", jsonString, ex.getMessage()), (Throwable)ex);
        }
        return e;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean execute() {
        boolean bl;
        log.info((Object)String.format("Executing event for username [%s]", this.eventSubject));
        try {
            this.isBeingProcessed = true;
            bl = this.executeInternal();
            Object var4_3 = null;
        }
        catch (Exception e) {
            boolean bl2;
            try {
                log.error((Object)String.format("Failed to submit event [%s] for [%s].", this.type.description, this.eventSubject), (Throwable)e);
                bl2 = false;
                Object var4_4 = null;
            }
            catch (Throwable throwable) {
                Object var4_5 = null;
                log.info((Object)String.format("Execution of event for username [%s] completed.", this.eventSubject));
                this.isBeingProcessed = false;
                throw throwable;
            }
            log.info((Object)String.format("Execution of event for username [%s] completed.", this.eventSubject));
            this.isBeingProcessed = false;
            return bl2;
        }
        log.info((Object)String.format("Execution of event for username [%s] completed.", this.eventSubject));
        this.isBeingProcessed = false;
        return bl;
    }

    public boolean executeInternal() throws Exception {
        log.info((Object)String.format("Looking up userid for username [%s]", this.eventSubject));
        User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
        int userid = userBean.getUserID(this.eventSubject, null, false);
        if (userid == -1) {
            log.error((Object)String.format("Unable to execute event: UserID not found for [%s]", this.eventSubject));
            return false;
        }
        log.info((Object)String.format("Found userid [%d] for username[%s]", userid, this.eventSubject));
        String jsonStr = this.toJSONString();
        String pathPrefix = String.format("/user/%d/activity", userid);
        log.info((Object)("Making API call to PUT " + pathPrefix));
        MigboApiUtil apiUtil = MigboApiUtil.getInstance();
        JSONObject response = apiUtil.put(pathPrefix, jsonStr);
        log.info((Object)String.format("Activity [%s] [%d-%d-%d] for [%s] submitted successfully: response : %s", this.type.description, userid, this.type.value, this.timestamp, this.eventSubject, response.toString()));
        return true;
    }

    public boolean isBeingProcessed() {
        return this.isBeingProcessed;
    }

    public boolean equals(Event o) {
        if (!(o.getClass().getName().equals(this.getClass().getName()) && o.eventSubject.equals(this.eventSubject) && o.timestamp.equals(this.timestamp))) {
            return false;
        }
        for (String key : this.params.keySet()) {
            if (this.params.get(key).equals(o.getParameter(key))) continue;
            return false;
        }
        return true;
    }

    public boolean retryOnFailure() {
        return false;
    }
}

