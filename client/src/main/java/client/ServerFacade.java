package client;
import DTOs.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clearDatabase() throws DataAccessException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    public RegisterResult registerUser(RegisterRequest req) throws DataAccessException {
        var path = "/user";
        return this.makeRequest("POST", path, req, RegisterResult.class);
    }

    public LoginResult loginUser(LoginRequest req) throws DataAccessException {
        var path = "/session";
        return this.makeRequest("POST", path, req, LoginResult.class);
    }

    public ClearResult logoutUser(String auth) throws DataAccessException {
        var path = "/session";
        return this.makeRequest("DELETE", path, auth, null);
    }

    public ListGamesResult listGames(String auth) throws DataAccessException {
        var path = "/game";
        return this.makeRequest("GET", path, auth, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        var path = "/game";
        return this.makeRequest("POST", path, req, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest req) throws DataAccessException {
        var path = "/game";
        return this.makeRequest("PUT", path, req, JoinGameResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (request instanceof String authToken) {
                http.setRequestProperty("Authorization", authToken);
            } else {
                writeBody(request, http);
            }

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException("Error " + status + ": " + http.getResponseMessage());
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
