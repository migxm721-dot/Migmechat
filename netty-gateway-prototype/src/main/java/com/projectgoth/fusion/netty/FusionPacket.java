package com.projectgoth.fusion.netty;

/**
 * Minimal local stub mirroring the public API of the main project's
 * {@code com.projectgoth.fusion.packet.FusionPacket} class.
 *
 * <p>This stub exists only to make the prototype module self-contained during
 * development.  Once this module is wired into the main project build (i.e.
 * the main module is declared as a {@code provided} or {@code compile}
 * dependency), remove this class and replace all usages with the real
 * {@code FusionPacket}.
 *
 * <p>Replace the {@code TODO} markers in {@link FusionPacketDecoder} and
 * {@link FusionPacketEncoder} with calls to:
 * <ul>
 *   <li>{@code FusionPacket.haveFusionPacket(java.nio.ByteBuffer)} – check frame completeness</li>
 *   <li>{@code FusionPacket.fromByteBuffer(java.nio.ByteBuffer)} – parse a packet</li>
 *   <li>{@code FusionPacket#toByteBuffer()} – serialise a packet</li>
 * </ul>
 */
public final class FusionPacket {

    private final int type;
    private final byte[] payload;

    private FusionPacket(int type, byte[] payload) {
        this.type = type;
        this.payload = payload.clone();
    }

    /**
     * Stub factory – mirrors {@code FusionPacket.fromByteBuffer}.
     *
     * @param rawPayload raw bytes read after the length header
     * @return a new {@code FusionPacket} wrapping the payload
     */
    public static FusionPacket fromRawPayload(byte[] rawPayload) {
        // Extract the packet type from the first byte if present; default to 0
        int type = (rawPayload.length > 0) ? (rawPayload[0] & 0xFF) : 0;
        return new FusionPacket(type, rawPayload);
    }

    /**
     * Returns the numeric packet type (first byte of payload by convention).
     *
     * @return packet type
     */
    public int getType() {
        return type;
    }

    /**
     * Stub serialiser – mirrors {@code FusionPacket#toByteBuffer}.
     *
     * @return raw payload bytes (does not include the length header)
     */
    public byte[] toRawPayload() {
        return payload.clone();
    }
}
