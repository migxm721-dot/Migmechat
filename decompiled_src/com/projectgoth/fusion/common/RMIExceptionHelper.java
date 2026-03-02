/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.rmi.RemoteException;

public class RMIExceptionHelper {
    public static String getRootMessage(RemoteException e) {
        Throwable exception = e;
        for (Throwable cause = ((Throwable)exception).getCause(); cause != null; cause = cause.getCause()) {
            exception = cause;
        }
        String message = ((Throwable)exception).getMessage();
        if (message == null) {
            return e.getClass().getName();
        }
        return message;
    }
}

