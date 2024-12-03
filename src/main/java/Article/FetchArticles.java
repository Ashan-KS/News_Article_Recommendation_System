package Article;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FetchArticles {
    private List<Article> articles = new ArrayList();
    private List<Article> articlesInput = new ArrayList();

    public List<Article> getArticlesInput() {
        return this.articlesInput;
    }

    public List<Article> fetchAllArticles() {
        String url = "jdbc:sqlite:articles.db";
        String selectSQL = "SELECT * FROM articles";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                String headline = rs.getString("headline");
                String desc = rs.getString("description");
                String authors = rs.getString("authors");
                String category = rs.getString("category");
                String link = rs.getString("url");
                String date = rs.getString("date");
                int id = rs.getInt("articleID");

                Article article = new Article(headline, desc, authors, category, link, date, id);
                Article articleInput = new Article(headline, desc, category, 0, url);

                this.articles.add(article);
                this.articlesInput.add(articleInput);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching articles: " + e.getMessage());
        }

        return this.articles;
    }
}
