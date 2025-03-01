package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final AuthDAO auth_dao;
    private final GameDAO game_dao;
    private final UserDAO user_dao;

    public GameService(
            AuthDAO authDataAccess,
            GameDAO gameDataAccess,
            UserDAO userDataAccess
    ) {
        this.auth_dao = authDataAccess;
        this.game_dao = gameDataAccess;
        this.user_dao = userDataAccess;
    }

    public Collection<GameData> getAllGames(){
        return game_dao.listGames();
    }

    public void createGame(){
    }

    public void verifyColor(){}
    public void updateGameData(){}
    public void updateGame(){}


    public void getGame(){}

}
