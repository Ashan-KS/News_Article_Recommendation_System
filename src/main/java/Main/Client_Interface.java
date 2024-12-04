package Main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import Admin.AdminActions;
import View.View;
import Account.Login;
import Account.Signup;
import Account.UpdateProfile;
import Account.User;
import Article.Article;
import Article.FetchArticles;
import Database.Database;
import RecommendationEngine.RecommendationEngine;
import java.util.*;

public class Client_Interface {
    private static final String SERVER_ADDRESS = "localhost"; // Server address for connection
    private static final int SERVER_PORT = 12345; // Port to connect to the server

    public static void main(String[] args) {
        User user = null; // User object to hold the logged-in user
        List<Article> articlesInput = null; // List for input articles
        List<Article> userHistory = null; // List to store user's reading history

        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             Scanner sc = new Scanner(System.in);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner serverInput = new Scanner(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            // Display the main menu for login or sign-up
            System.out.println("===============================================================================================================================================");
            System.out.println("                                                           News Article Recommendation System                                                 \n");
            System.out.println("     Please Select An Option\n");
            System.out.println("     - 1. Login");
            System.out.println("     - 2. Sign Up");

            boolean loggedIn = false; // Flag to track user login status
            int choice = 0; // Choice for login or sign-up

            // Validate input for login or sign-up choice
            while (true) {
                System.out.print("\n     - Enter Choice: ");
                String input = scanner.nextLine().strip();

                try {
                    choice = Integer.parseInt(input);
                    if (choice == 1 || choice == 2) {
                        break; // Valid input, exit the loop
                    } else {
                        System.out.println("     Invalid choice! Please enter 1 or 2.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("     Invalid input! Please enter a valid integer (1 or 2).");
                }
            }

            // Log in or sign up based on user choice
            if (choice == 1) {
                Login login = new Login();
                loggedIn = true;
                user = login.getUser();
            } else if (choice == 2) {
                Signup signup = new Signup();
                loggedIn = true;
                user = signup.getUser();
            }

            // Show options for users after logging in
            if (loggedIn && "User".equalsIgnoreCase(user.getLoginType())) {

                FetchArticles fetch = new FetchArticles();
                List<Article> articles = fetch.fetchAllArticles();
                articlesInput = fetch.getArticlesInput();

                while (true) { // Main menu loop for user options
                    System.out.println("===============================================================================================================================================");
                    System.out.println("\n                                                          --Choose an option--             ");
                    System.out.println("     - 1. View Articles");
                    System.out.println("     - 2. Get Recommendations");
                    System.out.println("     - 3. View Profile");
                    System.out.println("     - 4. Logout");

                    int option = -1; // Placeholder for user choice

                    // Validate input for menu options
                    while (true) {
                        try {
                            System.out.print("\n     - Enter choice: ");
                            String input = scanner.nextLine().strip();
                            option = Integer.parseInt(input);
                            break; // Exit validation loop on valid input
                        } catch (NumberFormatException e) {
                            System.out.println("       Invalid input! Please enter a valid integer (1-4).\n");
                        }
                    }

                    // Process user menu options
                    switch (option) {
                        case 1:
                            View view = new View();
                            view.setUser(user);
                            view.displayArticles(articles); // Display articles to the user
                            break;
                        case 2:
                            System.out.println("\n===============================================================================================================================================\n");
                            System.out.println("     Loading Recommendaions...\n");
                            userHistory = Database.retrievePreferences(user.getId(), articles);
                            articlesInput.removeAll(userHistory); // Exclude articles already in user history
                            RecommendationEngine model = new RecommendationEngine();
                            model.setArticlesInput(articlesInput);
                            model.setUserHistory(userHistory);
                            model.Recommend(); // Generate and display recommendations
                            System.out.println("\n");
                            break;
                        case 3:
                            UpdateProfile update = new UpdateProfile();
                            update.viewProfile(user); // Display user profile
                            break;
                        case 4:
                            System.out.println("\n       Logging out...");
                            System.out.println("\n===============================================================================================================================================\n");
                            return; // Exit the user menu
                        default:
                            System.out.println("       Invalid option, please try again.");
                    }
                }
            }
            // Admin-specific menu if logged in as admin
            else if (loggedIn && "Admin".equalsIgnoreCase(user.getLoginType())) {
                while (true) {
                    System.out.println("===============================================================================================================================================");
                    System.out.println("\n                                                          --Choose an option--             ");
                    System.out.println("     - 1. Add Articles");
                    System.out.println("     - 2. Edit Articles");
                    System.out.println("     - 3. Remove Articles");
                    System.out.println("     - 4. Logout");

                    int option = -1; // Placeholder for admin choice

                    // Validate input for admin menu options
                    while (true) {
                        try {
                            System.out.print("\n     - Enter choice: ");
                            String input = scanner.nextLine().strip();
                            option = Integer.parseInt(input);
                            break; // Exit validation loop on valid input
                        } catch (NumberFormatException e) {
                            System.out.println("       Invalid input! Please enter a valid integer.");
                        }
                    }

                    AdminActions admin = new AdminActions();
                    admin.setUser(user);
                    // Process admin menu options
                    switch (option) {
                        case 1:
                            admin.addArticles(); // Admin adds new articles
                            break;
                        case 2:
                            admin.editArticle(); // Admin edits an existing article
                            break;
                        case 3:
                            admin.deleteArticle(); // Admin deletes an article
                            break;
                        case 4:
                            System.out.println("\n       Logging out...");
                            System.out.println("\n===============================================================================================================================================\n");
                            return; // Exit the admin menu
                        default:
                            System.out.println("       Invalid option, please try again.");
                    }
                }
            }

            scanner.close();

        } catch (IOException e) {
            System.err.println("       Client error: " + e.getMessage()); // Handle client-side IO exceptions
        }
    }
}
