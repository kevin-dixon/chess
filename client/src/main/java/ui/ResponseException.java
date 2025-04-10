package ui;

public class ResponseException extends Throwable {
    private final int statusCode;
    private final String message;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "HTTP " + statusCode + ": " + message;
    }
}
