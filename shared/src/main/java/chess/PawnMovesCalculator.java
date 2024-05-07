package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesToReturn = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        int yDir = myColor == ChessGame.TeamColor.BLACK ? -1 : 1;
        ChessPosition oneUp = new ChessPosition(myPosition.getRow() + yDir + 1, myPosition.getColumn() + 1);
        if(board.getPiece(oneUp) == null) {
            movesToReturn.add(new ChessMove(myPosition, oneUp, null));
            if (myPosition.getRow() == 3.5 - 2.5 * yDir) {
                ChessPosition twoUp = new ChessPosition(myPosition.getRow() + 2 * yDir + 1, myPosition.getColumn() + 1);
                if (board.getPiece(twoUp) == null) {
                    movesToReturn.add(new ChessMove(myPosition, twoUp, null));
                }
            }
        }
        ChessPosition upLeft = new ChessPosition(myPosition.getRow() + yDir + 1, myPosition.getColumn());
        if(board.getPiece(upLeft) != null && board.getPiece(upLeft).getTeamColor() != myColor) {
            movesToReturn.add(new ChessMove(myPosition, upLeft, null));
        }
        ChessPosition upRight = new ChessPosition(myPosition.getRow() + yDir + 1, myPosition.getColumn() + 2);
        if(board.getPiece(upRight) != null && board.getPiece(upRight).getTeamColor() != myColor) {
            movesToReturn.add(new ChessMove(myPosition, upRight, null));
        }
        ArrayList<ChessMove> withPromotions = new ArrayList<>();
        for (ChessMove move : movesToReturn) { // Promotions
            if(myColor == ChessGame.TeamColor.WHITE && move.getEndPosition().getRow() == 7
            || myColor == ChessGame.TeamColor.BLACK && move.getEndPosition().getRow() == 0) {
                withPromotions.add(new ChessMove(myPosition, move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                withPromotions.add(new ChessMove(myPosition, move.getEndPosition(), ChessPiece.PieceType.QUEEN));
                withPromotions.add(new ChessMove(myPosition, move.getEndPosition(), ChessPiece.PieceType.BISHOP));
                withPromotions.add(new ChessMove(myPosition, move.getEndPosition(), ChessPiece.PieceType.ROOK));
            } else {
                withPromotions.add(move);
            }
        }
        return withPromotions;
    }
}