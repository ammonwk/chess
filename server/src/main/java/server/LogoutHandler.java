package server;

import DTOs.ClearResult;
import DTOs.DataAccessException;
import DTOs.ErrorResult;
import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        Gson gson = new Gson();
        // String authToken = req.headers("Authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new ErrorResult("Error: Unauthorized"));
        }

        try {
            userService.logout(authToken);
            res.status(200);
            return gson.toJson(new ClearResult("Logged out successfully"));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
