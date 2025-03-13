package model;

import chess.ChessGame;

public record GameData (
        int gameID,
        String whiteUsername,
        String blackUsername,
        ChessGame game,
        String gameName
) {

    public String getBlackUsername() {
        return blackUsername;
    }
    public String getWhiteUsername() {
        return whiteUsername;
    }
}
