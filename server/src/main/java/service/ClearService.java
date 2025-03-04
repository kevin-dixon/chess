package service;

import dataaccess.*;
import dataaccess.DataAccessException;
import spark.Request;

public class ClearService {
    private final AuthDAO auth_dao;
    private final GameDAO game_dao;
    private final UserDAO user_dao;

    public ClearService(
            AuthDAO authDataAccess,
            GameDAO gameDataAccess,
            UserDAO userDataAccess
            ) {
        this.auth_dao = authDataAccess;
        this.game_dao = gameDataAccess;
        this.user_dao = userDataAccess;
    }

    public void clear(Request req) throws DataAccessException {
        deleteAllGames();
        deleteAllAuths();
        deleteAllUsers();
    }

    private void deleteAllAuths() throws DataAccessException {
        auth_dao.deleteAllAuths();
    }
    private void deleteAllGames() throws DataAccessException {
        game_dao.deleteAllGames();
    }
    private void deleteAllUsers() throws DataAccessException {
        user_dao.deleteAllUsers();
    }
}
