/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.aim;

import com.projectgoth.fusion.fdl.enums.PresenceType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum AIMStatus {
    AVAILABLE(0, "Available"),
    AWAY(1, "Away"),
    INVISIBLE(256, "Invisible"),
    OFFLINE(-1, "Offline");

    private int value;
    private String description;

    private AIMStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static AIMStatus valueOf(int value) {
        for (AIMStatus status : AIMStatus.values()) {
            if (status.value != value) continue;
            return status;
        }
        return null;
    }

    public PresenceType toFusionPresence() {
        switch (this) {
            case AVAILABLE: {
                return PresenceType.AVAILABLE;
            }
            case AWAY: {
                return PresenceType.AWAY;
            }
        }
        return PresenceType.OFFLINE;
    }

    public static AIMStatus fromFusionPresence(PresenceType fusionPresence) {
        switch (fusionPresence) {
            case AVAILABLE: 
            case ROAMING: {
                return AVAILABLE;
            }
            case AWAY: 
            case BUSY: {
                return AWAY;
            }
        }
        return OFFLINE;
    }
}

