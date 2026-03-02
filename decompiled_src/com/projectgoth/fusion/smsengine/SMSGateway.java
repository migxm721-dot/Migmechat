/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.data.SMSGatewayData;
import com.projectgoth.fusion.smsengine.DispatchStatus;
import com.projectgoth.fusion.smsengine.SMSMessage;

public abstract class SMSGateway {
    protected SMSGatewayData gatewayData;

    public SMSGateway(SMSGatewayData gatewayData) {
        this.gatewayData = gatewayData;
    }

    public Integer getID() {
        return this.gatewayData.id;
    }

    public abstract DispatchStatus dispatchMessage(SMSMessage var1);
}

