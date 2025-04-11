package ui;

import server.ServerFacade;
import websocket.NotificationHandler;
import model.GameData;

import java.util.Arrays;
import java.util.List;

public class UserClient {
    private final ServerFacade server;
    private final String userName;
    private final String authToken;
    private final NotificationHandler notificationHandler;
    private List<String> lastListedGames;
    private final String serverUrl;

    public UserClient(String serverUrl, String userName, String authToken, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverUrl); // Create a new ServerFacade instance
        this.userName = userName;
        this.authToken = authToken;
        this.notificationHandler = notificationHandler;
        this.serverUrl = serverUrl;
    }

    public String help() {
        return """
                create <NAME> - create a new game
                list - list all available games
                play <GAME_NUMBER> [WHITE|BLACK] - join a game as a player
                observe <GAME_NUMBER> - observe a game
                logout - log out of your account
                help - display this help text
                """;
    }

    public Object evaluate(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = tokens[0].toLowerCase();
            var params = List.of(tokens).subList(1, tokens.length);

            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String createGame(List<String> params) {
        if (params.isEmpty()) return "Error: insufficient parameters for create game";
        try {
            String gameName = params.get(0);
            server.createGame(authToken, gameName);
            return "Game created successfully.";
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String listGames() {
        try {
            var games = server.listGames(authToken);
            lastListedGames = Arrays.stream(games).map(GameData::toString).toList();
            StringBuilder response = new StringBuilder("Available games:\n");
            for (int i = 0; i < lastListedGames.size(); i++) {
                response.append(i + 1).append(". ").append(lastListedGames.get(i)).append("\n");
            }
            return response.toString();
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object playGame(List<String> params) {
        if (params.size() < 2) return "Error: insufficient parameters for play game";
        try {
            // Validate the game ID format (6 digits)
            String gameId = params.get(0);
            if (!gameId.matches("\\d{6}")) {
                return "Error: invalid game ID. Game IDs must be 6 digits.";
            }

            // Validate the player color
            String color = params.get(1).toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                return "Error: invalid color. Choose either WHITE or BLACK.";
            }

            // Join the game
            server.joinGame(authToken, Integer.parseInt(gameId), color);
            return new GameClient(serverUrl, authToken, gameId, color.equals("BLACK"));
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object observeGame(List<String> params) {
        if (params.isEmpty()) return "Error: insufficient parameters for observe game";
        try {
            // Validate the game ID format (6 digits)
            String gameId = params.get(0);
            if (!gameId.matches("\\d{6}")) {
                return "Error: invalid game ID. Game IDs must be 6 digits.";
            }

            // Call the server to observe the game
            server.observeGame(authToken, Integer.parseInt(gameId));
            return new GameClient(serverUrl, authToken, gameId, false);
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object logout() {
        try {
            server.logout(authToken);
            return new ChessClient(serverUrl, notificationHandler);
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }
}
