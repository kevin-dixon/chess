package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.requests.JoinGameRequest;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;


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
            String authToken = request.headers("authorization");

            if (authToken == null || authToken.isEmpty() || !userService.validAuthToken(authToken)) {
                response.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized - invalid or missing auth token"));
            }

            JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);

            gameService.joinGame(authToken, joinGameRequest.getGameID(), joinGameRequest.getPlayerColor());
            response.status(200);
            return gson.toJson(Map.of("message", "Successfully joined the game"));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Team Color Already Taken")) {
                response.status(403); // Return 403 for team color already taken
            } else {
                response.status(400); // Return 400 for other errors
            }
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
