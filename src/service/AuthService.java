package service;
import java.util.HashMap;
import java.util.Map;
import db.DatabaseConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private static final int MAX_ATTEMPTS = 3;
private Map<String, Integer> loginAttempts = new HashMap<>();

    // ================= REGISTER =================
    public void register(String username, String password, String role) {

        // -------- Input Validation --------
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty");
            return;
        }

        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters long");
            return;
        }

        if (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("User")) {
            System.out.println("Role must be Admin or User");
            return;
        }

        username = username.trim().toLowerCase(); // security normalization
        String hashedPassword = PasswordUtil.hashPassword(password);

        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
           stmt.setString(3, role.equalsIgnoreCase("Admin") ? "Admin" : "User");

            stmt.executeUpdate();
            System.out.println("User registered successfully.");

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("Username already exists.");
            } else {
                System.out.println("Registration failed.");
            }
        }
    }

    // ================= LOGIN =================
   
public void login(String username, String password) {

    if (username == null || password == null) {
        System.out.println("Invalid credentials");
        return;
    }

    username = username.trim().toLowerCase();

    // Check if user is temporarily locked
    int attempts = loginAttempts.getOrDefault(username, 0);
    if (attempts >= MAX_ATTEMPTS) {
        System.out.println("Account temporarily locked due to multiple failed attempts.");
        return;
    }

    String hashedInputPassword = PasswordUtil.hashPassword(password);
    String sql = "SELECT password_hash, role FROM users WHERE username = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            loginAttempts.put(username, attempts + 1);
            System.out.println("Invalid credentials");
            return;
        }

        String storedHash = rs.getString("password_hash");
        String role = rs.getString("role");

        if (storedHash.equals(hashedInputPassword)) {
            System.out.println("Login successful. Role: " + role);
            loginAttempts.remove(username); // reset on success
        } else {
            loginAttempts.put(username, attempts + 1);
            System.out.println("Invalid credentials");
        }

    } catch (SQLException e) {
        System.out.println("Login failed.");
    }
}

    }

