/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.util.LinkedList;
import java.util.Queue;

public class SingleRateLimiter {
    private Queue<Long> eventHistory = new LinkedList<Long>();
    private long rateLimitTime;
    private long rateLimitAmount;

    public SingleRateLimiter(long rateLimitTime, long rateLimitAmount) {
        this.rateLimitTime = rateLimitTime;
        this.rateLimitAmount = rateLimitAmount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean onEvent(Long now) {
        Queue<Long> queue = this.eventHistory;
        synchronized (queue) {
            if ((long)this.eventHistory.size() > this.rateLimitAmount) {
                this.eventHistory.poll();
            }
            this.eventHistory.offer(now);
            while (now - this.eventHistory.peek() > this.rateLimitTime) {
                this.eventHistory.poll();
            }
            return (long)this.eventHistory.size() > this.rateLimitAmount;
        }
    }
}

