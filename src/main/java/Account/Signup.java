package Account;

import java.sql.*;
import java.util.Scanner;

public class Signup {
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public Signup() {
        System.out.println("\n=============================================================================================================================================");
        System.out.println("                                                     New User Registration                                                        ");

        Scanner scanner = new Scanner(System.in);
        String dbUrl = "jdbc:sqlite:articles.db";
        String email;

        while (true) {
            System.out.print("     - Enter email: ");
            email = scanner.nextLine().strip();

            String checkEmailQuery = "SELECT 1 FROM users WHERE email = ?";
            try (Connection conn = DriverManager.getConnection(dbUrl);
                 PreparedStatement pstmt = conn.prepareStatement(checkEmailQuery)) {

                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("This email is already taken by a user. Please enter a different email.");
                        continue;
                    } else {
                        break;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error checking email: " + e.getMessage());
            }
        }

        System.out.print("     - Enter password: ");
        String password = scanner.nextLine();
        System.out.print("     - Enter username: ");
        String username = scanner.nextLine();

        this.user = new User(username, email, password, "User");
        this.insertUser();

        System.out.println("\n                                                     Account Registered Successfully                                                         ");
        System.out.println("=============================================================================================================================================");
        System.out.println("=============================================================================================================================================");
        System.out.println("                                                            Welcome back " + user.getUsername());
    }

    public void insertUser() {
        String dbUrl = "jdbc:sqlite:articles.db";
        String insertSQL = "INSERT INTO users (email, password, username, loginType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, this.user.getEmail());
            pstmt.setString(2, this.user.getPassword());
            pstmt.setString(3, this.user.getUsername());
            pstmt.setString(4, this.user.getLoginType());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.user.setId(rs.getInt(1));
                }
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }
}