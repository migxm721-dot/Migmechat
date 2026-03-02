/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ErrorCause;
import javax.ejb.EJBException;

public class EJBExceptionWithErrorCause
extends EJBException {
    private ErrorCause errorCause;
    private Object[] errorMsgArgs;

    public EJBExceptionWithErrorCause(ErrorCause reasonType, Object ... errorMsgArgs) {
        super(EJBExceptionWithErrorCause.formatErrorMessage(reasonType.getDefaultErrorMessage(), errorMsgArgs));
        this.errorCause = reasonType;
        this.errorMsgArgs = errorMsgArgs;
    }

    public EJBExceptionWithErrorCause(Throwable t, ErrorCause reasonType, Object ... errorMsgArgs) {
        super(EJBExceptionWithErrorCause.formatErrorMessage(reasonType.getDefaultErrorMessage(), errorMsgArgs));
        this.initCause(t);
        this.errorCause = reasonType;
        this.errorMsgArgs = errorMsgArgs;
    }

    private static String formatErrorMessage(String format, Object ... args) {
        if (args == null || args.length == 0) {
            return format;
        }
        return String.format(format, args);
    }

    public ErrorCause getErrorCause() {
        return this.errorCause;
    }

    public Object[] getErrorMsgArgs() {
        return this.errorMsgArgs;
    }
}

