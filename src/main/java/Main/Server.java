package Main;
import Account.User;
import Article.ArticleCategorizer;
import Database.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        final int PORT = 12345; // Port for the server
        final int THREAD_POOL_SIZE = 10; // Number of threads in the thread pool

        User user = null;

        Scanner scanner = new Scanner(System.in);

        // Initialize database and create table if it doesn't exist
        Database.UsersTableMaker();
        Database.PreferenceTableMaker();
        Database.ArticlesTableMaker();
        ArticleCategorizer categorizer = new ArticleCategorizer();
        categorizer.Categorize();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept a client connection
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Handle the client in a separate thread
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }
    private static void handleClient(Socket clientSocket) {
        // Only log the connection and ensure the connection is kept open until the client disconnects
        try (BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            System.out.println("Client " + clientSocket.getInetAddress() + " is connected.");

            String message;
            // The server keeps the connection open and listens to client input
            while ((message = clientInput.readLine()) != null) {
                System.out.println("Received from client: " + message);
            }

            // When the client disconnects, the loop will exit and the socket will be closed
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close(); // Close the client connection
                System.out.println("Client socket closed.");
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
