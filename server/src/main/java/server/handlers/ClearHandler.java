package server.handlers;

import com.google.gson.Gson;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            clearService.clear(request);
            response.status(200);
            response.type("application/json");
            return "{}";
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");
            return gson.toJson(new StandardResponse(StatusResponse.ERROR, "Error: " + e.getMessage()));
        }
    }
}
