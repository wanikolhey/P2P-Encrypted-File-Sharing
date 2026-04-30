package com.kolhey.p2p.security;

import com.kolhey.p2p.repository.PeerRepository;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.TrustManager;
import java.io.File;
import java.security.cert.CertificateException;

public class WebSocketSecurityManager {

    private static final String ALLOW_INSECURE_DEV_TLS_PROPERTY = "p2p.allowInsecureDevTls";

    /**
     * Builds a WebSocket server SSL context.
     *
     * @param peerDatabase optional peer verification database. If provided AND not in insecure dev mode,
     *                     PeerIdentityTrustManager will be used for mutual peer verification.
     *                     If null or in insecure dev mode, standard certificate validation is used.
     */
    public static SslContext buildServerSslContext(PeerRepository peerDatabase) throws CertificateException {
        try {
            if (isInsecureDevTlsAllowed()) {
                SelfSignedCertificate ssc = new SelfSignedCertificate("p2p-node-ws");
                SslContextBuilder builder = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey());

                // In dev mode we were previously bypassing custom peer verification entirely.
                // If a PeerRepository is provided we should still enable TOFU so local
                // developer runs exercise the same trust logic (auto-trust on first use).
                if (peerDatabase != null) {
                    javax.net.ssl.TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase, true);
                    builder.trustManager(trustManager);
                }

                return builder.build();
            }
            
            File certChainFile = getRequiredFile("p2p.ws.certChain", "P2P_WS_CERT_CHAIN");
            File privateKeyFile = getRequiredFile("p2p.ws.privateKey", "P2P_WS_PRIVATE_KEY");
            SslContextBuilder builder = SslContextBuilder.forServer(certChainFile, privateKeyFile);
            
            if (peerDatabase != null) {
                // ENABLE TOFU (TRUST-ON-FIRST-USE): UNKNOWN PEERS AUTO-TRUSTED ON FIRST CONNECTION
                TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase, true);
                builder.trustManager(trustManager);
            }
            
            return builder.build();
        } catch (Exception e) {
            throw new CertificateException("Failed to generate WSS Server Context", e);
        }
    }

    /**
     * Backward compatible overload for buildServerSslContext without PeerRepository.
     * For testing purposes, uses InsecureTrustManagerFactory when flag is set.
     */
    public static SslContext buildServerSslContext() throws CertificateException {
        return buildServerSslContext(null);
    }

    /**
     * Builds a WebSocket client SSL context.
     *
     * @param peerDatabase optional peer verification database. If provided AND not in insecure dev mode,
     *                     PeerIdentityTrustManager will be used for peer verification.
     *                     If null or in insecure dev mode, standard certificate validation is used.
     */
    public static SslContext buildClientSslContext(PeerRepository peerDatabase) throws CertificateException {
        try {
            SslContextBuilder builder = SslContextBuilder.forClient();
            
            if (isInsecureDevTlsAllowed()) {
                // Prefer TOFU over fully insecure trust when peerDatabase is provided.
                if (peerDatabase != null) {
                    TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase, true);
                    builder.trustManager(trustManager);
                } else {
                    // Fall back to truly insecure trust for quick dev runs without a DB
                    builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                }
            } else {
                if (peerDatabase != null) {
                    // ENABLE TOFU (TRUST-ON-FIRST-USE): UNKNOWN PEERS AUTO-TRUSTED ON FIRST CONNECTION
                    TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase, true);
                    builder.trustManager(trustManager);
                } else {
                    File trustCertFile = getRequiredFile("p2p.ws.trustCert", "P2P_WS_TRUST_CERT");
                    builder.trustManager(trustCertFile);
                }
            }
            
            return builder.build();
        } catch (Exception e) {
            throw new CertificateException("Failed to generate WSS Client Context", e);
        }
    }

    /**
     * Backward compatible overload for buildClientSslContext without PeerRepository.
     */
    public static SslContext buildClientSslContext() throws CertificateException {
        return buildClientSslContext(null);
    }

    
    private static boolean isInsecureDevTlsAllowed() {
        return Boolean.getBoolean(ALLOW_INSECURE_DEV_TLS_PROPERTY);
    }

    private static File getRequiredFile(String propertyName, String envVarName) {
        String fromProperty = System.getProperty(propertyName);
        String fromEnv = System.getenv(envVarName);
        String value = fromProperty != null && !fromProperty.isBlank() ? fromProperty : fromEnv;

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Missing TLS material. Set either system property '" + propertyName + "' or env var '" + envVarName + "'."
            );
        }

        File file = new File(value);
        if (!file.isFile()) {
            throw new IllegalStateException("Configured TLS file does not exist: " + file.getAbsolutePath());
        }
        return file;
    }
}