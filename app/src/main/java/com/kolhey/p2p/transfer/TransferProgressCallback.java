package com.kolhey.p2p.transfer;

public interface TransferProgressCallback {
    void onProgress(String fileName, long bytesTransferred, long totalSize);
    void onComplete(String fileName);
    void onError(String fileName, Throwable cause);
}