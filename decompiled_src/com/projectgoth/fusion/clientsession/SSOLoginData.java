/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.clientsession;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.restapi.data.SSOCheckResponseData;
import org.apache.log4j.Logger;

public class SSOLoginData {
    public String username = "";
    public Integer userID = null;
    public Integer presence = null;
    public Integer voiceCapability = null;
    public Short clientVersion = null;
    public String mobileDevice = "";
    public String userAgent = "";
    public Integer deviceType = ClientType.AJAX1.value();
    public Integer passwordHash = null;
    public Long sessionStartTimeInMillis = null;
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SSOLoginData.class));
    public static final String DATA_SEPARATOR = "##";

    public SSOLoginData() {
    }

    public String toString() {
        return this.username + DATA_SEPARATOR + Integer.toString(this.userID != null ? this.userID : 0) + DATA_SEPARATOR + Integer.toString(this.presence != null ? this.presence : 0) + DATA_SEPARATOR + Integer.toString(0) + DATA_SEPARATOR + Short.toString(this.clientVersion != null ? this.clientVersion : (short)0) + DATA_SEPARATOR + (this.mobileDevice != null ? this.mobileDevice : "") + DATA_SEPARATOR + (this.userAgent != null ? this.userAgent : "") + DATA_SEPARATOR + Integer.toString(this.passwordHash != null ? this.passwordHash : 0) + DATA_SEPARATOR + Long.toString(this.sessionStartTimeInMillis != null ? this.sessionStartTimeInMillis : 0L);
    }

    public SSOLoginData(String data) throws IllegalArgumentException {
        String[] tokens = data.split(DATA_SEPARATOR);
        if (9 != tokens.length) {
            log.error((Object)("Error in creating SSOLoginData, invalid string entered [" + data + "]"));
            throw new IllegalArgumentException("Error in creating SSOLoginData, invalid string entered [" + data + "]");
        }
        this.username = tokens[0];
        this.userID = Integer.parseInt(tokens[1]);
        this.presence = Integer.parseInt(tokens[2]);
        this.voiceCapability = Integer.parseInt(tokens[3]);
        this.clientVersion = Short.valueOf(tokens[4]);
        this.mobileDevice = tokens[5];
        this.userAgent = tokens[6];
        this.passwordHash = Integer.parseInt(tokens[7]);
        this.sessionStartTimeInMillis = Long.parseLong(tokens[8]);
    }

    public SSOCheckResponseData toSSOCheckResponseData() {
        SSOCheckResponseData data = new SSOCheckResponseData();
        data.username = this.username;
        data.userid = this.userID;
        data.presence = this.presence;
        data.clientVersion = this.clientVersion;
        data.mobileDevice = this.mobileDevice;
        data.userAgent = this.userAgent;
        data.deviceType = this.deviceType;
        data.sessionStartTimeInSeconds = this.sessionStartTimeInMillis / 1000L;
        return data;
    }
}

