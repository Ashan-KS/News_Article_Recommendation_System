import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Extra {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:articles.db";
        String insertSQL = "INSERT INTO users (email, password, username, loginType) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url);

            try {
                PreparedStatement pstmt = conn.prepareStatement(insertSQL);

                try {
                    pstmt.setString(1, "banana");
                    pstmt.setString(2, "ashan");
                    pstmt.setString(3, "ashan");
                    pstmt.setString(4, "Admin");
                    pstmt.executeUpdate();
                } catch (Throwable var9) {
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                        } catch (Throwable var8) {
                            var9.addSuppressed(var8);
                        }
                    }

                    throw var9;
                }

                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Throwable var10) {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                    }
                }

                throw var10;
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException var11) {
            System.out.println("Error inserting user: " + var11.getMessage());
        }

    }
}
