package model;

import chess.ChessGame;

public record GameData (
        int gameID,
        String whiteUsername,
        String blackUsername,
        ChessGame game,
        String gameName
) {
    public GameData updateGameName(String newName) {
        return new GameData((gameID), (whiteUsername), (blackUsername), (game), newName);
    }
    public GameData updateWhiteUser(String newUsername) {
        return new GameData((gameID), newUsername, (blackUsername), (game), (gameName));
    }
    public GameData updateBlackUser(String newUsername) {
        return new GameData((gameID), (whiteUsername), newUsername, (game), (gameName));
    }
}
