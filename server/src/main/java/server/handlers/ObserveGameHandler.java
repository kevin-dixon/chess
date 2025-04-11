package server.handlers;

import com.google.gson.Gson;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class ObserveGameHandler implements Route {

    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ObserveGameHandler(GameService gameService, UserService userService) {
        this.userService = userService;
        this.gameService = gameService;
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
            var observeGameRequest = gson.fromJson(request.body(), model.requests.ObserveGameRequest.class);

            // Validate gameID
            if (!gameService.isValidGame(observeGameRequest.getGameID())) {
                response.status(404);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: game not found");
                return gson.toJson(errorResponse);
            }

            // Return example board for now
            String exampleBoard = """
                    8  ♜  ♞  ♝  ♛  ♚  ♝  ♞  ♜
                    7  ♟  ♟  ♟  ♟  ♟  ♟  ♟  ♟
                    6                        
                    5                        
                    4                        
                    3                        
                    2  ♙  ♙  ♙  ♙  ♙  ♙  ♙  ♙
                    1  ♖  ♘  ♗  ♕  ♔  ♗  ♘  ♖
                       a  b  c  d  e  f  g  h
                    """;

            response.status(200);
            response.type("application/json");
            return gson.toJson(Map.of("board", exampleBoard));
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            return gson.toJson(errorResponse);
        }
    }
}