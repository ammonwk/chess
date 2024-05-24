package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.*;

public class RegisterHandler implements Route {
    private UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            AuthData authData = userService.register(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
            res.status(200);
            return gson.toJson(new RegisterResult(authData.username(), authData.authToken()));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new RegisterResult("Error: " + e.getMessage(), null));
        }
    }
}
