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

    public void createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        var request = new CreateGameRequest(gameName);

        record CreateGameResponse(String gameID) {}
        CreateGameResponse response = this.makeRequestWithAuth("POST", path, request, CreateGameResponse.class, authToken);

    }

    public void joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(gameID, playerColor);

        record JoinGameResponse(String message) {}
        JoinGameResponse response = this.makeRequestWithAuth("PUT", path, request, JoinGameResponse.class, authToken);

    }

    public void observeGame(String authToken, int gameID) throws ResponseException {
        var path = "/game/observe";
        var request = new ObserveGameRequest(gameID);

        record ObserveGameResponse(String message) {}
        ObserveGameResponse response = this.makeRequestWithAuth("POST", path, request, ObserveGameResponse.class, authToken);

        System.out.println("Observe Game Response: " + response.message());
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        var path = "/game/leave";
        var request = new LeaveGameRequest(gameID);

        record LeaveGameResponse(String message) {}
        LeaveGameResponse response = this.makeRequestWithAuth("POST", path, request, LeaveGameResponse.class, authToken);

        System.out.println("Leave Game Response: " + response.message());
    }

    public <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
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

    private static class ObserveGameRequest {
        int gameID;

        ObserveGameRequest(int gameID) {
            this.gameID = gameID;
        }
    }

    private static class LeaveGameRequest {
        int gameID;

        LeaveGameRequest(int gameID) {
            this.gameID = gameID;
        }
    }

}
