package Article;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FetchArticles {
    // Lists to store articles fetched from the database
    private List<Article> articles = new ArrayList<>();
    private List<Article> articlesInput = new ArrayList<>();

    // Getter for the list of articles with simplified input data
    public List<Article> getArticlesInput() {
        return this.articlesInput;
    }

    // Fetch all articles from the database and populate the articles and articlesInput lists
    public List<Article> fetchAllArticles() {
        String url = "jdbc:sqlite:articles.db"; // Database connection URL
        String selectSQL = "SELECT * FROM articles"; // SQL query to retrieve all articles

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            // Iterate through the result set and create Article objects
            while (rs.next()) {
                String headline = rs.getString("headline");
                String desc = rs.getString("description");
                String authors = rs.getString("authors");
                String category = rs.getString("category");
                String link = rs.getString("url");
                String date = rs.getString("date");
                int id = rs.getInt("articleID");

                // Create an Article object with full details
                Article article = new Article(headline, desc, authors, category, link, date, id);

                // Create a simplified Article object for input processing
                Article articleInput = new Article(headline, desc, category, 0, url);

                // Add articles to their respective lists
                this.articles.add(article);
                this.articlesInput.add(articleInput);
            }

        } catch (SQLException e) {
            // Log errors encountered while fetching articles
            System.out.println("Error fetching articles: " + e.getMessage());
        }

        return this.articles; // Return the full list of articles
    }

}
