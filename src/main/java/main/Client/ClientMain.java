package main.Client;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager("localhost", 12345);
        Scanner scanner = new Scanner(System.in);
        ConsoleManager consoleManager = new ConsoleManager(connectionManager);
        consoleManager.run();
        scanner.close();
    }
}
