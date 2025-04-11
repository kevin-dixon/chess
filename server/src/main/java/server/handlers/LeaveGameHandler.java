package server.handlers;

import com.google.gson.Gson;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class LeaveGameHandler implements Route {

    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();

    public LeaveGameHandler(GameService gameService, UserService userService) {
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

            // Remove the player from the game
            gameService.removePlayerFromGame(gameID, userService.getUsernameFromAuthToken(authToken));

            // Return success response
            response.status(200);
            response.type("application/json");
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Player removed from game successfully.");
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
