/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.data.ChatRoomData;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlRootElement(name="user")
public class ChatroomSettingsMultiIDData {
    public Integer minMigLevel;
    public String rateLimitByIP;

    public void retrieveFromChatRoomData(ChatRoomData data) {
        this.minMigLevel = data.minMigLevel;
        this.rateLimitByIP = data.rateLimitByIp;
    }

    public void updateToChatRoomData(ChatRoomData data) {
        data.minMigLevel = this.minMigLevel;
        data.rateLimitByIp = this.rateLimitByIP;
    }
}

