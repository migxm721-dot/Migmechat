/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jivesoftware.smack.packet.Presence
 *  org.jivesoftware.smack.packet.Presence$Mode
 *  org.jivesoftware.smack.packet.Presence$Type
 */
package com.projectgoth.fusion.chat.external.gtalk;

import com.projectgoth.fusion.fdl.enums.PresenceType;
import org.jivesoftware.smack.packet.Presence;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum GTalkStatus {
    AVAILABLE("available", "Available"),
    AWAY("away", "Away"),
    CHAT("chat", "Chat"),
    DND("dnd", "Do Not Disturb"),
    XA("xa", "Extended Away"),
    OFFLINE("offline", "Offline");

    private String value;
    private String description;

    private GTalkStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static GTalkStatus parse(String value) {
        for (GTalkStatus status : GTalkStatus.values()) {
            if (!status.value.equals(value)) continue;
            return status;
        }
        return null;
    }

    public Presence toXMPPPresence() {
        Presence p = new Presence(Presence.Type.available);
        switch (this) {
            case AVAILABLE: {
                p.setMode(Presence.Mode.available);
                return p;
            }
            case CHAT: {
                p.setMode(Presence.Mode.chat);
                return p;
            }
            case AWAY: {
                p.setMode(Presence.Mode.away);
                return p;
            }
            case XA: {
                p.setMode(Presence.Mode.xa);
                return p;
            }
            case DND: {
                p.setMode(Presence.Mode.dnd);
                return p;
            }
        }
        return new Presence(Presence.Type.unavailable);
    }

    public PresenceType toFusionPresence() {
        switch (this) {
            case AVAILABLE: 
            case CHAT: {
                return PresenceType.AVAILABLE;
            }
            case AWAY: 
            case XA: {
                return PresenceType.AWAY;
            }
            case DND: {
                return PresenceType.BUSY;
            }
        }
        return PresenceType.OFFLINE;
    }

    public static GTalkStatus fromFusionPresence(PresenceType fusionPresence) {
        switch (fusionPresence) {
            case AVAILABLE: 
            case ROAMING: {
                return AVAILABLE;
            }
            case AWAY: {
                return AWAY;
            }
            case BUSY: {
                return DND;
            }
        }
        return OFFLINE;
    }

    public static GTalkStatus fromXMPPPresence(Presence presence) {
        if (presence.isAvailable()) {
            Presence.Mode mode = presence.getMode();
            if (mode == null) {
                mode = Presence.Mode.available;
            }
            switch (mode) {
                case chat: {
                    return CHAT;
                }
                case away: {
                    return AWAY;
                }
                case dnd: {
                    return DND;
                }
                case xa: {
                    return XA;
                }
            }
            return AVAILABLE;
        }
        return OFFLINE;
    }
}

