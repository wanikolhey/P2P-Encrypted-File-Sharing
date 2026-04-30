package com.kolhey.p2p.integration;

import com.kolhey.p2p.network.websocket.WebSocketClientNode;
import com.kolhey.p2p.network.websocket.WebSocketServerNode;
import com.kolhey.p2p.repository.SqlitePeerRepository;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EndToEndTransferIntegrationTest {

    @Test
    public void serverClientTransferStoresFingerprintInClientRepo() throws Exception {
        System.setProperty("p2p.allowInsecureDevTls", "true");

        Path serverDir = Files.createTempDirectory("node-server");
        Path clientDir = Files.createTempDirectory("node-client");

        SqlitePeerRepository serverRepo = new SqlitePeerRepository(serverDir.toAbsolutePath().toString());
        SqlitePeerRepository clientRepo = new SqlitePeerRepository(clientDir.toAbsolutePath().toString());

        int port = 53211; // ephemeral test port

        WebSocketServerNode server = new WebSocketServerNode("127.0.0.1", port, serverRepo, null);

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "ws-server-thread");
        serverThread.start();

        // wait briefly for server to bind
        // wait for server to bind (poll socket)
        boolean ready = false;
        for (int i = 0; i < 50; i++) {
            try (java.net.Socket s = new java.net.Socket()) {
                s.connect(new java.net.InetSocketAddress("127.0.0.1", port), 200);
                ready = true;
                break;
            } catch (Exception ignored) {
                Thread.sleep(100);
            }
        }
        assertTrue(ready, "Server did not bind to port in time");

        // create a small temp file to send
        Path tmpFile = Files.createTempFile("send-me", ".txt");
        Files.writeString(tmpFile, "hello peer\n");

        WebSocketClientNode client = new WebSocketClientNode(clientRepo, null);

        try {
            client.connectAndSend("127.0.0.1", port, tmpFile.toFile());

            // After the handshake the client's peer DB should contain the server fingerprint
            Map<String, String> peers = clientRepo.getAllTrustedPeers();
            assertFalse(peers.isEmpty(), "Client repository should contain server fingerprint after TOFU handshake");

        } finally {
            client.stop();
            server.stop();
            serverThread.interrupt();
        }
    }
}
