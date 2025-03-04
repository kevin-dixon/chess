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
            //Parse the JSON request body
            UserData userData = gson.fromJson(request.body(), UserData.class);

            //Validate input
            if (userData.username().isEmpty() || userData.password().isEmpty()) {
                response.status(400);
                response.type("application/json");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: unauthorized");
                return gson.toJson(errorResponse);
            }

            //Authenticate user with password and authToken
            AuthData authData = userService.login(userData);
            response.status(200);
            response.type("application/json");
            return gson.toJson(new UserAuthResponse(userData.username(), authData.authToken()));
        } catch (DataAccessException e) {
            response.type("application/json");
            Map<String, String> errorResponse = new HashMap<>();
            if (e.getMessage().contains("unauthorized")) {
                response.status(401);
                errorResponse.put("message", "Error: unauthorized");
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
