package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesToReturn = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        int[][] directions = {{1,0}, {0,-1}, {-1,0}, {0,1}};
        for (int[] direction : directions) {
            int[] peekPoint = {myPosition.getRow(), myPosition.getColumn()};
            while (true) {
                peekPoint[0] += direction[0];
                peekPoint[1] += direction[1];
                if (peekPoint[0] > 8 || peekPoint[1] > 8 || peekPoint[0] < 1 || peekPoint[1] < 1) {
                    break; // Don't go off the board
                }
                ChessPosition here = new ChessPosition(peekPoint[0], peekPoint[1]);
                if (board.getPiece(here) == null) {
                    movesToReturn.add(new ChessMove(myPosition, here, null));
                    // Continue
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
}