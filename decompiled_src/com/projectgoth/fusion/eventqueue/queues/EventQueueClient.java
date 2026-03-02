/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.eventqueue.queues;

import com.projectgoth.fusion.eventqueue.Event;

public interface EventQueueClient {
    public void enqueue(Event var1) throws Exception;

    public Event getForProcessing() throws Exception;

    public void processingCompleted(boolean var1) throws Exception;

    public void requeueEventInProcess() throws Exception;

    public void setProcessingQueueID(int var1);

    public int numberOfEventsPending() throws Exception;

    public int numberOfEventsInProcess() throws Exception;

    public int flushPendingQueue() throws Exception;

    public int flushProcessingQueue() throws Exception;

    public void disconnectClient() throws Exception;
}

