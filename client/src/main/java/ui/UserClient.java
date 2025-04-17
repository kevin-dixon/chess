package ui;

import server.ServerFacade;
import websocket.NotificationHandler;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class UserClient {
    private final ServerFacade server;
    private final String authToken;
    private final NotificationHandler notificationHandler;
    private final String serverUrl;

    private List<GameData> cachedGameList; // Cached list of games

    public UserClient(String serverUrl,
                      String userName,
                      String authToken,
                      NotificationHandler notificationHandler,
                      List<GameData> cachedGameList) throws Exception {
        this.server = new ServerFacade(serverUrl);
        this.authToken = authToken;
        this.notificationHandler = notificationHandler;
        this.serverUrl = serverUrl;
        this.cachedGameList = cachedGameList != null ? cachedGameList : new ArrayList<>();
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
            return e.getMessage();
        }
    }

    private String createGame(List<String> params) {
        if (params.isEmpty()) {
            return "Error: insufficient parameters for create game";
        }
        try {
            String gameName = params.get(0);
            server.createGame(authToken, gameName);
            return "Game created successfully.";
        } catch (Exception | ResponseException e) {
            return e.getMessage();
        }
    }

    private String listGames() {
        try {
            // Fetch games from the server
            var games = server.listGames(authToken);

            // Update the cached game list
            cachedGameList = List.of(games);

            // Build the response string
            StringBuilder response = new StringBuilder("Available games:\n");
            for (int i = 0; i < cachedGameList.size(); i++) {
                GameData game = cachedGameList.get(i);
                response.append(i + 1).append(". ").append(game.gameName())
                        .append(" (White: ").append(game.getWhiteUsername() == null
                                ? "Open" : game.getWhiteUsername())
                        .append(", Black: ").append(game.getBlackUsername() == null
                                ? "Open" : game.getBlackUsername())
                        .append(")\n");
            }
            return response.toString();
        } catch (Exception | ResponseException e) {
            return e.getMessage();
        }
    }

    private Object playGame(List<String> params) {
        if (params.size() < 2) {
            return "Error: insufficient parameters for play game";
        }
        try {
            // Validate the game index
            int gameIndex = Integer.parseInt(params.get(0)) - 1;
            if (gameIndex < 0 || gameIndex >= cachedGameList.size()) {
                return "Error: invalid game number";
            }

            // Validate the player color
            String color = params.get(1).toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                return "Error: invalid player color (must be WHITE or BLACK)";
            }

            // Get the game ID from the cached game list
            int gameId = cachedGameList.get(gameIndex).gameID();

            // Join the game
            server.joinGame(authToken, gameId, color);
            return new GameClient(serverUrl,
                    authToken,
                    String.valueOf(gameId),
                    color.equals("BLACK"),
                    cachedGameList);
        } catch (NumberFormatException e) {
            return "Error: game number must be an integer";
        } catch (Exception | ResponseException e) {
            return e.getMessage();
        }
    }

    private Object observeGame(List<String> params) {
        if (params.isEmpty()) {
            return "Error: insufficient parameters for observe game";
        }
        try {
            // Validate the game index
            int gameIndex = Integer.parseInt(params.get(0)) - 1;
            if (gameIndex < 0 || gameIndex >= cachedGameList.size()) {
                return "Error: invalid game number";
            }

            // Get the game ID from the cached game list
            int gameId = cachedGameList.get(gameIndex).gameID();

            // Observe the game
            server.observeGame(authToken, gameId);
            return new GameClient(serverUrl,
                    authToken,
                    String.valueOf(gameId),
                    false,
                    cachedGameList);
        } catch (NumberFormatException e) {
            return "Error: game number must be an integer";
        } catch (Exception | ResponseException e) {
            return e.getMessage();
        }
    }

    private Object logout() {
        try {
            server.logout(authToken);
            return new ChessClient(serverUrl, notificationHandler);
        } catch (Exception | ResponseException e) {
            return e.getMessage();
        }
    }
}