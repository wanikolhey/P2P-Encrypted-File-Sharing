# P2P FileShare

A peer-to-peer encrypted file sharing application written in Java. It allows users to share files and media directly over a local area network without relying on third-party servers or the internet.

## About

P2P FileShare establishes direct connections between devices on the same network. All transfers are encrypted, keeping your files private and secure without routing data through external services.

## Features

✨ **Modern GUI** - Intuitive desktop application with lilac and off-white color scheme  
🔐 **End-to-End Encryption** - All file transfers are encrypted  
📡 **Peer Discovery** - Automatic discovery of available peers using mDNS  
🚀 **Multiple Transfer Protocols** - Support for QUIC and WebSocket  
💻 **Cross-Platform** - Works on Windows, macOS, and Linux  
🌐 **Local Network** - No internet required, works on LAN  

## Requirements

- Java 17 or higher
- JavaFX 21.0.3 (automatically downloaded by Gradle)

## Quick Start

### Running the GUI Application (Recommended)

```bash
cd app
gradle runGui
```

The GUI will launch with:
- **Dashboard** - Overview and quick stats
- **Peers** - Discover and manage connected peers
- **Transfer Files** - Send files securely to peers
- **Settings** - Configure node parameters

### Running CLI Mode

```bash
cd app
gradle run
```

Or directly:

```bash
java -cp "build/libs/*" com.kolhey.p2p.Main
```

## GUI Features

### 🎨 User Interface
- **Modern Design**: Clean, professional interface with lilac and off-white color scheme
- **Responsive Layout**: Adapts to different window sizes
- **Real-time Updates**: Live peer discovery and transfer progress
- **Status Indicators**: Visual feedback for peer connectivity and transfer status

### 📱 Dashboard
- Welcome message and quick overview
- Statistics on connected peers and transfers
- Quick access to common actions

### 🔍 Peer Discovery
- Real-time list of available peers on the network
- Network information (IP addresses, ports)
- One-click connect and file send functionality
- Automatic peer refresh

### 📤 File Transfer
- Intuitive file selection interface
- Recipient selection from discovered peers
- Real-time progress tracking
- Transfer history

### ⚙️ Settings
- Node configuration (QUIC and WebSocket ports)
- Application information
- Network settings

## Building from Source

### Prerequisites
```bash
# Ensure you have Java 17+
java -version

# Or configure in build.gradle.kts
```

### Build Steps

```bash
# Build the entire project
gradle build

# Run tests
gradle test

# Create distribution
gradle assemble
```

## Usage Examples

### Launch GUI
```bash
gradle runGui
```

### Run in CLI Mode
```bash
gradle run
```

### Get Help
```bash
java -cp "build/libs/*" com.kolhey.p2p.Main --help
```

## Architecture

### Backend Components
- **PeerDiscoveryManager** - Discovers peers using mDNS/JmDNS
- **QuicServerNode/QuicClientNode** - QUIC protocol implementation
- **WsServerNode/WsClientNode** - WebSocket protocol support
- **FileIOService** - File transfer and persistence
- **Security Managers** - Encryption and TLS handling

### Frontend Components
- **P2PFileShareApp** - Main JavaFX application
- **MainWindowController** - Central UI orchestrator
- **PeerDiscoveryController** - Peer list management
- **FileTransferController** - File transfer UI
- **UITheme & UIComponentFactory** - Consistent styling

## Color Theme

The GUI uses a professional lilac and off-white color scheme:

| Element | Color | Hex Code |
|---------|-------|----------|
| Primary Accent | Lilac | #C4A0E9 |
| Light Accent | Light Lilac | #E6D5F5 |
| Dark Accent | Dark Lilac | #9575CD |
| Background | Off-White | #F5F5F5 |
| Card Background | White | #FFFFFF |
| Success | Green | #4CAF50 |
| Warning | Orange | #FF9800 |
| Error | Red | #F44336 |

## Project Structure

```
.
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/kolhey/p2p/
│   │   │       ├── Main.java
│   │   │       ├── crypto/          # Encryption & security
│   │   │       ├── discovery/       # Peer discovery
│   │   │       ├── io/              # File I/O
│   │   │       ├── quic/            # QUIC protocol
│   │   │       ├── ws/              # WebSocket protocol
│   │   │       └── gui/             # GUI application
│   │   │           ├── P2PFileShareApp.java
│   │   │           ├── controllers/
│   │   │           └── utils/
│   │   └── test/
│   └── build.gradle.kts
├── testing/                          # CLI testing utilities
├── gradle/
├── README.md
└── LICENSE
```

## Troubleshooting

### GUI Won't Start
1. Verify Java version: `java -version` (need 17+)
2. Check JavaFX is available: `gradle dependencies | grep javafx`
3. Run with stacktrace: `gradle runGui --stacktrace`

### Peers Not Discovered
1. Firewall may block mDNS (port 5353)
2. Ensure both devices on same network
3. Check network connectivity: `ping <peer_ip>`
4. Restart the application

### File Transfer Fails
1. Verify file path and permissions
2. Ensure recipient node is running
3. Check network connectivity
4. Review application logs

## Security Considerations

- **SSL/TLS**: All connections use secure protocols
- **Encryption**: File transfers are encrypted with BouncyCastle
- **Local Network Only**: No exposure to public internet
- **Open Source**: Code available for security review

## Development

### Adding Features

1. **New GUI Component**: Add to `UIComponentFactory.java`
2. **New Controller**: Create in `controllers/` package
3. **Styling Updates**: Modify `UITheme.java` CSS
4. **Backend Integration**: Use `P2PServiceManager.java`

### Running Tests
```bash
gradle test
```

### Code Style
- Follow Java naming conventions
- Use JavaFX `Platform.runLater()` for UI thread operations
- Implement proper resource cleanup in shutdown hooks

## Performance Notes

- **File Chunks**: 64KB chunks for efficient memory usage
- **Connection Pool**: Netty NIO for high-efficiency networking
- **Concurrent Operations**: Thread pool for parallel peer discovery
- **Memory**: ~256MB minimum, scales with transfer sizes

## Future Roadmap

- [ ] Batch file transfers
- [ ] Transfer history and statistics
- [ ] Bandwidth throttling
- [ ] Dark mode theme
- [ ] Application system tray integration
- [ ] File integrity verification
- [ ] Advanced network settings UI
- [ ] Plugin system for custom protocols

## License

See [LICENSE](LICENSE) for details.

## Contributing

Contributions are welcome! Please ensure:
- Code follows project style guidelines
- All tests pass: `gradle test`
- Documentation is updated
- Commits are clear and descriptive

## Support

For issues, feature requests, or questions:
1. Check existing documentation
2. Review the troubleshooting section
3. Check the issues on the repository
4. Provide detailed error messages and logs when reporting issues

---

**Version**: 1.0.0  
**Last Updated**: 2026  
**Status**: Production Ready
