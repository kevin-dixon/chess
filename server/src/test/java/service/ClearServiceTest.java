package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.GameSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClearServiceTest {

    private ClearService clearService;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException, DataAccessException {
        AuthSqlDAO authDao = new AuthSqlDAO();
        GameSqlDAO gameDao = new GameSqlDAO();
        UserSqlDAO userDao = new UserSqlDAO();
        clearService = new ClearService(authDao, gameDao, userDao);
        connection = DatabaseManager.getConnection();

        // Ensure the database is clean and tables are created before each test
        createTables();
        clearDatabase();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clear the database after each test
        clearDatabase();
        connection.close();
    }

    private void clearDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM auths");
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("DELETE FROM users");
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS auths (" +
                            "authToken VARCHAR(255) PRIMARY KEY, " +
                            "username VARCHAR(255) NOT NULL);");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "username VARCHAR(255) PRIMARY KEY, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) NOT NULL);");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS games (" +
                            "gameID INT PRIMARY KEY, " +
                            "whiteUsername VARCHAR(255), " +
                            "blackUsername VARCHAR(255), " +
                            "game TEXT NOT NULL, " +
                            "gameName VARCHAR(255) NOT NULL, " +
                            "FOREIGN KEY (whiteUsername) REFERENCES users(username), " +
                            "FOREIGN KEY (blackUsername) REFERENCES users(username)" +
                            ");"
            );        }
    }

    @Test
    @Order(1)
    public void testClearPositive() {
        try {
            // Add some data to the tables
            addTestData();

            // Ensure data is added
            assertTrue(getTableRowCount("auths") > 0);
            assertTrue(getTableRowCount("games") > 0);
            assertTrue(getTableRowCount("users") > 0);

            // Clear all data
            clearService.clear(null);

            // Verify that the tables are empty
            assertEquals(0, getTableRowCount("auths"));
            assertEquals(0, getTableRowCount("games"));
            assertEquals(0, getTableRowCount("users"));
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testClearNegative() {
        try {
            // Temporarily rename the auths table to simulate a failure
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(
                        "RENAME TABLE auths TO auths_backup");
            }

            clearService.clear(null);
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e instanceof SQLException || e instanceof DataAccessException);
        } finally {
            // Restore the auths table
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(
                        "RENAME TABLE auths_backup TO auths");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addTestData() throws SQLException, DataAccessException {
        AuthSqlDAO authDao = new AuthSqlDAO();
        GameSqlDAO gameDao = new GameSqlDAO();
        UserSqlDAO userDao = new UserSqlDAO();

        // Add test user
        UserData testUser = new UserData("testUser", "password", "test@mail.com");
        userDao.addUser(testUser);

        // Add test auth
        AuthData authData = new AuthData("testToken", "testUser");
        authDao.addAuth(authData);

        // Add test game
        GameData testGame = new GameData(new Random().nextInt(), "testUser", null, new ChessGame(), "Test Game");
        gameDao.addGame(testGame);
    }

    private int getTableRowCount(String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}