/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.recommendation.collector;

import com.projectgoth.fusion.recommendation.collector.CollectorTransformationException;
import com.projectgoth.fusion.slice.CollectedDataIce;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ICollectorTransformation<TLoggable> {
    public String getName();

    public Collection<TLoggable> toLoggables(CollectedDataIce var1) throws CollectorTransformationException;
}

