package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    public int gameId;
    public String username;

    public LeaveCommand(String authToken, int gameId, String username) {
        super(authToken);
        this.gameId = gameId;
        this.username = username;
        commandType = CommandType.LEAVE;
    }

    public int getGameId() {
        return gameId;
    }

    public String getUsername() {
        return username;
    }
}
