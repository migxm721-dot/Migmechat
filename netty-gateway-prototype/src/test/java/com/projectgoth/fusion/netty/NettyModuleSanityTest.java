package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity tests that verify the Netty pipeline initialises correctly and basic
 * encode/decode round-trips work without requiring a real network socket.
 *
 * Uses Netty's {@link EmbeddedChannel} for in-process testing.
 */
class NettyModuleSanityTest {

    @Test
    void pipelineInitializesWithoutError() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new FusionPacketDecoder(),
                new FusionPacketEncoder(),
                new GatewayChannelHandler()
        );
        assertTrue(channel.isActive(), "EmbeddedChannel should be active after pipeline init");
        channel.finish();
    }

    @Test
    void encoderWritesFusionFrameCorrectly() {
        EmbeddedChannel channel = new EmbeddedChannel(new FusionPacketEncoder());

        byte[] payload = "hello".getBytes();
        FusionPacketMessage msg = new FusionPacketMessage(200, payload);
        channel.writeOutbound(msg);

        ByteBuf out = channel.readOutbound();
        assertNotNull(out, "Encoder should produce output");
        try {
            // Header: 1 version + 4 type + 4 length = 9 bytes
            assertEquals(FusionPacketDecoder.HEADER_LENGTH + payload.length, out.readableBytes());

            byte version    = out.readByte();
            int  type       = out.readInt();
            int  length     = out.readInt();

            assertEquals(0x01, version);
            assertEquals(200,  type);
            assertEquals(payload.length, length);
        } finally {
            out.release();
        }
        channel.finish();
    }

    @Test
    void decoderReassemblesFragmentedFrames() {
        EmbeddedChannel channel = new EmbeddedChannel(new FusionPacketDecoder());

        byte[] payload = "world".getBytes();

        // Feed the header as the first fragment (decoder needs to wait for full payload).
        ByteBuf header = Unpooled.buffer(FusionPacketDecoder.HEADER_LENGTH);
        header.writeByte(0x01);            // version
        header.writeInt(202);              // type
        header.writeInt(payload.length);   // payload length
        channel.writeInbound(header);
        assertNull(channel.readInbound(), "Decoder must not emit a message on header-only fragment");

        // Feed the payload as the second fragment.
        ByteBuf body = Unpooled.copiedBuffer(payload);
        channel.writeInbound(body);
        FusionPacketMessage decoded = channel.readInbound();
        assertNotNull(decoded, "Decoder should emit a message after full frame received");
        assertEquals(202, decoded.getPacketType());
        assertArrayEquals(payload, decoded.getPayload());

        channel.finish();
    }
}
