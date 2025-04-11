package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.GameSqlDAO;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;

public class GameService {
    private final GameSqlDAO gameDao;
    private final AuthSqlDAO authDao;

    public GameService(GameSqlDAO gameDataAccess, AuthSqlDAO authDataAccess) {
        this.gameDao = gameDataAccess;
        this.authDao = authDataAccess;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException, SQLException {
        // Validate authToken
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        return gameDao.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException, SQLException {
        // Validate authToken
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        // Create new game
        GameData newGame = new GameData(
                newGameID(),
                null,
                null,
                new ChessGame(),
                gameName
        );
        // Add new game to database
        gameDao.addGame(newGame);
        return newGame.gameID();
    }

    private int newGameID() {
        Random random = new Random();
        //System.out.println(randomNumber);
        return 100000 + random.nextInt(900000);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException, SQLException {
        // Validate authToken
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        // Validate input and game existence
        GameData gameData = gameDao.getGameByID(gameID);
        if (gameData == null || (!Objects.equals(playerColor, "WHITE") && !Objects.equals(playerColor, "BLACK"))) {
            throw new DataAccessException("bad request");
        }

        // Check if the color is already taken
        if (("WHITE".equals(playerColor) && gameData.whiteUsername() != null) ||
                ("BLACK".equals(playerColor) && gameData.blackUsername() != null)) {
            throw new DataAccessException("already taken");
        }

        // Add player to the game
        gameDao.addPlayerToGame(gameID, playerColor, authData.username());
    }

    public GameData getGameByID(int gameID) throws DataAccessException {
        try {
            return gameDao.getGameByID(gameID);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve game: " + e.getMessage());
        }
    }

    public void removePlayerFromGame(int gameID, String username) throws DataAccessException {
        try {
            gameDao.removePlayerFromGame(gameID, username);
        } catch (Exception e) {
            throw new DataAccessException("Failed to remove player from game: " + e.getMessage());
        }
    }

    public void observeGame(String authToken, int gameID) {
    }

    public void leaveGame(String authToken, int gameID) {
    }
}
