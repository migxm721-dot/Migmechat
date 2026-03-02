/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.fdl.enums.DeviceModeType;
import com.projectgoth.fusion.fdl.packets.FusionPktDataDeviceMode;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FusionPktDeviceMode
extends FusionPktDataDeviceMode {
    public FusionPktDeviceMode(short transactionId) {
        super(transactionId);
    }

    public FusionPktDeviceMode(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktDeviceMode(FusionPacket packet) {
        super(packet);
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        DeviceModeType mode = this.getDeviceMode();
        if (mode == null || !connection.isMobileClientV2()) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unsupported device mode");
            return new FusionPacket[]{pktError};
        }
        connection.setDeviceMode(mode);
        return new FusionPacket[]{new FusionPktOk(this.transactionId)};
    }
}

