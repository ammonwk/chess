package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dtos.DataAccessException;
import model.GameData;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        if (message.contains("CONNECT")) {
            ConnectCommand command = new Gson().fromJson(message, ConnectCommand.class);
            connect(command, session);
        }// else if (message.contains("MAKE_MOVE")) {
//            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
//            makeMove(command, session);
//        } else if (message.contains("LEAVE")) {
//            LeaveCommand command = new Gson().fromJson(message, LeaveCommand.class);
//            Leave(command, session);
//        } else if (message.contains("RESIGN")) {
//            ResignCommand command = new Gson().fromJson(message, ResignCommand.class);
//            Resign(command, session);
//        }
    }

    private void connect(ConnectCommand connectCommand, Session session) {
        try {
            connections.add(connectCommand.getAuthString(), session);
            String username = dataAccess.getAuth(connectCommand.getAuthString()).username();
            var message = String.format("%s joined the game.", username);
            var notification = new NotificationMessage(message);
            connections.broadcast(connectCommand.getAuthString(), notification);
            GameData gameData = dataAccess.getGame(connectCommand.getGameId());
            ChessGame game = gameData.game();
            connections.sendGame(connectCommand.getAuthString(),
                    game);
            System.out.println("Sent game: " + game.toString());
            System.out.println("Sent notification: " + notification);
        } catch (DataAccessException e) {
            System.out.println("DataAccessException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
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