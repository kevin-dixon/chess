package server.handlers;

import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegistrationHandler implements Route {

    private final UserService userService;

    public RegistrationHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        userService.register(request);
        return "Registration Handler Response";
    }
}
