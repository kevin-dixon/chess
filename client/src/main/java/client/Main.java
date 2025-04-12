package client;

import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:0";

        if (args.length > 0) {
            serverUrl = args[0];
        }

        System.out.println("Starting Chess Client...");
        new Repl(serverUrl).run();
    }
}
