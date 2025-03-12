package dataaccess.sqldatabase;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.UserData;

import java.sql.*;

public class UserSqlDAO {

    public UserData addUser(UserData newUserData) throws SQLException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newUserData.username());
            stmt.setString(2, newUserData.password());
            stmt.setString(3, newUserData.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(String.format("Unable to configure database: %s", e.getMessage()));
        }

        return newUserData;
    }

    public void listUsers() throws DataAccessException {
    }

    public UserData getUser(String username) throws SQLException {
        UserData userData = null;
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    userData = new UserData(username, password, email);
                }
            }
        }

        return userData;
    }

    public void deleteAllUsers() throws DataAccessException {
    }
}