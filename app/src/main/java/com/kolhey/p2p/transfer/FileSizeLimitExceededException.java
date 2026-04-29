package com.kolhey.p2p.transfer;

import java.io.IOException;

public class FileSizeLimitExceededException extends IOException {

    public FileSizeLimitExceededException(String message) {
        super(message);
    }
}