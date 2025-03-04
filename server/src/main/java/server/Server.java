package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.handlers.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    AuthDAO authDAO = new AuthDAO();
    GameDAO gameDAO = new GameDAO();
    UserDAO userDAO = new UserDAO();

    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
    GameService gameService = new GameService(gameDAO);
    UserService userService = new UserService(authDAO, userDAO);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);


        Spark.staticFiles.location("web");

        // Register your endpoints
        Spark.delete("/db/clear", new ClearHandler(clearService));
        Spark.post("/user/register", new RegistrationHandler(userService));
        Spark.post("/session/login", new LoginHandler(userService));
        Spark.delete("/session/logout", new LogoutHandler(userService));
        Spark.get("/game/list", new ListGamesHandler(gameService));
        Spark.post("/game/create", new CreateGameHandler(gameService));
        Spark.put("/game/join", new JoinGameHandler(gameService));

        //Handle exceptions
        Spark.exception(Exception.class, (exception, req, res) -> {
            res.status(500);
            res.body("Server Error: " + exception.getMessage());
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
