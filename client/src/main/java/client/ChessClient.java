package client;

import chess.ChessMove;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import dtos.*;

public class ChessClient {
    private ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade webSocket;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public void clear() throws DataAccessException {
        server = new ServerFacade(serverUrl);
        server.clearDatabase();
    }

    public String register(String username, String password, String email) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        String result = server.registerUser(new RegisterRequest(username, password, email)).authToken();
        webSocket = new WebSocketFacade(serverUrl, notificationHandler);
        return result;
    }

    public String login(String username, String password) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        String result = server.loginUser(new LoginRequest(username, password)).authToken();
        webSocket = new WebSocketFacade(serverUrl, notificationHandler);
        return result;
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
        JoinGameResult result = server.joinGame(new JoinGameRequest(authToken, gameId, playerColor));
        webSocket.connect(authToken, gameId);
        return result;
    }

    public void leaveGame(String authToken, int gameId) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        webSocket.leave(authToken, gameId);
    }

    public void observeGame(String authToken, int gameId) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        webSocket.connect(authToken, gameId);
    }

    public void makeMove(String authToken, int gameId, ChessMove move) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        webSocket.makeMove(authToken, gameId, move);
    }

}
