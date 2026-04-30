package com.kolhey.p2p.network.websocket;

import com.kolhey.p2p.security.WebSocketSecurityManager;
import com.kolhey.p2p.repository.PeerRepository;
import com.kolhey.p2p.ui.support.TransferServiceManager;
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

public class WebSocketClientNode {

    private final PeerRepository peerDatabase;
    private final TransferServiceManager serviceManager;
    private EventLoopGroup group;
    private Channel clientChannel;

    public WebSocketClientNode(PeerRepository peerDatabase) {
        this(peerDatabase, null);
    }

    public WebSocketClientNode(PeerRepository peerDatabase, TransferServiceManager serviceManager) {
        this.peerDatabase = peerDatabase;
        this.serviceManager = serviceManager;
    }

    public void connectAndSend(String targetIp, int targetPort) throws Exception {
        connectAndSend(targetIp, targetPort, null);
    }

    public void connectAndSend(String targetIp, int targetPort, File fileToSend) throws Exception {
        group = new NioEventLoopGroup();
        final SslContext sslCtx = WebSocketSecurityManager.buildClientSslContext(peerDatabase);

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
                            // Add transfer handler but delay firing channelActive slightly to allow
                            // the pipeline to finish processing any leftover HTTP objects.
                            ctx.pipeline().addLast(new WebSocketTransferHandler(true, fileToSend, serviceManager));
                            // Remove this setup handler since we don't need it anymore
                            ctx.pipeline().remove(this);
                            // Schedule a short delayed fire of channelActive to avoid race with
                            // leftover FullHttpRequest/Response objects being delivered to the
                            // new handler (which expects WebSocketFrame messages).
                            ctx.executor().schedule(() -> {
                                try {
                                    ctx.pipeline().fireChannelActive();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, 100, java.util.concurrent.TimeUnit.MILLISECONDS);
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