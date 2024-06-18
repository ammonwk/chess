package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
import java.util.Collection;
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
            makeMove(command, session);
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
        GameData gameData;
        String username;
        connections.add(connectCommand.getAuthString(), session);
        try {
            gameData = getGameData(connectCommand.getAuthString(), connectCommand.gameId, session);
            username = dataAccess.getAuth(connectCommand.getAuthString()).username();
        } catch (DataAccessException e) {
            connections.returnError(session.hashCode(), new ErrorMessage("Error: Unauthorized."));
            return;
        }
        gameConnections.put(session.hashCode(), gameData.gameID());
        var notification = getNotificationMessage(gameData, username);
        List<Integer> toNotify;
        toNotify = getUsernamesForGame(gameData.gameID());
        Integer hash = session.hashCode();
        toNotify.remove(hash);
        connections.broadcastNotification(toNotify, notification);
        ChessGame game = gameData.game();
        connections.sendGame(connectCommand.getAuthString(),
                game);
        System.out.println("Sent game: " + game.toString());
        System.out.println("Sent notification: " + notification);
    }

    private static NotificationMessage getNotificationMessage(GameData gameData, String username) {
        String message;
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            message = String.format("%s joined the game as White.", username);
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            message = String.format("%s joined the game as Black.", username);
        } else {
            message = String.format("%s joined the game as an observer.", username);
        }
        var notification = new NotificationMessage(message);
        return notification;
    }

    private void leave(LeaveCommand leaveCommand, Session session) {
        connections.remove(leaveCommand.getAuthString());
        gameConnections.remove(session.hashCode());
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

    private GameData getGameData(String authString, Integer gameId, Session session) throws DataAccessException {
        String username;
        GameData gameData;
        AuthData auth = dataAccess.getAuth(authString);
        if (auth == null) {
            connections.returnError(session.hashCode(), new ErrorMessage("Error: Unauthorized."));
            return null;
        }
        username = auth.username();
        gameData = dataAccess.getGame(gameId);
        if (gameData == null) {
            connections.returnError(session.hashCode(), new ErrorMessage("Error: No game found."));
            return null;
        }

        return gameData;
    }

    private void makeMove(MakeMoveCommand moveCommand, Session session) {
        GameData gameData;
        String username;
        try {
            gameData = getGameData(moveCommand.getAuthString(), moveCommand.gameId, session);
            username = dataAccess.getAuth(moveCommand.getAuthString()).username();
            ChessGame.TeamColor color = gameData.whiteUsername().equals(username) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            ChessGame.TeamColor turn = gameData.game().getTeamTurn();
            if(color != turn) {
                connections.returnError(session.hashCode(), new ErrorMessage("Error: It is not your turn."));
                return;
            }
            try {
                gameData.game().makeMove(moveCommand.move);
                gameData.game().setTeamTurn(turn == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
                dataAccess.updateGame(gameData);
                List<Integer> toNotify;
                toNotify = getUsernamesForGame(gameData.gameID());
                connections.broadcastGameChange(toNotify, gameData.game());
            } catch (InvalidMoveException e) {
                connections.returnError(session.hashCode(), new ErrorMessage("Error: Invalid Move."));
                return;
            }
        } catch (DataAccessException e) {
            connections.returnError(session.hashCode(), new ErrorMessage("Error: Unauthorized."));
            return;
        }

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
}