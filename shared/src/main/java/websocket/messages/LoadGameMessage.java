package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    public String message;
    public ChessGame game;

    public LoadGameMessage(String message, ChessGame game) {
        super(ServerMessageType.ERROR);
        this.message = message;
        this.game = game;
    }

    public String getMessage() {
        return message;
    }

}
