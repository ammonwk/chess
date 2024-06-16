package websocket.messages;

import websocket.commands.UserGameCommand;

public class ErrorMessage extends ServerMessage {
    public String message;

    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
