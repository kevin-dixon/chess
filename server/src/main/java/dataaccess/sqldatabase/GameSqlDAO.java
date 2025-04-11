package dataaccess.sqldatabase;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class GameSqlDAO {
    private final Gson gson;

    public GameSqlDAO() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessGame.class, new ChessGameAdapter());
        this.gson = builder.create();
    }

    public GameData addGame(GameData newGameData) throws SQLException, DataAccessException {
        String sql = "INSERT INTO games (gameID, whiteUsername, blackUsername, game, gameName) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newGameData.gameID());
            stmt.setString(2, newGameData.whiteUsername());
            stmt.setString(3, newGameData.blackUsername());
            stmt.setString(4, gson.toJson(newGameData.game()));
            stmt.setString(5, newGameData.gameName());
            stmt.executeUpdate();
        }
        return newGameData;
    }

    public Collection<GameData> listGames() throws SQLException, DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                String gameName = rs.getString("gameName");
                games.add(new GameData(gameID, whiteUsername, blackUsername, game, gameName));
            }
        }
        return games;
    }

    public GameData getGameByID(int gameID) throws SQLException, DataAccessException {
        GameData gameData = null;
        String sql = "SELECT * FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                    String gameName = rs.getString("gameName");
                    gameData = new GameData(gameID, whiteUsername, blackUsername, game, gameName);
                }
            }
        }
        return gameData;
    }

    public void addPlayerToGame(int gameID, String playerColor, String username) throws SQLException, DataAccessException {
        if (getGameByID(gameID) == null) {
            throw new DataAccessException("Invalid GameID");
        }

        String sql = "UPDATE games SET " + (playerColor.equals("WHITE") ? "whiteUsername" : "blackUsername") + " = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();
        }
    }

    public void deleteAllGames() throws SQLException, DataAccessException {
        String sql = "DELETE FROM games";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    public void removePlayerFromGame(int gameID, String username) throws SQLException, DataAccessException {
        String sql = "UPDATE games SET whiteUsername = CASE WHEN whiteUsername = ? THEN NULL ELSE whiteUsername END, " +
                "blackUsername = CASE WHEN blackUsername = ? THEN NULL ELSE blackUsername END WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setInt(3, gameID);
            stmt.executeUpdate();
        }
    }

}