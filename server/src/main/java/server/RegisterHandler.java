package server;

import com.google.gson.Gson;
import service.*;
import model.*;
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

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: Missing required fields"));
        }

        try {
            AuthData authData = userService.register(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
            res.status(200);
            return gson.toJson(new RegisterResult(authData.username(), authData.authToken()));
        } catch (DataAccessException e) {
            String message = e.getMessage();
            if (message.equals("Error: User already taken")) {
                res.status(403);
                return gson.toJson(new ErrorResult(message));
            } else {
                res.status(500);
                return gson.toJson(new ErrorResult("Error: " + message));
            }
        }
    }
}
