package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import spark.Request;

import java.util.UUID;

public class UserService {
    private final AuthDAO auth_dao;
    private final UserDAO user_dao;

    public UserService(
            AuthDAO authDataAccess,
            UserDAO userDataAccess
    ) {
        this.auth_dao = authDataAccess;
        this.user_dao = userDataAccess;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        // Check if user already exists
        if (user_dao.getUser(userData.username()) != null) {
            throw new DataAccessException("already taken");
        }

        // Create new user
        user_dao.addUser(userData);

        // Generate auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        auth_dao.addAuth(authData);

        return authData;
    }

    public void login(Request req) {
        //get user
            //if null error username not found
        //else
            //check password
            //create auth for user
    }
    public void logout(Request req) {
        //delete auth
    }

    public AuthData createAuth(AuthData authData) throws DataAccessException {
        return auth_dao.addAuth(authData);
    }

    public void checkPassword(){}

    public void deleteAuth(String authToken) throws DataAccessException {
        auth_dao.deleteAuth(authToken);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return auth_dao.getAuth(authToken);
    }
}
