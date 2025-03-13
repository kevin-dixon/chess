package service;

import dataaccess.DataAccessException;
import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.GameSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import org.junit.jupiter.api.*;
import spark.Request;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import dataaccess.DatabaseManager;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClearServiceTest {

    private ClearService clearService;
    private AuthSqlDAO authDao;
    private GameSqlDAO gameDao;
    private UserSqlDAO userDao;

    @BeforeAll
    void setUp() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS auths (authToken VARCHAR(255) PRIMARY KEY, username VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS games (gameID INT PRIMARY KEY, whiteUsername VARCHAR(255), blackUsername VARCHAR(255), game BLOB, gameName VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255), email VARCHAR(255))");
        }

        authDao = new AuthSqlDAO();
        gameDao = new GameSqlDAO();
        userDao = new UserSqlDAO();
        clearService = new ClearService(authDao, gameDao, userDao);
    }

    @AfterAll
    void tearDown() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS auths");
            stmt.execute("DROP TABLE IF EXISTS games");
            stmt.execute("DROP TABLE IF EXISTS users");
        }
    }

    @Test
    void clearSuccess() throws SQLException, DataAccessException {
        clearService.clear(new Request() {});
        Assertions.assertEquals(0, authDao.listAuths().size());
        Assertions.assertEquals(0, gameDao.listGames().size());
        Assertions.assertEquals(0, userDao.listUsers().size());
    }

    @Test
    void clearFailure() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        conn.close();
        assertThrows(SQLException.class, () -> clearService.clear(new Request() {}));
    }
}

/*
package service;

import chess.ChessGame;
import dataaccess.localmemory.AuthDAO;
import dataaccess.localmemory.GameDAO;
import dataaccess.localmemory.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService clearService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        userDAO = new UserDAO();
        clearService = new ClearService(authDAO, gameDAO, userDAO);
    }

    @Test
    public void testClear() throws Exception {
        //Add some data
        userDAO.addUser(new UserData("username", "password", "email@gmail.com"));
        gameDAO.addGame(new GameData(9, "whiteUsername", "blackUsername", new ChessGame(), "newGameName!"));
        authDAO.addAuth(new AuthData("authToken", "username"));

        //Make sure data got added
        assertFalse(userDAO.listUsers().isEmpty());
        assertFalse(gameDAO.listGames().isEmpty());
        assertFalse(authDAO.listAuths().isEmpty());

        //Clear the data
        clearService.clear(null);

        assertTrue(userDAO.listUsers().isEmpty());
        assertTrue(gameDAO.listGames().isEmpty());
        assertTrue(authDAO.listAuths().isEmpty());
    }

    @Test
    public void testClearOnEmpty() throws Exception {
        //Clear the data
        clearService.clear(null);

        assertTrue(userDAO.listUsers().isEmpty());
        assertTrue(gameDAO.listGames().isEmpty());
        assertTrue(authDAO.listAuths().isEmpty());
    }
}*/
