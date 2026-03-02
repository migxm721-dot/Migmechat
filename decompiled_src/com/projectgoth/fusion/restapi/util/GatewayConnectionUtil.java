/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.ParseException
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.entity.StringEntity
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.util.EntityUtils
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 */
package com.projectgoth.fusion.restapi.util;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

public class GatewayConnectionUtil {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GatewayConnectionUtil.class));
    private String gatewayHttpHost;
    private int gatewayHttpPort;
    private HttpHost hcHost;
    private static GatewayConnectionUtil theInstance;
    private static Object lock;

    private GatewayConnectionUtil() {
        Properties props = null;
        String propFile = System.getProperty("fusion.datasvc.config", "/usr/fusion/etc/datasvc.properties");
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
        this.gatewayHttpHost = props.getProperty("migcore.gatewayhttp.host", "localhost");
        this.gatewayHttpPort = Integer.parseInt(props.getProperty("migcore.gatewayhttp.port", "80"));
        this.hcHost = new HttpHost(this.gatewayHttpHost, this.gatewayHttpPort, "http");
        log.info((Object)String.format("api url is %s", this.hcHost.toString()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static GatewayConnectionUtil getInstance() {
        if (theInstance == null) {
            Object object = lock;
            synchronized (object) {
                if (theInstance == null) {
                    theInstance = new GatewayConnectionUtil();
                }
            }
        }
        return theInstance;
    }

    public FusionPacket[] sendRequest(FusionRequest request) throws GatewayConnectionException {
        return this.sendRequest(request, null);
    }

    public FusionPacket[] sendRequest(FusionRequest request, String sid) throws GatewayConnectionException {
        try {
            return this.sendGatewayRequest(request.toJSON(2), sid, "");
        }
        catch (JSONException je) {
            throw new GatewayConnectionException(-1, je.getMessage());
        }
        catch (GatewayConnectionException ge) {
            throw new GatewayConnectionException(-1, ge.getMessage());
        }
    }

    public FusionPacket[] sendRequest(FusionRequest request, String sid, String remoteIp) throws GatewayConnectionException {
        try {
            return this.sendGatewayRequest(request.toJSON(2), sid, remoteIp);
        }
        catch (JSONException je) {
            throw new GatewayConnectionException(-1, je.getMessage());
        }
        catch (GatewayConnectionException ge) {
            throw new GatewayConnectionException(-1, ge.getMessage());
        }
    }

    private FusionPacket[] sendGatewayRequest(String jsonRequest, String sid, String remoteIp) throws GatewayConnectionException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("/xml");
        httppost.addHeader("X-Mig33-JSON-Version", Integer.toString(2));
        httppost.addHeader("Content-Type", "application/json; charset=UTF-8");
        httppost.addHeader("Connection", "close");
        httppost.addHeader("Cookie", String.format("sid=%s", sid));
        if (!StringUtil.isBlank(remoteIp)) {
            httppost.addHeader("X-Forwarded-For", remoteIp);
        }
        jsonRequest = jsonRequest.trim();
        try {
            httppost.setEntity((HttpEntity)new StringEntity(jsonRequest));
        }
        catch (UnsupportedEncodingException e1) {
            String errMsg = String.format("Error in create request to make gateway http call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e1);
            throw new GatewayConnectionException(-1, errMsg);
        }
        HttpResponse response = null;
        try {
            log.info((Object)String.format("Making gateway http call to %s", this.hcHost.toString()));
            response = httpclient.execute(this.hcHost, (HttpRequest)httppost);
            log.info((Object)String.format("Received response from gateway http call %s", this.hcHost.toString()));
        }
        catch (ClientProtocolException e) {
            String errMsg = String.format("Error in making gateway http call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new GatewayConnectionException(-1, errMsg);
        }
        catch (IOException e) {
            String errMsg = String.format("IOException in making gateway http call to %s", this.hcHost.toString());
            log.error((Object)errMsg, (Throwable)e);
            throw new GatewayConnectionException(-1, errMsg);
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                String res = EntityUtils.toString((HttpEntity)entity);
                if (res.length() == 0) {
                    log.error((Object)("No response returned from request " + jsonRequest));
                    throw new GatewayConnectionException(-1, "Unable to parse gateway http call result.");
                }
                try {
                    return FusionPacket.parseJSONArray(res, 2);
                }
                catch (Exception e) {
                    String errMsg = String.format("Unable to parse gateway http call result into JSON array, api %s, %s", this.hcHost.toString(), e.getMessage());
                    log.error((Object)errMsg, (Throwable)e);
                    throw new GatewayConnectionException(-1, errMsg);
                }
            }
            catch (ParseException e) {
                String errMsg = String.format("Unable to parse gateway http call result, api %s", this.hcHost.toString());
                log.error((Object)errMsg, (Throwable)e);
                throw new GatewayConnectionException(-1, errMsg);
            }
            catch (IOException e) {
                String errMsg = String.format("Unable to parse gateway http call result, api %s", this.hcHost.toString());
                log.error((Object)errMsg, (Throwable)e);
                throw new GatewayConnectionException(-1, errMsg);
            }
        }
        log.error((Object)String.format("null entity in making gateway http call to %s", this.hcHost.toString()));
        throw new GatewayConnectionException(-1, "null entity");
    }

    static {
        lock = new Object();
    }

    public static class GatewayConnectionException
    extends Exception {
        public int errno;

        public GatewayConnectionException(int errno, String errMsg) {
            super(errMsg);
            this.errno = errno;
        }

        public boolean isLocalException() {
            return this.errno <= 100;
        }
    }
}

