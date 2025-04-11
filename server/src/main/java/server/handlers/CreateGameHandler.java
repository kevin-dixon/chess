package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.requests.CreateGameRequest;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class CreateGameHandler implements Route {

    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("authorization");

            if (authToken == null || authToken.isEmpty() || !userService.validAuthToken(authToken)) {
                response.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized - invalid or missing auth token"));
            }

            CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
            int gameID = gameService.createGame(authToken, createGameRequest.getGameName());

            response.status(200);
            return gson.toJson(Map.of("gameID", gameID));
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}