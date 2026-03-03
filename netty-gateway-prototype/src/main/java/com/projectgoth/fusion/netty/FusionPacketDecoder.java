package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Decodes raw TCP bytes into {@link FusionPacketMessage} objects using the
 * Fusion binary framing protocol.
 *
 * <p>Fusion packet header layout (9 bytes):
 * <pre>
 *   [0]     version  (1 byte)
 *   [1..4]  type     (4 bytes, big-endian int)
 *   [5..8]  length   (4 bytes, big-endian int – payload length after header)
 * </pre>
 *
 * <p>This is a skeleton decoder. Replace the stub parsing logic with calls to
 * the production parser once the netty-gateway-prototype module is declared a
 * compile-scope dependency of the core module:
 * <ul>
 *   <li>{@code FusionPacket.haveFusionPacket(ByteBuffer)} – buffer-availability guard</li>
 *   <li>{@code FusionPacket.fromByteBuffer(ByteBuffer)} – full deserialisation</li>
 * </ul>
 */
public class FusionPacketDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(FusionPacketDecoder.class);

    /** Total header size in bytes (version 1 + type 4 + payload-length 4). */
    static final int HEADER_LENGTH = 9;

    /** Maximum allowed payload size (4 MB) to guard against memory exhaustion. */
    static final int MAX_PAYLOAD_SIZE = 4 * 1024 * 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait until we have at least a full header.
        if (in.readableBytes() < HEADER_LENGTH) {
            return;
        }

        in.markReaderIndex();

        // --- Fusion packet header parsing (stub – replace with FusionPacket.haveFusionPacket) ---
        in.readByte();                      // version byte (ignored in this stub)
        int packetType    = in.readInt();   // packet type (4 bytes, big-endian)
        int payloadLength = in.readInt();   // payload length (4 bytes, big-endian)

        if (payloadLength < 0 || payloadLength > MAX_PAYLOAD_SIZE) {
            log.warn("Invalid payload length {}; closing channel.", payloadLength);
            ctx.close();
            return;
        }

        // Wait until the full payload is available.
        if (in.readableBytes() < payloadLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] payload = new byte[payloadLength];
        in.readBytes(payload);

        // TODO: replace stub construction with:
        //   ByteBuffer buf = ByteBuffer.wrap(rawFrame);
        //   if (FusionPacket.haveFusionPacket(buf)) {
        //       FusionPacket packet = FusionPacket.fromByteBuffer(buf);
        //       out.add(new FusionPacketMessage(packet));
        //   }
        out.add(new FusionPacketMessage(packetType, payload));
        log.debug("Decoded FusionPacket: type={} payloadLen={}", packetType, payloadLength);
    }
}
