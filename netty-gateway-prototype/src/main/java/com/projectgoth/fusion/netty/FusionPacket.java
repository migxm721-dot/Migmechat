package com.projectgoth.fusion.netty;

import java.util.Arrays;

/**
 * Lightweight value object representing a Fusion binary protocol packet within
 * the Netty gateway prototype module.
 *
 * <p>This class mirrors the public wire-format API of
 * {@code com.projectgoth.fusion.packet.FusionPacket} without depending on the
 * core JAR (which lives in a private Nexus repository). When the core module is
 * added as a dependency, replace usages of this class with the real
 * {@code FusionPacket} and remove this file.
 *
 * <h2>Wire format</h2>
 * <pre>
 *   [0]       STX marker  (0x02)
 *   [1..2]    packet type (short, big-endian)
 *   [3..4]    transaction ID (short, big-endian)
 *   [5..8]    content length N (int, big-endian)
 *   [9..9+N)  fields payload
 * </pre>
 */
public final class FusionPacket {

    /** Minimum header length: STX(1) + type(2) + txnId(2) + contentLength(4). */
    public static final int HEADER_SIZE = 9;

    private final short type;
    private final short transactionId;
    private final byte[] payload;

    public FusionPacket(short type, short transactionId, byte[] payload) {
        this.type = type;
        this.transactionId = transactionId;
        this.payload = payload == null ? new byte[0] : payload.clone();
    }

    public short getType() {
        return type;
    }

    public short getTransactionId() {
        return transactionId;
    }

    public byte[] getPayload() {
        return payload.clone();
    }

    /**
     * Serialises this packet to the Fusion binary wire format.
     *
     * @return byte array ready to send over the wire.
     */
    public byte[] toBytes() {
        int total = HEADER_SIZE + payload.length;
        byte[] buf = new byte[total];
        buf[0] = 0x02;
        buf[1] = (byte) (type >> 8);
        buf[2] = (byte) type;
        buf[3] = (byte) (transactionId >> 8);
        buf[4] = (byte) transactionId;
        int len = payload.length;
        buf[5] = (byte) (len >> 24);
        buf[6] = (byte) (len >> 16);
        buf[7] = (byte) (len >> 8);
        buf[8] = (byte) len;
        System.arraycopy(payload, 0, buf, HEADER_SIZE, payload.length);
        return buf;
    }

    /**
     * Parses a single packet from a complete byte array.
     *
     * <p>Mirrors the logic of {@code FusionPacket.haveFusionPacket(ByteBuffer)}
     * in the core library.
     *
     * @param data raw bytes of exactly one packet (length must equal
     *             {@code HEADER_SIZE + contentLength}).
     * @return parsed {@link FusionPacket}.
     * @throws IllegalArgumentException if the byte array is malformed.
     */
    public static FusionPacket fromBytes(byte[] data) {
        if (data == null || data.length < HEADER_SIZE) {
            throw new IllegalArgumentException("Packet too short: " + (data == null ? 0 : data.length));
        }
        short type = (short) (((data[1] & 0xFF) << 8) | (data[2] & 0xFF));
        short txnId = (short) (((data[3] & 0xFF) << 8) | (data[4] & 0xFF));
        int contentLength = ((data[5] & 0xFF) << 24)
                          | ((data[6] & 0xFF) << 16)
                          | ((data[7] & 0xFF) << 8)
                          |  (data[8] & 0xFF);
        if (contentLength < 0 || data.length < HEADER_SIZE + contentLength) {
            throw new IllegalArgumentException("Truncated packet data");
        }
        byte[] payload = Arrays.copyOfRange(data, HEADER_SIZE, HEADER_SIZE + contentLength);
        return new FusionPacket(type, txnId, payload);
    }

    @Override
    public String toString() {
        return "FusionPacket{type=" + type + ", txnId=" + transactionId
                + ", payloadLen=" + payload.length + '}';
    }
}
