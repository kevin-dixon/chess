package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    private final ServerMessageType serverMessageType;
    private final String errorMessage;
    private final String message;
    private final ChessGame game;

    public ServerMessage(ServerMessageType serverMessageType, String errorMessage, String message, ChessGame game) {
        this.serverMessageType = serverMessageType;
        this.errorMessage = errorMessage;
        this.message = message;
        this.game = game;
    }


    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }


    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
