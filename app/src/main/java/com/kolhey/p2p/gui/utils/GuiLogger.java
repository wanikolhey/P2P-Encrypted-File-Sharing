package com.kolhey.p2p.gui.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility for GUI operations
 */
public class GuiLogger {
    
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "p2p-gui.log";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static boolean fileLoggingEnabled = true;
    
    static {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
            fileLoggingEnabled = false;
        }
    }
    
    /**
     * Log info message
     */
    public static void info(String message) {
        log("INFO", message);
    }
    
    /**
     * Log warning message
     */
    public static void warn(String message) {
        log("WARN", message);
    }
    
    /**
     * Log error message
     */
    public static void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * Log error with exception
     */
    public static void error(String message, Throwable throwable) {
        log("ERROR", message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * Log debug message
     */
    public static void debug(String message) {
        log("DEBUG", message);
    }
    
    /**
     * Internal logging method
     */
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String logMessage = String.format("[%s] %s - %s", timestamp, level, message);
        
        // Console logging
        System.out.println(logMessage);
        
        // File logging
        if (fileLoggingEnabled) {
            try {
                Files.write(
                    Paths.get(LOG_DIR, LOG_FILE),
                    (logMessage + System.lineSeparator()).getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                System.err.println("Failed to write to log file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Clear log file
     */
    public static void clearLogs() {
        try {
            Files.deleteIfExists(Paths.get(LOG_DIR, LOG_FILE));
            GuiLogger.info("Logs cleared");
        } catch (IOException e) {
            error("Failed to clear logs", e);
        }
    }
}
