package com.migme.fusion.netty.codec;

import com.migme.fusion.protocol.FusionPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes FusionPacket objects into binary format.
 *
 * Packet format:
 * [1 byte: packet type][4 bytes: sequence id][4 bytes: payload length][N bytes: payload]
 */
public class FusionPacketEncoder extends MessageToByteEncoder<FusionPacket> {

    private static final Logger log = LoggerFactory.getLogger(FusionPacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, FusionPacket packet, ByteBuf out) {
        byte[] payload = packet.getPayload();
        int payloadLength = payload != null ? payload.length : 0;

        out.writeByte(packet.getType().getCode());
        out.writeInt(packet.getSequenceId());
        out.writeInt(payloadLength);
        if (payloadLength > 0) {
            out.writeBytes(payload);
        }

        log.debug("Encoded packet: type={}, seqId={}, payloadLen={}", packet.getType(), packet.getSequenceId(), payloadLength);
    }
}
