package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class JoinGameHandler implements Route {

    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            //Get authToken
            String authToken = request.headers("authorization");

            //Validate authToken
            if (authToken == null || authToken.isEmpty() || !userService.validAuthToken(authToken)) {
                response.status(401);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: unauthorized");
                return gson.toJson(errorResponse);
            }

            // Parse the JSON request body
            Map<String, Object> requestBody = gson.fromJson(request.body(), Map.class);
            String playerColor = (String) requestBody.get("playerColor");
            Integer gameID = requestBody.get("gameID") != null ? ((Double) requestBody.get("gameID")).intValue() : null;

            //Validate input
            if (playerColor == null || playerColor.isEmpty() || gameID == null) {
                response.status(400);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: bad request");
                return gson.toJson(errorResponse);
            }

            //Join the game
            gameService.joinGame(authToken, gameID, playerColor);
            response.status(200);
            response.type("application/json");
            return "{}";
        } catch (DataAccessException e) {
            String message = e.getMessage();
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            if ("unauthorized".equals(message)) {
                response.status(401);
                errorResponse.put("message", "Error: unauthorized");
            } else if ("already taken".equals(message)) {
                response.status(403);
                errorResponse.put("message", "Error: already taken");
            } else {
                response.status(500);
                errorResponse.put("message", "Error: " + message);
            }
            return gson.toJson(errorResponse);
        }
    }
}
