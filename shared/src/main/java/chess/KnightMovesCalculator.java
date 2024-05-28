package chess;

import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    private static final int[][] MOVES = {
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
    };

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return MovesCalculatorUtils.calculateMovesToSquares(board, myPosition, MOVES);
    }
}
