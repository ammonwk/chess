package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints here
        Spark.delete("/db", RequestHandler::handleClearDatabase);
        Spark.post("/user", RequestHandler::handleRegister);
        Spark.post("/session", RequestHandler::handleLogin);
        Spark.post("/logout", RequestHandler::handleLogout);
        Spark.get("/game", RequestHandler::handleListGames);
        Spark.post("/game", RequestHandler::handleCreateGame);
        Spark.put("/game", RequestHandler::handleJoinGame);

        // Handle exceptions
        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body("Internal Server Error");
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}