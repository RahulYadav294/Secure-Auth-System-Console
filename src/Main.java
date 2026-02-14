import service.AuthService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        AuthService authService = new AuthService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n==== Secure Auth System ====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();

                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    System.out.print("Enter role (Admin/User): ");
                    String role = scanner.nextLine();

                    authService.register(username, password, role);
                    break;

                case 2:
                    System.out.print("Enter username: ");
                    String loginUser = scanner.nextLine();

                    System.out.print("Enter password: ");
                    String loginPass = scanner.nextLine();

                    authService.login(loginUser, loginPass);
                    break;

                case 3:
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
