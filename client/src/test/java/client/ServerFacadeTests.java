package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.GameData;
import ui.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server; // Server instance
    private static ServerFacade facade; // Client-side ServerFacade
    private static final int SERVER_PORT = 0; // Port for the server
    private static final String SERVER_URL = "http://localhost:" + SERVER_PORT; // Server URL

    @BeforeAll
    public static void init() {
        // Start the server
        server = new Server();
        server.run(SERVER_PORT);
        System.out.println("Started test HTTP server on port " + SERVER_PORT);

        // Initialize the ServerFacade for the client
        facade = new ServerFacade(SERVER_URL);
    }

    @AfterAll
    public static void stopServer() {
        // Stop the server after all tests
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception, ResponseException {
        // Clear the database before each test
        facade.clearDatabase();
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
    void logoutSuccess() throws Exception, ResponseException {
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
    void createGameSuccess() throws Exception, ResponseException {
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
    void joinGameSuccess() throws Exception, ResponseException {
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
    void observeGameSuccess() throws Exception, ResponseException {
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
    void leaveGameSuccess() throws Exception, ResponseException {
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
    void clearDatabaseSuccess() throws Exception, ResponseException {
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
        assertEquals(SERVER_URL, facade.getServerUrl(), "Server URL should match the initialized value");
    }

    @Test
    void getServerUrlNegativeTest() {
        ServerFacade invalidFacade = new ServerFacade("http://invalid-url");
        assertEquals("http://invalid-url", invalidFacade.getServerUrl(), "Server URL should match the initialized value");
    }
}