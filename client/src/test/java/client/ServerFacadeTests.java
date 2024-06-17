package client;

import dtos.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import dtos.CreateGameResult;

public class ServerFacadeTests {

    private static Server server;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setUp() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        try {
            client.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerWorks() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertDoesNotThrow(() -> {client.register("test", "Test", "test");});
    }

    @Test
    public void registerRequiresUsername() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertThrows(DataAccessException.class, () -> {client.register(null, "Test", "test");});
    }

    @Test
    public void loginWorks() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertDoesNotThrow(() -> {client.register("test", "Test", "test");});
        Assertions.assertDoesNotThrow(() -> {client.login("test", "Test");});
    }

    @Test
    public void invalidLoginThrows() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertThrows(DataAccessException.class, () -> {client.login(null, "Test");});
    }

    @Test
    public void logoutWorks() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertDoesNotThrow(() -> {
            client.register("test", "Test", "test");
            String auth = client.login("test", "Test");
            client.logout(auth);
        });
    }

    @Test
    public void invalidLogoutThrows() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertThrows(DataAccessException.class, () -> {client.logout("Test");});
    }

    @Test
    public void createGameWorks() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertDoesNotThrow(() -> {
            client.register("test", "Test", "test");
            String auth = client.login("test", "Test");
            client.createGame(auth, "test");
        });
    }

    @Test
    public void createGameThrows() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertThrows(DataAccessException.class, () -> {client.createGame("Test", "test");});
    }

    @Test
    public void listGamesWorks() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertDoesNotThrow(() -> {
            client.register("test", "Test", "test");
            String auth = client.login("test", "Test");
            client.createGame(auth, "test");
            Assertions.assertEquals(1, client.listGames(auth).games().size());
        });
    }

    @Test
    public void listGamesEmpty() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertDoesNotThrow(() -> {
            client.register("test", "Test", "test");
            String auth = client.login("test", "Test");
            Assertions.assertEquals(0, client.listGames(auth).games().size());
        });
    }

    @Test
    public void joinGameWorks() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertDoesNotThrow(() -> {
            client.register("test", "Test", "test");
            String auth = client.login("test", "Test");
            CreateGameResult game = client.createGame(auth, "test");
            client.joinGame(auth, game.gameID(), "WHITE");
        });
    }

    @Test
    public void joinGameThrows() {
        ChessClient client = new ChessClient("http://localhost:" + port, null);
        Assertions.assertThrows(DataAccessException.class, () -> {client.joinGame("Test", 1, "WHITE");});
    }
}
