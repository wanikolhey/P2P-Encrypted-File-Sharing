package com.kolhey.p2p;

import com.kolhey.p2p.database.PeerDatabase;
import com.kolhey.p2p.database.SqlitePeerDatabase;
import com.kolhey.p2p.discovery.NetworkUtil;
import com.kolhey.p2p.discovery.PeerDiscoveryManager;
<<<<<<< HEAD
import com.kolhey.p2p.quic.QuicClientNode;
import com.kolhey.p2p.quic.QuicServerNode;
import com.kolhey.p2p.ws.WsClientNode;
import com.kolhey.p2p.ws.WsServerNode;
import javax.jmdns.ServiceInfo;
=======
import com.kolhey.p2p.gui.P2PFileShareApp;
>>>>>>> fbbd9eb (gui)

import java.net.InetAddress;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.Scanner;

public class Main {
<<<<<<< HEAD
    public static void main(String[] args) throws Exception {
        System.out.println("--- Starting Secure P2P Node ---\n");

        // Verify security configuration
        boolean devMode = Boolean.getBoolean("p2p.allowInsecureDevTls");
        System.out.println("=== SECURITY CONFIGURATION ===");
        System.out.println("Dev TLS Mode: " + devMode);
        System.out.println("Peer Identity Verification: " + (devMode ? "DISABLED (Dev Mode)" : "ENABLED ✅"));
        if (devMode) {
            System.out.println("⚠️  WARNING: Running in INSECURE dev mode with self-signed certs");
            System.out.println("   PeerIdentityTrustManager is BYPASSED");
        } else {
            System.out.println("✓ Production mode - PeerIdentityTrustManager ACTIVE");
        }
        System.out.println("==============================\n");
=======
    public static void main(String[] args) throws InterruptedException {
        // Check if GUI mode is requested
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            // Launch GUI application
            System.out.println("Starting P2P File Sharing GUI...");
            P2PFileShareApp.launch(P2PFileShareApp.class);
            return;
        }
        
        // Check for CLI mode flag
        if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
            printHelp();
            return;
        }
        
        // Default: CLI mode
        System.out.println("--- Starting P2P Node (CLI Mode) ---");
        System.out.println("Tip: Use '--gui' flag to launch the GUI: java com.kolhey.p2p.Main --gui");
        System.out.println();
>>>>>>> fbbd9eb (gui)

        String nodeName = "Node-" + UUID.randomUUID().toString().substring(0, 4);
        InetAddress localIp = NetworkUtil.getLocalIPv4Address();
        
        // Get configurable ports from environment variables (default to 8080 and 9000)
        int wsPort = getPortFromEnv("P2P_WS_PORT", 8080);
        int quicPort = getPortFromEnv("P2P_QUIC_PORT", 9000);
        
        System.out.println("Configuration:");
        System.out.println("  Node Name: " + nodeName);
        System.out.println("  IP Address: " + localIp.getHostAddress());
        System.out.println("  WebSocket Port: " + wsPort);
        System.out.println("  QUIC Port: " + quicPort);
        System.out.println();
        
        // 1. Initialize the SQLite Identity Database
        // (Using the current working directory for storage)
        PeerDatabase peerDb = new SqlitePeerDatabase(System.getProperty("user.dir") + "/.p2p-data");

        // 2. Start the WSS Server
        WsServerNode wsServer = new WsServerNode(localIp.getHostAddress(), wsPort, peerDb);
        wsServer.start();
        System.out.println("✓ WebSocket Secure (WSS) server started on wss://" + localIp.getHostAddress() + ":" + wsPort + "/p2p");

        // 3. Start the QUIC Server
        QuicServerNode quicServer = new QuicServerNode(localIp.getHostAddress(), quicPort, peerDb);
        quicServer.start();
        System.out.println("✓ QUIC server started on " + localIp.getHostAddress() + ":" + quicPort);

        // 4. Start Discovery
        PeerDiscoveryManager discovery = new PeerDiscoveryManager();
        try {
            discovery.start(nodeName, quicPort, wsPort);
            System.out.println("✓ Peer discovery started. Node name: " + nodeName);
        } catch (Exception e) {
            System.err.println("Failed to start peer discovery: " + e.getMessage());
            wsServer.stop();
            quicServer.stop();
            return;
        }

        // 5. Initialize outbound clients for terminal-driven connections
        QuicClientNode quicClient = new QuicClientNode(peerDb);
        WsClientNode wsClient = new WsClientNode(peerDb);

        // 6. Register shutdown hook for graceful exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n--- Shutting down P2P Node ---");
            
            try {
                discovery.stop();
                System.out.println("✓ Peer discovery stopped");
            } catch (Exception e) {
                System.err.println("Error stopping discovery: " + e.getMessage());
            }
            
            try {
                wsServer.stop();
                System.out.println("✓ WebSocket server stopped");
            } catch (Exception e) {
                System.err.println("Error stopping WebSocket server: " + e.getMessage());
            }
            
            try {
                quicServer.stop();
                System.out.println("✓ QUIC server stopped");
            } catch (Exception e) {
                System.err.println("Error stopping QUIC server: " + e.getMessage());
            }

            try {
                quicClient.stop();
                System.out.println("✓ QUIC client stopped");
            } catch (Exception e) {
                System.err.println("Error stopping QUIC client: " + e.getMessage());
            }

            try {
                wsClient.stop();
                System.out.println("✓ WebSocket client stopped");
            } catch (Exception e) {
                System.err.println("Error stopping WebSocket client: " + e.getMessage());
            }
            
