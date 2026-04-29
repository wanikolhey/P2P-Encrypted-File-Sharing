package com.kolhey.p2p.ui.support;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
 * Factory for creating consistently styled UI components
 */
public class UiComponentFactory {
    
    public static Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-primary");
        button.setStyle("-fx-font-size: 12pt; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        return button;
    }
    
    public static Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-secondary");
        button.setStyle("-fx-font-size: 11pt; -fx-padding: 8px 16px;");
        return button;
    }
    
    public static Button createSmallButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #60A5FA, #2DD4BF);" +
            "-fx-text-fill: #081120;" +
            "-fx-font-size: 10pt;" +
            "-fx-padding: 6px 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-border-radius: 999;" +
            "-fx-background-radius: 999;" +
            "-fx-cursor: hand;"
        );
        return button;
    }
    
    public static Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-title");
        label.setFont(new Font("Segoe UI Semibold", 18));
        return label;
    }
    
    public static Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-header");
        label.setFont(new Font("Segoe UI Semibold", 14));
        return label;
    }
    
    public static Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-info");
        label.setFont(new Font("Segoe UI", 11));
        return label;
    }
    
    public static VBox createCard(String title, javafx.scene.Node... children) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: rgba(18, 35, 58, 0.92);" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.22), 16, 0.1, 0, 8);"
        );
        
        if (title != null && !title.isEmpty()) {
            Label titleLabel = createHeaderLabel(title);
            card.getChildren().add(titleLabel);
            Separator separator = new Separator();
            separator.setPrefWidth(Double.MAX_VALUE);
            card.getChildren().add(separator);
        }
        
        card.getChildren().addAll(children);
        return card;
    }
    
    public static TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(
            "-fx-font-size: 11pt;" +
            "-fx-padding: 8px;" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-control-inner-background: #0E1B2E;" +
            "-fx-background-color: #0E1B2E;" +
            "-fx-text-fill: #EDF5FF;"
        );
        return textField;
    }
    
    public static TextArea createStyledTextArea(String promptText) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setStyle(
            "-fx-font-size: 11pt;" +
            "-fx-padding: 8px;" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-control-inner-background: #0E1B2E;" +
            "-fx-background-color: #0E1B2E;" +
            "-fx-text-fill: #EDF5FF;"
        );
        return textArea;
    }
    
    public static ProgressBar createStyledProgressBar() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.getStyleClass().add("progress-bar");
        progressBar.setStyle("-fx-accent: #2DD4BF;");
        progressBar.setPrefHeight(8);
        return progressBar;
    }
    
    public static ComboBox<String> createStyledComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setStyle(
            "-fx-font-size: 11pt;" +
            "-fx-padding: 8px;" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-background-color: #0E1B2E;" +
            "-fx-text-fill: #EDF5FF;"
        );
        return comboBox;
    }
    
    public static HBox createHeaderWithStatus(String title) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #0E1B2E, #12233A);" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-width: 0 0 2 0;"
        );
        
        Label titleLabel = createTitleLabel(title);
        header.getChildren().add(titleLabel);
        
        return header;
    }
    
    public static Circle createStatusIndicator(String status) {
        Circle circle = new Circle(6);
        switch (status.toLowerCase()) {
            case "online":
                circle.setFill(UiTheme.SUCCESS);
                break;
            case "offline":
                circle.setFill(UiTheme.ERROR);
                break;
            case "connecting":
                circle.setFill(UiTheme.WARNING);
                break;
            case "trusted":
                circle.setFill(UiTheme.SUCCESS);
                break;
            case "new":
                circle.setFill(UiTheme.INFO);
                break;
            case "failed":
                circle.setFill(UiTheme.ERROR);
                break;
            default:
                circle.setFill(UiTheme.LIGHT_GRAY);
        }
        return circle;
    }

    public static HBox createStatusBadge(String peerName, String status) {
        HBox badge = new HBox(8);
        badge.setPadding(new Insets(8, 12, 8, 12));
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.06);" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-radius: 999;" +
            "-fx-background-radius: 999;"
        );

        Circle indicator = createStatusIndicator(status);
        Label nameLabel = new Label(peerName);
        nameLabel.setFont(new Font("Arial", 11));

        badge.getChildren().addAll(indicator, nameLabel);
        return badge;
    }

    /**
     * Create authentication status badge with color coding
     */
    public static HBox createAuthenticationStatusBadge(String status) {
        HBox badge = new HBox(8);
        badge.setPadding(new Insets(6, 12, 6, 12));
        badge.setAlignment(Pos.CENTER_LEFT);

        String color;
        String icon;
        switch (status.toLowerCase()) {
            case "trusted":
                color = "#4CAF50";
                icon = "✓";
                break;
            case "new":
                color = "#2196F3";
                icon = "◆";
                break;
            case "failed":
                color = "#F44336";
                icon = "✕";
                break;
            default:
                color = "#999999";
                icon = "?";
        }

        badge.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: #081120;" +
            "-fx-font-weight: bold;" +
            "-fx-border-radius: 999;" +
            "-fx-background-radius: 999;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
            "-fx-text-fill: #081120;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 10pt;"
        );

        Label statusLabel = new Label(status);
        statusLabel.setStyle(
            "-fx-text-fill: #081120;" +
            "-fx-font-size: 10pt;" +
            "-fx-font-weight: bold;"
        );

        badge.getChildren().addAll(iconLabel, statusLabel);
        return badge;
    }

    /**
     * Create a notification toast component
     */
    public static VBox createNotificationToast(String title, String message, String type) {
        VBox toast = new VBox(8);
        toast.setPadding(new Insets(12));
        toast.setStyle(
            "-fx-background-color: rgba(18, 35, 58, 0.98);" +
            "-fx-border-color: #2A4667;" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.30), 12, 0.1, 0, 4);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 11pt;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #EDF5FF;"
        );

        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-font-size: 10pt;" +
            "-fx-text-fill: #99ADC8;" +
            "-fx-wrap-text: true;"
        );
        messageLabel.setWrapText(true);

        toast.getChildren().addAll(titleLabel, messageLabel);
        return toast;
    }
}
