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

}