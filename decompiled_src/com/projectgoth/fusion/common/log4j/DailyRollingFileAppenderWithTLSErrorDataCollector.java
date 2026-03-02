/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.DailyRollingFileAppender
 *  org.apache.log4j.spi.ErrorHandler
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.projectgoth.fusion.common.log4j;

import com.projectgoth.fusion.common.log4j.TLSErrorDataCollectorErrorHandler;
import com.projectgoth.fusion.common.log4j.TLSLoggingInvocationCtxStack;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

public class DailyRollingFileAppenderWithTLSErrorDataCollector
extends DailyRollingFileAppender {
    public DailyRollingFileAppenderWithTLSErrorDataCollector() {
        super.setErrorHandler((ErrorHandler)TLSErrorDataCollectorErrorHandler.create());
    }

    public synchronized void setErrorHandler(ErrorHandler eh) {
        super.setErrorHandler((ErrorHandler)TLSErrorDataCollectorErrorHandler.create(eh));
    }

    public synchronized void doAppend(LoggingEvent event) {
        if (!TLSLoggingInvocationCtxStack.getInstance().ctxExists()) {
            throw new IllegalStateException("DailyRollingFileAppenderWithTLSErrorDataCollector requires callers to push context prior to writing logs.");
        }
        super.doAppend(event);
    }
}

