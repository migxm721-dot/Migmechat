/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.data;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RSSFeedForUser
implements Comparable<RSSFeedForUser> {
    private static final Logger log = Logger.getLogger(RSSFeedForUser.class);
    public static final String CURRENT_JSON_SCHEMA_VERSION = "1.0";
    private static final String RSSFEED_ID_FORMAT = "%d-%s";
    public int userId;
    public URL url;
    public String id;
    public String _version;
    public String description;

    public RSSFeedForUser() {
    }

    public RSSFeedForUser(int userId, String url) throws MalformedURLException {
        this._version = CURRENT_JSON_SCHEMA_VERSION;
        this.url = new URL(url);
        this.userId = userId;
        this.id = this.getID();
        this.description = "";
    }

    private String getID() {
        return String.format(RSSFEED_ID_FORMAT, this.userId, DigestUtils.md5Hex((String)this.url.toString()).toString());
    }

    public String toJSONString() {
        return this.toJSONObject().toString();
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", (Object)this.id);
            obj.put("_version", (Object)this._version);
            obj.put("url", (Object)this.url.toString());
            obj.put("userId", this.userId);
            obj.put("description", (Object)this.description);
        }
        catch (JSONException e) {
            log.error((Object)("Unable to convert RSSFeedForUser object to JSON " + e.getMessage()));
        }
        return obj;
    }

    @Override
    public int compareTo(RSSFeedForUser other) {
        return this.id.compareTo(other.id);
    }
}

