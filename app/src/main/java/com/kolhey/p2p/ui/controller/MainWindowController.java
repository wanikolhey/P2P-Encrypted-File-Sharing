package com.kolhey.p2p.ui.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import com.kolhey.p2p.ui.support.TransferServiceManager;
import com.kolhey.p2p.ui.support.FileTransferEvent;
import com.kolhey.p2p.ui.support.UiComponentFactory;
import com.kolhey.p2p.ui.support.UiTheme;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Observable;
import java.util.Observer;

/**
 * Main Window Controller - Orchestrates all GUI components
 */
public class MainWindowController implements Observer {
    
    private TransferServiceManager serviceManager;
    private PeerDiscoveryController peerController;
    private FileTransferController fileTransferController;
    private ExecutorService executorService;
    private Timeline discoveryUpdateTimeline;
    
    // UI Components
    private Label statusLabel;
    private Label nodeNameLabel;
    private Circle statusIndicator;
    private ProgressIndicator loadingIndicator;
    private TabPane tabPane;
    private TextField wsPortField;
    
    private int configuredWsPort;
    
    public MainWindowController() {
        this(8080);  // Default port
    }

    public MainWindowController(int wsPort) {
        this.serviceManager = TransferServiceManager.getInstance();
        this.serviceManager.addObserver(this);
        this.executorService = Executors.newFixedThreadPool(2);
        this.configuredWsPort = wsPort;
    }

    public void setControllerReferences() {
        if (peerController != null && tabPane != null && fileTransferController != null) {
            peerController.setTabPaneReference(tabPane);
            peerController.setFileTransferController(fileTransferController);
        }
    }
    
    public VBox createMainLayout() {
        VBox mainLayout = new VBox();
        mainLayout.setStyle("-fx-background-color: transparent;");
        mainLayout.setFillWidth(true);
        
        // Header
        HBox header = createHeader();
        
        // Tab Pane with different views
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setSide(Side.BOTTOM);
        
        // Tab 1: Dashboard
        Tab dashboardTab = createDashboardTab();
        
        // Tab 2: Peer Discovery
        peerController = new PeerDiscoveryController(serviceManager);
        Tab peersTab = new Tab("Peers", peerController.createPeerView());
        peersTab.setStyle("-fx-text-fill: #2C2C2C;");
        
        // Tab 3: File Transfer
        fileTransferController = new FileTransferController(serviceManager, executorService);
        Tab transferTab = new Tab("Transfer Files", fileTransferController.createTransferView());
        transferTab.setStyle("-fx-text-fill: #2C2C2C;");
        
        // Tab 4: Settings
        Tab settingsTab = createSettingsTab();
        
        tabPane.getTabs().addAll(dashboardTab, peersTab, transferTab, settingsTab);

        // Set up controller references for tab navigation
        setControllerReferences();

        // Apply tab styling
        tabPane.setStyle(
            "-fx-tab-min-width: 120;" +
            "-fx-tab-max-width: 150;"
        );
        
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        mainLayout.getChildren().addAll(header, tabPane);
        
        // Start auto-refresh of peers
        startDiscoveryUpdater();
        
        return mainLayout;
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #0E1B2E, #12233A, #18304C);" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-width: 0 0 2 0;"
        );
        
        // Title
        Label titleLabel = UiComponentFactory.createTitleLabel("🔐 P2P File Sharing");
        
        // Status Indicator
        HBox statusBox = createStatusBox();
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Start/Stop Button
        Button toggleButton = createToggleButton();
        
