package websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class ChessWebSocket {

    private static final Map<Session, Integer> SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<Integer, ChessGame> GAMES = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private final GameService gameService = new GameService(new dataaccess.sqldatabase.GameSqlDAO(), new dataaccess.sqldatabase.AuthSqlDAO());

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            handleCommand(session, command);
        } catch (Exception e) {
            sendError(session, "Invalid command format: " + e.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Integer gameID = SESSION_MAP.remove(session);
        if (gameID != null) {
            broadcastNotification(gameID, "A player has disconnected.");
        }
        System.out.println("Client disconnected: " + session);
    }

    private void handleCommand(Session session, UserGameCommand command) {
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case MAKE_MOVE -> handleMakeMove(session, command);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
            default -> sendError(session, "Unknown command type.");
        }
    }

    private void handleConnect(Session session, UserGameCommand command) {
        try {
            if (!gameService.isValidAuthToken(command.getAuthToken())) {
                sendError(session, "Invalid authentication token.");
                return;
            }

            GameData gameData = gameService.getGameByID(command.getGameID());
            if (gameData == null) {
                sendError(session, "Invalid game ID: " + command.getGameID());
                return;
            }

            ChessGame game = gameData.game();
            SESSION_MAP.put(session, command.getGameID());
            GAMES.putIfAbsent(command.getGameID(), game);

            sendLoadGame(session, game);

            SESSION_MAP.forEach((otherSession, gameID) -> {
                if (!otherSession.equals(session) && gameID.equals(command.getGameID())) {
                    sendNotification(otherSession, command.getAuthToken() + " has joined the game.");
                }
            });
        } catch (Exception e) {
            sendError(session, "Failed to connect: " + e.getMessage());
        }
    }

    private void sendNotification(Session session, String message) {
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, message, null);
        sendMessage(session, gson.toJson(notification));
    }

    private void handleMakeMove(Session session, UserGameCommand command) {
        Integer gameID = SESSION_MAP.get(session);
        if (gameID == null || !gameID.equals(command.getGameID())) {
            sendError(session, "Invalid game ID.");
            return;
        }

        ChessGame game = GAMES.get(gameID);

        try {
            // Validate the player's authentication token
            if (!gameService.isValidAuthToken(command.getAuthToken())) {
                sendError(session, "Invalid authentication token.");
                return;
            }
            // Ensure the player is not an observer
            GameData gameData = gameService.getGameByID(gameID);
            String username = gameService.getAuthUsername(command.getAuthToken());
            if (!username.equals(gameData.getWhiteUsername()) && !username.equals(gameData.getBlackUsername())) {
                sendError(session, "Observers cannot make moves.");
                return;
            }
            // Validate that it is the player's turn
            ChessGame.TeamColor currentTurn = game.getTeamTurn();
            ChessGame.TeamColor playerTeam = username.equals(gameData.getWhiteUsername())
                    ? ChessGame.TeamColor.WHITE
                    : ChessGame.TeamColor.BLACK;

            if (currentTurn != playerTeam) {
                sendError(session, "It is not your turn.");
                return;
            }
            // Validate that the player is moving their own pieces
            ChessPosition start = command.getMove().getStartPosition();
            ChessPiece piece = game.getBoard().getPiece(start);
            if (piece == null || piece.getTeamColor() != game.getTeamTurn()) {
                sendError(session, "You cannot move this piece.");
                return;
            }

            game.makeMove(command.getMove());
            broadcastLoadGame(gameID, game);

            SESSION_MAP.forEach((otherSession, id) -> {
                if (!otherSession.equals(session) && id.equals(gameID)) {
                    sendNotification(otherSession, command.getAuthToken() + " made a move: " + command.getMove());
                }
            });
        } catch (Exception e) {
            sendError(session, "Invalid move: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, UserGameCommand command) {
        Integer gameID = SESSION_MAP.remove(session);
        if (gameID != null) {
            String username = command.getAuthToken();

            try {
                gameService.leaveGame(command.getAuthToken(), gameID);
                broadcastNotification(gameID, username + " has left the game.");
            } catch (Exception e) {
                sendError(session, "Failed to leave game: " + e.getMessage());
            }
        }
    }

    private void handleResign(Session session, UserGameCommand command) {
        Integer gameID = SESSION_MAP.get(session);
        if (gameID == null || !gameID.equals(command.getGameID())) {
            sendError(session, "Invalid game ID.");
            return;
        }

        try {
            // Validate the player
            if (!gameService.isValidAuthToken(command.getAuthToken())) {
                sendError(session, "Invalid authentication token.");
                return;
            }
            GameData gameData = gameService.getGameByID(gameID);
            String username = gameService.getAuthUsername(command.getAuthToken());
            if (!username.equals(gameData.getWhiteUsername()) && !username.equals(gameData.getBlackUsername())) {
                sendError(session, "Observers cannot resign from the game.");
                return;
            }

            // Check if the game is already over
            if (!GAMES.containsKey(gameID)) {
                sendError(session, "The game is already over.");
                return;
            }

            broadcastNotification(gameID, username + " has resigned. The game is over.");
            GAMES.remove(gameID);
        } catch (Exception e) {
            sendError(session, "Failed to process resign command: " + e.getMessage());
        }
    }

    private void sendLoadGame(Session session, ChessGame game) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);
        sendMessage(session, gson.toJson(message));
    }

    private void broadcastLoadGame(Integer gameID, ChessGame game) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null, game);
        broadcastMessage(gameID, gson.toJson(message));
    }

    private void broadcastNotification(Integer gameID, String notification) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, null, notification, null);
        broadcastMessage(gameID, gson.toJson(message));
    }

    private void sendError(Session session, String errorMessage) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage, null, null);
        sendMessage(session, gson.toJson(message));
    }

    private void sendMessage(Session session, String message) {
        try {
            System.out.println("Sending message: " + message);
            session.getRemote().sendString(message);
        } catch (Exception e) {
            System.err.println("Failed to send message to session: " + e.getMessage());
        }
    }

    private void broadcastMessage(Integer gameID, String message) {
        SESSION_MAP.forEach((session, id) -> {
            if (id.equals(gameID)) {
                sendMessage(session, message);
            }
        });
    }
}