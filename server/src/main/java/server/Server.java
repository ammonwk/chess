package server;

import dataaccess.*;
import service.*;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        DataAccess dataAccess;
        try {
            dataAccess = new SqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        Spark.delete("/db", new ClearHandler(dataAccess));
        Spark.post("/user", new RegisterHandler(new UserService(dataAccess)));
        Spark.post("/session", new LoginHandler(new UserService(dataAccess)));
        Spark.delete("/session", new LogoutHandler(new UserService(dataAccess)));
        Spark.get("/game", new ListGamesHandler(new GameService(dataAccess)));
        Spark.post("/game", new CreateGameHandler(new GameService(dataAccess)));
        Spark.put("/game", new JoinGameHandler(new GameService(dataAccess)));

        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body("{\"message\":\"Error: " + exception.getMessage() + "\"}");
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
