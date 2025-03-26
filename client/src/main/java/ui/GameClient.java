package ui;

import model.GameData;
import server.ServerFacade;
import websocket.NotificationHandler;

public class GameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private State state = State.SIGNEDOUT;

    public GameClient(String serverUrl, NotificationHandler notifyHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notifyHandler;
    }

    public String createGame(String authToken, String gameName) throws Exception, ResponseException {
        return server.createGame(authToken, gameName);
    }

    public GameData[] listGames(String authToken) throws Exception, ResponseException {
        return server.listGames(authToken);
    }

    public String joinGame(String authToken, int gameID, String playerColor) throws Exception, ResponseException {
        return server.joinGame(authToken, gameID, playerColor);
    }
}
