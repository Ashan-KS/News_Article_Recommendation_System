//package RecommendationEngine;
//
//import Article.Article;
//
//import java.util.List;
//
//public class Model{
//    List<Article> articlesInput = null;
//    List<Article> userHistory = null;
//    public static String articlesToPlainText(List<Article> articles) {
//        StringBuilder plainText = new StringBuilder();
//        for (Article article : articles) {
//            plainText.append("Title: ").append(article.getHeadline()).append("\n");
//            plainText.append("Description: ").append(article.getDescription()).append("\n\n");
//            plainText.append("Rating: ").append(article.getRating()).append("\n\n");
//        }
//        return plainText.toString().trim(); // Remove trailing newline
//    }
//    public void setArticlesInput(List<Article> articlesInput) {
//        this.articlesInput = articlesInput;
//    }
//
//    public List<Article> getArticlesInput() {
//        return this.articlesInput;
//    }
//
//    public void setUserHistory(List<Article> userHistory) {
//        this.userHistory = userHistory;
//    }
//
//    public List<Article> getUserHistory() {
//        return this.userHistory;
//    }
//
//    public void Recommend(){
//
//
//    }
//
//}
