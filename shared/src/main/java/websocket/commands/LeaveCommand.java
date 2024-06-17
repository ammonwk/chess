package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    public int gameId;

    public LeaveCommand(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
        commandType = CommandType.LEAVE;
    }

    public int getGameId() {
        return gameId;
    }
}
