/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.fdl.enums.DeviceModeType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;

public class FusionPktDeviceModeOld
extends FusionRequest {
    public FusionPktDeviceModeOld() {
        super((short)604);
    }

    public FusionPktDeviceModeOld(short transactionId) {
        super((short)604, transactionId);
    }

    public FusionPktDeviceModeOld(FusionPacket packet) {
        super(packet);
    }

    public boolean sessionRequired() {
        return true;
    }

    public Enums.DeviceModeEnum getDeviceMode() {
        return Enums.DeviceModeEnum.fromValue(this.getIntField((short)1));
    }

    public void setDeviceMode(Enums.DeviceModeEnum deviceMode) {
        this.setField((short)1, deviceMode.value());
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        Enums.DeviceModeEnum mode = this.getDeviceMode();
        if (mode == null || !connection.isMobileClientV2()) {
            FusionPktError pktError = new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unsupported device mode");
            return new FusionPacket[]{pktError};
        }
        connection.setDeviceMode(DeviceModeType.fromValue(mode.value()));
        return new FusionPacket[]{new FusionPktOk(this.transactionId)};
    }
}

