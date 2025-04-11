package server;
import server.Server;

public class Main {
    public static void main(String[] args) {
        // Default port
        int port = 8080;

        // Allow the port to be passed as a command-line argument
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 8080.");
            }
        }

        System.out.println("Starting Chess Server on port " + port + "...");
        Server server = new Server();
        server.run(port); // Start the server
    }
}