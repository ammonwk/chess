package service;

import dtos.DataAccessException;
import dtos.ListGamesResult;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    private DataAccess dataAccess;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    void setup() {
        dataAccess = new InMemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
    }

    @Test
    void registerUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        assertNotNull(auth);
        assertEquals("testUser", auth.username());
    }

    @Test
    void registerUserNegative() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        userService.register(user);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(user);
        });
        assertEquals("Error: User already taken", exception.getMessage());
    }

    @Test
    void registerUserMissingFields() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(new UserData(null, "password", "test@example.com"));
        });
        assertEquals("Error: Invalid user data", exception.getMessage());

        exception = assertThrows(DataAccessException.class, () -> {
            userService.register(new UserData("testUser", null, "test@example.com"));
        });
        assertEquals("Error: Invalid user data", exception.getMessage());

        exception = assertThrows(DataAccessException.class, () -> {
            userService.register(new UserData("testUser", "password", null));
        });
        assertEquals("Error: Invalid user data", exception.getMessage());
    }

    @Test
    void loginPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        userService.register(user);
        AuthData auth = userService.login("testUser", "password");
        assertNotNull(auth);
        assertEquals("testUser", auth.username());
    }

    @Test
    void loginNegative() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        userService.register(user);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("testUser", "wrongPassword");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void logoutPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        userService.logout(auth.authToken());
    }

    @Test
    void logoutNegative() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.logout("invalidToken");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void createGamePositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        GameData game = gameService.createGame(auth.authToken(), "testGame");
        assertNotNull(game);
        assertEquals("testGame", game.gameName());
    }

    @Test
    void createGameNegative() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame("invalidToken", "testGame");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void createGameEmptyGameName() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(auth.authToken(), "");
        });
        assertEquals("Error: Invalid game name", exception.getMessage());

        exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(auth.authToken(), "   ");
        });
        assertEquals("Error: Invalid game name", exception.getMessage());
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        gameService.createGame(auth.authToken(), "testGame1");
        gameService.createGame(auth.authToken(), "testGame2");

        ListGamesResult result = gameService.listGames(auth.authToken());
        assertNotNull(result);
        assertEquals(2, result.games().size());
    }

    @Test
    void listGamesNegative() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.listGames("invalidToken");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        GameData game = gameService.createGame(auth.authToken(), "testGame");
        gameService.joinGame(auth.authToken(), game.gameID(), "WHITE");

        GameData updatedGame = dataAccess.getGame(game.gameID());
        assertEquals("testUser", updatedGame.whiteUsername());
    }

    @Test
    void joinGameInvalidColor() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        GameData game = gameService.createGame(auth.authToken(), "testGame");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(auth.authToken(), game.gameID(), "BLUE");
        });
        assertEquals("Error: Invalid player color", exception.getMessage());
    }

    @Test
    void joinGameAlreadyTakenColor() throws DataAccessException {
        UserData user1 = new UserData("testUser1", "password", "test1@example.com");
        AuthData auth1 = userService.register(user1);
        GameData game = gameService.createGame(auth1.authToken(), "testGame");
        gameService.joinGame(auth1.authToken(), game.gameID(), "WHITE");

        UserData user2 = new UserData("testUser2", "password", "test2@example.com");
        AuthData auth2 = userService.register(user2);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(auth2.authToken(), game.gameID(), "WHITE");
        });
        assertEquals("Error: Color already taken", exception.getMessage());
    }
}
