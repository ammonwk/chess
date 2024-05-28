package chess;

import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    private static final int[][] DIRECTIONS = {
            {1, 0}, {0, -1}, {-1, 0}, {0, 1}
    };

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return MovesCalculatorUtils.calculateMovesInDirections(board, myPosition, DIRECTIONS, false);
    }
}
