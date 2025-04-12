package server;

public class Main {
    public static void main(String[] args) {
        int port = 0;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using a random available port.");
            }
        }

        System.out.println("Starting Chess Server...");
        Server server = new Server();
        int actualPort = server.run(port);
        System.out.println("Server running on port " + actualPort);
    }
}