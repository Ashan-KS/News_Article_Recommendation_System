package Admin;

import Account.User;
import Article.Article;
import Article.ArticleCategorizer;

import java.sql.*;
import java.util.Scanner;

public class AdminActions {

    // Represents a class handling article management
    private User user;  // Stores the user associated with the session
    private static final Scanner scanner; // Shared scanner instance for user input

    // Sets the current user
    public void setUser(User user) {
        this.user = user;
    }

    // Gets the current user
    public User getUser() {
        return user;
    }

    // Prompts the user for input with a message
    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().strip();
    }

    // Static block to initialize the scanner
    static {
        scanner = new Scanner(System.in);
    }

    // Facilitates adding multiple articles in one session
    public void addArticles() {
        boolean addMore = true;

        while (addMore) {
            System.out.println("\n===============================================================================================================================================");
            this.addArticle(); // Adds a single article
            System.out.println("\n     ------------------------------------------");
            System.out.print("     Do you want to add another article? (yes/no): ");
            String response = scanner.nextLine().strip().toLowerCase();
            System.out.println("\n===============================================================================================================================================");
            if (!response.equals("yes")) {
                addMore = false; // Exits loop if user doesn't confirm
                ArticleCategorizer categorizer = new ArticleCategorizer();
                categorizer.Categorize();
            }
        }
    }

    // Collects details for a new article and stores it in the database
    public void addArticle() {
        System.out.println("\n     --- Add Article ---");

        while (true) {
            String headline = this.getInput("     - Enter headline: ");
            if (!headline.isEmpty()) {
                while (true) {
                    String description = this.getInput("     - Enter description: ");
                    if (!description.isEmpty()) {
                        // Collects remaining fields
                        String authors = this.getInput("     - Enter authors: ");
                        String url = this.getInput("     - Enter URL: ");
                        String date = this.getInput("     - Enter date (YYYY-MM-DD): ");
                        String category = ""; // Placeholder for category

                        // Creates a new Article object
                        Article newArticle = new Article(headline, description, authors, category, url, date);
                        this.insertArticleToDatabase(newArticle); // Inserts into database
                        return;
                    }
                    System.out.println("     Error: Description cannot be empty!");
                }
            }
            System.out.println("     Error: Headline cannot be empty!");
        }
    }

    // Inserts a new article into the database
    private void insertArticleToDatabase(Article article) {
        String url = "jdbc:sqlite:articles.db";
        String insertSQL = "INSERT INTO articles (headline, description, authors, category, predicted, url, date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // Sets parameters for the prepared statement
            pstmt.setString(1, article.getHeadline());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getAuthors());
            pstmt.setString(4, article.getCategory());
            pstmt.setString(5, article.getCategory());
            pstmt.setString(6, article.getUrl());
            pstmt.setString(7, article.getDate());
            pstmt.executeUpdate(); // Executes the insertion query
            System.out.println("\n     Article added successfully!");

        } catch (SQLException e) {
            System.out.println("     Error adding article: " + e.getMessage());
        }

        // Triggers categorization after article is added
        ArticleCategorizer categorizer = new ArticleCategorizer();
        categorizer.Categorize();
    }

    // Deletes an article based on its ID
    public void deleteArticle() {
        String url = "jdbc:sqlite:articles.db";
        System.out.println("\n===============================================================================================================================================");
        System.out.println("\n     --- Delete Article ---");

        Article article = null;
        int articleID;
        while (true) {
            try {
                // Reads and validates the article ID
                System.out.print("     - Enter the Article ID you want to delete: ");
                articleID = Integer.parseInt(scanner.nextLine().strip());

                article = getArticle(articleID);

                // Check if the article is null (not found)
                if (article == null) {
                    System.out.println("     Invalid Article ID. No article found with the given ID.\n");
                }
                else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("       Invalid Article ID. Please enter a valid number.\n");
                return;
            }
        }

        printArticleDetails(article);

        String confirmation = null;
        while (true){
            System.out.print("\n     Are you sure you want to delete this article? (yes/no): ");
            confirmation = scanner.nextLine().strip().toLowerCase();
            if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("no")){
                break;
            }
        }

        if (confirmation.equalsIgnoreCase("yes")) {
            String deleteSQL = "DELETE FROM articles WHERE articleID = ?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmtDelete = conn.prepareStatement(deleteSQL)) {

                pstmtDelete.setInt(1, articleID); // Sets article ID for deletion
                int rowsAffected = pstmtDelete.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("\n     Article deleted successfully!\n");
                } else {
                    System.out.println("\n     Article deletion failed. No rows affected.");
                }

            } catch (SQLException e) {
                System.out.println("     Error deleting article: " + e.getMessage());
            }

        } else {
            System.out.println("\n     Article deletion cancelled.");
        }
    }

    private void printArticleDetails(Article article) {
        System.out.println("\n     Current Article Details:");
        System.out.println("     1. Headline: " + article.getHeadline());
        System.out.println("     2. Description: " + article.getDescription());
        System.out.println("     3. Authors: " + article.getAuthors());
        System.out.println("     4. Category: " + article.getCategory());
        System.out.println("     5. URL: " + article.getUrl());
        System.out.println("     6. Date: " + article.getDate());
    }

    public Article getArticle(int articleID) {
        String url = "jdbc:sqlite:articles.db";
        String selectSQL = "SELECT * FROM articles WHERE articleID = ?";
        Article article = null;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setInt(1, articleID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    article = new Article(
                            rs.getString("headline"),
                            rs.getString("description"),
                            rs.getString("authors"),
                            rs.getString("predicted"),
                            rs.getString("url"),
                            rs.getString("date")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching article: " + e.getMessage());
        }

        return article;
    }
    public void editArticle() {
        String url = "jdbc:sqlite:articles.db";
        System.out.println("\n===============================================================================================================================================");
        System.out.println("\n     --- Edit Article ---");

        Article article = null;
        int articleID;
        while (true) {
            try {
                // Reads and validates the article ID
                System.out.print("     - Enter the Article ID you want to edit: ");
                articleID = Integer.parseInt(scanner.nextLine().strip());

                article = getArticle(articleID);

                // Check if the article is null (not found)
                if (article == null) {
                    System.out.println("     Invalid Article ID. No article found with the given ID.\n");
                }
                else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("       Invalid Article ID. Please enter a valid number.");
                return;
            }
        }

        // Edit the article fields
        if (article != null) {
            editArticleFields(article);
        }

        String updateSQL = "UPDATE articles SET headline = ?, description = ?, authors = ?, category = ?, url = ?, date = ? WHERE articleID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, article.getHeadline());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getAuthors());
            pstmt.setString(4, "");
            pstmt.setString(5, article.getUrl());
            pstmt.setString(6, article.getDate());
            pstmt.setInt(7, articleID);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("\n     Article Updated Successfully!\n");
            } else {
                System.out.println("\n     No changes were made to the article.");
            }

            ArticleCategorizer categorizer = new ArticleCategorizer();
            categorizer.Categorize();

        } catch (SQLException e) {
            System.out.println("Error updating article: " + e.getMessage());
        }
    }

    private void editArticleFields(Article article) {
        while(true) {
            System.out.println("\n===============================================================================================================================================");
            System.out.println("\n     --- Article Details ---");
            System.out.println("     1. Headline: " + article.getHeadline());
            System.out.println("     2. Description: " + article.getDescription());
            System.out.println("     3. Authors: " + article.getAuthors());
            System.out.println("     4. Category: " + article.getCategory());
            System.out.println("     5. URL: " + article.getUrl());
            System.out.println("     6. Date: " + article.getDate());
            System.out.println("     7. Save and Exit");
            System.out.print("\n     Enter the number of the field you want to edit: ");
            switch (scanner.nextLine().strip()) {
                case "1":
                    article.setHeadline(this.getInput("\n     - Enter new headline: "));
                    break;
                case "2":
                    article.setDescription(this.getInput("\n     - Enter new description: "));
                    break;
                case "3":
                    article.setAuthors(this.getInput("\n     - Enter new authors: "));
                    break;
                case "4":
                    System.out.println("\n     - Category is auto generated");
                    break;
                case "5":
                    article.setUrl(this.getInput("\n     - Enter new URL: "));
                    break;
                case "6":
                    article.setDate(this.getInput("\n     - Enter new date (YYYY-MM-DD): "));
                    break;
                case "7":
                    return;
                default:
                    System.out.println("\n       Invalid choice. Please enter a number between 1 and 7.");
            }
        }
    }
}
