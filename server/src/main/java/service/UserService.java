package service;

import dtos.DataAccessException;
import dataaccess.*;
import model.*;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

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
        // Encrypt password
        user = new UserData(user.username(), BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email());
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
        if (user == null || !BCrypt.checkpw(password, user.password())) {
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