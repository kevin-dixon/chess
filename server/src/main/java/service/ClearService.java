package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

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

    public void deleteAllAuths() throws DataAccessException {
        auth_dao.deleteAllAuths();
    }
    public void deleteAllGames() throws DataAccessException {
        game_dao.deleteAllGames();
    }
    public void deleteAllUsers() throws DataAccessException {
        user_dao.deleteAllUsers();
    }
}
