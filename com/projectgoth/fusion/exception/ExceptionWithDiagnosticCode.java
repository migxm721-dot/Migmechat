/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.exception;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.FusionException;
import org.apache.log4j.Logger;

public class ExceptionWithDiagnosticCode
extends FusionException {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ExceptionWithDiagnosticCode.class));
    private static final String PREFIX = " (";
    private static final String SUFFIX = ")";

    public ExceptionWithDiagnosticCode(String message, Exception rootException, String contextInfo) {
        super(message + PREFIX + ExceptionWithDiagnosticCode.makeObfuscatedErrorCode(rootException, contextInfo) + SUFFIX);
    }

    public static String makeObfuscatedErrorCode(Exception rootException, String contextInfo) {
        return Integer.toString(Math.abs(ExceptionWithDiagnosticCode.makeErrorCode(rootException, contextInfo).hashCode()));
    }

    private static String makeErrorCode(Exception rootException, String contextInfo) {
        return ExceptionWithDiagnosticCode.stackTraceConcat(Thread.currentThread().getStackTrace()) + rootException + rootException.getMessage() + ExceptionWithDiagnosticCode.stackTraceConcat(rootException.getStackTrace()) + contextInfo;
    }

    private static String stackTraceConcat(StackTraceElement[] ste) {
        String concat = "";
        for (StackTraceElement elem : ste) {
            concat = concat + elem.toString();
        }
        return concat;
    }
}

