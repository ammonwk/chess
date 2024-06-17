package websocket.commands;

import com.google.gson.annotations.SerializedName;

public class ConnectCommand extends UserGameCommand {
    @SerializedName("gameID")
    public int gameId;

    public ConnectCommand(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
        commandType = CommandType.CONNECT;
    }

    public int getGameId() {
        return gameId;
    }

}
