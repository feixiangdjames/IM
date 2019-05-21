package com.yrw.im.gateway.connector.start;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yrw.im.common.code.MsgDecoder;
import com.yrw.im.common.code.MsgEncoder;
import com.yrw.im.gateway.connector.handler.ConnectorTransferHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 2019-05-02
 * Time: 17:50
 *
 * @author yrw
 */
public class ConnectorClient {

    private static Logger logger = LoggerFactory.getLogger(ConnectorClient.class);

    public static Injector injector = Guice.createInjector();

    static void start(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast("MsgDecoder", injector.getInstance(MsgDecoder.class));
                    p.addLast("MsgEncoder", new MsgEncoder());
                    p.addLast("ClientTransferHandler", injector.getInstance(ConnectorTransferHandler.class));
                }
            }).connect(host, port)
            .addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("connector connect transfer successfully...");
                } else {
                    logger.error("connector connect transfer failed!");
                }
            });
    }
}