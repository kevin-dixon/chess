package service;

import dataaccess.DataAccessException;
import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class UserService {

    private final AuthSqlDAO authDao;
    private final UserSqlDAO userDao;

    public UserService(
            AuthSqlDAO authDataAccess,
            UserSqlDAO userDataAccess
    ) {
        this.authDao = authDataAccess;
        this.userDao = userDataAccess;
    }

    public AuthData register(UserData userData) throws DataAccessException, SQLException {
        // Check if user already exists
        if (userDao.getUser(userData.username()) != null) {
            throw new DataAccessException("already taken");
        }

        // Create new user
        userDao.addUser(userData);

        // Generate auth token
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDao.addAuth(authData);

        return authData;
    }

    public AuthData login(UserData userData) throws DataAccessException, SQLException {
        UserData existingUser = userDao.getUser(userData.username());
        if (existingUser == null || !userDao.verifyUser(userData.username(), userData.password())) {
            throw new DataAccessException("unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDao.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException, SQLException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        authDao.deleteAuth(authToken);
    }

    public boolean validAuthToken(String authToken) {
        try {
            return authDao.getAuth(authToken) != null;
        } catch (DataAccessException | SQLException e) {
            return false;
        }
    }

}
