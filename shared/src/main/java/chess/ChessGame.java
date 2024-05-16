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

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
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
        BLACK
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
        for(ChessMove move : moves) {
            if (board.getPiece(move.getStartPosition()) == null) {
                continue;
            } //else if (board.getPiece(move.getStartPosition()).getTeamColor() != turn) {
                //continue;
            //}

            // Create a new ChessGame instance with a cloned board
            ChessGame testGame = new ChessGame();
            testGame.setBoard(board.clone());
            if(piece.getTeamColor() == TeamColor.BLACK) {
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
        if(!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece piece;
        if (move.getPromotionPiece() == null) {
            piece = board.getPiece(move.getStartPosition());
        } else {
            piece = new ChessPiece(turn, move.getPromotionPiece());
        }

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);

        if (turn == TeamColor.BLACK) {
            turn = TeamColor.WHITE;
        } else {
            turn = TeamColor.BLACK;
        }
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
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    kingAt = position;
                }
            }
        }
        if(kingAt == null) {
            return false; // There should always be a king, but no king can't be in check
        }
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    if (moves.contains(new ChessMove(position, kingAt, null))) {
                        return true;
                    }else if (moves.contains(new ChessMove(position, kingAt, ChessPiece.PieceType.QUEEN))) {
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
                if(validMoves(new ChessPosition(i, j)).size() > 0) {
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

        if(turn != teamColor) {
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
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
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
}
