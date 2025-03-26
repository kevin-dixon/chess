package server;

import com.google.gson.Gson;
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

}
