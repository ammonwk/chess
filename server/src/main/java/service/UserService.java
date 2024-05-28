package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: Invalid user data");
        }
        if (dataAccess.getUser(user.username()) != null) {
            throw new DataAccessException("Error: User already taken");
        }
        dataAccess.createUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        if (username == null || password == null) {
            throw new DataAccessException("Error: Invalid credentials");
        }
        UserData user = dataAccess.getUser(username);
        if (user == null || !user.password().equals(password)) {
            throw new DataAccessException("Error: Unauthorized");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: Unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }
}