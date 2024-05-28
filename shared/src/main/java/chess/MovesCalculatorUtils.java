package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MovesCalculatorUtils {

    public static Collection<ChessMove> calculateMovesInDirections(ChessBoard board, ChessPosition myPosition, int[][] directions, boolean singleStep) {
        ArrayList<ChessMove> movesToReturn = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        for (int[] direction : directions) {
            int[] peekPoint = {myPosition.getRow(), myPosition.getColumn()};
            while (true) {
                peekPoint[0] += direction[0];
                peekPoint[1] += direction[1];
                if (peekPoint[0] > 7 || peekPoint[1] > 7 || peekPoint[0] < 0 || peekPoint[1] < 0) {
                    break; // Don't go off the board
                }
                ChessPosition here = new ChessPosition(peekPoint[0] + 1, peekPoint[1] + 1);
                if (board.getPiece(here) == null) {
                    movesToReturn.add(new ChessMove(myPosition, here, null));
                    if (singleStep) {
                        break; // Only one step for King and Knight
                    }
                } else if (board.getPiece(here).getTeamColor() != myColor) {
                    movesToReturn.add(new ChessMove(myPosition, here, null));
                    break; // Don't move past enemy pieces after capturing
                } else { // A team member piece
                    break;
                }
            }
        }
        return movesToReturn;
    }

    public static Collection<ChessMove> calculateMovesToSquares(ChessBoard board, ChessPosition myPosition, int[][] moves) {
        ArrayList<ChessMove> movesToReturn = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        for (int[] move : moves) {
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];
            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                ChessPosition newPos = new ChessPosition(newRow + 1, newCol + 1);
                if (board.getPiece(newPos) == null || board.getPiece(newPos).getTeamColor() != myColor) {
                    movesToReturn.add(new ChessMove(myPosition, newPos, null));
                }
            }
        }
        return movesToReturn;
    }
}
