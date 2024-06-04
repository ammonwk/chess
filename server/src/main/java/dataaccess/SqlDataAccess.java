package dataaccess;

import com.google.gson.Gson;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlDataAccess implements DataAccess{
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();

    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = { "DROP TABLE IF EXISTS users;",
    "DROP TABLE IF EXISTS auths;",
    "DROP TABLE IF EXISTS games;",
    """
    CREATE TABLE IF NOT EXISTS users (
      `id` int NOT NULL AUTO_INCREMENT,
      `username` varchar(256) NOT NULL,
      `email` varchar(256) NOT NULL,
      `password` varchar(256) NOT NULL,
      PRIMARY KEY (`id`),
      INDEX(email),
      INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,
    """
    CREATE TABLE IF NOT EXISTS auths (
      `id` int NOT NULL AUTO_INCREMENT,
      `username` varchar(256) NOT NULL,
      `authtoken` varchar(256) NOT NULL,
      PRIMARY KEY (`id`),
      INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,
    """
    CREATE TABLE IF NOT EXISTS games (
      `id` int NOT NULL AUTO_INCREMENT,
      `gameName` varchar(256) NOT NULL,
      `gameId` varchar(256) NOT NULL,
      `playerColor` ENUM('white', 'black') DEFAULT 'white',
      `gamestate` varchar(256) NOT NULL,
      PRIMARY KEY (`id`),
      INDEX(gameName),
      INDEX(gameId)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements ) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE users");
        executeUpdate("TRUNCATE auth");
        executeUpdate("TRUNCATE games");
    }

    @Override
    public synchronized void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, email, hashedPassword) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.email(), user.password());
    }

    @Override
    public synchronized UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("SELECT username, email FROM users WHERE username = ?");) {
                statement.setString(1, username);
                try (ResultSet rs = statement.executeQuery()) {
                    if(rs.next()) {
                        return new UserData(rs.getString(1), "", rs.getString(3));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO users (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, auth.username(), auth.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("SELECT username, authToken FROM auths WHERE authToken = ?");) {
                statement.setString(1, authToken);
                try (ResultSet rs = statement.executeQuery()) {
                    if(rs.next()) {
                        return new AuthData(rs.getString(1), rs.getString(2));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public synchronized void createGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    @Override
    public synchronized GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }
}
