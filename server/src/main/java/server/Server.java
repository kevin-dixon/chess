package server;

import dataaccess.*;
import server.handlers.*;
import service.*;
import spark.*;

public class Server {

    AuthDAO authDAO = new AuthDAO();
    GameDAO gameDAO = new GameDAO();
    UserDAO userDAO = new UserDAO();

    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
    GameService gameService = new GameService(gameDAO, authDAO);
    UserService userService = new UserService(authDAO, userDAO);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/user", new RegistrationHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(userService));
        Spark.get("/game", new ListGamesHandler(gameService));
        Spark.post("/game", new CreateGameHandler(gameService));
        Spark.put("/game", new JoinGameHandler(gameService, userService));

        //Handle exceptions
        Spark.exception(Exception.class, (exception, req, res) -> {
            res.status(500);
            res.type("application/json");
            res.body("{\"message\": \"Error: " + exception.getMessage() + "\"}");
            exception.printStackTrace();
        });

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
