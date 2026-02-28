package com.migme.fusion.config;

import com.migme.fusion.netty.NettyTcpServer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyServerConfig {

    @Bean
    public ApplicationRunner nettyServerRunner(NettyTcpServer nettyTcpServer) {
        return args -> nettyTcpServer.start();
    }
}
