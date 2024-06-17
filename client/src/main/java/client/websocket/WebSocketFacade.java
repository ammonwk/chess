package client.websocket;

import chess.ChessGame;
import client.DrawsBoard;
import client.Repl;
import com.google.gson.Gson;
import dtos.DataAccessException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import static ui.EscapeSequences.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    if(message.contains("LOAD_GAME")) {
                        ChessGame game = new Gson().fromJson(message, LoadGameMessage.class).game;
                        notificationHandler.drawBoard(game);
                    } else if (message.contains("NOTIFICATION")) {
                        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notification);
                    } else if(message.contains("ERROR")) {
                        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(notification);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int GameId) throws DataAccessException {
        try {
            ConnectCommand command = new ConnectCommand(authToken, GameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void leave(String authToken, int GameId) throws DataAccessException {
        try {
            LeaveCommand command = new LeaveCommand(authToken, GameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
