package server.handlers;

import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        userService.logout(request);
        return "Clear Handler Response";
    }
}
