package client;

import static ui.EscapeSequences.*;

import java.util.Arrays;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ServerFacade;
import service.*;

public class ChessClient {
    private ServerFacade server;
    private final String serverUrl;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String register(String username, String password, String email) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        return server.registerUser(new RegisterRequest(username, password, email)).authToken();
    }

    public String login(String username, String password) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        return server.loginUser(new LoginRequest(username, password)).authToken();
    }

    public Object logout(String authToken) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        return server.logoutUser(authToken);
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        return server.listGames(authToken);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        return server.createGame(new CreateGameRequest(authToken, gameName));
    }

    public JoinGameResult joinGame(String authToken, int gameId, String playerColor) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        return server.joinGame(new JoinGameRequest(authToken, gameId, playerColor));
    }
}
