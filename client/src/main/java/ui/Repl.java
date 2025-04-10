package ui;

import websocket.NotificationHandler;
import webSocketMessages.Notification;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import static java.awt.Color.*;

public class Repl {
    private Object activeClient;
    private UserClient userClient;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.notificationHandler = new NotificationHandler();
        this.activeClient = new ChessClient(serverUrl, notificationHandler);
    }

    public void run() {
        System.out.println("Type help to get started.");
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                System.out.println(SET_TEXT_COLOR_MAGENTA + result + RESET_TEXT_COLOR);
            } catch (Throwable e) {
                System.out.println(SET_TEXT_COLOR_RED + "Error: " + e.getMessage() + RESET_TEXT_COLOR);
            }
        }
        System.out.println("Goodbye!");
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

    private void notify(Notification notif) {
        System.out.println(RED + notif.message());
        printPrompt();
    }

    private String evaluate(String input) throws Exception {
        if (activeClient instanceof ChessClient chessClient) {
            Object result = chessClient.evaluate(input);
            if (result instanceof UserClient) {

                userClient = (UserClient) result;
                activeClient = result;

                return "Logged in successfully.";
            }
            return result.toString();
        } else if (activeClient instanceof UserClient userClient) {
            Object result = userClient.evaluate(input);
            if (result instanceof ChessClient) {
                activeClient = result;
                this.userClient = null;

                return "Logged out successfully.";
            } else if (result instanceof GameClient) {
                activeClient = result;

                return "Joined game successfully.";
            }
            return result.toString();
        } else if (activeClient instanceof GameClient gameClient) {
            Object result = gameClient.evaluate(input);
            if (result.equals("Exiting game.")) {

                activeClient = userClient;
                return (String) result;
            }
            return result.toString();
        }
        return "Invalid state.";
    }
}
