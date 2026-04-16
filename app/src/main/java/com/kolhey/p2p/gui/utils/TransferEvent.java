package com.kolhey.p2p.gui.utils;

public final class TransferEvent {

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

    private TransferEvent(Type type, String transferId, String fileName, String peerLabel,
                          long bytesTransferred, long totalSize, String message) {
        this.type = type;
        this.transferId = transferId;
        this.fileName = fileName;
        this.peerLabel = peerLabel;
        this.bytesTransferred = bytesTransferred;
        this.totalSize = totalSize;
        this.message = message;
    }

    public static TransferEvent started(String transferId, String fileName, String peerLabel, long totalSize) {
        return new TransferEvent(Type.STARTED, transferId, fileName, peerLabel, 0L, totalSize, null);
    }

    public static TransferEvent progress(String transferId, String fileName, String peerLabel,
                                         long bytesTransferred, long totalSize) {
        return new TransferEvent(Type.PROGRESS, transferId, fileName, peerLabel, bytesTransferred, totalSize, null);
    }

    public static TransferEvent completed(String transferId, String fileName, String peerLabel, long totalSize) {
        return new TransferEvent(Type.COMPLETED, transferId, fileName, peerLabel, totalSize, totalSize, null);
    }

    public static TransferEvent failed(String transferId, String fileName, String peerLabel,
                                       long bytesTransferred, long totalSize, String message) {
        return new TransferEvent(Type.FAILED, transferId, fileName, peerLabel, bytesTransferred, totalSize, message);
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