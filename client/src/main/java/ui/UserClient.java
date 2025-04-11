package ui;

import server.ServerFacade;
import websocket.NotificationHandler;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserClient {
    private final ServerFacade server;
    private final String userName;
    private final String authToken;
    private final NotificationHandler notificationHandler;
    private final String serverUrl;

    // Keeps track of the last listed games and their indices
    private List<GameData> lastListedGames;


    public UserClient(String serverUrl, String userName, String authToken, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverUrl); // Create a new ServerFacade instance
        this.userName = userName;
        this.authToken = authToken;
        this.notificationHandler = notificationHandler;
        this.serverUrl = serverUrl;
        this.lastListedGames = new ArrayList<>();
    }

    public String help() {
        return """
                create <NAME> - a new game
                list - all available games
                play <GAME_NUMBER> [WHITE|BLACK] - a game as a player
                observe <GAME_NUMBER> - a game
                logout - of your account
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
            // Fetch games from the server
            var games = server.listGames(authToken);

            // Update the lastListedGames with the fetched games
            lastListedGames = List.of(games);

            // Build the response string
            StringBuilder response = new StringBuilder("Available games:\n");
            for (int i = 0; i < lastListedGames.size(); i++) {
                GameData game = lastListedGames.get(i);
                response.append(i + 1).append(". ")
                        .append("Name: ").append(game.gameName()).append(", ")
                        .append("White: ").append(game.whiteUsername() != null ? game.whiteUsername() : "Open").append(", ")
                        .append("Black: ").append(game.blackUsername() != null ? game.blackUsername() : "Open")
                        .append("\n");
            }
            return response.toString();
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object playGame(List<String> params) {
        if (params.size() < 2) return "Error: insufficient parameters for play game";
        try {
            // Validate the game index
            int gameIndex = Integer.parseInt(params.get(0)) - 1;
            if (gameIndex < 0 || gameIndex >= lastListedGames.size()) {
                return "Error: invalid game number.";
            }

            // Validate the player color
            String color = params.get(1).toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                return "Error: invalid color. Choose either WHITE or BLACK.";
            }

            // Get the game ID from the lastListedGames
            int gameId = lastListedGames.get(gameIndex).gameID();

            // Join the game
            server.joinGame(authToken, gameId, color);
            return new GameClient(serverUrl, authToken, String.valueOf(gameId), color.equals("BLACK"));
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object observeGame(List<String> params) {
        if (params.isEmpty()) return "Error: insufficient parameters for observe game";
        try {
            // Validate the game index
            int gameIndex = Integer.parseInt(params.get(0)) - 1;
            if (gameIndex < 0 || gameIndex >= lastListedGames.size()) {
                return "Error: invalid game number.";
            }

            // Get the game ID from the lastListedGames
            int gameId = lastListedGames.get(gameIndex).gameID();

            // Call the server to observe the game
            server.observeGame(authToken, gameId);
            return new GameClient(serverUrl, authToken, String.valueOf(gameId), false);
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
