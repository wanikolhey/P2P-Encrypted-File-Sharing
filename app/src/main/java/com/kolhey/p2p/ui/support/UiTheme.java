package com.kolhey.p2p.ui.support;

import javafx.scene.paint.Color;

/**
 * UI Theme configuration for the P2P File Sharing application.
 * Color scheme: Midnight teal with warm amber accents.
 */
public class UiTheme {
    
    // Core palette
    public static final Color BACKGROUND = Color.web("#081120");
    public static final Color BACKGROUND_ALT = Color.web("#0E1B2E");
    public static final Color SURFACE = Color.web("#12233A");
    public static final Color SURFACE_ALT = Color.web("#18304C");
    public static final Color BORDER = Color.web("#2A4667");
    public static final Color TEXT_PRIMARY = Color.web("#EDF5FF");
    public static final Color TEXT_MUTED = Color.web("#99ADC8");
    public static final Color PRIMARY = Color.web("#2DD4BF");
    public static final Color PRIMARY_DARK = Color.web("#0F766E");
    public static final Color SECONDARY = Color.web("#F59E0B");
    public static final Color ACCENT = Color.web("#60A5FA");
    public static final Color ACCENT_SOFT = Color.web("#93C5FD");
    public static final Color CARD_HIGHLIGHT = Color.web("#1B3350");
    public static final Color LIGHT_GRAY = Color.web("#28425F");
    public static final Color DARK_GRAY = Color.web("#9FB3C8");
    
    // Semantic Colors
    public static final Color SUCCESS = Color.web("#34D399");
    public static final Color WARNING = Color.web("#FBBF24");
    public static final Color ERROR = Color.web("#FB7185");
    public static final Color INFO = Color.web("#38BDF8");

    // Connection State Colors
    public static final Color CONNECTING = Color.web("#F59E0B");
    public static final Color AUTHENTICATED = Color.web("#34D399");
    public static final Color AUTHENTICATION_FAILED = Color.web("#FB7185");
    public static final Color NEW_PEER = Color.web("#60A5FA");
    
