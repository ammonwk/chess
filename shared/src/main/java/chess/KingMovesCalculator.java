package chess;

import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    private static final int[][] MOVES = {
            {0, 1}, {1, 0}, {1, 1}, {-1, 0}, {-1, 1}, {-1, -1}, {0, -1}, {1, -1}
    };

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return MovesCalculatorUtils.calculateMovesToSquares(board, myPosition, MOVES);
    }
}
