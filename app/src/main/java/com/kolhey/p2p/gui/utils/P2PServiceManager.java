package com.kolhey.p2p.gui.utils;

import com.kolhey.p2p.discovery.PeerDiscoveryManager;
import com.kolhey.p2p.io.FileIOService;
import javax.jmdns.ServiceInfo;
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
    private String nodeIp;
    private boolean isRunning;
    
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
        this.discoveryManager = new PeerDiscoveryManager();
        this.discoveryManager.start(nodeName, quicPort, wsPort);
        this.isRunning = true;
        
        setChanged();
        notifyObservers("node_started");
    }
    
    /**
     * Stop the P2P node
     */
    public void stopNode() {
        if (discoveryManager != null) {
            discoveryManager.stop();
            isRunning = false;
            
            setChanged();
            notifyObservers("node_stopped");
        }
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
