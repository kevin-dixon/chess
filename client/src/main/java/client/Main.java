package client;

import ui.Repl;

public class Main {
    public static void main(String[] args) {
        // Default server URL
        var serverUrl = "http://localhost:8080";

        // Allow the server URL to be passed as a command-line argument
        if (args.length > 0) {
            serverUrl = args[0];
        }

        System.out.println("Starting Chess Client...");
        new Repl(serverUrl).run();
    }
}
