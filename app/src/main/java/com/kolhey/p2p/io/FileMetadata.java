package com.kolhey.p2p.io;

import java.io.Serializable;

public class FileMetadata implements Serializable {
    private String fileName;
    private long fileSize;
    //private String fileHash; //for integrity checks later

    public FileMetadata(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    // Getters
    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
}