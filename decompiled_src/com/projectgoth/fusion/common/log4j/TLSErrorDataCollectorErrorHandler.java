/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Appender
 *  org.apache.log4j.Logger
 *  org.apache.log4j.spi.ErrorHandler
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.projectgoth.fusion.common.log4j;

import com.projectgoth.fusion.common.log4j.AppenderErrorData;
import com.projectgoth.fusion.common.log4j.LoggingInvocationCtx;
import com.projectgoth.fusion.common.log4j.TLSLoggingInvocationCtxStack;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

public final class TLSErrorDataCollectorErrorHandler
implements ErrorHandler {
    private final ErrorHandler nextErrorHandler;

    private TLSErrorDataCollectorErrorHandler() {
        this(null);
    }

    private TLSErrorDataCollectorErrorHandler(ErrorHandler nextErrorHandler) {
        this.nextErrorHandler = nextErrorHandler;
    }

    public static TLSErrorDataCollectorErrorHandler create() {
        return new TLSErrorDataCollectorErrorHandler();
    }

    public static TLSErrorDataCollectorErrorHandler create(ErrorHandler nextErrorHandler) {
        return new TLSErrorDataCollectorErrorHandler(nextErrorHandler);
    }

    private static LoggingInvocationCtx getCurrentCtx() {
        return TLSLoggingInvocationCtxStack.getInstance().getCurrentCtx();
    }

    public boolean hasEmptyAppenderErrors() {
        return TLSErrorDataCollectorErrorHandler.getCurrentCtx().hasAppenderErrors();
    }

    public void activateOptions() {
        if (this.nextErrorHandler != null) {
            this.nextErrorHandler.activateOptions();
        }
    }

    public void setLogger(Logger logger) {
        if (this.nextErrorHandler != null) {
            this.nextErrorHandler.setLogger(logger);
        }
    }

    public void error(String message, Exception e, int errorCode) {
        LoggingInvocationCtx ctx = TLSErrorDataCollectorErrorHandler.getCurrentCtx();
        ctx.add(new AppenderErrorData(message, null, errorCode, null));
        if (this.nextErrorHandler != null) {
            this.nextErrorHandler.error(message, e, errorCode);
        }
    }

    public void error(String message) {
        LoggingInvocationCtx ctx = TLSErrorDataCollectorErrorHandler.getCurrentCtx();
        ctx.add(new AppenderErrorData(message, null, null, null));
        if (this.nextErrorHandler != null) {
            this.nextErrorHandler.error(message);
        }
    }

    public void error(String message, Exception e, int errorCode, LoggingEvent event) {
        LoggingInvocationCtx ctx = TLSErrorDataCollectorErrorHandler.getCurrentCtx();
        ctx.add(new AppenderErrorData(message, e, errorCode, event));
        if (this.nextErrorHandler != null) {
            this.nextErrorHandler.error(message, e, errorCode, event);
        }
    }

    public void setAppender(Appender appender) {
        if (this.nextErrorHandler != null) {
            this.nextErrorHandler.setAppender(appender);
        }
    }

    public void setBackupAppender(Appender appender) {
        if (this.nextErrorHandler != null) {
            this.nextErrorHandler.setBackupAppender(appender);
        }
    }
}

