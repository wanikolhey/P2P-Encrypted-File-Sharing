//4th file for sharing any type of file (text, image, pdf, etc.) using a simple TCP connection.
import java.io.*;
import java.net.*;

public class FileSender {
    public static void main(String[] args) {
        String receiverIP = "localhost"; 
        int port = 8888;
        String fileName = "test_image.jpg"; 

        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Error: File not found! Make sure " + fileName + " is in the folder.");
            return;
        }

        try (Socket socket = new Socket(receiverIP, port);
             FileInputStream fis = new FileInputStream(file);
             OutputStream os = socket.getOutputStream()) {

            System.out.println("Connected to receiver. Sending: " + fileName);

            byte[] buffer = new byte[4096]; // same 4KB buffer as the receiver
            int bytesRead;

            // Read from disk, write to network
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

