# Quick Start Guide - P2P File Sharing GUI

## Installation

### Prerequisites
- **Java 17+** installed on your system
- **Git** (to clone the repository)

### Step 1: Verify Java Installation

Open terminal/command prompt and run:
```bash
java -version
```

You should see Java 17 or higher. If not, download from [java.com](https://www.java.com).

### Step 2: Clone the Repository

```bash
git clone [repository-url]
cd P2P-Encrypted-File-Sharing
```

### Step 3: Build the Project

```bash
cd app
gradle build
```

This will download all dependencies including JavaFX. First build may take 2-3 minutes.

## Launching the Application

### Option 1: Using Gradle (Recommended)

```bash
gradle runGui
```

The GUI will automatically launch.

### Option 2: Using Scripts

**Windows:**
```bash
..\run-gui.bat
```

**macOS/Linux:**
```bash
chmod +x ../run-gui.sh
../run-gui.sh
```

## First Time Setup

### 1. Start a Node

When the application launches:
1. Click the green **"▶ Start Node"** button in the top right
2. The status will change from "Offline" (red) to "Online" (green)
3. You'll be assigned a node name like "Node-A1B2"

### 2. Discover Peers

The **Peers** tab will automatically show available peers on your network:
1. Go to the **Peers** tab
2. Wait 1-2 seconds for peer discovery
3. Connected peers will appear as badges with:
   - Peer name
   - IP address
   - Available protocols and ports

### 3. Send a File

Transfer files through the **Transfer Files** tab:

1. Click **"📂 Select File"**
2. Choose a file from your computer
3. Recipient will be auto-populated if peers are available
4. Click **"🚀 Send File"**
5. Monitor progress in the "Active Transfers" section

## Understanding the Interface

### Dashboard Tab
- Overview of your node
- Quick statistics
- Fast access to common actions

### Peers Tab
- Lists discovered peers on your network
- Shows network information
- Quick connect buttons

### Transfer Files Tab
- File selection
- Progress tracking
- Transfer history

### Settings Tab
- Node configuration
- Network port settings
- Application information

## Common Tasks

### Send a File to a Peer

1. Go to **Transfer Files** tab
2. Click **"📂 Select File"**
3. Choose your file
4. Select recipient peer
5. Click **"🚀 Send File"**
6. Wait for transfer to complete

### Check Connected Peers

1. Go to **Peers** tab
2. See all available peers with:
   - Node names
   - IP addresses
   - Supported protocols
   - Status indicators

### Configure Network Ports

1. Go to **Settings** tab
2. Under "Node Configuration":
   - Set QUIC Port (default: 9000)
   - Set WebSocket Port (default: 8080)
3. Changes require node restart

### Stop the Application

1. Click the red **"⏹ Stop Node"** button
2. Or close the window directly

## Troubleshooting

### Peers Not Showing Up

**Problem**: No peers discovered even though other devices are on network

**Solutions**:
1. Ensure firewall allows mDNS (port 5353)
2. Make sure all devices are on the same network
3. Restart the application
4. Check that other device also has the app running

### File Transfer Fails

**Problem**: File transfer doesn't complete or shows error

**Solutions**:
1. Verify file still exists and isn't modified
2. Ensure recipient node is running
3. Check network connectivity
4. Try sending a smaller file first

### GUI Won't Start

**Problem**: Application won't launch

**Solutions**:
```bash
# Check Java version
java -version

# Verify JavaFX is available
gradle dependencies | grep javafx

# Run with debug info
gradle runGui --stacktrace
```

### Slow Performance

**Problem**: GUI is sluggish or unresponsive

**Solutions**:
1. Close other applications to free resources
2. Reduce number of peers being synced
3. Pause large file transfers
4. Restart the application

## Security Notes

- ✅ All transfers are encrypted
- ✅ No data passes through external servers
- ✅ Works only on local networks
- ✅ Each connection uses TLS

## File Storage

Received files are saved to:
- **Windows**: `downloads` folder in your home directory
- **macOS/Linux**: `downloads` folder in current directory

## Next Steps

### Basic Usage
1. ✅ Start node
2. ✅ Discover peers
3. ✅ Send a file
4. ✅ Receive a file

### Advanced Usage
- Configure custom ports
- Monitor transfer statistics
- Check application logs in `logs` folder

### For Developers
- See [DEVELOPMENT.md](./gui/DEVELOPMENT.md) for extending the application

## Key Features Recap

| Feature | How to Use |
|---------|----------|
| Start P2P Node | Click "▶ Start Node" button |
| Find Peers | Go to "Peers" tab |
| Send File | "Transfer Files" tab → Select file & peer → Click "Send" |
| Check Status | Watch indicator in header (red=offline, green=online) |
| Stop Node | Click "⏹ Stop Node" button |

## Support

### Check Logs
```bash
# View application logs
cat logs/p2p-gui.log
```

### Restart Application
Simply close and reopen the application.

### Reset Settings
Delete the `logs` folder to clear history (optional).

## Performance Tips

1. **Keep nodes running**: Leave application open to maintain peer discovery
2. **Use same network**: All devices must be on same WiFi/LAN
3. **Monitor transfers**: Large files may take time depending on network speed
4. **Close unused apps**: Free up system resources

## What's Next?

- Share files between multiple devices
- Use for local network backups
- Collaborate with team members on LAN
- Secure file exchange without cloud services

---

**Version**: 1.0.0  
**Last Updated**: 2026

For more help, see the [Main README](../../../README.md) or [GUI README](./gui/README.md)
