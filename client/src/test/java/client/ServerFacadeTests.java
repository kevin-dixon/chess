package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.GameData;
import ui.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on port " + port);

        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clearDatabase();
    }

    @Test
    void registerSuccess() throws ResponseException {
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
    void loginSuccess() throws ResponseException {
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
    void logoutSuccess() throws ResponseException {
        var authToken = facade.register("player8", "password", "player8@email.com");
        facade.logout(authToken);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.listGames(authToken); // Token should no longer be valid
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void logoutFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.logout("invalidToken"); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        // Clear the database to ensure no leftover data
        facade.clearDatabase();

        // Create new games
        var authToken = facade.register("player4", "password", "player4@email.com");
        facade.createGame(authToken, "Game 1");
        facade.createGame(authToken, "Game 2");

        // Fetch the list of games
        GameData[] games = facade.listGames(authToken);

        // Verify that the list contains the expected games
        boolean game1Exists = false;
        boolean game2Exists = false;
        for (GameData game : games) {
            if ("Game 1".equals(game.gameName())) {
                game1Exists = true;
            }
            if ("Game 2".equals(game.gameName())) {
                game2Exists = true;
            }
        }

        assertTrue(game1Exists, "Game 1 should exist in the list of games");
        assertTrue(game2Exists, "Game 2 should exist in the list of games");
    }

    @Test
    void listGamesFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.listGames("invalidToken"); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void createGameSuccess() throws ResponseException {
        var authToken = facade.register("player3", "password", "player3@email.com");
        int gameId = facade.createGame(authToken, "Test Game");
        assertTrue(gameId > 0, "Game ID should be greater than 0");
    }

    @Test
    void createGameFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.createGame("invalidToken", "Test Game"); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        var authToken = facade.register("player5", "password", "player5@email.com");
        int gameId = facade.createGame(authToken, "Joinable Game");

        facade.joinGame(authToken, gameId, "WHITE");
        GameData[] games = facade.listGames(authToken);
        assertEquals("player5", games[0].whiteUsername(), "White player should be set to player5");
    }

    @Test
    void joinGameFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.joinGame("invalidToken", 1, "WHITE"); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void observeGameSuccess() throws ResponseException {
        var authToken = facade.register("player6", "password", "player6@email.com");
        int gameId = facade.createGame(authToken, "Observable Game");

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
    }

    @Test
    void leaveGameSuccess() throws ResponseException {
        var authToken = facade.register("player7", "password", "player7@email.com");
        int gameId = facade.createGame(authToken, "Leavable Game");

        facade.leaveGame(authToken, gameId);
        GameData[] games = facade.listGames(authToken);
        assertNull(games[0].whiteUsername(), "White player should be null after leaving the game");
    }

    @Test
    void leaveGameFailure() {
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.leaveGame("invalidToken", 1); // Invalid auth token
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void clearDatabaseSuccess() throws ResponseException {
        facade.register("testUser", "password", "test@mail.com");

        // Clear the database
        facade.clearDatabase();
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            facade.listGames("invalidToken");
        });
        assertTrue(exception.getMessage().contains("unauthorized"), "Expected error message to contain 'unauthorized'");
    }

    @Test
    void clearDatabaseFailure() {
        ServerFacade invalidFacade = new ServerFacade("http://invalid-url");
        ResponseException exception = assertThrows(ResponseException.class, invalidFacade::clearDatabase);
        assertTrue(exception.getMessage().contains("HTTP"), "Expected error message to contain 'HTTP'");
    }

    @Test
    void getServerUrlTest() {
        String actualUrl = facade.getServerUrl();
        assertNotNull(actualUrl, "Server URL should not be null");
        assertTrue(actualUrl.startsWith("http://localhost:"), "Server URL should start with 'http://localhost:'");

        String[] urlParts = actualUrl.split(":");
        assertEquals(3, urlParts.length, "Server URL should contain a valid port");
    }

    @Test
    void getServerUrlNegativeTest() {
        ServerFacade invalidFacade = new ServerFacade("http://invalid-url");
        assertEquals("http://invalid-url", invalidFacade.getServerUrl(), "Server URL should match the initialized value");
    }
}