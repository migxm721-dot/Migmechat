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

@XmlAccessorType(value=XmlAccessType.NONE)
@XmlRootElement(name="accountTransaction")
public class CreditCardTransactionListRequestData {
    @XmlElement
    public String startDate = "";
    @XmlElement
    public String endDate = "";
    @XmlElement
    public String sortBy = "";
    @XmlElement
    public String sortOrder = "";
    @XmlElement
    public String showAuth = "";
    @XmlElement
    public String showPend = "";
    @XmlElement
    public String showRej = "";
    @XmlElement
    public String username = "";
    @XmlElement
    public int displayLimit = 0;
}

