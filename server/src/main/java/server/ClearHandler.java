package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;
import dataaccess.*;

public class ClearHandler implements Route {
    private DataAccess dataAccess;

    public ClearHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public Object handle(Request req, Response res) {
        Gson gson = new Gson();
        try {
            dataAccess.clear();
            res.status(200);
            return gson.toJson(new ClearResult("Database cleared successfully"));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(new ClearResult("Error: " + e.getMessage()));
        }
    }
}
