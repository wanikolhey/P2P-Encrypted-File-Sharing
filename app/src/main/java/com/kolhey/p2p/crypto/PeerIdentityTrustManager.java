package com.kolhey.p2p.crypto;

import javax.net.ssl.X509TrustManager;

import com.kolhey.p2p.database.PeerDatabase;

import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HexFormat;

public class PeerIdentityTrustManager implements X509TrustManager {

    private final PeerDatabase peerDatabase;

    public PeerIdentityTrustManager(PeerDatabase peerDatabase) {
        this.peerDatabase = peerDatabase;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        verifyPeerIdentity(chain[0]);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        verifyPeerIdentity(chain[0]);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0]; 
    }

    private void verifyPeerIdentity(X509Certificate cert) throws CertificateException {
        try {
            byte[] publicKey = cert.getPublicKey().getEncoded();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(publicKey);
            String fingerprint = HexFormat.of().formatHex(hash);

            if (peerDatabase.isTrusted(fingerprint)) {
                System.out.println("[Security] Peer verified. Fingerprint: " + fingerprint.substring(0, 8) + "...");
                return;
            } else {
                System.err.println("\n[SECURITY ALERT] Blocked unknown peer connection!");
                System.err.println("-> Presented Fingerprint: " + fingerprint);
                System.err.println("-> Action: Dropping TLS Handshake.\n");
                
                throw new CertificateException("Peer identity not found in local Keychain.");
            }
        } catch (Exception e) {
            throw new CertificateException("Cryptographic failure during verification", e);
        }
    }
}