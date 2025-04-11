package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import model.responses.UserAuthResponse;
import ui.ResponseException;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.gson = new Gson();
    }

    //Add a function for each api call client can make
    /**
     * Example api call for functionality:
     * public User addUser(User user) throws Exception {
     *     var path = "/user";
     *     return this.makeRequest("POST", path, user, User.class);
     * }
     * **/

    public String register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        var request = new UserData(username, password, email);

        // Expect a UserAuthResponse object from the server
        UserAuthResponse response = this.makeRequest("POST", path, request, UserAuthResponse.class);

        // Return only the authToken to the client
        return response.authToken();
    }

    public String login(String username, String password) throws ResponseException {
        var path = "/session";
        var request = new UserData(username, password, null); // Email is null for login

        // Expect a UserAuthResponse object from the server
        UserAuthResponse response = this.makeRequest("POST", path, request, UserAuthResponse.class);

        // Return only the authToken to the client
        return response.authToken();
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequestWithAuth("DELETE", path, null, null, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequestWithAuth("GET", path, null, GameData[].class, authToken);
    }

    public String createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        return this.makeRequestWithAuth("POST", path, request, String.class, authToken);
    }

    public String joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(gameID, playerColor);
        return this.makeRequestWithAuth("PUT", path, request, String.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T makeRequestWithAuth(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("Authorization", authToken);

            writeBody(request, http);
            http.connect();
            //throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (responseClass == null) {
            return null;
        }
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(respBody);
            return new Gson().fromJson(reader, responseClass);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "HTTP error: " + status);
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }


    private boolean isSuccessful(int status) {
        return status >= 200 && status < 300;
    }

    public void observeGame(String authToken, int i) {
    }

    private static class CreateGameRequest {
        String gameName;

        CreateGameRequest(String gameName) {
            this.gameName = gameName;
        }
    }

    private static class JoinGameRequest {
        int gameID;
        String playerColor;

        JoinGameRequest(int gameID, String playerColor) {
            this.gameID = gameID;
            this.playerColor = playerColor;
        }
    }

}
