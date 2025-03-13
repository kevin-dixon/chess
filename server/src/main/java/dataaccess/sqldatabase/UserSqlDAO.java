package dataaccess.sqldatabase;

import dataaccess.DatabaseManager;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class UserSqlDAO {

    public UserData addUser(UserData newUserData) throws SQLException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(newUserData.password(), BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newUserData.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, newUserData.email());
            stmt.executeUpdate();
        }

        return newUserData;
    }

    public Collection<UserData> listUsers() throws SQLException {
        Collection<UserData> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

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

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
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

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    //Verify user from BCrypt hash
    public boolean verifyUser(String username, String providedClearTextPassword) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        String hashedPassword;

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hashedPassword = rs.getString("password");
                } else {
                    return false;
                }
            }
        }

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }
}