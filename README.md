# P2P FileShare

A peer-to-peer encrypted file sharing application written in Java. It allows users to share files and media directly over a local area network without relying on third-party servers or the internet.

## About

P2P FileShare establishes direct connections between devices on the same network. All transfers are encrypted, keeping your files private and secure without routing data through external services.

## Requirements

- Java 11 or higher

## Usage

Compile and run the client:

```
javac testing/*.java testing/exceptions/*.java
java testing.Client
```

When prompted, enter the peer's IP address (e.g. `192.168.1.100`) and port number. Once connected, you can send data directly to the other device.

## License

See [LICENSE](LICENSE) for details.
