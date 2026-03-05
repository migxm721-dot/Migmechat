/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.persist.model.Persistent
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.userevent.domain;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.ProfileUpdatedUserEventIce;
import com.projectgoth.fusion.userevent.domain.UserEvent;
import com.sleepycat.persist.model.Persistent;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Persistent
public class ProfileUpdatedUserEvent
extends UserEvent {
    public static final String EVENT_NAME = "PROFILE_UPDATED";
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ProfileUpdatedUserEvent.class));

    public ProfileUpdatedUserEvent() {
    }

    public ProfileUpdatedUserEvent(ProfileUpdatedUserEventIce event) {
        super(event);
    }

    @Override
    public ProfileUpdatedUserEventIce toIceEvent() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("creating " + this.getClass().getName()));
        }
        ProfileUpdatedUserEventIce iceEvent = new ProfileUpdatedUserEventIce(this.getTimestamp(), this.getGeneratingUsername(), null, this.getText());
        return iceEvent;
    }

    public static Map<String, String> findSubstitutionParameters(ProfileUpdatedUserEventIce event) {
        Map<String, String> map = UserEvent.findSubstitutionParameters(event);
        return map;
    }
}

