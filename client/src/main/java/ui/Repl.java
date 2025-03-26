package ui;

import websocket.NotificationHandler;

import java.util.Scanner;

import static java.awt.Color.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        this.client = new ChessClient(serverUrl, new NotificationHandler());
    }

    public void run() {
        System.out.println("Welcome...");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.evaluate(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        //TODO: implement
    }
}
