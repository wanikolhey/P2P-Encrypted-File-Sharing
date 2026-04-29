package com.kolhey.p2p.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.kolhey.p2p.ui.controller.MainWindowController;
import com.kolhey.p2p.ui.support.UiTheme;
import com.kolhey.p2p.ui.support.UiComponentFactory;

import java.util.List;
import java.net.URL;

/**
 * Main GUI Application for P2P File Sharing
 */
public class P2PFileSharingApplication extends Application {
    
    private MainWindowController mainController;
    
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
            String title = "P2P File Sharing - Encrypted (WS: " + configuredWsPort + ")";
            primaryStage.setTitle(title);
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Create Main Scene with configured port
            mainController = new MainWindowController(configuredWsPort);
            VBox root = mainController.createMainLayout();
            
            Scene scene = new Scene(root);
            URL stylesheetUrl = getClass().getResource("/styles.css");
            if (stylesheetUrl != null) {
                scene.getStylesheets().add(stylesheetUrl.toExternalForm());
            }
            
            // Apply inline CSS
            scene.getRoot().setStyle(
                "-fx-font-family: 'Segoe UI', 'Arial', sans-serif;" +
                "-fx-background-color: linear-gradient(to bottom right, #081120, #0E1B2E 55%, #12233A);"
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
     * Parse command line arguments for port configuration.
     * Usage: --ws-port=8080
     */
    private void parseCommandLineArgs(List<String> args) {
        for (String arg : args) {
            if (arg.startsWith("--ws-port=")) {
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
