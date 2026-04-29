package com.kolhey.p2p.network.websocket;

import com.kolhey.p2p.security.WebSocketSecurityManager;
import com.kolhey.p2p.repository.PeerRepository;
import com.kolhey.p2p.ui.support.TransferServiceManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;

public class WebSocketServerNode {

    private final String bindIp;
    private final int bindPort;
    private final PeerRepository peerDatabase;
    private final TransferServiceManager serviceManager;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public WebSocketServerNode(String bindIp, int bindPort, PeerRepository peerDatabase, TransferServiceManager serviceManager) {
        this.bindIp = bindIp;
        this.bindPort = bindPort;
        this.peerDatabase = peerDatabase;
        this.serviceManager = serviceManager;
    }

    public void start()
    throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        final SslContext sslContext = WebSocketSecurityManager.buildServerSslContext(peerDatabase);

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
                    ch.pipeline().addLast(new HttpServerCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(65536));
                    ch.pipeline().addLast(new WebSocketServerProtocolHandler("/p2p"));
                    ch.pipeline().addLast(new WebSocketTransferHandler(false, null, serviceManager));
                }
            });

            serverChannel = b.bind(bindIp, bindPort).sync().channel();
            System.out.println("WebSocket server started at ws://" + bindIp + ":" + bindPort + "/p2p");
    }

    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        System.out.println("WebSocket server stopped.");
    }
}