# Netty Gateway Prototype

A non-invasive, Netty-based gateway prototype for the Fusion platform.  It runs **in parallel** with the existing `ConnectionTCP`/`ConnectionHTTP` networking layer and does not replace or modify those classes.  The goal is to enable incremental testing and gradual migration toward a Netty-based transport.

---

## Overview

| Class | Role |
|---|---|
| `NettyGatewayInitializer` | `ChannelInitializer<SocketChannel>` – wires the pipeline |
| `FusionPacketDecoder` | `ByteToMessageDecoder` – reassembles TCP bytes into `FusionPacket` objects |
| `FusionPacketEncoder` | `MessageToByteEncoder` – serialises outbound `FusionPacket` objects to bytes |
| `GatewayChannelHandler` | `ChannelInboundHandlerAdapter` – dispatches decoded packets to the application layer |
| `FusionPacket` | Lightweight local value object mirroring the wire format of `com.projectgoth.fusion.packet.FusionPacket` |

---

## Build

### Build the prototype module only (recommended for development)

```bash
mvn -pl netty-gateway-prototype -am clean package
```

### Build with the `netty-prototype` Maven profile

The profile exposes two configuration properties:

| Property | Default | Description |
|---|---|---|
| `netty.gateway.enabled` | `false` | Set to `true` at runtime to start the Netty listener |
| `netty.gateway.port` | `9119` | TCP port the Netty server binds to |

```bash
mvn -Pnetty-prototype -pl netty-gateway-prototype -am clean package
```

---

## Run the unit tests

```bash
mvn -pl netty-gateway-prototype -am clean test
```

---

## Wire configuration

### System property

Pass `-Dnetty.gateway.enabled=true -Dnetty.gateway.port=9119` to the JVM when starting the gateway process.

### Bootstrap example

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import com.projectgoth.fusion.netty.NettyGatewayInitializer;

public class NettyGatewayBootstrap {

    public static void main(String[] args) throws InterruptedException {
        int port = Integer.getInteger("netty.gateway.port", 9119);

        EventLoopGroup bossGroup   = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new NettyGatewayInitializer());

            ChannelFuture f = b.bind(port).sync();
            System.out.println("Netty gateway prototype listening on port " + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```

Run with:

```bash
java -Dnetty.gateway.enabled=true -Dnetty.gateway.port=9119 \
     -cp "netty-gateway-prototype/target/netty-gateway-prototype-*.jar:..." \
     NettyGatewayBootstrap
```

---

## Wiring to the existing `PacketProcessor`

The `GatewayChannelHandler` currently echoes packets back to the client as a stub.  To connect it to the real application layer:

1. Add `com.projectgoth:Fusion` as a `<dependency>` with `<scope>provided</scope>` in `netty-gateway-prototype/pom.xml`.
2. Update `GatewayChannelHandler` to accept a `PacketProcessor` in its constructor (see the TODO comment in the class).
3. Replace the stub echo in `dispatch()` with:
   ```java
   packetProcessor.processPacket(null, packet.toBytes(), null);
   ```
4. Similarly, replace `FusionPacket` usages in `FusionPacketDecoder` with calls to `com.projectgoth.fusion.packet.FusionPacket.parse(byte[])`.

---

## A/B switching

The Netty server binds to a **different port** (default `9119`) from the existing gateway ports.  Use a load-balancer rule or client-side configuration to send a percentage of traffic to `9119` while the existing gateway continues to handle the rest unchanged.

---

## Limitations of this prototype

- The `FusionPacket` class in this module is a local stub that mirrors the wire format.  It does **not** include all field-level parsing that the core library provides.
- The `GatewayChannelHandler` echoes packets rather than processing them — replace the `dispatch()` method as described above.
- TLS, HTTP upgrade, and compression are not yet implemented in this pipeline.
