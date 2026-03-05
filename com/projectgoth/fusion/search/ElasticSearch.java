/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.search;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.search.VersionConflictException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class ElasticSearch {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ElasticSearch.class));
    private static final String PROPERTIES_FILENAME = "elasticsearch.properties";
    private static Properties properties;
    private static String elasticSearchURL;

    public static JSONObject index(IndexType indexType, DocumentType documentType, int documentID, int version, JSONObject document) throws VersionConflictException, Exception {
        String path = indexType.toString() + '/' + (Object)((Object)documentType) + '/' + documentID;
        path = version == -1 ? path + "?op_type=create" : path + "?version=" + version;
        String response = ElasticSearch.doHTTPRequest("PUT", path, document.toString());
        JSONObject responseJSON = new JSONObject(response);
        if (responseJSON.has("error")) {
            throw new Exception(responseJSON.getString("error"));
        }
        return responseJSON;
    }

    public static JSONObject get(IndexType indexType, DocumentType documentType, int documentID) throws Exception {
        String path = indexType.toString() + '/' + (Object)((Object)documentType) + '/' + documentID;
        String response = ElasticSearch.doHTTPRequest("GET", path, null);
        if (response == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    public static JSONObject delete(IndexType indexType, DocumentType documentType, int documentID) throws Exception {
        String path = indexType.toString() + '/' + (Object)((Object)documentType) + '/' + documentID;
        String response = ElasticSearch.doHTTPRequest("DELETE", path, null);
        if (response == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    public static JSONObject putMapping(IndexType[] indexTypes, DocumentType documentType, JSONObject mapping) throws Exception {
        String indexes = null;
        if (indexTypes == null) {
            indexes = "_all";
        } else {
            for (int i = 0; i < indexTypes.length; ++i) {
                if (i > 0) {
                    indexes = indexes + ",";
                }
                indexes = indexes + (Object)((Object)indexTypes[i]);
            }
        }
        String path = indexes + '/' + (Object)((Object)documentType) + "/_mapping";
        String response = ElasticSearch.doHTTPRequest("PUT", path, mapping.toString());
        if (response == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    public static JSONObject getMapping(IndexType[] indexTypes, DocumentType[] documentTypes) throws Exception {
        String response;
        String indexes = null;
        if (indexTypes == null) {
            indexes = "_all";
        } else {
            for (int i = 0; i < indexTypes.length; ++i) {
                if (i > 0) {
                    indexes = indexes + ",";
                }
                indexes = indexes + (Object)((Object)indexTypes[i]);
            }
        }
        String documents = null;
        if (documentTypes != null) {
            for (int i = 0; i < documentTypes.length; ++i) {
                if (i > 0) {
                    documents = documents + ",";
                }
                documents = documents + (Object)((Object)indexTypes[i]);
            }
        }
        String path = indexes + '/';
        if (documentTypes != null) {
            path = path + documents + '/';
        }
        if ((response = ElasticSearch.doHTTPRequest("GET", path = path + "_mapping", null)) == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    public static JSONObject search(IndexType[] indexTypes, DocumentType[] documentTypes, JSONObject query) throws Exception {
        String response;
        String indexes = "";
        if (indexTypes == null) {
            indexes = "_all";
        } else {
            for (int i = 0; i < indexTypes.length; ++i) {
                if (i > 0) {
                    indexes = indexes + ",";
                }
                indexes = indexes + (Object)((Object)indexTypes[i]);
            }
        }
        String documents = "";
        if (documentTypes != null) {
            for (int i = 0; i < documentTypes.length; ++i) {
                if (i > 0) {
                    documents = documents + ",";
                }
                documents = documents + (Object)((Object)documentTypes[i]);
            }
        }
        String path = indexes + '/';
        if (documentTypes != null) {
            path = path + documents + '/';
        }
        if ((response = ElasticSearch.doHTTPRequest("GET", path = path + "_search", query.toString())) == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    public static JSONObject count(IndexType[] indexTypes, DocumentType[] documentTypes, JSONObject query) throws Exception {
        String response;
        String indexes = null;
        if (indexTypes == null) {
            indexes = "_all";
        } else {
            for (int i = 0; i < indexTypes.length; ++i) {
                if (i > 0) {
                    indexes = indexes + ",";
                }
                indexes = indexes + (Object)((Object)indexTypes[i]);
            }
        }
        String documents = null;
        if (documentTypes != null) {
            for (int i = 0; i < documentTypes.length; ++i) {
                if (i > 0) {
                    documents = documents + ",";
                }
                documents = documents + (Object)((Object)indexTypes[i]);
            }
        }
        String path = indexes + '/';
        if (documentTypes != null) {
            path = path + documents + '/';
        }
        if ((response = ElasticSearch.doHTTPRequest("GET", path = path + "_count", query.toString())) == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    public static JSONObject status() throws Exception {
        String response = ElasticSearch.doHTTPRequest("GET", "_status", null);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    public static JSONObject refresh(IndexType indexType) throws Exception {
        String response = ElasticSearch.doHTTPRequest("PUT", indexType.toString() + "/_refresh", null);
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {
            throw new Exception(jsonObject.getString("error"));
        }
        return jsonObject;
    }

    private static synchronized void loadProperties() throws Exception {
        properties = new Properties();
        String propertiesLocation = ConfigUtils.getConfigDirectory() + PROPERTIES_FILENAME;
        log.info((Object)("Loading ElasticSearch configuration file " + propertiesLocation));
        try {
            FileInputStream inputStream = new FileInputStream(new File(propertiesLocation));
            properties.load(inputStream);
            elasticSearchURL = properties.getProperty("elasticSearchURL");
            if (elasticSearchURL == null) {
                throw new Exception("elasticSearchURL not specified");
            }
            if (!elasticSearchURL.endsWith("/")) {
                elasticSearchURL = elasticSearchURL + "/";
            }
            log.info((Object)("ElasticSearch URL: " + elasticSearchURL));
            log.info((Object)"ElasticSearch configuration successfully loaded");
        }
        catch (Exception e) {
            log.error((Object)("Unable to load ElasticSearch configuration file: " + e.getMessage()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String doHTTPRequest(String method, String path, String body) throws VersionConflictException, Exception {
        String string;
        String url = elasticSearchURL + path;
        HttpURLConnection httpConn = (HttpURLConnection)new URL(url).openConnection();
        httpConn.setUseCaches(false);
        httpConn.setRequestMethod(method);
        if (body != null && body.length() > 0) {
            httpConn.setDoOutput(true);
            httpConn.getOutputStream().write(body.getBytes("UTF-8"));
        }
        if (httpConn.getResponseCode() == 404) {
            return null;
        }
        if (httpConn.getResponseCode() == 409) {
            throw new VersionConflictException();
        }
        if (httpConn.getResponseCode() < 200 && httpConn.getResponseCode() >= 300) {
            throw new Exception("HTTP " + httpConn.getResponseCode() + " " + httpConn.getResponseMessage());
        }
        BufferedReader reader = null;
        try {
            String line;
            reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            string = response.toString();
            Object var10_9 = null;
        }
        catch (Throwable throwable) {
            Object var10_10 = null;
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {}
            throw throwable;
        }
        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (IOException e) {
            // empty catch block
        }
        return string;
    }

    static {
        try {
            ElasticSearch.loadProperties();
        }
        catch (Exception e) {
            log.error((Object)e.getMessage());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DocumentType {
        CHAT_ROOM("chat_room"),
        GROUP("group"),
        PROFILE("profile");

        private String value;

        private DocumentType(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum IndexType {
        CHAT_ROOMS("chat_rooms"),
        GROUPS("groups"),
        PROFILES("profiles");

        private String value;

        private IndexType(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
}

