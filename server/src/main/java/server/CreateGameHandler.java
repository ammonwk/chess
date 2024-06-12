package server;

import DTOs.CreateGameRequest;
import DTOs.CreateGameResult;
import DTOs.DataAccessException;
import DTOs.ErrorResult;
import com.google.gson.Gson;
import service.*;
import model.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        Gson gson = new Gson();
        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new ErrorResult("Error: Unauthorized"));
        }

        if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: Invalid game name"));
        }

        try {
            GameData gameData = gameService.createGame(authToken, createGameRequest.gameName());
            res.status(200);
            return gson.toJson(new CreateGameResult(gameData.gameID()));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorResult(e.getMessage()));
        }
    }
}
