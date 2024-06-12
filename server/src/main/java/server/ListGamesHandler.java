package server;

import DTOs.DataAccessException;
import DTOs.ErrorResult;
import DTOs.ListGamesResult;
import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        Gson gson = new Gson();
        // String authToken = req.headers("Authorization");

        try {
            ListGamesResult result = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
