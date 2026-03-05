/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.packets;

import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PacketType;
import com.projectgoth.fusion.packet.FusionPacket;
import java.util.HashMap;

public class FusionPktDataImSessionStatus
extends FusionPacket {
    public FusionPktDataImSessionStatus() {
        super(PacketType.IM_SESSION_STATUS);
    }

    public FusionPktDataImSessionStatus(short transactionId) {
        super(PacketType.IM_SESSION_STATUS, transactionId);
    }

    public FusionPktDataImSessionStatus(FusionPacket packet) {
        super(packet);
    }

    public final ImType getImType() {
        return ImType.fromValue(this.getByteField((short)1));
    }

    public final void setImType(ImType imType) {
        this.setField((short)1, imType.value());
    }

    public final StatusType getStatus() {
        return StatusType.fromValue(this.getByteField((short)2));
    }

    public final void setStatus(StatusType status) {
        this.setField((short)2, status.value());
    }

    public final String getReason() {
        return this.getStringField((short)3);
    }

    public final void setReason(String reason) {
        this.setField((short)3, reason);
    }

    public final Boolean getSupportsConference() {
        return this.getBooleanField((short)4);
    }

    public final void setSupportsConference(boolean supportsConference) {
        this.setField((short)4, supportsConference);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusType {
        LOGGED_IN(1),
        LOGGED_OUT(2);

        private byte value;
        private static final HashMap<Byte, StatusType> LOOKUP;

        private StatusType(byte value) {
            this.value = value;
        }

        public byte value() {
            return this.value;
        }

        public static StatusType fromValue(int value) {
            return LOOKUP.get((byte)value);
        }

        public static StatusType fromValue(Byte value) {
            return LOOKUP.get(value);
        }

        static {
            LOOKUP = new HashMap();
            for (StatusType statusType : StatusType.values()) {
                LOOKUP.put(statusType.value, statusType);
            }
        }
    }
}

