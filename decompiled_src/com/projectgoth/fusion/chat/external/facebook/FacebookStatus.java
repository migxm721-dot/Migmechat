/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jivesoftware.smack.packet.Presence
 *  org.jivesoftware.smack.packet.Presence$Mode
 *  org.jivesoftware.smack.packet.Presence$Type
 */
package com.projectgoth.fusion.chat.external.facebook;

import com.projectgoth.fusion.fdl.enums.PresenceType;
import org.jivesoftware.smack.packet.Presence;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FacebookStatus {
    OFFLINE("Offline"),
    AVAILABLE("Available"),
    AWAY("Away");

    private String description;

    private FacebookStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
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

    public Presence toXMPPPresence() {
        switch (this) {
            case AVAILABLE: {
                Presence p = new Presence(Presence.Type.available);
                p.setMode(Presence.Mode.available);
                return p;
            }
            case AWAY: {
                Presence p = new Presence(Presence.Type.available);
                p.setMode(Presence.Mode.away);
                return p;
            }
        }
        return new Presence(Presence.Type.unavailable);
    }

    public static FacebookStatus fromFusionPresence(PresenceType fusionPresence) {
        switch (fusionPresence) {
            case AVAILABLE: 
            case ROAMING: 
            case BUSY: {
                return AVAILABLE;
            }
            case AWAY: {
                return AWAY;
            }
        }
        return OFFLINE;
    }

    public static FacebookStatus fromXMPPPresence(Presence presence) {
        if (presence.isAvailable()) {
            return AVAILABLE;
        }
        if (presence.isAway()) {
            return AWAY;
        }
        return OFFLINE;
    }
}

