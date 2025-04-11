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

public class LoginHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            // Parse the JSON request body
            UserData userData = gson.fromJson(request.body(), UserData.class);

            // Validate input
            if (userData.username() == null || userData.password() == null) {
                response.status(400);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: bad request - missing username or password");
                return gson.toJson(errorResponse);
            }

            // Authenticate user
            AuthData authData = userService.login(userData);
            response.status(200);
            response.type("application/json");
            return gson.toJson(new UserAuthResponse(userData.username(), authData.authToken()));
        } catch (DataAccessException e) {
            response.status(401);
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: unauthorized - invalid username or password");
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
