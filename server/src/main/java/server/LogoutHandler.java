package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.*;

public class LogoutHandler implements Route {
    private UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        Gson gson = new Gson();
        try {
            userService.logout(authToken);
            res.status(200);
            return gson.toJson(new ClearResult("Logged out successfully"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ClearResult("Error: " + e.getMessage()));
        }
    }
}
