package service;

import dataaccess.DataAccessException;
import dataaccess.sqldatabase.*;
import spark.Request;

import java.sql.SQLException;

public class ClearService {
    private final AuthSqlDAO authDao;
    private final GameSqlDAO gameDao;
    private final UserSqlDAO userDao;

    public ClearService(
            AuthSqlDAO authDataAccess,
            GameSqlDAO gameDataAccess,
            UserSqlDAO userDataAccess
            ) {
        this.authDao = authDataAccess;
        this.gameDao = gameDataAccess;
        this.userDao = userDataAccess;
    }

    public void clear(Request req) throws DataAccessException, SQLException {
        deleteAllGames();
        deleteAllAuths();
        deleteAllUsers();
    }

    private void deleteAllAuths() throws DataAccessException, SQLException {
        authDao.deleteAllAuths();
    }
    private void deleteAllGames() throws DataAccessException, SQLException {
        gameDao.deleteAllGames();
    }
    private void deleteAllUsers() throws DataAccessException, SQLException {
        userDao.deleteAllUsers();
    }
}
