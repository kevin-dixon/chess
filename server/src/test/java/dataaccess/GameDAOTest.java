package dataaccess;

import chess.ChessGame;
import dataaccess.sqldatabase.GameSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTest {

    private GameSqlDAO gameDao;
    private UserSqlDAO userDao;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException, DataAccessException {
        gameDao = new GameSqlDAO();
        userDao = new UserSqlDAO();
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
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("DELETE FROM users");
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
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
            );
        }
    }

    @Test
    @Order(1)
    public void testAddGamePositive() throws SQLException, DataAccessException {
        UserData whiteUser = new UserData("whiteUser", "password", "white@mail.com");
        UserData blackUser = new UserData("blackUser", "password", "black@mail.com");
        userDao.addUser(whiteUser);
        userDao.addUser(blackUser);

        GameData gameData = new GameData(new Random().nextInt(), "whiteUser", "blackUser", new ChessGame(), "Test Game");
        GameData result = gameDao.addGame(gameData);

        assertNotNull(result);
        assertEquals("whiteUser", result.whiteUsername());
        assertEquals("blackUser", result.blackUsername());
        assertEquals("Test Game", result.gameName());
    }

    @Test
    @Order(2)
    public void testAddGameNegative() {
        try {
            GameData gameData = new GameData(new Random().nextInt(), "nonexistentUser", "blackUser", new ChessGame(), "Test Game");
            gameDao.addGame(gameData);
            fail("Exception should have been thrown");
        } catch (SQLException | DataAccessException e) {
            assertTrue(e instanceof SQLException || e instanceof DataAccessException);
        }
    }

    @Test
    @Order(3)
    public void testListGamesPositive() throws SQLException, DataAccessException {
        UserData whiteUser = new UserData("whiteUser", "password", "white@mail.com");
        UserData blackUser = new UserData("blackUser", "password", "black@mail.com");
        userDao.addUser(whiteUser);
        userDao.addUser(blackUser);

        GameData gameData = new GameData(new Random().nextInt(), "whiteUser", "blackUser", new ChessGame(), "Test Game");
        gameDao.addGame(gameData);

        Collection<GameData> games = gameDao.listGames();
        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    @Test
    @Order(4)
    public void testListGamesNegative() throws SQLException, DataAccessException {
        clearDatabase(); // Ensure the database is empty

        Collection<GameData> games = gameDao.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    @Order(5)
    public void testGetGameByIDPositive() throws SQLException, DataAccessException {
        UserData whiteUser = new UserData("whiteUser", "password", "white@mail.com");
        UserData blackUser = new UserData("blackUser", "password", "black@mail.com");
        userDao.addUser(whiteUser);
        userDao.addUser(blackUser);

        int gameID = new Random().nextInt();
        GameData gameData = new GameData(gameID, "whiteUser", "blackUser", new ChessGame(), "Test Game");
        gameDao.addGame(gameData);

        GameData result = gameDao.getGameByID(gameID);
        assertNotNull(result);
        assertEquals("whiteUser", result.whiteUsername());
        assertEquals("blackUser", result.blackUsername());
        assertEquals("Test Game", result.gameName());
    }

    @Test
    @Order(6)
    public void testGetGameByIDNegative() throws SQLException, DataAccessException {
        GameData result = gameDao.getGameByID(-1); // Use an invalid gameID
        assertNull(result);
    }

    @Test
    @Order(7)
    public void testAddPlayerToGamePositive() throws SQLException, DataAccessException {
        UserData whiteUser = new UserData("whiteUser", "password", "white@mail.com");
        UserData blackUser = new UserData("blackUser", "password", "black@mail.com");
        userDao.addUser(whiteUser);
        userDao.addUser(blackUser);

        int gameID = new Random().nextInt();
        GameData gameData = new GameData(gameID, null, "blackUser", new ChessGame(), "Test Game");
        gameDao.addGame(gameData);

        gameDao.addPlayerToGame(gameID, "WHITE", "whiteUser");

        GameData result = gameDao.getGameByID(gameID);
        assertNotNull(result);
        assertEquals("whiteUser", result.whiteUsername());
    }

    @Test
    @Order(8)
    public void testAddPlayerToGameNegative() {
        try {
            gameDao.addPlayerToGame(-1, "WHITE", "whiteUser"); // Use an invalid gameID
            fail("Exception should have been thrown");
        } catch (SQLException | DataAccessException e) {
            assertTrue(e instanceof SQLException || e instanceof DataAccessException);
        }
    }

    @Test
    @Order(9)
    public void testDeleteAllGamesPositive() throws SQLException, DataAccessException {
        UserData whiteUser = new UserData("whiteUser", "password", "white@mail.com");
        UserData blackUser = new UserData("blackUser", "password", "black@mail.com");
        userDao.addUser(whiteUser);
        userDao.addUser(blackUser);

        GameData gameData1 = new GameData(new Random().nextInt(), "whiteUser", "blackUser", new ChessGame(), "Test Game 1");
        GameData gameData2 = new GameData(new Random().nextInt(), "whiteUser", "blackUser", new ChessGame(), "Test Game 2");
        gameDao.addGame(gameData1);
        gameDao.addGame(gameData2);

        gameDao.deleteAllGames();

        assertTrue(gameDao.listGames().isEmpty());
    }

    @Test
    @Order(10)
    public void testDeleteAllGamesNegative() {
        try {
            gameDao.deleteAllGames();
            // No exception is expected, as deleting all games should be a no-op if the table is already empty
        } catch (SQLException | DataAccessException e) {
            fail("Exception should not have been thrown");
        }
    }
}