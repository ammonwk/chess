package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesToReturn = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        int[][] directions = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}};
        for (int[] direction : directions) {
            if(myPosition.getRow() + direction[0] > 7
                    || myPosition.getRow() + direction[0] < 0
                    || myPosition.getColumn() + direction[1] > 7
                    || myPosition.getColumn() + direction[1] < 0) {
                continue;
            }
            ChessPosition peekPoint = new ChessPosition(myPosition.getRow() + direction[0] + 1,
                    myPosition.getColumn() + direction[1] + 1);
            if(board.getPiece(peekPoint) == null || board.getPiece(peekPoint).getTeamColor() != myColor) {
                movesToReturn.add(new ChessMove(myPosition, peekPoint, null));
            }
        }
        return movesToReturn;
    }
}