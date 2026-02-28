package com.migme.fusion.netty;

import com.migme.fusion.netty.codec.FusionPacketDecoder;
import com.migme.fusion.netty.codec.FusionPacketEncoder;
import com.migme.fusion.netty.handler.FusionProtocolHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyTcpServer {

    private static final Logger log = LoggerFactory.getLogger(NettyTcpServer.class);

    @Value("${fusion.netty.port:9119}")
    private int port;

    @Value("${fusion.netty.boss-threads:1}")
    private int bossThreads;

    @Value("${fusion.netty.worker-threads:4}")
    private int workerThreads;

    private final FusionProtocolHandler fusionProtocolHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public NettyTcpServer(FusionProtocolHandler fusionProtocolHandler) {
        this.fusionProtocolHandler = fusionProtocolHandler;
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(bossThreads);
        workerGroup = new NioEventLoopGroup(workerThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new IdleStateHandler(120, 0, 0, TimeUnit.SECONDS),
                                new FusionPacketDecoder(),
                                new FusionPacketEncoder(),
                                fusionProtocolHandler
                        );
                    }
                });

        serverChannel = bootstrap.bind(port).sync().channel();
        log.info("Fusion TCP Server started on port {}", port);
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping Fusion TCP Server...");
        if (serverChannel != null) {
            serverChannel.close().awaitUninterruptibly();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("Fusion TCP Server stopped");
    }
}
