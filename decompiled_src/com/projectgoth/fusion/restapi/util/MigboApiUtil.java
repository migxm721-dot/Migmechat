/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpException
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpResponseInterceptor
 *  org.apache.http.ParseException
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.conn.ClientConnectionManager
 *  org.apache.http.conn.ConnectionKeepAliveStrategy
 *  org.apache.http.conn.params.ConnManagerParams
 *  org.apache.http.conn.params.ConnPerRoute
 *  org.apache.http.conn.params.ConnPerRouteBean
 *  org.apache.http.conn.scheme.PlainSocketFactory
 *  org.apache.http.conn.scheme.Scheme
 *  org.apache.http.conn.scheme.SchemeRegistry
 *  org.apache.http.conn.scheme.SocketFactory
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
 *  org.apache.http.message.BasicHeaderElementIterator
 *  org.apache.http.params.BasicHttpParams
 *  org.apache.http.params.HttpConnectionParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.EntityUtils
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.util;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.TrustModifier;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import com.projectgoth.fusion.restapi.util.GzipDecompressingEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class MigboApiUtil {
    private static final String APPLICATION_VND_MIG33API_V1_JSON = "application/vnd.mig33api-v1+json;charset=UTF-8";
    private static final String DEFAULT_CHARSET = "UTF-8";
    public static final String MIGBO_SEND_EMAIL_URL = "/user/%s/email/%s?follower=%s";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MigboApiUtil.class));
    public static final String JSONKEY_ERROR = "error";
    public static final String JSONKEY_ERRNO = "errno";
    public static final String JSONKEY_ERRORMESSAGE = "message";
    public static final String JSONKEY_DATA = "data";
    private String migboDatasvcHost;
    private int migboDatasvcPort;
    private String migboDatasvcPathPrefix;
    private boolean migboDatasvcUseHttps;
    private boolean migboDatasvcUseGzipCompression;
    private int maxTotalConnectionsToMigbo;
    private long timeOutInMillis;
    private int keepAliveInSeconds;
    private String protocol;
    private HttpHost hcHost;
    DefaultHttpClient httpclient;
    private static MigboApiUtil theInstance = new MigboApiUtil();
    private static MigboApiUtil theInstanceForValhalla = null;
    private static final Pattern API_PATH_USER_ID = Pattern.compile("/user/([0-9]+)/.*");

    private MigboApiUtil() {
        this(false);
    }

    private MigboApiUtil(boolean useValhallaMigboDatasvc) {
        Properties props = null;
        String propFile = System.getProperty("fusion.datasvc.config", "/usr/fusion/etc/datasvc.properties");
        log.info((Object)String.format("Attempting to load from %s", propFile));
        File f = new File(propFile);
        if (f.exists()) {
            props = new Properties();
            try {
                log.info((Object)String.format("Loading config from file %s", propFile));
                props.load(new FileInputStream(f));
            }
            catch (FileNotFoundException e) {
                log.error((Object)String.format("Unable to find config file from %s", propFile));
                props = null;
            }
            catch (IOException e) {
                log.error((Object)String.format("Unable to load config from %s", propFile), (Throwable)e);
                props = null;
            }
        } else {
            log.info((Object)String.format("Config file %s not found", propFile));
        }
        if (props == null) {
            log.info((Object)String.format("Loading config from System.properties", new Object[0]));
            props = System.getProperties();
        }
        this.migboDatasvcHost = props.getProperty("migbo.datasvc.host", "localhost");
        this.migboDatasvcPort = Integer.parseInt(props.getProperty("migbo.datasvc.port", "80"));
        this.migboDatasvcPathPrefix = props.getProperty("migbo.datasvc.pathPrefix", "migbo_datasvc");
        this.migboDatasvcUseHttps = Boolean.parseBoolean(props.getProperty("migbo.datasvc.useHttps", "false"));
        this.migboDatasvcUseGzipCompression = Boolean.parseBoolean(props.getProperty("migbo.datasvc.useGzipCompression", "false"));
        if (useValhallaMigboDatasvc) {
            this.migboDatasvcHost = SystemProperty.get(SystemPropertyEntities.Migbo.VALHALLA_MIGBO_DATASVC_HOST);
            this.migboDatasvcPort = SystemProperty.getInt(SystemPropertyEntities.Migbo.VALHALLA_MIGBO_DATASVC_PORT);
            this.migboDatasvcUseHttps = SystemProperty.getBool(SystemPropertyEntities.Migbo.VALHALLA_MIGBO_DATASVC_USEHTTPS);
        }
        this.maxTotalConnectionsToMigbo = Integer.parseInt(props.getProperty("migbo.datasvc.maxconn", "200"));
        this.timeOutInMillis = Long.parseLong(props.getProperty("migbo.datasvc.conn_timeout_millis", "5000"));
        this.keepAliveInSeconds = Integer.parseInt(props.getProperty("migbo.datasvc.keepalive_seconds", "60"));
        this.protocol = this.migboDatasvcUseHttps ? "https" : "http";
        this.hcHost = new HttpHost(this.migboDatasvcHost, this.migboDatasvcPort, this.protocol);
        log.info((Object)String.format("api url is %s", this.hcHost.toString()));
    }

    public static MigboApiUtil getInstance() {
        return theInstance;
    }

    public static synchronized void resetMigboApiUtil() {
        if (theInstance == null) {
            return;
        }
        if (MigboApiUtil.theInstance.httpclient != null) {
            MigboApiUtil.theInstance.httpclient.getConnectionManager().shutdown();
            MigboApiUtil.theInstance.httpclient = null;
        }
        theInstance = new MigboApiUtil();
    }

    private static synchronized MigboApiUtil getInstanceForValhalla() {
        if (theInstanceForValhalla == null) {
            theInstanceForValhalla = new MigboApiUtil(true);
        }
        return theInstanceForValhalla;
    }

    public static synchronized void resetMigboApiUtilForValhalla() {
        if (theInstanceForValhalla == null) {
            return;
        }
        if (MigboApiUtil.theInstanceForValhalla.httpclient != null) {
            MigboApiUtil.theInstanceForValhalla.httpclient.getConnectionManager().shutdown();
            MigboApiUtil.theInstanceForValhalla.httpclient = null;
        }
    }

    private HttpClient getHttpClient() throws MigboApiException {
        if (this.httpclient == null) {
            this.createHttpClient();
        }
        if (this.httpclient == null) {
            String errMsg = "Unable to create httpclient";
            log.error((Object)errMsg);
            throw new MigboApiException(-1, errMsg);
        }
        return this.httpclient;
    }

    private synchronized boolean createHttpClient() {
        if (this.httpclient != null) {
            return true;
        }
        try {
            log.info((Object)"Creating http client");
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", (SocketFactory)PlainSocketFactory.getSocketFactory(), 80));
            BasicHttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout((HttpParams)params, (int)((int)this.timeOutInMillis));
            HttpConnectionParams.setSoTimeout((HttpParams)params, (int)((int)this.timeOutInMillis));
            ConnManagerParams.setMaxTotalConnections((HttpParams)params, (int)this.maxTotalConnectionsToMigbo);
            ConnManagerParams.setTimeout((HttpParams)params, (long)this.timeOutInMillis);
            ConnManagerParams.setMaxConnectionsPerRoute((HttpParams)params, (ConnPerRoute)new ConnPerRouteBean(this.maxTotalConnectionsToMigbo));
            ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager((HttpParams)params, schemeRegistry);
            DefaultHttpClient client = new DefaultHttpClient((ClientConnectionManager)cm, (HttpParams)params);
            client.setKeepAliveStrategy(this.getKeepAliveStrategy());
            if (this.migboDatasvcUseHttps) {
                try {
                    TrustModifier.relaxHostChecking(client);
                }
                catch (NoSuchAlgorithmException e) {
                    log.error((Object)String.format("Unable to add https support for httpclient", new Object[0]), (Throwable)e);
                    return false;
                }
                catch (KeyManagementException e) {
                    log.error((Object)String.format("Unable to add https support for httpclient", new Object[0]), (Throwable)e);
                    return false;
                }
            }
            if (this.migboDatasvcUseGzipCompression) {
                client.addRequestInterceptor(new HttpRequestInterceptor(){

                    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                        if (!request.containsHeader("Accept-Encoding")) {
                            request.addHeader("Accept-Encoding", "gzip");
                        }
                    }
                });
                client.addResponseInterceptor(new HttpResponseInterceptor(){

                    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                        Header ceheader;
                        HttpEntity entity = response.getEntity();
                        if (entity != null && (ceheader = entity.getContentEncoding()) != null) {
                            HeaderElement[] codecs = ceheader.getElements();
                            for (int i = 0; i < codecs.length; ++i) {
                                if (!codecs[i].getName().equalsIgnoreCase("gzip")) continue;
                                response.setEntity((HttpEntity)new GzipDecompressingEntity(response.getEntity()));
                                return;
                            }
                        }
                    }
                });
            }
            this.httpclient = client;
            log.info((Object)"Http client created");
            return true;
        }
        catch (Exception e) {
            log.error((Object)("Unexpected Exception while attempting to create new HTTP Client :" + e.getMessage()), (Throwable)e);
            return false;
        }
    }

    private boolean isValhallaApiPath(String path, String method) {
        Matcher m;
        if (this != theInstanceForValhalla && SystemProperty.getBool(new SystemPropertyEntities.GuardsetEnabled(GuardCapabilityEnum.VALHALLA_TEST)) && (m = API_PATH_USER_ID.matcher(path)).matches()) {
            try {
                int userId = Integer.parseInt(m.group(1));
                User userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                return userEJB.isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.VALHALLA_TEST.value());
            }
            catch (Exception e) {
                log.error((Object)("Failed to check valhalla data svc api for path " + path + ", method " + method), (Throwable)e);
            }
        }
        return false;
    }

    private JSONObject createMigboDisabledResponse(String method, String pathPrefix, String postData) {
        JSONObject root = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put(JSONKEY_ERRORMESSAGE, (Object)"migbo is disabled");
            data.put("method", (Object)method);
            data.put("pathPrefix", (Object)pathPrefix);
            data.put("postData", (Object)postData);
            root.put(JSONKEY_DATA, (Object)data);
        }
        catch (JSONException e) {
            // empty catch block
        }
        return root;
    }

    public JSONObject post(String pathPrefix, String postData) throws MigboApiException {
        if (SystemProperty.getInt(SystemPropertyEntities.Migbo.MIGBO_DISABLED) != 0) {
            log.warn((Object)("Migbo api call is not made because migbo is disabled now. type: POST, pathPrefix: " + pathPrefix + ", postData: " + postData));
            return this.createMigboDisabledResponse("post", pathPrefix, postData);
        }
        if (this.isValhallaApiPath(pathPrefix, "post")) {
            return MigboApiUtil.getInstanceForValhalla().post(pathPrefix, postData);
        }
        HttpClient httpclient = this.getHttpClient();
        String path = this.migboDatasvcPathPrefix + pathPrefix;
        HttpPost httppost = new HttpPost(path);
        httppost.addHeader("Content-Type", APPLICATION_VND_MIG33API_V1_JSON);
        httppost.addHeader("Connection", "close");
        try {
            httppost.setEntity((HttpEntity)new StringEntity(postData, DEFAULT_CHARSET));
        }
        catch (UnsupportedEncodingException e1) {
            String errMsg = String.format("Error in create request to make migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e1);
            throw new MigboApiException(-1, errMsg);
        }
        HttpResponse response = null;
        try {
            log.info((Object)String.format("Making POST migbo data svc api call to %s%s", this.hcHost.toString(), path));
            response = httpclient.execute(this.hcHost, (HttpRequest)httppost);
            log.info((Object)String.format("Received response from migbo data svc api call %s", this.hcHost.toString()));
        }
        catch (ClientProtocolException e) {
            String errMsg = String.format("Error in making migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        catch (IOException e) {
            String errMsg = String.format("IOException in making migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        return this.parseHttpResponse(response);
    }

    public void postOneWay(String pathPrefix, String postData) throws MigboApiException {
        if (SystemProperty.getInt(SystemPropertyEntities.Migbo.MIGBO_DISABLED) != 0) {
            log.warn((Object)("Migbo api call is not made because migbo is disabled now. type: POST oneway, pathPrefix: " + pathPrefix + ", postData: " + postData));
            return;
        }
        if (this.isValhallaApiPath(pathPrefix, "postOneWay")) {
            MigboApiUtil.getInstanceForValhalla().postOneWay(pathPrefix, postData);
            return;
        }
        if (!SystemProperty.getBool(SystemPropertyEntities.Migbo.ONEWAY_MIGBO_API_CALLS_ENABLED)) {
            JSONObject result = this.post(pathPrefix, postData);
            log.info((Object)String.format("One Way Post DISABLE! Make a normal post instead, pathPrefix:%s, postData:%s, return result:%s", pathPrefix, postData, result == null ? "NULL" : result.toString()));
            return;
        }
        HttpClient httpclient = this.getHttpClient();
        StringBuilder sb = new StringBuilder().append(this.migboDatasvcPathPrefix).append(pathPrefix).append(pathPrefix.contains("?") ? "&" : "?").append("oneway=1");
        String path = sb.toString();
        HttpPost httppost = new HttpPost(path);
        httppost.addHeader("Content-Type", APPLICATION_VND_MIG33API_V1_JSON);
        try {
            httppost.setEntity((HttpEntity)new StringEntity(postData, DEFAULT_CHARSET));
        }
        catch (UnsupportedEncodingException e1) {
            String errMsg = String.format("Error in create request to make oneway migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e1);
            throw new MigboApiException(-1, errMsg);
        }
        try {
            log.info((Object)String.format("Making oneway POST migbo data svc api call to %s%s", this.hcHost.toString(), path));
            this.consumeHttpResponse(httpclient.execute(this.hcHost, (HttpRequest)httppost));
        }
        catch (ClientProtocolException e) {
            String errMsg = String.format("Error in making oneway migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        catch (IOException e) {
            String errMsg = String.format("IOException in making oneway migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
    }

    public JSONObject put(String pathPrefix, String putData) throws MigboApiException {
        if (SystemProperty.getInt(SystemPropertyEntities.Migbo.MIGBO_DISABLED) != 0) {
            log.warn((Object)("Migbo api call is not made because migbo is disabled now. type: PUT, pathPrefix: " + pathPrefix + ", postData: " + putData));
            return this.createMigboDisabledResponse("put", pathPrefix, putData);
        }
        if (this.isValhallaApiPath(pathPrefix, "put")) {
            return MigboApiUtil.getInstanceForValhalla().put(pathPrefix, putData);
        }
        HttpClient httpclient = this.getHttpClient();
        String path = this.migboDatasvcPathPrefix + pathPrefix;
        HttpPut httpput = new HttpPut(path);
        httpput.addHeader("Content-Type", APPLICATION_VND_MIG33API_V1_JSON);
        httpput.addHeader("Connection", "close");
        try {
            httpput.setEntity((HttpEntity)new StringEntity(putData, DEFAULT_CHARSET));
        }
        catch (UnsupportedEncodingException e1) {
            String errMsg = String.format("Error in create request to make migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e1);
            throw new MigboApiException(-1, errMsg);
        }
        HttpResponse response = null;
        try {
            log.info((Object)String.format("Making PUT migbo data svc api call to %s%s", this.hcHost.toString(), path));
            response = httpclient.execute(this.hcHost, (HttpRequest)httpput);
            log.info((Object)String.format("Received response from migbo data svc api call %s", this.hcHost.toString()));
        }
        catch (ClientProtocolException e) {
            String errMsg = String.format("Error in making migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        catch (IOException e) {
            String errMsg = String.format("IOException in making migbo data svc api call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        return this.parseHttpResponse(response);
    }

    public JSONObject get(String pathPrefix) throws MigboApiException {
        if (SystemProperty.getInt(SystemPropertyEntities.Migbo.MIGBO_DISABLED) != 0) {
            log.warn((Object)("Migbo api call is not made because migbo is disabled now. type: GET, pathPrefix: " + pathPrefix));
            return this.createMigboDisabledResponse("get", pathPrefix, null);
        }
        if (this.isValhallaApiPath(pathPrefix, "get")) {
            return MigboApiUtil.getInstanceForValhalla().get(pathPrefix);
        }
        HttpClient httpclient = this.getHttpClient();
        String path = this.migboDatasvcPathPrefix + pathPrefix;
        HttpGet httpget = new HttpGet(path);
        httpget.addHeader("Content-Type", APPLICATION_VND_MIG33API_V1_JSON);
        httpget.addHeader("Connection", "close");
        HttpResponse response = null;
        try {
            log.info((Object)String.format("Making GET migbo data svc api call to %s%s", this.hcHost.toString(), path));
            response = httpclient.execute(this.hcHost, (HttpRequest)httpget);
            log.info((Object)String.format("Received response from migbo data svc api call %s%s", this.hcHost.toString(), path));
        }
        catch (ClientProtocolException e) {
            String errMsg = String.format("Error in making migbo data svc api call to %s%s", this.hcHost.toString(), path);
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        catch (IOException e) {
            String errMsg = String.format("IOException in making migbo data svc api call to %s%s", this.hcHost.toString(), path);
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        return this.parseHttpResponse(response);
    }

    public JSONObject delete(String pathPrefix) throws MigboApiException {
        if (SystemProperty.getInt(SystemPropertyEntities.Migbo.MIGBO_DISABLED) != 0) {
            log.warn((Object)("Migbo api call is not made because migbo is disabled now. type: DELETE, pathPrefix: " + pathPrefix));
            return this.createMigboDisabledResponse("delete", pathPrefix, null);
        }
        if (this.isValhallaApiPath(pathPrefix, "delete")) {
            return MigboApiUtil.getInstanceForValhalla().delete(pathPrefix);
        }
        HttpClient httpclient = this.getHttpClient();
        String path = this.migboDatasvcPathPrefix + pathPrefix;
        HttpDelete httpdel = new HttpDelete(path);
        httpdel.addHeader("Content-Type", APPLICATION_VND_MIG33API_V1_JSON);
        httpdel.addHeader("Connection", "close");
        HttpResponse response = null;
        try {
            log.info((Object)String.format("Making DELETE migbo data svc api call to %s%s", this.hcHost.toString(), path));
            response = httpclient.execute(this.hcHost, (HttpRequest)httpdel);
            log.info((Object)String.format("Received response from migbo data svc api call %s%s", this.hcHost.toString(), path));
        }
        catch (ClientProtocolException e) {
            String errMsg = String.format("Error in making migbo data svc api call to %s%s", this.hcHost.toString(), path);
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        catch (IOException e) {
            String errMsg = String.format("IOException in making migbo data svc api call to %s%s", this.hcHost.toString(), path);
            log.error((Object)errMsg, (Throwable)e);
            throw new MigboApiException(-1, errMsg);
        }
        return this.parseHttpResponse(response);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private JSONObject parseHttpResponse(HttpResponse response) throws MigboApiException {
        JSONObject errMsg322;
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            log.error((Object)String.format("null entity in making migbo data svc api call to %s", this.hcHost.toString()));
            throw new MigboApiException(-1, "null entity");
        }
        try {
            try {
                String res = EntityUtils.toString((HttpEntity)entity);
                try {
                    JSONObject root = new JSONObject(res);
                    if (!root.isNull(JSONKEY_ERROR)) {
                        JSONObject error = root.getJSONObject(JSONKEY_ERROR);
                        int errno = error.getInt(JSONKEY_ERRNO);
                        String errmessage = error.getString(JSONKEY_ERRORMESSAGE);
                        String errMsg2 = String.format("Error returned by migbo data svc api call, api %s, %d:%s", this.hcHost.toString(), errno, errmessage);
                        log.error((Object)errMsg2);
                        throw new MigboApiException(errno, errmessage);
                    }
                    if (root.isNull(JSONKEY_DATA)) {
                        String errMsg322 = String.format("Result returned by migbo data svc api call contains neither error nor data, api %s, %s", this.hcHost.toString(), res);
                        log.error((Object)errMsg322);
                        throw new MigboApiException(-1, errMsg322);
                    }
                    errMsg322 = root;
                }
                catch (Exception e) {
                    String errMsg4 = String.format("Unable to parse migbo data svc api call result into JSON array, api %s, %s", this.hcHost.toString(), e.getMessage());
                    log.error((Object)errMsg4, (Throwable)e);
                    throw new MigboApiException(-1, e.getMessage());
                }
                Object var10_17 = null;
                if (entity == null) return errMsg322;
            }
            catch (ParseException e2) {
                String errMsg5 = String.format("Unable to parse migbo data svc api call result, api %s", this.hcHost.toString());
                log.error((Object)errMsg5, (Throwable)e2);
                throw new MigboApiException(-1, errMsg5);
            }
            catch (IOException e3) {
                String errMsg6 = String.format("Unable to parse migbo data svc api call result, api %s", this.hcHost.toString());
                log.error((Object)errMsg6, (Throwable)e3);
                throw new MigboApiException(-1, errMsg6);
            }
        }
        catch (Throwable throwable) {
            Object var10_18 = null;
            if (entity == null) throw throwable;
            try {
                entity.consumeContent();
                throw throwable;
            }
            catch (IOException ioe) {
                log.warn((Object)String.format("IOException caught while trying to clean up http connection resource: %s", ioe.getMessage()));
            }
            throw throwable;
        }
        try {}
        catch (IOException ioe) {
            log.warn((Object)String.format("IOException caught while trying to clean up http connection resource: %s", ioe.getMessage()));
            return errMsg322;
        }
        entity.consumeContent();
        return errMsg322;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String consumeHttpResponse(HttpResponse response) throws MigboApiException {
        String string;
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            log.error((Object)String.format("null entity in making migbo data svc api call to %s", this.hcHost.toString()));
            throw new MigboApiException(-1, "null entity");
        }
        try {
            try {
                string = EntityUtils.toString((HttpEntity)entity);
                Object var6_6 = null;
                if (entity == null) return string;
            }
            catch (ParseException e) {
                String errMsg = String.format("Unable to parse migbo data svc api call result, api %s", this.hcHost.toString());
                log.error((Object)errMsg, (Throwable)e);
                throw new MigboApiException(-1, errMsg);
            }
            catch (IOException e) {
                String errMsg = String.format("Unable to parse migbo data svc api call result, api %s", this.hcHost.toString());
                log.error((Object)errMsg, (Throwable)e);
                throw new MigboApiException(-1, errMsg);
            }
        }
        catch (Throwable throwable) {
            Object var6_7 = null;
            if (entity == null) throw throwable;
            try {
                entity.consumeContent();
                throw throwable;
            }
            catch (IOException ioe) {
                log.warn((Object)String.format("IOException caught while trying to clean up http connection resource: %s", ioe.getMessage()));
            }
            throw throwable;
        }
        try {}
        catch (IOException ioe) {
            log.warn((Object)String.format("IOException caught while trying to clean up http connection resource: %s", ioe.getMessage()));
            return string;
        }
        entity.consumeContent();
        return string;
    }

    public boolean postAndCheckOk(String pathPrefix, String postData) throws MigboApiException {
        JSONObject root = this.post(pathPrefix, postData);
        try {
            return "ok".equalsIgnoreCase(root.getString(JSONKEY_DATA));
        }
        catch (JSONException e) {
            log.error((Object)("JSONException caught while parsing " + root.toString()));
            return false;
        }
    }

    private ConnectionKeepAliveStrategy getKeepAliveStrategy() {
        return new ConnectionKeepAliveStrategy(){

            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value == null || !param.equalsIgnoreCase("timeout")) continue;
                    try {
                        return Long.parseLong(value) * 1000L;
                    }
                    catch (NumberFormatException ignore) {
                    }
                }
                return 1000 * MigboApiUtil.this.keepAliveInSeconds;
            }
        };
    }

    public static class MigboApiException
    extends Exception {
        private static final long serialVersionUID = 40782198099192882L;
        public int errno;

        public MigboApiException(int errno, String errMsg) {
            super(errMsg);
            this.errno = errno;
        }

        public boolean isLocalException() {
            return this.errno <= 100;
        }
    }
}

