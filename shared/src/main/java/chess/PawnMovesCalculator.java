package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        System.out.println(board.toString());
        return new ArrayList<>();
    }
}