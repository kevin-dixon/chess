package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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

    public Collection<GameData> getAllGames() throws DataAccessException {
        return game_dao.listGames();
    }

    public GameData createGame(GameData game) throws DataAccessException{
        //check for game name or id duplicates
        String newGameName = game.gameName();
        int newGameID = game.gameID();
        if (getGame(newGameID) != null || getGame(newGameName) != null) {
            //duplicate game
            throw new DataAccessException("Error: Game already exists");
        }
        return game_dao.addGame(game);
    }

    public void verifyColor(){}
    public void updateGameData(){}
    public void updateGame(){}


    public GameData getGame(String gameName){
        //TODO: implement
        return null;
    }

    public GameData getGame(int gameID){
        //TODO: implement
        return null;
    }

}
