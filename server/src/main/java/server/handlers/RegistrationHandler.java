package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import model.responses.UserAuthResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class RegistrationHandler implements Route {

    private final UserService userService;
    private final Gson gson = new Gson();

    public RegistrationHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        try {
            // Parse the JSON request body
            UserData userData = gson.fromJson(request.body(), UserData.class);

            // Validate input
            if (userData.password() == null || userData.username() == null || userData.email() == null) {
                response.status(400);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: bad request - missing required fields");
                return gson.toJson(errorResponse);
            } else if (userData.username().isEmpty() || userData.password().isEmpty() || userData.email().isEmpty()) {
                response.status(400);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: bad request - empty fields");
                return gson.toJson(errorResponse);
            }

            // Register user
            AuthData authData = userService.register(userData);
            response.status(200);
            response.type("application/json");
            return gson.toJson(new UserAuthResponse(userData.username(), authData.authToken()));
        } catch (DataAccessException e) {
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            if (e.getMessage().contains("already taken")) {
                response.status(403);
                errorResponse.put("message", "Error: username already taken");
            } else {
                response.status(500);
                errorResponse.put("message", "Error: " + e.getMessage());
            }
            return gson.toJson(errorResponse);
        } catch (Exception e) {
            response.status(500);
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            return gson.toJson(errorResponse);
        }
    }
}
