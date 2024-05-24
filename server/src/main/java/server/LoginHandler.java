package server;

import com.google.gson.Gson;
import service.*;
import model.*;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.*;

public class LoginHandler implements Route {
    private UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        try {
            AuthData authData = userService.login(loginRequest.username(), loginRequest.password());
            res.status(200);
            return gson.toJson(new LoginResult(authData.username(), authData.authToken()));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new LoginResult("Error: " + e.getMessage(), null));
        }
    }
}
