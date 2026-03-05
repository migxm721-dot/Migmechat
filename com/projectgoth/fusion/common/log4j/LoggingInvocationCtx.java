/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common.log4j;

import com.projectgoth.fusion.common.log4j.AppenderErrorData;
import java.util.LinkedList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LoggingInvocationCtx {
    private final List<AppenderErrorData> appenderErrorDataList = new LinkedList<AppenderErrorData>();

    public void add(AppenderErrorData appenderErrorData) {
        this.appenderErrorDataList.add(appenderErrorData);
    }

    public List<AppenderErrorData> getAppenderErrorDataList() {
        return this.appenderErrorDataList;
    }

    public boolean hasAppenderErrors() {
        return false == this.appenderErrorDataList.isEmpty();
    }
}

