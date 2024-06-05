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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlDataAccess implements DataAccess{
    private final Map<Integer, GameData> games = new HashMap<>();

    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
    """
    CREATE TABLE IF NOT EXISTS users (
      `id` int NOT NULL AUTO_INCREMENT,
      `username` varchar(256) NOT NULL,
      `email` varchar(256) NOT NULL,
      `hashedPassword` varchar(256) NOT NULL,
      PRIMARY KEY (`id`),
      INDEX(email),
      INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,
    """
    CREATE TABLE IF NOT EXISTS auths (
      `id` int NOT NULL AUTO_INCREMENT,
      `username` varchar(256) NOT NULL,
      `authToken` varchar(256) NOT NULL,
      PRIMARY KEY (`id`),
      INDEX(username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    """,
    """
    CREATE TABLE IF NOT EXISTS games (
      `id` int NOT NULL AUTO_INCREMENT,
      `gameId` varchar(256) NOT NULL,
      `game` TEXT DEFAULT NULL,
      PRIMARY KEY (`id`),
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
        executeUpdate("TRUNCATE auths");
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
            try (PreparedStatement statement = conn.prepareStatement("SELECT username, hashedPassword, email FROM users WHERE username = ?");) {
                statement.setString(1, username);
                try (ResultSet rs = statement.executeQuery()) {
                    if(rs.next()) {
                        return new UserData(rs.getString(1), rs.getString(2), rs.getString(3));
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
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, auth.username(), auth.authToken());
        System.out.println("Created auth: " + auth.username() + " with token: " + auth.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("SELECT username, authToken FROM auths WHERE authToken = ?");) {
                statement.setString(1, authToken);
                try (ResultSet rs = statement.executeQuery()) {
                    if(rs.next()) {
                        System.out.println("Retrieved auth for token: " + authToken + ", username: " + rs.getString(1));
                        return new AuthData(rs.getString(2), rs.getString(1));
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
        var statement = "DELETE FROM auths WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    @Override
    public synchronized void createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (gameId, game) VALUES (?, ?)";
        var json = new Gson().toJson(game);
        executeUpdate(statement, game.gameID(), json);
    }

    @Override
    public synchronized GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("SELECT gameID, game FROM games WHERE gameID = ?");) {
                statement.setString(1, String.valueOf(gameID));
                try (ResultSet rs = statement.executeQuery()) {
                    if(rs.next()) {
                        return new Gson().fromJson(rs.getString("game"), GameData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games_to_return = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("SELECT game FROM games");) {
                try (ResultSet rs = statement.executeQuery()) {
                    while(rs.next()) {
                        GameData game = new Gson().fromJson(rs.getString("game"), GameData.class);
                        games_to_return.add(game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return games_to_return;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE games SET game = (?) WHERE gameID = (?)";
        var json = new Gson().toJson(game);
        executeUpdate(statement, json, game.gameID());
    }
}
