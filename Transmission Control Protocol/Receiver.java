//first file basic for text sharing.
import java.io.*;
import java.net.*;
public class Receiver {
    public static void main(String[] args) {
        int port = 8888; // This is the port we chose
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Receiver is waiting on port " + port + "...");
            System.out.println("Tell your friend your IP address (run 'ipconfig' to find it)!");

            // This line stops the program and waits until someone connects
            Socket socket = serverSocket.accept(); 
            System.out.println("Connected! Someone is sending a message...");

            // Setup tools to read the incoming text
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String message = reader.readLine();
            System.out.println("Message received: " + message);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
