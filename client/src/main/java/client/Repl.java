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
            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.printf("♕ Welcome to Chess! Please enter a number to choose an option:\n"
                    + "(1) Help\n" + "(2) Login\n" + "(3) Register\n" + "(4) Quit\n" + "> ");

            try {
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Get gud.");
                        break;
                    case 2:
                        System.out.printf("Enter your username.\n> ");
                        String username = scanner.next();
                        System.out.printf("Enter your password.\n> ");
                        String password = scanner.next();
                        try {
                            authToken = client.login(username, password);
                            System.out.println(authToken);
                        } catch (DataAccessException e) {
                            System.out.println(SET_TEXT_COLOR_RED + "Login failed: " + e.getMessage() + SET_TEXT_COLOR_WHITE);
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