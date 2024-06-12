package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import service.ListGamesResult;

import java.util.InputMismatchException;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;
    private String username;
    private String authToken;
    private int inGame;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
        username = null;
        inGame = 0;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            if (username == null) {
                exit = beforeLoginREPL(scanner);
            } else {
                if (inGame == 0) {
                    afterLoginREPL(scanner);
                } else {
                    inGameREPL(scanner);
                }
            }
        }
        scanner.close();
    }

    private boolean beforeLoginREPL(Scanner scanner) {
        System.out.print(SET_TEXT_COLOR_WHITE + "♕ Welcome to Chess! Please enter a number to choose an option:\n"
                + "(1) Help\t\t\t(2) Login\n(3) Register\t\t(4) Quit\n" + "> ");

        try {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("If don't have an account yet, start by registering. If you're forgotten your password, that's rough buddy.");
                    break;
                case 2:
                    System.out.print("Enter your username.\n> ");
                    String enteredUsername = scanner.next();
                    System.out.print("Enter your password.\n> ");
                    String enteredPassword = scanner.next();
                    try {
                        authToken = client.login(enteredUsername, enteredPassword);
                        System.out.println("Welcome, " + enteredUsername + "!");
                        username = enteredUsername;
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error: Incorrect username or password." + SET_TEXT_COLOR_WHITE);
                    }
                    break;
                case 3:
                    System.out.print("Choose a username.\n> ");
                    String newUsername = scanner.next();
                    System.out.print("Choose a password.\n> ");
                    String newPassword = scanner.next();
                    System.out.print("Enter your email.\n> ");
                    String email = scanner.next();
                    try {
                        authToken = client.register(newUsername, newPassword, email);
                        System.out.println("Welcome, " + newUsername + "!");
                        username = newUsername;
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

    private void afterLoginREPL(Scanner scanner) {
        System.out.print(SET_TEXT_COLOR_WHITE + "♕ Welcome, " + username + ", to Chess! Please enter a number to choose an option:\n"
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
                        if(games.games().isEmpty()) {
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
                case 4:
                    System.out.print("What will the new game be called?\nName of game: ");
                    String gameName = scanner.next();
                    try {
                        client.createGame(authToken, gameName);
                        System.out.println(gameName + " was created successfully.");
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error in creating game: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
                    }
                    break;
                case 5:
                    System.out.println("Of the following games,");
                    try {
                        ListGamesResult games = client.listGames(authToken);
                        if(games.games().isEmpty()) {
                            System.out.println("There are currently no active games. Create a game before trying to join one.");
                        } else {
                            int i = 0;
                            for (ListGamesResult.GameSummary game : games.games()) {
                                i = i + 1;
                                System.out.println(Integer.toString(i) + ") " + game.gameName()
                                        + " (White user: " + game.whiteUsername() + ", Black user: " + game.blackUsername() + ")");
                            }
                            System.out.print("Write a number for which game you would like to join:\n> ");
                            //int toJoin = scanner.nextInt();
                            ListGamesResult.GameSummary game = games.games().get(scanner.nextInt() - 1);
                            String joinColor;
                            if(game.whiteUsername() == null && game.blackUsername() == null) {
                                System.out.print("Enter 1 to join as white, or 2 to join as black:\n> ");
                                joinColor = scanner.nextInt() == 1 ? "WHITE" : "BLACK";
                            } else if (game.whiteUsername() == null) {
                                joinColor = "WHITE";
                            } else if (game.blackUsername() == null) {
                                joinColor = "BLACK";
                            } else {
                                System.out.println("That game is full. White is played by " + game.whiteUsername()
                                        + ", and Black is played by " + game.blackUsername());
                                break;
                            }
                            client.joinGame(authToken, game.gameID(), joinColor);
                            System.out.println("Successfully joined " + game.gameName() + " as " + joinColor);
                            inGame = game.gameID();
                        }
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error in joining: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
                    }
                    break;
                case 6:
                    System.out.println("Of the following games,");
                    try {
                        ListGamesResult games = client.listGames(authToken);
                        if(games.games().isEmpty()) {
                            System.out.println("There are currently no active games. Create a game before trying to join one.");
                        } else {
                            int i = 0;
                            for (ListGamesResult.GameSummary game : games.games()) {
                                i = i + 1;
                                System.out.println(Integer.toString(i) + ") " + game.gameName()
                                        + " (White user: " + game.whiteUsername() + ", Black user: " + game.blackUsername() + ")");
                            }
                            System.out.print("Write a number for which game you would like to observe:\n> ");
                            //int toJoin = scanner.nextInt();
                            ListGamesResult.GameSummary game = games.games().get(scanner.nextInt() - 1);
                            System.out.println("Observing " + game.gameName() + " as WHITE");
                            inGame = game.gameID();
                        }
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error in joining: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 4");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // clear the invalid input
        }
    }

    private void inGameREPL(Scanner scanner) {
        System.out.println("Drawing initial state of chess game...");
        ChessGame game = new ChessGame();
        drawsBoard board1 = new drawsBoard();
        drawsBoard board2 = new drawsBoard();
        board1.draw(game, "w");
        System.out.print("\n");
        board2.draw(game, "b");
        inGame = 0;
    }
}