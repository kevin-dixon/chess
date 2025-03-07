package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final AuthDAO authDao;
    private final UserDAO userDao;

    public UserService(
            AuthDAO authDataAccess,
            UserDAO userDataAccess
    ) {
        this.authDao = authDataAccess;
        this.userDao = userDataAccess;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        //Check if user already exists
        if (userDao.getUser(userData.username()) != null) {
            throw new DataAccessException("already taken");
        }

        //Create new user
        userDao.addUser(userData);

        //Generate auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDao.addAuth(authData);

        return authData;
    }

    public AuthData login(UserData userData) throws DataAccessException {
        //Check if user exists or wrong password
        UserData existingUser = userDao.getUser(userData.username());
        if (existingUser == null || !existingUser.password().equals(userData.password())) {
            throw new DataAccessException("unauthorized");
        }

        //Generate auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDao.addAuth(authData);

        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        authDao.deleteAuth(authToken);
    }

    public boolean validAuthToken(String authToken) {
        AuthData authData = authDao.getAuth(authToken);
        return authData != null;
    }

}
