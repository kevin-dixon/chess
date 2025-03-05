package model;

public record AuthData (
        String authToken,
        String username
) {
    AuthData updateAuthToken(String newToken) {
        return new AuthData(newToken, (username));
    }
}