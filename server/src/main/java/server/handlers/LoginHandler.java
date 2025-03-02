package server.handlers;

import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        return "\nImplement Login Handling";
    }
}
