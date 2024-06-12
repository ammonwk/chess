package client;

import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.DataAccessException;

import java.util.InputMismatchException;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String authToken = null;

        while (!exit) {
            System.out.printf(SET_TEXT_COLOR_WHITE + "♕ Welcome to Chess! Please enter a number to choose an option:\n"
                    + "(1) Help\t\t\t(2) Login\n(3) Register\t\t(4) Quit\n" + "> ");

            try {
                int choice = scanner.nextInt();
                String username;
                String password;

                switch (choice) {
                    case 1:
                        System.out.println("If don't have an account yet, start by registering. If you're forgotten your password, that's rough buddy.");
                        break;
                    case 2:
                        System.out.printf("Enter your username.\n> ");
                        username = scanner.next();
                        System.out.printf("Enter your password.\n> ");
                        password = scanner.next();
                        try {
                            authToken = client.login(username, password);
                            System.out.println("Welcome, " + username + "!");
                            // System.out.println(authToken);
                        } catch (DataAccessException e) {
                            System.out.println(SET_TEXT_COLOR_RED + "Error: Incorrect username or password." + SET_TEXT_COLOR_WHITE);
                        }
                        break;
                    case 3:
                        System.out.printf("Choose a username.\n> ");
                        username = scanner.next();
                        System.out.printf("Choose a password.\n> ");
                        password = scanner.next();
                        System.out.printf("Enter your email.\n> ");
                        String email = scanner.next();
                        try {
                            authToken = client.register(username, password, email);
                            System.out.println(authToken);
                        } catch (DataAccessException e) {
                            System.out.println(SET_TEXT_COLOR_RED + "Registration failed: That username is taken." + SET_TEXT_COLOR_WHITE);
                        }
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Thanks for playing! Goodbye.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a number between 1 and 4");

                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // clear the invalid input
            }
        }
        scanner.close();
    }

}