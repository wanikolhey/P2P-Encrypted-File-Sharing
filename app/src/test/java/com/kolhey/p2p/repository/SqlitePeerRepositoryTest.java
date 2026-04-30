package com.kolhey.p2p.repository;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SqlitePeerRepositoryTest {

    @Test
    public void trustRevokeAndPersistence() throws Exception {
        Path dir = Files.createTempDirectory("peerrepo-test");
        String storage = dir.toAbsolutePath().toString();

        SqlitePeerRepository repo = new SqlitePeerRepository(storage);
        String fp = "AA:BB:CC:DD";

        assertFalse(repo.isTrusted(fp));

        repo.trustPeer("alice", fp);
        assertTrue(repo.isTrusted(fp));

        Map<String, String> peers = repo.getAllTrustedPeers();
        assertTrue(peers.containsKey(fp));

        // New instance should see persisted entry
        SqlitePeerRepository repo2 = new SqlitePeerRepository(storage);
        assertTrue(repo2.isTrusted(fp));

        // Revoke via repo2 and verify it's removed
        repo2.revokePeer(fp);
        assertFalse(repo2.isTrusted(fp));

        // New instance also should not see the revoked peer
        SqlitePeerRepository repo3 = new SqlitePeerRepository(storage);
        assertFalse(repo3.isTrusted(fp));
    }
}
