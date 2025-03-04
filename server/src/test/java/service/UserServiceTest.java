package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
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

        AuthData authData = userService.register(newUser);

        assertNotNull(authData);
        assertEquals("username", authData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    public void testRegisterExistingUser() throws Exception {
        UserData dupUser = new UserData("username-duplicate", "password1234", "email@gmail.com");
        //add user
        userDAO.addUser(dupUser);

        //check message if adding duplicate user
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> userService.register(dupUser));
        assertEquals("already taken", thrown.getMessage());
    }

    //TODO: add more tests
}