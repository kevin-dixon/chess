package server;

import server.handlers.*;
import spark.*;

public class Server {

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.delete("/clear", new ClearHandler());


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
