package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.Notification;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Notification notification = new Gson().fromJson(message, Notification.class);
        connections.broadcast(message, notification);
    }

    public void broadcast(Notification notification, String message) throws IOException {
        connections.broadcast(message, notification);
    }
}