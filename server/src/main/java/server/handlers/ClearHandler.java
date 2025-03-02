package server.handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        return "\nImplement Clear Handling";
    }
}
