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
    private static final String SERVER_ADDRESS = "localhost"; // Server address (localhost for testing)
    private static final int SERVER_PORT = 12345; // Server port

    public static void main(String[] args) {
        User user = null;

        List<Article> articlesInput = null;
        List<Article> userHistory = null;

        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             Scanner sc = new Scanner(System.in);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner serverInput = new Scanner(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            // After receiving the greeting message, initialize MenuInterface
            // This will display the menu
            System.out.println("===============================================================================================================================================");
            System.out.println("                                                           News Article Recommendation System                                                 \n");
            System.out.println("     Please Select An Option\n");
            System.out.println("     - 1. Login");
            System.out.println("     - 2. Sign Up");

            boolean loggedIn = false;
            int choice = 0;
            while (true) {
                System.out.print("\n     Your Choice: ");
                String input = scanner.nextLine().strip(); // Take input as a string

                try {
                    choice = Integer.parseInt(input); // Attempt to parse the input as an integer
                    if (choice == 1 || choice == 2) {
                        break; // Exit loop if the choice is valid
                    } else {
                        System.out.println("     Invalid choice! Please enter 1 or 2.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("     Invalid input! Please enter a valid integer (1 or 2).");
                }
            }

            if (choice == 1) {
                Login login = new Login();
                loggedIn = true;
                user = login.getUser();
            } else if (choice == 2) {
                Signup signup = new Signup();
                loggedIn = true;
                user = signup.getUser();
            }

            // Once logged in, show options
            if (loggedIn && "User".equalsIgnoreCase(user.getLoginType())) {

                FetchArticles fetch = new FetchArticles();
                List<Article> articles = fetch.fetchAllArticles();
                articlesInput = fetch.getArticlesInput();

                while (true) { // Outer loop for menu
                    System.out.println("===============================================================================================================================================");
                    System.out.println("\n                                                          --Choose an option--             ");
                    System.out.println("     - 1. View Articles");
                    System.out.println("     - 2. Get Recommendations");
                    System.out.println("     - 3. View Profile");
                    System.out.println("     - 4. Logout");

                    int option = -1; // Default invalid value for choice

                    // Validate if the input is an integer
                    while (true) {
                        try {
                            System.out.print("\n     - Enter choice: ");
                            String input = scanner.nextLine().strip();
                            option = Integer.parseInt(input); // Convert the string to an integer
                            System.out.println("\n");
                            break; // Exit validation loop on successful input
                        } catch (NumberFormatException e) {
                            System.out.println("       Invalid input! Please enter a valid integer (1-4).\n");
                        }
                    }

                    // Process the validated choice
                    switch (option) {
                        case 1:
                            View view = new View();
                            view.setUser(user);
                            view.displayArticles(articles);
                            break;
                        case 2:
                            userHistory = Database.retrievePreferences(user.getId(), articles);
                            articlesInput.removeAll(userHistory);
                            RecommendationEngine model = new RecommendationEngine();
                            model.setArticlesInput(articlesInput);
                            model.setUserHistory(userHistory);
                            model.Recommend();
                            break;
                        case 3:
                            UpdateProfile update = new UpdateProfile();
                            update.viewProfile(user);
                            break;
                        case 4:
                            System.out.println("       Logging out...");
                            return; // Exit the menu loop and method
                        default:
                            System.out.println("       Invalid option, please try again.");
                    }
                }
            }
            else if (loggedIn && "Admin".equalsIgnoreCase(user.getLoginType())) {
                while (true){
                    System.out.println("===============================================================================================================================================");
                    System.out.println("\n                                                          --Choose an option--             ");
                    System.out.println("     - 1. Add Articles");
                    System.out.println("     - 2. Edit Articles");
                    System.out.println("     - 3. Remove Articles");
                    System.out.println("     - 4. Logout");

                    int option = -1; // Default invalid value for choice

                    // Validate if the input is an integer
                    while (true) {
                        try {
                            System.out.print("\n     - Enter choice: ");
                            String input = scanner.nextLine().strip();
                            option = Integer.parseInt(input); // Convert the string to an integer
                            break; // Exit validation loop on successful input
                        } catch (NumberFormatException e) {
                            System.out.println("       Invalid input! Please enter a valid integer.");
                        }
                    }

                    AdminActions admin = new AdminActions();
                    admin.setUser(user);
                    // Process the validated choice
                    switch (option) {
                        case 1:
                            admin.addArticles();
                            break;
                        case 2:
                            admin.editArticle();
                            break;
                        case 3:
                            admin.deleteArticle();
                            break;
                        case 4:
                            System.out.println("       Logging out...");
                            return; // Exit the menu loop and method
                        default:
                            System.out.println("       Invalid option, please try again.");
                    }
                }
            }

            scanner.close();

        } catch (IOException e) {
            System.err.println("       Client error: " + e.getMessage());
        }
    }
}
