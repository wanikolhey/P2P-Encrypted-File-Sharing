//2nd file basic for text sharing.
import java.io.*;
import java.net.*;
public class Sender {
    public static void main(String[] args) {
        // Change this to 2nd computer's IP address
        // Use "localhost" if you are testing on the same computer.
        String receiverIP = "localhost"; 
        int port = 8888;

        try (Socket socket = new Socket(receiverIP, port)) {
            System.out.println("Successfully connected to " + receiverIP);

            // Setup tools to send text
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("Hello from the other side! Phase 1 complete.");
            System.out.println("Message sent!");

        } catch (IOException e) {
            System.out.println("Could not connect. Is the Receiver running?");
            e.printStackTrace();
        }
    }
}