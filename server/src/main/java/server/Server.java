package server;

import server.handlers.RegistrationHandler;
import service.UserService;
import spark.*;

public class Server {
    private final UserService user_service;
    private final RegistrationHandler registration_handler;

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
