package server;

import com.google.gson.Gson;
import service.*;
import model.*;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.*;

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
        try {
            GameData gameData = gameService.createGame(authToken, createGameRequest.gameName());
            res.status(200);
            return gson.toJson(new CreateGameResult(gameData.gameID()));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ClearResult("Error: " + e.getMessage()));
        }
    }
}
