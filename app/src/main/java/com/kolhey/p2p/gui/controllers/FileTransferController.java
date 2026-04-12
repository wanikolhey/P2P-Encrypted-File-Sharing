package com.kolhey.p2p.gui.controllers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.kolhey.p2p.gui.utils.P2PServiceManager;
import com.kolhey.p2p.gui.utils.UIComponentFactory;
import com.kolhey.p2p.gui.utils.UITheme;
import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * Controller for File Transfer View
 */
public class FileTransferController {
    
    private P2PServiceManager serviceManager;
    private ExecutorService executorService;
    private File selectedFile;
    private ListView<HBox> transferListView;
    
    public FileTransferController(P2PServiceManager serviceManager, ExecutorService executorService) {
        this.serviceManager = serviceManager;
        this.executorService = executorService;
    }
    
    public VBox createTransferView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #F5F5F5;");
        
        // Send File Section
        VBox sendSection = createSendFileSection();
        
        // Active Transfers Section
        VBox transfersSection = createTransfersSection();
        
        VBox.setVgrow(transfersSection, Priority.ALWAYS);
        
        ScrollPane scrollPane = new ScrollPane(new VBox(15, sendSection, transfersSection));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F5F5F5;");
        
        return scrollPane;
    }
    
    private VBox createSendFileSection() {
        VBox card = UIComponentFactory.createCard("Send File", new Label(""));
        card.getChildren().clear();
        
        Label titleLabel = UIComponentFactory.createHeaderLabel("Send File to Peer");
        card.getChildren().add(titleLabel);
        
        // File Selection Area
        VBox fileSelectionArea = new VBox(10);
        fileSelectionArea.setPadding(new Insets(20));
        fileSelectionArea.setStyle(
            "-fx-border-color: #E6D5F5;" +
            "-fx-border-style: dashed;" +
            "-fx-border-radius: 5;" +
            "-fx-background-color: white;" +
            "-fx-background-radius: 5;"
        );
        fileSelectionArea.setAlignment(Pos.CENTER);
        
        Label dragDropLabel = new Label("📁 Drag & Drop or Click to Select File");
        dragDropLabel.setStyle(
            "-fx-font-size: 12pt;" +
            "-fx-text-fill: #999999;"
        );
        
        Label selectedFileLabel = new Label("No file selected");
        selectedFileLabel.setStyle(
            "-fx-font-size: 11pt;" +
            "-fx-text-fill: #C4A0E9;" +
            "-fx-font-weight: bold;"
        );
        
        Button selectFileBtn = UIComponentFactory.createPrimaryButton("📂 Select File");
        selectFileBtn.setOnAction(event -> {
            File file = openFileChooser();
            if (file != null) {
                selectedFile = file;
                selectedFileLabel.setText("Selected: " + file.getName() + " (" + formatFileSize(file.length()) + ")");
                dragDropLabel.setText("✓ File ready to send");
                dragDropLabel.setStyle("-fx-text-fill: #4CAF50;");
            }
        });
        
        fileSelectionArea.getChildren().addAll(dragDropLabel, selectedFileLabel, selectFileBtn);
        card.getChildren().add(fileSelectionArea);
        
        // Recipient Selection
        HBox recipientBox = createRecipientSelectionBox();
        card.getChildren().add(recipientBox);
        
        // Send Button
        Button sendBtn = UIComponentFactory.createPrimaryButton("🚀 Send File");
        sendBtn.setPrefWidth(Double.MAX_VALUE);
        sendBtn.setOnAction(event -> handleSendFile());
        
        card.getChildren().add(sendBtn);
        
        return card;
    }
    
    private HBox createRecipientSelectionBox() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(10, 0, 0, 0));
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label recipientLabel = new Label("Recipient:");
        recipientLabel.setStyle(
            "-fx-font-size: 11pt;" +
            "-fx-font-weight: bold;"
        );
        
        ComboBox<String> recipientCombo = UIComponentFactory.createStyledComboBox();
        recipientCombo.setPrefWidth(250);
        recipientCombo.setPromptText("Select a peer...");
        
        // Populate recipients from active peers
        executorService.execute(() -> {
            Platform.runLater(() -> {
                recipientCombo.getItems().clear();
                serviceManager.getActivePeers().keySet().forEach(recipientCombo.getItems()::add);
                
                if (recipientCombo.getItems().isEmpty()) {
                    recipientCombo.setPromptText("No peers available");
                    recipientCombo.setDisable(true);
                }
            });
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = UIComponentFactory.createSecondaryButton("🔄");
        refreshBtn.setOnAction(event -> {
            recipientCombo.getItems().clear();
            serviceManager.getActivePeers().keySet().forEach(recipientCombo.getItems()::add);
        });
        
        box.getChildren().addAll(recipientLabel, recipientCombo, spacer, refreshBtn);
        box.setStyle("-fx-user-data: " + recipientCombo.getId());
        
        return box;
    }
    
    private VBox createTransfersSection() {
        VBox card = UIComponentFactory.createCard("Active Transfers", new Label(""));
        card.getChildren().clear();
        
        Label titleLabel = UIComponentFactory.createHeaderLabel("Transfer Progress");
        card.getChildren().add(titleLabel);
        
        transferListView = new ListView<>();
        transferListView.setStyle(
            "-fx-control-inner-background: white;" +
            "-fx-border-color: #E6D5F5;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );
        transferListView.setPrefHeight(200);
        
        // Empty state
        Label emptyLabel = new Label("No active transfers");
        emptyLabel.setStyle(
            "-fx-text-fill: #999999;" +
            "-fx-font-size: 11pt;"
        );
        StackPane emptyPane = new StackPane(emptyLabel);
        emptyPane.setAlignment(Pos.CENTER);
        transferListView.setPlaceholder(emptyPane);
        
        card.getChildren().add(transferListView);
        VBox.setVgrow(transferListView, Priority.ALWAYS);
        
        return card;
    }
    
    private void handleSendFile() {
        if (selectedFile == null) {
            showAlert(Alert.AlertType.WARNING, "No File Selected", 
                "Please select a file first.");
            return;
        }
        
        // Add transfer to list
        HBox transferItem = createTransferItem(selectedFile.getName(), 0);
        transferListView.getItems().add(transferItem);
        
        // Simulate transfer
        executorService.execute(() -> {
            for (int i = 0; i <= 100; i += 10) {
                final int progress = i;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                Platform.runLater(() -> {
                    // Update progress bar in transfer item
                    System.out.println("[Transfer] " + selectedFile.getName() + ": " + progress + "%");
                });
            }
            
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.INFORMATION, "Transfer Complete", 
                    "File sent successfully: " + selectedFile.getName());
                selectedFile = null;
            });
        });
    }
    
    private HBox createTransferItem(String fileName, int progress) {
        HBox transferItem = new HBox(10);
        transferItem.setPadding(new Insets(12));
        transferItem.setAlignment(Pos.CENTER_LEFT);
        transferItem.setStyle(
            "-fx-background-color: #F5F5F5;" +
            "-fx-border-color: #E6D5F5;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );
        
        Label fileLabel = new Label("📄 " + fileName);
        fileLabel.setStyle(
            "-fx-font-size: 11pt;" +
            "-fx-font-weight: bold;"
        );
        
        ProgressBar progressBar = UIComponentFactory.createStyledProgressBar();
        progressBar.setProgress(progress / 100.0);
        progressBar.setPrefWidth(200);
        
        Label percentLabel = new Label(progress + "%");
        percentLabel.setStyle("-fx-font-size: 10pt;");
        percentLabel.setPrefWidth(40);
        
        Button cancelBtn = UIComponentFactory.createSmallButton("✕");
        cancelBtn.setStyle(
            "-fx-background-color: #F44336;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10pt;" +
            "-fx-padding: 4px 8px;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        transferItem.getChildren().addAll(fileLabel, spacer, progressBar, percentLabel, cancelBtn);
        return transferItem;
    }
    
    public void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Share");
        
        Stage stage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            this.selectedFile = selectedFile;
        }
    }
    
    public File openFileChooser(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Share");
        return fileChooser.showOpenDialog(owner);
    }
    
    private String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
