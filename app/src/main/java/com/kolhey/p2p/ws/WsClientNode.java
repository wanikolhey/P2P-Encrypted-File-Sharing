package com.kolhey.p2p.ws;

import com.kolhey.p2p.crypto.WsSecurityManager;
import com.kolhey.p2p.database.PeerDatabase;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;

import java.io.File;
import java.net.URI;

public class WsClientNode {

    private final PeerDatabase peerDatabase;
    private EventLoopGroup group;
    private Channel clientChannel;

    public WsClientNode(PeerDatabase peerDatabase) {
        this.peerDatabase = peerDatabase;
    }

    public void connectAndSend(String targetIp, int targetPort) throws Exception {
        connectAndSend(targetIp, targetPort, null);
    }

    public void connectAndSend(String targetIp, int targetPort, File fileToSend) throws Exception {
        group = new NioEventLoopGroup();
        final SslContext sslCtx = WsSecurityManager.buildClientSslContext(peerDatabase);

        URI uri = new URI("wss://" + targetIp + ":" + targetPort + "/p2p");

        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());

        Bootstrap b = new Bootstrap();
        b.group(group)
         .channel(NioSocketChannel.class)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) {
                 ChannelPipeline p = ch.pipeline();
                 p.addLast(sslCtx.newHandler(ch.alloc(), targetIp, targetPort));
                 p.addLast(new HttpClientCodec());
                 p.addLast(new HttpObjectAggregator(8192));
                 
                 p.addLast(new WebSocketClientProtocolHandler(handshaker));
                 
                 p.addLast(new SimpleChannelInboundHandler<Object>() {
                     @Override
                     public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                         if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
                             System.out.println("WSS Handshake Complete! Secure connection established.");
                             // Add transfer handler and trigger send on activation.
                             ctx.pipeline().addLast(new WsFileTransferHandler(true, fileToSend));
                             // Remove this setup handler since we don't need it anymore
                             ctx.pipeline().remove(this); 
                             // Trigger the handler's active state
                             ctx.pipeline().fireChannelActive();
                         }
                     }
                     @Override
                     protected void channelRead0(ChannelHandlerContext ctx, Object msg) {}
                 });
             }
         });

        System.out.println("Attempting WSS connection to " + uri.toString() + "...");
        clientChannel = b.connect(targetIp, targetPort).sync().channel();
        clientChannel.closeFuture().sync();
    }

    public void stop() {
        if (clientChannel != null) clientChannel.close();
        if (group != null) group.shutdownGracefully();
    }
}