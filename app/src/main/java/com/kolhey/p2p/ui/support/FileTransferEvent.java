package com.kolhey.p2p.ui.support;

public final class FileTransferEvent {

    public enum Type {
        STARTED,
        PROGRESS,
        COMPLETED,
        FAILED
    }

    private final Type type;
    private final String transferId;
    private final String fileName;
    private final String peerLabel;
    private final long bytesTransferred;
    private final long totalSize;
    private final String message;

    private FileTransferEvent(Type type, String transferId, String fileName, String peerLabel,
                          long bytesTransferred, long totalSize, String message) {
        this.type = type;
        this.transferId = transferId;
        this.fileName = fileName;
        this.peerLabel = peerLabel;
        this.bytesTransferred = bytesTransferred;
        this.totalSize = totalSize;
        this.message = message;
    }

    public static FileTransferEvent started(String transferId, String fileName, String peerLabel, long totalSize) {
        return new FileTransferEvent(Type.STARTED, transferId, fileName, peerLabel, 0L, totalSize, null);
    }

    public static FileTransferEvent progress(String transferId, String fileName, String peerLabel,
                                         long bytesTransferred, long totalSize) {
        return new FileTransferEvent(Type.PROGRESS, transferId, fileName, peerLabel, bytesTransferred, totalSize, null);
    }

    public static FileTransferEvent completed(String transferId, String fileName, String peerLabel, long totalSize) {
        return new FileTransferEvent(Type.COMPLETED, transferId, fileName, peerLabel, totalSize, totalSize, null);
    }

    public static FileTransferEvent failed(String transferId, String fileName, String peerLabel,
                                       long bytesTransferred, long totalSize, String message) {
        return new FileTransferEvent(Type.FAILED, transferId, fileName, peerLabel, bytesTransferred, totalSize, message);
    }

    public Type getType() {
        return type;
    }

    public String getTransferId() {
        return transferId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPeerLabel() {
        return peerLabel;
    }

    public long getBytesTransferred() {
        return bytesTransferred;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public String getMessage() {
        return message;
    }

    public double getProgressFraction() {
        if (totalSize <= 0L) {
            return 0.0;
        }
        return Math.min(1.0, (double) bytesTransferred / (double) totalSize);
    }
}