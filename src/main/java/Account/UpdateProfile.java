package Account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UpdateProfile {
    private User user;

    // Setter to assign a User object
    public void setUser(User user) {
        this.user = user;
    }

    // Displays the user profile and allows navigation to update options
    public void viewProfile(User user) {
        this.user = user;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int pwLength = user.getPassword().length();
            String pwStars = "*".repeat(pwLength); // Mask the password with asterisks

            // Display user profile details
            System.out.println("\n===============================================================================================================================================\n");
            System.out.println("     ===========================================================================");
            System.out.println("     |                              User Profile                               |");
            System.out.println("     ===========================================================================");
            System.out.printf("     | %-15s | %-50s    |\n", "1. Username", user.getUsername());
            System.out.printf("     | %-15s | %-50s    |\n", "2. Email", user.getEmail());
            System.out.printf("     | %-15s | %-50s    |\n", "3. Password", pwStars);
            System.out.printf("     | %-15s | %-50s    |\n", "4. Login Type", user.getLoginType());
            System.out.println("     ===========================================================================");

            // Provide options to update or exit
            System.out.println("     Options:");
            System.out.println("     1. Update Profile");
            System.out.println("     2. Go Back to Menu");
            System.out.println("     ===========================================================================");
            System.out.print("     - Enter choice: ");

            switch (scanner.nextLine().strip()) {
                case "1":
                    System.out.println("\n");
                    this.updateUserProfile(); // Navigate to update profile options
                    break;
                case "2":
                    return; // Exit profile view
                default:
                    System.out.println("     Invalid choice. Please try again.");
            }
        }
    }

    // Validates the email format using a regex
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    // Provides options to update the username, email, or password
    public void updateUserProfile() {
        Scanner scanner = new Scanner(System.in);
        String prevEmail = this.user.getEmail(); // Store the current email for reference in updates

        while (true) {
            System.out.println("===============================================================================================================================================\n");
            System.out.println("     ==================================================");
            System.out.println("                    Update Profile Options             ");
            System.out.println("     ==================================================");
            System.out.println("     1. Update Username");
            System.out.println("     2. Update Email");
            System.out.println("     3. Update Password");
            System.out.println("     4. Exit");
            System.out.println("     ==================================================");
            System.out.print("     - Enter choice: ");

            switch (scanner.nextLine().strip()) {
                case "1": // Update username
                    System.out.print("\n     - Enter new username: ");
                    String newUsername = scanner.nextLine().strip();
                    this.user.setUsername(newUsername);
                    System.out.println("       Username updated successfully!\n");
                    this.updateUser(prevEmail);
                    break;
                case "2": // Update email
                    System.out.print("\n     - Enter new email: ");
                    String newEmail = scanner.nextLine().strip();

                    if (isValidEmail(newEmail)) {
                        if (this.isEmailUnique(newEmail)) {
                            this.user.setEmail(newEmail);
                            System.out.println("       Email updated successfully!\n");
                            this.updateUser(prevEmail);
                        } else {
                            System.out.println("       Error: This email is already in use. Please try again.\n");
                        }
                    } else {
                        System.out.println("       Invalid email format. Please try again.\n");
                    }
                    break;
                case "3": // Update password
                    System.out.print("\n     - Enter your current password: ");
                    String currentPassword = scanner.nextLine();
                    if (this.user.getPassword().equals(currentPassword)) {
                        System.out.print("     - Enter new password: ");
                        String newPassword = scanner.nextLine();
                        this.user.setPassword(newPassword);
                        System.out.println("       Password updated successfully!\n");
                        this.updateUser(prevEmail);
                    } else {
                        System.out.println("       Error: Incorrect current password. Please try again.\n");
                    }
                    break;
                case "4": // Exit update menu
                    this.updateUser(prevEmail);
                    System.out.println("       Profile changes saved. Exiting...");
                    return;
                default:
                    System.out.println("       Invalid choice. Please try again.\n");
            }
        }
    }

    // Checks if the new email is unique in the database
    public boolean isEmailUnique(String newEmail) {
        String query = "SELECT email FROM users WHERE email = ?";
        String url = "jdbc:sqlite:articles.db";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                return !rs.next(); // Return true if no matching email is found
            }
        } catch (SQLException e) {
            System.out.println("     Error checking email: " + e.getMessage());
            return false;
        }
    }

    // Updates the user's details in the database
    public void updateUser(String prevEmail) {
        String url = "jdbc:sqlite:articles.db";
        String updateQuery = "UPDATE users SET username = ?, email = ?, password = ?, loginType = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Bind the updated user details to the query
            stmt.setString(1, this.user.getUsername());
            stmt.setString(2, this.user.getEmail());
            stmt.setString(3, this.user.getPassword());
            stmt.setString(4, this.user.getLoginType());
            stmt.setString(5, prevEmail);

            stmt.executeUpdate(); // Execute the update query

        } catch (SQLException e) {
            System.out.println("     Error updating user: " + e.getMessage());
        }
    }
}
