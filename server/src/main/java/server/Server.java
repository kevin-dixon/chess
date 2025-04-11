package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.GameSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import server.handlers.*;
import service.*;
import spark.*;

public class Server {

    AuthSqlDAO authDAO = new AuthSqlDAO();
    GameSqlDAO gameDAO = new GameSqlDAO();
    UserSqlDAO userDAO = new UserSqlDAO();

    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
    GameService gameService = new GameService(gameDAO, authDAO);
    UserService userService = new UserService(authDAO, userDAO);

    public Server() {
    }

    public int run(int desiredPort) {

        try {
            // Ensure the database is created before starting the server
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create database: " + e.getMessage(), e);
        }

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        //Register your endpoints
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/user", new RegistrationHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(userService));
        Spark.get("/game", new ListGamesHandler(gameService, userService));
        Spark.post("/game", new CreateGameHandler(gameService, userService));
        Spark.put("/game", new JoinGameHandler(gameService, userService));
        Spark.put("/game", new ObserveGameHandler(gameService, userService));

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
