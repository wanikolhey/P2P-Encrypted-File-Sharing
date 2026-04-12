package com.kolhey.p2p.crypto;

import com.kolhey.p2p.database.PeerDatabase;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.incubator.codec.quic.QuicSslContext;
import io.netty.incubator.codec.quic.QuicSslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;

public class QuicSecurityManager {

    private static final String ALLOW_INSECURE_DEV_TLS_PROPERTY = "p2p.allowInsecureDevTls";
    public static final String P2P_ALPN_PROTOCOL = "secure-prp-transfer-v1";

    /**
     * Builds a QUIC server SSL context.
     *
     * @param peerDatabase optional peer verification database. If provided AND not in insecure dev mode,
     *                     PeerIdentityTrustManager will be used for mutual peer verification.
     *                     If null or in insecure dev mode, standard certificate validation is used.
     */
    public static QuicSslContext buildServerSslContext(PeerDatabase peerDatabase)
    throws CertificateException, SSLException {
        if (isInsecureDevTlsAllowed()) {
            SelfSignedCertificate ssc = new SelfSignedCertificate("p2p-node");

            QuicSslContextBuilder builder = QuicSslContextBuilder.forServer(
                ssc.privateKey(),
                null,
                ssc.certificate())
                .applicationProtocols(P2P_ALPN_PROTOCOL);
            
            // In insecure dev mode, skip custom peer verification and use default trust
            // PeerIdentityTrustManager requires pre-registered peer fingerprints which we don't have in dev mode
            
            return builder.build();
        }

        File keyStoreFile = getRequiredFile("p2p.quic.keyStore", "P2P_QUIC_KEYSTORE");
        String keyStorePassword = getRequiredValue("p2p.quic.keyStorePassword", "P2P_QUIC_KEYSTORE_PASSWORD");
        KeyManagerFactory keyManagerFactory = buildKeyManagerFactory(keyStoreFile, keyStorePassword);

        QuicSslContextBuilder builder = QuicSslContextBuilder.forServer(keyManagerFactory, keyStorePassword)
            .applicationProtocols(P2P_ALPN_PROTOCOL);
        
        if (peerDatabase != null) {
            TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase);
            builder.trustManager(trustManager);
        }

        return builder.build();
    }

    /**
     * Backward compatible overload for buildServerSslContext without PeerDatabase.
     */
    public static QuicSslContext buildServerSslContext()
    throws CertificateException, SSLException {
        return buildServerSslContext(null);
    }

    /**
     * Builds a QUIC client SSL context.
     *
     * @param peerDatabase optional peer verification database. If provided AND not in insecure dev mode,
     *                     PeerIdentityTrustManager will be used for peer verification.
     *                     If null or in insecure dev mode, standard certificate validation is used.
     */
    public static QuicSslContext buildClientSslContext(PeerDatabase peerDatabase) {
        try {
            QuicSslContextBuilder builder = QuicSslContextBuilder.forClient()
                .applicationProtocols(P2P_ALPN_PROTOCOL);

            if (isInsecureDevTlsAllowed()) {
                // In insecure dev mode, use default insecure trust manager
                // PeerIdentityTrustManager requires pre-registered peer fingerprints which we don't have in dev mode
                builder.trustManager(io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE);
            } else {
                if (peerDatabase != null) {
                    TrustManager trustManager = new PeerIdentityTrustManager(peerDatabase);
                    builder.trustManager(trustManager);
                } else {
                    File trustCertFile = getRequiredFile("p2p.quic.trustCert", "P2P_QUIC_TRUST_CERT");
                    builder.trustManager(trustCertFile);
                }
            }

            return builder.build();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to build QUIC client SSL context", exception);
        }
    }

    /**
     * Backward compatible overload for buildClientSslContext without PeerDatabase.
     */
    public static QuicSslContext buildClientSslContext() {
        return buildClientSslContext(null);
    }

    private static boolean isInsecureDevTlsAllowed() {
        return Boolean.getBoolean(ALLOW_INSECURE_DEV_TLS_PROPERTY);
    }

    private static File getRequiredFile(String propertyName, String envVarName) {
        String value = getRequiredValue(propertyName, envVarName);

        File file = new File(value);
        if (!file.isFile()) {
            throw new IllegalStateException("Configured TLS file does not exist: " + file.getAbsolutePath());
        }
        return file;
    }

    private static String getRequiredValue(String propertyName, String envVarName) {
        String fromProperty = System.getProperty(propertyName);
        String fromEnv = System.getenv(envVarName);
        String value = fromProperty != null && !fromProperty.isBlank() ? fromProperty : fromEnv;

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Missing TLS material. Set either system property '" + propertyName + "' or env var '" + envVarName + "'."
            );
        }

        return value;
    }

    private static KeyManagerFactory buildKeyManagerFactory(File keyStoreFile, String keyStorePassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] passwordChars = keyStorePassword.toCharArray();
            try (FileInputStream inputStream = new FileInputStream(keyStoreFile)) {
                keyStore.load(inputStream, passwordChars);
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, passwordChars);
            return keyManagerFactory;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load QUIC key store: " + keyStoreFile.getAbsolutePath(), exception);
        }
    }
}
