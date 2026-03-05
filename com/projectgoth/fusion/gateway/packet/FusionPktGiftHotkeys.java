/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.fdl.packets.FusionPktDataGiftHotkeys;
import com.projectgoth.fusion.packet.FusionPacket;

public class FusionPktGiftHotkeys
extends FusionPktDataGiftHotkeys {
    public FusionPktGiftHotkeys(short transactionId) {
        super(transactionId);
    }

    public FusionPktGiftHotkeys(FusionPacket packet) {
        super(packet);
    }
}

