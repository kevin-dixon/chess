package websocket;

import com.google.gson.Gson;
import ui.ChessClient;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketCommunicator {

    private Session session;
    private final Gson gson = new Gson();

    public WebSocketCommunicator(String serverUri) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(serverUri));
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> System.out.println("Game loaded: " + serverMessage.getGame());
                case NOTIFICATION -> System.out.println("Notification: " + serverMessage.getMessage());
                case ERROR -> System.err.println("Error: " + serverMessage.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Disconnected: " + reason);
    }

    public void sendCommand(UserGameCommand command) {
        try {
            session.getBasicRemote().sendText(gson.toJson(command));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addObserver(ChessClient chessClient) {
        //todo: implement
    }
}