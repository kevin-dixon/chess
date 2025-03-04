package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
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
        Collection<GameData> games = gameService.listGames();
        assertNotNull(games);

        //Add some Games
        String authToken = "validAuthToken";
        String gameName = "Test Game";
        String gameName2 = "Test Game 2";
        AuthData authData = new AuthData(authToken, "username");
        authDAO.addAuth(authData);
        gameService.createGame(authToken, gameName);
        gameService.createGame(authToken, gameName2);

        //Check that list has games
        assertFalse(games.isEmpty());
    }

    @Test
    public void testUnauthorizedListGames() throws Exception {
        //TODO: add authentication to ListGames
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
        int gameID = 123456;
        String playerColor = "WHITE";

        AuthData authData = new AuthData(authToken, "username");
        authDAO.addAuth(authData);

        GameData gameData = new GameData(gameID, "", "", new ChessGame(), "Test Game");
        gameDAO.addGame(gameData);

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

        assertEquals("Unauthorized", thrown.getMessage());
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

        assertEquals("Game not found", thrown.getMessage());
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

        assertEquals("Already taken", thrown.getMessage());
    }
}