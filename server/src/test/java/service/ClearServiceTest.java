package service;

import chess.ChessGame;
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

    //TODO: add more tests
}