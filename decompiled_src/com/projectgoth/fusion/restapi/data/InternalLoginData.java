/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.restapi.data.SSOEnums;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="internalLoginData")
public class InternalLoginData {
    public int reuseExistingSession = 0;
    public String sid = "";
    public int presence = 1;
    public short clientVersion = 0;
    public String mobileDevice = "1";
    public byte deviceType = (byte)10;
    public String view = SSOEnums.View.MIGBO_AJAXV2.name();
    @XmlElement(required=true, nillable=false)
    public String remoteIpAddress = "";
    @XmlElement(required=true, nillable=false)
    public String userAgent = "";

    public boolean reuseExistingSession() {
        return this.reuseExistingSession == 1;
    }
}

