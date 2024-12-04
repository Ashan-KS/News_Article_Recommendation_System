import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminRegister {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:articles.db";
        String insertSQL = "INSERT INTO users (email, password, username, loginType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, "admin@gmail.com");
            pstmt.setString(2, "ashan");
            pstmt.setString(3, "ashan");
            pstmt.setString(4, "Admin");
            pstmt.executeUpdate();

        } catch (SQLException sqlException) {
            System.out.println("Error inserting user: " + sqlException.getMessage());
        }
    }
}

