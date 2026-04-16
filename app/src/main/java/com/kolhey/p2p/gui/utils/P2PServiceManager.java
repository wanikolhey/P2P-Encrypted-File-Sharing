package com.kolhey.p2p.gui.utils;

import com.kolhey.p2p.database.PeerDatabase;
import com.kolhey.p2p.database.SqlitePeerDatabase;
import com.kolhey.p2p.discovery.PeerDiscoveryManager;
import com.kolhey.p2p.io.FileIOService;
import com.kolhey.p2p.quic.QuicServerNode;
import com.kolhey.p2p.ws.WsClientNode;
import com.kolhey.p2p.ws.WsServerNode;
import javax.jmdns.ServiceInfo;
import java.io.File;
import java.util.Map;
import java.util.Observable;

/**
 * Backend service manager for GUI integration
 */
public class P2PServiceManager extends Observable {
    
    private static P2PServiceManager instance;
    private PeerDiscoveryManager discoveryManager;
    private FileIOService fileIOService;
    private String nodeName;
    private boolean isRunning;
    private PeerDatabase peerDatabase;
    private QuicServerNode quicServerNode;
    private WsServerNode wsServerNode;
    
    private P2PServiceManager() {
        this.fileIOService = new FileIOService();
        this.isRunning = false;
    }
    
    public static synchronized P2PServiceManager getInstance() {
        if (instance == null) {
            instance = new P2PServiceManager();
        }
        return instance;
    }
    
    /**
     * Start the P2P node with discovery
     */
    public void startNode(String nodeName, int quicPort, int wsPort) throws Exception {
        if (isRunning) {
            throw new IllegalStateException("Node is already running");
        }

        this.nodeName = nodeName;
        this.peerDatabase = new SqlitePeerDatabase("data/" + nodeName);
        this.quicServerNode = new QuicServerNode("0.0.0.0", quicPort, peerDatabase, this);
        this.wsServerNode = new WsServerNode("0.0.0.0", wsPort, peerDatabase, this);
        this.discoveryManager = new PeerDiscoveryManager();

        try {
            quicServerNode.start();
            wsServerNode.start();
            discoveryManager.start(nodeName, quicPort, wsPort);
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
    public void notifyTransferEvent(TransferEvent event) {
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

        int wsPort;
        try {
            String wsPortStr = peerInfo.getPropertyString("ws_port");
            wsPort = wsPortStr != null ? Integer.parseInt(wsPortStr) : peerInfo.getPort();
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Selected peer has an invalid WebSocket port", e);
        }

        WsClientNode client = new WsClientNode(peerDatabase);
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
        if (quicServerNode != null) {
            quicServerNode.stop();
            quicServerNode = null;
        }
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
    public FileIOService getFileIOService() {
        return fileIOService;
    }
    
    /**
     * Get discovery manager
     */
    public PeerDiscoveryManager getDiscoveryManager() {
        return discoveryManager;
    }
}
