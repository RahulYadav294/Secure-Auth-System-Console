package service;

import db.DatabaseConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

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

        username = username.toLowerCase(); // security normalization
        String hashedPassword = PasswordUtil.hashPassword(password);

        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, role);

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

        username = username.toLowerCase();
        String hashedInputPassword = PasswordUtil.hashPassword(password);

        String sql = "SELECT password_hash, role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Invalid credentials");
                return;
            }

            String storedHash = rs.getString("password_hash");
            String role = rs.getString("role");

            if (storedHash.equals(hashedInputPassword)) {
                System.out.println("Login successful. Role: " + role);
            } else {
                System.out.println("Invalid credentials");
            }

        } catch (SQLException e) {
            System.out.println("Login failed.");
        }
    }
}
