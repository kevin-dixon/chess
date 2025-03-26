package ui;


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
                //add more commands here
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String[] params) throws ResponseException {
        return "implement sign in";
    }

    //add more functions

}
