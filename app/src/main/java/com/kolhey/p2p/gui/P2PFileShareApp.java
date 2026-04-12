package com.kolhey.p2p.gui;

import javafx.application.Application;
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

/**
 * Main GUI Application for P2P File Sharing
 */
public class P2PFileShareApp extends Application {
    
    private MainWindowController mainController;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Configure Stage
            primaryStage.setTitle("P2P File Sharing - Encrypted");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Create Main Scene
            mainController = new MainWindowController();
            VBox root = mainController.createMainLayout();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css") != null
                ? getClass().getResource("/styles.css").toExternalForm()
                : "");
            
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
        mainController.shutdown();
        System.out.println("[GUI] Application closing...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
