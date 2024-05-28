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

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new ErrorResult("Error: Unauthorized"));
        }

        // Check if gameID is valid
        if (joinGameRequest.gameID() <= 0) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: Invalid game ID"));
        }

        // Check if playerColor is valid
        if (joinGameRequest.playerColor() == null ||
                (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK"))) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: Invalid player color"));
        }

        try {
            gameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
            res.status(200);
            return gson.toJson(new JoinGameResult("Joined game successfully"));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.equals("Error: Unauthorized")) {
                res.status(401);
            } else if (message.equals("Error: Game not found") || message.equals("Error: Color already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return gson.toJson(new ErrorResult("Error: " + message));
        }
    }
}
