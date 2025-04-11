package server;

import com.google.gson.Gson;
import model.GameData;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.LeaveGameRequest;
import model.requests.ObserveGameRequest;
import model.UserData;
import model.responses.UserAuthResponse;
import ui.ResponseException;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        var request = new UserData(username, password, email);

        UserAuthResponse response = this.makeRequest("POST", path, request, UserAuthResponse.class);

        return response.authToken();
    }

    public String login(String username, String password) throws ResponseException {
        var path = "/session";
        var request = new UserData(username, password, null);

        UserAuthResponse response = this.makeRequest("POST", path, request, UserAuthResponse.class);

        return response.authToken();
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequestWithAuth("DELETE", path, null, null, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        var path = "/game";

        record ListGamesResponse(GameData[] games) {}
        ListGamesResponse response = this.makeRequestWithAuth("GET", path, null, ListGamesResponse.class, authToken);

        return response.games();
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        var request = new CreateGameRequest(gameName);

        record CreateGameResponse(int gameID) {}
        CreateGameResponse response = this.makeRequestWithAuth("POST", path, request, CreateGameResponse.class, authToken);

        return response.gameID();
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(gameID, playerColor);

        this.makeRequestWithAuth("PUT", path, request, null, authToken);
    }

    public void observeGame(String authToken, int gameID) throws ResponseException {
        var path = "/game/observe";
        var request = new ObserveGameRequest(gameID);

        this.makeRequestWithAuth("POST", path, request, null, authToken);
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        var path = "/game/leave";
        var request = new LeaveGameRequest(gameID);

        this.makeRequestWithAuth("POST", path, request, null, authToken);
    }

    public void clearDatabase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
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
            throwIfNotSuccessful(http);
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
            String errorMessage = "HTTP error: " + status;

            // Attempt to read the error message from the response body
            try (InputStream errorStream = http.getErrorStream()) {
                if (errorStream != null) {
                    InputStreamReader reader = new InputStreamReader(errorStream);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBody.append(line);
                    }

                    // Parse the error message from the JSON response
                    var errorResponse = new Gson().fromJson(responseBody.toString(), ErrorResponse.class);
                    if (errorResponse != null && errorResponse.getMessage() != null) {
                        errorMessage = errorResponse.getMessage();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            throw new ResponseException(status, errorMessage);
        }
    }

    private boolean isSuccessful(int status) {
        return status >= 200 && status < 300;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}