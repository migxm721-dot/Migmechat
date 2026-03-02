/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.CreatedChatroomUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class CreatedChatroomUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "CREATE_PUBLIC_CHATROOM";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CreatedChatroomUserEvent.class));
    private String chatroom;

    public CreatedChatroomUserEvent() {
    }

    public CreatedChatroomUserEvent(UserEvent event, String chatroom) {
        super(event);
        this.chatroom = chatroom;
    }

    public CreatedChatroomUserEvent(CreatedChatroomUserEventIce event) {
        super(event);
        this.chatroom = event.chatroom;
    }

    public String getChatroom() {
        return this.chatroom;
    }

    public void setChatroom(String friend) {
        this.chatroom = friend;
    }

    @Override
    public CreatedChatroomUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        CreatedChatroomUserEventIce iceEvent = new CreatedChatroomUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.chatroom);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" chatroom [").append(this.chatroom).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(CreatedChatroomUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("chatroom", event.chatroom);
        return map;
    }
}

