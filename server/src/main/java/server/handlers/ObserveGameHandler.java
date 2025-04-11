package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class ObserveGameHandler implements Route {

    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();

    public ObserveGameHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            // Get authToken from headers
            String authToken = request.headers("authorization");

            // Validate authToken
            if (authToken == null || authToken.isEmpty() || !userService.validAuthToken(authToken)) {
                response.status(401);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: unauthorized");
                return gson.toJson(errorResponse);
            }

            // Parse the gameID from the request body
            Map<String, Object> requestBody = gson.fromJson(request.body(), Map.class);
            int gameID = ((Double) requestBody.get("gameID")).intValue();

            // Check if the game exists
            GameData gameData = gameService.getGameByID(gameID);
            if (gameData == null) {
                response.status(404);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: game not found");
                return gson.toJson(errorResponse);
            }

            // Return success response
            response.status(200);
            response.type("application/json");
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Observing game successfully");
            return gson.toJson(successResponse);
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            return gson.toJson(errorResponse);
        }
    }
}