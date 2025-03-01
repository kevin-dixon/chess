package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import model.requests.JoinGameRequest;

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
        if (getGame(game) != null) {
            //duplicate game
            throw new DataAccessException("Error: Game already exists");
        }
        return game_dao.addGame(game);
    }

    public boolean verifyColor(JoinGameRequest joinGameRequest, GameData game) throws DataAccessException{
        ChessGame.TeamColor newColor = joinGameRequest.playerColor();
        if (newColor == ChessGame.TeamColor.BLACK) {
            return game.blackUsername() == null;

        }
        else {
            return game.whiteUsername() == null;
        }
    }

    public void updateGameData(){}
    public void updateGame(){}

    public GameData getGame(GameData game){
        return game_dao.getGame(game);
    }

}
