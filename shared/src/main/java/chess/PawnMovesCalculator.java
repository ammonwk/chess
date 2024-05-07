package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesToReturn = new ArrayList<>();
        int yDir = board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK ? -1 : 1;
        ChessPosition oneUp = new ChessPosition(myPosition.getRow() + 1 * yDir, myPosition.getColumn());
        movesToReturn.add(new ChessMove(myPosition, oneUp, null));
        if(myPosition.getRow() == 4.5 - 2.5 * yDir) {
            ChessPosition twoUp = new ChessPosition(myPosition.getRow() + 2 * yDir, myPosition.getColumn());
            movesToReturn.add(new ChessMove(myPosition, twoUp, null));
        }
        return movesToReturn;
    }
}