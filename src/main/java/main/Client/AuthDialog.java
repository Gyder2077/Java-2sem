package main.Client;

import main.Utils.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * Диалог авторизации: только UI и отправка запроса.
 * Вся бизнес-логика — на сервере в UserManager.
 */
public class AuthDialog {
    private final ConnectionManager connectionManager;
    private final Scanner scanner;

    public AuthDialog(ConnectionManager connectionManager, Scanner scanner) {
        this.connectionManager = connectionManager;
        this.scanner = scanner;
    }

    /**
     * Запускает диалог авторизации.
     * @return true, если вход/регистрация успешны
     */
    public boolean authenticate() {
        while (true) {
            System.out.print("Do you have an account? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("y") || answer.isEmpty()) {
                if (tryAuth("Login")) return true;
            } else if (answer.equals("n")) {
                if (tryAuth("Registration")) return true;
            } else {
                System.out.println("Enter y or n");
            }
        }
    }

    private boolean tryAuth(String mode) {
        System.out.println("\n" + mode);
        System.out.print("Nickname: ");
        String login = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (login.isEmpty() || password.isEmpty()) {
            System.out.println("Cannot be empty");
            return false;
        }
        if (login.length() > 64 || password.length() > 128) {
            System.out.println("Too much data");
            return false;
        }

        String passwordHash = PasswordHasher.hash(password);
        Request request = new Request(login, passwordHash, null);

        try {
            Response response = connectionManager.sendRequest(request);
            if (response.success()) {
                System.out.println(response.message() + "\n");
                UserSession.getInstance().setCredentials(login, password);
                return true;
            }
            System.out.println(response.message());
            if (mode.equals("Registration")) {
                System.out.println("Try another account");
            }
            return false;
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            return false;
        }
    }
}