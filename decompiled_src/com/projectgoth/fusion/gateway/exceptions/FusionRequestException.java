/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.exceptions;

public class FusionRequestException
extends Exception {
    private static final long serialVersionUID = 1L;
    ExceptionType type;

    public FusionRequestException(ExceptionType type, String s) {
        super(s);
        this.type = type;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ExceptionType {
        PREVALIDATION;

    }
}

