package service;

import model.User;
import util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private Map<String, User> userStore = new HashMap<>();

    public void register(String username, String password, String role) {
        if (userStore.containsKey(username)) {
            System.out.println("User already exists!");
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(username, hashedPassword, role);
        userStore.put(username, user);

        System.out.println("User registered successfully.");
    }

    public boolean login(String username, String password) {
        if (!userStore.containsKey(username)) {
            System.out.println("User not found!");
            return false;
        }

        User user = userStore.get(username);
        String hashedInputPassword = PasswordUtil.hashPassword(password);

        if (user.getPasswordHash().equals(hashedInputPassword)) {
            System.out.println("Login successful. Role: " + user.getRole());
            return true;
        } else {
            System.out.println("Invalid password!");
            return false;
        }
    }
}
