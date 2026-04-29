package com.kolhey.p2p.ui.support;

import com.kolhey.p2p.repository.PeerRepository;
import com.kolhey.p2p.repository.SqlitePeerRepository;
import com.kolhey.p2p.network.discovery.PeerDiscoveryService;
import com.kolhey.p2p.transfer.FileTransferService;
import com.kolhey.p2p.network.websocket.WebSocketClientNode;
import com.kolhey.p2p.network.websocket.WebSocketServerNode;
import javax.jmdns.ServiceInfo;
import java.io.File;
import java.util.Map;
import java.util.Observable;

/**
 * Backend service manager for GUI integration
 */
public class TransferServiceManager extends Observable {
    
    private static TransferServiceManager instance;
    private PeerDiscoveryService discoveryManager;
    private FileTransferService fileIOService;
    private String nodeName;
    private boolean isRunning;
    private PeerRepository peerDatabase;
    private WebSocketServerNode wsServerNode;

    private TransferServiceManager() {
        this.fileIOService = new FileTransferService();
        this.isRunning = false;
    }
    
    public static synchronized TransferServiceManager getInstance() {
        if (instance == null) {
            instance = new TransferServiceManager();
        }
        return instance;
    }
    
    /**
     * Start the P2P node with discovery
     */
    public void startNode(String nodeName, int wsPort) throws Exception {
        if (isRunning) {
            throw new IllegalStateException("Node is already running");
        }

        this.nodeName = nodeName;
        this.peerDatabase = new SqlitePeerRepository("data/" + nodeName);
        this.wsServerNode = new WebSocketServerNode("0.0.0.0", wsPort, peerDatabase, this);
        this.discoveryManager = new PeerDiscoveryService();

        try {
            wsServerNode.start();
            discoveryManager.start(nodeName, wsPort);
            this.isRunning = true;
        } catch (Exception e) {
            stopNode();
            throw e;
        }
        
        setChanged();
        notifyObservers("node_started");
    }

    /**
     * Broadcast a file transfer event to GUI observers.
     */
    public void notifyTransferEvent(FileTransferEvent event) {
        if (event == null) {
            return;
        }

        setChanged();
        notifyObservers(event);
    }

    /**
     * Broadcast a connection event to GUI observers.
     */
    public void notifyConnectionEvent(PeerConnectionEvent event) {
        if (event == null) {
            return;
        }

        setChanged();
        notifyObservers(event);
    }

    /**
     * Send a file to a selected peer using the peer's discovered WebSocket endpoint.
     */
    public void sendFileToPeer(String peerName, File file) throws Exception {
        if (!isRunning) {
            throw new IllegalStateException("Node is not running");
        }
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("Selected file is invalid");
        }

        ServiceInfo peerInfo = getActivePeers().get(peerName);
        if (peerInfo == null) {
            throw new IllegalStateException("Selected peer is no longer available: " + peerName);
        }

        String[] addresses = peerInfo.getHostAddresses();
        if (addresses == null || addresses.length == 0) {
            throw new IllegalStateException("Selected peer does not have a resolvable IP address");
        }

        // Notify that we're attempting connection
        notifyConnectionEvent(PeerConnectionEvent.attemptingConnection(peerName, addresses[0], "WS"));

        int wsPort;
        try {
            String wsPortStr = peerInfo.getPropertyString("ws_port");
            wsPort = wsPortStr != null ? Integer.parseInt(wsPortStr) : peerInfo.getPort();
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Selected peer has an invalid WebSocket port", e);
        }

        WebSocketClientNode client = new WebSocketClientNode(peerDatabase, this);
        try {
            client.connectAndSend(addresses[0], wsPort, file);
        } finally {
            client.stop();
        }
    }
    
    /**
     * Stop the P2P node
     */
    public void stopNode() {
        if (wsServerNode != null) {
            wsServerNode.stop();
            wsServerNode = null;
        }
        if (discoveryManager != null) {
            discoveryManager.stop();
            discoveryManager = null;
        }
        isRunning = false;

        setChanged();
        notifyObservers("node_stopped");
    }
    
    /**
     * Get active peers from discovery manager
     */
    public Map<String, ServiceInfo> getActivePeers() {
        if (discoveryManager != null) {
            return discoveryManager.getActivePeers();
        }
        return Map.of();
    }
    
    /**
     * Get node name
     */
    public String getNodeName() {
        return nodeName;
    }
    
    /**
     * Check if node is running
     */
    public boolean isNodeRunning() {
        return isRunning;
    }
    
    /**
     * Get file IO service
     */
    public FileTransferService getFileIOService() {
        return fileIOService;
    }
    
    /**
     * Get discovery manager
     */
    public PeerDiscoveryService getDiscoveryManager() {
        return discoveryManager;
    }

    /**
     * Get peer authentication status from the peer database
     */
    public String getPeerAuthenticationStatus(String peerName) {
        if (peerDatabase == null) {
            return "Unknown";
        }
        // This would need peer database implementation to look up by name
        // For now, return generic status
        return "Unknown";
    }
}
