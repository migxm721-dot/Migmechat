/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.exception.ExceptionWithDiagnosticCode;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import org.apache.log4j.Logger;

public class FusionPktInternalServerError
extends FusionPktError {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktInternalServerError.class));

    public FusionPktInternalServerError(short transactionId, Exception rootException, String context) {
        super(transactionId);
        this.setErrorCode(FusionPktError.Code.UNDEFINED);
        String errorCode = ExceptionWithDiagnosticCode.makeObfuscatedErrorCode(rootException, context);
        String customerFacingError = context + " - Internal Server Error (" + errorCode + ")";
        this.setErrorDescription(customerFacingError);
        log.error((Object)(customerFacingError + " rootException=" + rootException), (Throwable)rootException);
    }
}