    // CSS Stylesheet
    public static final String STYLESHEET = 
        "/* Root Styling */\n" +
        ".root {\n" +
        "    -fx-font-family: 'Segoe UI', 'Arial', sans-serif;\n" +
        "    -fx-font-size: 11pt;\n" +
        "    -fx-background-color: linear-gradient(to bottom right, #081120, #0E1B2E 55%, #12233A);\n" +
        "}\n" +
        "\n" +
        "/* Main Container */\n" +
        ".main-container {\n" +
        "    -fx-background-color: transparent;\n" +
        "}\n" +
        "\n" +
        "/* Buttons */\n" +
        ".btn-primary {\n" +
        "    -fx-background-color: linear-gradient(to bottom right, #2DD4BF, #0F766E);\n" +
        "    -fx-text-fill: #07111D;\n" +
        "    -fx-font-size: 12pt;\n" +
        "    -fx-padding: 10px 20px;\n" +
        "    -fx-border-radius: 999;\n" +
        "    -fx-background-radius: 999;\n" +
        "    -fx-cursor: hand;\n" +
        "    -fx-font-weight: bold;\n" +
        "}\n" +
        "\n" +
        ".btn-primary:hover {\n" +
        "    -fx-background-color: linear-gradient(to bottom right, #5EEAD4, #14B8A6);\n" +
        "}\n" +
        "\n" +
        ".btn-primary:pressed {\n" +
        "    -fx-background-color: #0F766E;\n" +
        "}\n" +
        "\n" +
        ".btn-secondary {\n" +
        "    -fx-background-color: rgba(255, 255, 255, 0.08);\n" +
        "    -fx-text-fill: #EDF5FF;\n" +
        "    -fx-font-size: 11pt;\n" +
        "    -fx-padding: 8px 16px;\n" +
        "    -fx-border-color: #2A4667;\n" +
        "    -fx-border-radius: 999;\n" +
        "    -fx-background-radius: 999;\n" +
        "    -fx-cursor: hand;\n" +
        "}\n" +
        "\n" +
        ".btn-secondary:hover {\n" +
        "    -fx-background-color: rgba(96, 165, 250, 0.18);\n" +
        "}\n" +
        "\n" +
        "/* Text Fields */\n" +
        ".text-field {\n" +
        "    -fx-font-size: 11pt;\n" +
        "    -fx-padding: 8px;\n" +
        "    -fx-border-color: #2A4667;\n" +
        "    -fx-border-radius: 5;\n" +
        "    -fx-background-radius: 5;\n" +
        "    -fx-background-color: #0E1B2E;\n" +
        "    -fx-text-fill: #EDF5FF;\n" +
        "}\n" +
        "\n" +
        ".text-field:focused {\n" +
        "    -fx-border-color: #2DD4BF;\n" +
        "    -fx-border-width: 2;\n" +
        "}\n" +
        "\n" +
        "/* Labels */\n" +
        ".label-title {\n" +
        "    -fx-font-size: 18pt;\n" +
        "    -fx-font-weight: bold;\n" +
        "    -fx-text-fill: #93C5FD;\n" +
        "}\n" +
        "\n" +
        ".label-header {\n" +
        "    -fx-font-size: 14pt;\n" +
        "    -fx-font-weight: bold;\n" +
        "    -fx-text-fill: #EDF5FF;\n" +
        "}\n" +
        "\n" +
        ".label-info {\n" +
        "    -fx-font-size: 11pt;\n" +
        "    -fx-text-fill: #99ADC8;\n" +
        "}\n" +
        "\n" +
        "/* Cards/Panels */\n" +
        ".card {\n" +
        "    -fx-background-color: rgba(18, 35, 58, 0.94);\n" +
        "    -fx-border-color: #2A4667;\n" +
        "    -fx-border-radius: 16;\n" +
        "    -fx-background-radius: 16;\n" +
        "    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.28), 16, 0.12, 0, 8);\n" +
        "    -fx-padding: 15;\n" +
        "}\n" +
        "\n" +
        "/* Tabs */\n" +
        ".tab-pane {\n" +
        "    -fx-tab-min-width: 100;\n" +
        "    -fx-background-color: transparent;\n" +
        "}\n" +
        "\n" +
        ".tab {\n" +
        "    -fx-background-color: #0E1B2E;\n" +
        "    -fx-text-fill: #99ADC8;\n" +
        "}\n" +
        "\n" +
        ".tab:selected {\n" +
        "    -fx-background-color: linear-gradient(to bottom right, #2DD4BF, #60A5FA);\n" +
        "    -fx-text-fill: #081120;\n" +
        "}\n" +
        "\n" +
        "/* Progress Bar */\n" +
        ".progress-bar {\n" +
        "    -fx-accent: #2DD4BF;\n" +
        "}\n" +
        "\n" +
        "/* Scroll Pane */\n" +
        ".scroll-pane {\n" +
        "    -fx-background-color: transparent;\n" +
        "}\n" +
        "\n" +
        "/* ListView */\n" +
        ".list-view {\n" +
        "    -fx-background-color: rgba(8, 17, 32, 0.58);\n" +
        "    -fx-border-color: #2A4667;\n" +
        "    -fx-border-radius: 12;\n" +
        "    -fx-background-radius: 12;\n" +
        "}\n" +
        "\n" +
        ".list-cell {\n" +
        "    -fx-padding: 8px;\n" +
        "    -fx-border-color: transparent;\n" +
        "    -fx-text-fill: #EDF5FF;\n" +
        "}\n" +
        "\n" +
        ".list-cell:filled:selected {\n" +
        "    -fx-background-color: rgba(45, 212, 191, 0.22);\n" +
        "}\n" +
        "\n" +
        ".list-cell:filled:hover {\n" +
        "    -fx-background-color: rgba(96, 165, 250, 0.14);\n" +
        "}\n" +
        "\n" +
        "/* Separators */\n" +
        ".separator {\n" +
        "    -fx-padding: 10;\n" +
        "    -fx-opacity: 0.45;\n" +
        "}\n" +
        "\n" +
        "/* VBox and HBox */\n" +
        ".vbox, .hbox {\n" +
        "    -fx-spacing: 10;\n" +
        "    -fx-padding: 10;\n" +
        "}\n" +
        "\n" +
        "/* Status Indicator */\n" +
        ".status-online {\n" +
        "    -fx-fill: #34D399;\n" +
        "}\n" +
        "\n" +
        ".status-offline {\n" +
        "    -fx-fill: #FB7185;\n" +
        "}\n" +
        "\n" +
        ".status-connecting {\n" +
        "    -fx-fill: #F59E0B;\n" +
        "}\n";
    
    /**
     * Get the color as a hex string for CSS
     */
    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X", 
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }
}
