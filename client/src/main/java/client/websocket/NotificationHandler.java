package client.websocket;

import chess.ChessGame;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void error(ErrorMessage error);
    void notify(NotificationMessage notification);
    void drawBoard(ChessGame game);
}