package service;

import dataaccess.DataAccessException;
import dataaccess.sqldatabase.AuthSqlDAO;
import dataaccess.sqldatabase.UserSqlDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import dataaccess.DatabaseManager;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private UserService userService;
    private AuthSqlDAO authDao;
    private UserSqlDAO userDao;

    @BeforeAll
    void setUp() throws SQLException, DataAccessException {
        Connection conn = DatabaseManager.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS auths (authToken VARCHAR(255) PRIMARY KEY, username VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS games (gameID INT PRIMARY KEY, whiteUsername VARCHAR(255), blackUsername VARCHAR(255), game BLOB, gameName VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255), email VARCHAR(255))");
        }

        authDao = new AuthSqlDAO();
        userDao = new UserSqlDAO();
        userService = new UserService(authDao, userDao);
    }

    @AfterAll
    void tearDown() throws SQLException, DataAccessException {
        Connection conn = DatabaseManager.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS auths");
            stmt.execute("DROP TABLE IF EXISTS games");
            stmt.execute("DROP TABLE IF EXISTS users");
        }
    }

    @Test
    void registerSuccess() throws SQLException, DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        AuthData authData = userService.register(userData);
        assertNotNull(authData);
    }

    @Test
    void registerUserExists() throws SQLException, DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        userDao.addUser(userData);
        assertThrows(DataAccessException.class, () -> userService.register(userData));
    }

    @Test
    void loginSuccess() throws SQLException, DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        userDao.addUser(userData);
        AuthData authData = userService.login(userData);
        assertNotNull(authData);
    }

    @Test
    void loginUnauthorized() throws SQLException, DataAccessException {
        UserData userData = new UserData("user", "password", "email@example.com");
        assertThrows(DataAccessException.class, () -> userService.login(userData));
    }

    @Test
    void logoutSuccess() throws SQLException, DataAccessException {
        AuthData authData = new AuthData("token", "user");
        authDao.addAuth(authData);
        userService.logout("token");
        assertNull(authDao.getAuth("token"));
    }

    @Test
    void logoutUnauthorized() throws SQLException, DataAccessException {
        assertThrows(DataAccessException.class, () -> userService.logout("token"));
    }

    @Test
    void validAuthTokenValid() throws SQLException, DataAccessException {
        AuthData authData = new AuthData("token", "user");
        authDao.addAuth(authData);
        assertTrue(userService.validAuthToken("token"));
    }

    @Test
    void validAuthTokenInvalid() throws SQLException, DataAccessException {
        assertFalse(userService.validAuthToken("token"));
    }
}

/*
package service;

import dataaccess.localmemory.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.localmemory.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        userService = new UserService(authDAO, userDAO);
    }

    @Test
    public void testRegisterNewUser() throws Exception {
        UserData newUser = new UserData("username", "password", "email");
        //Register new user
        AuthData authData = userService.register(newUser);

        //Check that user exists
        UserData retrievedUser = userDAO.getUser(newUser.username());
        assertNotNull(retrievedUser);
        assertEquals(newUser.username(), retrievedUser.username());
        assertEquals(newUser.password(), retrievedUser.password());
        assertEquals(newUser.email(), retrievedUser.email());

        //Check that auth token exists
        AuthData retrievedAuthData = authDAO.getAuth(authData.authToken());
        assertNotNull(retrievedAuthData);
        assertEquals(authData.authToken(), retrievedAuthData.authToken());
        assertEquals(authData.username(), retrievedAuthData.username());
    }

    @Test
    public void testRegisterExistingUser() throws Exception {
        UserData dupUser = new UserData("username-duplicate", "password1234", "email@gmail.com");
        //Register new user
        userDAO.addUser(dupUser);

        //Check message if adding duplicate user
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> userService.register(dupUser));
        assertEquals("already taken", thrown.getMessage());
    }

    @Test
    public void testLoginUser() throws Exception {
        UserData newUser = new UserData("username", "password", "email");
        //Register new user
        AuthData authData = userService.register(newUser);

        //Check that user exists
        assertNotNull(authData);

        //Attempt login authentication
        AuthData loginAuthData = userService.login(newUser);
        assertNotNull(loginAuthData);
        assertEquals(newUser.username(), loginAuthData.username());

        //Check that auth token exists in the database
        AuthData retrievedAuthData = authDAO.getAuth(loginAuthData.authToken());
        assertNotNull(retrievedAuthData);
        assertEquals(loginAuthData.authToken(), retrievedAuthData.authToken());
        assertEquals(loginAuthData.username(), retrievedAuthData.username());
    }

    @Test
    public void testWrongPasswordLogin() throws Exception {
        UserData newUser = new UserData("username", "password", "email");
        //Register new user
        AuthData authData = userService.register(newUser);

        //Create wrong password user data
        UserData wrongPassword = new UserData("username", "wrong-password", "email");

        //Check wrong password result message
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> userService.login(wrongPassword));
        assertEquals("unauthorized", thrown.getMessage());
    }

    @Test
    public void testLogoutUser() throws Exception {
        UserData newUser = new UserData("username", "password", "email");
        //Register new user
        AuthData authData = userService.register(newUser);

        //Check that user exists
        assertNotNull(authData);

        //Attempt login
        AuthData loginAuthData = userService.login(newUser);
        assertNotNull(loginAuthData);
        assertEquals(newUser.username(), loginAuthData.username());

        //Attempt logout
        userService.logout(loginAuthData.authToken());

        //Verify the auth token is removed from the database
        AuthData retrievedAuthData = authDAO.getAuth(loginAuthData.authToken());
        assertNull(retrievedAuthData);
    }

    @Test
    public void testLogoutWithInvalidToken() {
        //Attempt logout with invalid token
        String invalidToken = "invalidAuthToken";

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> userService.logout(invalidToken));
        assertEquals("unauthorized", thrown.getMessage());
    }

    @Test
    public void testValidAuthToken() throws Exception {
        UserData newUser = new UserData("username", "password", "email");
        //Register new user
        AuthData authData = userService.register(newUser);

        //Check if authToken is valid
        assertTrue(userService.validAuthToken(authData.authToken()));
    }

    @Test
    public void testInvalidAuthToken() throws Exception {
        UserData newUser = new UserData("valid-username", "password", "email");
        //Register new user
        AuthData authData = userService.register(newUser);

        //Create Invalid AuthData
        AuthData invalidAuthData = new AuthData("InvalidAuthToken", "valid-username");

        //Check if authToken is valid
        assertFalse(userService.validAuthToken(invalidAuthData.authToken()));
    }
}*/
