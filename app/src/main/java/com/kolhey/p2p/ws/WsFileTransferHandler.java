package com.kolhey.p2p.ws;

import com.kolhey.p2p.gui.utils.P2PServiceManager;
import com.kolhey.p2p.gui.utils.TransferEvent;
import com.kolhey.p2p.io.FileIOService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WsFileTransferHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final int MAX_TEXT_MESSAGE_LENGTH = 1024;
    private final FileIOService fileIOService = new FileIOService();
    
    // State tracking for incoming files - each handler instance has its own state
    private boolean headerReceived = false;
    private String currentFileName;
    private long currentFileSize;
    private long totalBytesRead = 0;
    private String currentTransferId;

    private final boolean isClient;
    private final File pendingOutgoingFile;
    private final P2PServiceManager serviceManager;

    public WsFileTransferHandler(boolean isClient) {
        this(isClient, null, null);
    }

    public WsFileTransferHandler(boolean isClient, File pendingOutgoingFile) {
        this(isClient, pendingOutgoingFile, null);
    }

    public WsFileTransferHandler(boolean isClient, File pendingOutgoingFile, P2PServiceManager serviceManager) {
        this.isClient = isClient;
        this.pendingOutgoingFile = pendingOutgoingFile;
        this.serviceManager = serviceManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println((isClient ? "[Client]" : "[Server]") + " WebSocket connection established.");
        if (isClient && pendingOutgoingFile != null) {
            try {
                startFileTransfer(ctx, pendingOutgoingFile);
                ctx.close();
            } catch (IOException e) {
                System.err.println("[WS Sender] Failed to send file: " + e.getMessage());
                ctx.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            if (text.length() > MAX_TEXT_MESSAGE_LENGTH) {
                ctx.close();
                return;
            }
            // Handle control messages (JSON handshakes, cancel signals, etc.)
            System.out.println("Received Control Message: " + text);
        } 
        else if (frame instanceof BinaryWebSocketFrame) {
            // Route binary data to our file processor
            handleBinaryStream(ctx, frame.content());
        }
    }

    /**
     * Logic to distinguish between the first "Metadata" frame and subsequent "Chunk" frames.
     */
    private void handleBinaryStream(ChannelHandlerContext ctx, ByteBuf buffer) {
        try {
            if (!headerReceived) {
                // STAGE 1: PARSE HEADER
                buffer.markReaderIndex();
                if (buffer.readableBytes() < 4) return;
                int nameLength = buffer.readInt();
                
                if (buffer.readableBytes() < nameLength + 8) {
                    buffer.resetReaderIndex();
                    return; 
                }

                byte[] nameBytes = new byte[nameLength];
                buffer.readBytes(nameBytes);
                this.currentFileName = new String(nameBytes, StandardCharsets.UTF_8);
                this.currentFileSize = buffer.readLong();

                this.headerReceived = true;
                this.totalBytesRead = 0;
                this.currentTransferId = UUID.randomUUID().toString();
                System.out.println("[WS Receiver] Preparing to receive: " + currentFileName + " (" + currentFileSize + " bytes)");
                notifyTransferEvent(TransferEvent.started(currentTransferId, currentFileName,
                    String.valueOf(ctx.channel().remoteAddress()), currentFileSize));
            } else {
                // STAGE 2: SAVE CHUNKS
                int readable = buffer.readableBytes();
                fileIOService.saveChunk(currentFileName, buffer, currentFileSize);
                totalBytesRead += readable;
                notifyTransferEvent(TransferEvent.progress(currentTransferId, currentFileName,
                    String.valueOf(ctx.channel().remoteAddress()), totalBytesRead, currentFileSize));

                if (totalBytesRead >= currentFileSize) {
                    System.out.println("[WS Receiver] Transfer complete for " + currentFileName);
                    notifyTransferEvent(TransferEvent.completed(currentTransferId, currentFileName,
                        String.valueOf(ctx.channel().remoteAddress()), currentFileSize));
                    // Reset state for the next potential file on this connection
                    headerReceived = false;
                    currentFileName = null;
                    currentTransferId = null;
                    totalBytesRead = 0;
                }
            }
        } catch (IOException e) {
            System.err.println("WS File I/O Error: " + e.getMessage());
            notifyTransferEvent(TransferEvent.failed(currentTransferId, currentFileName,
                String.valueOf(ctx.channel().remoteAddress()), totalBytesRead, currentFileSize, e.getMessage()));
            ctx.close();
        }
    }

    /**
     * Call this method from your Client or Server logic to push a file across the wire.
     */
    public void startFileTransfer(ChannelHandlerContext ctx, File file) throws IOException {
        System.out.println("[WS Sender] Starting transfer for " + file.getName());
        
        // 1. Send Header as Binary Frame
        ByteBuf header = fileIOService.createHeader(file);
        ctx.writeAndFlush(new BinaryWebSocketFrame(header));

        // 2. Stream Chunks as Binary Frames
        long pos = 0;
        long fileLength = file.length();
        while (pos < fileLength) {
            ByteBuf chunk = fileIOService.readChunk(file, pos);
            pos += chunk.readableBytes();
            
            // Wrap the Netty ByteBuf in a WebSocket binary frame
            ctx.writeAndFlush(new BinaryWebSocketFrame(chunk));
            
            // Basic back-pressure: pause if the network buffer is full
            if (!ctx.channel().isWritable()) {
                try { Thread.sleep(5); } catch (InterruptedException ignored) {}
            }
        }
        System.out.println("[WS Sender] Finished sending " + file.getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("WebSocket stream error: " + cause.getMessage());
        notifyTransferEvent(TransferEvent.failed(currentTransferId, currentFileName,
            String.valueOf(ctx.channel().remoteAddress()), totalBytesRead, currentFileSize, cause.getMessage()));
        ctx.close();
    }

    private void notifyTransferEvent(TransferEvent event) {
        if (serviceManager != null) {
            serviceManager.notifyTransferEvent(event);
        }
    }
}
