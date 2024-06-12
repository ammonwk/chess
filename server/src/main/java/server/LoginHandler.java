package server;

import dtos.DataAccessException;
import dtos.ErrorResult;
import dtos.LoginRequest;
import dtos.LoginResult;
import com.google.gson.Gson;
import service.*;
import model.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

        if (loginRequest.username() == null || loginRequest.password() == null) {
            res.status(400);
            return gson.toJson(new ErrorResult("Error: Missing username or password"));
        }

        try {
            AuthData authData = userService.login(loginRequest.username(), loginRequest.password());
            res.status(200);
            return gson.toJson(new LoginResult(authData.username(), authData.authToken()));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
