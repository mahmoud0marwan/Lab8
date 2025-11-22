import com.google.gson.annotations.Expose;

public abstract class User {
    @Expose
    protected String userId;
    @Expose
    protected String username;
    @Expose
    protected String email;
    @Expose
    protected String passwordHash;
    @Expose
    protected String role;

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean authenticate(String password) {
        String passwordH = AuthManager.hashPassword(password);
        return this.passwordHash.equals(passwordH);
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}