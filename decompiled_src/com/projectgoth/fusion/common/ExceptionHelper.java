/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Vector;

public class ExceptionHelper {
    public static String getRawRootMessage(Exception e) {
        Throwable exception = e;
        for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
            exception = cause;
        }
        String message = exception.getMessage();
        if (message == null) {
            return e.getClass().getName();
        }
        return message;
    }

    public static String getRootMessage(Exception e) {
        return "EJBException:" + ExceptionHelper.getRawRootMessage(e);
    }

    public static Hashtable getRootMessageAsHashtable(Exception e) {
        Throwable exception = e;
        for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
            exception = cause;
        }
        String message = exception.getMessage();
        String returnMessage = "";
        if (message == null) {
            message = e.getClass().getName();
        }
        returnMessage = message;
        Hashtable<String, String> hash = new Hashtable<String, String>();
        hash.put("EJBException", returnMessage);
        return hash;
    }

    public static Vector getRootMessageAsVector(Exception e) {
        Throwable exception = e;
        for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
            exception = cause;
        }
        String message = exception.getMessage();
        String returnMessage = "";
        if (message == null) {
            message = e.getClass().getName();
        }
        returnMessage = message;
        Vector error = new Vector();
        Hashtable<String, String> hash = new Hashtable<String, String>();
        hash.put("EJBException", returnMessage);
        error.add(hash);
        return error;
    }

    public static String setErrorMessage(String errorString) {
        return "EJBException:" + errorString;
    }

    public static String removeErrorMessagePrefix(String errorString) {
        if (errorString.indexOf("EJBException:") == 0) {
            return errorString.substring("EJBException:".length());
        }
        return errorString;
    }

    public static Hashtable setErrorMessageAsHashtable(String errorString) {
        Hashtable<String, String> hash = new Hashtable<String, String>();
        hash.put("EJBException", errorString);
        return hash;
    }

    public static Vector setErrorMessageAsVector(String errorString) {
        Vector error = new Vector();
        Hashtable<String, String> hash = new Hashtable<String, String>();
        hash.put("EJBException", errorString);
        error.add(hash);
        return error;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static StringBuilder appendStackTrace(Throwable ex, StringBuilder target) {
        if (ex == null) {
            return target.append(ex);
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        try {
            ex.printStackTrace(pw);
            pw.flush();
            Object var5_4 = null;
            pw.close();
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            pw.close();
            throw throwable;
        }
        target.append(stringWriter.getBuffer());
        return target;
    }
}

