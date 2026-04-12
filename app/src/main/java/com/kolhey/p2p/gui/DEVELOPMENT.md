# GUI Development Guide

## Overview

This document provides guidance for developers extending the P2P File Sharing GUI application.

## Architecture

### Package Structure

```
com.kolhey.p2p.gui/
├── P2PFileShareApp.java                 # Main JavaFX Application
├── controllers/
│   ├── MainWindowController.java        # Main window orchestrator
│   ├── PeerDiscoveryController.java      # Peer management
│   └── FileTransferController.java       # File transfer handling
└── utils/
    ├── UITheme.java                     # Color scheme & CSS
    ├── UIComponentFactory.java           # Component factory
    ├── P2PServiceManager.java            # Backend integration
    ├── AppConstants.java                 # Application constants
    ├── AnimationUtils.java               # Animation utilities
    └── GuiLogger.java                    # Logging utility
```

## Key Classes

### P2PFileShareApp
- Main JavaFX Application entry point
- Bootstraps the GUI and main scene
- Handles window lifecycle

### MainWindowController
- Orchestrates all UI components
- Manages tab switching
- Handles node startup/shutdown
- Coordinates peer discovery updates

### UITheme
- Centralized color definitions
- CSS stylesheet management
- Theme switching support

### UIComponentFactory
- Factory methods for styled components
- Consistent UI element creation
- Reusable component patterns

### P2PServiceManager
- Singleton service manager
- Bridges GUI with P2P backend
- Manages node lifecycle
- Coordinates peer discovery

## Adding New Features

### Adding a New Tab

1. Create a new controller class:
```java
public class MyFeatureController {
    private P2PServiceManager serviceManager;
    
    public MyFeatureController(P2PServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    
    public VBox createFeatureView() {
        VBox container = new VBox();
        // Build your UI here
        return container;
    }
}
```

2. Add to MainWindowController:
```java
MyFeatureController myController = new MyFeatureController(serviceManager);
Tab myTab = new Tab("My Feature", myController.createFeatureView());
myTab.setStyle("-fx-text-fill: #2C2C2C;");
tabPane.getTabs().add(myTab);
```

### Adding New UI Components

1. Add factory method to UIComponentFactory:
```java
public static MyComponent createMyComponent(String text) {
    MyComponent component = new MyComponent();
    component.setStyle("-fx-background-color: #C4A0E9;");
    return component;
}
```

2. Use throughout the application

### Updating Theme

1. Modify UITheme.java:
```java
public static final Color MY_COLOR = Color.web("#HEXCODE");
```

2. Add CSS to stylesheet:
```java
public static final String STYLESHEET = 
    "..." +
    ".my-style {" +
    "    -fx-background-color: #C4A0E9;" +
    "}";
```

## Threading and Synchronization

### Important Patterns

Always use `Platform.runLater()` for GUI updates from background threads:

```java
executorService.execute(() -> {
    // Background work
    String result = doBackgroundWork();
    
    // Update GUI on FX thread
    Platform.runLater(() -> {
        label.setText(result);
    });
});
```

## Event Handling

### Button Events
```java
button.setOnAction(event -> {
    handleButtonClick();
});
```

### Observable Events
```java
service.addObserver((observable, arg) -> {
    Platform.runLater(() -> {
        updateUI();
    });
});
```

## Resource Management

### Proper Cleanup

Always clean up resources in shutdown handlers:

```java
@Override
public void shutdown() {
    if (timeline != null) timeline.stop();
    if (executor != null) executor.shutdown();
    if (service != null) service.stop();
}
```

## Logging

Use GuiLogger for consistent logging:

```java
GuiLogger.info("Node started");
GuiLogger.warn("No peers found");
GuiLogger.error("Transfer failed", exception);
GuiLogger.debug("Debug information");
```

## Testing GUI Components

### Manual Testing Checklist
- [ ] Component renders correctly
- [ ] Colors match theme
- [ ] Resizing works properly
- [ ] Events trigger correctly
- [ ] No memory leaks on repeated actions
- [ ] Thread safety (no UI freezes)

