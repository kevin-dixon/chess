package server;

import dataaccess.DatabaseManager;
import dataaccess.localmemory.AuthDAO;
import dataaccess.localmemory.GameDAO;
import dataaccess.localmemory.UserDAO;
import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.GameSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import server.handlers.*;
import service.*;
import spark.*;

import javax.xml.crypto.Data;

public class Server {

    //AuthDAO authDAO = new AuthDAO();
    //GameDAO gameDAO = new GameDAO();
    //UserDAO userDAO = new UserDAO();

    AuthSqlDAO authDAO = new AuthSqlDAO();
    GameSqlDAO gameDAO = new GameSqlDAO();
    UserSqlDAO userDAO = new UserSqlDAO();

    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
    GameService gameService = new GameService(gameDAO, authDAO);
    UserService userService = new UserService(authDAO, userDAO);

    public Server() {
    }

    public int run(int desiredPort) {
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
