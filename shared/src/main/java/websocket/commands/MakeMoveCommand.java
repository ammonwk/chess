package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    public int gameId;
    public String username;
    public ChessMove move;

    public MakeMoveCommand(String authToken, int gameId, ChessMove move) {
        super(authToken);
        this.gameId = gameId;
        this.move = move;
        commandType = CommandType.MAKE_MOVE;
    }

    public int getGameId() {
        return gameId;
    }

}
