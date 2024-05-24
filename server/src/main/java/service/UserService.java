package service;

import dataaccess.*;
import model.*;
import java.util.*; // UUID

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) != null) {
            throw new DataAccessException("User already taken");
        }
        dataAccess.createUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);
        if (user == null || !user.password().equals(password)) {
            throw new DataAccessException("Unauthorized");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        dataAccess.deleteAuth(authToken);
    }
}
