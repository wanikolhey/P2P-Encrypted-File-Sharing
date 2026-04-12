package com.kolhey.p2p.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import com.kolhey.p2p.io.FileIOService;

public class FileTransferStreamHandler extends ChannelInboundHandlerAdapter {

    private final FileIOService fileIOService = new FileIOService();
    private boolean headerReceived = false;
    private String currentFileName;
    private long currentFileSize;
    private final boolean isSender;

    public FileTransferStreamHandler(boolean isSender) {
        this.isSender = isSender;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (isSender) {
            System.out.println("[Client] Sending initiation packet...");
            ByteBuf message = Unpooled.copiedBuffer("P2P_HANDSHAKE_INIT", CharsetUtil.UTF_8);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof ByteBuf)) {
            ReferenceCountUtil.release(msg);
            ctx.close();
            return;
        }

        ByteBuf in = (ByteBuf) msg;
        try {
            if (!headerReceived) {
            // --- STAGE 1: RECEIVE METADATA (HEADER) ---
            // Expecting: [Int: NameLength][String: FileName][Long: FileSize]
            if (in.readableBytes() < 4) return; // Wait for at least the name length integer

            int nameLength = in.readInt();
            if (in.readableBytes() < nameLength + 8) return; // Wait for full name and long size

            byte[] nameBytes = new byte[nameLength];
            in.readBytes(nameBytes);
            this.currentFileName = new String(nameBytes, CharsetUtil.UTF_8);
            this.currentFileSize = in.readLong();

            this.headerReceived = true;
            System.out.println("[Receiver] Starting transfer for: " + currentFileName + " (" + currentFileSize + " bytes)");
        } else {
        // --- STAGE 2: RECEIVE FILE DATA (CHUNKS) ---
        // Feed the raw ByteBuf directly into your IO service
            fileIOService.saveChunk(currentFileName, in, currentFileSize);
        
        // Optional: Check if the transfer is complete to reset state
        // (This is handled internally by saveChunk closing the stream)
            }
        } catch (Exception e) {
            System.err.println("I/O Error during transfer: " + e.getMessage());
            ctx.close();
    
        } finally {
            in.release(); 
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Stream error: " + cause.getMessage());
        ctx.close();
    }
}