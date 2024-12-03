package Account;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Signup {
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
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

            if (!isValidEmail(email)) {
                System.out.println("Invalid email format. Please enter a valid email.");
                continue;
            }

            this.insertEmail(email);

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
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.updateUser(this.user.getEmail());

        System.out.println("\n                                                     Account Registered Successfully                                                         ");
        System.out.println("=============================================================================================================================================");
        System.out.println("=============================================================================================================================================");
        System.out.println("                                                            Welcome back " + user.getUsername());
    }
    public void insertEmail(String email) {
        String url = "jdbc:sqlite:articles.db";
        String insertSQL = "INSERT INTO users (email, password, username, loginType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, email);
            pstmt.setString(2, "");
            pstmt.setString(3, "");
            pstmt.setString(4, "");
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }
}