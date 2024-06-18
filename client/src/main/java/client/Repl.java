package client;

import chess.*;
import client.websocket.NotificationHandler;
import dtos.DataAccessException;
import dtos.ListGamesResult;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;
    private String username;
    private String authToken;
    private int inGame;
    private ChessGame currentGame;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
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
                    handlePlayGame(scanner);
                    break;
                case 6:
                    handleObserveGame(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number between 1 and 4");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // clear the invalid input
        }
    }

    private void handleObserveGame(Scanner scanner) {
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
                client.observeGame(authToken, game.gameID());
                // inGame = game.gameID();
            }
        } catch (DataAccessException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Error in joining: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
        }
    }

    private void handlePlayGame(Scanner scanner) {
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
                    return;
                }
                client.joinGame(authToken, game.gameID(), joinColor);
                System.out.println("Successfully joined " + game.gameName() + " as " + joinColor);
                inGame = game.gameID();
            }
        } catch (DataAccessException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Error in joining: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
        }
    }

    private void inGameREPL(Scanner scanner) {
        String gameName = "";
        String color = "";
        ChessGame game = new ChessGame();
        DrawsBoard drawsBoard = new DrawsBoard();
        try {
            ListGamesResult allGames = client.listGames(authToken);
            for(ListGamesResult.GameSummary possibleGame : allGames.games()) {
                if(possibleGame.gameID() == inGame) {
                    gameName = possibleGame.gameName();
                    if(possibleGame.whiteUsername().equals(username)) {
                        color = "w";
                    } else if(possibleGame.blackUsername().equals(username)) {
                        color = "b";
                    } else {
                        System.out.println(color);
                        System.out.println(SET_TEXT_COLOR_RED + "Authentication error. Please log in again." + SET_TEXT_COLOR_WHITE);
                        inGame = 0;
                    }
                }
            }
            if(gameName.equals("") || color.equals("")) {
                System.out.println(SET_TEXT_COLOR_RED + "Authentication error. Please log in again." + SET_TEXT_COLOR_WHITE);
                inGame = 0;
            }
        } catch (DataAccessException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Authentication error. Please log in again." + SET_TEXT_COLOR_WHITE);
            inGame = 0;
        }

        try { // Wait for the ChessBoard to come back
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            // ignore it
        }
        System.out.print(SET_TEXT_COLOR_WHITE + username + ", you are playing as " + (color.equals("w") ? "white" : "black")
                + " in the game \"" + gameName + "\". Please enter a number to choose an option:\n"
                + "(1) Help\t\t\t(2) Redraw Chess Board\t\t\t(3) Leave\n(4) Make Move\t\t(5) Resign"
                + "\t\t(6) Highlight Legal Moves\n" + "> ");

        try {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Go ahead and choose an option, you'll be given more instructions at that point.");
                    break;
                case 2:
                    System.out.print("\n");
                    drawsBoard.draw(currentGame, color);
                    System.out.print("\n");
                    break;
                case 3:
                    try {
                        client.leaveGame(authToken, inGame);
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error in leaving: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
                    }
                    inGame = 0;
                    System.out.println("Leaving game...\nGoodbye.");
                    break;
                case 4:
                    Boolean validMove = false;
                    ChessMove parsedMove = null;
                    while (!validMove) {
                        System.out.println("Print your move. E.g. b2:b3\nIf you are promoting a pawn, say f7:f8:q, for example, for a queen.");
                        String move = scanner.next();
                        try {
                            ChessPosition startingPosition = new ChessPosition(move.charAt(1) - '0', move.charAt(0) - 'a' + 1);
                            ChessPosition endingPosition = new ChessPosition(move.charAt(4) - '0', move.charAt(3) - 'a' + 1);
                            ChessPiece.PieceType promotion = null;
                            if(move.length() > 6) {
                                switch (Character.toLowerCase(move.charAt(6))) {
                                    case 'r' -> promotion = ChessPiece.PieceType.ROOK;
                                    case 'n' -> promotion = ChessPiece.PieceType.KNIGHT;
                                    case 'b' -> promotion = ChessPiece.PieceType.BISHOP;
                                    case 'q' -> promotion = ChessPiece.PieceType.QUEEN;
                                    default -> promotion = null;
                                }
                            }
                            parsedMove = new ChessMove(startingPosition, endingPosition, promotion);
                            validMove = true;
                        } catch (Exception e) {
                            System.out.println(SET_TEXT_COLOR_RED + "I couldn't parse that. Please follow the format g1:f3, etc." + SET_TEXT_COLOR_WHITE);
                        }
                    }
                    try {
                        client.makeMove(authToken, inGame, parsedMove);
                    } catch (DataAccessException e) {
                        System.out.println(SET_TEXT_COLOR_RED + "Error in move: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
                    }

                    break;
                default:
                    System.out.println("Invalid input. Please enter a number.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
    }

    @Override
    public void drawBoard(ChessGame game) {
        String color = "";
        currentGame = game;
        try {
            ListGamesResult allGames = client.listGames(authToken);
            for(ListGamesResult.GameSummary possibleGame : allGames.games()) {
                if(possibleGame.gameID() == inGame) {
                    if(possibleGame.whiteUsername().equals(username)) {
                        color = "w";
                    } else if(possibleGame.blackUsername().equals(username)) {
                        color = "b";
                    }
                }
            }
        } catch (DataAccessException e) {
            System.out.println(SET_TEXT_COLOR_RED + "Authentication error. Please log in again." + SET_TEXT_COLOR_WHITE);
            inGame = 0;
        }
        System.out.print("\n");
        DrawsBoard drawsBoard = new DrawsBoard();
        drawsBoard.draw(game, color);
    }

    public void notify(NotificationMessage notification) {
        System.out.println("\n" + SET_TEXT_COLOR_WHITE + notification);
    }

    public void error(ErrorMessage errorMessage) {
        System.out.println("\n" + SET_TEXT_COLOR_RED + errorMessage + SET_TEXT_COLOR_WHITE);
    }
}