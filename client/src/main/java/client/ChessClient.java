package client;

import static ui.EscapeSequences.*;

import java.util.Arrays;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ServerFacade;
import service.LoginRequest;

public class ChessClient {
    private ServerFacade server;
    private final String serverUrl;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String login(String username, String password) throws DataAccessException {
        server = new ServerFacade(serverUrl);
        return server.loginUser(new LoginRequest(username, password)).authToken();
    }
}
