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
    private final GameDAO game_dao;

    public GameService(
            GameDAO gameDataAccess
    ) {
        this.game_dao = gameDataAccess;
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

    public void updateGameUser(GameData gameData, ChessGame.TeamColor playerColor, String username){
        //TODO: implement
    }

    public void updateGame(GameData gameData){
        //TODO: implement
    }

    public GameData getGame(GameData game) throws DataAccessException {
        return game_dao.getGame(game);
    }

}
