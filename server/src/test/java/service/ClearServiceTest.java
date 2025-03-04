package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
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
        // Add some data
        userDAO.addUser(new UserData("username", "password", "email@gmail.com"));
        //gameDAO.addGame(new GameData(1, "gameName"));
        authDAO.addAuth(new AuthData("authToken", "username"));

        // Clear the data
        clearService.clear(null);

        assertTrue(userDAO.listUsers().isEmpty());
        assertTrue(gameDAO.listGames().isEmpty());
        assertTrue(authDAO.listAuths().isEmpty());
    }

    //TODO: add more tests
}