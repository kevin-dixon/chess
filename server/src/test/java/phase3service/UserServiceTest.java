package phase3service;
/*

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
