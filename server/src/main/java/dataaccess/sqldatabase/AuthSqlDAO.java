package dataaccess.sqldatabase;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class AuthSqlDAO {

    public AuthData addAuth(AuthData newAuthData) throws SQLException, DataAccessException {
        String sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newAuthData.authToken());
            stmt.setString(2, newAuthData.username());
            stmt.executeUpdate();
        }
        return newAuthData;
    }

    public AuthData getAuth(String authToken) throws SQLException, DataAccessException {
        AuthData authData = null;
        String sql = "SELECT * FROM auths WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    authData = new AuthData(authToken, username);
                }
            }
        }
        return authData;
    }

    public void deleteAuth(String authToken) throws SQLException, DataAccessException {
        String sql = "DELETE FROM auths WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        }
    }

    public void deleteAllAuths() throws SQLException, DataAccessException {
        String sql = "DELETE FROM auths";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}