package com.kolhey.p2p.transfer;

import io.netty.buffer.ByteBuf;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileTransferServiceTest {

    private final FileTransferService fileIOService = new FileTransferService();

    @Test
    void createHeaderRejectsFilesLargerThan10Gb() {
        File oversizedFile = new File("huge.bin") {
            @Override
            public long length() {
                return 10L * 1024 * 1024 * 1024 + 1;
            }

            @Override
            public String getName() {
                return "huge.bin";
            }
        };

        FileSizeLimitExceededException exception = assertThrows(
            FileSizeLimitExceededException.class,
            () -> fileIOService.createHeader(oversizedFile)
        );

        assertEquals("File 'huge.bin' is larger than 10 GB and cannot be sent.", exception.getMessage());
    }

    @Test
    void createHeaderAllowsFilesAtThe10GbBoundary() {
        File boundaryFile = new File("boundary.bin") {
            @Override
            public long length() {
                return 10L * 1024 * 1024 * 1024;
            }

            @Override
            public String getName() {
                return "boundary.bin";
            }
        };

        ByteBuf header = assertDoesNotThrow(() -> fileIOService.createHeader(boundaryFile));

        assertNotNull(header);
        header.release();
    }
}