package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ChessWebSocket {

    private static final Map<Session, String> clients = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        clients.put(session, null);
        System.out.println("Client connected: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received: " + message);
        // Deserialize message and handle commands
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        handleCommand(session, command);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        clients.remove(session);
        System.out.println("Client disconnected: " + session);
    }

    private void handleCommand(Session session, UserGameCommand command) {
        switch (command.getCommandType()) {
            case CONNECT -> sendLoadGame(session, command.getGameID());
            case MAKE_MOVE -> broadcastMove(command);
            case LEAVE -> clients.remove(session);
            case RESIGN -> broadcastResignation(command);
        }
    }

    private void sendLoadGame(Session session, Integer gameID) {
        ChessGame game = new ChessGame(); // Fetch game from database
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, game);
        sendMessage(session, gson.toJson(message));
    }

    private void broadcastMove(UserGameCommand command) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "Player made a move: " + command.getMove(), null);
        broadcastMessage(gson.toJson(message));
    }

    private void broadcastResignation(UserGameCommand command) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "Player resigned", null);
        broadcastMessage(gson.toJson(message));
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        clients.keySet().forEach(session -> sendMessage(session, message));
    }
}