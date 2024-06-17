package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dtos.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    public final ConcurrentHashMap<Integer, Integer> gameConnections = new ConcurrentHashMap<>();
    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        if (message.contains("CONNECT")) {
            ConnectCommand command = new Gson().fromJson(message, ConnectCommand.class);
            connect(command, session);
        } else if (message.contains("MAKE_MOVE")) {
            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
            //makeMove(command, session);
        } else if (message.contains("LEAVE")) {
            LeaveCommand command = new Gson().fromJson(message, LeaveCommand.class);
            leave(command, session);
        } else if (message.contains("RESIGN")) {
            ResignCommand command = new Gson().fromJson(message, ResignCommand.class);
            //resign(command, session);
        }
    }

    public List<Integer> getUsernamesForGame(int thisGame) {
        return gameConnections.entrySet().stream()
                .filter(entry -> entry.getValue().equals(thisGame))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void connect(ConnectCommand connectCommand, Session session) {
        connections.add(connectCommand.getAuthString(), session);
        String username;
        GameData gameData = null;
        try {
            AuthData auth = dataAccess.getAuth(connectCommand.getAuthString());
            if (auth == null) {
                connections.returnError(session.hashCode(), new ErrorMessage("Error: Unauthorized."));
                return;
            }
            username = auth.username();
            gameData = dataAccess.getGame(connectCommand.getGameId());
            if (gameData == null) {
                connections.returnError(session.hashCode(), new ErrorMessage("Error: No game found."));
                return;
            }
        } catch (DataAccessException e) {
            connections.returnError(session.hashCode(), new ErrorMessage("Error: Unauthorized."));
            return;
        }
        gameConnections.put(session.hashCode(), gameData.gameID());
        String message;
        if (gameData.whiteUsername().equals(username)) {
            message = String.format("%s joined the game as White.", username);
        } else if (gameData.blackUsername().equals(username)) {
            message = String.format("%s joined the game as Black.", username);
        } else {
            message = String.format("%s joined the game as an observer.", username);
        }
        var notification = new NotificationMessage(message);
        List<Integer> toNotify;
        try {
            toNotify = getUsernamesForGame(gameData.gameID());
            Integer hash = session.hashCode();
            toNotify.remove(hash);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        connections.broadcastNotification(toNotify, notification);
        ChessGame game = gameData.game();
        connections.sendGame(connectCommand.getAuthString(),
                game);
        System.out.println("Sent game: " + game.toString());
        System.out.println("Sent notification: " + notification);
    }

    private void leave(LeaveCommand leaveCommand, Session session) {
        connections.remove(leaveCommand.getAuthString());
        gameConnections.remove(leaveCommand.getAuthString());
        GameData gameData = null;
        String username;
        try {
            username = dataAccess.getAuth(leaveCommand.getAuthString()).username();
            gameData = dataAccess.getGame(leaveCommand.getGameId());
            if (gameData.whiteUsername().equals(username)) {
                dataAccess.updateGame(
                        new GameData(gameData.gameID(),
                                null,
                                gameData.blackUsername(),
                                gameData.gameName(),
                                gameData.game()));
            } else if (gameData.blackUsername().equals(username)) {
                dataAccess.updateGame(
                        new GameData(gameData.gameID(),
                                gameData.whiteUsername(),
                                null,
                                gameData.gameName(),
                                gameData.game()));
            }

        } catch (DataAccessException e) {
            connections.returnError(session.hashCode(), new ErrorMessage("Error: Could not connect."));
            return;
        }

        var message = String.format("%s left the game.", username);
        var notification = new NotificationMessage(message);
        connections.broadcastNotification(getUsernamesForGame(gameData.gameID()), notification);
        System.out.println("Sent notification: " + notification);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        if (error instanceof EofException) {
            System.err.println("Client " + session.getRemoteAddress() + " closed the connection.");
        } else if (error instanceof TimeoutException) {
            System.err.println("Client " + session.getRemoteAddress() + " timed out.");
        }
        session.close();
    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws DataAccessException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new DataAccessException(ex.getMessage());
//        }
//    }
}