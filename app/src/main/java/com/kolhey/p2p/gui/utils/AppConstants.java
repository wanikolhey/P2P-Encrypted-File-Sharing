package com.kolhey.p2p.gui.utils;

/**
 * Application constants for the GUI
 */
public class AppConstants {
    
    // Application Info
    public static final String APP_NAME = "P2P File Sharing";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_DESCRIPTION = "Secure, decentralized file sharing over local networks";
    
    // Network Defaults
    public static final int DEFAULT_QUIC_PORT = 9000;
    public static final int DEFAULT_WS_PORT = 8080;
    public static final int PEER_DISCOVERY_REFRESH_INTERVAL = 2; // seconds
    
    // File Transfer Settings
    public static final int CHUNK_SIZE = 64 * 1024; // 64KB
    public static final String DOWNLOADS_FOLDER = "downloads";
    
    // UI Window Defaults
    public static final int WINDOW_MIN_WIDTH = 900;
    public static final int WINDOW_MIN_HEIGHT = 600;
    public static final int WINDOW_DEFAULT_WIDTH = 1200;
    public static final int WINDOW_DEFAULT_HEIGHT = 800;
    
    // UI Animation Defaults
    public static final int ANIMATION_DURATION_MS = 300;
    
    // Error Messages
    public static final String ERROR_NO_FILE_SELECTED = "Please select a file first.";
    public static final String ERROR_NODE_ALREADY_RUNNING = "Node is already running.";
    public static final String ERROR_NODE_NOT_RUNNING = "Node is not running.";
    public static final String ERROR_NO_PEERS_AVAILABLE = "No peers available.";
    
    // Success Messages
    public static final String SUCCESS_FILE_SENT = "File sent successfully!";
    public static final String SUCCESS_NODE_STARTED = "Node started successfully.";
    public static final String SUCCESS_NODE_STOPPED = "Node stopped.";
    
    // Logging
    public static final String LOG_PREFIX_GUI = "[GUI]";
    public static final String LOG_PREFIX_TRANSFER = "[Transfer]";
    public static final String LOG_PREFIX_DISCOVERY = "[Discovery]";
}
