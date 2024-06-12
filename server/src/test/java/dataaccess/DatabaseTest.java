package dataaccess;

import DTOs.DataAccessException;
import org.junit.jupiter.api.*;
import service.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.*;

import java.util.ArrayList;

class DatabaseTest {
    private DataAccess dataAccess;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        try {
            dataAccess = new SqlDataAccess();
            dataAccess.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clearTest() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void createUserWorks() throws DataAccessException {
        UserData user = new UserData("test", "test", "test");
        dataAccess.createUser(user);
    }

    @Test
    void createUserDuplicate() throws DataAccessException {
        UserData user = new UserData("test", "test", "test");
        dataAccess.createUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(user);
        });
    }

    @Test
    void getUserWorks() throws DataAccessException {
        UserData user = new UserData("test", "test", "test");
        dataAccess.createUser(user);
        Assertions.assertEquals(user, dataAccess.getUser(user.username()));
    }

    @Test
    void getUserNotFound() throws DataAccessException {
        Assertions.assertEquals(dataAccess.getUser("nobody"), null);
    }

    @Test
    void createAuthWorks() throws DataAccessException {
        AuthData auth = new AuthData("test", "test");
        dataAccess.createAuth(auth);
    }

    @Test
    void createAuthOverride() throws DataAccessException {
        AuthData auth = new AuthData("test", "test");
        dataAccess.createAuth(auth);
        dataAccess.createAuth(auth);
    }

    @Test
    void getAuthWorks() throws DataAccessException {
        AuthData auth = new AuthData("test", "test");
        dataAccess.createAuth(auth);
        Assertions.assertEquals("test", dataAccess.getAuth("test").authToken());
    }

    @Test
    void getAuthNone() throws DataAccessException {
        Assertions.assertEquals(null, dataAccess.getAuth("test"));
    }

    @Test
    void deleteAuthWorks() throws DataAccessException {
        AuthData auth = new AuthData("test", "test");
        dataAccess.createAuth(auth);
        Assertions.assertEquals("test", dataAccess.getAuth("test").authToken());
        dataAccess.deleteAuth("test");
        Assertions.assertEquals(null, dataAccess.getAuth("test"));
    }

    @Test
    void deleteAuthNothing() throws DataAccessException {
        dataAccess.deleteAuth("a token that doesn't exist");
    }

    @Test
    void createGameWorks() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(1, "white", "black", "test", game);
        dataAccess.createGame(data);
    }

    @Test
    void createGameNull() throws DataAccessException {
        GameData data = new GameData(1, null, null, null, null);
        dataAccess.createGame(data);
    }

    @Test
    void getGameWorks() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(1, "white", "black", "test", game);
        dataAccess.createGame(data);
        Assertions.assertTrue(data.game().getBoard().equals(dataAccess.getGame(1).game().getBoard()));
    }

    @Test
    void getGameNone() throws DataAccessException {
        Assertions.assertEquals(null, dataAccess.getGame(1));
    }

    @Test
    void listGamesWorks() throws DataAccessException {
        dataAccess.createGame(new GameData(1, "white", "black", "test", new ChessGame()));
        Assertions.assertEquals(1, dataAccess.listGames().size());
    }

    @Test
    void listGamesNone() throws DataAccessException {
        Assertions.assertEquals(new ArrayList<>(), dataAccess.listGames());
    }

    @Test
    void updateGameWorks() throws DataAccessException, InvalidMoveException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(1, "white", "black", "test", game);
        dataAccess.createGame(data);
        Assertions.assertTrue(data.game().getBoard().equals(dataAccess.getGame(1).game().getBoard()));
        game.makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(4,1), null));
        dataAccess.updateGame(data);
        Assertions.assertTrue(data.game().getBoard().equals(dataAccess.getGame(1).game().getBoard()));
    }

    @Test
    void updateGameNeverAdded() throws DataAccessException, InvalidMoveException {
        ChessGame game = new ChessGame();
        GameData data = new GameData(1, "white", "black", "test", game);
        dataAccess.updateGame(data);
        Assertions.assertEquals(null, dataAccess.getGame(1));
    }
}