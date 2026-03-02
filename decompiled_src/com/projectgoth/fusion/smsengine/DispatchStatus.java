/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.smsengine.SMSMessage;

public class DispatchStatus {
    public StatusEnum status;
    public String transactionID;
    public String failedReason;
    public String source;
    public boolean billed;

    public DispatchStatus(StatusEnum status, SMSMessage message) {
        this.status = status;
        this.source = message.getSource();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StatusEnum {
        SUCCEEDED,
        FAILED,
        TRY_OTHER_GATEWAYS;

    }
}

