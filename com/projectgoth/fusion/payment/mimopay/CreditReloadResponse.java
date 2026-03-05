/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.projectgoth.fusion.payment.mimopay;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="RESPONSE")
public class CreditReloadResponse {
    @XmlElement(name="SERVICENAME")
    public String serviceName;
    @XmlElement(name="MIMOTRANSID")
    public String mimoTransactionID;
    @XmlElement(name="RETCODE")
    public Integer retCode;
    @XmlElement(name="TRANSID")
    public String transID;
    @XmlElement(name="RVALUE")
    public Double reloadValue;
    @XmlElement(name="REMARK")
    public String remark;
    @XmlElement(name="TIMESTAMP")
    public Long timestamp;
}

