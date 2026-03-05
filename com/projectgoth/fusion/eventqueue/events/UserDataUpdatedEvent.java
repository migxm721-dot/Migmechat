/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.eventqueue.events;

import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.eventqueue.Event;
import java.util.HashMap;
import java.util.Map;

public class UserDataUpdatedEvent
extends Event {
    public static final String TYPE_KEY = "type";

    public UserDataUpdatedEvent() {
        super(Enums.EventTypeEnum.USERDATA_UPDATED);
    }

    public UserDataUpdatedEvent(String username, TypeEnum type) {
        super(username, Enums.EventTypeEnum.USERDATA_UPDATED);
        this.putParameter(TYPE_KEY, type.toString());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TypeEnum {
        PROFILE(1),
        SETTINGS(2),
        USER_DETAIL(3),
        DISPLAY_PICTURE(4),
        STATUS_MESSAGE(5);

        private static final Map<Integer, TypeEnum> lookup;
        int value;

        private TypeEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static TypeEnum fromValue(int v) {
            return lookup.get(v);
        }

        static {
            lookup = new HashMap<Integer, TypeEnum>();
            for (TypeEnum e : TypeEnum.values()) {
                lookup.put(e.value, e);
            }
        }
    }
}

