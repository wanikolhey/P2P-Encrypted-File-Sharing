//3rd file for sharing any type of file (text, image, pdf, etc.) using a simple TCP connection.
import java.io.*;
import java.net.*;

public class FileReceiver {
    public static void main(String[] args) {
        int port = 8888;
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Receiver is ready. Waiting for a file...");
            
            try (Socket socket = serverSocket.accept();
                 InputStream is = socket.getInputStream();
                 // We save it as 'received_file.dat' for now. 
                 FileOutputStream fos = new FileOutputStream("received_file.dat")) {
                
                System.out.println("Connection established! Receiving data...");

                byte[] buffer = new byte[4096]; //4KB buffer
                int bytesRead;
                long totalBytes = 0;

                // Read from network, write to disk
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                System.out.println("File received successfully!");
                System.out.println("Total size: " + totalBytes + " bytes");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

