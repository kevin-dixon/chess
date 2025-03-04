package server.handlers;

import com.google.gson.Gson;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class LogoutHandler implements Route {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            //Get the authToken
            String authToken = request.headers("authorization");

            //Validate input
            if (authToken == null || authToken.isEmpty()) {
                response.status(401);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: unauthorized");
                return gson.toJson(errorResponse);
            }

            //Logout user
            userService.logout(authToken);
            response.status(200);
            response.type("application/json");
            return "{}";
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            return gson.toJson(errorResponse);
        }
    }
}
