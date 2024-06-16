package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    public int gameId;
    public String username;

    public ConnectCommand(String authToken, int gameId, String username) {
        super(authToken);
        this.gameId = gameId;
        this.username = username;
        commandType = CommandType.CONNECT;
    }

    public int getGameId() {
        return gameId;
    }

    public String getUsername() {
        return username;
    }
}
