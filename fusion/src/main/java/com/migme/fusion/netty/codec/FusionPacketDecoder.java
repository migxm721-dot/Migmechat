package com.migme.fusion.netty.codec;

import com.migme.fusion.protocol.FusionPacket;
import com.migme.fusion.protocol.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Decodes Fusion Binary Protocol packets from the byte stream.
 *
 * Packet format:
 * [1 byte: packet type][4 bytes: sequence id][4 bytes: payload length][N bytes: payload]
 */
public class FusionPacketDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(FusionPacketDecoder.class);

    private static final int HEADER_SIZE = 9; // 1 + 4 + 4

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < HEADER_SIZE) {
            return;
        }

        in.markReaderIndex();

        int typeCode = in.readUnsignedByte();
        int sequenceId = in.readInt();
        int payloadLength = in.readInt();

        if (in.readableBytes() < payloadLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] payload = new byte[payloadLength];
        if (payloadLength > 0) {
            in.readBytes(payload);
        }

        try {
            PacketType packetType = PacketType.fromCode(typeCode);
            FusionPacket packet = FusionPacket.builder()
                    .type(packetType)
                    .sequenceId(sequenceId)
                    .payload(payload)
                    .build();
            out.add(packet);
            log.debug("Decoded packet: type={}, seqId={}, payloadLen={}", packetType, sequenceId, payloadLength);
        } catch (IllegalArgumentException e) {
            log.warn("Received unknown packet type code: 0x{}", Integer.toHexString(typeCode));
        }
    }
}
