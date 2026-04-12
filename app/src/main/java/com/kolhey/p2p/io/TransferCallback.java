package com.kolhey.p2p.io;

public interface TransferCallback {
    void onProgress(String fileName, long bytesTransferred, long totalSize);
    void onComplete(String fileName);
    void onError(String fileName, Throwable cause);
}