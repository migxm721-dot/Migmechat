package com.projectgoth.fusion.netty;

/**
 * Lightweight carrier object that travels through the Netty pipeline after
 * {@link FusionPacketDecoder} has framed a complete Fusion binary packet.
 *
 * <p>This stub mirrors the public surface of {@code FusionPacket} that the
 * encoder/handler needs. Replace the constructor call in
 * {@link FusionPacketDecoder} with {@code FusionPacket.fromByteBuffer()} to
 * use the real domain object once the core module is available on the
 * classpath.
 */
public final class FusionPacketMessage {

    private final int packetType;
    private final byte[] payload;

    public FusionPacketMessage(int packetType, byte[] payload) {
        this.packetType = packetType;
        this.payload    = payload;
    }

    public int getPacketType() {
        return packetType;
    }

    public byte[] getPayload() {
        return payload.clone();
    }
}
