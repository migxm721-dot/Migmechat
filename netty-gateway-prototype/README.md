# netty-gateway-prototype

Non-invasive Netty-based gateway adapter prototype for the Migmechat / Fusion backend.

This module provides a safe, parallel implementation of the TCP gateway using
[Netty 4.1.x](https://netty.io/) to allow incremental migration away from the
legacy `NIO Selector/SocketChannel` (`ConnectionTCP`/`ConnectionHTTP`) code
**without modifying existing production classes**.

---

## Architecture overview

```
Client ──TCP──▶  [FusionPacketDecoder]
                        │  (FusionPacket)
                        ▼
               [GatewayChannelHandler]  ──▶  PacketProcessor (TODO: wire real impl)
                        │  (FusionPacket echo in prototype mode)
                        ▼
                [FusionPacketEncoder]
                        │
Client ◀──TCP──  (response bytes)
```

Key classes:

| Class | Role |
|---|---|
| `NettyGatewayInitializer` | `ChannelInitializer` – assembles the pipeline |
| `FusionPacketDecoder` | `ByteToMessageDecoder` – reassembles length-prefixed frames |
| `FusionPacketEncoder` | `MessageToByteEncoder` – serialises `FusionPacket` to wire bytes |
| `GatewayChannelHandler` | `ChannelInboundHandlerAdapter` – forwards packets to `PacketProcessor` |
| `FusionPacket` | Local stub – **replace with** `com.projectgoth.fusion.packet.FusionPacket` |

---

## Prerequisites

- Java 17+
- Maven 3.6+

---

## Building the prototype

```bash
# Build only this module (and resolve its dependencies)
mvn -pl netty-gateway-prototype -am clean package

# Build and run unit tests
mvn -pl netty-gateway-prototype -am clean verify
```

To enable the `netty-prototype` Maven profile (sets `netty.prototype.enabled=true`):

```bash
mvn -pl netty-gateway-prototype -am -P netty-prototype clean verify
```

---

## Running the Netty server prototype

The prototype does not ship a `main` method by default because it is intended
to be activated alongside the existing gateway process.  The shortest path to
binding a port is to add a small bootstrap class or use the snippet below in a
test/smoke-test runner:

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import com.projectgoth.fusion.netty.NettyGatewayInitializer;

public class NettyGatewayMain {
    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("Server.ServerPort", 9119);

        EventLoopGroup bossGroup   = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new NettyGatewayInitializer());

            ChannelFuture f = b.bind(port).sync();
            System.out.printf("Netty gateway prototype listening on port %d%n", port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```

Configure the port via a system property:

```bash
java -DServer.ServerPort=9119 -jar netty-gateway-prototype-1.0.0-SNAPSHOT.jar
```

---

## Wiring configuration (Server / ServerPort properties)

The existing `project.properties` / `database.properties` files already define
server properties.  To integrate:

1. Load the properties file in `NettyGatewayMain` (or the calling code):
   ```java
   Properties p = new Properties();
   p.load(new FileInputStream("project.properties"));
   int port = Integer.parseInt(p.getProperty("Server.ServerPort", "9119"));
   ```
2. Pass the port value to `b.bind(port)`.

---

## Replacing the stubs with real Fusion classes

The prototype uses a local `FusionPacket` stub so it can be built without
depending on the main Fusion module.  Once ready to integrate:

1. Add the main Fusion module as a `provided` dependency in `pom.xml`.
2. Remove `netty-gateway-prototype/src/main/java/com/projectgoth/fusion/netty/FusionPacket.java`.
3. In `FusionPacketDecoder.decode()`:  
   Replace `FusionPacket.fromRawPayload(payload)` with  
   `FusionPacket.fromByteBuffer(byteBuffer)` (the real parser).
4. In `FusionPacketEncoder.encode()`:  
   Replace `msg.toRawPayload()` with `msg.toByteBuffer()`.
5. In `GatewayChannelHandler.processPacket()`:  
   Replace the echo stub with a call to `PacketProcessor.process(connection, packet)`,  
   adapting the Netty `ChannelHandlerContext` to the existing `Connection` interface.

---

## Running the CI build

The GitHub Actions workflow (`.github/workflows/maven.yml`) builds and tests
this module automatically on every PR targeting the default branch:

```yaml
# runs: mvn -T1C -DskipTests=false clean verify
```
