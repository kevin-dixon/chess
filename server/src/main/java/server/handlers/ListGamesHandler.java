package server.handlers;

import com.google.gson.Gson;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ListGamesHandler implements Route {

    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService, UserService userService) {
        this.userService = userService;
        this.gameService = gameService;
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

            //Get games list
            var gamesList = gameService.listGames(authToken).stream()
                    .map(game -> Map.of(
                            "gameID", game.gameID(),
                            "whiteUsername", game.whiteUsername(),
                            "blackUsername", game.blackUsername(),
                            "gameName", game.gameName()
                    ))
                    .collect(Collectors.toList());

            //Parse games into response
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("games", gamesList);
            response.status(200);
            response.type("application/json");
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
