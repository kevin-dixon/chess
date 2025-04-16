package ui;

import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.ServerMessageObserver;
import websocket.WebSocketCommunicator;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private final NotificationHandler notificationHandler;
    private final WebSocketCommunicator webSocketCommunicator;

    public ChessClient(String serverUrl, NotificationHandler notifyHandler) throws Exception {
        this.server = new ServerFacade(serverUrl);
        this.notificationHandler = notifyHandler;
        this.webSocketCommunicator = new WebSocketCommunicator(serverUrl + "/ws");
        this.webSocketCommunicator.addObserver(this);
    }

    @Override
    public void onServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> System.out.println("Game loaded: " + message.getGame());
            case NOTIFICATION -> System.out.println("Notification: " + message.getMessage());
            case ERROR -> System.err.println("Error: " + message.getMessage());
        }
    }

    public void sendCommand(Object command) {
        webSocketCommunicator.sendCommand((UserGameCommand) command);
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
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private Object register(String[] params) {
        if (params.length < 3) {
            return "Error: insufficient parameters for register";
        }
        try {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            // Use the simplified ServerFacade register method
            String authToken = server.register(username, password, email);
            return new UserClient(server.getServerUrl(), username, authToken, notificationHandler, new ArrayList<>());
        } catch (ResponseException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object login(String[] params) {
        if (params.length < 2) {
            return "Error: insufficient parameters for login";
        }
        try {
            String username = params[0];
            String password = params[1];

            // Use the simplified ServerFacade login method
            String authToken = server.login(username, password);
            return new UserClient(server.getServerUrl(), username, authToken, notificationHandler, new ArrayList<>());
        } catch (ResponseException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String quit() {
        System.out.println("Exiting...\nGoodbye!");
        System.exit(0);
        return "";
    }

}