### Unit Testing
```java
@Test
public void testComponentCreation() {
    Button btn = UIComponentFactory.createPrimaryButton("Test");
    assertNotNull(btn);
    assertEquals("Test", btn.getText());
}
```

## Performance Optimization

### Best Practices

1. **Lazy Loading**: Load content only when needed
2. **Caching**: Cache discovered peers to reduce network calls
3. **Thread Pooling**: Use ExecutorService for background tasks
4. **UI Binding**: Use JavaFX binding for reactive updates

### Code Example
```java
// Good: Use Timeline with reasonable intervals
Timeline timeline = new Timeline(
    new KeyFrame(Duration.seconds(2), event -> refreshList())
);
timeline.setCycleCount(Animation.INDEFINITE);

// Bad: Don't use continuous refresh
Thread.sleep(1000) in main thread
```

## Error Handling

### User-Facing Errors
```java
Alert alert = new Alert(Alert.AlertType.ERROR);
alert.setTitle("Error");
alert.setHeaderText("Operation Failed");
alert.setContentText("Details about the error");
alert.showAndWait();
```

### Logging Errors
```java
try {
    doSomething();
} catch (Exception e) {
    GuiLogger.error("Operation failed", e);
}
```

## Styling Best Practices

### Use Factory Methods
```java
// Good
Button btn = UIComponentFactory.createPrimaryButton("Click");

// Avoid
Button btn = new Button("Click");
btn.setStyle("...");
```

### CSS Over Inline Styles
```java
// Prefer this:
button.getStyleClass().add("btn-primary");

// Over this:
button.setStyle("-fx-background-color: #C4A0E9;");
```

## Future Enhancements

### Planned Features
1. Dark mode theme support
2. Customizable port configuration UI
3. Transfer history and statistics
4. Advanced network settings
5. Plugin architecture for protocols
6. File preview functionality
7. Bandwidth throttling UI
8. System tray integration

### Extension Points

1. **Protocol Support**: Extend TransferProtocol interface
2. **UI Themes**: Create new theme classes extending UITheme
3. **Controllers**: Add new feature controllers same as existing ones
4. **Utilities**: Add helper classes to utils package

## Code Style Guidelines

### Naming Conventions
- Classes: PascalCase
- Methods/Variables: camelCase
- Constants: UPPER_SNAKE_CASE
- Private fields: prefixed with underscore (optional)

### Formatting
```java
public class MyClass {
    private String myField;
    
    public void myMethod() {
        // Implementation
    }
}
```

### Documentation
```java
/**
 * Brief description of the method.
 * 
 * @param param1 Description of param1
 * @return Description of return value
 */
public void myMethod(String param1) {
    // Implementation
}
```

## Debugging Tips

### Enable Debug Logging
```java
GuiLogger.debug("Variable value: " + value);
```

### Use System.out for Quick Tests
```java
System.out.println("[DEBUG] State: " + state);
```

### JavaFX Scene Graph Inspector
```bash
gradle runGui -Djavafx.debug=true
```

## Common Issues and Solutions

### Issue: UI Freezes
**Solution**: Move long operations to background thread
```java
executorService.execute(() -> {
    // Do work
    Platform.runLater(() -> {
        // Update UI
    });
});
```

### Issue: Color Theme Not Applied
**Solution**: Ensure CSS is loaded before creating components
```java
scene.getRoot().setStyle("-fx-background-color: #F5F5F5;");
```

### Issue: Memory Leak
**Solution**: Clean up observers and threads
```java
@Override
public void shutdown() {
    service.deleteObservers();
    executor.shutdown();
}
```

## Resources

- [JavaFX Documentation](https://openjfx.io/)
- [JavaFX CSS Reference](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/doc-files/cssref.html)
- [Project README](./README.md)
- [Main Project Documentation](../../../README.md)

## Contributing

When adding new features:
1. Follow existing code style
2. Test thoroughly
3. Update documentation
4. Add logging for debugging
5. Ensure backward compatibility
6. Submit pull request with description

---

**Last Updated**: 2026  
**Version**: 1.0.0
