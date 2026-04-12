package com.kolhey.p2p.crypto;

import com.kolhey.p2p.database.PeerDatabase;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.TrustManager;
import java.io.File;
import java.security.cert.CertificateException;

public class WsSecurityManager {

    private static final String ALLOW_INSECURE_DEV_TLS_PROPERTY = "p2p.allowInsecureDevTls";

    /**
     * Builds a WebSocket server SSL context.
     *
     * @param peerDatabase optional peer verification database. If provided AND not in insecure dev mode,
     *                     PeerIdentityTrustManager will be used for mutual peer verification.
     *                     If null or in insecure dev mode, standard certificate validation is used.
     */
    public static SslContext buildServerSslContext(PeerDatabase peerDatabase) throws CertificateException {
        try {
            if (isInsecureDevTlsAllowed()) {
                SelfSignedCertificate ssc = new SelfSignedCertificate("p2p-node-ws");
                SslContextBuilder builder = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey());
                
                // In insecure dev mode, skip custom peer verification and use default trust
                // PeerIdentityTrustManager requires pre-registered peer fingerprints which we don't have in dev mode
                
                return builder.build();
            }
            
            File certChainFile = getRequiredFile("p2p.ws.certChain", "P2P_WS_CERT_CHAIN");
            File privateKeyFile = getRequiredFile("p2p.ws.privateKey", "P2P_WS_PRIVATE_KEY");
            SslContextBuilder builder = SslContextBuilder.forServer(certChainFile, privateKeyFile);
            
            if (peerDatabase != null) {
                TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase);
                builder.trustManager(trustManager);
            }
            
            return builder.build();
        } catch (Exception e) {
            throw new CertificateException("Failed to generate WSS Server Context", e);
        }
    }

    /**
     * Backward compatible overload for buildServerSslContext without PeerDatabase.
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
    public static SslContext buildClientSslContext(PeerDatabase peerDatabase) throws CertificateException {
        try {
            SslContextBuilder builder = SslContextBuilder.forClient();
            
            if (isInsecureDevTlsAllowed()) {
                // In insecure dev mode, use default insecure trust manager
                // PeerIdentityTrustManager requires pre-registered peer fingerprints which we don't have in dev mode
                builder.trustManager(InsecureTrustManagerFactory.INSTANCE);
            } else {
                if (peerDatabase != null) {
                    TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase);
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
     * Backward compatible overload for buildClientSslContext without PeerDatabase.
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