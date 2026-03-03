package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes {@link FusionPacketMessage} objects into raw bytes using the Fusion
 * binary framing protocol.
 *
 * <p>Output format mirrors the decoding layout in {@link FusionPacketDecoder}:
 * <pre>
 *   [0]     version  = 0x01  (1 byte)
 *   [1..4]  type              (4 bytes, big-endian int)
 *   [5..8]  payload length    (4 bytes, big-endian int)
 *   [9..]   payload           (variable)
 * </pre>
 *
 * <p>This is a skeleton encoder. Replace stub serialisation with a call to the
 * production serialiser once the netty-gateway-prototype module is wired to the
 * core module:
 * <ul>
 *   <li>{@code FusionPacket.toByteBuffer()} – full serialisation</li>
 * </ul>
 */
public class FusionPacketEncoder extends MessageToByteEncoder<FusionPacketMessage> {

    private static final Logger log = LoggerFactory.getLogger(FusionPacketEncoder.class);

    /** Protocol version byte written into every frame header. */
    private static final byte PROTOCOL_VERSION = 0x01;

    @Override
    protected void encode(ChannelHandlerContext ctx, FusionPacketMessage msg, ByteBuf out) {
        byte[] payload = msg.getPayload();

        // TODO: replace stub serialisation with:
        //   ByteBuffer buf = fusionPacket.toByteBuffer();
        //   out.writeBytes(buf);

        out.writeByte(PROTOCOL_VERSION);
        out.writeInt(msg.getPacketType());
        out.writeInt(payload.length);
        out.writeBytes(payload);

        log.debug("Encoded FusionPacket: type={} payloadLen={}", msg.getPacketType(), payload.length);
    }
}
