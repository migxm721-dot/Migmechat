/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.recommendation.collector.sinks.log4j;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.log4j.LoggingInvocationCtx;
import com.projectgoth.fusion.common.log4j.TLSLoggingInvocationCtxStack;
import com.projectgoth.fusion.recommendation.collector.ICollectorSink;
import com.projectgoth.fusion.recommendation.collector.sinks.log4j.Log4JSinkException;
import java.util.Collection;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Log4JSink<TLoggable>
implements ICollectorSink<TLoggable> {
    private final String name;
    private final Logger loggerSink;

    public Log4JSink(String name, String loggerSinkCategoryName) {
        this.name = name;
        String fullLoggerSinkCategory = "LOG4JSINK";
        if (!StringUtil.isBlank(loggerSinkCategoryName)) {
            fullLoggerSinkCategory = fullLoggerSinkCategory + ":" + loggerSinkCategoryName;
        }
        this.loggerSink = Logger.getLogger((String)fullLoggerSinkCategory);
    }

    @Override
    public String getName() {
        return this.name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(Collection<TLoggable> loggableColl) throws Log4JSinkException {
        TLSLoggingInvocationCtxStack ctxStack = TLSLoggingInvocationCtxStack.getInstance();
        ctxStack.pushCtx();
        try {
            for (TLoggable loggable : loggableColl) {
                this.loggerSink.info((Object)this.toString(loggable));
            }
            LoggingInvocationCtx ctx = ctxStack.getCurrentCtx();
            if (ctx.hasAppenderErrors()) {
                throw new Log4JSinkException("Found some errors while appending logs").addAppenderErrors(ctx.getAppenderErrorDataList());
            }
            Object var6_5 = null;
            ctxStack.popCtx();
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            ctxStack.popCtx();
            throw throwable;
        }
    }

    protected String toString(TLoggable loggable) {
        if (loggable == null) {
            return "";
        }
        return loggable.toString();
    }
}

