package Admin;

import Account.User;
import Article.Article;
import Article.ArticleCategorizer;

import java.sql.*;
import java.util.Scanner;

public class AdminActions {

    private User user;
    private static final Scanner scanner;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().strip();
    }

    static {
        scanner = new Scanner(System.in);
    }

    public void addArticles() {
        boolean addMore = true;

        while(addMore) {
            this.addArticle();
            System.out.println("\n------------------------------------------");
            System.out.print("Do you want to add another article? (yes/no): ");
            String response = scanner.nextLine().strip().toLowerCase();
            if (!response.equals("yes")) {
                addMore = false;
                System.out.println("\nReturning to the main menu...");
            }
        }
    }

    public void addArticle() {
        System.out.println("\n------------------------------------------");
        System.out.println("           Add New Article");
        System.out.println("------------------------------------------");

        while(true) {
            String headline = this.getInput("Enter headline: ");
            if (!headline.isEmpty()) {
                while(true) {
                    String description = this.getInput("Enter description: ");
                    if (!description.isEmpty()) {
                        String authors = this.getInput("Enter authors: ");
                        String url = this.getInput("Enter URL: ");
                        String date = this.getInput("Enter date (YYYY-MM-DD): ");
                        String category = "";
                        Article newArticle = new Article(headline, description, authors, category, url, date);
                        this.insertArticleToDatabase(newArticle);
                        return;
                    }

                    System.out.println("Error: Description cannot be empty!");
                }
            }

            System.out.println("Error: Headline cannot be empty!");
        }
    }



    private void insertArticleToDatabase(Article article) {
        String url = "jdbc:sqlite:articles.db";
        String insertSQL = "INSERT INTO articles (headline, description, authors, category, predicted, url, date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, article.getHeadline());
            pstmt.setString(2, article.getDescription());
            pstmt.setString(3, article.getAuthors());
            pstmt.setString(4, article.getCategory());
            pstmt.setString(5, article.getCategory());
            pstmt.setString(6, article.getUrl());
            pstmt.setString(7, article.getDate());
            pstmt.executeUpdate();
            System.out.println("\nArticle added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding article: " + e.getMessage());
        }

        ArticleCategorizer categorizer = new ArticleCategorizer();
        categorizer.Categorize();
    }

    public void deleteArticle() {
        String url = "jdbc:sqlite:articles.db";
        System.out.println("\n--- Delete Article ---");
        System.out.print("Enter the Article ID you want to delete: ");

        int articleID;
        try {
            articleID = Integer.parseInt(scanner.nextLine().strip());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Article ID. Please enter a valid number.");
            return;
        }

        String selectSQL = "SELECT * FROM articles WHERE articleID = ?";
        Article article = null;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmtSelect = conn.prepareStatement(selectSQL)) {

            pstmtSelect.setInt(1, articleID);
            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                    article = new Article(
                            rs.getString("headline"),
                            rs.getString("description"),
                            rs.getString("authors"),
                            rs.getString("category"),
                            rs.getString("url"),
                            rs.getString("date")
                    );

                    System.out.println("\nArticle found:");
                    printArticleDetails(article);
                } else {
                    System.out.println("No article found with the given ID.");
                    return;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching article: " + e.getMessage());
            return;
        }

        System.out.print("\nAre you sure you want to delete this article? (yes/no): ");
        String confirmation = scanner.nextLine().strip().toLowerCase();

        if (confirmation.equals("yes")) {
            String deleteSQL = "DELETE FROM articles WHERE articleID = ?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmtDelete = conn.prepareStatement(deleteSQL)) {

                pstmtDelete.setInt(1, articleID);
                int rowsAffected = pstmtDelete.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("\nArticle deleted successfully!");
                } else {
                    System.out.println("\nArticle deletion failed. No rows affected.");
                }

            } catch (SQLException e) {
                System.out.println("Error deleting article: " + e.getMessage());
            }

        } else {
            System.out.println("\nArticle deletion cancelled.");
        }
    }

    private void printArticleDetails(Article article) {
        System.out.println("\nCurrent Article Details:");
        System.out.println("1. Headline: " + article.getHeadline());
        System.out.println("2. Description: " + article.getDescription());
        System.out.println("3. Authors: " + article.getAuthors());
        System.out.println("4. Category: " + article.getCategory());
        System.out.println("5. URL: " + article.getUrl());
        System.out.println("6. Date: " + article.getDate());
    }

    public void editArticle() {
        String url = "jdbc:sqlite:articles.db";
        System.out.println("\n--- Edit Article ---");
        System.out.print("Enter the Article ID you want to edit: ");

        int articleID;
        try {
            articleID = Integer.parseInt(scanner.nextLine().strip());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Article ID. Please enter a valid number.");
            return;
        }

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
                            rs.getString("category"),
                            rs.getString("url"),
                            rs.getString("date")
                    );
                    System.out.println("\nArticle found! Ready to edit.\n");
                } else {
                    System.out.println("No article found with the given ID.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching article: " + e.getMessage());
            return;
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
            pstmt.setString(4, article.getCategory());
            pstmt.setString(5, article.getUrl());
            pstmt.setString(6, article.getDate());
            pstmt.setInt(7, articleID);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("\nArticle updated successfully!");
            } else {
                System.out.println("\nNo changes were made to the article.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating article: " + e.getMessage());
        }
    }

    private void editArticleFields(Article article) {
        while(true) {
            System.out.println("\nCurrent Article Details:");
            System.out.println("1. Headline: " + article.getHeadline());
            System.out.println("2. Description: " + article.getDescription());
            System.out.println("3. Authors: " + article.getAuthors());
            System.out.println("4. Category: " + article.getCategory());
            System.out.println("5. URL: " + article.getUrl());
            System.out.println("6. Date: " + article.getDate());
            System.out.println("7. Save and Exit");
            System.out.print("\nEnter the number of the field you want to edit: ");
            switch (scanner.nextLine().strip()) {
                case "1":
                    article.setHeadline(this.getInput("Enter new headline: "));
                    break;
                case "2":
                    article.setDescription(this.getInput("Enter new description: "));
                    break;
                case "3":
                    article.setAuthors(this.getInput("Enter new authors: "));
                    break;
                case "4":
                    article.setCategory(this.getInput("Enter new category: "));
                    break;
                case "5":
                    article.setUrl(this.getInput("Enter new URL: "));
                    break;
                case "6":
                    article.setDate(this.getInput("Enter new date (YYYY-MM-DD): "));
                    break;
                case "7":
                    System.out.println("\nSaving changes...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
            }
        }
    }
}
