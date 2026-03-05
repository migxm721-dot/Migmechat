/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector.sinks.log4j;

import com.projectgoth.fusion.common.log4j.AppenderErrorData;
import com.projectgoth.fusion.recommendation.collector.CollectorSinkException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Log4JSinkException
extends CollectorSinkException {
    private final List<AppenderErrorData> appenderErrors = new ArrayList<AppenderErrorData>();

    public Log4JSinkException() {
    }

    public Log4JSinkException(String msg, Throwable t) {
        super(msg, t);
    }

    public Log4JSinkException(String msg) {
        super(msg);
    }

    public Log4JSinkException(Throwable t) {
        super(t);
    }

    public Log4JSinkException addAppenderErrors(Collection<AppenderErrorData> appenderErrors) {
        this.appenderErrors.addAll(appenderErrors);
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + ".AppenderErrors=[" + this.appenderErrors.toString() + "]";
    }
}

