package ui;

import server.ServerFacade;
import websocket.NotificationHandler;

public class UserClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private State state = State.SIGNEDOUT;

    public UserClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public String register(String username, String password, String email) throws Exception, ResponseException {
        return server.register(username, password, email);
    }

    public String login(String username, String password) throws Exception, ResponseException {
        return server.login(username, password);
    }

    public String logout(String authToken) throws Exception, ResponseException {
        return server.logout(authToken);
    }
}
