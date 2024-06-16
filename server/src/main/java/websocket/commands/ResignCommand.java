package websocket.commands;

public class ResignCommand extends UserGameCommand {
    public int gameId;
    public String username;

    public ResignCommand(String authToken, int gameId, String username) {
        super(authToken);
        this.gameId = gameId;
        this.username = username;
        commandType = CommandType.RESIGN;
    }

    public int getGameId() {
        return gameId;
    }

    public String getUsername() {
        return username;
    }
}
