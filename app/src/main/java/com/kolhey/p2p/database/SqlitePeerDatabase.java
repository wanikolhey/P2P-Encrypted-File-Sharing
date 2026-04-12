package com.kolhey.p2p.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlitePeerDatabase implements PeerDatabase {
    private final String dbUrl;

    public SqlitePeerDatabase(String storageDirectory) {
        // 1. Ensure the storage directory exists on the hard drive
        new File(storageDirectory).mkdirs();
        
        // 2. Define the JDBC connection string
        this.dbUrl = "jdbc:sqlite:" + storageDirectory + "/trusted_peers.db";
        
        // 3. Initialize the schema
        initDatabase();
    }

    /**
     * Creates the table if this is the first time the node is booting.
     */
    private void initDatabase() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS peers (" +
                "fingerprint TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "trusted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        // try-with-resources automatically closes the connection to prevent memory leaks
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            System.err.println("CRITICAL: Failed to initialize SQLite database: " + e.getMessage());
        }
    }

    @Override
    public boolean isTrusted(String fingerprint) {
        String sql = "SELECT 1 FROM peers WHERE fingerprint = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fingerprint);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // If rs.next() is true, a row exists, meaning the peer is trusted
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Database read error: " + e.getMessage());
            return false; // Fail secure: if DB crashes, trust no one
        }
    }

    @Override
    public void trustPeer(String peerName, String fingerprint) {
        // INSERT OR REPLACE handles the case where the user updates a peer's name
        String sql = "INSERT OR REPLACE INTO peers (fingerprint, name) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fingerprint);
            pstmt.setString(2, peerName);
            pstmt.executeUpdate();
            
            System.out.println("Identity verified. Peer '" + peerName + "' saved to Keychain.");
            
        } catch (SQLException e) {
            System.err.println("Database write error: " + e.getMessage());
        }
    }

    @Override
    public void revokePeer(String fingerprint) {
        String sql = "DELETE FROM peers WHERE fingerprint = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fingerprint);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Trust revoked. Peer removed from Keychain.");
            }
        } catch (SQLException e) {
            System.err.println("Database delete error: " + e.getMessage());
        }
    }
}