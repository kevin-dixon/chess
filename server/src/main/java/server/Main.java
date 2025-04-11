package server;

public class Main {
    public static void main(String[] args) {
        // Default port
        int port = 0; // Use 0 to let the server select a random available port

        // Allow the port to be passed as a command-line argument
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using a random available port.");
            }
        }

        System.out.println("Starting Chess Server...");
        Server server = new Server();
        int actualPort = server.run(port); // Start the server and get the actual port
        System.out.println("Server running on port " + actualPort);
    }
}