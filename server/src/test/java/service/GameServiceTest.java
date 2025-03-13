package service;

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
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTest {

    private GameService gameService;
    private UserService userService;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException, DataAccessException {
        GameSqlDAO gameDao = new GameSqlDAO();
        AuthSqlDAO authDao = new AuthSqlDAO();
        UserSqlDAO userDao = new UserSqlDAO();
        gameService = new GameService(gameDao, authDao);
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
    public void testListGamesPositive() throws SQLException, DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@mail.com");
        AuthData authData = userService.register(newUser);

        // Create a game
        gameService.createGame(authData.authToken(), "Test Game");

        Collection<GameData> games = gameService.listGames(authData.authToken());
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    @Order(2)
    public void testListGamesNegative() {
        try {
            gameService.listGames("invalidToken");
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e instanceof DataAccessException);
        }
    }

    @Test
    @Order(3)
    public void testCreateGamePositive() throws SQLException, DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@mail.com");
        AuthData authData = userService.register(newUser);

        int gameId = gameService.createGame(authData.authToken(), "Test Game");
        assertTrue(gameId > 0);
    }

    @Test
    @Order(4)
    public void testCreateGameNegative() {
        try {
            gameService.createGame("invalidToken", "Test Game");
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e instanceof DataAccessException);
        }
    }

    @Test
    @Order(5)
    public void testJoinGamePositive() throws SQLException, DataAccessException {
        UserData newUser = new UserData("testUser", "password", "test@mail.com");
        AuthData authData = userService.register(newUser);

        int gameId = gameService.createGame(authData.authToken(), "Test Game");
        gameService.joinGame(authData.authToken(), gameId, "WHITE");

        Collection<GameData> games = gameService.listGames(authData.authToken());
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals("testUser", games.iterator().next().getWhiteUsername());
    }

    @Test
    @Order(6)
    public void testJoinGameNegative() {
        try {
            gameService.joinGame("invalidToken", 1, "WHITE");
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertTrue(e instanceof DataAccessException);
        }
    }
}