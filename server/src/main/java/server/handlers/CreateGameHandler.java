package server.handlers;

import com.google.gson.Gson;
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
            //Get the authToken
            String authToken = request.headers("authorization");

            //Validate authToken
            if (authToken == null || authToken.isEmpty() || !userService.validAuthToken(authToken)) {
                response.status(401);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: bad request");
                return gson.toJson(errorResponse);
            }

            //Parse the JSON request body
            Map<String, String> requestBody = gson.fromJson(request.body(), Map.class);
            String gameName = requestBody.get("gameName");

            //Validate input
            if (gameName == null || gameName.isEmpty()) {
                response.status(400);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: bad request");
                return gson.toJson(errorResponse);
            }

            //Create the game
            int gameID = gameService.createGame(authToken, gameName);
            response.status(200);
            response.type("application/json");
            Map<String, Integer> successResponse = new HashMap<>();
            successResponse.put("gameID", gameID);
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