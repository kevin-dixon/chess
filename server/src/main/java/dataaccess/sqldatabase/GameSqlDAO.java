package dataaccess.sqldatabase;

import dataaccess.DatabaseManager;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class GameSqlDAO {

    public GameData addGame(GameData gameData) throws SQLException {
        String sql = "INSERT INTO games (gameID, blackUser, whiteUser) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameData.gameID());
            stmt.setString(2, gameData.getBlackUsername());
            stmt.setString(3, gameData.getWhiteUsername());
            stmt.executeUpdate();
        }

        return gameData;
    }

    public Collection<GameData> listGames() throws SQLException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";

        //TODO: add string parsing

        return games;
    }

    public GameData getGameByID(int gameID) throws SQLException {
        GameData gameData = null;
        String sql = "SELECT * FROM games WHERE gameID = ?";

        //TODO: add string parsing

        return gameData;
    }

    public void deleteAllGames() throws SQLException {
        String sql = "DELETE FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    public void addPlayerToGame(int gameID, String playerColor, String username) throws SQLException {
        String sql;
        if ("BLACK".equals(playerColor)) {
            sql = "UPDATE games SET blackUser = ? WHERE gameID = ?";
        } else if ("WHITE".equals(playerColor)) {
            sql = "UPDATE games SET whiteUser = ? WHERE gameID = ?";
        } else {
            throw new IllegalArgumentException("bad request");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();
        }
    }
}