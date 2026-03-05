/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.domain;

import com.projectgoth.fusion.reputation.domain.Metrics;

public class PhoneCallMetrics
implements Metrics {
    private String username;
    private int duration;

    public void reset(String username) {
        this.username = username;
        this.duration = 0;
    }

    public String getUsername() {
        return this.username;
    }

    public int getDuration() {
        return this.duration;
    }

    public void addDuration(int duration) {
        this.duration += duration;
    }

    public boolean hasMetrics() {
        return this.duration != 0;
    }

    public String toLine() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.username).append(',').append(this.duration);
        return builder.toString();
    }
}

