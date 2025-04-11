package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.GameData;
import model.responses.UserAuthResponse;
import ui.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080); // Start server on a random port
        System.out.println("Started test HTTP server on port " + port);
        facade = new ServerFacade("http://localhost:" + port); // Initialize ServerFacade with the server's port
    }

    @AfterAll
    public static void stopServer() {
        server.stop(); // Stop the server after all tests
    }

    @BeforeEach
    public void clearDatabase() throws Exception, ResponseException {
        facade.makeRequest("DELETE", "/db", null, null); // Clear the database before each test
    }

    @Test
    void registerSuccess() throws Exception, ResponseException {
        var authToken = facade.register("player1", "password", "player1@email.com");
        assertNotNull(authToken);
        assertTrue(authToken.length() > 10);
    }

    @Test
    void registerFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.register("", "password", "player1@email.com"); // Invalid username
        });
        assertTrue(exception.getMessage().contains("bad request"));
    }

    @Test
    void loginSuccess() throws Exception, ResponseException {
        facade.register("player2", "password", "player2@email.com");
        var authToken = facade.login("player2", "password");
        assertNotNull(authToken);
        assertTrue(authToken.length() > 10);
    }

    @Test
    void loginFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.login("nonexistent", "password"); // Nonexistent user
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void createGameSuccess() throws Exception, ResponseException {
        var authToken = facade.register("player3", "password", "player3@email.com");
        facade.createGame(authToken, "Test Game");
        GameData[] games = facade.listGames(authToken);
        assertEquals(1, games.length);
    }

    @Test
    void createGameFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.createGame("invalidToken", "Test Game"); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void listGamesSuccess() throws Exception, ResponseException {
        var authToken = facade.register("player4", "password", "player4@email.com");
        facade.createGame(authToken, "Game 1");
        facade.createGame(authToken, "Game 2");

        GameData[] games = facade.listGames(authToken);
        assertEquals(2, games.length, "There should be exactly two games listed");
        assertEquals("Game 1", games[0].gameName(), "First game name should match");
        assertEquals("Game 2", games[1].gameName(), "Second game name should match");
    }

    @Test
    void listGamesFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.listGames("invalidToken"); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void joinGameSuccess() throws Exception, ResponseException {
        var authToken = facade.register("player5", "password", "player5@email.com");
        facade.createGame(authToken, "Joinable Game");

        GameData[] games = facade.listGames(authToken);
        int gameId = games[0].gameID();

        facade.joinGame(authToken, gameId, "WHITE");
        GameData updatedGame = facade.listGames(authToken)[0];
        assertEquals("player5", updatedGame.whiteUsername(), "White player should be set to player5");
    }

    @Test
    void joinGameFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.joinGame("invalidToken", 1, "WHITE"); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

/*    @Test
    void observeGameSuccess() throws Exception, ResponseException {
        var authToken = facade.register("player6", "password", "player6@email.com");
        facade.createGame(authToken, "Observable Game");

        GameData[] games = facade.listGames(authToken);
        int gameId = games[0].gameID();

        facade.observeGame(authToken, gameId);
        // No exception means success
        assertTrue(true, "Observe game should succeed without exceptions");
    }

    @Test
    void observeGameFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.observeGame("invalidToken", 1); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }*/

    @Test
    void leaveGameSuccess() throws Exception, ResponseException {
        var authToken = facade.register("player7", "password", "player7@email.com");
        facade.createGame(authToken, "Leavable Game");

        GameData[] games = facade.listGames(authToken);
        int gameId = games[0].gameID();

        facade.leaveGame(authToken, gameId);
        GameData updatedGame = facade.listGames(authToken)[0];
        assertNull(updatedGame.whiteUsername(), "White player should be null after leaving the game");
    }

    @Test
    void leaveGameFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.leaveGame("invalidToken", 1); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }
}