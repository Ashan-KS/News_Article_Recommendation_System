package Account;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String loginType;

    // Constructor to make an user object
    public User(String username, String email, String password, String loginType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.loginType = loginType;
    }

    public String getEmail() {
        return this.email;
    }

    public String getLoginType() {
        return this.loginType;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}