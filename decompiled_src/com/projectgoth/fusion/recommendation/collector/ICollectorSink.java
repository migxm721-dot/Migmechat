/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector;

import com.projectgoth.fusion.recommendation.collector.CollectorSinkException;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ICollectorSink<TLoggable> {
    public String getName();

    public void write(Collection<TLoggable> var1) throws CollectorSinkException;
}