            System.out.println("--- P2P Node shutdown complete ---");
        }));

        // 7. Terminal command loop for no-GUI usage
        System.out.println("\nNode is running. Type 'help' for commands, or 'exit' to quit.\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("p2p> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                if ("help".equalsIgnoreCase(line)) {
                    printHelp();
                    continue;
                }

                if ("peers".equalsIgnoreCase(line)) {
                    printPeers(discovery.getActivePeers(), nodeName);
                    continue;
                }

                if ("exit".equalsIgnoreCase(line) || "quit".equalsIgnoreCase(line)) {
                    System.out.println("Exiting on user command...");
                    break;
                }

                String[] parts = line.split("\\s+");
                if (parts.length == 2 && "connect-quic".equalsIgnoreCase(parts[0])) {
                    connectToDiscoveredPeer(parts[1], "quic", discovery.getActivePeers(), quicClient, wsClient);
                    continue;
                }

                if (parts.length == 2 && "connect-ws".equalsIgnoreCase(parts[0])) {
                    connectToDiscoveredPeer(parts[1], "ws", discovery.getActivePeers(), quicClient, wsClient);
                    continue;
                }

                if (parts.length == 3 && "connect-quic-host".equalsIgnoreCase(parts[0])) {
                    connectToHost(parts[1], parts[2], "quic", quicClient, wsClient);
                    continue;
                }

                if (parts.length == 3 && "connect-ws-host".equalsIgnoreCase(parts[0])) {
                    connectToHost(parts[1], parts[2], "ws", quicClient, wsClient);
                    continue;
                }

                System.out.println("Unknown command. Type 'help' for usage.");
            }
        }
    }

    /**
     * Get port configuration from environment variable or use default.
     */
    private static int getPortFromEnv(String envVar, int defaultPort) {
        String value = System.getenv(envVar);
        if (value != null && !value.isBlank()) {
            try {
                int port = Integer.parseInt(value);
                if (port > 0 && port <= 65535) {
                    return port;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultPort;
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  help                                 - Show commands");
        System.out.println("  peers                                - List discovered peers");
        System.out.println("  connect-quic <peerName>              - Connect to discovered peer via QUIC");
        System.out.println("  connect-ws <peerName>                - Connect to discovered peer via WSS");
        System.out.println("  connect-quic-host <ip> <port>        - Connect directly via QUIC");
        System.out.println("  connect-ws-host <ip> <port>          - Connect directly via WSS");
        System.out.println("  exit                                 - Stop this node");
    }

    private static void printPeers(Map<String, ServiceInfo> activePeers, String nodeName) {
        if (activePeers.isEmpty()) {
            System.out.println("No peers discovered yet.");
            return;
        }

        System.out.println("Discovered peers:");
        for (Map.Entry<String, ServiceInfo> entry : activePeers.entrySet()) {
            String peerName = entry.getKey();
            if (nodeName.equals(peerName)) {
                continue;
            }

            ServiceInfo info = entry.getValue();
            String[] addresses = info.getHostAddresses();
            String address = (addresses != null && addresses.length > 0) ? addresses[0] : "unknown";
            String quicPort = info.getPropertyString("quic_port");
            String wsPort = info.getPropertyString("ws_port");

            System.out.println("  " + peerName + " -> " + address
                + " (quic=" + (quicPort != null ? quicPort : "n/a")
                + ", ws=" + (wsPort != null ? wsPort : "n/a") + ")");
        }
    }

    private static void connectToDiscoveredPeer(
        String peerName,
        String protocol,
        Map<String, ServiceInfo> activePeers,
        QuicClientNode quicClient,
        WsClientNode wsClient
    ) {
        ServiceInfo info = activePeers.get(peerName);
        if (info == null) {
            System.out.println("Peer not found: " + peerName + ". Run 'peers' to list available peers.");
            return;
        }

        String[] addresses = info.getHostAddresses();
        if (addresses == null || addresses.length == 0) {
            System.out.println("Peer has no resolved host address: " + peerName);
            return;
        }

        String ip = addresses[0];
        String portText = "quic".equalsIgnoreCase(protocol)
            ? info.getPropertyString("quic_port")
            : info.getPropertyString("ws_port");

        if (portText == null || portText.isBlank()) {
            System.out.println("Peer does not advertise " + protocol + " support: " + peerName);
            return;
        }

        connectToHost(ip, portText, protocol, quicClient, wsClient);
    }

    private static void connectToHost(
        String ip,
        String portText,
        String protocol,
        QuicClientNode quicClient,
        WsClientNode wsClient
    ) {
        try {
            int port = Integer.parseInt(portText);
            if ("quic".equalsIgnoreCase(protocol)) {
                quicClient.connectAndSend(ip, port);
            } else {
                wsClient.connectAndSend(ip, port);
            }
            System.out.println("Connected via " + protocol.toUpperCase() + " to " + ip + ":" + port);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port: " + portText);
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
    
    private static void printHelp() {
        System.out.println("P2P File Sharing Application - Help\n");
        System.out.println("Usage: java com.kolhey.p2p.Main [options]\n");
        System.out.println("Options:");
        System.out.println("  --gui         Launch the GUI application (recommended for most users)");
        System.out.println("  --help        Display this help message");
        System.out.println("  (no args)     Start in CLI mode with peer discovery\n");
        System.out.println("Examples:");
        System.out.println("  java com.kolhey.p2p.Main --gui      # Launch the desktop GUI");
        System.out.println("  java com.kolhey.p2p.Main            # Start CLI mode\n");
    }
}