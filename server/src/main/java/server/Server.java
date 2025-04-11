package server;

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
        int port = desiredPort == 0 ? 0 : desiredPort; // Use 0 to let Spark select a random available port
        try {
            DatabaseManager.createDatabase();
            Spark.port(port);
            Spark.staticFiles.location("web");

            // Register endpoints
            Spark.delete("/db", new ClearHandler(clearService));
            Spark.post("/user", new RegistrationHandler(userService));
            Spark.post("/session", new LoginHandler(userService));
            Spark.delete("/session", new LogoutHandler(userService));
            Spark.get("/game", new ListGamesHandler(gameService, userService));
            Spark.post("/game", new CreateGameHandler(gameService, userService));
            Spark.put("/game", new JoinGameHandler(gameService, userService));
            Spark.post("/game/observe", new ObserveGameHandler(gameService, userService));
            Spark.post("/game/leave", new LeaveGameHandler(gameService, userService));

            Spark.init();
            Spark.awaitInitialization();
            return Spark.port(); // Return the actual port being used
        } catch (Exception e) {
            throw new RuntimeException("Failed to start server: " + e.getMessage(), e);
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}