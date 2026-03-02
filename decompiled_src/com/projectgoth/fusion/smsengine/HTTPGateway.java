/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.SMSGatewayData;
import com.projectgoth.fusion.smsengine.DispatchStatus;
import com.projectgoth.fusion.smsengine.SMSGateway;
import com.projectgoth.fusion.smsengine.SMSMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class HTTPGateway
extends SMSGateway {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(HTTPGateway.class));
    private static final String URL_ENCODING = "UTF-8";
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;
    private String separator;
    private boolean encodeParam;

    public HTTPGateway(SMSGatewayData gatewayData) {
        super(gatewayData);
        if (gatewayData.method == SMSGatewayData.MethodEnum.GET) {
            this.separator = "&";
            this.encodeParam = true;
        } else {
            this.separator = "\r\n";
            this.encodeParam = false;
        }
    }

    private void addParam(StringBuilder builder, String param) {
        if (param != null) {
            if (builder.length() > 0) {
                builder.append(this.separator);
            }
            builder.append(param);
        }
    }

    private void addParam(StringBuilder builder, String param, String value) throws UnsupportedEncodingException {
        if (param != null) {
            if (builder.length() > 0) {
                builder.append(this.separator);
            }
            if (this.encodeParam) {
                builder.append(URLEncoder.encode(param, URL_ENCODING));
            } else {
                builder.append(param);
            }
            builder.append("=");
            if (this.encodeParam) {
                builder.append(URLEncoder.encode(value, URL_ENCODING));
            } else {
                builder.append(value.replaceAll("\n", "<LF>").replaceAll("\r", "<CR>"));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public DispatchStatus dispatchMessage(SMSMessage message) {
        DispatchStatus dispatchStatus;
        block30: {
            BufferedReader reader;
            block29: {
                HttpURLConnection httpConn;
                URL url;
                StringBuilder param = null;
                Pattern successPattern = null;
                Pattern errorPattern = null;
                dispatchStatus = new DispatchStatus(DispatchStatus.StatusEnum.TRY_OTHER_GATEWAYS, message);
                try {
                    successPattern = Pattern.compile(this.gatewayData.successPattern);
                    errorPattern = Pattern.compile(this.gatewayData.errorPattern);
                    param = new StringBuilder();
                    this.addParam(param, this.gatewayData.usernameParam);
                    this.addParam(param, this.gatewayData.passwordParam);
                    this.addParam(param, this.gatewayData.sourceParam, message.getSource());
                    this.addParam(param, this.gatewayData.destinationParam, message.getDestination());
                    this.addParam(param, this.gatewayData.extraParam);
                    String messageText = message.getMessageText();
                    if (!messageText.matches("^[\\x00-\\xFF]*$")) {
                        if (this.gatewayData.unicodeCharset != null) {
                            messageText = this.byteArrayToHexString(messageText.getBytes(this.gatewayData.unicodeCharset));
                            messageText = messageText.replaceAll("feff", "");
                        }
                        if (this.gatewayData.unicodeMessageParam == null) {
                            this.addParam(param, this.gatewayData.messageParam, messageText);
                        } else {
                            this.addParam(param, this.gatewayData.unicodeMessageParam, messageText);
                        }
                        this.addParam(param, this.gatewayData.unicodeParam);
                    } else {
                        this.addParam(param, this.gatewayData.messageParam, messageText);
                    }
                }
                catch (Exception e) {
                    log.warn((Object)("Gateway " + this.gatewayData.id + ". Unable to dispatch " + message.getShortDescription() + " - " + e.toString()), (Throwable)e);
                    return dispatchStatus;
                }
                reader = null;
                if (this.gatewayData.method == SMSGatewayData.MethodEnum.GET) {
                    url = new URL(this.gatewayData.url + "?" + param.toString());
                    httpConn = (HttpURLConnection)url.openConnection();
                    httpConn.setConnectTimeout(15000);
                    httpConn.setReadTimeout(15000);
                    httpConn.connect();
                } else {
                    url = new URL(this.gatewayData.url);
                    httpConn = (HttpURLConnection)url.openConnection();
                    httpConn.setConnectTimeout(15000);
                    httpConn.setReadTimeout(15000);
                    httpConn.setDoOutput(true);
                    if (this.gatewayData.authorization != null) {
                        httpConn.setRequestProperty("Authorization", this.gatewayData.authorization);
                    }
                    httpConn.setUseCaches(false);
                    httpConn.getOutputStream().write(param.toString().getBytes(URL_ENCODING));
                }
                log.debug((Object)("Gateway " + this.gatewayData.id + ". Dispatching " + message.getShortDescription() + ". " + url.toString()));
                if (httpConn.getResponseCode() == 200) {
                    String line;
                    reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Matcher matcher = successPattern.matcher(response.toString());
                    if (matcher.find()) {
                        dispatchStatus.status = DispatchStatus.StatusEnum.SUCCEEDED;
                        dispatchStatus.transactionID = matcher.group(1);
                        dispatchStatus.billed = this.gatewayData.deliveryReporting == null || this.gatewayData.deliveryReporting == false;
                    } else {
                        dispatchStatus.status = DispatchStatus.StatusEnum.FAILED;
                        matcher = errorPattern.matcher(response);
                        if (matcher.find()) {
                            StringBuilder error = new StringBuilder();
                            int c = matcher.groupCount();
                            for (int i = 0; i < c; ++i) {
                                error.append(matcher.group(i + 1)).append(". ");
                            }
                            dispatchStatus.failedReason = error.toString();
                        }
                    }
                } else {
                    dispatchStatus.status = DispatchStatus.StatusEnum.FAILED;
                    dispatchStatus.failedReason = "HTTP " + httpConn.getResponseCode() + " " + httpConn.getResponseMessage();
                }
                if (dispatchStatus.status == DispatchStatus.StatusEnum.SUCCEEDED) {
                    log.info((Object)("Gateway " + this.gatewayData.id + ". Successfully dispatched " + message.getShortDescription() + ". Transaction ID = " + dispatchStatus.transactionID));
                    break block29;
                }
                if (dispatchStatus.status != DispatchStatus.StatusEnum.FAILED) break block29;
                log.warn((Object)("Gateway " + this.gatewayData.id + ". Failed to dispatch " + message.getShortDescription() + " - " + dispatchStatus.failedReason));
            }
            Object var16_17 = null;
            try {
                if (reader != null) {
                    reader.close();
                }
                break block30;
            }
            catch (IOException e2) {
                reader = null;
            }
            break block30;
            {
                catch (Exception e) {
                    log.warn((Object)("Gateway " + this.gatewayData.id + ". Exception caught while dispatching " + message.getShortDescription() + " - " + e.toString()), (Throwable)e);
                    Object var16_18 = null;
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                        break block30;
                    }
                    catch (IOException e2) {
                        reader = null;
                    }
                }
            }
            catch (Throwable throwable) {
                Object var16_19 = null;
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException e2) {
                    reader = null;
                }
                throw throwable;
            }
        }
        return dispatchStatus;
    }

    private String byteArrayToHexString(byte[] ba) {
        StringBuilder builder = new StringBuilder();
        for (byte b : ba) {
            builder.append(Integer.toHexString(b >> 4 & 0xF));
            builder.append(Integer.toHexString(b & 0xF));
        }
        return builder.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SMS Gateway: ID=");
        builder.append(this.gatewayData.id);
        builder.append(" Name=");
        builder.append(this.gatewayData.name);
        builder.append(" URL=");
        builder.append(this.gatewayData.url);
        return builder.toString();
    }
}

