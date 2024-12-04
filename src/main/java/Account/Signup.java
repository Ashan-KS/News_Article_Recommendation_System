package Account;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Signup {
    private User user;

    // Setter method to assign the created user object
    public void setUser(User user) {
        this.user = user;
    }

    // Getter method to retrieve the created user object
    public User getUser() {
        return this.user;
    }

    // Validates the format of an email using a regex pattern
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    // Constructor to handle user registration
    public Signup() {
        System.out.println("\n===============================================================================================================================================");
        System.out.println("                                                                  New User Registration                                                        \n");

        Scanner scanner = new Scanner(System.in);
        String dbUrl = "jdbc:sqlite:articles.db";
        String email;

        // Loop to ensure a valid and unique email is provided
        while (true) {
            System.out.print("     - Enter email: ");
            email = scanner.nextLine().strip();

            if (!isValidEmail(email)) {
                System.out.println("       Invalid email format. Please enter a valid email.\n");
                continue;
            }

            // Attempt to insert the email into the database
            if (!insertEmail(email)) {
                System.out.println("       This email is already taken by a user. Please enter a different email.\n");
                continue;
            } else {
                break; // Email is valid and unique
            }
        }

        // Prompt for additional user details
        System.out.print("     - Enter password: ");
        String password = scanner.nextLine();
        System.out.print("     - Enter username: ");
        String username = scanner.nextLine();

        // Create a new User object with the provided details
        this.user = new User(username, email, password, "User");

        // Update user profile after registration
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.setUser(user);
        updateProfile.updateUser(this.user.getEmail());

        // Registration success message
        System.out.println("\n                                                     Account Registered Successfully                                                         ");
        System.out.println("===============================================================================================================================================");
        System.out.println("                                                            Welcome " + user.getUsername());
    }

    // Inserts the email into the database and checks if it is unique
    public boolean insertEmail(String email) {
        String url = "jdbc:sqlite:articles.db";
        String insertSQL = "INSERT INTO users (email, password, username, loginType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            conn.setAutoCommit(false); // Start transaction

            pstmt.setString(1, email);
            pstmt.setString(2, ""); // Default empty password
            pstmt.setString(3, ""); // Default empty username
            pstmt.setString(4, ""); // Default empty loginType

            pstmt.executeUpdate(); // Attempt to insert the email
            conn.commit(); // Commit the transaction
            return true; // Email inserted successfully

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                // Email already exists in the database
                return false;
            }
            System.out.println("Error inserting email: " + e.getMessage());
            return false;
        }
    }
}
