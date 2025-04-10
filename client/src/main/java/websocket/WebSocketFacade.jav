package websocket;

import com.google.gson.Gson;
import webSocketMessages.Notification;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {
    private Session session;
    private final NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws Exception {
        this.notificationHandler = notificationHandler;
        URI socketURI = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            Notification notification = new Gson().fromJson(message, Notification.class);
            notificationHandler.notify(notification);
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void close() throws IOException {
        session.close();
    }
}