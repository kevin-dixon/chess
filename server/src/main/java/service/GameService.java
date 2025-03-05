package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private final GameDAO game_dao;
    private final AuthDAO auth_dao;

    public GameService(GameDAO gameDataAccess, AuthDAO authDataAccess) {
        this.game_dao = gameDataAccess;
        this.auth_dao = authDataAccess;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        return game_dao.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        //Validate authToken
        AuthData authData = auth_dao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        //Create new game
        GameData newGame = new GameData(
                newGameID(),
                null,
                null,
                new ChessGame(),
                gameName
        );
        //Add new game to database
        game_dao.addGame(newGame);
        return newGame.gameID();
    }

    private int newGameID() {
        Random random = new Random();
        //System.out.println(randomNumber);
        return 100000 + random.nextInt(900000);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        // Validate authToken
        AuthData authData = auth_dao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        // Validate game existence
        GameData gameData = game_dao.getGameByID(gameID);
        if (gameData == null) {
            throw new DataAccessException("bad request");
        }

        // Check if the color is already taken
        if (("WHITE".equals(playerColor) && !gameData.whiteUsername().isEmpty()) ||
                ("BLACK".equals(playerColor) && !gameData.blackUsername().isEmpty())) {
            throw new DataAccessException("already taken");
        }

        // Add player to the game
        game_dao.addPlayerToGame(gameID, playerColor, authData.username());
    }
}
