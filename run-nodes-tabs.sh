#!/bin/bash
# Start multiple nodes in separate terminal tabs
# Usage: ./run-nodes-tabs.sh [number_of_nodes]

NUM_NODES=${1:-3}
BASE_QUIC=9000
BASE_WS=8080

echo "Starting $NUM_NODES nodes in separate terminal windows..."
echo ""

# On macOS, use 'open' with Terminal app
# On Linux, use 'gnome-terminal', 'konsole', or 'xterm'

for ((i=1; i<=NUM_NODES; i++)); do
    QUIC_PORT=$((BASE_QUIC + i - 1))
    WS_PORT=$((BASE_WS + i - 1))
    NODE_NAME="TestNode-$i"
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        open -a Terminal <<EOF
cd "$(pwd)"
echo "[Node $i] Starting with QUIC:$QUIC_PORT, WS:$WS_PORT"
./gradlew run >/dev/null 2>&1 &
echo "[Node $i] Waiting for GUI... (Process ID: \$!)"
wait
EOF
    else
        # Linux
        gnome-terminal -- bash -c "cd $(pwd); ./gradlew run; read -p 'Press Enter to close...'" &
    fi
    
    echo "[Node $i] Launched - QUIC Port: $QUIC_PORT, WS Port: $WS_PORT"
    sleep 1
done

echo ""
echo "All nodes launched in separate windows!"
echo "Remember to configure different ports in each node's Settings tab:"
echo "  Node 1: QUIC=9000, WS=8080 (default)"
echo "  Node 2: QUIC=9001, WS=8081"
echo "  Node 3: QUIC=9002, WS=8082"
