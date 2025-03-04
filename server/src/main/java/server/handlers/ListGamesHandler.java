package server.handlers;

import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {

    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        gameService.list(request);
        return "Clear Handler Response";
    }
}
