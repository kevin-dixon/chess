package ui;

public class ResponseException extends Throwable {
    public ResponseException(int i, String message) {
    }

    public String getMessage() {
        return "error";
    }
}
