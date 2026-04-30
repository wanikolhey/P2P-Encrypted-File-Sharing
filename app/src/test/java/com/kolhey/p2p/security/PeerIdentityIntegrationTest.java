package com.kolhey.p2p.security;

import com.kolhey.p2p.repository.SqlitePeerRepository;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PeerIdentityIntegrationTest {

    @Test
    public void tofuStoresFingerprintAndPersists() throws Exception {
        Path dir = Files.createTempDirectory("peer-tofu-test");
        String storage = dir.toAbsolutePath().toString();

        SqlitePeerRepository repo = new SqlitePeerRepository(storage);

        // Generate a self-signed certificate (simulates the remote peer cert)
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        try (FileInputStream fis = new FileInputStream(ssc.certificate())) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);

            // Create trust manager with TOFU enabled and point it at our Sqlite repo
            PeerIdentityTrustManager tm = new PeerIdentityTrustManager(repo, true);

            // Simulate client verifying server certificate -> should auto-trust and store
            tm.checkServerTrusted(new X509Certificate[]{cert}, "RSA");

            Map<String, String> peers = repo.getAllTrustedPeers();
            assertFalse(peers.isEmpty(), "TOFU should have stored the peer fingerprint");

            String fp = peers.keySet().iterator().next();

            // New repository instance should read the persisted entry
            SqlitePeerRepository repo2 = new SqlitePeerRepository(storage);
            assertTrue(repo2.isTrusted(fp));

            // Revoke and verify removal persists
            repo2.revokePeer(fp);
            assertFalse(repo2.isTrusted(fp));

            SqlitePeerRepository repo3 = new SqlitePeerRepository(storage);
            assertFalse(repo3.isTrusted(fp));
        } finally {
            // cleanup generated cert files
            ssc.delete();
        }
    }
}
