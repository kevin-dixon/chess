/*
package service;

import chess.ChessGame;
import dataaccess.localmemory.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.localmemory.GameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() {
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    public void testListGames() throws Exception {
        String authToken = "validAuthToken";
        AuthData authData = new AuthData(authToken, "username");
        authDAO.addAuth(authData);

        Collection<GameData> games = gameService.listGames(authToken);
        assertNotNull(games);

        //Add some Games
        String gameName = "Test Game";
        String gameName2 = "Test Game 2";
        gameService.createGame(authToken, gameName);
        gameService.createGame(authToken, gameName2);

        //Check that list has games
        assertFalse(games.isEmpty());
    }

    @Test
    public void testUnauthorizedListGames() throws Exception {
        String authToken = "invalidAuthToken";

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.listGames(authToken);
        });

        assertEquals("unauthorized", thrown.getMessage());
    }

    @Test
    public void testCreateGameValidAuthToken() throws Exception {
        String authToken = "validAuthToken";
        String gameName = "Test Game";

        AuthData authData = new AuthData(authToken, "username");
        authDAO.addAuth(authData);

        int gameID = gameService.createGame(authToken, gameName);
        assertTrue(gameID >= 100000 && gameID <= 999999);

        GameData gameData = gameDAO.getGameByID(gameID);
        assertNotNull(gameData);
        assertEquals(gameName, gameData.gameName());
    }

    @Test
    public void testCreateGameInvalidAuthToken() {
        String authToken = "invalidAuthToken";
        String gameName = "Test Game";

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(authToken, gameName);
        });

        assertEquals("unauthorized", thrown.getMessage());
    }


    @Test
    public void testJoinGameValid() throws Exception {
        String authToken = "validAuthToken";
        String playerColor = "WHITE";

        AuthData authData = new AuthData(authToken, "username");
        authDAO.addAuth(authData);

        // Create new game and get the generated gameID
        int gameID = gameService.createGame(authToken, "NewGame");

        // Attempt to join the game
        gameService.joinGame(authToken, gameID, playerColor);

        GameData updatedGameData = gameDAO.getGameByID(gameID);
        assertEquals("username", updatedGameData.whiteUsername());
    }

    @Test
    public void testJoinGameInvalidAuthToken() {
        String authToken = "invalidAuthToken";
        int gameID = 123456;
        String playerColor = "WHITE";

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(authToken, gameID, playerColor);
        });

        assertEquals("unauthorized", thrown.getMessage());
    }

    @Test
    public void testJoinGameGameNotFound() {
        String authToken = "validAuthToken";
        int gameID = 123456;
        String playerColor = "WHITE";

        AuthData authData = new AuthData(authToken, "username");
        authDAO.addAuth(authData);

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(authToken, gameID, playerColor);
        });

        assertEquals("bad request", thrown.getMessage());
    }

    @Test
    public void testJoinGameAlreadyTaken() {
        String authToken = "validAuthToken";
        int gameID = 123456;
        String playerColor = "WHITE";

        AuthData authData = new AuthData(authToken, "username");
        authDAO.addAuth(authData);

        GameData gameData = new GameData(gameID, "whitePlayer", "", new ChessGame(), "Test Game");
        gameDAO.addGame(gameData);

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(authToken, gameID, playerColor);
        });

        assertEquals("already taken", thrown.getMessage());
    }
}*/
