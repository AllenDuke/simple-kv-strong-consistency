/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke;

import com.github.allenduke.cluster.EventHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author allenduke
 * @description
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */

@Component
public class InsideController {

    private static final Logger logger = LoggerFactory.getLogger(InsideController.class);

    @Value("${node.port}")
    private Integer port;

    @Value("${inside.bossSize}")
    private Integer bossSize;

    @Value("${inside.workerSize}")
    private Integer workerSize;

    @PostConstruct
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossSize);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerSize);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            /**
             * 开启tcp keepAlive,，当开启后，会有tcp层面上的心跳机制，我们应该关闭而去做我们自己的更为定制化的心跳探测
             */
            //serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
                                    pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));

                                    // 使用心跳可以更早发现故障
                                    pipeline.addLast(new IdleStateHandler( 100, 100, 100, TimeUnit.MILLISECONDS));

                                    pipeline.addLast(new EventHandler());
                                }
                            }
                    );
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("server is ready! ");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("节点启动失败", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
