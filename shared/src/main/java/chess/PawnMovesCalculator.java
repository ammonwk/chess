package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesToReturn = new ArrayList<>();
        int yDir = board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK ? -1 : 1;

    }
}