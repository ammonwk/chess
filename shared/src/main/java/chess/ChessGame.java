package chess;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;
    private ChessMove lastMove;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        lastMove = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        // Method to get the opposite turn
        public TeamColor not() {
            return this == BLACK ? WHITE : BLACK;
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        // Add castling moves for the king
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.hasMoved()) {
            addCastlingMoves(validMoves, startPosition, piece);
        }
        // Add en passant for a pawn
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            addEnPassant(validMoves, startPosition, piece);
        }
        for (ChessMove move : moves) {
            if (board.getPiece(move.getStartPosition()) == null) {
                continue;
            } //else if (board.getPiece(move.getStartPosition()).getTeamColor() != turn) {
            //continue;
            //}

            // Create a new ChessGame instance with a cloned board
            ChessGame testGame = new ChessGame();
            testGame.setBoard(board.clone());
            if (piece.getTeamColor() == TeamColor.BLACK) {
                testGame.setTeamTurn(TeamColor.WHITE);
            } else {
                testGame.setTeamTurn(TeamColor.BLACK);
            }


            // Apply the move on the cloned board directly
            testGame.getBoard().addPiece(move.getStartPosition(), null);
            testGame.getBoard().addPiece(move.getEndPosition(), piece);

            if (testGame.getTeamTurn() == TeamColor.BLACK) {
                testGame.setTeamTurn(TeamColor.WHITE);
            } else if (testGame.getTeamTurn() == TeamColor.WHITE) {
                testGame.setTeamTurn(TeamColor.BLACK);
            }

            // Check if the player is still in check after making the move
            if (testGame.isInCheck(testGame.getTeamTurn())) {
                continue;
            }
            validMoves.add(move);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!validMoves(move.getStartPosition()).contains(move)
                || board.getPiece(move.getStartPosition()).getTeamColor() != turn) {
            throw new InvalidMoveException();
        }

        ChessPiece piece;
        if (move.getPromotionPiece() == null) {
            piece = board.getPiece(move.getStartPosition());
        } else {
            piece = new ChessPiece(turn, move.getPromotionPiece());
        }

        // Handle castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int startRow = move.getStartPosition().getRow() + 1;
            int startCol = move.getStartPosition().getColumn() + 1;
            int endCol = move.getEndPosition().getColumn() + 1;

            // King side castling
            if (startCol == 5 && endCol == 7) {
                ChessPosition rookStartPos = new ChessPosition(startRow, 8);
                ChessPosition rookEndPos = new ChessPosition(startRow, 6);
                ChessPiece rook = board.getPiece(rookStartPos);
                if (rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK && !rook.hasMoved()) {
                    board.addPiece(rookStartPos, null);
                    board.addPiece(rookEndPos, rook);
                    rook.setMoved(true);
                }
            }

            // Queen side castling
            if (startCol == 5 && endCol == 3) {
                ChessPosition rookStartPos = new ChessPosition(startRow, 1);
                ChessPosition rookEndPos = new ChessPosition(startRow, 4);
                ChessPiece rook = board.getPiece(rookStartPos);
                if (rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK && !rook.hasMoved()) {
                    board.addPiece(rookStartPos, null);
                    board.addPiece(rookEndPos, rook);
                    rook.setMoved(true);
                }
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int startRow = move.getStartPosition().getRow() + 1;
            int endRow = move.getEndPosition().getRow() + 1;
            int startCol = move.getStartPosition().getColumn() + 1;
            int endCol = move.getEndPosition().getColumn() + 1;

            // If En Passant move
            if (Math.abs(startRow - endRow) == 1 && Math.abs(startCol - endCol) == 1 && board.getPiece(move.getEndPosition()) == null) {
                int capturedPawnRow = piece.getTeamColor() == TeamColor.WHITE ? endRow - 1 : endRow + 1;
                ChessPosition capturedPawnPos = new ChessPosition(capturedPawnRow, endCol);
                board.addPiece(capturedPawnPos, null); // Remove the captured pawn
            }
        }

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);

        // Track the move
        piece.setMoved(true);
        lastMove = move;
        turn = turn.not();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Find king
        ChessPosition kingAt = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    kingAt = position;
                }
            }
        }
        if (kingAt == null) {
            return false; // There should always be a king, but no king can't be in check
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    if (moves.contains(new ChessMove(position, kingAt, null))) {
                        return true;
                    } else if (moves.contains(new ChessMove(position, kingAt, ChessPiece.PieceType.QUEEN))) {
                        return true; // Special case for pawns pinning the king against the wall
                    }
                }
            }
        }
        return false;
    }


    private boolean hasValidMoves(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (board.getPiece(new ChessPosition(i, j)) != null
                        && board.getPiece(new ChessPosition(i, j)).getTeamColor() != teamColor) {
                    return false;
                }
                if (validMoves(new ChessPosition(i, j)).size() > 0) {
                    Collection<ChessMove> moves = validMoves(new ChessPosition(i, j));
                    System.out.println(moves);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return !hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        if (turn != teamColor) {
            return false;
        }

        return !hasValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                this.board.addPiece(position, piece);
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private void addCastlingMoves(Collection<ChessMove> validMoves, ChessPosition kingPosition, ChessPiece king) {
        if (king.getPieceType() != ChessPiece.PieceType.KING || king.hasMoved()) {
            return;
        }

        int row = kingPosition.getRow();
        ChessPosition leftRookPos = new ChessPosition(row + 1, 1);
        ChessPosition rightRookPos = new ChessPosition(row + 1, 8);
        ChessPiece leftRook = board.getPiece(leftRookPos);
        ChessPiece rightRook = board.getPiece(rightRookPos);

        if (leftRook != null && leftRook.getPieceType() == ChessPiece.PieceType.ROOK && !leftRook.hasMoved()) {
            if (isPathClearForCastling(kingPosition, leftRookPos) && !isPathUnderAttack(kingPosition, leftRookPos)) {
                validMoves.add(new ChessMove(kingPosition, new ChessPosition(row + 1, 3), null)); // Castling left
            }
        }

        if (rightRook != null && rightRook.getPieceType() == ChessPiece.PieceType.ROOK && !rightRook.hasMoved()) {
            if (isPathClearForCastling(kingPosition, rightRookPos) && !isPathUnderAttack(kingPosition, rightRookPos)) {
                validMoves.add(new ChessMove(kingPosition, new ChessPosition(row + 1, 7), null)); // Castling right
            }
        }
    }

    private boolean isPathClearForCastling(ChessPosition kingPosition, ChessPosition rookPosition) {
        int row = kingPosition.getRow() + 1;
        int colStart = Math.min(kingPosition.getColumn(), rookPosition.getColumn()) + 2;
        int colEnd = Math.max(kingPosition.getColumn(), rookPosition.getColumn());

        for (int col = colStart; col <= colEnd; col++) {
            if (board.getPiece(new ChessPosition(row, col)) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isPathUnderAttack(ChessPosition kingPosition, ChessPosition rookPosition) {
        int row = kingPosition.getRow() + 1;
        int colStart = Math.min(kingPosition.getColumn(), rookPosition.getColumn()) + 2;
        int colEnd = Math.max(kingPosition.getColumn(), rookPosition.getColumn());
        TeamColor tempTurn = turn;
        if (board.getPiece(kingPosition) != null) {
            tempTurn = board.getPiece(kingPosition).getTeamColor();
        } // Act as if it were the king's turn

        for (int col = colStart; col <= colEnd; col++) {
            ChessPosition pos = new ChessPosition(row, col);
            ChessGame testGame = new ChessGame();
            testGame.setBoard(board.clone());
            testGame.getBoard().addPiece(kingPosition, null);
            testGame.getBoard().addPiece(pos, new ChessPiece(tempTurn, ChessPiece.PieceType.KING));
            if (testGame.isInCheck(tempTurn)) {
                return true;
            }
        }
        return false;
    }

    private void addEnPassant(Collection<ChessMove> validMoves, ChessPosition pawnPosition, ChessPiece pawn) {
        if (pawn.getPieceType() != ChessPiece.PieceType.PAWN || lastMove == null) {
            return;
        }

        int direction = pawn.getTeamColor() == TeamColor.WHITE ? 1 : -1;
        int row = pawnPosition.getRow() + 1;
        int col = pawnPosition.getColumn() + 1;

        // Check the left side for en passant
        if (col > 0) {
            ChessPosition leftPos = new ChessPosition(row, col - 1);
            ChessPiece leftPiece = board.getPiece(leftPos);
            if (leftPiece != null && leftPiece.getPieceType() == ChessPiece.PieceType.PAWN
                    && leftPiece.getTeamColor() != pawn.getTeamColor() && leftPiece == board.getPiece(lastMove.getEndPosition())
                    && lastMove.getEndPosition().equals(leftPos)
                    && Math.abs(lastMove.getEndPosition().getRow() - lastMove.getStartPosition().getRow()) == 2) {
                ChessPosition enPassantCapture = new ChessPosition(row + direction, col - 1);
                validMoves.add(new ChessMove(pawnPosition, enPassantCapture, null));
            }
        }

        // Check the right side for en passant
        if (col < 7) {
            ChessPosition rightPos = new ChessPosition(row, col + 1);
            ChessPiece rightPiece = board.getPiece(rightPos);
            if (rightPiece != null && rightPiece.getPieceType() == ChessPiece.PieceType.PAWN
                    && rightPiece.getTeamColor() != pawn.getTeamColor() && rightPiece == board.getPiece(lastMove.getEndPosition())
                    && lastMove.getEndPosition().equals(rightPos)
                    && Math.abs(lastMove.getEndPosition().getRow() - lastMove.getStartPosition().getRow()) == 2) {
                ChessPosition enPassantCapture = new ChessPosition(row + direction, col + 1);
                validMoves.add(new ChessMove(pawnPosition, enPassantCapture, null));
            }
        }
    }
}
