package client.websocket;

import chess.ChessGame;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void error(NotificationMessage notification);
    void notify(NotificationMessage notification);
    void drawBoard(ChessGame game);
}