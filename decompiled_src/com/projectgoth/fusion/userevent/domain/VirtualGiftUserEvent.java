/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.VirtualGiftUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent(version=1)
public class VirtualGiftUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "VIRTUAL_GIFT";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(VirtualGiftUserEvent.class));
    private String recipient;
    private String giftName;
    private int virtualGiftReceivedId;

    public VirtualGiftUserEvent() {
    }

    public VirtualGiftUserEvent(UserEvent event, String recipient, String giftName, int virtualGiftReceivedId) {
        super(event);
        this.recipient = recipient;
        this.giftName = giftName;
        this.virtualGiftReceivedId = virtualGiftReceivedId;
    }

    public VirtualGiftUserEvent(VirtualGiftUserEventIce event) {
        super(event);
        this.recipient = event.recipient;
        this.giftName = event.giftName;
        this.virtualGiftReceivedId = event.virtualGiftReceivedId;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public String getGiftName() {
        return this.giftName;
    }

    @Override
    public VirtualGiftUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        VirtualGiftUserEventIce iceEvent = new VirtualGiftUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText(), this.recipient, this.giftName, this.virtualGiftReceivedId);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" recipient [").append(this.recipient).append("]");
        buffer.append(" giftName [").append(this.giftName).append("]");
        buffer.append(" virtualGiftReceivedId [").append(this.virtualGiftReceivedId).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(VirtualGiftUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("recipient", event.recipient);
        map.put("giftName", event.giftName);
        map.put("virtualGiftReceivedId", Integer.toString(event.virtualGiftReceivedId));
        return map;
    }
}

