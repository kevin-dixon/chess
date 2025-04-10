package ui;


import model.UserData;
import server.ServerFacade;
import websocket.NotificationHandler;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private final NotificationHandler notificationHandler;

    public ChessClient(String serverUrl, NotificationHandler notifyHandler) {
        this.server = new ServerFacade(serverUrl);
        this.notificationHandler = notifyHandler;
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - exit the program
                help - display this help text
                """;
    }

    public Object evaluate(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object register(String[] params) {
        if (params.length < 3) return "Error: insufficient parameters for register";
        try {
            String authToken = server.register(params[0], params[1], params[2]);
            return new UserClient(server.getServerUrl(), params[0], authToken, notificationHandler);
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object login(String[] params) {
        if (params.length < 2) return "Error: insufficient parameters for login";
        try {
            String authToken = server.login(params[0], params[1]);
            return new UserClient(server.getServerUrl(), params[0], authToken, notificationHandler);
        } catch (Exception | ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    /*private String login(String[] params) throws ResponseException {
        if (params.length < 2) return "Error: insufficient parameters for login";
        try {
            String response = server.login(params[0], params[1]);
            userName = params[0];
            state = State.SIGNEDIN;
            return "Logged in as " + userName + "\n" + response;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }*/

}
