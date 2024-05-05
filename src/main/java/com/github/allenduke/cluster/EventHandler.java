/*
 * Copyright (c) allenduke 2024.
 */

package com.github.allenduke.cluster;

import com.github.allenduke.Node;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author allenduke
 * @description
 * @contact AllenDuke@163.com
 * @date 2024/5/5
 */
@Component
public class EventHandler extends SimpleChannelInboundHandler {

    @Resource
    private Cluster Cluster;

    @Resource
    private Node node;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        if (!(msg instanceof String)) {
            return;
        }
        String message = (String) msg;
        if (message.startsWith("ping")) {
            // 收到leader的ping
            ctx.writeAndFlush("pong");
            return;
        }
        if (message.startsWith("set")) {
            String[] split = message.split(" ");
            if (split.length != 4) {
                ctx.writeAndFlush("err");
                return;
            }
            Long instructionNum = Long.parseLong(split[0]);
            String key = split[1];
            String value = split[2];
            node.setFromMaster(instructionNum, key, value);
            ctx.writeAndFlush("ok");
            return;
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 出现空闲，节点失联
            // todo 通知集群，如果是leader，则需要重新选举
            ctx.channel().close();
        }
    }
}
