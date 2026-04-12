package com.kolhey.p2p.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileIOService {

    private static final int CHUNK_SIZE = 64 * 1024; // 64KB chunks

    /**
     * Prepares a header ByteBuf to be sent before the file data.
     */
    public ByteBuf createHeader(File file) {
        byte[] fileNameBytes = file.getName().getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(fileNameBytes.length);
        buffer.writeBytes(fileNameBytes);
        buffer.writeLong(file.length());
        return buffer;
    }

    /**
     * Reads a specific chunk from a file.
     */
    public ByteBuf readChunk(File file, long position) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel channel = raf.getChannel()) {
            
            long remaining = file.length() - position;
            int toRead = (int) Math.min(CHUNK_SIZE, remaining);
            
            ByteBuf buffer = Unpooled.directBuffer(toRead);
            buffer.writeBytes(channel, position, toRead);
            return buffer;
        }
    }

    /**
     * Writes incoming bytes to a file in the downloads folder.
     * ⚠️ IMPORTANT: Each handler instance should call this method within a try-finally or catch block
     * to ensure proper cleanup. This method is NOT thread-safe and should be called only from one thread.
     * 
     * @param fileName the name of the file being downloaded
     * @param data the ByteBuf containing chunk data
     * @param totalSize the total expected file size (used to detect completion)
     * @throws IOException if file I/O fails
     */
    public void saveChunk(String fileName, ByteBuf data, long totalSize) throws IOException {
        File downloadDir = new File("downloads");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        
        File targetFile = new File(downloadDir, fileName);
        int readableBytes = data.readableBytes();
        
        // CRITICAL: Use try-with-resources to ensure FileOutputStream is ALWAYS closed
        // This prevents file handle leaks if an exception occurs
        try (FileOutputStream fos = new FileOutputStream(targetFile, true)) {
            data.readBytes(fos.getChannel(), 0, readableBytes);
            fos.flush();
            
            // Check if this is the final chunk by comparing file size to totalSize
            long currentSize = targetFile.length();
            if (currentSize >= totalSize) {
                System.out.println("File saved successfully: " + fileName);
            }
        }
        // FileOutputStream automatically closes in the finally block, preventing leaks
    }
}