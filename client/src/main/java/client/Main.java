package client;

import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";

        if (args.length > 0) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}
