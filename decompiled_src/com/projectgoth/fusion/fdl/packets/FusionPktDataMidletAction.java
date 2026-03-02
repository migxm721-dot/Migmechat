/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.packets;

import com.projectgoth.fusion.fdl.enums.PacketType;
import com.projectgoth.fusion.packet.FusionPacket;
import java.util.HashMap;

public class FusionPktDataMidletAction
extends FusionPacket {
    public FusionPktDataMidletAction() {
        super(PacketType.MIDLET_ACTION);
    }

    public FusionPktDataMidletAction(short transactionId) {
        super(PacketType.MIDLET_ACTION, transactionId);
    }

    public FusionPktDataMidletAction(FusionPacket packet) {
        super(packet);
    }

    public final ActionType getAction() {
        return ActionType.fromValue(this.getIntField((short)1));
    }

    public final void setAction(ActionType action) {
        this.setField((short)1, action.value());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ActionType {
        MAKE_CONTACTLIST_ACTIVE(1),
        USE_DEFAULT_THEME(2),
        CLEAR_EMOTICON_CACHE(3),
        USE_DEFAULT_LANGUAGE(4),
        REFRESH_CONTACT_LIST(5);

        private int value;
        private static final HashMap<Integer, ActionType> LOOKUP;

        private ActionType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static ActionType fromValue(Integer value) {
            return LOOKUP.get(value);
        }

        static {
            LOOKUP = new HashMap();
            for (ActionType actionType : ActionType.values()) {
                LOOKUP.put(actionType.value, actionType);
            }
        }
    }
}

