# netty-gateway-prototype

A non-invasive, incremental Netty 4.1.x adapter prototype for the Fusion
gateway.  It provides a parallel TCP/HTTP ingress path that can be tested
alongside the existing `ConnectionTCP` / `ConnectionHTTP` classes without
modifying any production code.

---

## Why a prototype?

Replacing the entire NIO `Selector`/`SocketChannel` networking layer in-place
is risky.  This module lets you:

* Run the Netty stack **in parallel** on a separate port (e.g. `9119`) while
  production traffic continues on the legacy stack.
* Benchmark and profile Netty performance independently.
* Incrementally wire `GatewayChannelHandler` into the existing
  `PacketProcessor` API call-by-call.
* Enable or disable the prototype via a Maven profile or system property —
  no code changes required.

---

## Prerequisites

| Tool  | Minimum version |
|-------|-----------------|
| JDK   | 17              |
| Maven | 3.8             |

---

## Building

Build **only** this module (fastest iteration loop):

```bash
mvn -pl netty-gateway-prototype -am clean package
```

Build and run tests:

```bash
mvn -pl netty-gateway-prototype -am clean verify
```

Activate the `netty-prototype` Maven profile (sets the
`netty.prototype.enabled` property used by the bootstrap class):

```bash
mvn -pl netty-gateway-prototype -am clean verify -P netty-prototype
```

---

## Running the prototype

The module does **not** include a `main` method by default (it is a library
JAR).  To wire it into the existing server startup code add the following to
your server's initialisation block:

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import com.projectgoth.fusion.netty.NettyGatewayInitializer;

// Read port from the same server.properties used by the legacy stack.
int port = Integer.getInteger("netty.prototype.port", 9119);

NioEventLoopGroup bossGroup   = new NioEventLoopGroup(1);
NioEventLoopGroup workerGroup = new NioEventLoopGroup();
try {
    new ServerBootstrap()
        .group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new NettyGatewayInitializer())
        .bind(port)
        .sync()
        .channel()
        .closeFuture()
        .sync();
} finally {
    workerGroup.shutdownGracefully();
    bossGroup.shutdownGracefully();
}
```

### Configuration properties

| Property                    | Default | Description                                         |
|-----------------------------|---------|-----------------------------------------------------|
| `netty.prototype.enabled`   | `false` | Set to `true` to start the Netty listener at boot.  |
| `netty.prototype.port`      | `9119`  | TCP port the Netty gateway binds to.                |
| `Server`                    | —       | Existing server host (shared with legacy stack).    |
| `ServerPort`                | —       | Existing server port (shared with legacy stack).    |

Pass properties on the command line:

```bash
java -Dnetty.prototype.enabled=true \
     -Dnetty.prototype.port=9119 \
     -jar your-server.jar
```

---

## Wiring to the production `PacketProcessor`

1. Add `netty-gateway-prototype` as a `compile`-scope dependency in the core
   module's `pom.xml`.
2. In `GatewayChannelHandler.dispatch()`, replace the stub with:
   ```java
   // Convert FusionPacketMessage → FusionPacket (production domain object)
   ByteBuffer buf = ByteBuffer.wrap(/* reassemble header + payload */);
   if (FusionPacket.haveFusionPacket(buf)) {
       FusionPacket fusionPacket = FusionPacket.fromByteBuffer(buf);
       packetProcessor.process(ctx.channel(), fusionPacket);
   }
   ```
3. Inject the `PacketProcessor` instance via the `GatewayChannelHandler`
   constructor (add a `PacketProcessor` field and update
   `NettyGatewayInitializer` accordingly).

---

## Running the unit tests

```bash
mvn -pl netty-gateway-prototype test
```

The test suite (`NettyModuleSanityTest`) uses Netty's `EmbeddedChannel` for
fully in-process testing — no network socket required.

---

## Module structure

```
netty-gateway-prototype/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/projectgoth/fusion/netty/
    │   ├── NettyGatewayInitializer.java   # ChannelInitializer – wires the pipeline
    │   ├── FusionPacketDecoder.java        # ByteToMessageDecoder – frames raw bytes
    │   ├── FusionPacketEncoder.java        # MessageToByteEncoder – serialises packets
    │   ├── FusionPacketMessage.java        # Lightweight pipeline carrier object
    │   └── GatewayChannelHandler.java     # Business-logic handler (stub dispatcher)
    └── test/java/com/projectgoth/fusion/netty/
        └── NettyModuleSanityTest.java      # JUnit 5 / EmbeddedChannel sanity tests
```
