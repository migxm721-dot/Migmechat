/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.ShortTextStatusUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class ShortTextStatusUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "SHORT_TEXT_STATUS";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ShortTextStatusUserEvent.class));
    private String status;

    public ShortTextStatusUserEvent() {
    }

    public ShortTextStatusUserEvent(UserEvent event, String friend) {
        super(event);
        this.status = friend;
    }

    public ShortTextStatusUserEvent(ShortTextStatusUserEventIce event) {
        super(event);
        this.status = event.status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String friend) {
        this.status = friend;
    }

    @Override
    public ShortTextStatusUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        ShortTextStatusUserEventIce iceEvent = new ShortTextStatusUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.status);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" status [").append(this.status).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(ShortTextStatusUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("status", event.status);
        return map;
    }
}

