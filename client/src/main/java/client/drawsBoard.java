package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class drawsBoard {

    public drawsBoard() {

    }

    public void draw(ChessGame game, String player) {
        drawHeader(player);
        drawPieces(player, game.getBoard());
        drawHeader(player);
    }

    public void drawHeader(String player) {
        String[] squares = {" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        if (player.equals("b")) { // Reverse the header
            String[] reversed = new String[squares.length];
            for (int i = 0; i < squares.length; i++) {
                reversed[i] = squares[squares.length - 1 - i];
            }
            squares = reversed;
        }
        for (String square : squares) {
            drawSquare(square, SET_TEXT_COLOR_DARKER_GRAY, SET_BG_COLOR_MEDIUM_LIGHT_BROWN);
        }
        System.out.print("\n");
    }

    public void drawPieces(String player, ChessBoard board) {
        for (int row = 1; row < 9; row++) {
            drawSquare(String.valueOf(row), SET_TEXT_COLOR_DARKER_GRAY, SET_BG_COLOR_MEDIUM_LIGHT_BROWN);
            for (int col = 1; col < 9; col++) {
                ChessPiece piece;
                if(player == "w") {
                    piece = board.getPiece(new ChessPosition(9 - row, col));
                } else {
                    piece = board.getPiece(new ChessPosition(row, 9 - col));
                }
                String print = piece == null ? " " : piece.toString();

                String backgroundColor = Math.floorMod(row + col, 2) == 0 ? SET_BG_COLOR_MEDIUM_BROWN : SET_BG_COLOR_DARK_BROWN;
                String textColor = SET_TEXT_COLOR_DARKER_GRAY;
                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        textColor = SET_TEXT_COLOR_OFF_WHITE;
                    } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        textColor = SET_TEXT_COLOR_BLACK;
                    }
                }
                drawSquare(print, textColor, backgroundColor);
            }
            drawSquare(String.valueOf(row), SET_TEXT_COLOR_DARKER_GRAY, SET_BG_COLOR_MEDIUM_LIGHT_BROWN);
            System.out.print("\n");
        }
    }

    public void drawSquare(String content, String color, String background) {
        System.out.print(background + color + " " + content + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }
}
