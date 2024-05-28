package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;
    private PieceMovesCalculator movesCalculator;
    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.movesCalculator = createMovesCalculator(type);
        this.hasMoved = false;
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
            case BISHOP:
                return new BishopMovesCalculator();
            case KNIGHT:
                return new KnightMovesCalculator();
            case QUEEN:
                return new QueenMovesCalculator();
            case KING:
                return new KingMovesCalculator();
            case PAWN:
                return new PawnMovesCalculator();
            default:
                throw new IllegalArgumentException("Error: Invalid piece type: " + type);
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
            throw new RuntimeException("Error: " + this.type + " is not a valid piece type");
        }
        if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            returnValue = returnValue.toLowerCase();
        }
        return returnValue;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, movesCalculator);
    }

    @Override
    protected ChessPiece clone() {
        try {
            ChessPiece clonedPiece = (ChessPiece) super.clone();
            clonedPiece.movesCalculator = createMovesCalculator(clonedPiece.type);
            return clonedPiece;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}