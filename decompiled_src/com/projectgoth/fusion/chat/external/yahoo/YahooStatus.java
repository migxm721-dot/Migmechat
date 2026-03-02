/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.yahoo;

import com.projectgoth.fusion.fdl.enums.PresenceType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum YahooStatus {
    AVAILABLE(0, "Available"),
    NONE(0, "None"),
    DEFAULT(0, "Default"),
    BRB(1, "Be Right Back"),
    BUSY(2, "Busy"),
    NOTATHOME(3, "Not at Home"),
    NOTATDESK(4, "Not at Desk"),
    NOTINOFFICE(5, "Not in Office"),
    ONPHONE(6, "On the Phone"),
    ONVACATION(7, "On Vacation"),
    OUTTOLUNCH(8, "Out to Lunch"),
    STEPPEDOUT(9, "Stepped Out"),
    INVISIBLE(12, "Invisible"),
    VISIBLE(13, "Visible"),
    TYPING(22, "Typing"),
    NOTIFY(22, "Notify"),
    CUSTOM(99, "Custom"),
    IDLE(999, "Idle"),
    OFFLINE(0x5A55AA56, "Offline");

    private int value;
    private String description;

    private YahooStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static YahooStatus valueOf(int value) {
        for (YahooStatus status : YahooStatus.values()) {
            if (status.value != value) continue;
            return status;
        }
        return null;
    }

    public PresenceType toFusionPresence() {
        switch (this) {
            case TYPING: 
            case NONE: 
            case DEFAULT: 
            case VISIBLE: 
            case AVAILABLE: {
                return PresenceType.AVAILABLE;
            }
            case BUSY: 
            case ONPHONE: {
                return PresenceType.BUSY;
            }
            case BRB: 
            case NOTATHOME: 
            case NOTATDESK: 
            case NOTINOFFICE: 
            case ONVACATION: 
            case OUTTOLUNCH: 
            case STEPPEDOUT: 
            case CUSTOM: 
            case IDLE: {
                return PresenceType.AWAY;
            }
            case INVISIBLE: 
            case OFFLINE: {
                return PresenceType.OFFLINE;
            }
        }
        return PresenceType.OFFLINE;
    }

    public static YahooStatus fromFusionPresence(PresenceType fusionPresence) {
        switch (fusionPresence) {
            case AVAILABLE: 
            case ROAMING: {
                return AVAILABLE;
            }
            case AWAY: {
                return IDLE;
            }
            case BUSY: {
                return BUSY;
            }
            case OFFLINE: {
                return OFFLINE;
            }
        }
        return OFFLINE;
    }
}

