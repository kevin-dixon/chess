package service;

import dataaccess.DataAccessException;
import dataaccess.localmemory.AuthDAO;
import dataaccess.localmemory.GameDAO;
import dataaccess.localmemory.UserDAO;
import spark.Request;

public class ClearService {
    private final AuthDAO authDao;
    private final GameDAO gameDao;
    private final UserDAO userDao;

    public ClearService(
            AuthDAO authDataAccess,
            GameDAO gameDataAccess,
            UserDAO userDataAccess
            ) {
        this.authDao = authDataAccess;
        this.gameDao = gameDataAccess;
        this.userDao = userDataAccess;
    }

    public void clear(Request req) throws DataAccessException {
        deleteAllGames();
        deleteAllAuths();
        deleteAllUsers();
    }

    private void deleteAllAuths() throws DataAccessException {
        authDao.deleteAllAuths();
    }
    private void deleteAllGames() throws DataAccessException {
        gameDao.deleteAllGames();
    }
    private void deleteAllUsers() throws DataAccessException {
        userDao.deleteAllUsers();
    }
}
