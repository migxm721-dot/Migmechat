package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Netty {@link ByteToMessageDecoder} that reassembles raw TCP bytes into
 * complete {@link FusionPacket} instances and passes them upstream.
 *
 * <h2>Wire format (binary)</h2>
 * <pre>
 *   Offset  Length  Description
 *   ------  ------  -----------
 *     0       1     STX marker (0x02)
 *     1       2     packet type  (short, big-endian)
 *     3       2     transaction ID (short, big-endian)
 *     5       4     content length N (int, big-endian)
 *     9       N     fields payload
 * </pre>
 * Total packet size = {@code N + 9} bytes.
 *
 * <p>The parsing logic mirrors {@code FusionPacket.haveFusionPacket(ByteBuffer)}
 * from the core library.  When the full Fusion core JAR is on the classpath,
 * replace the inline length check and packet construction below with calls to
 * the real {@code FusionPacket} API.
 */
public class FusionPacketDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(FusionPacketDecoder.class);

    /** Minimum header size – STX(1) + type(2) + txnId(2) + contentLength(4). */
    static final int HEADER_SIZE = 9;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait until we have at least the fixed-size header.
        if (in.readableBytes() < HEADER_SIZE) {
            return;
        }

        // Peek at the content-length field without advancing the reader index.
        // Offset 5 from the current reader position holds the 4-byte int.
        int readerIndex = in.readerIndex();
        int contentLength = in.getInt(readerIndex + 5);
        int totalLength = HEADER_SIZE + contentLength;

        if (contentLength < 0 || totalLength < HEADER_SIZE) {
            log.warn("Invalid content length {}; closing channel", contentLength);
            ctx.close();
            return;
        }

        // Not enough bytes yet – wait for more.
        if (in.readableBytes() < totalLength) {
            return;
        }

        // Read exactly one packet's bytes and wrap them.
        byte[] packetBytes = new byte[totalLength];
        in.readBytes(packetBytes);

        // TODO: replace with FusionPacket.parse(packetBytes) once the core JAR
        //       is a declared dependency of this module.
        FusionPacket packet = FusionPacket.fromBytes(packetBytes);
        log.debug("Decoded packet type={} txnId={} length={}",
                packet.getType(), packet.getTransactionId(), totalLength);
        out.add(packet);
    }
}
