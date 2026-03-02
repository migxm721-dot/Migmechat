/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.exception;

import com.projectgoth.fusion.exception.ExceptionWithDiagnosticCode;

public class InternalServerErrorException
extends ExceptionWithDiagnosticCode {
    private static final String MSG = "Internal Server Error";

    public InternalServerErrorException(Exception rootException, String contextInfo) {
        super(MSG, rootException, contextInfo);
    }
}

