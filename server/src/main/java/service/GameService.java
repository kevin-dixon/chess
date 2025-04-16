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
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        return gameDao.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException, SQLException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        int gameID = newGameID();
        while (gameDao.getGameByID(gameID) != null) {
            gameID = newGameID(); // Ensure unique gameID
        }

        GameData newGame = new GameData(gameID, null, null, new ChessGame(), gameName);
        gameDao.addGame(newGame);
        return gameID; // Return the generated gameID
    }

    private int newGameID() {
        Random random = new Random();
        //System.out.println(randomNumber);
        return 100000 + random.nextInt(900000);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException, SQLException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        GameData gameData = gameDao.getGameByID(gameID);
        if (gameData == null) {
            throw new DataAccessException("Invalid GameID");
        }

        if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            throw new DataAccessException("Invalid Team Color");
        }

        if (("WHITE".equals(playerColor) && gameData.whiteUsername() != null) ||
                ("BLACK".equals(playerColor) && gameData.blackUsername() != null)) {
            throw new DataAccessException("Team Color Already Taken");
        }

        gameDao.addPlayerToGame(gameID, playerColor, authData.username());
    }

    public GameData getGameByID(int gameID) throws DataAccessException {
        try {
            return gameDao.getGameByID(gameID);
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve game: " + e.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws DataAccessException, SQLException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }

        String username = authData.username();
        gameDao.removePlayerFromGame(gameID, username);
    }

    public boolean isValidGame(int gameID) throws SQLException, DataAccessException {
        return gameDao.getGameByID(gameID) != null;
    }

    public boolean isValidAuthToken(String authToken) throws DataAccessException, SQLException {
        AuthData authData = authDao.getAuth(authToken);
        return authData != null;
    }

    public String getAuthUsername(String authToken) {
        try {
            AuthData authData = authDao.getAuth(authToken);
            if (authData == null) {
                throw new DataAccessException("Invalid authentication token.");
            }
            return authData.username();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve username for auth token: " + e.getMessage(), e);
        }
    }
}
