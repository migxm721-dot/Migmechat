/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common.log4j;

import com.projectgoth.fusion.common.log4j.LoggingInvocationCtx;
import java.util.EmptyStackException;
import java.util.Stack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TLSLoggingInvocationCtxStack
extends ThreadLocal<Stack<LoggingInvocationCtx>> {
    public static TLSLoggingInvocationCtxStack getInstance() {
        return SingletonHolder.getInstance();
    }

    private TLSLoggingInvocationCtxStack() {
    }

    @Override
    protected Stack<LoggingInvocationCtx> initialValue() {
        return new Stack<LoggingInvocationCtx>();
    }

    public LoggingInvocationCtx getCurrentCtx() {
        Stack stk = (Stack)this.get();
        try {
            return (LoggingInvocationCtx)stk.peek();
        }
        catch (EmptyStackException ex) {
            throw new IllegalStateException("Failed sanity check. No invocation context in the stack.forgot to pushCtx?", ex);
        }
    }

    public boolean ctxExists() {
        Stack stk = (Stack)this.get();
        return false == stk.empty();
    }

    public LoggingInvocationCtx pushCtx() {
        Stack stk = (Stack)this.get();
        LoggingInvocationCtx ctx = new LoggingInvocationCtx();
        stk.push(ctx);
        return ctx;
    }

    public void popCtx() {
        Stack stk = (Stack)this.get();
        stk.pop();
    }

    private static class SingletonHolder {
        private static TLSLoggingInvocationCtxStack INSTANCE = new TLSLoggingInvocationCtxStack();

        private SingletonHolder() {
        }

        public static TLSLoggingInvocationCtxStack getInstance() {
            return INSTANCE;
        }
    }
}

