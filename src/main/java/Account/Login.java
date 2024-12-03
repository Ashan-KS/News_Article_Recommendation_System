package Account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Login {
    private User user;

    public User getUser() {
        return this.user;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public Login() {
        System.out.println("\n===============================================================================================================================================");
        System.out.println("                                                           Login to Existing Account                                                          ");

        Scanner scanner = new Scanner(System.in);
        String url = "jdbc:sqlite:articles.db";

        while (true) {
            System.out.print("\n     - Enter email: ");
            String email = scanner.nextLine().strip();

            // Validate the email format
            if (!isValidEmail(email)) {
                System.out.println("       Invalid email format. Please enter a valid email.");
                continue; // Skip to the next iteration to prompt for the email again
            }

            String query = "SELECT userID, password, username, loginType FROM users WHERE email = ?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        this.user = new User(rs.getString("username"), email, rs.getString("password"), rs.getString("loginType"));
                        this.user.setId(rs.getInt("userID"));
                        break;
                    } else {
                        System.out.println("       No account registered with this email. Please try again.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("       An error occurred while accessing the database: " + e.getMessage());
            }
        }

        String loginType;
        while(true) {
            System.out.print("     - Enter password: ");
            loginType = scanner.nextLine().strip();
            if (loginType.equals(this.user.getPassword())) {
                while(true) {
                    System.out.print("     - Select login type (User/Admin): ");
                    loginType = scanner.nextLine().strip();
                    if (!loginType.equalsIgnoreCase("User") && !loginType.equalsIgnoreCase("Admin")) {
                        System.out.println("       Invalid login type. Please enter either 'User' or 'Admin'.\n");
                    }
                    else if (!loginType.equalsIgnoreCase("Admin") || !"User".equalsIgnoreCase(this.user.getLoginType())) {
                        this.user.setLoginType(loginType);
                        break;
                    }
                    else {
                        System.out.println("       Access denied. Admin privileges are unavailable. Please log in as a user.\n");
                    }
                }

                System.out.println("\n=============================================================================================================================================");
                System.out.println("                                                            Welcome back " + this.user.getUsername());
                return;
            }
            System.out.println("       Password Incorrect, Try again\n");
        }
    }
}
