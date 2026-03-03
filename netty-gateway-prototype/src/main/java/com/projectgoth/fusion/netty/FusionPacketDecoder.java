package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Netty {@link ByteToMessageDecoder} skeleton that reassembles raw TCP bytes
 * into {@link FusionPacket} objects.
 *
 * <h2>Fusion packet framing</h2>
 * <p>The existing codebase uses a length-prefixed binary framing described by
 * {@code FusionPacket.haveFusionPacket} / {@code FusionPacket.fromByteBuffer}.
 * Until the main module is on the classpath of this prototype, a minimal stub
 * is included below.  Replace the {@code TODO} blocks with direct calls to the
 * real parser once this module is wired into the main project.
 *
 * <p>This class is part of the non-invasive Netty prototype and does not
 * modify existing {@code ConnectionTCP}/{@code ConnectionHTTP} classes.
 */
public class FusionPacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(FusionPacketDecoder.class);

    /**
     * Minimum bytes needed to read the packet length header (4-byte big-endian int).
     * Adjust to match the actual Fusion framing if it differs.
     */
    private static final int HEADER_LENGTH = 4;

    /**
     * Maximum accepted payload length. Frames advertising a larger payload are
     * rejected and the channel is closed to prevent excessive memory allocation
     * (DoS prevention). Adjust if the Fusion protocol legitimately uses larger frames.
     */
    private static final int MAX_PAYLOAD_LENGTH = 65535;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait until we have at least the header
        if (in.readableBytes() < HEADER_LENGTH) {
            return;
        }

        // Mark the reader index so we can reset if there is not enough data yet
        in.markReaderIndex();

        // TODO: Replace with FusionPacket.haveFusionPacket(byteBuffer) logic
        int payloadLength = in.readInt();

        if (payloadLength <= 0 || payloadLength > MAX_PAYLOAD_LENGTH) {
            LOG.warn("Received invalid payload length {}; closing channel", payloadLength);
            ctx.close();
            return;
        }

        if (in.readableBytes() < payloadLength) {
            // Not enough data yet – reset and wait for more
            in.resetReaderIndex();
            return;
        }

        byte[] payload = new byte[payloadLength];
        in.readBytes(payload);

        // TODO: Replace with FusionPacket.fromByteBuffer(byteBuffer) to produce the
        //       real FusionPacket instance once this module can depend on the main jar.
        FusionPacket packet = FusionPacket.fromRawPayload(payload);
        LOG.debug("Decoded FusionPacket type={} len={}", packet.getType(), payloadLength);
        out.add(packet);
    }
}
