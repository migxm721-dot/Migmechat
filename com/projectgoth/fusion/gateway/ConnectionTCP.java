/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway;

import Ice.Current;
import com.projectgoth.fusion.chatsync.ChatSyncStorageExecutor;
import com.projectgoth.fusion.chatsync.MessageStatusEvent;
import com.projectgoth.fusion.chatsync.ServerGeneratedReceivedEventPusher;
import com.projectgoth.fusion.common.ByteBufferHelper;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.fdl.enums.DeviceModeType;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.Gateway;
import com.projectgoth.fusion.gateway.GatewayContext;
import com.projectgoth.fusion.gateway.PacketProcessor;
import com.projectgoth.fusion.gateway.PacketProcessorCallback;
import com.projectgoth.fusion.gateway.packet.FusionPacketFactory;
import com.projectgoth.fusion.gateway.packet.FusionPktMessage;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

public class ConnectionTCP
extends ConnectionI
implements PacketProcessorCallback {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ConnectionTCP.class));
    private static final int READ_BUFFER_SIZE = 1024;
    private static final int MAX_READ_BUFFER_SIZE = 358400;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer sendBuffer;
    private PacketProcessor packetProcessor;
    private Queue<FusionPacket> outgoingQueue = new LinkedList<FusionPacket>();
    private AtomicBoolean writing = new AtomicBoolean(false);
    private boolean sessionTerminated;

    public ConnectionTCP(Gateway gateway, SocketChannel channel, GatewayContext gatewayContext) {
        super(gateway, channel, gatewayContext);
        this.packetProcessor = new PacketProcessor(this, this);
    }

    public Gateway.ServerType getServerType() {
        return Gateway.ServerType.TCP;
    }

    public void onSessionTerminated() {
        super.onSessionTerminated();
        this.sessionTerminated = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onChannelReadable() {
        try {
            if (this.readBuffer.remaining() == 0) {
                this.readBuffer = ByteBufferHelper.adjustSize(this.readBuffer, 1024, 358400, 2.0);
            }
            int bytesRead = this.channel.read(this.readBuffer);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Read " + bytesRead + " bytes from " + this.getDisplayName()));
            }
            if (bytesRead == -1) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Terminating connection " + this.getDisplayName() + " - Socket closed (EOF)"));
                }
                this.onSessionTerminated();
                this.disconnect();
                return;
            }
            if (bytesRead == 0) {
                log.info((Object)"onChannelReadable called with zero bytes on the channel??");
            }
            this.readBuffer.flip();
            try {
                while (FusionPacket.haveFusionPacket(this.readBuffer)) {
                    FusionRequest request = FusionPacketFactory.readSpecificRequest(this.readBuffer);
                    if (request == null) {
                        throw new Exception("Failed to convert Fusion packet into a request");
                    }
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Received packet " + request.getType() + " from " + this.getDisplayName() + "\n" + request.toString() + "\n"));
                    }
                    this.accessed();
                    this.packetProcessor.queueFusionRequest(request);
                }
                Object var4_7 = null;
                this.readBuffer.compact();
            }
            catch (Throwable throwable) {
                Object var4_8 = null;
                this.readBuffer.compact();
                throw throwable;
            }
            this.gateway.registerConnection(this, 1);
        }
        catch (BufferOverflowException e) {
            log.warn((Object)("Terminating connection " + this.getDisplayName() + " - Read buffer is full"));
            this.onSessionTerminated();
            this.disconnect();
        }
        catch (IOException e) {
            log.warn((Object)("Terminating connection " + this.getDisplayName() + " - " + e.getMessage()), (Throwable)e);
            this.onSessionTerminated();
            this.disconnect();
        }
        catch (FusionException fe) {
            if (SystemPropertyEntities.Temp.Cache.se493WebSocketsEnabled.getValue().booleanValue()) {
                log.warn((Object)("Terminating connection " + this.getDisplayName() + " - Trying to make invalid connection to gateway"), (Throwable)((Object)fe));
                this.disconnect();
            }
        }
        catch (Exception e) {
            log.error((Object)("Terminating connection " + this.getDisplayName() + " - " + e.getMessage()), (Throwable)e);
            this.onSessionTerminated();
            this.disconnect();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onChannelWritable() {
        ConnectionTCP connectionTCP = this;
        synchronized (connectionTCP) {
            try {
                FusionPacket pkt;
                if (this.sendBuffer != null && this.sendBuffer.hasRemaining()) {
                    this.channel.write(this.sendBuffer);
                    if (this.sendBuffer.hasRemaining()) {
                        this.gateway.registerConnection(this, 4);
                        return;
                    }
                }
                Queue<FusionPacket> queue = this.outgoingQueue;
                synchronized (queue) {
                    pkt = this.outgoingQueue.poll();
                }
                while (pkt != null) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Sending packet " + pkt.getType() + " to " + this.getDisplayName() + "\n" + pkt.toString() + "\n"));
                    }
                    this.sendBuffer = ByteBuffer.wrap(pkt.toByteArray());
                    this.channel.write(this.sendBuffer);
                    this.sendServerGeneratedReceivedEventIfNeeded(pkt);
                    if (this.sendBuffer.hasRemaining()) {
                        this.gateway.registerConnection(this, 4);
                        return;
                    }
                    queue = this.outgoingQueue;
                    synchronized (queue) {
                        pkt = this.outgoingQueue.poll();
                    }
                }
                this.writing.set(false);
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Terminating " + this.getDisplayName() + " - " + e.getMessage()));
                }
                this.onSessionTerminated();
                this.disconnect();
                this.writing.set(false);
            }
        }
    }

    private void sendServerGeneratedReceivedEventIfNeeded(FusionPacket pkt) {
        try {
            if (SystemPropertyEntities.MessageStatusEventSettings.Cache.enabled.getValue().booleanValue() && pkt.getType() == 500 && ((FusionPktMessage)pkt).getMessageTypeEnum() == MessageType.FUSION && ((FusionPktMessage)pkt).isIndividualMessage() && !MessageStatusEvent.isClientMessageStatusEventCapable(this.deviceType, this.clientVersion)) {
                ServerGeneratedReceivedEventPusher receivedEvent = new ServerGeneratedReceivedEventPusher((FusionPktMessage)pkt, Enums.MessageStatusEventTypeEnum.RECEIVED, true, this.gatewayContext.getRegistryPrx());
                ChatSyncStorageExecutor.getInstance().scheduleStorage(receivedEvent);
            }
        }
        catch (Exception e) {
            log.error((Object)("Exception processing server-generated RECEIVED event, event sender=" + this.getUsername() + ": e=" + e), (Throwable)e);
        }
    }

    public void onCreatingSession() {
        super.onCreatingSession();
        this.addConnectionToIceAdaptor(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendFusionPacket(FusionPacket packet) throws FusionException {
        block8: {
            try {
                if (!this.channel.isOpen()) {
                    throw new Exception("Connection closed");
                }
                if (this.isDeviceAwake()) {
                    Queue<FusionPacket> queue = this.outgoingQueue;
                    synchronized (queue) {
                        this.outgoingQueue.add(packet);
                    }
                    if (this.writing.compareAndSet(false, true)) {
                        this.onChannelWritable();
                    }
                    break block8;
                }
                this.queueForDelivery(new FusionPacket[]{packet});
            }
            catch (Exception e) {
                throw new FusionException(e.getMessage());
            }
        }
    }

    public void onFusionRequestProcessed(FusionRequest request, FusionPacket[] responses) {
        if (responses != null) {
            if (this.isDeviceAwake()) {
                this.streamPackets(responses);
            } else {
                this.queueForDelivery(responses);
            }
        }
        if (this.sessionTerminated) {
            this.disconnect();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void streamPackets(FusionPacket[] packets) {
        Queue<FusionPacket> queue = this.outgoingQueue;
        synchronized (queue) {
            this.outgoingQueue.addAll(Arrays.asList(packets));
        }
        this.onChannelWritable();
    }

    public void onFusionRequestDiscarded(FusionRequest request) {
    }

    public void onFusionRequestException(FusionRequest request, Exception e) {
        this.onSessionTerminated();
        this.disconnect();
    }

    public boolean processPacket(ConnectionPrx requestingConnection, byte[] packet, Current __current) throws FusionException {
        return false;
    }

    public void packetProcessed(byte[] result, Current __current) {
    }

    public void silentlyDropIncomingPackets(Current __current) {
        log.warn((Object)("No longer processing incoming packets for this connection for user [" + this.username + "]"));
        this.packetProcessor.stopProcessing();
    }

    public synchronized void onDeviceModeChanged(DeviceModeType mode) {
        switch (mode) {
            case AWAKE: {
                if (!log.isDebugEnabled()) break;
                log.debug((Object)("Device " + this.getDisplayName() + " woke up..."));
                break;
            }
            case SLEEP: {
                if (!log.isDebugEnabled()) break;
                log.debug((Object)("Device " + this.getDisplayName() + " went into sleep mode..."));
                break;
            }
        }
    }

    private void queueForDelivery(FusionPacket[] packets) {
        for (FusionPacket pkt : packets) {
            if (pkt == null) continue;
            try {
                if (!ConnectionTCP.packetTypeIsBlockedInSleepMode(pkt.getType())) {
                    if (500 == pkt.getType() && !((FusionPktMessage)pkt).isPrivateOrGroupChatMessage()) continue;
                    this.streamPackets(new FusionPacket[]{pkt});
                    continue;
                }
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Packet " + pkt.getType() + " dropped for " + this.getDisplayName() + "..."));
            }
            catch (Exception e) {
                log.warn((Object)("Failed to send packet: " + pkt.getType()));
            }
        }
    }
}

