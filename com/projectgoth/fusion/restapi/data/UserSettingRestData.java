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
@XmlRootElement(name="settings")
public class UserSettingRestData {
    @XmlElement(required=true, nillable=false)
    public int type;
    @XmlElement(required=true, nillable=false)
    public int value;

    public String toString() {
        return String.format("[type:%s, value:%s]", this.type, this.value);
    }
}

