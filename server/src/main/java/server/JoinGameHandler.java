package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.*;

public class JoinGameHandler implements Route {
    private GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        Gson gson = new Gson();
        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
        try {
            gameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
            res.status(200);
            return gson.toJson(new JoinGameResult("Joined game successfully"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new JoinGameResult("Error: " + e.getMessage()));
        }
    }
}
