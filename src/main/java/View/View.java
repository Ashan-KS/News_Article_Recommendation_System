package View;

import java.util.List;
import java.util.Scanner;
import Account.User;
import Article.Article;
import Database.Database;

public class View {
    User user; // Holds the current user object
    private static final int PAGE_SIZE = 10; // Number of articles to display per page

    // Sets the current user for this view instance
    public void setUser(User user) {
        this.user = user;
    }

    // Returns the current user
    public User getUser() {
        return this.user;
    }

    // Displays a list of articles, with pagination support
    public void displayArticles(List<Article> articles) {
        Scanner scanner = new Scanner(System.in); // Scanner for user input
        if (articles.isEmpty()) {
            // If no articles are available, display a message and exit
            System.out.println("\n     No articles available to display.");
        } else {
            int totalArticles = articles.size(); // Total number of articles available
            int currentPage = 0; // Tracks the current page of articles

            while (true) {
                int start = currentPage * PAGE_SIZE; // Index of the first article on the current page
                int end = Math.min(start + PAGE_SIZE, totalArticles); // Index of the last article on the current page

                // Display the header for the current page of articles
                System.out.println("===============================================================================================================================================");
                System.out.println("\n                                                       --- Articles (Page " + (currentPage + 1) + ") ---\n");

                // Display articles for the current page
                for (int i = start; i < end; i++) {
                    System.out.printf("     %d. %s\n", i + 1, articles.get(i).getHeadline()); // Print the headline of each article
                }

                // Display navigation options based on the current page and the number of articles
                if (end < totalArticles && currentPage > 0) {
                    System.out.print("\n     Enter the number of the article to view, 'n' to view the next page, 'p' to view the previous page, or 'q' to quit:");
                } else if (end < totalArticles) {
                    System.out.print("\n     Enter the number of the article to view, 'n' to view the next page, or 'q' to quit:");
                } else if (currentPage > 0) {
                    System.out.print("\n     Enter the number of the article to view, 'p' to view the previous page, or 'q' to quit:");
                } else {
                    System.out.print("\n     Enter the number of the article to view or 'q' to quit:");
                }

                // Read the user input and process it
                String input = scanner.nextLine().strip();
                if (input.equalsIgnoreCase("q")) {
                    // If the user enters 'q', exit the article view
                    System.out.println("     Exiting article view...");
                    return;
                }

                if (input.equalsIgnoreCase("n") && end < totalArticles) {
                    // If 'n' is entered and there are more articles, go to the next page
                    currentPage++;
                } else if (input.equalsIgnoreCase("p") && currentPage > 0) {
                    // If 'p' is entered and the current page is greater than 0, go to the previous page
                    currentPage--;
                } else {
                    try {
                        // Try parsing the input as an article number and validate the range
                        int selectedIndex = Integer.parseInt(input) - 1;
                        if (selectedIndex >= start && selectedIndex < end) {
                            // If the selected index is valid, display the article details
                            Article selectedArticle = articles.get(selectedIndex);
                            this.displayArticleDetails(selectedArticle);
                        } else {
                            // Display an error if the article number is invalid
                            System.out.println("     Invalid article number. Please select a valid article.");
                        }
                    } catch (NumberFormatException e) {
                        // Handle non-integer input
                        System.out.println("     Invalid input. Please enter a valid number or command.");
                    }
                }
            }
        }
    }

    // Displays detailed information about a selected article and allows the user to rate it
    private void displayArticleDetails(Article article) {
        Scanner scanner = new Scanner(System.in); // Scanner for user input

        // Display detailed information about the selected article
        System.out.println("===============================================================================================================================================");
        System.out.println("                                            " + article.getHeadline());
        System.out.println("     Date          : " + article.getDate());
        System.out.println("     Authors       : " + article.getAuthors());
        System.out.println("     Category      : " + article.getCategory());
        System.out.println("     Description   : " + article.getDescription());
        System.out.println("     Link          : " + article.getUrl());

        // Prompt the user to rate the article
        System.out.println("\n     --- Rate This Article ---");
        System.out.println("     1. Like");
        System.out.println("     2. Neutral");
        System.out.println("     3. Dislike");

        while (true) {
            try {
                System.out.print("     Enter your rating: ");
                // Read the rating input
                String input = scanner.nextLine().strip();
                int rating;

                // Map user input to corresponding ratings
                if (input.equals("1")) {
                    rating = 1; // Like
                } else if (input.equals("2")) {
                    rating = 0; // Neutral
                } else if (input.equals("3")) {
                    rating = -1; // Dislike
                } else {
                    throw new IllegalArgumentException("     Invalid option. Please enter 1, 2, or 3.\n");
                }

                // Set the rating for the article and update user preferences in the database
                article.setRating(rating);
                Database database = new Database();
                database.updateUserPreferences(this.user.getId(), article);
                return; // Exit after rating the article
            } catch (IllegalArgumentException e) {
                // Handle invalid input for rating
                System.out.println(e.getMessage());
            }
        }
    }
}
