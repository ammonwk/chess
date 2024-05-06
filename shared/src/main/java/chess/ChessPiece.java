package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;
    private PieceMovesCalculator movesCalculator;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.movesCalculator = createMovesCalculator(type);

    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    private PieceMovesCalculator createMovesCalculator(PieceType type) {
        switch (type) {
            case ROOK:
                return new RookMovesCalculator();
//            case "Bishop":
//                return new BishopMovesCalculator();
//            case "Knight":
//                return new KnightMovesCalculator();
//            case "Queen":
//                return new QueenMovesCalculator();
//            case "King":
//                return new KingMovesCalculator();
//            case "Pawn":
//                return new PawnMovesCalculator();
            default:
                throw new IllegalArgumentException("Invalid piece type: " + type);
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return movesCalculator.calculateMoves(board, myPosition);
    }

    @Override
    public String toString() {
        String returnValue;
        switch (this.type) {
            case KING:
                returnValue = "K";
                break;
            case QUEEN:
                returnValue = "Q";
                break;
            case BISHOP:
                returnValue = "B";
                break;
            case KNIGHT:
                returnValue = "N";
                break;
            case ROOK:
                returnValue = "R";
                break;
            case PAWN:
                returnValue = "P";
                break;
        default:
            throw new RuntimeException(this.type + " is not a valid piece type");
        }
        if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            returnValue = returnValue.toLowerCase();
        }
        return returnValue;
    }
}