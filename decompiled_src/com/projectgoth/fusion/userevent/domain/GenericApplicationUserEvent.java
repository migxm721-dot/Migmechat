/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.slice.GenericApplicationUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent(version=1)
public class GenericApplicationUserEvent
extends UserEvent {
    public static final String WEB_URL_KEY = "web";
    public static final String ANDROID_URL_KEY = "android";
    public static final String J2ME_URL_KEY = "j2me";
    public static final int EVENT_TEXT_MAX_LENGTH = 200;
    public static final String EVENT_NAME = "GENERIC_APP_EVENT";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GenericApplicationUserEvent.class));
    private String text;
    private Map<String, String> urls;

    public GenericApplicationUserEvent() {
    }

    public GenericApplicationUserEvent(UserEvent event, String text) {
        super(event);
        this.text = text;
    }

    public GenericApplicationUserEvent(GenericApplicationUserEventIce event) {
        super(event);
        this.text = event.text;
        this.urls = event.urls;
    }

    public Map<String, String> getURLs() {
        return this.urls;
    }

    public void setURLs(Map<String, String> urls) {
        this.urls = urls;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public GenericApplicationUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        long eventTimestamp = this.getTimestamp();
        String eventUsername = this.getGeneratingUsername();
        String eventDisplayPicture = null;
        String eventText = this.getText();
        Map<String, String> eventURLs = this.getURLs();
        GenericApplicationUserEventIce iceEvent = new GenericApplicationUserEventIce(eventTimestamp, eventUsername, eventDisplayPicture, eventText, eventURLs);
        return iceEvent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append(" text [").append(this.text).append("]");
        return buffer.toString();
    }

    public static Map<String, String> findSubstitutionParameters(GenericApplicationUserEventIce event, ClientType deviceType) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        map.put("text", event.text);
        String deviceURL = null;
        switch (deviceType) {
            case AJAX1: 
            case AJAX2: {
                deviceURL = event.urls.get(WEB_URL_KEY);
                break;
            }
            case ANDROID: {
                deviceURL = event.urls.get(ANDROID_URL_KEY);
                break;
            }
            case MIDP1: 
            case MIDP2: {
                deviceURL = event.urls.get(J2ME_URL_KEY);
                break;
            }
            default: {
                deviceURL = null;
            }
        }
        if (StringUtil.isBlank(deviceURL)) {
            map.put("url", "");
        } else {
            StringBuffer buffer = new StringBuffer("(<a href='").append(deviceURL).append("'>view</a>)");
            map.put("url", buffer.toString());
        }
        return map;
    }
}

