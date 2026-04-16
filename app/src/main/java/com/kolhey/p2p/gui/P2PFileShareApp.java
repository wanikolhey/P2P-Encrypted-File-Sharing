package com.kolhey.p2p.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.kolhey.p2p.gui.controllers.MainWindowController;
import com.kolhey.p2p.gui.utils.UITheme;
import com.kolhey.p2p.gui.utils.UIComponentFactory;

import java.util.List;
import java.net.URL;

/**
 * Main GUI Application for P2P File Sharing
 */
public class P2PFileShareApp extends Application {
    
    private MainWindowController mainController;
    
    // Static port configuration (can be set via command line args)
    public static int configuredQuicPort = 9000;
    public static int configuredWsPort = 8080;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Parse command line arguments if provided
            Parameters params = getParameters();
            if (!params.getRaw().isEmpty()) {
                parseCommandLineArgs(params.getRaw());
            }
            
            // Configure Stage
            String title = "P2P File Sharing - Encrypted (QUIC: " + configuredQuicPort + ", WS: " + configuredWsPort + ")";
            primaryStage.setTitle(title);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Create Main Scene with configured ports
            mainController = new MainWindowController(configuredQuicPort, configuredWsPort);
            VBox root = mainController.createMainLayout();
            
            Scene scene = new Scene(root);
            URL stylesheetUrl = getClass().getResource("/styles.css");
            if (stylesheetUrl != null) {
                scene.getStylesheets().add(stylesheetUrl.toExternalForm());
            }
            
            // Apply inline CSS
            scene.getRoot().setStyle(
                "-fx-font-family: 'Segoe UI', 'Arial', sans-serif;" +
                "-fx-background-color: #F5F5F5;"
            );
            
            primaryStage.setScene(scene);
            
            // Handle window close
            primaryStage.setOnCloseRequest(event -> handleWindowClose());
            
            primaryStage.show();
            
            System.out.println("[GUI] Application started successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start application: " + e.getMessage());
        }
    }
    
    private void handleWindowClose() {
        if (mainController != null) {
            mainController.shutdown();
        }
        System.out.println("[GUI] Application closing...");
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * Parse command line arguments for port configuration
     * Usage: --quic-port=9000 --ws-port=8080
     */
    private void parseCommandLineArgs(List<String> args) {
        for (String arg : args) {
            if (arg.startsWith("--quic-port=")) {
                try {
                    configuredQuicPort = Integer.parseInt(arg.substring("--quic-port=".length()));
                    System.out.println("[Config] QUIC Port: " + configuredQuicPort);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid QUIC port: " + arg);
                }
            } else if (arg.startsWith("--ws-port=")) {
                try {
                    configuredWsPort = Integer.parseInt(arg.substring("--ws-port=".length()));
                    System.out.println("[Config] WebSocket Port: " + configuredWsPort);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid WS port: " + arg);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
