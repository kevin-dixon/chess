package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import ui.ResponseException;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    //Add a function for each api call client can make
    /**
     * Example api call for functionality:
     * public User addUser(User user) throws Exception {
     *     var path = "/user";
     *     return this.makeRequest("POST", path, user, User.class);
     * }
     * **/

    public String register(String username, String password, String email) throws Exception, ResponseException {
        var path = "/user";
        var request = new UserData(username, password, email);
        return this.makeRequest("POST", path, request, String.class);
    }

    public String login(String username, String password) throws Exception, ResponseException {
        var path = "/session";
        var request = new UserData(username, password, null);
        return this.makeRequest("POST", path, request, String.class);
    }

    public String logout(String authToken) throws Exception, ResponseException {
        var path = "/session";
        return this.makeRequestWithAuth("DELETE", path, null, String.class, authToken);
    }

    public GameData[] listGames(String authToken) throws Exception, ResponseException {
        var path = "/game";
        return this.makeRequestWithAuth("GET", path, null, GameData[].class, authToken);
    }

    public String createGame(String authToken, String gameName) throws Exception, ResponseException {
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        return this.makeRequestWithAuth("POST", path, request, String.class, authToken);
    }

    public String joinGame(String authToken, int gameID, String playerColor) throws Exception, ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(gameID, playerColor);
        return this.makeRequestWithAuth("PUT", path, request, String.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws URISyntaxException, IOException, ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private <T> T makeRequestWithAuth(String method, String path, Object request, Class<T> responseClass, String authToken) throws URISyntaxException, IOException, ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("authorization", authToken);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }

    private void writeBody(Object request, HttpURLConnection http) {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws ResponseException, IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: "+status);
        }
    }

    private boolean isSuccessful(int status) {
        //Any non 200 value status will return failure
        return status / 100 == 2;
    }

    public void observeGame(String authToken, int i) {
    }

    public String getServerUrl() {
        return serverUrl;
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
