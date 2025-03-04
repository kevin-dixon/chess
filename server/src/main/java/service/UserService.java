package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import spark.Request;

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

    public void register(Request req) {
        //get user
            //if null create new user
            //create auth for user
        //else return user already exists error
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

    public UserData getUser(String username) throws DataAccessException {
        return user_dao.getUser(username);
    }

    public UserData createUser(UserData userData) throws DataAccessException {
        return user_dao.addUser(userData);
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
