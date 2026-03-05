/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.projectgoth.fusion.restapi.data;

import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="events")
public class EventNewPostData {
    public String timestamp;
    public String fullPostid;
    public String parentFullPostid;
    public String originality;
    public String application;
    public String type;
    public List<String> hashtags;
    public List<Map<String, String>> links;
    public List<Integer> mentions;
}

