package server;

import server.handlers.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    /** Server Services **/
    private final UserService user_service;
    private final GameService game_service;
    private final ClearService clear_service;

    /** Server Handlers **/
    private final ClearHandler clear_handler;
    private final CreateGameHandler creategame_handler;
    private final JoinGameHandler joingame_handler;
    private final ListGamesHandler listgames_handler;
    private final LoginHandler login_handler;
    private final LogoutHandler logout_handler;
    private final RegistrationHandler registration_handler;

    public Server(UserService userService, GameService gameService, ClearService clearService) {
        // Services
        this.user_service = userService;
        this.game_service = gameService;
        this.clear_service = clearService;

        // Handlers
        clear_handler = new ClearHandler();
        creategame_handler = new CreateGameHandler();
        joingame_handler = new JoinGameHandler();
        listgames_handler = new ListGamesHandler();
        login_handler = new LoginHandler();
        logout_handler = new LogoutHandler();
        registration_handler = new RegistrationHandler();
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
