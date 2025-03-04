package server.handlers;

import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {

    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        gameService.create(request);
        return "Clear Handler Response";
    }
}