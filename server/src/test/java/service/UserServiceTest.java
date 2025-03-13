package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private UserService userService;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException, DataAccessException {
        AuthSqlDAO authDao = new AuthSqlDAO();
        UserSqlDAO userDao = new UserSqlDAO();
        userService = new UserService(authDao, userDao);
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
                            "FOREIGN KEY (blackUsername) REFERENCES users(username));");
        }
    }

    @Test
    @Order(1)
    public void testRegisterPositive() throws SQLException, DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@mail.com");
        AuthData authData = userService.register(newUser);

        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    @Order(2)
    public void testRegisterNegative() throws SQLException, DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@mail.com");
        userService.register(newUser);

        try {
            userService.register(newUser); // Attempt to register the same user again
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e instanceof DataAccessException);
        }
    }

    @Test
    @Order(3)
    public void testLoginPositive() throws SQLException, DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@mail.com");
        userService.register(newUser);

        AuthData authData = userService.login(newUser);
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    @Order(4)
    public void testLoginNegative() {
        try {
            UserData newUser = new UserData("testUser", "wrongPassword", "test@mail.com");
            userService.login(newUser);
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e instanceof DataAccessException);
        }
    }

    @Test
    @Order(5)
    public void testLogoutPositive() throws SQLException, DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@mail.com");
        AuthData authData = userService.register(newUser);

        userService.logout(authData.authToken());

        assertFalse(userService.validAuthToken(authData.authToken()));
    }

    @Test
    @Order(6)
    public void testLogoutNegative() {
        try {
            userService.logout("invalidToken");
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e instanceof DataAccessException);
        }
    }
}