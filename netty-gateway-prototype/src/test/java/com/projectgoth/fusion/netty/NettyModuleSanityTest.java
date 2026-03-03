package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity tests that verify the Netty pipeline initialises and processes
 * basic packet framing correctly.
 */
class NettyModuleSanityTest {

    @Test
    void pipelineInitialisesWithoutError() {
        // Create an EmbeddedChannel using the same initializer that would be
        // used in production (minus the SocketChannel generic type requirement).
        EmbeddedChannel channel = new EmbeddedChannel(
                new FusionPacketDecoder(),
                new FusionPacketEncoder(),
                new GatewayChannelHandler()
        );
        assertTrue(channel.isOpen(), "Channel should be open after initialisation");
        channel.close();
    }

    @Test
    void decoderReassemblesCompleteFrame() {
        EmbeddedChannel channel = new EmbeddedChannel(
                new FusionPacketDecoder(),
                new FusionPacketEncoder(),
                new GatewayChannelHandler()
        );

        byte[] payload = {0x01, 0x02, 0x03};

        // Write a correctly framed packet: 4-byte length header + payload
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(payload.length);
        buf.writeBytes(payload);

        channel.writeInbound(buf);
        channel.flushInbound();

        // GatewayChannelHandler echoes the packet back; verify encoder output
        ByteBuf outbound = channel.readOutbound();
        assertNotNull(outbound, "Expected encoded outbound frame");

        int encodedLength = outbound.readInt();
        assertEquals(payload.length, encodedLength, "Encoded length header must match payload size");

        byte[] encodedPayload = new byte[encodedLength];
        outbound.readBytes(encodedPayload);
        assertArrayEquals(payload, encodedPayload, "Encoded payload must round-trip correctly");

        outbound.release();
        channel.close();
    }

    @Test
    void decoderWaitsForIncompleteFrame() {
        EmbeddedChannel channel = new EmbeddedChannel(new FusionPacketDecoder());

        // Send only the 4-byte header announcing a 10-byte payload, without the payload
        ByteBuf incomplete = Unpooled.buffer();
        incomplete.writeInt(10); // length header only

        channel.writeInbound(incomplete);
        channel.flushInbound();

        // Decoder should not have produced any output yet
        assertNull(channel.readInbound(), "Decoder must not emit a packet for an incomplete frame");

        channel.close();
    }

    @Test
    void fusionPacketStubRoundTrip() {
        byte[] original = {0x05, 0x0A, 0x0B};
        FusionPacket packet = FusionPacket.fromRawPayload(original);

        assertEquals(0x05, packet.getType(), "First byte should be the packet type");
        assertArrayEquals(original, packet.toRawPayload(), "Payload must round-trip unchanged");
    }
}
