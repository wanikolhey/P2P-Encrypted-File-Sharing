# P2P Encrypted File Sharing

A decentralized, secure, and high-speed file transfer application built in Java. This project enables two users on a Local Area Network (LAN) to exchange files directly with end-to-end encryption, bypassing the need for central cloud servers.

---

## Key Features
- **Decentralized Architecture:** True Peer-to-Peer (P2P) communication using Java Sockets.
- **End-to-End Encryption (E2EE):** All data is encrypted using **AES-256 (GCM Mode)** before leaving the sender's device.
- **Secure Key Agreement:** Implements **ECDH (Elliptic Curve Diffie-Hellman)** to securely generate shared keys over an insecure network.
- **Resource Efficient:** Uses chunk-based streaming (4KB buffers) to handle large files without crashing system memory.
- **Integrity Verification:** Uses **SHA-256 Hashing** to ensure the received file is a bit-perfect match of the original.

---

## Technical Overview
The system follows a modular Object-Oriented design:
1. **Network Module:** Manages TCP/IP connections, port listening, and multi-threaded data handling.
2. **Crypto Module:** Handles the generation of public/private keys and the encryption/decryption pipeline.
3. **IO Module:** Manages local file reading/writing and the assembly of metadata headers.
4. **UI Module:** A Command Line Interface (CLI) that coordinates the user's input and displays progress.



---

## Tech Stack
- **Language:** Java (JDK 17+)
- **Security:** Java Cryptography Extension (JCE)
- **Networking:** Java Sockets API
- **Version Control:** Git & GitHub

---

## Getting Started

### Prerequisites
- **Java Development Kit (JDK) 17** or higher.
- Two devices connected to the same Wi-Fi/LAN.
