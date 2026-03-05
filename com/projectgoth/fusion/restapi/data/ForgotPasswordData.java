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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="forgotpassword")
public class ForgotPasswordData {
    @XmlElement(required=true, nillable=false)
    public String username;
    @XmlElement(required=false, nillable=true)
    public Integer type;
    @XmlElement(required=false, nillable=true)
    public String emailAddress;
    @XmlElement(required=false, nillable=true)
    public int securityQuestion;
    @XmlElement(required=false, nillable=true)
    public String securityAnswer;
    @XmlElement(required=false, nillable=true)
    public String ipAddress;
    @XmlElement(required=false, nillable=true)
    public String mobileDevice;
    @XmlElement(required=false, nillable=true)
    public String userAgent;

    public String toString() {
        return String.format("[username:%s, type:%s, emailAddress:%s, securityQuestion:%s, securityAnswer:%s, ipAddress:%s, mobileDevice:%s, userAgent:%s]", this.username, this.type, this.emailAddress, this.securityQuestion, this.securityAnswer, this.ipAddress, this.mobileDevice, this.userAgent);
    }
}

