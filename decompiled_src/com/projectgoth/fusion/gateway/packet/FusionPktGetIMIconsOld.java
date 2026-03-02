/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ByteBufferHelper;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktIMIconsOld;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.File;
import java.util.LinkedList;

public class FusionPktGetIMIconsOld
extends FusionRequest {
    public FusionPktGetIMIconsOld() {
        super((short)926);
    }

    public FusionPktGetIMIconsOld(short transactionId) {
        super((short)926, transactionId);
    }

    public FusionPktGetIMIconsOld(FusionPacket packet) {
        super(packet);
    }

    public byte[] getIMTypes() {
        return this.getByteArrayField((short)1);
    }

    public void setIMTypes(byte[] imTypes) {
        this.setField((short)1, imTypes);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        try {
            byte[] imTypes = this.getIMTypes();
            if (imTypes == null || imTypes.length == 0) {
                return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "IM types not specified").toArray();
            }
            LinkedList<FusionPktIMIconsOld> packetsToReturn = new LinkedList<FusionPktIMIconsOld>();
            for (byte imType : imTypes) {
                ImType imEnum = ImType.fromValue((int)imType);
                if (imEnum == null || imEnum == ImType.FUSION) {
                    return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Invalid IM type " + imType).toArray();
                }
                String path = connection.getGateway().getIMFilePath() + File.separator + imEnum.toString().toLowerCase() + File.separator;
                if (connection.getDeviceType() == ClientType.ANDROID && connection.getClientVersion() >= 300) {
                    path = path + "android" + File.separator;
                }
                FusionPktIMIconsOld iconsPkt = new FusionPktIMIconsOld(this.transactionId);
                iconsPkt.setIMType(imType);
                iconsPkt.setOnline(ByteBufferHelper.readFile(new File(path + "Online.png")).array());
                iconsPkt.setRoaming(ByteBufferHelper.readFile(new File(path + "Roaming.png")).array());
                iconsPkt.setBusy(ByteBufferHelper.readFile(new File(path + "Busy.png")).array());
                iconsPkt.setAway(ByteBufferHelper.readFile(new File(path + "Away.png")).array());
                iconsPkt.setOffline(ByteBufferHelper.readFile(new File(path + "Offline.png")).array());
                packetsToReturn.add(iconsPkt);
            }
            return packetsToReturn.toArray(new FusionPacket[packetsToReturn.size()]);
        }
        catch (Exception e) {
            return new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Unable to get IM icons - " + e.getMessage()).toArray();
        }
    }
}

