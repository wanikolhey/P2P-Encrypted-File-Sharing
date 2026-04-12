#!/bin/bash
# P2P File Sharing - GUI Launch Script for Linux/macOS

echo "P2P File Sharing - Launching GUI..."
echo "Building project..."

cd "$(dirname "$0")"

# Build the project
gradle build -q

if [ $? -ne 0 ]; then
    echo "Build failed. Please check errors above."
    exit 1
fi

echo "Starting GUI application..."

# Run the GUI
gradle runGui

echo "GUI application closed."
