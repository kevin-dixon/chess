package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class ChessWebSocketClient {

    private Session session;
    private final Gson gson = new Gson();

    public ChessWebSocketClient(String serverUri) throws Exception {
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
        System.out.println("Received: " + message);
        // Handle incoming messages
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
}