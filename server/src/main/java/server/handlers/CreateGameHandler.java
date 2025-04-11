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
            // Get authToken
            String authToken = request.headers("authorization");

            // Validate authToken
            if (authToken == null || authToken.isEmpty() || !userService.validAuthToken(authToken)) {
                response.status(401);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: unauthorized - invalid or missing auth token");
                return gson.toJson(errorResponse);
            }

            // Parse the JSON request body
            CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);

            // Create the game
            gameService.createGame(authToken, createGameRequest.getGameName());
            response.status(200);
            response.type("application/json");
            return gson.toJson(Map.of("message", "Game created successfully"));
        } catch (DataAccessException e) {
            response.status(400);
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: bad request - " + e.getMessage());
            return gson.toJson(errorResponse);
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            return gson.toJson(errorResponse);
        }
    }
}