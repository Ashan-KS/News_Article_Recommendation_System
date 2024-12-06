package Account;

import Database.Database;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Signup {
    private User user;
    private Database database = new Database();

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
            if (!database.insertEmail(email)) {
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
        database.setUser(user);
        database.updateUser(this.user.getEmail());

        // Registration success message
        System.out.println("\n                                                     Account Registered Successfully                                                         ");
        System.out.println("===============================================================================================================================================");
        System.out.println("                                                            Welcome " + user.getUsername());
    }
}
