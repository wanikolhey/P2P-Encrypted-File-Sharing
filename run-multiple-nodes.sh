#!/bin/bash
# Script to run multiple P2P nodes for testing on the same device
# Usage: ./run-multiple-nodes.sh [number_of_nodes]

NUM_NODES=${1:-3}  # Default to 3 nodes if not specified
BASE_QUIC_PORT=9000
BASE_WS_PORT=8080

echo "==========================================="
echo "P2P File Sharing - Multi-Node Test Setup"
echo "==========================================="
echo "Starting $NUM_NODES nodes..."
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to run a node
run_node() {
    local node_num=$1
    local quic_port=$((BASE_QUIC_PORT + node_num - 1))
    local ws_port=$((BASE_WS_PORT + node_num - 1))
    
    echo -e "${BLUE}[Node $node_num]${NC} Starting with QUIC:$quic_port, WS:$ws_port"
    
    # Launch in background with labels
    ./gradlew run \
        --args="--node-name=TestNode-$node_num --quic-port=$quic_port --ws-port=$ws_port" \
        2>&1 | sed "s/^/[Node $node_num] /" &
}

# Build first
echo -e "${GREEN}Building project...${NC}"
./gradlew build -q > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo -e "${GREEN}Build successful!${NC}"
echo ""

# Launch nodes
for ((i=1; i<=NUM_NODES; i++)); do
    run_node $i
    sleep 2  # Stagger startup
done

echo ""
echo -e "${GREEN}All nodes started!${NC}"
echo -e "${BLUE}Ports being used:${NC}"
for ((i=1; i<=NUM_NODES; i++)); do
    quic=$((BASE_QUIC_PORT + i - 1))
    ws=$((BASE_WS_PORT + i - 1))
    echo "  Node $i: QUIC=$quic, WebSocket=$ws"
done

echo ""
echo "Press Ctrl+C to stop all nodes..."
wait
