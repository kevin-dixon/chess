package server.handlers;

import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {

    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        clearService.clear(request);
        return "Clear Handler Response";
    }
}
