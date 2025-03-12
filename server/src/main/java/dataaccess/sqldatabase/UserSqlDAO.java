package dataaccess.sqldatabase;

import dataaccess.DatabaseManager;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

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

    public Collection<UserData> listUsers() throws SQLException {
        Collection<UserData> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                users.add(new UserData(username, password, email));
            }
        }
        return users;
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

    public void deleteAllUsers() throws SQLException {
        String sql = "DELETE FROM users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}