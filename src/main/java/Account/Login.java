package Account;

import Database.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Login {
    private User user;
    private Database database = new Database();

    // Getter method to retrieve the authenticated user object
    public User getUser() {
        return this.user;
    }

    // Validates the format of an email using a regex pattern
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    // Constructor for handling the login process
    public Login() {
        System.out.println("\n===============================================================================================================================================");
        System.out.println("                                                           Login to Existing Account                                                          ");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\n     - Enter email: ");
            String email = scanner.nextLine().strip();

            // Check if the provided email is valid
            if (!isValidEmail(email)) {
                System.out.println("       Invalid email format. Please enter a valid email.");
                continue;
            }

            this.user = database.searchUser(email);

            if (user != null){
                break;
            }
        }

        String loginType;

        while (true) {
            System.out.print("     - Enter password: ");
            loginType = scanner.nextLine().strip();

            // Check if the entered password matches the user's password
            if (loginType.equals(this.user.getPassword())) {
                while (true) {
                    System.out.print("     - Select login type (User/Admin): ");
                    loginType = scanner.nextLine().strip();

                    // Validate login type input
                    if (!loginType.equalsIgnoreCase("User") && !loginType.equalsIgnoreCase("Admin")) {
                        System.out.println("       Invalid login type. Please enter either 'User' or 'Admin'.\n");
                    }
                    // Ensure admin access is allowed only for admin accounts
                    else if (!loginType.equalsIgnoreCase("Admin") || !"User".equalsIgnoreCase(this.user.getLoginType())) {
                        this.user.setLoginType(loginType);
                        break;
                    } else {
                        System.out.println("       Access denied. Admin privileges are unavailable. Please log in as a user.\n");
                    }
                }

                // Successful login message
                System.out.println("\n===============================================================================================================================================");
                System.out.println("                                                            Welcome back " + this.user.getUsername());
                return;
            }

            // Handle incorrect password input
            System.out.println("       Password Incorrect, Try again\n");
        }
    }
}
