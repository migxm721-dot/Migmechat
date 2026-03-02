/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;

public interface PacketProcessorCallback {
    public void onFusionRequestProcessed(FusionRequest var1, FusionPacket[] var2);

    public void onFusionRequestDiscarded(FusionRequest var1);

    public void onFusionRequestException(FusionRequest var1, Exception var2);
}

