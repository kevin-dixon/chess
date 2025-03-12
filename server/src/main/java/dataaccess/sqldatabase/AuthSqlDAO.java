package dataaccess.sqldatabase;

import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class AuthSqlDAO {

    public AuthData addAuth(AuthData newAuthData) throws SQLException {
        String sql = "INSERT INTO auths (authToken, userID) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newAuthData.authToken());
            stmt.setString(2, newAuthData.username());
            stmt.executeUpdate();
        }

        return newAuthData;
    }

    public Collection<AuthData> listAuths() throws SQLException {
        Collection<AuthData> auths = new ArrayList<>();
        String sql = "SELECT * FROM auths";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String authToken = rs.getString("authToken");
                String userID = rs.getString("userID");
                auths.add(new AuthData(authToken, userID));
            }
        }

        return auths;
    }

    public AuthData getAuth(String authToken) throws SQLException {
        AuthData authData = null;
        String sql = "SELECT * FROM auths WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String userID = rs.getString("userID");
                    authData = new AuthData(authToken, userID);
                }
            }
        }

        return authData;
    }

    public void deleteAuth(String authToken) throws SQLException {
        String sql = "DELETE FROM auths WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        }
    }

    public void deleteAllAuths() throws SQLException {
        String sql = "DELETE FROM auths";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}