package com.kolhey.p2p.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import com.kolhey.p2p.io.FileIOService;

public class WsFileTransferHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final int MAX_TEXT_MESSAGE_LENGTH = 1024;
    private static final String HANDSHAKE_INIT = "{\"action\": \"P2P_HANDSHAKE_INIT\"}";
    private static final String HANDSHAKE_ACK = "{\"status\": \"ACK_READY\"}";
    private final FileIOService fileIOService = new FileIOService();
    private boolean headerReceived = false;
    private String currentFileName;
    private long currentFileSize;
    private final boolean isClient;

    public WsFileTransferHandler(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (isClient) {
            System.out.println("[Client] Sending WSS initialization frame...");
            ctx.writeAndFlush(new TextWebSocketFrame(HANDSHAKE_INIT));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            if (text.length() > MAX_TEXT_MESSAGE_LENGTH) {
                System.err.println("WebSocket stream error: oversized text frame");
                ctx.close();
                return;
            }
            System.out.println((isClient ? "[Client]" : "[Server]") + " Received Text: " + text);

            if (!isClient) {
                if (!HANDSHAKE_INIT.equals(text)) {
                    System.err.println("WebSocket stream error: invalid client handshake payload");
                    ctx.close();
                    return;
                }
                ctx.writeAndFlush(new TextWebSocketFrame(HANDSHAKE_ACK));
            } else if (!HANDSHAKE_ACK.equals(text)) {
                System.err.println("WebSocket stream error: invalid server acknowledgement payload");
                ctx.close();
            }
        } 
        else if (frame instanceof BinaryWebSocketFrame) {
            io.netty.buffer.ByteBuf buffer = frame.content();

            try {
                if (!headerReceived) {
            // --- STAGE 1: RECEIVE BINARY METADATA ---
            // Expecting: [Int: NameLength][String: FileName][Long: FileSize]
                if (buffer.readableBytes() < 4) return;

                int nameLength = buffer.readInt();
                if (buffer.readableBytes() < nameLength + 8) return;

                byte[] nameBytes = new byte[nameLength];
                buffer.readBytes(nameBytes);
                this.currentFileName = new String(nameBytes, java.nio.charset.StandardCharsets.UTF_8);
                this.currentFileSize = buffer.readLong();

                this.headerReceived = true;
                System.out.println("[WS Receiver] Starting transfer for: " + currentFileName);
            } else {
            // --- STAGE 2: RECEIVE FILE CHUNKS ---
            // Pass the binary frame content to the IO service
                fileIOService.saveChunk(currentFileName, buffer, currentFileSize);
            
            // Note: SimpleChannelInboundHandler handles reference counting (releasing the buffer)
            // so we don't need a manual release here like in the QUIC handler.
            }
        } catch (Exception e) {
            System.err.println("WebSocket I/O Error: " + e.getMessage());
            ctx.close();
        }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("WebSocket stream error: " + cause.getMessage());
        ctx.close();
    }
}
