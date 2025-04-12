package ui;

public class ResponseException extends Throwable {
    private final String message;

    public ResponseException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
