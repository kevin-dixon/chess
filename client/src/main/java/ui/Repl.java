package ui;

import websocket.NotificationHandler;
import webSocketMessages.Notification;

import java.util.Scanner;
import static ui.EscapeSequences.*;
import static java.awt.Color.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        this.client = new ChessClient(serverUrl, new NotificationHandler());
    }

    public void run() {
        System.out.println("Type help to get started.");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.evaluate(line);
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
}
