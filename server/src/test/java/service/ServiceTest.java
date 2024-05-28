package service;

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
    void testRegisterUser_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        assertNotNull(auth);
        assertEquals("testUser", auth.username());
    }

    @Test
    void testRegisterUser_Negative() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        userService.register(user);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(user);
        });
        assertEquals("Error: User already taken", exception.getMessage());
    }

    @Test
    void testLogin_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        userService.register(user);
        AuthData auth = userService.login("testUser", "password");
        assertNotNull(auth);
        assertEquals("testUser", auth.username());
    }

    @Test
    void testLogin_Negative() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        userService.register(user);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login("testUser", "wrongPassword");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void testLogout_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        userService.logout(auth.authToken());
    }

    @Test
    void testLogout_Negative() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.logout("invalidToken");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void testCreateGame_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        GameData game = gameService.createGame(auth.authToken(), "testGame");
        assertNotNull(game);
        assertEquals("testGame", game.gameName());
    }

    @Test
    void testCreateGame_Negative() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame("invalidToken", "testGame");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void testListGames_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        gameService.createGame(auth.authToken(), "testGame1");
        gameService.createGame(auth.authToken(), "testGame2");

        ListGamesResult result = gameService.listGames(auth.authToken());
        assertNotNull(result);
        assertEquals(2, result.games().size());
    }

    @Test
    void testListGames_Negative() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.listGames("invalidToken");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());
    }

    @Test
    void testJoinGame_Positive() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        GameData game = gameService.createGame(auth.authToken(), "testGame");
        gameService.joinGame(auth.authToken(), game.gameID(), "WHITE");

        GameData updatedGame = dataAccess.getGame(game.gameID());
        assertEquals("testUser", updatedGame.whiteUsername());
    }

    @Test
    void testJoinGame_Negative() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "test@example.com");
        AuthData auth = userService.register(user);
        GameData game = gameService.createGame(auth.authToken(), "testGame");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(auth.authToken(), game.gameID(), "BLUE");
        });
        assertEquals("Error: Invalid player color", exception.getMessage());
    }
}
