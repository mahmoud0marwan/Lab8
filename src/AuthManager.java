import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AuthManager {

    private  List<User> users;
    private JsonDatabaseManager j;

    public AuthManager(JsonDatabaseManager j) {
        this.j = j;
        this.users = j.loadUsers();
    }

    public boolean signup(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (!validateEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                return false;
            }
        }

        String newId;
        do {
            newId = IdGenerator.generateUserId();
        } while (!j.isUserIdUnique(newId));

        user.setUserId(newId);
        users.add(user);
        j.saveUsers(users);
        return true;
    }

    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                if (verifyPassword(password, u.getPasswordHash())) {
                    return u;
                } else {
                    throw new IllegalArgumentException("Wrong email or password");
                }
            }
        }
        throw new IllegalArgumentException("Wrong email or password");
    }

    void logout()
    {

    }

    boolean validateEmail(String email)
    {
        if(email.contains("@")&&email.contains("."))
            return true;
        else
            return false;
    }
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static boolean verifyPassword(String password, String expectedHexHash) {
        if (expectedHexHash == null) return false;
        String hashed = hashPassword(password);
        return hashed.equals(expectedHexHash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}
