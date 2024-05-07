package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] pieces;

    public ChessBoard() {
        this.pieces = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.pieces[position.getColumn()][position.getRow()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.pieces[position.getColumn()][position.getRow()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
            this.pieces = new ChessPiece[8][8];

            // Place pawns
            for (int col = 0; col < 8; col++) {
                this.pieces[col][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
                this.pieces[col][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            }

            // Place rooks
            this.pieces[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            this.pieces[7][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            this.pieces[0][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
            this.pieces[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

            // Place knights
            this.pieces[1][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            this.pieces[6][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            this.pieces[1][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
            this.pieces[6][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);

            // Place bishops
            this.pieces[2][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
            this.pieces[5][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
            this.pieces[2][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
            this.pieces[5][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

            // Place queens
            this.pieces[3][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
            this.pieces[3][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);

            // Place kings
            this.pieces[4][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
            this.pieces[4][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.pieces);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard other = (ChessBoard) obj;
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                if (this.pieces[col][row] == null) {
                    if (other.pieces[col][row] != null) {
                        return false;
                    }
                } else if (!this.pieces[col][row].equals(other.pieces[col][row])) {
                    return false;
                }
            }
        }
        return true;
    }
}
