# P2P File Sharing GUI

A modern, visually appealing desktop application for secure peer-to-peer file sharing over local networks.

## Features

### 🎨 Modern UI Design
- **Lilac & Off-White Color Scheme**: Professional and modern aesthetic
- **Responsive Layout**: Adapts to different screen sizes
- **Intuitive Navigation**: Easy-to-use tabbed interface

### 🔐 Security
- **End-to-End Encryption**: All transfers are encrypted
- **Peer Discovery**: Automatic discovery of available peers on the network
- **Secure Communication**: Uses QUIC and WebSocket protocols with TLS

### 📁 File Transfer
- **Drag & Drop Support**: Select files easily
- **Progress Tracking**: Real-time transfer progress
- **Multiple Protocols**: Support for QUIC and WebSocket
- **Batch Transfers**: Send multiple files

### 🌐 Peer Management
- **Live Peer Discovery**: See available peers in real-time
- **Network Information**: View IP addresses and available ports
- **Status Indicators**: Visual indicators for peer status

## Running the Application

### Option 1: Using Gradle (Recommended)

```bash
cd app
gradle runGui
```

### Option 2: Using the JAR File

```bash
gradle build
java --add-modules javafx.controls,javafx.fxml -cp "build/libs/*" com.kolhey.p2p.gui.P2PFileShareApp
```

## GUI Structure

### Dashboard Tab
- Welcome message
- Quick statistics (connected peers, active transfers, encryption status)
- Quick access buttons

### Peers Tab
- List of discovered peers on the network
- Real-time peer status
- Network information (IP addresses, ports)
- Quick connect and send file options

### Transfer Files Tab
- File selection interface
- Recipient selection
- Transfer progress tracking
- History of completed transfers

### Settings Tab
- Node configuration (QUIC and WebSocket port settings)
- Application information

## Color Theme

### Primary Colors
- **Lilac**: `#C4A0E9` - Primary accent color
- **Light Lilac**: `#E6D5F5` - Background accents
- **Dark Lilac**: `#9575CD` - Hover states

### Background Colors
- **Off-White**: `#F5F5F5` - Main background
- **White**: `#FFFFFF` - Card backgrounds
- **Light Gray**: `#E8E8E8` - Secondary backgrounds

### Semantic Colors
- **Success**: `#4CAF50` - Online status, successful operations
- **Warning**: `#FF9800` - Connecting status, warnings
- **Error**: `#F44336` - Offline status, errors
- **Info**: `#2196F3` - Information

## Architecture

### Package Structure
```
com.kolhey.p2p.gui/
├── P2PFileShareApp.java           # Main application entry point
├── controllers/
│   ├── MainWindowController.java   # Main window orchestrator
│   ├── PeerDiscoveryController.java # Peer management
│   └── FileTransferController.java  # File transfer handling
└── utils/
    ├── UITheme.java                # Theme and styling constants
    ├── UIComponentFactory.java      # Reusable UI components
    └── P2PServiceManager.java       # Backend service integration
```

### Key Components

#### UITheme
Centralized styling configuration with color constants and CSS stylesheets.

#### UIComponentFactory
Factory methods for creating consistently styled UI components:
- Buttons (primary, secondary, small)
- Labels (title, header, info)
- Cards and panels
- Input fields
- Progress indicators

#### P2PServiceManager
Singleton service manager that bridges the GUI with the P2P backend:
- Node lifecycle management
- Peer discovery coordination
- File IO service integration

#### Controllers
- **MainWindowController**: Orchestrates all tabs and main UI flow
- **PeerDiscoveryController**: Manages peer list display and updates
- **FileTransferController**: Handles file selection and transfer UI

## System Requirements

- **Java Version**: 17 or higher
- **JavaFX**: 21.0.3 (automatically downloaded by Gradle)
- **Network**: Local network connectivity for peer discovery
- **RAM**: Minimum 256MB, recommended 512MB+

## Troubleshooting

### GUI Won't Start
1. Ensure Java 17+ is installed: `java -version`
2. Check JavaFX is properly configured: `gradle dependencies | grep javafx`
3. Try running with explicit module settings:
   ```bash
   gradle runGui --stacktrace
   ```

### Peers Not Discovered
1. Ensure your firewall allows peer discovery (mDNS ports 5353)
2. Check network connectivity: `ping <other_peer_ip>`
3. Verify both nodes are on the same network
4. Restart the application

### File Transfer Issues
1. Check file permissions and path validity
2. Ensure recipient node is running and discoverable
3. Monitor transfer progress in the GUI
4. Check logs for detailed error messages

## Future Enhancements

- [ ] Batch file selection and transfer
- [ ] Transfer history and statistics
- [ ] Custom port configuration UI
- [ ] Bandwidth limitation settings
- [ ] File encryption key exchange UI
- [ ] Dark mode theme
- [ ] Application tray integration
- [ ] File preview for supported formats

## Development

### Adding New Features

1. **New UI Component**: Add to `UIComponentFactory.java`
2. **New Controller**: Create in `controllers/` package
3. **New Styling**: Update `UITheme.java` CSS stylesheet

### Code Style
- Consistent naming conventions
- JavaFX Platform.runLater() for thread-safe UI updates
- Proper resource cleanup in shutdown handlers

## Support

For issues or feature requests, please refer to the main repository documentation.
