package ui;

import websocket.NotificationHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private Object activeClient;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.notificationHandler = new NotificationHandler();
        this.activeClient = new ChessClient(serverUrl, notificationHandler); // Initialize ChessClient
    }

    public void run() {
        System.out.println("Welcome to Chess Client!");
        System.out.println("Type 'help' to get started.");
        moveCursorToCommandPrompt(); // Position the cursor for the first command
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

            moveCursorToCommandPrompt(); // Position the cursor for the next command
        }
        System.out.println("Goodbye!");
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

    private String evaluate(String input) throws Exception {
        if (activeClient instanceof ChessClient chessClient) {
            Object result = chessClient.evaluate(input);
            if (result instanceof UserClient userClient) {
                activeClient = userClient; // Switch to UserClient
                return "Logged in successfully.";
            }
            return result.toString();
        } else if (activeClient instanceof UserClient userClient) {
            Object result = userClient.evaluate(input);
            if (result instanceof ChessClient chessClient) {
                activeClient = chessClient; // Switch back to ChessClient
                return "Logged out successfully.";
            } else if (result instanceof GameClient gameClient) {
                activeClient = gameClient; // Switch to GameClient
                return "Joined game successfully.";
            }
            return result.toString();
        } else if (activeClient instanceof GameClient gameClient) {
            Object result = gameClient.evaluate(input);
            if (result instanceof UserClient userClient) {
                activeClient = userClient; // Switch back to UserClient
                return "Returned to the main menu.";
            }
            return result.toString();
        }
        return "Invalid state.";
    }

    private void moveCursorToCommandPrompt() {
        // Move the cursor to the bottom of the screen for typing commands
        System.out.print(moveCursorToLocation(1, 30)); // Adjust the row (30) as needed
    }
}