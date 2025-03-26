package ui;


import model.UserData;
import server.ServerFacade;
import websocket.NotificationHandler;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class ChessClient {
    private String userName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl, NotificationHandler notifyHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notifyHandler;
    }

    public String help() {
        System.out.print(SET_TEXT_COLOR_MAGENTA);
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    public String evaluate(String in) {
        try {
            var tokens = in.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch(cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String[] params) throws ResponseException {
        if (params.length < 3) return "Error: insufficient parameters for register";
        try {
            return server.register(params[0], params[1], params[2]);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String login(String[] params) throws ResponseException {
        if (params.length < 2) return "Error: insufficient parameters for login";
        try {
            String response = server.login(params[0], params[1]);
            userName = params[0];
            state = State.SIGNEDIN;
            return "Logged in as " + userName + "\n" + response;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String logout() throws ResponseException {
        try {
            String response = server.logout(userName);
            userName = null;
            state = State.SIGNEDOUT;
            return "Logged out\n" + response;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length < 1) return "Error: insufficient parameters for create game";
        try {
            return server.createGame(userName, params[0]);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String listGames() throws ResponseException {
        try {
            String response = Arrays.toString(server.listGames(userName));
            return "Games:\n" + response;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String joinGame(String[] params) throws ResponseException {
        if (params.length < 2) return "Error: insufficient parameters for join game";
        try {
            int gameID = Integer.parseInt(params[0]);
            return server.joinGame(userName, gameID, params[1].toUpperCase());
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

}
