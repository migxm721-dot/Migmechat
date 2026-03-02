/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.PacketProcessorCallback;
import com.projectgoth.fusion.gateway.exceptions.FusionRequestException;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class PacketProcessor
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(PacketProcessor.class));
    private PacketProcessorCallback callback;
    private Gateway gateway;
    private ConnectionI connection;
    private Queue<FusionRequest> requestQueue = new LinkedList<FusionRequest>();
    private long lastThrottledExecution;
    private boolean stop;

    public PacketProcessor(PacketProcessorCallback callback, ConnectionI connection) {
        this.callback = callback;
        this.gateway = connection.getGateway();
        this.connection = connection;
    }

    public void run() {
        this.processIncomingQueue();
    }

    public void stopProcessing() {
        this.stop = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getQueueSize() {
        Queue<FusionRequest> queue = this.requestQueue;
        synchronized (queue) {
            return this.requestQueue.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean queueFusionRequest(FusionRequest request) {
        Queue<FusionRequest> queue = this.requestQueue;
        synchronized (queue) {
            if (this.stop) {
                return false;
            }
            if (this.gateway.getMinIntervalBetweenPackets() > 0 && this.requestQueue.size() >= this.gateway.getMaxPacketsToBuffer()) {
                String infractionNote = this.logInfraction();
                for (FusionRequest r : this.requestQueue) {
                    this.callback.onFusionRequestDiscarded(r);
                }
                this.requestQueue.clear();
                this.stop = true;
                UserPrx userPrx = this.connection.getUserPrx();
                if (userPrx != null) {
                    userPrx.disconnectFlooder(infractionNote);
                }
                return false;
            }
            this.requestQueue.add(request);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Request " + request.getType() + " from " + this.connection.getDisplayName() + " queued. " + this.requestQueue.size() + " requests on queue"));
            }
            if (this.requestQueue.size() == 1) {
                this.schedule();
            }
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processIncomingQueue() {
        Queue<FusionRequest> queue;
        block23: {
            FusionRequest request;
            queue = this.requestQueue;
            synchronized (queue) {
                request = this.requestQueue.peek();
                if (request == null) {
                    return;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("Processing packet: %d\n", request.getType()));
            }
            try {
                List<String> delayedPackets;
                FusionPacket[] responses;
                List<String> discardedPackets = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.GatewaySettings.DISCARDED_FUSION_PACKETS));
                String packetSimpleName = request.getSimpleName();
                if (this.stop || discardedPackets.contains(packetSimpleName)) {
                    if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.INSTRUMENT_DISCARDED_PACKETS)) {
                        this.gateway.getSampler().add(request.getSimpleName() + "_discarded", 0L);
                    }
                    this.callback.onFusionRequestDiscarded(request);
                    break block23;
                }
                List<String> blockedPackets = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.GatewaySettings.BLOCKED_FUSION_PACKETS));
                if (blockedPackets.contains(packetSimpleName)) {
                    responses = new FusionPktError(request.getTransactionId(), FusionPktError.Code.UNDEFINED, packetSimpleName + " disabled for now. Please try again later.").toArray();
                    if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.INSTRUMENT_BLOCKED_PACKETS)) {
                        this.gateway.getSampler().add(request.getSimpleName() + "_blocked", 0L);
                    }
                } else {
                    try {
                        long startTime = System.nanoTime();
                        responses = request.process(this.connection);
                        if (SystemProperty.getBool(SystemPropertyEntities.GatewaySettings.INSTRUMENT_PACKETS)) {
                            this.gateway.getSampler().add(request.getSimpleName(), (System.nanoTime() - startTime) / 1000L);
                        }
                    }
                    catch (FusionRequestException e) {
                        responses = new FusionPktError(request.getTransactionId(), FusionPktError.Code.UNDEFINED, e.getMessage()).toArray();
                    }
                }
                if ((delayedPackets = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.GatewaySettings.DELAYED_FUSION_PACKETS))).contains(packetSimpleName)) {
                    long delayInMilli = SystemProperty.getLong(SystemPropertyEntities.GatewaySettings.DEFAULT_RESPONSE_DELAY);
                    request.setResponseDelay(delayInMilli);
                }
                if (request.getResponseDelay() > 0L) {
                    this.gateway.scheduleTask(request.getThreadPool(), new DelayedNotificationTask(request, responses), request.getResponseDelay(), TimeUnit.MILLISECONDS);
                } else {
                    this.callback.onFusionRequestProcessed(request, responses);
                }
            }
            catch (Exception e) {
                if (SystemPropertyEntities.Temp.Cache.se606NpeFixEnabled.getValue().booleanValue()) {
                    log.warn((Object)("Unexpected exception caught while processing packet " + request.getType() + " e=" + e), (Throwable)e);
                } else {
                    log.warn((Object)("Unexpected exception caught while processing packet " + request.getType()), (Throwable)e);
                }
                this.callback.onFusionRequestException(request, e);
            }
        }
        queue = this.requestQueue;
        synchronized (queue) {
            this.requestQueue.poll();
            this.schedule();
        }
    }

    private void schedule() {
        block7: {
            FusionRequest request = this.requestQueue.peek();
            if (request == null) {
                return;
            }
            try {
                long interval = this.gateway.getMinIntervalBetweenPackets();
                if (interval == 0L || !request.throttlingRequired()) {
                    this.gateway.executeTask(request.getThreadPool(), this);
                } else if (System.currentTimeMillis() - this.lastThrottledExecution > interval) {
                    this.gateway.executeTask(request.getThreadPool(), this);
                    this.lastThrottledExecution = System.currentTimeMillis();
                } else {
                    this.gateway.scheduleTask(request.getThreadPool(), this, interval, TimeUnit.MILLISECONDS);
                    this.lastThrottledExecution = System.currentTimeMillis() + interval;
                }
            }
            catch (RejectedExecutionException e) {
                FusionPacket error = request.getErrorPacket("Server too busy");
                if (error == null) break block7;
                this.callback.onFusionRequestProcessed(request, error.toArray());
            }
        }
    }

    private String logInfraction() {
        StringBuilder builder = new StringBuilder();
        for (FusionRequest requestOnQueue : this.requestQueue) {
            if (builder.length() == 0) {
                builder.append(requestOnQueue.getType());
                continue;
            }
            builder.append(",").append(requestOnQueue.getType());
        }
        String infractionNote = this.connection.getDisplayName() + " exceeded throttling rate. Packets [" + builder.toString() + "]";
        log.warn((Object)infractionNote);
        return infractionNote;
    }

    private class DelayedNotificationTask
    implements Runnable {
        private FusionRequest request;
        private FusionPacket[] responses;

        public DelayedNotificationTask(FusionRequest request, FusionPacket[] responses) {
            this.request = request;
            this.responses = responses;
        }

        public void run() {
            PacketProcessor.this.callback.onFusionRequestProcessed(this.request, this.responses);
        }
    }
}

