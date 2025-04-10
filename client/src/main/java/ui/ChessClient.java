package ui;

import com.google.gson.Gson;
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
            String username = params[0];
            String password = params[1];
            String email = params[2];

            // Use the simplified ServerFacade register method
            String authToken = server.register(username, password, email);
            return new UserClient(server.getServerUrl(), username, authToken, notificationHandler);
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private Object login(String[] params) {
        if (params.length < 2) return "Error: insufficient parameters for login";
        try {
            String username = params[0];
            String password = params[1];

            // Use the simplified ServerFacade login method
            String authToken = server.login(username, password);
            return new UserClient(server.getServerUrl(), username, authToken, notificationHandler);
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

}