        header.getChildren().addAll(titleLabel, statusBox, spacer, toggleButton);
        return header;
    }
    
    private HBox createStatusBox() {
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.06);" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-radius: 999;" +
            "-fx-background-radius: 999;" +
            "-fx-padding: 8 15 8 15;"
        );
        
        statusIndicator = new Circle(8);
        statusIndicator.setFill(UiTheme.ERROR);
        
        VBox textBox = new VBox(2);
        textBox.setAlignment(Pos.CENTER_LEFT);
        
        nodeNameLabel = UiComponentFactory.createInfoLabel("Node: -");
        nodeNameLabel.setStyle("-fx-font-weight: bold;");
        statusLabel = UiComponentFactory.createInfoLabel("Status: Offline");
        
        textBox.getChildren().addAll(nodeNameLabel, statusLabel);
        
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(16, 16);
        loadingIndicator.setProgress(-1);
        loadingIndicator.setVisible(false);
        
        statusBox.getChildren().addAll(statusIndicator, textBox, loadingIndicator);
        return statusBox;
    }
    
    private Button createToggleButton() {
        Button toggleButton = UiComponentFactory.createPrimaryButton("▶ Start Node");
        toggleButton.setPrefWidth(150);
        toggleButton.setOnAction(event -> {
            if (serviceManager.isNodeRunning()) {
                stopNode();
                toggleButton.setText("▶ Start Node");
            } else {
                if (!applySettingsPorts()) {
                    return;
                }
                startNode(toggleButton);
            }
        });
        return toggleButton;
    }
    
    private Tab createDashboardTab() {
        VBox dashboardContent = new VBox(15);
        dashboardContent.setPadding(new Insets(20));
        dashboardContent.setStyle("-fx-background-color: transparent;");
        
        // Welcome Section
        VBox welcomeCard = UiComponentFactory.createCard("Welcome to P2P File Sharing",
            new Label("Secure, decentralized file sharing over your local network."));
        
        // Quick Stats
        HBox statsBox = createStatsBox();
        
        // Quick Access
        VBox quickAccessCard = createQuickAccessCard();
        
        ScrollPane scrollPane = new ScrollPane(
            new VBox(15,
                welcomeCard,
                statsBox,
                quickAccessCard
            )
        );
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        Tab dashboardTab = new Tab("Dashboard", scrollPane);
        dashboardTab.setStyle("-fx-text-fill: #2C2C2C;");
        return dashboardTab;
    }
    
    private HBox createStatsBox() {
        HBox statsBox = new HBox(15);
        statsBox.setPadding(new Insets(0));
        
        VBox peersCard = createStatCard("📡", "Connected Peers", "0", "#C4A0E9");
        VBox transfersCard = createStatCard("📤", "Active Transfers", "0", "#9575CD");
        VBox securityCard = createStatCard("🔒", "Encrypted", "Yes", "#7E57C2");
        
        statsBox.getChildren().addAll(peersCard, transfersCard, securityCard);
        HBox.setHgrow(peersCard, Priority.ALWAYS);
        HBox.setHgrow(transfersCard, Priority.ALWAYS);
        HBox.setHgrow(securityCard, Priority.ALWAYS);
        
        return statsBox;
    }
    
    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 5, 0.0, 0, 2);"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32pt;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 12pt;" +
            "-fx-text-fill: white;" +
            "-fx-opacity: 0.8;"
        );
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 24pt;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }
    
    private VBox createQuickAccessCard() {
        VBox card = UiComponentFactory.createCard("Quick Access", new Label(""));
        card.getChildren().clear();
        
        Label titleLabel = UiComponentFactory.createHeaderLabel("Quick Access");
        card.getChildren().add(titleLabel);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        
        Button selectFileBtn = UiComponentFactory.createPrimaryButton("📁 Select File to Send");
        Button browseDownloads = UiComponentFactory.createSecondaryButton("📂 Open Downloads");
        
        selectFileBtn.setOnAction(event -> fileTransferController.openFileChooser());
        
        buttonBox.getChildren().addAll(selectFileBtn, browseDownloads);
        card.getChildren().add(buttonBox);
        
        return card;
    }
    
    private Tab createSettingsTab() {
        VBox settingsContent = new VBox(15);
        settingsContent.setPadding(new Insets(20));
        settingsContent.setStyle("-fx-background-color: transparent;");
        
        // Node Configuration
        VBox configCard = UiComponentFactory.createCard("Node Configuration", new Label(""));
        configCard.getChildren().clear();
        
        Label configTitle = UiComponentFactory.createHeaderLabel("Node Settings");
        configCard.getChildren().add(configTitle);
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));
        
        Label wsLabel = new Label("WebSocket Port:");
        wsPortField = UiComponentFactory.createStyledTextField("8080");
        wsPortField.setText(String.valueOf(configuredWsPort));
        
        grid.add(wsLabel, 0, 0);
        grid.add(wsPortField, 1, 0);
        
        configCard.getChildren().add(grid);
        
        // Information Card
        VBox infoCard = UiComponentFactory.createCard("Information", new Label(""));
        infoCard.getChildren().clear();
        
        Label infoTitle = UiComponentFactory.createHeaderLabel("Application Info");
        Label versionLabel = UiComponentFactory.createInfoLabel("Version: 1.0.0");
        Label authorLabel = UiComponentFactory.createInfoLabel("P2P Encrypted File Sharing");
        Label licenseLabel = UiComponentFactory.createInfoLabel("Licensed under MIT");
        
        infoCard.getChildren().addAll(infoTitle, new Separator(), versionLabel, authorLabel, licenseLabel);
        
        ScrollPane scrollPane = new ScrollPane(new VBox(15, configCard, infoCard));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        Tab settingsTab = new Tab("Settings", scrollPane);
        settingsTab.setStyle("-fx-text-fill: #2C2C2C;");
        return settingsTab;
    }
    
    private void startNode(Button toggleButton) {
        loadingIndicator.setVisible(true);
        executorService.execute(() -> {
            try {
                String nodeName = "Node-" + UUID.randomUUID().toString().substring(0, 4);
                serviceManager.startNode(nodeName, configuredWsPort);

                Platform.runLater(() -> {
                    statusIndicator.setFill(UiTheme.SUCCESS);
                    statusLabel.setText("Status: Online");
                    nodeNameLabel.setText("Node: " + nodeName);
                    loadingIndicator.setVisible(false);
                    if (toggleButton != null) {
                        toggleButton.setText("⏹ Stop Node");
                    }
                });

                System.out.println("[MainWindow] Node started: " + nodeName);
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    if (toggleButton != null) {
                        toggleButton.setText("▶ Start Node");
                    }
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to start node");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
                e.printStackTrace();
            }
        });
    }

    private boolean applySettingsPorts() {
        try {
            int wsPort = parsePort(wsPortField != null ? wsPortField.getText() : String.valueOf(configuredWsPort), "WebSocket");

            configuredWsPort = wsPort;
            return true;
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Port Configuration");
            alert.setHeaderText("Please fix port settings");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    private int parsePort(String rawValue, String label) {
        String value = rawValue != null ? rawValue.trim() : "";
        if (value.isEmpty()) {
            throw new IllegalArgumentException(label + " port cannot be empty.");
        }

        int port;
        try {
            port = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " port must be a number.");
        }

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(label + " port must be between 1 and 65535.");
        }
        return port;
    }
    
    private void stopNode() {
        serviceManager.stopNode();
        statusIndicator.setFill(UiTheme.ERROR);
        statusLabel.setText("Status: Offline");
        nodeNameLabel.setText("Node: -");
    }
    
    private void startDiscoveryUpdater() {
        discoveryUpdateTimeline = new Timeline(
            new KeyFrame(Duration.seconds(2), event -> {
                if (peerController != null) {
                    peerController.refreshPeerList();
                }
            })
        );
        discoveryUpdateTimeline.setCycleCount(Animation.INDEFINITE);
        discoveryUpdateTimeline.play();
    }
    
    public void shutdown() {
        if (discoveryUpdateTimeline != null) {
            discoveryUpdateTimeline.stop();
        }
        stopNode();
        serviceManager.deleteObserver(this);
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            if (arg instanceof FileTransferEvent) {
                if (fileTransferController != null) {
                    fileTransferController.handleTransferEvent((FileTransferEvent) arg);
                }
                return;
            }

            if (arg instanceof com.kolhey.p2p.ui.support.PeerConnectionEvent) {
                handleConnectionEvent((com.kolhey.p2p.ui.support.PeerConnectionEvent) arg);
                return;
            }

            if ("node_started".equals(arg)) {
                System.out.println("[MainWindow] Node started event received");
            } else if ("node_stopped".equals(arg)) {
                System.out.println("[MainWindow] Node stopped event received");
            }
        });
    }

    private void handleConnectionEvent(com.kolhey.p2p.ui.support.PeerConnectionEvent event) {
        System.out.println("[MainWindow] Connection Event: " + event);

        switch (event.getType()) {
            case ATTEMPTING_CONNECTION:
                System.out.println("[MainWindow] Attempting connection to " + event.getPeerName() +
                                   " via " + event.getProtocol());
                break;
            case PEER_CONNECTED:
                System.out.println("[MainWindow] Connected to " + event.getPeerName() +
                                   " (Auth: " + event.getAuthenticationStatus() + ")");
                break;
            case PEER_DISCONNECTED:
                System.out.println("[MainWindow] Disconnected from " + event.getPeerName());
                break;
            case AUTHENTICATION_SUCCESS:
                System.out.println("[MainWindow] Authentication successful with " + event.getPeerName());
                break;
            case AUTHENTICATION_FAILED:
                System.out.println("[MainWindow] Authentication failed with " + event.getPeerName());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Transfer Failed");
                alert.setHeaderText("File transfer failed");
                alert.setContentText(event.getMessage());
                alert.showAndWait();
                break;
        }
    }
}
