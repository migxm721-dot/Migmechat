/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.domain;

public interface Metrics {
    public static final char DELIMETER = ',';

    public String toLine();

    public boolean hasMetrics();
}

