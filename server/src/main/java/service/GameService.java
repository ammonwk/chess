package service;

import dataaccess.*;
import model.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameService {
    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new DataAccessException("Invalid game name");
        }
        String username = authData.username();
        int gameID = UUID.randomUUID().hashCode();
        while (dataAccess.getGame(gameID) != null) {
            gameID = UUID.randomUUID().hashCode();
        }
        GameData gameData = new GameData(gameID, username, null, gameName, null);
        dataAccess.createGame(gameData);
        return gameData;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }
        List<GameData> gameDataList = dataAccess.listGames();
        List<ListGamesResult.GameSummary> gameSummaries = gameDataList.stream()
                .map(gameData -> new ListGamesResult.GameSummary(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName()
                ))
                .collect(Collectors.toList());
        return new ListGamesResult(gameSummaries);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }
        String username = authData.username();
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found");
        }
        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new DataAccessException("Invalid player color");
        }
        if (playerColor.equals("WHITE") && gameData.whiteUsername() == null) {
            gameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else if (playerColor.equals("BLACK") && gameData.blackUsername() == null) {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        } else {
            throw new DataAccessException("Color already taken");
        }
        dataAccess.updateGame(gameData);
    }
}
