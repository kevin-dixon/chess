package ui;

import model.GameData;
import server.ServerFacade;
import websocket.NotificationHandler;

public class GameClient {
    private final ServerFacade server;
    private final String authToken;
    private final String gameId;
    private final boolean isBlackPerspective;

    public GameClient(String serverUrl, String authToken, String gameId, boolean isBlackPerspective) {
        this.server = new ServerFacade(serverUrl); // Create a new ServerFacade instance
        this.authToken = authToken;
        this.gameId = gameId;
        this.isBlackPerspective = isBlackPerspective;
    }

    public String drawChessBoard() {
        return isBlackPerspective ? "Drawing board from black's perspective." : "Drawing board from white's perspective.";
    }

    public String help() {
        return """
                draw - show example board
                exit - exit the game
                help - display this help text
                """;
    }

    public Object evaluate(String input) {
        return switch (input.toLowerCase()) {
            case "draw" -> drawChessBoard();
            case "exit" -> "Exiting game.";
            default -> help();
        };
    }
}
