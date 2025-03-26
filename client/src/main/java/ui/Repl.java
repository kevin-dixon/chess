package ui;

import websocket.NotificationHandler;
import webSocketMessages.Notification;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import static java.awt.Color.*;

public class Repl {
    private ChessClient chessClient;
    private UserClient userClient;
    private GameClient gameClient;
    private String serverUrl;
    private String authToken;

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.chessClient = new ChessClient(serverUrl, new NotificationHandler());
        this.userClient = new UserClient(serverUrl, new NotificationHandler());
        this.gameClient = new GameClient(serverUrl, new NotificationHandler());
    }

    public void run() {
        System.out.println("Type help to get started.");
        System.out.println(chessClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                System.out.print(SET_TEXT_COLOR_MAGENTA + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }

    private void notify(Notification notif) {
        System.out.println(RED + notif.message());
        printPrompt();
    }

    private String evaluate(String in) throws Exception, ResponseException {
        var tokens = in.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (cmd) {
            case "register":
                if (params.length < 3) return "Error: insufficient parameters for register";
                return userClient.register(params[0], params[1], params[2]);

            case "login":
                if (params.length < 2) return "Error: insufficient parameters for login";
                authToken = userClient.login(params[0], params[1]);
                return "Logged in as " + params[0];

            case "logout":
                if (authToken == null) return "Error: not logged in";
                return userClient.logout(authToken);

            case "create":
                if (authToken == null) return "Error: not logged in";
                if (params.length < 1) return "Error: insufficient parameters for create game";
                return gameClient.createGame(authToken, params[0]);

            case "join":
                if (authToken == null) return "Error: not logged in";
                if (params.length < 2) return "Error: insufficient parameters for join game";
                int gameID = Integer.parseInt(params[0]);
                return gameClient.joinGame(authToken, gameID, params[1].toUpperCase());

            case "quit":
                return "quit";

            default:
                return chessClient.help();
        }
    }
}
