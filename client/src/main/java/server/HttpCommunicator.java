package server;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpCommunicator {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public HttpCommunicator(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public <T> T sendRequest(String method, String path, Object requestBody, Class<T> responseClass) throws IOException {
        URL url = new URL(serverUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);

        if (requestBody != null) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(gson.toJson(requestBody).getBytes());
            }
        }

        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            try (InputStream is = connection.getInputStream()) {
                return gson.fromJson(new InputStreamReader(is), responseClass);
            }
        } else {
            throw new IOException("HTTP error: " + connection.getResponseCode());
        }
    }
}