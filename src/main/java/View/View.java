package View;

import java.util.List;
import java.util.Scanner;
import Account.User;
import Article.Article;
import Database.Database;

public class View {
    User user;
    private static final int PAGE_SIZE = 10;

    public View() {
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void displayArticles(List<Article> articles) {
        Scanner scanner = new Scanner(System.in);
        if (articles.isEmpty()) {
            System.out.println("\nNo articles available to display.");
        } else {
            int totalArticles = articles.size();
            int currentPage = 0;

            while (true) {
                int start = currentPage * PAGE_SIZE;
                int end = Math.min(start + PAGE_SIZE, totalArticles);
                System.out.println("===============================================================================================================================================");
                System.out.println("\n                                                       --- Articles (Page " + (currentPage + 1) + ") ---\n");

                for (int i = start; i < end; i++) {
                    System.out.printf("     %d. %s\n", i + 1, articles.get(i).getHeadline());
                }

                if (end < totalArticles && currentPage > 0) {
                    System.out.print("\n     Enter the number of the article to view, 'n' to view the next page, 'p' to view the previous page, or 'q' to quit:");
                } else if (end < totalArticles) {
                    System.out.print("\n     Enter the number of the article to view, 'n' to view the next page, or 'q' to quit:");
                } else if (currentPage > 0) {
                    System.out.print("\n     Enter the number of the article to view, 'p' to view the previous page, or 'q' to quit:");
                } else {
                    System.out.print("\n     Enter the number of the article to view or 'q' to quit:");
                }

                String input = scanner.nextLine().strip();
                if (input.equalsIgnoreCase("q")) {
                    System.out.println("     Exiting article view...");
                    return;
                }

                if (input.equalsIgnoreCase("n") && end < totalArticles) {
                    currentPage++;
                } else if (input.equalsIgnoreCase("p") && currentPage > 0) {
                    currentPage--;
                } else {
                    try {
                        int selectedIndex = Integer.parseInt(input) - 1;
                        if (selectedIndex >= start && selectedIndex < end) {
                            Article selectedArticle = articles.get(selectedIndex);
                            this.displayArticleDetails(selectedArticle);
                        } else {
                            System.out.println("       Invalid article number. Please select a valid article.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("       Invalid input. Please enter a valid number or command.");
                    }
                }
            }
        }
    }

    private void displayArticleDetails(Article article) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===============================================================================================================================================");
        System.out.println("                                            "+ article.getHeadline());
        System.out.println("     Date          : " + article.getDate());
        System.out.println("     Authors       : " + article.getAuthors());
        System.out.println("     Category      : " + article.getCategory());
        System.out.println("     Description   : " + article.getDescription());
        System.out.println("     Link          : " + article.getUrl());

        System.out.println("\n     --- Rate This Article ---");
        System.out.println("     1. Like");
        System.out.println("     2. Neutral");
        System.out.println("     3. Dislike");
        System.out.print("     Enter your rating: ");

        while (true) {
            try {
                String input = scanner.nextLine().strip();
                int rating;

                if (input.equals("1")) {
                    rating = 1;
                } else if (input.equals("2")) {
                    rating = 0;
                } else if (input.equals("3")) {
                    rating = -1;
                } else {
                    throw new IllegalArgumentException("Invalid option. Please enter 1, 2, or 3.");
                }

                article.setRating(rating);
                Database database = new Database();
                database.updateUserPreferences(this.user.getId(), article);
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
