package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public void sendGame(String userAuth, ChessGame game) {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (userAuth.equals(c.username)) {
                    try {
                        c.send(new Gson().toJson(new LoadGameMessage(game)));
                    } catch (IOException e) {
                        throw new RuntimeException("Error when connecting to sendGame: " + e.getMessage());
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void broadcastGameChange(List<Integer> sessions, ChessGame game) {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (sessions.contains(c.session.hashCode())) {
                    try {
                        c.send(new Gson().toJson(new LoadGameMessage(game)));
                    } catch (IOException e) {
                        throw new RuntimeException("Error when connecting to gameChange: " + e.getMessage());
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void broadcastNotification(List<Integer> sessions, NotificationMessage notification) {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (sessions.contains(c.session.hashCode())) {
                    try {
                        c.send(new Gson().toJson(notification, NotificationMessage.class));
                    } catch (IOException e) {
                        throw new RuntimeException("Error when connecting: " + e.getMessage());
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void returnError (Integer session, ErrorMessage notification) {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.session.hashCode() == session) {
                    try {
                        c.send(new Gson().toJson(notification, ErrorMessage.class));
                    } catch (IOException e) {
                        throw new RuntimeException("Error when connecting: " + e.getMessage());
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}