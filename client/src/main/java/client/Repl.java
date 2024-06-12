package client;

import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.DataAccessException;
import service.ListGamesResult;

import java.util.InputMismatchException;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;
    private String username;
    private String authToken;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
        username = null;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            switch (username) {
                case null:
                    exit = beforeLoginREPL(scanner);
                    break;
                default:
                    exit = afterLoginREPL(scanner);
            }
        }
        scanner.close();
    }

    private boolean beforeLoginREPL(Scanner scanner) {
        System.out.printf(SET_TEXT_COLOR_WHITE + "♕ Welcome to Chess! Please enter a number to choose an option:\n"
                + "(1) Help\t\t\t(2) Login\n(3) Register\t\t(4) Quit\n" + "> ");

        try {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("If don't have an account yet, start by registering. If you're forgotten your password, that's rough buddy.");
                    break;
                case 2:
                    System.out.printf("Enter your username.\n> ");
                    String username = scanner.next();
                    System.out.printf("Enter your password.\n> ");
                    String password = scanner.next();
                    try {
                        authToken = client.login(username, password);
                        System.out.println("Welcome, " + username + "!");
                        this.username = username;
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
                        System.out.println("Welcome, " + username + "!");
                        this.username = username;
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Registration failed: That username is taken." + SET_TEXT_COLOR_WHITE);
                    }
                    break;
                case 4:
                    System.out.println("Thanks for playing! Goodbye.");
                    return true;
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 4");
            }
            return false;
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // clear the invalid input
            return false;
        }
    }

    private boolean afterLoginREPL(Scanner scanner) {
        System.out.printf(SET_TEXT_COLOR_WHITE + "♕ Welcome, " + username + ", to Chess! Please enter a number to choose an option:\n"
                + "(1) Help\t\t\t(2) Logout\t\t\t(3) List Games\n(4) Create Game\t\t(5) Play Game\t\t(6) Observe Game\n" + "> ");

        try {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Choose 3 to see what games are in action, or 4 to get one started!");
                    break;
                case 2:
                    try {
                        client.logout(authToken);
                        System.out.println("Logged out successfully.");
                        this.username = null;
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error in logging out: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
                    }
                    break;
                case 3:
                    try {
                        ListGamesResult games = client.listGames(authToken);
                        if(games.games().size() == 0) {
                            System.out.println("There are currently no active games.");
                        } else {
                            int i = 0;
                            System.out.println("The following games were found:");
                            for (ListGamesResult.GameSummary game : games.games()) {
                                i = i + 1;
                                System.out.println(Integer.toString(i) + ") " + game.gameName()
                                        + " (White user: " + game.whiteUsername() + ", Black user: " + game.blackUsername() + ")");
                            }
                        }
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error in listing games: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 4");
            }
            return false;
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // clear the invalid input
            return false;
        }
    }

}